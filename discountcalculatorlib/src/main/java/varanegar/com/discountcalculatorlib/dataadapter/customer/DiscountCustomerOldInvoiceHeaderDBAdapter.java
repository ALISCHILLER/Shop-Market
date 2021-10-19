package varanegar.com.discountcalculatorlib.dataadapter.customer;

import android.database.Cursor;

import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.entity.customer.DiscountCustomerOldInvoiceHeader;

/**
 * Created by m.aghajani on 11/11/2015.
 */
public class DiscountCustomerOldInvoiceHeaderDBAdapter extends DiscountBaseDataAdapter {

    public static final String KEY_SALEID = "SaleId";
    public static final String KEY_SALENO = "SaleNo";
    public static final String KEY_SALEVOCHERNO = "SaleVocherNo";
    public static final String KEY_SALEDATE = "SaleDate";
    public static final String KEY_STOCKID = "StockId"; //StockDCRef
    public static final String KEY_CUSTOMERID = "CustomerId";
    public static final String KEY_DEALERID = "DealerId";
    public static final String KEY_DISTRIBUTER = "DistributerName";
    public static final String KEY_GOODS_GROUP_TREE_XML = "GoodsGroupTreeXML";
    public static final String KEY_GOODS_DETAIL_XML = "GoodsDetailXml";
    public static final String KEY_GOODS_MAIN_SUB_TYPE_XML = "GoodsMainSubTypeDetailXML";
    public static final String KEY_CUST_CATEGORY_ID = "CustCtgrRef";
    public static final String KEY_CUST_ACT_ID = "CustActRef";
    public static final String KEY_CUST_LEVEL_ID = "CustLevelRef";

    public static final String KEY_ORDER_ID = "OrderId";
    public static final String KEY_ORDER_NO = "OrderNo";
    public static final String KEY_DIS1 = "Dis1";
    public static final String KEY_DIS2 = "Dis2";
    public static final String KEY_DIS3 = "Dis3";
    public static final String KEY_ADD1 = "Add1";
    public static final String KEY_ADD2 = "Add2";
    public static final String KEY_ORDER_TYPE = "OrderType";
    public static final String KEY_BUY_TYPE_ID = "BuyTypeId";
    public static final String KEY_DC_REF = "DCRef";
    public static final String KEY_DIS_TYPE = "DisType";
    public static final String KEY_ACC_YEAR = "AccYear";
    public static final String KEY_DC_SALE_OFFICE_REF = "DCSaleOfficeRef";
    public static final String KEY_CHARGE = "Charge";
    public static final String KEY_TAX = "Tax";
    public static final String KEY_PAYMENT_USANCE_REF = "paymentUsanceRef";
    public final String KEY_STOCKDC_CODE = "StockDCCode";
    public final String KEY_DEALER_CODE = "DealerCode";
    public final String KEY_SUPERVISOR_CODE = "SupervisorCode";



    public static final String DATABASE_TABLE = "DiscountCustomerOldInvoiceHeader";
    private static String TAG = "DiscountCustomerDBAdapter";
    private static DiscountCustomerOldInvoiceHeaderDBAdapter instance;

