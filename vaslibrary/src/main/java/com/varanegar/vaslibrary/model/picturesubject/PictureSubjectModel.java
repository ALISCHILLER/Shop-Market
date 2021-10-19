package com.varanegar.vaslibrary.model.picturesubject;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.Generated;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/5/2017.
 * Table contains list of subjects
 */
@Table
public class PictureSubjectModel extends BaseModel {
    @Column
    public String Title;
}
