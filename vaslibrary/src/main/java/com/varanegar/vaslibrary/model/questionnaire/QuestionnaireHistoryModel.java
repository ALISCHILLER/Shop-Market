package com.varanegar.vaslibrary.model.questionnaire;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/3/2017.
 */
@Table
public class QuestionnaireHistoryModel extends BaseModel
{
    @Column
    public UUID QuestionnaireId;
    @Column
    public UUID CustomerId;
}
