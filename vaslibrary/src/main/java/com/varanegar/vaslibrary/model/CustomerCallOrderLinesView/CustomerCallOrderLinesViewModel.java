package com.varanegar.vaslibrary.model.CustomerCallOrderLinesView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

//import com.varanegar.framework.validation.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 13/02/2017.
 */
@Table
public class CustomerCallOrderLinesViewModel extends BaseModel {

    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    @Column
    public Currency UnitPrice;
    @Column
    public BigDecimal InvoiceQty;
    @Column
    public BigDecimal RequestQty;
    @Column
    public String LargeUnitName;
    @Column
    public String SmallUnitName;
    @Column
    public Currency InvoiceAmount;
    @Column
    public Currency RequestAmount;
    @Column
    @NotNull
    public UUID CustomerUniqueId;
    @Column
    public BigDecimal ReturnTotalQty;





}

