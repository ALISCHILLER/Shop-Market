package com.varanegar.vaslibrary.model.customeremphaticpackageview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/16/2017.
 */
@Table
public class CustomerEmphaticPackageViewModel extends BaseModel {
    @Column
    public UUID RuleId;
    @Column
    public UUID TypeId;
    @Column
    public UUID CustomerId;
    @Column
    public String FromDate;
    @Column
    public String ToDate;
    @Column
    public Date WarningDate;
    @Column
    public Date DangerDate;
    @Column
    public BigDecimal PackageCount;
    @Column
    public String Title;
}
