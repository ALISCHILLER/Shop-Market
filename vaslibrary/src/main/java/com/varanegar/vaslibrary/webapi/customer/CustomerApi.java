package com.varanegar.vaslibrary.webapi.customer;

import android.content.Context;

import com.varanegar.vaslibrary.manager.picture.ImageViewModel;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerBarcodeModel;
import com.varanegar.vaslibrary.model.customer.CustomerCategoryModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerMainSubTypeModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.io.File;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Query;

/**
 * Created by atp on 5/31/2017.
 */

public class CustomerApi extends BaseApi implements ICustomerApi {

    public CustomerApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<CustomerModel>> get(
            @Query("Date") String dateAfter,
            @Query("dealerId") String dealerId,
            @Query("customerId") String customerId,
            @Query("DeviceSettingNo") String DeviceSettingNo) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.get(dateAfter, dealerId, customerId, DeviceSettingNo);
    }

    @Override
    public Call<List<CustomerModel>> get(String tourId) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.get(tourId);
    }

    @Override
    public Call<SyncGuidViewModel> registerNewCustomer(
            @Body SyncGetNewCustomerViewModel syncGetNewCustomerViewModel) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.registerNewCustomer(syncGetNewCustomerViewModel);
    }

   @Override
    public Call<SyncGuidViewModel> registerNewZarCustomer(
            SyncZarGetNewCustomerViewModel syncGetNewCustomerViewModel
            ) {
       ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
               .build().create(ICustomerApi.class);
       return api.registerNewZarCustomer(syncGetNewCustomerViewModel);
    }

    @Override
    public Call<Boolean> registernewNationalCardImage(UUID CustomeId) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.registernewNationalCardImage(CustomeId);
    }

    @Override

    public Call<Boolean> registerNewZarCustomerNationalCardImage(
            UUID customerId,
            MultipartBody.Part body) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.registerNewZarCustomerNationalCardImage(customerId,body);
    }

    public Call<Boolean> registerNewZarCustomerNationalCardImage(
            UUID customerId,
            File file) {
//        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
//                .build().create(ICustomerApi.class);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data;"),
                        file
                );


        MultipartBody.Part body =
                MultipartBody.Part.createFormData(
                        file.getName(),
                        file.getName(),
                        requestFile);

        return registerNewZarCustomerNationalCardImage(customerId, body);

    }

    @Override
    public Call<List<CustomerMainSubTypeModel>> getCustomerMainSubTypes() {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getCustomerMainSubTypes();
    }

    @Override
    public Call<List<CustomerActivityModel>> getCustomerActivities() {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getCustomerActivities();
    }

    @Override
    public Call<List<CustomerCategoryModel>> getCustomerCategories() {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getCustomerCategories();
    }

    @Override
    public Call<List<CustomerLevelModel>> getCustomerLevels() {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getCustomerLevels();
    }

    @Override
    public Call<List<SyncSendCustomerFinanceDataViewModel>> getFinanceData(
            String subSystemTypeUniqueId,
            String tourId,
            String dealerId,
            String deviceSettingNo,
            String customerId) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getFinanceData(subSystemTypeUniqueId,
                tourId, dealerId, deviceSettingNo, customerId);
    }

    @Override
    public Call<List<CustomerBarcodeModel>> getCustomerBarcode(
            String dateAfter, String dealerId, String deviceSettingNo) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getCustomerBarcode(dateAfter, dealerId, deviceSettingNo);
    }

    @Override
    public Call<List<CustomerAdditionalInfoModel>> getCustomerAdditionalInfo(String customerId) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getCustomerAdditionalInfo(customerId);
    }

    @Override
    public Call<ZarCustomerInfoViewModel> getCustomerZarCustomerInfo(String customerCode) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.getCustomerZarCustomerInfo(customerCode);
    }

    @Override
    public Call<String> postCustomerZarCustomerInfo(
            SyncZarGetNewCustomerViewModel customerInfoViewModel) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.postCustomerZarCustomerInfo(customerInfoViewModel);
    }

    @Override
    public Call<Boolean> CheckCustomerCredits(String customerCode) {
        ICustomerApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(ICustomerApi.class);
        return api.CheckCustomerCredits(customerCode);
    }
}