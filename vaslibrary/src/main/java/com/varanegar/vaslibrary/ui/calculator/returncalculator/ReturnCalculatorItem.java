package com.varanegar.vaslibrary.ui.calculator.returncalculator;

import android.content.Context;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/3/2017.
 */
public class ReturnCalculatorItem extends BaseModel {

    private final Context context;
    public UUID ReturnUniqueId;

    public ReturnCalculatorItem(Context context, UUID returnType, String typeName, UUID returnReason, String reasonName) {
        this.context = context;
        this.returnType = returnType;
        this.returnReason = returnReason;
        this.typeName = typeName;
        this.reasonName = reasonName;
        discreteUnits = new ArrayList<>();
    }

    private ArrayList<DiscreteUnit> discreteUnits;

    @Nullable
    public BaseUnit getBulkUnit() {
        return bulkUnit;
    }

    private BaseUnit bulkUnit;

    public UUID getReturnType() {
        return returnType;
    }

    public UUID getReturnReason() {
        return returnReason;
    }

    public UUID returnType;
    public UUID returnReason;
    public String typeName;
    public String reasonName;

    @Override
    public String toString() {
        return typeName + " " + context.getString(R.string.because) + " " + reasonName + " " + context.getString(R.string.count) + " : " + getTotalQty().toString();
    }

    public BigDecimal getTotalQty() {
        BigDecimal qty = new BigDecimal(0);
        if (bulkUnit != null)
            return bulkUnit.getQty();
        for (DiscreteUnit discreteUnit :
                discreteUnits) {
            qty = qty.add(discreteUnit.getQty().multiply(new BigDecimal((discreteUnit.ConvertFactor))));
        }
        return qty;
    }

    public void setBulkUnit(BaseUnit bulkUnit) {
        this.bulkUnit = bulkUnit;
    }

    public void addDiscreteUnit(DiscreteUnit discreteUnit) {
        if (this.discreteUnits == null)
            discreteUnits = new ArrayList<>();
        this.discreteUnits.add(discreteUnit);
    }

    public ArrayList<DiscreteUnit> getDiscreteUnits() {
        if (this.discreteUnits == null)
            return new ArrayList<>();
        return discreteUnits;
    }
}
