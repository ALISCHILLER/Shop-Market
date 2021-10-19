package com.varanegar.vaslibrary.manager.bank;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.bank.Bank;
import com.varanegar.vaslibrary.model.bank.BankModel;
import com.varanegar.vaslibrary.model.bank.BankModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.bank.BankApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 3/26/2018.
 */

public class BankManager extends BaseManager<BankModel> {
    public BankManager(@NonNull Context context) {
        super(context, new BankModelRepository());
    }

    public void sync(final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.Bank);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        BankApi bankApi = new BankApi(getContext());
        bankApi.runWebRequest(bankApi.getBanks(dateString), new WebCallBack<List<BankModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<BankModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.Bank);
                        Timber.i("Updating banks completed");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating bank completed. List of city was empty");
                    call.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public List<BankModel> getAllBanks() {
        Query query = new Query();
        query.from(Bank.BankTbl).orderByAscending(Bank.BankName);
        return getItems(query);
    }
}
