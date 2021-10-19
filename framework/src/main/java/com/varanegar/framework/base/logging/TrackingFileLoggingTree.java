package com.varanegar.framework.base.logging;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.varanegar.framework.util.HelperMethods;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by atp on 12/20/2016.
 */
public class TrackingFileLoggingTree extends Timber.DebugTree {
    private FilePrinter printer;
    private String packageName;
    private String versionName = "unknown";

    private static File getAbsoluteFile(String relativePath, Context context) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(HelperMethods.getExternalFilesDir(context, null), relativePath);
            return file;
        } else {
            File file = new File(context.getFilesDir(), relativePath);
            return file;
        }
    }

    public static String getLogDirectory(Context context) {
        return getAbsoluteFile("/logs", context).getAbsolutePath();
    }

    public TrackingFileLoggingTree(Context context) {
        final String logDirectory = getLogDirectory(context);
        printer = new FilePrinter(logDirectory, new FileNameGenerator() {
            @Override
            public String generateFileName(long time) {
                return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time)) + "-tracking.log";
            }
        });
        packageName = context.getPackageName();
        try {
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        return packageName + "/" + versionName + "/" + super.createStackElementTag(element) + ":" + element.getLineNumber() + "/Time: " + currentDateTimeString + " ";
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        printer.println(priority, tag, message);
    }
}
