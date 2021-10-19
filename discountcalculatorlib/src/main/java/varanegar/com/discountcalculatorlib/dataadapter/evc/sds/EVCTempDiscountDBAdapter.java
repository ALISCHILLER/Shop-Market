package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;

public class EVCTempDiscountDBAdapter extends DiscountBaseDataAdapter {

    public static final String KEY_ID = "Id";
    public static final String KEY_TEM_Dis_Group = "DisGroup";
    public static final String KEY_TEMP_DIS_TYPE = "DisType";
    public static final String KEY_TEMP_DIS_PRIORITY = "Priority";
    public static final String KEY_TEMP_DIS_APPLY = "ApplyInGroup";
    public static final String KEY_TEMP_DIS_QTY_UNIT = "QtyUnit";
    public static final String KEY_TEMP_DIS_MIN_QTY = "MinQty";
    public static final String KEY_TEMP_DIS_MAX_QTY = "MaxQty";
    public static final String KEY_TEMP_DIS_MIN_AMOUNT = "MinAmount";
    public static final String KEY_TEMP_DIS_MAX_AMOUNT = "MaxAmount";
    public static final String KEY_TEMP_DIS_MIN_WEIGHT = "MinWeight";
    public static final String KEY_TEMP_DIS_MAX_WEIGHT = "MaxWeight";
    public static final String KEY_TEMP_DIS_MIN_ROW_COUNT = "MinRowsCount";
    public static final String KEY_TEMP_DIS_MAX_ROW_COUNT = "MaxRowsCount";
    public static final String KEY_TEMP_DIS_ORDER_TYPE = "OrderType";
    public static final String KEY_TEMP_DIS_GOODS_REF = "GoodsRef";
    public static final String KEY_TEMP_DIS_IS_ACTIVE = "IsActive";
    public static final String KEY_TEMP_DIS_PAY_TYPE = "PayType";
    public static final String KEY_TEMP_DIS_AREA_REF = "AreaRef";
    public static final String KEY_TEMP_DIS_STATE_REF = "StateRef";
    public static final String KEY_TEMP_DIS_DC_REF = "DcRef";
    public static final String KEY_TEMP_DIS_CUS_ACT_REF = "CustActRef";
    public static final String KEY_TEMP_DIS_CUS_CTGR_REF = "CustCtgrRef";
    public static final String KEY_TEMP_DIS_CUST_LEVEL_REF = "CustLevelRef";
    public static final String KEY_TEMP_DIS_CUST_REF = "CustRef";
    public static final String KEY_TEMP_DIS_SALE_OFFICE_REF = "SaleOfficeRef";
    public static final String KEY_TEMP_DIS_SALE_ZONE_REF = "SaleZoneRef";
    public static final String KEY_TEMP_DIS_GOODS_GROUP_REF = "GoodsGroupRef";
    public static final String KEY_TEMP_DIS_GOODS_CTGR_REF = "GoodsCtgrRef";
    public static final String KEY_TEMP_DIS_MANUFACTURER_REF = "ManufacturerRef";
    public static final String KEY_TEMP_DIS_ORDER_NO = "OrderNo";
    public static final String KEY_TEMP_DIS_MAIN_TYPE_REF = "MainTypeRef";
    public static final String KEY_TEMP_DIS_SUB_TYPE_REF = "SubTypeRef";
    public static final String KEY_TEMP_DIS_BRAND_REF = "BrandRef";
    public static final String KEY_TEMP_DIS_PRIZE_REF = "PrizeRef";
    public static final String KEY_TEMP_DIS_PRIZE_STEP = "PrizeStep";
    public static final String KEY_TEMP_DIS_PRIZE_STEP_TYPE = "PrizeStepType";
    public static final String KEY_TEMP_DIS_START_DATE = "StartDate";
    public static final String KEY_TEMP_DIS_END_DATE = "EndDate";
    public static final String KEY_TEMP_DIS_CALC_METHODE = "CalcMethod";

   // public static final String KEY_PRIZE_PACKAGE_REF= "PrizePackageRef";



    public static final String DATABASE_TABLE = (GlobalVariables.getBackOfficeVersion() == BackOfficeVersion.SDS16 ? "EVCTempDiscountSDS4_16" : "EVCTempDiscountSDS4_19");

    private static EVCTempDiscountDBAdapter instance;


    private EVCTempDiscountDBAdapter()
    {
    }

