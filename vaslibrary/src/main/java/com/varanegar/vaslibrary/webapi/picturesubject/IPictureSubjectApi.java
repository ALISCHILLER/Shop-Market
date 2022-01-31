package com.varanegar.vaslibrary.webapi.picturesubject;

import com.varanegar.vaslibrary.manager.picture.PictureSubjectZarModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerHistoryModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureSubjectModel;

import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 7/5/2017.
 */

public interface IPictureSubjectApi {

    @GET("api/v2/ngt/picturesubject/sync/loaddata")
    Call<List<PictureSubjectModel>> getPictureSubjects(@Query("date") String date);

    @GET("api/v2/ngt/picturetemplate/sync/loaddata")
    Call<List<PictureTemplateModel>> getPictureTemplates(@Query("date") String date);

    @GET("api/v2/ngt/picturesubject/customer/sync/loaddata")
    Call<List<PictureCustomerHistoryModel>> getCustomerPictureHistory(@Query("date") String date, @Query("tourId") String tourId, @Query("dealerId") String dealerId, @Query("deviceSettingCode") String deviceSettingCode);


    @GET("api/v2/ngt/picturetemplate/sync/getTemplate")
    Call<List<PictureSubjectZarModel>> getTemplate(@Query("subSystemTypeUniqueId") UUID subSystemTypeUniqueId);
}
