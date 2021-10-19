package varanegar.com.discountcalculatorlib.handler.vnlite;

import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCHeaderVnLiteDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.vnlite.EVCItemVnLiteDBAdapter;

/**
 * Created by A.Razavi on 6/16/2018.
 */

public class PromotionDoEVCBySpecialValueVnLiteV3 {

    /*usp_DoEVCbySpecialValue*/
    public static void doEVCBySpecialValue(String evcId) {
        EVCItemVnLiteDBAdapter.getInstance().resetEvc(evcId);
        EVCItemVnLiteDBAdapter.getInstance().updateDis1(evcId);
        EVCItemVnLiteDBAdapter.getInstance().updateAdd1(evcId);
        EVCItemVnLiteDBAdapter.getInstance().updateNetAmount(evcId);
        EVCHeaderVnLiteDBAdapter.getInstance().updateByItems(evcId);
    }
}
