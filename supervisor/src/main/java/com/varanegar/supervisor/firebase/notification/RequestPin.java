package com.varanegar.supervisor.firebase.notification;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_Model;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_ModelRepository;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class RequestPin {
    private final Context _context;

    private UUID _customerId;//key = custoemr
    private String _pinType;//key = pin
    private UUID _dealerId;//key = user
    private String _customerName;//key = customerName
    private String _dealerName;//key = userName

    private boolean isValid = true;

    public RequestPin(Context context, RemoteMessage remoteMessage) {
        this._context = context;
        Map<String, String> map = remoteMessage.getData();
        String customerId = map.get("customer");
        String pinType = map.get("pin");
        String dealerId = map.get("user");
        String customerName = map.get("customerName");
        String delaerName = map.get("userName");
        if (customerId==null || customerId.isEmpty())
        {
            isValid=false;
            return;
        }
        if (pinType==null || pinType.isEmpty())
        {
            isValid=false;
            return;
        }
        if (dealerId==null || dealerId.isEmpty())
        {
            isValid=false;
            return;
        }
        if (customerName==null || customerName.isEmpty())
        {
            isValid=false;
            return;
        }
        if (delaerName==null || delaerName.isEmpty())
        {
            isValid=false;
            return;
        }

        this._customerId = UUID.fromString(customerId);
        this._pinType = pinType;
        this._dealerId = UUID.fromString(dealerId);
        this._customerName = customerName;
        this._dealerName = delaerName;
    }

    public void send() {
        if (!isValid) return;
        //pin type string name
        String pinName="";
        switch (_pinType){
            case "pin1":
                pinName = "پین رسیدی";
                break;
            case "pin2":
                pinName = "پین چک";
                break;
            case "pin3":
                pinName = "پین کل مرجوع";
                break;
            case "pin4":
                pinName = "پین ویرایش مشتری";
                break;
        }

        //save request to db
        PinRequest_Model pinRequest_model=new PinRequest_Model();
        pinRequest_model.customerId=_customerId;
        pinRequest_model.pinType=_pinType;
        pinRequest_model.customerName=_customerName;
        pinRequest_model.dealerName=_dealerName;
        pinRequest_model.dealerId=_dealerId;
        pinRequest_model.date=new Date();
        PinRequest_ModelRepository pinRequest_modelRepository=new PinRequest_ModelRepository();
        pinRequest_modelRepository.insertOrUpdate(pinRequest_model);

        //send notif
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_context, channelId)
                .setSmallIcon(R.drawable.yekzan_notif_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.orange))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSubText(subText)
                .setCustomContentView(notificationLayout)
                .setShowWhen(!hideTime);



        //button to approve request
    }

    private void sendNotification(String message) {

    }
}
