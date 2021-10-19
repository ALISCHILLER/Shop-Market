package com.varanegar.vaslibrary.manager.customeroldinvoicedissale;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleModel;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleModelRepository;

/**
 * Created by A.Jafarzadeh on 1/21/2018.
 */

public class CustomerOldInvoiceDisSaleManager extends BaseManager<CustomerOldInvoiceDisSaleModel> {
    public CustomerOldInvoiceDisSaleManager(@NonNull Context context) {
        super(context, new CustomerOldInvoiceDisSaleModelRepository());
    }
}
