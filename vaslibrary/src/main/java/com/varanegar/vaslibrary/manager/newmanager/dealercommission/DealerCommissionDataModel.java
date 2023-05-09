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
    public String LastUpdate;
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
    @Column
    public int FinalTarget ;
    @Column
    public int FinalSales ;
    @Column
    public int FinalAchive ;

    @Column
    public int waferTarget ;
    @Column
    public int cakeTarget ;
    @Column
    public int biscuitTarget ;
    @Column
    public int oilTarget ;
    @Column
    public int waferSales ;
    @Column
    public int cakeSales ;
    @Column
    public int biscuitSales ;
    @Column
    public int oilSales ;
    @Column
    public int waferPayment ;
    @Column
    public int cakePayment ;
    @Column
    public int biscuitPayment ;
    @Column
    public int oilPayment ;

    @Column
    public float waferAchive ;
    @Column
    public float cakeAchive ;
    @Column
    public float biscuitAchive ;
    @Column
    public float oilAchive ;


   // -----
   @Column
    public int oilTarget_Frying ;
    @Column
    public int oilTarget_Mix ;
    @Column
    public int oilTarget_Corn ;
    @Column
    public int oilSales_Frying ;
    @Column
    public int oilSales_Mix ;
    @Column
    public int oilSales_Corn ;
    @Column
    public int oilPayment_Frying ;
    @Column
    public int oilPayment_Mix ;
    @Column
    public int oilPayment_Corn ;
    @Column
    public int finalPayment_CON ;
    @Column
    public int finalPayment_OIL ;
    @Column
    public int coverageRatePaymentCON ;
    @Column
    public int coverageRatePaymentIOL ;
    @Column
    public float oilAchive_Frying ;
    @Column
    public float oilAchive_Mix ;
    @Column
    public float oilAchive_Corn ;

}
