package com.varanegar.vaslibrary.model.discountSDS;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.Date;


@Table
public class DiscountVnLtModel extends BaseModel {
    @Column
    public Integer DisGroup;
    @Column
    public String BackOfficeId;
    @Column
    public int PromotionId;
    @Column
    public Integer Priority;
    @Column
    public String StartDate;
    @Column
    public String EndDate;
    @Column
    public String Comment;
    @Column
    public BigDecimal MinQty;
    @Column
    public BigDecimal MaxQty;
    @Column
    public Currency MinAmount;
    @Column
    public Currency MaxAmount;
    @Column
    public Integer MinRowCount;
    @Column
    public Integer MaxRowCount;
    @Column
    public BigDecimal PrizeQty;
    @Column
    public BigDecimal AddPerc;
    @Column
    public BigDecimal DiscountPerc;
    @Column
    public Integer IsPrize;
    @Column
    public Integer PrizeProductId; //PrizeRef;
    @Column
    //public Integer DisType;
    public Integer PromotionTypeId;

    @Column
    public Integer ManufacturerId; //ManufacturerRef;

    @Column
    public Integer ProductSubGroup1Id;
    @Column
    public Integer ProductSubGroup2Id;
    @Column
    public Integer CustomerSubGroup1Id;
    @Column
    public Integer CustomerSubGroup2Id;
    @Column
    public boolean ReduceOfQty;
    @Column
    @Nullable
    public Integer ProductId; //GoodsRef;
    @Column
    public boolean DetailIsActive;
    @Column
    public Integer DetailPriority;
    @Column
    @Nullable
    public Integer ProductGroupId;//GoodsCtgrRef;
    @Column
    @Nullable
    public Integer CustomerId;//CustRef;
    @Column
    @Nullable
    public Integer CustomerGroupId; //CustCtgrRef;
    @Column
    public Integer PromotionDetailCustomerGroupId;
    @Column
    public Integer PromotionDetailId;
    @Column
    public Integer PromotionDetailCustomerId;

    @Column
    @Nullable
    public Integer CenterId; //DCRef;

    @Column
    public Integer CalcPriority;
    @Column
    @Nullable
    public Integer PayTypeId; //PayType

    @Column
    public BigDecimal MinWeight;
    @Column
    public BigDecimal MaxWeight;
    @Column
    public Integer PrizeStep;
    @Column
    public Integer PromotionCalcBaseId;
    @Column
    public Currency DiscountAmount;
    @Column
    public Currency TotalDiscount;
/*

      @Column
    public Integer PrizeStepType;
  @Column
    public Integer Code;

    @Column
    public Integer PrizeType;

    @Column
    public Integer QtyUnit;
    @Column
    public BigDecimal PrizeUnit;


    @Column
    @Nullable
    public Integer CustActRef;
    @Column
    @Nullable
    public Integer StateRef;
    @Column
    @Nullable
    public Integer AreaRef;
    @Column
    @Nullable
    public Integer DisAccRef;
    @Column
    public Integer OrderType;
    @Column
    public BigDecimal SupPerc;
    @Column
    public Integer SaleOfficeRef;
    @Column
    public Integer ApplyInGroup;
    @Column
    public Integer CustLevelRef;
    @Column
    public Integer GoodsGroupRef;
    @Column
    public Integer SaleZoneRef;
    @Column
    public Integer MainTypeRef;
    @Column
    public Integer SubTypeRef;
    @Column
    public Integer BrandRef;
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
    public Integer PrizePackageRef;

    @Column
    public Integer HasAdvanceCondition;
    @Column
    public Integer PrizeStepUnit;
*/
}
