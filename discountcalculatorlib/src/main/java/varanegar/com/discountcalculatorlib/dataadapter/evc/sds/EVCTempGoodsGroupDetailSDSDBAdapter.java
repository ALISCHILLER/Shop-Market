package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductBoGroupDBAdapter;
import varanegar.com.discountcalculatorlib.entity.evc.DiscountGoodsGroupDetailXml;
import varanegar.com.discountcalculatorlib.entity.evc.DiscountGoodsGroupDetailXmlList;


public class EVCTempGoodsGroupDetailSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_EVC_TEMP_ROWID = "_id";
    public final String KEY_EVC_TEMP_GROUPD_ID = "Id";
    public final String KEY_EVC_TEMP_GROUP_PARENT = "ParentId";
    public final String KEY_EVC_TEMP_GROUP_NLEFT = "NLeft";
    public final String KEY_EVC_TEMP_GROUP_NRIGHT = "NRight";


    public static final String DATABASE_TABLE = "EVCTempGoodsGroupDetailSDS";
    private static String TAG = "EVCTempGoodsGroupDetailSDSDBAdapter";
    private static EVCTempGoodsGroupDetailSDSDBAdapter instance;

    public EVCTempGoodsGroupDetailSDSDBAdapter() {
    }

    public static EVCTempGoodsGroupDetailSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempGoodsGroupDetailSDSDBAdapter();
        }

        return instance;

    }

    public void deleteAllEVCTempsById() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void fillEVCTempByXml(String categoryInfo) {

        try
        {
            Serializer serializer = new Persister();
            DiscountGoodsGroupDetailXmlList groupList = serializer.read(DiscountGoodsGroupDetailXmlList.class, categoryInfo);

            ContentValues values = new ContentValues();
            for (DiscountGoodsGroupDetailXml xml : groupList.recordList)
            {
                values.put(KEY_EVC_TEMP_GROUPD_ID, xml.Id);
                values.put(KEY_EVC_TEMP_GROUP_PARENT, xml.Parent);
                values.put(KEY_EVC_TEMP_GROUP_NLEFT, xml.NLeft);
                values.put(KEY_EVC_TEMP_GROUP_NRIGHT, xml.NRight);

                db.insert(DATABASE_TABLE, null, values);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void fillEVCTempByMainTable()
    {
        String query = "";
        query = "INSERT INTO " + DATABASE_TABLE + " (Id, ParentId, NLeft, NRight) " +
                " SELECT Id, ParentRef, NLeft, NRight " +
                " FROM " + DiscountProductBoGroupDBAdapter.DATABASE_TABLE;
        db.execSQL(query);
    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

}

