package varanegar.com.discountcalculatorlib.handler.sds.v4_16;


import varanegar.com.discountcalculatorlib.utility.DiscountException;

public class PromotionDoEVCByStatuteTableSDSV3 {

	public static int doEVCByStatuteTable(String evcId, String callId, int orderId, int disType, String orderDate, int evcType, String orderTypeId, int saleId, int orderNo) throws DiscountException {

		PromotionFillStatuteByIdSDSV3.fillStatuteById(evcId, orderId, orderDate, orderTypeId, saleId, orderNo);

		//String Message = PromotionFillStatuteByIdSDSV3.CheckValidEvcStatutes(evcId);

		if(disType == 2 || disType == 4)
		{
			int retStat = PromotionCalcExtraValuesSDSV3.calcExtraValues(evcId, callId, orderId, evcType, saleId, 0);
			if (retStat != 0)
				return retStat;

			if(disType == 2)
			{
				PromotionApplyStatuteOnEVCItemSDSV3.applyStatuteOnEVCItem(evcId);
				PromotionApplyStatuteOnEVCSDSV3.applyStatuteOnEVC(evcId);
			}

			//TODO ASAL 4.16.73
			//CheckValidPrizePreSell(ID);

		}
		return 0;
	}
}
