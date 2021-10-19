package com.varanegar.vaslibrary.print.drivers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.kishcore.sdk.hybrid.api.SDKManager;
import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;
import com.varanegar.vaslibrary.print.Helper.RahyabPrintModel;
import com.varanegar.vaslibrary.print.Helper.RahyabTestPrintableDataList;

import java.util.ArrayList;

/**
 * Created by A.Torabi on 11/10/2018.
 */

public class RahyabPdaPrinterDriver extends PrinterDriver {
    static private SDKManager sdkManager;

    public static void init(Context context) {
        SDKManager.init(context);
    }

    public RahyabPdaPrinterDriver(Context context) {
        super(context);
    }

    @Override
    public int printWidth() {
        return 57;
    }

    @Override
    public String name() {
        return "rahyab";
    }

    @Override
    public int dpi() {
        return 172;
    }

    @Override
    public void print(final Bitmap bitmap, PrintCallback response) {
//        ArrayList<RahyabPrintModel> printModels = new ArrayList<>();
        if (SDKManager.getPrinterStatus() == SDKManager.STATUS_OK) {
//            printModels.add(new RahyabPrintModel(new BitmapDrawable(context.getResources(), bitmap)));
//            SDKManager.print(context, new RahyabTestPrintableDataList(printModels), null);
            SDKManager.printBitmap(context, bitmap, true, 20, null, null);
        }
        response.done();
    }

    @Override
    public void connect(ConnectionCallback response) {
        response.connected();
    }

    @Override
    public void disconnect() {

    }
}
