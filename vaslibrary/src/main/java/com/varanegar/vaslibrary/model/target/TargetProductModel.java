package com.varanegar.vaslibrary.model.target;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */
@Table
public class TargetProductModel extends BaseModel {
    @Column
    public UUID TargetDetailUniqueId;
    @Column
    public UUID ProductUniqueId;
    @Column
    public UUID ProductUnitUniqueId;
    @Column
    public double Amount;
    @Column
    public double Qty;
}
