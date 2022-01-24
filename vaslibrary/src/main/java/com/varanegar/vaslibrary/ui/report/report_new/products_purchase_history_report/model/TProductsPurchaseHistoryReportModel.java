package com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;

@Table
public class TProductsPurchaseHistoryReportModel extends ProductsPurchaseHistoryReportViewModel{
    @Column
    public String ProductBackOfficeCode  ;
    @Column
    public String ProductName   ;
    @Column
    public String ProductCategoryCode    ;

    @Column
    public String ProductCategoryName;

    @Column
    public Currency ProductNetWeight ;
    @Column
    public Currency ProductNetCount_CA  ;
}
