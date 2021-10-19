package com.varanegar.dist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.varanegar.dist.MainActivity;

/**
 * Created by A.Torabi on 10/3/2018.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }
}