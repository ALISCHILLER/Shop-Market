package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.entity.customer.DiscountCustomer;


public class EVCTempVnLiteDBAdapter extends DiscountBaseDataAdapter
{

	public final String KEY_EVC_TEMP_ROWID = "_id";
	public final String KEY_EVC_TEMP_ITEM_REF_ID = "EVCItemRefId";
	public final String KEY_EVC_TEMP_DISCOUNT_ID = "DiscountId";
	public final String KEY_EVC_TEMP_DIS_GROUP = "DiscountGroup";
//	public final String KEY_EVC_TEMP_DIS_TYPE = "";
	public final String KEY_EVC_TEMP_PRIORITY = "Priority";
	public final String KEY_EVC_TEMP_REQ_QTY = "ReqQty";
    public final String KEY_EVC_TEMP_REQ_AMOUNT = "ReqAmount";
    public final String KEY_EVC_TEMP_MIN_QTY = "MinQty";
    public final String KEY_EVC_TEMP_MAX_QTY = "MaxQty";
    public final String KEY_EVC_TEMP_MIN_AMOUNT = "MinAmount";
    public final String KEY_EVC_TEMP_MAX_AMOUNT = "MaxAmount";
    public final String KEY_EVC_TEMP_REQ_WEIGHT = "ReqWeight";
    public final String KEY_EVC_TEMP_MIN_WEIGHT = "MinWeight";
    public final String KEY_EVC_TEMP_MAX_WEIGHT = "MaxWeight";
    public final String KEY_EVC_TEMP_PRODUCT_ID = "ProductId";
    public final String KEY_EVC_TEMP_MAIN_PRODUCT_ID = "MainProductId";
    public final String KEY_EVC_TEMP_REQ_ROW_COUNT = "ReqRowCount";
    public final String KEY_EVC_TEMP_MIN_ROW_COUNT = "MinRowCount";
    public final String KEY_EVC_TEMP_MAX_ROW_COUNT = "MaxRowCount";


	public static final String DATABASE_TABLE = "EVCTempVnLite";


	private static EVCTempVnLiteDBAdapter instance;

	public EVCTempVnLiteDBAdapter()
	{
	}

