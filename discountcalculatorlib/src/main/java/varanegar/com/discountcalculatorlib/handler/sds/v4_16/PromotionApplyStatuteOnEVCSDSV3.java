package varanegar.com.discountcalculatorlib.handler.sds.v4_16;

import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;

public class PromotionApplyStatuteOnEVCSDSV3 {

	public static void applyStatuteOnEVC(String evcId)
	{
		EVCHeaderSDSDBAdapter.getInstance().applyStatuteOnEVC(evcId);
	}
}
