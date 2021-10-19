package com.varanegar.vaslibrary.model.productSubType;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class ProductSubTypeModel extends BaseModel {

    @Column
    public String SubCode;
    @Column
    public String SubName;
    @Column
    public int MainTypeRef;

}
