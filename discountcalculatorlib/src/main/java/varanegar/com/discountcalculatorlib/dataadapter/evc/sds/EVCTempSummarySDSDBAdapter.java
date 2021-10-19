package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;


public class EVCTempSummarySDSDBAdapter extends DiscountBaseDataAdapter
{

	public final String KEY_EVC_TEMP_ROWID = "_id";
	public final String KEY_DISCOUNT_ID = "DisId";
	public final String KEY_DIS_GROUP = "DisGroup";
	public final String KEY_DIS_TYPE = "DisType";
	public final String KEY_EVC_ITEM_REF_ID = "EVCItemRef";
	public final String KEY_ROW_ORDER = "RowOrder";
	public final String KEY_PRIORITY = "Priority";
	public final String KEY_REQ_QTY = "ReqQty";
	public final String KEY_REQ_ROW_COUNT = "ReqRowCount";
	public final String KEY_REQ_AMOUNT = "ReqAmount";
	public final String KEY_REQ_WIGHT = "ReqWeight";
	public final String KEY_EVC_ID = "EVCId";


	public static final String DATABASE_TABLE = "EVCTempSummarySDS";
	private static String TAG = "EVCTempSummarySDSDBAdapter";
	private static EVCTempSummarySDSDBAdapter instance;

	public EVCTempSummarySDSDBAdapter()
	{
	}
	
