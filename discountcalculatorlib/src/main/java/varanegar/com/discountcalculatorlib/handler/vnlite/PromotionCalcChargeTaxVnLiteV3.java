package varanegar.com.discountcalculatorlib.handler.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;

public class PromotionCalcChargeTaxVnLiteV3 {

	public static void calcChargeTax(String evcId)
	{
		EVCItemVnLiteDBAdapter.getInstance().calcChargeTax(evcId);
		EVCHeaderVnLiteDBAdapter.getInstance().updateChargeTax(evcId);
	}
}
