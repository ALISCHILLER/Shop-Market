package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.tour.TourModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by atp on 6/11/2017.
 */
public class UpdateQueue {
    private static final int SUCCESS = 200;
    private static final int ERROR = 203;
    private final Context context;
    private boolean stopped;

    public int getQueueNumber() {
        return queueNumber;
    }

    private final int queueNumber;

    public static HashMap<String, Integer> getGroups() {
        return groups;
    }

    private static HashMap<String, Integer> groups = new HashMap<>();

    public UpdateQueue(Context context, int queueNumber) {
        Timber.v("Update queue " + queueNumber + " created.");
        this.stopped = false;
        this.context = context;
        this.queueNumber = queueNumber;
        int coreNum = Runtime.getRuntime().availableProcessors();
        int threadsNum = coreNum - 1;
        if (threadsNum <= 0)
            threadsNum = 1;
        pool = Executors.newFixedThreadPool(threadsNum);
        Timber.v("Pool created with size = " + threadsNum + " for queue " + queueNumber);
        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String taskName = msg.getData().getString("TaskName");
                if (taskName == null)
                    return true;
                if (processed.contains(taskName))
                    return true;
                processed.add(taskName);
                switch (msg.what) {
                    case SUCCESS:
                        success++;
                        queue();
                        break;
                    case ERROR:
                        failures++;
                        queue();
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private ExecutorService pool;
    private Handler handler;
    private OnQueueEvent onQueueEvent;

    public boolean isStopping() {
        return stopped;
    }

    public interface OnQueueEvent {
        void onSuccess();

        void onFailure(List<String> errors);

        void onCancel();
    }

    private ArrayList<TourAsyncTask> tourAsyncTasks = new ArrayList<>();

    private int tasks = 0;
    private int failures = 0;
    private int success = 0;
    private int aborted = 0;
    public Set<String> processed = new HashSet<>();
    private List<String> errors = new ArrayList<>();

    public void add(@NonNull final TourAsyncTask tourAsyncTask) {
        String taskName = null;
        try {
            taskName = tourAsyncTask.name();
        } catch (Exception ex) {
            Timber.e(ex);
        }
        if (taskName == null || taskName.isEmpty())
            throw new IllegalArgumentException("async task name should be not empty");
        final String finalTaskName = taskName;
        boolean duplicateTaskName = Linq.exists(tourAsyncTasks, new Linq.Criteria<TourAsyncTask>() {
            @Override
            public boolean run(TourAsyncTask item) {
                return item.name().equals(finalTaskName);
            }
        });
        if (duplicateTaskName)
            throw new IllegalStateException(taskName + " is duplicate! please change it to a unique name.");
        tasks++;
        tourAsyncTasks.add(tourAsyncTask);
        String groupName = context.getString(tourAsyncTask.group());
        Integer groupSize = groups.get(groupName);
        if (groupSize == null) {
            groups.put(groupName, 1);
        } else {
            groupSize++;
            groups.put(groupName, groupSize);
        }

    }

    private void queue() {
        if (success + failures + aborted == tasks && onQueueEvent != null) {
            if (tasks == success) {
                if (!stopped) {
                    Timber.v("Queue " + queueNumber + "  finished successfully and success submitted!");
                    onQueueEvent.onSuccess();
                } else {
                    Timber.v("Queue " + queueNumber + "  finished successfully but it was canceled beforehand!");
                    onQueueEvent.onCancel();
                }
            } else {
                if (!stopped) {
                    Timber.v(failures + " async tasks of the queue " + queueNumber + "  failed and failure submitted!");
                    onQueueEvent.onFailure(errors);
                } else {
                    Timber.v(failures + " async tasks of the queue " + queueNumber + "  failed but it was canceled beforehand!");
                    onQueueEvent.onCancel();
                }
            }
        }
    }

    public void stop() {
        Timber.v("Queue " + queueNumber + "  stopped.");
        stopped = true;
        pool.shutdownNow();
        for (TourAsyncTask tourAsyncTask :
                tourAsyncTasks) {
            tourAsyncTask.cancel();
        }
    }

    private void run() {
        for (final TourAsyncTask tourAsyncTask :
                tourAsyncTasks) {
            if (!pool.isShutdown())
                pool.execute(new java.lang.Runnable() {
                    @Override
                    public void run() {
                        Timber.v("Queue " + queueNumber + " task " + tourAsyncTask.name() + " executed.");
                        TourManager tourManager = new TourManager(context);
                        final TourModel tour = tourManager.loadTour();
                        final TourUpdateLogManager tourUpdateLogManager = new TourUpdateLogManager(context);
                        String group = context.getString(tourAsyncTask.group());
                        try {
                            if (!VaranegarApplication.is(VaranegarApplication.AppId.Supervisor))
                                tourUpdateLogManager.save(tour, tourAsyncTask.name(), group);
                            tourAsyncTask.run(new UpdateCall() {
                                @Override
                                protected void onFinish() {

                                }

                                @Override
                                protected void onFailure(String error) {
                                    Timber.e("Queue " + queueNumber + " task " + tourAsyncTask.name() + " failed.");
                                    String group = context.getString(tourAsyncTask.group());
                                    tourUpdateLogManager.saveError(tour, tourAsyncTask.name(), group, error);
                                    errors.add(error);
                                    Message msg = handler.obtainMessage(ERROR);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("TaskName", tourAsyncTask.name());
                                    msg.setData(bundle);
                                    msg.sendToTarget();
                                }

                                @Override
                                protected void onSuccess() {
                                    Timber.v("Queue " + queueNumber + " task " + tourAsyncTask.name() + " succeeded.");
                                    String group = context.getString(tourAsyncTask.group());
                                    if (!VaranegarApplication.is(VaranegarApplication.AppId.Supervisor))
                                    tourUpdateLogManager.saveSuccess(tour, tourAsyncTask.name(), group);
                                    Message msg = handler.obtainMessage(SUCCESS);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("TaskName", tourAsyncTask.name());
                                    msg.setData(bundle);
                                    msg.sendToTarget();
                                }
                            });
                        } catch (Exception ignored) {

                        }

                    }
                });
            else {
                Timber.v("Queue " + queueNumber + " task " + tourAsyncTask.name() + " aborted because the queue was canceled.");
                aborted++;
                queue();
            }
        }
    }

    public void waitForAll(OnQueueEvent onQueueEvent) {
        this.onQueueEvent = onQueueEvent;
        run();
    }
}
