package com.varanegar.vaslibrary.model.customerpathview;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customer.CustomerModel;

import java.util.UUID;

@Table
public class UnConfirmedCustomerPathViewModel extends CustomerModel
{
    @Column
    public UUID VisitTemplatePathId;
    @Column
    public Currency TotalOrderAmount;
    @Column
    public int PathRowId;
    @Column
    public String CallType;
    @Column
    public boolean ConfirmStatus;
}
