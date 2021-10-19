package com.varanegar.vaslibrary.jobscheduler;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.varanegar.framework.util.jobscheduler.SchedulerActivity;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public class SendTrackingPointsService extends IntentService {
    Executor pool;
    Handler handler;

    public SendTrackingPointsService() {
        super("SendTrackingPointsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pool = Executors.newSingleThreadExecutor();
        handler = new Handler(getMainLooper());
        foreGround();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        pool.execute(() -> {
            LocationManager locationManager = new LocationManager(SendTrackingPointsService.this);
            locationManager.tryToSendAll();
        });
    }

    private void foreGround() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createDefaultChannel();
        }
        String appName = getApplicationContext().getString(com.varanegar.framework.R.string.app_name);
        String service = getApplicationContext().getString(com.varanegar.framework.R.string.varanegar_tracking_service);
        String ticker = getApplicationContext().getString(com.varanegar.framework.R.string.varanegar_tracking_service);
        Intent intent = new Intent(this, SchedulerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ChannelId)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentText(service)
                .setContentTitle(appName);
        notificationBuilder.setSmallIcon(R.drawable.ic_my_location_black_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(2, notificationBuilder.build());
        } else {
            startForeground(2, notificationBuilder.getNotification());
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

    public static final String ChannelId = "default_channel_id";

}
