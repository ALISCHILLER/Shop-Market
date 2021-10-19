package com.varanegar.vaslibrary.print.drivers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.base.VasApplication;

import java.io.File;
import java.io.FileOutputStream;

import timber.log.Timber;

public class SepehrPdaPrintDriver extends PrinterDriver {
    Context context;
    public SepehrPdaPrintDriver(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int printWidth() {
        return 57;
    }

    @Override
    public String name() {
        return "sepehr";
    }

    @Override
    public int dpi() {
        return 172;
    }

    @Override
    public void print(Bitmap bitmap, PrintCallback printCallback) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/Varanegar_forsepehr");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            String fname = "factor.jpg";
            File file = new File (myDir, fname);
            if (file.exists ())
                file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("com.dml.sima7.sepehr.activity.Intent_PrintFactorActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP/* | Intent.FLAG_ACTIVITY_NEW_TASK*/);
            context.startActivity(intent);
            printCallback.done();
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Timber.e("Main", "Second application is not installed!");

        }
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) {
        connectionCallback.connected();
    }

    @Override
    public void disconnect() {

    }
}
