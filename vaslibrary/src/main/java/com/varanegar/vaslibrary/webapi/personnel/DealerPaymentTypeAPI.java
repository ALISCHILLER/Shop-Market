package com.varanegar.vaslibrary.webapi.personnel;

import android.content.Context;

import com.varanegar.vaslibrary.model.dealerPaymentType.DealerPaymentTypeModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by g.aliakbar on 13/03/2018.
 */

public class DealerPaymentTypeAPI extends BaseApi implements IDealerPaymentTypeAPI {
    public DealerPaymentTypeAPI(Context context) {
        super(context);
    }

    @Override
    public Call<List<DealerPaymentTypeModel>> getDealerPaymentType(String personnelId) {
        IDealerPaymentTypeAPI api = getRetrofitBuilder(TokenType.UserToken).build().create(IDealerPaymentTypeAPI.class);
        return api.getDealerPaymentType(personnelId);
    }
}
