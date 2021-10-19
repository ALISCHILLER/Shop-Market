package com.varanegar.vaslibrary.ui.calculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by A.Torabi on 7/26/2017.
 */
public class CalculatorUnits {
    public CalculatorUnits(@NonNull List<DiscreteUnit> units, @Nullable BaseUnit bulkUnit) {
        this.discreteUnits = units;
        this.bulkUnit = bulkUnit;
    }

    @Nullable
    public BaseUnit getBulkUnit() {
        return bulkUnit;
    }

    @NonNull
    public List<DiscreteUnit> getDiscreteUnits() {
        return discreteUnits;
    }

    private BaseUnit bulkUnit;

    public void setUnits(@NonNull List<DiscreteUnit> discreteUnits,@Nullable BaseUnit bulkUnit) {
        this.discreteUnits = discreteUnits;
        this.bulkUnit = bulkUnit;
    }

    private List<DiscreteUnit> discreteUnits;

    public BigDecimal getTotalQty() {
        double totalQty = 0;
        for (DiscreteUnit discreteUnit :
                discreteUnits) {
            totalQty += discreteUnit.value * discreteUnit.ConvertFactor;
        }
        return new BigDecimal(totalQty);
    }
}
