package com.varanegar.vaslibrary.model.publicPrice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by s.foroughi on 09/01/2017.
 */

@Table
public class PublicPriceModel extends BaseModel {
    @Column
    @NotNull
    public UUID ProducUniqueId;
    @Column
    public Currency UnitPrice;
    @Column
    public String PriceId;
    @Column
    public String PriceClassId;
}
