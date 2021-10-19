package com.varanegar.vaslibrary.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.questionnaire.FormAdapter;
import com.varanegar.framework.base.questionnaire.FormView;
import com.varanegar.framework.base.questionnaire.controls.FormControl;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireAnswerManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireLineManager;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireDemandType;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/4/2017.
 */

public class QuestionnaireFormFragment extends VisitFragment {

    private UUID questionnaireId;
    private UUID customerId;
    private FormAdapter adapter;
    private QuestionnaireCustomerViewManager questionnaireCustomerViewManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        questionnaireId = UUID.fromString(getArguments().getString("79403496-ce59-4533-931e-e983eee6f75d"));
        customerId = UUID.fromString(getArguments().getString("0c91c445-4c34-4162-a6b5-17ff2ec1644b"));
        View view = inflater.inflate(R.layout.fragment_questionnaire_form, container, false);
        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().popFragment();
            }
        });

        FormView formView = (FormView) view.findViewById(R.id.form_view);
        questionnaireCustomerViewManager = new QuestionnaireCustomerViewManager(getContext());
        final QuestionnaireCustomerViewModel questionnaireCustomerViewModel = questionnaireCustomerViewManager.getCustomerQuestionnaire(customerId, questionnaireId);
        if (questionnaireCustomerViewModel == null)
            throw new NullPointerException("Questionnaire id not found");
        toolbar.setTitle(questionnaireCustomerViewModel.Title);
        QuestionnaireLineManager questionnaireLineManager = new QuestionnaireLineManager(getContext());
        List<FormControl> controls = questionnaireLineManager.generateFormControls(questionnaireId, customerId);
        adapter = new FormAdapter(getVaranegarActvity());
        adapter.addControls(controls);
        formView.setAdapter(adapter);
        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySave();
            }
        });

        return view;
    }

    private void trySave() {
        if (adapter.validate()) {
            QuestionnaireAnswerManager questionnaireAnswerManager = new QuestionnaireAnswerManager(getContext());
            try {
                questionnaireAnswerManager.saveAnswers(adapter, customerId, questionnaireId);
                List<QuestionnaireCustomerViewModel> questionnaires = questionnaireCustomerViewManager.getQuestionnaires(customerId, false);
                if (questionnaires.size() == 0) {
                    saveCallAndExit();
                } else {
                    boolean exist = Linq.exists(questionnaires, new Linq.Criteria<QuestionnaireCustomerViewModel>() {
                        @Override
                        public boolean run(QuestionnaireCustomerViewModel item) {
                            return item.DemandType == QuestionnaireDemandType.Mandatory;
                        }
                    });
                    if (exist) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setIcon(Icon.Success);
                        dialog.setMessage(R.string.questionnaire_was_saved);
                        dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getVaranegarActvity().popFragment();
                            }
                        });
                        dialog.show();
                    } else {
                        saveCallAndExit();
                    }
                }
            } catch (Exception ex) {
                getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
            }
        } else
            getVaranegarActvity().showSnackBar(R.string.please_correct_errors, MainVaranegarActivity.Duration.Short);
    }

    private void saveCallAndExit() {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.saveQuestionnaireCall(customerId);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Success);
            dialog.setMessage(R.string.all_questionnaires_finished);
            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getVaranegarActvity().popFragment();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Timber.e(e);
            getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
        }
    }

    public void setArguments(UUID customerId, UUID questionnaireId) {
        addArgument("0c91c445-4c34-4162-a6b5-17ff2ec1644b", customerId.toString());
        addArgument("79403496-ce59-4533-931e-e983eee6f75d", questionnaireId.toString());
    }

    @Override
    public void onBackPressed() {
        if (adapter.isChanged()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Warning);
            dialog.setTitle(R.string.warning);
            dialog.setMessage(R.string.do_you_want_to_save_changes);
            dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    trySave();
                }
            });
            dialog.setNegativeButton(R.string.no, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getVaranegarActvity().popFragment();
                }
            });
            dialog.show();
        } else {
            getVaranegarActvity().popFragment();
        }
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }
}
