package com.varanegar.vaslibrary.model.customeremphaticproduct;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2017.
 */
@Table
public class CustomerEmphaticProductModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public UUID ProductId;

    @Column(isEnum = true)
    public EmphasisType Type;
    @Column
    public int ProductCount;
    @Column
    public Date WarningDate;
    @Column
    public Date DangerDate;
    @Column
    public UUID EmphasisRuleId;
    @Column
    public UUID TypeLawUniqueId;
}
