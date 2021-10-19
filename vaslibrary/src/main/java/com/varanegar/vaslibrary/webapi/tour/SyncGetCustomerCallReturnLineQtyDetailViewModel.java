package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerCallReturnLineQtyDetailViewModel
{
    @NotNull
    public UUID UniqueId;

    @NotNull
    public UUID CustomerCallReturnLineUniqueId;

    @NotNull
    public UUID ProductUnitUniqueId;

    @NotNull
    public double Qty;

    @NotNull
    public double UnitPrice;
}
