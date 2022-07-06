package com.varanegar.presale.firebase.notification;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.varanegar.presale.MainActivity;
import com.varanegar.presale.R;


import java.util.Map;
import java.util.UUID;

public class PinApprove extends GeneralNotification {
    private String _customerName;//key = customer
    private UUID _customerId;//key = customer_id
    private String _pin;//key = pin

    private boolean isValid = true;

    public PinApprove(Context context, RemoteMessage remoteMessage) {
        super(context,remoteMessage);
        Map<String, String> map = remoteMessage.getData();
        _customerName = map.get("customer");
        _pin = map.get("pin");
        String customerId = map.get("customer_id");

        if (customerId == null || customerId.isEmpty()) {
            isValid = false;
            return;
        }
        if (_customerName == null || _customerName.isEmpty()) {
            isValid = false;
            return;
        }
        if (_pin == null || _pin.isEmpty()) {
            isValid = false;
            return;
        }

        _customerId = UUID.fromString(customerId);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void sendNotification() {
        if (!isValid) return;

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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
        Intent snoozeIntent = new Intent(mContext, MyBroadcastReceiver.class);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        snoozeIntent.putExtra("pin_code", _pin);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(mContext, 0, snoozeIntent, 0);
        //send notif
        final NotificationCompat.Builder mBuilder = new
                NotificationCompat.Builder(mContext, ZAR_CHANNEL_ID)
                .setSmallIcon(R.drawable.zar)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_copy_black," کپی پین کد",
                        snoozePendingIntent)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
               // .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle("زر ماکارون")
                .setContentText(mContext.getString(R.string.pin_approved, _customerName,
                        _pin))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mContext.getString
                                (R.string.pin_approved, _customerName, _pin)))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        //int notifId = (int) (Math.random() * (99));
        int notifId = Integer.parseInt(_customerId.toString()
                .replaceAll("-", "")
                .substring(0, 4), 16);

        notificationManager.notify(notifId, mBuilder.build());
    }
}
