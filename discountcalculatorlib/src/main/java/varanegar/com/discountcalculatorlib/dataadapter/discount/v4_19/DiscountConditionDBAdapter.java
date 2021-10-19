package varanegar.com.discountcalculatorlib.dataadapter.discount.v4_19;

import varanegar.com.discountcalculatorlib.dataadapter.base.DiscountBaseDataAdapter;

public class DiscountConditionDBAdapter  extends DiscountBaseDataAdapter
{

	public static final String KEY_ID = "Id";
	public static final String DATABASE_TABLE = "DiscountConditionSDS4_19";

	private static DiscountConditionDBAdapter instance;

	public DiscountConditionDBAdapter() {
	}

	public static DiscountConditionDBAdapter getInstance()
	{
		if(instance == null)
		{
			instance = new DiscountConditionDBAdapter();
		}

		return instance;
	}

	public void clearAllData(){
		db.delete(DATABASE_TABLE, null , null);
	}

}
