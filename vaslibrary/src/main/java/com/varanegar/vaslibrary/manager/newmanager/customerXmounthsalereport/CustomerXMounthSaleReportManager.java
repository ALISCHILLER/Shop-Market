package com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.processor.annotations.Column;
import com.varanegar.vaslibrary.manager.newmanager.commodity_rationing.CommodityRationingView;
import com.varanegar.vaslibrary.manager.newmanager.commodity_rationing.CommodityRationingViewModel;
import com.varanegar.vaslibrary.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerXMounthSaleReportManager extends BaseManager<CustomerXMounthSaleReportModel> {

    public CustomerXMounthSaleReportManager(@NonNull Context context) {
        super(context, new CustomerXMounthSaleReportModelRepository());
    }


    public List<CustomerXMounthSaleReportModel> getAll(UUID customerUniqueId){
        Query query = new Query();
        query.from(From.table(CustomerXMounthSaleReport.CustomerXMounthSaleReportTbl))
                .whereAnd(Criteria.contains(CustomerXMounthSaleReport.customerUniqueId,
                        String.valueOf(customerUniqueId)));
        return getItems(query);
    }
    public void save(UUID customerUniqueId, List<ProductModel> result){

        List<CustomerXMounthSaleReportModel>  xMounthSaleReportModels=new ArrayList<>();
        for (ProductModel productModel:
                result) {
            CustomerXMounthSaleReportModel customerXMounthSaleReportModel=
                    new CustomerXMounthSaleReportModel();
            customerXMounthSaleReportModel.UniqueId= productModel.UniqueId;
            customerXMounthSaleReportModel.ProductCode=productModel.ProductCode;
            customerXMounthSaleReportModel.ProductName=productModel.ProductName;
            customerXMounthSaleReportModel.customerUniqueId=customerUniqueId;
            xMounthSaleReportModels.add(customerXMounthSaleReportModel);
        }


        try {
            insertOrUpdate(xMounthSaleReportModels);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
