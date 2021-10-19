package com.varanegar.vaslibrary.model.customerCallOrderOrderView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/10/2018.
 */
@Table
public class OrderViewModel extends BaseModel {
    @Column
    public UUID OrderUniqueId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public Currency UnitPrice;
    @Column
    public UUID ProductId;
    @Column
    public String FreeReasonId;
    @Column
    public String FreeReasonName;
    @Column
    public String Qty;
    @Column
    public BigDecimal ConvertFactor;
    @Column
    public String ProductUnitId;
    @Column
    public String UnitName;
    @Column
    public String AllConvertFactors;
    @Column
    public String AllUnitNames;
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
    public BigDecimal OnHandQty;
    @Column
    public BigDecimal RemainedAfterReservedQty;
    @Column
    public BigDecimal ProductTotalOrderedQty;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String InventoryQty;
    @Column
    public String InventoryUnitName;
    @Column
    public BigDecimal RequestBulkQty;
    @Column
    public BigDecimal TotalQtyBulk;
    @Column
    public String Description;
}
