package com.varanegar.vaslibrary.manager.oldinvoicemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderTempModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderTempModelRepository;

/**
 * Created by A.Jafarzadeh on 8/15/2018.
 */

public class CustomerOldInvoiceHeaderTempManager extends BaseManager<CustomerOldInvoiceHeaderTempModel> {
    public CustomerOldInvoiceHeaderTempManager(@NonNull Context context) {
        super(context, new CustomerOldInvoiceHeaderTempModelRepository());
    }
}
