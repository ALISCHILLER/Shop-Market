package com.varanegar.supervisor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.varanegar.supervisor.firebase.MyWorker;


import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
        Log.d(TAG, "onMessageReceived: remoteMessage.getMessageId() = " + remoteMessage.getMessageId());
        Log.d(TAG, "onMessageReceived: remoteMessage.getMessageType() = " + remoteMessage.getMessageType());
        Log.d(TAG, "onMessageReceived: remoteMessage.getData() = " + remoteMessage.getData());
        Log.d(TAG, "onMessageReceived: remoteMessage.getData() = " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "onMessageReceived: remoteMessage.getData() = " + remoteMessage.getNotification().getTitle());
        String message=remoteMessage.getNotification().getBody();
        sendNotification(message);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "sendNotification: ");
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        saveToken(token);
        FirebaseMessaging.getInstance().subscribeToTopic(getPackageName());
        FirebaseMessaging.getInstance().subscribeToTopic("version" + BuildConfig.VERSION_CODE);
        Log.d(TAG, "onNewToken: token = " + token);
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("Firebase_Token", Context.MODE_PRIVATE);
        String oldToken = sharedPreferences.getString("172F4321-16BB-4415-85D1-DD88FF04234C","");

        if (oldToken.isEmpty()) {
            sharedPreferences.edit().putString("172F4321-16BB-4415-85D1-DD88FF04234C",token).apply();
        } else if (!token.equals(oldToken)) {
            sharedPreferences.edit().putString("172F4321-16BB-4415-85D1-DD88FF04234C",token).apply();
            sharedPreferences.edit().putString("172F4321-16BB-4415-85D1-DD88FF04234C__old",oldToken).apply();
        }
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.zar)
                        .setContentTitle("zar")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