    public static DiscountCustomerOldInvoiceHeaderDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountCustomerOldInvoiceHeaderDBAdapter();
        }

        return instance;

    }

    private Cursor getAllDiscountCustomerOldInvoiceHeaderCursor(int customerId)
    {
        Cursor c = null;
        if(db != null)
        {
            try
            {
                c = db.query(DATABASE_TABLE, new String[] {KEY_SALEID, KEY_SALENO, KEY_SALEVOCHERNO, KEY_SALEDATE,
                        KEY_STOCKID, KEY_CUSTOMERID, KEY_DEALERID, KEY_DISTRIBUTER}, "CustomerId='" +customerId + "'", null, null, null, KEY_SALEID + " asc" );
            }
            catch(Exception ex)
            {
            }
        }

        return c;
    }

    public DiscountCustomerOldInvoiceHeader[] getAllCustomerOldInvoiceHeaderList(int CustomerId)
    {
        ArrayList<DiscountCustomerOldInvoiceHeader> result = new ArrayList<>();
        DiscountCustomerOldInvoiceHeader[] finalValue = null;
        Cursor c = null;

        try {
            c = getAllDiscountCustomerOldInvoiceHeaderCursor(CustomerId);

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        DiscountCustomerOldInvoiceHeader inv;
                            inv = new DiscountCustomerOldInvoiceHeader(c.getInt(c.getColumnIndex(KEY_CUSTOMERID)),
                                    c.getInt(c.getColumnIndex(KEY_DEALERID)), c.getInt(c.getColumnIndex(KEY_STOCKID)),
                                    c.getInt(c.getColumnIndex(KEY_SALEID)), c.getInt(c.getColumnIndex(KEY_SALENO)),
                                    c.getString(c.getColumnIndex(KEY_SALEDATE)), c.getInt(c.getColumnIndex(KEY_SALEVOCHERNO)),
                                    c.getString(c.getColumnIndex(KEY_DISTRIBUTER)));
                            result.add(inv);
                    } while (c.moveToNext());
                }
                finalValue = new DiscountCustomerOldInvoiceHeader[result.size()];
                finalValue = result.toArray(finalValue);
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {

            if(c != null) c.close();;
//            db.close();
            return finalValue;
        }
    }



    public DiscountCustomerOldInvoiceHeader getCustomerOldInvoiceHeader(int SaleId)
    {
        DiscountCustomerOldInvoiceHeader invoice = null;
        Cursor c = null;

        try {
            if (db != null) {
                c = db.query(DATABASE_TABLE, new String[]{KEY_SALEID, KEY_SALENO, KEY_SALEVOCHERNO, KEY_SALEDATE,
                        KEY_STOCKID, KEY_CUSTOMERID, KEY_DEALERID, KEY_DISTRIBUTER}, "SaleId = '" + SaleId + "'", null, null, null, null);
                if (c != null) {
                    if (c.moveToFirst()) {
                            invoice = new DiscountCustomerOldInvoiceHeader(c.getInt(c.getColumnIndex(KEY_CUSTOMERID)),
                                    c.getInt(c.getColumnIndex(KEY_DEALERID)), c.getInt(c.getColumnIndex(KEY_STOCKID)),
                                    c.getInt(c.getColumnIndex(KEY_SALEID)), c.getInt(c.getColumnIndex(KEY_SALENO)),
                                    c.getString(c.getColumnIndex(KEY_SALEDATE)), c.getInt(c.getColumnIndex(KEY_SALEVOCHERNO)),
                                    c.getString(c.getColumnIndex(KEY_DISTRIBUTER)));
                    }
                }
            }

            return invoice;
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {

            if(c != null) c.close();
//            db.close();
            return invoice;
        }
    }

    public String getReferenceXMLs(int saleId)
    {
        String result = "";
        String query = " SELECT " + KEY_GOODS_GROUP_TREE_XML + "," + KEY_GOODS_DETAIL_XML + "," + KEY_GOODS_MAIN_SUB_TYPE_XML
                + " FROM " + DATABASE_TABLE + " WHERE " + KEY_SALEID + " = " + saleId;
        Cursor c = null;


        try
        {
            if(db != null)
            {
                c = db.rawQuery(query, null);

                if (c != null && c.moveToFirst())
                    result = c.getString(0) + "," + c.getString(1) + "," + c.getString(2) ;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {

            if(c != null) c.close();
//            db.close();
            return result;
        }
    }


    public int getSaleIdFromOrderNo(int orderNo)
    {
        Cursor c = null;
        if(db != null)
        {
            try
            {
                String query = "SELECT SaleId FROM " + DATABASE_TABLE + " AS ret WHERE ret.SaleNo = " + orderNo ;
                c = db.rawQuery(query , null);
                if (c != null && c.moveToFirst())
                    return c.getInt(0);
            }
            catch(Exception ex)
            {
                return 0;
            }
        }

        return 0;
    }
    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }

}
