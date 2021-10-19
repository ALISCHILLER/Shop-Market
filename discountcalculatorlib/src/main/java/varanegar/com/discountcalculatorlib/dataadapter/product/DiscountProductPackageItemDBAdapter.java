package varanegar.com.discountcalculatorlib.dataadapter.product;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountProductPackageItemDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountGoodsPackageItem";
    private static String TAG = "DiscountProductPackageItemDBAdapter";
    private static DiscountProductPackageItemDBAdapter instance;

    public DiscountProductPackageItemDBAdapter()
    {
    }

    public static DiscountProductPackageItemDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountProductPackageItemDBAdapter();
        }
        return instance;
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }
}
