package com.varanegar.vaslibrary.model.call;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table(name = "CustomerCallReturnLines")
public class ReturnLineBaseModel extends BaseModel {
    @Column
    @NotNull
    public UUID ReturnUniqueId;
    @Column
    @NotNull
    public UUID ProductUniqueId;
    @Column
    public Currency RequestUnitPrice;
    @Column
    public boolean IsFreeItem;
    @Column
    public Currency TotalRequestAdd1Amount;
    @Column
    public Currency TotalRequestAdd2Amount;
    @Column
    public Currency TotalRequestAddOtherAmount ;
    @Column
    public Currency TotalRequestTax;
    @Column
    public Currency TotalRequestCharge;
    @Column
    public Currency TotalRequestNetAmount;
    @Column
    public Currency TotalRequestDis1Amount ;
    @Column
    public Currency TotalRequestDis2Amount ;
    @Column
    public Currency TotalRequestDis3Amount ;
    @Column
    public Currency TotalRequestDisOtherAmount ;
    @Column
    public int SortId;
    @Column
    public int IndexInfo;
    @Column
    public BigDecimal Weight;
    @Column
    public UUID ReturnProductTypeId;
    @Column
    public UUID ReturnReasonId;
    @Column
    public BigDecimal RequestBulkQty;
    @Column
    public UUID RequestBulkUnitId;
    @Column
    public boolean IsPromoLine;
    @Column
    public UUID StockId;
    @Column
    public String ReferenceNo;
    @Column
    public String ItemRef;
}
