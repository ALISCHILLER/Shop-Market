package com.varanegar.vaslibrary.webapi.distribution;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 2/24/2018.
 */

public class DistributionCustomerCallOrderLineViewModel {
    public UUID Id;

    public UUID CustomerCallOrderUniqueId;

    public UUID ProductUniqueId;

    public int RowIndex;

    public UUID PriceUniqueId;

    public double UnitPrice;

    public UUID FreeReasonUniqueId;

    public double Weight;

    public double Volume;

    public boolean IsRequestPrizeItem;

    public double RequestAmount;

    public double RequestAdd1Amount;

    public double RequestAdd2Amount;

    public double RequestDis1Amount;

    public double RequestDis2Amount;

    public double RequestDis3Amount;

    public double RequestDiscountAmount;

    public double RequestTaxAmount;

    public double RequestChargeAmount;

    public double InvoiceDis1;

    public double InvoiceDis2;

    public double InvoiceDis3;
}
