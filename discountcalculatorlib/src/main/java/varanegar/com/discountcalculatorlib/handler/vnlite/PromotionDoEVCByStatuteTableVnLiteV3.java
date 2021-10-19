package varanegar.com.discountcalculatorlib.handler.vnlite;

public class PromotionDoEVCByStatuteTableVnLiteV3 {

    /*usp_DoEVCbyStatuteTable*/
    public static void doEVCByStatuteTable(String evcId, String callId, int evcType, String orderDate)
    {

        PromotionFillStatuteByIdVnLiteV3.fillStatuteById(evcId, orderDate, evcType);
        PromotionApplyStatuteOnEVCItemPreviewVnLiteV3.applyStatuteOnEVCItemPreview(evcId, callId);
        PromotionCalcExtraValuesVnLiteV3.calcExtraValues(evcId);
        PromotionApplyStatuteOnEVCItemVnLiteV3.applyStatuteOnEVCItem(evcId);
        PromotionApplyStatuteOnEVCVnLiteV3.applyStatuteOnEVC(evcId);
    }
}
