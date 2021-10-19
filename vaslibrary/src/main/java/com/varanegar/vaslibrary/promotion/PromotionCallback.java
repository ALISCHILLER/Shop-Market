package com.varanegar.vaslibrary.promotion;

/**
 * Created by A.Torabi on 2/16/2019.
 */

public interface PromotionCallback {
    void onSuccess(CustomerCallOrderPromotion data);
    void onFailure(String error);
    void onProcess(String msg);
}
