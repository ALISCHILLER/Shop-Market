package com.varanegar.supervisor.webapi.model_old;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerCallReturnLineQtyDetailViewModel {
    public UUID CustomerCallReturnLineUniqueId;
    public UUID ProductUnitUniqueId;
    public BigDecimal Qty;
    public String UnitName;
    public BigDecimal ConvertFactor;
}
