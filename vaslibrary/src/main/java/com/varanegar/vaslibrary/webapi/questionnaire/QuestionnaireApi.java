package com.varanegar.vaslibrary.webapi.questionnaire;

import android.content.Context;

import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHistoryModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 7/3/2017.
 */

public class QuestionnaireApi extends BaseApi implements IQuestionnaireApi {

    public QuestionnaireApi(Context context) {
        super(context);
    }


    @Override
    public Call<List<QuestionnaireHeaderViewModel>> getQuestionnaireHeaders(@Query("date") String date) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IQuestionnaireApi.class).getQuestionnaireHeaders(date);

    }

    @Override
    public Call<List<QuestionnaireHistoryModel>> getCustomerQuestionnaire(@Query("date") String date) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IQuestionnaireApi.class).getCustomerQuestionnaire(date);

    }
}
