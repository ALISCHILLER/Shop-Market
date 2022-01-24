package com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.model.ProductCustomerGroupSalesSummaryModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class CustomerGroupSalesSummaryFragment extends BaseCustomerGroupSalesSummaryFragment<ProductCustomerGroupSalesSummaryModel> {

    @Override
    protected Call<List<ProductCustomerGroupSalesSummaryModel>> reportApi() {
        return new ReportApi(getContext()).CustomerGroupSales(Collections.singletonList(getDealerId()
                .toString()), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<ProductCustomerGroupSalesSummaryModel> createAdapter() {
        return new CustomerGroupSalesSummaryAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return "گزارش خلاصه فروش گروه مشتری";
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
