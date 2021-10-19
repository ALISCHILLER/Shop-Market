package com.varanegar.vaslibrary.model.targetsalemanproductview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 1/4/2018.
 */
@Table
public class TargetSaleManProductViewModel extends BaseModel {
    @Column
    public String ProductName;
    @Column
    public int Target;
    @Column
    public int AchievedInPeriod;
    @Column
    public double AchievedInDayPercent;
    @Column
    public double AchievedInPeriodPercent;
    @Column
    public int SaleAverageInDaysRemain;
}
