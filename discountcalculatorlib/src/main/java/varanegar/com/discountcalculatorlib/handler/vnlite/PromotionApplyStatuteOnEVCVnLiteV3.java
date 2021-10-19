package varanegar.com.discountcalculatorlib.handler.vnlite;


import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;

public class PromotionApplyStatuteOnEVCVnLiteV3 {

	public static void applyStatuteOnEVC(String evcId)
	{
		EVCHeaderVnLiteDBAdapter.getInstance().applyStatuteOnEVC(evcId);
	}
}
