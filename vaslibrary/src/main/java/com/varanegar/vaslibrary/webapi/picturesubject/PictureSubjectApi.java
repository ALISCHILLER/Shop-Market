package com.varanegar.vaslibrary.webapi.picturesubject;

import android.content.Context;

import com.varanegar.vaslibrary.manager.picture.PictureSubjectZarModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerHistoryModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureSubjectModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 7/5/2017.
 */

public class PictureSubjectApi extends BaseApi implements IPictureSubjectApi {
    public PictureSubjectApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<PictureSubjectModel>> getPictureSubjects(@Query("date") String date) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IPictureSubjectApi.class).getPictureSubjects(date);
    }

    @Override
    public Call<List<PictureTemplateModel>> getPictureTemplates(@Query("date") String date) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IPictureSubjectApi.class).getPictureTemplates(date);
    }

    @Override
    public Call<List<PictureCustomerHistoryModel>> getCustomerPictureHistory(String date, String tourId, String dealerId, String deviceSettingCode) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IPictureSubjectApi.class).getCustomerPictureHistory(date, tourId, dealerId, deviceSettingCode);
    }

    @Override
    public Call<List<PictureSubjectZarModel>> getTemplate(UUID subSystemTypeUniqueId) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IPictureSubjectApi.class).getTemplate(subSystemTypeUniqueId);
    }
}
