package com.varanegar.vaslibrary.manager.questionnaire;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomer;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerModelRepository;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireDemandType;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHeaderModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/31/2017.
 */

public class QuestionnaireCustomerManager extends BaseManager<QuestionnaireCustomerModel> {
    public QuestionnaireCustomerManager(@NonNull Context context) {
        super(context, new QuestionnaireCustomerModelRepository());
    }

    public void saveQuestionnaires(final UUID customerId, final List<QuestionnaireHeaderModel> headers) throws DbException, ValidationException {
        // fetch all questionnaires that have been already calculated for this customer
        final List<QuestionnaireCustomerModel> existingQuestionnaires = getCustomerQuestionnaires(customerId);
        // delete all of them for the customer

        deleteQuestionnaires(customerId);
        // iterate over templates and insert(update) customer questionnaires
        final int[] count = {0};
        for (final QuestionnaireHeaderModel p :
                headers) {
            // Maybe this is second time that visitor enters the questionnaires form therefore,
            // find if this questionnaire is already calculated for customer.
            // We do this because we do not want to lose questionnaires that have been answered before
            QuestionnaireCustomerModel questionnaireCustomerModel = Linq.findFirst(existingQuestionnaires, new Linq.Criteria<QuestionnaireCustomerModel>() {
                @Override
                public boolean run(QuestionnaireCustomerModel item) {
                    return item.QuestionnaireId.equals(p.UniqueId);
                }
            });
            // If this is a new questionnaire for this customer assign a new unique id
            if (questionnaireCustomerModel == null) {
                questionnaireCustomerModel = new QuestionnaireCustomerModel();
                questionnaireCustomerModel.UniqueId = UUID.randomUUID();
                questionnaireCustomerModel.CustomerId = customerId;
                questionnaireCustomerModel.QuestionnaireId = p.UniqueId;
            }
            questionnaireCustomerModel.DemandTypeId = p.DemandTypeUniqueId;
            if (p.DemandTypeUniqueId.equals(QuestionnaireDemandTypeId.Mandatory) || p.DemandTypeUniqueId.equals(QuestionnaireDemandTypeId.JustOnce)) {
                SysConfigManager configManager = new SysConfigManager(getContext());
                OwnerKeysWrapper wrapper = configManager.readOwnerKeys();
                CustomerPathViewManager pathViewManager = new CustomerPathViewManager(getContext());
                if (wrapper.isPoober() && pathViewManager.isInVisitDayPath(customerId)) {
                    questionnaireCustomerModel.DemandType = QuestionnaireDemandType.Mandatory;
                } else if (!wrapper.isPoober())
                    questionnaireCustomerModel.DemandType = QuestionnaireDemandType.Mandatory;
            } else
                questionnaireCustomerModel.DemandType = QuestionnaireDemandType.Optional;
            // insert or update item
            insertOrUpdate(questionnaireCustomerModel);
        }

    }


    public List<QuestionnaireCustomerModel> getCustomerQuestionnaires(UUID customerId) {
        Query query = new Query()
                .from(QuestionnaireCustomer.QuestionnaireCustomerTbl)
                .whereAnd(Criteria.equals(QuestionnaireCustomer.CustomerId, customerId.toString()));
        return getItems(query);
    }

    public QuestionnaireCustomerModel getCustomerQuestionnaire(UUID customerId, UUID questionnaireId) {
        Query query = new Query()
                .from(QuestionnaireCustomer.QuestionnaireCustomerTbl)
                .whereAnd(Criteria.equals(QuestionnaireCustomer.CustomerId, customerId.toString())
                        .and(Criteria.equals(QuestionnaireCustomer.QuestionnaireId, questionnaireId.toString())));
        return getItem(query);
    }

    private void deleteQuestionnaires(UUID customerId) throws DbException {
        delete(Criteria.equals(QuestionnaireCustomer.CustomerId, customerId.toString()));
    }

    public void resetQuestionnaire(UUID customerId) throws ValidationException, DbException {
        List<QuestionnaireCustomerModel> items = getItems(new Query().from(QuestionnaireCustomer.QuestionnaireCustomerTbl).whereAnd(Criteria.equals(QuestionnaireCustomer.CustomerId, customerId.toString())));
        for (QuestionnaireCustomerModel item :
                items) {
            item.NoAnswerReason = null;
        }
        if (items.size() > 0)
            update(items);
    }
//
//    public UUID saveToFile(QuestionnaireHeaderModel item) throws IOException {
//        UUID fileName = UUID.randomUUID();
//        return saveToFile(item, fileName);
//    }
//
//    public UUID saveToFile(FormAdapter item, UUID questionnaireId) throws IOException {
//        FormBuilder formBuilder = FormBuilder.getInstance();
//        String json = formBuilder.serializeValue(item);
//        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(questionnaireId.toString(), Context.MODE_PRIVATE));
//        outputStreamWriter.write(json);
//        outputStreamWriter.close();
//        return questionnaireId;
//    }
//
//    public List<FormControl> readFromFile(UUID fileId) throws IOException {
//        FileInputStream inputStream = getContext().openFileInput(fileId.toString());
//        byte[] buffer = new byte[1024];
//        int n = 0;
//        StringBuilder content = new StringBuilder();
//        while ((n = inputStream.read(buffer)) != -1) {
//            content.append(new String(buffer, 0, n));
//        }
//        FormBuilder formBuilder = FormBuilder.getInstance();
//        List<FormControl> controls = formBuilder.deserializeValue(content.toString());
//        return controls;
//    }


}
