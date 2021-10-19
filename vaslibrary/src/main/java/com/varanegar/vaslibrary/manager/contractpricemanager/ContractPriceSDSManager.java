package com.varanegar.vaslibrary.manager.contractpricemanager;

import android.content.Context;

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
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceSDS;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceSDSModel;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceSDSModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.price.ContractPriceApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/11/2017.
 */

public class ContractPriceSDSManager extends BaseManager<ContractPriceSDSModel> {

    public ContractPriceSDSManager(Context context) {
        super(context, new ContractPriceSDSModelRepository());
    }

    public Call sync(final UpdateCall updateCall) {
        ContractPriceApi contractPriceApi = new ContractPriceApi(getContext());
        Date date = new UpdateManager(getContext()).getLog(UpdateKey.ContractPriceSDS);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        Call<List<ContractPriceSDSModel>> call = contractPriceApi.getVaranegarContractPrices(dateString);
        try {
            List<ContractPriceSDSModel> result = contractPriceApi.runWebRequest(call);
            if (result.size() > 0)
                try {
                    sync(result);
                    new UpdateManager(getContext()).addLog(UpdateKey.ContractPriceSDS);
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
                updateCall.success();
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
            delete(Criteria.lesserThan(ContractPriceSDS.EndDate, date));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
