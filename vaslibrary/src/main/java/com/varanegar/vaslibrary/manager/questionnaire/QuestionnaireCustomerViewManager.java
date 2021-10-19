package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerView;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModelRepository;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireDemandType;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/31/2017.
 */

public class QuestionnaireCustomerViewManager extends BaseManager<QuestionnaireCustomerViewModel> {
    public QuestionnaireCustomerViewManager(@NonNull Context context) {
        super(context, new QuestionnaireCustomerViewModelRepository());
    }

    public List<QuestionnaireCustomerViewModel> getQuestionnaires(UUID customerId) {
        Query query = new Query();
        query.from(QuestionnaireCustomerView.QuestionnaireCustomerViewTbl)
                .whereAnd(Criteria.equals(QuestionnaireCustomerView.CustomerId, customerId.toString()))
                .whereAnd(Criteria.equals(QuestionnaireCustomerView.DemandTypeId, QuestionnaireDemandTypeId.JustOnce.toString())
                        .and(Criteria.equals(QuestionnaireCustomerView.AlreadyAnswered, false))
                        .or(Criteria.notEquals(QuestionnaireCustomerView.DemandTypeId, QuestionnaireDemandTypeId.JustOnce.toString())));
        return getItems(query);
    }

    public List<QuestionnaireCustomerViewModel> getQuestionnaires(UUID customerId, boolean answered) {
        Query query = new Query();
        query.from(QuestionnaireCustomerView.QuestionnaireCustomerViewTbl)
                .whereAnd(Criteria.equals(QuestionnaireCustomerView.CustomerId, customerId.toString()))
                .whereAnd(Criteria.equals(QuestionnaireCustomerView.DemandTypeId, QuestionnaireDemandTypeId.JustOnce.toString())
                        .and(Criteria.equals(QuestionnaireCustomerView.AlreadyAnswered, false))
                        .or(Criteria.notEquals(QuestionnaireCustomerView.DemandTypeId, QuestionnaireDemandTypeId.JustOnce.toString()))
                        .and(Criteria.equals(QuestionnaireCustomerView.HasAnswer, answered)));
        return getItems(query);
    }

    public QuestionnaireCustomerViewModel getCustomerQuestionnaire(UUID customerId, UUID questionnaireId) {
        Query query = new Query();
        query.from(QuestionnaireCustomerView.QuestionnaireCustomerViewTbl)
                .whereAnd(Criteria.equals(QuestionnaireCustomerView.CustomerId, customerId.toString())
                        .and(Criteria.equals(QuestionnaireCustomerView.QuestionnaireId, questionnaireId.toString())));
        return getItem(query);
    }


    public String checkMandatoryQuestionnaire(final UUID customerId) {
        QuestionnaireManager manager = new QuestionnaireManager(getContext());
        try {
            manager.calculateCustomerQuestionnaire(customerId);
            List<QuestionnaireCustomerViewModel> questionnaires = getQuestionnaires(customerId, false);
            if (questionnaires.size() == 0)
                return null;
            else {
                QuestionnaireCustomerViewModel force = Linq.findFirst(questionnaires, new Linq.Criteria<QuestionnaireCustomerViewModel>() {
                    @Override
                    public boolean run(QuestionnaireCustomerViewModel item) {
                        return item.DemandType == QuestionnaireDemandType.Mandatory;
                    }
                });
                if (force == null) {
                    return null;
                } else {
                    return getContext().getString(R.string.answering_questionnaire) + " " + force.Title + " " + getContext().getString(R.string.is_mandatory);
                }
            }
        } catch (DbException e) {
            return null;
        } catch (ValidationException e) {
            return null;
        }
    }
}
