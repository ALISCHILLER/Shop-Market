package com.varanegar.vaslibrary.promotion.nestle;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 2/5/2018.
 */
@Table
public class NestlePromotionModel extends BaseModel {
    @Column
    public int MaterialNumber;
    @Column
    public Currency ZPR0;
    @Column
    public int Z001;
    @Column
    public int X007;
    @Column
    public int X359;
    @Column
    public int Z011;
    @Column
    public Currency DisZ001;
    @Column
    public Currency DisX007;
    @Column
    public Currency DisX359;
    @Column
    public Currency DisZ011;
    @Column
    public UUID ProductUniqueId;
    @Column
    public Currency Dis1;
}
