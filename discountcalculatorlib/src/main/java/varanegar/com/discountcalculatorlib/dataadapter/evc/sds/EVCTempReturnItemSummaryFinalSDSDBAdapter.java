package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProductUnit;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

/**
 * Created by m.aghajani on 7/4/2016.
 */
public class EVCTempReturnItemSummaryFinalSDSDBAdapter extends DiscountBaseDataAdapter {

    public final String KEY_EVC_TEMP_RETURN_ROWID = "_id";
    public final String KEY_EVC_TEMP_RETURN_EVC_ID = "EVCId";
    public final String KEY_EVC_TEMP_RETURN_ROW_ORDER = "RowOrder";
    public final String KEY_EVC_TEMP_RETURN_GOODS_REF = "GoodsRef";
    public final String KEY_EVC_TEMP_RETURN_UNIT_QTY = "UnitQty";
    public final String KEY_EVC_TEMP_RETURN_CPRICE_REF = "CPriceRef";
    public final String KEY_EVC_TEMP_RETURN_UNIT_REF = "UnitRef";
    public final String KEY_EVC_TEMP_RETURN_UNIT_CAPASITY = "UnitCapasity";
    public final String KEY_EVC_TEMP_RETURN_TOTAL_QTY = "TotalQty";
    public final String KEY_EVC_TEMP_RETURN_AMOUNT_NUT = "AmountNut";
    public final String KEY_EVC_TEMP_RETURN_DISCOUNT = "Discount";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_DISCOUNT = "RemainDiscount";
    public final String KEY_EVC_TEMP_RETURN_AMOUNT = "Amount";
    public final String KEY_EVC_TEMP_RETURN_PRIZE_TYPE = "PrizeType";
    public final String KEY_EVC_TEMP_RETURN_SUP_AMOUNT = "SupAmount";
    public final String KEY_EVC_TEMP_RETURN_ADD_AMOUNT = "AddAmount";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_ADD_AMOUNT = "RemainAddAmount";
    public final String KEY_EVC_TEMP_RETURN_ADD1 = "Add1";
    public final String KEY_EVC_TEMP_RETURN_ADD2 = "Add2";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_ADD1 = "RemainAdd1";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_ADD2 = "RemainAdd2";
    public final String KEY_EVC_TEMP_RETURN_USER_PRICE = "UserPrice";
    public final String KEY_EVC_TEMP_RETURN_CUST_PRICE = "CustPrice";
    public final String KEY_EVC_TEMP_RETURN_PRICE_ID = "PriceId";
    public final String KEY_EVC_TEMP_RETURN_CHARGE = "Charge";
    public final String KEY_EVC_TEMP_RETURN_TAX = "Tax";
    public final String KEY_EVC_TEMP_RETURN_DIS1 = "Dis1";
    public final String KEY_EVC_TEMP_RETURN_DIS2 = "Dis2";
    public final String KEY_EVC_TEMP_RETURN_DIS3 = "Dis3";

    public final String KEY_EVC_TEMP_RETURN_DISOTHER = "OtherDiscount";
    public final String KEY_EVC_TEMP_RETURN_ADDOTHER = "OtherAddition";

    public final String KEY_EVC_TEMP_RETURN_REMAIN_DIS1 = "RemainDis1";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_DIS = "RemainDis2";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_DIS3 = "RemainDis3";


    public static final String DATABASE_TABLE = "EVCTempReturnItemSummaryFinalSDS";
    private static String TAG = "EVCTempReturnItemSummaryFinalSDSDBAdapter";
    private static EVCTempReturnItemSummaryFinalSDSDBAdapter instance;

    public EVCTempReturnItemSummaryFinalSDSDBAdapter() {
    }


    public static EVCTempReturnItemSummaryFinalSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempReturnItemSummaryFinalSDSDBAdapter();
        }

        return instance;

    }

    /*SLE.usp_GetRetExtraValue*/
    public void fillEVCTempReturnItemSummaryFinal(String newSaleEVCId) {
        BigDecimal sumRemOtherAddition, sumRemOtherDiscount, sumDiscountAll, sumRemDiscount, sumAmountAll, sumRemAddAmount, sumRemAdd1, sumRemAdd2, sumRemDis1, sumRemDis2, sumRemDis3;
        sumRemOtherAddition = sumRemOtherDiscount = sumDiscountAll = sumRemDiscount = sumAmountAll = sumRemAddAmount = sumRemAdd1 = sumRemAdd2 = sumRemDis1 = sumRemDis2 = sumRemDis3 = BigDecimal.ZERO;

        Cursor c = getRemValues(newSaleEVCId);
        if (c != null && c.moveToFirst()) {
            sumDiscountAll = new BigDecimal(c.getDouble(0));
            sumRemDiscount = new BigDecimal(c.getDouble(1));
            sumAmountAll = new BigDecimal(c.getDouble(2));
            sumRemAddAmount = new BigDecimal(c.getDouble(3));
            sumRemAdd1 = new BigDecimal(c.getDouble(4));
            sumRemAdd2 = new BigDecimal(c.getDouble(5));
            sumRemOtherAddition = new BigDecimal(c.getDouble(9));

            sumRemDis1 = new BigDecimal(c.getDouble(6));
            sumRemDis2 = new BigDecimal(c.getDouble(7));
            sumRemDis3 = new BigDecimal(c.getDouble(8));
            sumRemOtherDiscount = new BigDecimal(c.getDouble(10));
        }

        if (sumAmountAll.compareTo(BigDecimal.ZERO) == 0)
            sumAmountAll = BigDecimal.ONE;
        /*#tmpTable3*/
        String query = "INSERT INTO " + DATABASE_TABLE
                + " (EVCId, RowOrder, GoodsRef, UnitQty, CPriceRef, UnitRef, UnitCapasity, TotalQty, AmountNut, Discount, RemainDiscount, Amount, PrizeType \n"
                + " , SupAmount, AddAmount, RemainAddAmount, Add1, Add2, RemainAdd1, RemainAdd2, RemainOtherAddition, UserPrice, CustPrice, PriceId, Charge, Tax, Dis1, Dis2, Dis3 \n"
                + " , RemainDis1, RemainDis2, RemainDis3, RemainOtherDiscount, FreeReasonId \n"
                + " , OtherDiscount, OtherAddition ) \n"
                + " SELECT EVCId, RowOrder,  GoodsRef, UnitQty, CPriceRef, UnitRef, UnitCapasity, TotalQty, AmountNut, Discount \n"
                + "  , IFNULL( CAST ( " + sumRemDiscount.doubleValue() + " * Amount / " + sumDiscountAll.doubleValue() + " AS INT), 0) As RemainDiscount \n"
                + "  , Amount ,PrizeType ,SupAmount ,AddAmount \n"
                + "  , CASE WHEN AddAmount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemAddAmount.doubleValue() + " * Amount / " + sumAmountAll.doubleValue() + " AS INT),0) end As RemainAddAmount \n"
                + "  , Add1, Add2 \n"
                + "  , CASE WHEN AddAmount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemAdd1.doubleValue() + " * Amount / " + sumAmountAll.doubleValue() + " AS INT),0) end As RemainAdd1 \n"
                + "  , CASE WHEN AddAmount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemAdd2.doubleValue() + " * Amount / " + sumAmountAll.doubleValue() + " AS INT),0) end As RemainAdd2 \n"
                + "  , CASE WHEN AddAmount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemOtherAddition.doubleValue() + " * Amount / " + sumAmountAll.doubleValue() + " AS INT),0) end As RemOtherAddition \n"
                + "  , UserPrice, CustPrice, PriceId, IFNULL(Charge, 0), IFNULL(Tax, 0), Dis1, Dis2, Dis3 \n"
                + "  , CASE WHEN Discount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemDis1.doubleValue() + " * Amount / " + sumDiscountAll.doubleValue() + " AS INT),0) end As RemainDis1"
                + "  , CASE WHEN Discount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemDis2.doubleValue() + " * Amount / " + sumDiscountAll.doubleValue() + " AS INT),0) end As RemainDis2"
                + "  , CASE WHEN Discount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemDis3.doubleValue() + " * Amount / " + sumDiscountAll.doubleValue() + " AS INT),0) end As RemainDis3"
                + "  , CASE WHEN Discount = 0 OR PrizeType=1 THEN 0 else IFNULL(CAST( " + sumRemOtherDiscount.doubleValue() + " * Amount / " + sumDiscountAll.doubleValue() + " AS INT),0) end As RemainOtherDiscount"
                + "  , FreeReasonId "
                + "  , OtherDiscount, OtherAddition "
                + " FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE
                + " WHERE TotalQty > 0 AND EVCId = '" + newSaleEVCId + "'";

        db.execSQL(query);

        updateRemAmount(newSaleEVCId, sumRemDiscount.doubleValue(), sumRemAddAmount.doubleValue(), sumRemAdd1.doubleValue(), sumRemAdd2.doubleValue()
                , sumRemDis1.doubleValue(), sumRemDis2.doubleValue(), sumRemDis3.doubleValue()
                , sumRemOtherAddition.doubleValue(), sumRemOtherDiscount.doubleValue());
    }

    public void updateDiscountAndAddAmount(String newSaleEVCId) {

        //#tmpTable3
        String query = "UPDATE " + DATABASE_TABLE
                + " SET Discount = Discount + RemainDiscount \n"
                + " , AddAmount = AddAmount + RemainAddAmount \n"
                + " , Add1 = Add1 + RemainAdd1 \n"
                + " , Add2 = Add2 + RemainAdd2 \n"
                + " , OtherAddition=OtherAddition+RemainOtherAddition\n"
                + " , Dis1 = Dis1 + RemainDis1 \n"
                + " , Dis2 = Dis2 + RemainDis2 \n"
                + " , Dis3 = Dis3 + RemainDis3 \n"
                + " , OtherDiscount=OtherDiscount+RemainOtherDiscount \n"
                //+ " , AmountNut = Amount - Discount - RemainDiscount + AddAmount + RemainAddAmount\n"
                + " Where PrizeType=0  AND FreeReasonId IS NULL AND EVCId ='" + newSaleEVCId + "'";
        db.execSQL(query);

        //#tmpTable3
        query = "UPDATE " + DATABASE_TABLE +
                " Set AmountNut=Amount-(Dis1 + Dis2 + Dis3 + OtherDiscount)+(Add1 + Add2 + OtherAddition) " +
                "     , Discount = Dis1 + Dis2 + Dis3 + OtherDiscount " +
                "     , AddAmount = Add1 + Add2 + OtherAddition " +
                " Where PrizeType=0 AND FreeReasonId IS NULL ";
        db.execSQL(query);

    }

    public List<DiscountCallReturnLineData> getCallWithPromo(String evcId, DiscountCallReturnData returnData) {
        List<DiscountCallReturnLineData> result = new ArrayList<>();
        Cursor cItemData = null;


        try {
            cItemData = getResult(evcId);

            if (cItemData != null && cItemData.moveToFirst()) {
                int unitQty = 0;

                do {
                    DiscountCallReturnLineData returnLineData = new DiscountCallReturnLineData();

                    returnLineData.productId = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_GOODS_REF));

                    returnLineData.productCode = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTCODE));
                    returnLineData.productName = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTNAME));
                    returnLineData.returnSmallQtyId = cItemData.getInt(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_SMALLUNITID));

                    returnLineData.isPromoLine = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRIZE_TYPE)) == 1;

                    returnLineData.isFreeItem = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRIZE_TYPE));

                    if (returnLineData.isFreeItem == 0) {
                        DiscountCallReturnLineData mainLine = returnData.getCallReturnLineData(returnLineData.productId, false);

                        returnLineData.returnLineUniqueId = mainLine.returnLineUniqueId;
                        returnLineData.returnUniqueId = mainLine.returnUniqueId;
                        returnLineData.customerId = mainLine.customerId;
                        returnLineData.sortId = mainLine.sortId;
                        returnLineData.indexInfo = mainLine.indexInfo;
                        returnLineData.callUniqueId = mainLine.callUniqueId;
                        returnLineData.weight = mainLine.weight;
                        returnLineData.referenceId = mainLine.referenceId;
                        returnLineData.referenceNo = mainLine.referenceNo;
                        returnLineData.dealerId = mainLine.dealerId;
                        returnLineData.referenceDate = mainLine.referenceDate;
                        returnLineData.referenceQty = mainLine.referenceQty;
                        returnLineData.returnReasonId = mainLine.returnReasonId;
                    } else {
                        returnLineData.returnLineUniqueId = UUID.randomUUID().toString();
                        returnLineData.returnUniqueId = returnData.returnUniqueId;
                        returnLineData.customerId = returnData.customerId;
                        returnLineData.sortId = 0;
                        returnLineData.indexInfo = returnLineData.productId;
                        returnLineData.callUniqueId = returnData.callUniqueId;
                        returnLineData.weight = BigDecimal.ZERO;
                        returnLineData.referenceId = returnData.returnRefId;
                        returnLineData.referenceNo = returnData.returnRefNo;
                        returnLineData.dealerId = returnData.dealerId;
                        returnLineData.referenceDate = returnData.returnRefDate;
                        returnLineData.referenceQty = BigDecimal.ZERO;
                        returnLineData.returnReasonId = returnData.returnReasonId;

                        returnLineData.returnProductTypeId = DiscountCallReturnLineData.ReturnProductTypeEnum.INTACT.value();
                    }


                    returnLineData.returnTotalQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_TOTAL_QTY)));
                    unitQty = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_CAPASITY));

                    if (returnLineData.returnTotalQty.intValue() % unitQty == 0) {
                        returnLineData.returnBigQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_QTY)));
                        returnLineData.returnBigQtyId = cItemData.getLong(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_REF));
                        returnLineData.returnBigQtyName = cItemData.getString(cItemData.getColumnIndex(DiscountProductUnitDBAdapter.KEY_PUNIT_PRODUCTUNITNAME));
                    } else {
                        DiscountProductUnit smallUnit = DiscountProductUnitDBAdapter.getInstance().getSmallUnit(returnLineData.productId);

                        returnLineData.returnBigQty = returnLineData.returnTotalQty.divide(smallUnit.quantity, 0, RoundingMode.FLOOR);
                        returnLineData.returnBigQtyId = smallUnit.productUnitId;
                        returnLineData.returnBigQtyName = smallUnit.productUnitName;
                    }

                    returnLineData.priceId = cItemData.getLong(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRICE_ID));
                    returnLineData.unitPrice = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_CUST_PRICE)));
                    returnLineData.totalReturnAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_AMOUNT)));
                    returnLineData.totalReturnDiscount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DISCOUNT)));
                    returnLineData.totalReturnSupAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_SUP_AMOUNT)));
                    returnLineData.totalReturnAddAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD_AMOUNT)));
                    returnLineData.totalReturnCharge = BigDecimal.ZERO;
                    returnLineData.totalReturnTax = BigDecimal.ZERO;
                    returnLineData.totalReturnDis1 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS1)));
                    returnLineData.totalReturnDis2 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS2)));
                    returnLineData.totalReturnDis3 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS3)));
                    returnLineData.totalReturnDisOther = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DISOTHER)));
                    returnLineData.totalReturnAdd1Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD1)));
                    returnLineData.totalReturnAdd2Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD2)));
                    returnLineData.totalReturnAddOtherAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADDOTHER)));

                    returnLineData.totalReturnDis1 = (returnLineData.totalReturnDis1 == null ? BigDecimal.ZERO : returnLineData.totalReturnDis1);
                    returnLineData.totalReturnDis2 = (returnLineData.totalReturnDis2 == null ? BigDecimal.ZERO : returnLineData.totalReturnDis2);
                    returnLineData.totalReturnDis3 = (returnLineData.totalReturnDis3 == null ? BigDecimal.ZERO : returnLineData.totalReturnDis3);
                    returnLineData.totalReturnDisOther = (returnLineData.totalReturnDisOther == null ? BigDecimal.ZERO : returnLineData.totalReturnDisOther);
                    returnLineData.totalReturnAdd1Amount = (returnLineData.totalReturnAdd1Amount == null ? BigDecimal.ZERO : returnLineData.totalReturnAdd1Amount);
                    returnLineData.totalReturnAdd2Amount = (returnLineData.totalReturnAdd2Amount == null ? BigDecimal.ZERO : returnLineData.totalReturnAdd2Amount);
                    returnLineData.totalReturnAddOtherAmount = (returnLineData.totalReturnAddOtherAmount == null ? BigDecimal.ZERO : returnLineData.totalReturnAddOtherAmount);

                    returnLineData.totalReturnAddAmount = returnLineData.totalReturnAdd1Amount.add(returnLineData.totalReturnAdd2Amount);
                    returnLineData.totalReturnNetAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_AMOUNT_NUT)));

                    result.add(returnLineData);

                } while (cItemData.moveToNext());
            }


            for (DiscountCallReturnLineData lineData : returnData.callReturnLineItemData.values()) {
                boolean fined = false;
                if (!lineData.isPromoLine) {
                    for (DiscountCallReturnLineData linePromotionData : result) {
                        if (lineData.productId == linePromotionData.productId &&
                                lineData.isPromoLine == linePromotionData.isPromoLine &&
                                lineData.isFreeItem == linePromotionData.isFreeItem) {
                            fined = true;
                            break;
                        }
                    }
                    if (!fined) {
                        DiscountCallReturnLineData tempData = lineData;
                        tempData.callUniqueId = returnData.callUniqueId;
                        tempData.customerId = returnData.customerId;
                        tempData.returnTotalQty = BigDecimal.ZERO;
                        tempData.returnBigQty = BigDecimal.ZERO;
                        tempData.returnBigQtyId = 0;
                        tempData.returnBigQtyName = null;
                        tempData.priceId = 0;
                        tempData.unitPrice = BigDecimal.ZERO;
                        tempData.totalReturnAmount = BigDecimal.ZERO;
                        tempData.totalReturnDiscount = BigDecimal.ZERO;
                        tempData.totalReturnSupAmount = BigDecimal.ZERO;
                        tempData.totalReturnAddAmount = BigDecimal.ZERO;
                        tempData.totalReturnCharge = BigDecimal.ZERO;
                        tempData.totalReturnTax = BigDecimal.ZERO;
                        tempData.totalReturnDis1 = BigDecimal.ZERO;
                        tempData.totalReturnDis2 = BigDecimal.ZERO;
                        tempData.totalReturnDis3 = BigDecimal.ZERO;
                        tempData.totalReturnDisOther = BigDecimal.ZERO;
                        tempData.totalReturnAdd1Amount = BigDecimal.ZERO;
                        tempData.totalReturnAdd2Amount = BigDecimal.ZERO;
                        tempData.totalReturnAddOtherAmount = BigDecimal.ZERO;
                        tempData.totalReturnNetAmount = BigDecimal.ZERO;
                        result.add(tempData);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }
    public List<DiscountCallReturnLineData> getNewCallWithPromo( DiscountCallReturnData returnData) {
        List<DiscountCallReturnLineData> result = new ArrayList<>();
        Cursor cItemData = null;

        try {
            cItemData = getNewResult();

            if (cItemData != null && cItemData.moveToFirst()) {
                int unitQty = 0;

                do {
                    DiscountCallReturnLineData returnLineData = new DiscountCallReturnLineData();

                    returnLineData.productId = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_GOODS_REF));

                    returnLineData.productCode = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTCODE));
                    returnLineData.productName = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTNAME));
                    returnLineData.returnSmallQtyId = cItemData.getInt(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_SMALLUNITID));

                    returnLineData.isPromoLine = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRIZE_TYPE)) == 1;

                    returnLineData.isFreeItem = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRIZE_TYPE));

                    if (returnLineData.isFreeItem == 0) {
                        DiscountCallReturnLineData mainLine = returnData.getCallReturnLineData(returnLineData.productId, false);

                        returnLineData.returnLineUniqueId = mainLine.returnLineUniqueId;
                        returnLineData.returnUniqueId = mainLine.returnUniqueId;
                        returnLineData.customerId = mainLine.customerId;
                        returnLineData.sortId = mainLine.sortId;
                        returnLineData.indexInfo = mainLine.indexInfo;
                        returnLineData.callUniqueId = mainLine.callUniqueId;
                        returnLineData.weight = mainLine.weight;
                        returnLineData.referenceId = mainLine.referenceId;
                        returnLineData.referenceNo = mainLine.referenceNo;
                        returnLineData.dealerId = mainLine.dealerId;
                        returnLineData.referenceDate = mainLine.referenceDate;
                        returnLineData.referenceQty = mainLine.referenceQty;
                        returnLineData.returnReasonId = mainLine.returnReasonId;
                        returnLineData.returnProductTypeId = mainLine.returnProductTypeId;
                        returnLineData.ReturnProductTypeId = mainLine.ReturnProductTypeId;
                    } else {
                        returnLineData.returnLineUniqueId = UUID.randomUUID().toString();
                        returnLineData.returnUniqueId = returnData.returnUniqueId;
                        returnLineData.customerId = returnData.customerId;
                        returnLineData.sortId = 0;
                        returnLineData.indexInfo = returnLineData.productId;
                        returnLineData.callUniqueId = returnData.callUniqueId;
                        returnLineData.weight = BigDecimal.ZERO;
                        returnLineData.referenceId = returnData.returnRefId;
                        returnLineData.referenceNo = returnData.returnRefNo;
                        returnLineData.dealerId = returnData.dealerId;
                        returnLineData.referenceDate = returnData.returnRefDate;
                        returnLineData.referenceQty = BigDecimal.ZERO;
                        returnLineData.returnReasonId = returnData.returnReasonId;

                        returnLineData.returnProductTypeId = DiscountCallReturnLineData.ReturnProductTypeEnum.INTACT.value();

                    }


                    returnLineData.returnTotalQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_TOTAL_QTY)));
                    unitQty = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_CAPASITY));

                    if (returnLineData.returnTotalQty.intValue() % unitQty == 0) {
                        returnLineData.returnBigQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_QTY)));
                        returnLineData.returnBigQtyId = cItemData.getLong(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_REF));
                        returnLineData.returnBigQtyName = cItemData.getString(cItemData.getColumnIndex(DiscountProductUnitDBAdapter.KEY_PUNIT_PRODUCTUNITNAME));
                    } else {
                        DiscountProductUnit smallUnit = DiscountProductUnitDBAdapter.getInstance().getSmallUnit(returnLineData.productId);

                        returnLineData.returnBigQty = returnLineData.returnTotalQty.divide(smallUnit.quantity, 0, RoundingMode.FLOOR);
                        returnLineData.returnBigQtyId = smallUnit.productUnitId;
                        returnLineData.returnBigQtyName = smallUnit.productUnitName;
                    }

                    returnLineData.priceId = cItemData.getLong(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRICE_ID));
                    returnLineData.unitPrice = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_CUST_PRICE)));
                    returnLineData.totalReturnAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_AMOUNT)));
                    returnLineData.totalReturnDiscount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DISCOUNT)));
                    returnLineData.totalReturnSupAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_SUP_AMOUNT)));
                    returnLineData.totalReturnAddAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD_AMOUNT)));
                    returnLineData.totalReturnCharge = BigDecimal.ZERO;
                    returnLineData.totalReturnTax = BigDecimal.ZERO;
                    returnLineData.totalReturnDis1 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS1)));
                    returnLineData.totalReturnDis2 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS2)));
                    returnLineData.totalReturnDis3 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS3)));
                    returnLineData.totalReturnDisOther = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DISOTHER)));
                    returnLineData.totalReturnAdd1Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD1)));
                    returnLineData.totalReturnAdd2Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD2)));
                    returnLineData.totalReturnAddOtherAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADDOTHER)));

                    returnLineData.totalReturnDis1 = (returnLineData.totalReturnDis1 == null ? BigDecimal.ZERO : returnLineData.totalReturnDis1);
                    returnLineData.totalReturnDis2 = (returnLineData.totalReturnDis2 == null ? BigDecimal.ZERO : returnLineData.totalReturnDis2);
                    returnLineData.totalReturnDis3 = (returnLineData.totalReturnDis3 == null ? BigDecimal.ZERO : returnLineData.totalReturnDis3);
                    returnLineData.totalReturnDisOther = (returnLineData.totalReturnDisOther == null ? BigDecimal.ZERO : returnLineData.totalReturnDisOther);
                    returnLineData.totalReturnAdd1Amount = (returnLineData.totalReturnAdd1Amount == null ? BigDecimal.ZERO : returnLineData.totalReturnAdd1Amount);
                    returnLineData.totalReturnAdd2Amount = (returnLineData.totalReturnAdd2Amount == null ? BigDecimal.ZERO : returnLineData.totalReturnAdd2Amount);
                    returnLineData.totalReturnAddOtherAmount = (returnLineData.totalReturnAddOtherAmount == null ? BigDecimal.ZERO : returnLineData.totalReturnAddOtherAmount);

                    returnLineData.totalReturnAddAmount = returnLineData.totalReturnAdd1Amount.add(returnLineData.totalReturnAdd2Amount);
                    returnLineData.totalReturnNetAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_AMOUNT_NUT)));

                    result.add(returnLineData);

                } while (cItemData.moveToNext());
            }


            for (DiscountCallReturnLineData lineData : returnData.callReturnLineItemData.values()) {
                boolean fined = false;
                if (!lineData.isPromoLine) {
                    for (DiscountCallReturnLineData linePromotionData : result) {
                        if (lineData.productId == linePromotionData.productId &&
                                lineData.isPromoLine == linePromotionData.isPromoLine &&
                                lineData.isFreeItem == linePromotionData.isFreeItem) {
                            fined = true;
                            break;
                        }
                    }
                    if (!fined) {
                        DiscountCallReturnLineData tempData = lineData;
                        tempData.callUniqueId = returnData.callUniqueId;
                        tempData.customerId = returnData.customerId;
                        tempData.returnTotalQty = BigDecimal.ZERO;
                        tempData.returnBigQty = BigDecimal.ZERO;
                        tempData.returnBigQtyId = 0;
                        tempData.returnBigQtyName = null;
                        tempData.priceId = 0;
                        tempData.unitPrice = BigDecimal.ZERO;
                        tempData.totalReturnAmount = BigDecimal.ZERO;
                        tempData.totalReturnDiscount = BigDecimal.ZERO;
                        tempData.totalReturnSupAmount = BigDecimal.ZERO;
                        tempData.totalReturnAddAmount = BigDecimal.ZERO;
                        tempData.totalReturnCharge = BigDecimal.ZERO;
                        tempData.totalReturnTax = BigDecimal.ZERO;
                        tempData.totalReturnDis1 = BigDecimal.ZERO;
                        tempData.totalReturnDis2 = BigDecimal.ZERO;
                        tempData.totalReturnDis3 = BigDecimal.ZERO;
                        tempData.totalReturnDisOther = BigDecimal.ZERO;
                        tempData.totalReturnAdd1Amount = BigDecimal.ZERO;
                        tempData.totalReturnAdd2Amount = BigDecimal.ZERO;
                        tempData.totalReturnAddOtherAmount = BigDecimal.ZERO;
                        tempData.totalReturnNetAmount = BigDecimal.ZERO;
                        result.add(tempData);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }
//    public void deleteAllEVCYTempReturnSummaryFinalById(String evcId)
//    {
//        db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
//    }

    public void deleteAllEVCYTempReturnSummeryFinals() {
        db.delete(DATABASE_TABLE, null, null);
    }

    private Cursor getResult(String evcId) {
//        int status = EVCHeaderSDSDBAdapter.getInstance().getStatusForReturn(evcId);
        int status = 0;

        String query = "";

        if (status == 1) {
            query = "Select RowOrder ,GoodsRef ,UnitQty ,CPriceRef ,ei.UnitRef ,UnitCapasity ,TotalQty \n"
                    + " , (Amount + AddAmount - Discount \n"
                    + "                 + ( CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100) \n"
                    + "                          THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100 AS INT) \n"
                    + "                          ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100 AS INT) +1 END) \n"
                    + "                 + ( CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100) \n"
                    + "                          THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100 AS INT) \n"
                    + "                          ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100 AS INT) +1 END) \n" + ") as AmountNut \n"
                    + " , Discount As Discount ,Amount ,PrizeType ,SupAmount ,AddAmount, UserPrice ,CustPrice \n"
                    + " , PriceId, IFNULL(ei.charge,0) Charge, IFNULL(ei.Tax,0) Tax \n"
                    + " , (CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100) \n"
                    + "         THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100 AS INT) \n"
                    + "         ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100 AS INT) +1 END) as ChargeAmount"
                    + " , (CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100) \n"
                    + "         THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100 AS INT) \n"
                    + "         ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100 AS INT) + 1 END) as TaxAmount \n"
                    + " , 0 Dis1, 0 Dis2, 0 Dis3 , 0 Add1, 0 Add2,  p.ProductCode, p.ProductName "
                    + " , p.SmallUnitId, p.LargeUnitId, pu.ProductUnitName , 0 OtherDiscount, 0 OtherAddition \n"
                    + " FROM " + DATABASE_TABLE + " ei  \n"
                    + " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " p ON ei.GoodsRef = p.ProductId AND evcId='" + evcId + "' \n"
                    + " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pu ON pu.BackOfficeId = ei.UnitRef AND pu.ProductId = p.productId \n"
                    + " WHERE EVCId = '" + evcId + "'";
        } else {
            query = "Select RowOrder ,GoodsRef , UnitQty ,CPriceRef ,ei.UnitRef ,UnitCapasity ,TotalQty \n "
                    + " , (Amount + AddAmount - Discount) AS AmountNut ,Discount As Discount ,Amount ,PrizeType ,SupAmount ,AddAmount ,UserPrice ,CustPrice \n"
                    + " , PriceId , IFNULL(ei.charge,0) Charge, IFNULL(ei.Tax,0) Tax, 0 as ChargeAmount, 0 as TaxAmount \n"
                    + " , Dis1, Dis2, Dis3, Add1, Add2, p.ProductCode, p.ProductName, p.SmallUnitId, p.LargeUnitId, pu.ProductUnitName"
                    + " , OtherDiscount, OtherAddition "
                    + " FROM " + DATABASE_TABLE + " ei "
                    + " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " p ON ei.GoodsRef = p.ProductId AND evcId='" + evcId + "' \n"
                    + " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pu ON pu.BackOfficeId = ei.UnitRef AND pu.ProductId = p.productId \n"
                    + " WHERE EVCId = '" + evcId + "'";
        }

        return db.rawQuery(query, null);

    }
    private Cursor getNewResult() {
        String query = "";

        query = "SELECT r.RowOrder, r.GoodsRef, g.ProductCode\n" +
                "  , g.ProductName\n" +
                "  , NewRetQty/pu.Quantity as UnitQty \n" +
                "  , r.CPriceRef\n" +
                "  , r.RetUnitRef UnitRef\n" +
                "  , pu.Quantity as UnitCapasity\n" +
                "  , NewRetQty as TotalQty\n" +
                "  , Amount-(Dis1+Dis2+Dis3+OtherDiscount)+(Add1+Add2+OtherAddition) as AmountNut\n" +
                "  , Dis1+Dis2+Dis3+OtherDiscount as Discount \n" +
                "  , Amount\n" +
                "  , PrizeType\n" +
                "  , 0 as SupAmount\n" +
                "  , Add1+Add2+OtherAddition as AddAmount\n" +
                "  , UserPrice \n" +
                "  , CustPrice\n" +
                "  , PriceRef as PriceId\n" +
                "  , 0 Charge \n" +
                "  , 0 Tax \n" +
                "  , 0 as ChargeAmount\n" +
                "  , 0 as TaxAmount\n" +
                "  , Dis1 , Dis2 , Dis3 ,  Add1  , Add2 , OtherDiscount, OtherAddition\n" +
                "  , g.SmallUnitId, g.LargeUnitId\n" +
                "  , pu.ProductUnitName\n" +
                "FROM tempSaleItm r\n" +
                "INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId = r.GoodsRef\n" +
                "INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pu ON pu.BackOfficeId = r.RetUnitRef AND pu.ProductId = g.productId\n" +
                "ORDER BY PrizeType, RowOrder";


        return db.rawQuery(query, null);

    }
    private Cursor getRemValues(String newSaleEVCId) {
        String query = "SELECT * \n"
                + " FROM ( \n"
                + " (SELECT IFNULL(SUM(AMOUNT), 0) AS SumDiscountAll FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " WHERE TotalQty > 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(Discount), 0) AS SumRemDiscount FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " WHERE TotalQty = 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(Amount), 0) AS SumAmountAll FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " WHERE TotalQty > 0 and AddAmount <> 0 AND EVCId = '" + newSaleEVCId + "' )  \n"
                + " ,(SELECT IFNULL(SUM(AddAmount), 0) AS SumRemAddAmount FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty = 0 and AddAmount <> 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(Add1), 0) AS SumRemAdd1 FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty = 0 AND AddAmount <> 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(Add2), 0) AS SumRemAdd2 FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty = 0 AND AddAmount <> 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(Dis1), 0) AS SumDis1 FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty = 0 AND Discount <> 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(Dis2), 0) AS SumDis2 FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty = 0 AND Discount <> 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " , (SELECT IFNULL(SUM(Dis3), 0) AS SumDis3 FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty = 0 AND Discount <> 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " , (SELECT IFNULL(SUM(OtherAddition), 0) AS OtherAddition FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty <= 0 AND AddAmount <> 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " , (SELECT IFNULL(SUM(OtherDiscount), 0) AS OtherDiscount FROM " + EVCTempReturnItemSummarySDSDBAdapter.DATABASE_TABLE + " Where TotalQty<=0 and Discount<>0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + ")X";

        return db.rawQuery(query, null);
    }

    private void updateRemAmount(String newSaleEVCId, double sumRemDiscount, double sumRemAddAmount, double sumRemAdd1, double sumRemAdd2,
                                 double sumRemDis1, double sumRemDis2, double sumRemDis3,
                                 double sumRemOtherAddition, double sumRemOtherDiscount) {
        String query = "UPDATE " + DATABASE_TABLE
                + " SET RemainDiscount = RemainDiscount + (" + sumRemDiscount + " - (SELECT SUM(RemainDiscount) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainAddAmount = RemainAddAmount + (" + sumRemAddAmount + " - (SELECT SUM(RemainAddAmount) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainAdd1 = RemainAdd1 + (" + sumRemAdd1 + " - (SELECT SUM(RemainAdd1) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainAdd2 = RemainAdd2 + (" + sumRemAdd2 + " - (SELECT SUM(RemainAdd2) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainOtherAddition=RemainOtherAddition+(" + sumRemOtherAddition + " -(Select Sum(RemainOtherAddition) From  " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n"
                + " , RemainDis1 = RemainDis1 + (" + sumRemDis1 + " - (SELECT SUM(RemainDis1) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainDis2 = RemainDis2 + (" + sumRemDis2 + " - (SELECT SUM(RemainDis2) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainDis3 = RemainDis3 + (" + sumRemDis3 + " - (SELECT SUM(RemainDis3) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainOtherDiscount=RemainOtherDiscount+(" + sumRemOtherDiscount + " -(Select Sum(RemainOtherDiscount) From  " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n"
                + " Where GoodsRef = (Select GoodsRef From " + DATABASE_TABLE + " where PrizeType=0 AND EVCId = '" + newSaleEVCId + "' LIMIT 1) \n"
                + " AND PrizeType=0";
        db.execSQL(query);

    }
}
