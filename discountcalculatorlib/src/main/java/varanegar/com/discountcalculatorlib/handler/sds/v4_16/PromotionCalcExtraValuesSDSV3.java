package varanegar.com.discountcalculatorlib.handler.sds.v4_16;

import android.database.Cursor;

import varanegar.com.discountcalculatorlib.dataadapter.customer.DiscountCustomerOldInvoiceDetailDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemStatutesSDSDBAdapter;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.BackOfficeVersion;

public class PromotionCalcExtraValuesSDSV3 {
/*SLE.usp_CalcExtraValues*/
	public static int calcExtraValues(String evcId, String callId, int orderId, int evcType, int saleId, int dcRef) throws DiscountException
	{
		return getCalcPriorities(evcId, callId, orderId, evcType, saleId, dcRef);
	}
	/*SLE.usp_CalcExtraValues*/
	private static int getCalcPriorities(String evcId, String callId, int orderId, int evcType, int saleId, int dcRef) throws DiscountException
	{
		int prizeType, disType, discountId;
		double totalDiscountApplied = 0l;

		Cursor cPriority = EVCHeaderSDSDBAdapter.getInstance().getCalcPriority(evcId);
		if(cPriority != null && cPriority.moveToFirst())
		{
			do{
				prizeType = cPriority.getInt(1);
				disType = cPriority.getInt(2);
				discountId = cPriority.getInt(3);

				if ( (prizeType == 1) || (prizeType == 5) || (prizeType == 6))
				{
					PromotionApplyStatuteOnEVCItemPreviewSDSV3.applyStatuteOnEVCItemPreview(evcId, callId, discountId, orderId, prizeType, evcType, saleId, dcRef);
					//todo ??? @IgnorOnHand
				}
				else if(disType == 2)
                {
                    updateDiscountBasedOnPriority1(evcId, cPriority.getInt(0), discountId);
                    updateDiscountBasedOnPriority2(evcId, cPriority.getInt(0), discountId);
                    updateAddAmountBasedOnPriority(evcId, cPriority.getInt(0), discountId);
					if (GlobalVariables.getBackOfficeVersion() !=  BackOfficeVersion.SDS16
						&& prizeType == 7) {
						totalDiscountApplied = EVCItemStatutesSDSDBAdapter.getInstance().updateDiscountApplied(evcId, discountId, totalDiscountApplied);
					}
                    updateDiscountBasedOnPriority3(evcId, cPriority.getInt(0), discountId);
                }
			}while(cPriority.moveToNext());

			int prizeCalcType;
			if (evcType == 2 || evcType ==  10 || evcType == 12){
				prizeCalcType =  DiscountCustomerOldInvoiceDetailDBAdapter.getInstance().getPrizeCalcType(saleId);
			}
			else
				prizeCalcType = (GlobalVariables.getPrizeCalcType() ? 1 :0);

			if (prizeCalcType == 1)
				EVCHeaderSDSDBAdapter.getInstance().updateDisAdd(evcId);
		}
		return 0;
	}
	
	private static void updateDiscountBasedOnPriority1(String evcId, int priority, int discountId)
	{
		EVCItemStatutesSDSDBAdapter.getInstance().updateDiscountBasedOnPriority1(evcId, priority, discountId);
	}
	
	private static void updateDiscountBasedOnPriority2(String evcId, int priority, int discountId)
	{
		EVCItemStatutesSDSDBAdapter.getInstance().updateDiscountBasedOnPriority2(evcId, priority, discountId);
	}
	
	private static void updateAddAmountBasedOnPriority(String evcId, int priority, int discountId)
	{
		EVCItemStatutesSDSDBAdapter.getInstance().updateAddAmountBasedOnPriority(evcId, priority, discountId);
	}
	
	private static void updateDiscountBasedOnPriority3(String evcId, int priority, int discountId)
	{
		EVCItemStatutesSDSDBAdapter.getInstance().updateDiscountBasedOnPriority3(evcId, priority, discountId);
	}
}
