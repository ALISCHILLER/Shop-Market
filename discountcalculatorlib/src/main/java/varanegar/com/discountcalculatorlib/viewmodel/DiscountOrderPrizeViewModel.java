package varanegar.com.discountcalculatorlib.viewmodel;

import java.math.BigDecimal;

/**
 * Created by A.Jafarzadeh on 4/9/2018.
 */

public class DiscountOrderPrizeViewModel {
    public int id;
    public int discountRef;
    public int orderDiscountRef;
    public int goodsRef;
    public String unitRef;
    public int qty;
    public BigDecimal totalQTy = BigDecimal.ZERO;
    public String goodCode;
    public String goodName;
}
