package com.varanegar.vaslibrary.manager.newmanager.customerLastBill;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReport;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCredit;

import java.util.List;

public class CustomerLastBillManager extends BaseManager<CustomerLastBillModel> {
    public CustomerLastBillManager(@NonNull Context context) {
        super(context, new CustomerLastBillModelRepository());
    }


    public List<CustomerLastBillModel> getAll(String customerCode){
        Query query = new Query();
        query.from(From.table(CustomerLastBill.CustomerLastBillTbl))
                .whereAnd(Criteria.contains(CustomerLastBill.CustomerCode, customerCode));
        return getItems(query);
    }
}
