package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerQtyDetailViewModel
{
    @NotNull
    public UUID UniqueId;

    @NotNull
    public UUID CustomerCallOrderLineUniqueId;

    @NotNull
    public UUID ProductUnitUniqueId;

    @NotNull
    public double Qty;
}
