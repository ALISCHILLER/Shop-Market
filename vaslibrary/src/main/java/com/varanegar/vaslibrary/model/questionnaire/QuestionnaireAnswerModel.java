package com.varanegar.vaslibrary.model.questionnaire;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 11/1/2017.
 */
@Table
public class QuestionnaireAnswerModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public UUID QuestionnaireId;
    @Column
    public UUID QuestionnaireLineId;
    @Column
    public String Value;
    @Column
    public UUID AttachmentId;
}
