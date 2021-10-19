package varanegar.com.discountcalculatorlib.dataadapter.price;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountContractPriceVnLiteDBAdapter extends DiscountBaseDataAdapter
{

    public static final String DATABASE_TABLE = "DiscountContractPriceVnLite";
    private static DiscountContractPriceVnLiteDBAdapter instance;


    private DiscountContractPriceVnLiteDBAdapter()
    {
    }

    public static DiscountContractPriceVnLiteDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountContractPriceVnLiteDBAdapter();
        }

        return instance;

    }

    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

}
