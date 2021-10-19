package com.varanegar.vaslibrary.model.picturesubject;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 10/16/2017.
 */
@Table
public class PictureTemplateDetailModel extends BaseModel {
    @Column
    public UUID PictureTemplateUniqueId;
    @Column
    public UUID PictureSubjectUniqueId;
    @Column
    public UUID DemandTypeUniqueId;
    @Column(isEnum = true)
    public PictureDemandType DemandType;
}
