package com.varanegar.vaslibrary.model.goodscusttemp;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 11/18/2018.
 */
@Table
public class GoodsCustTempModel extends BaseModel {
    @Column
    public int Qty;
    @Column
    public UUID ProductId;
    @Column
    public String UnitId;
}
