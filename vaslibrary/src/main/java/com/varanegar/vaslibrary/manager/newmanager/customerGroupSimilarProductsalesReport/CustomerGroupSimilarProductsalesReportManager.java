package com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReport;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerGroupSimilarProductsalesReportManager extends BaseManager<CustomerGroupSimilarProductsalesReportModel> {
    public CustomerGroupSimilarProductsalesReportManager(@NonNull Context context) {
        super(context, new CustomerGroupSimilarProductsalesReportModelRepository());
    }
    public List<CustomerGroupSimilarProductsalesReportModel> getAll(String customerUniqueId){
        Query query = new Query();
        query.from(From.table(CustomerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportTbl))
                .whereAnd(Criteria.contains(CustomerGroupSimilarProductsalesReport.customerCode,
                        String.valueOf(customerUniqueId)));
        return getItems(query);
    }
    public void save(UUID customerUniqueId, List<CustomerGroupSimilarProductsalesReportModel> result){
        try {
            insertOrUpdate(result);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
