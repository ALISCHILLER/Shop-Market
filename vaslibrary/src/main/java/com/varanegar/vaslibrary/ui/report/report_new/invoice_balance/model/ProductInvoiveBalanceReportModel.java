package com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.model;


import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import java.math.BigDecimal;

@Table
public class ProductInvoiveBalanceReportViewModel extends BaseModel {
    @Column
    public String CustomerBackOfficeCode;
    @Column
    public String CustomerName;
    @Column
    public String InvoiceNumber ;
    @Column
    public String InvoiceShmsiDate  ;

    @Column
    public String InvoiceOverDue   ;
    @Column
    public Currency InvoiceFinalPrice ;
    @Column
    public Currency PaidPose  ;
    @Column
    public Currency PaidCash  ;
    @Column
    public Currency PaidCheck ;
    @Column
    public Currency IvoiceRemain ;
    @Column
    public BigDecimal UsancePaid ;
}
