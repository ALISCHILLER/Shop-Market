package com.varanegar.vaslibrary.ui.calculator;

/**
 * Created by atp on 2/20/2017.
 */

public class DiscreteUnit extends BaseUnit {
    public double ConvertFactor;

    public double getTotalQty() {
        return value * ConvertFactor;
    }
}
