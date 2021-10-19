package com.varanegar.vaslibrary.model.CustomerPaymentType;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 05/15/2017.
 */
@Table
public class CustomerPaymentTypeOrderModel extends BaseModel
{
    @Column
    @NotNull
    public UUID PaymentTypeUniqueId;
    @Column
    @NotNull
    public String PaymentTypeUniqueName;
    @Column
    public int CheckDebit;
    @Column
    public int CheckCredit;
    @Column
    @NotNull
    public UUID BackOfficeId;
}
