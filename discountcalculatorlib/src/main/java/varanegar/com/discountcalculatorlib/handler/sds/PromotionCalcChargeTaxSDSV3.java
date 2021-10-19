package varanegar.com.discountcalculatorlib.handler.sds;


import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;

public class PromotionCalcChargeTaxSDSV3 {

	public static void calcChargeTax(String evcId)
	{
		EVCItemSDSDBAdapter.getInstance().calcChargeTax(evcId);
		EVCHeaderSDSDBAdapter.getInstance().updateChargeTax(evcId);
	}
}
