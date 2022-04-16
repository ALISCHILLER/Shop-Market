package com.varanegar.supervisor.firebase.notification;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

public class PublicNotification extends GeneralNotification{
    public PublicNotification(Context context, RemoteMessage remoteMessage) {
        super(context,remoteMessage);
    }
}
