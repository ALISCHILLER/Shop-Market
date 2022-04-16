package com.varanegar.presale.firebase.notification;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("pin_code");
        ClipboardManager clipboard = (ClipboardManager)context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("pin code",message);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive:"+message);
    }
}
