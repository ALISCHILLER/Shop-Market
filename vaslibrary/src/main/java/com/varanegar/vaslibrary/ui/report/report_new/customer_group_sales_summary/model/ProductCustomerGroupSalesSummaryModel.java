package com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model;


import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import java.math.BigDecimal;

@Table
public class ProductCustomerGroupSalesSummaryModel extends BaseModel {
    @Column
    public String CustomerGroup;
    @Column
    public String CustomerGroupTXT;
    @Column
    public String CustomerActivity;
    @Column
    public String CustomerActivityTXT ;
    @Column
    public Currency NetWeight;
    @Column
    public BigDecimal NetCount_CA;

}
