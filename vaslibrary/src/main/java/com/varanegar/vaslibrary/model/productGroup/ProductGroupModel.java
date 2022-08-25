package com.varanegar.vaslibrary.model.productGroup;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class ProductGroupModel extends BaseModel{

    @Column
    public UUID ProductGroupParentId;
    @Column
    public String ProductGroupName;
    @Column
    public int OrderOf;
    @Column
    public Date LastUpdate;
    @Column
    public int RowIndex;

    @Override
    public String toString() {
        return ProductGroupName;
    }

}
