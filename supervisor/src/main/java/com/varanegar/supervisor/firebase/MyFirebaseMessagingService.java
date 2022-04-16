package com.varanegar.supervisor.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.varanegar.supervisor.BuildConfig;
import com.varanegar.supervisor.firebase.notification.GeneralNotification;
import com.varanegar.supervisor.firebase.notification.PublicNotification;
import com.varanegar.supervisor.firebase.notification.RequestPin;

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
                case "request_pin":
                    new RequestPin(this, remoteMessage).sendNotification();
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
        FirebaseMessaging.getInstance().subscribeToTopic("version" +
                BuildConfig.VERSION_CODE);
        Log.d(TAG, "onNewToken: token = " + token);
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("Firebase_Token", Context.MODE_PRIVATE);
        String oldToken = sharedPreferences
                .getString("172F4321-16BB-4415-85D1-DD88FF04234C", "");

        if (oldToken.isEmpty()) {
            sharedPreferences.edit().putString("172F4321-16BB-4415-85D1-DD88FF04234C", token)
                    .apply();
        } else if (!token.equals(oldToken)) {
            sharedPreferences.edit().putString("172F4321-16BB-4415-85D1-DD88FF04234C",
                    token).apply();
            sharedPreferences.edit().putString("172F4321-16BB-4415-85D1-DD88FF04234C__old",
                    oldToken).apply();
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
