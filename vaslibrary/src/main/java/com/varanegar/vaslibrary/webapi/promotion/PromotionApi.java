package com.varanegar.vaslibrary.webapi.promotion;

import android.content.Context;

import com.varanegar.vaslibrary.ui.fragment.ReturnDisEvcHeaderViewModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 3/26/2018.
 */

public class PromotionApi extends BaseApi implements IPromotionApi {
    public PromotionApi(Context context) {
        super(context);
    }

    @Override
    public Call<ReturnDistViewModel> returnDisControl(List<ReturnDisEvcHeaderViewModel> body) {
        IPromotionApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IPromotionApi.class);
        return api.returnDisControl(body);
    }
}
