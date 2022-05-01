package com.varanegar.vaslibrary.webapi.apiNew.modelNew.customer_not_allowed_product;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.AllocationError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.InventoryError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyWarning;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class CustomerNotAllowProductManager extends BaseManager<CustomerNotAllowProductModel> {
    private Call<List<CustomerNotAllowProductModel>> call;
    private List<CustomerModel> customerModel;
    public CustomerNotAllowProductManager(@NonNull Context context) {
        super(context, new CustomerNotAllowProductModelRepository());
    }



    public void sync(@NonNull final UpdateCall updateCall){
        CustomerManager customerManager=new CustomerManager(getContext());
        customerModel=customerManager.getAll();

        List<String> ids = new ArrayList<>();
        for (int i = 0; i <customerModel.size(); i++) {
            ids.add(customerModel.get(i).UniqueId.toString());
        }
        //call api

        ApiNew apiNew=new ApiNew(getContext());
        call=apiNew.getProductNotAllowed(ids);


        apiNew.runWebRequest(call, new WebCallBack<List<CustomerNotAllowProductModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerNotAllowProductModel> result, Request request) {

                CustomerNotAllowProductModelRepository repository=
                        new CustomerNotAllowProductModelRepository();
                if (result.size()>0){
                    repository.deleteAll();
                    repository.insertOrUpdate(result);
                }else {
                    repository.deleteAll();
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




    public static void checkNotAllowed(Context context, UUID customerId,UUID productId)
            throws InventoryError, AllocationError, OnHandQtyWarning {

        Query query=new Query();
        query.from(CustomerNotAllowProduct.CustomerNotAllowProductTbl)
                .whereAnd(Criteria.equals(CustomerNotAllowProduct.CustomerId, customerId))
                .whereAnd(Criteria.equals(CustomerNotAllowProduct.ProductId, productId));

        CustomerNotAllowProductModelRepository repository =
                new CustomerNotAllowProductModelRepository();

        List<CustomerNotAllowProductModel> data =  repository.getItems(query);

        if(data.size()>0){
            throw new InventoryError("مشتری مجاز به خرید این کالا نیست.");
        }

    }
    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }
}
