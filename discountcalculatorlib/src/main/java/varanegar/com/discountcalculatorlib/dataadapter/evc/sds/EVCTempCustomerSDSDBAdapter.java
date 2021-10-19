package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;

/**
 * Created by m.aghajani on 3/28/2016.
 */
public class EVCTempCustomerSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_CUSTOMER_ROWID = "_id";
    public final String KEY_CUSTOMER_ID = "CustRef";
    public final String KEY_CUSTOMER_CATEGORY_ID = "CustCtgrRef";
    public final String KEY_CUSTOMER_ACT_ID = "CustActRef";
    public final String KEY_CUSTOMER_LEVEL_ID = "CustLevelRef";
    public final String KEY_CUSTOMER_AREA_ID = "AreaRef";
    public final String KEY_CUSTOMER_SALE_PATH_ID = "SalePathRef";
    public final String KEY_CUSTOMER_ZONE_ID = "ZoneId";
    public final String KEY_CUSTGROUP_ID = "CustGroupRef";
    public final String KEY_CUST_CUSTOMERCHEQUERETQTY = "CustChequeRetQty";

    public static final String DATABASE_TABLE = "EVCTempCustomerSDS";
    private static String TAG = "EVCTempCustomerSDSDBAdapter";
    private static EVCTempCustomerSDSDBAdapter instance;

    private EVCTempCustomerSDSDBAdapter() {
    }

    public static EVCTempCustomerSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempCustomerSDSDBAdapter();
        }

        return instance;

    }

    /*usp_FillEVCStatuteByID_FillDiscount*/
    public void fillCustomerTemp(String evcId, EVCType evcType, int refId)
    {
        String query = "";

        //TODO not sure about filling SalePathRef From Customer
        if(evcType == EVCType.SELLRETURN)
        query = "INSERT INTO " + DATABASE_TABLE +
                " (CustRef, CustCtgrRef, CustActRef, CustLevelRef, AreaRef, SalePathRef, ZoneId, CustomerRemain, CustChequeRetQty, " +
                "CountyRef, StateRef ,CustGroupRef) " +
                " SELECT c.CustomerId, "+
                "        CASE WHEN (shd.SaleId IS NOT NULL) THEN IFNULL(shd.CustCtgrRef, 0) ELSE IFNULL(c.CustomerCategoryId, 0) END AS CustCtgrRef, " +
                "        CASE WHEN (shd.SaleId IS NOT NULL) THEN IFNULL(shd.CustActRef, 0) ELSE IFNULL(c.CustomerActivityId, 0) END AS CustActRef, " +
                "        CASE WHEN (shd.SaleId IS NOT NULL) THEN IFNULL(shd.CustLevelRef, 0) ELSE IFNULL(c.CustomerLevelId, 0) END AS CustLevelRef, " +
                "        c.AreaId, c.StateId, c.ZoneId, C.CustomerRemain, C.CustChequeRetQty, c.CountyRef, c.StateId ,CustGroupRef " +
                " FROM " + DiscountCustomerDBAdapter.DATABASE_TABLE + " c " +
                " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " o  ON c.CustomerId = o.CustRef" +
                " LEFT JOIN " + DiscountCustomerOldInvoiceHeaderDBAdapter.DATABASE_TABLE + " shd ON shd.SaleId = '" + refId + "' " +
                " WHERE o.EvcId = '" + evcId + "'";
        else
            query = "INSERT INTO " + DATABASE_TABLE +
                    " (CustRef, CustCtgrRef, CustActRef, CustLevelRef, AreaRef, SalePathRef, ZoneId, CustomerRemain, CustGroupRef, CountyRef, StateRef) " +
                    " SELECT c.CustomerId, IFNULL(c.CustomerCategoryId, 0) AS CustCtgrRef, " +
                    "       IFNULL(c.CustomerActivityId, 0) AS CustActRef, IFNULL(c.CustomerLevelId, 0) AS CustLevelRef, " +
                    "       c.AreaId, c.StateId, c.ZoneId, C.CustomerRemain, c.CustGroupRef, c.CountyRef, c.StateId " +
                    " FROM " + DiscountCustomerDBAdapter.DATABASE_TABLE + " c " +
                    " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " o  ON c.CustomerId = o.CustRef" +
                    " WHERE o.EvcId = '" + evcId + "'";
        db.execSQL(query);
    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }


    public Cursor getCustomerInfo(String CustRef) {
        Cursor c = null;
        if (db != null) {
            String sql = " SELECT  IFNULL(C.AreaRef,0) AreaRef," +
                            "IFNULL(C.CustCtgrRef,0) CustCtgrRef," +
                            "IFNULL(C.CustActRef,0) CustActRef," +
                            "IFNULL(C.CustLevelRef,0) CustLevelRef," +
                            "IFNULL(C.ZoneId,0) ZoneId, " +
                            "IFNULL(C.SalePathRef,0)SalePathRef \n" +
                    " FROM " + DATABASE_TABLE + " C \n" +
                    " WHERE C.CustRef = " + CustRef + " ";
            c = db.rawQuery(sql, null);
        }
        return c;
    }

}
