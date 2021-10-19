package varanegar.com.discountcalculatorlib.dataadapter.evc.sds;

import android.database.sqlite.SQLiteDatabase;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;


public class EVCTempGoodsCategorySDSDBAdapter extends DiscountBaseDataAdapter
{

	public final String KEY_EVC_TEMP_ROWID = "_id";


	public static final String DATABASE_TABLE = "EVCTempGoodsCategorySDS";
	private static String TAG = "EVCTempGoodsCategorySDSDBAdapter";
	private static EVCTempGoodsCategorySDSDBAdapter instance;


	public EVCTempGoodsCategorySDSDBAdapter()
	{
	}


	public static EVCTempGoodsCategorySDSDBAdapter getInstance()
	{
		
		if(instance == null)
		{
			instance = new EVCTempGoodsCategorySDSDBAdapter();
		}
		
		return instance;
		
	}
	
	public void deleteAllEVCTemps()
	{
		db.delete(DATABASE_TABLE, null, null);
	}

	public void clearAllData(SQLiteDatabase db)
	{
		db.delete(DATABASE_TABLE, null, null);
	}

}
	 
