package com.varanegar.vaslibrary.model.invoiceinfo;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/26/2018.
 */
@Table
public class InvoicePaymentInfoModel extends BaseModel {
    @Column
    public UUID InvoiceId;
    @Column
    public UUID PaymentId;
    @Column
    public Currency Amount;
}
