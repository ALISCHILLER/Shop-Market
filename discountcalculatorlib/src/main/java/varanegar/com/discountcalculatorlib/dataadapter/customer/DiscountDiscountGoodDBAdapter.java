package varanegar.com.discountcalculatorlib.dataadapter.customer;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Jafarzadeh on 3/12/2019.
 */

public class DiscountDiscountGoodDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountDiscountGood";
    private static String TAG = "DiscountDiscountGoodDBAdapter";
    private static DiscountDiscountGoodDBAdapter instance;

    public static DiscountDiscountGoodDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountDiscountGoodDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
