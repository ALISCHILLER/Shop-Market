package varanegar.com.discountcalculatorlib.viewmodel;

import java.util.List;
import java.util.Map;

/**
 * Created by m.aghajani on 8/2/2016.
 */
public interface OnNewReturnPrizeAddListener {

    void onPrizeAdding(Map<Integer, DiscountCallReturnData> seperatedItems, Map<Integer, List<DiscountCallReturnLineData>> finalResult);
}
