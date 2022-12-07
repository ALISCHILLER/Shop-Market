package com.varanegar.vaslibrary.manager.newmanager.dealercommission;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.newmodel.commodity_rationing.CommodityRationingModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class DealerCommissionDataManager extends BaseManager<DealerCommissionDataModel> {
    public DealerCommissionDataManager(@NonNull Context context) {
        super(context, new DealerCommissionDataModelRepository());
    }




    public void sync(final UpdateCall updateCall) {
        ApiNew apiNew = new ApiNew(getContext());
        UserModel userModel = UserManager.readFromFile(getContext());
        Call<DealerCommissionDataModel> call = apiNew
                .getDealerCommissionData(String.valueOf(userModel.UniqueId));
        apiNew.runWebRequest(call, new WebCallBack<DealerCommissionDataModel>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(DealerCommissionDataModel result, Request request) {
                try {
                    deleteAll();
                    if (result!=null){
                        insert(result);
                    }
                    updateCall.success();
                } catch (ValidationException e) {
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.data_validation_failed));
                } catch (DbException e) {
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.data_error));
                }
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
