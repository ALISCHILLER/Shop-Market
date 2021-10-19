package com.varanegar.vaslibrary.model.customerCallOrderLinesInvoiceView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 21/02/2017.
 */
@Table
public class CustomerCallOrderLinesInvoiceViewModel extends BaseModel{

    @Column
    @NotNull
    public UUID OrderUniqueId;
    @Column
    @NotNull
    public UUID OrderLineUniqueId;
    @Column
    @NotNull
    public UUID ProductId;
    @Column
    public String ProductName;
    @Column
    public String Qty;
    @Column
    @NotNull
    public UUID ProductUnitId;
    @Column
    public String UnitName;

}
