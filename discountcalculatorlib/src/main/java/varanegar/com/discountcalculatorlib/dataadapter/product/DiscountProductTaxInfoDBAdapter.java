package varanegar.com.discountcalculatorlib.dataadapter.product;

import android.database.Cursor;
import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;
import varanegar.com.discountcalculatorlib.entity.product.DiscountProductTaxInfo;

public class DiscountProductTaxInfoDBAdapter extends DiscountBaseDataAdapter {
	public static final String KEY_PTAX_PRODUCTID = "ProductId";
	public static final String KEY_PTAX_TAX_PERCDENT = "TaxRate";
	public static final String KEY_PTAX_CHARGE_PERCENT = "ChargeRate";

	private final String DATABASE_TABLE = "DiscountProductTaxInfo";

	private static DiscountProductTaxInfoDBAdapter instance;

	public DiscountProductTaxInfoDBAdapter() {
	}

	public static DiscountProductTaxInfoDBAdapter getInstance() {

		if (instance == null) {
			instance = new DiscountProductTaxInfoDBAdapter();
		}
		return instance;

	}

	public void clearAllData(){
		db.delete(DATABASE_TABLE, null , null);
	}

	private Cursor getAllTaxInfo() {
		//prepareData();
		try {
			if (db != null) {
				return db.query(DATABASE_TABLE, new String[]{KEY_PTAX_PRODUCTID,
								KEY_PTAX_CHARGE_PERCENT, KEY_PTAX_TAX_PERCDENT},
						null, null, null, null, null);
			} else return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public DiscountProductTaxInfo getProductTaxInfo(int productId) {
		Cursor c = db.query(DATABASE_TABLE, new String[]{KEY_PTAX_PRODUCTID,
						KEY_PTAX_CHARGE_PERCENT, KEY_PTAX_TAX_PERCDENT},
				KEY_PTAX_PRODUCTID + " = " + productId, null, null, null, null);
		DiscountProductTaxInfo inv = null;
		if ((c != null) && (c.moveToFirst())) {
			try {
				inv = new DiscountProductTaxInfo(c.getInt(c.getColumnIndex(KEY_PTAX_PRODUCTID)),
						c.getDouble(c.getColumnIndex(KEY_PTAX_TAX_PERCDENT)),
						c.getDouble(c.getColumnIndex(KEY_PTAX_CHARGE_PERCENT)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return inv;
	}
}