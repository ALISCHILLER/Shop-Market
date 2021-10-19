package com.varanegar.vaslibrary.webapi.questionnaire;

import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHistoryModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 7/3/2017.
 */

public interface IQuestionnaireApi {
    @GET("api/v2/ngt/questionnaire/sync/loaddata")
    Call<List<QuestionnaireHeaderViewModel>> getQuestionnaireHeaders(@Query("date") String date);
    @GET("api/v2/ngt/questionnaire/customer/sync/loaddata")
    Call<List<QuestionnaireHistoryModel>> getCustomerQuestionnaire(@Query("date") String date);

}
