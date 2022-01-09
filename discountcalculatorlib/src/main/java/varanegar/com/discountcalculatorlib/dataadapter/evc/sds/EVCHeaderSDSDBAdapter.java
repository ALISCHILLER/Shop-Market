package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;


import android.content.ContentValues;
import android.database.Cursor;

import java.math.BigDecimal;
import java.util.Locale;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerSellPayTypeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19.DisAccDBAdapter;
import varanegar.com.discountcalculatorlib.handler.sds.PromotionGetRetExtraValueV3;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.general.DiscountFreeReasonDBAdapter;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;

public class EVCHeaderSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_ROWID = "_id";
    public final String KEY_REF_ID = "RefId";
    public final String KEY_DIS_1 = "Dis1";
    public final String KEY_DIS_2 = "Dis2";
    public final String KEY_DIS_3 = "Dis3";
    public final String KEY_DIS_OTHER = "OtherDiscount";
    public final String KEY_ADD_1 = "Add1";
    public final String KEY_ADD_2 = "Add2";
    public final String KEY_ADD_OTHER = "OtherAddition";
    public final String KEY_ORDER_YPE = "OrderType";
    public final String KEY_PAY_TYPE = "PayType";
    public final String KEY_DC_REF = "DCRef";
    public final String KEY_DIS_TYPE = "DisType";
    public final String KEY_CUST_REF = "CustRef";
    public final String KEY_EVC_SALE_OFFICE_ID = "SaleOfficeRef";
    public final String KEY_CALL_ID = "CallId";
    public final String KEY_EVC_ID = "EvcId";
    public final String KEY_DC_SALES_OFFICE_REF = "DCSaleOfficeRef";
    public final String KEY_TAX = "Tax";
    public final String KEY_CHARGE = "Charge";
    public final String KEY_AMOUNT = "Amount";
    public final String KEY_NET_AMOUNT = "NetAmount";
    public final String KEY_EVC_TYPE = "EVCType";
    public final String KEY_DC_CODE = "DCCode";
    public final String KEY_EVC_DATE = "EvcDate";
    public final String KEY_ACC_YEAR = "AccYear";
    public final String KEY_STOCKDC_REF = "StockDCRef";
    public final String KEY_STOCKDC_CODE = "StockDCCode";
    public final String KEY_DEALER_REF = "DealerRef";
    public final String KEY_DEALER_CODE = "DealerCode";
    public final String KEY_SUPERVISOR_REF = "SupervisorRef";
    public final String KEY_SUPERVISOR_CODE = "SupervisorCode";
    public final String KEY_PAYMENT_USANCE_REF = "PaymentUsanceRef";

    public static final String DATABASE_TABLE = "EVCHeaderSDS";
    private static String TAG = "EVCHeaderSDSDBAdapter";
    private static EVCHeaderSDSDBAdapter instance;

    private EVCHeaderSDSDBAdapter() {
    }

    public static EVCHeaderSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCHeaderSDSDBAdapter();
        }

        return instance;

    }

    public long saveEVCHeader(DiscountCallOrderData call, String evcId, int evcTypeId, int disTypeId)
    {

        return saveEVCHeader(call.totalOrderDis1.doubleValue(), call.totalOrderDis2.doubleValue(), call.totalOrderDis3.doubleValue(), call.totalOrderAdd1.doubleValue()
                , call.totalOrderAdd2.doubleValue(), call.OrderType, call.invoicePaymentTypeId, evcTypeId, call.customerId, call.callUniqueId, evcId, -1
                , disTypeId, call.DCRef, call.SaleOfficeRef, call.dcCode, call.saleDate, call.accYear, call.stockDCRef, call.stockDCCode, call.dealerRef, call.dealerCode
                , call.supervisorRef, call.supervisorCode, call.supervisorName, call.dealerName, call.stockDCName, call.totalInvoiceDisOther.doubleValue(), call.totalInvoiceAddOther.doubleValue());
    }

    public String getOldInvoiceReturnDate(int returnRef)
    {
        String date = null;
        String query = " SELECT SaleDate \n"
                + " FROM " + DiscountCustomerOldInvoiceHeaderDBAdapter.DATABASE_TABLE
                + " WHERE SaleId = " + returnRef;

        if(db != null) {

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                date = c.getString(0);
            }
        }

        return date;
    }

    public void saveEVCHeader(DiscountCallReturnData call, String evcId1, String evcId2, int evcTypeId) {

       String sql = "INSERT INTO " + DATABASE_TABLE
               + " (EvcId, CallId, RefID, Dis1, Dis2, Dis3, Add1, Add2, OrderType, PayType, StockDCRef, DCRef, DisType, CustRef, AccYear, DCSaleOfficeRef, SaleOfficeRef \n"
               + "  , EVCType, EvcDate, DealerRef, Charge, Tax, PaymentUsanceRef, StockDCCode, DealerCode, SupervisorCode) \n"

               + " SELECT '%S' , '%S' , " + call.returnRefId + ", Dis1, Dis2, Dis3, Add1, Add2, OrderType, BuyTypeId, StockId, DCRef, DisType, CustomerId, AccYear, DCSaleOfficeRef, SaleOfficeRef \n"
               + "        ," + evcTypeId + ", SaleDate, DealerId, Charge, Tax, paymentUsanceRef, StockDCCode, DealerCode, SupervisorCode \n"
               + " FROM " + DiscountCustomerOldInvoiceHeaderDBAdapter.DATABASE_TABLE
               + " WHERE SaleId = " + call.returnRefId;

        //Main
        db.execSQL(String.format(Locale.ENGLISH,sql, evcId1, call.callUniqueId));

        //Clone
        db.execSQL(String.format(Locale.ENGLISH, sql, evcId2, call.callUniqueId));
    }



    private long saveEVCHeader(double dis1, double dis2, double dis3, double add1, double add2, int orderType, String paymentTypeId, int evcTypeId, int customerId
            , String callUniqueId, String evcId, int refId, int disTypeId, int DCRef, int SaleOfficeRef, String dcCode, String date, int accYear, int stockDcRef, String stockDcCode
            , int dealerId, String dealerCode, int supervisorId, String supervisorCode
            , String supervisorName, String dealerName, String stockDCName, double disOther, double addOther) {

        ContentValues values = new ContentValues();
        values.put(KEY_DIS_1, dis1);
        values.put(KEY_DIS_2, dis2);
        values.put(KEY_DIS_3, dis3);
        values.put(KEY_DIS_OTHER, disOther);
        values.put(KEY_ADD_1, add1);
        values.put(KEY_ADD_2, add2);
        values.put(KEY_ADD_OTHER, addOther);
        values.put(KEY_ORDER_YPE, orderType);
        //todo this line has bug
        values.put(KEY_PAY_TYPE, DiscountCustomerSellPayTypeDBAdapter.getInstance().getCustomerBuyTypeId(Integer.parseInt(paymentTypeId)));
        values.put(KEY_CUST_REF, customerId);
        values.put(KEY_CALL_ID, callUniqueId);
        values.put(KEY_EVC_ID, evcId);
        values.put(KEY_REF_ID, refId);
        values.put(KEY_EVC_TYPE, evcTypeId);
        values.put(KEY_DIS_TYPE, disTypeId);
        values.put(KEY_DC_REF, DCRef);
        values.put(KEY_EVC_SALE_OFFICE_ID, SaleOfficeRef);

        values.put(KEY_DC_CODE, dcCode);
        values.put(KEY_EVC_DATE, date);
        values.put(KEY_ACC_YEAR, accYear);
        values.put(KEY_STOCKDC_REF, stockDcRef);
        values.put(KEY_STOCKDC_CODE, stockDcCode);
        values.put(KEY_DEALER_REF, dealerId);
        values.put(KEY_DEALER_CODE, dealerCode);
        values.put(KEY_SUPERVISOR_REF, supervisorId);
        values.put(KEY_SUPERVISOR_CODE, supervisorCode);
        values.put(KEY_PAYMENT_USANCE_REF, paymentTypeId);

        values.put("SupervisorName", supervisorName);
        values.put("DealerName", dealerName);
        values.put("StockDCName", stockDCName);

        long id = db.insertOrThrow(DATABASE_TABLE, null, values);

        return id;
    }


    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public Cursor getCalcPriority(String evcId) {
        Cursor c = null;
        if (db != null) {
            String sql = " SELECT DISTINCT D.CalcPriority , D.PrizeType, E.DisType, D.ID as DiscountId \n" +
                    " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI \n" +
                    " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E ON E.EvcID  = EI.EVCRef AND EI.FreeReasonId is Null \n" +
                    " INNER JOIN " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES  ON ES.EVCItemRef=EI._ID \n" +
                    " INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID = ES.DisRef \n" +
                    " WHERE EI.EVCRef = '" + evcId + "' AND E.DisType IN ('2', '4') AND D.PrizeType IN ('1','2','3','4', '5', '6') \n" +
                    " ORDER BY D.CalcPriority, CASE D.CalcMethod WHEN 1 then 1 else 2 end,D.DisGroup,D.Priority";
            c = db.rawQuery(sql, null);
        }
        return c;
    }

    /*usp_ApplyStatuteOnEVC*/
    public void applyStatuteOnEVC(String evcId) {

        String sql = "Update  " + DATABASE_TABLE +
                " Set \n Dis1 = IFNULL(Dis1,0) + (" + //" case when  " + (GlobalVariables.getPrizeCalcType() ? 1 : 0)+ " = 0 then IFNULL(Dis1,0) else 0 end + ( " +
                " Select IFNULL(SUM(ES.Discount),0) " +
                " from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                " Inner Join " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES On EI._ID = ES.EVCItemRef " +
                " Inner Join " + DiscountDBAdapter.DATABASE_TABLE + " D On D.ID = ES.DisRef " +
                " Where EI.EVCRef='" + evcId + "' AND D.DisAccRef=1 AND EI.FreeReasonId IS NULL " + //AND EI.PrizeType=0
                " ) + ("+
                " SELECT IFNULL(SUM(EI.Discount),0) "+
                " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI "+
                " INNER JOIN " + DiscountFreeReasonDBAdapter.DATABASE_TABLE +" F ON EI.FreeReasonId=F.FreeReasonId "+
                " Where EI.EVCRef='" + evcId + "' AND F.DisAccType=1 AND EI.FreeReasonId IS NOT NULL " + //
                //	--+CASE WHEN @PrizeCalcType=1 THEN (SELECT ISNULL(SUM(isnull(EvcItemDis1, 0)),0) FROM sle.tblEvcItem EI WHERE PrizeType=1 and EI.EVCRef=@ID) ELSE 0 END
                ") " ;
        if (GlobalVariables.getPrizeCalcType() )
            sql +="+ (SELECT IFNULL(SUM(IFNULL(EvcItemDis1, 0)),0) " +
                    " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + "  EI WHERE PrizeType=1 AND EI.EVCRef='" + evcId + "') ";
        sql +=
                "\n ,Dis2 = IFNULL(Dis2,0) + ( " + //case when  " + (GlobalVariables.getPrizeCalcType() ? 1 : 0) + " = 0 then IFNULL(Dis2,0) else 0 end + ( " +
                        " Select IFNULL(SUM(ES.Discount),0) " +
                        " from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                        " Inner Join " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES On EI._ID = ES.EVCItemRef " +
                        " Inner Join " + DiscountDBAdapter.DATABASE_TABLE + " D On D.ID = ES.DisRef " +
                        " Where EI.EVCRef='" + evcId + "' AND D.DisAccRef=2 AND EI.FreeReasonId IS NULL " + //AND EI.PrizeType=0
                        " ) " +
                        "+ ("+
                        " SELECT IFNULL(SUM(EI.Discount),0) "+
                        " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                        " INNER JOIN " + DiscountFreeReasonDBAdapter.DATABASE_TABLE +" F ON EI.FreeReasonId=F.FreeReasonId "+
                        " WHERE EI.EVCRef='" + evcId + "' AND F.DisAccType=2 AND EI.FreeReasonId IS NOT NULL  " + //AND EI.PrizeType=0
                        ") " ;
        if (GlobalVariables.getPrizeCalcType() )
            sql +="+ (SELECT IFNULL(SUM(IFNULL(EvcItemDis2, 0)),0) " +
                    " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + "  EI WHERE PrizeType=1 AND EI.EVCRef='" + evcId + "' ) ";
        sql +=
                "\n ,Dis3= IFNULL(Dis3,0) + ("+
                        " Select IFNULL(SUM(ES.Discount),0) " +
                        " from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                        " Inner Join " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES On EI._ID = ES.EVCItemRef " +
                        " Inner Join " + DiscountDBAdapter.DATABASE_TABLE + " D On D.ID = ES.DisRef " +
                        " Where EI.EVCRef='" + evcId + "' AND D.DisAccRef=3 AND EI.FreeReasonId IS NULL" +
                        " ) + ("+
                        " SELECT IFNULL(SUM(EI.Discount),0) "+
                        " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                        " INNER JOIN " + DiscountFreeReasonDBAdapter.DATABASE_TABLE +" F ON EI.FreeReasonId=F.FreeReasonId AND EI.PrizeType=0 "+
                        " WHERE EI.EVCRef='" + evcId + "' AND F.DisAccType=3 AND EI.FreeReasonId IS NOT NULL "+ //
                        ") " ;

        if (GlobalVariables.getPrizeCalcType() )
            sql +="+ (SELECT IFNULL(SUM(IFNULL(EvcItemDis3, 0)),0) " +
                    " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + "  EI WHERE PrizeType=1 AND EI.EVCRef='" + evcId + "') ";
        sql +=
            "\n ,Add1= IFNULL(Add1,0) + ( " +
                    " Select IFNULL(SUM(ES.AddAmount),0) " +
                    " from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                    " Inner Join " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES On EI._ID = ES.EVCItemRef " +
                    " Inner Join " + DiscountDBAdapter.DATABASE_TABLE + " D On D.ID = ES.DisRef " +
                    " Where EI.EVCRef='" + evcId + "' AND D.DisAccRef=4 " +
                    " ) " +
            "\n ,Add2= IFNULL(Add2,0) + ( " +
                    " Select IFNULL(SUM(ES.AddAmount),0) " +
                    " from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                    " Inner Join " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES On EI._ID = ES.EVCItemRef " +
                    " Inner Join " + DiscountDBAdapter.DATABASE_TABLE + " D On D.ID = ES.DisRef " +
                    " Where EI.EVCRef='" + evcId + "' AND D.DisAccRef=5 " +
                    " ) " +
            "\n ,Amount= ( " +
                    " Select IFNULL(SUM(EI.Amount),0) " +
                    " from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                    " Where EI.EVCRef='" + evcId + "'" +
                    " ) " +
            "\n ,NetAmount= ( " +
                    " Select IFNULL(SUM(EI.AmountNut),0) " +
                    " from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                    " Where EI.EVCRef='" + evcId + "'" +
                    " ) " +
            "\n ,OtherDiscount= IFNULL(OtherDiscount,0) + ( "+
                    " Select IFNULL(SUM(ES.Discount),0) "+
                    " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI "+
                    " INNER JOIN " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES On EI._ID = ES.EVCItemRef "+
                    " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " D On D.ID = ES.DisRef "+
                    " INNER JOIN " + DisAccDBAdapter.DATABASE_TABLE + " da on da.Id=d.DisAccRef and da.IsDiscount=1 and da.IsDefault=0 "+
                    " Where EI.EVCRef= '" + evcId + "'" +" AND EI.FreeReasonId IS NULL " +
                    " ) + ( " +
                    " SELECT IFNULL(SUM(EI.Discount),0) " +
                    " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                    " INNER JOIN " + DiscountFreeReasonDBAdapter.DATABASE_TABLE +" F ON EI.FreeReasonId=F.FreeReasonId                                                                           "+
                    " INNER JOIN " + DisAccDBAdapter.DATABASE_TABLE + " da on da.Id=F.DisAccType and da.IsDiscount=1 and da.IsDefault=0 " +
                    " Where EI.EVCRef= '" + evcId + "'" +" AND EI.FreeReasonId IS NOT NULL) ";

        if (GlobalVariables.getPrizeCalcType() )
            sql +="+ (SELECT IFNULL(SUM(IFNULL(EvcItemOtherDiscount, 0)),0) " +
                    " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + "  EI WHERE PrizeType=1 AND EI.EVCRef='" + evcId + "') ";

        sql +=
        "\n ,OtherAddition= IFNULL(OtherAddition,0) + ( "+
                " Select IFNULL(SUM(ES.AddAmount),0) "+
                " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES On EI._id = ES.EVCItemRef " +
                " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE  + " D On D.ID = ES.DisRef " +
                " INNER JOIN " + DisAccDBAdapter.DATABASE_TABLE + " da on da.Id=d.DisAccRef and da.IsDiscount=0 and da.IsDefault=0 "+
                " Where EI.EVCRef= '" + evcId + "')"+
        " Where EvcID= '" + evcId + "'";
        db.execSQL(sql);
    }

    public void updateChargeTax(String evcId) {
        Cursor c = getTaxChargeSum(evcId);
        if (c != null & c.moveToFirst()) {
            String sql = "update " + DATABASE_TABLE + " set tax = " + c.getInt(0) + ", charge =" + c.getInt(1) + " where EvcId='" + evcId + "'";
            db.execSQL(sql);
        }

    }

    public DiscountCallOrderData fillCallWithPromoForOnline(String evcId, DiscountCallOrderData discountCallOrderData) {
        Cursor cHeaderData = null;
        if (db != null) {

            cHeaderData = db.query(DATABASE_TABLE, new String[]{KEY_DIS_1, KEY_DIS_2, KEY_DIS_3, KEY_DIS_OTHER}, KEY_EVC_ID + "='" + evcId + "'", null, null, null, null);
        }

        try {

            if(cHeaderData != null && cHeaderData.moveToFirst()) {

                discountCallOrderData.totalInvoiceDis1 = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_1)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_1)));
                discountCallOrderData.totalInvoiceDis2 = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_2)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_2)));
                discountCallOrderData.totalInvoiceDis3 = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_3)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_3)));
                discountCallOrderData.totalInvoiceDisOther = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_OTHER)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_OTHER)));

                discountCallOrderData = EVCItemSDSDBAdapter.getInstance().fillCallLineWithPromo(evcId, discountCallOrderData);
                discountCallOrderData = EVCTempGoodsPackageItemSDSDBAdapter.getInstance().fillEVCPrizePackage(evcId, discountCallOrderData);
            }

        }catch (Exception ex)
        {
            throw ex;
        }
        finally {

            if(cHeaderData != null) cHeaderData.close();
            //TODO sometimes it has nullpointer exception in SQLiteDatabase -- we should handle this problem in proper way
            // db.close();
            return discountCallOrderData;
        }
    }


    public DiscountCallOrderData fillCallWithPromo(String evcId, DiscountCallOrderData discountCallOrderData) {
        Timber.d("calcPromotionSDS : fillCallWithPromo started ");
        Cursor cHeaderData = null;
        if (db != null) {

            cHeaderData = db.query(DATABASE_TABLE, new String[]{KEY_DIS_1, KEY_DIS_2, KEY_DIS_3, KEY_DIS_OTHER}, KEY_EVC_ID + "='" + evcId + "'", null, null, null, null);
        }

        try {

            if(cHeaderData != null && cHeaderData.moveToFirst()) {

                discountCallOrderData.totalInvoiceDis1 = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_1)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_1)));
                discountCallOrderData.totalInvoiceDis2 = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_2)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_2)));
                discountCallOrderData.totalInvoiceDis3 = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_3)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_3)));
                discountCallOrderData.totalInvoiceDisOther = cHeaderData.isNull(cHeaderData.getColumnIndex(KEY_DIS_OTHER)) ? BigDecimal.ZERO : new BigDecimal(cHeaderData.getDouble(cHeaderData.getColumnIndex(KEY_DIS_OTHER)));

                discountCallOrderData = EVCItemSDSDBAdapter.getInstance().fillCallLineWithPromo(evcId, discountCallOrderData);
                discountCallOrderData = EVCTempGoodsPackageItemSDSDBAdapter.getInstance().fillEVCPrizePackage(evcId, discountCallOrderData);

                EVCItemStatutesSDSDBAdapter.getInstance().updateOrderLineId();
            }

        }catch (Exception ex)
        {
            throw ex;
        }
        finally {

            if(cHeaderData != null) cHeaderData.close();
           //TODO sometimes it has nullpointer exception in SQLiteDatabase -- we should handle this problem in proper way
            // db.close();
            return discountCallOrderData;
        }
    }

    public String getEVCValuesForReturn(String evcId)
    {
        int evcType, disType;
        evcType = disType = 0;
        String query = "SELECT " + KEY_EVC_TYPE + ", " + KEY_DIS_TYPE + " FROM " + DATABASE_TABLE +" WHERE EvcId = '" + evcId + "'";

        if(db != null) {

            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                evcType = c.getInt(0);
                disType = c.getInt(1);
            }
        }

        return evcType + "," + disType;
    }

    public int getStatusForReturn(String evcId)
    {
        int status = 0;

        String query = "SELECT COUNT(*) FROM " + DATABASE_TABLE + " WHERE (IFNULL(Tax, 0) <> 0 OR IFNULL(Charge, 0) <> 0) AND EvcId = '" + evcId + "'";
        Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst())
        {
            if(c.getInt(0) > 0)
                status = 1;
        }


        return status;
    }

    public void updateEVCHeaderForSellReturn(String evcId, int evcType, double dis1, double dis2, double dis3, double add1, double add2)
    {
        String query = "UPDATE " + DATABASE_TABLE + " SET EVCType = " + evcType
                + ", Dis1 = " + dis1 + " , Dis2 = " + dis2 + " ,Dis3 = " + dis3
                + ", Add1 = " + add1 + " , Add2 = " + add2
                + "  WHERE EvcId = '" + evcId + "'";
        db.execSQL(query);
    }

    public void updateEVCType(String evcId, int evcType)
    {
        db.execSQL("UPDATE " + DATABASE_TABLE + " SET EVCType = " + evcType + " WHERE EvcId = '" + evcId + "'");
    }

    public int getDisType(String evcId)
    {
        int disType = 0;

        Cursor c = db.query(DATABASE_TABLE,new String[]{KEY_DIS_TYPE}, KEY_EVC_ID + " ='" + evcId  + "'", null, null, null, null, null);
        if(c != null && c.moveToFirst())
        {
            disType = c.getInt(c.getColumnIndex(KEY_DIS_TYPE));
        }

        return disType;
    }

    public int getEVCType(String evcId)
    {
        int evcType = 0;

        Cursor c = db.query(DATABASE_TABLE,new String[]{KEY_EVC_TYPE}, KEY_EVC_ID + " ='" + evcId  + "'", null, null, null, null, null);
        if(c != null && c.moveToFirst())
        {
            evcType = c.getInt(c.getColumnIndex(KEY_EVC_TYPE));
        }

        return evcType;
    }

    public PromotionGetRetExtraValueV3.EvcHeaderData getReturnEvcHeaderData(String evcId)
    {
        PromotionGetRetExtraValueV3.EvcHeaderData result = null;
        String query = "SELECT DisType, Dis1, Dis2, Dis3, Add1, Add2, RefId FROM " + DATABASE_TABLE + " WHERE EvcId = '" + evcId + "'";
        Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst())
        {
            int saleRef = c.getInt(c.getColumnIndex("RefId"));
            result = new PromotionGetRetExtraValueV3.EvcHeaderData(c.getInt(0), new BigDecimal(c.getDouble(1)), new BigDecimal(c.getDouble(2)), new BigDecimal(c.getDouble(3))
                    , new BigDecimal(c.getDouble(4)) , new BigDecimal(c.getDouble(5)), saleRef );
        }

        return result;
    }
    //region update with SpecialValue
    public void updateDis1WithSpecialValues(String evcId)
    {
        String query = "UPDATE " + DATABASE_TABLE +
                " SET Dis1 = (Select SUM(X) FROM (SELECT CAST((Amount*IFNULL(Dis1,0))/100 as integer) AS X FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE
                + " WHERE EVCRef=EvcId )Y ) \n "
                + " WHERE EvcId = '" + evcId + "'";

                db.execSQL(query);
    }

    public void updateDis2WithSpecialValues(String evcId)
    {
        String query = "UPDATE " + DATABASE_TABLE +
                " SET Dis2 = (Select SUM(X) FROM (SELECT CAST((Amount*IFNULL(Dis2,0))/100 as integer) AS X FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE
                + " WHERE EVCRef=EvcId )Y ) \n "
                + " WHERE EvcId = '" + evcId + "'";
        db.execSQL(query);
    }


    public void updateDis3WithSpecialValues(String evcId)
    {
        String query = "UPDATE " + DATABASE_TABLE +
                " SET Dis3 = (Select SUM(X) FROM (SELECT CAST((Amount*IFNULL(Dis3,0))/100 as integer) AS X FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE
                + " WHERE EVCRef=EvcId )Y ) \n "
                + " WHERE EvcId = '" + evcId + "'";
        db.execSQL(query);
    }

    public void updateAdd1WithSpecialValues(String evcId)
    {
        String query = "UPDATE " + DATABASE_TABLE + " SET Add1 = (Select SUM(X) FROM (SELECT CAST(((Amount - Discount)/*Amount*/*IFNULL(Add1,0))/100 as integer) AS X "
                + " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE
                + " WHERE EVCRef=EvcId )Y ) \n "
                + " WHERE EvcId = '" + evcId + "'";

                db.execSQL(query);
    }

    public void updateAdd2WithSpecialValues(String evcId)
    {
        String query = "UPDATE " + DATABASE_TABLE +
                " SET Add2 = (Select SUM(X) FROM (SELECT CAST(((Amount - Discount)/*Amount*/*IFNULL(Add2,0))/100 as integer) AS X "
                + " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE
                + " WHERE EVCRef=EvcId )Y ) \n "
                + " WHERE EvcId = '" + evcId + "'";
        db.execSQL(query);
    }
    //endregion update with SpecialValue

    public void updateAddDisWithSpecialValues(String evcId)
    {
        String query = "UPDATE " + DATABASE_TABLE +
        " SET Dis1 = ifnull((select sum(ifnull(EvcItemDis1, 0)) from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " where EvcRef=EvcId), 0) \n"+
        ", Dis2 = ifnull((select sum(ifnull(EvcItemDis2, 0)) from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " where EvcRef=EvcId), 0)  \n"+
        ", Dis3 = ifnull((select sum(ifnull(EvcItemDis3, 0)) from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " where EvcRef=EvcId), 0)  \n"+
        ", OtherDiscount = ifnull((select sum(ifnull(EvcItemOtherDiscount, 0)) from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " where EvcRef=EvcId), 0) \n"+
        ", Add1 = ifnull((select sum(ifnull(EvcItemAdd1, 0)) from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " where EvcRef=EvcId), 0)  \n"+
        ", Add2 = ifnull((select sum(ifnull(EvcItemAdd2, 0)) from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " where EvcRef=EvcId), 0)  \n"+
        ", OtherAddition = ifnull((select sum(ifnull(EvcItemOtherAddition, 0)) from EVCItemSDS where EvcRef=EvcId), 0) \n"+
        " WHERE EvcId = '" + evcId + "'";

        db.execSQL(query);
    }
    public void deleteEVCHeadersById(String evcId) {
        db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
    }

    public void deleteAllEVCHeader()
    {
        db.execSQL("delete from " + DATABASE_TABLE);
        //db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getTaxChargeSum(String evcId) {
        Cursor cursor = null;
        if (db != null) {
            String sql = "select sum(tax) as taxTotal, sum(charge) as chargeTotal from " + EVCItemSDSDBAdapter.DATABASE_TABLE + " where EvcRef='" + evcId + "'";
            cursor = db.rawQuery(sql, null);
        }
        return cursor;
    }

    public String CheckValidEvcStatutes(String evcId) {
        Cursor cursor = null;
        String sql =
                " SELECT D.Code,D.DisGroup,D.Priority "+
        " FROM " + DATABASE_TABLE +" E "+
        " JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI ON E.Id=EI.EVCRef AND E.Id='" + evcId +"'"+
        " JOIN " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " EIS ON EIS.EVCItemRef=EI.ID "+
        " JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE  + " D ON D.Id=EIS.DisRef "+
        " WHERE D.PrizeType IN(1,5) AND E.OrderType=1006 ";
        cursor = db.rawQuery(sql, null);
        if (cursor  != null & cursor .moveToFirst()) {
            String message = " امکان تخصيص جايزه براي حواله هاي اماني وجود ندارد"+
                    " کد قانون: " + cursor.getString(0) +
                    " گروه: " + cursor.getString(1) +
                    " اولويت: " + cursor.getInt(2);

            return message;
        }
        return "";

    }

    public void updateDisAdd(String evcId) {
        String query = " UPDATE " + DATABASE_TABLE +
        " SET   Dis1=IFNULL(Dis1,0)+ IFNULL((SELECT SUM(EvcItemDis1) FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE  + " WHERE PrizeType=1 AND EVCRef='"+ evcId + "'),0),"+
                "Dis2=IFNULL(Dis2,0)+ IFNULL((SELECT SUM(EvcItemDis2) FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE  + " WHERE PrizeType=1 AND EVCRef='"+ evcId + "'),0),"+
                "Dis3=IFNULL(Dis3,0)+ IFNULL((SELECT SUM(EvcItemDis3) FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE  + " WHERE PrizeType=1 AND EVCRef='"+ evcId + "'),0),"+
                "Add1=IFNULL(Add1,0)+ IFNULL((SELECT SUM(EvcItemAdd1) FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE  + " WHERE PrizeType=1 AND EVCRef='"+ evcId + "'),0),"+
                "Add2=IFNULL(Add2,0)+ IFNULL((SELECT SUM(EvcItemAdd2) FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE  + " WHERE PrizeType=1 AND EVCRef='"+ evcId + "'),0),"+
                "OtherDiscount=IFNULL(OtherDiscount,0)+ IFNULL((SELECT SUM(EvcItemOtherDiscount) FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE  + " WHERE PrizeType=1 AND EVCRef='"+ evcId + "'),0),"+
                "OtherAddition=IFNULL(OtherAddition,0)+ IFNULL((SELECT SUM(EvcItemOtherAddition) FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE  + " WHERE PrizeType=1 AND EVCRef='"+ evcId + "'),0)";
        db.execSQL(query);
    }

    public Cursor getEvcInfo(String evcId) {
        Cursor c = null;
        if (db != null) {
            String sql = " SELECT EVCType, CustRef,  DCRef, PayType, OrderType, RefId , EVCDate \n" +
                    " FROM " + DATABASE_TABLE + " \n" +
                    " WHERE EVCId = '" + evcId + "' ";
            c = db.rawQuery(sql, null);
        }
        return c;
    }

    public String getEvcId(int customerId)
    {
        String sql = " select evcId from " + DATABASE_TABLE + " where CustRef ='" + customerId + "'";
        Cursor  c = db.rawQuery(sql, null);
        if(c != null && c.moveToFirst())
        {
            return c.getString(0);
        }
        else
            return null;
    }
    public void UpdateToZero(String evcId) {
        String query = "Update  "+ DATABASE_TABLE +"\n" +
                "SET Dis1=0,Dis2=0,Dis3=0,Add1=0,Add2=0,OtherAddition=0,OtherDiscount=0 \n"+
                "WHERE EVCId ='"+evcId+"' ";
        db.execSQL(query);
    }
    public void UpdateFromOldInvoice(String evcId, int orderId) {
        String query = "Update  "+ DATABASE_TABLE +"\n" +
            "SET Dis1=IFNULL((SELECT O.Dis1 FROM DiscountCustomerOldInvoiceHeader o WHERE saleId = "+orderId+"),0),\n" +
            "Dis2=IFNULL((SELECT O.Dis2 FROM DiscountCustomerOldInvoiceHeader o WHERE saleId = "+orderId+"),0),\n" +
            "Dis3=IFNULL((SELECT O.Dis3 FROM DiscountCustomerOldInvoiceHeader o WHERE saleId = "+orderId+"),0),\n" +
            "Add1=IFNULL((SELECT O.Add1 FROM DiscountCustomerOldInvoiceHeader o WHERE saleId = "+orderId+"),0),\n" +
            "Add2=IFNULL((SELECT O.Add2 FROM DiscountCustomerOldInvoiceHeader o WHERE saleId = "+orderId+"),0),\n" +
            "OtherAddition=0,OtherDiscount=0\n" +
            "WHERE EVCId ='"+evcId+"' ";
        db.execSQL(query);
    }

}

