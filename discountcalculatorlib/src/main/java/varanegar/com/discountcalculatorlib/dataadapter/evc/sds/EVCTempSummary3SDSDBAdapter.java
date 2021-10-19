package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by Asal on 04/11/2017.
 */

public class EVCTempSummary3SDSDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "EVCTempSummary3SDS";
    private static String TAG = "EVCTempSummary3SDSDBAdapter";
    private static EVCTempSummary3SDSDBAdapter instance;

    public EVCTempSummary3SDSDBAdapter() {
    }

    public static EVCTempSummary3SDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempSummary3SDSDBAdapter();
        }

        return instance;

    }

    public void delete(String evcId) {
        String sql = "DELETE FROM " + DATABASE_TABLE + " WHERE EvcId = '" + evcId + "'";
        db.execSQL(sql);
    }

    //insert into #TmpTable3
    public void insert(String evcId) {
        String sql =
        " INSERT INTO " + DATABASE_TABLE + " (EVCItemRef, RowOrder, DisRef, DisGroup, EvcId) "+
        " SELECT EVCItemRef, RowOrder, DisID, DisGroup,'"+evcId+"'\n" +
        " FROM " + EVCTempSummarySDSDBAdapter.DATABASE_TABLE +
        " WHERE DisGroup || '-' || Priority in (SELECT tm.DisGroup || '-' || MIN(tm.Priority)\n" +
        "    FROM " + EVCTempSummarySDSDBAdapter.DATABASE_TABLE +" AS tm \n" +
        " WHERE EvcId = '" + evcId + "'"+
        "    Group By tm.DisGroup)\n" +
        " AND EvcId = '" + evcId + "'";
        db.execSQL(sql);
    }


}