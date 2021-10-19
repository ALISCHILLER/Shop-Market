package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductMainSubTypeDBAdapter;
import varanegar.com.discountcalculatorlib.entity.evc.DiscountGoodsMainSubTypeDetailXml;
import varanegar.com.discountcalculatorlib.entity.evc.DiscountGoodsMainSubTypeDetailXmlList;


public class EVCTempGoodsMainSubTypeSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_EVC_TEMP_ROWID = "_id";
    public final String KEY_EVC_TEMP_SUBTYPE_ID = "Id";
    public final String KEY_EVC_TEMP_SUBTYPE_GOODS_REF = "GoodsRef";
    public final String KEY_EVC_TEMP_SUBTYPE_MAIN_GROUP_REf = "MainTypeRef";
    public final String KEY_EVC_TEMP_SUBTYPE_SUB_GROUP_REF = "SubTypeRef";




    public static final String DATABASE_TABLE = "EVCTempGoodsMainSubTypeSDS";
    private static String TAG = "EVCTempGoodsMainSubTypeSDSDBAdapter";
    private static EVCTempGoodsMainSubTypeSDSDBAdapter instance;

    public EVCTempGoodsMainSubTypeSDSDBAdapter() {
    }

    public static EVCTempGoodsMainSubTypeSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempGoodsMainSubTypeSDSDBAdapter();
        }

        return instance;

    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void fillEVCTempByXml(String categoryInfo) {

        try
        {
            Serializer serializer = new Persister();
            DiscountGoodsMainSubTypeDetailXmlList groupList = serializer.read(DiscountGoodsMainSubTypeDetailXmlList.class, categoryInfo);

            ContentValues values = new ContentValues();
            for (DiscountGoodsMainSubTypeDetailXml xml : groupList.recordList)
            {
                values.put(KEY_EVC_TEMP_SUBTYPE_ID, xml.Id);
                values.put(KEY_EVC_TEMP_SUBTYPE_GOODS_REF, xml.GoodsRef);
                values.put(KEY_EVC_TEMP_SUBTYPE_MAIN_GROUP_REf, xml.MainGroupRef);
                values.put(KEY_EVC_TEMP_SUBTYPE_SUB_GROUP_REF, xml.SubGroupRef);

                db.insert(DATABASE_TABLE, null, values);
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void fillEVCTempByMainTable(String evcId)
    {
        String query = "";
        query = "INSERT INTO " + DATABASE_TABLE +
                " (Id, GoodsRef, MainTypeRef, SubTypeRef) " +
                " SELECT MS.Id, MS.GoodsRef, MS.MainTypeRef, MS.SubTypeRef " +
                " FROM " + DiscountProductMainSubTypeDBAdapter.DATABASE_TABLE + " MS INNER JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " ei " +
                " ON ei.GoodsRef = MS.GoodsRef AND ei.PrizeType = 0 " +
                " WHERE ei.EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

}

