package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerMainSubTypeDBAdapter;

public class EVCTempCustomersMainSubTypeSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_EVC_TEMP_ROWID = "_id";
    public final String KEY_EVC_TEMP_SUBTYPE_ID = "Id";
    public final String KEY_EVC_TEMP_SUBTYPE_CustomerS_REF = "CustomersRef";
    public final String KEY_EVC_TEMP_SUBTYPE_MAIN_GROUP_REf = "MainTypeRef";
    public final String KEY_EVC_TEMP_SUBTYPE_SUB_GROUP_REF = "SubTypeRef";


    public static final String DATABASE_TABLE = "EVCTempCustomersMainSubTypeSDS";
    private static EVCTempCustomersMainSubTypeSDSDBAdapter instance;

    public EVCTempCustomersMainSubTypeSDSDBAdapter() {
    }

    public static EVCTempCustomersMainSubTypeSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempCustomersMainSubTypeSDSDBAdapter();
        }

        return instance;

    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }


    /*usp_FillEVCStatuteByID_FillDiscount*/
    public void fillEVCTempByMainTable(String evcId, int custRef)
    {
        String query = "";
        query = "INSERT INTO " + DATABASE_TABLE +
                " (Id, CustRef, MainTypeRef, SubTypeRef) " +
                " SELECT MS.Id, MS.CustRef, MS.MainTypeRef, MS.SubTypeRef " +
                " FROM " + DiscountCustomerMainSubTypeDBAdapter.DATABASE_TABLE + " MS " +
                " WHERE MS.CustRef = " + custRef ;
        db.execSQL(query);
    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

}

