package com.varanegar.vaslibrary.model.contractprice;

import com.google.gson.annotations.JsonAdapter;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.network.gson.typeadapters.NestleDateDeSerializer;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;

/**
 * Created by A.Torabi on 12/19/2017.
 */
@Table
public class ContractPriceNestleModel extends BaseModel {
    @Column
    public String ConditionType;
    @Column
    public String CustomerHierarchyNumber;
    @Column
    public String MaterialNumber;
    @Column
    public Currency ConditionAmount;
    @Column
    public String ConditionUnit;
    @Column
    public String ConditionPricingUnit;
    @Column
    public String ConditionUnitofMeasure;
    @Column
    @JsonAdapter(NestleDateDeSerializer.class)
    public Date ConditionValidOn;
    @Column
    @JsonAdapter(NestleDateDeSerializer.class)
    public Date ConditionValidTo;
}
