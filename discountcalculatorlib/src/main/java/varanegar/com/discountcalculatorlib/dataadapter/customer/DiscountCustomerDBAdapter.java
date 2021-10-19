package varanegar.com.discountcalculatorlib.dataadapter.customer;

import android.database.Cursor;
import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.vdmclient.call.Customer;

public class DiscountCustomerDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_CUST_CUSTOMERCODE = "CustomerCode";
    public final String KEY_CUST_CUSTOMERNAME = "CustomerName";
    public final String KEY_CUST_CUSTOMERID = "CustomerId";
    public final String KEY_CUST_UNIQAUE_ID = "UniqueId";
    public final String KEY_CUST_PHONE = "Phone";
    public final String KEY_CUST_MOBILE = "Mobile";
    public final String KEY_CUST_ADDRESS = "Address";
    public final String KEY_CUST_STORENAME = "StoreName";
    public final String KEY_CUST_LAT = "Latitude";
    public final String KEY_CUST_LNG = "Longitude";
    public final String KEY_CUST_ALARM = "Alarm";
    public final String KEY_CUST_OPENCHEQUE_COUNT = "OpenChequeCount";
    public final String KEY_CUST_OPENCHEQUE_AMOUNT = "OpenChequeAmount";
    public final String KEY_CUST_RETURNCHEQUE_COUNT = "ReturnChequeCount";
    public final String KEY_CUST_RETURNCHEQUE_AMOUNT = "ReturnChequeAmount";
    public final String KEY_CUST_OPENINVOICE_COUNT = "OpenInvoiceCount";
    public final String KEY_CUST_OPENINVOICE_AMOUNT = "OpenInvoiceAmount";
    public final String KEY_CUST_REMAINDEBIT = "RemainDebit";
    public final String KEY_CUST_REMAINCREDIT = "RemainCredit";
    public final String KEY_CUST_CUSTOMERREMAIN = "CustomerRemain";
    public final String KEY_CUST_CUSTOMERCHEQUERETQTY = "CustChequeRetQty";
    public final String KEY_CUST_ECONOMICCODE = "EconomicCode";
    public final String KEY_CUST_NATIONALCODE = "NationalCode";
    public final String KEY_CUST_INITDEBIT = "InitDebit";
    public final String KEY_CUST_INITCREDIT = "InitCredit";
    public final String KEY_CUST_TOTALORDER = "totalorder";
    public final String KEY_CUST_VISITSTATUS = "visitstatus";
    public final String KEY_CUST_VISITSTATUS_REASON = "visitstatusreason";
    public final String KEY_CUST_VISITORID = "salesmanid";
    public final String KEY_CUST_PASAJID = "pasajid";
    public final String KEY_CUST_VISITLAT = "visitlat";
    public final String KEY_CUST_VISITLNG = "visitlng";
    public final String KEY_CUST_GPSSAVE = "gpssave";
    public final String KEY_CUST_NO_SALE_REASON = "nosalereasonid";
    public final String KEY_CUST_ERROR_TYPE = "ErrorType";
    public final String KEY_CUST_ERROR_MESSAGE = "ErrorMessage";
    public final String KEY_CUST_NO_SEND = "NoSend";
    public final String KEY_CUST_SEND_DATA_STATUS = "SendDataStatus";
    public final String KEY_CUST_ISINFO_EDIT = "IsInfoEdit";
    public final String KEY_CUST_ISINFO_SEND = "IsInfoSend";
    public final String KEY_CUST_SORTID = "SortId";
    public final String KEY_CUST_CITYID = "CityId";
    public final String KEY_CUST_STATEID = "StateId";
    public final String KEY_CUST_LEVEL_ID = "CustomerLevelId";
    public final String KEY_CUST_ACTIVITY_ID = "CustomerActivityId";
    public final String KEY_CUST_CATEGORY_ID = "CustomerCategoryId";
    public final String KEY_CUST_CENTER_ID = "CenterId";
    public final String KEY_CUST_ZONE_ID = "ZoneId";
    public final String KEY_CUST_AREA_ID = "AreaId";
    public final String KEY_CUST_STATE_ID = "StateId";
    public final String KEY_CUST_HAS_CANCEL_ORDER = "HasCancelOrder";
    public final String KEY_CUST_IS_PRINTED = "IsPrinted";
    public final String KEY_CUST_ACTION_GUID = "ActionGUID";
    public final String KEY_CUST_ACTION_TYPE = "ActionType";
    public final String KEY_CUSTGROUP_ID = "CustGroupRef";


    public static final String DATABASE_TABLE = "DiscountCustomer";
    private static String TAG = "DiscountCustomerDBAdapter";
    private static DiscountCustomerDBAdapter instance;

    public static DiscountCustomerDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountCustomerDBAdapter();
        }

        return instance;

    }
    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }
    public Customer getById(int customerId ){
        String sql =
                " select   SalePathName \n" +
                "         ,SaleAreaName \n" +
                "         ,SaleZoneName \n" +
                "         ,DistPathName \n" +
                "         ,DistAreaName \n" +
                "         ,DistZoneName \n" +
                "         ,CityName \n" +
                "         ,CountyName \n" +
                "         ,StateName \n" +
                "         ,CustomerName CustName \n" +
                "         ,ExtraField1 \n" +
                "         ,ExtraField2 \n" +
                "         ,ExtraField3 \n" +
                "         ,OwnerTypeName \n" +
                "         ,ExtraField4 \n" +
                "        , ExtraField6 \n" +
                "        , ExtraField7 \n" +
                "        , ExtraField8 \n" +
                "        , ExtraField9 \n" +
                "        , ExtraField10 \n" +
                "        , ExtraField11 \n" +
                "        , ExtraField12 \n" +
                "         ,ExtraField13 \n" +
                "         ,ExtraField14 \n" +
                "         ,ExtraField15 \n" +
                "         ,ExtraField16 \n" +
                "         ,ExtraField17 \n" +
                "         ,ExtraField18 \n" +
                "         ,ExtraField5 \n" +
                "         ,CustLevelName \n" +
                "         ,CustActName \n" +
                "         ,CustCtgrName \n" +
                "         ,DcRef \n" +
                "         ,CustomerId CustRef \n" +
                "/*      \n" +
                "   ,CustCtgrRef \n" +
                "         ,CustActRef \n" +
                "         ,CustLevelRef \n" +
                "         ,AreaRef \n" +
                "         ,SalePathRef \n" +
                "         ,SaleZoneRef \n" +
                "         ,CustomerRemAmount \n" +
                "         ,CustChequeRetQty \n" +
                "         ,CountyRef \n" +
                "         ,StateRef \n" +
                "         ,CustGroupRef \n" +
                "         ,CustomerCode \n" +
                "         ,CustomerCategoryCode \n" +
                "         ,CustomerActivityCode \n" +
                "         ,CustomerLevelCode \n" +
                "         ,CityZone \n" +
                "         ,CityArea \n" +
                "         ,OwnerTypeCode \n" +
                "         ,SalePathNo \n" +
                "         ,SaleAreaNo \n" +
                "         ,SaleZoneNo \n" +
                "         ,DistPathNo \n" +
                "         ,DistAreaNo \n" +
                "         ,DistZoneNo \n" +
                "         ,CityCode \n" +
                "         ,CountyCode \n" +
                "         ,StateCode \n" +
                "         ,ExtraField19 \n" +
                "         ,ExtraField20  */\n" +
                " from " + DATABASE_TABLE + " where customerId = " + customerId ;

        Cursor c = db.rawQuery(sql, null);
        Customer customer = new Customer();

        if (c != null && c.moveToFirst()) {
            customer.CustRef = c.getInt(c.getColumnIndex("CustRef"));
            customer.CountyName = c.getString(c.getColumnIndex("CountyName"));
            customer.CustName = c.getString(c.getColumnIndex("CustName"));
        }
        return customer;
    }

    private Cursor getAllContacts() {

        if (db != null) {
            return db.query(DATABASE_TABLE, new String[]{KEY_CUST_CUSTOMERCODE, KEY_CUST_CUSTOMERNAME,
                    KEY_CUST_CUSTOMERID, KEY_CUST_PHONE,
                    KEY_CUST_MOBILE, KEY_CUST_ADDRESS, KEY_CUST_LAT, KEY_CUST_LNG,
                    KEY_CUST_STORENAME, KEY_CUST_ALARM, KEY_CUST_OPENCHEQUE_COUNT, KEY_CUST_OPENCHEQUE_AMOUNT,
                    KEY_CUST_RETURNCHEQUE_COUNT, KEY_CUST_RETURNCHEQUE_AMOUNT,
                    KEY_CUST_OPENINVOICE_COUNT, KEY_CUST_OPENINVOICE_AMOUNT, KEY_CUST_REMAINDEBIT, KEY_CUST_REMAINCREDIT,
                    KEY_CUST_TOTALORDER, KEY_CUST_VISITSTATUS, KEY_CUST_VISITSTATUS_REASON,
                    KEY_CUST_PASAJID, KEY_CUST_VISITLAT, KEY_CUST_VISITLNG, KEY_CUST_GPSSAVE,
                    KEY_CUST_NO_SALE_REASON, KEY_CUST_ERROR_TYPE, KEY_CUST_ERROR_MESSAGE, KEY_CUST_NO_SEND,
                    KEY_CUST_SEND_DATA_STATUS, KEY_CUST_ISINFO_EDIT, KEY_CUST_ISINFO_SEND, KEY_CUST_SORTID,
                    KEY_CUST_CITYID, KEY_CUST_STATEID, KEY_CUST_ECONOMICCODE, KEY_CUST_NATIONALCODE,
                    KEY_CUST_CUSTOMERREMAIN, KEY_CUST_INITDEBIT, KEY_CUST_INITCREDIT,
                    KEY_CUST_LEVEL_ID, KEY_CUST_ACTIVITY_ID, KEY_CUST_CATEGORY_ID, KEY_CUST_CENTER_ID, KEY_CUST_ZONE_ID,
                    KEY_CUST_AREA_ID, KEY_CUST_STATE_ID, KEY_CUST_HAS_CANCEL_ORDER, KEY_CUST_IS_PRINTED, KEY_CUST_ACTION_GUID,
                    KEY_CUST_ACTION_TYPE, KEY_CUST_UNIQAUE_ID
            }, null, null, null, null, KEY_CUST_CUSTOMERNAME + " ASC ");
        } else return null;
    }

}
