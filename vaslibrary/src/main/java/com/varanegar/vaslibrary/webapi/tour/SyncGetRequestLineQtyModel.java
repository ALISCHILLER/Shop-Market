package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 3/27/2018.
 */

public class SyncGetRequestLineQtyModel {
    @NotNull
    public UUID UniqueId;
    public BigDecimal Qty;
    @NotNull
    public UUID ProductUnitId;
}
