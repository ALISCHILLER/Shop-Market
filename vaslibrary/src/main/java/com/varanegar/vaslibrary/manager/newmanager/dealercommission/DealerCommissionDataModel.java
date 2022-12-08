package com.varanegar.vaslibrary.manager.newmanager.dealercommission;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

@Table
public class DealerCommissionDataModel extends BaseModel {

    @Column
    public String CenterName;
    @Column
    public int Year;
    @Column
    public int Month;
    @Column
    public String DealerCode;
    @Column
    public String DealerName;
    @Column
    public int SpaghettiTarget;
    @Column
    public int ShapedTarget;
    @Column
    public int LasagnaTarget;
    @Column
    public int NestTarget ;
    @Column
    public int JumboTarget ;
    @Column
    public int CakePowderTarget ;
    @Column
    public int FlourTarget ;
    @Column
    public int PottageTarget ;
    @Column
    public int SpaghettiSales ;
    @Column
    public int ShapedSales ;
    @Column
    public int LasagnaSales ;
    @Column
    public int NestSales ;
    @Column
    public int JumboSales ;
    @Column
    public int CakePowderSales;
    @Column
    public int FlourSales ;
    @Column
    public int PottageSales ;
    @Column
    public float SpaghettiAchive;
    @Column
    public float ShapedAchive ;
    @Column
    public float LasagnaAchive ;
    @Column
    public float NestAchive ;
    @Column
    public float JumboAchive ;
    @Column
    public float CakePowderAchive ;
    @Column
    public float FlourAchive ;
    @Column
    public float PottageAchive ;
    @Column
    public int RankOnGlobal ;
    @Column
    public int RankOnTeam ;
    @Column
    public int SpaghettiPayment ;
    @Column
    public int ShapedPayment ;
    @Column
    public int LasagnaPayment ;
    @Column
    public int NestPayment ;
    @Column
    public int JumboPayment ;
    @Column
    public int CakePowderPayment ;
    @Column
    public int FlourPayment ;
    @Column
    public int PottagePayment ;
    @Column
    public int CoverageRatePayment ;
    @Column
    public int HitRatePayment ;
    @Column
    public int LpscPayment ;
    @Column
    public int FinalPayment ;
}
