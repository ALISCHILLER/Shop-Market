package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class EVCTempReturnItemSummaryVnLiteDBAdapter extends DiscountBaseDataAdapter
{

    public final String KEY_EVC_TEMP_RETURN_ROWID = "_id";
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



    public static final String DATABASE_TABLE = "EVCTempReturnItemSummaryVnLite";
    private static String TAG = "EVCTempReturnItemSummaryVnLiteDBAdapter";
    private static EVCTempReturnItemSummaryVnLiteDBAdapter instance;

    public EVCTempReturnItemSummaryVnLiteDBAdapter()
    {
    }

    public void deleteAllByEVCId(String evcId)
    {
        db.delete(DATABASE_TABLE, "EVCId='" + evcId + "'", null);
    }

    public static EVCTempReturnItemSummaryVnLiteDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempReturnItemSummaryVnLiteDBAdapter();
        }

        return instance;

    }

    public void fillEVCTempReturnItemSummary(String newSaleEVCId)
    {
        //#tmpTable2
        String sql = "INSERT INTO " + DATABASE_TABLE + " (EVCId, RowOrder, GoodsRef "
                + " ,UnitQty ,CPriceRef, UnitRef, UnitCapasity \n"
                + " ,TotalQty, AmountNut, Discount, Amount, PrizeType \n"
                + " , AddAmount, UserPrice, CustPrice, PriceId, Charge, Tax, DetailId) \n"
                + " SELECT '" + newSaleEVCId + "', RowOrder, GoodsRef "
                + " , SUM(UnitQty) As UnitQty, CPriceRef, UnitRef, UnitCapasity \n"
                + " , SUM(TotalQty) As TotalQty \n"
                + " , SUM(Amount) - SUM(Discount) + SUM(AddAmount) AS AmountNut \n"
                + " , SUM(Discount) AS Discount \n"
                + " , SUM(Amount) AS Amount \n"
                + " , PrizeType ,SUM(AddAmount) As AddAmount \n"
//                + " , SUM(Add1) AS Add1, SUM(Add2) AS Add2 \n"
                + " , UserPrice ,CustPrice ,PriceId, IFNULL(Charge,0) Charge, IFNULL(Tax,0) Tax \n"
                + " , IFNULL(DetailId, 0) AS DetailId  "
  //              + " , SUM(Dis1) AS Dis1, SUM(Dis2) AS Dis2, SUM(Dis3) AS Dis3 \n"
                + " FROM ( SELECT * FROM " + EVCTempReturnItemVnLiteDBAdapter.DATABASE_TABLE + ") X \n"
                + " WHERE EVCId = '" + newSaleEVCId + "'"
                + " GROUP BY RowOrder ,GoodsRef ,CPriceRef ,UnitRef , UnitCapasity ,PrizeType ,UserPrice ,CustPrice ,PriceId ,Tax,Charge \n"
                + " ORDER BY PrizeType ,RowOrder ";

        db.execSQL(sql);
    }

    public void deleteAllEVCYTempReturnSummaryById(String evcId)
    {
        db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
    }

    public void deleteAllEVCYTempReturnSummeries()
    {
        db.delete(DATABASE_TABLE, null, null);
    }
}
