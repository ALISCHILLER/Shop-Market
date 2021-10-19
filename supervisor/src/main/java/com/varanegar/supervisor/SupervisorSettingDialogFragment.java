package com.varanegar.supervisor;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.CenterSysConfigModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.personnel.UserApi;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by atp on 8/21/2016.
 */
public class SupervisorSettingDialogFragment extends CuteDialogWithToolbar {
    private SettingsUpdateListener updateListener;
    private PairedItemsEditable ipPairedItemsEditable;
    private PairedItemsEditable localIpPairedItemsEditable;
    private SysConfigManager sysConfigManager;
    private ActionProcessButton getButton;
    private BaseRecyclerAdapter<CenterSysConfigModel> adapter;
    private View view;

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    public void setConfigListener(SettingsUpdateListener listener) {
        this.updateListener = listener;
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.get_settings);
        setSizingPolicy(SizingPolicy.Medium);
        view = inflater.inflate(R.layout.fragment_supervisor_settings_dialog, container, false);
        getButton = (ActionProcessButton) view.findViewById(R.id.getSettingsButton);
        getButton.setMode(ActionProcessButton.Mode.ENDLESS);

        ipPairedItemsEditable = (PairedItemsEditable) view.findViewById(R.id.ipExitText);
        localIpPairedItemsEditable = (PairedItemsEditable) view.findViewById(R.id.local_ip_edit_text);
        sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel serverAddress = sysConfigManager.read(ConfigKey.ValidServerAddress, SysConfigManager.local);
        if (serverAddress != null) {
            ipPairedItemsEditable.setValue(serverAddress.Value);
        }
        SysConfigModel localServerAddress = sysConfigManager.read(ConfigKey.LocalServerAddress, SysConfigManager.local);
        if (localServerAddress != null) {
            localIpPairedItemsEditable.setValue(localServerAddress.Value);
        }
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ip = ipPairedItemsEditable.getValue();
                if (ip != null && !ip.isEmpty() && !HelperMethods.validateUrl(ip)) {
                    ipPairedItemsEditable.setError(getString(R.string.url_is_not_correct));
                    ipPairedItemsEditable.requestFocus();
                    return;
                }

