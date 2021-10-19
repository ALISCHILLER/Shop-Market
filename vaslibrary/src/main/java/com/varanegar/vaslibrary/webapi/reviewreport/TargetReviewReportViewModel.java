package com.varanegar.vaslibrary.webapi.reviewreport;

import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by e.hashemzadeh on 20/06/21.
 */

@Table
public class TargetReviewReportViewModel extends ReviewReportViewModel {
    @Column
    public String calcDate;
    @Column
    public String condition;
    @Column
    public String type;
    @Column
    public String title;
    @Column
    public double isSold;
    @Column
    public double target;
    @Column
    public double targetToDate;
    @Column
    public double achievementToDate;
    @Column
    public double achievement;
    @Column
    public double dailyPlan;
    @Column
    public int achievementStimate;
    @Column
    public int calculationPeriodId;
    @Column
    public double remainTarget;
    @Column
    public int dayOrderNumber;
    @Column
    public int remainDays;
    @Column
    public double PoursantToDate;
    @Column
    public double PoursantTillDate;
}
