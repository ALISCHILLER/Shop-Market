package varanegar.com.discountcalculatorlib.dataadapter.base;

import android.database.sqlite.SQLiteDatabase;
import varanegar.com.discountcalculatorlib.helper.DiscountDbHelper;

public class DiscountBaseDataAdapter {
    protected static SQLiteDatabase db;

    public DiscountBaseDataAdapter(){
        db = DiscountDbHelper.getDb();
    }



}
