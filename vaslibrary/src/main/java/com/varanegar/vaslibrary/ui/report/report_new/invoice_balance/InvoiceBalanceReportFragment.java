package com.varanegar.vaslibrary.ui.report.report_new;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.model.ProductInvoiveBalanceReportViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class InvoiceBalanceReportFragment extends BaseInvoiceBalanceReportFragment<ProductInvoiveBalanceReportViewModel>{
    @Override
    protected Call<List<ProductInvoiveBalanceReportViewModel>> reportApi() {
        return new ReportApi(getContext()).product(Collections.singletonList(getDealerId().toString()), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<ProductInvoiveBalanceReportViewModel> createAdapter() {
        return new ProductInvoiceReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return "گزارش مانده فاکتور";
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
