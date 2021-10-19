package com.varanegar.vaslibrary.model.questionnaire;


import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 10/18/2017.
 */
@Table
public class QuestionnaireLineOptionModel extends BaseModel {
    @Column
    @NotNull
    public UUID QuestionnaireLineUniqueId;
    @Column
    public String Title;
    @Column
    public int RowIndex;
    @Column
    public UUID QuestionGroupUniqueId;
    @Nullable
    @Column
    public UUID AnswerAttachmentUniqueId;
}
