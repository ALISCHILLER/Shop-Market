package com.varanegar.vaslibrary.model.picturesubject;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 10/18/2017.
 * This table includes all subjects that is calculated for a customer
 */
@Table
public class PictureCustomerModel extends BaseModel {
    @Column
    public UUID PictureSubjectId;
    @Column
    public UUID CustomerId;
    @Column
    public UUID DemandTypeUniqueId;
    @Column(isEnum = true)
    public PictureDemandType DemandType;
    @Column
    public String NoPictureReason;
    @Column
    public UUID FileId;
    @Column
    public String Title;
}
