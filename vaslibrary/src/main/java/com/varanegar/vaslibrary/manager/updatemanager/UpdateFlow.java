package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.customer.SyncGuidViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by A.Torabi on 2/14/2018.
 */

public abstract class UpdateFlow {

    private final Context context;
    private UpdateCall call;

    public Context getContext() {
        return context;
    }

    public UpdateFlow(Context context) {
        this.context = context;
    }

    private List<UpdateQueue> queues = new ArrayList<>();

    private UpdateQueue getUpdateQueue(final int queueNumber) {
        UpdateQueue updateQueue = Linq.findFirst(queues, new Linq.Criteria<UpdateQueue>() {
            @Override
            public boolean run(UpdateQueue item) {
                return item.getQueueNumber() == queueNumber;
            }
        });
        if (updateQueue == null) {
            updateQueue = new UpdateQueue(context, queueNumber);
            queues.add(updateQueue);
            return updateQueue;
        } else
            return updateQueue;
    }

    private void runQueues(final int i, final UpdateCall call) {
        UpdateQueue updateQueue = queues.get(i);
        updateQueue.waitForAll(new UpdateQueue.OnQueueEvent() {
            @Override
            public void onSuccess() {
                if (queues.size() > i + 1)
                    runQueues(i + 1, call);
                else
                    call.success();
            }

            @Override
            public void onFailure(List<String> errors) {
                call.onFailure(errors.get(0));
            }

            @Override
            public void onCancel() {
                call.failure(getContext().getString(R.string.tour_canceled));
            }
        });
    }

    protected abstract void addAsyncTasks(List<TourAsyncTask> tasks, UpdateCall call);

    public void stop() {
        for (UpdateQueue queue :
                queues) {
            queue.stop();
        }
    }

    public boolean isStopping() {
        for (UpdateQueue queue :
                queues) {
            if (!queue.isStopping())
                return false;
        }
        return true;
    }

    protected void start(@NonNull final UpdateCall call) {
        this.call = call;
        UpdateQueue.getGroups().clear();
        List<TourAsyncTask> tasks = new ArrayList<>();
        addAsyncTasks(tasks, call);
        Set<String> taskNames = new HashSet<>();
        for (TourAsyncTask syncTask :
                tasks) {
            if (taskNames.contains(syncTask.name()))
                call.failure(syncTask.name() + " already has been added");
            UpdateQueue queue = getUpdateQueue(syncTask.queueId());
            queue.add(syncTask);
        }

        Linq.sort(queues, new Comparator() {
            @Override
            public int compare(Object o, Object t1) {
                UpdateQueue left = (UpdateQueue) o;
                UpdateQueue right = (UpdateQueue) t1;
                if (left.getQueueNumber() > right.getQueueNumber()) return 1;
                else if (left.getQueueNumber() == right.getQueueNumber()) return 0;
                else return -1;
            }
        });
        runQueues(0, call);
    }


}
