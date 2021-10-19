package com.varanegar.vaslibrary.model.customerinventory;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */
@Table
public class CustomerInventoryViewModel extends BaseModel {

    @Column
    public  UUID CustomerId;
    @Column
    public UUID ProductId;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public String Qty;
    @Column
    public String UnitName;
    @Column
    public BigDecimal TotalQty;
    @Column
    public boolean IsAvailable;
    @Column
    public boolean IsSold;
}
