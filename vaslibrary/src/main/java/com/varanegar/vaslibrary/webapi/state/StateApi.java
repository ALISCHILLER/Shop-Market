package com.varanegar.vaslibrary.webapi.state;

import android.content.Context;

import com.varanegar.vaslibrary.model.state.StateModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public class StateApi extends BaseApi implements IStateApi {
    public StateApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<StateModel>> getStates(String dateAfter) {
        IStateApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IStateApi.class);
        return api.getStates(dateAfter);
    }
}
