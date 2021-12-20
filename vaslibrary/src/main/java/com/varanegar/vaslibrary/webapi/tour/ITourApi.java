package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.vaslibrary.model.tour.TourModel;

import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 7/2/2017.
 */

public interface ITourApi {
    @GET("api/v2/ngt/tour/daytour/ByTourNo")
    Call<TourModel> getTour(@Query("tourNo") String tourNo);

    @GET("api/v2/ngt/tour/daytour")
    Call<TourModel> getTour(@Query("dealerId") String dealerId, @Query("DeviceSettingNo") String DeviceSettingNo, @Query("SubSystemTypeUniqueId") String SubSystemTypeUniqueId);

    @GET("api/v2/ngt/tour/sync/tourreceived")
    Call<ResponseBody> confirmTourReceived(@Query("id") String tourId, @Query("DeviceSettingNo") String DeviceSettingNo);

    @GET("api/v2/ngt/tour/sync/toursent")
    Call<ResponseBody> confirmTourSent(@Query("id") String tourId);

    @POST("api/v2/ngt/tour/sync/savedata")
    Call<ResponseBody> saveTourData(@Body SyncGetTourViewModel syncGetTourViewModel, @Query("DeviceSettingCode") String DeviceSettingCode);

    @GET("api/v2/ngt/tour/sync/canceltour")
    Call<ResponseBody> cancelTour(@Query("id") String tourId);

    @POST("api/v2/ngt/tour/sync/verifydata")
    Call<List<UUID>> verifyData(@Query("tourId") String tourId, @Body List<UUID> customers);

    @GET("api/v2/ngt/tour/tourstatus")
    Call<UUID> getTourStatus(@Query("id") String tourId);

    @GET("api/v2/ngt/tour/Dist/IsReadyToResetTour")
    Call<String> getRestBackup(@Query("tourNo") String tourNo,@Query("driverID") String driverID);
    @GET("api/v2/ngt/tour/Dist/BackUpRestored")
    Call<Boolean> toreRestBackup(@Query("id")String id);

}
