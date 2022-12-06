package com.varanegar.vaslibrary.manager.newmanager.commodity_rationing;

import com.varanegar.framework.database.model.BaseModel;

import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import java.util.UUID;

@Table
public class Product_RationingViewModel extends BaseModel {
    @Column
    public UUID quotasUniqueId;
    @Column
    public UUID productUniuqeId;
    @Column
    public UUID productUnitUniuqeId;
    @Column
    public String productCode;
    @Column
    public String productName;
    @Column
    public int quantity;
}
