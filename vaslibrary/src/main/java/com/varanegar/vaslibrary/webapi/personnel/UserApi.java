package com.varanegar.vaslibrary.webapi.personnel;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.vaslibrary.model.user.User;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;

/**
 * Created by atp on 6/7/2017.
 */

public class UserApi extends BaseApi implements IUserApi {
    public UserApi(Context context) {
        super(context);
    }


    @Override
    public Call<List<UserModel>> getAll(@Path("id") String id) {
        IUserApi userApi = getRetrofitBuilder().build().create(IUserApi.class);
        return userApi.getAll(id);
    }

    @Override
    public Call<List<UserModel>> getSupervisorUsers() {
        IUserApi userApi = getRetrofitBuilder().build().create(IUserApi.class);
        return userApi.getSupervisorUsers();
    }

    @Override
    public Call<ResponseBody> changePass(ChangePasswordViewModel viewModel) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IUserApi.class).changePass(viewModel);
    }
}
