package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;

public class EVCTempCustomerVnLiteDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_CUSTOMER_ROWID = "_id";
    public final String KEY_CUSTOMER_ID = "CustRef";
    public final String KEY_CUSTOMER_CATEGORY_ID = "CustCtgrRef";
    public final String KEY_CUSTOMER_ACT_ID = "CustActRef";
    public final String KEY_CUSTOMER_LEVEL_ID = "CustLevelRef";
    public final String KEY_CUSTOMER_AREA_ID = "AreaRef";
    public final String KEY_CUSTOMER_SALE_PATH_ID = "SalePathRef";
    public final String KEY_CUSTOMER_ZONE_ID = "ZoneId";


    public static final String DATABASE_TABLE = "EVCTempCustomerVnLite";
    private static String TAG = "EVCTempCustomerVnLiteBAdapter";
    private static EVCTempCustomerVnLiteDBAdapter instance;

    private EVCTempCustomerVnLiteDBAdapter() {
    }

    public static EVCTempCustomerVnLiteDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempCustomerVnLiteDBAdapter();
        }

        return instance;

    }

    public void fillCustomerTemp(String evcId, EVCType evcType, int refId)
    {
        String query = "";

        //TODO not sure about filling SalePathRef From Customer
        if(evcType == EVCType.SELLRETURN)
        query = "INSERT INTO " + DATABASE_TABLE +
                " (CustRef, CustCtgrRef, CustActRef, CustLevelRef, AreaRef, SalePathRef, ZoneId) " +
                " SELECT c.CustomerId, "+
                "        CASE WHEN (shd.SaleId IS NOT NULL) THEN IFNULL(shd.CustCtgrRef, 0) ELSE IFNULL(c.CustomerCategoryId, 0) END AS CustCtgrRef, " +
                "        CASE WHEN (shd.SaleId IS NOT NULL) THEN IFNULL(shd.CustActRef, 0) ELSE IFNULL(c.CustomerActivityId, 0) END AS CustActRef, " +
                "        CASE WHEN (shd.SaleId IS NOT NULL) THEN IFNULL(shd.CustLevelRef, 0) ELSE IFNULL(c.CustomerLevelId, 0) END AS CustLevelRef, " +
                "        c.AreaId, c.StateId, c.ZoneId" +
                " FROM " + DiscountCustomerDBAdapter.DATABASE_TABLE + " c " +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " o  ON c.CustomerId = o.CustRef" +
                " LEFT JOIN " + DiscountCustomerOldInvoiceHeaderDBAdapter.DATABASE_TABLE + " shd ON shd.SaleId = '" + refId + "' " +
                " WHERE o.EvcId = '" + evcId + "'";
        else
            query = "INSERT INTO " + DATABASE_TABLE +
                    " (CustRef, CustCtgrRef, CustActRef, CustLevelRef, AreaRef, SalePathRef, ZoneId) " +
                    " SELECT c.CustomerId, IFNULL(c.CustomerCategoryId, 0) AS CustCtgrRef, " +
                    "       IFNULL(c.CustomerActivityId, 0) AS CustActRef, IFNULL(c.CustomerLevelId, 0) AS CustLevelRef, " +
                    "       c.AreaId, c.StateId, c.ZoneId" +
                    " FROM " + DiscountCustomerDBAdapter.DATABASE_TABLE + " c " +
                    " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " o  ON c.CustomerId = o.CustRef" +
                    " WHERE o.EvcId = '" + evcId + "'";
        db.execSQL(query);
    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
