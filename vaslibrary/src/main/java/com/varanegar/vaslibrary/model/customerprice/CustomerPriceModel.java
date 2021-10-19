package com.varanegar.vaslibrary.model.customerprice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/20/2017.
 */

@Table(name = "CustomerPrice")
public class CustomerPriceModel extends BaseModel {
    @Column
    public UUID CallOrderId;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public UUID ProductUniqueId;
    @Column
    public UUID PriceId;
    @Column
    public Currency UserPrice;
    @Column
    public Currency Price;

}
