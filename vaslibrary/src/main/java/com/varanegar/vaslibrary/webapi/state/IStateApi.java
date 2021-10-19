package com.varanegar.vaslibrary.webapi.state;

import com.varanegar.vaslibrary.model.state.StateModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public interface IStateApi {
    @GET("api/v2/ngt/state/sync/loaddata")
    Call<List<StateModel>> getStates(@Query("Date") String dateAfter);
}
