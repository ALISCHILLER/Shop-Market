package com.varanegar.framework.util.jobscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.varanegar.framework.R;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public class JobSchedulerService extends Service {
    public static final String ChannelId = "default_channel_id";

    class SchedulerServiceBinder extends Binder {
        public JobSchedulerService getService() {
            return JobSchedulerService.this;
        }
    }

    SchedulerServiceBinder binder = new SchedulerServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Timber.i("Job Scheduler service is binding");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.i("Job Scheduler service is starting");
        int icon = -1;
        if (intent != null)
            icon = intent.getIntExtra("icon_res_id", -1);
        foreGround(icon);
        return START_STICKY;
    }

    void startJobManager(Context context, Class<? extends JobScheduler> jobSchedulerClass) {
        JobManager.initialize(context, jobSchedulerClass);
        JobManager.initializeExact(context, jobSchedulerClass, 1000);
        Timber.i("Job Manager initialized");
    }

    private void foreGround(int icon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createDefaultChannel();
        }
        String appName = getApplicationContext().getString(R.string.app_name);
        String service = getApplicationContext().getString(R.string.varanegar_service);
        String ticker = getApplicationContext().getString(R.string.varanegar_scheduler_started);
        Intent intent = new Intent(this, SchedulerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ChannelId)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentText(service)
                .setContentTitle(appName);
        if (icon != -1)
            notificationBuilder.setSmallIcon(icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(1, notificationBuilder.build());
        } else {
            startForeground(1, notificationBuilder.getNotification());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createDefaultChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence channelName = "Default Channel";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(ChannelId, channelName, importance);
        if (notificationManager != null)
            notificationManager.createNotificationChannel(notificationChannel);
    }

    public static void start(final Context context, @DrawableRes int icon, final Class<? extends JobScheduler> jobSchedulerClass) {
        Intent serviceIntent = new Intent(context, JobSchedulerService.class);
        serviceIntent.putExtra("icon_res_id", icon);
        context.startService(serviceIntent);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                JobSchedulerService jobSchedulerService = ((SchedulerServiceBinder) service).getService();
                jobSchedulerService.startJobManager(context, jobSchedulerClass);
                Timber.i("Job Scheduler service connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Timber.i("Job Scheduler service disconnected");
            }
        };
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    private static ServiceConnection connection;
}
