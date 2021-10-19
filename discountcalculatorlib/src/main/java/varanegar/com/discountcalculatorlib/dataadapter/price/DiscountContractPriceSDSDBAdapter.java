package varanegar.com.discountcalculatorlib.dataadapter.price;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountContractPriceSDSDBAdapter extends DiscountBaseDataAdapter
{

    public static final String DATABASE_TABLE = "DiscountContractPriceSDS";
    private static DiscountContractPriceSDSDBAdapter instance;


    private DiscountContractPriceSDSDBAdapter()
    {
    }

    public static DiscountContractPriceSDSDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountContractPriceSDSDBAdapter();
        }

        return instance;

    }

    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

}
