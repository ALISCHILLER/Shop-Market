package com.varanegar.vaslibrary.model.evcitemstatuessdscustomers;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/17/2018.
 */
@Table
public class EVCItemStatuesCustomersModel extends BaseModel {
    @Column
    public int EVCItemRef;
    @Column
    public int RowOrder;
    @Column
    public int DisRef;
    @Column
    public int DisGroup;
    @Column
    public double AddAmount;
    @Column
    public double SupAmount;
    @Column
    public double Discount;
    @Column
    public int EvcId;
    @Column
    public UUID CustomerId;
    @Column
    public UUID OrderLineId;
//    @Column
//    @Nullable
//    public UUID OrderLineId;
}
