package varanegar.com.discountcalculatorlib.dataadapter.product;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountProductPackageDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountGoodsPackage";
    private static String TAG = "DiscountProductPackageDBAdapter";
    private static DiscountProductPackageDBAdapter instance;

    public DiscountProductPackageDBAdapter()
    {
    }

    public static DiscountProductPackageDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountProductPackageDBAdapter();
        }
        return instance;
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }

}
