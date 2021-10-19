package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;

import android.database.Cursor;

import java.math.BigDecimal;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.price.DiscountContractPriceVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;


public class EVCTempSummaryFinalVnLiteDBAdapter extends DiscountBaseDataAdapter
{

	public final String KEY_EVC_TEMP_FINAL_ROWID = "_id";
	public final String KEY_EVC_TEMP_FINAL_PRODUCT_ID = "ProductId";
	public final String KEY_EVC_TEMP_FINAL_EVC_ID = "EVCId";
	public final String KEY_EVC_TEMP_FINAL_QTY = "Qty";
	public final String KEY_EVC_TEMP_FINAL_DISCOUNT_ID = "DistId";
	public final String KEY_EVC_TEMP_FINAL_UNITPRICE = "UnitPrice";
	public final String KEY_EVC_TEMP_FINAL_PRICE_ID = "PriceId";
    public final String KEY_EVC_TEMP_FINAL_REDUCE_QTY = "ReduceOfQty";



	public static final String DATABASE_TABLE = "EVCTempSummaryFinalVnLite";

	private static EVCTempSummaryFinalVnLiteDBAdapter instance;


	public EVCTempSummaryFinalVnLiteDBAdapter()
	{
	}
	
	public static EVCTempSummaryFinalVnLiteDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCTempSummaryFinalVnLiteDBAdapter();
		}
		
		return instance;
		
	}
	 

	public void deleteAllEVCYTempSummarieById(String evcId)
	{
		db.delete(DATABASE_TABLE, "evcId='" + evcId + "'", null);
	}

	public void fillEVCItemSummaryFinalOld(String evcId)
	{
		String sql = "";
		sql = "insert into " + DATABASE_TABLE + " (ProductId, Qty, DisId, UnitPrice, PriceId, ReduceOfQty, EvcId) \n" +

                " select D.PrizeRef AS ProductId \n" +
                " , (CASE  \n" +
				"     WHEN (D.MinQty IS Not NULL) AND IFNULL(D.PrizeStep,0)>0 \n" +
                "       THEN CAST(( ( SUM(EI.TotalQty / ( case when iFnull(g.LargeUnitQty,0)=0 then 1   \n" +
				"                                               when substr(cast(D.DisType as text),4,1)='5' then  ifnull(g.LargeUnitQty,0)  \n" +
				"                                               else 1 end)) \n" +
                "                       - D.MinQty) / D.PrizeStep) AS INT) * D.PrizeQty  \n" +
				"     WHEN IFNULL(D.PrizeStep,0)>0 \n" +
                "       THEN CAST((( SUM(EI.TotalQty / ( case when iFnull(g.LargeUnitQty,0)=0 then 1   \n" +
				"                                              when substr(cast(D.DisType as text),4,1)='5' then ifnull(g.LargeUnitQty,0)  \n" +
				"                                              else 1 end ))) \n" +
                "                       / D.PrizeStep) AS INT) * D.PrizeQty   \n" +
				"  ELSE IFNULL(D.PrizeQty,1) END) AS UnitQty \n" +
				" , ES.DisRef \n" +
				//TODO Asal Change to price
				",0 as UnitPrice \n" +//" , CP.UnitPrice as UnitPrice \n" +
                ",0 AS PriceId  " +//" , CP.PriceId AS PriceId  \n" +
				" , D.ReduceOfQty as ReduceOfQty \n" +
                " , E.EVCId AS EVCId \n" +

                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n" +
				" INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EVCID= EI.EVCRef \n" +
				" INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +" ES ON ES.EVCItemRef = EI._id \n" +

				" INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D ON D.PromotionDetailId = ES.DisRef  \n" +
				" INNER JOIN " + DiscountCustomerDBAdapter.DATABASE_TABLE + " C ON C.CustomerId= E.CustRef \n" +
				" INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " G ON G.ProductID=EI.GoodsRef \n" +

               //TODO Asal Change to price
                //" INNER JOIN " + CustomerProductPriceDBAdapter.DATABASE_TABLE + " CP ON CP.CustomerId = C.CustomerId AND CP.ProductId =  D.PrizeRef \n"+
				" WHERE EI.EVCRef='" + evcId + "' \n" +
				" AND  D.IsPrize = 1 \n" +
				" AND IFNULL(D.CustCtgrRef , IFNULL(c.CustomerCategoryId,0) ) = IFNULL(c.CustomerCategoryId,0) \n" +
				"    AND IFNULL(D.CustRef , c.CustomerId ) = c.CustomerId \n" +
				"    AND IFNULL(D.GoodsCtgrRef , g.ProductBOGroupId) = g.ProductBOGroupId \n" +
				"    AND IFNULL(D.GoodsRef , g.ProductId) = g.ProductId \n" +
				"    AND IFNULL(IFNULL(D.ManufacturerRef , g.ManufacturerId),0) = IFNULL( g.ManufacturerId,0) \n" +
				" AND IFNULL(D.PayType ,IFNULL(e.PayType,0))= IFNULL(e.PayType,0) \n" +
				" AND IFNULL(D.ProductSubGroup1Id , IFNULL(g.ProductSubGroupId,0)) = IFNULL(g.ProductSubGroupId,0) \n" +
				" AND IFNULL(D.ProductSubGroup2Id , IFNULL(g.ProductSubGroup2Id,0)) = IFNULL(g.ProductSubGroup2Id,0) \n" +
				" AND IFNULL(D.CustomerSubGroup1Id , IFNULL(c.CustomerSubGroup1Id,0) ) = IFNULL(c.CustomerSubGroup1Id,0) \n" +
				" AND IFNULL(D.CustomerSubGroup2Id , IFNULL(c.CustomerSubGroup2Id,0) ) = IFNULL(c.CustomerSubGroup2Id,0) \n" +
				//" AND IFNULL(D.CenterId , IFNULL(e.CenterId,0) ) = IFNULL(e.CenterId,0)\n" +
				" GROUP BY ES.DisRef, D.PrizeRef, D.PrizeQty, D.MinQty, D.PrizeStep, E.EVCID , D.DisType , D.ReduceOfQty\n";
				//"   ,CP.PriceId, CP.UnitPrice\n";

		db.execSQL(sql);

	}

	public void fillEVCItemSummaryFinal(String evcId, String callId)
	{
		String selectData =
				" SELECT ProductId, (UnitQty*unitcapacity) AS Qty,DisRef AS PromotionDetailId, UnitPrice,PriceId, ReduceOfQty, EVCid  \n" +
				" FROM(  \n" +
				"    SELECT    ES.DisRef \n" +
				"          , (CASE   \n" +
				"          WHEN (D.MinQty IS Not NULL) AND IFNULL(D.PrizeStep,0)>0 THEN CAST(((SUM(EI.TotalQty/( case when IFNULL(g.LargeUnitQty,0)=0 then 1    \n" +
				"                                  when substr(cast(D.DisType as CHAR(4)),4,1)='5' then  IFNULL(g.LargeUnitQty,0)   \n" +
				"                                     else 1      \n" +
				"                                end)) - D.MinQty) / D.PrizeStep)AS INT) * D.PrizeQty   \n" +
				"           WHEN IFNULL(D.PrizeStep,0)>0 THEN CAST(((SUM(EI.TotalQty/( case when ifnull(g.LargeUnitQty,0)=0 then 1    \n" +
				"                                                  when substr(cast(D.DisType as CHAR(4)),4,1)='5' then  ifnull(g.LargeUnitQty,0)   \n" +
				"                                                     else 1      \n" +
				"                                                end ))) / D.PrizeStep)AS INT) * D.PrizeQty   \n" +
				"   ELSE IFNULL(D.PrizeQty,1) END) AS UnitQty  \n" +
				" ,D.PrizeRef AS ProductId  \n" +
				" ,1 as unitcapacity  \n" +
				" ,E.EVCId AS EVCId \n" +
				" ,E.ACCYear AS FiscalYearId  \n" +
				" ,CP.BackofficeId AS PriceId\n" +
				" ,SellPrice as UnitPrice  \n" +
				" ,D.ReduceOfQty \n" +
				" ,E.EVCId AS EVCId \n" +
				" FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n" +
				" INNER JOIN "+ EVCHeaderVnLiteDBAdapter.DATABASE_TABLE +" E ON E.EVCID= EI.EVCRef\n" +
				" INNER JOIN "+ EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +" ES ON EI._id= ES.EVCItemRef \n" +
				" INNER JOIN "+ DiscountVnLiteDBAdapter.DATABASE_TABLE +"  D ON D.PromotionDetailId = ES.DisRef \n" +
				" LEFT JOIN "+ DiscountContractPriceVnLiteDBAdapter.DATABASE_TABLE +" CP ON CP.BackofficeId= (SELECT CP2.BackofficeId \n" +
				" FROM " + DiscountContractPriceVnLiteDBAdapter.DATABASE_TABLE + " CP2  \n" +
				" INNER JOIN "+ DiscountCustomerDBAdapter.DATABASE_TABLE + " C2 ON C2.CustomerId= E.CustRef\n" +
				" WHERE 1 = 1    \n" +
				" AND (CP2.ProductRef Is Not Null And CP2.ProductRef = D.PrizeRef)  \n" +
				" AND (E.DateOf BETWEEN CP2.StartDate AND IFNULL(CP2.EndDate,E.DateOf))  \n" +
				" AND IFNULL(CP2.CustomerRef, E.CustRef) = E.CustRef  \n" +
				" AND IFNULL(CP2.CustomerGroupRef, IFNULL(C2.CustomerCategoryId,0)) = IFNULL(C2.CustomerCategoryId,0)  \n" +
				" /*VNPL***/  \n" +
				" AND IFNULL(CP2.CustomerSubGroup1Ref, IFNULL(C2.CustomerSubGroup1Id,0) ) = IFNULL(C2.CustomerSubGroup1Id,0)  \n" +
				" AND IFNULL(CP2.CustomerSubGroup2Ref, IFNULL(C2.CustomerSubGroup2Id,0) ) = IFNULL(C2.CustomerSubGroup2Id,0)  \n" +
				" AND IFNULL(CP2.TargetCenterRef , IFNULL(E.DcRef,0)) = IFNULL(E.DcRef,0)  \n" +
				" /*VNPL***/  \n" +
				" ORDER BY CP2.ProductRef Desc \n" +
				" LIMIT 1  \n" +
				") \n" +
				" INNER JOIN " + DiscountCustomerDBAdapter.DATABASE_TABLE + " C ON C.CustomerId= E.CustRef \n" +
				" INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + "  G ON G.ProductID=EI.GoodsRef \n" +
				" WHERE EI.EVCRef='" + evcId + "' \n" +
				" AND  D.IsPrize = 1  \n" +
				" AND IFNULL(D.CustCtgrRef , IFNULL(c.CustomerCategoryId,0) ) = IFNULL(c.CustomerCategoryId,0)  \n" +
				" AND IFNULL(D.CustRef , c.CustomerId ) = c.CustomerId  \n" +
				" AND IFNULL(D.GoodsCtgrRef , g.ProductBOGroupId) = g.ProductBOGroupId  \n" +
				" AND IFNULL(D.GoodsRef , g.ProductId) = g.ProductId  \n" +
				" AND IFNULL(IFNULL(D.ManufacturerRef , g.ManufacturerId),0) = IFNULL( g.ManufacturerId,0)  \n" +
				" AND IFNULL(D.PayType ,IFNULL(e.PayType,0))= IFNULL(e.PayType,0)  \n" +
				" AND IFNULL(D.ProductSubGroup1Id , IFNULL(g.ProductSubGroup1Id,0)) = IFNULL(g.ProductSubGroup1Id,0)  \n" +
				" AND IFNULL(D.ProductSubGroup2Id , IFNULL(g.ProductSubGroup2Id,0)) = IFNULL(g.ProductSubGroup2Id,0)  \n" +
				" AND IFNULL(D.CustomerSubGroup1Id , IFNULL(c.CustomerSubGroup1Id,0) ) = IFNULL(c.CustomerSubGroup1Id,0)  \n" +
				" AND IFNULL(D.CustomerSubGroup2Id , IFNULL(c.CustomerSubGroup2Id,0) ) = IFNULL(c.CustomerSubGroup2Id,0)  \n" +
				" AND IFNULL(D.DcRef , IFNULL(e.DcRef,0) ) = IFNULL(e.DcRef,0) \n" +
				" GROUP BY ES.DisRef, D.PrizeRef, D.PrizeQty, D.MinQty, D.PrizeStep, E.EVCID , D.DisType , D.ReduceOfQty\n" +
				") X  ";

		int productId;	BigDecimal qty; int promotionDetailId; BigDecimal unitPrice; int priceId;int reduceOfQty;

		Cursor c = db.rawQuery(selectData, null);
		if (c != null && c.moveToFirst()) {
			do {

				productId = c.getInt(c.getColumnIndex("ProductId"));
				qty = new BigDecimal(c.getDouble(c.getColumnIndex("Qty")));
				promotionDetailId = c.getInt(c.getColumnIndex("PromotionDetailId"));
				unitPrice = new BigDecimal(c.getDouble(c.getColumnIndex("UnitPrice")));
				priceId = c.getInt(c.getColumnIndex("PriceId"));
				reduceOfQty = c.getInt(c.getColumnIndex("ReduceOfQty"));

				/*
				int prizeQtyMaxLimit;
				select @PrizeQtyMaxLimit = isnull(PrizeQtyMaxLimit, 0)
				from PromotionDetail
				where PromotionDetailId = @PromotionDetailId

				if @PrizeQtyMaxLimit>0
				begin
				declare @TotalPrizeQty int
				select @TotalPrizeQty = sum(Qty)
				from SellDetailPromotionDetail pd
				inner join SellDetail sd on pd.SellDetailId=sd.SellDetailId and pd.PromotionDetailId=@PromotionDetailId and sd.IsPrize=1
				inner join Sell s on s.SellId=sd.SellId and s.IsCanceled=0
				if @PrizeQtyMaxLimit < isnull(@TotalPrizeQty, 0) + @Qty
						begin
				if @PrizeQtyMaxLimit - isnull(@TotalPrizeQty, 0) > 0
				set @Qty = @PrizeQtyMaxLimit - isnull(@TotalPrizeQty, 0)
     			else
				set @Qty = 0

				if @Qty = 0
				delete from EVCDetailStatutes
				where PromotionDetailId = @PromotionDetailId


						end
				end
				*/
				if (qty.compareTo(BigDecimal.ZERO) > 0) {
					//declare @EVCDetailID int
					EVCItemVnLiteDBAdapter.getInstance().fillByEVCItemPreview(evcId, callId, productId, qty, unitPrice, promotionDetailId, priceId);
					//SET @EVCDetailID =SCOPE_IDENTITY()
/*
					if (reduceOfQty == 1) {
						EVCItemVnLiteDBAdapter.getInstance().updateReduceOfQty(evcId);

						Declare @MainEVCDetailid int , @MainQty float
						set @MainEVCDetailid =0
						set @MainQty=0

						select @MainEVCDetailid=EVCDetailid, @MainQty=Qty
						from EVCDetail
						where EVCID= @EVCid And ProductId = @ProductId And IsPrize =0

						if(@MainQty >= @Qty and @MainEVCDetailid<>0)
						begin
						update EVCDetail
						Set Qty = Qty - @Qty
								, Amount = (Qty - @Qty)*UnitPrice
						where EVCDetailID= @MainEVCDetailid

						update EVCDetail
						set DetailId =(select DetailId from EVCDetail where EVCDetailID = @MainEVCDetailid)
						where EVCDetailID= @EVCDetailID
					}
						*/
				}

			} while (c.moveToNext());
		}
	}

	public void clearAllData()
	{
		db.delete(DATABASE_TABLE, null, null);
	}
}
	 
