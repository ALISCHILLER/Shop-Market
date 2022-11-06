package com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Soleymani on 11/05/2022.
 */
@Table
public class CheckCustomerCreditModel extends BaseModel {

    @Column
    public  String customerBackOfficeCode;
    @Column
    public Currency CustomerCreditLimit;
    @Column
    public Currency customerUsedCredit;
    @Column
    public Currency customerRemainCredit;
}
