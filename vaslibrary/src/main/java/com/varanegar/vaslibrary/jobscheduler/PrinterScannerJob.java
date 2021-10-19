package com.varanegar.vaslibrary.jobscheduler;

import android.content.Context;

import com.varanegar.framework.util.jobscheduler.Job;
import com.varanegar.vaslibrary.print.PrinterManager;

/**
 * Created by A.Torabi on 3/17/2018.
 */

public class PrinterScannerJob implements Job {
    @Override
    public Long getInterval() {
        return 300L;
    }

    @Override
    public void run(Context context) {
        PrinterManager printerManager = new PrinterManager(context);
        printerManager.startScanner();
    }
}
