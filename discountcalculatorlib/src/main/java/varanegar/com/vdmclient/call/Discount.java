package varanegar.com.vdmclient.call;

import java.math.BigDecimal;

/**
 * Created by A.Jafarzadeh on 3/16/2019.
 */

public class Discount extends BaseCallDataModel {
    public Integer MinCustChequeRetQty;
    public Integer MixCondition;
    public boolean IsComplexCondition;
    public String SqlCondition;
    public Integer TotalMaxRowCount;
    public Integer TotalMinRowCount;
    public BigDecimal TotalMaxAmount;
    public BigDecimal TotalDiscount;
    public BigDecimal TotalMinAmount;
    public BigDecimal MaxCustRemAmount;
    public BigDecimal MinCustRemAmount;
    public int CalcPriority;
    public int CalcMethod;
    public String EndDate;
    public String StartDate;
    public Integer MaxCustChequeRetQty;
    public BigDecimal DisPrice;
    public int IsSelfPrize;
    public String DiscountGoodRefList;
    public String DiscountOrderNoList;
    public String DiscountMainCustTypeRefList;
    public String DiscountSaleZoneRefList;
    public String DiscountAreaRefList;
    public String DiscountStateRefList;
    public String DiscountOrderRefList;
    public String DiscountCustGroupRefList;
    public String DiscountSaleOfficeRefList;
    public String DiscountOrderTypeList;
    public String DiscountPaymentUsanceRefList;
    public String DiscountPayTypeList;
    public String DiscountCustLevelRefList;
    public String DiscountCustActRefList;
    public String DiscountCustCtgrRefList;
    public String DiscountDcRefList;
    public String PrizeSelectionList;
    public String DiscountSubCustTypeRefList;
    public BigDecimal AddPerc;
    public int DisAccRef;
    public BigDecimal MaxAmount;
    public BigDecimal MinAmount;
    public Integer MaxQty;
    public Integer MinQty;
    public Integer QtyUnit;
    public int Priority;
    public int DisType;
    public int DisGroup;
    public int Code;
    public int Id;
    public int ApplyInGroup;
    public Integer PrizeStepUnit;
    public Integer PrizeStepType;
    public Integer PrizeStep;
    public int PrizeType;
    public Integer PrizeQty;
    public BigDecimal SupPerc;
    public BigDecimal MinWeight;
    public Integer MinRowsCount;
    public BigDecimal DisPerc;
    public Integer PrizePackageRef;
    public Integer PrizeIncluded;
    public Integer PrizeUnit;
    public Integer PrizeRef;
    public Integer BrandRef;
    public Integer SubTypeRef;
    public Integer MainTypeRef;
    public Integer ManufacturerRef;
    public Integer GoodsCtgrRef;
    public Integer GoodsGroupRef;
    public int IsActive;
    public Integer GoodsRef;
    public Integer OrderType;
    public Integer MaxRowsCount;
    public BigDecimal MaxWeight;
    public String CustRefList;
}
