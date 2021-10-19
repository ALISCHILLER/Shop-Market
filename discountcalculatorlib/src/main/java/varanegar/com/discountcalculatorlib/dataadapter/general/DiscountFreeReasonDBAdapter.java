package varanegar.com.discountcalculatorlib.dataadapter.general;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.entity.general.DiscountFreeReason;

public class DiscountFreeReasonDBAdapter extends DiscountBaseDataAdapter {


    public static final String KEY_FREE_REASON_ID = "Id";
    public static final String KEY_FREE_REASON_NAME = "FreeReasonName";
    public static final String KEY_FREE_REASON_CALC_PRICE_TYPE = "CalcPriceType";
    public static final String KEY_FREE_REASON_DISACCREF = "DisAccType";



    public static final String DATABASE_TABLE = "DiscountFreeReason";
    private static String TAG = "DiscountFreeReasonDBAdapter";
    private static DiscountFreeReasonDBAdapter instance;

    private DiscountFreeReasonDBAdapter()
    {
    }

    public static DiscountFreeReasonDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DiscountFreeReasonDBAdapter();
        }

        return instance;

    }

    public ArrayList<DiscountFreeReason> getFreeReason()
    {
        ArrayList<DiscountFreeReason> reasonList = new ArrayList<>();
        Cursor c = null;

        try {
            c = db.query(DATABASE_TABLE, new String[]{KEY_FREE_REASON_ID, KEY_FREE_REASON_NAME, KEY_FREE_REASON_CALC_PRICE_TYPE}, null, null, null, null, null);

            if (c.moveToFirst()) {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    DiscountFreeReason reason = new DiscountFreeReason();
                    reason.freeReasonId = c.getInt(c.getColumnIndex(KEY_FREE_REASON_ID));
                    reason.freeReasonName = c.getString(c.getColumnIndex(KEY_FREE_REASON_NAME));
                    reason.calcPriceType= c.getInt(c.getColumnIndex(KEY_FREE_REASON_CALC_PRICE_TYPE));

                    reasonList.add(reason);
                }
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {

            if(c != null) c.close();
//            db.close();
            return reasonList;
        }
    }


    public DiscountFreeReason getFreeReasonById(int id)
    {
        DiscountFreeReason reason = null;
        Cursor c = null;

        try {
            c = db.query(DATABASE_TABLE, new String[]{KEY_FREE_REASON_ID, KEY_FREE_REASON_NAME, KEY_FREE_REASON_CALC_PRICE_TYPE}, KEY_FREE_REASON_ID + "=" + id
                    , null, null, null, null);
            if (c.moveToFirst()) {
                reason = new DiscountFreeReason();
                reason.freeReasonId = c.getInt(c.getColumnIndex(KEY_FREE_REASON_ID));
                reason.freeReasonName = c.getString(c.getColumnIndex(KEY_FREE_REASON_NAME));
                reason.calcPriceType= c.getInt(c.getColumnIndex(KEY_FREE_REASON_CALC_PRICE_TYPE));
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {

            if(c != null) c.close();
//            db.close();
            return reason;
        }
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }



}
