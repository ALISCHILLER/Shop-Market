package varanegar.com.discountcalculatorlib.handler.sds.v4_16;

import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCHeaderSDSDBAdapter;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.EVCItemSDSDBAdapter;

/**
 * Created by m.aghajani on 8/7/2016.
 */
public class PromotionDoEVCBySpecialValueV3 {
    public PromotionDoEVCBySpecialValueV3() {
    }

    public static void doEVCBySpecialValue(String evcId) {

        resetValues(evcId);
        updateDis1(evcId);
        updateDis2(evcId);
        updateDis3(evcId);
        updateAdd1(evcId);
        updateAdd2(evcId);
        updateNetAmountWithSpecialValues(evcId);
    }

    private static void resetValues(String evcId) {
        EVCItemSDSDBAdapter.getInstance().resetValuesForSpecialValues(evcId);
    }

    private static void updateDis1(String evcId)
    {
        EVCItemSDSDBAdapter.getInstance().updateDis1WithSpecialValues(evcId);
        EVCHeaderSDSDBAdapter.getInstance().updateDis1WithSpecialValues(evcId);
    }

    private static void updateDis2(String evcId)
    {
        EVCItemSDSDBAdapter.getInstance().updateDis2WithSpecialValues(evcId);
        EVCHeaderSDSDBAdapter.getInstance().updateDis2WithSpecialValues(evcId);
    }

    private static void updateDis3(String evcId) {
        EVCItemSDSDBAdapter.getInstance().updateDis3WithSpecialValues(evcId);
        EVCHeaderSDSDBAdapter.getInstance().updateDis3WithSpecialValues(evcId);
    }

    private static void updateAdd1(String evcId)
    {
        EVCItemSDSDBAdapter.getInstance().updateAdd1WithSpecialValues(evcId);
        EVCHeaderSDSDBAdapter.getInstance().updateAdd1WithSpecialValues(evcId);
    }

    private static void updateAdd2(String evcId)
    {
        EVCItemSDSDBAdapter.getInstance().updateAdd2WithSpecialValues(evcId);
        EVCHeaderSDSDBAdapter.getInstance().updateAdd2WithSpecialValues(evcId);
    }

    private static void updateNetAmountWithSpecialValues(String evcId)
    {
        EVCItemSDSDBAdapter.getInstance().updateNetAmountWithSpecialValues(evcId);
    }
}
