package varanegar.com.discountcalculatorlib.dataadapter.customer;

import android.database.Cursor;

import java.util.List;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.utility.GlobalFunctions;

public class DiscountCustomerSellPayTypeDBAdapter extends DiscountBaseDataAdapter {

    public static final String KEY_SellPayType_ROW_ID = "_id";
    public static final String KEY_SellPayType_CUSTOMER_ID = "CustomerId";
    public static final String KEY_SellPayType_PAYTYPE_ID = "SellPayTypeId"; // مهلت پرداخت
    public static final String KEY_SellPayType_PAYTYPE_NAME = "SellPayTypeName";
    public static final String KEY_SellPayType_BUYTYPE_ID = "BuyTypeId"; //نوع پرداخت
    public static final String KEY_SellPayType_CHECK_DEBIT = "CheckDebit";//کنترل اعتبار بدهکاری
    public static final String KEY_SellPayType_CHECK_CREDIT = "CheckCredit";//کنترل اعتبار بستانکاری
    public static final String KEY_SellPayType_PAYMENT_DeadLine = "PaymentDeadLine";//مهلت
    public static final String KEY_SellPayType_PAYMENT_TIME = "PaymentTime";//مدت
    public static final String KEY_SellPayType_DMC_RECEIPT = "DmcReceipt";//مدت


    public static final String DATABASE_TABLE = "DiscountSellPayType";
    private static String TAG = "DiscountCustomerSellPayTypeDBAdapter";
    private static DiscountCustomerSellPayTypeDBAdapter instance;


    private DiscountCustomerSellPayTypeDBAdapter() {
    }

    public static DiscountCustomerSellPayTypeDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountCustomerSellPayTypeDBAdapter();
        }

        return instance;

    }

    private Cursor getCustomerBuyTypeContacts(int customerId) {
        Cursor c = null;
        if (db != null) {
            c = db.query(DATABASE_TABLE
                    , new String[]{KEY_SellPayType_CUSTOMER_ID, KEY_SellPayType_BUYTYPE_ID, KEY_SellPayType_PAYTYPE_ID, KEY_SellPayType_PAYTYPE_NAME, KEY_SellPayType_CHECK_DEBIT
                    , KEY_SellPayType_CHECK_CREDIT, KEY_SellPayType_PAYMENT_DeadLine, KEY_SellPayType_PAYMENT_TIME, KEY_SellPayType_DMC_RECEIPT}
                    , KEY_SellPayType_CUSTOMER_ID + "='" + customerId + "'", null, null, null, null);
        }

        return c;
    }

    private Cursor getCustomerSellPayTypeBuyIdList(List<Integer> idList) {

        Cursor c = null;

        if (db != null) {
            c = db.query(DATABASE_TABLE
                    , new String[]{KEY_SellPayType_CUSTOMER_ID, KEY_SellPayType_BUYTYPE_ID, KEY_SellPayType_PAYTYPE_ID, KEY_SellPayType_PAYTYPE_NAME, KEY_SellPayType_CHECK_DEBIT
                    , KEY_SellPayType_CHECK_CREDIT, KEY_SellPayType_PAYMENT_DeadLine, KEY_SellPayType_PAYMENT_TIME, KEY_SellPayType_DMC_RECEIPT}
                    , KEY_SellPayType_PAYTYPE_ID + " IN(" + GlobalFunctions.CreateStringFromList(idList, ',') + ")", null, null, null, null);
        }

        return c;
    }


    public int getCustomerBuyTypeId(long paymentUsanceId)
    {
        int result = 0;
        Cursor cursor = null;

        try {
            cursor = db.query(DATABASE_TABLE, new String[]{KEY_SellPayType_BUYTYPE_ID}, KEY_SellPayType_PAYTYPE_ID + " = '" + paymentUsanceId + "'", null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex(KEY_SellPayType_BUYTYPE_ID));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
//            db.close();
            return result;
        }
    }

    public int isReceiptPayment(long paymentUsanceId)
    {
        int result = 0;
        Cursor cursor = null;

        try {
            String strQuery = "select " + KEY_SellPayType_DMC_RECEIPT + " from " + DATABASE_TABLE + " where " + KEY_SellPayType_PAYTYPE_ID + "='" +  paymentUsanceId + "'";
            cursor = db.rawQuery(strQuery, null); //db.query(DATABASE_TABLE, new String[]{KEY_SellPayType_DMC_RECEIPT}, KEY_SellPayType_PAYTYPE_ID + " = '" + paymentUsanceId + "'", null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
//            db.close();
            return result;
        }
    }

    public int getMaxPaymentTime(List<Integer> paymentIdList) {

        int result = 0;
        Cursor cursor = null;

        try {

            String strQuery = "SELECT MAX(" + KEY_SellPayType_PAYMENT_TIME + ") FROM " + DATABASE_TABLE;
            strQuery += " WHERE " + KEY_SellPayType_PAYTYPE_ID + " IN(" + GlobalFunctions.CreateStringFromList(paymentIdList, ',') + ")";

            cursor = db.rawQuery(strQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {

            if(cursor != null) cursor.close();
//            db.close();
            return result;
        }
    }
    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }

}
