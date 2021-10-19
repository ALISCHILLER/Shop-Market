package com.varanegar.vaslibrary.base;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.logging.DebugTree;
import com.varanegar.framework.base.logging.FileLoggingTree;
import com.varanegar.framework.base.logging.LogConfig;
import com.varanegar.framework.base.logging.TrackingFileLoggingTree;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/20/2017.
 */

public class VasLogConfig extends LogConfig {

    private static TrackingFileLoggingTree trackingFileLoggingTree;

    @Override
    protected void config(Context context) {
        Timber.plant(new DebugTree(), new FileLoggingTree(context));
        trackingFileLoggingTree = new TrackingFileLoggingTree(context);
    }

    public static void addTrackingTree() {
        if (trackingFileLoggingTree != null)
            Timber.plant(trackingFileLoggingTree);
    }

    public static void removeTrackingTree() {
        if (trackingFileLoggingTree != null)
            Timber.uproot(trackingFileLoggingTree);
    }

}
