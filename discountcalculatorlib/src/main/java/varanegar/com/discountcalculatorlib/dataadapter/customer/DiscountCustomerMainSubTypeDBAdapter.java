package varanegar.com.discountcalculatorlib.dataadapter.customer;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountCustomerMainSubTypeDBAdapter extends DiscountBaseDataAdapter
{
    public static final String KEY_ID = "Id";
    public static final String KEY_Customer_REF = "CustomersRef";
    public static final String KEY_MAIN_TYPE_REF = "MainTypeRef";
    public static final String KEY_SUB_TYPE_REF =  "SubTypeRef";

    public static final String DATABASE_TABLE = "DiscountCustomerMainSubType";
    private static DiscountCustomerMainSubTypeDBAdapter instance;


    private DiscountCustomerMainSubTypeDBAdapter()
    {
    }

    public static DiscountCustomerMainSubTypeDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountCustomerMainSubTypeDBAdapter();
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
