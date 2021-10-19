package varanegar.com.discountcalculatorlib.callback;

import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;

/**
 * Created by A.Torabi on 2/16/2019.
 */

public interface DiscountHandlerOrderCallback {
    void onSuccess(DiscountCallOrderData callOrderData);

    void onFailure(String error);
}
