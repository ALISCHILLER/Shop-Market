package com.varanegar.vaslibrary.print.drivers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saeedeh on 4/12/2021.
 */
public class TejaratElectronicParsianPdaPrinterDriver extends PrinterDriver {
    Context context;

    public TejaratElectronicParsianPdaPrinterDriver(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int printWidth() {
        return 58;
    }

    @Override
    public String name() {
        return "TejaratElectronicParsian";
    }

    @Override
    public int dpi() {
        return 172;
    }


    @Override
    public void print(Bitmap bitmap, PrintCallback printCallback) {
        List<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.add(bitmap);
        saveFile(bitmapList);
        Intent intent = new Intent();
        ComponentName cName = new ComponentName("com.pec.smartpos", "com.pec.smartpos.cpsdk.PecService");
        intent.setComponent(cName);
        intent.putExtra("printType", "receiptBitMap");
        intent.putExtra("NumOfBitMap", bitmapList.size());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        printCallback.done();
    }

    private void saveFile(List<Bitmap> bitmapList) {
        try {
            assert bitmapList != null;
            if (bitmapList.size() == 0) return;
            int i = 0;
            // don't change this path name
            String savePath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(savePath);
            if (!file.exists()) file.mkdirs();
            for (Bitmap bitmap : bitmapList) {
                String filename = savePath + "/pic" + i + ".bmp";
                file = new File(filename);
                if (!file.exists()) {
                    if (file.createNewFile()) {
                        FileOutputStream fileos = new FileOutputStream(filename);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileos);
                        fileos.flush();
                        fileos.close();
                    }
                }
                i++;
            }
        } catch (Exception e) {
            Log.e("Exception : ", e.getMessage());
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
