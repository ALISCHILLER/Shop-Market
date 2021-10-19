package com.varanegar.vaslibrary.manager.contractpricemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiException;
import com.varanegar.framework.network.listeners.InterruptedException;
import com.varanegar.framework.network.listeners.NetworkException;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceVnLite;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceVnLiteModel;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceVnLiteModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.price.ContractPriceApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Torabi on 12/5/2017.
 */

public class ContractPriceVnLiteManager extends BaseManager<ContractPriceVnLiteModel> {
    private Call<List<ContractPriceVnLiteModel>> call;

    public ContractPriceVnLiteManager(@NonNull Context context) {
        super(context, new ContractPriceVnLiteModelRepository());
    }

    public Call sync(UpdateCall updateCall) {
        ContractPriceApi contractPriceApi = new ContractPriceApi(getContext());
        call = contractPriceApi.getRastakContractPrices();
        try {
            List<ContractPriceVnLiteModel> result = contractPriceApi.runWebRequest(call);
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
        } catch (InterruptedException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.request_canceled));
        }
        return call;
    }

    public void clearAdditionalData() {
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        try {
            delete(Criteria.lesserThan(ContractPriceVnLite.EndDate, date));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
