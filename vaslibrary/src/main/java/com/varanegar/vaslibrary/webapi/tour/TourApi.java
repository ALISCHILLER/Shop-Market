package com.varanegar.vaslibrary.webapi.tour;

import android.content.Context;

import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 7/2/2017.
 */

public class TourApi extends BaseApi implements ITourApi {
    public TourApi(Context context) {
        super(context);
    }

    @Override
    public Call<TourModel> getTour(String tourNo) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.getTour(tourNo);
    }

    @Override
    public Call<TourModel> getTour(@Query("dealerId") String dealerId, @Query("DeviceSettingNo") String DeviceSettingNo, @Query("SubSystemTypeUniqueId") String SubSystemTypeUniqueId) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.getTour(dealerId, DeviceSettingNo, SubSystemTypeUniqueId);
    }

    @Override
    public Call<ResponseBody> confirmTourReceived(@Query("id") String tourId, @Query("DeviceSettingNo") String DeviceSettingNo) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.confirmTourReceived(tourId,DeviceSettingNo);
    }

    @Override
    public Call<ResponseBody> confirmTourSent(@Query("id") String tourId) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.confirmTourSent(tourId);
    }

    @Override
    public Call<ResponseBody> saveTourData(@Body SyncGetTourViewModel syncGetTourViewModel, String DeviceSettingCode) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.saveTourData(syncGetTourViewModel, DeviceSettingCode);
    }

    @Override
    public Call<ResponseBody> cancelTour(@Query("id") String tourId) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.cancelTour(tourId);
    }

    @Override
    public Call<List<UUID>> verifyData(String tourId, List<UUID> customers) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.verifyData(tourId,customers);
    }

    @Override
    public Call<UUID> getTourStatus(String tourId) {
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.getTourStatus(tourId);
    }
    @Override
    public Call<String> getRestBackup(String tourNo,String driverID){
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.getRestBackup(tourNo,driverID);
    }

    @Override
    public Call<Boolean> toreRestBackup(String id){
        ITourApi api = getRetrofitBuilder(TokenType.UserToken).build().create(ITourApi.class);
        return api.toreRestBackup(id);

    }

}
