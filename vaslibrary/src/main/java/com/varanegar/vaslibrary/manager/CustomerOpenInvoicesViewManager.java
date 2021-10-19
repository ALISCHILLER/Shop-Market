package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.CustomerOpenInvoicesView.CustomerOpenInvoicesView;
import com.varanegar.vaslibrary.model.CustomerOpenInvoicesView.CustomerOpenInvoicesViewModel;
import com.varanegar.vaslibrary.model.CustomerOpenInvoicesView.CustomerOpenInvoicesViewModelRepository;

import java.util.UUID;

/**
 * Created by s.foroughi on 25/01/2017.
 */
public class CustomerOpenInvoicesViewManager extends BaseManager<CustomerOpenInvoicesViewModel> {

        public static class OpenInvoiceBaseOnType {
                public static UUID BaseOnDealer = UUID.fromString("B80EE8D3-4FE6-43A5-924C-0DD384CF3538");
        }


        public CustomerOpenInvoicesViewManager(Context context) {
                super(context , new CustomerOpenInvoicesViewModelRepository());
        }

        public static Query getAll() {
                Query query = new Query();
                query.from(CustomerOpenInvoicesView.CustomerOpenInvoicesViewTbl);
                return query;
        }

        public static Query getAll(UUID key) {
                Query query = new Query();
                query.from(CustomerOpenInvoicesView.CustomerOpenInvoicesViewTbl).whereAnd(Criteria.equals(CustomerOpenInvoicesView.CustomerId, key));
                return query;
        }

        public static Query getAllForDealer(UUID key, UUID dealerId) {
                Query query = new Query();
                query.from(CustomerOpenInvoicesView.CustomerOpenInvoicesViewTbl).whereAnd(Criteria.equals(CustomerOpenInvoicesView.CustomerId, key))
                .whereAnd(Criteria.equals(CustomerOpenInvoicesView.DealerId, dealerId));
                return query;
        }
}

