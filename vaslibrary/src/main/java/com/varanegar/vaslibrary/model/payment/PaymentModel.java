package com.varanegar.vaslibrary.model.payment;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 12/9/2017.
 */
@Table
public class PaymentModel extends BaseModel {
    @Column
    @NotNull
    public UUID CustomerId;
    @Column
    @NotNull
    public Date Date;
    @Column
    @NotNull
    public Currency Amount;
    @Column
    public UUID CityId;
    @Column
    public UUID BankId;
    @Column
    public String CheckAccountNumber;
    @Column
    public String CheckNumber;
    @Column
    public String Ref;
    @Column
    @NotNull
    public UUID PaymentType;
    @Column
    public int RowNo;
    @Column
    public Date ChqDate;
    @Column
    public String ChqAccountName;
    @Column
    public String ChqBranchName;
    @Column
    public String ChqBranchCode;
    @Column
    public String SayadNumber;
}
