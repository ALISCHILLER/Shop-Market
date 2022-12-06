package com.varanegar.vaslibrary.model.newmodel.commodity_rationing;

import java.util.List;
import java.util.UUID;

public class CommodityRationingModel  {
    public UUID uniqueId;
    public String quotasName;
    public String fromDate;
    public String toDate;
    public String dataCenterOwnerIds;
    public String customerLevelIds;
    public String customerActivityIds;
    public String customerCategoryIds;
    public String personnelIds;
    public int quotasType;
    public List<Product_RationingModel> products ;
}
