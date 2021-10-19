package com.varanegar.vaslibrary.model.targetcustomerproductview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 1/4/2018.
 */
@Table
public class TargetCustomerProductViewModel extends BaseModel {
    @Column
    public String CustomerName;
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
