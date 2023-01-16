package com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReport;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerSumMoneyAndWeightReportManager extends BaseManager<CustomerSumMoneyAndWeightReportModel> {
    public CustomerSumMoneyAndWeightReportManager(@NonNull Context context) {
        super(context, new CustomerSumMoneyAndWeightReportModelRepository());
    }
    public CustomerSumMoneyAndWeightReportModel getAll(String customerUniqueId){
        Query query = new Query();
        query.from(From.table(CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportTbl))
                .whereAnd(Criteria.contains(CustomerSumMoneyAndWeightReport.CustomerCode,
                        String.valueOf(customerUniqueId)));
        return getItem(query);
    }
    public void save(UUID customerUniqueId, List<CustomerSumMoneyAndWeightReportModel> result){

        try {
            insertOrUpdate(result);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
