package com.varanegar.vaslibrary.webapi.imageapi;

import com.varanegar.vaslibrary.model.image.LogoModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by e.hashemzadeh on 6/11/2019.
 */

public interface ILogoApi {
    @GET("api/imageManager/getbytype")
    Call<List<LogoModel>> logoInfo(@Query("imageType") String type);
}