    public static EVCTempDiscountDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempDiscountDBAdapter();
        }

        return instance;

    }

    /*usp_FillEVCStatuteByID_FillDiscount*/
    public void fillDiscountTemp(String evcId, EVCType evcType, int refId) {
        if (GlobalVariables.getBackOfficeVersion()  == BackOfficeVersion.SDS16)
            fillDiscountTemp4_16(evcId, evcType, refId);
        else
            fillDiscountTemp4_19(evcId, evcType, refId);
    }

    public void fillDiscountTemp4_16(String evcId, EVCType evcType, int refId)
    {
        String query = "INSERT INTO " + DATABASE_TABLE +
                       " (Id, DisGroup, DisType, Priority, ApplyInGroup, QtyUnit, MinQty, MaxQty, MinAmount, MaxAmount, MinWeight, MaxWeight, MinRowsCount, MaxRowsCount \n" +
                       " , OrderType, GoodsRef, IsActive, PayType, AreaRef, StateRef, DcRef, CustActRef, CustCtgrRef, CustLevelRef, CustRef, SaleOfficeRef, SaleZoneRef, GoodsGroupRef \n"+
                       " , GoodsCtgrRef, ManufacturerRef, OrderNo, MainTypeRef, SubTypeRef, BrandRef, PrizeRef, PrizeStep, PrizeStepType, StartDate, EndDate, CalcMethod,PrizePackageRef,MinCustRemAmount,MaxCustRemAmount) \n"+

                       " SELECT d.Id, d.DisGroup, d.DisType, d.Priority, d.ApplyInGroup,d.QtyUnit ,d.MinQty ,d.MaxQty ,d.MinAmount ,d.MaxAmount ,d.MinWeight, d.MaxWeight ,d.MinRowsCount ,d.MaxRowsCount \n"+
                       " ,d.OrderType ,d.GoodsRef, d.IsActive, d.PayType, d.AreaRef, d.StateRef , d.DcRef , d.CustActRef , d.CustCtgrRef, d.CustLevelRef, d.CustRef, d.SaleOfficeRef, d.SaleZoneRef \n" +
                       " ,d.GoodsGroupRef, d.GoodsCtgrRef, d.ManufacturerRef, d.OrderNo, d.MainTypeRef, d.SubTypeRef, d.BrandRef, d.PrizeRef, d.PrizeStep, d.PrizeStepType \n" +
                       " ,d.StartDate, d.EndDate, d.CalcMethod , d.PrizePackageRef , d.MinCustRemAmount, d.MaxCustRemAmount " +
                       " FROM " + DiscountDBAdapter.DATABASE_TABLE + " d " +
                       " WHERE 1 = 1 ";

        if(evcType == EVCType.SELLRETURN)
        {
            query += " AND EXISTS(SELECT 1 FROM " + DisSaleSDSDBAdapter.DATABASE_TABLE + " ds WHERE ds.HdrRef = " + refId +
                     " AND ds.DisGroup = d.DisGroup) \n" +
                     " AND (IFNULL(d.IsActive, 1) = 1 or EXISTS (SELECT 1 FROM " + DisSaleSDSDBAdapter.DATABASE_TABLE + " ds WHERE ds.HdrRef=" + refId + " and ds.DisRef=d.Id))";
            //TODO not sure about this line
//            AND ('''+@Date+''' BETWEEN D.StartDate AND ISNULL(D.ENDDate,'''+ @Date+''') or D.ID in (select DisRef from sle.tblDisSale ds where ds.HdrRef='+CAST(@SaleRef AS varchar)+'))'
        }

        db.execSQL(query);
    }
    /*usp_FillEVCStatuteByID_FillDiscount*/
    public void fillDiscountTemp4_19(String evcId, EVCType evcType, int refId)
    {
        String query = "INSERT INTO " + DATABASE_TABLE +
                " (Id, DisGroup, DisType, Priority, ApplyInGroup, QtyUnit, MinQty, MaxQty, MinAmount, MaxAmount, MinWeight, MaxWeight, MinRowsCount, MaxRowsCount \n" +
                " , GoodsRef, IsActive" +
                " , GoodsGroupRef, GoodsCtgrRef, ManufacturerRef, MainTypeRef, SubTypeRef, BrandRef, PrizeRef, PrizeStep, PrizeStepType, StartDate, EndDate, CalcMethod " +
                " , PrizePackageRef, MinCustRemAmount, MaxCustRemAmount, TotalMinAmount, TotalMaxAmount, TotalMinRowCount, TotalMaxRowCount " +
                " , PrizeStepUnit,MixCondition" +
                " , MinCustChequeRetQty, MaxCustChequeRetQty) \n"+

                " SELECT d.Id, d.DisGroup, d.DisType, d.Priority, d.ApplyInGroup,d.QtyUnit ,d.MinQty ,d.MaxQty ,d.MinAmount ,d.MaxAmount ,d.MinWeight, d.MaxWeight ,d.MinRowsCount ,d.MaxRowsCount \n"+
                " ,d.GoodsRef, d.IsActive" +
                " ,d.GoodsGroupRef, d.GoodsCtgrRef, d.ManufacturerRef, d.MainTypeRef, d.SubTypeRef, d.BrandRef, d.PrizeRef, d.PrizeStep, d.PrizeStepType, d.StartDate, d.EndDate, d.CalcMethod, \n" +
                "  D.PrizePackageRef, D.MinCustRemAmount, D.MaxCustRemAmount , TotalMinAmount, TotalMaxAmount, TotalMinRowCount, TotalMaxRowCount" +
                " ,Case when D.PrizeStep is null then null else D.PrizeStepUnit end as PrizeStepUnit,D.MixCondition " +
                ", D.MinCustChequeRetQty, D.MaxCustChequeRetQty  "+
                " FROM " + DiscountDBAdapter.DATABASE_TABLE + " d " +
                " WHERE 1 = 1 ";

        if(evcType == EVCType.SELLRETURN)
        {
            query += " AND EXISTS(SELECT 1 FROM " + DisSaleSDSDBAdapter.DATABASE_TABLE + " ds WHERE ds.HdrRef = " + refId +
                    " AND ds.DisGroup = d.DisGroup) \n" +
                    " AND (IFNULL(d.IsActive, 1) = 1 or EXISTS (SELECT 1 FROM " + DisSaleSDSDBAdapter.DATABASE_TABLE + " ds WHERE ds.HdrRef=" + refId + " and ds.DisRef=d.Id))";
            //TODO not sure about this line
//            AND ('''+@Date+''' BETWEEN D.StartDate AND ISNULL(D.ENDDate,'''+ @Date+''') or D.ID in (select DisRef from sle.tblDisSale ds where ds.HdrRef='+CAST(@SaleRef AS varchar)+'))'
        }
        db.execSQL(query);
    }

    public void deleteInvalidDiscountForSellReturn(String evcId, int saleId)
    {
        String query = "DELETE FROM " + DATABASE_TABLE
                + " WHERE DisGroup IN ( \n"
                + "       SELECT d.DisGroup \n"
                + " FROM " + DATABASE_TABLE +" d   \n"
                + " INNER JOIN EvcSkipDiscount de ON d.ID=de.DisRef) \n"
                + " AND GoodsRef IS NOT NULL AND PrizeRef IS NOT NULL \n"
                + " AND GoodsRef=PrizeRef "
                + " AND IFNULL(MinQty, 0) = 0 \n"
                + " AND IfNULL(MinAmount, 0) = 0 ";
        db.execSQL(query);

        query = "DELETE FROM " + DATABASE_TABLE +
                " WHERE Id in ( "+
                        "   SELECT d.Id "+
                        "   FROM "+ DATABASE_TABLE + " d "+
                        "   INNER JOIN (\n"+
                                " SELECT d2.DisGroup, d2.GoodsRef, d2.ManufacturerRef, d2.BrandRef, d2.MainTypeRef, d2.SubTypeRef, d2.GoodsGroupRef, d2.GoodsCtgrRef, de.EvcRef \n" +
                                " FROM "+ EVCSkipDiscountDBAdapter.DATABASE_TABLE +" de \n" +
                                " INNER JOIN "+ DiscountDBAdapter.DATABASE_TABLE +" d2 on d2.ID=de.DisRef and de.SaleRef="+saleId+") ds ON d.DisGroup=ds.DisGroup "+
                        " WHERE (ds.EvcRef= '" + evcId + "') "+
                        " AND (d.GoodsRef is null and ds.GoodsRef is null)"+
                        " AND (IFNULL(d.ManufacturerRef, 0) = IFNULL(ds.ManufacturerRef, 0))"+
                        " AND (IFNULL(d.BrandRef, 0) = IFNULL(ds.BrandRef, 0))"+
                        " AND (IFNULL(d.MainTypeRef, 0) = IFNULL(ds.MainTypeRef, 0))"+
                        " AND (IFNULL(d.SubTypeRef, 0) = IFNULL(ds.SubTypeRef, 0))"+
                        " AND (IFNULL(d.GoodsGroupRef, 0) = IFNULL(ds.GoodsGroupRef, 0))"+
                        " AND (IFNULL(d.GoodsCtgrRef, 0) = IFNULL(ds.GoodsCtgrRef, 0))"+
                ")";

        db.execSQL(query);

    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
