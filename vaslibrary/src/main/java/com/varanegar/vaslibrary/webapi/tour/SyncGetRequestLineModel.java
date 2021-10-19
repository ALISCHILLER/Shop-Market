package com.varanegar.vaslibrary.webapi.tour;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 3/27/2018.
 */

public class SyncGetRequestLineModel {
    @NotNull
    public UUID UniqueId;
    @NotNull
    public UUID ProductId;
    public double UnitPrice;
    public int RowIndex;
    public BigDecimal BulkQty;
    public UUID BulkQtyUnitUniqueId;
    public List<SyncGetRequestLineQtyModel> RequestItemLineQtyDetails = new ArrayList<>();
}
