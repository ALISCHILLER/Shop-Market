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
    public CustomerSumMoneyAndWeightReportModel getAll(UUID customerUniqueId){
        Query query = new Query();
        query.from(From.table(CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportTbl))
                .whereAnd(Criteria.contains(CustomerSumMoneyAndWeightReport.CustomerId,
                        String.valueOf(customerUniqueId)));
        return getItem(query);
    }
    public void save(UUID customerUniqueId, List<CustomerSumMoneyAndWeightReportModel> result){

        List<CustomerSumMoneyAndWeightReportModel>  sumMoneyAndWeightReportModels=new ArrayList<>();
        for (CustomerSumMoneyAndWeightReportModel sumMoneyAndWeightReportModel:
                result) {
            CustomerSumMoneyAndWeightReportModel sumMoneyAndWeightReportModel1=
                    new CustomerSumMoneyAndWeightReportModel();
            sumMoneyAndWeightReportModel1.UniqueId= sumMoneyAndWeightReportModel.UniqueId;
            sumMoneyAndWeightReportModel1.Money_Sum= sumMoneyAndWeightReportModel.Money_Sum;
            sumMoneyAndWeightReportModel1.Weight_Sum= sumMoneyAndWeightReportModel.Weight_Sum;
            sumMoneyAndWeightReportModel1.CustomerId= String.valueOf(customerUniqueId);
            sumMoneyAndWeightReportModels.add(sumMoneyAndWeightReportModel1);
        }


        try {
            insertOrUpdate(sumMoneyAndWeightReportModels);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
