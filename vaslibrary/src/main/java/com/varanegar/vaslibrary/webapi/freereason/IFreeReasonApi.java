package com.varanegar.vaslibrary.webapi.freereason;

import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by A.Torabi on 7/26/2017.
 */

public interface IFreeReasonApi {
    @GET("api/v2/ngt/freereason")
    Call<List<FreeReasonModel>> getAll();
}
