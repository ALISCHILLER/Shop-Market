package varanegar.com.discountcalculatorlib.dataadapter.product;


import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;


public class DiscountProductBoGroupDBAdapter extends DiscountBaseDataAdapter {

    public static final String KEY_ID = "Id";
    public static final String KEY_PARENT_REF = "ParentRef";
    public static final String KEY_GOOD_GROUP_NAME ="GoodsGroupName";
    public static final String KEY_BAR_CODE ="BarCode";
    public static final String KEY_DL_CODE ="DLCode";
    public static final String KEY_N_LEFT="NLeft";
    public static final String KEY_N_RIGHT="NRight";
    public static final String KEY_N_LEVEL="NLevel";


    public static final String DATABASE_TABLE = "DiscountProductBoGroup";
    private static String TAG = "DiscountProductBoGroupDBAdapter";
    private static DiscountProductBoGroupDBAdapter instance;


    private DiscountProductBoGroupDBAdapter()
    {
    }

    public static DiscountProductBoGroupDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountProductBoGroupDBAdapter();
        }

        return instance;

    }

    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }


}
