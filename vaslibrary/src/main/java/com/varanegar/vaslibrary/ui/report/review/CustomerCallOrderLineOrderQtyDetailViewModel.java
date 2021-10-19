package com.varanegar.vaslibrary.ui.report.review;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerCallOrderLineOrderQtyDetailViewModel {
    public UUID CustomerCallOrderLineUniqueId;
    public UUID ProductUnitUniqueId;
    public String UnitName;
    public BigDecimal Qty;
    public BigDecimal ConvertFactor;
    public boolean IsRemoved;
}
