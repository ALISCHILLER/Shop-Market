package com.varanegar.dist.firebase.notification;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver  extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("pin_code");
        ClipboardManager clipboard = (ClipboardManager)context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("pin code",message);
        clipboard.setPrimaryClip(clip);
        Log.d(TAG, "onReceive:"+message);
    }
}
