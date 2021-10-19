package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;

/**
 * Created by m.aghajani on 3/28/2016.
 */
public class EVCTempDiscountVnLiteDBAdapter extends DiscountBaseDataAdapter {

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



    public static final String DATABASE_TABLE = "EVCTempDiscountVnLite";
    private static String TAG = "EVCTempDiscountVnLiteDBAdapter";
    private static EVCTempDiscountVnLiteDBAdapter instance;


    private EVCTempDiscountVnLiteDBAdapter()
    {
    }

    public static EVCTempDiscountVnLiteDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempDiscountVnLiteDBAdapter();
        }

        return instance;

    }

    public void fillDiscountTemp(String evcId, EVCType evcType, int refId)
    {
        String query = "INSERT INTO " + DATABASE_TABLE +
                       " (Id, DisGroup, DisType, Priority, ApplyInGroup, QtyUnit, MinQty, MaxQty, MinAmount, MaxAmount, MinWeight, MaxWeight, MinRowsCount, MaxRowsCount \n" +
                       " , OrderType, GoodsRef, IsActive, PayType, AreaRef, StateRef, DcRef, CustActRef, CustCtgrRef, CustLevelRef, CustRef, SaleOfficeRef, SaleZoneRef, GoodsGroupRef \n"+
                       " , GoodsCtgrRef, ManufacturerRef, OrderNo, MainTypeRef, SubTypeRef, BrandRef, PrizeRef, PrizeStep, PrizeStepType, StartDate, EndDate, CalcMethod) \n"+

                       " SELECT d.Id, d.DisGroup, d.DisType, d.Priority, d.ApplyInGroup,d.QtyUnit ,d.MinQty ,d.MaxQty ,d.MinAmount ,d.MaxAmount ,d.MinWeight, d.MaxWeight ,d.MinRowsCount ,d.MaxRowsCount \n"+
                       " ,d.OrderType ,d.GoodsRef, d.IsActive, d.PayType, d.AreaRef, d.StateRef , d.DcRef , d.CustActRef , d.CustCtgrRef, d.CustLevelRef, d.CustRef, d.SaleOfficeRef, d.SaleZoneRef \n" +
                       " ,d.GoodsGroupRef, d.GoodsCtgrRef, d.ManufacturerRef, d.OrderNo, d.MainTypeRef, d.SubTypeRef, d.BrandRef, d.PrizeRef, d.PrizeStep, d.PrizeStepType \n" +
                       " ,d.StartDate, d.EndDate, d.CalcMethod" +
                       " FROM " + DiscountVnLiteDBAdapter.DATABASE_TABLE + " d " +
                       " WHERE 1 = 1 ";

        if(evcType == EVCType.SELLRETURN)
        {
            query += " AND EXISTS(SELECT 1 FROM " + DisSaleVnLiteDBAdapter.DATABASE_TABLE + " ds WHERE ds.HdrRef = " + refId +
                     " AND ds.DisGroup = d.DisGroup) \n" +
                     " AND (IFNULL(d.IsActive, 1) = 1 or EXISTS (SELECT 1 FROM " + DisSaleVnLiteDBAdapter.DATABASE_TABLE + " ds WHERE ds.HdrRef=" + refId + " and ds.DisRef=d.Id))";
            //TODO not sure about this line
//            AND ('''+@Date+''' BETWEEN D.StartDate AND ISNULL(D.ENDDate,'''+ @Date+''') or D.ID in (select DisRef from sle.tblDisSale ds where ds.HdrRef='+CAST(@SaleRef AS varchar)+'))'
        }

        db.execSQL(query);
    }

    public void deleteInvalidDiscountForSellReturn(String evcId)
    {
        String query = "DELETE FROM " + DATABASE_TABLE
                + " WHERE DisGroup IN ( \n"
                + "       SELECT d.DisGroup \n"
                + "        FROM " + EVCHeaderVnLiteDBAdapter.DATABASE_TABLE +" e \n"
                + "          INNER JOIN " + DisSaleVnLiteDBAdapter.DATABASE_TABLE + " ds on DS.HdrRef = e.RefId \n"
                + "          INNER JOIN " + DATABASE_TABLE + " d on ds.DisRef = d.Id  \n"
                + "        WHERE d.GoodsRef IS NOT NULL AND d.PrizeRef IS NOT NULL \n"
                + "        AND d.GoodsRef = d.PrizeRef AND IFNULL(d.MinQty , 0) = 0 AND IFNULL(d.MinAmount , 0) = 0 \n"
                + "        AND e.EvcId ='" + evcId + "' \n"
                + " )";

        db.execSQL(query);
    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
