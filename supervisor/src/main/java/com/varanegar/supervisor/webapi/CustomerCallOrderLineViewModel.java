package com.varanegar.supervisor.webapi;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */
@Table
public class CustomerCallOrderLineViewModel extends BaseModel {
    public UUID CustomerCallOrderUniqueId;
    public UUID ProductUniqueId;
    public UUID ProductUniqueId2;
    @Column
    public String ProductName;
    @Column
    public String ProductCode;
    @Column
    public Currency UnitPrice;
    public UUID PriceUniqueId;
    public String Comment;
    public UUID FreeReasonUniqueId;
    @Column
    public String FreeReasonName;
    public UUID ProductTypeUniqueId;
    public UUID StockUniqueId;
    @Column
    public String StockName;
    public UUID BackOfficeOrderUniqueId;
    @Column
    public int BackOfficeOrderRef;
    @Column
    public int BackOfficeOrderNo;
    @Column
    public Currency RequestAmount;
    public Currency RequestAdd1Amount;
    public Currency RequestAdd2Amount;
    public Currency RequestTaxAmount;
    public Currency RequestChargeAmount;
    public Currency RequestDis1Amount;
    public Currency RequestDis2Amount;
    public Currency RequestDis3Amount;
    @Column
    public Currency RequestNetAmount;
    public Currency RequestOtherDiscountAmount;
    public boolean RequestQtyFlag;
    @Column
    public String RequestQty;
    public boolean TotalPriceFlag;
    @Column
    public Currency RequestTotalPrice;
    public boolean RequestUnitFlag;
    @Column
    public String RequestUnit;
    public Currency InvoiceAmount;
    public Currency InvoiceAdd1Amount;
    public Currency InvoiceAdd2Amount;
    public Currency InvoiceTaxAmount;
    public Currency InvoiceChargeAmount;
    public Currency InvoiceOtherDiscountAmount;
    public Currency InvoiceDis1Amount;
    public Currency InvoiceDis2Amount;
    public Currency InvoiceDis3Amount;
    public Currency InvoiceNetAmount;
    public Currency InvoiceAddAmount;
    public Currency InvoiceDiscountAmount;
    public boolean InvoiceQtyFlag;
    public String InvoiceQty;
    public List<CustomerCallOrderLineOrderQtyDetailViewModel> CustomerCallOrderLineOrderQtyDetails;
    public List<CustomerCallOrderLineOrderQtyDetailViewModel> ProductUnits;
    public List<CustomerCallOrderLineInvoiceQtyDetailViewModel> CustomerCallOrderLineInvoiceQtyDetails;
    public boolean IsRequestFreeItem;
    public boolean IsEditedBySupervisor = false;

    public CustomerCallOrderLineViewModel() {
        CustomerCallOrderLineOrderQtyDetails = new ArrayList<>();
        CustomerCallOrderLineInvoiceQtyDetails = new ArrayList<>();
    }
}
