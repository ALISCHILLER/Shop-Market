package varanegar.com.discountcalculatorlib.dataadapter.customer;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountCustomerBoGroupDBAdapter extends DiscountBaseDataAdapter
{
    public static final String KEY_ID = "Id";
    public static final String KEY_ParentRef = "ParentRef";
    public static final String KEY_NLeft = "NLeft";
    public static final String KEY_NRight =  "NRight";
    public static final String KEY_NLevel =  "NLevel";

    public static final String DATABASE_TABLE = "DiscountCustomerBoGroup";
    private static DiscountCustomerBoGroupDBAdapter instance;


    private DiscountCustomerBoGroupDBAdapter()
    {
    }

    public static DiscountCustomerBoGroupDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountCustomerBoGroupDBAdapter();
        }

        return instance;

    }

    public boolean CustomerMainSubTypeDBAdapter()
    {
        return db.delete(DATABASE_TABLE, "1", null) > 0;
    }

    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

}
