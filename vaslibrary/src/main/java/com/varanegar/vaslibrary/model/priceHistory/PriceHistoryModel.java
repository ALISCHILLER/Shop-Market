package com.varanegar.vaslibrary.model.priceHistory;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class PriceHistoryModel extends BaseModel{
    @Column
    public int BackOfficeId;
    @Column
    public int GoodsRef;
    @Column
    @SerializedName("dcRef")
    public Integer DCRef;
    @Column
    public Currency SalePrice;
    @Column
    public Currency UserPrice;
    @Column
    public Integer GoodsCtgrRef;
    @Column
    public String StartDate;
    @Column
    public String EndDate;
    @Column
    public boolean IsActive;
    @Column
    public int UsanceDay;
    @Column
    public int CustRef;
    @Column
    public int CustActRef;
    @Column
    public int CustCtgrRef;
    @Column
    public int CustLevelRef;
    @Column
    public int StateRef;
    @Column
    public int AreaRef;
    @Column
    public int CountyRef;
    @Column
    public int UserRef;
    @Column
    public Date ModifiedDate;
    @Column
    public Date ModifiedDateBeforeSend;
    @Column
    public int UserRefBeforeSend;
}
