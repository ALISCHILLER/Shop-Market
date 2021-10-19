package com.varanegar.vaslibrary.model.onhandqty;

import java.math.BigDecimal;

/**
 * Created by g.aliakbar on 07/03/2018.
 */

public class OnHandQtyStock {
    public BigDecimal OnHandQty;
    public BigDecimal RemainedAfterReservedQty;
    public BigDecimal TotalQty;
    public BigDecimal ProductTotalOrderedQty;
    public BigDecimal OrderPoint;
    public String UnitNames;
    public String ConvertFactors;
    public boolean HasAllocation;
}
