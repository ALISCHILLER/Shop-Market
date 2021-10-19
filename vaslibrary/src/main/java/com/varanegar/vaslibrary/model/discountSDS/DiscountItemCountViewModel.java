package com.varanegar.vaslibrary.model.discountSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/24/2017.
 */
@Table
public class DiscountItemCountViewModel extends BaseModel {
    @Column
    public int GoodsRef;
    @Column
    public int DisRef;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public UUID ProductId;
}
