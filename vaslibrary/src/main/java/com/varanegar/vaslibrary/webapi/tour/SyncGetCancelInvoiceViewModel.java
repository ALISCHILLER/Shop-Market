package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCancelInvoiceViewModel
{
    @NotNull
    public UUID TourUniqueId;
    @NotNull
    public UUID CustomerUniqueId;
    @NotNull
    public String Comment;
    @NotNull
    public double Amount;
}
