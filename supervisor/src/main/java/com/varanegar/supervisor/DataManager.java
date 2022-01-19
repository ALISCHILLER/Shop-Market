package com.varanegar.supervisor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.supervisor.model.SupervisorTourId;
import com.varanegar.vaslibrary.manager.updatemanager.ProductUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.QuestionnaireUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModel;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomerModelRepository;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModel;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomerModelRepository;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModelRepository;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.product.ProductGroupApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class DataManager {
    private Context _context;
    private UserModel _userModel;
    private SupervisorApi _api;
    private List<String> visitorIds;

    public interface Callback {
        void onSuccess();

        void onError(String error);
    }

    public interface AfterCallback {
        void callAfter();
    }

    public DataManager(Context _context) {
        this._context = _context;
        _userModel = UserManager.readFromFile(_context);
        _api = new SupervisorApi(_context);
    }

    public void getAllData(Callback callback) {
        final int[] c = {0};
        final boolean[] hasErr = {false};
        final int maxReq = 8;
        Callback callbackInner = new Callback() {
            @Override
            public void onSuccess() {
                if (!hasErr[0]) {
                    c[0]++;
                    if (c[0] >= maxReq) callback.onSuccess();
                }
            }

            @Override
            public void onError(String error) {
                if (!hasErr[0]){
                    hasErr[0] = true;
                    callback.onError(error);
                }
            }
        };

        getSupervisorTour2(callbackInner,() -> supervisorTourSent2(callbackInner));

        getVisitor2(callbackInner, () -> getSupervisorCustomers2(callbackInner));

        getNewCustomers2(callbackInner);

        getProductGroup2(callbackInner);

        getQuestionnaire2(callbackInner);

        supervisorTourSent2(callbackInner);
    }

    public void getVisitor2(final Callback callback, final AfterCallback afterCallback) {
        visitorIds = new ArrayList<>();
        if (_userModel != null && _userModel.UniqueId!=null) {
            _api.runWebRequest(_api.getVisitors(_userModel.UniqueId.toString()),
                    new WebCallBack<List<VisitorModel>>() {
                @Override
                protected void onFinish() {
                }

                @Override
                protected void onSuccess(List<VisitorModel> result, Request request) {
                    VisitorManager visitorManager = new VisitorManager(_context);
                    try {
                        visitorManager.deleteAll();
                        if (result.size() > 0) {
                            visitorManager.insert(result);
                            for (int i = 0; i < result.size(); i++) {
                                visitorIds.add(String.valueOf(result.get(i).UniqueId));
                            }
                        }
                        callback.onSuccess();
                        afterCallback.callAfter();
                    } catch (ValidationException e) {
                        callback.onError(_context.getString(R.string.data_error));
                    } catch (DbException e) {
                        callback.onError(_context.getString(R.string.error_saving_request));
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, _context);
                    callback.onError(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    callback.onError(_context.getString(R.string.connection_to_server_failed));
                }
            });
        } else {
            callback.onError(_context.getString(R.string.user_not_found));
        }
    }

    public void getSupervisorCustomers2(final Callback callback){
        _api.runWebRequest(_api.getsupervisorCustomers(visitorIds),
                new WebCallBack<List<SupervisorFullCustomerModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<SupervisorFullCustomerModel> result, Request request) {
                try {
                    if (result != null && result.size() > 0) {
                        SupervisorFullCustomerModelRepository repository =
                                new SupervisorFullCustomerModelRepository();
                        repository.deleteAll();
                        repository.insert(result);
                    }
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(_context.getString(R.string.error_saving_request));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, _context);
                callback.onError(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(_context.getString(R.string.connection_to_server_failed));
            }
        });
    }

    public void getNewCustomers2(final Callback callback){
        _api.runWebRequest(_api.getCustomers(_userModel.UniqueId),
                new WebCallBack<List<SupervisorCustomerModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<SupervisorCustomerModel> result, Request request) {
                try {
                    if (result != null && result.size() > 0) {
                        SupervisorCustomerModelRepository repository =
                                new SupervisorCustomerModelRepository();
                        repository.deleteAll();
                        repository.insert(result);
                    }
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(_context.getString(R.string.error_saving_request));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, _context);
                callback.onError(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(_context.getString(R.string.connection_to_server_failed));
            }
        });
    }

    public void getSupervisorTour2(final Callback callback, final AfterCallback afterCallback){
        SupervisorApi api = new SupervisorApi(_context);
        api.runWebRequest(api.getTourBySupervisorId(_userModel.UniqueId),
                new WebCallBack<SupervisorTourId>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(SupervisorTourId result, Request request) {
                if (result.uniqueId != null) {
                    SharedPreferences sharedconditionCustomer = _context
                            .getSharedPreferences("SupervisorId", Context.MODE_PRIVATE);
                    sharedconditionCustomer.edit()
                            .putString("SupervisorIduniqueId", String.valueOf(result.uniqueId))
                            .apply();
                }

                callback.onSuccess();
                afterCallback.callAfter();
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, _context);
                callback.onError(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(_context.getString(R.string.connection_to_server_failed));
            }
        });
    }

    public void getProductGroup2(final Callback callback){
        UpdateManager updateManager = new UpdateManager(_context);
        Date date = updateManager.getLog(UpdateKey.ProductGroup);
        String dateString = DateHelper.toString(
                date,
                DateFormat.MicrosoftDateTime,
                Locale.US);
        ProductGroupApi productGroupApi = new ProductGroupApi(_context);
        Call<List<ProductGroupModel>> call;
        call = productGroupApi.getAll(dateString);
        productGroupApi.runWebRequest(call, new WebCallBack<List<ProductGroupModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ProductGroupModel> result, Request request) {
                try {
                    if (result != null && result.size() > 0) {
                        ProductGroupModelRepository repository = new ProductGroupModelRepository();
                        repository.deleteAll();
                        repository.insert(result);
                    }
                    SysConfigManager sysConfigManager = new SysConfigManager(_context);
                    sysConfigManager.sync(new UpdateCall() {
                        @Override
                        protected void onFinish() {
                            callback.onSuccess();
                        }
                    });
                    callback.onSuccess();
                } catch (Exception e) {
                    Timber.e(e);
                    callback.onError(_context.getString(R.string.error_saving_request));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, _context);
                callback.onError(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t.getMessage());
                callback.onError(_context.getString(R.string.connection_to_server_failed));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
            }
        });
    }

    public void getQuestionnaire2(final Callback callback){
        QuestionnaireUpdateFlow flow = new QuestionnaireUpdateFlow(_context);
        flow.syncQuestionnaire(new UpdateCall() {
            @Override
            protected void onFinish() {
                super.onFinish();
            }

            @Override
            protected void onSuccess() {
                try {
                    supervisor_tour_sent(callback, _context);
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(_context.getString(R.string.error_saving_request));
                }
            }

            @Override
            protected void onFailure(String error) {
                callback.onError(error);
            }

            @Override
            protected void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void supervisorTourSent2(final Callback callback){
        SharedPreferences sharedPreferences = _context
                .getSharedPreferences("SupervisorId", Context.MODE_PRIVATE);
        UUID userModel = UUID.fromString(
                sharedPreferences.getString("SupervisorIduniqueId", null));
        SupervisorApi api = new SupervisorApi(_context);
        api.runWebRequest(api.supervisor_tour_sent(userModel), new WebCallBack<String>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(String result, Request request) {
                try {
                    SysConfigManager sysConfigManager = new SysConfigManager(_context);
                    sysConfigManager.sync(new UpdateCall() {
                        @Override
                        protected void onFinish() {
                            callback.onSuccess();
                        }
                    });
                    callback.onSuccess();
                } catch (Exception e) {
                    callback.onError(_context.getString(R.string.error_saving_request));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, _context);
                callback.onError(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(_context.getString(R.string.connection_to_server_failed));
            }
        });
    }



    public static void getVisitor(final Context context, final Callback callback) {
        final UserModel userModel = UserManager.readFromFile(context);
        List<String> visitor = new ArrayList<>();
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
                        if (result.size() > 0) {
                            visitorManager.insert(result);
                            for (int i = 0; i < result.size(); i++) {

                                visitor.add(String.valueOf(result.get(i).UniqueId));
                            }
                        }
                        getCustomers(userModel, visitor, callback, context);
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

    public static void getCustomers(UserModel userModel, List<String> visitorModel, final Callback callback, final Context context) {
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
                    //  getProductGroup(callback,context);
                    getSupervisorId(userModel, visitorModel, callback, context);

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


    public static void getSupervisorId(UserModel userModel, List<String> visitorModel, final Callback callback, final Context context) {
        SupervisorApi api = new SupervisorApi(context);
        api.runWebRequest(api.getTourBySupervisorId(userModel.UniqueId), new WebCallBack<SupervisorTourId>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(SupervisorTourId result, Request request) {

                if (result.uniqueId != null) {
                    SharedPreferences sharedconditionCustomer = context.getSharedPreferences("SupervisorId", Context.MODE_PRIVATE);
                    sharedconditionCustomer.edit().putString("SupervisorIduniqueId", String.valueOf(result.uniqueId)).apply();
                }

                getsupervisorCustomers(visitorModel, callback, context);
                //   getProductGroup(callback,context);
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

    public static void getsupervisorCustomers(List<String> userModel, final Callback callback, final Context context) {
        SupervisorApi api = new SupervisorApi(context);
        api.runWebRequest(api.getsupervisorCustomers(userModel), new WebCallBack<List<SupervisorFullCustomerModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<SupervisorFullCustomerModel> result, Request request) {
                try {
                    if (result != null && result.size() > 0) {
                        SupervisorFullCustomerModelRepository repository = new SupervisorFullCustomerModelRepository();
                        repository.deleteAll();
                        repository.insert(result);
                    }
                    getProductGroup(callback, context);
//                    SysConfigManager sysConfigManager = new SysConfigManager(context);
//                    sysConfigManager.sync(new UpdateCall() {
//                        @Override
//                        protected void onFinish() {error = {WebRequest$1$1@13703}
//                            callback.onSuccess();
//                        }
//                    });
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


    public static void getProductGroup(final Callback callback, final Context context) {
        UpdateManager updateManager = new UpdateManager(context);
        Date date = updateManager.getLog(UpdateKey.ProductGroup);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        ProductGroupApi productGroupApi = new ProductGroupApi(context);
        Call<List<ProductGroupModel>> call;
        call = productGroupApi.getAll(dateString);
        productGroupApi.runWebRequest(call, new WebCallBack<List<ProductGroupModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ProductGroupModel> result, Request request) {

                try {
                    if (result != null && result.size() > 0) {
                        ProductGroupModelRepository repository = new ProductGroupModelRepository();
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
                    getQuestionnaire(callback, context);
                } catch (Exception e) {
                    Timber.e(e);
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
                Timber.e(t.getMessage());
                callback.onError(context.getString(R.string.connection_to_server_failed));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);

            }
        });

    }

    public static void getQuestionnaire(final Callback callback, final Context context) {
        QuestionnaireUpdateFlow flow = new QuestionnaireUpdateFlow(context);
        flow.syncQuestionnaire(new UpdateCall() {
            @Override
            protected void onFinish() {
                super.onFinish();
            }

            @Override
            protected void onSuccess() {
                try {
                    supervisor_tour_sent(callback, context);
                } catch (Exception e) {
                    callback.onError(context.getString(R.string.error_saving_request));
                }
            }

            @Override
            protected void onFailure(String error) {
                String err = error;
                callback.onError(err);
            }

            @Override
            protected void onError(String error) {
                String err = error;
                callback.onError(err);
            }
        });
    }

    public static void supervisor_tour_sent(final Callback callback, final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SupervisorId", Context.MODE_PRIVATE);
        UUID userModel = UUID.fromString(sharedPreferences.getString("SupervisorIduniqueId", null));
        SupervisorApi api = new SupervisorApi(context);
        api.runWebRequest(api.supervisor_tour_sent(userModel), new WebCallBack<String>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(String result, Request request) {
                try {
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
