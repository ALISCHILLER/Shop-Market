package com.varanegar.vaslibrary.model.distribution;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 2/26/2018.
 */
@Table
public class DistributionCustomerCallModel extends BaseModel {
    @Column
    public UUID CustomerUniqueId;
    @Column
    public UUID DistributionUniqueId;
    @Column
    public String DistributionRef;
    @Column
    public String DistributionNo;
    @Column
    public String DistributionPDate;
    @Column
    public Date DistributionDate;
}
