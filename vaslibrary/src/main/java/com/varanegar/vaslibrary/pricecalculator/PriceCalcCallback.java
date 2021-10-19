package com.varanegar.vaslibrary.pricecalculator;

/**
 * Created by A.Torabi on 9/19/2017.
 */
public interface PriceCalcCallback {
    void onSucceeded();

    void onFailed(String error);
}
