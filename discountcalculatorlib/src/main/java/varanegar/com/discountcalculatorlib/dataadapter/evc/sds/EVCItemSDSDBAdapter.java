package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19.DisAccDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.general.DiscountFreeReasonDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.price.DiscountContractPriceSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.price.DiscountPriceHistoryDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.entity.general.DiscountFreeReason;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.CallOrderLineItemStatusData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;
import varanegar.com.discountcalculatorlib.viewmodel.MyInt;


public class EVCItemSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_EVC_ITEM_ID = "_id";
    public final String KEY_ROW_ORDER = "RowOrder";
    public final String KEY_GOODS_REF = "GoodsRef";
    public final String KEY_UNIT_QTY = "UnitQty";
    public final String KEY_UNIT_CAPACITY = "UnitCapasity";
    public final String KEY_TOTAL_QTY = "TotalQty";
    public final String KEY_AMOUNT_NUT = "AmountNut";
    public final String KEY_DISCOUNT = "Discount";
    public final String KEY_AMOUNT = "Amount";
    public final String KEY_PRIZE_TYPE = "PrizeType";
    public final String KEY_SUP_AMOUNT = "SupAmount";
    public final String KEY_ADD_AMOUNT = "AddAmount";
    public final String KEY_CUST_PRICE = "CustPrice";
    public final String KEY_USER_PRICE = "UserPrice";
    public final String KEY_CHARGE_PERCENT = "ChargePercent";
    public final String KEY_EVC_ID = "EVCRef";
    public final String KEY_CALL_ID = "CallId";
    public final String KEY_TOTAL_WEIGHT = "TotalWeight";
    public final String KEY_UNIT_REF = "UnitRef";
    public final String KEY_TAX = "Tax";
    public final String KEY_PRICE_ID = "PriceId";
    public final String KEY_TAX_PERCENT = "TaxPercent";
    public final String KEY_DIS_REF = "DisRef";
    public final String KEY_CHARGE = "Charge";
    public final String KEY_PACK_QTY = "PackQty";
    public final String KEY_BATCH_REF = "BatchId";
    public final String KEY_REDUCE_Qty = "ReduceOfQty";
    public final String KEY_ITEM_VOLUME = "ItemVolume";
    public final String KEY_CPRICE_REF = "CPriceRef";
    public final String KEY_PERIODIC_DISCOUNT_REF = "PeriodicDiscountRef";

    public final String KEY_DIS_1 = "EvcItemDis1";
    public final String KEY_DIS_2 = "EvcItemDis2";
    public final String KEY_DIS_3 = "EvcItemDis3";
    public final String KEY_ADD_1 = "EvcItemAdd1";
    public final String KEY_ADD_2 = "EvcItemAdd2";
    public final String KEY_FREE_REASON_ID = "FreeReasonId";
    public final String KEY_ORDERLINE_ID = "OrderLineId";

    public static final String DATABASE_TABLE = "EVCItemSDS";
    private static String TAG = "EVCItemSDSDBAdapter";
    private static EVCItemSDSDBAdapter instance;

    public EVCItemSDSDBAdapter() {
    }

    public static EVCItemSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCItemSDSDBAdapter();
        }

        return instance;

    }

    public long saveEVCItem(DiscountCallOrderLineData lineData, String evcId) {
        return saveEVCItem(lineData.sortId, lineData.productId, lineData.invoiceTotalQty, 1, lineData.weight, 0
                , lineData.unitPrice, lineData.cartonType, 0, 0, 0, lineData.priceId, lineData.userprice, lineData.totalRequestCharge, lineData.totalRequestTax
                , lineData.invoiceBigQtyId, evcId, lineData.callUniqueId, lineData.invoiceBigQty, lineData.volume, lineData.freeReasonId
                , lineData.orderLineId.toString());
    }

    public void saveEVCItem(DiscountCallReturnData headerData, String evcId1, String evcId2, boolean isNew) {

        String sql = "INSERT INTO " + DATABASE_TABLE
                + " (EVCRef, CallId, RowOrder, GoodsRef, UnitQty, UnitCapasity, TotalQty, "
                + " AmountNut, Discount, Amount, PrizeType, SupAmount, AddAmount, CustPrice, UserPrice \n"
                + " , CPriceRef, UnitRef, PriceId, charge, tax, chargePercent, taxPercent) \n"
                + " SELECT '%S' , '" + headerData.callUniqueId + "' , RowOrder, ProductId, UnitQty, UnitCapasity, TotalQty, AmountNut, 0, Amount, PrizeType, SupAmount \n"
                + "       , 0, CustPrice, UserPrice, CPriceRef, UnitRef, PriceId, charge, tax \n"
                + "       , IFNULL( CASE (Amount - Discount + AddAmount) WHEN 0 THEN 0 ELSE ((charge / (Amount - Discount + AddAmount)) * 100) END, 0 ) AS chargePercent \n"
                + "       , IFNULL( CASE (Amount - Discount + AddAmount) WHEN 0 THEN 0 ELSE ((tax / (Amount - Discount + AddAmount)) * 100) END,0) AS taxPercent \n"
                + " FROM " + DiscountCustomerOldInvoiceDetailDBAdapter.DATABASE_TABLE
                + " WHERE SaleId = '" + headerData.returnRefId +"'"+ (!isNew ? " AND PrizeType = 0" : "");

        //Main
        db.execSQL(String.format(sql, evcId1));

        //Clone
        db.execSQL(String.format(sql, evcId2));

    }

    private long saveEVCItem(int sortId, int productId, BigDecimal totalQty, int unitCapacity, BigDecimal weight, double discount, BigDecimal unitPrice, int cartonType
            , int prizeType, double supAmount, double addAmount, long priceId, BigDecimal userPrice, BigDecimal totalCharge, BigDecimal totalTax, long bigQtyId
            , String evcId, String callUniqueId, BigDecimal bigQty, int volume
            , int freeReasonId, String  orderLineId) {

        int calcType = 0;
        if (freeReasonId != 0) {
            DiscountFreeReason discountFreeReason = DiscountFreeReasonDBAdapter.getInstance().getFreeReasonById(freeReasonId);
            if (discountFreeReason != null)
                calcType = discountFreeReason.calcPriceType;
        }
        ContentValues values = new ContentValues();
        values.put(KEY_ROW_ORDER, sortId);
        values.put(KEY_ORDERLINE_ID, orderLineId);

        values.put(KEY_GOODS_REF, productId);
        values.put(KEY_TOTAL_QTY, totalQty.doubleValue());
        values.put(KEY_UNIT_CAPACITY, unitCapacity);
        if (weight != null)
            values.put(KEY_TOTAL_WEIGHT, totalQty.longValue() * weight.doubleValue());
        values.put(KEY_DISCOUNT, discount);

        if (calcType == 2){
            values.put(KEY_AMOUNT_NUT, 0.0);
            values.put(KEY_AMOUNT, 0.0);
            values.put(KEY_CUST_PRICE, 0.0);
            values.put(KEY_USER_PRICE, 0.0);
        }
        else{
            if (GlobalVariables.getDecimalDigits() > 0) {
                values.put(KEY_AMOUNT_NUT, Math.round(Math.round((unitPrice.doubleValue() * cartonType) * 1.0) * (totalQty.longValue() / (cartonType * 1.0))));
                values.put(KEY_AMOUNT, Math.round(Math.round((unitPrice.doubleValue() * cartonType) * 1.0) * (totalQty.longValue() / (cartonType * 1.0))));
            } else {
                values.put(KEY_AMOUNT_NUT, unitPrice.doubleValue() * totalQty.longValue());
                values.put(KEY_AMOUNT, unitPrice.doubleValue() * totalQty.longValue());
            }
            values.put(KEY_CUST_PRICE, unitPrice.doubleValue());
            values.put(KEY_USER_PRICE, userPrice.doubleValue());
        }
        values.put(KEY_PRIZE_TYPE, prizeType);
        values.put(KEY_SUP_AMOUNT, supAmount);
        values.put(KEY_ADD_AMOUNT, addAmount);
        values.put(KEY_PRICE_ID, priceId);
        values.put(KEY_CHARGE_PERCENT, totalCharge.doubleValue());
        values.put(KEY_UNIT_QTY, bigQty.doubleValue());
        values.put(KEY_UNIT_REF, bigQtyId);
        values.put(KEY_EVC_ID, evcId);
        values.put(KEY_CALL_ID, callUniqueId);
        values.put(KEY_ITEM_VOLUME, volume);
        if(freeReasonId != 0)
            values.put(KEY_FREE_REASON_ID, freeReasonId);

        return db.insertOrThrow(DATABASE_TABLE, null, values);
    }

    public int fillByEVCItemPreview(String evcId, String callId, int discountId, int calcPrizeType, int evcType, int saleId, int dcRef) {
        int result = 1;
        String sql = "INSERT INTO " + DATABASE_TABLE + "(" +
                " GoodsRef, UnitCapasity, EVCRef,  " +
                " UnitQty, AmountNut, RowOrder, CPriceRef," +
                " TotalQty, PrizeType, Discount, " +
                " Amount, SupAmount, AddAmount, UserPrice, CustPrice, PriceId, DisRef, UnitRef, CallId, EvcItemDis1,EvcItemDis2,EvcItemDis3, EvcItemOtherDiscount) \n" +
                " SELECT GP.GoodsRef , GPP.Quantity as UnitCapasity, '" + evcId+"' as evcRef,  " +
                " AD.PrizeCount*GP.UnitQty as UnitQty,0 AS AmountNut, " +
                " 0 AS RowOrder, " ;
        if (evcType == 2 || evcType == 10 || evcType == 12)
            sql += " IFNULL((select CPriceRef from " + DiscountCustomerOldInvoiceDetailDBAdapter.DATABASE_TABLE +" as diss where diss.ProductId =GP.GoodsRef and SaleId = "+saleId+" LIMIT 1), pr.Id) as CPriceRef, ";
        else
            sql += "CP.Id as CPriceRef,";

        sql += " AD.PrizeCount*GP.UnitQty*GPP.Quantity AS TotalQty, 1 AS PrizeType, \n";
        if (calcPrizeType == 1) sql += "CP.SalePrice*(AD.PrizeCount*GP.UnitQty*GPP.Quantity) AS Discount,";
        else sql += "0 AS Discount,";

        if (calcPrizeType == 1) sql += "CP.SalePrice*(AD.PrizeCount*GP.UnitQty*GPP.Quantity) AS Amount,";
        else sql += " 0 AS Amount, ";

        sql += "0 AS SupAmount, 0 AS AddAmount,";

        if (calcPrizeType == 1) sql += "Pr.UserPrice AS UserPrice,"; else sql +=" 0 AS UserPrice,";
        if (calcPrizeType == 1) sql += "CP.SalePrice AS CustPrice,"; else sql +=" 0 AS CustPrice,";

        if (evcType == 2 || evcType == 10 || evcType == 12)
            sql += " IFNULL((select PriceId from " + DiscountCustomerOldInvoiceDetailDBAdapter.DATABASE_TABLE +" as diss where diss.ProductId =GP.GoodsRef and SaleId = "+saleId+" LIMIT 1), pr.Id) as PriceRef, ";
        else sql += "Pr.Id as PriceRef, " ;

        sql += "AD.DiscountId, GPP.BackofficeId AS UnitRef,'" + callId + "',";

        if (calcPrizeType == 1) sql += "CASE WHEN DisAccRef=1 THEN CP.SalePrice*(AD.PrizeCount*GP.UnitQty*GPP.Quantity) ELSE 0 END AS EvcItemDis1,";
        else sql += " 0 AS EvcItemDis1,";

        if (calcPrizeType == 1) sql += "CASE WHEN DisAccRef=2 THEN CP.SalePrice*(AD.PrizeCount*GP.UnitQty*GPP.Quantity) ELSE 0 END AS EvcItemDis2,";
        else  sql += "0 AS EvcItemDis2,";

        if (calcPrizeType == 1) sql += "CASE WHEN DisAccRef=3 THEN CP.SalePrice*(AD.PrizeCount*GP.UnitQty*GPP.Quantity) ELSE 0 END  AS EvcItemDis3, ";
        else sql += "0 AS EvcItemDis3, ";
        if (calcPrizeType == 1) sql += "CASE WHEN DisAccRef>3 THEN CP.SalePrice*(AD.PrizeCount*GP.UnitQty*GPP.Quantity) ELSE 0 END  AS OtherDiscount ";
        else sql += "0 AS OtherDiscount ";

        sql +=
                " FROM " + EVCTempAcceptedDiscountAdapter.DATABASE_TABLE + " AD \n"+
                        " INNER JOIN " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + " GP ON GP.DiscountId=AD.DiscountId \n" +
                        " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE  + " GPP ON GPP.ProductId = GP.GoodsRef AND GPP.BackOfficeId = GP.UnitRef\n" +

                        " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " header ON header.EvcId = GP.EVCRef " +
                        " INNER JOIN " + EVCTempCustomerSDSDBAdapter.DATABASE_TABLE + " c ON header.CustRef = c.CustRef " +

                        " LEFT JOIN " + DiscountPriceHistoryDBAdapter.DATABASE_TABLE +" Pr ON Pr.GoodsRef = GP.GoodsRef " +
                        " AND Pr.ID = ( Select Pr2.ID "+
                        " From " + DiscountPriceHistoryDBAdapter.DATABASE_TABLE +" Pr2 "+
                        " Where Pr2.GoodsRef = GP.GoodsRef " +
                        " AND (AD.EvcOprDate BETWEEN Pr2.StartDate AND IFNULL(Pr2.EndDate, AD.EvcOprDate)) "+
                        " AND IFNULL(Pr2.DCRef, '" + dcRef +"') = '"+dcRef+ "'"+
                        " ORDER BY Pr2.DCRef DESC "+
                        " LIMIT 1"+
                        ")"+
                        " LEFT JOIN " + DiscountContractPriceSDSDBAdapter.DATABASE_TABLE +" CP	ON 1 = 1 "+
                        " AND CP.ID= ( "+
                        " SELECT  CP2.ID "+
                        " FROM " + DiscountContractPriceSDSDBAdapter.DATABASE_TABLE +" CP2 "+
                        " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE  + " P2 ON P2.BackOfficeId = CP2.UnitRef AND P2.ProductId = GP.GoodsRef \n"+
                        " WHERE  (  ( CP2.GoodsRef Is Not Null  And CP2.GoodsRef = GP.GoodsRef ) )  "+
                        " AND (AD.EvcOprDate BETWEEN CP2.StartDate AND IFNULL(CP2.EndDate,AD.EvcOprDate)) \n"+
                        " AND (IFNULL(CP2.CustRef, c.CustRef) = c.CustRef) \n"+
                        " AND (IFNULL(CP2.DCRef, "+ dcRef +") = "+ dcRef+")\n"+
                        " AND (IFNULL(CP2.CustCtgrRef, IFNULL(c.CustCtgrRef,-1) ) = IFNULL(c.CustCtgrRef,-1) )\n"+
                        " AND (IFNULL(CP2.CustActRef,IFNULL(c.CustActRef,-1)) = IFNULL(c.CustActRef,-1))\n"+
                        " AND (IFNULL(CP2.StateRef, IFNULL(c.StateRef,-1)) = IFNULL(c.StateRef,-1))\n" +
                        " AND (IFNULL(CP2.CountyRef, IFNULL(c.CountyRef,-1)) = IFNULL(c.CountyRef,-1))\n"+
                        " AND (IFNULL(CP2.AreaRef, IFNULL(c.AreaRef,-1) ) = IFNULL(c.AreaRef,-1) )\n" +
                        " AND (IFNULL(CP2.OrderTypeRef, header.OrderType ) = header.OrderType )\n" +
                        " AND (IFNULL(CP2.CustLevelRef, c.CustLevelRef)= c.CustLevelRef)\n"+
                        " AND (IFNULL(CP2.BuyTypeRef, header.PayType ) = header.PayType)\n"+
                        " ORDER BY CP2.GoodsRef Desc , CP2.Priority --CP2.CustActRef DESC, CP2.CustCtgrRef DESC \n"+
                        " LIMIT 1 " +
                        ")\n"+
                        " WHERE GP.EVCRef='" + evcId + "' \n";

        db.execSQL(sql);

        Cursor affectedCursor = db.rawQuery("SELECT CHANGES()", null);
        if (affectedCursor != null && affectedCursor.moveToFirst()) {
            if (affectedCursor.getInt(0) == 0) {
                EVCItemStatutesSDSDBAdapter.getInstance().deleteUnAffectedEVCItemStatutes(evcId, discountId);
                result = 0;
            }
        }
        return result;
    }

    public void updatePrice(int discountId){
        String sql ="UPDATE " + EVCItemSDSDBAdapter.DATABASE_TABLE +"\n" +
                " SET Discount=TotalQty*CustPrice\n" +
                " , Amount=TotalQty*CustPrice\n" +
                " , UserPrice = ifnull((select UserPrice from DiscountContractPriceSDS where Id=EVCItemSDS.PriceId limit 1), UserPrice)\n" +
                " , CustPrice = ifnull((select SalePrice from DiscountContractPriceSDS where Id=EVCItemSDS.CPriceRef limit 1), CustPrice) \n" +
                " where DisRef ='" + discountId + "'";
        db.execSQL(sql);
        sql = "UPDATE " + EVCItemSDSDBAdapter.DATABASE_TABLE +"\n" +
                "SET EvcItemDis1 = ( SELECT CASE WHEN DisAccRef=1 THEN TotalQty*CustPrice ELSE 0 END \n" +
                "   FROM EVCTempAcceptedDiscountSDS ad WHERE ad.DiscountId= EVCItemSDS.DisRef)\n" +
                ", EvcItemDis2 = ( SELECT CASE WHEN DisAccRef=2 THEN TotalQty*CustPrice ELSE 0 END \n" +
                "   FROM EVCTempAcceptedDiscountSDS ad WHERE ad.DiscountId= EVCItemSDS.DisRef)\n" +
                ", EvcItemDis3 = ( SELECT CASE WHEN DisAccRef=3 THEN TotalQty*CustPrice ELSE 0 END \n" +
                "   FROM EVCTempAcceptedDiscountSDS ad WHERE ad.DiscountId= EVCItemSDS.DisRef) \n" +
                " where DisRef ='" + discountId + "'";
        db.execSQL(sql);
    }

    public void deleteInvalidPrizeItems(String evcId) {
        String sql = " DELETE FROM  " + DATABASE_TABLE +
                " WHERE _ID IN (SELECT EI._ID " +
                " FROM " + DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E ON E.EvcID = EI.EVCRef " +
                " WHERE EI.PrizeType='1' AND E.EvcID='" + evcId + "' " +
                " 	and EI.UnitQty=0 )";
        db.execSQL(sql);
    }

    public void updatePrizeIncludedItems(String evcId) {
        Cursor cItem = getPaidPrizeIncludedEVCItems(evcId);
        Cursor cPrize = getPrizeIncludedPrizeItems(evcId);

        if (cItem != null && cPrize != null) {
            if (cItem.moveToFirst()) {
                do {
                    if (cPrize.moveToFirst()) {
                        do {
                            if (cItem.getInt(0) == cPrize.getInt(0)) {
                                String sql = " update " + DATABASE_TABLE + " set " +
                                        " totalqty = totalqty - " + cPrize.getDouble(4) +
                                        " , amount = totalqty - " + cPrize.getDouble(4) + " * custPrice" +
                                        " , AmountNut = totalqty - " + cPrize.getDouble(4) + " * custPrice - Discount + AddAmount " +
                                        " , unitqty = case when " + cItem.getInt(1) + "=" + cPrize.getInt(1) + " then   unitqty = unitqty - " + cPrize.getDouble(2) +
                                        " when (" + cItem.getDouble(2) * cItem.getDouble(3) + " - " + cPrize.getDouble(2) * cPrize.getDouble(3) + ") % " + cItem.getDouble(3) + "=0 then (" + cItem.getDouble(2) * cItem.getDouble(3) + " - " + cPrize.getDouble(2) * cPrize.getDouble(3) + ") % " + cItem.getDouble(3) +
                                        " else (" + cItem.getDouble(2) * cItem.getDouble(3) + " - " + cPrize.getDouble(2) * cPrize.getDouble(3) + ") % " + cPrize.getDouble(3) + " end " +
                                        " , unitref = case when " + cItem.getInt(1) + "=" + cPrize.getInt(1) + " then   unitref " +
                                        " when (" + cItem.getDouble(2) * cItem.getDouble(3) + " - " + cPrize.getDouble(2) * cPrize.getDouble(3) + ") % " + cItem.getDouble(3) + "=0 then unitref " +
                                        " else " + cPrize.getInt(1) + " end " +
                                        " , unitcapasity = case when " + cItem.getInt(1) + "=" + cPrize.getInt(1) + " then   unitcapasity " +
                                        " when (" + cItem.getDouble(2) * cItem.getDouble(3) + " - " + cPrize.getDouble(2) * cPrize.getDouble(3) + ") % " + cItem.getDouble(3) + "=0 then unitcapasity " +
                                        " else " + cPrize.getDouble(3) + " end " +
                                        " where _id = " + cItem.getInt(5);
                                db.execSQL(sql);
                            }
                        } while (cPrize.moveToNext());
                    }
                } while (cItem.moveToNext());
            }
        }
    }

    public void mergePrize1(String evcId) {
        String sql = "UPDATE  " + DATABASE_TABLE +
                " SET "+
                " TotalQty = (SELECT Sum(TotalQty) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref  AND prizetype = 1 AND EVCRef='" + evcId + "' AND RowOrder<>_Id) " +
                " ,Amount = (SELECT Sum(Amount) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref  AND prizetype = 1 AND EVCRef='" + evcId + "' AND RowOrder<>_Id) " +
                " ,Discount = (SELECT Sum(Discount) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref  AND prizetype = 1 AND EVCRef='" + evcId + "' AND RowOrder<>_Id) " +
                " ,EvcItemDis1 = (SELECT Sum(ifnull(EvcItemDis1, 0)) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref  AND prizetype = 1 AND EVCRef='" + evcId + "' AND RowOrder<>_Id) " +
                " ,EvcItemDis2 = (SELECT Sum(ifnull(EvcItemDis2, 0)) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref  AND prizetype = 1 AND EVCRef='" + evcId + "' AND RowOrder<>_Id) " +
                " ,EvcItemDis3 = (SELECT Sum(ifnull(EvcItemDis3, 0)) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref  AND prizetype = 1 AND EVCRef='" + evcId + "' AND RowOrder<>_Id) " +
                " ,EvcItemOtherDiscount = (SELECT Sum(ifnull(EvcItemOtherDiscount, 0)) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref  AND prizetype = 1 AND EVCRef='" + evcId + "' AND RowOrder<>_Id) " +
                " WHERE prizetype = 1 " +
                " AND _ID = (SELECT MAX(_ID) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref AND  prizetype = 1 AND EVCRef='" + evcId + "') " +
                " AND RowOrder<>_Id "+
                " AND EVCRef='" + evcId + "'";
        db.execSQL(sql);

        sql = "DELETE from  " + DATABASE_TABLE +
                " WHERE prizetype = 1 " +
                " AND _ID <> (SELECT MAX(_ID) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref AND " + DATABASE_TABLE + ".UnitRef=UnitRef  AND prizetype = 1 AND EVCRef='" + evcId + "') " +
                " AND EVCRef='" + evcId + "' AND RowOrder<>_Id ";
        db.execSQL(sql);
    }

    public void mergePrize2(String evcId, int evcType) {
        String c = "";
        if (evcType == 2 || evcType == 10 || evcType == 12)
            c = " and (ForRetSale=1 OR ForSale=1) ";

        String sql = "UPDATE  " + DATABASE_TABLE +
                " SET   UnitQty = (SELECT  TotalQty/Quantity FROM " + DiscountProductUnitDBAdapter.DATABASE_TABLE +" pk WHERE pk.ProductId=" + DATABASE_TABLE +".GoodsRef and TotalQty%Quantity=0 " + c +" order by Quantity desc LIMIT 1 )" +
                " ,unitref= (SELECT  BackOfficeId FROM " + DiscountProductUnitDBAdapter.DATABASE_TABLE +" pk WHERE pk.ProductId=" + DATABASE_TABLE +".GoodsRef and TotalQty%Quantity=0 " + c +" order by Quantity desc LIMIT 1 )" +
                " ,unitcapasity = (SELECT  Quantity FROM " + DiscountProductUnitDBAdapter.DATABASE_TABLE +" pk WHERE pk.ProductId =" + DATABASE_TABLE +".GoodsRef and TotalQty%Quantity=0 " + c +" order by Quantity desc LIMIT 1 ) " +
                " WHERE prizetype = 1 and " +
                " _ID = (SELECT MAX(_ID) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref AND prizetype = 1 AND EVCRef='" + evcId + "') " +
                " AND EVCRef='" + evcId + "'";
        db.execSQL(sql);

        sql = "DELETE from  " + DATABASE_TABLE +
                " WHERE prizetype = 1 " +
                " AND _ID <> (SELECT MAX(_ID) FROM " + DATABASE_TABLE + " e WHERE " + DATABASE_TABLE + ".Goodsref=Goodsref AND prizetype = 1 AND EVCRef='" + evcId + "') " +
                " AND EVCRef='" + evcId + "' ";
        db.execSQL(sql);
    }

    public void updatePrizeMinUnit(String evcId) {
        String sql = " UPDATE  " + DATABASE_TABLE +
                " SET  UnitQty = (SELECT  UnitQty*unitCapasity /(select p.Quantity " +
                " from " + DATABASE_TABLE + " e " +
                " inner join  " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId=e.goodsref " +
                " inner join  " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " p on p.BackOfficeId=g.SmallUnitId and g.ProductId=p.ProductId " +
                " where e._id=" + DATABASE_TABLE + "._id) " +
                " FROM " + DATABASE_TABLE + " e WHERE e._id=" + DATABASE_TABLE + "._id limit 1) " +
                " ,unitref =(select g.SmallUnitId " +
                " from " + DATABASE_TABLE + " e " +
                " inner join  " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId=e.goodsref " +
                " inner join  " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " p on p.BackOfficeId=g.SmallUnitId and g.ProductId=p.ProductId " +
                " where e._id=" + DATABASE_TABLE + "._id limit 1) " +
                " ,unitCapasity = (select p.Quantity " +
                " from " + DATABASE_TABLE + " e " +
                " inner join  " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId=e.goodsref " +
                " inner join  " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " p on p.BackOfficeId=g.SmallUnitId and g.ProductId=p.ProductId " +
                " where e._id=" + DATABASE_TABLE + "._id limit 1) " +
                " ,TotalQty   = (SELECT   UnitQty*unitCapasity FROM " + DATABASE_TABLE + " e WHERE e._id=" + DATABASE_TABLE + "._id) " +
                " WHERE prizetype = 1 " +
                " AND EVCRef= '" + evcId + "'";
        db.execSQL(sql);
    }

    public void applyStatuteOnEVCItem1(String evcId) {

        String calctype  = "ifnull((SELECT F.CalcPriceType FROM " + DiscountFreeReasonDBAdapter.DATABASE_TABLE +" F  WHERE F.FreeReasonId=EVCItemSDS.FreeReasonId),0)";

        String sql = "UPDATE  " + DATABASE_TABLE +
                " SET Discount = CASE WHEN " + calctype +"=1 OR PrizeType=1 THEN Discount " +
                " ELSE  IFNULL((    SELECT Sum(IFNULL(Discount,0))"+
                "   FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef "+
                "   WHERE d.PrizeType not in (1,5) and EVCItemRef="+DATABASE_TABLE+"._ID),0)"+
                " END "+
                ",SupAmount = CASE WHEN " + calctype +"=1 THEN SupAmount" +
                " ELSE  IFNULL((    SELECT Sum(IFNULL(SupAmount,0)) " +
                " FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +
                " WHERE EVCItemRef="+DATABASE_TABLE+"._ID),0)"+
                "END"  +
                ",AddAmount = CASE WHEN " + calctype +" =1 THEN AddAmount " +
                " ELSE IFNULL((SELECT Sum(IFNULL(AddAmount,0)) " +
                " FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +
                " WHERE EVCItemRef="+DATABASE_TABLE+"._ID),0)  " +
                "END"+

                ",EvcItemDis1 = " +
                " CASE WHEN FreeReasonId IS NOT NULL THEN "+
                " CASE WHEN EXISTS(SELECT 1 FROM "+ DiscountFreeReasonDBAdapter.DATABASE_TABLE + " AS F " +
                " WHERE F.FreeReasonId = "+ DATABASE_TABLE +".FreeReasonId AND F.DisAccType=1 AND (F.CalcPriceType=1 OR PrizeType=1) ) " +
                " THEN Discount ELSE 0 END "+
                " ELSE IFNULL((SELECT Sum(IFNULL(Discount,0)) " +
                " FROM "+ EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +" ES " +
                " INNER JOIN "+ DiscountDBAdapter.DATABASE_TABLE  +" d on d.Id=es.DisRef " +
                " WHERE d.PrizeType not in (1,5,6) and EVCItemRef= "+DATABASE_TABLE+"._ID and d.DisAccRef=1),0) END "+

                ",EvcItemDis2 = " +
                " CASE WHEN FreeReasonId IS NOT NULL THEN "+
                " CASE WHEN EXISTS(SELECT 1 FROM "+DiscountFreeReasonDBAdapter.DATABASE_TABLE + " AS F " +
                " WHERE F.FreeReasonId = "+ DATABASE_TABLE +".FreeReasonId AND F.DisAccType = 2 AND (F.CalcPriceType=1 OR PrizeType=1) ) " +
                " THEN Discount ELSE 0 END "+
                " ELSE IFNULL((SELECT Sum(IFNULL(Discount,0)) " +
                " FROM "+ EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +" ES " +
                " INNER JOIN "+ DiscountDBAdapter.DATABASE_TABLE  +" d on d.Id=es.DisRef " +
                " WHERE d.PrizeType not in (1,5,6) and EVCItemRef= "+DATABASE_TABLE+"._ID AND d.DisAccRef=2),0) END \n"+
                ",EvcItemDis3 = " +
                " CASE WHEN FreeReasonId IS NOT NULL THEN "+
                " CASE WHEN EXISTS(SELECT 1 FROM "+DiscountFreeReasonDBAdapter.DATABASE_TABLE + " AS F " +
                " WHERE F.FreeReasonId = "+ DATABASE_TABLE +" .FreeReasonId AND F.DisAccType = 3 AND (F.CalcPriceType=1 OR PrizeType=1) ) " +
                " THEN Discount ELSE 0 END "+
                " ELSE IFNULL((SELECT Sum(IFNULL(Discount,0)) " +
                " FROM "+ EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +" ES " +
                " INNER JOIN "+ DiscountDBAdapter.DATABASE_TABLE  +" d on d.Id=es.DisRef " +
                " WHERE d.PrizeType not in (1,5,6) AND EVCItemRef= "+DATABASE_TABLE+"._ID AND d.DisAccRef=3),0) END \n" +
                ",EvcItemAdd1 = "+
                " CASE WHEN EXISTS(SELECT 1 FROM "+DiscountFreeReasonDBAdapter.DATABASE_TABLE + " AS F " +
                " WHERE F.FreeReasonId = "+ DATABASE_TABLE +" .FreeReasonId AND F.DisAccType=4 AND F.CalcPriceType=1) THEN AddAmount ELSE "+
                " IFNULL((SELECT Sum(IFNULL(AddAmount,0)) FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " es " +
                " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef " +
                " WHERE EVCItemRef = "+DATABASE_TABLE+"._ID and d.DisAccRef=4),0)  END \n"+
                ",EvcItemAdd2 = "+
                " CASE WHEN EXISTS(SELECT 1 FROM "+DiscountFreeReasonDBAdapter.DATABASE_TABLE + " AS F " +
                " WHERE F.FreeReasonId = "+ DATABASE_TABLE +" .FreeReasonId AND F.DisAccType = 5 AND F.CalcPriceType=1) THEN AddAmount ELSE "+
                " IFNULL((SELECT Sum(IFNULL(AddAmount,0)) FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " es " +
                " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef " +
                " WHERE EVCItemRef = "+DATABASE_TABLE+"._ID and d.DisAccRef=5),0)  END \n"+
                ",EvcItemOtherDiscount= " +
                " CASE WHEN FreeReasonId IS NOT NULL THEN \n"+
                " CASE WHEN IFNULL((SELECT 1 FROM " + DiscountFreeReasonDBAdapter.DATABASE_TABLE + " AS F " +
                " JOIN " + DisAccDBAdapter.DATABASE_TABLE +" da  ON da.Id = F.DisAccType " +
                " WHERE F.FreeReasonId = "+ DATABASE_TABLE +" .FreeReasonId AND "+
                " da.IsDefault=0 AND da.IsDiscount=1 AND (F.CalcPriceType=1 OR PrizeType=1) ),0 ) = 1 " +
                "  THEN Discount ELSE 0 END \n"+
                " ELSE IFNULL(( SELECT Sum(IFNULL(Discount,0)) "+
                " FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES "+
                " JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef "+
                " JOIN " + DisAccDBAdapter.DATABASE_TABLE + " da on da.Id=d.DisAccRef and da.IsDiscount=1 and da.IsDefault=0 "+
                " WHERE EVCItemRef="+DATABASE_TABLE + "._ID and d.PrizeType not in (1,5)),0) END \n"+
                " ,EvcItemOtherAddition = "+
                " IFNULL(( SELECT Sum(IFNULL(AddAmount,0)) "+
                " FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " es " +
                " JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef "+
                " JOIN " + DisAccDBAdapter.DATABASE_TABLE + " da on da.Id = d.DisAccRef and da.IsDiscount=0 and da.IsDefault=0 "+
                " WHERE EVCItemRef="+DATABASE_TABLE + "._ID),0) \n"+
                " WHERE EVCRef = '" + evcId + "' AND PrizeType = 0 ";
        db.execSQL(sql);
    }

    public void applyStatuteOnEVCItem2(String evcId) {

        String calctype  = "IFNULL((SELECT F.CalcPriceType FROM " + DiscountFreeReasonDBAdapter.DATABASE_TABLE +" F  WHERE F.FreeReasonId=EVCItemSDS.FreeReasonId),0)";

        String sql = "UPDATE  " + DATABASE_TABLE +
                " SET Tax = CASE WHEN " + calctype + "=1 THEN Tax ELSE IFNULL((Amount - Discount + AddAmount) * IFNULL(TaxPercent,0)/100,0) END , "+
                " Charge = CASE WHEN " + calctype + "=1 THEN Charge ELSE  IFNULL((Amount - Discount + AddAmount) * IFNULL(chargePercent,0)/100,0) END ";
        db.execSQL(sql);

    }

    public void applyStatuteOnEVCItem3(String evcId) {
        String sql = " UPDATE  " + DATABASE_TABLE +
                " SET AmountNut = Amount - Discount + AddAmount + tax + charge " +
                " WHERE EVCRef = '" + evcId + "' AND " + DATABASE_TABLE + ".FreeReasonId is Null";
        db.execSQL(sql);
    }

    public void calcChargeTax(String evcId) {
        Cursor c = getEvcItems(evcId);
        if (c != null && c.moveToFirst()) {
            do {
                updateEvcItemTax(c.getInt(0));
            } while (c.moveToNext());
            updateAmountByTax();
        }
    }


    public void updateProductPriceOnEVCItem(DiscountCallOrderData callData, String evcId) {
        Cursor c = null;

        try {
            String sql = "select _id, GoodsRef from " + DATABASE_TABLE + " where EVCRef='" + evcId + "'";
            c = db.rawQuery(sql, null);
            if (c != null && c.moveToFirst()) {
                do {
                    for (DiscountCallOrderLineData cld : callData.callOrderLineItemData) {
                        if (c.getInt(1) == cld.productId) {
                            String sql2 = "update " + DATABASE_TABLE + " set priceid='" + cld.priceId + "' where _id=" + c.getInt(0);
                            db.execSQL(sql2);
                            break;
                        }
                    }
                } while (c.moveToNext());
            }

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (c != null) c.close();
        }
    }

    public DiscountCallOrderData fillCallLineWithPromo(String evcId, DiscountCallOrderData callData) {
        callData.callLineItemDataWithPromo = new ArrayList<>();
        Cursor cItemData = null;
        Cursor cEVCItemStatuteInfo = null;

        try {
            cItemData = getEvcItemData(evcId, callData.totalOrderDis1, callData.totalOrderDis2, callData.totalOrderDis3, callData.totalOrderAdd1, callData.totalOrderAdd2);

            if (cItemData != null && cItemData.moveToFirst()) {
                do {
                    DiscountCallOrderLineData tempData = new DiscountCallOrderLineData();

                    tempData.evcId = evcId;
                    tempData.callUniqueId = callData.callUniqueId;
                    tempData.orderUniqueId = callData.orderUniqueId;
                    tempData.customerId = callData.customerId;

                    tempData.disRef = cItemData.getInt(cItemData.getColumnIndex(KEY_DIS_REF));
                    tempData.isRequestPrizeItem = cItemData.getInt(cItemData.getColumnIndex(KEY_PRIZE_TYPE)) == 1;
                    tempData.unitPrice = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_CUST_PRICE)));
                    tempData.unitCapacity = cItemData.getInt(cItemData.getColumnIndex(KEY_UNIT_CAPACITY));

                    tempData.priceId = cItemData.getInt(cItemData.getColumnIndex(KEY_PRICE_ID));
                    tempData.productId = cItemData.getInt(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTID));
                    tempData.productCode = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTCODE));
                    tempData.productName = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTNAME));
                    tempData.invoiceSmallQtyId = cItemData.getInt(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_SMALLUNITID));
                    tempData.invoiceBigQtyId = cItemData.getInt(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_LARGEUNITID));
                    tempData.invoiceBigQtyName = cItemData.getString(cItemData.getColumnIndex(DiscountProductUnitDBAdapter.KEY_PUNIT_PRODUCTUNITNAME));

                    tempData.invoiceTotalQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_TOTAL_QTY)));
                    tempData.invoiceBigQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_UNIT_QTY)));

