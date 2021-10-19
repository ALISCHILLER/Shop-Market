package com.varanegar.vaslibrary.webapi.customeroldinvoiceheader;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/24/2017.
 */

public class CustomerOldInvoiceHeaderApi extends BaseApi implements ICustomerOldInvoiceHeaderApi {
    public CustomerOldInvoiceHeaderApi(Context context) {
        super(context);
    }

    @Override
    public Call<ResponseBody> deleteSQLite(String fileName) {
        ICustomerOldInvoiceHeaderApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerOldInvoiceHeaderApi.class);
        return api.deleteSQLite(fileName);
    }

    @Override
    public Call<ResponseBody> invoiceSQLite(String tourId, String dealerId, String dateAfter, String deviceSettingNo, boolean ReturnOldInvoice, UUID SubSystemTypeUniqueId) {
        ICustomerOldInvoiceHeaderApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerOldInvoiceHeaderApi.class);
        return api.invoiceSQLite(tourId, dealerId, dateAfter, deviceSettingNo, ReturnOldInvoice, SubSystemTypeUniqueId);
    }

    @Override
    public Call<CustomerOldInvoicesViewModel> invoice(@Query("tourId") String tourId, @Query("dealerId") String dealerId, @Query("Date") String dateAfter, @Query("deviceSettingNo") String deviceSettingNo) {
        ICustomerOldInvoiceHeaderApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerOldInvoiceHeaderApi.class);
        return api.invoice(tourId, dealerId, dateAfter, deviceSettingNo);
    }

    @Override
    public Call<CustomerOldInvoicesViewModel> invoice(String tourId, String dealerId, String dateAfter, String deviceSettingNo, String customerId, String startDate, String endDate) {
        ICustomerOldInvoiceHeaderApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ICustomerOldInvoiceHeaderApi.class);
        return api.invoice(tourId, dealerId, dateAfter, deviceSettingNo, customerId, startDate, endDate);
    }
}
