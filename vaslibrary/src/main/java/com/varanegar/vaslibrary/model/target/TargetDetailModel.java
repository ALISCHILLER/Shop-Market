package com.varanegar.vaslibrary.model.target;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */
@Table
public class TargetDetailModel extends BaseModel {
    @Column
    public UUID TargetMasterUniqueId;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public UUID PersonnelUniqueId;
    @Column
    public int CustomerCount;
    @Column
    public int VisitCount;
    @Column
    public int SuccessfulVisitCount;
    @Column
    public int OrderCount;
    @Column
    public int OrderItemCount;
    @Column
    public Currency OrderAmount;
}
