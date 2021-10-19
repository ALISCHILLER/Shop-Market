package com.varanegar.vaslibrary.model.productorderview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 18/02/2017.
 */
@Table
public class ProductOrderViewModel extends BaseModel {
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public String ProductGroupId;
    @Column
    @NotNull
    public UUID CustomerId;
    @Column
    public BigDecimal TotalQty;
    @Column
    public String Qty;
    @Column
    public int IsForSale;
    @Column
    public String ConvertFactor;
    @Column
    public String UnitName;
    @Column
    public String ProductUnitId;
    @Column
    public Currency RequestAmount;
    @Column
    public Currency Price;
    @Column
    public UUID PriceId;
    @Column
    public Currency UserPrice;
    @Column
    public boolean IsFreeItem;
    @Column
    public boolean IsRequestFreeItem;
    @Column(isEnum = true)
    public EmphasisType EmphaticType;
    @Column
    public BigDecimal EmphaticProductCount;
    @Column
    public BigDecimal RemainedAfterReservedQty;
    @Column
    public BigDecimal OnHandQty;
    @Column
    public BigDecimal ProductTotalOrderedQty;
    @Column
    public BigDecimal OrderPoint;
    @Column
    public int Average;
    @Column
    public int InvoiceCount;
    @Column
    public BigDecimal DangerQty;
    @Column
    public BigDecimal WarningQty;
    @Column
    public int EmphaticPriority;
    @Column
    public String PrizeComment;
    @Column
    public boolean CustomerInventoryIsAvailable;
    @Column
    public BigDecimal CustomerInventoryTotalQty;
    /**
     * Colon separated uuids. Each product could be in several catalogs
     */
    @Column
    public String CatalogId;
    @Column
    public BigDecimal RemainedQty;
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
    public int PayDuration;
    @Column
    public int CatalogOrderOf;
    @Column
    public BigDecimal RequestBulkQty;
    @Column
    public BigDecimal TotalQtyBulk;
    @Column
    public String Description;
    @Column
    public UUID OrderLineId;
    @Override
    public String toString() {
        return ProductName + " (" + ProductCode + ") ";
    }
}
