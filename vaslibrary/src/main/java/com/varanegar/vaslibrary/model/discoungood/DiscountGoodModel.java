package com.varanegar.vaslibrary.model.discoungood;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */
@Table
public class DiscountGoodModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public String DiscountRef;
    @Column
    public String GoodsRef;
}
