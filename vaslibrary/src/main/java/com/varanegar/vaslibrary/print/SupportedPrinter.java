package com.varanegar.vaslibrary.print;

import com.varanegar.printlib.bixolon.SPP_R200II;
import com.varanegar.printlib.driver.PrinterDriver;

/**
 * Created by A.Torabi on 11/11/2018.
 */

public class SupportedPrinter {
    public String PrinterName;
    public Class<? extends PrinterDriver> PrinterDriver;

    public SupportedPrinter(String printerName, Class<? extends PrinterDriver> printerClass) {
        this.PrinterName = printerName;
        this.PrinterDriver = printerClass;
    }
}
