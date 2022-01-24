package com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model;

import com.varanegar.framework.database.model.ModelProjection;

public class ProductCustomerGroupSalesSummaryView extends ModelProjection<ProductCustomerGroupSalesSummaryModel> {

    protected ProductCustomerGroupSalesSummaryView(String name) {
        super(name);
    }

    public static ProductCustomerGroupSalesSummaryView CustomerGroupTXT = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummary.CustomerGroupTXT");

    public static ProductCustomerGroupSalesSummaryView CustomerActivity = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummary.CustomerActivity");

    public static ProductCustomerGroupSalesSummaryView CustomerActivityTXT = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummary.CustomerActivityTXT");

    public static ProductCustomerGroupSalesSummaryView NetWeight = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummary.NetWeight");

    public static ProductCustomerGroupSalesSummaryView NetCount_CA = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummary.NetCount_CA");

    public static ProductCustomerGroupSalesSummaryView ProductCustomerGroupSalesSummaryTbl = new
            ProductCustomerGroupSalesSummaryView
            ("ProductCustomerGroupSalesSummary");

}
