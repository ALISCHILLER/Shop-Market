package com.varanegar.vaslibrary.model.questionnaire;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 10/31/2017.
 */
@Table
public class QuestionnaireCustomerModel extends BaseModel {
    @Column
    public UUID QuestionnaireId;
    @Column
    public UUID CustomerId;
    @Column
    public UUID DemandTypeId;
    @Column(isEnum = true)
    public QuestionnaireDemandType DemandType;
    @Column
    public String NoAnswerReason;
}
