package com.varanegar.vaslibrary.model.customer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 1/28/2018.
 */
@Table
public class CustomerCategoryModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    @NotNull
    public String CustomerCategoryName;

    @Override
    public String toString() {
        return CustomerCategoryName;
    }
}
