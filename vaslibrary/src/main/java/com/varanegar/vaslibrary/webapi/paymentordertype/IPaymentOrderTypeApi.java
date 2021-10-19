package com.varanegar.vaslibrary.webapi.paymentordertype;

import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/7/2017.
 */

public interface IPaymentOrderTypeApi
{
    @GET("api/v2/ngt/paymenttypeorder")
    Call<List<PaymentTypeOrderModel>> getAll(@Query("DeviceSettingNo") String DeviceSettingNo);
}
