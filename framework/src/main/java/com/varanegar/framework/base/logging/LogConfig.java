package com.varanegar.framework.base.logging;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by atp on 5/6/2017.
 */

public abstract class LogConfig {
    private static LogConfig _instance;

    public static LogConfig getInstance(Context context , Class<? extends  LogConfig> configClass) throws IllegalAccessException, InstantiationException {
        if (_instance == null) {
            if (TopExceptionHandler.mInstance == null)
                TopExceptionHandler.mInstance = new TopExceptionHandler(context);
            Thread.setDefaultUncaughtExceptionHandler(TopExceptionHandler.mInstance);
            _instance = configClass.newInstance();
            _instance.config(context);
            _instance.wipeOldLogs(context);
        }
        return _instance;
    }

    protected abstract void config(Context context);

    public void wipeOldLogs(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int locationPermission = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (locationPermission == PackageManager.PERMISSION_GRANTED)
                wipe(context);
        } else {
            wipe(context);
        }
    }

    private void wipe(Context context) {
        String path = FileLoggingTree.getLogDirectory(context);
        File directory = new File(path);
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".log");
            }
        });
        if (files != null && files.length > 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -10);
            for (File file :
                    files) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date fileDate = format.parse((file.getName().replaceFirst(".log", "")));
                    if (fileDate.before(cal.getTime())) {
                        try {
                            file.delete();
                        } catch (Exception ex) {
                            Timber.e(ex);
                        }
                    }
                } catch (ParseException e) {
                    Timber.e(e);
                }

            }
        }

    }
}
