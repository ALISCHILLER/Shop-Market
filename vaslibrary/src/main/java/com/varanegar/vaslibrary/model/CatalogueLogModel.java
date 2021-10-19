package com.varanegar.vaslibrary.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

@Table
public class CatalogueLogModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public UUID CatalogTypeUniqueId;
    @Column
    public UUID EntityId;
    @Column
    public Date StartTime;
    @Column
    public Date EndTime;
}
