package com.varanegar.vaslibrary.model.picturesubject;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 10/16/2017.
 * This table contains all pictures that we have taken from a customer
 */
@Table
public class PictureCustomerHistoryModel extends BaseModel {
    @Column
    public UUID PictureSubjectId;
    @Column
    @NotNull
    public UUID CustomerId;
    
}
