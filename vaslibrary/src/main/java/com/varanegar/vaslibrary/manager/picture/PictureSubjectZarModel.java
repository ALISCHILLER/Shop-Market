package com.varanegar.vaslibrary.manager.picture;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

@Table
public class PictureSubjectZarModel extends BaseModel {

    @Column
    public String subjectTitle;
    @Column
    public UUID centerUniqueIds;
    @Column
    public UUID customerCategoryUniqueIds;
    @Column
    public UUID customerActivityUniqueIds ;
}
