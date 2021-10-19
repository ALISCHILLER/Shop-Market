package com.varanegar.vaslibrary.webapi.paymentordertype;

import android.content.Context;

import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/7/2017.
 */

public class PaymentOrderTypeApi extends BaseApi implements IPaymentOrderTypeApi
{

    public PaymentOrderTypeApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<PaymentTypeOrderModel>> getAll(@Query("DeviceSettingNo") String DeviceSettingNo) {
        IPaymentOrderTypeApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IPaymentOrderTypeApi.class);
        return api.getAll(DeviceSettingNo);
    }

}
