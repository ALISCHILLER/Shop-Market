package com.varanegar.vaslibrary.webapi.tour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerCallPaymentViewModel {
    @NonNull
    public UUID UniqueId;
    @NotNull
    public UUID SettlementTypeUniqueId;
    @NotNull
    public double Amount;
    @NotNull
    public String ChqNo;
    public Date ChqDate;
    @NotNull
    public Date Date;
    @Nullable
    public UUID ChqBankUniqueId;
    @NotNull
    public String ChqBranchCode;
    @NotNull
    public String ChqBranchName;
    @Nullable
    public UUID ChqCityUniqueId;
    @NotNull
    public String ChqAccountName;
    @NotNull
    public String ChqAccountNo;
    @NotNull
    public String FollowNo;
    @NotNull
    public String SayadNo;
    public List<SyncGetCustomerCallPaymentDetailViewModel> PaymentDetails = new ArrayList<>();
}

