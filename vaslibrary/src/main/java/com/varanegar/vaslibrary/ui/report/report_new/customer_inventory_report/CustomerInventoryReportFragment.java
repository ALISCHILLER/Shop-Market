package com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report;

import androidx.annotation.NonNull;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.CustomerInventoryReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.PCustomerInventoryReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class CustomerInventoryReportFragment extends BaseCustomerInventoryReportFragment<PCustomerInventoryReportModel> {

    @Override
    protected Call<List<PCustomerInventoryReportModel>> reportApi() {
        return new ReportApi(getContext()).inventoryReport(getInventoryRequest());
    }

    @Override
    protected SimpleReportAdapter<PCustomerInventoryReportModel> createAdapter() {
        return new CustomerInventoryReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return "گزارش موجودی مشتری";
    }


    @NonNull
    @Override
    protected UUID getCustomerId() {
        return UUID.fromString(getStringArgument("67485d97-5f0e-4b1e-9677-0798dec7a587"));
    }

}
