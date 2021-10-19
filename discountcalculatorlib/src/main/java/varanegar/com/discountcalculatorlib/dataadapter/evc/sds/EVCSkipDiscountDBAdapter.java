package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

/**
 * Created by Asal on 06/12/2016.
 */

public class EVCSkipDiscountDBAdapter {
    public final String KEY_EVC_SKIP_DISCOUNT_ID = "id";
    public final String KEY_EVC_ID = "EvcRef";
    public final String KEY_SALE_ID = "SaleRef";
    public final String KEY_Discount_ID = "DisRef";


    public static final String DATABASE_TABLE = "EVCSkipDiscount";
    private static String TAG = "EVCSkipDiscountDBAdapter";
    private static EVCSkipDiscountDBAdapter instance;

    public EVCSkipDiscountDBAdapter()
    {
    }

    public static EVCSkipDiscountDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCSkipDiscountDBAdapter();
        }

        return instance;

    }

}
