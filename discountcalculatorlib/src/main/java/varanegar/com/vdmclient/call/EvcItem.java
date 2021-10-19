package varanegar.com.vdmclient.call;

import java.math.BigDecimal;

/**
 * Created by A.Razavi on 3/13/2019.
 */

public class EvcItem {
    public Integer Id ;
    public Integer RowOrder ;
    public Integer GoodsRef ;
    public Integer GoodsCtgrRef ;
    public Integer TotalQty ;
    public Integer UnitCapasity ;
    public Long TotalWeight ;
    public BigDecimal Discount ;
//    public decimal AmountNut
    //public BigDecimal NetAmount { get { return AmountNut; } }
    public BigDecimal Amount ;
    public BigDecimal CustPrice ;
    //public BigDecimal UnitPrice { get { return CustPrice; } }
    public BigDecimal UserPrice ;
    public Integer PrizeType ;
    public BigDecimal SupAmount ;
    public BigDecimal AddAmount ;
    public Integer CPriceRef ;
    public Integer PriceRef ;
    public BigDecimal ChargePercent ;
    public BigDecimal TaxPercent ;
    public BigDecimal UnitQty ;
    public Long UnitRef ;
    public String EvdId ;
    public Integer EvcRef ;
    public String CallId ;
    public Integer Volume ;
    public Integer FreeReasonId ;
    public BigDecimal Tax ;
    public BigDecimal Charge ;
    public Integer DisRef ;
    public Integer PeriodicDiscountRef ;
    public BigDecimal EvcItemDis1 ;
    public BigDecimal EvcItemDis2 ;
    public BigDecimal EvcItemDis3 ;
    public BigDecimal EvcItemAdd1 ;
    public BigDecimal EvcItemAdd2 ;
    public BigDecimal EvcItemOtherDiscount ;
    public BigDecimal EvcItemOtherAddition ;
    public BigDecimal SalePrice;

}
