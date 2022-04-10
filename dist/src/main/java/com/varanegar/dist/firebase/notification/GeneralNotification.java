package com.varanegar.dist.firebase.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.varanegar.dist.MainActivity;
import com.varanegar.dist.R;

public abstract class GeneralNotification {
    public static final String ZAR_CHANNEL_ID = "ZAR_CHANNEL";
    protected Context mContext;

    public GeneralNotification(Context context) {
        mContext = context;
        createNotificationChannel(context);
    }

    public static void createNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);

            if (notificationManager.getNotificationChannel(ZAR_CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        ZAR_CHANNEL_ID, "Zar Notification", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Zar Notification");
                channel.setVibrationPattern(new long[]{1000, 1000});
                channel.enableVibration(true);
                channel.setLightColor(ContextCompat.getColor(context, R.color.orange));
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void sendNotification(Context context, String messageBody) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.zar)
                        .setContentTitle("zar")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
