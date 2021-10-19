package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.entity.evc.DiscountGoodsDetailXml;
import varanegar.com.discountcalculatorlib.entity.evc.DiscountGoodsDetailXmlList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;


public class EVCTempGoodsDetailSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_EVC_TEMP_ROWID = "_id";
    public final String KEY_EVC_TEMP_DETAIL_ID = "Id";
    public final String KEY_EVC_TEMP_DETAIL_GROUP_REF = "GoodsGroupRef";
    public final String KEY_EVC_TEMP_DETAIL_MANUFACTURER = "ManufacturerRef";
    public final String KEY_EVC_TEMP_DETAIL_BRAND = "BrandRef";
    public final String KEY_EVC_TEMP_DETAIL_CARTON_TYPE = "CartonType";
    public final String KEY_EVC_TEMP_DETAIL_WEIGHT = "Weight";
    public final String KEY_EVC_TEMP_DETAIL_CARTON_PRIZE_QTY = "CartonPrizeQty";
    public final String KEY_EVC_TEMP_DETAIL_CATEGORY = "GoodsCtgrRef";


    public static final String DATABASE_TABLE = "EVCTempGoodsDetailSDS";
    private static String TAG = "EVCTempGoodsDetailSDSDBAdapter";
    private static EVCTempGoodsDetailSDSDBAdapter instance;

    public EVCTempGoodsDetailSDSDBAdapter() {
    }

    public static EVCTempGoodsDetailSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempGoodsDetailSDSDBAdapter();
        }

        return instance;

    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void deleteAllEVCTempsById(String evcId) {
        db.delete(DATABASE_TABLE, "evcId ='" + evcId + "'", null);
    }

    public void fillEVCTempByXml(String categoryInfo) {

        try
        {
            Serializer serializer = new Persister();
            DiscountGoodsDetailXmlList groupList = serializer.read(DiscountGoodsDetailXmlList.class, categoryInfo);

            ContentValues values = new ContentValues();
            for (DiscountGoodsDetailXml xml : groupList.recordList)
            {
                values.put(KEY_EVC_TEMP_DETAIL_ID, xml.Id);
                values.put(KEY_EVC_TEMP_DETAIL_GROUP_REF, xml.GoodsGroupRef);
                values.put(KEY_EVC_TEMP_DETAIL_MANUFACTURER, xml.ManufacturerRef);
                values.put(KEY_EVC_TEMP_DETAIL_BRAND, xml.BrandRef);
                values.put(KEY_EVC_TEMP_DETAIL_CARTON_TYPE, xml.CartonType);
                values.put(KEY_EVC_TEMP_DETAIL_WEIGHT, xml.Weight);
                values.put(KEY_EVC_TEMP_DETAIL_CATEGORY, xml.GoodsCtgrRef);

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
                " (Id, GoodsGroupRef, ManufacturerRef, BrandRef, CartonType, Weight, CartonPrizeQty, GoodsCtgrRef) " +
                " SELECT g.ProductId, g.ProductBoGroupId, g.ManufacturerId, g.BrandId, IFNULL(g.CartonType, 0) AS CartonType , " +
                "        g.ProductWeight, IFNULL(g.CartonPrizeQty, 0), ProductCtgrId " +
                " FROM " + DiscountProductDBAdapter.DATABASE_TABLE + " g INNER JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " ei " +
                " ON ei.GoodsRef = g.ProductId AND ei.PrizeType = 0 " +
                " WHERE ei.EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

}

