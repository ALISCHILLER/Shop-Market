package com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model;

import com.varanegar.framework.database.model.ModelProjection;

public class ProductCustomerGroupSalesSummaryView extends ModelProjection<ProductCustomerGroupSalesSummaryViewModel> {

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

    public static ProductCustomerGroupSalesSummaryView NetWeight = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummaryView.NetWeight");

    public static ProductCustomerGroupSalesSummaryView NetCount_CA = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummaryView.NetCount_CA");

}
