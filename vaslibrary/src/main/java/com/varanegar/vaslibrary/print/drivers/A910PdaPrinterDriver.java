package com.varanegar.vaslibrary.print.drivers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.dal.ISys;
import com.pax.dal.entity.ETermInfoKey;
import com.pax.dal.exceptions.PrinterDevException;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;

import java.util.Arrays;
import java.util.Map;

import ir.ikccc.externalpayment.PrinterRequest;

/**
 * Created by Elnaz on 7/6/2020.
 */

public class A910PdaPrinterDriver extends PrinterDriver {
    Context context;
    private static IPrinter prn;
    private volatile static IDAL dal;
    private static ISys iSys;
    private static NeptuneLiteUser ppUser;

    public A910PdaPrinterDriver(Context context) {
        super(context);
        this.context = context;

        try {
            ppUser = NeptuneLiteUser.getInstance();
            dal = ppUser.getDal(context);
            prn = dal.getPrinter();
            iSys = dal.getSys();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int printWidth() {
        return 58;
    }

    @Override
    public String name() {
        return "A910";
    }

    @Override
    public int dpi() {
        return 172;
    }

    public String getModel() {
        try {
            Map<ETermInfoKey, String> terminalInfo = dal.getSys().getTermInfo();
            return terminalInfo.get(ETermInfoKey.MODEL).trim();
        } catch (Exception exceptions) {
            exceptions.printStackTrace();
        }
        return null;
    }

    @Override
    public void print(Bitmap bitmap, PrintCallback printCallback) {
        if (installedOrNot(context, "com.pos.pax")) {//check if it is beh pradakht PDA or not! packageName(debug):"com.bpm.pos"  and  packageName(signed PDA):"com.pos.pax"
            try {
                prn.init();
                prn.setGray(4);
                prn.printBitmap(bitmap);
                prn.start();
                int printerStatus = prn.getStatus();
                while (printerStatus == 1) {
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printerStatus = prn.getStatus();
                }
                if (printerStatus == 0) {
                    printCallback.done();
                }
            } catch (PrinterDevException e) {
                e.printStackTrace();
            }
        } else { //IranKish
            PrinterRequest printerRequest = new PrinterRequest((Activity) context);
            if (!printerRequest.send(bitmap))
                printCallback.failed();
            else
                printCallback.done();
        }
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) {
        connectionCallback.connected();
    }

    @Override
    public void disconnect() {

    }
    private boolean installedOrNot(Context cnt, String packageName) {
        PackageManager pm = cnt.getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (Exception e) {
            e.printStackTrace();
            appInstalled = false;
        }
        return appInstalled;
    }
}