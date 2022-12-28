package com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


@Table
public class CustomerSumMoneyAndWeightReportModel extends BaseModel {

    @Column
    public String Money_Sum ;
    @Column
    public String Weight_Sum ;
    @Column
    public String CustomerId ;

}
