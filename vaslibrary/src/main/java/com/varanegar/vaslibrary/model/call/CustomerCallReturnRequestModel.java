package com.varanegar.vaslibrary.model.call;

import com.varanegar.processor.annotations.Table;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CustomerCallReturnRequestModel extends ReturnBaseModel {

    public CustomerCallReturnModel convertRequestToReturnModel() {
        CustomerCallReturnModel returnModel = new CustomerCallReturnModel();
        returnModel.UniqueId = this.UniqueId;
        returnModel.CustomerUniqueId = this.CustomerUniqueId;
        returnModel.ReturnTypeUniqueId = this.ReturnTypeUniqueId;
        returnModel.PersonnelUniqueId = this.PersonnelUniqueId;
        returnModel.LocalPaperNo = this.LocalPaperNo;
        returnModel.BackOfficeDistId = this.BackOfficeDistId;
        returnModel.BackOfficeInvoiceId = this.BackOfficeInvoiceId;
        returnModel.BackOfficeInvoiceNo = this.BackOfficeInvoiceNo;
        returnModel.BackOfficeInvoiceDate = this.BackOfficeInvoiceDate;
        returnModel.ReturnRequestBackOfficeId = this.ReturnRequestBackOfficeId;
        returnModel.ReturnRequestBackOfficeDate = this.ReturnRequestBackOfficeDate;
        returnModel.ReturnRequestBackOfficeNo = this.ReturnRequestBackOfficeNo;
        returnModel.Comment = this.Comment;
        returnModel.DCRefSDS = this.DCRefSDS;
        returnModel.SaleOfficeRefSDS = this.SaleOfficeRefSDS;
        returnModel.StartTime = this.StartTime;
        returnModel.EndTime = this.EndTime;
        returnModel.DealerUniqueId = this.DealerUniqueId;
        returnModel.IsFromRequest = this.IsFromRequest;
        returnModel.ShipToPartyCode=this.ShipToPartyCode;
        returnModel.ShipToPartyUniqueId=this.ShipToPartyUniqueId;
        return returnModel;
    }
}
