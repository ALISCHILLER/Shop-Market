package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DisSaleSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_ROWID = "_id";
    public final String KEY_DIS_SALE_ID = "Id";
    public final String KEY_DIS_SALE_HDR_REF = "HdrRef";
    public final String KEY_DIS_SALE_ITEM_REF = "ItemRef";
    public final String KEY_DIS_SALE_ROW_NO = "RowNo";
    public final String KEY_DIS_SALE_ITEM_TYPE = "ItemType";
    public final String KEY_DIS_SALE_DIS_REF = "DisRef";
    public final String KEY_DIS_SALE_DIS_GROUP = "DisGroup";

    public static final String DATABASE_TABLE = "DiscountDisSaleSDS";
    private static String TAG = "DisSaleSDSDBAdapter";
    private static DisSaleSDSDBAdapter instance;

    private DisSaleSDSDBAdapter() {
    }
    public static DisSaleSDSDBAdapter getInstance() {
        if (instance == null) {
            instance = new DisSaleSDSDBAdapter();
        }
        return instance;
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }



}
