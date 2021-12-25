package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireDemandType;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHeaderModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOptionModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.questionnaire.QuestionnaireApi;
import com.varanegar.vaslibrary.webapi.questionnaire.QuestionnaireHeaderViewModel;
import com.varanegar.vaslibrary.webapi.questionnaire.QuestionnaireLineViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/3/2017.
 */

public class QuestionnaireManager {
    private Context context;

    public QuestionnaireManager(@NonNull Context context) {
        this.context = context;
    }

    public void sync(final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(context);
        final QuestionnaireApi questionnaireApi = new QuestionnaireApi(context);
        questionnaireApi.runWebRequest(questionnaireApi.getQuestionnaireHeaders(null,VaranegarApplication.getInstance().getAppId()), new WebCallBack<List<QuestionnaireHeaderViewModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<QuestionnaireHeaderViewModel> result, Request request) {
                if (result != null && result.size() > 0) {
                    List<QuestionnaireHeaderModel> questionnaireHeaderModels = new ArrayList<>();
                    final List<QuestionnaireLineModel> questionnaireLineModels = new ArrayList<>();
                    final List<QuestionnaireLineOptionModel> questionnaireLineOptionModels = new ArrayList<>();
                    for (final QuestionnaireHeaderViewModel questionnaireHeaderViewModel : result) {
                        questionnaireHeaderModels.add(questionnaireHeaderViewModel.convert());
                        for (final QuestionnaireLineViewModel questionnaireLineViewModel : questionnaireHeaderViewModel.questionnaireLines) {
                            questionnaireLineModels.add(questionnaireLineViewModel.convert());
                            questionnaireLineOptionModels.addAll(questionnaireLineViewModel.questionnaireLineOptions);
                        }
                    }
                    if (questionnaireHeaderModels.size() > 0) {
                        QuestionnaireHeaderManager questionnaireHeaderManager = new QuestionnaireHeaderManager(context);
                        try {
                            questionnaireHeaderManager.sync(questionnaireHeaderModels);
                            Timber.i("List of questionnaire headers updated");
                            if (questionnaireLineModels.size() > 0) {
                                new QuestionnaireLineManager(context).sync(questionnaireLineModels);
                                Timber.i("List of Questionnaire Lines updated");
                                if (questionnaireLineOptionModels.size() > 0) {
                                    new QuestionnaireLineOptionManager(context).insertOrUpdate(questionnaireLineOptionModels);
                                    Timber.i("List of Questionnaire line options updated");
                                    call.success();
                                } else {
                                    call.success();
                                    Timber.d("List of Questionnaire line options was empty");
                                }
                            } else {
                                call.success();
                                Timber.d("List of Questionnaire lines was empty");
                            }
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.failure(context.getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            call.failure(context.getString(R.string.data_error));
                        }
                    } else {
                        call.success();
                        Timber.d("List of Questionnaire headers was empty");
                    }
                } else {
                    call.success();
                    Timber.i("List of Questionnaire headers was empty");
                }

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(context.getString(R.string.network_error));
            }
        });
    }

    public void calculateCustomerQuestionnaire(final UUID customerId) throws DbException, ValidationException {
        // step 1: extract valid templates for the customer
        QuestionnaireHeaderManager headerManager = new QuestionnaireHeaderManager(context);
        List<QuestionnaireHeaderModel> headers;
        if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor))
            headers = headerManager.getValidTemplatesForSupervisorCustomer(customerId);
        else
            headers = headerManager.getValidTemplatesForCustomer(customerId);
        if (headers.size() == 0) {
            Timber.i("No Questionnaire template has been calculated for customer id = " + customerId.toString());
            return;
        }
        // step 2: find corresponding subjects according to the picture templates that we found in the step 1
        QuestionnaireCustomerManager questionnaireCustomerManager = new QuestionnaireCustomerManager(context);
        questionnaireCustomerManager.saveQuestionnaires(customerId, headers);
    }

    public String checkMandatoryQuestionnaire(final UUID customerId) {
        CustomerCallManager callManager = new CustomerCallManager(context);
        boolean lakOfVisit = callManager.isLackOfVisit(callManager.loadCalls(customerId));
        if (lakOfVisit)
            return null;
        try {
            calculateCustomerQuestionnaire(customerId);
            QuestionnaireCustomerViewManager questionnaireCustomerViewManager = new QuestionnaireCustomerViewManager(context);
            List<QuestionnaireCustomerViewModel> questionnaires = questionnaireCustomerViewManager.getQuestionnaires(customerId);
            if (questionnaires.size() == 0)
                return null;
            QuestionnaireCustomerViewModel force = Linq.findFirst(questionnaires, new Linq.Criteria<QuestionnaireCustomerViewModel>() {
                @Override
                public boolean run(QuestionnaireCustomerViewModel item) {
                    return item.DemandType == QuestionnaireDemandType.Mandatory && !item.HasAnswer && item.NoAnswerReason == null;
                }
            });
            if (force == null)
                return null;
            else
                return context.getString(R.string.answering_questionnaire) + " " + force.Title + " " + context.getString(R.string.is_mandatory);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
