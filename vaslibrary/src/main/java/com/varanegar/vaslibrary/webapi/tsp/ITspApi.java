package com.varanegar.vaslibrary.webapi.tsp;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ITspApi {
    @GET("/vrp/tsp/v1/driving/{Coordinates}")
    @Headers("x-api-key: eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjQ5ZDczNzc5M2NjNjRjZmEzZDI2MDg3NDk2ZGI4ZGIxM2JiZjFjNjIxMTY4MTRmMTI3MjgxZTkzNmQxMzBhY2E3ZmM0MmE0YzRlZjEwZWFmIn0.eyJhdWQiOiIxNTMxMyIsImp0aSI6IjQ5ZDczNzc5M2NjNjRjZmEzZDI2MDg3NDk2ZGI4ZGIxM2JiZjFjNjIxMTY4MTRmMTI3MjgxZTkzNmQxMzBhY2E3ZmM0MmE0YzRlZjEwZWFmIiwiaWF0IjoxNjMwMzQwNTQ4LCJuYmYiOjE2MzAzNDA1NDgsImV4cCI6MTYzMzAyMjU0OCwic3ViIjoiIiwic2NvcGVzIjpbImJhc2ljIl19.cdzj4Mh_Vt8Yev3ySEi5iNanapVocZVg85CiNiYvRxCc4v87AWtVEqKSgyL1MRvzrYxxruxIXHw_9TNH1IYi7jHb2VBuDQfyCaRaAV3DIzWd2UzDDXtRjGW6_-kxlsEhMyqCnrR2lX24Ny2UJDK5PMnEYJdUqLKO1gIl0D0eFTQX-7leLAYsl3smklpj3QM2WqMlnV0uV_WydcQ8GatTDQ0BBlvYiVH3J3T3lr1nIYUEpbz8z4SA86qvIluun7mCfSXiBJjNs_SNZE1moY0KZmImvTvQtZbSu-WJi_bcEQPuLJf1pXMIiqHnrh5qDtTVVbiAlViASMXfffdvDhVDoA")
    Call<JsonObject> getRoute(@Path("Coordinates") String coordinates,
                              @Query("roundtrip") boolean roundTrip,
                              @Query("source") String source,
                              @Query("destination") String destination,
                              @Query("steps") boolean steps);
}
