package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by A.Razavi on 12/11/2017.
 */

public class EvcTempDecreasePrizeSDSDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "EvcTempDecreasePrizeSDS";
    private static EvcTempDecreasePrizeSDSDBAdapter instance;

    private EvcTempDecreasePrizeSDSDBAdapter() {
    }

    public static EvcTempDecreasePrizeSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EvcTempDecreasePrizeSDSDBAdapter();
        }

        return instance;
    }
    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void fillTemp(){
        clearAllData();
        String query =  "INSERT INTO " + DATABASE_TABLE +
                " SELECT rp.GoodsRef, rp.ReturnPrizeQty - sp.RemPrizeQty as DecreasePrizeQty\n" +
                " FROM " + EvcTempReturnPrizeSDSDBAdapter.DATABASE_TABLE +" rp\n" +
                " INNER JOIN " + EVCTempRemPrizeSDSDBAdapter.DATABASE_TABLE +" sp on rp.GoodsRef=sp.GoodsRef\n" +
                " WHERE rp.ReturnPrizeQty > sp.RemPrizeQty";
        db.execSQL(query);
    }
}
