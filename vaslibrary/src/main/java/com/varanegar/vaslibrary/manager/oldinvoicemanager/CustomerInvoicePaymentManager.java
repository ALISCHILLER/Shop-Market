package com.varanegar.vaslibrary.manager.oldinvoicemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerInvoicePayment;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerInvoicePaymentModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerInvoicePaymentModelRepository;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;

import java.util.UUID;

/**
 * Created by A.Torabi on 12/19/2017.
 */

public class CustomerInvoicePaymentManager extends BaseManager<CustomerInvoicePaymentModel> {
    public CustomerInvoicePaymentManager(@NonNull Context context) {
        super(context, new CustomerInvoicePaymentModelRepository());
    }

    public void add(@NonNull OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel) throws ValidationException, DbException {
        Query query = new Query();
        query.from(CustomerInvoicePayment.CustomerInvoicePaymentTbl)
                .whereAnd(Criteria.equals(CustomerInvoicePayment.CustomerId, oldInvoiceHeaderViewModel.CustomerUniqueId.toString())
                        .and(Criteria.equals(CustomerInvoicePayment.InvoiceId, oldInvoiceHeaderViewModel.UniqueId.toString())));
        CustomerInvoicePaymentModel customerInvoicePaymentModel = getItem(query);
        if (customerInvoicePaymentModel == null) {
            customerInvoicePaymentModel = new CustomerInvoicePaymentModel();
            customerInvoicePaymentModel.UniqueId = UUID.randomUUID();
            customerInvoicePaymentModel.CustomerId = oldInvoiceHeaderViewModel.CustomerUniqueId;
            customerInvoicePaymentModel.InvoiceId = oldInvoiceHeaderViewModel.UniqueId;
        }
        insertOrUpdate(customerInvoicePaymentModel);
    }

    public void removeAll(UUID customerId) throws DbException {
        delete(Criteria.equals(CustomerInvoicePayment.CustomerId, customerId.toString()));
    }
}
