package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DisSalePrizePackageDBAdapter extends DiscountBaseDataAdapter {
    public final String KEY_ROWID = "_id";

    public final String KEY_ID = "Id";//serverId
    public final String KEY_SALEREF = "SaleRef";
    public final String KEY_DISCOUNTREF = "DiscountRef";
    public final String KEY_MINPACK = "MainGoodsPackageItemRef";
    public final String KEY_REPLACE_PACK = "ReplaceGoodsPackageItemRef";
    public final String KEY_PRIZECOUNT = "PrizeCount";
    public final String KEY_PRIZEQTY =  "PrizeQty";


    public static final String DATABASE_TABLE = "DiscountDisSalePrizePackageSDS";
    private static String TAG = "DisSalePrizePackageDBAdapter";
    private static DisSalePrizePackageDBAdapter instance;

    public static DisSalePrizePackageDBAdapter getInstance() {
        if (instance == null) {
            instance = new DisSalePrizePackageDBAdapter();
        }
        return instance;
    }
    private DisSalePrizePackageDBAdapter() {
    }
    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

}
