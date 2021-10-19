package com.varanegar.vaslibrary.model.customerCallReturnLinesWithPromo;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CustomerCallReturnLinesWithPromoModel extends BaseModel {

    @Column
    @NotNull
    public UUID ProductUniqueId;
    @Column
    public int IsFreeItem;
    @Column
    public Currency RequestAmount;
    @Column
    public Currency RequestAdd1Amount;
    @Column
    public Currency RequestAdd2Amount;
    @Column
    public Currency RequestOtherDiscount;
    @Column
    public Currency RequestTax;
    @Column
    public Currency RequestCharge;
    @Column
    public int SortId;
    @Column
    public String PriceId;
    @Column
    public Currency UnitPrice;
    @Column
    public BigDecimal ReturnQty;
    @Column
    public BigDecimal ReturnBulkQty;
    @Column
    public UUID ReturnBulkUnitUniqueId;
    @Column
    public BigDecimal RequestBulkQty;
    @Column
    public UUID RequestBulkUnitUniqueId;
    @Column
    public Currency ReturnAmount;
    @Column
    public Currency ReturnAdd1Amount;
    @Column
    public Currency ReturnAdd2Amount;
    @Column
    public Currency ReturnDiscount;
    @Column
    public Currency ReturnTax;
    @Column
    public Currency ReturnCharge;
    @Column
    public String Comment;
    @Column
    public String ReturnProductTypeId;
    @Column
    public String ReferenceId;
    @Column
    public int ReferenceNo;
    @Column
    public UUID ReturnReasonUniqueId;
    @Column
    public BigDecimal ReferenceQty;
    @Column
    public Currency ReturnDis1Amount;
    @Column
    public Currency ReturnDis2Amount;
    @Column
    public Currency ReturnDis3Amount;
    @Column
    public Currency TotalReturnAddAmount;
    @Column
    public Currency TotalReturnSupAmount;
    @Column
    public Date ReferenceDate;





}
