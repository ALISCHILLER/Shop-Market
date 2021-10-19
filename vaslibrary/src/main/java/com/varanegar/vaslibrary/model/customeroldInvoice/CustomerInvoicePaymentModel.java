package com.varanegar.vaslibrary.model.customeroldInvoice;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 12/19/2017.
 */
@Table
public class CustomerInvoicePaymentModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public UUID InvoiceId;
}