                final String localIpAddress = localIpPairedItemsEditable.getValue();
                if (localIpAddress == null || !HelperMethods.validateUrl(localIpAddress)) {
                    localIpPairedItemsEditable.setError(getString(R.string.url_is_not_correct));
                    localIpPairedItemsEditable.requestFocus();
                    return;
                }
                if (!Connectivity.isConnected(getContext())) {
                    ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                    connectionSettingDialog.show(getChildFragmentManager(), "ConnectionSettingDialog");
                    return;
                }
                setDisabled();
                getButton.setProgress(1);
                try {
                    sysConfigManager.deleteAll();
                    UserManager userManager = new UserManager(getContext());
                    userManager.deleteAll();
                    sysConfigManager.save(ConfigKey.ValidServerAddress, ip, SysConfigManager.local);
                    sysConfigManager.save(ConfigKey.LocalServerAddress, localIpAddress, SysConfigManager.local);
                    PingApi pingApi = new PingApi();
                    pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
                        @Override
                        public void done(final String ipAddress) {
                            sysConfigManager.getOwnerKeys(new SysConfigManager.IOwnerKeyResponse() {
                                @Override
                                public void onSuccess(List<CenterSysConfigModel> ownerKeys) {
                                    MainVaranegarActivity activity = getVaranegarActvity();
                                    if (activity != null && !activity.isFinishing()) {
                                        if (ownerKeys.size() == 1) {
                                            try {
                                                sysConfigManager.setOwnerKeys(ownerKeys.get(0));
                                                onOwnerKeySelected();
                                            } catch (Exception e) {
                                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                                dialog.setIcon(Icon.Error);
                                                dialog.setTitle(R.string.error);
                                                dialog.setMessage(R.string.error_saving_request);
                                                dialog.setPositiveButton(R.string.ok, null);
                                                dialog.show();
                                                Timber.e(e);
                                            }
                                        } else {
                                            BaseRecyclerView centerRecycler = (BaseRecyclerView) view.findViewById(R.id.center_recycler_view);
                                            showCenters();
                                            adapter = new BaseRecyclerAdapter<CenterSysConfigModel>(getVaranegarActvity(), ownerKeys) {
                                                @Override
                                                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                                    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.center_owners_row, parent, false);
                                                    return new CenterSysConfigViewHolder(itemView, adapter, getContext());
                                                }
                                            };
                                            centerRecycler.setAdapter(adapter);
                                            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<CenterSysConfigModel>() {
                                                @Override
                                                public void run(int position) {

                                                    try {
                                                        sysConfigManager.setOwnerKeys(adapter.get(position));
                                                        onOwnerKeySelected();
                                                    } catch (Exception e) {
                                                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                                        dialog.setIcon(Icon.Error);
                                                        dialog.setTitle(R.string.error);
                                                        dialog.setMessage(R.string.error_saving_request);
                                                        dialog.setPositiveButton(R.string.ok, null);
                                                        dialog.show();
                                                        Timber.e(e);
                                                    }

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                    Timber.e("Ip address not found");
                                    setEnabled();
                                    getButton.setProgress(-1);
                                    getButton.setText(R.string.ip_addresses_are_not_found);
                                }
                            });


                        }

                        @Override
                        public void failed() {
                            Timber.e("Ip address not found");
                            setEnabled();
                            getButton.setProgress(-1);
                            getButton.setText(R.string.ip_addresses_are_not_found);

                        }
                    });
                } catch (Exception ex) {

                }

            }

        });
        return view;
    }

    private void showCenters() {
        view.findViewById(R.id.settings_linear).setVisibility(View.GONE);
        view.findViewById(R.id.centers_linear).setVisibility(View.VISIBLE);
    }

    private void showSettings() {
        view.findViewById(R.id.settings_linear).setVisibility(View.VISIBLE);
        view.findViewById(R.id.centers_linear).setVisibility(View.GONE);
    }

    private void onOwnerKeySelected() {
        sysConfigManager.getInitialSettings(new UpdateCall() {
            @Override
            protected void onSuccess() {
                final UserApi userApi = new UserApi(getContext());
                userApi.runWebRequest(userApi.getSupervisorUsers(), new WebCallBack<List<UserModel>>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(List<UserModel> result, Request request) {
                        UserManager userManager = new UserManager(getContext());
                        try {
                            userManager.insertOrUpdate(result);
                            new UpdateManager(getContext()).addLog(UpdateKey.UserList);
                            getButton.setProgress(100);
                            if (updateListener != null)
                                updateListener.onSettingsUpdated();
                            dismiss();
                        } catch (Exception e) {
                            Timber.e(e);
                            setEnabled();
                            getButton.setProgress(-1);
                            getButton.setText(R.string.data_error);
                        }
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            showSettings();
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setIcon(Icon.Error);
                            dialog.setTitle(R.string.error);
                            dialog.setMessage(err);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                            setEnabled();
                            getButton.setProgress(-1);
                            getButton.setText(R.string.web_api_failure);
                        }
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            showSettings();
                            Timber.e(t);
                            setEnabled();
                            getButton.setProgress(-1);
                            getButton.setText(R.string.network_error);
                        }
                    }
                });
            }

            @Override
            protected void onFailure(String error) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    showSettings();
                    CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                    cuteMessageDialog.setIcon(Icon.Error);
                    cuteMessageDialog.setTitle(R.string.error);
                    cuteMessageDialog.setMessage(error);
                    cuteMessageDialog.setNeutralButton(R.string.ok, null);
                    cuteMessageDialog.show();
                    setEnabled();
                    getButton.setProgress(-1);
                    getButton.setText(error);
                }
            }
        });
    }

    private void setEnabled() {
        setClosable(true);
        ipPairedItemsEditable.setEnabled(true);
        localIpPairedItemsEditable.setEnabled(true);
    }

    private void setDisabled() {
        setClosable(false);
        ipPairedItemsEditable.setEnabled(false);
        localIpPairedItemsEditable.setEnabled(false);
    }

    public interface SettingsUpdateListener {
        void onSettingsUpdated();
    }

    private class CenterSysConfigViewHolder extends BaseViewHolder<CenterSysConfigModel> {

        public CenterSysConfigViewHolder(View itemView, BaseRecyclerAdapter<CenterSysConfigModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            ((TextView) itemView.findViewById(R.id.center_name_id)).setText(recyclerAdapter.get(position).Title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerAdapter.runItemClickListener(position);
                }
            });
        }
    }
}
