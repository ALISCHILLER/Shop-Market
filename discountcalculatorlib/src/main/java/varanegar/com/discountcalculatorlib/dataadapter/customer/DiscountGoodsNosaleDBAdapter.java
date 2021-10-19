package varanegar.com.discountcalculatorlib.dataadapter.customer;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Jafarzadeh on 3/12/2019.
 */

public class DiscountGoodsNosaleDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountGoodsNosale";
    private static String TAG = "DiscountGoodsNosaleDBAdapter";
    private static DiscountGoodsNosaleDBAdapter instance;

    public static DiscountGoodsNosaleDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountGoodsNosaleDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