	public static EVCTempVnLiteDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCTempVnLiteDBAdapter();
		}
		
		return instance;
		
	}
	 

	public void deleteAllEVCTempsById(String evcId)
	{
		db.delete(DATABASE_TABLE, "evcId ='" + evcId + "'", null);
	}

	/* [dbo].[usp_FillEVCStatuteByID]*/
	public void fillEVCTemp(String evcId, String orderDate, int evcType )
	{
		String sql = "INSERT INTO " + DATABASE_TABLE +
				" (DiscountId, DisGroup" +
                //" /*, EVCItemRefId*/" +
                " , PrizeRefId, Priority, ReqQty, " +
				" ReqAmount" +
                " , MinQty, MaxQty , MinAmount, MaxAmount" +
                " , ReqWeight , MinWeight , MaxWeight " +
                " , ProductId , MainProductId" +
                " , ReqRowCount" +
                " , MinRowsCount, MaxRowsCount, EvcId) " +

				" SELECT DISTINCT D.PromotionDetailId as DisID , D.ID " +
                //" /*, OI.Goodsref*/" +
                " , D.PrizeRef, D.CalcPriority " +
				" ,  (Case WHEN SubStr(D.DisType, length(D.DisType)) = '5' THEN 1.0 * oi.Qty/g.LargeUnitQty  ELSE oi.Qty END) AS ReqQty " +
				" ,  oi.ReqAmount " +
				" , D.MINQty , D.MAXQty " +
				" , D.MINAmount , D.MAXAmount AS MAXAmount" +
				" , oi.Qty * IFNull(G.ProductWeight,0)  as ReqWeight " +
				" , D.MinWeight * 1000 as MinWeight " +
                " , D.MaxWeight * 1000 as MaxWeight " +
                " , D.GoodsRef AS ProductId" +
                " , g.ProductId AS MainProductId" +
                " ,   IFNULL(" +
                "     (Select COUNT(*) from "+ EVCItemVnLiteDBAdapter.DATABASE_TABLE+" evd  INNER JOIN "+ DiscountProductDBAdapter.DATABASE_TABLE +" gg ON gg.ProductId = evd.GoodsRef   \n" +
				"      where evd.EVCRef = o.EVCID and PrizeType =0  " +
				"      AND (D.CustRef IS NULL OR D.CustRef = o.CustRef )  " +
				"      AND (D.CustCtgrRef Is NULL OR D.CustCtgrRef = c.CustomerCategoryId )  " +
				"      AND (D.GoodsCtgrRef IS NULL OR D.GoodsCtgrRef = gg.ProductBOGroupId) " +
				"      AND (D.GoodsRef IS NULL OR D.GoodsRef = gg.ProductId) " +
				"      AND (D.ManufacturerRef IS NULL OR D.ManufacturerRef = gg.ManufacturerId) " +
               /*VNPL***/
				"      AND (D.PayType IS NULL OR D.PayType = o.PayType) " +
				"      AND (D.ProductSubGroup1Id IS NULL OR D.ProductSubGroup1Id = gg.ProductSubGroup1Id)   " +
				"      AND (D.ProductSubGroup2Id IS NULL OR D.ProductSubGroup2Id = gg.ProductSubGroup2Id)   " +
				"      AND (D.CustomerSubGroup1Id IS NULL OR D.CustomerSubGroup1Id = c.CustomerSubGroup1Id) " +
				"   AND (D.CustomerSubGroup2Id IS NULL OR D.CustomerSubGroup2Id = c.CustomerSubGroup2Id) " +
				"	AND (D.DCRef    IS NULL OR D.DCRef=o.DCRef)"+
				" ),0) AS ReqRowCount " +
                " , D.MinRowsCount " +
                " , D.MaxRowsCount " +
                " , '" + evcId + "' " +

				" FROM "+ DiscountVnLiteDBAdapter.DATABASE_TABLE+" D \n" +
				" INNER JOIN (select oi.CustPrice, oi.GoodsRef,SUM(TotalQty) as qty,PrizeType as PrizeType,EVCRef  \n" +
				"             ,sum((Case when oi.PrizeType=1 then 0 else oi.TotalQty  *  oi.CustPrice  END)) As ReqAmount  \n" +
				"           from "+ EVCItemVnLiteDBAdapter.DATABASE_TABLE+" oi   \n" + //EVCDetail
				"           where EVCRef = '" + evcId + "'  and oi.PrizeType =0   \n" +
				"           group by  oi.GoodsRef,PrizeType,EVCRef) oi ON oi.EVCRef = '" + evcId + "'\n" +
				" INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " o ON o.EvcId = oi.EVCRef \n" + //dbo.EVC
				" INNER JOIN "+ DiscountCustomerDBAdapter.DATABASE_TABLE + " c ON c.CustomerId = o.custRef \n" +
				" INNER JOIN "+ DiscountProductDBAdapter.DATABASE_TABLE + " g ON g.ProductId = oi.GoodsRef \n" +
				" WHERE (D.CustCtgrRef IS NULL OR  D.CustCtgrRef = c.CustomerCategoryId)" +
				"    AND (D.CustRef IS NULL OR  D.CustRef = o.custRef )  " +
				"    AND (D.GoodsCtgrRef IS NULL OR  D.GoodsCtgrRef = g.ProductBOGroupId) " +
				"    AND (D.GoodsRef IS NULL OR  D.GoodsRef = oi.GoodsRef) " +
				"    AND (D.ManufacturerRef IS NULL OR  D.ManufacturerRef = g.ManufacturerId) " +
				"    AND (D.PayType IS NULL OR  D.PayType = o.PayType)  " +
				"    AND (D.ProductSubGroup1Id IS NULL OR  D.ProductSubGroup1Id = g.ProductSubGroup1Id)  " +
				"    AND (D.ProductSubGroup2Id IS NULL OR  D.ProductSubGroup2Id = g.ProductSubGroup2Id)  " +
				"    AND (D.CustomerSubGroup1Id IS NULL OR  D.CustomerSubGroup1Id = c.CustomerSubGroup1Id)  " +
				"    AND (D.CustomerSubGroup2Id IS NULL OR  D.CustomerSubGroup2Id = c.CustomerSubGroup2Id) " +
				" AND (D.DCRef    IS NULL OR D.DCRef=o.DCRef) ";
		//for return
		if (evcType == 1) {
			sql += " AND (o.DateOf BETWEEN D.StartDate AND IFNULL(D.ENDDate ,o.DateOf )) ";

		}
		if (evcType == 2 || evcType == 10) {
			sql +=
			" AND (D.PromotionDetailId IN (SELECT PromotionDetailId FROM " + DisSaleVnLiteDBAdapter.getInstance().DATABASE_TABLE + ")) "+
			" AND (o.DateOf BETWEEN D.StartDate AND IFNULL(D.ENDDate ,o.DateOf)) ";
		}
		if (evcType == 3) { //توزیع
		//	sql += " AND (SELECT InvoiceDate " +
		//			FROM dbo.Sell  WHERE sellID=o.RefID) BETWEEN D.StartDate AND IFNULL(D.ENDDate,(SELECT InvoiceDate FROM dbo.Sell WHERE sellID=o.RefID) ))

		}

		db.execSQL(sql);
	}
	
	public void clearAllData()
	{
		db.delete(DATABASE_TABLE, null, null);
	}

}
	 
