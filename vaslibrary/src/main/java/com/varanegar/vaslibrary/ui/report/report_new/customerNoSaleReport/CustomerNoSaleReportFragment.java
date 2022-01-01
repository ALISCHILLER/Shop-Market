package com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.CustomerPurchaseHistoryReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class CustomerNoSaleReportFragment extends BaseCustomerNoSaleReportFragment<CustomerModel>{
    @Override
    protected Call<List<String>> reportApi() {
        return new ReportApi(getContext()).CustomerNoSaleReport
                (Collections.singletonList(getDealerId().toString()),getStartDateString()
                        ,getEndDateString(),
                        getProduct_group());
    }

    @Override
    protected SimpleReportAdapter<CustomerModel> createAdapter() {
        return new CustomerNoSaleReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return "مشتریان بدون خرید ماه جاری";
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
