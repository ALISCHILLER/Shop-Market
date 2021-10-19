package varanegar.com.discountcalculatorlib.dataadapter.price;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountPriceHistoryDBAdapter extends DiscountBaseDataAdapter
{

    public static final String DATABASE_TABLE = "DiscountPriceHistory";
    private static DiscountPriceHistoryDBAdapter instance;


    private DiscountPriceHistoryDBAdapter()
    {
    }

    public static DiscountPriceHistoryDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountPriceHistoryDBAdapter();
        }

        return instance;

    }

    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

}
