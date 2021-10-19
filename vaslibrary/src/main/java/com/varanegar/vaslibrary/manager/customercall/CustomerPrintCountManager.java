package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.customercall.CustomerPrintCount;
import com.varanegar.vaslibrary.model.customercall.CustomerPrintCountModel;
import com.varanegar.vaslibrary.model.customercall.CustomerPrintCountModelRepository;

import java.util.UUID;

/**
 * Created by g.aliakbar on 10/04/2018.
 */

public class CustomerPrintCountManager extends BaseManager<CustomerPrintCountModel> {

    public CustomerPrintCountManager(@NonNull Context context) {
        super(context, new CustomerPrintCountModelRepository());
    }

    public int getCount(UUID customerId) {
        Query query = new Query();
        query.from(CustomerPrintCount.CustomerPrintCountTbl).whereAnd(Criteria.equals(CustomerPrintCount.CustomerId, customerId.toString()));
        CustomerPrintCountModel printCountModel = getItem(query);
        if (printCountModel == null)
            return 0;
        else
            return printCountModel.PrintCounts;
    }

    public void addCount(UUID customerId) throws ValidationException, DbException {
        Query query = new Query();
        query.from(CustomerPrintCount.CustomerPrintCountTbl).whereAnd(Criteria.equals(CustomerPrintCount.CustomerId, customerId.toString()));
        CustomerPrintCountModel printCountModel = getItem(query);
        if (printCountModel == null) {
            printCountModel = new CustomerPrintCountModel();
            printCountModel.CustomerId = customerId;
            printCountModel.PrintCounts = 1;
            printCountModel.UniqueId = UUID.randomUUID();
        } else
            printCountModel.PrintCounts++;
        insertOrUpdate(printCountModel);
    }

    public void resetCount(@NonNull UUID customerId) throws DbException {
        delete(Criteria.equals(CustomerPrintCount.CustomerId, customerId.toString()));
    }
}
