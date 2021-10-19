package varanegar.com.discountcalculatorlib.handler.vnlite;

import android.database.Cursor;

import java.math.BigDecimal;
import java.util.ArrayList;

import varanegar.com.discountcalculatorlib.dataadapter.discount.DiscountVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemStatutesVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;

public class PromotionCalcExtraValuesVnLiteV3 {

	public static void calcExtraValues(String evcId)
	{
		getCalcPriorities(evcId);
	}

	/*[usp_CalcExtraValues]*/
	private static void getCalcPriorities(String evcId)
	{

		//BigDecimal totalAmount = EVCItemVnLiteDBAdapter.getInstance().getSumAmount(evcId);

		BigDecimal totalAmount = BigDecimal.ZERO;
		BigDecimal totalDiscount = BigDecimal.ZERO;

		totalDiscount = DiscountVnLiteDBAdapter.getInstance().getTotalDiscountAmount(evcId);
		totalAmount = DiscountVnLiteDBAdapter.getInstance().getTotalAmount(evcId);

		BigDecimal testDiscount = DiscountVnLiteDBAdapter.getInstance().getTestDiscountAmount(evcId, totalAmount);

		Cursor cPriority = EVCHeaderVnLiteDBAdapter.getInstance().getCalcPriority(evcId);
		if(cPriority != null && cPriority.moveToFirst())
		{
			do{
				updateDiscountBasedOnPriority1(evcId, cPriority.getInt(0), totalAmount);
				updateAddAmountBasedOnPriority(evcId, cPriority.getInt(0));
                //EVCItem update already exists in applyStatuteOnEVCItem an this is repititive even in VnLite SP
               updateDiscountAndAddAmountOnEVCItem(evcId);

			}while(cPriority.moveToNext());
		}

		if (totalDiscount.compareTo(testDiscount) != 0) {
			EVCItemVnLiteDBAdapter.getInstance().updateDiscountAmount(evcId, totalDiscount , testDiscount);
			EVCItemStatutesVnLiteDBAdapter.getInstance().updateDiscountAmount(evcId, totalDiscount , testDiscount);
		}
	}
	
	private static void updateDiscountBasedOnPriority1(String evcId, int priority, BigDecimal totalAmount)
	{
		EVCItemStatutesVnLiteDBAdapter.getInstance().updateDiscountBasedOnPriority1(evcId, priority, totalAmount);
	}

    private static void updateAddAmountBasedOnPriority(String evcId, int priority)
    {
        EVCItemStatutesVnLiteDBAdapter.getInstance().updateAddAmountBasedOnPriority(evcId, priority);
    }

    private static void updateDiscountAndAddAmountOnEVCItem(String evcId)
    {
        EVCItemVnLiteDBAdapter.getInstance().updateDiscountAndAddAmount(evcId);
    }
	
	private static void updateDiscountBasedOnPriority2(String evcId, int priority)
	{
		EVCItemStatutesVnLiteDBAdapter.getInstance().updateDiscountBasedOnPriority2(evcId, priority);
	}
	
	private static void updateDiscountBasedOnPriority3(String evcId, int priority)
	{
		EVCItemStatutesVnLiteDBAdapter.getInstance().updateDiscountBasedOnPriority3(evcId, priority);
	}
}
