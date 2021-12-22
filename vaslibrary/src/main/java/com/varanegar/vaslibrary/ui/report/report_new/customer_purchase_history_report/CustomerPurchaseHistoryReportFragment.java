package com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_purchase_history_report.model.PCustomerPurchaseHistoryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class CustomerPurchaseHistoryReportFragment extends BaseCustomerPurchaseHistoryReportFragment<PCustomerPurchaseHistoryViewModel> {

    @Override
    protected Call<List<PCustomerPurchaseHistoryViewModel>> reportApi() {
        return new ReportApi(getContext()).CustomerPurchaseHistoryReport
                (Collections.singletonList(getDealerId().toString()),
                        Collections.singletonList(getCustomerUniqueId()),
                        getStartDateString(), getEndDateString());
    }
    @Override
    protected SimpleReportAdapter<PCustomerPurchaseHistoryViewModel> createAdapter() {
        return new CustomerPurchaseHistoryReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
