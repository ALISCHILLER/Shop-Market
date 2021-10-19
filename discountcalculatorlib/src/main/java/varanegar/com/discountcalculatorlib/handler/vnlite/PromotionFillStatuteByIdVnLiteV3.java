package varanegar.com.discountcalculatorlib.handler.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.DisSaleVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemStatutesVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempSummaryVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCTempVnLiteDBAdapter;

public class PromotionFillStatuteByIdVnLiteV3 {

    /*usp_FillEVCStatuteByID*/
    public static void fillStatuteById(String evcId, String orderDate, int evcType)
    {
        extractValidDiscount(evcId, orderDate, evcType);
        filterInvalidQty(evcId);
        fillEVCItemStatute(evcId);
        //comment in new version 9703
        //deleteInvalidItemStatuse(evcId);
    }

    private static void extractValidDiscount(String evcId, String orderDate, int evcType)
    {
        EVCTempVnLiteDBAdapter.getInstance().fillEVCTemp(evcId, orderDate, evcType);
    }

    private static void filterInvalidQty(String evcId)
    {
        EVCTempSummaryVnLiteDBAdapter.getInstance().fillEVCItemSummary(evcId);
    }

    private static void fillEVCItemStatute(String evcId)
    {
        EVCItemStatutesVnLiteDBAdapter.getInstance().fillEVCTemp(evcId);
    }

//    private static void fillEVCSummaryFinal(String evcId, Customer customer) {
//
//        EVCTempSummaryFinalVnLiteDBAdapter.getInstance().fillEVCItemSummaryFinal(evcId);
//    }


    private static void deleteInvalidItemStatuse(String evcId) {

        EVCItemStatutesVnLiteDBAdapter.getInstance().deleteInvalidItemStatuse(evcId);
    }


}

