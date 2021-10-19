package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireManager;
import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.ui.fragment.QuestionnaireFragment;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/3/2017.
 */

public class CustomerQuestionnaireAction extends CheckBarcodeAction {
    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("BC4EFB46-FE38-4087-B24A-802F0C38716D");
    }

    public CustomerQuestionnaireAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        this.icon = R.drawable.ic_question_answer_white_24dp;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.questionaire);
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (getCallManager().isLackOfVisit(getCalls()))
            return getActivity().getString(R.string.customer_is_not_visited);
        TaskPriorityModel taskPriorityModel = getTaskPriorities().get(getTaskUniqueId());
        if (((VasActionsAdapter) getAdapter()).checkPriorities() && taskPriorityModel != null && !taskPriorityModel.IsEnabled && isMandatory() == null)
            return getActivity().getString(R.string.the_action_is_disabled_for_you);
        return null;
    }

    @Override
    public boolean isDone() {
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        return callManager.isQuestionnaireAnswered(getCalls());
    }

    @Nullable
    @Override
    protected String isMandatory() {
        if (getCallManager().isLackOfVisit(getCalls()))
            return null;
        return new QuestionnaireCustomerViewManager(getActivity()).checkMandatoryQuestionnaire(getSelectedId());
    }

    @Override
    public void run() {
        QuestionnaireManager questionnaireManager = new QuestionnaireManager(getActivity());
        try {
            questionnaireManager.calculateCustomerQuestionnaire(getSelectedId());
            QuestionnaireCustomerViewManager questionnaireCustomerViewManager = new QuestionnaireCustomerViewManager(getActivity());
            List<QuestionnaireCustomerViewModel> questionnaireCustomerViewModels = questionnaireCustomerViewManager.getQuestionnaires(getSelectedId());
            if (questionnaireCustomerViewModels.size() == 0) {
                getActivity().showSnackBar(R.string.no_questionnaire_for_this_customer, MainVaranegarActivity.Duration.Short);
            } else {
                QuestionnaireFragment fragment = new QuestionnaireFragment();
                fragment.setCustomerId(getSelectedId());
                getActivity().pushFragment(fragment);
            }
        } catch (Exception e) {
            Timber.e(e);
            getActivity().showSnackBar(R.string.calculating_questionnaire_failed, MainVaranegarActivity.Duration.Short);
        }
    }
}
