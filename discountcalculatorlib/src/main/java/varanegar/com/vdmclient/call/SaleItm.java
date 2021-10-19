package varanegar.com.vdmclient.call;

import java.math.BigDecimal;

/**
 * Created by A.Jafarzadeh on 3/16/2019.
 */

public class SaleItm extends BaseCallDataModel {
    public int Id;
    public int HdrRef;
    public int RowOrder;
    public int GoodsRef;
    public int PriceRef;
    public int CPriceRef;
    public int TotalQty;
    public int PrizeType;
    public BigDecimal Discount;
    public int IsDeleted;
    public Integer FreeReasonId;
    public BigDecimal CustPrice;
    public BigDecimal UserPrice;
    public BigDecimal Amount;
}
