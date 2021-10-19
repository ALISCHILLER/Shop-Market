package com.varanegar.vaslibrary.model.customerbatchprice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/24/2018.
 */

@Table
public class CustomerBatchPriceModel extends BaseModel {
    @Column
    public UUID CustomerUniqueId;
    @Column
    public UUID ProductUniqueId;
    @Column
    public int BatchRef;
    @Column
    public Currency UserPrice;
    @Column
    public UUID PriceId;
    @Column
    public Currency Price;
}
