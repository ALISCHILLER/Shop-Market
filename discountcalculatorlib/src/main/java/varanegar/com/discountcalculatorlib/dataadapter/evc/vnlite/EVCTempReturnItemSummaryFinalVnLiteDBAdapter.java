package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import android.database.Cursor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProductUnit;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

/**
 * Created by m.aghajani on 7/4/2016.
 */
public class EVCTempReturnItemSummaryFinalVnLiteDBAdapter extends DiscountBaseDataAdapter {

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
    public final String KEY_EVC_TEMP_RETURN_REMAIN_DIS1 = "RemainDis1";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_DIS = "RemainDis2";
    public final String KEY_EVC_TEMP_RETURN_REMAIN_DIS3 = "RemainDis3";



    public static final String DATABASE_TABLE = "EVCTempReturnItemSummaryFinalVnLite";
    private static String TAG = "EVCTempReturnItemSummaryFinalVnLiteDBAdapter";
    private static EVCTempReturnItemSummaryFinalVnLiteDBAdapter instance;

    public EVCTempReturnItemSummaryFinalVnLiteDBAdapter()
    {
    }

    public void deleteAllByEVCId(String evcId)
    {
        db.delete(DATABASE_TABLE, "EVCId='" + evcId + "'", null);
    }


    public static EVCTempReturnItemSummaryFinalVnLiteDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempReturnItemSummaryFinalVnLiteDBAdapter();
        }

        return instance;

    }

    //#tmpTable3
    public void fillEVCTempReturnItemSummaryFinal(String newSaleEVCId)
    {
        BigDecimal sumAddAll,sumDiscountAll, sumRemDiscount, sumAmountAll, sumRemAddAmount;

        sumDiscountAll = sumAddAll = sumRemDiscount = sumAmountAll = sumRemAddAmount = BigDecimal.ZERO;

        Cursor c = getRemValues(newSaleEVCId);
        if(c != null && c.moveToFirst())
        {
            sumDiscountAll = new BigDecimal(c.getDouble(0));
            sumRemDiscount = new BigDecimal(c.getDouble(1));
            sumAddAll = new BigDecimal(c.getDouble(2));
            sumRemAddAmount = new BigDecimal(c.getDouble(3));
        }

        if(sumAddAll.compareTo(BigDecimal.ZERO) == 0)
            sumAddAll = BigDecimal.ONE;
        if(sumDiscountAll.compareTo(BigDecimal.ZERO) == 0)
            sumDiscountAll = BigDecimal.ONE;



        String query = "INSERT INTO " + DATABASE_TABLE
                + " (EVCId, RowOrder, GoodsRef, "
                + " UnitQty, CPriceRef, "
                + "  TotalQty, "
                + " AmountNut, Discount, "
                + " RemainDiscount, "
                + " RemainAddAmount, "
                + " Amount, PrizeType \n"
                + " ,AddAmount "
                + " ,UserPrice, CustPrice, PriceId, Charge, Tax, DetailId \n"
                + " ) \n"
                + " SELECT EVCId, RowOrder,  GoodsRef, "
                + " UnitQty, CPriceRef,  "
                + " TotalQty, "
                + " AmountNut, Discount "
                + " , IFNULL( CAST ( " + sumRemDiscount.doubleValue() + " * Discount / " + sumDiscountAll.doubleValue() + " AS INT), 0) As DiscountAmount2 \n"
                + " , IFNULL( CAST ( " + sumRemAddAmount.doubleValue() + " * AddAmount / " + sumAddAll.doubleValue() + " AS INT), 0) As AddAmount2 \n"
                + " , Amount ,PrizeType "
                + " , AddAmount \n"
                + "  , UserPrice, CustPrice, PriceId, IFNULL(Charge, 0), IFNULL(Tax, 0), IFNULL(DetailId, 0) AS DetailId  \n"
                + " FROM " + EVCTempReturnItemSummaryVnLiteDBAdapter.DATABASE_TABLE
                + " WHERE TotalQty <> 0 AND EVCId = '" + newSaleEVCId +"'" ;

        db.execSQL(query);

        updateRemAmount(newSaleEVCId, sumRemDiscount, sumRemAddAmount );
    }

    public void updateDiscountAndAddAmount(String newSaleEVCId)
    {
        /*
        String query = "UPDATE " + DATABASE_TABLE
                + " SET Discount = Discount + RemainDiscount \n"
                + " , AddAmount = AddAmount + RemainAddAmount \n"
                + " , Add1 = Add1 + RemainAdd1 \n"
                + " , Add2 = Add2 + RemainAdd2 \n"
                + " , Dis1 = Dis1 + RemainDis1 \n"
                + " , Dis2 = Dis2 + RemainDis2 \n"
                + " , Dis3 = Dis3 + RemainDis3 \n"
                + " , AmountNut = Amount - Discount - RemainDiscount + AddAmount + RemainAddAmount\n"
                + " Where PrizeType=0 AND EVCId ='" + newSaleEVCId +"'";
*/
        String query = "UPDATE " + DATABASE_TABLE
                + " SET Discount = Discount + RemainDiscount\n "
                + " , AddAmount = AddAmount + RemainAddAmount\n "
                + " , AmountNut = Amount - Discount - RemainDiscount + AddAmount + RemainAddAmount\n "
                + " WHERE PrizeType=0 AND EVCId ='" + newSaleEVCId +"'";

        db.execSQL(query);
    }

    public List<DiscountCallReturnLineData> getCallWithPromo(String evcId, DiscountCallReturnData returnData)
    {
        List<DiscountCallReturnLineData> result = new ArrayList<>();
        Cursor cItemData = null;


        try
        {
            cItemData = getResult(evcId);

            if(cItemData != null && cItemData.moveToFirst())
            {
                int unitQty = 0;

                do {
                    DiscountCallReturnLineData returnLineData = new DiscountCallReturnLineData();

                    returnLineData.productId = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_GOODS_REF));

                    returnLineData.productCode = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTCODE));
                    returnLineData.productName = cItemData.getString(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_PRODUCTNAME));
                    returnLineData.returnSmallQtyId = cItemData.getInt(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_SMALLUNITID));
                    returnLineData.returnBigQtyId  = cItemData.getInt(cItemData.getColumnIndex(DiscountProductDBAdapter.KEY_PRODUCT_LARGEUNITID));

                    returnLineData.isFreeItem = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRIZE_TYPE));

                    if (returnLineData.isFreeItem == 0)
                    {
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
                        returnLineData.referenceQty = mainLine.referenceQty;
                        returnLineData.returnReasonId = mainLine.returnReasonId;
                    }
                    else
                    {
                        returnLineData.returnLineUniqueId = UUID.randomUUID().toString();
                        returnLineData.callUniqueId = returnLineData.returnLineUniqueId;
                        returnLineData.returnUniqueId = returnData.returnUniqueId;
                        returnLineData.customerId = returnData.customerId;
                        returnLineData.sortId =99;
                        returnLineData.indexInfo = returnLineData.productId;
                        returnLineData.callUniqueId = returnData.callUniqueId;
                        returnLineData.weight = BigDecimal.ZERO;
                        returnLineData.referenceId = returnData.returnRefId;
                        returnLineData.referenceNo = returnData.returnRefNo;
                        returnLineData.dealerId = returnData.dealerId;
                        returnLineData.referenceQty = BigDecimal.ZERO;
                        returnLineData.returnReasonId = returnData.returnReasonId;

                        returnLineData.returnProductTypeId = DiscountCallReturnLineData.ReturnProductTypeEnum.INTACT.value();
                    }


                    returnLineData.returnTotalQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_TOTAL_QTY)));
                    unitQty = cItemData.getInt(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_QTY));

                    if(unitQty == 0)
                    {
                        returnLineData.returnBigQty = BigDecimal.ZERO;
                        returnLineData.returnBigQtyId = 0l;
                        returnLineData.returnBigQtyName = "";
                        returnLineData.returnSmallQty = returnLineData.returnTotalQty;
                    }
                    else if(returnLineData.returnTotalQty.intValue() % unitQty == 0)
                    {
                        returnLineData.returnBigQty = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_UNIT_QTY)));
                        returnLineData.returnBigQtyId = cItemData.getLong(cItemData.getColumnIndex("LargeUnitId"));
                       //TODO? returnLineData.returnBigQtyName = cItemData.getString(cItemData.getColumnIndex(DiscountProductUnitDBAdapter.KEY_PUNIT_PRODUCTUNITNAME));
                    }
                    else
                    {
                        DiscountProductUnit unit = DiscountProductUnitDBAdapter.getInstance().getProductUnitById(returnLineData.productId, returnLineData.returnBigQtyId);
                        //ProductUnit BigU+nit = ProductUnitDBAdapter.getInstance().getProductUnitById(wasteItem.productId, wasteItem.returnBigQtyId);
                        if (unit != null) {
                            returnLineData.returnBigQty = returnLineData.returnTotalQty.divide(unit.quantity, 0, RoundingMode.FLOOR);
                            returnLineData.returnSmallQty = returnLineData.returnTotalQty.subtract(returnLineData.returnBigQty.multiply(unit.quantity));
                        }
                        else{
                            returnLineData.returnBigQty = BigDecimal.ZERO;
                            unit = DiscountProductUnitDBAdapter.getInstance().getProductUnitById(returnLineData.productId, returnLineData.returnSmallQtyId);
                            returnLineData.returnSmallQty = returnLineData.returnTotalQty;
                        }
                        returnLineData.returnBigQtyId = unit.productUnitId;
                        returnLineData.returnBigQtyName = unit.productUnitName;
                    }

                    returnLineData.priceId = cItemData.getLong(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_PRICE_ID));
                    returnLineData.unitPrice = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_CUST_PRICE)));
                    returnLineData.totalReturnAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_AMOUNT)));
                    returnLineData.totalReturnDiscount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DISCOUNT)));
                    returnLineData.totalReturnAddAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD_AMOUNT)));
                    returnLineData.totalReturnCharge = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("ChargeAmount")));
                    returnLineData.totalReturnTax = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex("TaxAmount")));
                    /*
                    returnLineData.totalReturnDis1 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS1)));
                    returnLineData.totalReturnDis2 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS2)));
                    returnLineData.totalReturnDis3 = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_DIS3)));
                    returnLineData.totalReturnAdd1Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD1)));
                    returnLineData.totalReturnAdd2Amount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex(KEY_EVC_TEMP_RETURN_ADD2)));
                    */
                    returnLineData.totalReturnNetAmount = new BigDecimal(cItemData.getDouble(cItemData.getColumnIndex( KEY_EVC_TEMP_RETURN_AMOUNT_NUT)));

                    if (returnLineData.returnLineUniqueId  == null)
                        returnLineData.returnLineUniqueId = UUID.randomUUID().toString();
                    result.add(returnLineData);

                }while (cItemData.moveToNext());
            }
        }
        catch (Exception ex)
        {
            String err = "getCallWithPromo Error :  " + ex.getStackTrace();
            Timber.e(err);
            throw new DiscountException(err);
        }

        return result;
    }

