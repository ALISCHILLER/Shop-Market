package varanegar.com.discountcalculatorlib.dataadapter.product;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountTourProductDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountTourProduct";
    private static String TAG = "DiscountTourProductDBAdapter";
    private static DiscountTourProductDBAdapter instance;

    public DiscountTourProductDBAdapter()
    {
    }

    public static DiscountTourProductDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountTourProductDBAdapter();
        }
        return instance;
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }

}
