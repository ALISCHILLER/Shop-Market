package varanegar.com.discountcalculatorlib.dataadapter.customer;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Jafarzadeh on 3/12/2019.
 */

public class DiscountRetSaleItemDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountRetSaleItem";
    private static String TAG = "DiscountRetSaleItemDBAdapter";
    private static DiscountRetSaleItemDBAdapter instance;

    public static DiscountRetSaleItemDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountRetSaleItemDBAdapter();
        }

        return instance;

    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }
}
