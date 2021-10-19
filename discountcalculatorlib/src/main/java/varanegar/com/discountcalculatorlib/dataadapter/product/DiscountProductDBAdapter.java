package varanegar.com.discountcalculatorlib.dataadapter.product;


import android.database.Cursor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProduct;


public class DiscountProductDBAdapter extends DiscountBaseDataAdapter {


    public static final String KEY_PRODUCT_PRODUCTID = "ProductId";
    public static final String KEY_PRODUCT_PRODUCTCODE = "ProductCode";
    public static final String KEY_PRODUCT_PRODUCTNAME = "ProductName";
    public static final String KEY_PRODUCT_SUBGROUPID = "ProductSubGroupId";
    public static final String KEY_PRODUCT_SMALLUNITID = "SmallUnitId";
    public static final String KEY_PRODUCT_SMALLUNITNAME = "SmallUnitName";
    public static final String KEY_PRODUCT_SMALLUNITQTY = "SmallUnitQty";
    public static final String KEY_PRODUCT_LARGEUNITID = "LargeUnitId";
    public static final String KEY_PRODUCT_LARGEUNITNAME = "LargeUnitName";
    public static final String KEY_PRODUCT_LARGEUNITQTY = "LargeUnitQty";
    public static final String KEY_PRODUCT_DESCRIPTION = "Description";
    public static final String KEY_PRODUCT_TYPE_ID = "ProductTypeId";
    public static final String KEY_MANUFACTURE_ID = "ManufacturerId";
    public static final String KEY_PRODUCT_BO_GROUP_ID = "ProductBoGroupId";
    public static final String KEY_BRAND_ID = "BrandId";
    public static final String KEY_PRODUCT_CTGR_ID = "ProductCtgrId";
    public static final String KEY_PRODUCT_TAX = "Tax";
    public static final String KEY_PRODUCT_CHARGE = "Charge";
    public static final String KEY_PRODUCT_WEIGHT = "ProductWeight";
    public static final String KEY_PRODUCT_CARTON_TYPE = "CartonType";

    public static final String KEY_PRODUCT_CARTON_PRIZE_QTY = "CartonPrizeQty";



    public static final String DATABASE_TABLE = "DiscountProduct";
    private static String TAG = "DiscountProductDBAdapter";
    private static DiscountProductDBAdapter instance;

    private DiscountProduct[] discountProductInfo = null;

    private DiscountProductDBAdapter() {
    }

    public static DiscountProductDBAdapter getInstance() {
        if (instance == null) {
            instance = new DiscountProductDBAdapter();
        }
        return instance;
    }

    public DiscountProduct getProductById(int _ProductId) {
        Cursor c = null;
        if (db != null) {
            c = db.query(DATABASE_TABLE, new String[]{KEY_PRODUCT_PRODUCTID, KEY_PRODUCT_PRODUCTCODE,
                    KEY_PRODUCT_PRODUCTNAME, KEY_PRODUCT_SUBGROUPID, KEY_PRODUCT_SMALLUNITID, KEY_PRODUCT_SMALLUNITNAME, KEY_PRODUCT_SMALLUNITQTY,
                    KEY_PRODUCT_LARGEUNITID, KEY_PRODUCT_LARGEUNITNAME, KEY_PRODUCT_LARGEUNITQTY, KEY_PRODUCT_DESCRIPTION, KEY_PRODUCT_TYPE_ID,
                    KEY_MANUFACTURE_ID, KEY_PRODUCT_BO_GROUP_ID, KEY_BRAND_ID, KEY_PRODUCT_CTGR_ID, KEY_PRODUCT_CHARGE, KEY_PRODUCT_TAX, KEY_PRODUCT_WEIGHT, KEY_PRODUCT_CARTON_TYPE},
                    KEY_PRODUCT_PRODUCTID + " = " + _ProductId , null, null, null, null);
        }
        return CursorToProduct(c);
    }

