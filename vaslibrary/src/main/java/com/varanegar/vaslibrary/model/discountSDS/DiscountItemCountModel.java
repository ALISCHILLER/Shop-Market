package com.varanegar.vaslibrary.model.discountSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 12/20/2017.
 */
@Table
public class DiscountItemCountModel extends BaseModel {
    @Column
    public int GoodsRef;
    @Column
    public int DisRef;
}
