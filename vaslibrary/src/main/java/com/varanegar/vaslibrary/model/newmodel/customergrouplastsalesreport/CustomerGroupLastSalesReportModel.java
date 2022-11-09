package com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

@Table
public class CustomerGroupLastSalesReportModel extends BaseModel {

    @Column
    public String ProductBackOfficeCode ;

    public String CustomerGroup ;
    @Column
    public String CustomerCode ;
    @Column
    public String CustomerGroupTXT ;
    @Column
    public String CustomerActivity;
    @Column
    public String CustomerActivityTXT ;

    public double NETWeight ;

    public double NETCount_CA ;

    public double NETCount_EA ;

    public String ProductGroupName  ;

    public int ProductGroupCode ;

    public double WeightProductGroup ;



    public double Reshteh ;

    public double Formi ;

    public double Jumbo ;

    public double Ashianeh ;

    public double Lazania ;

    public double PodrKeik ;

    public double ReshtehAsh ;

    public double Ard ;

    public double HaftGhaleh ;

    public double vegan ;

    public double protoeen ;


    @Column
    public String LastSaleProductGroup ;
    @Column
    public String LastSaleProductGroup_Reshteh ;
    @Column
    public String LastSaleProductGroup_Formi ;
    @Column
    public String LastSaleProductGroup_Jumbo ;
    @Column
    public String LastSaleProductGroup_Ashianeh ;
    @Column
    public String LastSaleProductGroup_Lazania ;
    @Column
    public String LastSaleProductGroup_PodrKeik ;
    @Column
    public String LastSaleProductGroup_ReshtehAsh ;
    @Column
    public String LastSaleProductGroup_Ard ;
    @Column
    public String LastSaleProductGroup_HaftGhaleh ;
    @Column
    public String LastSaleProductGroup_vegan ;
    @Column
    public String LastSaleProductGroup_protoeen ;
    
}
