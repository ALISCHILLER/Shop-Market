package com.varanegar.vaslibrary.webapi.distribution;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 2/24/2018.
 */

public class DistributionCustomerCallOrderViewModel {
    public UUID Id;

    public UUID CustomerCallUniqueId;

    public String BackOfficeOrderId;

    public String BackOfficeOrderNo;

    public Date SaleDate;

    public String SalePDate;

    public double OrderRoundAmount;

    public double OrderRoundTax;

    public double OrderRoundDiscount;

    public double OrderRoundCharge;

    public boolean IsInvoice;

    public UUID OrderPaymentTypeUniqueId;

    public int StockDCRefSDS;

    public int DisType;

    public double InvoiceRoundDis1;

    public double InvoiceRoundDis2;

    public double InvoiceRoundDis3;

    public double InvoiceRoundAdd1;

    public double InvoiceRoundAdd2;

    public int DCRefSDS;

    public int SaleOfficeRefSDS;

    public int AccYearSDS;

    public String StockDCCodeSDS;

    public int SupervisorRefSDS;

    public String SupervisorCodeSDS;

    public int DealerRefSDS;

    public String DealerCodeSDS;

    public String DcCodeSDS;

    public int SaleIdSDS;

    public int SaleNoSDS;

    public UUID OrderTypeUniqueId;

    public String BackOfficeSaleVoucherNo;

    public String BackOfficeInvoiceId;

    public String BackOfficeInvoiceNo;
}
