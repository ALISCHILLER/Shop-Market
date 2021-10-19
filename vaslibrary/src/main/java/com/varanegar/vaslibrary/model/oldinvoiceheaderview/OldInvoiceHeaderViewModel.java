package com.varanegar.vaslibrary.model.oldinvoiceheaderview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by s.foroughi on 14/01/2017.
 */
@Table
public class OldInvoiceHeaderViewModel extends BaseModel{

    @Column
    public String InvoiceDate;
    @Column
    public long InvoiceNo;
    @Column
    public int InvoiceRef;
    @Column
    public Currency Amount;
    @Column
    public Currency PayAmount;
    @Column
    public Currency RemAmount;
    @Column
    public Currency DiscountAmount;
    @Column
    public Currency TaxAmount;
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String Address;
    @Column
    public String CustomerName;
    @Column
    public String CustomerCode;
    @Column
    public boolean HasPayment;
    @Column
    public UUID PaymentTypeOrderUniqueId;
    @Column
    public UUID DealerId;

}
