package com.varanegar.vaslibrary.model.CustomerPaymentTypesView;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/7/2017.
 */

@Table
public class CustomerPaymentTypesViewModel extends BaseModel {
    @Column
    @NotNull
    public UUID CustomerUniqueId;
    @Column
    public int PaymentTypeOrderGroupRef;
    @Column
    @NotNull
    public UUID PaymentTypeOrderGroupUniqueId;
    @Column
    public String PaymentTypeOrderGroupName;
    @Column
    public int CheckDebit;
    @Column
    public String PaymentTypeOrderName;
    @Column
    public int CheckCredit;
    @Column
    public int BackOfficeId;

    @Override
    public String toString() {
        return PaymentTypeOrderName;
    }
}
