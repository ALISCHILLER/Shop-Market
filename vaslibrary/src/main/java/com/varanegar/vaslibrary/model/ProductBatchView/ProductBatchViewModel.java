package com.varanegar.vaslibrary.model.ProductBatchView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/18/2018.
 */
@Table
public class ProductBatchViewModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public String BatchNo;
    @Column
    public String ExpDate;
    @Column
    public String OnHandQty;
    @Column
    public String ItemRef;
    @Column
    public String BatchRef;
}
