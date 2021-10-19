package com.varanegar.vaslibrary.webapi.product;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.tour.SyncGetTourViewModel;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 4/4/2018.
 */

public class RequestProductApi extends BaseApi implements IRequestProductAPi {
    public RequestProductApi(Context context) {
        super(context);
    }

    @Override
    public Call<ResponseBody> saveRequestData(SyncGetTourViewModel syncGetTourViewModel, String DeviceSettingCode) {
        IRequestProductAPi api = getRetrofitBuilder(TokenType.UserToken).build().create(IRequestProductAPi.class);
        return api.saveRequestData(syncGetTourViewModel, DeviceSettingCode);
    }
}