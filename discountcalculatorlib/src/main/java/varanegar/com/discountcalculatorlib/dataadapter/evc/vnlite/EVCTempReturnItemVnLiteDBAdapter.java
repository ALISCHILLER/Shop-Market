package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;

public class EVCTempReturnItemVnLiteDBAdapter extends DiscountBaseDataAdapter
{

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
    public final String KEY_EVC_TEMP_RETURN_AMOUNT = "Amount";
    public final String KEY_EVC_TEMP_RETURN_PRIZE_TYPE = "PrizeType";
    public final String KEY_EVC_TEMP_RETURN_SUP_AMOUNT = "SupAmount";
    public final String KEY_EVC_TEMP_RETURN_ADD_AMOUNT = "AddAmount";
    public final String KEY_EVC_TEMP_RETURN_ADD1 = "Add1";
    public final String KEY_EVC_TEMP_RETURN_ADD2 = "Add2";
    public final String KEY_EVC_TEMP_RETURN_USER_PRICE = "UserPrice";
    public final String KEY_EVC_TEMP_RETURN_CUST_PRICE = "CustPrice";
    public final String KEY_EVC_TEMP_RETURN_PRICE_ID = "PriceId";
    public final String KEY_EVC_TEMP_RETURN_CHARGE = "Charge";
    public final String KEY_EVC_TEMP_RETURN_TAX = "Tax";
    public final String KEY_EVC_TEMP_RETURN_DIS1 = "Dis1";
    public final String KEY_EVC_TEMP_RETURN_DIS2 = "Dis2";
    public final String KEY_EVC_TEMP_RETURN_DIS3 = "Dis3";



    public static final String DATABASE_TABLE = "EVCTempReturnItemVnLite";
    private static String TAG = "EVCTempReturnItemVnLiteDBAdapter";
    private static EVCTempReturnItemVnLiteDBAdapter instance;

    public EVCTempReturnItemVnLiteDBAdapter()
    {
    }

    public static EVCTempReturnItemVnLiteDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempReturnItemVnLiteDBAdapter();
        }

        return instance;

    }

    public void deleteAllByEVCId(String evcId)
    {
        db.delete(DATABASE_TABLE, "EVCId='" + evcId + "'", null);
    }

    public void fillEVCTempReturnItem(String saleEVCId, String newSaleEVCId)
    {
        // INTO    #tmpTable
        String sql = "INSERT INTO " + DATABASE_TABLE + " (EVCId, RowOrder, GoodsRef, UnitQty, CPriceRef, UnitRef, UnitCapasity, TotalQty, AmountNut, Discount, Amount, PrizeType \n"
                /*,SupAmount ,Add1, Add2,Dis1, Dis2, Dis3*/
                + " , AddAmount, UserPrice, CustPrice, PriceId, Charge, Tax, DetailId, ReduceOfQty) \n"
                + " SELECT '" + newSaleEVCId + "', * "
                + " FROM ("
                + " SELECT  Min(RowOrder) as RowOrder , GoodsRef "
                + " , SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then UnitQty Else -1 * UnitQty End) As UnitQty \n"//UnitQty,PackQty

                + " ,CPriceRef, EI.UnitRef , UnitCapasity "
                + " ,SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then TotalQty Else -1 * TotalQty End) As TotalQty \n " //Qty,

                + " ,SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then AmountNut WHEN EVCRef = '" + newSaleEVCId + "' AND AmountNut < 0 THEN 0 WHEN EVCRef= '" + newSaleEVCId + "' THEN - AmountNut End) AS AmountNut \n" //NetAmount
                + " ,SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then Discount WHEN EVCRef = '" + newSaleEVCId + "' AND Discount < 0 THEN 0 WHEN EVCRef ='" +newSaleEVCId+ "' THEN  -1 * Discount End) As Discount \n" //DiscountAmount
                + " ,Sum(CASE WHEN EVCRef = '" + saleEVCId + "' Then Amount WHEN EVCRef = '" + newSaleEVCId + "' AND Amount < 0 THEN 0 WHEN EVCRef = '" + newSaleEVCId + "' THEN -Amount End) AS Amount \n"
                + " ,PrizeType \n" //IsPrize
               // + " ,SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then SupAmount Else -SupAmount End) AS SupAmount \n"
                + " ,SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then AddAmount WHEN EVCRef = '" + newSaleEVCId + "' AND AddAmount < 0 THEN 0 WHEN EVCRef = '" + newSaleEVCId + "' THEN  -1 * AddAmount End) As AddAmount \n"
/*
                + " ,IFNULL((SELECT SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then es.AddAmount WHEN EVCRef= '" + newSaleEVCId + "' AND es.AddAmount<0 THEN 0 WHEN EVCRef='" + newSaleEVCId + "' THEN -1 * es.AddAmount End) \n"
                + "             FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " es \n"
                + "             INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " d on d.Id = es.DisRef \n"
                + "             INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ei2 on es.EvcItemRef=ei2._Id and ei2.GoodsRef=ei.GoodsRef \n"
                + "             WHERE d.DisAccRef=4), 0) as Add1 \n"
                + " ,IFNULL((SELECT SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then es.AddAmount WHEN EVCRef = '" + newSaleEVCId + "' AND es.AddAmount<0 THEN 0 WHEN EVCRef = '" + newSaleEVCId + "' THEN -1 * es.AddAmount End) \n"
                + "             FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +  " es \n"
                + "             INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef \n"
                + "             INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ei2 on es.EvcItemRef=ei2._Id and ei2.GoodsRef=ei.GoodsRef \n"
                + "             WHERE d.DisAccRef=5), 0) as Add2 \n"
*/
                + " ,UserPrice ,CustPrice ,PriceId \n"  //CustPrice = UnitPrice
                + " ,SUM(CASE WHEN EVCRef='" + saleEVCId + "' Then chargePercent Else 0 End) AS Charge \n"
                + " ,SUM(CASE When EVCRef='" + saleEVCId + "' Then taxPercent Else 0 End) AS tax \n"
/*                + " ,IFNULL((SELECT SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then es.Discount WHEN EVCRef = '" + newSaleEVCId + "' AND es.Discount < 0 THEN 0 WHEN EVCRef='" + newSaleEVCId + "' THEN -1 * es.Discount End) \n"
                + "             FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " es \n"
                + "             INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef \n"
                + "             INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ei2 on es.EvcItemRef=ei2._Id and ei2.GoodsRef=ei.GoodsRef \n"
                + "             WHERE d.DisAccRef=1), 0) as Dis1 \n"
                + " ,IFNULL((SELECT SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then es.Discount WHEN EVCRef='" + newSaleEVCId + "' AND es.Discount < 0 THEN 0 WHEN EVCRef='" + newSaleEVCId + "' THEN -1 * es.Discount End) \n"
                + "             FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE+ " es \n"
                + "             INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef \n"
                + "             INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ei2 on es.EvcItemRef = ei2._Id and ei2.GoodsRef=ei.GoodsRef\n"
                + "             WHERE d.DisAccRef=2), 0) as Dis2"
                + " ,IFNULL((SELECT SUM(CASE WHEN EVCRef = '" + saleEVCId + "' Then es.Discount WHEN EVCRef = '" + newSaleEVCId + "' AND es.Discount < 0 THEN 0 WHEN EVCRef ='" + newSaleEVCId + "' THEN -1 * es.Discount End) \n"
                + "             FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " es \n"
                + "             INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " d on d.Id=es.DisRef \n"
                + "             INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ei2 on es.EvcItemRef = ei2._Id and ei2.GoodsRef=ei.GoodsRef \n"
                + "             WHERE d.DisAccRef=3), 0) as Dis3"
                */
                + " ,_id , ReduceOfQty "
                + " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n"
                + " WHERE EVCRef = '" + saleEVCId +"' OR EVCRef = '" + newSaleEVCId + "' \n"
                + " GROUP BY GoodsRef, CPriceRef ,EI.UnitRef , UnitCapasity ,PrizeType,UserPrice ,CustPrice ,PriceId,"
                + "  ReduceOfQty   \n"
                + " )X \n";
/*                + " WHERE Discount <> 0 \n"
                + " OR AddAmount <> 0 \n"
                + " OR (PrizeType=1 AND TotalQty<>0) \n"
                + " OR Amount<>0 OR CustPrice=0 ";
*/
        db.execSQL(sql);
    }

    public void update1(String saleEVCId,  String newSaleEVCId)
    {
/*
        String query = "UPDATE " + DATABASE_TABLE
                + " SET TotalQty = TotalQty + (SELECT TotalQty FROM " + DATABASE_TABLE + " I WHERE I.GoodsRef = " + DATABASE_TABLE + ".GoodsRef AND PrizeType=1) \n"
                + " WHERE PrizeType=0 \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " Where PrizeIncluded=1 \n"
                + "                  AND ID IN (SELECT DisRef FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " WHERE EVCItemRef in (Select _id From " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " Where EVCRef = '" + saleEVCId + "'))) \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " WHERE EVCRef = '" + newSaleEVCId + "' AND TotalQty<>0 AND PrizeType=1)";
                */
        String query = "UPDATE " + DATABASE_TABLE
                + " SET TotalQty = TotalQty + IFNULL((SELECT TotalQty " +
                                "   FROM " + DATABASE_TABLE + " T " +
                                "   WHERE T.GoodsRef = " + DATABASE_TABLE + ".GoodsRef " +
                                "   AND IFNULL(T.TotalQty, 0) < 0  \n" +
                                "   AND ABS(IFNULL(t.TotalQty, 0)) < IFNULL(" + DATABASE_TABLE + ".TotalQty, 0)  " +
                                "   AND T.PrizeType=0) ,0) \n"
                + " WHERE IFNULL(TotalQty,0)> 0 "
                + " AND PrizeType=1";

        db.execSQL(query);
    }

    public void resetUnitPrice(String saleEVCId,  String newSaleEVCId)
    {
        //update    #tmpTable set UnitPrice=0 where isprize=1
        String query = "UPDATE " + DATABASE_TABLE
                + " SET CustPrice = 0 WHERE PrizeType=1 ";

        db.execSQL(query);
    }

    public void update2(String saleEVCId, String newSaleEVCId)
    {
        String query = "UPDATE " + DATABASE_TABLE
                + " SET UnitQty = CAST((TotalQty/UnitCapasity) AS INT) \n"
                + " , Amount = TotalQty*CustPrice \n"
                + " , AmountNut = TotalQty*CustPrice \n"
                + " , Discount=0 \n"
                + " , AddAmount=0 \n"
                + " WHERE PrizeType = 0 \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " Where PrizeIncluded=1 \n"
                + "                  AND ID IN (SELECT DisRef FROM " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " WHERE EVCItemRef in (Select _id From " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " Where EVCRef = '" + saleEVCId + "' ))) \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " WHERE EVCRef= '" + newSaleEVCId + "' AND TotalQty <> 0 AND PrizeType=1)";

        db.execSQL(query);
    }

    public void deleteAllEVCYTempReturnById(String evcId)
    {
        db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
    }

    public void deleteAllEVCYTempReturns()
    {
        db.delete(DATABASE_TABLE, null, null);
    }
}
