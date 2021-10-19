package com.varanegar.vaslibrary.webapi.product;

import com.varanegar.vaslibrary.model.emphaticproduct.EmphaticProductModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by A.Torabi on 7/10/2017.
 */

public interface IEmphaticProductApi {
    @GET("api/v2/ngt/emphasisproduct/sync/loaddata")
    Call<List<EmphaticProductModel>> getAll();
}
