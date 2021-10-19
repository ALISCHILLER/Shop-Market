package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountEvcItemStatuteData;


public class EVCItemStatutesSDSDBAdapter extends DiscountBaseDataAdapter {

    public static final String KEY_EVC_ITEM_STATUTES_ROWID = "_id";
    public static final String KEY_EVC_ITEM_STATUTES_ITEM_REF = "EVCItemRef";
    public static final String KEY_EVC_ITEM_STATUTES_ROW_ORDER = "RowOrder";
    public static final String KEY_EVC_ITEM_STATUTES_DIS_REF = "DisRef";
    public static final String KEY_EVC_ITEM_STATUTES_DIS_GROUP = "DisGroup";
    public static final String KEY_EVC_ITEM_STATUTES_ADD_AMOUNT = "AddAmount";
    public static final String KEY_EVC_ITEM_STATUTES_SUP_AMOUNT = "SupAmount";
    public static final String KEY_EVC_ITEM_STATUTES_DISCOUNT = "Discount";
    public static final String KEY_EVC_ITEM_STATUTES_EVC_ID = "EvcId";


    public static final String DATABASE_TABLE = "EVCItemStatutesSDS";
    private static String TAG = "EVCItemStatutesSDSDBAdapter";
    private static EVCItemStatutesSDSDBAdapter instance;

    public EVCItemStatutesSDSDBAdapter() {
    }

    public static EVCItemStatutesSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCItemStatutesSDSDBAdapter();
        }

        return instance;

    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

