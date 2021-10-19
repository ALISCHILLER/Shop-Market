package varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite;


import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DisSaleVnLiteDBAdapter extends DiscountBaseDataAdapter {

    public static final String DATABASE_TABLE = "DiscountDisSaleVnLt";
    private static String TAG = "DisSaleVnLiteDBAdapter";
    private static DisSaleVnLiteDBAdapter instance;

    private DisSaleVnLiteDBAdapter() {
    }

    public static DisSaleVnLiteDBAdapter getInstance()
    {

        if(instance == null)
        {
            instance = new DisSaleVnLiteDBAdapter();
        }
        return instance;
    }

    public static void fillTempDisSale(String evcId){
        String sql = "INSERT INTO " + DATABASE_TABLE + " (PromotionId ) \n" +
                "SELECT PromotionId \n" +
                "FROM " + DATABASE_TABLE + " JOIN EVCHeaderSDS ON SellId = RefId ";
        db.execSQL(sql);
    }

    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }



}
