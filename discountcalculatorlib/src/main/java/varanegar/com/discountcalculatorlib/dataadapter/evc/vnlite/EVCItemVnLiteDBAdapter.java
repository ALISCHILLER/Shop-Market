package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import android.content.ContentValues;
import android.database.Cursor;

import java.math.BigDecimal;
import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductTaxInfoDBAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProduct;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProductTaxInfo;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.viewmodel.CallOrderLineItemStatusData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;


public class EVCItemVnLiteDBAdapter extends DiscountBaseDataAdapter {

    public static final String KEY_EVC_ITEM_ID = "_id";
    public static final String KEY_ROW_ORDER = "RowOrder";
    public static final String KEY_PRODUCT_ID = "GoodsRef";
    public static final String KEY_UNIT_QTY = "UnitQty";
    public static final String KEY_UNIT_CAPACITY = "UnitCapasity";
    public static final String KEY_TOTAL_QTY = "TotalQty";
    public static final String KEY_AMOUNT_NUT = "AmountNut";
    public static final String KEY_DISCOUNT = "Discount";
    public static final String KEY_AMOUNT = "Amount";
    public static final String KEY_PRIZE_TYPE = "PrizeType";
    public static final String KEY_SUP_AMOUNT = "SupAmount";
    public static final String KEY_ADD_AMOUNT = "AddAmount";
    public static final String KEY_CUST_PRICE = "CustPrice";
    public static final String KEY_USER_PRICE = "UserPrice";
    public static final String KEY_CHARGE_PERCENT = "ChargePercent";
    public static final String KEY_TAX_PERCENT = "TaxPercent";
    public static final String KEY_EVC_ID = "EVCRef";
    public static final String KEY_CALL_ID = "CallId";
    public static final String KEY_TOTAL_WEIGHT = "TotalWeight";
    public static final String KEY_UNIT_REF = "UnitRef";
    public static final String KEY_DIS_REF = "DisRef";
    public static final String KEY_DIS_TAX_AMOUNT = "Tax";
    public static final String KEY_CHARGE_AMOUNT = "Charge";
    public static final String KEY_PRICE_REF = "PriceId";
    public static final String KEY_PACK_QTY = "PackQty";
    public static final String KEY_BATCH_REF = "BatchId";
    public static final String KEY_REDUCE_QTY = "ReduceOfQty";

    public static final String KEY_Add1 = "EvcItemAdd1";
    public static final String KEY_Add2 = "EvcItemAdd2";
    public static final String KEY_Dis1 = "EvcItemDis1";
    public static final String KEY_Dis2 = "EvcItemDis2";
    public static final String KEY_Dis3 = "EvcItemDis3";
    public final String KEY_ORDERLINE_ID = "OrderLineId";

    public static final String DATABASE_TABLE = "EVCItemVnLite";

    private static EVCItemVnLiteDBAdapter instance;

    public EVCItemVnLiteDBAdapter() {
    }

