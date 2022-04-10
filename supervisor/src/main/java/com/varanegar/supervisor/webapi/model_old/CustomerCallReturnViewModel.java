package com.varanegar.supervisor.webapi.model_old;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.supervisor.webapi.model_old.CustomerCallReturnLineViewModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerCallReturnViewModel extends BaseModel {
    public String ReturnTypeName;
    public UUID ReturnTypeUniqueId;
    public String ReturnReasonName;
    public UUID ReturnReasonUniqueId;
    public UUID SubSystemTypeUniqueId;
    public String ReturnRequestRejectReasonName;
    public String BackOfficeInvoiceNo;
    public UUID ReturnRequestRejectReasonUniqueId;
    public String Comment;
    public String DistributerName;
    public String CustomerCallReturnTypeName;
    public UUID CustomerCallReturnTypeUniqueId;
    public Date ReturnStartTime;
    public String ReturnStartPTime;
    public Date ReturnEndTime;
    public String ReturnEndPTime;
    public Date SellReturnDate;
    public String SellReturnPDate;
    public String CustomerName;
    public String CustomerCode;
    public String StoreName;
    public String CallDate;
    public String BackOfficeReturnOrderNoCollection;
    public String BackOfficeReturnInvoiceNoCollection;
    public String OperationPDate;
    public Currency TotalReturnNetAmount;
    public Currency TotalReturnAmount;
    public Currency TotalReturnOtherDiscount;
    public Currency TotalReturnDis1;
    public Currency TotalReturnDis2;
    public Currency TotalReturnDis3;
    public Currency TotalReturnDiscount;
    public Currency TotalReturnCharge;
    public Currency TotalReturnTax;
    public Currency TotalReturnAdd1;
    public Currency TotalReturnAdd2;
    public Currency TotalReturnAddAmount;
    public List<CustomerCallReturnLineViewModel> OrderReturnLines;
    public boolean IsCanceled;

}
