package com.varanegar.vaslibrary.webapi.paymentusances;

import com.varanegar.vaslibrary.model.paymentusances.PaymentUsancesModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public interface IPaymentUsancesApi {
    @GET("api/v2/ngt/discount/PaymentUsances")
    Call<List<PaymentUsancesModel>> getPaymentUsances(@Query("Date") String Date);
}
