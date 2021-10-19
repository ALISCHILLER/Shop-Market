package com.varanegar.vaslibrary.model.customercallreturnview;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table
public class CustomerCallReturnBaseViewModel extends BaseModel {
    @Column
    public UUID ReturnUniqueId;
    @Column
    public UUID InvoiceId;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String ReturnProductTypeId;
    @Column
    public String ReturnReasonId;
    @Column
    public String ProductName;
    @Column
    public UUID ProductId;
    @Column
    public String ProductCode;
    @Column
    public String ConvertFactor;
    @Column
    public String ProductUnitId;
    @Column
    public String UnitName;
    @Column
    public BigDecimal TotalReturnQty;
    @Column
    public Currency RequestUnitPrice;
    @Column
    public Currency TotalRequestNetAmount;
    @Column
    public Currency TotalRequestAmount;
    @Column
    public BigDecimal InvoiceQty;
    @Column
    public String SaleNo;
    @Column
    public String Qty;
    @Column
    public UUID StockId;
    @Column
    public boolean IsPromoLine;
    @Column
    public boolean IsFromRequest;
    @Column
    public BigDecimal OriginalTotalReturnQty; // for return from request
    @Column
    public String Comment;
    @Column
    public UUID DealerUniqueId;
    @Column
    public boolean IsCancelled;
    @Column
    public String ReferenceNo;
    @Nullable
    @Column
    public UUID EditReasonId;
}
