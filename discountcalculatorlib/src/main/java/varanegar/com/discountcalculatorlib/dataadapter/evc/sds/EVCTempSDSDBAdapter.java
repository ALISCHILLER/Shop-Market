package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerBoGroupDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19.DiscountConditionDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductPackageDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.utility.GlobalFunctions;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;


public class EVCTempSDSDBAdapter extends DiscountBaseDataAdapter
{

	public final String KEY_EVC_TEMP_ROWID = "_id";
	public final String KEY_DISCOUNT_ID = "DiscountId";
	public final String KEY_DIS_GROUP = "DisGroup";
	public final String KEY_DIS_TYPE = "DisType";
	public final String KEY_EVC_ITEM_REF_ID = "EVCItemRefId";
	public final String KEY_ROW_ORDER = "RowOrder";
	public final String KEY_PRIORITY = "Priority";
	public final String KEY_REQ_QTY = "ReqQty";
	public final String KEY_REQ_AMOUNT = "ReqAmount";
	public final String KEY_REQ_ROW_COUNT = "ReqRowCount";
	public final String KEY_MIN_QTY = "MinQty";
	public final String KEY_MAX_QTY = "MaxQty";
	public final String KEY_UNIT_CAPACITY = "UnitCapasity";
	public final String KEY_MIN_AMOUNT = "MinAmount";
	public final String KEY_MAX_AMOUNT = "MaxAmount";
    public final String KEY_EVC_ID = "EVCId";
    public final String KEY_APPLY_IN_GROUP = "ApplyInGroup";
    public final String KEY_REQ_WEIGHT = "ReqWeight";
    public final String KEY_MIN_WEIGHT = "MinWeight";
    public final String KEY_MAX_WEIGHT = "MaxWeight";
    public final String KEY_MIN_ROW_COUNT = "MinRowsCount";
    public final String KEY_MAX_ROW_COUNT = "MaxRowsCount";
    public final String KEY_DIST_ITEM_ID = "DistItemRefId";
    public final String KEY_PRIZE_STEP = "PrizeStep";
    public final String KEY_PRIZE_STEP_TYPE = "PrizeStepType";





	public static final String DATABASE_TABLE = "EVCTempSDS";
	private static String TAG = "EVCTempReturnItemSDSDBAdapter";
	private static EVCTempSDSDBAdapter instance;

	public EVCTempSDSDBAdapter()
	{
	}

