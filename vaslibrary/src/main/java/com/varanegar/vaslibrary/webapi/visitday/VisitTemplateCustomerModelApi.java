package com.varanegar.vaslibrary.webapi.visitday;

import android.content.Context;

import com.varanegar.vaslibrary.model.VisitTemplatePathCustomer.VisitTemplatePathCustomerModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */

public class VisitTemplateCustomerModelApi extends BaseApi implements IVisitTemplatePathCustomerModelApi {

    public VisitTemplateCustomerModelApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<VisitTemplatePathCustomerModel>> getAll(@Query("Date") String dateAfter, @Query("dealerId") String dealerId, @Query("DeviceSettingNo") String DeviceSettingNo, @Query("customerId") String customerId) {
        IVisitTemplatePathCustomerModelApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IVisitTemplatePathCustomerModelApi.class);
        return api.getAll(dateAfter, dealerId, DeviceSettingNo, customerId);
    }
}
