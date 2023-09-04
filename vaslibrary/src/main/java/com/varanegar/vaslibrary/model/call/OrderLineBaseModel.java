package com.varanegar.vaslibrary.model.call;

import com.google.gson.annotations.SerializedName;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/1/2018.
 */

public class OrderLineBaseModel extends BaseModel {
    @Column
    @NotNull
    public UUID OrderUniqueId;
    @Column
    @NotNull
    public UUID ProductUniqueId;
    @Column
    public int SortId;
    @Column
    public boolean IsRequestFreeItem;
    @Column
    public BigDecimal RequestBulkQty;
    @Column
    public UUID RequestBulkQtyUnitUniqueId;
    @Column
    public Currency RequestOtherAddAmount ;
    @Column
    public Currency RequestAdd1Amount;
    @Column
    public Currency RequestAdd2Amount;
    @Column
    public Currency RequestTaxAmount;
    @Column
    public Currency RequestChargeAmount;
    @Column
    public Currency RequestDis1Amount;
    @Column
    public Currency RequestDis2Amount;
    @Column
    public Currency RequestDis3Amount;
    @Column
    public Currency RequestOtherDiscountAmount;
//    @Column
//    public Currency InvoiceAmount;
//    @Column
//    public Currency InvoiceAdd1Amount;
//    @Column
//    public Currency InvoiceAdd2Amount;
//    @Column
//    public Currency InvoiceTaxAmount;
//    @Column
//    public Currency InvoiceChargeAmount;
//    @Column
//    public Currency InvoiceOtherDiscountAmount;
//    @Column
//    public Currency InvoiceDis1Amount;
//    @Column
//    public Currency InvoiceDis2Amount;
//    @Column
//    public Currency InvoiceDis3Amount;
    @Column
    public UUID EVCId;
    @Column
    @SerializedName("freeReasonUniqueId")
    public UUID FreeReasonId;
    @Column
    public BigDecimal InvoiceBulkQty;
    @Column
    public UUID InvoiceBulkQtyUnitUniqueId;
    @Column
    public int DiscountRef;
    @Column
    public UUID DiscountId;
    @Column
    @SerializedName("isRequestPrizeItem")
    public boolean IsPromoLine;
    @Column
    public int PayDuration;
    @Column
    public int RuleNo;
    @Column
    public String Description;
    @Column
    public String saleS_ITEM ;
    @Column
    public String higheR_LEVEL;
    @Column
    public String cart ;
    @Column
    public String iteM_CATEGORY ;

}
