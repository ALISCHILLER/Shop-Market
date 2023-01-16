package com.varanegar.vaslibrary.manager.newmanager.checkCustomerCreditsManager;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReport;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCredit;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModelRepository;

import java.util.List;

public class CheckCustomerCreditManager extends BaseManager<CheckCustomerCreditModel> {
    public CheckCustomerCreditManager(@NonNull Context context) {
        super(context, new CheckCustomerCreditModelRepository());
    }


    public CheckCustomerCreditModel getItem(String customerCode){
        Query query = new Query();
        query.from(From.table(CheckCustomerCredit.CheckCustomerCreditTbl))
                .whereAnd(Criteria.contains(CheckCustomerCredit.customerCode,
                        String.valueOf(customerCode)));
        return getItem(query);
    }

}
