package com.varanegar.vaslibrary.webapi.dataforregister;

import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IDataForRegisterApi {
    @GET("api/v2/ngt/customer/GetDataForRegister")
    Call<List<DataForRegisterModel>> getAll();
}
