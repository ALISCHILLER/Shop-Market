package varanegar.com.discountcalculatorlib.viewmodel;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceLineQtyModelData {

    public UUID OrderLineUniqueId;
    public String Vrkme;
    public UUID UniqueId;
    public BigDecimal Qty;
    public UUID ProductUnitId;
    public UUID UnitUniqueId;
}
