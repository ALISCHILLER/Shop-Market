package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;

public class DiscountItemCountSDSDBAdapter extends DiscountBaseDataAdapter {


    public static final String DATABASE_TABLE = "DiscountDiscountItemCount";
    private static String TAG = "DiscountItemCountSDSDBAdapter";
    private static DiscountItemCountSDSDBAdapter instance;

    private DiscountItemCountSDSDBAdapter() {
    }
    public static DiscountItemCountSDSDBAdapter getInstance() {
        if (instance == null) {
            instance = new DiscountItemCountSDSDBAdapter();
        }
        return instance;
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }



}
