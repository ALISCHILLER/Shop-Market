package varanegar.com.discountcalculatorlib.dataadapter.product;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProductUnit;

public class DiscountProductUnitDBAdapter extends DiscountBaseDataAdapter {


    public static final String KEY_PUNIT_PRODUCTUNITID = "ProductUnitId";
    public static final String KEY_PUNIT_PRODUCTUNITNAME = "ProductUnitName";
    public static final String KEY_PUNIT_PRODUCTID = "ProductId";
    public static final String KEY_PUNIT_QUANTITY = "Quantity";
    public static final String KEY_PUNIT_IS_DEFAULT = "IsDefault";
    public static final String KEY_PUNIT_IS_FOR_SALES = "ForSale";
    public static final String KEY_PUNIT_BACK_OFFICE_ID = "BackOfficeId";
    public static final String KEY_PUNIT_STATUS = "Status";

    public static final String DATABASE_TABLE = "DiscountProductUnit";
    private static String TAG = "DiscountProductUnitDBAdapter";
    private static DiscountProductUnitDBAdapter instance;

    private DiscountProductUnit[] discountProductUnitInfo;

    private DiscountProductUnitDBAdapter() {
    }

    public static DiscountProductUnitDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountProductUnitDBAdapter();
        }

        return instance;

    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

    public DiscountProductUnit[] getProductUnitList() {

        ArrayList<DiscountProductUnit> result = new ArrayList<>();
        Cursor c = null;

        try {
            c = getAllProductUnits();

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        DiscountProductUnit inv;
                        inv = new DiscountProductUnit(c.getInt(c.getColumnIndex(KEY_PUNIT_PRODUCTID))
                                , c.getLong(c.getColumnIndex(KEY_PUNIT_PRODUCTUNITID))
                                , c.getString(c.getColumnIndex(KEY_PUNIT_PRODUCTUNITNAME))
                                , new BigDecimal(c.getDouble(c.getColumnIndex(KEY_PUNIT_QUANTITY)))
                                , c.getInt(c.getColumnIndex(KEY_PUNIT_IS_DEFAULT)), c.getInt(c.getColumnIndex(KEY_PUNIT_IS_FOR_SALES))
                                , c.getInt(c.getColumnIndex(KEY_PUNIT_BACK_OFFICE_ID))
                                , c.getInt(c.getColumnIndex(KEY_PUNIT_STATUS)));
                        result.add(inv);
                    } while (c.moveToNext());
                }
                discountProductUnitInfo = new DiscountProductUnit[result.size()];
                discountProductUnitInfo = result.toArray(discountProductUnitInfo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            if (c != null) c.close();
//            if (closeDB) db.close();
            return discountProductUnitInfo;
        }
    }

    public DiscountProductUnit getProductUnitById(int productId, long productUnitId) {

        DiscountProductUnit result = null;
        if (discountProductUnitInfo == null || discountProductUnitInfo.length == 0)
            getProductUnitList();

        try {
            for (int i = 0; i < discountProductUnitInfo.length; i++)
                if (discountProductUnitInfo[i].productUnitId == productUnitId && discountProductUnitInfo[i].productId == productId) {
                    result = discountProductUnitInfo[i];
                    break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public DiscountProductUnit getSmallUnit(int productId)
    {
        DiscountProductUnit result = null;
        if (discountProductUnitInfo == null || discountProductUnitInfo.length == 0)
            getProductUnitList();

        try {
            for (int i = 0; i < discountProductUnitInfo.length; i++)
                if (discountProductUnitInfo[i].productId == productId && discountProductUnitInfo[i].status == 1) {
                    result = discountProductUnitInfo[i];
                    break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private Cursor getAllProductUnits() {

        Cursor c = null;
        if (db != null) {
            c = db.query(DATABASE_TABLE, new String[]{KEY_PUNIT_PRODUCTID, KEY_PUNIT_PRODUCTUNITID,
                    KEY_PUNIT_PRODUCTUNITNAME, KEY_PUNIT_QUANTITY, KEY_PUNIT_IS_DEFAULT, KEY_PUNIT_IS_FOR_SALES, KEY_PUNIT_STATUS
                    , KEY_PUNIT_BACK_OFFICE_ID}, null, null, null, null, KEY_PUNIT_PRODUCTID + " asc, " + KEY_PUNIT_QUANTITY + " desc");
        }
        return c;
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }
}
