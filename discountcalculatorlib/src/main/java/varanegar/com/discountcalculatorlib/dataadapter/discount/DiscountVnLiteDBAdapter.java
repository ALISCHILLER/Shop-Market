package varanegar.com.discountcalculatorlib.dataadapter.discount;

import android.database.Cursor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemStatutesVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;


public class DiscountVnLiteDBAdapter extends DiscountBaseDataAdapter {

    public static final String DATABASE_TABLE = "DiscountVnLite";

    private static DiscountVnLiteDBAdapter instance;
    protected DiscountVnLiteDBAdapter()
    {
    }

    public static DiscountVnLiteDBAdapter getInstance()
    {
        if(instance == null)
        {
            instance = new DiscountVnLiteDBAdapter();
        }

        return instance;
    }

    public int getCount(){
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE, null);
        if (c.moveToFirst()){
            return c.getInt(0);
        }
        return 0;
    }

    public void clearAllData(){
        db.execSQL("delete from " + DATABASE_TABLE);
       // db.delete(DATABASE_TABLE, null , null);
    }

    public BigDecimal getTotalAmount(String evcId){

        String sql = " SELECT SUM(EI.Amount) as Amount \n" +
                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n" +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EVCID= EI.EVCRef \n" +
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +" ES ON ES.EVCItemRef = EI._id  \n" +
                " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId = ES.DisRef  \n" +
                " WHERE ifnull(D.TotalDiscount,0) != 0 and EI.evcRef ='"+evcId+"' ";
        Cursor cItem = db.rawQuery(sql, null);

        if (cItem != null) {
            if (cItem.moveToFirst()) {
                return new BigDecimal(cItem.getDouble(cItem.getColumnIndex("Amount")));
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalDiscountAmount(String evcId){

        String sql = "SELECT SUM(DiscountAmount) as DiscountAmount " +
                " FROM( SELECT DISTINCT D.PromotionDetailId, D.TotalDiscount as DiscountAmount \n" +
                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n" +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EVCID= EI.EVCRef \n" +
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +" ES ON ES.EVCItemRef = EI._id  \n" +
                " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId = ES.DisRef  \n" +
                " WHERE ifnull(D.TotalDiscount,0) != 0 and EI.evcRef ='"+evcId+"' ) AS TT ";
        Cursor cItem = db.rawQuery(sql, null);

        if (cItem != null) {
            if (cItem.moveToFirst()) {
                return new BigDecimal(cItem.getDouble(cItem.getColumnIndex("DiscountAmount")));
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTestDiscountAmount(String evcId, BigDecimal totalAmount){

        String sql = " SELECT  SUM(Cast((IFNULL(D.TotalDiscount,0)*((EI.Amount*1.0)/ "+totalAmount+")) AS INT )) as DiscountAmount \n" +
                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n" +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EVCID= EI.EVCRef \n" +
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +" ES ON ES.EVCItemRef = EI._id  \n" +
                " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId = ES.DisRef  \n" +
                " WHERE IFNULL(D.TotalDiscount,0) != 0 and EI.EVCRef= '" + evcId +"'";
        Cursor cItem = db.rawQuery(sql, null);

        if (cItem != null) {
            if (cItem.moveToFirst()) {
                return new BigDecimal(cItem.getDouble(cItem.getColumnIndex("DiscountAmount")));
            }
        }
        return BigDecimal.ZERO;
    }

}
