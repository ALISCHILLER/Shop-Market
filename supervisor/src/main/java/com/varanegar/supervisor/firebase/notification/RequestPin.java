package com.varanegar.supervisor.firebase.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.supervisor.MainActivity;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_Model;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_ModelRepository;
import com.varanegar.supervisor.fragment.list_notification_Fragment.ListNotification_Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class RequestPin extends GeneralNotification {
    private UUID _customerId;//key = custoemr
    private String _pinType;//key = pin
    private UUID _dealerId;//key = user
    private String _customerName;//key = customerName
    private String _dealerName;//key = userName
    public UUID _customer_call_order;
    private boolean isValid = true;

    public RequestPin(Context context, RemoteMessage remoteMessage) {
        super(context,remoteMessage);
        Map<String, String> map = remoteMessage.getData();
        String customerId = map.get("customer");
        String pinType = map.get("pin");
        String dealerId = map.get("user");
        String customerName = map.get("customerName");
        String delaerName = map.get("userName");
        String customer_call_order=map.get("customer_call_order");
        if (customerId == null || customerId.isEmpty()) {
            isValid = false;
            return;
        }
        if (pinType == null || pinType.isEmpty()) {
            isValid = false;
            return;
        }
        if (dealerId == null || dealerId.isEmpty()) {
            isValid = false;
            return;
        }
        if (customerName == null || customerName.isEmpty()) {
            isValid = false;
            return;
        }
        if (delaerName == null || delaerName.isEmpty()) {
            isValid = false;
            return;
        }
        if ((customer_call_order == null || customer_call_order.isEmpty())
                && !pinType.equals("pin4")) {
            isValid = false;
            return;
        }
        this._customerId = UUID.fromString(customerId);
        this._pinType = pinType;
        this._dealerId = UUID.fromString(dealerId);
        this._customerName = customerName;
        this._dealerName = delaerName;
        if (customer_call_order != null )
        this._customer_call_order=UUID.fromString(customer_call_order);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void sendNotification() {
        if (!isValid) return;
        //pin type string name
        String pinName = "";
        switch (_pinType) {
            case "pin1":
                pinName = mContext.getString(R.string.pin1);
                break;
            case "pin2":
                pinName = mContext.getString(R.string.pin2);
                break;
            case "pin3":
                pinName = mContext.getString(R.string.pin3);
                break;
            case "pin4":
                pinName = mContext.getString(R.string.pin4);
                break;
        }

        //save request to db

        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = simpleDateFormat.format(now);
        PinRequest_Model pinRequest_model = new PinRequest_Model();
        pinRequest_model.UniqueId = UUID.randomUUID();
        pinRequest_model.customerId = _customerId;
        pinRequest_model.pinType = _pinType;
        pinRequest_model.pinName = pinName;
        pinRequest_model.customerName = _customerName;
        pinRequest_model.dealerName = _dealerName;
        pinRequest_model.dealerId = _dealerId;
        pinRequest_model.customer_call_order=_customer_call_order;
        pinRequest_model.Status = "";
        pinRequest_model.date = date;

        PinRequest_ModelRepository pinRequest_modelRepository = new PinRequest_ModelRepository();
        pinRequest_modelRepository.insertOrUpdate(pinRequest_model);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pin_code","pin_layout");
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

        //send notif
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, ZAR_CHANNEL_ID)
                .setSmallIcon(R.drawable.zar)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle("زر ماکارون")
                .setContentText(mContext.getString(R.string.request_pin, _dealerName, pinName, _customerName))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mContext.getString
                                (R.string.request_pin, _dealerName, pinName, _customerName)))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int notifId = Integer.parseInt(_dealerId.toString()
                .replaceAll("-", "")
                .substring(0, 4), 16);

        notificationManager.notify(notifId, mBuilder.build());
    }
}
