package varanegar.com.discountcalculatorlib.dataadapter.discount;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProduct;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;

public class DiscountItemDBAdapter  extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountDiscountItemCount";

    private static DiscountItemDBAdapter instance;

    public static DiscountItemDBAdapter getInstance() {
        if (instance == null) {
            instance = new DiscountItemDBAdapter();
        }
        return instance;
    }

    private DiscountItemDBAdapter() {

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public synchronized List<DiscountCallOrderLineData> getPrizeProducts(String disRef) {
        List<DiscountCallOrderLineData> prizeProducts = new ArrayList<>();
        String sql = "SELECT I.GoodsRef, P.ProductCode, P.ProductName, P.SmallUnitId, P.LargeUnitId, P.CanBeFree \n" +
                "FROM " + DATABASE_TABLE + " AS I \n" +
                "JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " AS P \n" +
                "ON I.GoodsRef = P.ProductId \n" +
                "WHERE I.disRef = '" + disRef + "'\n" +
                "AND P.IsForSale = 1";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        do {
            DiscountCallOrderLineData callLineDataWithPromo = new DiscountCallOrderLineData();
            callLineDataWithPromo.productId = c.getInt(0);
            callLineDataWithPromo.productCode = c.getString(1);
            callLineDataWithPromo.productName = c.getString(2);
            callLineDataWithPromo.invoiceSmallQtyId = c.getLong(3);
            callLineDataWithPromo.invoiceBigQtyId = c.getLong(4);
            callLineDataWithPromo.isFreeItem = c.getInt(5);
            prizeProducts.add(callLineDataWithPromo);
        } while (c.moveToNext());

        return prizeProducts;
    }

    public static int CheckOrderDiscountRef(int newDiscountId, int orderId) {
        String sql;
        int orderDisRef = 0;
        sql = "SELECT order_dp.DisRef DiscountRef\n" +
                "FROM " + DATABASE_TABLE + " order_dp\n" +
                "JOIN " + DATABASE_TABLE + " new_dp \n" +
                "WHERE order_dp.DisRef in (select DiscountRef from DiscountOrderPrize WHERE OrderRef = " + orderId + ")\n" +
                "AND new_dp.DisRef = " + newDiscountId + "\n" +
                "AND order_dp.GoodsRef=new_dp.GoodsRef\n" +
                "LIMIT 1";
        Cursor affectedCursor = db.rawQuery(sql, null);
        if (affectedCursor != null && affectedCursor.moveToFirst()) {
            orderDisRef = affectedCursor.getInt(0);
        }
        if (orderDisRef != 0) {
            return orderDisRef;
        }
        String sql2 = "SELECT order_dp.DisRef DiscountRef\n" +
                "FROM DiscountDiscountItemCount order_dp\n" +
                "cross join DiscountDiscountItemCount new_dp\n" +
                "where order_dp.DisRef in (select DiscountRef  from DiscountOrderPrize  WHERE OrderRef = ?)\n" +
                "and new_dp.DisRef = ?\n" +
                "and order_dp.GoodsRef=new_dp.GoodsRef\n" +
                "LIMIT 1";
        Cursor affectedCursor2 = null;
        affectedCursor2 = db.rawQuery(sql2, new String[]{String.valueOf(orderId), String.valueOf(newDiscountId)});
        if (affectedCursor2 != null && affectedCursor2.moveToFirst()) {
            orderDisRef = affectedCursor2.getInt(affectedCursor2.getColumnIndex("DiscountRef"));
        }
        if (orderDisRef == 0) {
            String error =
                    "به دلیل تغییر قانون جایزه انتخابی، برای کالاهای قانون جدید، قانون مشابهی در قوانین حواله مرجع وجود ندارد" +
                            " قانون جدید = " + newDiscountId;
            throw new DiscountException(error);
        }
        return orderDisRef;
    }

}
