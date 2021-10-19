package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;


public class EVCTempSummaryVnLiteDBAdapter extends DiscountBaseDataAdapter
{

	public final String KEY_EVC_TEMP_ROWID = "_id";
	public final String KEY_EVC_DISCOUNT_ID = "DisID";
    public final String KEY_EVC_DISCOUNT_GROUP = "DisGroup";
    public final String KEY_EVC_PRIORITY = "priority";
    public final String KEY_EVC_REQ_QTY = "ReqQty";
    public final String KEY_EVC_REQ_AMOUNT = "reqamount";
    public final String KEY_EVC_REQ_WEIGHT = "ReqWeight";
    public final String KEY_EVC_MAIN_PRODUCT_ID = "MainProductId";
    public final String KEY_EVC_ID = "EVCId";


	public static final String DATABASE_TABLE = "EVCTempSummaryVnLite";

	private static EVCTempSummaryVnLiteDBAdapter instance;
    private static boolean isDbAttached = false;



	public EVCTempSummaryVnLiteDBAdapter()
	{
	}
	
	public static EVCTempSummaryVnLiteDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCTempSummaryVnLiteDBAdapter();
		}
		
		return instance;
		
	}
	 
	public void deleteAllEVCYTempSummarieById(String evcId)
	{
		db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
	}

	/*[dbo].[usp_FillEVCStatuteByID]*/
    public void fillEVCItemSummary(String evcId)
    {
        String sql = "";
		//#tmpTable2
        sql = "insert into " + DATABASE_TABLE +
				" (DisId, DisGroup, Priority, ReqQty, ReqAmount, ReqWeight,  ProductId, MainProductId,EvcId)" +
                " select DiscountId, DisGroup  , priority , ReqQty ,reqamount , ReqWeight, MainProductId, MainProductId,'" + evcId + "' " +
                " from " + EVCTempVnLiteDBAdapter.DATABASE_TABLE +
                " WHERE DiscountId IN (SELECT DiscountId from " + EVCTempVnLiteDBAdapter.DATABASE_TABLE  +
                "   where ProductId is null  and EvcId ='" + evcId + "' " +
                "    Group By DiscountId ,ReqRowCount  " +
                "    Having  (Sum(cast(ReqQty as decimal(18,2))) BETWEEN MAX(IFNULL(MINQty,0)) AND (CASE WHEN MAX(IFNULL(MAXQty,0))=0 THEN Sum(ReqQty) ELSE MAX(IFNULL(MAXQty,0)) END) )  " +
                "     AND (Sum(cast(ReqAmount as decimal(18,2))) BETWEEN MAX(IFNULL(MINAmount,0)) AND (CASE WHEN MAX(IFNULL(MAXAmount,0))=0 THEN Sum(reqamount ) ELSE MAX(IFNULL(MAXAmount,0)) END) )  " +
                "     AND (Sum(ReqWeight) BETWEEN MAX (IFNULL(MinWeight,0)) AND (CASE WHEN MAX(IFNULL(MaxWeight,0))=0 THEN Sum(ReqWeight) ELSE MAX(IFNULL(MaxWeight,0))END ) )  " +
                "     AND (ReqRowCount BETWEEN MAX (IFNULL(MinRowsCount,1)) AND (CASE WHEN MAX(IFNULL(MaxRowsCount,0))=0 THEN ReqRowCount ELSE MAX(IFNULL(MaxRowsCount,0))END ) )  " +
                "     )   and EvcId ='" + evcId + "' " +

                " UNION ALL " +

                " SELECT DiscountId , DisGroup , priority , ReqQty , reqamount , ReqWeight ,  MainProductId, MainProductId,'" + evcId + "'" +
                " from " + EVCTempVnLiteDBAdapter.DATABASE_TABLE + " " +
                " WHERE ProductId is not null   \n" +
                "   AND ReqQty  BETWEEN IFNULL(MINQty,0) AND IFNULL(MAXQty,ReqQty)  \n" +
                "   AND reqamount BETWEEN IFNULL(MINAmount,0) AND IFNULL(MAXAmount,ReqAmount)  \n" +
                "   AND ReqWeight BETWEEN IFNULL(MinWeight,0) AND IFNULL (MaxWeight,ReqWeight)  \n" +
                "   AND ReqRowCount BETWEEN IFNULL(MinRowsCount,1) AND IFNULL (MaxRowsCount,ReqRowCount)  " +
				"	AND EvcId ='" + evcId + "' ";

        db.execSQL(sql);

    }
	
	public void clearAllData()
	{
		db.delete(DATABASE_TABLE, null, null);
	}
}
	 
