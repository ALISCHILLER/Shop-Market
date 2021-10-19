package com.varanegar.vaslibrary.manager.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.client.android.CaptureActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customer.CustomerBarcode;
import com.varanegar.vaslibrary.model.customer.CustomerBarcodeModel;
import com.varanegar.vaslibrary.model.customer.CustomerBarcodeModelRepository;
import com.varanegar.vaslibrary.model.customer.CustomerModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.fragment.settlement.IOnActivityResultListener;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.framework.database.DbException;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Elnaz on 6/15/2020.
 */

public class CustomerBarcodeManager extends BaseManager<CustomerBarcodeModel> implements IOnActivityResultListener {
    Context context;
    public static int barcodeRequestCode = 1001;

    public CustomerBarcodeManager(@NonNull Context context) {
        super(context, new CustomerBarcodeModelRepository());
        this.context = context;
    }
    public void sync(final UpdateCall call) {
        CustomerApi customerApi = new CustomerApi(getContext());
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.CustomerBarcode);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        final SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        if (settingsId == null) {
            call.failure(getContext().getString(R.string.settings_id_not_found));
            return;
        }
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null) {
            call.failure(getContext().getString(R.string.user_not_found));
            return;
        }
        final String dealerId = userModel.UniqueId.toString();
        customerApi.runWebRequest(customerApi.getCustomerBarcode(dateString, dealerId, settingsId.Value), new WebCallBack<List<CustomerBarcodeModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerBarcodeModel> result, Request request) {
                try {
                    deleteAll(); // barcode webservice doesn't check dataAfter
                    if (result != null && result.size() > 0) {
                        try {
                            insert(result);
                            new UpdateManager(getContext()).addLog(UpdateKey.CustomerBarcode);
                            Timber.i("Updating customer barcode completed");
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("Updating customer barcode completed. List of barcode was empty");
                        call.success();
                    }
                } catch (DbException e) {
                    Timber.e(e);
                    call.failure(getContext().getString(R.string.error_deleting_old_data));
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

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                call.failure(getContext().getString(R.string.request_canceled));
            }
        });
    }

    public List<String> getCustomerBarcode(UUID customerId) {
        Query query = new Query();
        query.select(CustomerBarcode.Barcode).from(CustomerBarcode.CustomerBarcodeTbl).whereAnd(Criteria.equals(CustomerBarcode.CustomerUniqueId, customerId));
        return VaranegarApplication.getInstance().getDbHandler().getString(query);
    }

    public void readBarcode () {
        VasActivity activity = (VasActivity) context;
        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SAVE_HISTORY", false);
        activity.startActivityForResult(intent, barcodeRequestCode);
        activity.customerBarcodeManagerListener = this;
    }

    @Override
    public void onReceiveResult(Context context, int requestCode, int resultCode, @Nullable Intent data, @Nullable Bundle bundle) {
        if (resultCode == RESULT_OK) {
            String barcode = data.getStringExtra("SCAN_RESULT");
            UpdateManager updateManager = new UpdateManager(getContext());
            updateManager.saveBarcode(barcode);
            VaranegarApplication.getInstance().save("dd003d32-4f05-423f-b7ba-3ccc9f54fb39",barcode);
        } else if (resultCode == RESULT_CANCELED) {
            Timber.i("SCAN_RESULT", "RESULT_CANCELED");
        }
    }
}
