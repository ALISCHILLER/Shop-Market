package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;


public class EVCTempSummaryFinalSDSDBAdapter extends DiscountBaseDataAdapter
{

    public final String KEY_EVC_TEMP_ROWID = "_id";
    public final String KEY_DISCOUNT_ID = "DisId";
    public final String KEY_DIS_GROUP = "DisGroup";
    public final String KEY_DIS_TYPE = "DisType";
    public final String KEY_EVC_ITEM_REF_ID = "EVCItemRef";
    public final String KEY_ROW_ORDER = "RowOrder";
    public final String KEY_PRIORITY = "Priority";
    public final String KEY_REQ_QTY = "ReqQty";
    public final String KEY_REQ_ROW_COUNT = "ReqRowCount";
    public final String KEY_REQ_AMOUNT = "ReqAmount";
    public final String KEY_REQ_WIGHT = "ReqWeight";
    public final String KEY_EVC_ID = "EVCId";


    public static final String DATABASE_TABLE = "EVCTempSummaryFinalSDS";
    private static String TAG = "EVCTempSummaryFinalSDSDBAdapter";
    private static EVCTempSummaryFinalSDSDBAdapter instance;


    public EVCTempSummaryFinalSDSDBAdapter()
    {
    }

    public static EVCTempSummaryFinalSDSDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempSummaryFinalSDSDBAdapter();
        }

        return instance;

    }

    public void clearAllData(SQLiteDatabase db)
    {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void deleteAllEVCYTempSummarieById(String evcId)
    {
        db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
    }

    public void insertFromTemp3(String evcId)
    {
        String sql = " INSERT INTO " + DATABASE_TABLE + "(EVCItemRef, RowOrder, DisId, DisGroup, ReqAmount, EVCID  /*SupAmount, Discount*/)\n" +
                " SELECT EVCItemRef, RowOrder, DisRef, DisGroup, 0, '"+ evcId+"'"+
                " FROM " + EVCTempSummary3SDSDBAdapter.DATABASE_TABLE +
                " WHERE EvcId = '"+ evcId+"'";
        db.execSQL(sql);

    }

    public void deleteEvcSkipDiscount(String evcId, int saleId)
    {
        String sql = "DELETE FROM " + EVCTempSummaryFinalSDSDBAdapter.DATABASE_TABLE +
        " WHERE DisId in(SELECT sk.DisRef " +
        "    FROM EvcSkipDiscount sk " +
        "    WHERE sk.SaleRef="+ saleId +")";
        db.execSQL(sql);

    }

    public void deleteByEvcRef(String evcId) {

        String sql = "DELETE FROM " + DATABASE_TABLE +
                "  WHERE EVCItemRef in ( SELECT ei._Id\n" +
                "  FROM EVCItemSDS ei\n" +
                "      INNER JOIN DiscountProduct g on g.ProductId = ei.GoodsRef\n" +
                "      INNER JOIN "+ DiscountDBAdapter.DATABASE_TABLE +" ds on ds.Id = EVCTempSummaryFinalSDS.DisId\n" +
                "  WHERE EVCRef = '"+evcId+"'\n" +
                "      AND EXISTS(\n" +
                "                SELECT 1 FROM EvcSkipDiscount skd \n" +
                "                INNER JOIN "+ DiscountDBAdapter.DATABASE_TABLE +" d2 on EvcRef = '"+evcId+"'\n" +
                "                        and d2.ID = skd.DisRef\n" +
                "                and d2.DisGroup = ds.DisGroup\n" +
                "                and d2.Priority < ds.Priority\n" +
                "                and ei.GoodsRef = d2.GoodsRef)\n" +
                ")\n";
        db.execSQL(sql);
    }

    public void deleteAllEVCYTempSummaries()
    {
        db.delete(DATABASE_TABLE, null, null);
    }
}

