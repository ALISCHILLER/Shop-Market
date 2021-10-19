package varanegar.com.discountcalculatorlib.dataadapter.product;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.DiscountEvcPrizeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemStatutesSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempAcceptedDiscountAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCTempOrderPrizeSDSDBAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;

/**
 * Created by A.Razavi on 3/27/2018.
 */

public class DiscountOrderPrizeDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountOrderPrize";
    private static DiscountOrderPrizeDBAdapter instance;

    public DiscountOrderPrizeDBAdapter() {
    }

    public static DiscountOrderPrizeDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountOrderPrizeDBAdapter();
        }

        return instance;

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public static ArrayList<Integer> GetChangedDiscounts(int orderRef) throws DiscountException {
        Cursor c = db.rawQuery("SELECT ord.DiscountRef\n" +
                "FROM DiscountOrderPrize ord JOIN DiscountEvcPrize evc ON ord.DiscountRef = evc.DiscountRef\n" +
                "WHERE Ord.OrderRef = " + orderRef + "\n"+
                "GROUP BY ord.DiscountRef\n" +
                "HAVING  SUM(ord.Qty)  > SUM(evc.PrizeQty) \n", null);
        ArrayList<Integer> discountlist = new ArrayList<Integer>();
        if ((c != null) && (c.moveToFirst())) {
            do {
                discountlist.add(c.getInt(0));
            } while (c.moveToNext());
        }
        return discountlist;

    }

    public static DiscountPrizeViewModel GetPrizes(int discountRef) throws DiscountException {
        DiscountPrizeViewModel discountPrize = new DiscountPrizeViewModel();
        discountPrize.discountRef = discountRef;
        discountPrize.orderPrizeList = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT SUM(evc.PrizeQty)\n" +
                "FROM DiscountEvcPrize evc\n" +
                "WHERE evc.DiscountRef = "+discountRef, null);

        if ((c != null) && (c.moveToFirst())) {
            discountPrize.qty = c.getInt(0);
        }

        c = db.rawQuery("SELECT GoodsRef, Qty, SaleQty, ProductName, ProductCode\n" +
                "FROM DiscountOrderPrize ord join DiscountProduct on GoodsRef = ProductId \n" +
                "WHERE ord.DiscountRef = " + discountRef, null);

        if ((c != null) && (c.moveToFirst())) {
            do {
                DiscountOrderPrizeViewModel prize = new DiscountOrderPrizeViewModel();
                prize.goodsRef = c.getInt(c.getColumnIndex("GoodsRef"));
                prize.qty = c.getInt(c.getColumnIndex("Qty"));
                prize.goodCode = c.getString(c.getColumnIndex("ProductCode"));
                prize.goodName = c.getString(c.getColumnIndex("ProductName"));
                discountPrize.orderPrizeList.add(prize);
            } while (c.moveToNext());
        }

        return discountPrize;
    }

    public static ArrayList<DiscountOrderPrizeViewModel> GetOrderPrizes(int discountRef, int orderRef) {
        ArrayList<DiscountOrderPrizeViewModel> orderPrizeList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT GoodsRef, Qty, SaleQty, ProductName, ProductCode\n" +
                "FROM DiscountOrderPrize ord join DiscountProduct on GoodsRef = ProductId \n" +
                "WHERE ord.DiscountRef = " + discountRef + " AND ord.OrderRef = " + orderRef, null);

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

    public void checkPrize(String evcId, int evcType, int orderRef){
        int existEvcPrive = 0;
        String sql = " SELECT 1 FROM " + DiscountEvcPrizeDBAdapter.DATABASE_TABLE +
                " WHERE EvcRef= '" +evcId+ "' and QtyUnit is null";
        Cursor tempC = db.rawQuery(sql, null);
        if (tempC != null && tempC.moveToFirst()) {
            existEvcPrive = tempC.getInt(0);
        }

        if (evcType == 3 && existEvcPrive == 1) {
            sql = "SELECT X.DiscountRef as orderDiscountRef, Y.DiscountRef as discountRef, X.Qty as sumOrderPrize, Y.PrizeQty as expectedPrize   \n" +
                  "  FROM(\n" +
                  "   SELECT IFNULL(SUM(SaleQty), 0)Qty, DiscountRef\n" +
                  "   FROM " + DiscountOrderPrizeDBAdapter.DATABASE_TABLE + " O\n" +
                  "   WHERE OrderRef = "+orderRef +" AND qty > 0\n" +
                  "   GROUP BY DiscountRef\n" +
                  "  ) AS X\n" +
                  "  LEFT JOIN\n" +
                  " (SELECT Min(PrizeQty) AS PrizeQty, OrderDiscountRef, DiscountRef\n" +
                  "  FROM " + DiscountEvcPrizeDBAdapter.DATABASE_TABLE +" WHERE EvcRef = '"+ evcId +"'\n" +
                  "  GROUP BY OrderDiscountRef\n" +
                  " )AS Y ON X.DiscountRef = Y.OrderDiscountRef\n" +
                  " WHERE X.Qty <> IFNULL(PrizeQty, 0) " +
                    //TODO ASAL : in sql version dose not have limit
                  " Limit 1";
            int discountRef = 0;
            int orderDiscountRef = 0;
            int sumOrderPrize = 0;
            int expectedPrize = 0;
            int rowcount = 0;
            Cursor affectedCursor = db.rawQuery(sql, null);
            if (affectedCursor != null && affectedCursor.moveToFirst()) {
                rowcount = 1;
                discountRef = affectedCursor.getInt(affectedCursor.getColumnIndex("discountRef"));
                orderDiscountRef = affectedCursor.getInt(affectedCursor.getColumnIndex("orderDiscountRef"));
                sumOrderPrize = affectedCursor.getInt(affectedCursor.getColumnIndex("sumOrderPrize"));
                expectedPrize = affectedCursor.getInt(affectedCursor.getColumnIndex("expectedPrize"));
            }
            if (rowcount>0)
            {
                DiscountPrizeViewModel dpv = new DiscountPrizeViewModel();
                dpv.discountRef = discountRef;
                dpv.orderDiscountRef = orderDiscountRef;
                dpv.qty = expectedPrize;
                dpv.evcId = evcId;
                dpv.orderPrizeList = DiscountOrderPrizeDBAdapter.getInstance().GetOrderPrizes(orderDiscountRef, orderRef);
                throw new DiscountException(3001,
                        "مجموع جايزه انتخابي در درخواست با قانون آن هماهنگي ندارد" +
                                " کد قانون تخفيفات : " + discountRef +
                                " تعداد جايزه انتخاب شده در درخواست : " + sumOrderPrize +
                                " تعداد جايزه مورد انتظار از قانون : " + expectedPrize,
                        dpv
                );
            }
		else
            {
                sql = " UPDATE DiscountEvcPrize\n" +
                      " SET PrizeQty = (SELECT op.Qty\n" +
                      " FROM DiscountOrderPrize op \n" +
                      " WHERE op.OrderRef = " + orderRef +
                      " AND DiscountEvcPrize.EvcRef = '" + evcId +"'" +
                      " AND op.GoodsRef = DiscountEvcPrize.GoodsRef)";
                db.execSQL(sql);
            }
        }
    }

    public static  void UpdateOrderDiscount(int orderRef){
        db.execSQL("UPDATE " + DATABASE_TABLE +"\n"+
                " SET OrderDiscountRef = DiscountRef \n" +
                " WHERE OrderRef = "+ orderRef +
                " AND OrderDiscountRef IS NULL");

    }
}
