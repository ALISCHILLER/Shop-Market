package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;

import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountOrderPrizeDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductPackageDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductPackageItemDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountTourProductDBAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProduct;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.ApplicationType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.CallOrderLineGoodPackageItemData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.OrderMaster;

public class EVCTempGoodsPackageItemSDSDBAdapter extends DiscountBaseDataAdapter {

    public static final String KEY_GOOD_PACKAGE_ROWID = "id";
    public static final String KEY_GOOD_PACKAGE_EVCID = "EvcRef";
    public static final String KEY_GOOD_PACKAGE_DISCOUNT_ID = "DiscountId" ;
    public static final String KEY_GOOD_PACKAGE_GOODPACKREF = "GoodsRef";
    public static final String KEY_GOOD_PACKAGE_REPLACEGOODPACKREF = "ReplaceGoodsPackageItemRef";
    public static final String KEY_GOOD_PACKAGE_BASEGOODSREF = "BaseGoodsRef";
    public static final String KEY_GOOD_PACKAGE_UNITQTY = "UnitQty";
    public static final String KEY_GOOD_PACKAGE_TOTALQTY = "TotalQty";
    public static final String KEY_GOOD_PACKAGE_UNITREF = "UnitRef";
    public static final String KEY_GOOD_PACKAGE_GOODSPACKAGEREF = "GoodsPackageRef";
    public static final String KEY_GOOD_PACKAGE_PRIZECOUNT = "PrizeCount";

    public static final String DATABASE_TABLE = "EVCTempGoodsPackageItemSDS";
    private static String TAG = "EVCTempGoodsPackageItemSDSDBAdapter";
    private static EVCTempGoodsPackageItemSDSDBAdapter instance;

    public EVCTempGoodsPackageItemSDSDBAdapter()
    {
    }

    public static EVCTempGoodsPackageItemSDSDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempGoodsPackageItemSDSDBAdapter();
        }
        return instance;
    }

    public int getPrizePackageRef(int disId)
    {
        int prizePackageRef = 0;

        Cursor c = db.rawQuery("SELECT ID FROM DiscountGoodsPackage WHERE DiscountRef =  " + disId, null);
        if(c != null && c.moveToFirst())
        {
            prizePackageRef = c.getInt(0);
        }

        return prizePackageRef;
    }
    public DiscountPrizeViewModel fillEVCGoodPackageTemp(String evcId, int disId, int prizeRef, int saleRef, int prizeType, int evcType, int orderRef) throws  DiscountException{
        if (prizeRef != 0) {
            if (evcType == 1 || evcType == 4 ){
                fillEVCTempGoodPackage14(evcId, disId, prizeRef);
            }
            else{  //-- پيگيري حواله - برگشت فاکتور
                fillEVCTempGoodPackageReturn(evcId, evcType, disId, prizeRef, saleRef);
            }
            String errMsg= "";
            String q = "SELECT g.ProductCode \n" +
                    "FROM " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + " i \n" +
                    "INNER JOIN DiscountProduct g on g.ProductId=i.BaseGoodsRef \n" +
                    "WHERE i.GoodsRef is null";
            Cursor cq = db.rawQuery(q, null);
            if (q != null && cq.moveToFirst()) {
                errMsg += cq.getString(0);
            }
            if (!errMsg.isEmpty()) {
                errMsg = "در نتيجه پيگيري حواله/برگشت فاکتور و تغيير اولويت قانون، براي کالاهاي زير امکان ثبت جايزه وجود ندارد" + errMsg;
                throw new DiscountException(errMsg);
            }
        }
        else if (prizeType == 1){
            String sql = "INSERT INTO " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + "(DiscountId, GoodsPackageRef, GoodsRef, UnitQty, UnitRef, PrizeCount, EvcRef)\n" +
                    " SELECT " + disId +", null, d.PrizeRef, d.PrizeQty,  d.PrizeUnit as UnitRef, adis.PrizeCount , '"+ evcId+"'"+
                    " FROM " + DiscountDBAdapter.DATABASE_TABLE +" d\n" +
                    " INNER JOIN " +  EVCTempAcceptedDiscountAdapter.DATABASE_TABLE +" adis on adis.DiscountId=d.Id\n" +
                    " WHERE d.Id=" + disId;
            if (!sql.isEmpty())
                db.execSQL(sql);
        }
        else if (prizeType==6){
            return fillEVCTempGoodPackage6(evcId,disId,prizeRef,orderRef, prizeType,evcType);
        }
        return null;

    }
    /*
    public void fillEVCGoodPackageTemp(String evcId, int disId, int prizeRef, int saleRef, int prizeType, int evcType, int orderRef) throws  DiscountException{
        if (prizeRef != 0) {
            if (evcType == 1 || evcType == 4 ){
                fillEVCTempGoodPackage14(evcId, disId, prizeRef);
            }
            else{  //-- پيگيري حواله - برگشت فاکتور
                fillEVCTempGoodPackageReturn(evcId, evcType, disId, prizeRef, saleRef);
            }
            String errMsg= "";
            String q = "SELECT g.ProductCode \n" +
                    "FROM " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + " i \n" +
                    "INNER JOIN DiscountProduct g on g.ProductId=i.BaseGoodsRef \n" +
                    "WHERE i.GoodsRef is null";
            Cursor cq = db.rawQuery(q, null);
            if (q != null && cq.moveToFirst()) {
                errMsg += cq.getString(0);
            }
            if (!errMsg.isEmpty()) {
              errMsg = "در نتيجه پيگيري حواله/برگشت فاکتور و تغيير اولويت قانون، براي کالاهاي زير امکان ثبت جايزه وجود ندارد" + errMsg;
              throw new DiscountException(errMsg);
            }
        }
        else if (prizeType == 1){
            String sql = "INSERT INTO " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE + "(DiscountId, GoodsPackageRef, GoodsRef, UnitQty, UnitRef, PrizeCount, EvcRef)\n" +
                    " SELECT " + disId +", null, d.PrizeRef, d.PrizeQty,  d.PrizeUnit as UnitRef, adis.PrizeCount , '"+ evcId+"'"+
                    " FROM " + DiscountDBAdapter.DATABASE_TABLE +" d\n" +
                    " INNER JOIN " +  EVCTempAcceptedDiscountAdapter.DATABASE_TABLE +" adis on adis.DiscountId=d.Id\n" +
                    " WHERE d.Id=" + disId;
            if (!sql.isEmpty())
                db.execSQL(sql);
        }
        else if (prizeType==6){
            fillEVCTempGoodPackage6(evcId,disId,prizeRef,orderRef, prizeType,evcType);
        }

    }
*/
    private DiscountPrizeViewModel fillEVCTempGoodPackage6(String evcId, int disId, int prizeRef, int orderRef, int prizeType, int evcType) throws  DiscountException {
        boolean ignoreOnhandQtyControl = (GlobalVariables.getApplicationType() == ApplicationType.PRESALE);

        String sql = "";

        int evcPrize = 0;
        sql = " SELECT 1 FROM " + DiscountEvcPrizeDBAdapter.DATABASE_TABLE + " WHERE EvcRef = '"+ evcId+"' and QtyUnit=1";
        Cursor tempC = db.rawQuery(sql, null);
        if (tempC != null && tempC.moveToFirst()) {
            evcPrize = tempC.getInt(0);
        }
        int orderPrize = 0;
        sql = " SELECT 1 FROM " + DiscountOrderPrizeDBAdapter.DATABASE_TABLE + " WHERE OrderRef = " +orderRef;
        tempC = db.rawQuery(sql, null);
        if (tempC != null && tempC.moveToFirst()) {
            orderPrize = tempC.getInt(0);
        }
        if (orderPrize == 1 && evcPrize == 0) {
            int orderDiscountRef = DiscountItemDBAdapter.getInstance().CheckOrderDiscountRef(disId, orderRef);
            sql = "INSERT INTO "+ EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE + "(OrderRef, DiscountRef, GoodsRef, Qty, IsDeleted, SaleQty, IsAutomatic) "+
                    " SELECT OrderRef, "+ disId +" as DiscountRef, GoodsRef, Qty, 0, SaleQty, 0 "+
                    " FROM " + DiscountOrderPrizeDBAdapter.DATABASE_TABLE +
                    " WHERE OrderRef = " + orderRef + " AND DiscountRef = " + orderDiscountRef;
            db.execSQL(sql);
        }
        else if (evcType==1) {
            if (ignoreOnhandQtyControl) {
                sql =
                        "INSERT INTO " + EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE + "(OrderRef, DiscountRef, GoodsRef, Qty, IsDeleted, SaleQty, IsAutomatic) " +
                                " SELECT  "+ orderRef +", d.Id, pl.GoodsRef, adis.PrizeCount * d.PrizeQty, 0 as IsDeleted, 0 as SaleQty, 1 "+
                                " FROM " + EVCTempAcceptedDiscountAdapter.DATABASE_TABLE + " adis "+
                                " JOIN (SELECT * FROM " + DiscountItemDBAdapter.DATABASE_TABLE + " pl where pl.DisRef = " + disId + " )as pl "+
                                " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id = " + disId +
                                " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE +" g on g.ProductId = pl.GoodsRef "+
                                " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk on pk.ProductId = pl.GoodsRef and pk.BackOfficeId = "+
                                " CASE WHEN d.PrizeUnit = 0 then g.LargeUnitId else g.SmallUnitId end " +
                                " LEFT JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " ei on ei.EvcRef = '" + evcId + "' and ei.GoodsRef = pl.GoodsRef and ei.PrizeType = 0 "+
                                " WHERE adis.DiscountId = "+ disId+
                                " LIMIT  1";
                db.execSQL(sql);
            } else {

                sql =" INSERT INTO " + EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE + "(OrderRef, DiscountRef, GoodsRef, Qty, IsDeleted, SaleQty, IsAutomatic) " +
                        " SELECT " + orderRef + ", pl.DisRef, pl.GoodsRef, adis.PrizeCount * d.PrizeQty, " +
                        " 0 as IsDeleted, 0 as SaleQty, 1 "+
                        " FROM " + DiscountItemDBAdapter.DATABASE_TABLE + " pl "+
                        " LEFT JOIN " + DiscountTourProductDBAdapter.DATABASE_TABLE + " sg on sg.ProductId = pl.GoodsRef " +
                        " INNER JOIN " + EVCTempAcceptedDiscountAdapter.DATABASE_TABLE + " adis on adis.DiscountId= " + disId +
                        " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d on d.Id= " + disId +
                        " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId=pl.GoodsRef " +
                        " INNER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk on pk.ProductId = pl.GoodsRef and pk.BackOfficeId = "+
                        " CASE WHEN d.PrizeUnit = 0 then g.LargeUnitId else g.SmallUnitId end "+
                        " LEFT JOIN " + EVCItemSDSDBAdapter.DATABASE_TABLE + " ei ON ei.EvcRef = '" + evcId + "' and ei.GoodsRef = pl.GoodsRef and ei.PrizeType = 0 " +
                        " WHERE pl.DisRef= " + disId +
                        " AND((IFNULL(OnHandQty,0) + IFNULL(RenewQty,0) - IFNULL(SoldQty, 0)) >= (pk.Quantity * adis.PrizeCount)) " +
                        " LIMIT 1";
                db.execSQL(sql);
            }
            int temporderPrize = 0;
            sql = " SELECT 1 FROM " + EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE +
                    " WHERE DiscountRef = " + disId + " AND OrderRef = " + orderRef + " AND Qty > 0";
            tempC = db.rawQuery(sql, null);
            temporderPrize = 0;
            if (tempC != null && tempC.moveToFirst()) {
                temporderPrize = tempC.getInt(0);
            }

            if (temporderPrize == 0)
            {
                throw new DiscountException(3001, "به دليل کسري موجودي، امکان تخصيص اتوماتيک جايزه انتخابي وجود ندارد");
            }

        }
        else if (evcType == 3) {
            sql = "INSERT INTO " + EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE + "(OrderRef, DiscountRef, GoodsRef, Qty, IsDeleted, SaleQty, IsAutomatic) " +
                    " select " + orderRef+", " + disId +", GoodsRef, PrizeQty, 0, PrizeQty, 0 " +
                    " FROM " + DiscountEvcPrizeDBAdapter.DATABASE_TABLE +
                    " where EvcRef= '" + evcId + "' and DiscountRef= " + disId;
            db.execSQL(sql);

        }
        else if (evcType == 3 && ignoreOnhandQtyControl) {
            sql = " INSERT INTO EVCTempOrderPrizeSDS(Id, OrderRef, DiscountRef, GoodsRef, Qty, IsDeleted, SaleQty, IsAutomatic)\n" +
                    " SELECT 1, " + orderRef + ",pl.DisRef, pl.GoodsRef, adis.PrizeCount * d.PrizeQty, 0 as IsDeleted, 0 as SaleQty, 1\n" +
                    " FROM " + DiscountItemDBAdapter.DATABASE_TABLE +" pl\n" +
                    " LEFT JOIN " + DiscountTourProductDBAdapter.DATABASE_TABLE +" sg on sg.ProductId = pl.GoodsRef --and sg.StockDCRef = @StockDCRef\n" +
                    " INNER JOIN EVCTempAcceptedDiscountSDS adis on adis.DiscountId = @DiscountId\n" +
                    " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE +" d on d.Id = " + disId +
                    " INNER JOIN DiscountProduct g on g.ProductId = pl.GoodsRef\n" +
                    " INNER JOIN DiscountProductUnit pk on pk.ProductId = pl.GoodsRef and pk.BackofficeId =\n" +
                    " CASE WHEN d.PrizeUnit = 0 then g.LargeUnitId else g.SmallUnitId end\n" +
                    " LEFT JOIN "+ EVCItemSDSDBAdapter.DATABASE_TABLE +" ei on ei.EvcRef = '"+ evcId + "' and ei.GoodsRef = pl.GoodsRef and ei.PrizeType = 0\n" +
                    " WHERE pl.DisRef = "+ disId +
                    " AND((IFNULL(OnHandQty, 0) - IFNULL(ei.TotalQty, 0)) >= (pk.Quantity * adis.PrizeCount))"; //or @IgnoreOnhandQtyControl=1

            db.execSQL(sql, null);


            int existsOrderPrize = 0;
            sql = " SELECT 1 FROM " + EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE + " WHERE DiscountRef = " + disId + " and OrderRef = " + orderRef + " and Qty > 0 ";
            tempC = db.rawQuery(sql, null);
            if (tempC != null && tempC.moveToFirst()) {
                existsOrderPrize = tempC.getInt(0);
            }

            if (existsOrderPrize == 0)
            {
                DiscountPrizeViewModel dpv = new DiscountPrizeViewModel();
                dpv.discountRef = disId;
                dpv.qty = 0;
                dpv.evcId = evcId;
                dpv.orderPrizeList = DiscountOrderPrizeDBAdapter.getInstance().GetOrderPrizes(disId, orderRef);
                throw new DiscountException(3001,
                        "به دلیل کسری موجودی، امکان تخصیص اتوماتیک جایزه انتخابی وجود ندارد",
                        dpv
                );

            }
        }


        int sumOrderPrize =  0;
        sql =   " SELECT sum(Qty) " +
                " FROM " + EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE + " OP "+
                " WHERE DiscountRef= " + disId + " and OrderRef= " + orderRef + " and isdeleted=0 and ifnull(qty,0)>0 ";
        tempC = db.rawQuery(sql, null);
        if (tempC != null && tempC.moveToFirst()) {
            sumOrderPrize = tempC.getInt(0);
        }

        int expectedPrize =0;
        sql = " SELECT ifnull(sum(adis.PrizeCount*d.PrizeQty), 0) "+
                " FROM " + DiscountDBAdapter.DATABASE_TABLE + " d "+
                " INNER JOIN " + EVCTempAcceptedDiscountAdapter.DATABASE_TABLE +" adis on adis.DiscountId=d.Id "+
                " WHERE d.Id= " + disId;

        tempC = db.rawQuery(sql, null);
        if (tempC != null && tempC.moveToFirst()) {
            expectedPrize = tempC.getInt(0);
        }
        if (expectedPrize != sumOrderPrize && !ignoreOnhandQtyControl && evcType != 3 && evcType != 1 ) {
            DiscountPrizeViewModel dpv = new DiscountPrizeViewModel();
            dpv.discountRef = disId;
            dpv.qty = expectedPrize;
            dpv.evcId = evcId;
            dpv.orderPrizeList = DiscountOrderPrizeDBAdapter.getInstance().GetOrderPrizes(disId, orderRef);
            return dpv;

        }
        evcPrize = 0;
        sql = " SELECT 1 FROM " + DiscountEvcPrizeDBAdapter.DATABASE_TABLE + " WHERE EvcRef = '"+ evcId+"' ";
        tempC = db.rawQuery(sql, null);
        if (tempC != null && tempC.moveToFirst()) {
            evcPrize += tempC.getInt(0);
        }

        sql = "INSERT INTO " + DATABASE_TABLE +" (DiscountId, GoodsPackageRef, GoodsRef, UnitQty, UnitRef, PrizeCount, EVCRef) "+
                " SELECT "+disId+", null, op.GoodsRef ";

        if (evcPrize == 0 && evcType==3 && expectedPrize != sumOrderPrize)
            sql += " ,adis.PrizeCount * d.PrizeQty as UnitQty";

        else sql += " ,op.Qty as UnitQty ";

        sql += ",case when d.PrizeUnit = 0 then g.LargeUnitId else g.SmallUnitId end as UnitRef ";

        if ( expectedPrize != sumOrderPrize)
            sql += ", adis.PrizeCount*d.PrizeQty as PrizeCount ";
        else
            sql += ",Case when op.IsAutomatic = 1 then op.Qty else adis.PrizeCount*d.PrizeQty end as PrizeCount ";

        sql +=   ",'"+evcId+"'"+
                "   FROM " + DiscountDBAdapter.DATABASE_TABLE +" d"+
                " inner join " + EVCTempAcceptedDiscountAdapter.DATABASE_TABLE  +" adis on adis.DiscountId = d.Id "+
                " inner join " + EVCTempOrderPrizeSDSDBAdapter.DATABASE_TABLE + " op on op.DiscountRef = d.Id "+
                " inner join " + DiscountProductDBAdapter.DATABASE_TABLE + " g on g.ProductId = op.GoodsRef "+
                " WHERE DiscountRef = " + disId + " AND OrderRef = " + orderRef + " and op.Qty > 0 and op.isdeleted = 0 ";
        db.execSQL(sql);
        // }

        if (prizeType==6){
            db.execSQL("UPDATE " + EVCTempAcceptedDiscountAdapter.DATABASE_TABLE +" SET PrizeCount=1 WHERE DiscountId=" + disId);
        }
        return null;
    }


    private void fillEVCTempGoodPackage14(String evcId, int disId, int prizeRef) {
        String sql;//TODO ASAL?
        // do we have @IgnoreOnhandQtyControl?
        // do we need #TmpGoodsPackage?
                /* *
                IF @IgnoreOnhandQtyControl=0 -> if (GlobalVariables.getApplicationType() == ApplicationType.PRESALE) {
                select GoodsRef,GoodsCode
                into #TmpGoodsPackage
                from #AcceptedDiscount A
                inner join DiscountGoodsPackage P ON P.DiscountRef=A.DiscountId
                inner join DiscountGoodsPackageItem PaI ON PaI.GoodsPackageRef=P.Id
                inner join DiscountProduct G ON G.Id=PaI.GoodsRef
                WHERE Pai.ReplaceGoodsRef IS NULL
                }
                * */

        sql = "INSERT INTO " + DATABASE_TABLE +" (DiscountId, GoodsPackageRef, BaseGoodsRef, GoodsRef, UnitQty, UnitRef, ReplaceGoodsPackageItemRef, PrizeCount, EVCRef) "+
                " SELECT "+disId+", GPI.GoodsPackageRef, BaseGoodsRef, GPI.GoodsRef, GPI.UnitQty, GPI.UnitRef, GPI.Id, B.PrizeCount, '"+evcId+"' \n"+
                " FROM ( \n"+
                " SELECT GoodsPackageRef, BaseGoodsRef, min(PrizePriority) PrizePriority, min(PrizeCount) as PrizeCount \n" +
                " from ( "+
                " select GPI.GoodsPackageRef, IFNULL(GPI.ReplaceGoodsRef, GPI.GoodsRef) as BaseGoodsRef, IFNULL( GPI.PrizePriority, -1) PrizePriority "+
                ", adis.PrizeCount as PrizeCount \n"+
                " from "+ DiscountProductPackageItemDBAdapter.DATABASE_TABLE +" GPI \n";

        if (GlobalVariables.getApplicationType() == ApplicationType.HOTSALE || GlobalVariables.getApplicationType() == ApplicationType.DISTRIBUTION) {
            sql += "inner join " + DiscountTourProductDBAdapter.DATABASE_TABLE +" SG on SG.ProductId=GPI.GoodsRef \n";
        }

        sql += "inner join  DiscountProductUnit P on P.ProductId=GPI.GoodsRef and P.BackOfficeId=GPI.UnitRef \n"+
                "inner join " +  EVCTempAcceptedDiscountAdapter.DATABASE_TABLE +"  adis on adis.DiscountId=" + disId + " \n"+
                "left join EVCItemSDS ei on ei.EvcRef='"+evcId+"' and ei.GoodsRef=GPI.GoodsRef /*ifnull(ReplaceGoodsRef, GPI.GoodsRef)*/ and PrizeType=0 \n"+
        //SG.StockDCRef=@StockDCRef and SG.AccYear=@AccYear and
                " where GPI.GoodsPackageRef=" +prizeRef+ "\n" ;
        if (GlobalVariables.getApplicationType() == ApplicationType.HOTSALE || GlobalVariables.getApplicationType() == ApplicationType.DISTRIBUTION) {
            sql += "and (((IFNULL(OnHandQty,0)-IFNULL(SoldQty,0)+IFNULL(RenewQty,0))-IFNULL(ei.TotalQty,0))>=(P.Quantity*GPI.UnitQty*adis.PrizeCount) )";//or @IgnoreOnhandQtyControl=1
        }

        sql  += ") A"+
                " group by GoodsPackageRef, BaseGoodsRef"+
                ") B"+
                " inner join " + DiscountProductPackageItemDBAdapter.DATABASE_TABLE + " GPI on IFNULL(GPI.ReplaceGoodsRef, GPI.GoodsRef)=BaseGoodsRef " +
                "and IFNULL(GPI.PrizePriority, -1)=B.PrizePriority and GPI.GoodsPackageRef=B.GoodsPackageRef ";
        sql = String.format(sql,disId);

    
        boolean ignoreOnhandQtyControl = (GlobalVariables.getApplicationType() == ApplicationType.PRESALE);
        if (!sql.isEmpty())
            db.execSQL(sql);

        if (!ignoreOnhandQtyControl) {
            String sqlOnhand = "SELECT ProductCode, ifnull(ProductName, 'نا موجود' ||  DiscountGoodsPackageItem.GoodsRef ) ProductName, DiscountSDS4_19.Code, DiscountGoodsPackage.Id  \n" +
                    "FROM DiscountGoodsPackageItem \n" +
                    "JOIN DiscountGoodsPackage on DiscountGoodsPackage.Id = "+prizeRef+"\n" +
                    "JOIN DiscountSDS4_19 on DiscountGoodsPackage.DiscountRef = DiscountSDS4_19.ID\n" +
                    "JOIN EVCTempAcceptedDiscountSDS on EVCTempAcceptedDiscountSDS.DiscountId = DiscountSDS4_19.ID\n"+
                    "LEFT JOIN DiscountProduct on DiscountProduct.ProductId = DiscountGoodsPackageItem.GoodsRef\n"+
                    "WHERE GoodsPackageRef = "+prizeRef+" AND ReplaceGoodsRef is null\n" +
                    "AND GoodsPackageRef not in(select ifnull(GoodsPackageRef,0) from EVCTempGoodsPackageItemSDS)";
            /*
                    "SELECT ProductCode, ProductName, DiscountSDS4_19.Code\n" +
                    "FROM DiscountProduct join DiscountGoodsPackageItem on DiscountProduct.ProductId = DiscountGoodsPackageItem.GoodsRef\n" +
                    "JOIN DiscountGoodsPackage on DiscountGoodsPackage.Id = "+prizeRef+"\n" +
                    "join DiscountSDS4_19 on DiscountGoodsPackage.DiscountRef = DiscountSDS4_19.ID\n" +
                    "WHERE \n" +
                    "GoodsPackageRef = "+ prizeRef +" AND ReplaceGoodsRef is null AND\n" +
                    "(SELECT COUNT(1)\n" +
                    "FROM EVCTempGoodsPackageItemSDS) <>\n" +
                    "(SELECT COUNT(1) \n" +
                    "FROM DiscountGoodsPackageItem\n" +
                    "WHERE GoodsPackageRef = "+ prizeRef+")\n";
*/
            Cursor tempC = db.rawQuery(sqlOnhand, null);
            if (tempC != null && tempC.moveToFirst()) {
                String goodname = tempC.getString(tempC.getColumnIndex("ProductName"));
                String code = tempC.getString(tempC.getColumnIndex("Code"));

                throw new DiscountException("در نتيجه محاسبه قانون جايزه به کد " + code
                + "کالاهي جايزه " + goodname + " "
                +"کسري موجودي دارد");

            }
        }
    }


    private void fillEVCTempGoodPackageReturn(String evcId, int evcType, int disId, int prizeRef, int saleId){
        int saleDiscountRef = CompareGoodsPackage(evcType, prizeRef, disId, saleId);
        String sql =
                " INSERT INTO " + DATABASE_TABLE +"(DiscountId, GoodsPackageRef, BaseGoodsRef, GoodsRef, UnitQty, UnitRef, ReplaceGoodsPackageItemRef, PrizeCount, EvcRef)" +
                " SELECT " + disId +", NewGPI.GoodsPackageRef, IFNULL(NewGPI.ReplaceGoodsRef, NewGPI.GoodsRef) as BaseGoodsRef, NewGPI.GoodsRef "+
                        " , NewGPI.UnitQty, NewGPI.UnitRef, NewGPI.Id, adis.PrizeCount,'" + evcId+ "'\n" +
                        " FROM " + DisSalePrizePackageDBAdapter.DATABASE_TABLE + " DSPP \n"+
                        " INNER JOIN EVCTempAcceptedDiscountSDS adis on adis.DiscountId=" + disId +
                        "  AND DSPP.SaleRef= " + saleId +
                        "  AND DSPP.DiscountRef=" + saleDiscountRef+ "\n"+
                        " INNER JOIN " + DiscountProductPackageItemDBAdapter.DATABASE_TABLE + " SaleGPI on SaleGPI.Id=DSPP.ReplaceGoodsPackageItemRef \n" +
                        " LEFT JOIN " + DiscountProductPackageItemDBAdapter.DATABASE_TABLE + " NewGPI on NewGPI.GoodsPackageRef= " + prizeRef +
                        " AND ifnull(NewGPI.ReplaceGoodsRef, 0) = IFNULL(SaleGPI.ReplaceGoodsRef, 0) "+
                        " AND NewGPI.GoodsRef = SaleGPI.GoodsRef ";
        if (!sql.isEmpty())
            db.execSQL(sql);
    }

    /*sle.usp_CompareGoodsPackage*/
    private int CompareGoodsPackage(int evcType ,int newGoodsPackageRef, int newDiscountRef, int saleRef) throws DiscountException{
        String errMsg = "";
        int saleGoodsPackageRef = 0;
        int discountCode = 0;
        int discountGroup = 0;
        int discountPriority = 0;
        int saleDiscountRef = 0;

        String sql;
        sql = "SELECT Code, DisGroup, Priority FROM " + DiscountDBAdapter.DATABASE_TABLE  + " WHERE Id= "+ newDiscountRef;
        Cursor d = db.rawQuery(sql, null);
        if(d != null && d.moveToFirst()) {
            discountCode = d.getInt(0);
            discountGroup = d.getInt(1);
            discountPriority = d.getInt(2);
        }

        sql = " SELECT d1.Id, gp.Id "+
        " FROM " + DisSalePrizePackageDBAdapter.DATABASE_TABLE +" dsp "+
        " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " d1 on d1.Id=dsp.DiscountRef and d1.PrizeType=5 "+
        " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + "  d2 on d2.Id=" + newDiscountRef + " and d2.PrizeType=5 "+
        " INNER JOIN " + DiscountProductPackageDBAdapter.DATABASE_TABLE + " gp on gp.DiscountRef=d1.Id "+
        " WHERE (d1.DisGroup= d2.DisGroup or " + evcType +"=3) and dsp.SaleRef= "+ saleRef +
        " LIMIT 1 ";
        Cursor c = db.rawQuery(sql, null);
        if(c != null && c.moveToFirst())
        {
            saleDiscountRef = c.getInt(0);
            saleGoodsPackageRef = c.getInt(1);
        }


        sql = " SELECT 1 FROM " + DisSaleSDSDBAdapter.DATABASE_TABLE + " ds " +
                " WHERE ( ds.HdrRef =  " + saleRef + " ) AND (ds.DisRef = " + newDiscountRef + ")";
        c = db.rawQuery(sql, null);
        int exists = 0;
        if (c != null && c.moveToFirst()) {
            exists = c.getInt(0);
        }


        if ((saleDiscountRef == newDiscountRef) || (exists == 1)) {
            saleDiscountRef = newDiscountRef;
            return saleDiscountRef;
        }
        if (saleGoodsPackageRef == 0)
            throw new DiscountException("نوع قانون جايزه در فاکتور و پيگيري حواله/برگشت فاکتور همخواني ندارد");

        int isOk = 0;

        Cursor Step1Cursor;
        sql = " SELECT gp.Id as SaleGoodsPackageRef "+
              " FROM " + DisSalePrizePackageDBAdapter.DATABASE_TABLE + " dsp "+
              " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + "  d1 on d1.Id=dsp.DiscountRef and d1.PrizeType=5 "+
              " INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + "  d2 on d2.Id= " + newDiscountRef + " and d2.PrizeType=5 "+
              " INNER JOIN " + DiscountProductPackageDBAdapter.DATABASE_TABLE + " gp on gp.DiscountRef=d1.Id "+
              " WHERE d1.DisGroup=d2.DisGroup and dsp.SaleRef= " + saleRef;
        Step1Cursor = db.rawQuery(sql,null);
        if (Step1Cursor != null && Step1Cursor.moveToFirst()) {
            do {
                saleGoodsPackageRef = Step1Cursor.getInt(0);
                
                errMsg = "";
                sql = " SELECT ProductCode + ' , ' as code "+
                " FROM DiscountGoodsPackageItem i2 "+
                " INNER JOIN DiscountProduct g on g.ProductId=i2.GoodsRef "+
                " WHERE GoodsPackageRef= " + newGoodsPackageRef +
                       " AND ReplaceGoodsRef is null " +
                " AND NOT EXISTS (SELECT 1 FROM DiscountGoodsPackageItem i1 "+
                       " WHERE GoodsPackageRef= " + saleGoodsPackageRef +
                       " AND ReplaceGoodsRef is null  AND i1.GoodsRef=i2.GoodsRef) ";
                c = db.rawQuery(sql, null);
                if (c != null && c.moveToFirst()) {
                    errMsg += c.getString(0);
                }

                if (errMsg.isEmpty()) isOk = 1;
            } while (Step1Cursor.moveToNext() && isOk != 1);
        }

        if (!errMsg.isEmpty()){
             errMsg = "با تغيير قانون به کد " + discountCode + " در گروه تخفيف شماره " + discountGroup + " با اولويت " + discountPriority + " "
                     + "کالاهاي اصلي زير در سبد جايزه قانون مرتبط با فاکتور وجود ندارد" + errMsg;
             throw new DiscountException(errMsg);
        }
        else{
             sql = "SELECT DiscountRef from " + DiscountProductPackageDBAdapter.DATABASE_TABLE + " WHERE Id= " + saleGoodsPackageRef;
             c = db.rawQuery(sql, null);
             if (c != null && c.moveToFirst()) {
                 saleDiscountRef = c.getInt(0);
             }
        }

        sql = "SELECT ProductCode + ' - ' as err\n" +
            " FROM " + DisSalePrizePackageDBAdapter.DATABASE_TABLE + " pp\n" +
            " INNER JOIN " + DiscountProductPackageItemDBAdapter.DATABASE_TABLE +" i1 on i1.id=pp.ReplaceGoodsPackageItemRef\n" +
            " INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE +" g on g.ProductId=i1.GoodsRef\n" +
            " WHERE SaleRef= " + saleRef +
            "  and DiscountRef= " + saleDiscountRef +
            "  and exists (\n" +
            "   select 1\n" +
            "   from " + DiscountProductPackageItemDBAdapter.DATABASE_TABLE +" ip2\n" +
            "   where ip2.ReplaceGoodsRef= " + newGoodsPackageRef +
            "    and ip2.ReplaceGoodsRef is null\n" +
            "    and ip2.GoodsRef=IFNULL(i1.ReplaceGoodsRef, i1.GoodsRef))\n" +
            "  AND NOT EXISTS (\n" +
            "   select 1\n" +
            "   from " + DiscountProductPackageItemDBAdapter.DATABASE_TABLE +" i2\n" +
            "   where i2.GoodsPackageRef=" + newGoodsPackageRef +
            "    and i2.GoodsRef=i1.GoodsRef)\n";
        c = db.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            errMsg += c.getString(0);
        }

        if (!errMsg.isEmpty()){
             errMsg = "با تغيير قانون به کد " + discountCode + " در گروه تخفيف شماره " + discountGroup + " با اولويت " + discountPriority + " " +
                      "کالاهاي جايگزين زير در سبد جايزه قانون مرتبط با فاکتور وجود ندارد" + errMsg;
            throw new DiscountException(errMsg);
        }
        return saleDiscountRef;
    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void deleteEVCTempsByEvcId(String id) {
        db.delete(DATABASE_TABLE, "evcId = " + id , null);
    }

    public DiscountCallOrderData fillEVCPrizePackageForOnline(OrderMaster orderMaster, DiscountCallOrderData callData) {
        callData.callOrderLineGoodPackageItemDatas = new ArrayList<>();

        ArrayList<CallOrderLineGoodPackageItemData> packageDataList = new ArrayList<>();

//        Cursor cEVCItemPackageInfo = getEvcPrizePackageData(String.valueOf(orderMaster.EvcType));

        if (orderMaster != null) {
            for (int i = 0; i < orderMaster.OrderLineList.size(); i++)
            {
                if (orderMaster.OrderLineList.get(i).PrizeType == 1){
                    CallOrderLineGoodPackageItemData packageData = new CallOrderLineGoodPackageItemData();

                    packageData.setEvcId(String.valueOf(orderMaster.EvcType));
                    packageData.setOrderUniqueId(callData.orderUniqueId);
                    packageData.setDiscountRef(orderMaster.OrderLineList.get(i).DisRef);
                    packageData.setMainGoodsPackageItemRef(orderMaster.OrderLineList.get(i).UnitRef);
                    packageData.setReplaceGoodsPackageItemRef(1);
                    packageData.setPrizeQty(orderMaster.OrderLineList.get(i).TotalQty);
                    packageData.setPrizeCount(orderMaster.OrderLineList.get(i).TotalQty);

                    packageDataList.add(packageData);
                }
            }
        }
        callData.callOrderLineGoodPackageItemDatas = packageDataList;
        /*??? DELET
        deletePrizePackageDataByEvcId(evcId);
        String sql =
                " INSERT INTO "+ CustomerCallOrderLinesGoodPackageItemDBAdapter.DATABASE_TABLE +
                        " (DiscountRef, MainGoodsPackageItemRef, ReplaceGoodsPackageItemRef, " +
                        " PrizeQty, PrizeCount, evcRef)\n" +
                        " SELECT DiscountId DiscountRef,  GoodsRef MainGoodsPackageItemRef, ReplaceGoodsPackageItemRef, "+
                        "   TotalQty PrizeQty,  PrizeCount, '" + evcId +"' "+
                        " FROM  " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE ;
        db.execSQL(sql);
        */
        return callData;
    }

    public DiscountCallOrderData fillEVCPrizePackage(String evcId, DiscountCallOrderData callData) {
        callData.callOrderLineGoodPackageItemDatas = new ArrayList<>();

        ArrayList<CallOrderLineGoodPackageItemData> packageDataList = new ArrayList<>();

        Cursor cEVCItemPackageInfo = getEvcPrizePackageData(evcId);

        if (cEVCItemPackageInfo != null && cEVCItemPackageInfo .moveToFirst()) {
            do {
                CallOrderLineGoodPackageItemData packageData = new CallOrderLineGoodPackageItemData();

                packageData.setEvcId(evcId);
                packageData.setOrderUniqueId(callData.orderUniqueId);
                packageData.setDiscountRef(cEVCItemPackageInfo.getInt(cEVCItemPackageInfo.getColumnIndex(KEY_GOOD_PACKAGE_DISCOUNT_ID)));
                packageData.setMainGoodsPackageItemRef(cEVCItemPackageInfo.getInt(cEVCItemPackageInfo.getColumnIndex(KEY_GOOD_PACKAGE_GOODPACKREF)));
                packageData.setReplaceGoodsPackageItemRef(cEVCItemPackageInfo.getInt(cEVCItemPackageInfo.getColumnIndex(KEY_GOOD_PACKAGE_REPLACEGOODPACKREF)));
                packageData.setPrizeQty(cEVCItemPackageInfo.getInt(cEVCItemPackageInfo.getColumnIndex(KEY_GOOD_PACKAGE_TOTALQTY)));
                packageData.setPrizeCount(cEVCItemPackageInfo.getInt(cEVCItemPackageInfo.getColumnIndex(KEY_GOOD_PACKAGE_PRIZECOUNT)));

                packageDataList.add(packageData);
            } while (cEVCItemPackageInfo.moveToNext());
        }
        callData.callOrderLineGoodPackageItemDatas = packageDataList;
        /*??? DELET
        deletePrizePackageDataByEvcId(evcId);
        String sql =
                " INSERT INTO "+ CustomerCallOrderLinesGoodPackageItemDBAdapter.DATABASE_TABLE +
                        " (DiscountRef, MainGoodsPackageItemRef, ReplaceGoodsPackageItemRef, " +
                        " PrizeQty, PrizeCount, evcRef)\n" +
                        " SELECT DiscountId DiscountRef,  GoodsRef MainGoodsPackageItemRef, ReplaceGoodsPackageItemRef, "+
                        "   TotalQty PrizeQty,  PrizeCount, '" + evcId +"' "+
                        " FROM  " + EVCTempGoodsPackageItemSDSDBAdapter.DATABASE_TABLE ;
        db.execSQL(sql);
        */
        return callData;
    }


    public void deletePrizePackageDataByEvcId(String id)
    {
        db.delete(DATABASE_TABLE, "EvcRef = '" + id +"'" , null);
    }


    public Cursor getEvcPrizePackageData(String evcId)
    {
            Cursor c = null;
            if (db != null)
                c = db.query(DATABASE_TABLE,
                        new String[]{KEY_GOOD_PACKAGE_DISCOUNT_ID, KEY_GOOD_PACKAGE_GOODPACKREF,
                                KEY_GOOD_PACKAGE_REPLACEGOODPACKREF, KEY_GOOD_PACKAGE_BASEGOODSREF,
                                KEY_GOOD_PACKAGE_UNITQTY,KEY_GOOD_PACKAGE_UNITQTY, KEY_GOOD_PACKAGE_TOTALQTY,
                                KEY_GOOD_PACKAGE_UNITREF, KEY_GOOD_PACKAGE_GOODSPACKAGEREF, KEY_GOOD_PACKAGE_PRIZECOUNT}
                        , KEY_GOOD_PACKAGE_EVCID + "='" + evcId+"'"
                        , null, null, null, null);
            return c;
    }

}