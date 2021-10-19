package com.varanegar.vaslibrary.webapi.tour;

import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerCallReturnLineViewModel {

    public UUID UniqueId;
    public UUID StockUniqueId;
    public UUID ProductUniqueId;
    public boolean IsPrize;
    public UUID CallUniqueId;
    public UUID PriceUniqueId;
    public double UnitPrice;
    public double ReferenceQty;
    public Date ReferenceDate;
    public String CurrentType;
    public double CurrentQty;
    public String PackageName;
    public String Comment;
    public String ReferenceId;
    public String ReferenceNo;
    public double TotalRequestAmount;
    public double TotalRequestAdd1Amount;
    public double TotalRequestAdd2Amount;
    public double TotalRequestDiscount;
    public double TotalRequestTax;
    public double TotalRequestCharge;
    public double TotalRequestNetAmount;
    public double TotalRequestAddOtherAmount;
    public double RequestQty;

    public double TotalReturnAmount;
    public double TotalReturnAdd1;
    public double TotalReturnAdd2;
    public double TotalReturnAddOther;
    public double TotalReturnDis1;
    public double TotalReturnDis2;
    public double TotalReturnDis3;
    public double TotalReturnTax;
    public double TotalReturnCharge;
    public double TotalReturnNetAmount;
    public double TotalReturnDisOther;

    public UUID ReturnReasonUniqueId;
    public UUID ReturnProductTypeUniqueId;
    public double ReturnBulkQty;
    public UUID ReturnBulkUnitUniqueId;
    public boolean IsFreeItem;
    @Nullable
    public UUID EditReasonUniqueId;

    public List<SyncGetCustomerCallReturnLineQtyDetailViewModel> CustomerCallReturnLineQtyDetails;

}
