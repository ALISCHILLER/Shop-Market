package com.varanegar.vaslibrary.model.goodsfixunit;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */
@Table
public class GoodsFixUnitModel extends BaseModel {
    @Column
    public int GoodsRef;
    @Column
    public int UnitRef;
    @Column
    public String UnitName;
    @Column
    public int Qty;
}
