package varanegar.com.discountcalculatorlib.callback;

import java.util.List;

import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

/**
 * Created by A.Torabi on 2/16/2019.
 */

public interface DiscountHandlerReturnCallback {
    void onSuccess(List<DiscountCallReturnLineData> callReturnLineData);

    void onFailure(String error);
}
