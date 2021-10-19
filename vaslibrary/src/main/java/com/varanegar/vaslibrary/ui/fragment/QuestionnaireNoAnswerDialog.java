package com.varanegar.vaslibrary.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerManager;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerModel;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 11/1/2017.
 */

public class QuestionnaireNoAnswerDialog extends CuteAlertDialog {
    private EditText reasonEditText;
    OnReasonSelected onReasonSelected;

    public interface OnReasonSelected {
        void onDone();
    }

    public void setArguments(UUID customerId, UUID questionnaireId) {
        Bundle bundle = new Bundle();
        bundle.putString("09417e42-73df-48b9-b36a-c7a3ea6ae5dc", customerId.toString());
        bundle.putString("ad004459-cdf0-4766-9cd7-e8efebd5abfa", questionnaireId.toString());
        setArguments(bundle);
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_questionnaire_answer_dialog, viewGroup, true);
        setTitle(R.string.no_questionnaire_answer_reason);
        reasonEditText = (EditText) view.findViewById(R.id.reason_edit_text);

    }

    @Override
    public void ok() {
        String reason = reasonEditText.getText().toString();
        if (reason.isEmpty()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.please_input_reason);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setIcon(Icon.Error);
            dialog.show();
        } else {
            UUID questionnaireId = UUID.fromString(getArguments().getString("ad004459-cdf0-4766-9cd7-e8efebd5abfa"));
            UUID customerId = UUID.fromString(getArguments().getString("09417e42-73df-48b9-b36a-c7a3ea6ae5dc"));
            QuestionnaireCustomerManager manager = new QuestionnaireCustomerManager(getContext());
            QuestionnaireCustomerModel questionnaire = manager.getCustomerQuestionnaire(customerId, questionnaireId);
            if (questionnaire != null) {
                questionnaire.NoAnswerReason = reason;
                try {
                    manager.update(questionnaire);
                    Timber.i("no questionnaire answer reason selected");
                    dismiss();
                    if (onReasonSelected != null)
                        onReasonSelected.onDone();
                } catch (Exception ex) {
                    Timber.e(ex);
                    getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
                }
            }
        }

    }

    @Override
    public void cancel() {

    }
}
