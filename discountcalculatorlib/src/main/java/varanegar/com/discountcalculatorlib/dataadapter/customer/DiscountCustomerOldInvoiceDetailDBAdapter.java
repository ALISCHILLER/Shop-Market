package varanegar.com.discountcalculatorlib.dataadapter.customer;

import android.database.Cursor;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;

public class DiscountCustomerOldInvoiceDetailDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "DiscountCustomerOldInvoiceDetail";
    private static String TAG = "DiscountCustomerOldInvoiceDetailDBAdapter";
    private static DiscountCustomerOldInvoiceDetailDBAdapter instance;

    public static DiscountCustomerOldInvoiceDetailDBAdapter getInstance() {

        if (instance == null) {
            instance = new DiscountCustomerOldInvoiceDetailDBAdapter();
        }

        return instance;

    }

    public int getMethodType(int saleId)
    {
        int result = 0;

        String query = " SELECT COUNT(*) FROM " + DATABASE_TABLE +
                "    WHERE SaleId = '" + saleId + "' " +
                "    AND (Amount <> (TotalQty * CustPrice))   ";

        Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst())
        {
            if(c.getInt(0) > 0)
                result = 1;
        }

        return result;
    }

    public int getPrizeCalcType(int saleId)
    {
        int result = 0;

        String query = " SELECT COUNT(*) FROM " + DATABASE_TABLE +
                "    WHERE SaleId = '" + saleId + "' " +
                "    AND PrizeType=1 and Discount<>0   ";

        Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst())
        {
            if(c.getInt(0) > 0)
                result = 1;
        }

        return result;
    }

    /*sle.usp_DecreaseEVCItemByQty_FindPrizeByStep*/
    public String getReferenceProductValues(int saleId, int productId, int prizeType)
    {
        String result = "";
        Cursor c = db.rawQuery("SELECT TotalQty, CustPrice, UserPrice FROM " + DATABASE_TABLE + " WHERE SaleId = " + saleId + " AND ProductId = " + productId + " AND PrizeType = " + prizeType, null);

        if(c != null && c.moveToFirst())
        {
            result = c.getDouble(0) + ":" + c.getDouble(1) + ":" + c.getDouble(2);
        }

        return result;
    }

    public void fillTempSaleItm(int saleId){
        db.execSQL("DROP TABLE IF EXISTS tempSaleItm");
        db.execSQL( "CREATE TABLE tempSaleItm (\n" +
                "RowOrder  INTEGER,\n" +
                "SaleId  INTEGER,\n" +
                "GoodsRef  INTEGER,\n" +
                "TotalQty  INTEGER,\n" +
                "Amount  INTEGER,\n" +
                "Dis1Perc  REAL,\n" +
                "Dis2Perc  REAL,\n" +
                "Dis3Perc  REAL,\n" +
                "OtherDiscountPerc  REAL,\n" +
                "Add1Perc  REAL,\n" +
                "Add2Perc  REAL,\n" +
                "OtherAdditionPerc  REAL,\n" +
                "PrizeType  INTEGER,\n" +
                "FreeReasonId  INTEGER,\n" +
                "CustPrice  INTEGER,\n" +
                "UserPrice  INTEGER,\n" +
                "CPriceRef  INTEGER,\n" +
                "PriceRef  INTEGER,\n" +
                "PreviousRetQty  INTEGER,\n" +
                "NewRetQty  INTEGER,\n" +
                "RetUnitRef  INTEGER,\n" +
                "Dis1  INTEGER,\n" +
                "Dis2  INTEGER,\n" +
                "Dis3  INTEGER,\n" +
                "OtherDiscount  INTEGER,\n" +
                "Add1  INTEGER,\n" +
                "Add2  INTEGER,\n" +
                "OtherAddition  INTEGER\n" +
                ");");
        db.execSQL("INSERT INTO tempSaleItm(RowOrder,SaleId,GoodsRef,TotalQty,Amount,Dis1Perc,Dis2Perc,Dis3Perc ,\n" +
                "OtherDiscountPerc,Add1Perc,Add2Perc,OtherAdditionPerc,PrizeType,FreeReasonId,\n" +
                "CustPrice,UserPrice, CPriceRef,PriceRef,\n" +
                "PreviousRetQty,NewRetQty,RetUnitRef,\n" +
                "Dis1,Dis2,Dis3,OtherDiscount  ,\n" +
                "Add1,Add2,OtherAddition  )\n" +
                "SELECT RowOrder, SaleId,\n" +
                "ProductId as GoodsRef, TotalQty, Amount\n" +
                ", case when Amount=0 then 0 else cast(Dis1 as real)/Amount end as Dis1Perc\n" +
                ", case when Amount=0 then 0 else cast(Dis2 as real)/Amount end as Dis2Perc\n" +
                ", case when Amount=0 then 0 else cast(Dis3 as real)/Amount end as Dis3Perc\n" +
                ", case when Amount=0 then 0 else cast(ifnull(OtherDiscount,0) as real)/Amount end as OtherDiscountPerc\n" +
                ", case when Amount=0 then 0 else cast(Add1 as real)/Amount end as Add1Perc\n" +
                ", case when Amount=0 then 0 else cast(Add2 as real)/Amount end as Add2Perc\n" +
                ", case when Amount=0 then 0 else cast(ifnull(OtherAddition,0) as real)/Amount end as OtherAdditionPerc\n" +
                ", PrizeType, ifnull(FreeReasonId, 0) as FreeReasonId \n" +
                ", CustPrice, UserPrice, CPriceRef, PriceId as PriceRef \n" +
                ", 0 as PreviousRetQty \n" +
                ", 0 NewRetQty, 0 as RetUnitRef \n" +
                ", 0 as Dis1, 0 as Dis2, 0 as Dis3, 0 as OtherDiscount, 0 as Add1, 0 as Add2, 0 as OtherAddition \n" +
                "from " + DATABASE_TABLE+" si\n" +
                "where SaleId="+ saleId+" and IsDeleted=0 ");
    }
    public void fillTempSaleDetail(){
        db.execSQL("DROP TABLE IF EXISTS tempSaleDetail");
        db.execSQL( "CREATE TABLE tempSaleDetail (\n" +
                "GoodsRef  INTEGER,\n" +
                "FreeReasonId  INTEGER,\n" +
                "SaleTotalQty  INTEGER,\n" +
                "SalePrizeQty  INTEGER,\n" +
                "SaleSumQty  INTEGER\n" +
                ");");
        db.execSQL("INSERT INTO tempSaleDetail (GoodsRef,FreeReasonId,SaleTotalQty,SalePrizeQty,SaleSumQty)\n" +
                "select * from (\n" +
                " select GoodsRef, ifnull(FreeReasonId, 0) as FreeReasonId\n" +
                "       , sum(TotalQty) as SaleTotalQty\n" +
                "       , sum(PrizeQty) as SalePrizeQty\n" +
                "       , sum(TotalQty+PrizeQty) as SaleSumQty\n" +
                " from (\n" +
                "      select GoodsRef, FreeReasonId, TotalQty as TotalQty, 0 as PrizeQty, Amount, CustPrice \n" +
                "      from tempSaleItm where PrizeType=0 and FreeReasonId=0\n" +
                "      union all\n" +
                "      select GoodsRef, FreeReasonId, 0 as TotalQty, TotalQty as PrizeQty, Amount, CustPrice \n" +
                "      from tempSaleItm where PrizeType=1 and FreeReasonId=0\n" +
                "      union all\n" +
                "      select GoodsRef, FreeReasonId, TotalQty as TotalQty, 0 as PrizeQty, Amount, CustPrice \n" +
                "      from tempSaleItm where FreeReasonId<>0\n" +
                "        ) A\n" +
                " group by GoodsRef, FreeReasonId\n" +
                ") B;");
    }


    public void updateTempSaleItm(){
        db.execSQL("UPDATE tempSaleItm \n" +
                "SET NewRetQty = IFNULL((SELECT CASE WHEN tempSaleItm.PrizeType=0 or tempSaleItm.FreeReasonId<>0 then sp.TotalQty \n" +
                "else sp.PrizeQty end - PreviousRetQty\n" +
                "from tempRetQtySplit sp where sp.GoodsRef=tempSaleItm.GoodsRef and sp.FreeReasonId=tempSaleItm.FreeReasonId),NewRetQty)");

        db.execSQL("UPDATE tempSaleItm\n" +
                "SET RetUnitRef = (\n" +
                "SELECT BackOfficeId as UnitRef\n" +
                "FROM DiscountProductUnit p\n" +
                "WHERE p.ProductId=tempSaleItm.GoodsRef\n" +
                "and p.ForRetSale=1\n" +
                "and tempSaleItm.NewRetQty%p.Quantity=0\n" +
                "ORDER BY CASE WHEN p.ForRetSale=1 then 0 else 1 end\n" +
                "LIMIT 1\n" +
                ")\n" +
                ", Amount=round(CustPrice*NewRetQty,0)\n" +
                "                , Dis1=round(CustPrice*Dis1Perc*NewRetQty,0)\n" +
                "                , Dis2=round(CustPrice*Dis2Perc*NewRetQty,0)\n" +
                "                , Dis3=round(CustPrice*Dis3Perc*NewRetQty,0)\n" +
                "                , OtherDiscount=round(CustPrice*OtherDiscountPerc*NewRetQty,0)\n" +
                "                , Add1=round(CustPrice*Add1Perc*NewRetQty,0)\n" +
                "                , Add2=round(CustPrice*Add2Perc*NewRetQty,0)\n" +
                "                , OtherAddition=round(CustPrice*OtherAdditionPerc*NewRetQty,0)\n" );
       

        db.execSQL("delete from tempSaleItm where NewRetQty=0");

        //Repare Negative Prize
        db.execSQL("delete from tempSaleItm where GoodsRef in (select GoodsRef from tempSaleItm group by GoodsRef having sum(NewRetQty)=0)");

        Cursor c = db.rawQuery("SELECT 1 FROM tempSaleItm i WHERE PrizeType=1 and NewRetQty<0 \n" +
                " AND EXISTS (SELECT 1 FROM tempSaleItm i2 where i2.GoodsRef=i.GoodsRef and i2.PrizeType=0 and i2.NewRetQty<i.NewRetQty)", null);
        if(c != null && c.moveToFirst())
        {
            boolean check = c.getInt(0) == 1;
            if (check) {
               throw new DiscountException("ÈÇ ÊæÌå Èå ãÍÇÓÈÇÊ ÇäÌÇã ÔÏå¡ ÊÚÏÇÏ ÎÇáÕ äåÇíí ãäÝí ÇÓÊ");
            }
        }
        c.close();
    }

    public void updateTempSaleItm2(){
        Cursor c = db.rawQuery("SELECT 1 FROM tempSaleItm i WHERE PrizeType=1 and NewRetQty<0 \n" +
                " AND EXISTS (SELECT 1 FROM tempSaleItm i2 where i2.GoodsRef=i.GoodsRef and i2.PrizeType=0 and i2.NewRetQty>i.NewRetQty)", null);
        if(c != null && c.moveToFirst()) {
            boolean check = c.getInt(0) == 1;
            if (check) {
                db.execSQL("update tempSaleItm\n" +
                        "set NewRetQty = tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)\n" +
                        ", Amount = round(tempSaleItm.CustPrice * (tempSaleItm.NewRetQty +(select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        ", Dis1 = round(tempSaleItm.Dis1/tempSaleItm.NewRetQty*(tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        ", Dis2 = round(tempSaleItm.Dis2/tempSaleItm.NewRetQty*(tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        ", Dis3 = round(tempSaleItm.Dis3/tempSaleItm.NewRetQty*(tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        ", OtherDiscount = round(tempSaleItm.OtherDiscount/tempSaleItm.NewRetQty*(tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        ", Add1 = round(tempSaleItm.Add1/tempSaleItm.NewRetQty*(tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        ", Add2 = round(tempSaleItm.Add2/tempSaleItm.NewRetQty*(tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        ", OtherAddition = round(tempSaleItm.OtherAddition/tempSaleItm.NewRetQty*(tempSaleItm.NewRetQty + (select i2.NewRetQty from   tempSaleItm i2 where i2.GoodsRef=tempSaleItm.GoodsRef and i2.PrizeType=1)), 0)\n" +
                        "where tempSaleItm.PrizeType=0\n" +
                        "and tempSaleItm.GoodsRef in (select  i2.GoodsRef from  tempSaleItm i2 where i2.PrizeType=1 )");

                db.execSQL("delete from tempSaleItm where PrizeType=1");

            }
        }

    }

    public void returnFinalResult(){
        /*-
        select r.RowOrder, r.GoodsRef, GoodsCode, GoodsName,
                NewRetQty/p.Qty as UnitQty,
        r.CPriceRef, @AccYear as AccYear, p.UnitRef, p.Qty as UnitCapasity, NewRetQty as TotalQty
		, Amount-(Dis1+Dis2+Dis3+OtherDiscount)+(Add1+Add2+OtherAddition) as AmountNut,
        Dis1+Dis2+Dis3+OtherDiscount as Discount,
        Amount, PrizeType, 0 as SupAmount
		, Add1+Add2+OtherAddition as AddAmount
		, UserPrice, CustPrice, PriceRef, u.UnitName as SamllPrizeUnitName
		, case when r.FreeReasonId=0 then null else r.FreeReasonId end as FreeReasonId, FreeReasonName
		, Dis1 as DisAmount1, Dis2 as DisAmount2, Dis3 as DisAmount3, Add1 as AddAmount1, Add2 as AddAmount2
		, sg.IsBatch, r.OtherDiscount, r.OtherAddition
        from #SaleItm r
        inner join gnr.tblGoods g on g.Id=r.GoodsRef
        inner join gnr.tblPackage p on p.GoodsRef=r.GoodsRef and p.UnitRef=r.RetUnitRef
        inner join gnr.tblUnit u on u.Id=p.UnitRef
        inner join gnr.tblStockGoods sg on sg.GoodsRef=r.GoodsRef AND sg.StockDCRef=@StockDCRef AND sg.AccYear=@AccYear
                left join sle.tblFreeReason fr on fr.ID=r.FreeReasonId
        order by PrizeType, RowOrder, FreeReasonId
*/
    }


    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }

}
