package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.Cursor;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.product.DiscountProductUnitDBAdapter;


public class EVCTempAcceptedDiscountAdapter extends DiscountBaseDataAdapter {

    public final String KEY_ACCEPT_DISCONT_ROWID = "_id";


    public final String KEY_ACCEPT_DISCONT_DISCOUNTID = "DiscountId";
    public final String KEY_ACCEPT_DISCONT_PRIZECOUNT = "PrizeCount";
    public final String KEY_ACCEPT_DISCONT_ACCYEAR = "AccYear";
    public final String KEY_ACCEPT_DISCONT_EVCOPRDATE = "EvcOprDate";
    public final String KEY_ACCEPT_DISCONT_DISACCREF = "DisAccRef";

    public static final String DATABASE_TABLE = "EVCTempAcceptedDiscountSDS";
    private static String TAG = "EVCTempAcceptedDiscountAdapter";
    private static EVCTempAcceptedDiscountAdapter instance;

    public EVCTempAcceptedDiscountAdapter()
    {
    }

    public static EVCTempAcceptedDiscountAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new EVCTempAcceptedDiscountAdapter();
        }
        return instance;
    }

    public int fillEVCAcceptedDiscountTemp(String evcId, int disId) {
        String sql2 = "delete from " + DATABASE_TABLE;
        db.execSQL(sql2);

        int result = 1;
        //String sql2= "delete FROM "+ DATABASE_TABLE;
        //db.execSQL(sql2);
        String sql = " INSERT INTO " + DATABASE_TABLE + "(DiscountId, PrizeCount, AccYear, EvcOprDate, DisAccRef) " +
                " SELECT DisRef, PrizeCount, AccYear, EvcDate,DisAccRef\n" +
                " FROM(\n" +
                "  SELECT\n" +
                "   ES.DisRef,\n" +
                "   CASE -- گام افزايش\n" +
                "    WHEN IFNULL(D.PrizeStepType,0) = 0  AND D.PrizeType IN (1,5,6) THEN -- تعدادي\n" +
                "     CASE\n" +
                "      WHEN (D.MinQty IS NOT NULL) AND IFNULL(D.PrizeStep,0)>0\n" +
                "       THEN CAST(((SUM(CASE WHEN EI.TotalQty<=0 THEN 0 ELSE EI.TotalQty END /(\n" +
                "        CASE\n" +
                "         WHEN D.DisType=300\n" +
                "         THEN 1 /*IFNULL(P3.Quantity,1)*/\n" +
                "         WHEN (D.DisType<>300 AND Ifnull(D.PrizeStepUnit, D.QtyUnit)<>0 )\n" +
                "         THEN 1\n" +
                "         ELSE G.CartonType\n" +
                "        END)) - (IFNULL(D.MinQty,0)*CASE WHEN D.DisType=300 then pk_QtyUnit.Quantity else 1 end )) " +
                "	/ (D.PrizeStep*CASE WHEN D.DisType=300 THEN IFNULL(pk_PrizeStepUnit.Quantity,pk_QtyUnit.Quantity ) ELSE 1 END)) AS integer) " +
                "      WHEN IFNULL(D.PrizeStep,0)>0\n" +
                "       THEN CAST(((SUM(CASE WHEN EI.TotalQty<=0 THEN 0 ELSE EI.TotalQty END/(\n" +
                "        CASE \n" +
                "         WHEN D.DisType=300 \n" +
                "         THEN 1 --IFNULL(pk_PrizeStepUnit.Quantity,1)\n" +
                "         WHEN (D.DisType<>300 AND IFNULL(D.PrizeStepUnit, D.QtyUnit)<>0) \n" +
                "         THEN 1 \n" +
                "         ELSE G.CartonType \n" +
                "        END))) / (D.PrizeStep*CASE WHEN D.DisType=300 THEN IFNULL(pk_PrizeStepUnit.Quantity,pk_QtyUnit.Quantity ) ELSE 1 END)) AS integer) \n" +
                "      ELSE 1 --IFNULL(D.PrizeQty,1)\n" +
                "     END\n" +
                "    WHEN IFNULL(D.PrizeStepType,0) = 1 AND D.PrizeType IN (1,5,6) THEN -- ريالي\n" +
                "     CAST(((SUM(CASE D.CalcMethod -- نحوه محاسبه\n" +
                "      WHEN 1 THEN EI.Amount        -- مبلغ ناخالص\n" +
                "      WHEN 2 THEN EI.Amount - EI.Discount + EI.AddAmount -- مبلغ خالص\n" +
                "      WHEN 3 THEN EI.Amount - EI.Discount     -- مبلغ ناخالص منهاي تخفيف\n" +
                "     END) - IFNULL(D.MinAmount, 0)) / D.PrizeStep ) AS INTEGER) \n" +
                "    END AS PrizeCount, E.AccYear AS AccYear, E.EVCDate, D.DisAccRef\n" +
                "  FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " EI \n" +
                "   INNER JOIN " + EVCHeaderSDSDBAdapter.DATABASE_TABLE + " E  ON E.EVCId = EI.EVCRef AND EI.EVCRef='" + evcId+ "'\n" +
                "   INNER JOIN EVCItemStatutesSDS ES ON EI._ID= ES.EVCItemRef\n" +
                "   INNER JOIN " + DiscountDBAdapter.DATABASE_TABLE + " D ON D.ID = ES.DisRef\n" +
                "   INNER JOIN " + DiscountProductDBAdapter.DATABASE_TABLE + " G ON G.ProductId = EI.GoodsRef\n" +
                "   LEFT OUTER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk_PrizeStepUnit ON pk_PrizeStepUnit.ProductId = D.GoodsRef AND pk_PrizeStepUnit.BackOfficeId= D.PrizeStepUnit\n" +
                "   LEFT OUTER JOIN " + DiscountProductUnitDBAdapter.DATABASE_TABLE + " pk_QtyUnit ON pk_QtyUnit.ProductId = D.GoodsRef AND pk_QtyUnit.BackOfficeId= D.QtyUnit\n" +
                " WHERE EI.EVCRef= '" + evcId+ "'\n" +
                "  AND D.PrizeType IN (1,5,6) AND D.Id = '" + disId + "'\n" +
                "  AND G.IsForSale = 1\n" +
                " GROUP BY ES.DisRef, D.MinQty, D.PrizeStep, E.AccYear, E._ID, D.PrizeStepType, D.MinAmount, E.EVCDate,D.DisAccRef, pk_PrizeStepUnit.Quantity, pk_QtyUnit.Quantity, d.DisType, D.PrizeType" +
                "  ) X\n" +
                " WHERE PrizeCount > 0\n";

        db.execSQL(sql);

        Cursor affectedCursor = db.rawQuery("SELECT CHANGES()", null);
        if (affectedCursor != null && affectedCursor.moveToFirst()) {
            if (affectedCursor.getInt(0) == 0) {
                EVCItemStatutesSDSDBAdapter.getInstance().deleteUnAffectedEVCItemStatutes(evcId, disId);
                result = 0;
            }
        }
        return result;

    }

    public void deleteAllEVCTemps() {
        db.delete(DATABASE_TABLE, null, null);
    }
}
