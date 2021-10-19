package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.questionnaire.controls.optionscontrol.OptionControl;
import com.varanegar.framework.base.questionnaire.controls.prioritycontrol.PriorityOption;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOption;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOptionModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOptionModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 10/21/2017.
 */

public class QuestionnaireLineOptionManager extends BaseManager<QuestionnaireLineOptionModel> {
    public QuestionnaireLineOptionManager(@NonNull Context context) {
        super(context, new QuestionnaireLineOptionModelRepository());
    }

    public List<QuestionnaireLineOptionModel> getQuestionnaireLineOptions(UUID questionnaireLineId) {
        Query query = new Query();
        query.from(QuestionnaireLineOption.QuestionnaireLineOptionTbl)
                .whereAnd(Criteria.equals(QuestionnaireLineOption.QuestionnaireLineUniqueId, questionnaireLineId.toString()))
                .orderByAscending(QuestionnaireLineOption.RowIndex);
        return getItems(query);
    }

    public List<OptionControl> getQuestionnaireLineOptionControls(UUID questionnaireLineId) {
        List<QuestionnaireLineOptionModel> options = getQuestionnaireLineOptions(questionnaireLineId);
        List<OptionControl> optionControls = new ArrayList<>();
        for (QuestionnaireLineOptionModel optionModel :
                options) {
            OptionControl optionControl = new OptionControl();
            optionControl.UniqueId = optionModel.UniqueId;
            optionControl.name = optionModel.Title;
            optionControl.value = optionModel.UniqueId.toString();
            optionControl.selected = false;
            optionControl.hasAttachment = optionModel.AnswerAttachmentUniqueId != null;
            optionControls.add(optionControl);
        }
        return optionControls;
    }

    public List<PriorityOption> getQuestionnaireLinePriorityControls(UUID questionnaireLineId) {
        List<QuestionnaireLineOptionModel> options = getQuestionnaireLineOptions(questionnaireLineId);
        List<PriorityOption> optionControls = new ArrayList<>();
        for (QuestionnaireLineOptionModel optionModel :
                options) {
            PriorityOption option = new PriorityOption();
            option.name = optionModel.Title;
            option.value = optionModel.UniqueId.toString();
            option.RowIndex = optionModel.RowIndex;
            optionControls.add(option);
        }
        return optionControls;
    }

}
