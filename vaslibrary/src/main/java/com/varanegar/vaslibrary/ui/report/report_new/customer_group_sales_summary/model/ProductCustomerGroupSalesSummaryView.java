package com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.vaslibrary.ui.report.report_new.model.ProductInvoiveBalanceReportView;

public class ProductCustomerGroupSalesSummaryView extends
        ModelProjection<ProductCustomerGroupSalesSummaryViewModel> {
    protected ProductCustomerGroupSalesSummaryView(String name) {
        super(name);
    }

    public static ProductCustomerGroupSalesSummaryView CustomerGroupTXT = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummaryView.CustomerGroupTXT");

    public static ProductCustomerGroupSalesSummaryView CustomerActivity = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummaryView.CustomerActivity");

    public static ProductCustomerGroupSalesSummaryView CustomerActivityTXT = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummaryView.CustomerActivityTXT");

    public static ProductCustomerGroupSalesSummaryView NETWeight = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummaryView.NETWeight");

    public static ProductCustomerGroupSalesSummaryView NETCount_CA = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummaryView.NETCount_CA");

}
