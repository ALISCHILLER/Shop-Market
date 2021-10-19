package com.varanegar.framework.util.jobscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;

import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public abstract class JobScheduler extends BroadcastReceiver {
    Handler handler;

    public Context getContext() {
        return context;
    }

    private Context context;

    public JobScheduler() {
        handler = new Handler();
    }

    private static List<JobPair> tasks = null;

    public static void resetJob(final Class<? extends Job> jobClass, Context context) {
        if (tasks != null) {
            JobPair jp = Linq.findFirst(tasks, new Linq.Criteria<JobPair>() {
                @Override
                public boolean run(JobPair item) {
                    return item.job.getClass().getName().equals(jobClass.getName());
                }
            });
            if (jp != null) {
                jp.time = SystemClock.elapsedRealtime();
                jp.job.run(context);
            } else {
                try {
                    Job job = jobClass.newInstance();
                    JobScheduler.register(job);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("JOB_SCHEDULER", Context.MODE_PRIVATE);
        boolean isExact = intent.getBooleanExtra("isExact", false);
        long lastTime = sharedPreferences.getLong("LAST_TIME", 0);
        if (isExact) {
            sharedPreferences.edit().putLong("LAST_TIME", SystemClock.elapsedRealtime()).apply();
            JobManager.initializeExact(context, this.getClass(), 600000);
        } else if (SystemClock.elapsedRealtime() - lastTime > 900000)
            JobManager.initializeExact(context, this.getClass(), 1000);
        this.context = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (tasks == null) {
                    tasks = new ArrayList<>();
                    registerJobs();
                }
                final Long elapsedRealtime = SystemClock.elapsedRealtime();
                for (final JobPair pair :
                        tasks) {
                    if (elapsedRealtime - pair.time >= pair.job.getInterval() * 1000) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                pair.job.run(context);
                            }
                        });
                        pair.time = elapsedRealtime;
                    }
                }
            }
        }).start();
    }


    public abstract void registerJobs();

    public static void register(final Job job) {
        int i = Linq.findFirstIndex(tasks, new Linq.Criteria<JobPair>() {
            @Override
            public boolean run(JobPair item) {
                return item.job.getClass().getName().equals(job.getClass().getName());
            }
        });
        if (i == -1)
            tasks.add(new JobPair(job));
    }
}