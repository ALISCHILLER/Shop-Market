package varanegar.com.discountcalculatorlib.dataadapter.discount;

import android.database.Cursor;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;


public class DiscountDBAdapter extends DiscountBaseDataAdapter {


    public static final String KEY_ID = "Id";
    public static final String KEY_PRIZE_PACKAGE_REF = "PrizePackageRef";

    public static final String DATABASE_TABLE = (GlobalVariables.getBackOfficeVersion() == BackOfficeVersion.SDS16 ? "DiscountSDS4_16" : "DiscountSDS4_19");

    private static DiscountDBAdapter instance;
    protected DiscountDBAdapter()
    {
    }

    public static DiscountDBAdapter getInstance()
    {
        if(instance == null)
        {
            instance = new DiscountDBAdapter();
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

}
