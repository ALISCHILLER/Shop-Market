package varanegar.com.discountcalculatorlib.handler.sds.v4_16;


import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;

public class PromotionApplyStatuteOnEVCItemSDSV3 {

	/*SLE.usp_ApplyStatuteOnEVCItem*/
	public static void applyStatuteOnEVCItem(String evcId)
	{
		EVCItemSDSDBAdapter.getInstance().applyStatuteOnEVCItem1(evcId);
		EVCItemSDSDBAdapter.getInstance().applyStatuteOnEVCItem2(evcId);
		EVCItemSDSDBAdapter.getInstance().applyStatuteOnEVCItem3(evcId);
	}
}