	public static EVCTempSDSDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCTempSDSDBAdapter();
		}
		
		return instance;
		
	}

	/*SLE.usp_FillEVCStatuteByID_FilterByEvcInfo*/
	public void fillEVCTemp(String evcId, int orderId, String orderDate, String orderTypeId, int saleId, int evcType, int orderNo) {
		if (GlobalVariables.getBackOfficeVersion() == BackOfficeVersion.SDS16)
			fillEVCTemp4_16(evcId, orderNo, orderDate, saleId);
		else
			fillEVCTemp4_19(evcId, orderNo, orderDate, orderTypeId, saleId, evcType);
	}

	/*SLE.usp_FillEVCStatuteByID_FilterByEvcInfo*/
	public void fillEVCTemp4_16(String evcId, int orderNo, String orderDate, int saleId)
	{
		//#tmpTable
		String sql = "insert into " + DATABASE_TABLE + " (DiscountId, DisGroup, DisType, EVCItemRefId, RowOrder, Priority, ReqQty, ReqAmount, MinQty, MaxQty \n" +
                ", MinAmount, MaxAmount, ApplyInGroup, ReqWeight, MinWeight, MaxWeight,  MinRowsCount, MaxRowsCount, ReqRowCount, PrizeStep, PrizeStepType,  EvcId) \n" +

            " SELECT DisID ,DisGroup ,DisType ,EVCItemRef ,RowOrder ,Priority ,ifnull(ReqQty,0) as ReqQty, ReqAmount ,MINQty * UnitCapasity As MINQty    \n" +
                "  , MAXQty * UnitCapasity As MAXQty ,MINAmount ,MAXAmount ,ApplyInGroup, IFNULL(ReqWeight,0) as ReqWeight , MinWeight , MaxWeight , MinRowsCount \n" +
                "  , MaxRowsCount, ReqRowCount , PrizeStep , PrizeStepType ,'" + evcId  + "' \n" +

                "  FROM (   \n" +
                " SELECT DISTINCT D.ID as DisID , D.DisGroup , D.DisType , OI._id As EVCItemRef , OI.RowOrder , D.priority    \n" +
                "   , CASE IFNULL(g.CartonPrizeQty, 0) \n" +
                "       WHEN 0 THEN (CASE WHEN D.DisType=300 OR (D.ApplyInGroup=0 and D.QtyUnit <> 0) OR (D.DisType <> 300 AND D.QtyUnit <> 0) THEN oi.TotalQty \n" +
                "                         ELSE Round(CAST (oi.TotalQty/g.CartonType AS INT),2) END)  \n" +
                "       ELSE (Case WHEN D.DisType = 300 OR (D.ApplyInGroup = 0 and D.QtyUnit <> 0) OR (D.DisType <> 300 AND D.QtyUnit <> 0) THEN oi.TotalQty + (oi.TotalQty / (g.CartonType-g.CartonPrizeQty)) * g.CartonPrizeQty  \n" +
                "                  ELSE Round((oi.TotalQty+((oi.TotalQty/(g.CartonType-g.CartonPrizeQty))*g.CartonPrizeQty))/g.CartonType,2) END) \n" +
                "       END AS ReqQty \n" +
				"  , (CASE WHEN oi.PrizeType = 1 THEN 0 ELSE  \n" +
                "      CASE" +
				"       when o.DisType=2 or d.CalcMethod=1 then oi.TotalQty * oi.CustPrice   \n" +
				"       when o.DisType=4 or d.CalcMethod=2 then oi.AmountNut  \n" +
				"       when o.DisType=4 or d.CalcMethod=3 then oi.Amount - oi.Discount  \n" +
				"       else oi.TotalQty * oi.CustPrice  \n" +
				"      end   \n" +
				"   END) As ReqAmount " +
                "  ,  (CASE WHEN oi.PrizeType = 1 or oi.TotalQty = 0 THEN 0 ELSE 1 END ) AS ReqRowCount \n" +
                "  , D.MINQty As MINQty , D.MAXQty As MAXQty \n" +
                "  , (CASE WHEN D.DisType = 300 THEN IFNULL(P2.Quantity ,1)  \n" +
                "          WHEN D.DisType <> 300 AND D.QtyUnit <> 0 THEN 1  \n" +
                "          WHEN D.ApplyInGroup = 0 and D.QtyUnit <> 0 THEN oi.UnitCapasity \n" +
                "          ELSE 1 END) AS UnitCapasity \n" +
                "  , D.MINAmount AS MINAmount, D.MAXAmount AS MAXAmount ,D.ApplyInGroup \n" +
                "  , oi.TotalQty * G.Weight  as ReqWeight \n" +
                "  , D.MinWeight * 1000 as MinWeight , D.MaxWeight * 1000 as MaxWeight , D.MinRowsCount , D.MaxRowsCount  \n" +
				"  , CASE WHEN D.DisType = 300 AND PrizeStepType = 0 THEN D.PrizeStep * P2.Quantity ELSE PrizeStep END AS PrizeStep \n" +
                "  , PrizeStepType \n" +

                " FROM " + EVCTempDiscountDBAdapter.DATABASE_TABLE + " D \n"+
                " INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " o ON o.EVCId = '" + evcId + "' \n" +
                " INNER JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " oi ON oi.PrizeType=0 and oi.EVCRef = '" + evcId + "' \n" +
				" AND (oi.FreeReasonId IS NULL)   \n"+
//                " INNER JOIN SLE.tblCPrice CP ON CP.ID = oi.CPriceRef " +
                " LEFT JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " P2 ON D.GoodsRef = P2.ProductId AND D.QtyUnit = P2.BackOfficeId \n" +
                " LEFT JOIN " + EVCTempCustomerSDSDBAdapter.DATABASE_TABLE + " C ON C.Custref = o.CustRef \n" +
//                " LEFT  JOIN gnr.vwArea A on #Cust.AreaRef = A.ID " +
                " LEFT JOIN " + EVCTempGoodsDetailSDSDBAdapter.DATABASE_TABLE + " g ON g.id = oi.Goodsref \n" +
//                " LEFT  JOIN gnr.tblDCSaleOffice DSO ON o.DCSaleOfficeRef=DSO.ID "
//                " LEFT JOIN GNR.vwSalePath SP ON #Cust.SalePathRef = SP.ID    " +
                " LEFT JOIN " + EVCTempGoodsMainSubTypeSDSDBAdapter.DATABASE_TABLE + " GMST On G.ID = GMST.GoodsRef \n" +
				" LEFT JOIN " +  DiscountCustomerOldInvoiceHeaderDBAdapter.DATABASE_TABLE + " ret ON ret.SaleId = " + saleId + " \n"+
                " WHERE ((D.OrderType is null) or (D.OrderType = o.OrderType)) \n" +
				" AND (o.DisType=2 OR (o.DisType=4 and PrizeRef is not null)) \n" +
                " AND ((o.EvcType in (2,10,12) AND NOT (d.GoodsRef IS NOT NULL AND d.PrizeRef IS NOT NULL AND d.GoodsRef=d.PrizeRef AND IFNULL(d.MinQty,0) = 0 AND IFNULL(d.MinAmount,0) = 0)) OR (o.EvcType NOT IN (2,10,12) )) \n"+
				" AND (	(	(o.EvcType in (2,10,12))	\n" +
					" AND NOT(ret.SaleId  IS NULL) " +
					"AND (NOT exists (select 1 from " + EVCSkipDiscountDBAdapter.DATABASE_TABLE + " es WHERE es.SaleRef= ret.SaleId and es.DisRef=d.Id))\n"+
				"		)OR (o.EvcType not in (2,10,12) )"+
				") \n"+
				//Asal : FOLLOW_ORDER is Added in in 5.0
                " AND ((o.EVCType <> " + EVCType.TOSELL.value() +
						" AND o.EVCType <> " + EVCType.FOLLOW_ORDER.value() + " ) or IFNULL(d.IsActive, 1) = 1)  \n" + // در حواله/فاکتور جديد فقط قوانين فعال
                " AND ((D.PayType is null) or (D.PayType = o.PayType))  \n" +
                " AND ((D.AreaRef is null) or (D.AreaRef = IFNULL(C.AreaRef,0)))  \n" +
                " AND ((D.StateRef is null) or (D.StateRef = IFNULL(C.SalePathRef,0))) \n" +
				" AND ((D.DCRef is null) or (D.DCRef = o.DCRef))  \n" +
                " AND ((D.CustActRef is null) or (D.CustActRef = IFNULL(C.CustActRef,0)))  \n" +
                " AND ((D.CustCtgrRef is null) or (D.CustCtgrRef = IFNULL(C.CustCtgrRef,0)))   \n" +
                " AND ((D.CustLevelRef is null) or (D.CustLevelRef = IFNULL(C.CustLevelRef,0)))  \n" +
                " AND ((D.CustRef is null) or (D.CustRef = o.CustRef))  \n" +
                " AND ((D.SaleOfficeRef is null) or (D.SaleOfficeRef = o.SaleOfficeRef))  \n" +
                " AND ((D.SaleZoneRef is null) or (D.SaleZoneRef = IFNULL(C.ZoneId,0))) \n" +
                " AND (D.GoodsGroupRef IS NULL OR g.GoodsGroupRef IN ( SELECT CT.ID \n" +
                "       FROM " + EVCTempGoodsGroupDetailSDSDBAdapter.DATABASE_TABLE + " CT \n" +
                "       INNER JOIN " + EVCTempGoodsGroupDetailSDSDBAdapter.DATABASE_TABLE + " P ON CT.NLEFT BETWEEN P.NLEFT AND P.NRIGHT \n" +
                "       WHERE P.ID = D.GoodsGroupRef)) \n" +
                " AND ((D.GoodsCtgrRef is null) or (D.GoodsCtgrRef =  g.GoodsCtgrRef)) \n" +
                " AND ((D.ManufacturerRef is null) or (D.ManufacturerRef = g.ManufacturerRef)) \n" +
                " AND ((D.GoodsRef is null) or (D.GoodsRef = oi.GoodsRef)) \n" +
                " AND ((D.OrderNo IS NULL ) OR (D.OrderNo='" + orderNo + "')) \n" +
                " AND ((D.MainTypeRef is null) or (D.MainTypeRef = IFNULL(GMST.MainTypeRef,0))) \n" +
                " AND ((D.SubTypeRef is null) or (D.SubTypeRef = IFNULL(GMST.SubTypeRef,0))) \n" +
                " AND ((D.BrandRef is null) or (D.BrandRef = IFNULL(G.BrandRef,0))) \n" +

		" AND (IFNULL(C.CustomerRemain,0) Between IFNULL(D.MinCustRemAmount,-999999999999) AND IFNULL(D.MaxCustRemAmount,IFNULL(C.CustomerRemain,0))) " +


				" AND (o.EVCType<>10 OR (o.EVCType=10  AND D.PrizeRef IS NULL "+
				" AND NOT EXISTS (select 1 from "+ DiscountProductPackageDBAdapter.DATABASE_TABLE +"  gp  where gp.DiscountRef =D.Id) ))\n"+
				" AND ('" + orderDate  + "' BETWEEN D.StartDate AND IFNULL(D.EndDate, '" + orderDate + "')) \n" +
                " ) AS X";

		db.execSQL(sql);
	}


	public void fillEVCTemp4_19(String evcId, int orderNo, String orderDate, String orderType, int saleId, int evcType)
	{

		String sqldelete = "DELETE FROM " + DATABASE_TABLE + " WHERE EvcId = '" +evcId+ "'";
		db.execSQL(sqldelete);

		//#tmpTable
		String sql = "INSERT INTO " + DATABASE_TABLE + " (DiscountId, DisGroup, DisType, EVCItemRefId, RowOrder, Priority, ReqQty, ReqAmount, MinQty, MaxQty \n" +
				" , MinAmount, MaxAmount, ApplyInGroup, ReqWeight, MinWeight, MaxWeight,  MinRowsCount, MaxRowsCount, ReqRowCount, PrizeStep, PrizeStepType \n " +
				" , TotalMinAmount, TotalMaxAmount, TotalMinRowCount, TotalMaxRowCount "+
				" , EvcId) \n" +

				" SELECT DisID ,DisGroup ,DisType ,EVCItemRef ,RowOrder ,Priority ,ifnull(ReqQty,0) as ReqQty" +
				"  , ReqAmount ,IFNULL((IFNULL(MINQty,0) * UnitCapasity)/*+case when PrizeStepType=0 then IFNULL(PrizeStep,0) else 0 end DMC-35565 */, case when PrizeStepType=0 then PrizeStep else 0 end ) As MINQty    \n" +
				"  , MAXQty * UnitCapasity As MAXQty " +
				"  , IFNULL(IFNULL(MINAmount,0)+case when PrizeStepType=1 then IFNULL(PrizeStep,0) else 0 end, case when PrizeStepType=0 then PrizeStep else 0 end ) AS MINAmount " +
				"  , MAXAmount ,ApplyInGroup, IFNULL(ReqWeight,0) as ReqWeight , MinWeight , MaxWeight , MinRowsCount \n" +
				"  , MaxRowsCount, ReqRowCount , PrizeStep , PrizeStepType , TotalMinAmount, TotalMaxAmount, TotalMinRowCount, TotalMaxRowCount " +
				"  ,'" + evcId  + "' \n" +

				"  FROM (   \n" +
				" SELECT DISTINCT D.ID as DisID , D.DisGroup , D.DisType , OI._id As EVCItemRef , OI.RowOrder , D.priority    \n" +
				"   , CASE IFNULL(g.CartonPrizeQty, 0) \n" +
				"       WHEN 0 THEN (CASE WHEN D.DisType=300 OR (D.ApplyInGroup=0 and Ifnull(D.prizestepunit, D.QtyUnit) <> 0) " +
				"	OR (D.DisType <> 300 AND Ifnull(D.prizestepunit, D.QtyUnit) <> 0) THEN oi.TotalQty \n" +
				"                         ELSE Round(CAST (oi.TotalQty/g.CartonType AS INT),2) END)  \n" +
				"       ELSE (Case WHEN D.DisType = 300 OR (D.ApplyInGroup = 0 and Ifnull(D.PrizeStepUnit, D.QtyUnit) <> 0) " +
				"	OR (D.DisType <> 300 AND Ifnull(D.PrizeStepUnit, D.QtyUnit) <> 0) THEN oi.TotalQty + (oi.TotalQty / (g.CartonType-g.CartonPrizeQty)) * g.CartonPrizeQty  \n" +
				"                  ELSE Round((oi.TotalQty+((oi.TotalQty/(g.CartonType-g.CartonPrizeQty))*g.CartonPrizeQty))/g.CartonType,2) END) \n" +
				"       END AS ReqQty \n" +
				"  , (CASE WHEN oi.PrizeType = 1 THEN 0 ELSE  \n" +
				"      CASE" +
				"       when o.DisType=2 or d.CalcMethod=1 then oi.TotalQty * oi.CustPrice   \n" +
				"       when o.DisType=4 or d.CalcMethod=2 then oi.AmountNut  \n" +
				"       when o.DisType=4 or d.CalcMethod=3 then oi.Amount - oi.Discount  \n" +
				"       else oi.TotalQty * oi.CustPrice  \n" +
				"      end   \n" +
				"   END) As ReqAmount " +
				"  ,  (CASE WHEN oi.PrizeType = 1 or oi.TotalQty = 0 THEN 0 ELSE 1 END ) AS ReqRowCount \n" +
				"  , D.MINQty As MINQty , D.MAXQty As MAXQty \n" +
				"  , (CASE WHEN D.DisType = 300 THEN IFNULL(PackQtyUnit.Quantity ,1)  \n" +
				"          WHEN D.DisType <> 300 AND D.QtyUnit <> 0 THEN 1  \n" +
				"          WHEN D.ApplyInGroup = 0 and D.QtyUnit <> 0 THEN oi.UnitCapasity \n" +
				"          ELSE 1 END) AS UnitCapasity \n" +
				"  , D.MINAmount AS MINAmount, D.MAXAmount AS MAXAmount ,D.ApplyInGroup \n" +
				"  , oi.TotalQty * G.Weight  as ReqWeight \n" +
				"  , D.MinWeight * 1000 as MinWeight , D.MaxWeight * 1000 as MaxWeight , D.MinRowsCount , D.MaxRowsCount  \n" +
				"  , CASE WHEN D.DisType = 300 AND PrizeStepType = 0 THEN D.PrizeStep * P2.Quantity ELSE PrizeStep END AS PrizeStep \n" +
				"  , PrizeStepType \n" +
				"  , TotalMinAmount, TotalMaxAmount, TotalMinRowCount, TotalMaxRowCount  \n " +
				" FROM " + EVCTempDiscountDBAdapter.DATABASE_TABLE + " D \n"+
				" INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " o ON o.EVCId = '" + evcId + "' \n" +
				" INNER JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " oi ON oi.PrizeType=0 and oi.EVCRef = '" + evcId + "' \n" +
				" AND (oi.FreeReasonId IS NULL)   \n"+
				//" INNER JOIN ContractPrice CP ON CP.ID = oi.CPriceRef " +
				" LEFT JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " P2 ON D.GoodsRef = P2.ProductId AND D.PrizeStepUnit = P2.BackOfficeId \n" +

				" LEFT JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " PackQtyUnit ON D.GoodsRef = PackQtyUnit.ProductId AND D.QtyUnit = PackQtyUnit.BackOfficeId \n" +

		" LEFT JOIN " + EVCTempCustomerSDSDBAdapter.DATABASE_TABLE + " C ON C.Custref = o.CustRef \n" +
//                " LEFT  JOIN gnr.vwArea A on #Cust.AreaRef = A.ID " +
				" LEFT JOIN " + EVCTempGoodsDetailSDSDBAdapter.DATABASE_TABLE + " g ON g.id = oi.Goodsref \n" +
//                " LEFT  JOIN gnr.tblDCSaleOffice DSO ON o.DCSaleOfficeRef=DSO.ID "
//                " LEFT JOIN GNR.vwSalePath SP ON #Cust.SalePathRef = SP.ID    " +
				" LEFT JOIN " + EVCTempGoodsMainSubTypeSDSDBAdapter.DATABASE_TABLE + " GMST On G.ID = GMST.GoodsRef \n" +
				" LEFT JOIN " +  DiscountCustomerOldInvoiceHeaderDBAdapter.DATABASE_TABLE + " ret ON ret.SaleId = " + saleId + " \n"+

				" WHERE ((D.OrderType is null) or (D.OrderType = o.OrderType)) \n" +
				" AND (o.DisType=2 OR (o.DisType=4 and (PrizeRef is not null" +
				"  or exists (select 1 from DiscountGoodsPackage gp where gp.DiscountRef=d.id)   \n" +
				"  or exists (select 1 from " + DiscountItemDBAdapter.DATABASE_TABLE +" where DisRef = d.Id))))  \n " +
				" AND ((o.EvcType in (2,10,12) AND NOT (d.GoodsRef IS NOT NULL AND d.PrizeRef IS NOT NULL AND d.GoodsRef=d.PrizeRef AND IFNULL(d.MinQty,0) = 0 AND IFNULL(d.MinAmount,0) = 0)) OR (o.EvcType NOT IN (2,10,12) )) \n"+
				" AND (	(	(o.EvcType in (2,10,12))	\n" +
				" AND NOT(ret.SaleId  IS NULL) " +
				"AND (NOT exists (select 1 from " + EVCSkipDiscountDBAdapter.DATABASE_TABLE + " es WHERE es.SaleRef= ret.SaleId and es.DisRef=d.Id))\n"+
				"		)OR (o.EvcType not in (2,10,12) )"+
				") \n"+
				//Asal : FOLLOW_ORDER is Added in in 5.0
				" AND ((o.EVCType <> " + EVCType.TOSELL.value() +
				" AND o.EVCType <> " + EVCType.FOLLOW_ORDER.value() + " ) or IFNULL(d.IsActive, 1) = 1)  \n" + // در حواله/فاکتور جديد فقط قوانين فعال
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.DCRef=o.DCRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.DCRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.PaymentUsanceRef=o.PaymentUsanceRef) or not exists (select 1 from " + DiscountConditionDBAdapter.DATABASE_TABLE+ " dc where dc.DiscountRef=d.Id and dc.PaymentUsanceRef is not null)) \n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustActRef=C.CustActRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustActRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustCtgrRef=C.CustCtgrRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustCtgrRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustLevelRef=C.CustLevelRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustLevelRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.PayType=o.PayType) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.PayType is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.SaleOfficeRef= o.SaleOfficeRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.SaleOfficeRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.StateRef=C.SalePathRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.StateRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.AreaRef=C.AreaRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.AreaRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.SaleZoneRef= IFNULL(C.ZoneId,0) ) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.SaleZoneRef is not null))\n"+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.OrderNo='"+orderNo +"' and IFNULL(dc.DCRef, o.DCRef)=o.DCRef and dc.SaleOfficeRef= o.SaleOfficeRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc  where dc.DiscountRef = d.Id and dc.OrderNo is not null)) \n" +
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustRef=o.CustRef) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustRef is not null)) \n "+
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.OrderType='"+orderType+"') or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.OrderType is not null)) \n " +
				" AND (exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and "+
				"	dc.CustGroupRef in(SELECT CBG.id FROM "+ DiscountCustomerBoGroupDBAdapter.DATABASE_TABLE + " CBG join "+ DiscountCustomerBoGroupDBAdapter.DATABASE_TABLE + " P ON CBG.NLEFT BETWEEN P.NLEFT AND P.NRIGHT WHERE P.id=C.CustGroupRef)) or not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.DiscountRef=d.Id and dc.CustGroupRef is not null)) \n"+

		" AND ( " +
				"not exists (select 1 from "+ DiscountConditionDBAdapter.DATABASE_TABLE + " dc where dc.Discountref=d.Id and (dc.MainCustTypeRef is not null or dc.SubCustTypeRef is not null )) \n"+
				" OR  \n" +
				"(IFNULL(d.MixCondition, 0)=1 and not exists ( SELECT 1 FROM ("+
				"	SELECT dc.MainCustTypeRef Discount_MainCustTypeRef, dc.SubCustTypeRef as Discount_SubCustTypeRef " +
				" , cms.MainTypeRef Cust_MainTypeRef, cms.SubTypeRef as Cust_SubTypeRef "+
				"	from " + DiscountConditionDBAdapter.DATABASE_TABLE +" dc "+
				"	left join " + EVCTempCustomersMainSubTypeSDSDBAdapter.DATABASE_TABLE +" cms on  dc.MainCustTypeRef=cms.MainTypeRef "+
				"	where discountref=d.Id " +
				"	and (dc.MainCustTypeRef is not null or dc.SubCustTypeRef is not null) ) A \n"+
				" WHERE (Discount_MainCustTypeRef<>IFNULL(Cust_MainTypeRef, 0) AND Discount_SubCustTypeRef IS NULL) OR \n" +
				" (Discount_SubCustTypeRef IS NOT NULL and Discount_MainCustTypeRef=Cust_MainTypeRef AND Discount_SubCustTypeRef<>Cust_SubTypeRef) "+
				") )\n" +
				" OR "+
				"(ifnull(d.MixCondition, 0)=0 and exists (  SELECT 1 FROM ("+
				"SELECT dc.MainCustTypeRef Discount_MainCustTypeRef, dc.SubCustTypeRef as Discount_SubCustTypeRef  \n" +
				" , cms.MainTypeRef Cust_MainTypeRef, cms.SubTypeRef as Cust_SubTypeRef "+
				"FROM " + DiscountConditionDBAdapter.DATABASE_TABLE +" dc \n"+
				"LEFT JOIN " + EVCTempCustomersMainSubTypeSDSDBAdapter.DATABASE_TABLE +" cms on dc.SubCustTypeRef=cms.SubTypeRef  \n"+ //and dc.MainCustTypeRef=cms.MainTypeRef
				"WHERE discountref=d.Id and (dc.MainCustTypeRef is not null and dc.SubCustTypeRef is not null )  " +
				") AS A \n" +
				" WHERE (Discount_MainCustTypeRef=ifnull(Cust_MainTypeRef, 0) and Discount_SubCustTypeRef is null) or   \n" +
				"       (Discount_SubCustTypeRef is not null AND Discount_MainCustTypeRef=Cust_MainTypeRef and Discount_SubCustTypeRef=Cust_SubTypeRef)  \n" +
				"     )   \n" +
				"    )  \n" +
				"   )\n" +

				" AND (D.GoodsGroupRef IS NULL OR g.GoodsGroupRef IN ( SELECT CT.ID " +
				"       FROM " + EVCTempGoodsGroupDetailSDSDBAdapter.DATABASE_TABLE + " CT " +
				"       INNER JOIN " + EVCTempGoodsGroupDetailSDSDBAdapter.DATABASE_TABLE + " P ON CT.NLEFT BETWEEN P.NLEFT AND P.NRIGHT " +
				"       WHERE P.ID = D.GoodsGroupRef)) \n" +
				" AND ((D.GoodsCtgrRef is null) or (D.GoodsCtgrRef =  g.GoodsCtgrRef)) \n" +
				" AND ((D.ManufacturerRef is null) or (D.ManufacturerRef = g.ManufacturerRef)) \n" +
				" AND ((D.GoodsRef is null) or (D.GoodsRef = oi.GoodsRef)) \n" +
				//remove 4.19 last :" AND ((D.OrderNo IS NULL ) OR (D.OrderNo='" + orderNo + "')) \n" +
				" AND ((D.MainTypeRef is null) or (D.MainTypeRef = IFNULL(GMST.MainTypeRef,0))) \n" +
				" AND ((D.SubTypeRef is null) or (D.SubTypeRef = IFNULL(GMST.SubTypeRef,0))) \n" +
				" AND ((D.BrandRef is null) or (D.BrandRef = IFNULL(G.BrandRef,0))) \n" ;

		if(evcType == 2 || evcType == 10 || evcType == 12) {
		sql +=
				" AND (C.CustomerRemain Between IFNULL(D.MinCustRemAmount,-999999999999) AND IFNULL(D.MaxCustRemAmount,C.CustomerRemain)) " +
				" AND (IFNULL(C.CustChequeRetQty,0) Between IFNULL(D.MinCustChequeRetQty,0) AND IFNULL(D.MaxCustChequeRetQty,IFNULL(c.CustChequeRetQty,0))) ";
		}

		sql +=
		" AND (o.EVCType<>10 OR (o.EVCType=10 "+
				" AND D.PrizeRef IS NULL "+
				//???
				//" AND NOT EXISTS (select 1 from " + sle.tblGoodsPackage + " gp where gp.DiscountRef=D.Id) "+
				" AND NOT EXISTS (select 1 from " + DiscountItemDBAdapter.DATABASE_TABLE +" pl where pl.DisRef=D.Id) "+
				"))\n" +

				" AND ('" + orderDate + "' BETWEEN D.StartDate AND IFNULL(D.EndDate, '" + orderDate + "')) \n" +
				" ) AS X";

		db.execSQL(sql);
	}

	public void clearAllData(SQLiteDatabase db)
	{
		db.delete(DATABASE_TABLE, null, null);
	}

	public void deleteAllEVCTemps()
	{
		db.delete(DATABASE_TABLE, null, null);
	}

	public void deleteByTemp3(String evcId)
	{
		String sql = " DELETE FROM " + DATABASE_TABLE +
				" WHERE EXISTS ( SELECT 1 FROM EVCTempSummary3SDS t2 WHERE EVCTempSDS.DisGroup=t2.DisGroup AND EVCTempSDS.EVCItemRefId = t2.EvcItemRef) "+
				" AND EVCId = '" + evcId +"'";
		db.execSQL(sql);
	}

	public void deletePriority(String evcId)
	{
		String sql = " DELETE FROM " + DATABASE_TABLE +
			" WHERE EVCId = '" + evcId +"' AND _id in (SELECT T1._id " +
				" FROM " + DATABASE_TABLE + " AS T1 "+
				"INNER JOIN (\n" +
					"SELECT DisGroup, MIN(Priority) AS MinPriority\n" +
					"FROM " + EVCTempSummarySDSDBAdapter.DATABASE_TABLE +"\n" +
					"Group By DisGroup\n" +
				") T2 ON T1.DisGroup=T2.DisGroup AND T1.Priority<T2.MinPriority\n"+
			")";
		db.execSQL(sql);
	}
}

