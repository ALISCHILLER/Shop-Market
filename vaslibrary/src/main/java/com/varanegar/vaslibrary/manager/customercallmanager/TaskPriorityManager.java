package com.varanegar.vaslibrary.manager.customercallmanager;

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
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.customercall.TaskPriority;
import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;
import com.varanegar.vaslibrary.model.customercall.TaskPriorityModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.taskpriority.TaskPriorityApi;

import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 1/21/2018.
 */

public class TaskPriorityManager extends BaseManager<TaskPriorityModel> {
    public TaskPriorityManager(@NonNull Context context) {
        super(context, new TaskPriorityModelRepository());
    }

    public void sync(final UpdateCall call) {
        try {
            deleteAll();
            SysConfigModel sysConfigModel = new SysConfigManager(getContext()).read(ConfigKey.SettingsId, SysConfigManager.local);
            TaskPriorityApi taskPriorityApi = new TaskPriorityApi(getContext());
            String date = DateHelper.toString(UpdateManager.MIN_DATE, DateFormat.MicrosoftDateTime, Locale.US);
            taskPriorityApi.runWebRequest(taskPriorityApi.getAll(sysConfigModel.Value, date), new WebCallBack<List<TaskPriorityModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<TaskPriorityModel> result, Request request) {
                    if (result.size() > 0) {
                        try {
                            long affectedRows = insert(result);
                            Timber.i("List of task priorities updated = " + affectedRows);
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.success();
                        } catch (DbException e) {
                            Timber.e(e);
                            call.success();
                        }
                    } else {
                        Timber.i("List of Task priorities was empty");
                        call.success();
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    WebApiErrorBody.log(error, getContext());
                    call.success();
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t);
                    call.success();
                }
            });
        } catch (DbException e) {
            Timber.e(e);
            call.failure(getContext().getString(R.string.error_deleting_old_data));
        }

    }

    public List<TaskPriorityModel> getAll() {
        return getItems(new Query().from(TaskPriority.TaskPriorityTbl));
    }
}
