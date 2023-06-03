package varanegar.com.discountcalculatorlib.helper;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 2/21/2017.
 */

public class BaseUnitData {
    public UUID ProductUnitId;
    public String Name;
    public String Unit;
    public double value = 0;
    public boolean IsDefault;
    public boolean Readonly;
    public BigDecimal getQty(){
        return new BigDecimal(value);
    }
}
