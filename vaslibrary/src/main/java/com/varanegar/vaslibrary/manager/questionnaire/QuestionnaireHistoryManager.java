package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHistoryModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHistoryModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.questionnaire.QuestionnaireApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/4/2017.
 */

public class QuestionnaireHistoryManager extends BaseManager<QuestionnaireHistoryModel> {
    public QuestionnaireHistoryManager(@NonNull Context context) {
        super(context, new QuestionnaireHistoryModelRepository());
    }

    public void sync(final UpdateCall call) {
        QuestionnaireApi questionaireApi = new QuestionnaireApi(getContext());
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.CustomerQuestionnaire);
        String dateAfter = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        questionaireApi.runWebRequest(questionaireApi.getCustomerQuestionnaire(dateAfter), new WebCallBack<List<QuestionnaireHistoryModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<QuestionnaireHistoryModel> result, Request request) {
                if (result.size() > 0) {
                    List<QuestionnaireHistoryModel> q = Linq.findAll(result, new Linq.Criteria<QuestionnaireHistoryModel>() {
                        @Override
                        public boolean run(QuestionnaireHistoryModel item) {
                            return item.CustomerId != null;
                        }
                    });
                    if (q.size() > 0) {
                        try {
                            sync(q);
                            new UpdateManager(getContext()).addLog(UpdateKey.CustomerQuestionnaire);
                            Timber.i("Updating customer questionnaire completed");
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        call.success();
                        Timber.d("List of Questionnaire was empty");
                    }
                } else {
                    call.success();
                    Timber.d("List of Questionnaire was empty");
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                call.failure(err);
            }

            @Override
            public void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(getContext().getString(R.string.network_error));
            }
        });
    }
}
