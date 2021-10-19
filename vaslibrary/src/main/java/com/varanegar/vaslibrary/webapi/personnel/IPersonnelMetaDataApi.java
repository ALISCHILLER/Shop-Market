package com.varanegar.vaslibrary.webapi.personnel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 12/21/2017.
 */

public interface IPersonnelMetaDataApi {
    @GET("api/v2/ngt/personnel/metadata")
    Call<PersonnelMetaDataViewModel> getPersonalMetaData(@Query("PersonnelId") String PersonnelId, @Query("SubSystemId") String SubSystemId);
}
