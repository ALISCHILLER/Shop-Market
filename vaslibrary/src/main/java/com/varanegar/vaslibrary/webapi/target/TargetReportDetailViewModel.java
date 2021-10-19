package com.varanegar.vaslibrary.webapi.target;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.List;
import java.util.UUID;

/**
 * Created by g.aliakbar on 10/03/2018.
 */
@Table
public class TargetReportDetailViewModel extends BaseModel {

    @Column
    public String SubjectTitle;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String CustomerCode;
    @Column
    public String CustomerName;
    @Column
    public double TargetAmount;
    @Column
    public double AchievementAmount;
    @Column
    public float AchievementAmountPercent;
    @Column
    public float AchievementAmountPercentInDay;
    @Column
    public double SaleAverage;
    @Column
    public double TargetQty;
    @Column
    public double AchievementQty;
    @Column
    public float AchievementQtyPercent;
    @Column
    public float AchievementQtyPercentInDay;
    @Column
    public double AverageQty;


}
