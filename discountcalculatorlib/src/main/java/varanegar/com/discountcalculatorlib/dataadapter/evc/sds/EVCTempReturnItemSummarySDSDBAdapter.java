package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

/**
 * Created by m.aghajani on 7/4/2016.
 */
public class EVCTempReturnItemSummarySDSDBAdapter extends DiscountBaseDataAdapter
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



    public static final String DATABASE_TABLE = "EVCTempReturnItemSummarySDS";
    private static String TAG = "EVCTempReturnItemSummarySDSDBAdapter";
    private static EVCTempReturnItemSummarySDSDBAdapter instance;

    public EVCTempReturnItemSummarySDSDBAdapter()
    {
    }


    public static EVCTempReturnItemSummarySDSDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempReturnItemSummarySDSDBAdapter();
        }

        return instance;

    }

    /*SLE.usp_GetRetExtraValue*/
    public void fillEVCTempReturnItemSummary(String newSaleEVCId)
    {
        /*#tmpTable2*/
        String sql = "INSERT INTO " + DATABASE_TABLE + " (EVCId, RowOrder, GoodsRef, UnitQty, CPriceRef, UnitRef, UnitCapasity, TotalQty, AmountNut, Discount, Amount, PrizeType \n"
                + " ,SupAmount, AddAmount, Add1, Add2, UserPrice, CustPrice, PriceId, Charge, Tax, Dis1, Dis2, Dis3, OtherDiscount, OtherAddition) \n"
                + " SELECT '" + newSaleEVCId + "', RowOrder, GoodsRef, Sum(TotalQty/UnitCapasity) As UnitQty, CPriceRef ,UnitRef, UnitCapasity \n"
                + " , SUM(TotalQty) As TotalQty \n"
                + " , SUM(Amount) - SUM(Discount) + SUM(AddAmount) AS AmountNut \n"
                + " , SUM(Discount) AS Discount \n"
                + " , SUM(Amount) AS Amount \n"
                + " , PrizeType ,SUM(SupAmount) As SupAmount ,SUM(AddAmount) As AddAmount \n"
                + " , SUM(AddAmount1) AS Add1, SUM(AddAmount2) AS Add2 \n"
                + " , UserPrice ,CustPrice ,PriceRef PriceId, IFNULL(Charge,0) Charge, IFNULL(Tax,0) Tax \n"
                + " , SUM(DisAmount1) AS Dis1, SUM(DisAmount2) AS Dis2, SUM(DisAmount3) AS Dis3 \n"
                + " , SUM(OtherDiscount) as OtherDiscount, sum(OtherAddition) as OtherAddition \n"
            + " FROM ( SELECT * FROM " + EVCTempReturnItemSDSDBAdapter.DATABASE_TABLE + ") X \n"
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
