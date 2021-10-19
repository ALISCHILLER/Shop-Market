package com.varanegar.framework.base.logging;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.varanegar.framework.R;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;

import timber.log.Timber;

/**
 * Created by A.Torabi on 8/28/2017.
 */

public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Context context = null;
    public static TopExceptionHandler mInstance;
    public TopExceptionHandler(Context ctx) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.context = ctx;
    }

    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof UnsupportedOperationException) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Error);
            dialog.setTitle(context.getString(R.string.error));
            dialog.setMessage(context.getString(R.string.un_supported_operation));
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } else {
            Timber.e(e, "Application crashed unexpectedly! ");
            defaultUEH.uncaughtException(t, e);
        }
    }
}
