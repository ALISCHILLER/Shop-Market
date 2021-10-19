package com.varanegar.vaslibrary.model.questionnaire;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/3/2017.
 */
@Table
public class QuestionnaireLineModel extends BaseModel {
    @Column
    public UUID QuestionnaireUniqueId;
    @Column
    public String Title;
    @Column
    public UUID QuestionnaireLineTypeUniqueId;
    @Column
    public boolean HasAttachment;
    @Column
    public int NumberOfOptions;
    @Column
    public UUID AttachmentTypeUniqueId;
    @Column
    public UUID QuestionGroupUniqueId;
    @Column
    public int RowIndex;
    @Column
    public String Validators;
}
