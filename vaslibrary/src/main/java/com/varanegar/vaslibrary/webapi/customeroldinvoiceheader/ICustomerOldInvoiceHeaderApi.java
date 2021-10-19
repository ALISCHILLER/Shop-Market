package com.varanegar.vaslibrary.webapi.customeroldinvoiceheader;

import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/24/2017.
 */

public interface ICustomerOldInvoiceHeaderApi {
    @GET("api/v2/ngt/invoice/delete/sqlite/{filename}")
    Call<ResponseBody> deleteSQLite(@Path("filename") String fileName);

    @GET("api/v2/ngt/invoice/sync/sqllite")
    Call<ResponseBody> invoiceSQLite(@Query("tourId") String tourId, @Query("dealerId") String dealerId, @Query("Date") String dateAfter, @Query("deviceSettingNo") String deviceSettingNo, @Query("ReturnOldInvoice") boolean ReturnOldInvoice, @Query("SubSystemTypeUniqueId") UUID SubSystemTypeUniqueId);

    @GET("api/v2/ngt/invoice/sync/loaddata")
    Call<CustomerOldInvoicesViewModel> invoice(@Query("tourId") String tourId, @Query("dealerId") String dealerId, @Query("Date") String dateAfter, @Query("deviceSettingNo") String deviceSettingNo);

    @GET("api/v2/ngt/invoice/sync/loaddata")
    Call<CustomerOldInvoicesViewModel> invoice(@Query("tourId") String tourId, @Query("dealerId") String dealerId, @Query("Date") String dateAfter, @Query("deviceSettingNo") String deviceSettingNo, @Query("customerId") String customerId, @Query("startDate") String startDate, @Query("endDate") String endDate);
}
