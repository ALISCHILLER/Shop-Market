package com.varanegar.vaslibrary.webapi.questionnaire;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.varanegar.framework.base.questionnaire.validator.ControlValidator;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOptionModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 10/21/2017.
 */

public class QuestionnaireLineViewModel extends BaseModel {
    public UUID QuestionnaireUniqueId;
    public String Title;
    public UUID QuestionnaireLineTypeUniqueId;
    public boolean HasAttachment;
    public int NumberOfOptions;
    public UUID AttachmentTypeUniqueId;
    public UUID QuestionGroupUniqueId;
    public int RowIndex;
    public List<ControlValidator> Validators;
    public List<QuestionnaireLineOptionModel> questionnaireLineOptions;
    public QuestionnaireLineModel convert(){
        QuestionnaireLineModel questionnaireLineModel = new QuestionnaireLineModel();
        questionnaireLineModel.UniqueId = UniqueId;
        questionnaireLineModel.QuestionnaireUniqueId = QuestionnaireUniqueId;
        questionnaireLineModel.Title = Title;
        questionnaireLineModel.QuestionnaireLineTypeUniqueId = QuestionnaireLineTypeUniqueId;
        questionnaireLineModel.HasAttachment = HasAttachment;
        questionnaireLineModel.NumberOfOptions = NumberOfOptions;
        questionnaireLineModel.AttachmentTypeUniqueId = AttachmentTypeUniqueId;
        questionnaireLineModel.QuestionGroupUniqueId = QuestionGroupUniqueId;
        questionnaireLineModel.RowIndex = RowIndex;
        Gson gson = new GsonBuilder().create();
        questionnaireLineModel.Validators = gson.toJson(Validators);
        return questionnaireLineModel;
    }
}
