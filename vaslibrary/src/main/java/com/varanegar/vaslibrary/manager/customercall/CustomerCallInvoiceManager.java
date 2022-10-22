package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoice;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 3/29/2017.
 */

public class CustomerCallInvoiceManager extends BaseManager<CustomerCallInvoiceModel> {
    public CustomerCallInvoiceManager(Context context) {
        super(context, new CustomerCallInvoiceModelRepository());
    }

    public List<CustomerCallInvoiceModel> getCustomerCallInvoices(int backOfficeOrderId) {
        Query query = new Query();
        query.from(CustomerCallInvoice.CustomerCallInvoiceTbl).
                whereAnd(Criteria.equals(CustomerCallInvoice.BackOfficeOrderId, backOfficeOrderId));
        return getItems(query);
    }
    public List<CustomerCallInvoiceModel> getCustomerCallInvoices(UUID customerUniqueId) {
        Query query = new Query();
        query.from(CustomerCallInvoice.CustomerCallInvoiceTbl).
                whereAnd(Criteria.equals(CustomerCallInvoice.CustomerUniqueId, customerUniqueId));
        return getItems(query);
    }

    public CustomerCallInvoiceModel getCustomerCallInvoice(UUID uniqueId) {
        Query query = new Query();
        query.from(CustomerCallInvoice.CustomerCallInvoiceTbl).whereAnd(Criteria.equals(CustomerCallInvoice.UniqueId, uniqueId.toString()));
        return getItem(query);
    }


    public List<CustomerCallInvoiceModel> getAll() {
        Query query = new Query();
        query.from(CustomerCallInvoice.CustomerCallInvoiceTbl);
        return getItems(query);
    }

    public List<String> getAllDealerName() {
        Query query = new Query();
        query.distinct().select(Projection.column(CustomerCallInvoice.DealerName));
        query.from(CustomerCallInvoice.CustomerCallInvoiceTbl);
        return VaranegarApplication.getInstance().getDbHandler().getString(query);
    }
}






