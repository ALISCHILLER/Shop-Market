package com.varanegar.presale.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.varanegar.presale.BuildConfig;
import com.varanegar.presale.MainActivity;
import com.varanegar.presale.R;
import com.varanegar.presale.firebase.notification.PinApprove;
import com.varanegar.presale.firebase.notification.PublicNotification;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



        private static final String TAG = "FirebaseService";

        @Override
        public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            Map<String, String> map = remoteMessage.getData();
            String type = map.get("type");
            if (type != null && !type.isEmpty()) {
                switch (type) {
                    case "pin_approved":
                        new PinApprove(this, remoteMessage).sendNotification();
                        break;
                    default:
                        new PublicNotification(this,remoteMessage).sendNotification();
                        break;
                }
            }
        }

        @Override
        public void onNewToken(@NonNull String token) {
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
        }

    public static void refreshToken(Context context, Callback calback){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        SharedPreferences sharedPreferences = context
                                .getSharedPreferences("Firebase_Token", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("172F4321-16BB-4415-85D1-DD88FF04234C", s)
                                .apply();
                        calback.onSuccess();
                    }
                });
        }

    public interface Callback {
        void onSuccess();

        void onError(String error);
    }
}

