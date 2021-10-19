package com.varanegar.vaslibrary.webapi.advancedealercreditcontrol;

import android.content.Context;

import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 8/7/2018.
 */

public class AdvanceDealerCreditControlApi extends BaseApi implements IAdvanceDealerCreditControlApi {
    public AdvanceDealerCreditControlApi(Context context) {
        super(context);
    }

    @Override
    public Call<SysConfigModel> getAdvanceDealerCreditsControl(String id) {
        IAdvanceDealerCreditControlApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IAdvanceDealerCreditControlApi.class);
        return api.getAdvanceDealerCreditsControl(id);
    }
}
