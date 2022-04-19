package com.varanegar.dist.firebase.notification;

import android.annotation.SuppressLint;
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

import com.google.firebase.messaging.RemoteMessage;
import com.varanegar.dist.MainActivity;
import com.varanegar.dist.R;

public abstract class GeneralNotification {
    public static final String ZAR_CHANNEL_ID = "ZAR_CHANNEL";
    protected Context mContext;
    protected RemoteMessage mRemoteMessage;

    public GeneralNotification(Context context, RemoteMessage remoteMessage) {
        mContext = context;
        mRemoteMessage = remoteMessage;
        createNotificationChannel(context);
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    @SuppressLint("UnspecifiedImmutableFlag")
    public void sendNotification() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(mContext,
                    0, intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getActivity(mContext,
                    0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        String channelId = mContext.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (mRemoteMessage.getNotification() == null) return;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, channelId)
                        .setSmallIcon(R.drawable.zar)
                        .setContentTitle(mRemoteMessage.getNotification().getTitle())
                        .setContentText(mRemoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(mRemoteMessage.getNotification().getBody()))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}