//                    tempData.totalInvoiceTax = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_TAX)) + cItemData.getDouble(cItemData.getColumnIndex(KEY_ADD_AMOUNT)));

                    tempData.invoiceDis1 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("Dis1")));
                    tempData.invoiceDis2 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("Dis2")));
                    tempData.invoiceDis3 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("Dis3")));
                    tempData.invoiceDisOther = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("DisOther")));
                    tempData.totalInvoiceAddOther = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("AddOther")));

                    tempData.totalInvoiceTax = new BigDecimal(0);
                    tempData.totalInvoiceAdd1Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("Add1")));
                    tempData.totalInvoiceCharge =  new BigDecimal(0);
                    tempData.totalInvoiceAdd2Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("Add2")));

                    tempData.totalInvoiceDiscount = tempData.invoiceDis1.add(tempData.invoiceDis2).add(tempData.invoiceDis3).add(tempData.invoiceDisOther);
                    tempData.totalInvoiceAmount = tempData.invoiceTotalQty.multiply(tempData.unitPrice).setScale(0, BigDecimal.ROUND_HALF_UP);
                    tempData.totalInvoiceNetAmount = tempData.totalInvoiceAmount.add(tempData.totalInvoiceAdd1Amount).add(tempData.totalInvoiceAddOther)
                            .add(tempData.totalInvoiceAdd2Amount).subtract(tempData.totalInvoiceDiscount);
                    tempData.freeReasonId = cItemData.getInt(cItemData.getColumnIndex("FreeReasonId"));

                    DiscountCallOrderLineData baseData = null;

                    for (DiscountCallOrderLineData lineData : callData.callOrderLineItemData) {
                        if (lineData.productId == tempData.productId && lineData.isRequestPrizeItem == tempData.isRequestPrizeItem && lineData.freeReasonId == tempData.freeReasonId) {
                            baseData = lineData;
                            break;
                        }
                    }

                    tempData.sortId = baseData != null ? baseData.sortId : 9999;
                    tempData.orderLineId = baseData != null ? baseData.orderLineId : UUID.randomUUID();
                    tempData.requestBigQty = baseData != null ? baseData.requestBigQty : BigDecimal.ZERO;
                    tempData.requestBigQtyId = baseData != null ? baseData.requestBigQtyId : tempData.invoiceBigQtyId;
                    tempData.requestBigQtyName = baseData != null ? baseData.requestBigQtyName : tempData.invoiceBigQtyName;
                    tempData.requestSmallQty = baseData != null ? baseData.requestSmallQty : BigDecimal.ZERO;
                    tempData.requestSmallQtyId = baseData != null ? baseData.requestSmallQtyId : tempData.invoiceSmallQtyId;
                    tempData.requestSmallQtyName = baseData != null ? baseData.requestSmallQtyName : tempData.invoiceSmallQtyName;
                    tempData.requestTotalQty = baseData != null ? baseData.requestTotalQty : BigDecimal.ZERO;
                    tempData.totalRequestAdd1Amount = baseData != null ? baseData.totalRequestAdd1Amount : BigDecimal.ZERO;
                    tempData.totalRequestAdd2Amount = baseData != null ? baseData.totalRequestAdd2Amount : BigDecimal.ZERO;
                    tempData.totalRequestAmount = baseData != null ? baseData.totalRequestAmount : BigDecimal.ZERO;
                    tempData.totalRequestCharge = baseData != null ? baseData.totalRequestCharge : BigDecimal.ZERO;
                    tempData.totalRequestDiscount = baseData != null ? baseData.totalRequestCharge : BigDecimal.ZERO;
                    tempData.totalRequestTax = baseData != null ? baseData.totalRequestTax : BigDecimal.ZERO;
                    tempData.totalRequestNetAmount = baseData != null ? baseData.totalRequestNetAmount : BigDecimal.ZERO;

                    ArrayList<CallOrderLineItemStatusData> statusDataList = new ArrayList<>();

                    if (!tempData.evcId.trim().equals("")) {
                        cEVCItemStatuteInfo = EVCItemStatutesSDSDBAdapter.getInstance().getEvcItemStatuteDataToSend(evcId, cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_ITEM_ID)));

                        if (cEVCItemStatuteInfo != null && cEVCItemStatuteInfo.moveToFirst()) {
                            do {
                                CallOrderLineItemStatusData statusData = new CallOrderLineItemStatusData();

                                statusData.setEvcId(evcId);
                                statusData.setProductId(tempData.productId);
                                statusData.setRowOrder(cEVCItemStatuteInfo.getInt(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesSDSDBAdapter.KEY_EVC_ITEM_STATUTES_ROW_ORDER)));
                                statusData.setDiscountId(cEVCItemStatuteInfo.getInt(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesSDSDBAdapter.KEY_EVC_ITEM_STATUTES_DIS_REF)));
                                statusData.setDisGroup(cEVCItemStatuteInfo.getInt(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesSDSDBAdapter.KEY_EVC_ITEM_STATUTES_DIS_GROUP)));
                                statusData.setDiscount(new BigDecimal(cEVCItemStatuteInfo.getDouble(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesSDSDBAdapter.KEY_EVC_ITEM_STATUTES_DISCOUNT))));
                                statusData.setAddAmount(new BigDecimal(cEVCItemStatuteInfo.getDouble(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesSDSDBAdapter.KEY_EVC_ITEM_STATUTES_ADD_AMOUNT))));
                                statusData.setSupAmount(new BigDecimal(cEVCItemStatuteInfo.getDouble(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesSDSDBAdapter.KEY_EVC_ITEM_STATUTES_SUP_AMOUNT))));

                                statusDataList.add(statusData);
                            } while (cEVCItemStatuteInfo.moveToNext());
                        }


                    }

                    tempData.callOrderLineItemStatusDatas = statusDataList;
                    callData.callLineItemDataWithPromo.add(tempData);
                } while (cItemData.moveToNext());
            }

        } catch (Exception ex) {
            throw ex;
        } finally {

            if (cItemData != null) cItemData.close();
            if (cEVCItemStatuteInfo != null) cEVCItemStatuteInfo.close();
            return callData;
        }
    }

    /*SLE.usp_DecreaseEVCItemByQty*/
    public BigDecimal checkReferenceDisSale(String evcId1, int saleId, int productId, BigDecimal totalQty, String callId, int evcType) {
        int prizeQty = 0;
        int discountId, discountPrizeStepType;
        int sumDiscountPrizeQty, discountPrizeQty, discountPrizeStep, disAccRef,
                mainSaleQty = 0,totalPrizeQty = 0,totalDiscountPrizeQty = 0;

        discountId = discountPrizeStepType = -1;
        discountPrizeQty = discountPrizeStep = -1;
        int sumPrizeQty = 0;

        BigDecimal custPrice = BigDecimal.ZERO,
                userPrice = BigDecimal.ZERO;
        String temp[] = DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getReferenceProductValues(saleId, productId, 0).split(":");
        if (temp.length > 1) {
            mainSaleQty = (int) Double.parseDouble(temp[0]);
            custPrice = new BigDecimal(temp[1]);
            userPrice = new BigDecimal(temp[2]);
        }

        //گام افزایش تعدادی
        String discountTable = "SELECT (d.PrizeQty * pkp.Quantity) AS DiscountPrizeQty,  d.id AS DiscountId, " +
                "PrizeStepType, (d.PrizeStep * pkg.Quantity) AS PrizeStep, d.DisAccRef as DisAccRef \n" +
                " FROM " + DisSaleSDSDBAdapter.DATABASE_TABLE + " ds \n" +
                " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id = ds.DisRef \n" +
                " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pkg on pkg.ProductId = d.GoodsRef and pkg.BackOfficeId = IFNULL(d.PrizeStepUnit, d.QtyUnit) \n" +
                " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pkp on pkp.ProductId = d.GoodsRef and pkp.BackOfficeId = d.PrizeUnit \n" +
                " WHERE ds.HdrRef = " + saleId + " AND d.GoodsRef = " + productId + " AND d.GoodsRef = d.PrizeRef \n" +
                " AND d.PrizeStepType = 0 " +// گام افزايش تعدادي
                " AND (IFNULL(d.MinQty, 0)  = 0)" +
                " AND (IFNULL(d.MinAmount, 0) = 0) " +
                " AND d.PrizeQty * pkp.Quantity is not null " +
                " UNION "+
                "SELECT (d.PrizeQty * pkp.Quantity) AS DiscountPrizeQty,  d.id AS DiscountId, " +
                "PrizeStepType, d.PrizeStep  AS DiscountPrizeStep , d.DisAccRef \n" +
                " FROM " + DisSaleSDSDBAdapter.DATABASE_TABLE + " ds \n" +
                " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id = ds.DisRef \n" +
                " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pkp on pkp.ProductId = d.GoodsRef and pkp.BackOfficeId = d.PrizeUnit \n" +
                " WHERE ds.HdrRef = " + saleId + " AND d.GoodsRef = " + productId + " AND d.GoodsRef = d.PrizeRef \n" +
                " AND d.PrizeStepType = 1 " +
                " AND ( IFNULL(d.MinQty, 0)  = 0 )" +
                " AND (IFNULL(d.MinAmount, 0)  = 0) " +
                " AND d.PrizeQty * pkp.Quantity is not null " ;

        Cursor c = db.rawQuery(" SELECT PrizeStepType, PrizeStep, SUM(DiscountPrizeQty) as SumDiscountPrizeQty \n" +
                        " FROM (" + discountTable +") As TT "+
                        " GROUP BY PrizeStepType, PrizeStep "
                , null);

        int prizeCalcType =  DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getPrizeCalcType(saleId);
        if (c != null && c.moveToFirst()) {
            do{
                discountPrizeStepType = c.getInt(0);
                discountPrizeStep = c.getInt(1);
                sumDiscountPrizeQty = c.getInt(2);
                String temp2[] = DecreaseEVCItemByQty_FindPrizeByStep(saleId, productId, /*FreeReasonId,*/ discountPrizeStepType, discountPrizeStep,
                        sumDiscountPrizeQty, totalQty , sumPrizeQty, mainSaleQty, custPrice).split(":");
                if (temp2[0] != null && temp2[0] != "")
                    totalQty = new BigDecimal(temp2[0]);
                if (temp2[1] != null && temp2[1] != "")
                    sumPrizeQty = Integer.parseInt(temp2[1]);
                Cursor d = db.rawQuery(" SELECT DiscountId, DisAccRef, DiscountPrizeQty \n" +
                                " FROM (" + discountTable +") As TT "+
                                " WHERE PrizeStep= "+discountPrizeStep +" AND PrizeStepType= "+ discountPrizeStepType
                        , null);

                if (d != null && d.moveToFirst()) {
                    do {
                        discountId = d.getInt(d.getColumnIndex("DiscountId"));
                        disAccRef = d.getInt(d.getColumnIndex("DisAccRef"));
                        discountPrizeQty = d.getInt(d.getColumnIndex("DiscountPrizeQty"));

                        prizeQty =  /*floor*/(int)(discountPrizeQty*1.0 / sumDiscountPrizeQty*sumPrizeQty);
                        totalDiscountPrizeQty = totalDiscountPrizeQty + discountPrizeQty;
                        if (totalDiscountPrizeQty == sumDiscountPrizeQty)
                            prizeQty = sumPrizeQty - totalPrizeQty;
                        else
                            totalPrizeQty = totalPrizeQty + prizeQty;
                        if (prizeQty != 0){
                            DecreaseEVCItemByQty_AddPrize(productId, prizeQty, saleId, custPrice, evcId1, callId, evcType, discountId,
                                    disAccRef, prizeCalcType, userPrice );
                        }
                        else{
                            String sql = " INSERT INTO " + EVCSkipDiscountDBAdapter.DATABASE_TABLE +"(SaleRef, DisRef, EvcRef, SkipGoodsRef) " +
                                    " SELECT " +saleId +","+ disAccRef + ",'"+ evcId1 +"', null";
                            db.execSQL(sql);
                        }

                    } while (d.moveToNext());
                }

            } while (c.moveToNext() && totalQty.compareTo(BigDecimal.ZERO) == 0  );

        }
        return totalQty;
    }

    /*sle.usp_DecreaseEVCItemByQty_FindPrizeByStep*/
    private String DecreaseEVCItemByQty_FindPrizeByStep(int saleId, int productId, int discountPrizeStepType, int discountPrizeStep,
                                                        int discountPrizeQty, BigDecimal gtotalQty , int prizeQty, int mainSaleQty,
                                                        BigDecimal mainSaleCustPrice) {
        Integer mainPrizeQty= 0, remPrizeQty= 0, totalQty= 0, pricedQty= 0,
                packageRemainQty= 0,discountPackageQty= 0,inStepPrizeQty = 0,packageCount = 0;

        mainPrizeQty = 0;


        totalQty = gtotalQty.intValue();

        String temp2[] = DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getReferenceProductValues(saleId, productId, 1).split(":");
        if (temp2.length > 1) {
            mainPrizeQty = (int) Double.parseDouble(temp2[0]);
        }
/* *
 	select @MainSaleQty = @MainSaleQty - isnull(sum(Qty),0) from sle.vwRetSaleItmFull where TSaleRef=@SaleRef and GoodsRef=@GoodsRef and PrizeType=0 and ISNULL(FreeReasonId,0)=@FreeReasonId
	select @MainPrizeQty = @MainPrizeQty - isnull(sum(Qty),0) from sle.vwRetSaleItmFull where TSaleRef=@SaleRef and GoodsRef=@GoodsRef and PrizeType=1
* */
        if (discountPrizeStepType == 1) {
            discountPrizeStep = new BigDecimal(discountPrizeStep).divide(mainSaleCustPrice, 0, RoundingMode.FLOOR).intValue();
        }

        if (mainPrizeQty > (mainSaleQty / discountPrizeStep) * discountPrizeQty) {
            mainPrizeQty = (mainSaleQty / discountPrizeStep) * discountPrizeQty;//Math.floor()
        }

        discountPackageQty = discountPrizeStep + discountPrizeQty;


        if (totalQty >( mainSaleQty/ discountPrizeStep)* discountPackageQty)
            totalQty =( mainSaleQty/discountPrizeStep)*discountPackageQty;

        if ( (mainSaleQty % discountPrizeStep == 0) &&
                ((( mainSaleQty /discountPrizeStep) * discountPrizeQty) > mainPrizeQty))
            remPrizeQty = discountPrizeQty -((( mainSaleQty /discountPrizeStep) *discountPrizeQty)- mainPrizeQty);

        if (remPrizeQty != 0)  {
            if (remPrizeQty > totalQty)  prizeQty = totalQty;
            else prizeQty = remPrizeQty;
            totalQty = totalQty - prizeQty;
            pricedQty = totalQty % discountPackageQty;
            if (pricedQty > discountPrizeStep)   pricedQty = discountPrizeStep;
        }
        else
            pricedQty =( mainSaleQty + mainPrizeQty) % discountPackageQty;

        if (pricedQty != 0) {

            if (totalQty < pricedQty) pricedQty = totalQty;
            totalQty = totalQty - pricedQty;
        }
        packageCount = totalQty / discountPackageQty;
        packageRemainQty = totalQty % discountPackageQty;

        if (totalQty > 0){
            if (packageRemainQty < discountPrizeQty)
                inStepPrizeQty = packageCount * discountPrizeQty + packageRemainQty;
            else
                inStepPrizeQty =packageCount *discountPrizeQty + discountPrizeQty;

            totalQty = totalQty - inStepPrizeQty;
            prizeQty = prizeQty +inStepPrizeQty;
        }
        totalQty = totalQty + pricedQty;
        totalQty = gtotalQty.intValue() - prizeQty;

        return totalQty +  ":" + prizeQty;
    }

    /*sle.usp_DecreaseEVCItemByQty_AddPrize*/
    private void DecreaseEVCItemByQty_AddPrize(int productId, int prizeQty, int saleId,  BigDecimal mainSaleCustPrice, String evcId1, String callId,
                                               int evcType, int discountId,
                                               int disAccRef, int prizeCalcType, BigDecimal mainSaleUserPrice){
        if (prizeQty<=0) return;

        String query = "INSERT INTO "+ EVCSkipDiscountDBAdapter.DATABASE_TABLE + "(SaleRef, DisRef, EvcRef, SkipGoodsRef) SELECT " + saleId +","+ discountId+",'"+ evcId1+"' , null ";
        db.execSQL(query);

        //select top 1 @CustPrice = CustPrice, @UserPrice=UserPrice from sle.tblSaleItm where HdrRef=@SaleRef and GoodsRef=@GoodsRef and isnull(FreeReasonId, 0)=isnull(@FreeReasonId, 0)
        //@CustPrice = mainSaleCustPrice,@UserPrice= mainSaleUserPrice

        query = "INSERT INTO " + DATABASE_TABLE
                + " (EVCRef, CallId, RowOrder, GoodsRef, UnitQty, CPriceRef, UnitRef, UnitCapasity, TotalQty, AmountNut"
                + " , Discount, Amount, PrizeType, SupAmount, AddAmount, UserPrice"
                + " , CustPrice, PriceId, DisRef, tax, charge, taxPercent, chargePercent, PeriodicDiscountRef,FreeReasonId,"
                + " EvcItemDis1, EvcItemDis2, EvcItemDis3, EvcItemOtherDiscount) "
                + " SELECT '" + evcId1 + "','" + callId + "', -1, " + productId + "," + prizeQty + ", CPriceRef, pk.BackOfficeId, pk.Quantity," + prizeQty + ", 0 ";
        //case when @PrizeCalcType=1 then @PrizeQty*@CustPrice else 0 end as Discount,
        if (prizeCalcType == 1) query += "," + prizeQty * mainSaleCustPrice.intValue();
        else query += ",0 as Discount ";

        //case when @PrizeCalcType=1 then @PrizeQty*@CustPrice else 0 end as Amount,
        if (prizeCalcType == 1) query += "," + prizeQty * mainSaleCustPrice.intValue();
        else query += ",0 as Amount ";
        query += " ,1 ,0 ,0 ";
        //case when @PrizeCalcType=1 then @UserPrice else 0 END AS UserPrice,
        if (prizeCalcType == 1) query += "," + mainSaleUserPrice;
        else query += ",0 as UserPrice ";

        //case when @PrizeCalcType=1 then @CustPrice else 0 END AS CustPrice,
        if (prizeCalcType == 1) query += "," + mainSaleCustPrice;
        else query += ",0 as CustPrice ";

        //PriceRef, @DiscountId, 0, 0, 0, 0, 0,null,
        query += ",PriceId, "+ discountId +", 0, 0, 0, 0, 0,null";

        //case when @PrizeCalcType=1 and @DisAccRef=1 then @PrizeQty*@CustPrice else 0 end as EvcItemDis1,
        if (prizeCalcType == 1 && disAccRef == 1 ) query += "," + prizeQty* mainSaleCustPrice.intValue();
        else query += ",0 as EvcItemDis1 ";

        //case when @PrizeCalcType=1 and @DisAccRef=2 then @PrizeQty*@CustPrice else 0 end as EvcItemDis2,
        if (prizeCalcType == 1 && disAccRef == 2 ) query += "," + prizeQty* mainSaleCustPrice.intValue();
        else query += ",0 as EvcItemDis2 ";

        //case when @PrizeCalcType=1 and @DisAccRef=3 then @PrizeQty*@CustPrice else 0 end as EvcItemDis3
        if (prizeCalcType == 1 && disAccRef == 3 ) query += "," + prizeQty* mainSaleCustPrice.intValue();
        else query += ",0 as EvcItemDis3 ";

        if (prizeCalcType == 1 && disAccRef > 3 ) query += "," + prizeQty* mainSaleCustPrice.intValue();
        else query += ",0 as EvcItemOtherDiscount ";

        query += " FROM " + DATABASE_TABLE + " ei "
                + " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk on pk.ProductId = ei.GoodsRef AND pk.Status = 1"
                + " WHERE EVCRef = '" + evcId1 + "' AND ei.GoodsRef = " + productId + " AND ei.PrizeType=0";


        db.execSQL(query);
        db.execSQL("UPDATE " + DATABASE_TABLE + " SET RowOrder = _id WHERE EVCRef = '" + evcId1 + "' AND GoodsRef = " + productId + " AND PrizeType=0");
        if (evcType == 10 || evcType == 12) {
            db.execSQL("UPDATE " + DATABASE_TABLE + " SET TotalQty = TotalQty + " + prizeQty + " WHERE EVCRef = '" + evcId1 + "' AND GoodsRef = " + productId + " AND PrizeType=1");
        }
    }

    /*SLE.usp_DecreaseEVCItemByQty_Internal*/
    public void DecreaseEVCItemByQty_Internal(String evcId2, BigDecimal totalQty, int productId, int methodType){
        String query = "";
        // Decrease TotalQty by return qty.
        query = "UPDATE " + DATABASE_TABLE + " SET "
                + " UnitQty = CAST( (TotalQty - " + totalQty + ")/ UnitCapasity AS INT) \n"
                + " ,TotalQty = TotalQty - " + totalQty
                + " ,Amount = CASE WHEN (" + GlobalVariables.getDigtsAfterDecimalForCPrice() + " > 0 OR " + methodType + " =1 ) \n"
                + "                     AND CAST( (TotalQty - " + totalQty + ") /UnitCapasity AS INT) <> 0  \n"
                + "                     AND CAST( (TotalQty - " + totalQty + ") /UnitCapasity AS INT) * UnitCapasity = (TotalQty - " + totalQty + ") \n"
                + "                THEN CAST( (TotalQty -" + totalQty + ")/UnitCapasity AS INT) * round (UnitCapasity*CustPrice," + GlobalVariables.getRoundType() + ") \n"
                + "                ELSE CAST( (TotalQty - " + totalQty + ") AS INT) * CustPrice END \n"
                + " ,AmountNut = CASE WHEN (" + GlobalVariables.getDigtsAfterDecimalForCPrice() + " > 0 OR " + methodType + " =1) \n"
                + "                     AND CAST( (TotalQty - " + totalQty + ") /UnitCapasity AS INT) <> 0  \n"
                + "                     AND CAST( (TotalQty - " + totalQty + ") /UnitCapasity AS INT) * UnitCapasity = (TotalQty - " + totalQty + ") \n"
                + "                THEN CAST( (TotalQty -" + totalQty + ")/UnitCapasity AS INT) * round (UnitCapasity*CustPrice," + GlobalVariables.getRoundType() + ") \n"
                + "                ELSE CAST( (TotalQty - " + totalQty + ") AS INT) * CustPrice END \n"
                + " WHERE EVCRef = '" + evcId2 + "' AND GoodsRef = " + productId + " AND PrizeType=0 \n";
        db.execSQL(query);
    }

    public void calcTaxCharge(String evcId2, int productId, int evcType) {
        String query = "";
        query = "UPDATE " + DATABASE_TABLE
                + " SET tax = IFNULL((CASE WHEN CAST((AmountNut * taxPercent / 100) AS INT) = (AmountNut * taxPercent / 100) THEN CAST((AmountNut * taxPercent / 100) AS INT) \n"
                + "                        ELSE 1 + CAST((AmountNut * taxPercent / 100) AS INT) END) ,0) \n"
                + " ,charge = IFNULL((CASE WHEN CAST((AmountNut * chargePercent / 100) AS INT) = (AmountNut * chargePercent / 100) THEN CAST((AmountNut * chargePercent / 100) AS INT) \n"
                + "                        ELSE 1 + CAST((AmountNut * chargePercent / 100) AS INT) END) ,0) \n"
                + " WHERE EVCRef= '" + evcId2 + "' AND GoodsRef=" + productId;
        db.execSQL(query);

        if (evcType != 2) {
            db.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE EVCRef='" + evcId2 + "' AND GoodsRef = " + productId + " AND TotalQty = 0");
        }
    }

    public void deleteInvalidReturnItem(String evcId) {
        db.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE TotalQty < 0 AND EVCRef = '" + evcId + "'");
    }

    public void updateEVCitemForSellReturn1(String evcId) {
        String query = " UPDATE " + DATABASE_TABLE +
                "  SET UnitQty = CAST( (TotalQty/UnitCapasity) AS INT)  \n" +
                " ,Amount = (SELECT CASE\n" +
                "  WHEN (ROUND(CartonType*CustPrice) = ROUND((CartonType*CustPrice) + 0.5) AND TotalQty%CartonType=0) THEN\n" +
                "  CAST( CAST(CartonType*CustPrice AS int) * (TotalQty/CartonType) AS int)\n" +
                "  ELSE\n" +
                "  CAST(TotalQty*CustPrice AS int)\n" +
                "  END\n" +
                "  FROM DiscountProduct\n" +
                "  WHERE ProductId = GoodsRef)\n" +
                " ,AmountNut = (SELECT CASE\n" +
                "  WHEN (ROUND(CartonType*CustPrice) = ROUND((CartonType*CustPrice) + 0.5) AND TotalQty%CartonType=0) THEN\n" +
                "  ROUND(ROUND(CartonType*CustPrice, 0) * (TotalQty/CartonType), 0)\n" +
                "  ELSE\n" +
                "  ROUND(TotalQty*CustPrice, 0)\n" +
                "    END\n" +
                "  FROM DiscountProduct\n" +
                "  WHERE ProductId = GoodsRef)\n"+
                "  ,Discount=0  \n" +
                "  ,AddAmount=0  \n" +
                "  WHERE EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }

    //region update with special value

    public void resetValuesForSpecialValues(String evcId) {

        String query = "UPDATE " + DATABASE_TABLE
                + " SET Discount=0 ,AddAmount=0 "
                + " WHERE EVCRef = '" + evcId + "'"; //AND FreeReasonId IS NULL
        db.execSQL(query);
    }

    public void updateDis1WithSpecialValues(String evcId) {

        int calctype = 0;
        if (GlobalVariables.getPrizeCalcType())
            calctype = 1;
        /*
        String query = "Update " + DATABASE_TABLE
                + " Set Discount = ( SELECT Discount + CASE WHEN PrizeType=0 THEN CAST((Amount*IFNULL(E.Dis1,0))/100 as integer)  WHEN PrizeType=1 AND " + calctype + "=1 THEN EvcItemDis1 END \n"
                + " FROM "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E WHERE E._id = EVCRef ) \n"
                + " WHERE EVCRef = '" + evcId + "' AND Amount > 0 AND FreeReasonId IS NULL ";
                */

        String query = "Update " + DATABASE_TABLE + "\n" +
                " SET EvcItemDis1 = " +
                " CASE WHEN FreeReasonId IS NOT NULL THEN "+
                " CASE WHEN IFNULL((SELECT F.DisAccType FROM " + DiscountFreeReasonDBAdapter.DATABASE_TABLE + " F WHERE F.FreeReasonId = EVCItemSDS.FreeReasonId  AND (F.CalcPriceType=1 OR PrizeType=1)  ),0) =1 THEN Amount ELSE 0 END "+
                " ELSE CASE WHEN PrizeType=0 THEN (SELECT ROUND((Amount*IFNULL(E.Dis1,0))/100) FROM " + EVCHeaderSDSDBAdapter.DATABASE_TABLE +" E WHERE E._ID = EVCRef) WHEN PrizeType=1 AND " + calctype + "=1 THEN EvcItemDis1 END "+
                " END \n" +
                " WHERE EVCRef = '" + evcId + "' AND Amount > 0 --AND FreeReasonId IS NULL ";
        db.execSQL(query);
    }

    public void updateDis2WithSpecialValues(String evcId)
    {
        int calctype = 0;
        if (GlobalVariables.getPrizeCalcType())
            calctype = 1;
        /*
        String query = "Update " + DATABASE_TABLE
                + " Set Discount = (SELECT Discount + CASE WHEN PrizeType=0 THEN Cast((Amount*IFNULL(E.Dis2,0))/100 as integer)  WHEN PrizeType=1 AND " + calctype + "=1 THEN EvcItemDis2 END \n"
                + " FROM "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E WHERE E._ID = EVCRef )\n"
                + " WHERE EVCRef = '" + evcId + "' AND Amount > 0 AND FreeReasonId IS NULL ";
                */

        String query = "Update " + DATABASE_TABLE + "\n" +
                " SET EvcItemDis2 = " +
                " CASE WHEN FreeReasonId IS NOT NULL THEN "+
                " CASE WHEN IFNULL((SELECT F.DisAccType FROM " + DiscountFreeReasonDBAdapter.DATABASE_TABLE + " F WHERE F.FreeReasonId = EVCItemSDS.FreeReasonId  AND (F.CalcPriceType=1 OR PrizeType=1)  ),0) =2 THEN Amount ELSE 0 END "+
                " ELSE CASE WHEN PrizeType=0 THEN (SELECT ROUND((Amount*IFNULL(E.Dis2,0))/100) FROM " + EVCHeaderSDSDBAdapter.DATABASE_TABLE +" E WHERE E._ID = EVCRef) WHEN PrizeType=1 AND " + calctype + "=1 THEN EvcItemDis2 END "+
                " END \n" +
                " WHERE EVCRef = '" + evcId + "' AND Amount > 0 --AND FreeReasonId IS NULL ";
        db.execSQL(query);

    }

    public void updateDis3WithSpecialValues(String evcId)
    {
        int calctype = 0;
        if (GlobalVariables.getPrizeCalcType())
            calctype = 1;
/*
        String query = "Update " + DATABASE_TABLE
                + " Set Discount = (SELECT Discount + CASE WHEN PrizeType=0 THEN CAST((Amount*IFNULL(E.Dis3,0))/100 as integer)  WHEN PrizeType=1 AND " + calctype + "=1 THEN EvcItemDis3 END \n"
                + " FROM "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E WHERE E._ID = EVCRef) \n"
                + " WHERE EVCRef = '" + evcId + "' AND Amount > 0 AND FreeReasonId IS NULL ";
                */
        String query = "Update " + DATABASE_TABLE + "\n" +
                " SET EvcItemDis3 = " +
                " CASE WHEN FreeReasonId IS NOT NULL THEN "+
                " CASE WHEN IFNULL((SELECT F.DisAccType FROM " + DiscountFreeReasonDBAdapter.DATABASE_TABLE + " F WHERE F.FreeReasonId = EVCItemSDS.FreeReasonId  AND (F.CalcPriceType=1 OR PrizeType=1)  ),0) =3 THEN Amount ELSE 0 END "+
                " ELSE CASE WHEN PrizeType=0 THEN (SELECT ROUND((Amount*IFNULL(E.Dis3,0))/100) FROM " + EVCHeaderSDSDBAdapter.DATABASE_TABLE +" E WHERE E._ID = EVCRef) WHEN PrizeType=1 AND " + calctype + "=1 THEN EvcItemDis3 END "+
                " END \n" +
                " WHERE EVCRef = '" + evcId + "' AND Amount > 0 --AND FreeReasonId IS NULL ";
        db.execSQL(query);

    }

    public void updateAdd1WithSpecialValues(String evcId)
    {
        /*
        String query = "Update " + DATABASE_TABLE
                + " Set AddAmount = (SELECT AddAmount + CAST(((Amount - Discount)*IFNULL(E.Add1,0))/100 as integer) \n"
                + " FROM "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E WHERE E._ID = EVCRef) \n"
                + " WHERE EVCRef = '" + evcId + "' AND Amount>0 AND FreeReasonId IS NULL AND PrizeType=0 ";
        */
        String query = "Update " + DATABASE_TABLE +
                " SET EvcItemAdd1 = "+
                " Round(((Amount - (IFNULL(EvcItemDis1, 0) + IFNULL(EvcItemDis2, 0) + IFNULL(EvcItemDis3, 0) + IFNULL(EvcItemOtherDiscount, 0))) * "+
                "        IFNULL((SELECT E.Add1 FROM "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E WHERE E._ID = EVCRef),0))/100) "+
                " WHERE EVCRef = '" + evcId + "' AND Amount>0 AND FreeReasonId IS NULL AND PrizeType=0 ";
        db.execSQL(query);
    }

    public void updateAdd2WithSpecialValues(String evcId)
    {
        /*
        String query = "Update " + DATABASE_TABLE
                + " Set AddAmount = (SELECT AddAmount + CAST(((Amount - Discount)*IFNULL(E.Add2,0))/100 as integer) \n"
                + " FROM "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E WHERE E._ID = EVCRef) \n"
                + " WHERE EVCRef = '" + evcId + "' AND Amount>0 AND FreeReasonId IS NULL AND PrizeType=0 ";
        */
        String query = "Update " + DATABASE_TABLE +
                " SET EvcItemAdd2 = "+
                " Round(((Amount - (IFNULL(EvcItemDis1, 0) + IFNULL(EvcItemDis2, 0) + IFNULL(EvcItemDis3, 0) + IFNULL(EvcItemOtherDiscount, 0))) * "+
                "        IFNULL((SELECT E.Add2 FROM "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E WHERE E._ID = EVCRef),0))/100) "+
                " WHERE EVCRef = '" + evcId + "' AND Amount>0 AND FreeReasonId IS NULL AND PrizeType=0 ";

        db.execSQL(query);
    }


    public void updateAddDiscountWithSpecialValues(String evcId)
    {
        String query = "Update " + DATABASE_TABLE +
                " SET Discount = ifnull(EvcItemDis1, 0) + ifnull(EvcItemDis2, 0) + ifnull(EvcItemDis3, 0) + ifnull(EvcItemOtherDiscount, 0) \n"+
                " , AddAmount = ifnull(EvcItemAdd1, 0) + ifnull(EvcItemAdd2, 0) + ifnull(EvcItemOtherAddition, 0) \n"+
                " WHERE EVCRef = '" + evcId + "' ";

        db.execSQL(query);
    }

    public void updateNetAmountWithSpecialValues(String evcId)
    {
        String query = "UPDATE " + DATABASE_TABLE + " SET AmountNut = Amount - Discount + AddAmount WHERE EVCRef ='" + evcId + "'  "; //AND FreeReasonId IS NULL
        db.execSQL(query);
/*
            query = " UPDATE " + DATABASE_TABLE +
                " SET Discount = CASE WHEN IFNULL((SELECT Fr.CalcPriceType FROM " + DiscountFreeReasonDBAdapter.DATABASE_TABLE + " Fr WHERE "+ DATABASE_TABLE +".FreeReasonId=Fr.FreeReasonId ) ,0) =1 THEN Amount ELSE 0 END " +
                ",AmountNut=0 " +
        " WHERE EVCRef ='" + evcId + "' AND FreeReasonId IS NOT NULL";
        db.execSQL(query);


        String sumq = "IFNULL( " +
                "            (SELECT SUM(CASE WHEN Fr.CalcPriceType=1 AND Fr.DisAccType= %s THEN Discount ELSE 0 END)  \n" +
                "             FROM  EVCItemSDS   EI \n" +
                "             INNER JOIN " + DiscountFreeReasonDBAdapter.DATABASE_TABLE+ " Fr ON EI.FreeReasonId=Fr.FreeReasonId \n" +
                "             WHERE EVCRef ='" + evcId + "' ),0) ";

        query = " UPDATE " + EVCHeaderSDSDBAdapter.DATABASE_TABLE +
                " SET Dis1=IFNULL(Dis1,0)+ "+ String.format(sumq, 1) + " \n"+
            ",Dis2=IFNULL(Dis2,0)+ "+ String.format(sumq, 2) + " \n"+
            ",Dis3=IFNULL(Dis3,0)+ "+ String.format(sumq, 3) + " \n"+
        " WHERE " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + "._id =  '" + evcId + "' " ;
        db.execSQL(query);
*/
    }

    //endregion update with special value

    public void deleteAllEVCItem()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getEvcItems(String evcId)
    {
        Cursor cursor = null;
        if(db != null)
        {
            String sql = "select EI._id, goodsref From " + DATABASE_TABLE + " EI INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E On E.EvcID = EI.EVCRef" +
                    " Where E.EvcID = '" + evcId + "'";
            cursor = db.rawQuery(sql, null);
        }
        return cursor;
    }

    private void updateEvcItemTax(int itemId)
    {
        String sql = "update " + DATABASE_TABLE + "  set tax=IFNULL(((Amount - Discount + AddAmount)* IFNULL(TaxPercent,0)/100),0) " +
                " ,charge = IFNULL(((Amount - Discount + AddAmount)* IFNULL(ChargePercent,0)/100),0) " +
                " where _id=" + itemId + " AND " + DATABASE_TABLE + ".FreeReasonId is Null";
        db.execSQL(sql);

    }

    private void updateAmountByTax()
    {
        String sql = "update " + DATABASE_TABLE + "  set AmountNut= Amount - Discount + AddAmount + charge + tax";
        db.execSQL(sql);
    }

    private Cursor getPaidPrizeIncludedEVCItems(String evcId)
    {
        Cursor cursor = null;

        if(db != null)
        {
            String sql = "select ei.Goodsref, unitref, unitqty, unitcapasity, ei.TotalQty, ei._id,  count(PrizeIncluded) as cnt from " +
                    DATABASE_TABLE +
                    " EI INNER JOIN " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES ON EI._ID= ES.EVCItemRef " +
                    " INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID= ES.DisRef " +
                    " Where EVCRef= '" + evcId +"' and EI.prizetype=0 and PrizeIncluded=1 and " +
                    " EI.goodsref=d.goodsref and d.prizeref=EI.goodsref " +
                    " Group BY ei.Goodsref, unitref, unitqty, unitcapasity, ei.TotalQty ";
            cursor = db.rawQuery(sql, null);
        }
        return cursor;
    }

    private Cursor getPrizeIncludedPrizeItems(String evcId)
    {
        Cursor cursor = null;

        if(db != null)
        {
            String sql = "select ei.Goodsref, unitref, unitqty, unitcapasity, totalQty from " +
                    DATABASE_TABLE + " ei INNER JOIN " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " ES ON ei.disref= ES.disref " +
                    " INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID= ES.DisRef  " +
                    " Where EVCRef= '" + evcId +"' and EI.prizetype=1 " +
                    " and d.prizeref=EI.goodsref " ;
            cursor = db.rawQuery(sql, null);
        }
        return cursor;
    }

    private Cursor getEvcItemData(String evcId, BigDecimal requestDis1, BigDecimal requestDis2, BigDecimal requestDis3, BigDecimal requestAdd1, BigDecimal requestAdd2)
    {
        Cursor c = null;
        if(db != null) {
        /*
            String sql =
                    " SELECT GoodsRef, SmallUnitQty, LargeUnitQty, CustPrice, PriceId, SmallUnitId, ei.UnitRef AS LargeUnitId, PrizeType \n" +
                            " , Discount, ei.Tax, ei.Charge, ei.Amount, ei.AmountNut, ei._id, p.productId, productCode, ProductName, UnitQty, TotalQty \n" +
                            " , UnitCapasity, ei.AddAMount, pu.ProductUnitName \n" +
                            " , (CASE WHEN eh.DisType=4 OR eh.DisType=3 \n" +
                            "         THEN CAST((ei.Amount * IFNULL(" + requestDis1.doubleValue() + " , 0)) / 100 AS INT) \n " +
                            "         ELSE IFNULL((SELECT SUM(e.Discount) FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +
                            "                                             e INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " d ON d.ID=e.DisRef " +
                            "                                             WHERE e.EVCItemref =  ei._id AND d.DisAccRef=1) , 0) \n" +
                            "   END) AS Dis1" +
                            " , (CASE WHEN eh.DisType=4 OR eh.DisType=3 \n " +
                            "         THEN CAST((EI.Amount * IFNULL(" + requestDis2.doubleValue() + ",0) )/100 AS INT) \n " +
                            "         ELSE IFNULL((SELECT SUM(e.Discount) FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +
                            "                                             e INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " d ON d.ID=e.DisRef " +
                            "                                             WHERE e.EVCItemref = ei._id AND d.DisAccRef=2) , 0) \n" +
                            "  END) AS Dis2" +
                            " , (CASE WHEN eh.DisType=4 OR eh.DisType=3 \n " +
                            "         THEN CAST((ei.Amount * IFNULL(" + requestDis3.doubleValue() + ",0) )/100 AS INT) \n" +
                            "         ELSE IFNULL((SELECT SUM(e.Discount) FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +
                            "                                             e INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " d ON d.ID=e.DisRef " +
                            "                                             WHERE e.EVCItemref =  ei._id AND d.DisAccRef=3),0) \n" +
                            "  END) AS Dis3 \n" +
                            " , (CASE WHEN eh.DisType=4 OR eh.DisType=3  \n" +
                            "         THEN CAST(((ei.Amount - ei.Discount) * IFNULL(" + requestAdd1.doubleValue() + ",0) ) /100 AS INT)  \n" +
                            "         ELSE IFNULL((SELECT SUM(e.AddAmount) FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +
                            "                                              e INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " d ON d.ID=e.DisRef \n" +
                            "                                              WHERE e.EVCItemref = ei._id AND d.DisAccRef=4),0)  \n" +
                            "      END) AS Add1 \n" +

                            " , (CASE WHEN eh.DisType = 4 OR eh.DisType = 3  \n" +
                            "      THEN CAST(((ei.Amount - ei.Discount) * IFNULL(" + requestAdd2.doubleValue() + ",0))/100 AS INT)  \n" +
                            "         ELSE IFNULL((SELECT SUM(e.AddAmount) FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +
                            "                                              e INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " d ON d.ID=e.DisRef \n" +
                            "                                              WHERE e.EVCItemref = ei._id AND d.DisAccRef=5),0)  \n" +
                            "      END) AS Add2, DisRef "+
                            " FROM " + DATABASE_TABLE + " ei \n" +
                            " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " eh ON ei.EVCRef = eh.EvcId \n" +
                            " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " p ON ei.GoodsRef = p.ProductId AND evcRef='" + evcId + "' \n" +
                            " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pu ON pu.BackOfficeId = ei.UnitRef AND pu.ProductId = p.productId AND p.IsForSale = 1 \n";
*/

            String sql =
                    " SELECT GoodsRef, SmallUnitQty, LargeUnitQty, CustPrice, PriceId, SmallUnitId, ei.UnitRef AS LargeUnitId, PrizeType \n" +
                            " , Discount, ei.Tax, ei.Charge, ei.Amount, ei.AmountNut, ei._id, p.productId, productCode, ProductName, UnitQty, TotalQty \n" +
                            " , UnitCapasity, ei.AddAMount, pu.ProductUnitName \n" +
                            " , (CASE WHEN (eh.DisType=4 OR eh.DisType=3)AND (Prizetype = 0)  AND(FreeReasonId IS NULL)\n" +
                            "         THEN CAST((ei.Amount * IFNULL(" + requestDis1.doubleValue() + " , 0)) / 100 AS INT) \n " +
                            "         ELSE IFNULL(ei.EvcItemDis1, 0) \n" +
                            "   END) AS Dis1" +
                            " , (CASE WHEN (eh.DisType=4 OR eh.DisType=3)AND (Prizetype = 0)  AND(FreeReasonId IS NULL)\n " +
                            "         THEN CAST((EI.Amount * IFNULL(" + requestDis2.doubleValue() + ",0) )/100 AS INT) \n " +
                            "         ELSE IFNULL(ei.EvcItemDis2, 0) \n" +
                            "  END) AS Dis2" +
                            " , (CASE WHEN (eh.DisType=4 OR eh.DisType=3)AND (Prizetype = 0)  AND(FreeReasonId IS NULL)\n " +
                            "         THEN CAST((ei.Amount * IFNULL(" + requestDis3.doubleValue() + ",0) )/100 AS INT) \n" +
                            "         ELSE IFNULL(ei.EvcItemDis3, 0) \n" +
                            "  END) AS Dis3 \n" +
                            " , ei.EvcItemOtherDiscount AS DisOther " +
                            " , (CASE WHEN (eh.DisType=4 OR eh.DisType=3)AND (Prizetype = 0)   AND(FreeReasonId IS NULL)\n" +
                            "         THEN CAST(((ei.Amount - ei.Discount) * IFNULL(" + requestAdd1.doubleValue() + ",0) ) /100 AS INT)  \n" +
                            "         ELSE IFNULL(ei.EvcItemAdd1, 0) \n" +
                            "      END) AS Add1 \n" +

                            " , (CASE WHEN (eh.DisType = 4 OR eh.DisType = 3)AND (Prizetype = 0)   AND(FreeReasonId IS NULL)\n" +
                            "      THEN CAST(((ei.Amount - ei.Discount) * IFNULL(" + requestAdd2.doubleValue() + ",0))/100 AS INT)  \n" +
                            "      ELSE IFNULL(ei.EvcItemAdd2, 0) \n" +
                            "      END) AS Add2 " +
                            " , ei.EvcItemOtherAddition AS AddOther " +
                            " , DisRef, FreeReasonId "+
                            " FROM " + DATABASE_TABLE + " ei \n" +
                            " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " eh ON ei.EVCRef = eh.EvcId \n" +
                            " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " p ON ei.GoodsRef = p.ProductId AND evcRef='" + evcId + "' \n" +
                            " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pu ON pu.BackOfficeId = ei.UnitRef AND pu.ProductId = p.productId AND p.IsForSale = 1 \n";


            c = db.rawQuery(sql, null);
        }
        return c;
    }


    /*SLE.usp_GetRetExtraValue_CalcPrizeForGoodsGroup*/
    public void GetRetExtraValue_CalcPrizeForGoodsGroup(String saleEVCID , String newSaleEVCID){
        BigDecimal discountPrizeTotalQty;
        BigDecimal prizePackageQty;
        BigDecimal totalRemPrizeQty;
        BigDecimal totalPricedQty;
	    int lastPrizeRef = -1;
        int saleRef;
        int discountRef;
        int evcType;
        int prizeCalcType;
        int disAccRef;
        int prizeQtyFoundStep1 = 0;
        int prizeQtyFoundStep2 = 0;
        BigDecimal remQty;
        int qtyUnit;
        int prizeStep;
        int prizeUnit;
        int prizeRef;
        int packageCount = 0;
        BigDecimal totalPrizeQtyFound;
        BigDecimal goodsRetQty;
        BigDecimal cartonType;
        BigDecimal prizeQty;

//        Cursor c = db.rawQuery("select RefId, EvcType from "+ EVCHeaderSDSDBAdapter.DATABASE_TABLE +"  where EvcId = " + saleEVCID, null);
//        if (c != null && c.moveToFirst()) {
//            saleRef = c.getInt(0);
//            evcType = c.getInt(1);
//        }
//        c.close();
//
//
//        db.execSQL("create table if not exists TempPrizePackageQty (GoodsRef int, Qty REAL, OriginalQty REAL)");
//
//        db.execSQL("");
//
//
//
//        select GoodsRef, sum(TotalQty * case when EvcRef = @SaleEVCID then 1 else -1 end) as Qty
//		, sum(TotalQty * case when EvcRef = @SaleEVCID then 1 else -1 end) as OriginalQty
//        into #RetItem
//        from #tblTempEvcItem with(readPast)
//                where EvcRef in (@SaleEVCID, @NewSaleEVCID)
//        and FreeReasonId is null
//        and PrizeType=0
//        group by GoodsRef
//
//        db.execSQL("delete from RetItem where Qty=0");
//        db.execSQL(
//        "create table if not exists TempPrizePackageQty (GoodsRef int,\n" +
//                "        Qty REAL,\n" +
//                "        PackageQty REAL,\n" +
//                "        CartonType REAL,\n" +
//                "        PrizeStep int)");
//
//        select distinct ds.DisRef, isnull(dc.QtyUnit, 0), dc.PrizeStep, dc.PrizeUnit, dc.PrizeRef, dc.PrizeQty*pk.Qty, dc.DisAccRef
//        from sle.tblDisSale ds
//        inner join sle.tblDiscount dc on 1=1
//        and ds.DisRef=dc.Id
//        and dc.DisType<>300 and ApplyInGroup=1
//        and dc.PrizeType = 1
//        and dc.PrizeStepType=0 -- گام افزايش تعدادي
//        and PrizeRef in (select GoodsRef from #RetItem)
//        and isnull(dc.MinQty, 0) /*+ isnull(dc.MaxQty, 0)*/ = 0
//        and isnull(dc.MinAmount, 0) /*+ isnull(dc.MaxAmount, 0)*/ = 0
//        inner join gnr.tblPackage pk on pk.GoodsRef=dc.PrizeRef and pk.UnitRef=dc.PrizeUnit
//        where HdrRef=@SaleRef
//                and not exists (
//                select 1 from sle.tblDisSale ds2
//        inner join sle.tblDiscount d2 on d2.Id=ds2.DisRef
//        where ds2.HdrRef=@SaleRef
//                and d2.PrizeType=1
//        and d2.PrizeStepType is null
//        and d2.PrizeRef = dc.PrizeRef)
//        order by PrizeRef
//        open c
//
//        fetch next from c into @DiscountRef, @QtyUnit, @PrizeStep, @PrizeUnit, @PrizeRef, @DiscountPrizeTotalQty, @DisAccRef
//        while @@fetch_status = 0 --and (@TotalRemPrizeQty > 0 or @TotalRemPrizeQty is null)
//        begin
//        if (lastPrizeRef==prizeRef && totalRemPrizeQty== BigDecimal.ZERO) {
//            String sql = " INSERT INTO " + EVCSkipDiscountDBAdapter.DATABASE_TABLE + "(SaleRef, DisRef, EvcRef, SkipGoodsRef) " +
//                    " SELECT " + saleId + "," + disAccRef + ",'" + saleEVCID + "', null";
//            db.execSQL(sql);
//        }
//		else {
//            set @LastPrizeRef =@PrizeRef
//            select@PrizePackageQty =@DiscountPrizeTotalQty +
//            case when
//                isnull(PrizeStepUnit, 0) = 0 then d.PrizeStep * g.CartonType
//					else d.PrizeStep
//                    end
//                from sle.tblDiscount d
//                inner join gnr.tblPackage pkp on pkp.GoodsRef = d.PrizeRef and pkp.UnitRef = d.PrizeUnit
//                inner join gnr.tblGoods g on g.Id = d.PrizeRef
//                where d.Id = @DiscountRef
//
//                    select @TotalRemPrizeQty =TotalQty from sle.tblSaleItm where HdrRef = @SaleRef and PrizeType = 1 and GoodsRef = @PrizeRef and IsDeleted = 0
//                select @TotalPricedQty =TotalQty from sle.tblSaleItm where HdrRef = @SaleRef and PrizeType = 0 and GoodsRef = @PrizeRef and IsDeleted = 0
//                select @TotalRemPrizeQty -=isnull(sum(TotalQty), 0)
//                from sle.tblRetSaleItm ri
//                inner join sle.tblRetSaleHdr rh on ri.HdrRef = rh.Id and rh.TSaleRef = @SaleRef and rh.CancelFlag = 0 and ri.PrizeType = 1 and ri.GoodsRef = @PrizeRef
//
//                    /* Starting Step2: calc prize for other goods except PrizeRef */
//                    delete from #TempPrizePackageQty
//                insert into #TempPrizePackageQty(GoodsRef, Qty, PackageQty, CartonType, PrizeStep)
//                select GoodsRef, Qty, PackageQty, CartonType, PrizeStep
//                from
//                        (
//                                select ri.GoodsRef, ri.Qty,
//            case when
//                @QtyUnit=0 then
//            case when
//                ri.GoodsRef = @PrizeRef then @PrizePackageQty else dc.PrizeStep * CartonType end
//						else case when
//                ri.GoodsRef = @PrizeRef then @PrizePackageQty else dc.PrizeStep end
//                end as PackageQty, CartonType, PrizeStep
//                from sle.tblDisSale ds
//                inner join sle.tblSaleItm si on si.HdrRef = ds.HdrRef and
//            case when
//                ds.ItemType in (1, 2)then ds.ItemRef else 0 end = si.RowOrder
//                and not exists(select 1 from sle.tblDisSale ds2 where ds2.HdrRef = ds.HdrRef and ds2.DisGroup = ds.DisGroup and ds2.ItemRef = si.RowOrder and ds.ItemType = 3 and ds2.ItemType < > 3)
//                inner join #RetItem ri on ri.GoodsRef = si.GoodsRef
//                inner join gnr.tblGoods g on g.Id = ri.GoodsRef
//                inner join sle.tblDiscount dc on dc.Id = @DiscountRef
//                    where ds.HdrRef = @SaleRef and ds.DisRef = @DiscountRef
//                    and si.FreeReasonId is null and si.PrizeType = 0 and si.IsDeleted = 0
//                and ds.RowNo = ds.ItemRef
//                and ri.GoodsRef<>@PrizeRef
//                union all
//
//                select ri.GoodsRef, ri.Qty,
//            case when
//                @QtyUnit=0 then
//            case when
//                ri.GoodsRef = @PrizeRef then CartonType else CartonType end
//						else case when
//                ri.GoodsRef = @PrizeRef then @PrizePackageQty else dc.PrizeStep end
//                end as PackageQty, CartonType, PrizeStep
//                from sle.tblDisSale ds
//                inner join sle.tblSaleItm si on si.HdrRef = ds.HdrRef and
//            case when
//                ds.ItemType in (1, 2)then ds.ItemRef else 0 end = si.Id
//                and not exists(select 1 from sle.tblDisSale ds2 where ds2.HdrRef = ds.HdrRef and ds2.DisGroup = ds.DisGroup and ds2.ItemRef = si.Id and ds.ItemType = 3 and ds2.ItemType < > 3)
//                inner join #RetItem ri on ri.GoodsRef = si.GoodsRef
//                inner join gnr.tblGoods g on g.Id = ri.GoodsRef
//                inner join sle.tblDiscount dc on dc.Id = @DiscountRef
//                    where ds.HdrRef = @SaleRef and ds.DisRef = @DiscountRef
//                    and si.FreeReasonId is null and si.PrizeType = 0 and si.IsDeleted = 0
//                and ds.RowNo<> ds.ItemRef
//                and ri.GoodsRef<>@PrizeRef
//			)A
//
//                if @QtyUnit=0
//                begin
//                select @PackageCount =FLOOR((sum(Qty / CartonType)) / 1) from #TempPrizePackageQty
//                select @PackageCount =floor( @PackageCount /@PrizeStep)
//                end
//			else
//                select @PackageCount =sum(Qty) / @PrizeStep from #TempPrizePackageQty
//
//                set @PackageCount =isnull( @PackageCount,0)
//                set @PrizeQtyFoundStep2 =@PackageCount *@DiscountPrizeTotalQty
//
//            update #RetItem
//                set Qty = Qty - @PrizeQtyFoundStep2
//                        where GoodsRef = @PrizeRef
//                    /* Finished Step2: calc prize for other goods except PrizeRef */
//
//                    /* Starting Step1: calc prize for just PrizeRef */
//                    set @PackageCount =0
//                set @RemQty =0
//
//                    -- SDS - 52358
//                if not exists (select 1 from #TempPrizePackageQty)
//                select @PackageCount =ri.Qty / @PrizePackageQty,@RemQty =ri.Qty % @PrizePackageQty
//                    from #RetItem ri
//                where GoodsRef = @PrizeRef and Qty > 0
//			else
//                select @PackageCount =ri.Qty / @PrizePackageQty,@RemQty =ri.Qty % @PrizePackageQty
//                    from #RetItem ri
//                where GoodsRef = @PrizeRef and Qty > 0
//                    -- and exists (select 1 from #TempPrizePackageQty where GoodsRef = @PrizeRef)
//
//                if @RemQty >@DiscountPrizeTotalQty
//            set@PrizeQtyFoundStep1 =@DiscountPrizeTotalQty
//			else
//                set @PrizeQtyFoundStep1 =@RemQty
//
//            select@PrizeQtyFoundStep1 =@PrizeQtyFoundStep1 + @PackageCount *@DiscountPrizeTotalQty,@PackageCount =0
//                update #RetItem
//                set Qty = Qty - @PrizeQtyFoundStep1
//                        where GoodsRef = @PrizeRef and Qty > 0
//                /* Finished Step1: calc prize for other goods except PrizeRef */
//
//                /* Final Control */
//                set @TotalPrizeQtyFound =@PrizeQtyFoundStep1 +@PrizeQtyFoundStep2
//                    select @RemQty=Qty from #RetItem where GoodsRef = @PrizeRef
//                if @RemQty<0 set @RemQty=0
//
//                if @TotalPrizeQtyFound >@TotalRemPrizeQty
//            set@TotalPrizeQtyFound =@TotalRemPrizeQty
//
//            select@RemQty =(OriginalQty - @TotalPrizeQtyFound)from #RetItem where GoodsRef = @PrizeRef
//                if @RemQty >@TotalPricedQty
//            select@TotalPrizeQtyFound +=( @RemQty - @TotalPricedQty)
//
//                --select '@PrizeQtyFound',@PrizeQtyFound,@TotalPricedQty
//                if @TotalPrizeQtyFound >0
//                begin
//                        -- select 'inserting...', @PrizeQtyFound
//                    set @TotalRemPrizeQty =@TotalRemPrizeQty -@TotalPrizeQtyFound
//                    exec sle.usp_DecreaseEVCItemByQty_AddPrize
//                @PrizeRef,@TotalPrizeQtyFound,@SaleRef,0,@SaleEVCID,@EvcType,@DiscountRef,0,@PrizeCalcType,@DisAccRef,@PrizeRef
//            select 'test3232323',*from #tblTempEvcItem
//                update #tblTempEvcItem with (readPast)
//                    set TotalQty = TotalQty + @TotalPrizeQtyFound
//                    where EvcRef = @NewSaleEVCID
//                    and GoodsRef = @PrizeRef
//                    and PrizeType = 0
//                and FreeReasonId is null
//                delete from #tblTempEvcItem with (readPast) where GoodsRef = @PrizeRef and EvcRef = @NewSaleEVCID and PrizeType = 0 and TotalQty<0
//                select 'test353523',*from #tblTempEvcItem
//                    end
//                end
//                fetch next from c into @DiscountRef,@QtyUnit,@PrizeStep,@PrizeUnit,@PrizeRef,@DiscountPrizeTotalQty,@DisAccRef
//
//                if @PrizeRef<>@LastPrizeRef
//            set@TotalRemPrizeQty =null
//
//
//        }
//        close c
//        deallocate c

    }

    public void clearAllData(SQLiteDatabase db) {
        db.delete(DATABASE_TABLE, null, null);
    }

    public String getDisRef(String goodsRef)
    {
        String sql = "SELECT DisRef FROM "+ DATABASE_TABLE + " WHERE GoodsRef = '" + goodsRef +"' AND PrizeType = 1 " ;
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst())
            return c.getString(0);
        else return "0";
    }

    public Cursor getEvcItemDataToSend(String evcId)
    {
        String sql = "select GoodsRef, TotalQty, LargeUnitQty, CustPrice, PriceId, SmallUnitId, LargeUnitId, PrizeType, Discount" +
                ", " + DATABASE_TABLE + ".Tax, " + DATABASE_TABLE + ".Charge, Amount, AmountNut, _id, productId, productCode, ProductName" +
                ", UnitQty, UnitCapasity, " + DATABASE_TABLE + ".AddAMount, " +
                DATABASE_TABLE + "." + KEY_DIS_1 +"," + DATABASE_TABLE + "." + KEY_DIS_2 +"," + DATABASE_TABLE + "." + KEY_DIS_3 +"," + DATABASE_TABLE + "." + KEY_ADD_1 + "," + DATABASE_TABLE + "." + KEY_ADD_2 + " " +
                "from " + DATABASE_TABLE +
                ", Product where " + DATABASE_TABLE  + ".GoodsRef = Product.ProductId and evcRef='" + evcId + "' and Product.IsForSale = '1'";
        return db.rawQuery(sql, null);
    }

    public void updateEVCitemFromDecreasePrize(String evcId){
        String query = " UPDATE " + DATABASE_TABLE + " \n" +
                "SET TotalQty = TotalQty + IFNULL((Select  dp.DecreasePrizeQty \n" +
                " FROM " + EvcTempDecreasePrizeSDSDBAdapter.DATABASE_TABLE +" dp \n" +
                " WHERE dp.GoodsRef=EVCItemSDS.GoodsRef),0)\n" +
                "WHERE EVCItemSDS.EVCRef='"+ evcId +"' AND EVCItemSDS.PrizeType=1";
        db.execSQL(query);

    }
    public void updateEVCitemTotalQty1(String saleEVCId, String newSaleEVCId){
        String query = "Update " +DATABASE_TABLE + " \n" +
                " Set TotalQty=TotalQty +\n" +
                " IFNULL((SELECT TotalQty FROM EVCItemSDS I WHERE EVCRef= '"+ saleEVCId +"' AND I.GoodsRef = EVCItemSDS.GoodsRef AND PrizeType=1 and RowOrder <> _Id) \n" +
                "    - IFNULL((SELECT TotalQty FROM EVCItemSDS I WHERE EVCRef= '"+newSaleEVCId+"' AND I.GoodsRef=EVCItemSDS.GoodsRef AND PrizeType=1 and RowOrder<> _Id),0),0) \n" +
                " Where EVCRef= '"+newSaleEVCId+"' AND PrizeType=0 \n" +
                " AND GoodsRef IN (SELECT GoodsRef FROM EVCItemSDS WHERE EVCRef= '"+ saleEVCId +"' AND PrizeType=1 and RowOrder<> _Id) \n" +
                " AND NOT EXISTS (SELECT 1 FROM DiscountProduct where CartonPrizeQty<>0 and ProductId = GoodsRef) "+
                " AND (SELECT TotalQty FROM EVCItemSDS I WHERE EVCRef= '" + saleEVCId+ "' AND I.GoodsRef=EVCItemSDS.GoodsRef AND PrizeType=1 and RowOrder<> _Id)> "+
                " IFNULL((SELECT TotalQty FROM EVCItemSDS I WHERE EVCRef= '" + newSaleEVCId + "' AND I.GoodsRef=EVCItemSDS.GoodsRef AND PrizeType=1 and RowOrder<> _Id),0) "+
                " AND FreeReasonId is null ";
        db.execSQL(query);
    }

    public void updateEVCitemTotalQty2(String saleEVCId, String newSaleEVCId){
        boolean exists = false;
        String sql =
                " SELECT 1"+
                        " FROM " + DATABASE_TABLE + " e1  INNER JOIN " +
                        DATABASE_TABLE + " e2 on e1.GoodsRef=e2.GoodsRef "+
                        " and e1.EVCRef= '" + saleEVCId +"' and e2.EVCRef= '" + newSaleEVCId +"'"+
                        " and e1.PrizeType=0 and e2.PrizeType=0 " +
                        " and ifnull(e1.FreeReasonId, 0) = ifnull(e2.FreeReasonId, 0) " +
                        " WHERE e2.TotalQty>e1.TotalQty";

        Cursor affectedCursor = db.rawQuery(sql, null);
        if (affectedCursor != null && affectedCursor.moveToFirst()) {
            if (affectedCursor.getInt(0) == 1) {
                exists = true;
            }
        }
        if (exists ){
            String query = "UPDATE "+ DATABASE_TABLE +"\n" +
                    "SET TotalQty= IFNULL((SELECT e1.TotalQty \n" +
                    "FROM " + DATABASE_TABLE + " e1 \n" +
                    "where e1.GoodsRef = EVCItemSDS.GoodsRef \n" +
                    "AND e1.EVCRef='"+saleEVCId+"' and EVCItemSDS.EVCRef='"+ newSaleEVCId +"' \n" +
                    "AND e1.PrizeType=0 and EVCItemSDS.PrizeType=0 \n" +
                    "AND IFNULL(e1.FreeReasonId, 0) = IFNULL(EVCItemSDS.FreeReasonId, 0) \n" +
                    "AND EVCItemSDS.TotalQty>e1.TotalQty ), TotalQty)" ;
            db.execSQL(query);
        }
    }

    public void DeletePrize(String evcId) {
        String query = "DELETE FROM "+ DATABASE_TABLE +"\n" +
                "WHERE EVCRef='"+evcId+"' and Prizetype=1";
        db.execSQL(query);
    }
    public void UpdateToZero(String evcId) {
        String query = "Update  "+ DATABASE_TABLE +"\n" +
                "SET AmountNut=Amount ,\n" +
                "Discount=0,\n" +
                "AddAmount=0,\n" +
                "EvcItemDis1=0,\n" +
                "EvcItemDis2=0,\n" +
                "EvcItemDis3=0,\n" +
                "EvcItemAdd1=0,\n" +
                "EvcItemAdd2=0,\n" +
                "EvcItemOtherDiscount=0,\n" +
                "EvcItemOtherAddition=0\n" +
                "WHERE EVCRef='"+evcId+"' ";
        db.execSQL(query);
    }


    public void updateByRemPrize(String saleEVCID){
        String query ="UPDATE EVCItemSDS\n" +
                " SET TotalQty= IFNULL((SELECT rm.RemPrizeQty\n" +
                "   FROM  EVCTempRemPrizeSDS rm\n" +
                "   WHERE rm.GoodsRef=EVCItemSDS.GoodsRef and EVCItemSDS.EvcRef= '" + saleEVCID + "' and EVCItemSDS.PrizeType=1\n" +
                "   AND EVCItemSDS.TotalQty>rm.RemPrizeQty),TotalQty)";
        db.execSQL(query);

    }

    public void updateByOrderPrize(){
        //Add in tablet
        String query ="Update " + DATABASE_TABLE +"\n" +
                "SET UnitQty =IFNULL((\n" +
                "SELECT prize.PrizeQty\n" +
                "FROM  " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +" item\n" +
                "JOIN  " + DiscountEvcPrizeDBAdapter.DATABASE_TABLE + " prize ON prize.DiscountRef = item.DisRef\n" +
                "JOIN  " + DiscountDBAdapter.DATABASE_TABLE+ " dis ON item.DisRef = dis.id\n" +
                "AND   PrizeType = 6\n" +
                "WHERE EVCItemSDS.GoodsRef = prize.GoodsRef AND dis.Id = EVCItemSDS.DisRef),UnitQty)";
        db.execSQL(query);
    }

    public void fillTempRetItm(DiscountCallReturnData discountCallReturnData ){
        db.execSQL("DROP TABLE IF EXISTS tempRetItm");
        db.execSQL( "CREATE TABLE tempRetItm (\n" +
                "GoodsRef  INTEGER,\n" +
                "FreeReasonId  INTEGER,\n" +
                "NewRetQty  INTEGER,\n" +
                "PreviousRetQty  INTEGER,\n" +
                "TotalRetQty  INTEGER\n" +
                ");");
       /* db.execSQL(
                "INSERT INTO tempRetItm (GoodsRef,FreeReasonId,NewRetQty,PreviousRetQty,TotalRetQty)\n" +
                "SELECT GoodsRef, ifnull(FreeReasonId, 0) as FreeReasonId\n" +
                ", sum(TotalQty * case when EvcRef = '" +saleEVCID+ "' then 1 else -1 end) as NewRetQty\n" +
                ", ifnull((SELECT sum(PreviousRetQty) \n" +
                "          FROM DiscountCustomerOldInvoiceDetail\n" +
                "          WHERE GoodsRef=ei.GoodsRef \n" +
                "          AND SaleId = " + saleId +"\n" +
                "          AND FreeReasonId=ifnull(FreeReasonId, 0)), 0) as PreviousRetQty\n" +
                ", 0 as TotalRetQty\n" +
                " FROM EVCItemSDS ei \n" +
                " WHERE EvcRef in ('"+saleEVCID+"','"+ newSaleEVCID+"')\n" +
                " GROUP BY GoodsRef, FreeReasonId");
        */
        for(DiscountCallReturnLineData lineData : discountCallReturnData.callReturnLineItemData.values())
        {
            if(lineData.returnTotalQty.compareTo(BigDecimal.ZERO) == 1)
            {
                db.execSQL(
                        "INSERT INTO tempRetItm (GoodsRef,FreeReasonId,NewRetQty,PreviousRetQty,TotalRetQty)\n" +
                        "SELECT " + lineData.productId + " as GoodsRef, " +
                        " 0  as FreeReasonId\n" +
                        ","+ lineData.returnTotalQty +" as NewRetQty\n" +
                        ", ifnull((SELECT sum(PreviousRetQty) \n" +
                        "          FROM DiscountCustomerOldInvoiceDetail\n" +
                        "          WHERE productId =" + lineData.productId +" \n" +
                        "          AND SaleId = " + discountCallReturnData.returnRefId +"\n" +
                        "          AND FreeReasonId=ifnull(FreeReasonId, 0)), 0) as PreviousRetQty\n" +
                        ", 0 as TotalRetQty\n" );
            }
        }

        db.execSQL("delete from tempRetItm where NewRetQty=0");
        db.execSQL("update tempRetItm set TotalRetQty=PreviousRetQty+NewRetQty");
    }


    public void fillRetQtySplit(){
        db.execSQL("DROP TABLE IF EXISTS tempRetQtySplit");
        db.execSQL( "CREATE TABLE tempRetQtySplit (\n" +
                "GoodsRef  INTEGER,\n" +
                "FreeReasonId  INTEGER,\n" +
                "RetQty  INTEGER,\n" +
                "TotalQty  INTEGER,\n" +
                "PrizeQty  INTEGER" +
                ");");
        db.execSQL("INSERT INTO tempRetQtySplit (GoodsRef,FreeReasonId,RetQty,TotalQty,PrizeQty)" +
                "SELECT ri.GoodsRef, ri.FreeReasonId, ri.NewRetQty as RetQty, \n" +
                "       TotalRetQty-cast((1.0*ri.TotalRetQty/SaleSumQty*SalePrizeQty) as int) as TotalQty, \n" +
                "       cast((1.0*ri.TotalRetQty/SaleSumQty*SalePrizeQty) as int) as PrizeQty\n" +
                "FROM tempRetItm ri\n" +
                "INNER JOIN tempSaleDetail sd on sd.GoodsRef=ri.GoodsRef and sd.FreeReasonId=ri.FreeReasonId;");
    }


}
	 
