package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19.DisAccDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;

/**
 * Created by m.aghajani on 7/4/2016.
 */
public class EVCTempReturnItemSDSDBAdapter extends DiscountBaseDataAdapter
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



    public static final String DATABASE_TABLE = "EVCTempReturnItemSDS";
    private static String TAG = "EVCTempReturnItemSDSDBAdapter";
    private static EVCTempReturnItemSDSDBAdapter instance;

    public EVCTempReturnItemSDSDBAdapter()
    {
    }


    public static EVCTempReturnItemSDSDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempReturnItemSDSDBAdapter();
        }

        return instance;

    }

    /*SLE.usp_GetRetExtraValue*/
    public void fillEVCTempReturnItem(String saleEVCId, String newSaleEVCId, int prizeCalcType)
    {
        //Into #tmpTable
        String sql = "INSERT INTO "+DATABASE_TABLE+" (EVCId, RowOrder, GoodsRef, FreeReasonId, UserPrice ,CustPrice ,PriceRef\n" +
                ",UnitQty, CPriceRef, UnitRef, UnitCapasity,  TotalQty, AmountNut, Discount, Amount, PrizeType \n" +
                " ,SupAmount, AddAmount,DisAmount1, DisAmount2, DisAmount3, AddAmount1, AddAmount2,OtherDiscount,OtherAddition, Charge, Tax) \n" +
                " SELECT '"+  newSaleEVCId  +"', * \n" +
                " FROM (\n" +
                "   SELECT  Min(RowOrder) as RowOrder , GoodsRef,FreeReasonId ,UserPrice ,CustPrice ,PriceId\n" +
                "   ,SUM(CASE WHEN EVCRef = '"+  saleEVCId  +"' Then UnitQty Else -1 * UnitQty End) As UnitQty \n" +
                "   ,CPriceRef, g.SmallUnitId , 1 as UnitCapasity \n" +
                "   ,SUM(CASE WHEN EVCRef = '"+  saleEVCId  +"' Then TotalQty Else -1 * TotalQty End) As TotalQty  \n" +
                "   ,SUM(CASE WHEN EVCRef = '"+  saleEVCId  +"' Then AmountNut WHEN EVCRef = '"+ newSaleEVCId  +"' AND AmountNut < 0 THEN 0 WHEN EVCRef= '"+ newSaleEVCId +"' THEN - AmountNut End) AS AmountNut \n" +
                "   ,SUM(CASE WHEN EVCRef = '"+ saleEVCId  +"' Then Discount WHEN EVCRef = '"+ newSaleEVCId  +"' AND Discount < 0 THEN 0 WHEN EVCRef ='"+ newSaleEVCId +"' THEN  -1 * Discount End) As Discount \n" +
                "   ,Sum(CASE WHEN EVCRef = '"+  saleEVCId  +"' Then Amount WHEN EVCRef = '"+ newSaleEVCId  +"' AND Amount < 0 THEN 0 WHEN EVCRef = '"+ newSaleEVCId +"' THEN -Amount End) AS Amount \n" +
                "   ,PrizeType \n" +
                "   ,SUM(CASE WHEN EVCRef ='"+  saleEVCId  +"' Then SupAmount Else -SupAmount End) AS SupAmount \n" +
                "   ,SUM(CASE WHEN EVCRef ='"+  saleEVCId  +"' Then AddAmount WHEN EVCRef = '"+  newSaleEVCId  +"' AND AddAmount < 0 THEN 0 WHEN EVCRef = '"+ newSaleEVCId +"' THEN  -1 * AddAmount End) As AddAmount \n" +
                "   , SUM(Case When EVCRef='"+  saleEVCId  +"' Then IFNULL(ei.EvcItemDis1, 0) WHEN EVCRef='"+  newSaleEVCId  +"' AND ifnull(EvcItemDis1, 0)<0 THEN 0 WHEN EVCRef='"+ newSaleEVCId +"' THEN  -1 * ifnull(ei.EvcItemDis1, 0) End) As DisAmount1 \n" +
                "   , SUM(Case When EVCRef='"+  saleEVCId  +"' Then IFNULL(ei.EvcItemDis2, 0) WHEN EVCRef='"+  newSaleEVCId  +"' AND ifnull(EvcItemDis2, 0)<0 THEN 0 WHEN EVCRef='"+ newSaleEVCId +"' THEN  -1 * ifnull(ei.EvcItemDis2, 0) End) As DisAmount2 \n" +
                "   , SUM(Case When EVCRef='"+  saleEVCId  +"' Then IFNULL(ei.EvcItemDis3, 0) WHEN EVCRef='"+  newSaleEVCId  +"' AND ifnull(EvcItemDis3, 0)<0 THEN 0 WHEN EVCRef='"+ newSaleEVCId +"' THEN  -1 * ifnull(ei.EvcItemDis3, 0) End) As DisAmount3 \n" +
                "   , SUM(Case When EVCRef='"+  saleEVCId  +"' Then IFNULL(ei.EvcItemAdd1, 0) WHEN EVCRef='"+  newSaleEVCId  +"' AND ifnull(EvcItemAdd1, 0)<0 THEN 0 WHEN EVCRef='"+ newSaleEVCId +"' THEN  -1 * ifnull(ei.EvcItemAdd1, 0) End) As AddAmount1 \n" +
                "   , SUM(Case When EVCRef='"+  saleEVCId  +"' Then IFNULL(ei.EvcItemAdd2, 0) WHEN EVCRef='"+  newSaleEVCId  +"' AND ifnull(EvcItemAdd2, 0)<0 THEN 0 WHEN EVCRef='"+ newSaleEVCId +"' THEN  -1 * ifnull(ei.EvcItemAdd2, 0) End) As AddAmount2 \n" +
                "   , SUM(Case When EVCRef='"+  saleEVCId  +"' Then IFNULL(ei.EvcItemOtherDiscount, 0) WHEN EVCRef='"+  newSaleEVCId  +"' AND ifnull(EvcItemOtherDiscount, 0)<0 THEN 0 WHEN EVCRef='"+ newSaleEVCId +"' THEN  -1 * ifnull(ei.EvcItemOtherDiscount, 0) End) As OtherDiscount \n" +
                "   , SUM(Case When EVCRef='"+  saleEVCId  +"' Then IFNULL(ei.EvcItemOtherAddition, 0) WHEN EVCRef='"+  newSaleEVCId  +"' AND ifnull(EvcItemOtherAddition, 0)<0 THEN 0 WHEN EVCRef='"+ newSaleEVCId +"' THEN  -1 * ifnull(ei.EvcItemOtherAddition, 0) End) As OtherAddition \n" +
                "   , 0 as Charge \n" +
                "   , 0 as Tax \n" +
                " FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI \n"+
                " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " G On G.ProductId = EI.GoodsRef \n"+
                " WHERE EVCRef = '"+  saleEVCId+ "' OR EVCRef = '"+  newSaleEVCId +"'"
                + " GROUP BY GoodsRef, CPriceRef /*,EI.UnitRef , UnitCapasity*/ ,PrizeType,UserPrice ,CustPrice ,PriceId, G.SmallUnitId, FreeReasonId  \n"
                + " )X \n"
                + " WHERE Discount <> 0 \n"
                + " OR AddAmount <> 0 \n"
                + " OR (PrizeType=1 AND TotalQty<>0) \n"
                + " OR Amount<>0 OR CustPrice=0 "
                + " OR FreeReasonId IS NOT NULL ";
        db.execSQL(sql);
    }

    public void update1(String eVCId)
    {
        String query = "UPDATE " + DATABASE_TABLE
        +" SET DisAmount1 = IFNULL((SELECT CASE fr.DisAccType when 1 then EVCTempReturnItemSDS.Discount else 0 end from DiscountFreeReason fr  where fr.FreeReasonID=EVCTempReturnItemSDS.FreeReasonId AND fr.CalcPriceType=1),DisAmount1)"
		+" , DisAmount2 = IFNULL((SELECT CASE fr.DisAccType when 2 then EVCTempReturnItemSDS.Discount else 0 end from DiscountFreeReason fr  where fr.FreeReasonID=EVCTempReturnItemSDS.FreeReasonId AND fr.CalcPriceType=1),DisAmount2)"
		+" , DisAmount3 = IFNULL((SELECT CASE fr.DisAccType when 3 then EVCTempReturnItemSDS.Discount else 0 end from DiscountFreeReason fr  where fr.FreeReasonID=EVCTempReturnItemSDS.FreeReasonId AND fr.CalcPriceType=1),DisAmount3) "
        +" , OtherDiscount = IFNULL((SELECT CASE  when da.IsDefault=0 then EVCTempReturnItemSDS.Discount else 0 end " +
                "FROM DiscountFreeReason fr  " +
                " INNER JOIN " + DisAccDBAdapter.DATABASE_TABLE + " da on da.Id=fr.DisAccType " +
                " WHERE fr.FreeReasonID=EVCTempReturnItemSDS.FreeReasonId AND fr.CalcPriceType=1),OtherDiscount) "
        +" WHERE EVCId = ' " + eVCId +"' ";
        db.execSQL(query);
    }

    public void update2(String saleEVCId, String newSaleEVCId)
    {
        String query = "UPDATE " + DATABASE_TABLE
                + " SET TotalQty = TotalQty + (SELECT TotalQty FROM " + DATABASE_TABLE + " I WHERE I.GoodsRef = " + DATABASE_TABLE + ".GoodsRef AND PrizeType=1) \n"
                + " WHERE PrizeType=0 \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " Where PrizeIncluded=1 \n"
                + "                  AND ID IN (SELECT DisRef FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " WHERE EVCItemRef in (Select _id From " + EVCItemSDSDBAdapter.DATABASE_TABLE + " Where EVCRef = '" + saleEVCId + "'))) \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " WHERE EVCRef = '" + newSaleEVCId + "' AND TotalQty<>0 AND PrizeType=1)";

        db.execSQL(query);
    }

    public void update3(String saleEVCId, String newSaleEVCId)
    {
        String query = "UPDATE " + DATABASE_TABLE
                + " SET UnitQty = CAST((TotalQty/UnitCapasity) AS INT) \n"
                + " , Amount = TotalQty*CustPrice \n"
                + " , AmountNut = TotalQty*CustPrice \n"
                + " , Discount=0 \n"
                + " , AddAmount=0 \n"
                + " WHERE PrizeType = 0 \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE + " Where PrizeIncluded=1 \n"
                + "                  AND ID IN (SELECT DisRef FROM " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE + " WHERE EVCItemRef in (Select _id From " + EVCItemSDSDBAdapter.DATABASE_TABLE + " Where EVCRef = '" + saleEVCId + "' ))) \n"
                + " AND GoodsRef IN (SELECT GoodsRef FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " WHERE EVCRef= '" + newSaleEVCId + "' AND TotalQty <> 0 AND PrizeType=1)";

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