/*SLE.usp_FillEVCStatuteByID*/
    public void fillEVCTemp(String evcId) {
        /*@EVCItemStatutes*/
        String sql = "insert into " + DATABASE_TABLE + " (EVCItemRef, RowOrder, DisRef, DisGroup, AddAmount, SupAmount, Discount, EVCId) " +
                "select EVCItemRef ,RowOrder ,DisID as  DisRef , DisGroup ,0 ,0 ,0, '" + evcId + "'" +
                " FROM  " + EVCTempSummaryFinalSDSDBAdapter.DATABASE_TABLE +
                " WHERE   EvcId='" + evcId + "' " ;
        db.execSQL(sql);
    }

    public void deleteInvalidItemStatuse(String evcId) {

        String sql =
        " DELETE FROM " + DATABASE_TABLE + ""+
        " WHERE  _ID in (SELECT es._ID  "+
        " FROM "+ EVCItemSDSDBAdapter.DATABASE_TABLE + " ei "+
        " JOIN " +DATABASE_TABLE + " es on es.EVCItemRef=ei._ID " +
        " JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId=ei.GoodsRef " +
        " JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " ds on ds.Id=es.DisRef "+
        " WHERE (EVCId = '"+evcId+"')"+
                " AND EXISTS ( SELECT 1 "+
                "FROM " +EVCSkipDiscountDBAdapter.DATABASE_TABLE + " skd "+
                "JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " d2  "+
                "ON d2.ID=skd.DisRef " + " AND EVCId = '"+evcId+"' " +
                "AND d2.DisGroup=ds.DisGroup "+
                "AND d2.Priority<ds.Priority "+
                "AND ei.GoodsRef=d2.GoodsRef "+
        ") )";
        db.execSQL(sql);
    }

    public void deleteUnAffectedEVCItemStatutes(String evcId, int discountId)
    {
        String sql = "DELETE FROM " + DATABASE_TABLE
                + " WHERE EVCItemRef IN ( \n"
                + "                 SELECT _id FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE
                + "                            WHERE EVCRef = '" + evcId + "') \n"
                + " AND DisRef = " + discountId ;

        db.execSQL(sql);
    }

    public void deleteUnAffectedEVCItemStatutes(String evcId)
    {
        String sql = "DELETE FROM " + DATABASE_TABLE
                + " WHERE EVCItemRef IN ( \n"
                + "                 SELECT _id FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE
                + "                 WHERE EVCRef = '" + evcId + "')";

        db.execSQL(sql);
    }


    public void deleteInvalidItemSatatutes(String evcID) {

        String sql = "DELETE from " + DATABASE_TABLE +
                " WHERE _ID IN (SELECT ES._ID  " +
                " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E ON E.EvcID = EI.EVCRef " +
                " INNER JOIN " + DATABASE_TABLE + " ES ON ES.DisRef=EI.DisRef" +
                " WHERE EI.PrizeType=1 AND ES.EvcID= '" + evcID + "' and E.EvcID= '" + evcID + "'" +
                " 	and EI.UnitQty=0)";
        db.execSQL(sql);
    }

    public void updateDiscountBasedOnPriority1(String evcId, int priority, int discountId) {

        String selectData = "SELECT ES._id, \n" +
                " IFNULL(ES.Discount,0) + (CASE \n" +
                " WHEN D.CalcMethod=1 THEN cast((IFNULL(D.DisPerc,0)*EI.Amount) / 100 as integer) \n" +
                " WHEN D.CalcMethod=3 THEN cast((IFNULL(D.DisPerc,0)*(EI.Amount-IFNULL(EI.Discount,0))) / 100 as integer) \n" +
                " ELSE cast((IFNULL(D.DisPerc,0)*(EI.Amount+IFNULL(EI.AddAmount,0)-IFNULL(EI.Discount,0))) / 100 as integer) \n" +
                " END ) as Discount \n" +
                " , IFNULL(ES.SupAmount,0) + (CASE \n" +
                " WHEN D.CalcMethod=1 THEN cast(cast((IFNULL(D.DisPerc,0)*EI.Amount) / 100 as integer) * IFNULL(D.SupPerc,0) / 100 as integer ) \n" +
                " WHEN D.CalcMethod=3 THEN cast(cast((IFNULL(D.DisPerc,0)*(EI.Amount-IFNULL(EI.Discount,0))) / 100 as integer) * IFNULL(D.SupPerc,0) / 100 as integer) \n" +
                " ELSE cast(cast((IFNULL(D.DisPerc,0)*(EI.Amount+IFNULL(EI.AddAmount,0)-IFNULL(EI.Discount,0))) / 100 as integer) * IFNULL(D.SupPerc,0) / 100 as integer) \n" +
                " END) as SupAmount \n" +
                " From " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI \n" +
                " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E ON E.EvcID  = EI.EVCRef AND EI.FreeReasonId is Null\n" +
                " INNER JOIN " + DATABASE_TABLE+ " ES  ON ES.EVCItemRef=EI._ID \n" +
                " INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " D   ON D.ID  = ES.DisRef AND D.ID = " + discountId + " \n" +
                " Where EI.EVCRef = '" + evcId + "' AND E.DisType <> 4 AND D.PrizeType = '2' AND CalcPriority=" + priority ;

        Cursor c = db.rawQuery(selectData, null);
        if (c != null && c.moveToFirst()) {
            do {
                String sql = "update " + DATABASE_TABLE + " set Discount=" + c.getDouble(1) + ", SupAmount=" + c.getDouble(2) +
                        " where _id=" + c.getInt(0);
                db.execSQL(sql);
            } while (c.moveToNext());
        }
    }

    public void updateDiscountBasedOnPriority2(String evcId, int priority, int discountId) {

        String selectData = "select ES._id, " +
                " IFNULL(ES.Discount,0) + IFNULL(D.DisPrice*EI.TotalQty,0) as discount, " +
                " IFNULL(ES.SupAmount,0) + cast(IFNULL(D.DisPrice,0) * IFNULL(D.SupPerc,0) / 100 as integer) as SupAmount " +
                " From " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E ON E.EvcID  = EI.EVCRef " +
                " INNER JOIN " + DATABASE_TABLE + " ES  ON ES.EVCItemRef=EI._ID " +
                " INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " D   ON D.ID  = ES.DisRef AND D.ID = " + discountId +
                " Where EI.EVCRef = '" + evcId + "' AND E.DisType <> 4 AND D.PrizeType = '3' AND CalcPriority= " + priority;
        Cursor c = db.rawQuery(selectData, null);
        if (c != null && c.moveToFirst()) {
            do {
                String sql = "update " + DATABASE_TABLE + " set Discount=" + c.getDouble(1) + ", SupAmount=" + c.getDouble(2) +
                        " where _id=" + c.getInt(0);
                db.execSQL(sql);
            } while (c.moveToNext());
        }
    }

    public void updateAddAmountBasedOnPriority(String evcId, int priority, int discountId) {

        String selectData = "select ES._id, " +
                " IFNULL(ES.AddAmount,0) + (CASE " +
                " WHEN D.CalcMethod=1 THEN cast((IFNULL(D.AddPerc,0)*EI.Amount) / 100 as integer) " +
                " WHEN D.CalcMethod=3 THEN cast((IFNULL(D.AddPerc,0)*(EI.Amount-IFNULL(EI.Discount,0))) / 100 as integer) " +
                " ELSE cast((IFNULL(D.AddPerc,0)*(EI.Amount+IFNULL(EI.AddAmount,0)-IFNULL(EI.Discount,0))) / 100 as integer) " +
                " END) as AddAmount " +
                " From " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E ON E.EvcID  = EI.EVCRef " +
                " INNER JOIN " + DATABASE_TABLE + " ES  ON ES.EVCItemRef=EI._ID " +
                " INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " D   ON D.ID  = ES.DisRef AND D.ID = " + discountId +
                " Where EI.EVCRef = '" + evcId + "' AND D.PrizeType = '4'  AND CalcPriority= " + priority;
        Cursor c = db.rawQuery(selectData, null);
        if (c != null && c.moveToFirst()) {
            do {
                String sql = "update " + DATABASE_TABLE + " set AddAmount=" + c.getDouble(1) +
                        " where _id=" + c.getInt(0);
                db.execSQL(sql);
            } while (c.moveToNext());
        }
    }

    public void updateDiscountBasedOnPriority3(String evcId, int priority, int discountId) {

        String selectData = "select EVCItemRef, Sum(IFNULL(Discount,0)) as discount, " +
                " Sum(IFNULL(SupAmount,0)) as SupAmount, " +
                " Sum(IFNULL(AddAmount,0)) as AddAmount " +
                " from " + DATABASE_TABLE + " where EvcId='" + evcId + "' " +
                " group by EVCItemRef ";

        Cursor c = db.rawQuery(selectData, null);
        if (c != null && c.moveToFirst()) {
            do {
                String sql = "update " + EVCItemSDSDBAdapter.DATABASE_TABLE +
                        " set Discount=" + c.getDouble(1) +
                        "  ,SupAmount=" + c.getDouble(2) +
                        "  ,AddAmount=" + c.getDouble(3) +
                        " where _id=" + c.getInt(0);
                db.execSQL(sql);
            } while (c.moveToNext());
        }
    }

    public double updateDiscountApplied(String evcId, int discountId, double totalDiscountApplied) throws DiscountException {
        double discountApplied = 0d;
        String selectData =
                "SELECT  SUM(EI.Amount) as SumEvcItemAmount " +
                        ",MAX(ES._id) as LastEvcItemId "+
                        ",MIN(d.TotalDiscount) as TotalDiscount " +
                        "FROM " + DATABASE_TABLE + " ES "+
                        "INNER JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI on es.EVCItemRef=EI.ID " +
                        "INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID = ES.DisRef AND D.ID= " +discountId +
                        "WHERE EI.EVCRef = " + evcId +" AND ES.DisRef = " + discountId;
        Cursor c = db.rawQuery(selectData, null);
        if (c != null && c.moveToFirst()) {
            double sumEvcItemAmount = c.getDouble(c.getColumnIndex("SumEvcItemAmount"));
            double lastEvcItemId = c.getDouble(c.getColumnIndex("LastEvcItemId"));
            double totalDiscount = c.getDouble(c.getColumnIndex("TotalDiscount"));
            if (totalDiscount > sumEvcItemAmount) {
                String errMsg =
                        "مبلغ جايزه قانون از مجموع مبالغ مشمول قانون بزرگتر است" +
                                "کد قانون تخفيف = " + discountId  +
                                "مبلغ تخفيف = " + totalDiscount +
                                "مجموع مبالغ مشمول = " + sumEvcItemAmount ;
                throw new DiscountException(errMsg);
            }

            String selectApplied =
                    " SELECT (EI.Amount/IFNULL( " + sumEvcItemAmount +",0)*"+ totalDiscount + ") as TotalDiscountApplied " +
                            " FROM " + DATABASE_TABLE + " ES "+
                            "INNER JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI on es.EVCItemRef=EI._ID " +
                            "INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID = ES.DisRef AND D.ID= " +discountId +
                            "WHERE EI.EVCRef = " + evcId +" AND ES.DisRef = " + discountId;
            Cursor c2 = db.rawQuery(selectApplied, null);
            if (c2 != null && c2.moveToFirst()) {
                discountApplied = c.getDouble(c.getColumnIndex("TotalDiscountApplied"));
            }
            totalDiscountApplied += discountApplied;

            String sql = " UPDATE " + DATABASE_TABLE +
                    " SET Discount = (SELECT IFNULL(ES.Discount,0) + (EI.Amount/IFNULL( " +sumEvcItemAmount + ",0)* " + totalDiscount + ")" +
                    " FROM " + DATABASE_TABLE + " ES "+
                    " INNER JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI on es.EVCItemRef=EI._ID " +
                    " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID = ES.DisRef AND D.ID= " + discountId +
                    " WHERE EI.EVCRef = " + evcId +" AND ES.DisRef = " + discountId + ")";
            db.execSQL(sql);

            db.execSQL(" UPDATE " + DATABASE_TABLE +
                    " SET Discount = IFNULL(Discount,0) + " + (totalDiscount - totalDiscountApplied) +
                    " WHERE _id=" + lastEvcItemId);

        }
        return totalDiscountApplied;
    }

    public Cursor getEvcItemStatuteDataToSend(String evcId, int evcItemRefId) {

        Cursor c = null;
        if (db != null)
            c = db.query(DATABASE_TABLE, new String[]{KEY_EVC_ITEM_STATUTES_ITEM_REF, KEY_EVC_ITEM_STATUTES_DIS_REF, KEY_EVC_ITEM_STATUTES_DISCOUNT
                    , KEY_EVC_ITEM_STATUTES_ADD_AMOUNT, KEY_EVC_ITEM_STATUTES_SUP_AMOUNT, KEY_EVC_ITEM_STATUTES_ROW_ORDER, KEY_EVC_ITEM_STATUTES_DIS_GROUP}
                    , KEY_EVC_ITEM_STATUTES_EVC_ID + "='" + evcId + "' AND " + KEY_EVC_ITEM_STATUTES_ITEM_REF + "='" + evcItemRefId + "'"
                    , null, null, null, null);
        return c;
    }

    public void deleteAllEVCItemStatutes()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

    public ArrayList<DiscountEvcItemStatuteData> getEvcItemStatuteDataToSend(String evcId)
    {
        ArrayList<DiscountEvcItemStatuteData> list = new ArrayList<>();
        String sql = "SELECT " + EVCItemSDSDBAdapter.DATABASE_TABLE +".OrderLineId, EVCItemRef, " +
                            DATABASE_TABLE + ".DisRef as DisRef, " +DATABASE_TABLE + ".Discount as Discount, " +
                            DATABASE_TABLE + ".AddAmount as AddAmount, " +
                            DATABASE_TABLE + ".SupAmount as SupAmount, GoodsRef " +
                    "   FROM " + DATABASE_TABLE + " JOIN " +
                            EVCItemSDSDBAdapter.DATABASE_TABLE + " ON EvcItemRef = EVCItemSDS._Id " +
                    "   WHERE EvcId ='" + evcId + "'";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                DiscountEvcItemStatuteData item = new DiscountEvcItemStatuteData();
                item.EvcItemRef = cursor.getInt(cursor.getColumnIndex("GoodsRef"));
                item.OrderLineId = cursor.getString(cursor.getColumnIndex("OrderLineId"));
                item.DisRef = cursor.getInt(cursor.getColumnIndex("DisRef"));
                item.Discount = cursor.getLong(cursor.getColumnIndex("Discount"));
                item.AddAmount = cursor.getLong(cursor.getColumnIndex("AddAmount"));
                item.SupAmount = cursor.getLong(cursor.getColumnIndex("SupAmount"));

                list.add(item);

            } while (cursor.moveToNext());
        }
        return list;
    }

    public void updateOrderLineId()
    {
        String sql = "UPDATE EVCItemStatutesSDS\n" +
                "SET OrderLineId = (SELECT OrderLineId FROM EVCItemSDS \n" +
                "WHERE  EVCItemSDS._id  = EVCItemStatutesSDS.EvcItemRef)";
        db.execSQL(sql);
    }
}
	 
