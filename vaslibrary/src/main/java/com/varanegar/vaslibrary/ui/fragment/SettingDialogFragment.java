package com.varanegar.vaslibrary.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.enums.PreUrl;
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
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;
import com.varanegar.vaslibrary.ui.fragment.picturecustomer.NoPictureReasonDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.personnel.UserApi;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by atp on 8/21/2016.
 */
public class SettingDialogFragment extends CuteDialogWithToolbar {
    private SettingsUpdateListener updateListener;
    private EditText firstExternalIpEditText;
    private EditText secondExternalIpEditText;
    private PairedItemsEditable idPairedItemsEditable;
    private EditText localIpEditText;
    private SysConfigManager sysConfigManager;
    private ActionProcessButton getButton;
    private String id;
    private BaseRecyclerAdapter<CenterSysConfigModel> adapter;
    private View view;
    private Spinner firstExternalSpinner;
    private Spinner secondExternalSpinner;
    private Spinner localSpinner;
    private int pluss;

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
        view = inflater.inflate(R.layout.fragment_settings_dialog, container, false);
        getButton = (ActionProcessButton) view.findViewById(R.id.getSettingsButton);
        getButton.setMode(ActionProcessButton.Mode.ENDLESS);

        firstExternalSpinner = view.findViewById(R.id.first_external_spinner);
        secondExternalSpinner = view.findViewById(R.id.second_external_spinner);
        localSpinner = view.findViewById(R.id.local_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.setting_dialog_spinner_item, new String[]{getString(R.string.http), getString(R.string.https)}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setPadding(0, 0, 0, 0);
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstExternalSpinner.setAdapter(spinnerAdapter);
        secondExternalSpinner.setAdapter(spinnerAdapter);
        localSpinner.setAdapter(spinnerAdapter);
        final LinearLayout secondExternalIpLayout = view.findViewById(R.id.second_external_ip_layout);
        ImageView addImageView = view.findViewById(R.id.add_external_ip);
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pluss++;
                if (pluss==5) {

                    InsertPinDialog dialog = new InsertPinDialog();
                    dialog.setCancelable(false);
                    dialog.setClosable(false);
                    dialog.setValues("8585075751");
                    dialog.setOnResult(new InsertPinDialog.OnResult() {
                        @Override
                        public void done() {
                            pluss = 0;
                            firstExternalIpEditText.setEnabled(true);
                            localIpEditText.setEnabled(true);

                        }

                        @Override
                        public void failed(String error) {
                            Timber.e(error);
                            pluss = 0;
                            if (error.equals(getActivity().getString(R.string.pin_code_in_not_correct))) {
                                printFailed(getActivity(), error);
                            } else {

                            }
                        }
                    });
                    dialog.show(requireActivity().getSupportFragmentManager(), "InsertPinDialog");


//                    view.setEnabled(false);
//                    ((ImageView) view).setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
//                    secondExternalIpLayout.setVisibility(View.VISIBLE);

                }
            }
        });
        firstExternalIpEditText = view.findViewById(R.id.first_external_ip_edit_text);
        secondExternalIpEditText = view.findViewById(R.id.second_external_ip_edit_text);
        localIpEditText = view.findViewById(R.id.local_ip_edit_text);
        idPairedItemsEditable = (PairedItemsEditable) view.findViewById(R.id.idEditText);
        sysConfigManager = new SysConfigManager(getContext());


        firstExternalIpEditText.setEnabled(false);
        localIpEditText.setEnabled(false);
        firstExternalIpEditText.setText("192.168.50.110:8080");
        localIpEditText.setText("192.168.50.110:8080");

        SysConfigModel serverAddress = sysConfigManager.read(ConfigKey.ValidServerAddress, SysConfigManager.local);
        if (serverAddress != null && !serverAddress.Value.isEmpty()) {
            String[] ips = serverAddress.Value.split(",");
            if (ips.length > 1) {
                addImageView.setEnabled(false);
                addImageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                secondExternalIpLayout.setVisibility(View.VISIBLE);
                secondExternalIpEditText.setText(removeHttp(ips[1]));
            }
            firstExternalIpEditText.setText(removeHttp(ips[0]));
        }
        SysConfigModel localServerAddress = sysConfigManager.read(ConfigKey.LocalServerAddress, SysConfigManager.local);
        if (localServerAddress != null) {
            localIpEditText.setText(removeHttp(localServerAddress.Value));
        }
        SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        if (settingsId != null) {
            idPairedItemsEditable.setValue(settingsId.Value);
        }

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("Url", Context.MODE_PRIVATE);
        String exPreUrl1 = sharedPreferences.getString("FirstExternalPreUrl", PreUrl.HTTP.toString());
        final String[] firstExternalPreUrl = {spinnerAdapter.getItem(0)};
        if (exPreUrl1.equalsIgnoreCase(PreUrl.HTTPS.toString())) {
            firstExternalSpinner.setSelection(1);
            firstExternalPreUrl[0] = spinnerAdapter.getItem(1);
        } else {
            firstExternalSpinner.setSelection(0);
            firstExternalPreUrl[0] = spinnerAdapter.getItem(0);
        }
        String exPreUrl2 = sharedPreferences.getString("SecondExternalPreUrl", PreUrl.HTTP.toString());
        final String[] secondExternalPreUrl = {spinnerAdapter.getItem(0)};
        if (exPreUrl2.equalsIgnoreCase(PreUrl.HTTPS.toString())) {
            secondExternalSpinner.setSelection(1);
            secondExternalPreUrl[0] = spinnerAdapter.getItem(1);
        } else {
            secondExternalSpinner.setSelection(0);
            secondExternalPreUrl[0] = spinnerAdapter.getItem(0);
        }
        String loPreUrl = sharedPreferences.getString("LocalPreUrl", PreUrl.HTTP.toString());
        final String[] localPreUrl = {spinnerAdapter.getItem(0)};
        if (loPreUrl.equalsIgnoreCase(PreUrl.HTTPS.toString())) {
            localSpinner.setSelection(1);
            localPreUrl[0] = spinnerAdapter.getItem(1);
        } else {
            localSpinner.setSelection(0);
            localPreUrl[0] = spinnerAdapter.getItem(0);
        }
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        firstExternalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                firstExternalPreUrl[0] = adapterView.getItemAtPosition(i).toString();
                if (i == 0)
                    editor.putString("FirstExternalPreUrl", PreUrl.HTTP.toString());
                else
                    editor.putString("FirstExternalPreUrl", PreUrl.HTTPS.toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        secondExternalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                secondExternalPreUrl[0] = adapterView.getItemAtPosition(i).toString();
                if (i == 0)
                    editor.putString("SecondExternalPreUrl", PreUrl.HTTP.toString());
                else
                    editor.putString("SecondExternalPreUrl", PreUrl.HTTPS.toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                localPreUrl[0] = adapterView.getItemAtPosition(i).toString();
                if (i == 0)
                    editor.putString("LocalPreUrl", PreUrl.HTTP.toString());
                else
                    editor.putString("LocalPreUrl", PreUrl.HTTPS.toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstIp = firstExternalIpEditText.getText().toString();
                String firstExternalIpAddressUrl = "";
                if (firstIp != null && !firstIp.isEmpty()) {
                    firstExternalIpAddressUrl = firstExternalPreUrl[0] + firstIp.trim();
                    if (firstIp.toLowerCase().startsWith("http")) {
                        firstExternalIpEditText.setError(getString(R.string.enter_address_without_http_or_https));
                        firstExternalIpEditText.requestFocus();
                        return;
                    }
                    String[] ip = firstIp.split(",");
                    if (ip.length > 1) {
                        firstExternalIpEditText.setError(getString(R.string.enter_one_address));
                        firstExternalIpEditText.requestFocus();
                        return;
                    }
                    if (!HelperMethods.validateUrl(firstExternalIpAddressUrl)) {
                        firstExternalIpEditText.setError(getString(R.string.url_is_not_correct));
                        firstExternalIpEditText.requestFocus();
                        return;
                    }
                }
                final String secondIp = secondExternalIpEditText.getText().toString();
                String secondExternalIpAddressUrl = "";
                if (secondIp != null && !secondIp.isEmpty()) {
                    secondExternalIpAddressUrl = secondExternalPreUrl[0] + secondIp.trim();
                    if (secondIp.toLowerCase().startsWith("http")) {
                        secondExternalIpEditText.setError(getString(R.string.enter_address_without_http_or_https));
                        secondExternalIpEditText.requestFocus();
                        return;
                    }
                    String[] ip = secondIp.split(",");
                    if (ip.length > 1) {
                        secondExternalIpEditText.setError(getString(R.string.enter_one_address));
                        secondExternalIpEditText.requestFocus();
                        return;
                    }
                    if (!HelperMethods.validateUrl(secondExternalIpAddressUrl)) {
                        secondExternalIpEditText.setError(getString(R.string.url_is_not_correct));
                        secondExternalIpEditText.requestFocus();
                        return;
                    }
                }
                String externalIpAddressUrl = "";
                if (!firstExternalIpAddressUrl.isEmpty() && !secondExternalIpAddressUrl.isEmpty())
                    externalIpAddressUrl = firstExternalIpAddressUrl + "," + secondExternalIpAddressUrl;
                else if (!firstExternalIpAddressUrl.isEmpty())
                    externalIpAddressUrl = firstExternalIpAddressUrl;
                else if (!secondExternalIpAddressUrl.isEmpty())
                    externalIpAddressUrl = secondExternalIpAddressUrl;
                id = idPairedItemsEditable.getValue();
                if (id == null || id.isEmpty()) {
                    idPairedItemsEditable.setError(getString(R.string.please_insert_id));
                    idPairedItemsEditable.requestFocus();
                    return;
                }
                final String localIpAddress = localIpEditText.getText().toString();
                String localIpAddressUrl = "";
                if (localIpAddress != null)
                    localIpAddressUrl = localPreUrl[0] + localIpAddress.trim();
                if (localIpAddress != null && localIpAddress.toLowerCase().startsWith("http")) {
                    localIpEditText.setError(getString(R.string.enter_address_without_http_or_https));
                    localIpEditText.requestFocus();
                    return;
                }
                if (localIpAddress == null || !HelperMethods.validateUrl(localIpAddressUrl)) {
                    localIpEditText.setError(getString(R.string.url_is_not_correct));
                    localIpEditText.requestFocus();
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
                    sysConfigManager.save(ConfigKey.ValidServerAddress, externalIpAddressUrl, SysConfigManager.local);
                    sysConfigManager.save(ConfigKey.LocalServerAddress, localIpAddressUrl, SysConfigManager.local);
                    sysConfigManager.save(ConfigKey.SettingsId, id, SysConfigManager.local);
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
                userApi.runWebRequest(userApi.getAll(id), new WebCallBack<List<UserModel>>() {
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
       // firstExternalIpEditText.setEnabled(true);
        secondExternalIpEditText.setEnabled(true);
        idPairedItemsEditable.setEnabled(true);
       // localIpEditText.setEnabled(true);
        firstExternalSpinner.setEnabled(true);
        secondExternalSpinner.setEnabled(true);
        localSpinner.setEnabled(true);
    }

    private void setDisabled() {
        setClosable(false);
        firstExternalIpEditText.setEnabled(false);
        secondExternalIpEditText.setEnabled(false);
        idPairedItemsEditable.setEnabled(false);
        localIpEditText.setEnabled(false);
        firstExternalSpinner.setEnabled(false);
        secondExternalSpinner.setEnabled(false);
        localSpinner.setEnabled(false);
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
    private void printFailed(Context context, String error) {
        try {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Warning);
            dialog.setTitle(R.string.DeliveryReasons);
            dialog.setMessage(error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e1) {
            Timber.e(e1);
        }
    }
    private String removeHttp(String ip) {
        return ip.replaceAll(getString(R.string.http), "").replaceAll(getString(R.string.https), "");
    }
}
