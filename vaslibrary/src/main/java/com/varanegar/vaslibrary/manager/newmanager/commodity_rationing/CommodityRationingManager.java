package com.varanegar.vaslibrary.manager.newmanager.commodity_rationing;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.Customer;
import com.varanegar.vaslibrary.model.newmodel.commodity_rationing.CommodityRationingModel;
import com.varanegar.vaslibrary.model.newmodel.commodity_rationing.Product_RationingModel;
import com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport.CustomerGroupLastSalesReportModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;


public class CommodityRationingManager extends BaseManager<CommodityRationingViewModel> {

    public CommodityRationingManager(@NonNull Context context) {
        super(context, new CommodityRationingViewModelRepository());
    }


    public CommodityRationingViewModel getCheckRationin(UUID quotasUniqueId, UUID customerLevelIds
            , UUID customerActivityIds, UUID customerCategoryIds) {
        Query query = new Query();
        query.from(From.table(CommodityRationingView.CommodityRationingViewTbl))
                .whereAnd(Criteria.contains(CommodityRationingView.UniqueId, String.valueOf(quotasUniqueId)))
                .whereAnd(Criteria.contains(CommodityRationingView.customerLevelIds, String.valueOf(customerLevelIds))
                                .or(Criteria.contains(CommodityRationingView.customerActivityIds, String.valueOf(customerActivityIds)))
                                .or(Criteria.contains(CommodityRationingView.customerCategoryIds, String.valueOf(customerCategoryIds)))

                );
//
//
//                .whereAnd(Criteria.contains(CommodityRationingView.UniqueId, String.valueOf(quotasUniqueId))
//                        .or(Criteria.contains(CommodityRationingView.customerLevelIds, String.valueOf(customerLevelIds))
//                                .or(Criteria.contains(CommodityRationingView.customerActivityIds, String.valueOf(customerActivityIds)))
//                                .or(Criteria.contains(CommodityRationingView.customerCategoryIds, String.valueOf(customerCategoryIds)))));
        return getItem(query);
    }


    private Call<List<CustomerGroupLastSalesReportModel>> call;

    public void sync(final UpdateCall updateCall) {

        ApiNew apiNew = new ApiNew(getContext());
        UserModel userModel = UserManager.readFromFile(getContext());
        Call<List<CommodityRationingModel>> call = apiNew
                .getCommodityRationin(String.valueOf(userModel.UniqueId));

        apiNew.runWebRequest(call, new WebCallBack<List<CommodityRationingModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CommodityRationingModel> result, Request request) {
                Product_RationingViewModelRepository product_rationingViewModelRepository =
                        new Product_RationingViewModelRepository();
                try {
                    deleteAll();
                    product_rationingViewModelRepository.deleteAll();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (result.size() > 0) {
                    List<CommodityRationingViewModel> commodityRationingViewModelList = new ArrayList<>();
                    List<Product_RationingViewModel> rationingViewModels = new ArrayList<>();
                    List<Product_RationingModel> product_rationingModels = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        CommodityRationingViewModel viewModel = new CommodityRationingViewModel();
                        viewModel.UniqueId = result.get(i).uniqueId;
                        viewModel.customerActivityIds = result.get(i).customerActivityIds;
                        viewModel.customerCategoryIds = result.get(i).customerCategoryIds;
                        viewModel.customerLevelIds = result.get(i).customerLevelIds;
                        viewModel.dataCenterOwnerIds = result.get(i).dataCenterOwnerIds;
                        viewModel.fromDate = result.get(i).fromDate;
                        viewModel.personnelIds = result.get(i).personnelIds;
                        viewModel.quotasName = result.get(i).quotasName;
                        viewModel.quotasType = result.get(i).quotasType;
                        viewModel.toDate = result.get(i).toDate;
                        product_rationingModels = result.get(i).products;
                        commodityRationingViewModelList.add(i, viewModel);
                    }


                    for (int j = 0; j < product_rationingModels.size(); j++) {
                        Product_RationingViewModel productRationingViewModel = new Product_RationingViewModel();
                        productRationingViewModel.UniqueId = product_rationingModels.get(j).uniqueId;
                        productRationingViewModel.quotasUniqueId = product_rationingModels.get(j).quotasUniqueId;
                        productRationingViewModel.productUniuqeId = product_rationingModels.get(j).productUniuqeId;
                        productRationingViewModel.productUnitUniuqeId = product_rationingModels.get(j).productUnitUniuqeId;
                        productRationingViewModel.productCode = product_rationingModels.get(j).productCode;
                        productRationingViewModel.productName = product_rationingModels.get(j).productName;
                        productRationingViewModel.quantity = product_rationingModels.get(j).quantity;
                        rationingViewModels.add(j, productRationingViewModel);
                    }


                    try {
                        deleteAll();
                        insert(commodityRationingViewModelList);
                        product_rationingViewModelRepository.deleteAll();
                        product_rationingViewModelRepository.insert(rationingViewModels);
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                }
                updateCall.success();
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {

                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });


    }

}
