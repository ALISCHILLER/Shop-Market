package com.varanegar.framework.util.jobscheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public class JobManager {
    private static AlarmManager alarmManager;

    public static void initialize(Context context, Class<? extends JobScheduler> schedulerClass) {
        try {
            Intent intent = new Intent(context, schedulerClass);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null)
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000L, pendingIntent);
        } catch (SecurityException ex) {
            Timber.e(ex);
        }

    }

    public static void initializeExact(Context context, Class<? extends JobScheduler> schedulerClass, long delay) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Intent intent2 = new Intent(context, schedulerClass);
                intent2.putExtra("isExact", true);
                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 2, intent2, 0);
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null)
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent2);
            } catch (SecurityException ex) {
                Timber.e(ex);
            }
        }
    }
}
