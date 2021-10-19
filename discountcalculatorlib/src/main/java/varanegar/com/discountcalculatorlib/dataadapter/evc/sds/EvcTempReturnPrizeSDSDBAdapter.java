package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceHeaderDBAdapter;

/**
 * Created by A.Razavi on 12/11/2017.
 */

public class EvcTempReturnPrizeSDSDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "EvcTempReturnPrizeSDS";
    private static EvcTempReturnPrizeSDSDBAdapter instance;

    private EvcTempReturnPrizeSDSDBAdapter() {
    }

    public static EvcTempReturnPrizeSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EvcTempReturnPrizeSDSDBAdapter();
        }

        return instance;
    }
    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void fillTemp(String saleEVCId, String newSaleEVCId){
        clearAllData();
        String query = "INSERT INTO " + DATABASE_TABLE +
                "   SELECT GoodsRef, sum(Qty) as ReturnPrizeQty \n" +
                "   FROM (" +
                "        SELECT GoodsRef, case when EvcRef= '" + saleEVCId +"' then 1 else -1 end * TotalQty as Qty\n" +
                "        FROM " + EVCItemSDSDBAdapter.DATABASE_TABLE + " I \n" +
                "        WHERE EVCRef in ('" + saleEVCId +"', '" + newSaleEVCId +"')\n" +
                "        AND PrizeType = 1\n" +
                "        AND RowOrder <> _Id\n" +
                ") A\n" +
                "group by GoodsRef";
        db.execSQL(query);
    }


}
