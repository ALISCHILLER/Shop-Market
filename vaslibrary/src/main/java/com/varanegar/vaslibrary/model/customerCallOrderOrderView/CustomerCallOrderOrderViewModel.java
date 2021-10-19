package com.varanegar.vaslibrary.model.customerCallOrderOrderView;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 21/02/2017.
 */
@Table
public class CustomerCallOrderOrderViewModel extends BaseModel {
    @Column
    public UUID OrderUniqueId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public Currency UnitPrice;
    @Column
    public Currency UserPrice;
    @Column
    public UUID ProductId;
    @Column
    public UUID FreeReasonId;
    @Column
    public String FreeReasonName;
    @Column
    public String Qty;
    @Column
    public String ConvertFactor;
    @Column
    public String ProductUnitId;
    @Column
    public String UnitId;
    @Column
    public String UnitName;
    @Column
    public BigDecimal TotalQty;
    @Column
    public boolean IsFreeItem;
    @Column
    public boolean IsRequestFreeItem;
    @Column(isEnum = true)
    public EmphasisType EmphaticType;
    @Column
    public BigDecimal EmphaticProductCount;
    @Column
    public UUID PriceId;
    @Column
    public Currency RequestAmount;
    @Column
    public Currency RequestAdd1Amount;
    @Column
    public Currency RequestAdd2Amount;
    @Column
    public Currency RequestAddOtherAmount;
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
    @Column
    public Currency InvoiceAmount;
    @Column
    public Currency InvoiceAdd1Amount;
    @Column
    public Currency InvoiceAdd2Amount;
    @Column
    public Currency InvoiceTaxAmount;
    @Column
    public Currency InvoiceChargeAmount;
    @Column
    public Currency InvoiceOtherDiscountAmount;
    @Column
    public Currency InvoiceDis1Amount;
    @Column
    public Currency InvoiceDis2Amount;
    @Column
    public Currency InvoiceDis3Amount;
    @Column
    public BigDecimal RemainedAfterReservedQty;
    @Column
    public BigDecimal OnHandQty;
    @Column
    public BigDecimal ProductTotalOrderedQty;
    @Column
    public BigDecimal OrderPoint;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public BigDecimal RequestBulkQty;
    @Column
    public UUID RequestBulkQtyUnitUniqueId;
    @Column
    public boolean IsPromoLine;
    @Column
    public Currency PromotionPrice;
    @Column
    public int SortId;
    @Column
    public String BatchOnHandQty;
    @Column
    public String BatchNo;
    @Column
    public String ExpDate;
    @Column
    public String BatchRef;
    @Column
    public boolean HasAllocation;
    @Column
    public BigDecimal OriginalTotalQty;
    @Column
    public int PayDuration;
    @Column
    public int RuleNo;
    @Column
    public int ProductPayDuration;
    @Column
    public double TotalWeight;
    @Column
    public BigDecimal TotalQtyBulk;
    @Column
    public UUID EmphasisRuleId;
    @Nullable
    @Column
    public UUID EditReasonId;
    @Column
    public String Description;
}
