package varanegar.com.discountcalculatorlib.viewmodel;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Razavi on 12/3/2017.
 */

public class DiscountEvcPrizeData {

    @SerializedName("discountRef")
    public int DiscountRef;

    @SerializedName("goodRef")
    public int GoodRef;

    @SerializedName("discountId")
    public UUID DiscountId;

    @SerializedName("qty")
    public BigDecimal Qty;
}
