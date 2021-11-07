package com.varanegar.supervisor;

import android.content.Context;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.supervisor.model.SupervisorCustomerModel;
import com.varanegar.supervisor.model.SupervisorCustomerModelRepository;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.List;

import okhttp3.Request;

public class DataManager {
    public interface Callback {
        void onSuccess();

        void onError(String error);
    }

    public static void getData(final Context context, final Callback callback) {
        final UserModel userModel = UserManager.readFromFile(context);
        if (userModel != null) {
            SupervisorApi api = new SupervisorApi(context);
            api.runWebRequest(api.getVisitors(userModel.UniqueId.toString()), new WebCallBack<List<VisitorModel>>() {
                @Override
                protected void onFinish() {
                }

                @Override
                protected void onSuccess(List<VisitorModel> result, Request request) {
                    VisitorManager visitorManager = new VisitorManager(context);
                    try {
                        visitorManager.deleteAll();
                        if (result.size() > 0)
                            visitorManager.insert(result);
                        getCustomers(userModel, callback, context);
                    } catch (ValidationException e) {
                        callback.onError(context.getString(R.string.data_error));
                    } catch (DbException e) {

                        callback.onError(context.getString(R.string.error_saving_request));
                    }

                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, context);
                    callback.onError(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    callback.onError(context.getString(R.string.connection_to_server_failed));
                }
            });
        } else {
            callback.onError(context.getString(R.string.user_not_found));
        }
    }

    private static void getCustomers(UserModel userModel, final Callback callback, final Context context) {
        SupervisorApi api = new SupervisorApi(context);
        api.runWebRequest(api.getCustomers(userModel.UniqueId), new WebCallBack<List<SupervisorCustomerModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<SupervisorCustomerModel> result, Request request) {
                try {
                    if (result != null && result.size() > 0) {
                        SupervisorCustomerModelRepository repository = new SupervisorCustomerModelRepository();
                        repository.deleteAll();
                        repository.insert(result);
                    }
                    SysConfigManager sysConfigManager = new SysConfigManager(context);
                    sysConfigManager.sync(new UpdateCall() {
                        @Override
                        protected void onFinish() {
                            callback.onSuccess();
                        }
                    });
                } catch (Exception e) {
                    callback.onError(context.getString(R.string.error_saving_request));
                }

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                callback.onError(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(context.getString(R.string.connection_to_server_failed));
            }
        });


    }

}
