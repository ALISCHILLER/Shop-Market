package com.varanegar.vaslibrary.print.drivers;

import android.content.Context;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.varanegar.printlib.driver.ConnectionCallback;
import com.varanegar.printlib.driver.PrintCallback;
import com.varanegar.printlib.driver.PrinterDriver;
import com.varanegar.vaslibrary.R;

import static android.device.PrinterManager.PRNSTS_BUSY;
import static android.device.PrinterManager.PRNSTS_ERR;
import static android.device.PrinterManager.PRNSTS_ERR_DRIVER;
import static android.device.PrinterManager.PRNSTS_OK;
import static android.device.PrinterManager.PRNSTS_OUT_OF_PAPER;
import static android.device.PrinterManager.PRNSTS_OVER_HEAT;
import static android.device.PrinterManager.PRNSTS_UNDER_VOLTAGE;

/**
 * Created by Elnaz on 6/6/2020.
 */

public class I9000SPdaPrinterDriver extends PrinterDriver {
    static private PrinterManager mPrinterManager;

    public I9000SPdaPrinterDriver(Context context) {
        super(context);
        mPrinterManager = new PrinterManager();
        mPrinterManager.open();
    }

    @Override
    public int printWidth() {
        return 58;
    }

    @Override
    public String name() {
        return "I9000S";
    }

    @Override
    public int dpi() {
        return 172;
    }

    @Override
    public void print(Bitmap bitmap, PrintCallback printCallback) {
        if (bitmap != null) {
            int ret = mPrinterManager.getStatus();
            if (ret == PRNSTS_OK) {
                mPrinterManager.setupPage(384, -1);
                mPrinterManager.clearPage();
                mPrinterManager.drawBitmap(bitmap, 0, 0);
                ret = mPrinterManager.printPage(0);
                mPrinterManager.paperFeed(16);
                getPrintStateMessage(ret);
                printCallback.done();
            } else {
                getPrintStateMessage(ret);
                printCallback.failed();
            }
        } else {
            printCallback.failed();
        }
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) {
        connectionCallback.connected();
    }

    @Override
    public void disconnect() {

    }

    private void getPrintStateMessage(final int status) {
        if (status == PRNSTS_OUT_OF_PAPER) {
            Toast.makeText(
                    context,
                    R.string.tst_info_paper,
                    Toast.LENGTH_SHORT).show();
        } else if (status == PRNSTS_OVER_HEAT) {
            Toast.makeText(
                    context,
                    R.string.tst_info_temperature,
                    Toast.LENGTH_SHORT).show();
        } else if (status == PRNSTS_UNDER_VOLTAGE) {
            Toast.makeText(
                    context,
                    R.string.tst_info_voltage,
                    Toast.LENGTH_SHORT).show();
        } else if (status == PRNSTS_BUSY) {
            Toast.makeText(
                    context,
                    R.string.tst_info_busy,
                    Toast.LENGTH_SHORT).show();
        } else if (status == PRNSTS_ERR) {
            Toast.makeText(
                    context,
                    R.string.tst_info_error,
                    Toast.LENGTH_SHORT).show();
        } else if (status == PRNSTS_ERR_DRIVER) {
            Toast.makeText(
                    context,
                    R.string.tst_info_driver_error,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
