package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireHistoryManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireManager;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandler;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandlerV3;

import java.util.List;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.DiscountCalculatorHandler;

/**
 * Created by e.hashemzadeh on 10/19/2021.
 */
public class QuestionnaireUpdateFlow extends UpdateFlow{
    public QuestionnaireUpdateFlow(Context context) {
        super(context);
    }

    @Override
    protected void addAsyncTasks(List<TourAsyncTask> tasks, UpdateCall call) {
        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                QuestionnaireManager questionnaireManager = new QuestionnaireManager(getContext());
                questionnaireManager.sync(call);
            }

            @Override
            public String name() {
                return "Questionnaire";
            }

            @Override
            public int group() {
                return R.string.questionnairie_info;
            }

            @Override
            public int queueId() {
                return 0;
            }
        });
        tasks.add(new SimpleTourAsyncTask() {
            @Override
            public void run(UpdateCall call) {
                QuestionnaireHistoryManager questionnaireHistoryManager = new QuestionnaireHistoryManager(getContext());
                questionnaireHistoryManager.sync(call);
            }

            @Override
            public String name() {
                return "QuestionnaireHistory";
            }

            @Override
            public int group() {
                return R.string.questionnairie_info;
            }

            @Override
            public int queueId() {
                return 1;
            }
        });
    }

    public void syncQuestionnaire(@NonNull final UpdateCall call) {
        start(new UpdateCall() {
            @Override
            protected void onSuccess() {
                call.onSuccess();
            }

            @Override
            protected void onFailure(String error) {
                call.failure(error);
            }
        });
    }
}
