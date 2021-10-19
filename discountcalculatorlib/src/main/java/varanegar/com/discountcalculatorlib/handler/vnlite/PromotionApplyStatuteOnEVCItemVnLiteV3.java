package varanegar.com.discountcalculatorlib.handler.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;

public class PromotionApplyStatuteOnEVCItemVnLiteV3 {

	/*usp_ApplyStatuteOnEVCItem*/
	public static void applyStatuteOnEVCItem(String evcId)
	{
		EVCItemVnLiteDBAdapter.getInstance().applyStatuteOnEVCItem1(evcId);
		EVCItemVnLiteDBAdapter.getInstance().applyStatuteOnEVCItem2(evcId);
		EVCItemVnLiteDBAdapter.getInstance().applyStatuteOnEVCItem3(evcId);
	}
}
