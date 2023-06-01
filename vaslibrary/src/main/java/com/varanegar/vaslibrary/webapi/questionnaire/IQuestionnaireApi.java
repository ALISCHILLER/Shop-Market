package com.varanegar.vaslibrary.webapi.questionnaire;

import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHistoryModel;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 7/3/2017.
 */

public interface IQuestionnaireApi {
    /**
     * اطلاعات پرسش نامه مشتریان
     * @param date
     * @return
     */
    @GET("api/v2/ngt/questionnaire/sync/loaddata")
    Call<List<QuestionnaireHeaderViewModel>> getQuestionnaireHeaders(@Query("date") String date
            ,@Query("SubSystemType") UUID SubSystemType);

    /**
     * تاریخچه پرس شنانمه های که جواد داده
     * @param date
     * @return
     */
    @GET("api/v2/ngt/questionnaire/customer/sync/loaddata")
    Call<List<QuestionnaireHistoryModel>> getCustomerQuestionnaire(@Query("date") String date);

}
