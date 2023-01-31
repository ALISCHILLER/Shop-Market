package com.varanegar.vaslibrary.manager.newmanager.customerLastBill;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


@Table
public class CustomerLastBillModel extends BaseModel {
    @Column
    public String ProductName ;
    @Column
    public String CustomerCode ;
    @Column
    public String ProductCode ;
    @Column
    public String CustomerName ;
    @Column
    public String ProductUnit_EA ;
    @Column
    public String ProductQTY_EA ;
    @Column
    public String ProductUnit_KAR ;
    @Column
    public String ProductQTY_KAR ;
    @Column
    public String Type ;
    @Column
    public String TypeSum ;
    @Column
    public String ProductUnit_SUM_EA ;
    @Column
    public String ProductQTY_SUM_EA ;
    @Column
    public String ProductQTY_SUM_KAR ;
    @Column
    public String ProductUnit_SUM_KAR ;
}
