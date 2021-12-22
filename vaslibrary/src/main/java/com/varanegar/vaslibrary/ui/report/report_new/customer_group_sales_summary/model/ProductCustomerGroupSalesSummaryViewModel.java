package com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.CustomerGroupSalesSummaryViewModel;

import java.math.BigDecimal;

@Table
public class ProductCustomerGroupSalesSummaryViewModel extends CustomerGroupSalesSummaryViewModel {

    @Column
    public String CustomerGroupTXT;
    @Column
    public String CustomerActivity;
    @Column
    public String CustomerActivityTXT ;
    @Column
    public BigDecimal NETWeight ;
    @Column
    public BigDecimal NETCount_CA;

}
