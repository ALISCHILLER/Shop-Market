package varanegar.com.vdmclient.call;

import java.math.BigDecimal;

/**
 * Created by A.Jafarzadeh on 3/16/2019.
 */

public class DiscountGoodsPackageItem extends BaseCallDataModel {
    public int Id;
    public int GoodsRef;
    public int UnitRef;
    public int UnitQty;
    public int TotalQty;
    public Integer ReplaceGoodsRef;
    public BigDecimal PrizePriority;
    public Integer DiscountRef;
}