	public static EVCTempSummarySDSDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCTempSummarySDSDBAdapter();
		}
		
		return instance;
		
	}

	public boolean ExistsData(String evcId) {
		Cursor exist = db.rawQuery("SELECT COUNT(*) FROM "+ DATABASE_TABLE +"  WHERE EvcId = '" + evcId + "'", null);
		return (exist != null && exist.moveToFirst() && exist.getInt(0) > 0);
	}

	public int GetLoopCount()
	{
		String sql =
		" SELECT max(PriorityCount) AS PC "+
		" FROM (  SELECT DisGroup, count(1) as PriorityCount \n"+
				" FROM " + DATABASE_TABLE + " group by DisGroup ) A \n";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null && cursor.moveToFirst())
			return cursor.getInt(0);
		return 0;
	}

	//insert into #TmpTable2
	public void InsertGoodRef(String evcId, int saleId)
	{
		String sql = "insert into " + DATABASE_TABLE + " (EVCItemRef, RowOrder, DisId, DisGroup, DisType,  Priority,  EvcId) \n" +
				" select i._Id as EvcItemRef, RowOrder, d.Id as DisRef, DisGroup, d.DisType, Priority,'" + evcId +"'\n"+
				" from " + EVCSkipDiscountDBAdapter.DATABASE_TABLE +" sd \n" +
				" inner join "+EVCHeaderSDSDBAdapter.DATABASE_TABLE +" e  on e.RefId = " + saleId +" and EvcType in (2,10,12) and sd.SaleRef= " + saleId+"\n" +
				" inner join "+ varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE +" d on d.Id=sd.DisRef\n" +
				" inner join " +EVCItemSDSDBAdapter.DATABASE_TABLE +" i  on i.EvcRef=e._id and sd.EvcRef=i.EvcRef and d.GoodsRef=i.GoodsRef and I.PrizeType=0 and FreeReasonId is null\n" +
				" where not exists (select 1 from " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +" es where es.DisRef=d.id and es.EvcId = '" + evcId +"' )";
		db.execSQL(sql);
	}
	public void InsertSkipGoodsRef(String evcId, int saleId)
	{
		String sql = "insert into " + DATABASE_TABLE + " (EVCItemRef, RowOrder, DisId, DisGroup, DisType,  Priority,  EvcId) \n" +
				" select i._Id as EvcItemRef, RowOrder, d.Id as DisRef, DisGroup, d.DisType, Priority,'" + evcId +"'\n"+
				" from " + EVCSkipDiscountDBAdapter.DATABASE_TABLE +" sd \n" +
				" inner join "+EVCHeaderSDSDBAdapter.DATABASE_TABLE +" e  on e.RefId = " + saleId + " and EvcType in (2,10,12) and sd.SaleRef= " + saleId+"\n" +
				" inner join "+ varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE +" d on d.Id=sd.DisRef\n" +
				" inner join " +EVCItemSDSDBAdapter.DATABASE_TABLE +" i  on i.EvcRef=e._id and sd.EvcRef=i.EvcRef and sd.SkipGoodsRef =i.GoodsRef and I.PrizeType=0 and FreeReasonId is null\n"+
				" where not exists (select 1 from " + EVCItemStatutesSDSDBAdapter.DATABASE_TABLE +" es where es.DisRef=d.id and es.EvcId = '" + evcId +"' )";
		db.execSQL(sql);
	}

	/*usp_FillEvcStatuteByID_ApplyDiscountCriteria*/
	public void fillEVCItemSummary(String evcId) throws DiscountException
	{
		String sql = "delete from "+ DATABASE_TABLE;
		db.execSQL(sql);

		String sql2 = "SELECT SUM(Amount) as totalEvcAmount, " +
				"COUNT(1) as totalEvcRowCount FROM EVCItemSDS WHERE EvcRef='"+evcId+"' AND PrizeType=0 AND TotalQty >0 ";

		BigDecimal totalEvcAmount = BigDecimal.ZERO;
		int totalEvcRowCount = 0;
		Cursor cursor = db.rawQuery(sql2, null);
		if (cursor != null && cursor.moveToFirst()) {
			totalEvcAmount = new BigDecimal(cursor.getDouble(cursor.getColumnIndex("totalEvcAmount")));
			totalEvcRowCount = cursor.getInt(cursor.getColumnIndex("totalEvcRowCount"));
		}

		sql = "insert into " + DATABASE_TABLE + " (DisId, DisGroup, DisType, EVCItemRef, RowOrder, Priority, ReqQty, ReqRowCount, ReqAmount, ReqWeight, EvcId) \n" +

				" select DiscountId, DisGroup, DisType , EVCItemRefId , RowOrder , priority , ReqQty ,ReqRowCount, reqamount , ReqWeight, '" + evcId + "'\n" +
				" from  " + EVCTempSDSDBAdapter.DATABASE_TABLE +
				" where DiscountId IN \n" +
				" (SELECT DiscountId \n" +
				"   FROM  " + EVCTempSDSDBAdapter.DATABASE_TABLE +
				"	WHERE ApplyInGroup = 1 and EvcId ='" + evcId + "'\n" +
				"	Group By DiscountId , PrizeStepType, MinQty, MaxQty, PrizeStep, MinAmount, MaxAmount \n" +
				"	Having " +
				" (\n" +
				"SUM(ReqQty) \n" +
				"BETWEEN CASE WHEN IFNULL(PrizeStepType,0)=0 THEN IFNULL(MinQty,0)/*+IFNULL(PrizeStep,0)*/ ELSE SUM(ReqQty) END \n" +
				"AND CASE WHEN IFNULL(PrizeStepType,0)=0 THEN IFNULL(MaxQty, SUM(ReqQty)) ELSE SUM(ReqQty) END\n" +
				")\n" +
				"AND (\n" +
				"SUM(ReqQty) \n" +
				"BETWEEN CASE WHEN PrizeStepType=1 THEN IFNULL(MinQty,0) ELSE SUM(ReqQty) END \n" +
				"AND CASE WHEN PrizeStepType=1 THEN IFNULL(MaxQty, SUM(ReqQty)) ELSE SUM(ReqQty) END\n" +
				")\n" +
				"AND (\n" +
				"Sum(ReqAmount) \n" +
				"BETWEEN CASE WHEN IFNULL(PrizeStepType,1)=1 THEN IFNULL(MinAmount,0)/*+IFNULL(PrizeStep,0)*/ ELSE SUM(ReqAmount) END \n" +
				"AND CASE WHEN IFNULL(PrizeStepType,1)=1 THEN IFNULL(MaxAmount, SUM(ReqAmount)) ELSE SUM(ReqAmount) END\n" +
				")\n" +
				"AND (\n" +
				"Sum(ReqAmount) \n" +
				"BETWEEN CASE WHEN PrizeStepType=0 THEN IFNULL(MinAmount,0) ELSE SUM(ReqAmount) END \n" +
				"AND CASE WHEN PrizeStepType=0 THEN IFNULL(MaxAmount, SUM(ReqAmount)) ELSE SUM(ReqAmount) END\n" +
				")\n" +
				"AND (\n" +
				"Sum(ReqRowCount) BETWEEN MAX(IFNULL(MinRowsCount,1)) AND (CASE WHEN MAX(IFNULL(MaxRowsCount,0))=0 THEN SUM(ReqRowCount) ELSE MAX(IFNULL(MaxRowsCount,0)) END) \n" +
				")\n" +
				"AND ( Sum (ReqWeight) BETWEEN MAX(IFNULL(MinWeight, 0)) AND (CASE WHEN MAX(IFNULL(MaxWeight, 0)) = 0 THEN SUM(ReqWeight) ELSE MAX(IFNULL(MaxWeight, 0)) END )  )\n" +


				" AND (PrizeStep IS NULL \n" +
				" OR (IFNULL(PrizeStepType,0)=0 AND SUM(ReqQty)>=PrizeStep) \n" +
				" OR (IFNULL(PrizeStepType,0)=1 AND SUM(ReqAmount)>=PrizeStep)\n" +
				" )"+


				") " +
				"   AND " + totalEvcAmount + " BETWEEN IFNULL(TotalMinAmount, 0) AND IfNULL(TotalMaxAmount, " + totalEvcAmount + " ) \n" +
				"   AND " + totalEvcRowCount + " BETWEEN IFNULL(TotalMinRowCount, 1) AND IFNULL(TotalMaxRowCount, " + totalEvcRowCount+ ")  \n" +
				"	And EvcId ='" + evcId + "' \n" +

				" UNION ALL \n" +

				" SELECT DiscountId , DisGroup , DisType , EVCItemRefId , RowOrder , priority , ReqQty , ReqRowCount, reqamount , ReqWeight, '" + evcId + "'\n" +
				" from  " + EVCTempSDSDBAdapter.DATABASE_TABLE +  /*#tmpTable */
				" WHERE ApplyInGroup=0 \n" +
				"	AND ReqQty    BETWEEN ifnull(MINQty,0) AND ifnull(MAXQty, ReqQty) \n" +
				"	AND ReqAmount BETWEEN ifnull(MINAmount,0) AND ifnull(MAXAmount, ReqAmount) \n" +
				"	AND ReqRowCount BETWEEN ifnull(MinRowsCount,1) AND ifnull(MaxRowsCount, ReqRowCount) \n" +
				"	AND ReqWeight BETWEEN ifnull ( MinWeight , 0 ) AND ifnull(MaxWeight, ReqWeight )  and EvcId ='" + evcId + "' \n";

		db.execSQL(sql);

		Cursor complexCondition = null;
		String complexConditionquery = "SELECT Code "
				+ " FROM " + DATABASE_TABLE +" AS e INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE
				+ " AS d ON e.DisId = d.Id "
				+ " WHERE (IFNULL(SqlCondition,'')= '' AND HasAdvanceCondition = 1)\n"
				+ " OR(IsComplexCondition = 1) "
				+ " GROUP BY Code " ;
		complexCondition = db.rawQuery(complexConditionquery, null);
		String complexCodes = "";
		if(complexCondition != null && complexCondition.moveToFirst())
		{
			do {

				int discountCode = complexCondition.getInt(0);
				if (complexCodes.length() > 0)
					complexCodes += ", " + discountCode;
				else
					complexCodes = " " + discountCode;
			}while (complexCondition.moveToNext());


			}
		if (complexCodes.length() > 0)
			throw new DiscountException("به خاطر وجود قوانین پیشرفته خارج از الگو امکان پیش نمایش تخفیفات روی تبلت وجود ندارد و فقط در حالت آنلاین قابل محاسبه هست.\n" + "کد قوانین: " + complexCodes);


		Cursor conditions = null;
		String conditionquery = "SELECT DisId, SqlCondition FROM " + DATABASE_TABLE
				+ " AS e INNER JOIN " + varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter.DATABASE_TABLE
				+ " AS d ON e.DisId = d.Id AND d.SqlCondition IS NOT NULL AND d.SqlCondition <> ''";
		conditions = db.rawQuery(conditionquery, null);




		String evcFull = "(" +
		"SELECT  e._Id AS Id ,\n" +
				" e.EvcDate ,\n" +
				" e.PayType ,\n" +
				"            e.AccYear ,\n" +
				"            e.DcRef ,\n" +
				"            e.DcCode ,\n" +
				"            e.CustRef ,\n" +
				" e.SupervisorName, \n" +
				" e.DealerName, \n" +
				" e.StockDCName, \n" +
				"            c.CustomerCode AS CustCode ,\n" +
				"            c.CustomerCategoryId AS CustCtgrRef ,\n" +
				"            c.CustomerCategoryCode AS CustCtgrCode ,\n" +
				"            c.CustomerActivityId AS CustActRef ,\n" +
				"            c.CustomerActivityCode AS CustActCode ,\n" +
				"            c.CustomerLevelId AS CustLevelRef ,\n" +
				"            c.CustomerLevelCode AS CustLevelCode ,\n" +
				"            c.CityZone ,\n" +
				"            c.CityArea ,\n" +
				"            c.OwnerTypeRef ,\n" +
				"            c.OwnerTypeCode ,\n" +
				"            c.SalePathRef ,\n" +
				"            c.SalePathNo ,\n" +
				"            c.SaleAreaRef ,\n" +
				"            c.SaleAreaNo ,\n" +
				"            c.SaleZoneRef ,\n" +
				"            c.SaleZoneNo ,\n" +
				"            c.DistPathId AS DistPathRef ,\n" +
				"            c.DistPathNo ,\n" +
				"            c.DistAreaId AS DistAreaRef ,\n" +
				"            c.DistAreaNo ,\n" +
				"            c.DistZoneId AS DistZoneRef ,\n" +
				"            c.DistZoneNo ,\n" +
				"            c.CityId AS CityRef ,\n" +
				"            c.CityCode ,\n" +
				"            c.CountyRef ,\n" +
				"            c.CountyCode ,\n" +
				"            c.StateId AS StateRef, \n" +
				"            c.StateCode \n" +
				",c.CustCtgrName\n" +
				",c.CustActName\n" +
				",c.CustLevelName\n" +
				",c.CityName\n" +
				",c.StateName\n" +
				",c.DcName\n" +
				",c.OwnerTypeName\n" +
				",c.SalePathName\n" +
				",c.SaleAreaName\n" +
				",c.SaleZoneName\n" +
				",c.DistPathName\n" +
				",c.DistAreaName\n" +
				",c.DistZoneName\n" +
				",c.CountyName\n" +
				",c.ExtraField1\n" +
				",c.ExtraField2\n" +
				",c.ExtraField3\n" +
				",c.ExtraField4\n" +
				",c.ExtraField5\n" +
				",c.ExtraField6\n" +
				",c.ExtraField7\n" +
				",c.ExtraField8\n" +
				",c.ExtraField9\n" +
				",c.ExtraField10\n" +
				",c.ExtraField11\n" +
				",c.ExtraField12\n" +
				",c.ExtraField13\n" +
				",c.ExtraField14\n" +
				",c.ExtraField15\n" +
				",c.ExtraField16\n" +
				",c.ExtraField17\n" +
				",c.ExtraField18\n" +
				",c.ExtraField19\n" +
				",c.ExtraField20,"+
				"e.DisType ,\n" +
				"e.OrderType ,\n" +
				"e.StockDCRef ,\n" +
				"e.StockDCCode ,\n" +
				"e.SaleOfficeRef ,\n" +
				"e.DealerRef ,\n" +
				"e.DealerCode ,\n" +
				"IFNULL(e.SupervisorRef, 0) AS SupervisorRef ,\n" +
				"IFNULL(e.SupervisorCode, 0) AS SupervisorCode ,\n" +
				"e.PaymentUsanceRef ,\n" +
				"ei.GoodsRef ,\n" +
				"g.ProductCode AS GoodsCode ,\n" +
				"g.ProductWeight AS GoodsWeight ,\n" +
				"g.GoodsVolume ,\n" +
				"g.ProductBoGroupId AS GoodsGroupRef ,\n" +
				"g.ManufacturerId AS ManufacturerRef ,\n" +
				"g.ManufacturerCode ,\n" +
				"g.BrandId AS BrandRef ,\n" +
				"g.ShipTypeId AS ShipTypeRef ,\n" +
				"g.ManufacturerName,\n" +
				"g.BrandName,\n" +
				"g.ShipTypeName,\n"+
				"ei.TotalQty ,\n" +
				"ei.CustPrice AS UnitPrice ,\n" +
				"ei.Amount ,\n" +
				"ei.AmountNut AS NetAmount ,\n" +
				"ei.Discount ,\n" +
				"ei.AddAmount ,\n" +
				"TotalWeight AS ItemWeight ,\n" +
				"( ei.TotalQty * IFNULL(ei.ItemVolume, 0) ) AS ItemVolume ,\n" +
				" dt.Title AS DisTypeName,\n" +
				" ot.OrderTypeName,\n" +
				" pt.PayTypeName\n" +
		"    FROM    EVCHeaderSDS AS e\n" +
		"    INNER JOIN EVCItemSDS AS ei ON ei.EVCRef = e.EvcId\n" +
		"    INNER JOIN DiscountProduct AS g ON ei.GoodsRef = g.ProductId\n" +
		"    INNER JOIN DiscountCustomer AS c ON c.CustomerId = e.CustRef " +
		"    LEFT JOIN DiscountDisType AS dt ON e.DisType = dt.code\n" +
		"    LEFT JOIN DiscountOrderType AS ot ON e.OrderType = ot.OrderTypeId\n" +
		"    INNER JOIN DiscountSellPayType AS pt ON e.PayType = pt.SellPayTypeId\n"+
		") AS EVCItemFull  ";

		if(conditions != null && conditions.moveToFirst())
		{
			do {

				int discountId = conditions.getInt(0);
				String whereClause = conditions.getString(1);
				if(!whereClause.trim().equals("")) {

					String query = "SELECT COUNT(*) " +
							" FROM  "+ evcFull +
							" WHERE 1=1 " + whereClause;

					Cursor innerCursor = null;
					try {
						innerCursor = db.rawQuery(query, null);
					} catch (Exception ex) {

						throw new DiscountException("قوانین پیشرفته تعریف شده در تبلت معتبر نمی باشد");
					}

					if (innerCursor != null && innerCursor.moveToFirst()) {
						if (innerCursor.getInt(0) == 0) {
							db.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE DisId = " + discountId + " AND EVCId = '" + evcId + "'");
						}
						else {
							Cursor exist = db.rawQuery("SELECT COUNT(*) FROM   "+ evcFull /*WHERE EvcRef = '" + evcId + "'*/, null);
							if(exist != null && exist.moveToFirst() && exist.getInt(0) > 0) {
								db.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE EvcItemRef not in (select EvcItemRef from  "+ evcFull /*WHERE EvcRef = '" + evcId + "'*/ +" ) and DisId= " + discountId + " AND EVCId = '" + evcId + "'");
								//view? db.execSQL("DELETE FROM EVCItemFull " /*WHERE  EvcRef = '" + evcId + "'"*/);
							}
						}
					}
				}

			}while (conditions.moveToNext());
		}

	}
	
	public void clearAllData(SQLiteDatabase db)
	{
		db.delete(DATABASE_TABLE, null, null);
	}

	public void deleteAllEVCYTempSummaries()
	{
		db.delete(DATABASE_TABLE, null, null);
	}
}
	 
