package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductPackageDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductPackageItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;

/**
 * Created by A.Razavi on 4/3/2018.
 */

public class DiscountEvcPrizePackageDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountEvcPrizePackage";
    private static DiscountEvcPrizePackageDBAdapter instance;

    public DiscountEvcPrizePackageDBAdapter() {
    }

    public static DiscountEvcPrizePackageDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountEvcPrizePackageDBAdapter();
        }

        return instance;

    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void insertByPrizePackageRef(String evcId, int disId, int prizeType,int prizePackageRef){
        String sql;
		sql = " INSERT INTO " + DATABASE_TABLE + " (EvcRef, DiscountRef, MainGoodsPackageItemRef, ReplaceGoodsPackageItemRef, PrizeCount, PrizeQty)"+
		" SELECT '" + evcId +"',"+ disId +", GP.Id, GP2.ReplaceGoodsPackageItemRef, GP2.PrizeCount, GP2.PrizeCount*pk.Quantity*GP2.UnitQty as PrizeQty "+
		" FROM " + DiscountProductPackageItemDBAdapter.DATABASE_TABLE + " GP "+
		" INNER JOIN " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + " GP2 on GP.GoodsRef=GP2.BaseGoodsRef "+
		" INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk on pk.ProductId=GP2.GoodsRef and pk.BackOfficeId=GP2.UnitRef "+
		" WHERE GP.GoodsPackageRef= " + prizePackageRef +
        " AND GP.ReplaceGoodsRef is null ";
		db.execSQL(sql);

    }

    public void insertByGoodPackage(String evcId, int disId, int prizeType,int prizePackageRef){
        String sql;
		sql = " INSERT INTO " + DATABASE_TABLE + " (EvcRef, DiscountRef, MainGoodsPackageItemRef, ReplaceGoodsPackageItemRef, PrizeCount, PrizeQty)"+
		" SELECT '" + evcId +"',"+ disId +", null, null, GP2.PrizeCount, GP2.PrizeCount*pk.Quantity*GP2.UnitQty as PrizeQty "+
		" FROM " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + " GP2 "+
		" INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk on pk.ProductId=GP2.GoodsRef and pk.BackOfficeId=GP2.UnitRef ";
		db.execSQL(sql);
    }
}
