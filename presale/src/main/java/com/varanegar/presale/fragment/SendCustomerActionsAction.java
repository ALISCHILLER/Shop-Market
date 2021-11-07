package com.varanegar.presale.fragment;

import android.app.ProgressDialog;
import android.view.View;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.action.CheckBarcodeAction;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 02/07/2017.
 */

public class SendCustomerActionsAction extends CheckBarcodeAction {
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return null;
    }

    public SendCustomerActionsAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_file_upload_black_24dp;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.send_customer_actions);
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel allowSendData = sysConfigManager.read(ConfigKey.OnlineSynch, SysConfigManager.cloud);
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

    private boolean isConfirmed() {
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        return callManager.isConfirmed(Linq.remove(getCalls(), it -> it.CallType == CustomerCallType.SendData));
    }

    @Override
    public boolean isDone() {
        return isDataSent();
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public void run() {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(getActivity().getString(R.string.please_wait));
            progressDialog.setMessage(getActivity().getString(R.string.sending_customer_action));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        setRunning(true);
        if (!Connectivity.isConnected(getActivity())) {
            ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
            connectionSettingDialog.show(getActivity().getSupportFragmentManager(), "ConnectionSettingDialog");
            dismissProgressDialog();
            setRunning(false);
            return;
        }

        TourManager tourManager = new TourManager(getActivity());
        tourManager.verifyData(new TourManager.VerifyCallBack() {
            @Override
            public void onFailure(String error) {
                getActivity().showSnackBar(error, MainVaranegarActivity.Duration.Short);
                dismissProgressDialog();
                setRunning(false);
            }

            @Override
            public void onSuccess(final List<UUID> result) {
                if (result != null && result.size() > 0) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                    dialog.setTitle(R.string.some_customer_data_is_not_saved);
                    CustomerManager customerManager = new CustomerManager(getActivity());
                    List<CustomerModel> customerModels = customerManager.getCustomers(result);
                    String msg = getActivity().getString(R.string.do_you_want_to_restore_these_customers);
                    for (CustomerModel customerModel :
                            customerModels) {
                        msg += System.getProperty("line.separator") + customerModel.CustomerName;
                    }
                    Timber.d(msg);
                    dismissProgressDialog();
                    dialog.setMessage(msg);
                    dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomerCallManager customerCallManager = new CustomerCallManager(getActivity());
                            for (UUID customerId :
                                    result) {
                                try {
                                    customerCallManager.removeCall(CustomerCallType.SendData, customerId);
                                } catch (DbException e) {
                                    Timber.e(e);
                                }
                            }
                        }
                    });
                    dialog.setNeutralButton(R.string.no, null);
                    dialog.setIcon(Icon.Error);
                    dialog.show();
                    setRunning(false);
                } else {
//                    MainVaranegarActivity activity = getActivity();
//                    if (activity != null && !activity.isFinishing()) {
//                        progressDialog = new ProgressDialog(activity);
//                        progressDialog.setTitle(getActivity().getString(R.string.please_wait));
//                        progressDialog.setMessage(getActivity().getString(R.string.sending_customer_action));
//                        progressDialog.setCancelable(false);
//                        progressDialog.show();
//                    }
                    TourManager tourManager = new TourManager(getActivity());
                    tourManager.populatedAndSendTour(getSelectedId(), new TourManager.TourCallBack() {
                        @Override
                        public void onSuccess() {
                            Timber.i("tour data for customer id = " + getSelectedId().toString() + " was sent successfully.");
                            dismissProgressDialog();
                            runActionCallBack();
                            setRunning(false);
                        }

                        @Override
                        public void onFailure(String error) {
                            Timber.e(error);
                            dismissProgressDialog();
                            MainVaranegarActivity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                dialog.setIcon(Icon.Error);
                                dialog.setTitle(R.string.error);
                                dialog.setMessage(error);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            }
                            setRunning(false);
                        }

                        @Override
                        public void onProgressChanged(String progress) {

                        }
                    });
                }
            }

        });


    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {
                Timber.d(ignored);
            }
    }
}
