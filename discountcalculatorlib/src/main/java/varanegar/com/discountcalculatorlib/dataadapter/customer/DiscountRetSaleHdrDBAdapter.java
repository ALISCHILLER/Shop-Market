package varanegar.com.discountcalculatorlib.dataadapter.customer;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Jafarzadeh on 3/12/2019.
 */

public class DiscountRetSaleHdrDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountRetSaleHdr";
    private static String TAG = "DiscountRetSaleHdrDBAdapter";
    private static DiscountRetSaleHdrDBAdapter instance;

    public static DiscountRetSaleHdrDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountRetSaleHdrDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
