package com.varanegar.vaslibrary.model.target;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */
@Table
public class TargetMasterModel extends BaseModel {
    @Column
    public String Title;
    @Column
    public UUID TargetTypeUniqueId;
    @Column
    public UUID TargetBaseUniqueId;
    @Column
    public UUID AmountTypeUniqueId;
    @Column
    public Date FromDate;
    @Column
    public String FromPDate;
    @Column
    public Date ToDate;
    @Column
    public String ToPDate;
    @Column
    public String SaleZoneUniqueIds;
    @Column
    public String StateUniqueIds;
    @Column
    public String CityUniqueIds;
    @Column
    public String CustomerActivityUniqueIds;
    @Column
    public String CustomerCategoryUniqueIds;
    @Column
    public String CustomerLevelUniqueIds;
    @Column
    public String CenterUniqueIds;
    @Column
    public String SaleOfficeUniqueIds;
    @Column
    public String ManufacturerUniqueIds;
    @Column
    public String ProductMainGroupUniqueIds;
    @Column
    public String ProductSubGroupUniqueIds;
}
