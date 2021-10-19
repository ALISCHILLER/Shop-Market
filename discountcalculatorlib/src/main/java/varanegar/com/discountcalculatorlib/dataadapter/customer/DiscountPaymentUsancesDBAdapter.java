package varanegar.com.discountcalculatorlib.dataadapter.customer;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Jafarzadeh on 3/12/2019.
 */

public class DiscountPaymentUsancesDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountPaymentUsances";
    private static String TAG = "DiscountPaymentUsancesDBAdapter";
    private static DiscountPaymentUsancesDBAdapter instance;

    public static DiscountPaymentUsancesDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountPaymentUsancesDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
