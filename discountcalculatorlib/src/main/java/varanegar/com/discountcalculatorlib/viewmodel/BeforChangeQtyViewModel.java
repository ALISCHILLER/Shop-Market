package varanegar.com.discountcalculatorlib.viewmodel;

import java.math.BigDecimal;

/**
 * Created by A.Razavi on 7/31/2018.
 */

public class BeforChangeQtyViewModel {
    public BigDecimal qty;
    public int productId;

    public BeforChangeQtyViewModel(int productId, BigDecimal qty){
        this.qty = qty;
        this.productId = productId;

    }

}
