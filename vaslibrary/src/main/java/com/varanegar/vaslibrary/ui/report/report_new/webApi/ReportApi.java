package com.varanegar.vaslibrary.ui.report.report_new.webApi;

import android.content.Context;

import com.varanegar.vaslibrary.ui.report.report_new.model.ProductInvoiveBalanceReportViewModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

public class InvoiceReportApi extends BaseApi implements IntInvoiceReportApi {
    public InvoiceReportApi(Context context) {
        super(context);
    }


    @Override
    public Call<List<ProductInvoiveBalanceReportViewModel>> product(List<String> dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IntInvoiceReportApi.class).product(dealerId, startDate, endDate);
    }
}
