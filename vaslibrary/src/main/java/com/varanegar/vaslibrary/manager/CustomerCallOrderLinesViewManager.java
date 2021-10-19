package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.customerCallOrderLinesOrderView.CustomerCallOrderLinesOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderLinesOrderView.CustomerCallOrderLinesOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderLinesOrderView.CustomerCallOrderLinesOrderViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 16/01/2017.
 */

public class CustomerCallOrderLinesViewManager extends BaseManager<CustomerCallOrderLinesOrderViewModel> {


    public CustomerCallOrderLinesViewManager(Context context) {

        super(context, new CustomerCallOrderLinesOrderViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(CustomerCallOrderLinesOrderView.CustomerCallOrderLinesOrderViewTbl);
        return query;
    }

    public static Query getAll(String orderUniqueId) {
        Query query = new Query();
        query.from(CustomerCallOrderLinesOrderView.CustomerCallOrderLinesOrderViewTbl)
                .whereAnd(Criteria.equals(CustomerCallOrderLinesOrderView.OrderUniqueId, orderUniqueId));
        return query;
    }


}

