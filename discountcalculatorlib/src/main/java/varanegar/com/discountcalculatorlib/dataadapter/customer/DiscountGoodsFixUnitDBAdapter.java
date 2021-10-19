package varanegar.com.discountcalculatorlib.dataadapter.customer;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Jafarzadeh on 3/12/2019.
 */

public class DiscountGoodsFixUnitDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountGoodsFixUnit";
    private static String TAG = "DiscountGoodsFixUnitDBAdapter";
    private static DiscountGoodsFixUnitDBAdapter instance;

    public static DiscountGoodsFixUnitDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountGoodsFixUnitDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
