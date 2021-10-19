package com.varanegar.vaslibrary.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/21/2018.
 */
public class BatchQtyDetailModel extends BaseModel {
    @Column
    public UUID CustomerCallOrderLineUniqueId;
    @Column
    public int BatchRef;
    @Column
    public int ItemRef;
    @Column
    public String BatchNo;
    @Column
    public BigDecimal Qty;
}
