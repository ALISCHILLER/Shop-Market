package com.varanegar.vaslibrary.manager.contractpricemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiException;
import com.varanegar.framework.network.listeners.InterruptedException;
import com.varanegar.framework.network.listeners.NetworkException;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceNestleModel;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceNestleModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.price.ContractPriceApi;

import java.util.List;

import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Torabi on 12/19/2017.
 */

public class ContractPriceNestleManager extends BaseManager<ContractPriceNestleModel> {
    public ContractPriceNestleManager(@NonNull Context context) {
        super(context, new ContractPriceNestleModelRepository());
    }

    public Call sync(UpdateCall updateCall) {
        ContractPriceApi contractPriceApi = new ContractPriceApi(getContext());
        Call<List<ContractPriceNestleModel>> call = contractPriceApi.getNestleContractPrices();
        try {
            List<ContractPriceNestleModel> result = contractPriceApi.runWebRequest(call);
            try {
                deleteAll();
                if (result.size() > 0)
                    try {
                        insert(result);
                        Timber.i("Updating contract price completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                else {
                    Timber.w("List of contract price was empty");
                    updateCall.failure(getContext().getString(R.string.contract_price_was_empty));
                }
            } catch (DbException e) {
                Timber.e(e);
                updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
            }
        } catch (ApiException e) {
            String err = WebApiErrorBody.log(e.getApiError(), getContext());
            updateCall.failure(err);
        } catch (NetworkException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.network_error));
        } catch (InterruptedException ex) {
            Timber.e(ex);
            updateCall.failure(getContext().getString(R.string.request_canceled));
        }
        return call;
    }
}
