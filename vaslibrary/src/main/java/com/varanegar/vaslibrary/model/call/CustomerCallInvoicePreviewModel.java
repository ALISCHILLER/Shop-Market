package com.varanegar.vaslibrary.model.call;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 8/28/2018.
 */
@Table
public class CustomerCallInvoicePreviewModel extends BaseModel {
    @Column
    public UUID CustomerUniqueId;
    @Column
    public BigDecimal TotalQty;
    @Column
    public Currency TotalPrice;
    @Column
    public String BackOfficeOrderNo;
    @Column
    public int SaleNoSDS;
    @Column
    public String Comment;
    @Column(isEnum = true)
    public CustomerCallType CallType;
    @Column
    public boolean ConfirmStatus;
    @Column
    public boolean IsInvoice;
}