    public static EVCItemVnLiteDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCItemVnLiteDBAdapter();
        }

        return instance;

    }


    public void updateProductPriceOnEVCItem(DiscountCallOrderData callData, String evcId) {

        Cursor c = null;

        try {

            String sql = "SELECT _id, GoodsRef FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " where EVCRef='" + evcId + "'";
            c = db.rawQuery(sql, null);
            if (c != null && c.moveToFirst()) {
                do {
                    for (DiscountCallOrderLineData cld : callData.callOrderLineItemData) {
                        if (c.getString(1).equals(cld.productId)) {
                            String sql2 = "update " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " set priceid='" + cld.priceId + "' where _id=" + c.getInt(0);
                            db.execSQL(sql2);
                            break;
                        }
                    }
                } while (c.moveToNext());
            }

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (c != null)
                c.close();
        }
    }

    public DiscountCallOrderData fillCallLineWithPromo(String evcId, DiscountCallOrderData callData)
    {

        callData.callLineItemDataWithPromo = new ArrayList<>();

        Cursor c = getEvcItemData(evcId);

        int productTypeId ;

        if (c != null && c.moveToFirst()) {
            do {
                DiscountCallOrderLineData tempData = new DiscountCallOrderLineData();
                tempData.totalInvoiceCharge = new BigDecimal(0);
                tempData.totalInvoiceTax = new BigDecimal(0);

                tempData.callUniqueId = callData.callUniqueId;
                tempData.isFreeItem = c.getInt(c.getColumnIndex("PrizeType"));
                tempData.isRequestPrizeItem = c.getInt(c.getColumnIndex("PrizeType")) == 1;

                tempData.orderUniqueId = callData.orderUniqueId;
                tempData.evcId = evcId;

                tempData.invoiceSmallQtyId = c.getInt(c.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_SMALLUNITID));
                tempData.invoiceBigQtyId = c.getInt(c.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_LARGEUNITID));

                tempData.invoiceTotalQty = new BigDecimal(c.getDouble(c.getColumnIndex(KEY_TOTAL_QTY)));

                tempData.customerId = callData.customerId;
                tempData.unitPrice = new BigDecimal(c.getDouble(c.getColumnIndex("CustPrice")));
                tempData.priceId = c.getInt(c.getColumnIndex("PriceId"));
                tempData.productId = c.getInt(c.getColumnIndex("productId"));
                //tempData.totalInvoiceTax = new BigDecimal(c.getDouble(9) + c.getDouble(19));
                tempData.productCode = c.getString(c.getColumnIndex("ProductCode"));
                tempData.productName = c.getString(c.getColumnIndex("ProductName"));
                tempData.disRef =c.getInt(c.getColumnIndex("DisRef"));

                productTypeId = c.getInt(c.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_TYPE_ID));
                if(productTypeId == 2) //bulk
                {
                    tempData.invoiceBigQty = new BigDecimal(c.getDouble(c.getColumnIndex(KEY_PACK_QTY)));
                    tempData.invoiceSmallQty = tempData.invoiceTotalQty;
                    tempData.invoiceBulkQty = tempData.invoiceTotalQty;
                }
                else
                {
                    tempData.invoiceSmallQty = new BigDecimal(c.getDouble(c.getColumnIndex(KEY_UNIT_QTY)));

                    DiscountProduct product = DiscountProductDBAdapter.getInstance().getProductByCode(tempData.productCode);
                    if(tempData.invoiceTotalQty.compareTo(BigDecimal.ZERO) != 0)
                    {
                        if (product.largeUnitQty != null && product.largeUnitQty.compareTo(BigDecimal.ZERO) > 0)
                            tempData.invoiceBigQty = tempData.invoiceTotalQty.divide(product.largeUnitQty, 0, BigDecimal.ROUND_DOWN);
                        else
                            tempData.invoiceBigQty = BigDecimal.ZERO;
                        tempData.invoiceSmallQty = tempData.invoiceTotalQty.subtract(tempData.invoiceBigQty.multiply(product.largeUnitQty));
                    }

                    tempData.invoiceBulkQty = BigDecimal.ZERO;
                }

                tempData.totalInvoiceAdd2Amount = new BigDecimal(0);
                if (callData.disTypeId == 2) { // آزاد
                    BigDecimal add1 = new BigDecimal(c.getDouble(c.getColumnIndex(KEY_Add1)));
                    if (add1.compareTo(BigDecimal.ZERO) == 0)
                        tempData.totalInvoiceAdd1Amount = BigDecimal.ZERO;
                    else { // calc percent
                        //TODO ?? Mr Ahmadi: calc percent or using same
                        tempData.totalInvoiceAdd1Amount = add1;
                    }
                }
                else { // تخفیف جوایز
                    tempData.totalInvoiceTax = new BigDecimal(c.getColumnIndex("Tax"));
                    tempData.totalInvoiceCharge = new BigDecimal(c.getColumnIndex("Charge"));
                    tempData.totalInvoiceAdd1Amount = new BigDecimal(c.getDouble(c.getColumnIndex("AddAmount")));
                    tempData.totalInvoiceDiscount = new BigDecimal(c.getDouble(c.getColumnIndex("Discount")));

                }

                if (tempData.totalInvoiceDiscount == null)
                    tempData.totalInvoiceDiscount = BigDecimal.ZERO;

                tempData.totalInvoiceNetAmount = tempData.totalInvoiceAmount = tempData.totalInvoiceTax = tempData.totalInvoiceCharge = BigDecimal.ZERO;

                if (tempData.isFreeItem == 0) {
                    tempData.totalInvoiceAmount = tempData.invoiceTotalQty.multiply(tempData.unitPrice).setScale(0, BigDecimal.ROUND_HALF_UP);

                    tempData.totalInvoiceNetAmount = tempData.totalInvoiceAmount.subtract(tempData.totalInvoiceDiscount).add(tempData.totalInvoiceAdd1Amount).add(tempData.totalInvoiceAdd2Amount);

                    DiscountProductTaxInfo taxInfo = DiscountProductTaxInfoDBAdapter.getInstance().getProductTaxInfo(tempData.productId);
                    if (taxInfo != null) {
                        BigDecimal tax = tempData.totalInvoiceTax.add(tempData.totalInvoiceNetAmount.multiply(taxInfo.taxPercent).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP));
                        BigDecimal charge = tempData.totalInvoiceCharge.add(tempData.totalInvoiceNetAmount.multiply(taxInfo.chargePerent).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP) );
                        tempData.totalInvoiceTax = tax;
                        tempData.totalInvoiceCharge = charge;
                    } else {
                        tempData.totalInvoiceTax = BigDecimal.ZERO;
                        tempData.totalInvoiceCharge = BigDecimal.ZERO;

                    }

                }

                tempData.totalInvoiceNetAmount = tempData.totalInvoiceAmount.add(tempData.totalInvoiceCharge)
                        .add(tempData.totalInvoiceTax).subtract(tempData.totalInvoiceDiscount)
                        .add(tempData.totalInvoiceAdd1Amount).add(tempData.totalInvoiceAdd2Amount);

                DiscountCallOrderLineData baseData = null;

                for (DiscountCallOrderLineData lineData : callData.callOrderLineItemData) {
                    if (lineData.productId == tempData.productId && lineData.isRequestPrizeItem == tempData.isRequestPrizeItem) {
                        baseData = lineData;
                        break;
                    }
                }
                tempData.orderLineId = baseData != null ? baseData.orderLineId : null;
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
                tempData.totalRequestDiscount = baseData != null ? baseData.totalRequestDiscount : BigDecimal.ZERO;
                tempData.totalRequestTax = baseData != null ? baseData.totalRequestTax : BigDecimal.ZERO;
                tempData.totalRequestNetAmount = baseData != null ? baseData.totalRequestNetAmount : BigDecimal.ZERO;

                ArrayList<CallOrderLineItemStatusData> statusDataList = new ArrayList<>();

                Cursor cEVCItemStatuteInfo = null;
                if (!tempData.evcId.trim().equals("")) {
                    cEVCItemStatuteInfo = EVCItemStatutesVnLiteDBAdapter.getInstance().getEvcItemStatuteDataToSend(evcId);

                    if (cEVCItemStatuteInfo != null && cEVCItemStatuteInfo.moveToFirst()) {
                        do {
                            CallOrderLineItemStatusData statusData = new CallOrderLineItemStatusData();

                            statusData.setEvcId(evcId);
                            statusData.setProductId(tempData.productId);
                            statusData.setRowOrder(cEVCItemStatuteInfo.getInt(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesVnLiteDBAdapter.KEY_EVC_ITEM_STATUTES_ROW_ORDER)));
                            statusData.setDiscountId(cEVCItemStatuteInfo.getInt(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesVnLiteDBAdapter.KEY_EVC_ITEM_STATUTES_DIS_REF)));
                            statusData.setDisGroup(cEVCItemStatuteInfo.getInt(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesVnLiteDBAdapter.KEY_EVC_ITEM_STATUTES_DIS_GROUP)));
                            statusData.setDiscount(new BigDecimal(cEVCItemStatuteInfo.getDouble(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesVnLiteDBAdapter.KEY_EVC_ITEM_STATUTES_DISCOUNT))));
                            statusData.setAddAmount(new BigDecimal(cEVCItemStatuteInfo.getDouble(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesVnLiteDBAdapter.KEY_EVC_ITEM_STATUTES_ADD_AMOUNT))));
                            statusData.setSupAmount(new BigDecimal(cEVCItemStatuteInfo.getDouble(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesVnLiteDBAdapter.KEY_EVC_ITEM_STATUTES_SUP_AMOUNT))));
                            statusData.setProductId(cEVCItemStatuteInfo.getInt(cEVCItemStatuteInfo.getColumnIndex(EVCItemStatutesVnLiteDBAdapter.KEY_EVC_ITEM_STATUTES_PRODUCT_ID)));
                            statusDataList.add(statusData);
                        } while (cEVCItemStatuteInfo.moveToNext());
                    }
                }

                tempData.callOrderLineItemStatusDatas = statusDataList;

                callData.callLineItemDataWithPromo.add(tempData);
            } while (c.moveToNext());
        }

        return callData;
    }

    public void deleteAllEVCItemsById(String evcId) {
        db.delete(DATABASE_TABLE, "evcRef='" + evcId + "'", null);
    }

    public void deletePrize(String evcId) {
        db.delete(DATABASE_TABLE, "evcRef='" + evcId + "' AND PrizeType = 1", null);
    }

    public long saveEVCItem(DiscountCallOrderLineData lineData, String evcId) {

        ContentValues values = new ContentValues();

        BigDecimal qty , smallQty;
        DiscountProduct product = DiscountProductDBAdapter.getInstance().getProductById(lineData.productId);

        if(product.isBulk()) {
            qty = lineData.invoiceTotalQty;
            smallQty = qty;
        }
        else {
            qty = lineData.invoiceTotalQty;
            smallQty = lineData.invoiceSmallQty;
        }

        values.put(KEY_ROW_ORDER, lineData.sortId);
        values.put(KEY_ORDERLINE_ID, lineData.orderLineId.toString());
        values.put(KEY_PRODUCT_ID, lineData.productId);

        values.put(KEY_PACK_QTY, lineData.invoiceBigQty.doubleValue());
        values.put(KEY_UNIT_QTY, smallQty.doubleValue());
        values.put(KEY_TOTAL_QTY, qty.doubleValue());

        values.put(KEY_UNIT_CAPACITY, 1);
        if (lineData.weight != null)
            values.put(KEY_TOTAL_WEIGHT, qty.doubleValue() * lineData.weight.doubleValue());

        values.put(KEY_DISCOUNT, 0);

        if (GlobalVariables.getDecimalDigits() > 0) {
            values.put(KEY_AMOUNT_NUT, Math.round(Math.round((lineData.unitPrice.doubleValue() * lineData.cartonType) * 1.0) * (qty.doubleValue() / (lineData.cartonType * 1.0))));
            values.put(KEY_AMOUNT, Math.round(Math.round((lineData.unitPrice.doubleValue() * lineData.cartonType) * 1.0) * (qty.doubleValue() / (lineData.cartonType * 1.0))));
        } else {
            values.put(KEY_AMOUNT_NUT, lineData.unitPrice.doubleValue() * qty.doubleValue());
            values.put(KEY_AMOUNT, lineData.unitPrice.doubleValue() * qty.doubleValue());
        }

        values.put(KEY_PRIZE_TYPE, lineData.isRequestPrizeItem);
        values.put(KEY_SUP_AMOUNT, 0);
        values.put(KEY_ADD_AMOUNT, 0);
        values.put(KEY_PRICE_REF, lineData.priceId);
        values.put(KEY_BATCH_REF, ""/*lineData.batchId*/);
        values.put(KEY_CUST_PRICE, lineData.unitPrice.doubleValue());
        values.put(KEY_USER_PRICE, lineData.userprice.doubleValue());
        values.put(KEY_CHARGE_PERCENT, lineData.totalInvoiceCharge.doubleValue());
        values.put(KEY_TAX_PERCENT, lineData.totalInvoiceTax.doubleValue());
        values.put(KEY_EVC_ID, evcId);
        values.put(KEY_CALL_ID, lineData.callUniqueId);
        values.put(KEY_UNIT_REF, lineData.invoiceSmallQtyId);

        values.put(KEY_Add1, lineData.totalInvoiceAdd1Amount.doubleValue());
        values.put(KEY_Add2, lineData.totalInvoiceAdd2Amount.doubleValue());

        long id = db.insert(DATABASE_TABLE, null, values);

        return id;
    }

    public void saveEVCItem(DiscountCallReturnData headerData, String evcId1, String evcId2) {

        String sql = "INSERT INTO " + DATABASE_TABLE
                + " (EVCRef, CallId, RowOrder, GoodsRef, UnitQty,PackQty, UnitCapasity, TotalQty, AmountNut, Discount, Amount, PrizeType,  AddAmount, CustPrice, UserPrice \n"
                + " ,  PriceId, charge, tax, chargePercent, taxPercent, CPriceRef) \n"
                + " SELECT '%S' , '" + headerData.callUniqueId + "' , RowOrder, ProductId, TotalQty, 0, UnitCapasity, TotalQty, AmountNut, Discount, Amount, PrizeType \n"
                + " , AddAmount, CustPrice, UserPrice,   PriceId, charge, tax \n"
                + " , IFNULL( CASE (Amount - Discount + AddAmount) WHEN 0 THEN 0 ELSE (((charge * 1.0) / (Amount - Discount + AddAmount)) * 100) END, 0 ) AS chargePercent \n"
                + " , IFNULL( CASE (Amount - Discount + AddAmount) WHEN 0 THEN 0 ELSE (((tax * 1.0) / (Amount - Discount + AddAmount)) * 100) END,0) AS taxPercent \n"
                + " , CPriceRef \n"
                + " FROM " + DiscountCustomerOldInvoiceDetailDBAdapter.DATABASE_TABLE
                + " WHERE SaleId = '" + headerData.returnRefId + "' AND PrizeType = 0";

            /*PromotionDetailId,DetailId*/

        //Main
        db.execSQL(String.format(sql, evcId1));

        //Clone
        db.execSQL(String.format(sql, evcId2));

    }

    /*dbo.usp_CalChargeAndTax*/
    public void calcChargeTax(String evcId) {
        Cursor c = getEvcItems(evcId);
        if (c != null && c.moveToFirst()) {
            do {
                updateEvcItemTax(c.getInt(0));
            } while (c.moveToNext());
            updateAmountByTax();
        }
    }

    public void fillByEVCItemPreview(String evcId, String callId, int productId, BigDecimal qty ,BigDecimal unitPrice,
                                     int promotionDetailId, int priceId) {
        String sql = "INSERT INTO " + DATABASE_TABLE + "(GoodsRef, EVCRef, AmountNut, " +
                " TotalQty, PrizeType, Discount, Amount, AddAmount, UserPrice, DisRef, PriceId, CallId) " +
                " VALUES(" + productId + ", '"+ evcId+ "', 0, "+
                qty+", 1, 0, 0,  0,  "+ unitPrice+", "+ promotionDetailId+","+priceId+", '"+callId+"')";

        db.execSQL(sql);
    }

    public void fillByEVCItemPreviewOld(String evcId, String callId) {
        String sql = "";
        sql = "insert into " + DATABASE_TABLE + "(GoodsRef, EVCRef, AmountNut, UnitCapasity, UnitQty, TotalQty, PrizeType, " +
                " Discount, Amount, AddAmount, UserPrice, DisRef, CallId) " +
                "	SELECT ProductId, EVCId, 0 AS AmountNut, 1 AS UnitCapasity, Qty AS UnitQty, Qty AS TotalQty, " +
                "		1 AS PrizeType, 0 AS Discount, 0 AS Amount, 0 AS AddAmount, 0 AS UserPrice, DisId, '" + callId + "' " +
                " 		FROM " + EVCTempSummaryFinalVnLiteDBAdapter.DATABASE_TABLE + " where EvcId = '" + evcId + "' ";
        db.execSQL(sql);
    }

    public void updateReduceOfQtyOld(String evcId) {
        String sql = "";
        sql = "UPDATE " + DATABASE_TABLE +
                " SET TotalQty = TotalQty -  " +
                "                (SELECT es.Qty  " +
                "                  FROM " + EVCTempSummaryFinalVnLiteDBAdapter.DATABASE_TABLE + " es INNER JOIN " + DATABASE_TABLE + " ei ON es.ProductId = ei.GoodsRef " +
                "                  AND es.EVCId = '" + evcId + "' AND es.ReduceOfQty =1)" +
                "   WHERE EVCRef = '" + evcId + "' AND PrizeType = 0 " +
                "   AND GoodsRef =  " +
                "           (SELECT es.ProductId " +
                "            FROM " + EVCTempSummaryFinalVnLiteDBAdapter.DATABASE_TABLE + " es INNER JOIN " + DATABASE_TABLE + " ei ON es.ProductId = ei.GoodsRef" +
                "            AND es.EVCId = '" + evcId + "' AND es.ReduceOfQty =1) " +
                "   AND TotalQty > " +
                "           (SELECT es.Qty " +
                "            FROM " + EVCTempSummaryFinalVnLiteDBAdapter.DATABASE_TABLE + " es INNER JOIN " + DATABASE_TABLE + " ei ON es.ProductId = ei.GoodsRef" +
                "            AND es.EVCId = '" + evcId + "' AND es.ReduceOfQty = 1)";
        db.execSQL(sql);
    }

    public void updateReduceOfQty(String evcId, BigDecimal qty) {
        String sql = "";
        sql = "UPDATE " + DATABASE_TABLE +
                " SET TotalQty = TotalQty -  " + qty +

                "                (SELECT es.Qty  " +
                "                  FROM " + EVCTempSummaryFinalVnLiteDBAdapter.DATABASE_TABLE + " es INNER JOIN " + DATABASE_TABLE + " ei ON es.ProductId = ei.GoodsRef " +
                "                  AND es.EVCId = '" + evcId + "' AND es.ReduceOfQty =1)" +
                "   WHERE EVCRef = '" + evcId + "' AND PrizeType = 0 " +
                "   AND GoodsRef =  " +
                "           (SELECT es.ProductId " +
                "            FROM " + EVCTempSummaryFinalVnLiteDBAdapter.DATABASE_TABLE + " es INNER JOIN " + DATABASE_TABLE + " ei ON es.ProductId = ei.GoodsRef" +
                "            AND es.EVCId = '" + evcId + "' AND es.ReduceOfQty =1) " +
                "   AND TotalQty > " +
                "           (SELECT es.Qty " +
                "            FROM " + EVCTempSummaryFinalVnLiteDBAdapter.DATABASE_TABLE + " es INNER JOIN " + DATABASE_TABLE + " ei ON es.ProductId = ei.GoodsRef" +
                "            AND es.EVCId = '" + evcId + "' AND es.ReduceOfQty = 1)";
        db.execSQL(sql);
    }

    public void updateReduceOfPrice(String evcId) {
        String sql = "";
        sql = "UPDATE " + DATABASE_TABLE +
                " SET Amount = totalqty * custPrice " +
                " ,   AmountNut = totalqty * custPrice - Discount + AddAmount" +
                " WHERE EVCRef='" + evcId + "'";
        db.execSQL(sql);
    }

    public void deleteInvalidPrizeItems(String evcId) {

        String sql = " DELETE FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                " WHERE _ID IN (SELECT EI._ID " +
                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EvcID = EI.EVCRef " +
                " INNER JOIN " + DiscountCustomerDBAdapter.DATABASE_TABLE + " C ON C.CustomerId = E.CustRef" +
                " WHERE EI.PrizeType='1' AND E.EvcID='" + evcId + "' " +
                " 	and EI.UnitQty=0 )";
        db.execSQL(sql);
    }

    public void updatePrizeIncludedItems(String evcId) {

        Cursor cItem = null;

        cItem = getPaidPrizeIncludedEVCItems(evcId);

        Cursor cPrize = getPrizeIncludedPrizeItems(evcId);

        if (cItem != null && cPrize != null) {
            if (cItem.moveToFirst()) {
                do {
                    if (cPrize.moveToFirst()) {
                        do {
                            if (cItem.getInt(0) == cPrize.getInt(0)) {
                                String sql = " update " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " set " +
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

    /*usp_ApplyStatuteOnEVCItem_preview  */
    public void mergePrize1(String evcId) {
        String sql = "UPDATE  " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                " SET TotalQty = (SELECT Sum(TotalQty) FROM EVCItemVnLite e WHERE EVCItemVnLite.GoodsRef = GoodsRef\n" +
                "  AND EVCItemVnLite.DisRef = DisRef AND PrizeType = 1 AND EVCRef = '" + evcId + "')\n" +
                "  WHERE PrizeType = 1\n" +
                "  AND _id = (SELECT MAX(_id) FROM EVCItemVnLite e \n" +
                "  WHERE EVCItemVnLite.GoodsRef = GoodsRef AND EVCItemVnLite.DisRef = DisRef\n" +
                "AND PrizeType = 1 AND EVCRef = '" + evcId + "')\n" +
                "AND EVCRef='" + evcId + "'  " ;
        db.execSQL(sql);

        sql = " DELETE FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
              " WHERE PrizeType = 1\n" +
              " AND _id <>(SELECT MAX(_id) FROM EVCItemVnLite e WHERE EVCItemVnLite.GoodsRef = GoodsRef AND EVCItemVnLite.DisRef = DisRef\n" +
              " AND PrizeType = 1 AND EVCRef = '" + evcId + "')\n" +
              " AND EVCRef='" + evcId + "'  " ;
        db.execSQL(sql);

        sql = "UPDATE  " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                " SET TotalQty = (SELECT TotalQty FROM EVCItemVnLite e WHERE e._id = EVCItemVnLite._id)\n" +
                " WHERE PrizeType = 1" +
                " AND EVCRef='" + evcId + "'  " ;
        db.execSQL(sql);
    }

    public void mergePrize1Old(String evcId) {
        Cursor c = null;
        try {
            String sql = "SELECT SUM(UnitQty)  AS UnitQty, SUM(TotalQty) AS TotalQty,  MAX(_ID), GoodsRef, IFNULL(UnitRef,0) \n" +
                    " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " \n" +
                    " WHERE  prizetype = 1 AND EVCRef='" + evcId + "' \n" +
                    " group by GoodsRef, IFNULL(UnitRef,0)";

            c = db.rawQuery(sql, null);

            if (c != null && c.moveToFirst()) {
                do {

                    sql = "UPDATE " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " \n " +
                            " SET UnitQty  = " + c.getDouble(0) + " , TotalQty = " + c.getDouble(1) + " \n" +
                            " WHERE _ID = " + c.getInt(2);

                    db.execSQL(sql);

                    sql = "DELETE from " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                            " WHERE prizetype = 1 " +
                            " AND GoodsRef = " + c.getInt(3) + " AND IFNULL(UnitRef,0) = " + c.getInt(4) + " AND _ID <> " + c.getInt(2) +
                            " AND EVCRef='" + evcId + "' ";
                    db.execSQL(sql);

                } while (c.moveToNext());
            }
        }catch (Exception ex)
        {
            throw ex;
        }
        finally {

            if(c != null) c.close();
        }

    }


    public void applyStatuteOnEVCItem2(String evcId) {

        String sql = "UPDATE  " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                " SET tax = IFNULL((Amount - Discount + AddAmount) * IFNULL(TaxPercent,0)/100,0) , " +
                " charge = IFNULL((Amount - Discount + AddAmount) * IFNULL(chargePercent,0)/100,0) " +
                " WHERE EVCRef = '" + evcId + "' AND PrizeType <> 1";
        db.execSQL(sql);
    }

    public void applyStatuteOnEVCItem3(String evcId) {
        String sql = " UPDATE " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                " SET AmountNut = Amount - Discount + AddAmount + tax + charge " +
                " WHERE EVCRef = '" + evcId + "' AND PrizeType <> 1";
        db.execSQL(sql);
    }

    public void  updateDiscountAndAddAmount(String evcId)
    {

        String sql = "UPDATE " + DATABASE_TABLE +
                     " SET Discount  = ( SELECT SUM(IFNULL(es.DisAmount, 0)) FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " es WHERE es.EVCItemRef = " + DATABASE_TABLE + "._id  AND EvcId = '"+evcId+"')" +
                     " , AddAmount = ( SELECT SUM(IFNULL(es.AddAmount, 0)) FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " es WHERE es.EVCItemRef = "+ DATABASE_TABLE + "._id AND EvcId = '"+evcId+"')" +
                     " WHERE EVCRef = '"+evcId+"' AND PrizeType = 0 " ;
        db.execSQL(sql);

    }

    public void clearAllData() {
        db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getPrizeIncludedPrizeItems(String evcId) {
        if (db != null) {
            String sql = "select ei.Goodsref, unitref, unitqty, unitcapasity, totalQty from " +
                    " " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ei INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES ON ei.disref= ES.disref " +
                    " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId= ES.DisRef  " +
                    " Where EVCRef= '" + evcId + "' and EI.prizetype=1 " +
                    " and d.prizeref=EI.goodsref ";
            return db.rawQuery(sql, null);
        } else
            return null;
    }

    public void applyStatuteOnEVCItem1(String evcId) {
        String sql = "UPDATE " + DATABASE_TABLE +
                " SET Discount = IFNULL((SELECT Sum(IFNULL(es.DisAmount , 0)) FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " es WHERE es.EVCItemRef=" + EVCItemVnLiteDBAdapter.DATABASE_TABLE + "._id AND es.EVCId = '"+evcId+"' ),0) " +
                " , AddAmount  = IFNULL((SELECT Sum(IFNULL(AddAmount,0)) FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " es WHERE es.EVCItemRef=" + EVCItemVnLiteDBAdapter.DATABASE_TABLE + "._id AND es.EVCID = '"+evcId+"'),0) " +
                " WHERE EVCRef = '" + evcId + "'";//+" AND PrizeType <> 1" ;
        db.execSQL(sql);
    }

    private Cursor getEvcItems(String evcId) {
        if (db != null) {
            String sql = "select EI._id, goodsref From " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E On E.EvcID = EI.EVCRef" +
                    " Where E.EvcID = '" + evcId + "'";
            return db.rawQuery(sql, null);
        } else
            return null;
    }

    private void updateEvcItemTax(int itemId) {

        String sql = "update " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                "  set tax=IFNULL(((Amount - Discount + AddAmount)* IFNULL(TaxPercent,0)/100),0) " +
                " ,charge=IFNULL(((Amount - Discount + AddAmount)* IFNULL(ChargePercent,0)/100),0) " +
                " where _id=" + itemId;
        db.execSQL(sql);

    }

    private void updateAmountByTax() {

        String sql = "update " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +
                "  set AmountNut= Amount - Discount + AddAmount + charge + tax";
        db.execSQL(sql);
    }

    public Cursor getEvcItemData(String evcId) {

        String sql = "SELECT E.GoodsRef, SmallUnitQty, LargeUnitQty, CustPrice, PriceId, SmallUnitId, LargeUnitId, PrizeType, Discount" +
                " , E.Tax, E.Charge, Amount, AmountNut, _id, P.productId as productId, ProductCode, ProductName" +
                " , UnitQty, UnitCapasity, E.AddAmount , E.PackQty, E.TotalQty, P.ProductTypeId"+
                " , EvcItemAdd1, EvcItemAdd2, EvcItemDis1, EvcItemDis2, EvcItemDis3, DisRef "+
                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " AS E, " +
                DiscountProductDBAdapter.DATABASE_TABLE + " AS P " +
                " WHERE E.GoodsRef = P.ProductId and evcRef='" + evcId + "'";
        return db.rawQuery(sql, null);
    }

    public void deleteInvalidReturnItem(String evcId) {
        db.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE TotalQty < 0 AND EVCRef = '" + evcId + "'");
    }
    public void deleteInvalidQty(String evcId) {
        db.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE TotalQty = 0 AND EVCRef = '" + evcId + "'");
    }

    private Cursor getPaidPrizeIncludedEVCItems(String evcId) {
        if (db != null) {
            String sql = "select ei.Goodsref, unitref, unitqty, unitcapasity, ei.TotalQty, ei._id,  count(PrizeIncluded) as cnt from " +
                    " " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES ON EI.GoodsRef= ES.EVCItemRef " +
                    " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId= ES.DisRef " +
                    " Where EVCRef= '" + evcId + "' and EI.prizetype=0 and PrizeIncluded=1 and " +
                    "	EI.goodsref=d.goodsref and d.prizeref=EI.goodsref " +
                    " Group BY ei.Goodsref, unitref, unitqty, unitcapasity, ei.TotalQty ";
            return db.rawQuery(sql, null);
        } else
            return null;

    }

    public void decreaseTotalQtyByReturnQty(String evcId2, BigDecimal unitQty,BigDecimal packQty,
                                            int productId, int evcType, BigDecimal totalQty) {
        String query = "";
        // Decrease TotalQty by return qty.
        query = "UPDATE " + DATABASE_TABLE + " SET "
                + " UnitQty = UnitQty  - " + unitQty
                + " ,TotalQty = TotalQty - " + totalQty
                + " ,PackQty = packQty - " + packQty
                + " ,Amount = (TotalQty - " + totalQty + ")  * CustPrice  \n"
                + " ,AmountNut = (TotalQty - " + totalQty + ")  * CustPrice  \n"
//                + " ,AmountNut = CASE WHEN (" + AppVariables.getInstance().getDigtsAfterDecimalForCPrice() + " > 0 OR " + methodType + " =1) \n"
//                + "                     AND CAST( (TotalQty - " + totalQty + ") /UnitCapasity AS INT) <> 0  \n"
//                + "                     AND CAST( (TotalQty - " + totalQty + ") /UnitCapasity AS INT) * UnitCapasity = (TotalQty - " + totalQty + ") \n"
//                + "                THEN CAST( (TotalQty -" + totalQty + ")/UnitCapasity AS INT) * round (UnitCapasity*CustPrice," + AppVariables.getInstance().getRoundType() + ") \n"
 //               + "                ELSE CAST( (TotalQty - " + totalQty + ") AS INT) * CustPrice END \n"
                + " WHERE EVCRef = '" + evcId2 + "' AND GoodsRef = " + productId + " AND PrizeType=0 \n";
        db.execSQL(query);

        query = "UPDATE " + DATABASE_TABLE
                + " SET tax = IFNULL((CASE WHEN CAST((AmountNut * taxPercent / 100) AS INT) = (AmountNut * taxPercent * 1.0 / 100.0) THEN CAST((AmountNut * taxPercent / 100) AS INT) \n"
                + "                        ELSE 1 + CAST((AmountNut * taxPercent / 100) AS INT) END) ,0) \n"
                + " ,charge = IFNULL((CASE WHEN CAST((AmountNut * chargePercent / 100) AS INT) = (AmountNut * chargePercent / 100) THEN CAST((AmountNut * chargePercent / 100) AS INT) \n"
                + "                        ELSE 1 + CAST((AmountNut * chargePercent / 100) AS INT) END) ,0) \n"
                + " WHERE EVCRef= '" + evcId2 + "' AND GoodsRef=" + productId;
        db.execSQL(query);

      //  if (evcType != 2) {
            db.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE EVCRef='" + evcId2 + "' AND GoodsRef = " + productId + " AND TotalQty = 0");
      //  }
    }

    public void updateEVCitemForSellReturn1(String evcId) {
        String query = " UPDATE " + DATABASE_TABLE +
                "  SET UnitQty = CAST( (TotalQty/UnitCapasity) AS INT)  \n" +
                "  ,Amount = TotalQty*CustPrice  \n" +
                "  ,AmountNut = TotalQty*CustPrice  \n" +
                "  ,Discount=0  \n" +
                "  ,AddAmount=0  \n" +
                "  WHERE EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }
    public void resetEvc(String evcId) {
        String query = " UPDATE " + DATABASE_TABLE +
                "  SET AmountNut = Amount \n" +
                "  ,Discount=0  \n" +
                "  ,AddAmount=0  \n" +
                "  WHERE EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }
    public void updateDis1(String evcId) {
        String query = " UPDATE " + DATABASE_TABLE +
         " SET Discount= Discount+ "+
         " IFNULL((SELECT CAST( ((Amount*E.DiscountAmount)/100) AS INT) "+
         " FROM EVCHeaderVnLite E WHERE  EVCId = '" + evcId + "' ),0) "+
         " WHERE EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }
    public void updateAdd1(String evcId) {
        String query = " UPDATE " + DATABASE_TABLE +
                " Set AddAmount = AddAmount + \n" +
                " IFNULL((SELECT CAST( ((Amount-IFNULL(E.DiscountAmount,0))* IFNULL(E.AddAmount,0) /100) AS INT)  \n" +
                " FROM EVCHeaderVnLite E WHERE  EVCId = '" + evcId + " '  ),0)"+
                " WHERE EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }
    public void updateNetAmount(String evcId) {
        String query = " UPDATE " + DATABASE_TABLE +
                " Set AmountNut = Amount - Discount + AddAmount \n" +
                " WHERE EVCRef = '" + evcId + "'";
        db.execSQL(query);
    }

    public BigDecimal getSumAmount(String evcId){

        Cursor cItem = db.rawQuery("SELECT SUM(Amount) Amount FROM   "+ DATABASE_TABLE + " where evcRef = '" + evcId +"'", null);

        if (cItem != null) {
            if (cItem.moveToFirst()) {
                return new BigDecimal(cItem.getDouble(cItem.getColumnIndex("Amount")));
            }
        }
        return BigDecimal.ZERO;
    }

    public void updateDiscountAmount(String evcId, BigDecimal totalDiscount , BigDecimal testDiscount){

        String query = " UPDATE " + DATABASE_TABLE +
                " SET Discount = IFNULL(Discount,0) + ( " + totalDiscount + " - " + testDiscount + ")"+
                " WHERE _id = (SELECT _id " +
                            "  FROM EVCItemVnLite " +
                            "  WHERE EVCRef = '" + evcId +"' AND IFNULL(Discount,0)<>0 ORDER BY _id DESC LIMIT 1)" ;
        db.execSQL(query);
    }

    public boolean ExistPrizeProductInMainProduct (String evcId){
        boolean exists = false;
        String sql = "SELECT 1 FROM "+ DATABASE_TABLE + "  WHERE EVCRef = '" + evcId +"' " +
                " AND PrizeType = 1 " +
                " AND GoodsRef in (SELECT e.GoodsRef " +
                "  FROM "+ DATABASE_TABLE + " as e "+
                "  WHERE e.EVCRef = '" + evcId +"' "+
                " AND PrizeType = 0 " +
                " AND IFNULL(e._id, 0) = IFNULL(" + DATABASE_TABLE + "._id, 0) " +
                ") ";
        Cursor affectedCursor = db.rawQuery(sql, null);
        if (affectedCursor != null && affectedCursor.moveToFirst()) {
            if (affectedCursor.getInt(0) == 1) {
                exists = true;
            }
        }
        return exists;
    }

    public void resetEvc2(String evcId, String newEvcId){
        String query = " UPDATE " + DATABASE_TABLE +
                " SET Discount= 0, "+
                " AddAmount = 0 , "+
                " AmountNut = 0 , "+
                " Amount = 0 , "+
                " tax = 0 , "+
                " Charge = 0 "+
        " WHERE   PrizeType = 1 "+
        "  AND (  EVCRef = '" + evcId +"' " +
            "  OR EVCRef = '" + newEvcId +"' )" ;
        db.execSQL(query);
    }

    /*usp_GetRetExtraValue*/
    public void updatePrizeGoodMainGood(){
//TODO

    /**براي کالاي جايزه اي در کالاهاي اصلي برگشت فاکتور وجود دارد****/
    /*
    IF EXISTS ( SELECT  *
            FROM    EVCDetail Main
    INNER JOIN EVCDetail Prize ON Prize.ProductId = Main.ProductId
    WHERE   Prize.IsPrize = 1
    AND Prize.EVCID = @SaleEVCID
            AND Main.IsPrize = 0
    AND Main.EVCID = @NewSaleEVCID
            AND ISNULL(Main.DetailId, 0) = ISNULL(Prize.DetailId, 0) )
    BEGIN

    DECLARE CUR CURSOR FAST_FORWARD
    FOR
    SELECT  ISNULL(D.PrizeStep, 999999999) AS PrizeStep ,
    ISNULL(D.PrizeQty, 0) AS PrizeQty ,
    Main.ProductId ,
            ISNULL(Main.DetailId, 0) ,
            Main.EVCDetailID
    FROM    dbo.EVCDetail EI
    INNER JOIN dbo.EVC E ON E.EVCID = EI.EVCID
    INNER JOIN dbo.EVCDetailStatutes ES ON EI.EVCDetailID = ES.EVCDetailID
    INNER JOIN VWPromotion D ON D.PromotionDetailId = ES.PromotionDetailId
    INNER JOIN EVCDetail Main ON EI.ProductId = Main.ProductId
    INNER JOIN SellReturnDetail srd ON SellReturnId = @SellReturnId
            AND srd.ProductId = EI.ProductId
    AND ISNULL(srd.BatchId, 0) = ISNULL(EI.DetailId, 0)
    AND srd.IsPrize = 0
    WHERE   EI.EVCID = @SaleEVCID
            AND D.IsPrize = 1
    AND Main.IsPrize = 0
    AND Main.EVCID = @NewSaleEVCID
            AND ISNULL(Main.DetailId, 0) = ISNULL(EI.DetailId, 0)
    AND EI.IsPrize = 1
    AND EI.EVCID = @SaleEVCID
            AND D.ReduceOfQty <> 1;

    DECLARE @PrizeStep FLOAT ,
    @PrizeQty FLOAT ,
            @CurProductId INT ,
            @BatchId INT ,
            @EvcDetailId INT;

    OPEN CUR;
    FETCH NEXT FROM CUR INTO @PrizeStep, @PrizeQty, @CurProductId, @BatchId, @EvcDetailId;

    WHILE @@FETCH_STATUS = 0
    BEGIN
    DECLARE @RetQty FLOAT ,
    @SellQty FLOAT;


    SELECT  @RetQty = Qty
    FROM    SellReturnDetail
    WHERE   SellReturnId = @SellReturnId
            AND ProductId = @CurProductId
            AND ISNULL(BatchId, 0) = @BatchId
            AND IsPrize = 0;

    SELECT  @SellQty = Qty
    FROM    SellDetail
    WHERE   SellId = ( SELECT   SellId
    FROM     SellReturn
    WHERE    SellReturnId = @SellReturnId
                                 )
    AND ProductId = @CurProductId
            AND ISNULL(BatchId, 0) = @BatchId
            AND IsPrize = 0;

    DECLARE @BaseQty INT ,
    @Prize INT ,--@RetQty int,
            @A1 INT ,
            @B1 INT ,
            @Result INT ,
            @QP INT;
    SET @Result = 0;
    SET @BaseQty = @PrizeStep;
    SET @Prize = @PrizeQty;
    --set @RetQty = @RetQty
    SET @B1 = @Prize + @BaseQty;
    SET @A1 = FLOOR(@RetQty * 1.0 / ( @BaseQty + @Prize ) * 1.0);

    IF @RetQty - @A1 * @B1 >= 0
    IF ( @RetQty - @A1 * @B1 ) > @BaseQty
    SELECT  @Result = @BaseQty;
    ELSE
    SELECT  @Result = @RetQty - @A1 * @B1;

    SELECT  @Result = @Result + @A1 * @BaseQty;


    UPDATE  EVCDetail
    SET     Qty2 = Qty - ( @SellQty - @Result ) ,
    Qty = @SellQty - @Result
            WHERE   EVCDetailID = @EvcDetailId;

    FETCH NEXT FROM CUR INTO @PrizeStep, @PrizeQty, @CurProductId, @BatchId, @EvcDetailId;
    END;
    CLOSE CUR;
    DEALLOCATE CUR;
    END;
    */
    /**براي کالاي جايزه اي در کالاهاي اصلي برگشت فاکتور وجود دارد****/


}

}

