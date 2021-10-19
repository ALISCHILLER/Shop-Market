package com.varanegar.vaslibrary.manager.customeroldinvoicedissale;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleModel;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleModelRepository;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleVnLtModel;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleVnLtModelRepository;

/**
 * Created by A.Razavi on 1/21/2018.
 */

public class CustomerOldInvoiceDisSaleVnLtManager extends BaseManager<CustomerOldInvoiceDisSaleVnLtModel> {
    public CustomerOldInvoiceDisSaleVnLtManager(@NonNull Context context) {
        super(context, new CustomerOldInvoiceDisSaleVnLtModelRepository());
    }
}
