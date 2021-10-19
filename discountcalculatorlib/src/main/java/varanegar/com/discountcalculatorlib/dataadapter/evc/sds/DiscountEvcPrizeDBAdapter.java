package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountEvcPrizeData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;

/**
 * Created by A.Razavi on 3/27/2018.
 */

public class DiscountEvcPrizeDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountEvcPrize";
    private static DiscountEvcPrizeDBAdapter instance;

    public DiscountEvcPrizeDBAdapter() {
    }

    public static DiscountEvcPrizeDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountEvcPrizeDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void insertByGoodPackage(String evcId, int disId, int prizeType,int prizePackageRef, int evcType, int orderId){

        boolean exist = false;
        Cursor affectedCursor = db.rawQuery("Select count(*) from " + DiscountEvcPrizeDBAdapter.DATABASE_TABLE +" where EvcRef = '" + evcId+ "' AND QtyUnit = 1", null);
        if (affectedCursor != null && affectedCursor.moveToFirst()) {
            if (affectedCursor.getInt(0) > 0) {
                exist = true;
            }
        }
        String sql;
        if (!exist){
            int orderDiscountRef = 0;
            if (evcType == 3) {

                orderDiscountRef = DiscountItemDBAdapter.getInstance().CheckOrderDiscountRef(disId, orderId);
            }


            sql = "INSERT INTO " + DATABASE_TABLE + " (EvcRef, DiscountRef, GoodsRef, PrizeQty,OrderDiscountRef) " +
                    " SELECT '" + evcId + "'," + disId + ",I.GoodsRef, I.PrizeCount AS TotalQty, "+ orderDiscountRef +"\n"+
                    " FROM " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + " I " +
                    " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID = " + disId + " AND I.DiscountId = D.ID AND D.PrizeType = 6 " +
                    " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk on pk.ProductId = I.GoodsRef and pk.BackOfficeId = I.UnitRef ";
            db.execSQL(sql);
        }
    }
    public void updateQtyUnit(String evcId){
        String sql = "UPDATE " + DATABASE_TABLE +
                " Set QtyUnit=1 Where EvcRef= '"+ evcId +"'";
        db.execSQL(sql);
    }


    public static void updateDiscountEvcPrize(ArrayList<DiscountOrderPrizeViewModel> list, int discountRef, int orderDiscountRef, int orderRef, String evcId){
        db.execSQL("DELETE FROM "+ DATABASE_TABLE + " WHERE DiscountRef =  "+ orderDiscountRef );

        String  query =
                "INSERT INTO DiscountEvcPrize (Id,EvcREf,DiscountRef,GoodsRef,PrizeQty,QtyUnit,OrderDiscountRef)\n" +
                        "SELECT Id,'"+evcId+"',"+ orderDiscountRef +",GoodsRef,Qty,IFNULL(SaleQty,Qty),DiscountRef\n" +
                        "FROM DiscountOrderPrize "+
                        "WHERE OrderRef = " + orderRef+ " AND DiscountRef =  "+ discountRef
                ;
        db.execSQL(query);

        for (DiscountOrderPrizeViewModel item:list) {
            String sql = "UPDATE " + DATABASE_TABLE +
                    " SET PrizeQty = " +item.qty +
                    " WHERE EvcRef = '" + evcId + "'"+
                    " AND DiscountRef = " + discountRef +
                    " AND GoodsRef = " + item.goodsRef;
            db.execSQL(sql);
        }
    }

    public  void resetEvcPrize(String evcId, int orderRef, List<DiscountPrizeViewModel> discountPrizeViewModels){

        if (discountPrizeViewModels == null || discountPrizeViewModels.isEmpty())
            return;

        String ids = "";
        for (DiscountPrizeViewModel item : discountPrizeViewModels ) {
            ids += (ids != "" ?  ","+item.discountRef : item.discountRef);
        }
        db.execSQL("DELETE FROM "+ DATABASE_TABLE + " WHERE DiscountRef not in("+ ids +")");

        String  query =
                "INSERT INTO DiscountEvcPrize (Id,EvcREf,DiscountRef,GoodsRef,PrizeQty,QtyUnit,OrderDiscountRef)\n" +
                        "SELECT Id,'"+evcId+"',OrderDiscountRef,GoodsRef,Qty,IFNULL(SaleQty,Qty),DiscountRef\n" +
                        "FROM DiscountOrderPrize "+
                        "WHERE OrderRef = " + orderRef+ " AND DiscountRef not in ( "+ ids + ") AND (Qty > 0) ";
        db.execSQL(query);
    }


    public static ArrayList<DiscountOrderPrizeViewModel> GetEvcPrizes(int discountRef) {
        ArrayList<DiscountOrderPrizeViewModel> orderPrizeList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT GoodsRef, PrizeQty Qty, ProductName, ProductCode\n" +
                "FROM " + DATABASE_TABLE + " ord join DiscountProduct on GoodsRef = ProductId  \n" +
                "WHERE ord.DiscountRef = " + discountRef, null);

        if ((c != null) && (c.moveToFirst())) {
            do {
                DiscountOrderPrizeViewModel prize = new DiscountOrderPrizeViewModel();
                prize.goodsRef = c.getInt(c.getColumnIndex("GoodsRef"));
                prize.qty = c.getInt(c.getColumnIndex("Qty"));
                prize.goodCode = c.getString(c.getColumnIndex("ProductCode"));
                prize.goodName = c.getString(c.getColumnIndex("ProductName"));
                orderPrizeList.add(prize);
            } while (c.moveToNext());
        }
        return orderPrizeList;
    }

    public static ArrayList<DiscountEvcPrizeData> GetEvcPrizesByOrder(String evcId) {
        ArrayList<DiscountEvcPrizeData> orderPrizeList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT ord.GoodsRef, ord.PrizeQty Qty, ord.DiscountRef, d.uniqueId as DiscountId " +
                "FROM " + DATABASE_TABLE + " ord join  " + DiscountDBAdapter.DATABASE_TABLE + " d on DiscountRef = d.id "+
                "WHERE EvcRef = '" + evcId + "'", null);

        if ((c != null) && (c.moveToFirst())) {
            do {
                DiscountEvcPrizeData prize = new DiscountEvcPrizeData();
                prize.GoodRef = c.getInt(c.getColumnIndex("GoodsRef"));
                prize.Qty = new BigDecimal(c.getDouble(c.getColumnIndex("Qty")));
                prize.DiscountRef = c.getInt(c.getColumnIndex("DiscountRef"));
                prize.DiscountId = UUID.fromString(c.getString(c.getColumnIndex("DiscountId")));
                orderPrizeList.add(prize);
            } while (c.moveToNext());
        }
        return orderPrizeList;
    }

    public static ArrayList<DiscountOrderPrizeViewModel> GetAllEvcPrizes() {
        ArrayList<DiscountOrderPrizeViewModel> orderPrizeList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT GoodsRef, PrizeQty Qty, DiscountRef \n" +
                "FROM DiscountEvcPrize ord  \n" , null);

        if ((c != null) && (c.moveToFirst())) {
            do {
                DiscountOrderPrizeViewModel prize = new DiscountOrderPrizeViewModel();
                prize.goodsRef = c.getInt(c.getColumnIndex("GoodsRef"));
                prize.qty = c.getInt(c.getColumnIndex("Qty"));
                prize.discountRef = c.getInt(c.getColumnIndex("DiscountRef"));
                orderPrizeList.add(prize);
            } while (c.moveToNext());
        }
        return orderPrizeList;
    }

    public static  void fill(String evcId){
        //TODO ASAL im not sure, i insert EvcPrize temporary
        db.delete(DATABASE_TABLE, null, null);
        db.execSQL("INSERT INTO " + DATABASE_TABLE +" (EvcRef" +
                ",DiscountRef" +
                ",GoodsRef" +
                ",PrizeQty" +
                ",QtyUnit)\n" +
                "SELECT " +
                "'"+evcId+"' as EvcRef" +
                ",DiscountRef" +
                ",GoodsRef" +
                ",Qty" +
                ",0\n" +
                " FROM DiscountOrderPrize");
    }

}
