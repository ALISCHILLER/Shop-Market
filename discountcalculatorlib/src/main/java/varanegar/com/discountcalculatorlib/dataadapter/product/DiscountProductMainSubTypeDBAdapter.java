package varanegar.com.discountcalculatorlib.dataadapter.product;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountProductMainSubTypeDBAdapter extends DiscountBaseDataAdapter {



    public static final String KEY_ID = "Id";
    public static final String KEY_GOOD_REF = "GoodsRef";
    public static final String KEY_MAIN_TYPE_REF = "MainTypeRef";
    public static final String KEY_SUB_TYPE_REF =  "SubTypeRef";


    public static final String DATABASE_TABLE = "DiscountProductMainSubType";
    private static String TAG = "DiscountProductMainSubTypeDBAdapter";
    private static DiscountProductMainSubTypeDBAdapter instance;


    private DiscountProductMainSubTypeDBAdapter()
    {
    }

    public static DiscountProductMainSubTypeDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountProductMainSubTypeDBAdapter();
        }

        return instance;

    }

    public boolean ProductMainSubTypeDBAdapter()
    {
        return db.delete(DATABASE_TABLE, "1", null) > 0;
    }

    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

}
