package com.varanegar.vaslibrary.action.newAcation;

import static java.security.AccessController.getContext;

import android.app.ProgressDialog;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.action.CheckPathAction;
import com.varanegar.vaslibrary.manager.updatemanager.CustomersUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.UUID;

import timber.log.Timber;

public class CustomerUpdateAction extends CheckPathAction {
    private static final String TAG = "CustomerUpdateAction";
    private ProgressDialog progressDialog;
    public CustomerUpdateAction(MainVaranegarActivity activity,
                                ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_customers_black_36dp;
    }

    @Override
    public String getName() {
        return "بروزرسانی مشتریان روز";
    }

    @Override
    protected boolean isDone() {
        return false;
    }


    void showProgressDialog() {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (progressDialog == null)
                progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(getActivity()
                    .getString(R.string.update_day_customers));
//            progressDialog.setMessage(activity
//                    .getString(R.string.update_day_customers));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ex) {
                Timber.d(ex);
            }
    }

    @Override
    public void run() {


        if (!Connectivity.isConnected(getActivity())) {
            ConnectionSettingDialog connectionSettingDialog =
                    new ConnectionSettingDialog();
            connectionSettingDialog.show(getActivity()
                    .getSupportFragmentManager(), "ConnectionSettingDialog");
            setRunning(false);
            return;
        }

        showProgressDialog();
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(getActivity(), new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                CustomersUpdateFlow flow = new CustomersUpdateFlow(getActivity(), null);
                flow.syncCustomersAndInitPromotionDb(new UpdateCall() {
                    @Override
                    protected void onSuccess() {
                        if (getActivity()== null) return;
                          dismissProgressDialog();
                            CuteMessageDialog cuteMessageDialog =
                                    new CuteMessageDialog(getActivity());
                            cuteMessageDialog.setIcon(Icon.Success);
                            cuteMessageDialog.setTitle(R.string.update_done);
                            cuteMessageDialog.setMessage(R.string.updated_day_customers);
                            cuteMessageDialog.setPositiveButton(R.string.ok, view -> refresh());
                            cuteMessageDialog.show();
                            runActionCallBack();
                    }

                    @Override
                    protected void onFailure(String error) {
                        dismissProgressDialog();
                        MainVaranegarActivity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            CuteMessageDialog dialog = new CuteMessageDialog(activity);
                            dialog.setMessage(error);
                            dialog.setTitle(R.string.error);
                            dialog.setIcon(Icon.Error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        }
                    }
                });
            }

            @Override
            public void failed() {
                dismissProgressDialog();
                    CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                    dialog.setMessage(R.string.network_error);
                    dialog.setTitle(R.string.error);
                    dialog.setIcon(Icon.Error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                }
            });
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



}
