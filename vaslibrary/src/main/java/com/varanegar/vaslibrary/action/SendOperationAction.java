package com.varanegar.vaslibrary.action;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class SendOperationAction extends CheckPathAction {
    private static final String TAG = "SendOperationAction";
    private ProgressDialog progressDialog;

    public SendOperationAction(MainVaranegarActivity activity,
                               ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_check_box_black_24dp;
        setAnimation(true);
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.send_customer_actions);
    }

    @Override
    protected boolean isDone() {
        return isDataSent();
    }

    @Override
    public void run() {
        setRunning(true);
        sendCustomerCalls();
    }

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return null;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());

        SysConfigModel allowSendData;
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            allowSendData = sysConfigManager.read(ConfigKey.DistAllowSync,
                    SysConfigManager.cloud);
        else
            allowSendData = sysConfigManager.read(ConfigKey.OnlineSynch,
                    SysConfigManager.cloud);

        if (SysConfigManager.compare(allowSendData, false))
            return getActivity().getString(R.string.can_not_send_customer_data);

        if (isDataSent())
            return getActivity().getString(R.string.customer_operation_is_sent_already);
        if (isConfirmed())
            return null;
        else
            return getActivity().getString(R.string.you_should_save_order);
    }

    private boolean isDataSent() {
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        return callManager.isDataSent(getCalls(), true);
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ex) {
                Timber.d(ex);
            }
    }


    private void sendCustomerCalls() {
        if (!Connectivity.isConnected(getActivity())) {
            ConnectionSettingDialog connectionSettingDialog =
                    new ConnectionSettingDialog();
            connectionSettingDialog.show(getActivity()
                    .getSupportFragmentManager(), "ConnectionSettingDialog");
            setRunning(false);
            return;
        }

        showProgressDialog();
        UserModel userModel = UserManager.readFromFile(getActivity());
        ApiNew apiNew=new ApiNew(getActivity());

        StringBuilder digits = new StringBuilder();
        StringBuilder letters = new StringBuilder();

        for (int i = 0; i < userModel.UserName.length(); i++) {
            char ch = userModel.UserName.charAt(i);
            if (Character.isDigit(ch)) {
                digits.append(ch);
            } else if (Character.isLetter(ch) || Character.isWhitespace(ch)) {
                letters.append(ch);
            }
        }
        String numbersString = digits.toString();

        boolean cheak ;
        Call<Boolean> call = apiNew
                .CheckVisitor(numbersString);
        apiNew.runWebRequest(call, new WebCallBack<Boolean>() {
            @Override
            protected void onFinish() {
                dismissProgressDialog();
            }

            @Override
            protected void onSuccess(Boolean result, Request request) {
                if (result){
                    gettourManager();
                }else{
                    showErrorMessage("حساب شما بلاک می باشد");
                }

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getActivity());
                Log.e("err", String.valueOf(err));
                dismissProgressDialog();
                showErrorMessage(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                    dismissProgressDialog();
                     showErrorMessage(String.valueOf(R.string.error_connecting_to_server));

            }
        });


    }


    private void gettourManager(){
        TourManager tourManager = new TourManager(getActivity());
        tourManager.verifyData(new TourManager.VerifyCallBack() {
            @Override
            public void onFailure(String error) {
                setRunning(false);
                dismissProgressDialog();
                showErrorMessage(error);
            }

            @Override
            public void onSuccess(final List<UUID> result) {
                if (result != null && result.size() > 0) {
                    dismissProgressDialog();
                    CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                    dialog.setTitle(R.string.some_customer_data_is_not_saved);
                    CustomerManager customerManager = new CustomerManager(getActivity());
                    List<CustomerModel> customerModels = customerManager.getCustomers(result);
                    StringBuilder msg = new StringBuilder(getActivity()
                            .getString(R.string.do_you_want_to_restore_these_customers));
                    for (CustomerModel customerModel :
                            customerModels) {
                        msg.append(System.getProperty("line.separator"))
                                .append(customerModel.CustomerName);
                    }
                    Timber.d(msg.toString());
                    dialog.setMessage(msg.toString());
                    dialog.setPositiveButton(R.string.yes, view -> {
                        CustomerCallManager customerCallManager =
                                new CustomerCallManager(getActivity());
                        for (UUID customerId :
                                result) {
                            try {
                                customerCallManager
                                        .removeCall(CustomerCallType.SendData, customerId);
                            } catch (DbException e) {
                                Timber.e(e);
                            }
                        }
                    });
                    dialog.setNeutralButton(R.string.no, null);
                    dialog.setIcon(Icon.Error);
                    dialog.show();
                    setRunning(false);
                } else {
                    Thread thread = new Thread(() -> {
                        TourManager tourManager1 = new TourManager(getActivity());
                        tourManager1.populatedAndSendTour(
                                getSelectedId(),
                                new TourManager.TourCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        Timber.i("data tour for customer id = " +
                                                getSelectedId().toString() +
                                                " was sent successfully");
                                        getActivity().runOnUiThread(() -> {
                                            setRunning(false);
                                            progressDialog.dismiss();
                                            CuteMessageDialog dialog =
                                                    new CuteMessageDialog(getActivity());
                                            dialog.setIcon(Icon.Success);
                                            dialog.setMessage(R.string.customer_actions_sent);
                                            dialog.setPositiveButton(R.string.ok, null);
                                            dialog.show();
                                            runActionCallBack();
                                            //  sendpoint();
                                        });
                                    }

                                    @Override
                                    public void onFailure(final String error) {
                                        Timber.e(error);
                                        getActivity().runOnUiThread(() -> {
                                            setRunning(false);
                                            progressDialog.dismiss();
                                            showErrorMessage(error);
                                        });
                                    }

                                    @Override
                                    public void onProgressChanged(String progress) {

                                    }
                                });
                    });
                    thread.start();
                }
            }
        });
    }
    public void sendpoint(){
        LocationManager locationManager = new LocationManager(getActivity());
        locationManager.tryToSendAll(new LocationManager.SendLocationListener() {
            @Override
            public void onSendFailed() {
                Timber.e("not Send Point");
            }
        });
    }
    void showProgressDialog() {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (progressDialog == null)
                progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(getActivity()
                    .getString(R.string.please_wait));
            progressDialog.setMessage(activity
                    .getString(R.string.sending_customer_action));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    void showErrorMessage(String error) {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private boolean isConfirmed() {
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        return callManager.isConfirmed(
                Linq.remove(getCalls(), it -> it.CallType == CustomerCallType.SendData));
    }
}