package com.varanegar.vaslibrary.model.CustomerOrderType;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


/**
 * Created by A.Jafarzadeh on 05/16/2017.
 */
@Table
public class CustomerOrderTypeModel extends BaseModel {
    @NotNull
    @Column
    public int BackOfficeId;
    @NotNull
    @Column
    public String OrderTypeName;

    @Override
    public String toString() {
        return OrderTypeName;
    }
}
