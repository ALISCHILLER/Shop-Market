package com.varanegar.vaslibrary.webapi.discount;

import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteTemplateModel;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 11/20/2017.
 */

public interface IEvcStatuteApi {
    @GET("api/v2/ngt/evcstatute/sync/loaddata")
    Call<List<EvcStatuteTemplateModel>> get(@Query("SubSystemType")UUID subSystemType);
}
