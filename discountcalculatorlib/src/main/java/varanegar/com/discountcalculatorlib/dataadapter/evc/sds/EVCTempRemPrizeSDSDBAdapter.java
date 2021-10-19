package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;

/**
 * Created by A.Razavi on 12/11/2017.
 */

public class EVCTempRemPrizeSDSDBAdapter extends DiscountBaseDataAdapter {
    public static final String DATABASE_TABLE = "EVCTempRemPrizeSDS";
    private static EVCTempRemPrizeSDSDBAdapter instance;

    private EVCTempRemPrizeSDSDBAdapter() {
    }

    public static EVCTempRemPrizeSDSDBAdapter getInstance() {

        if (instance == null) {
            instance = new EVCTempRemPrizeSDSDBAdapter();
        }

        return instance;
    }
    public void clearAllData()
    {
        db.delete(DATABASE_TABLE, null, null);
    }

    public void fillTemp(int saleId){
        clearAllData();
        String query =  "INSERT INTO " + DATABASE_TABLE +
                " SELECT GoodsRef, sum(Qty) as RemPrizeQty\n" +
                " FROM (\n" +
                "   select ProductId as GoodsRef, TotalQty as Qty " +
                "   FROM " + DiscountCustomerOldInvoiceDetailDBAdapter.DATABASE_TABLE +
                "   WHERE SaleId= "+ saleId +
                "   AND PrizeType=1 \n" +
                "  ) A\n" +
                "  group by GoodsRef\n";
        db.execSQL(query);
    }


}
