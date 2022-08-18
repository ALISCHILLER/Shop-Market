package com.varanegar.vaslibrary.model.customerinventory;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */
@Table
public class CustomerInventoryModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public UUID CustomerId;
    @Column
    public boolean IsAvailable;
    @Column
    public boolean IsSold;
    @Column
    public String FactoryDate;
}
