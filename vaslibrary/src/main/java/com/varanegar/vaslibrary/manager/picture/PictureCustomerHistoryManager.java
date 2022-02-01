package com.varanegar.vaslibrary.manager.picture;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.framework.validation.Validator;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerHistoryModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerHistoryModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.picturesubject.PictureSubjectApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 10/16/2017.
 */

public class PictureCustomerHistoryManager extends BaseManager<PictureCustomerHistoryModel> {
    public PictureCustomerHistoryManager(@NonNull Context context) {
        super(context, new PictureCustomerHistoryModelRepository());
    }

    public void sync(final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.CustomerPictureSubject);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        PictureSubjectApi api = new PictureSubjectApi(getContext());
        TourManager tourManager = new TourManager(getContext());
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null) {
            call.failure(getContext().getString(R.string.user_not_found));
            return;
        }

        String dealerId = userModel.UniqueId.toString();
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        api.runWebRequest(api.getCustomerPictureHistory(dateString, tourManager.loadTour().UniqueId.toString(), dealerId, settingsId.Value), new WebCallBack<List<PictureCustomerHistoryModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<PictureCustomerHistoryModel> result, Request request) {
                if (result != null && result.size() > 0) {
                    try {
                        sync(result);
                        Timber.i("List of customer picture subjects updated");
                        call.success();
                        new UpdateManager(getContext()).addLog(UpdateKey.CustomerPictureSubject);
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
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
}
