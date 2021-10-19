package com.varanegar.vaslibrary.webapi.tour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerCallReturnViewModel
{
    public UUID UniqueId;
    public UUID CustomerCallUniqueId;
    public UUID ReturnTypeUniqueId;
    public UUID ReturnReasonUniqueId;
    public UUID CallActionStatusUniqueId;
    public UUID SubSystemTypeUniqueId;
    public UUID ReturnRequestRejectReasonUniqueId;
    public String Comment;
//    public double TotalReturnAmount;
//    public double TotalReturnOtherDiscount;
//    public double TotalReturnDis1;
//    public double TotalReturnDis2;
//    public double TotalReturnDis3;
//    public double TotalReturnTax;
//    public double TotalReturnAdd1;
//    public double TotalReturnAdd2;
//    public double TotalReturnCharge;
    public String DistributerName;
    public UUID CustomerCallReturnTypeUniqueId;
    public Date ReturnStartTime;
    public Date ReturnEndTime;
    public Date SellReturnDate;
    public String BackOfficeInvoiceNo;
    public Date CallDate;
    public Date OperationDate;
    public UUID DealerUniqueId;
    public int BackOfficeInvoiceRef;
//    public double TotalReturnNetAmount;
    public String BackOfficeInvoiceId;
    public boolean ReplacementRegistration;
    public List<SyncGetCustomerCallReturnLineViewModel> OrderReturnLines;
}
