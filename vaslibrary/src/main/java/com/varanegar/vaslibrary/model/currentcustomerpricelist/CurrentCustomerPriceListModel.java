package com.varanegar.vaslibrary.model.currentcustomerpricelist;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.webapi.BaseApi;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/12/2017.
 */

@Table
public class CurrentCustomerPriceListModel extends BaseModel
{
    @Column
    public double MinQty;
    @Column
    public double MaxQty;
    @Column
    public Currency SalePrice;
    @Column
    public UUID ProductUniqueId;
    @Column
    public Currency UserPrice;
    @Column
    public UUID PriceRef;
}
