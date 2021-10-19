package varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Razavi on 3/27/2018.
 */

public class DisAccDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountDisAcc";

    private static DisAccDBAdapter instance;
    protected DisAccDBAdapter()
    {
    }

    public static DisAccDBAdapter getInstance()
    {
        if(instance == null)
        {
            instance = new DisAccDBAdapter();
        }

        return instance;
    }


    public void clearAllData(){
        db.execSQL("delete from " + DATABASE_TABLE);
        // db.delete(DATABASE_TABLE, null , null);
    }

}
