package com.varanegar.vaslibrary.webapi.distribution;


import com.google.gson.annotations.SerializedName;
import com.varanegar.vaslibrary.model.call.CallInvoiceLineModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnRequestModel;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyRequestModel;
import com.varanegar.vaslibrary.model.call.ReturnLinesRequestModel;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerCallModel;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQtyModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallOrderLineBatchQtyDetailViewModel;

import java.util.List;

/**
 * Created by A.Jafarzadeh on 2/24/2018.
 */

public class DistributionTourViewModel {

    public List<DistributionCustomerCallModel> DistributionCustomerCalls;

    public List<CustomerCallInvoiceModel> DistributionCustomerCallOrders;

    public List<CallInvoiceLineModel> DistributionCustomerCallOrderLines;

    public List<InvoiceLineQtyModel> DistributionCustomerCallOrderLineOrderQtyDetails;

    public List<InvoiceLineBatchQtyDetailViewModel> DistributionCustomerCallOrderLineBatchQtyDetails;

    public List<CustomerCallReturnRequestModel> DistributionCustomerCallReturns;

    public List<ReturnLinesRequestModel> DistributionCustomerCallReturnLines;

    public List<ReturnLineQtyRequestModel> DistributionCustomerCallReturnLineRequestQtyDetails;

    @SerializedName("agentMobail")
    public String AgentMobile;
}
