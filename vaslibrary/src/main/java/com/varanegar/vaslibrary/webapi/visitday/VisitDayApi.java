package com.varanegar.vaslibrary.webapi.visitday;

import android.content.Context;

import com.varanegar.vaslibrary.model.visitday.VisitDayModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/27/2017.
 */

public class VisitDayApi extends BaseApi implements IVisitDayApi {
    public VisitDayApi(Context context) {
        super(context);
    }
    @Override
    public Call<List<VisitDayModel>> getAll(@Query("Date") String dateAfter, @Query("dealerId") String dealerId) {
        IVisitDayApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IVisitDayApi.class);
        return api.getAll(dateAfter, dealerId);
    }
}
