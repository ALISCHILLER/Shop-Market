package com.varanegar.vaslibrary.webapi.tour;

import androidx.annotation.Nullable;

import com.varanegar.framework.validation.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/1/2017.
 */

public class SyncGetCustomerCallViewModel {
    @NotNull
    public UUID CustomerUniqueId;
    @NotNull
    public UUID VisitStatusUniqueId;
    @Nullable
    public UUID NoSaleReasonUniqueId;
    @Nullable
    public UUID UndeliveredReasonUniqueId;
    @Nullable
    public UUID ReturnReasonUniqueId;
    @NotNull
    public String Description;
    public Date CallDate;
    public Date StartTime;
    public Date EndTime;
    public Date DeliveryDate;
    public long VisitDuration;
    @NotNull
    public double Longitude;
    @NotNull
    public double Latitude;
    public boolean IsNewCustomer;
    public UUID VisitTemplatePathUniqueId;
    public List<SyncGetCustomerCallOrderViewModel> CustomerCallOrders;
    public List<SyncGetCustomerCallPictureViewModel> CustomerCallPictures;
    public List<SyncGetCustomerCallStockLevelViewModel> CustomerCallStockLevels;
    public List<SyncGetCustomerCallQuestionnaireViewModel> CustomerCallQuestionnaires;
    public List<SyncGetCustomerCallReturnViewModel> CustomerCallReturns;
    public List<SyncGetCustomerCallPaymentViewModel> CustomerCallPayments;
    public List<SyncGetCustomerCallCatalogViewModel> CustomerCallCatalogs;

}
