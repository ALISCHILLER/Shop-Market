package com.varanegar.vaslibrary.manager.newmanager.commodity_rationing;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


@Table
public class CommodityRationingViewModel extends BaseModel {
    @Column
    public String quotasName;
    @Column
    public String fromDate;
    @Column
    public String toDate;
    @Column
    public String dataCenterOwnerIds;
    @Column
    public String customerLevelIds;
    @Column
    public String customerActivityIds;
    @Column
    public String customerCategoryIds;
    @Column
    public String personnelIds;
    @Column
    public int quotasType;
}
