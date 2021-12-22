package com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;


@Table
public class PCustomerPurchaseHistoryViewModel  extends CustomerPurchaseHistoryViewModel {

    @Column
    public String CustomerBackOfficeCode;

    @Column
    public String CustomerName ;
    @Column
    public String InvoiceShamsiDate ;

    @Column
    public String InvoiceNumber;
    @Column
    public String ProductBackOfficeCode;

    @Column
    public String ProductName ;

    @Column
    public int Count_CA;
    @Column
    public int Count_EA ;
    @Column
    public int Price ;
    @Column
    public BigDecimal Amount  ;

}
