package com.varanegar.vaslibrary.webapi.customer;

import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerBarcodeModel;
import com.varanegar.vaslibrary.model.customer.CustomerCategoryModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerMainSubTypeModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;

import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by atp on 5/31/2017.
 */

public interface ICustomerApi {
    @GET("api/v2/ngt/customer/sync/loaddata")
    Call<List<CustomerModel>> get(
            @Query("Date") String dateAfter,
            @Query("dealerId") String dealerId,
            @Query("customerId") String customerId,
            @Query("DeviceSettingNo") String DeviceSettingNo);

    @GET("api/v2/ngt/customer/DistributionCustomers")
    Call<List<CustomerModel>> get(@Query("TourId") String tourId);

    @POST("api/v2/ngt/customer/registernewcustomer")
    Call<SyncGuidViewModel> registerNewCustomer(
            @Body SyncGetNewCustomerViewModel syncGetNewCustomerViewModel);


    @POST("api/v2/ngt/customer/registernewZarCustomer")
    Call<SyncGuidViewModel> registerNewZarCustomer(
            @Body SyncZarGetNewCustomerViewModel syncGetNewCustomerViewModel
    );

    @Multipart
    @POST("api/v2/ngt/customer/registernewZarCustomerNationalCardImage")
    Call<Boolean> registerNewZarCustomerNationalCardImage(
            @Query("customerId") UUID customerId,
            @Part MultipartBody.Part file);

    @GET("api/v2/ngt/customermainsubtype")
    Call<List<CustomerMainSubTypeModel>> getCustomerMainSubTypes();

    @GET("api/v2/ngt/customerActivity/sync/loaddata")
    Call<List<CustomerActivityModel>> getCustomerActivities();

    @GET("api/v2/ngt/customerCategory/sync/loaddata")
    Call<List<CustomerCategoryModel>> getCustomerCategories();

    @GET("api/v2/ngt/customerlevel/sync/loaddata")
    Call<List<CustomerLevelModel>> getCustomerLevels();

    @GET("api/v2/ngt/customer/FinanceData")
    Call<List<SyncSendCustomerFinanceDataViewModel>> getFinanceData(
            @Query("SubSystemTypeUniqueId") String subSystemTypeUniqueId,
            @Query("TourId") String tourId,
            @Query("DealerId") String dealerId,
            @Query("DeviceSettingNo") String deviceSettingNo,
            @Query("CustomerId") String customerId);

    @GET("api/v2/ngt/customer/customerbarcode")
    Call<List<CustomerBarcodeModel>> getCustomerBarcode(
            @Query("Date") String dateAfter,
            @Query("dealerId") String dealerId,
            @Query("DeviceSettingNo") String DeviceSettingNo);

    @GET("api/v2/ngt/customer/AdditionalInfo")
    Call<List<CustomerAdditionalInfoModel>> getCustomerAdditionalInfo(
            @Query("CustomerId") String customerId);

    @GET("api/v2/ngt/customer/GetFullData")
    Call<ZarCustomerInfoViewModel> getCustomerZarCustomerInfo(
            @Query("code") String customerCode);

    @POST("api/v2/ngt/customer/updateZarCustomer")
    Call<String> postCustomerZarCustomerInfo(
            @Body SyncZarGetNewCustomerViewModel customerInfoViewModel);

}
