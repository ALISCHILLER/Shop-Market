package com.varanegar.vaslibrary.model.evcTempAcceptedDiscountSDS;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.Date;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class EVCTempAcceptedDiscountSDSModel extends BaseModel{

    @Column
    public String DiscountId;
    @Column
    public int PrizeCount;
    @Column
    public int AccYear;
    @Column
    public Date EVCOprDate;
    @Column
    public int DisAccRef;


}
