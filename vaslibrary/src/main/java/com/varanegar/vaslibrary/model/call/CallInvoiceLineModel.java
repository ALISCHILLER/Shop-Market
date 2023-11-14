package com.varanegar.vaslibrary.model.call;

import com.google.gson.annotations.SerializedName;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table(name = "CustomerCallInvoiceLines")
public class CallInvoiceLineModel extends OrderLineBaseModel {
    @Column
    public Currency UnitPrice;
    @Column
    public Currency RequestAmount;
    @SerializedName("cPriceUniqueId")
    @Column
    public UUID CPriceUniqueId;
    @Column
    public Currency UserPrice;
    @Column
    public UUID PriceUniqueId;


    public CallOrderLineModel convertToCallOrderLine(){
        CallOrderLineModel callOrderLineModel = new CallOrderLineModel();
        callOrderLineModel.UniqueId = UniqueId;
        callOrderLineModel.OrderUniqueId = OrderUniqueId;
        callOrderLineModel.ProductUniqueId = ProductUniqueId;
        callOrderLineModel.SortId = SortId;
        callOrderLineModel.IsRequestFreeItem = IsRequestFreeItem;
        callOrderLineModel.RequestBulkQty = RequestBulkQty;
        callOrderLineModel.RequestBulkQtyUnitUniqueId = RequestBulkQtyUnitUniqueId;
        callOrderLineModel.RequestAdd1Amount = RequestAdd1Amount;
        callOrderLineModel.RequestAdd2Amount = RequestAdd2Amount;
        callOrderLineModel.RequestOtherAddAmount = RequestOtherAddAmount ;
        callOrderLineModel.RequestTaxAmount = RequestTaxAmount;
        callOrderLineModel.RequestChargeAmount = RequestChargeAmount;
        callOrderLineModel.RequestDis1Amount= RequestDis1Amount;
        callOrderLineModel.RequestDis2Amount = RequestDis2Amount;
        callOrderLineModel.RequestDis3Amount = RequestDis3Amount;
        callOrderLineModel.RequestOtherDiscountAmount = RequestOtherDiscountAmount;
        callOrderLineModel.EVCId  =EVCId;
        callOrderLineModel.FreeReasonId = FreeReasonId;
        callOrderLineModel.InvoiceBulkQty = InvoiceBulkQty;
        callOrderLineModel.InvoiceBulkQtyUnitUniqueId = InvoiceBulkQtyUnitUniqueId;
        callOrderLineModel.DiscountRef = DiscountRef;
        callOrderLineModel.DiscountId = DiscountId;
        callOrderLineModel.IsPromoLine = IsPromoLine;
        callOrderLineModel.PayDuration = PayDuration;
        callOrderLineModel.RuleNo = RuleNo;
        callOrderLineModel.cart = cart;
        callOrderLineModel.saleS_ITEM = saleS_ITEM;
        callOrderLineModel.higheR_LEVEL = higheR_LEVEL;
        callOrderLineModel.iteM_CATEGORY = iteM_CATEGORY;

        return callOrderLineModel;

    }
}
