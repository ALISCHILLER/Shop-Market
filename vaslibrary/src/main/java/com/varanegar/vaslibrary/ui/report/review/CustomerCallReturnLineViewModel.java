package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/10/2018.
 */
@Table
public class CustomerCallReturnLineViewModel extends BaseModel {
    public UUID ProductUniqueId;
    @Column
    public String ProductCode;
    @Column
    public String ProductName;
    public UUID PriceUniqueId;
    @Column
    public Currency UnitPrice;
    public String Comment;
    public UUID BackOfficeReturnOrderUniqueId;
    public int BackOfficeReturnOrderRef;
    public int BackOfficeReturnOrderNo;
    public UUID BackOfficeReturnInvoiceUniqueId;
    public int BackOfficeReturnInvoiceRef;
    public int BackOfficeReturnInvoiceNo;
    @Column
    public Currency TotalReturnAmount;
    public Currency TotalReturnAdd1;
    public Currency TotalReturnAdd2;
    public Currency TotalReturnAddAmount;
    public Currency TotalReturnDis1;
    public Currency TotalReturnDis2;
    public Currency TotalReturnDis3;
    public Currency TotalReturnDiscount;
    public Currency TotalReturnTax;
    public Currency TotalReturnCharge;
    public Currency TotalReturnSupAmount;
    @Column
    public Currency TotalReturnNetAmount;
    public UUID StockUniqueId;
    @Column
    public String StockName;
    public UUID ReturnReasonUniqueId;
    @Column
    public String ReturnReasonName;
    public UUID ReturnProductTypeUniqueId;
    @Column
    public String ReturnProductTypeName;
    public boolean ReturnQtyFlag;
    @Column
    public String ReturnQty;
    public boolean ReturnUnitFlag;
    @Column
    public String ReturnUnit;
    public boolean IsFreeItem;
    public List<CustomerCallReturnLineQtyDetailViewModel> CustomerCallReturnLineQtyDetails;
}
