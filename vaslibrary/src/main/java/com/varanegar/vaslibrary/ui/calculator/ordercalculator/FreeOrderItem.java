package com.varanegar.vaslibrary.ui.calculator.ordercalculator;

import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by A.Torabi on 1/10/2018.
 */

public class FreeOrderItem {
    public List<DiscreteUnit> discreteUnits;
    public BaseUnit bulkUnit;
    public FreeReasonModel freeReason;
    public BigDecimal getTotalQty() {
        BigDecimal qty = BigDecimal.ZERO;
        for (DiscreteUnit discreteUnit :
                discreteUnits) {
            qty = qty.add(discreteUnit.getQty().multiply(new BigDecimal((discreteUnit.ConvertFactor))));
        }
        return qty;
    }
}
