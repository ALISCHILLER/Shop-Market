package com.varanegar.vaslibrary.webapi.paymentusances;

import android.content.Context;

import com.varanegar.vaslibrary.model.paymentusances.PaymentUsancesModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.discount.IDiscountGoodApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public class PaymentUsancesApi extends BaseApi implements IPaymentUsancesApi {
    public PaymentUsancesApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<PaymentUsancesModel>> getPaymentUsances(String Date) {
        IPaymentUsancesApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IPaymentUsancesApi.class);
        return api.getPaymentUsances(Date);
    }
}
