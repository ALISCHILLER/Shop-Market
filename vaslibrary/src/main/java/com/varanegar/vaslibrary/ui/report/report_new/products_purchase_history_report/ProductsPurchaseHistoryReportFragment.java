package com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.model.TProductsPurchaseHistoryReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class ProductsPurchaseHistoryReportFragment extends
        BasProductsPurchaseHistoryReportFragment<TProductsPurchaseHistoryReportModel>{
    @Override
    protected Call<List<TProductsPurchaseHistoryReportModel>> reportApi() {
        return new ReportApi(getContext()).ProductsPurchaseHistoryReport
                (Collections.singletonList
                (getDealerId().toString()), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<TProductsPurchaseHistoryReportModel> createAdapter() {
        return new ProductsPurchaseHistoryReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return "گزارش خلاصه فروش کالا";
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
