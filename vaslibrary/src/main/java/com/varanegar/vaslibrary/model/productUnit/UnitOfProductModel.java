package com.varanegar.vaslibrary.model.productUnit;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

@Table
public class UnitOfProductModel extends BaseModel {

    @Column
    public String UnitName;
    @Column
    public int BackOfficeId;

    public int Count;
}
