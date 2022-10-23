package com.varanegar.vaslibrary.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.logging.LogConfig;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.model.PCustomerInventoryReportModel;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import java.util.List;
import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by m-latifi on 10/19/2022.
 */

public class SendCrashLogActivity extends MainVaranegarActivity {

    private TextView textViewWaiting;
    private Button buttonClose;

    //---------------------------------------------------------------------------------------------- onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_log);
        init();
        setListener();
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- checkStoragePermission
    @Override
    protected boolean checkStoragePermission() {
        return false;
    }
    //---------------------------------------------------------------------------------------------- checkStoragePermission


    //---------------------------------------------------------------------------------------------- checkCameraPermission
    @Override
    protected boolean checkCameraPermission() {
        return false;
    }
    //---------------------------------------------------------------------------------------------- checkCameraPermission


    //---------------------------------------------------------------------------------------------- checkLocationPermission
    @Override
    protected boolean checkLocationPermission() {
        return false;
    }
    //---------------------------------------------------------------------------------------------- checkLocationPermission


    //---------------------------------------------------------------------------------------------- createLogConfig
    @Override
    protected LogConfig createLogConfig() {
        return null;
    }
    //---------------------------------------------------------------------------------------------- createLogConfig


    //---------------------------------------------------------------------------------------------- init
    private void init() {
        String error = getIntent().getExtras().getString("data");
        Timber.e(error);
        Log.i("meri", error);
        buttonClose = findViewById(R.id.buttonClose);
        textViewWaiting = findViewById(R.id.textViewWaiting);
        buttonClose.setVisibility(View.GONE);
        textViewWaiting.setVisibility(View.VISIBLE);
//        requestForReport();
    }
    //---------------------------------------------------------------------------------------------- init


    //---------------------------------------------------------------------------------------------- setListener
    private void setListener() {
        buttonClose.setOnClickListener(v -> System.exit(1));
    }
    //---------------------------------------------------------------------------------------------- setListener



    //---------------------------------------------------------------------------------------------- requestForReport
    private void requestForReport() {
        textViewWaiting.setText(getResources().getString(R.string.inProgressSendReport));
        textViewWaiting.setEnabled(false);
        ReportApi invoiceReportApi = new ReportApi(this);
        invoiceReportApi.runWebRequest(crashReportApi(), new WebCallBack<List<PCustomerInventoryReportModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<PCustomerInventoryReportModel> result, Request request) {
                textViewWaiting.setVisibility(View.GONE);
                buttonClose.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                textViewWaiting.setText(getResources().getString(R.string.reTrySendRequestToserver));
                textViewWaiting.setEnabled(false);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                textViewWaiting.setText(getResources().getString(R.string.reTrySendRequestToserver));
            }
        });

    }
    //---------------------------------------------------------------------------------------------- requestForReport


    //---------------------------------------------------------------------------------------------- crashReportApi
    private Call<List<PCustomerInventoryReportModel>> crashReportApi() {
/*        CustomerPathViewModel pathView = new CustomerPathViewManager(this).getCustomerPathViewModel(customerId);
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null)
            return null;
        InventoryRequest request = new InventoryRequest(
                getDateString(startDate),
                getDateString(endDate),
                userModel.UniqueId.toString(),
                customerId.toString(),
                pathView.VisitTemplatePathId.toString()
        );
        return new ReportApi(getContext()).inventoryReport(request);*/
        return null;
    }
    //---------------------------------------------------------------------------------------------- crashReportApi


}
