package com.varanegar.vaslibrary.manager.oldinvoicemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailTempModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailTempModelRepository;

/**
 * Created by A.Jafarzadeh on 8/15/2018.
 */

public class CustomerOldInvoiceDetailTempManager extends BaseManager<CustomerOldInvoiceDetailTempModel> {
    public CustomerOldInvoiceDetailTempManager(@NonNull Context context) {
        super(context, new CustomerOldInvoiceDetailTempModelRepository());
    }
}
