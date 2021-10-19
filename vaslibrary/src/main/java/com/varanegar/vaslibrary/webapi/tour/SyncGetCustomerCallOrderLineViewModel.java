package com.varanegar.vaslibrary.webapi.tour;

import androidx.annotation.Nullable;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerCallOrderLineViewModel {
    @NotNull
    public UUID UniqueId;
    @NotNull
    public UUID CustomerCallOrderUniqueId;
    @NotNull
    public UUID ProductUniqueId;
    @NotNull
    public double UnitPrice;
    @NotNull
    public UUID PriceUniqueId;
    @NotNull
    public String Comment;
    @Nullable
    public UUID FreeReasonUniqueId;
    @Nullable
    public UUID StockUniqueId;
    @NotNull
    public double RequestAmount;
    @NotNull
    public double RequestAdd1Amount;
    @NotNull
    public double RequestAdd2Amount;
    @NotNull
    public double RequestOtherAddAmount;
    @NotNull
    public double RequestTaxAmount;
    @NotNull
    public double RequestChargeAmount;
    @NotNull
    public double RequestDis1Amount;
    @NotNull
    public double RequestDis2Amount;
    @NotNull
    public double RequestDis3Amount;
    @NotNull
    public double RequestOtherDiscountAmount;
    @NotNull
    public double InvoiceAmount;
    @NotNull
    public double InvoiceAdd1Amount;
    @NotNull
    public double InvoiceAdd2Amount;
    @NotNull
    public double InvoiceOtherAddAmount;
    @NotNull
    public double InvoiceTaxAmount;
    @NotNull
    public double InvoiceChargeAmount;
    @NotNull
    public double InvoiceOtherDiscountAmount;
    @NotNull
    public double InvoiceDis1Amount;
    @NotNull
    public double InvoiceDis2Amount;
    @NotNull
    public double InvoiceDis3Amount;
    public List<SyncGetCustomerCallOrderLineBatchQtyDetailViewModel> CustomerCallOrderLineBatchQtyDetails;
    public List<SyncGetCustomerCallOrderLineBatchQtyDetailViewModel> CustomerCallInvoiceLineBatchQtyDetails;
    public List<SyncGetCustomerQtyDetailViewModel> CustomerCallOrderLineOrderQtyDetails;
    public List<SyncGetCustomerQtyDetailViewModel> CustomerCallOrderLineInvoiceQtyDetails;
    public List<SyncGetCustomerCallOrderLinePromotionViewModel> CustomerCallOrderLinePromotions;
    @NotNull
    public boolean IsRequestFreeItem;
    @NotNull
    public int SortId;
    @NotNull
    public boolean IsRequestPrizeItem;
    @NotNull
    public int PayDuration;
    @NotNull
    public int RuleNo;
    @Nullable
    public UUID EditReasonUniqueId;
    public String Description;
}
