package com.varanegar.vaslibrary.webapi.personnel;

import com.varanegar.vaslibrary.model.user.UserModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by atp on 6/7/2017.
 */

public interface IUserApi {
    @GET("api/v2/ngt/devicesetting/{id}/users")
    Call<List<UserModel>> getAll(@Path("id") String id);
    @GET("api/v2/ngt/supervisorsetting/users")
    Call<List<UserModel>> getSupervisorUsers();
    @POST("api/accounts/changepassword")
    Call<ResponseBody> changePass(@Body ChangePasswordViewModel viewModel);
}