    private DiscountProduct CursorToProduct(Cursor c){
        DiscountProduct p = null;

        if (c != null && c.moveToFirst()) {
            p = new DiscountProduct(
                    c.getInt(c.getColumnIndex(KEY_PRODUCT_PRODUCTID)),
                    c.getString(c.getColumnIndex(KEY_PRODUCT_PRODUCTCODE)),
                    c.getString(c.getColumnIndex(KEY_PRODUCT_PRODUCTNAME)),
                    c.getInt(c.getColumnIndex(KEY_PRODUCT_SUBGROUPID)),
                    c.getLong(c.getColumnIndex(KEY_PRODUCT_SMALLUNITID)),
                    c.getString(c.getColumnIndex(KEY_PRODUCT_SMALLUNITNAME)),
                    new BigDecimal(c.getDouble(c.getColumnIndex(KEY_PRODUCT_SMALLUNITQTY))),
                    c.getLong(c.getColumnIndex(KEY_PRODUCT_LARGEUNITID)),
                    c.getString(c.getColumnIndex(KEY_PRODUCT_LARGEUNITNAME)),
                    new BigDecimal(c.getDouble(c.getColumnIndex(KEY_PRODUCT_LARGEUNITQTY))),
                    c.getString(c.getColumnIndex(KEY_PRODUCT_DESCRIPTION)),
                    c.getInt(c.getColumnIndex(KEY_MANUFACTURE_ID)),
                    c.getInt(c.getColumnIndex(KEY_PRODUCT_BO_GROUP_ID)),
                    c.getInt(c.getColumnIndex(KEY_BRAND_ID)),
                    c.getInt(c.getColumnIndex(KEY_PRODUCT_CTGR_ID)),
                    c.getInt(c.getColumnIndex(KEY_PRODUCT_TYPE_ID)),
                    new BigDecimal(c.getDouble(c.getColumnIndex(KEY_PRODUCT_CHARGE))),
                    new BigDecimal(c.getDouble(c.getColumnIndex(KEY_PRODUCT_TAX))),
                    new BigDecimal(c.getDouble(c.getColumnIndex(KEY_PRODUCT_WEIGHT))),
                    c.getInt(c.getColumnIndex(KEY_PRODUCT_CARTON_TYPE))
            );
        }

        return  p;
    }

    public BigDecimal checkCartonPrizeQtyForEVC(int productId, BigDecimal totalQty)
    {
        Cursor c = db.rawQuery("SELECT IFNULL(CartonPrizeQty,0), IFNULL(CartonType,0) FROM " + DATABASE_TABLE + " WHERE ProductId = " + productId, null);
        int cartonPrizeQty, cartonType;

        if(c != null && c.moveToFirst())
        {
            cartonPrizeQty = c.getInt(0);
            cartonType = c.getInt(1);

            if(cartonPrizeQty != 0)
            {
                BigDecimal qty = totalQty.divide(new BigDecimal(cartonType), 0, RoundingMode.FLOOR);
                qty = qty.multiply(new BigDecimal(cartonPrizeQty));
                totalQty = totalQty.subtract(qty);
            }
        }

        return totalQty;
    }

    public boolean isProductBulk(int id){
        DiscountProduct p = getProductById(id);
        return p.isBulk();
    }
    public void clearAllData(){
        db.delete(DATABASE_TABLE, null , null);
    }

    public DiscountProduct getProductByCode(String _ProductCode) {
        Cursor c = null;
        if (db != null) {
            c = db.query(DATABASE_TABLE, new String[]{KEY_PRODUCT_PRODUCTID, KEY_PRODUCT_PRODUCTCODE,
                            KEY_PRODUCT_PRODUCTNAME, KEY_PRODUCT_SUBGROUPID, KEY_PRODUCT_SMALLUNITID, KEY_PRODUCT_SMALLUNITNAME, KEY_PRODUCT_SMALLUNITQTY,
                            KEY_PRODUCT_LARGEUNITID, KEY_PRODUCT_LARGEUNITNAME, KEY_PRODUCT_LARGEUNITQTY, KEY_PRODUCT_DESCRIPTION, KEY_PRODUCT_TYPE_ID,
                            KEY_MANUFACTURE_ID, KEY_PRODUCT_BO_GROUP_ID, KEY_BRAND_ID, KEY_PRODUCT_CTGR_ID, KEY_PRODUCT_CHARGE, KEY_PRODUCT_TAX, KEY_PRODUCT_WEIGHT, KEY_PRODUCT_CARTON_TYPE},
                    KEY_PRODUCT_PRODUCTCODE + " = '" + _ProductCode + "'" , null, null, null, null);
        }
        return CursorToProduct(c);
    }

}
