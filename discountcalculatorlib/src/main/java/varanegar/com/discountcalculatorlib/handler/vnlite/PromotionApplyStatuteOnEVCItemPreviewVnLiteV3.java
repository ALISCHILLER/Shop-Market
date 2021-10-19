package varanegar.com.discountcalculatorlib.handler.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemStatutesVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempSummaryFinalVnLiteDBAdapter;

public class PromotionApplyStatuteOnEVCItemPreviewVnLiteV3{

	/*usp_ApplyStatuteOnEVCItem_preview*/
	public static void applyStatuteOnEVCItemPreview(String evcId, String callId)
	{
		if(checkPrice(evcId) != 0) return;
		fillEVCSummaryFinal(evcId, callId);
  		updateReduceOfQtyInEVCItem(evcId);
		deleteInvalidPrize(evcId);
		mergePrize(evcId);
	}

	/*[usp_ApplyStatuteOnEVCItem_CheckStatus]*/
	private static int checkPrice(String evcId)
	{
		return 0;
	}

	private static void fillEVCSummaryFinal(String evcId, String callId) {

		EVCTempSummaryFinalVnLiteDBAdapter.getInstance().fillEVCItemSummaryFinal(evcId, callId);
	}

	private static void fillPrizeItemsInEVCItem(String evcId, String callId)
	{
		EVCItemVnLiteDBAdapter.getInstance().fillByEVCItemPreviewOld(evcId, callId);
	}

	private static void updateReduceOfQtyInEVCItem(String evcId)
	{
		EVCItemVnLiteDBAdapter.getInstance().updateReduceOfQtyOld(evcId);

		EVCItemVnLiteDBAdapter.getInstance().updateReduceOfPrice(evcId);
	}

	/*[usp_DeleteInvalidPrize]*/
	private static void deleteInvalidPrize(String evcId)
	{
		EVCItemStatutesVnLiteDBAdapter.getInstance().deleteInvalidItemSatatutes(evcId);

		EVCItemVnLiteDBAdapter.getInstance().deleteInvalidPrizeItems(evcId);
	}


	private static void mergePrize(String evcId)
	{
		EVCItemVnLiteDBAdapter.getInstance().mergePrize1(evcId);

	}

	private static void updatePrizeMinUnit(String evcId)
	{
		//there is a dummy code in VnLite SP . we dont need taht
//		EVCItemDBAdapter.getInstance().updatePrizeMinUnit(evcId);
	}

	private static void setBatchForPrize(String evcId)
	{
		//TODO customize it for tablet
		//EVCItemVnLiteDBAdapter.getInstance().setBatchForPrize(evcId);
	}
}
