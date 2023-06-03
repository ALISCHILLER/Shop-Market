package varanegar.com.discountcalculatorlib.helper;

import java.math.BigDecimal;
import java.util.UUID;

public class DiscreteUnitData extends BaseUnitData {

    public double ConvertFactor;

    public double getTotalQty() {
        return value * ConvertFactor;
    }
}
