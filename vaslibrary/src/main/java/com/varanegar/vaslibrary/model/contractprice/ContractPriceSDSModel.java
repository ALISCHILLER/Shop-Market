package com.varanegar.vaslibrary.model.contractprice;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by s.foroughi on 11/01/2017.
 * This model is created for Varanegar
 */
@Table
public class ContractPriceSDSModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    @SerializedName("cPriceType")
    public int CPriceType;
    @Column
    @SerializedName("code")
    public int Code;
    @Column
    @SerializedName("goodsRef")
    public int GoodsRef;
    @Column
    @SerializedName("unitRef")
    public int UnitRef;
    @Column
    @SerializedName("custRef")
    public String CustRef;
    @Column
    @SerializedName("custCtgrRef")
    public Integer CustCtgrRef;
    @Column
    @SerializedName("dcRef")
    public Integer DCRef;
    @Column
    @SerializedName("custActRef")
    public Integer CustActRef;
    @Column
    @SerializedName("minQty")
    public BigDecimal MinQty;
    @Column
    @SerializedName("maxQty")
    public BigDecimal MaxQty;
    @Column
    @SerializedName("salePrice")
    public BigDecimal SalePrice;
    @Column
    @SerializedName("startDate")
    public String StartDate;
    @Column
    @SerializedName("endDate")
    public String EndDate;
    @Column
    @SerializedName("buyTypeRef")
    public Integer BuyTypeRef;
    @Column
    @SerializedName("usanceDay")
    public Integer UsanceDay;
    @Column
    @SerializedName("stateRef")
    public Integer StateRef;
    @Column
    @SerializedName("countyRef")
    public Integer CountyRef;
    @Column
    @SerializedName("areaRef")
    public Integer AreaRef;
    @Column
    @SerializedName("priority")
    public BigDecimal Priority;
    @Column
    @SerializedName("orderTypeRef")
    public Integer OrderTypeRef;
    @Column
    @SerializedName("goodsGroupRef")
    public int GoodsGroupRef;
    @Column
    @SerializedName("mainTypeRef")
    public int MainTypeRef;
    @Column
    @SerializedName("subTypeRef")
    public int SubTypeRef;
    @Column
    @SerializedName("custLevelRef")
    public Integer CustLevelRef;
    @Column
    @SerializedName("userRef")
    public int UserRef;
    @Column
    @SerializedName("modifiedDate")
    public Date ModifiedDate;
    @Column
    @SerializedName("dealerCtgrRef")
    public int DealerCtgrRef;
    @Column
    @SerializedName("modifiedDateBeforeSend")
    public Date ModifiedDateBeforeSend;
    @Column
    @SerializedName("userRefBeforeSend")
    public int UserRefBeforeSend;
    @Column
    @SerializedName("batchNoRef")
    public int BatchNoRef;
    @Column
    @SerializedName("batchNo")
    public String BatchNo;



}
