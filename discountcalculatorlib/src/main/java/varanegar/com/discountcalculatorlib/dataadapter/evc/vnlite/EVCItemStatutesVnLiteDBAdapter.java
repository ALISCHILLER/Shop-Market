package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;


import android.database.Cursor;

import java.math.BigDecimal;
import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCSkipDiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.entity.customer.DiscountCustomer;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountEvcItemStatuteData;

public class EVCItemStatutesVnLiteDBAdapter extends DiscountBaseDataAdapter
{

	public final String KEY_EVC_ITEM_STATUTES_ROWID = "_id";
    public static final String KEY_EVC_ITEM_STATUTES_ITEM_REF = "EVCItemRef";
    public static final String KEY_EVC_ITEM_STATUTES_ROW_ORDER = "RowOrder";
    public static final String KEY_EVC_ITEM_STATUTES_DIS_REF = "DisRef";
    public static final String KEY_EVC_ITEM_STATUTES_DIS_GROUP = "DisGroup";
    public static final String KEY_EVC_ITEM_STATUTES_ADD_AMOUNT = "AddAmount";
    public static final String KEY_EVC_ITEM_STATUTES_SUP_AMOUNT = "SupAmount";
    public static final String KEY_EVC_ITEM_STATUTES_DISCOUNT = "Discount";
    public static final String KEY_EVC_ITEM_STATUTES_EVC_ID = "EvcId";

    public static final String KEY_EVC_ITEM_STATUTES_PRODUCT_ID = "ProductId";
    public static final String KEY_EVC_ITEM_STATUTES_DIS_AMOUNT = "DisAmount";
    public static final String KEY_EVC_ITEM_STATUTES_DETAIL_ROW_ORDER = "EVCDetailRowOrder";


	public static final String DATABASE_TABLE = "EVCItemStatutesVnLite";


	private static EVCItemStatutesVnLiteDBAdapter instance;


	public EVCItemStatutesVnLiteDBAdapter()
	{
	}
	
	public static EVCItemStatutesVnLiteDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCItemStatutesVnLiteDBAdapter();
		}
		
		return instance;
		
	}
	 

	public void deleteAllEVCItemStatutesById(String evcId)
	{
		db.delete(DATABASE_TABLE, "evcId = '" + evcId + "'", null);
	}
    /*usp_FillEVCStatuteByID*/
	public void fillEVCTempOld(String evcId)
	{
        /*EVCDetailStatutes*/
		String sql = "insert into " + DATABASE_TABLE + " (ProductId, DisRef, DisGroup, DisAmount, AddAmount, EVCId, EVCDetailRowOrder, EVCItemRef) \n" +

				" select ProductId ,DisID  , DisGroup ,0 ,0 , '" + evcId + "', oi.RowOrder, oi._id \n" +
                " FROM " + EVCTempSummaryVnLiteDBAdapter.DATABASE_TABLE + " es \n" +
                " INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +" oi ON oi.EVCRef = es.EVCId \n" +
                "       AND oi.EVCRef = '" + evcId + "' AND oi.GoodsRef = es.MainProductId AND oi.PrizeType = 0 \n" +
				" WHERE IFNULL(cast(ProductId as text),'') || '-' || IFNULL(cast(DisGroup as text),'') || '-' || IFNULL(cast(Priority as text) , '') \n" +
				" 	IN (SELECT ( ifnull(cast(ProductId as text),'') || '-' || IFNULL(cast(DisGroup as text),'') || '-' || IFNULL(cast(MIN(Priority) as text),'') ) \n" +
				"   from " + EVCTempSummaryVnLiteDBAdapter.DATABASE_TABLE + " es \n" +
                "   INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " oi ON oi.EVCRef='" + evcId + "' AND es.MainProductId = oi.GoodsRef AND oi.PrizeType = 0 \n" +
				" 	Group By ProductId , DisGroup)\n" ;

		db.execSQL(sql);
	}

    /*usp_FillEVCStatuteByID*/
    public void fillEVCTemp(String evcId)
    {
        /*EVCDetailStatutes*/
        //String sql = "INSERT INTO " + DATABASE_TABLE + " (EVCItemRef, DisRef, DisGroup, AddAmount, DisAmountEVCId ) \n" +
        String sql = "INSERT INTO " + DATABASE_TABLE + " (EVCItemRef, DisRef, DisGroup ,AddAmount ,DisAmount, EVCId ) \n" +
                " SELECT oi._id, DisID, DisGroup, 0, 0, '" + evcId + "' \n" +
                " FROM " + EVCTempSummaryVnLiteDBAdapter.DATABASE_TABLE + " es \n" +
                " INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +" oi ON oi.EVCRef = es.EvcId " +
                " AND oi.GoodsRef = es.MainProductId AND oi.PrizeType = 0 \n" +
                " WHERE oi.EVCRef = '" + evcId +"'" +
                " AND IFNULL(cast(oi._id as text),'') || '-' || IFNULL(cast(DisGroup as text),'') || '-' || IFNULL(cast(Priority as text) , '') \n" +
                " 	IN (SELECT ( ifnull(cast(oi._id as text),'') || '-' || IFNULL(cast(DisGroup as text),'') || '-' || IFNULL(cast(MIN(Priority) as text),'') ) \n" +
                "   FROM " + EVCTempSummaryVnLiteDBAdapter.DATABASE_TABLE + " es \n" +
                "   INNER JOIN " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " oi " +
                " ON oi.EVCRef= es.EvcId AND es.MainProductId = oi.GoodsRef AND oi.PrizeType = 0 \n" +
                " WHERE oi.EVCRef = '" + evcId +"'" +
                " 	Group By ProductId , DisGroup)\n" ;
        db.execSQL(sql);
    }

    public void updateDiscountBasedOnPriority1(String evcId, int priority, BigDecimal totalAmount)
    {
		//--group by-- and --sum-- added by mehdi because of updating discount with 0
        String selectData = "SELECT ES._id, \n" +
                " IFNULL(ES.DisAmount,0) + \n" +
                " (CASE \n" +
                "   WHEN D.CalcMethod = 1 THEN \n"+
                "        (CASE WHEN IFNULL(DisPerc, 0) <> 0 THEN \n" +
                "               CAST((IFNULL(D.DisPerc,0)*EI.Amount) / 100 as integer) \n" +
                "   WHEN  (IFNULL( D.DisPrice,0)<>0) THEN "+
                "           CAST((IFNULL(D.DisPrice,0)*EI.TotalQty) as integer) \n"+
                "   ELSE "+
                " Round((IFNULL(D.TotalDiscount,0)*((EI.Amount*1.0)/ "+ totalAmount +")))   "+
                " END "+
                ")"+


                " WHEN D.CalcMethod = 3 THEN \n" +
                "        (CASE WHEN IFNULL(DisPerc, 0) <> 0 THEN \n" +
                "               CAST((IFNULL(D.DisPerc,0)* (EI.Amount - IFNULL(EI.Discount, 0))) / 100 as integer) \n" +

                "   WHEN  (IFNULL( D.DisPrice,0)<>0) THEN "+
                "           CAST((IFNULL(D.DisPrice,0)*EI.TotalQty) as integer) \n"+
                "   ELSE "+
                " Round((IFNULL(D.TotalDiscount,0)*((EI.Amount*1.0)/ "+ totalAmount +")))   "+
                " END ) \n"+

                " ELSE \n" +
                "  (CASE WHEN IFNULL(DisPerc, 0) <> 0 THEN \n" +
                "   CAST((IFNULL(D.DisPerc,0)* (EI.Amount + IFNULL(EI.AddAmount, 0) - IFNULL(EI.Discount, 0))) / 100 as integer) \n" +
                "   WHEN  (IFNULL( D.DisPrice,0)<>0) THEN "+
                "       CAST((IFNULL(D.DisPrice,0)*EI.TotalQty) as integer) \n"+
                "   ELSE  " +
                " ROUND((IFNULL(D.TotalDiscount,0)*((EI.Amount*1.0)/ "+ totalAmount +")))   "+
                "   END ) \n"+
                " END ) as Discount \n" +
                " From " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI \n" +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EvcID  = EI.EVCRef \n" +
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES  ON ES.EvcItemRef=EI._id \n" + //in main sp its ES.EVCDetailID=EI.EVCDetailID
                " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D   ON D.PromotionDetailId  = ES.DisRef \n" +                       //i dont know what will happen !!!
                " Where EI.EVCRef = '" + evcId + "' AND E.DisType <> 4 AND D.IsPrize = '2' AND CalcPriority= " + priority + " \n" +
                "";//" group by ES._id";
        Cursor c = db.rawQuery(selectData, null);

        if(c != null && c.moveToFirst())
        {
            do{
                String sql ="UPDATE " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +
                        " SET DisAmount=" + c.getDouble(1) +
                        " WHERE _id=" + c.getInt(0);
                db.execSQL(sql);
            }while(c.moveToNext());
        }
    }

    public void updateDiscountBasedOnPriority2(String evcId, int priority)
    {
        String selectData = "select ES._id, " +
                " IFNULL(ES.Discount,0) + IFNULL(D.DisPrice*EI.TotalQty,0) as discount, " +
                " IFNULL(ES.SupAmount,0) + cast(IFNULL(D.DisPrice,0) * IFNULL(D.SupPerc,0) / 100 as integer) as SupAmount " +
                " From " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EvcID  = EI.EVCRef " +
              //inja  ??
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES  ON ES.EVCItemRef=EI.GoodsRef " +
                " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D   ON D.PromotionDetailId  = ES.DisRef " +
                " Where EI.EVCRef = '" + evcId + "' AND E.DisType <> 4 AND D.IsPrize = '3' AND CalcPriority= " + priority;
        ;
        Cursor c = db.rawQuery(selectData, null);
        if(c != null && c.moveToFirst())
        {
            do{
                String sql ="update " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " set DisAmount=" + c.getDouble(1) + ", SupAmount=" + c.getDouble(2) +
                        " where _id=" + c.getInt(0);
                db.execSQL(sql);
            }while(c.moveToNext());
        }
    }

    public void updateAddAmountBasedOnPriority(String evcId, int priority)
    {
        String selectData = "select ES._id, " +
                " IFNULL(ES.AddAmount,0) + (CASE " +
                " WHEN D.CalcMethod=1 THEN cast((IFNULL(D.AddPerc,0)*EI.Amount) / 100 as integer) " +
                " WHEN D.CalcMethod=3 THEN cast((IFNULL(D.AddPerc,0)*(EI.Amount-IFNULL(EI.Discount,0))) / 100 as integer) " +
                " ELSE cast((IFNULL(D.AddPerc,0)*(EI.Amount+IFNULL(EI.AddAmount,0)-IFNULL(EI.Discount,0))) / 100 as integer) " +
                " END) as AddAmount " +
                " From " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EvcID  = EI.EVCRef " +
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES  ON ES.EVCItemRef= EI._id " + // in main sp it is ES.ProductId = EI.ProductId
                " INNER JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " D   ON D.PromotionDetailId  = ES.DisRef " +                         // i dont know what will happen !!!
                " Where EI.EVCRef = '" + evcId + "' AND D.IsPrize = '4'  "; //"AND CalcPriority= " + priority;
        Cursor c = db.rawQuery(selectData, null);
        if(c != null && c.moveToFirst())
        {
            do{
                String sql ="update  "+ EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE +" set AddAmount=" + c.getDouble(1) +
                        " where _id=" + c.getInt(0);
                db.execSQL(sql);
            }while(c.moveToNext());
        }
    }

    public void updateDiscountBasedOnPriority3(String evcId, int priority)
    {
        String selectData = "select EVCItemRef, Sum(IFNULL(Discount,0)) as discount, " +
                " Sum(IFNULL(SupAmount,0)) as SupAmount, " +
                " Sum(IFNULL(AddAmount,0)) as AddAmount " +
                " from " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " where EvcId='" + evcId + "' " +
                " group by EVCItemRef ";

        Cursor c = db.rawQuery(selectData, null);
        if(c != null && c.moveToFirst())
        {
            do{
                String sql ="update " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " set Discount=" + c.getDouble(1) +
                        "  ,SupAmount=" + c.getDouble(2) +
                        "  ,AddAmount=" + c.getDouble(3) +
                        " where _id=" + c.getInt(0);
                db.execSQL(sql);
            }while(c.moveToNext());
        }
    }

    public void deleteInvalidItemSatatutes(String evcID) {

        String sql = "DELETE from " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE+
                " WHERE _ID IN (SELECT ES._ID  " +
                " FROM " + EVCItemVnLiteDBAdapter.DATABASE_TABLE + " EI " +
                " INNER JOIN " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE + " E ON E.EvcID = EI.EVCRef " +
                " INNER JOIN " + EVCItemStatutesVnLiteDBAdapter.DATABASE_TABLE + " ES ON ES.DisRef =EI.DisRef" +
                " INNER JOIN " + DiscountCustomerDBAdapter.DATABASE_TABLE + " C ON C.CustomerId=E.CustRef" +
                " WHERE EI.PrizeType=1 AND E.EvcID= '" + evcID + "'"+
                " 	and EI.TotalQty <= 0)";
        db.execSQL(sql);
    }

	public void clearAllData()
	{
		db.delete(DATABASE_TABLE, null, null);
	}

    public Cursor getEvcItemStatuteDataToSend(String evcId)
    {
        String sql = "select ProductId, EvcItemRef, DisRef, DisAmount AS Discount, AddAmount, 0 AS SupAmount, DisGroup, RowOrder from " + DATABASE_TABLE + " where EvcId ='" + evcId + "'";
        return db.rawQuery(sql, null);
    }

    public ArrayList<DiscountEvcItemStatuteData> getEvcItemStatuteDataToSendVnLite(String evcId)
    {
        ArrayList<DiscountEvcItemStatuteData> list = new ArrayList<>();
        String sql = "SELECT  " + EVCItemVnLiteDBAdapter.DATABASE_TABLE +".OrderLineId, EVCItemRef, " +DATABASE_TABLE + ".DisRef as DisRef, " +DATABASE_TABLE + ".DisAmount as Discount, " +
                DATABASE_TABLE + ".AddAmount as AddAmount, " +
                " 0 as SupAmount, ProductId as GoodsRef " +
                " FROM " + DATABASE_TABLE + " JOIN " +
                EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ON "+ DATABASE_TABLE +".EvcItemRef = "+ EVCItemVnLiteDBAdapter.DATABASE_TABLE +"._Id " +
                "   WHERE EvcId ='" + evcId + "'";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                DiscountEvcItemStatuteData item = new DiscountEvcItemStatuteData();
                item.EvcItemRef = cursor.getInt(cursor.getColumnIndex("GoodsRef"));
                item.OrderLineId = cursor.getString(cursor.getColumnIndex("OrderLineId"));
                item.DisRef = cursor.getInt(cursor.getColumnIndex("DisRef"));
                item.Discount = cursor.getLong(cursor.getColumnIndex("Discount"));
                item.AddAmount = cursor.getLong(cursor.getColumnIndex("AddAmount"));
                item.SupAmount = cursor.getLong(cursor.getColumnIndex("SupAmount"));

                list.add(item);

            } while (cursor.moveToNext());
        }
        return list;
    }


    public void deleteInvalidItemStatuse(String evcId) {

        String sql =
                " DELETE FROM " + DATABASE_TABLE + ""+
                " WHERE  _ID in (SELECT es._ID  "+
                " FROM "+ EVCItemVnLiteDBAdapter.DATABASE_TABLE + " ei "+
                " JOIN " +DATABASE_TABLE + " es on es.EVCItemRef=ei._ID " +
                " JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId=ei.GoodsRef " +
                " JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " ds on ds.Id=es.DisRef "+
                " WHERE (EVCId = '"+evcId+"')"+
                " AND EXISTS ( SELECT 1 "+
                "FROM " + EVCSkipDiscountDBAdapter.DATABASE_TABLE + " skd "+
                "JOIN " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " d2  " +
                "ON d2.ID=skd.DisRef " +
                "AND d2.DisGroup=ds.DisGroup "+
                "AND d2.Priority<ds.Priority "+
                "AND ei.GoodsRef=d2.GoodsRef "+
                ") )";


        db.execSQL(sql);
    }

    public void updateDiscountAmount(String evcId, BigDecimal totalDiscount , BigDecimal testDiscount){

        String query = " UPDATE " + DATABASE_TABLE +
            " SET DisAmount = IFNULL(DisAmount,0) + ( " + totalDiscount + " - " + testDiscount + ")"+
            " WHERE  _id = (SELECT _id " +
                        "   FROM EVCItemStatutesVnLite " +
                        "   WHERE evcItemRef = (SELECT _id " +
                                                " FROM EVCItemVnLite "+
                                                " WHERE  EVCRef = '"+ evcId +"' AND IFNULL(Discount,0)<>0 ORDER BY _id desc LIMIT 1)  " +
                " LIMIT 1 )";

        db.execSQL(query);
    }


    public void deleteExist(String evcId) {
        String sql =
                " DELETE FROM " + DATABASE_TABLE + " WHERE  EVCID = '" + evcId +"'";

       db.execSQL(sql);
    }

}
	 
