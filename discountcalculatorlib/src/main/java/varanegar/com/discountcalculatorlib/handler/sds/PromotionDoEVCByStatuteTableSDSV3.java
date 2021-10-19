package varanegar.com.discountcalculatorlib.handler.sds;


import varanegar.com.discountcalculatorlib.utility.DiscountException;

public class PromotionDoEVCByStatuteTableSDSV3 {

	public static int doEVCByStatuteTable(String evcId, String callId, int orderId, int disType, String orderDate, int evcType, String orderTypeId, int saleId, int orderNo,int dCRef) throws DiscountException {

		PromotionFillStatuteByIdSDSV3.fillStatuteById(evcId, orderId, orderDate, orderTypeId, saleId, orderNo);

		//String Message = PromotionFillStatuteByIdSDSV3.CheckValidEvcStatutes(evcId);

		if(disType == 2 || disType == 4)
		{
			int retStat = PromotionCalcExtraValuesSDSV3.calcExtraValues(evcId, callId, orderId, evcType, saleId, dCRef);
			if (retStat != 0)
				return retStat;

			if(disType == 2)
			{
				PromotionApplyStatuteOnEVCItemSDSV3.applyStatuteOnEVCItem(evcId);
				PromotionApplyStatuteOnEVCSDSV3.applyStatuteOnEVC(evcId);
			}


			DoDiscountForAmani();
			//TODO ASAL in 4.16.73
			//CheckValidPrizePreSell(ID);

		}
		return 0;
	}


	//EXEC SLE.usp_DoDiscountForAmani @ID
	private static void DoDiscountForAmani(){
		//TODO ASAL in 5.0

	}
}