//    public void deleteAllEVCYTempReturnSummaryFinalById(String evcId)
//    {
//        db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
//    }

    public void deleteAllEVCYTempReturnSummeryFinals()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

    /*usp_GetRetExtraValue*/
    private Cursor getResult(String evcId)
    {
        String query = "";
        /*
        query = "Select RowOrder ,GoodsRef ,UnitQty ,CPriceRef ,p.SmallUnitId as UnitRef ,TotalQty \n"

                + " , (Amount + AddAmount - Discount \n"
                + "                 + ( CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100) \n"
                + "                          THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100 AS INT) \n"
                + "                          ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) /100 AS INT) +1 END) \n"
                + "                 + ( CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100) \n"
                + "                          THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100 AS INT) \n"
                + "                          ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) /100 AS INT) +1 END) \n"+") as AmountNut \n"
                + " , Discount As Discount ,Amount ,PrizeType ,AddAmount, UserPrice ,CustPrice \n"
                + " , PriceId, IFNULL(ei.charge,0) Charge, IFNULL(ei.Tax,0) Tax \n"
                + " , (CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100) \n"
                + "         THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100 AS INT) \n"
                + "         ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.charge,0) / 100 AS INT) +1 END) as ChargeAmount"
                + " , (CASE WHEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100 AS INT) = ((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100) \n"
                + "         THEN CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100 AS INT) \n"
                + "         ELSE CAST((Amount + AddAmount - Discount) * IFNULL(ei.Tax,0) / 100 AS INT) + 1 END) as TaxAmount \n"
                + " , 0 Dis1, 0 Dis2, 0 Dis3 , 0 Add1, 0 Add2, p.ProductCode, p.ProductName, p.SmallUnitId, p.LargeUnitId, pu.ProductUnitName \n"
                + " FROM " + DATABASE_TABLE + " ei  \n"
                + " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " p ON ei.GoodsRef = p.ProductId AND evcId='" + evcId + "' \n"
                + " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pu ON pu.ProductUnitId = p.SmallUnitId AND pu.ProductId = p.productId \n"
                + " WHERE EVCId = '" + evcId + "'";


            query = "Select RowOrder ,GoodsRef , UnitQty ,CPriceRef ,p.SmallUnitId, TotalQty \n "
                    + " , (Amount + AddAmount - Discount) AS AmountNut ,Discount As Discount ,Amount ,PrizeType , AddAmount ,UserPrice ,CustPrice \n"
                    + " , PriceId , IFNULL(ei.charge,0) Charge, IFNULL(ei.Tax,0) Tax, 0 as ChargeAmount, 0 as TaxAmount \n"
                    + " , 0 Dis1, 0 Dis2, 0 Dis3 , 0 Add1, 0 Add2, p.ProductCode, p.ProductName, p.SmallUnitId, p.LargeUnitId, pu.ProductUnitName "
                    + " FROM " + DATABASE_TABLE + " ei "
                    + " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " p ON ei.GoodsRef = p.ProductId AND evcId='" + evcId + "' \n"
                    + " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pu ON pu.ProductUnitId = p.SmallUnitId AND pu.ProductId = p.productId \n"
                    + " WHERE EVCId = '" + evcId + "'";
*/
        query = "Select RowOrder ,GoodsRef , UnitQty ,CPriceRef ,p.SmallUnitId, TotalQty, \n "+
                " Discount As Discount ," +
                " Amount ,PrizeType , " +
                " AddAmount ,UserPrice ," +
                " CustPrice, \n"+
                " PriceId , " +
                " IFNULL(ei.charge,0) Charge, " +
                " IFNULL(ei.Tax,0) Tax, " +
        "IFNULL(ROUND(ROUND(( ( Amount - Discount + AddAmount ) * IFNULL(ei.Tax, 0) / 100 ), 0),0), 0) AS TaxAmount  , \n"+
        "IFNULL(ROUND(ROUND(( ( Amount - Discount + AddAmount ) * IFNULL(ei.Charge, 0) / 100 ), 0),0), 0) AS ChargeAmount , \n"+
        "( Amount + AddAmount - Discount + IFNULL(ROUND(ROUND(( ( Amount - Discount + AddAmount ) * IFNULL(ei.Tax, 0) \n"+
        "        / 100 ), 4),0), 0) "+
        "        + IFNULL(ROUND(ROUND(( ( Amount - Discount + AddAmount ) * IFNULL(ei.Charge, 0) / 100 ), 0),0), 0) ) AS AmountNut , \n" +
        " 0 Dis1, 0 Dis2, 0 Dis3 , 0 Add1, 0 Add2, p.ProductCode, p.ProductName, p.SmallUnitId, p.LargeUnitId "
                + " FROM " + DATABASE_TABLE + " ei "
                + " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " p ON ei.GoodsRef = p.ProductId AND evcId='" + evcId + "' \n"
                + " WHERE EVCId = '" + evcId + "' AND TotalQty > 0 ";

        return db.rawQuery(query, null);

    }

    private Cursor getRemValues(String newSaleEVCId)
    {
        String query  = "SELECT * \n"
                + " FROM ( \n"
                + "  (SELECT IFNULL(SUM(Discount), 1) AS SumDiscountAll FROM " + EVCTempReturnItemSummaryVnLiteDBAdapter.DATABASE_TABLE + " WHERE TotalQty > 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(Discount), 0) AS SumRemDiscount FROM " + EVCTempReturnItemSummaryVnLiteDBAdapter.DATABASE_TABLE + " WHERE TotalQty = 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + " ,(SELECT IFNULL(SUM(AddAmount),1) AS SumAddAll FROM " + EVCTempReturnItemSummaryVnLiteDBAdapter.DATABASE_TABLE + " WHERE TotalQty > 0 AND EVCId = '" + newSaleEVCId + "' )  \n"
                + " ,(SELECT IFNULL(SUM(AddAmount),0) AS SumRemAddAmount FROM " + EVCTempReturnItemSummaryVnLiteDBAdapter.DATABASE_TABLE + " Where TotalQty = 0 AND EVCId = '" + newSaleEVCId + "')  \n"
                + ")X";

        return db.rawQuery(query, null);
    }

    private void updateRemAmount(String newSaleEVCId, BigDecimal sumRemDiscount, BigDecimal sumRemAddAmount )
    {

        String query = "UPDATE " + DATABASE_TABLE
                + " SET RemainDiscount = RemainDiscount + (" + sumRemDiscount + " - (SELECT SUM(RemainDiscount) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " , RemainAddAmount = RemainAddAmount + (" + sumRemAddAmount + " - (SELECT SUM(RemainAddAmount) FROM " + DATABASE_TABLE + " WHERE EVCId = '" + newSaleEVCId + "'))\n "
                + " Where GoodsRef = (Select GoodsRef From " + DATABASE_TABLE + " where PrizeType=0 AND EVCId = '" + newSaleEVCId + "' LIMIT 1) \n"
                + " AND PrizeType=0";

        db.execSQL(query);

    }
}
