package com.varanegar.vaslibrary.print.drivers;

import android.content.Context;
import android.graphics.Bitmap;

import com.pax.dal.IDAL;
import com.pax.dal.IPrinter;
import com.pax.dal.exceptions.PrinterDevException;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;

/**
 * Created by Elnaz on 6/15/2020.
 */

public class SamanKishPrinterDriver extends PrinterDriver {

    private static IDAL idal;
    private static IPrinter iPrinter;

    public SamanKishPrinterDriver(Context context) {
        super(context);
        try {
            idal = NeptuneLiteUser.getInstance().getDal(context);
            iPrinter = idal.getPrinter();
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
        return "SamanKish";
    }

    @Override
    public int dpi() {
        return 172;
    }

    @Override
    public void print(Bitmap bitmap, PrintCallback printCallback) {
        try {
            iPrinter.init();
            iPrinter.setGray(100);
            iPrinter.printBitmap(bitmap);
            iPrinter.printStr("\n\n\n", "");
            iPrinter.start();
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
        printCallback.done();
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) {
        connectionCallback.connected();
    }

    @Override
    public void disconnect() {

    }
}
