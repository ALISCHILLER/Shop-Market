package com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.ProductCustomerGroupSalesSummaryModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.ProductCustomerGroupSalesSummaryView;

public class CustomerGroupSalesSummaryAdapter extends SimpleReportAdapter<ProductCustomerGroupSalesSummaryModel> {
    public CustomerGroupSalesSummaryAdapter(MainVaranegarActivity activity) {
        super(activity, ProductCustomerGroupSalesSummaryModel.class);
    }

    public CustomerGroupSalesSummaryAdapter(VaranegarFragment fragment) {
        super(fragment, ProductCustomerGroupSalesSummaryModel.class);
    }

    @Override
    public void bind(ReportColumns columns, ProductCustomerGroupSalesSummaryModel entity){
        columns.add(bind(entity, ProductCustomerGroupSalesSummaryView.CustomerGroupTXT,"گروه مشتری").setWeight(2));
        columns.add(bind(entity,ProductCustomerGroupSalesSummaryView.CustomerActivityTXT,"فعالیت مشتری").setWeight(2).setFrizzed());
        columns.add(bind(entity,ProductCustomerGroupSalesSummaryView.NetWeight,"وزن خالص").sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity,ProductCustomerGroupSalesSummaryView.NetCount_CA,"تعداد کارتن خالص").sendToDetail().setWeight(1.5f).calcTotal());
    }
}
