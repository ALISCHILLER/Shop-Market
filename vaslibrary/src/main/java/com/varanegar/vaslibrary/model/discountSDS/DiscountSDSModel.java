package com.varanegar.vaslibrary.model.discountSDS;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class DiscountSDSModel extends BaseModel {

    @Column
    public Integer DisGroup;
    @Column
    public Integer Priority;
    @Column
    public Integer Code;
    @Column
    public Integer DisType;
    @Column
    public Integer PrizeType;
    @Column
    public String StartDate;
    @Column
    public String EndDate;
    @Column
    public BigDecimal MinQty;
    @Column
    public BigDecimal MaxQty;
    @Column
    public Integer QtyUnit;
    @Column
    public Currency MinAmount;
    @Column
    public Currency MaxAmount;
    @Column
    public BigDecimal PrizeQty;
    @Column
    public Integer PrizeRef;
    @Column
    public Integer PrizeStep;
    @Column
    public BigDecimal PrizeUnit;
    @Column
    public Currency DisPerc;
    @Column
    public Currency DisPrice;
    @Column
    @Nullable
    public Integer GoodsRef;
    @Column
    @Nullable
    @SerializedName("dcRef")
    public Integer DCRef;
    @Column
    @Nullable
    public Integer CustActRef;
    @Column
    @Nullable
    public Integer CustCtgrRef;
    @Column
    @Nullable
    public Integer StateRef;
    @Column
    @Nullable
    public Integer AreaRef;
    @Column
    @Nullable
    public Integer GoodsCtgrRef;
    @Column
    @Nullable
    public Integer CustRef;
    @Column
    @Nullable
    public Integer DisAccRef;
    @Column
    @Nullable
    public Integer PayType;
    @Column
    public Integer OrderType;
    @Column
    public BigDecimal SupPerc;
    @Column
    public BigDecimal AddPerc;
    @Column
    public Integer SaleOfficeRef;
    @Column
    public String Comment;
    @Column
    public Integer ApplyInGroup;
    @Column
    public Integer CalcPriority;
    @Column
    public Integer CalcMethod;
    @Column
    public Integer CustLevelRef;
    @Column
    public Integer GoodsGroupRef;
    @Column
    public Integer ManufacturerRef;
    @Column
    public Integer SaleZoneRef;
    @Column
    public Integer MainTypeRef;
    @Column
    public Integer SubTypeRef;
    @Column
    public Integer BrandRef;
    @Column
    public BigDecimal MinWeight;
    @Column
    public BigDecimal MaxWeight;
    @Column
    public Integer UserRef;
    @Column
    public Date ModifiedDate;
    @Column
    public Integer PrizeIncluded;
    @Column
    public Date ModifiedDateBeforeSend;
    @Column
    public Integer UserRefBeforeSend;
    @Column
    public Integer MinRowsCount;
    @Column
    public BigDecimal MinCustChequeRetQty;
    @Column
    public BigDecimal MaxCustChequeRetQty;
    @Column
    public Currency MinCustRemAmount;
    @Column
    public Currency MaxCustRemAmount;
    @Column
    public Integer MaxRowsCount;
    @Column
    public Integer IsActive;
    @Column
    public Integer OrderNo;
    @Column
    public Integer PrizeStepType;
    @Column
    public Integer IsPrize;
    @Column
    public String SqlCondition;
    @Column
    public Integer PrizePackageRef;
    @Column
    public Integer DetailIsActive;
    @Column
    public Integer DetailPriority;
    @Column
    public Integer PromotionDetailCustomerGroupId;
    @Column
    public Integer PromotionDetailId;
    @Column
    public Integer PromotionDetailCustomerId;
    @Column
    public BigDecimal ReduceOfQty;
    @Column
    public Integer HasAdvanceCondition;
    @Column
    public Integer BackOfficeId;
    @Column
    public Currency TotalMinAmount;
    @Column
    public Currency TotalMaxAmount;
    @Column
    public Integer TotalMinRowCount;
    @Column
    public Integer TotalMaxRowCount;
    @Column
    public Integer MixCondition;
    @Column
    public Integer PrizeStepUnit;
    @Column
    public String CustRefList;
    @Column
    public String DiscountAreaRefList;
    @Column
    public String DiscountCustActRefList;
    @Column
    public String DiscountCustCtgrRefList;
    @Column
    public String DiscountCustGroupRefList;
    @Column
    public String DiscountCustLevelRefList;
    @Column
    public String DiscountDcRefList;
    @Column
    public String DiscountGoodRefList;
    @Column
    public String DiscountMainCustTypeRefList;
    @Column
    public String DiscountOrderNoList;
    @Column
    public String DiscountOrderRefList;
    @Column
    public String DiscountOrderTypeList;
    @Column
    public String DiscountPaymentUsanceRefList;
    @Column
    public String DiscountPayTypeList;
    @Column
    public String DiscountSaleOfficeRefList;
    @Column
    public String DiscountSaleZoneRefList;
    @Column
    public String DiscountStateRefList;
    @Column
    public String DiscountSubCustTypeRefList;
    @Column
    public boolean IsComplexCondition;
    @Column
    public String PrizeSelectionList;
    @Column
    public Currency TotalDiscount;
    @Column
    public int IsSelfPrize;
    @Column
    public boolean DiscountPreventSaveOrder;
    @Column
    public boolean DiscountPreventSaveSale;
    @Column
    public boolean DiscountPreventSaveFollowVoucher;
    @Column
    public boolean DiscountPreventSaveTablet;
    @Column
    public String DiscountPreventMessage;
    @Column
    public int DiscountTypeRef;
    @Column
    public String PreventSaleResultDesc;
}
