package com.varanegar.vaslibrary.manager.tourmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.gson.Gson;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.prefs.Preferences;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CallInvoiceLineBatchQtyDetailManager;
import com.varanegar.vaslibrary.manager.CallOrderLineBatchQtyDetailManager;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.CustomerInventoryManager;
import com.varanegar.vaslibrary.manager.CustomerInventoryQtyManager;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.DistributionCustomerCallManager;
import com.varanegar.vaslibrary.manager.EVCItemStatuesCustomersManager;
import com.varanegar.vaslibrary.manager.InvoiceLineQtyManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductInventoryManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.RequestReportViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.VisitTemplatePathCustomerManager;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToPartyManager;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToPartyModel;
import com.varanegar.vaslibrary.manager.cataloguelog.CatalogueLogManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;
import com.varanegar.vaslibrary.manager.customercall.CallInvoiceLineManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLinesQtyTempManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLinesTempManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLineQtyManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLinesRequestManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCalls;
import com.varanegar.vaslibrary.manager.invoiceinfo.InvoiceInfoViewManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.SendTourEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.SendTourLocationViewModel;
import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.manager.printer.CancelInvoiceManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.productrequest.RequestLineViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireAnswerManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireCustomerViewManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireLineManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateQueue;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.CatalogueLog;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLine;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.VisitTemplatePathCustomer.VisitTemplatePathCustomerModel;
import com.varanegar.vaslibrary.model.call.CallInvoiceLine;
import com.varanegar.vaslibrary.model.call.CallInvoiceLineModel;
import com.varanegar.vaslibrary.model.call.CallOrderLine;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoice;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrder;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturn;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModel;
import com.varanegar.vaslibrary.model.call.ReturnLineQty;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyModel;
import com.varanegar.vaslibrary.model.call.ReturnLinesRequestModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTemp;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTempModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTemp;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTempModel;
import com.varanegar.vaslibrary.model.catalog.Catalog;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceNestle;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceVnLite;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderLinesItemStatutes.CustomerCallOrderLinesItemStatutes;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallReturnLinesWithPromo.CustomerCallReturnLinesWithPromo;
import com.varanegar.vaslibrary.model.customeractiontime.CustomerActionTime;
import com.varanegar.vaslibrary.model.customercall.CustomerCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customercall.CustomerPrintCount;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventory;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryModel;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryQty;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryQtyModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerInvoicePayment;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPrice;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerCall;
import com.varanegar.vaslibrary.model.evcitemstatuessdscustomers.EVCItemStatuesCustomersModel;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQty;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQtyModel;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQty;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQty;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrize;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
import com.varanegar.vaslibrary.model.payment.Payment;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomer;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerViewModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureFile;
import com.varanegar.vaslibrary.model.picturesubject.PictureSubject;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateDetail;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateHeader;
import com.varanegar.vaslibrary.model.priceclass.PriceClassVnLite;
import com.varanegar.vaslibrary.model.printer.CancelInvoice;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireAnswer;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireAnswerModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomer;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireCustomerViewModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireHeader;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLine;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineModel;
import com.varanegar.vaslibrary.model.questionnaire.QuestionnaireLineOption;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.tour.TourStatus;
import com.varanegar.vaslibrary.print.SentTourInfoPrint.TourInfo;
import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCancelInvoiceViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallCatalogViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallOrderLineBatchQtyDetailViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallOrderLinePromotionViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallOrderLineViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallOrderPrizeViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallOrderViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallPaymentDetailViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallPaymentViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallPictureDetailViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallPictureViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallQuestionnaireAnswerViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallQuestionnaireViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallReturnLineQtyDetailViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallReturnLineViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallReturnViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallStockLevelLineQtyDetailViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallStockLevelLineViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallStockLevelViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerCallViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerQtyDetailViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerUpdateDataViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCustomerUpdateLocationViewModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetRequestLineModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetTourViewModel;
import com.varanegar.vaslibrary.webapi.tour.TourApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

/**
 * Created by A.Jafarzadeh on 7/2/2017.
 */

public class TourManager {

    private static final String FILE_NAME = "tour.dat";
    private final CustomerCallManager callManager;
    private Context context;
    private List<CustomerCallModel> calls;
    private CustomerCalls customerCalls;
    private static SyncService.ServiceBinder receiveTourServiceBinder;
    private static SendTourService.ServiceBinder sendTourServiceBinder;
    private String tourNo = null;

    private ServiceConnection receiveTourServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            receiveTourServiceBinder = (SyncService.ServiceBinder) iBinder;
            receiveTourServiceBinder.getService().startTourDownload();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Timber.e("Service unbounded");
            receiveTourServiceBinder = null;
            TourModel tourModel = loadTour();
            if (tourModel != null && tourModel.Status != TourStatus.Failed && tourModel.Status != TourStatus.Confirmed)
                deleteTour();
        }
    };

    private static TourModel staticTourModel;
    private boolean stopSendingTour;
    private Call<ResponseBody> sendTourDataCall;
    private boolean dataSent = false;

    public static String getLatestError() {
        String err = latestError;
        latestError = null;
        return err;
    }

    private static String latestError;

    public TourManager(Context context) {
        this.context = context;
        callManager = new CustomerCallManager(context);
    }

    public void testTourSyncViewModel() {
        final SyncGetTourViewModel syncGetTourViewModel = new SyncGetTourViewModel(context, UUID.randomUUID(), 0);

        List<CustomerModel> customerModels = new CustomerManager(context).getAll();
        for (CustomerModel customerModel :
                customerModels) {
            CustomerCallManager callManager = new CustomerCallManager(context);
            calls = callManager.loadCalls(customerModel.UniqueId);
            customerCalls = new CustomerCalls(calls);
            if (callManager.isConfirmed(calls) && !callManager.isDataSent(calls, null)) {
                populateCustomerCallDataForSend(syncGetTourViewModel, customerModel);
            }
        }

        Gson gson = VaranegarGsonBuilder.build().create();
        String json = gson.toJson(syncGetTourViewModel);
        Timber.d(json);
    }

    public synchronized void saveTour(TourModel tour, Context context) throws IOException {
        staticTourModel = tour;
        String json = VaranegarGsonBuilder.build(false).create().toJson(tour);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILE_NAME, MODE_PRIVATE));
        outputStreamWriter.write(json);
        outputStreamWriter.close();
        loadTour();
    }

    public synchronized boolean isTourAvailable() {
        TourModel tourModel = loadTour();
        return tourModel != null && tourModel.Status == TourStatus.Confirmed;
    }

    public synchronized boolean isTourDownloading() {
        TourModel tourModel = loadTour();
        return tourModel != null && tourModel.Status == TourStatus.Downloading;
    }

    public synchronized boolean isTourSending() {
        TourModel tourModel = loadTour();
        return tourModel != null && tourModel.Status == TourStatus.IsSending;
    }

    @Nullable
    public synchronized TourModel loadTour() {
        if (staticTourModel != null)
            return staticTourModel;
        try {
            FileInputStream inputStream = context.openFileInput(FILE_NAME);
            byte[] buffer = new byte[1024];
            int n = 0;
            StringBuilder content = new StringBuilder();
            while ((n = inputStream.read(buffer)) != -1) {
                content.append(new String(buffer, 0, n));
            }
            String json = content.toString();
            staticTourModel = VaranegarGsonBuilder.build(false).create().fromJson(json, TourModel.class);
            return staticTourModel;
        } catch (FileNotFoundException e) {
            Timber.i("Tour file not found.");
            return null;
        } catch (Exception e) {
            Timber.e(e, "Loading tour file failed.");
            return null;
        }
    }

    private synchronized void deleteTour() {
        latestError = null;
        staticTourModel = null;
        context.deleteFile(FILE_NAME);
    }

    private synchronized void confirmTourClose(WebCallBack callBack) {
        TourApi tourApi = new TourApi(context);
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        tourApi.runWebRequest(tourApi.confirmTourReceived(loadTour().UniqueId.toString(), settingsId.Value), callBack);
    }

    public synchronized void confirmTourSend(@NonNull final TourCallBack updateCall) {
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                TourModel tourModel;
                tourModel = loadTour();
                SharedPreferences prefs = context.getSharedPreferences(TourReportFragment.IS_VIRTUAL, MODE_PRIVATE);
                if (prefs.getBoolean(TourReportFragment.IS_VIRTUAL, false)) {
                    Timber.i("getting virtual tour done");
                    updateCall.onSuccess();
                } else {
                    TourApi tourApi = new TourApi(context);
                    tourApi.runWebRequest(tourApi.confirmTourSent(tourModel.UniqueId.toString()), new WebCallBack<ResponseBody>() {
                        @Override
                        protected void onFinish() {

                        }

                        @Override
                        protected void onSuccess(ResponseBody result, Request request) {
                            Timber.i(result.toString());
                            updateCall.onSuccess();
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            String err = WebApiErrorBody.log(error, context);
                            updateCall.onFailure(err);
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            Timber.e(t);
                            updateCall.onFailure(t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void failed() {
                updateCall.onFailure(context.getString(R.string.network_error));
            }
        });

    }

    public synchronized void saveConfirm() throws IOException {
        latestError = null;
        TourModel tourModel = loadTour();
        if (tourModel != null)
            tourModel.Status = TourStatus.Confirmed;
        saveTour(tourModel, context);
    }



    public synchronized void saveFailure(String error) {
        latestError = error;
        TourModel tourModel = loadTour();
        if (tourModel != null) {
            tourModel.Status = TourStatus.Failed;
            try {
                saveTour(tourModel, context);
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    public synchronized void saveIsSending() {
        latestError = null;
        TourModel tourModel = loadTour();
        if (tourModel != null) {
            tourModel.Status = TourStatus.IsSending;
            try {
                saveTour(tourModel, context);
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    public static List<ModelProjection> getListOfTourTempTables() {
        return Arrays.asList(new ModelProjection[]{Catalog.CatalogTbl,
                CustomerCallOrder.CustomerCallOrderTbl,
                CallOrderLine.CallOrderLineTbl,
                CustomerCallOrderLinesItemStatutes.CustomerCallOrderLinesItemStatutesTbl,
                OrderLineQty.OrderLineQtyTbl,
                InvoiceLineQty.InvoiceLineQtyTbl,
                CustomerCallReturn.CustomerCallReturnTbl,
                ReturnLineQty.ReturnLineQtyTbl,
                CustomerCallReturnLinesWithPromo.CustomerCallReturnLinesWithPromoTbl,
                PictureSubject.PictureSubjectTbl,
                CustomerCall.CustomerCallTbl,
                PictureCustomer.PictureCustomerTbl,
                PictureFile.PictureFileTbl,
                PictureTemplateHeader.PictureTemplateHeaderTbl,
                PictureTemplateDetail.PictureTemplateDetailTbl,
                QuestionnaireCustomer.QuestionnaireCustomerTbl,
                QuestionnaireAnswer.QuestionnaireAnswerTbl,
                QuestionnaireHeader.QuestionnaireHeaderTbl,
                QuestionnaireLineOption.QuestionnaireLineOptionTbl,
                QuestionnaireLine.QuestionnaireLineTbl,
                CustomerInventory.CustomerInventoryTbl,
                CustomerInventoryQty.CustomerInventoryQtyTbl,
                CustomerInvoicePayment.CustomerInvoicePaymentTbl,
                ContractPriceVnLite.ContractPriceVnLiteTbl,
                ContractPriceNestle.ContractPriceNestleTbl,
                OrderPrize.OrderPrizeTbl,
                CustomerActionTime.CustomerActionTimeTbl,
                PriceClassVnLite.PriceClassVnLiteTbl,
                DistributionCustomerCall.DistributionCustomerCallTbl,
                CancelInvoice.CancelInvoiceTbl,
                CustomerPrintCount.CustomerPrintCountTbl,
                CustomerPrice.CustomerPriceTbl,
                Payment.PaymentTbl,
                RequestLine.RequestLineTbl,
                CustomerCallInvoice.CustomerCallInvoiceTbl,
                CallInvoiceLine.CallInvoiceLineTbl,
                InvoiceLineQty.InvoiceLineQtyTbl,
                CatalogueLog.CatalogueLogTbl,
                CallOrderLinesTemp.CallOrderLinesTempTbl,
                CallOrderLinesQtyTemp.CallOrderLinesQtyTempTbl});
    }


    public interface TourDownloadCallBack {
        void onFailure(String error);

        void onStart();
    }

    public synchronized void startDownload(final String tourNo, final TourDownloadCallBack call, final Class<? extends SyncService> syncClass) {
        if (isTourAvailable()) {
            Timber.d("Tour is available");
            call.onFailure(context.getString(R.string.there_is_a_tour_already));
            return;
        }
        if (isTourDownloading()) {
            Timber.d("Tour is downloading");
            call.onFailure(context.getString(R.string.tour_is_downloading_already));
            return;
        }
        try {
            new LocationManager(context).clearPoints();
            new TrackingLogManager(context).clearLogs();
        } catch (DbException e) {
            Timber.e(e);
        }
        deleteTour();
        UpdateQueue.getGroups().clear();
        Timber.d("tour file deleted");
        sync(tourNo, new UpdateCall() {
            @Override
            protected void onFinish() {
                super.onFinish();
            }

            @Override
            protected void onSuccess() {
                call.onStart();
                Timber.d("New tour file saved");
                if (receiveTourServiceBinder == null) {
                    Intent intent = new Intent(context, syncClass);
                    context.startService(intent);
                    context.bindService(intent, receiveTourServiceConnection, Context.BIND_AUTO_CREATE);
                } else {
                    receiveTourServiceBinder.getService().startTourDownload();
                }
            }

            @Override
            protected void onFailure(String error) {
                call.onFailure(error);
                Timber.d("Failed to download tour file");
            }
        });
    }

    public synchronized void stopDownload() {
        if (receiveTourServiceBinder == null) {
            TourModel tourModel = loadTour();
            if (tourModel != null && tourModel.Status != TourStatus.Failed && tourModel.Status != TourStatus.Confirmed)
                deleteTour();
        } else
            receiveTourServiceBinder.getService().stopTourDownload();
    }

    public synchronized boolean isStopping() {
        if (receiveTourServiceBinder == null)
            return false;
        else
            return receiveTourServiceBinder.getService().isStopping();
    }

    private void sync(String tourNo, @NonNull final UpdateCall updateCall) {
        final TourApi tourApi = new TourApi(context);
        String dealerId = UserManager.readFromFile(context).UniqueId.toString();
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        if (tourNo == null) {
            tourApi.runWebRequest(tourApi.getTour(dealerId, settingsId.Value,
                    VaranegarApplication.getInstance().getAppId().toString()), new WebCallBack<TourModel>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(TourModel result, Request request) {
                    if (result == null) {
                        Timber.wtf("Tour is null!");
                        updateCall.failure(context.getString(R.string.there_is_no_tour));
                    } else if (result.UniqueId == null) {
                        Timber.wtf("Tour id is null!");
                        updateCall.failure(context.getString(R.string.tour_id_is_null));
                    } else {
                        try {
                            result.LocalId = UUID.randomUUID();
                            result.Status = TourStatus.Downloading;
                            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                                result.DayVisitPathId = VisitTemplatePathCustomerManager.getDefaultDistributionPathId();
                            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)){
                                SharedPreferences sharedconditionCustomer = context.getSharedPreferences("Presale", Context.MODE_PRIVATE);
                                sharedconditionCustomer.edit().putString("ZarNotificationToken", String.valueOf(result.ZarNotificationToken)).apply();
                             }
                            result.StartTime = new Date();

                            saveTour(result, context);
                            Timber.i("tourId = " + result.UniqueId.toString());
                            updateCall.success();
                        } catch (IOException e) {
                            Timber.wtf(e);
                            updateCall.failure(context.getString(R.string.error_saving_tour_file));
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, context);
                    updateCall.failure(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t);
                    updateCall.failure(context.getString(R.string.network_error));
                }
            });
        } else {
            tourApi.runWebRequest(tourApi.getTour(tourNo), new WebCallBack<TourModel>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(TourModel result, Request request) {
                    if (result == null) {
                        Timber.wtf("Tour is null!");
                        updateCall.failure(context.getString(R.string.there_is_no_tour));
                    } else if (result.UniqueId == null) {
                        Timber.wtf("Tour id is null!");
                        updateCall.failure(context.getString(R.string.tour_id_is_null));
                    } else {
                        try {
                            result.LocalId = UUID.randomUUID();
                            result.Status = TourStatus.Downloading;
                            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                                result.DayVisitPathId = VisitTemplatePathCustomerManager.getDefaultDistributionPathId();
                            result.IsVirtual = true;
                            saveTour(result, context);
                            Timber.i("tourId = " + result.UniqueId.toString());
                            updateCall.success();
                        } catch (IOException e) {
                            Timber.wtf(e);
                            updateCall.failure(context.getString(R.string.error_saving_tour_file));
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, context);
                    updateCall.failure(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t);
                    updateCall.failure(context.getString(R.string.network_error));
                }
            });
        }
    }

    public void cancelVirtualTour() {
        TourModel tourModel = loadTour();
        Timber.i("Canceling virtual tour");
        Preferences preferences = new Preferences(context);
        preferences.clearAfterTourPreferences();
        emptyTempTables();
        int tourNo = tourModel.TourNo;
        deleteTour();
        Timber.i("Virtual tour no : " + tourNo + " canceled");
        try {
            long affectedRows = new LocationManager(context).clearPoints();
            Timber.i(affectedRows + " points removed from location manager");
            long a = new TrackingLogManager(context).clearLogs();
            Timber.i(a + " logs removed from Tracking log manager");
        } catch (DbException e) {
            Timber.e(e, "Removing points from location manager failed ");
        }
    }

    public void cancelTour(final TourCallBack callBack) {
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                final TourModel tourModel = loadTour();
                CustomerManager customerManager = new CustomerManager(context);
                List<CustomerModel> customerModels = customerManager.getCustomersWithDataSent();
                if (customerModels.size() == 0) {
                    TourApi tourApi = new TourApi(context);
                    tourApi.runWebRequest(tourApi.cancelTour(tourModel.UniqueId.toString()), new WebCallBack<ResponseBody>() {
                        @Override
                        protected void onFinish() {

                        }

                        @Override
                        protected void onSuccess(ResponseBody result, Request request) {
                            Timber.i("Canceling tour succeeded");
                            Preferences preferences = new Preferences(context);
                            preferences.clearAfterTourPreferences();
                            TourInfo tourInfo = createTourInfo();
                            if (tourInfo != null) {
                                final LocationManager locationManager = new LocationManager(context);
                                SendTourLocationViewModel locationViewModel = new SendTourLocationViewModel();
                                locationViewModel.eventData = new SendTourEventViewModel();
                                locationViewModel.eventData.CustomersCount = tourInfo.DayCustomersCount;
                                locationViewModel.eventData.VisitedCustomers = tourInfo.DayVisitedCount;
                                locationViewModel.eventData.OrderedCustomers = tourInfo.DayOrderedCount;
                                locationViewModel.eventData.SumOfOrdered = tourInfo.DayOrderSum;
                                locationViewModel.eventData.LackOfVisit = tourInfo.DayLackOfVisitCount;
                                locationViewModel.eventData.LackOfOrder = tourInfo.DayLackOfOrderCount;
                                locationViewModel.eventData.VisitToCustomer = new DecimalFormat("#.00").format(tourInfo.DayVisitRatio);
                                locationViewModel.eventData.Spd = tourInfo.Spd;
                                locationViewModel.eventData.TotalVisitTime = tourInfo.VisitTime;
                                locationViewModel.eventData.TotalTourTime = tourInfo.TourTime;
                                locationViewModel.eventData.TourId = tourInfo.TourId;
                                locationViewModel.eventData.TourNo = tourInfo.TourNo;
                                locationViewModel.eventData.PersonnelId = tourInfo.PersonnelId;
                                locationViewModel.eventData.Time = tourInfo.Time;
                                locationViewModel.eventData.description = context.getString(R.string.tour_canceled);
                                TrackingLogManager.addLog(context, LogType.EVENT, LogLevel.Info, "انصراف از تور");
                                locationManager.addTrackingPoint(locationViewModel, new OnSaveLocation() {
                                    @Override
                                    public void onSaved(LocationModel location) {
                                        Timber.i("Send tour point added.");
                                        locationManager.tryToSendItem(location);
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Timber.e(error);
                                    }
                                });
                            }
                            emptyTempTables();
                            int tourNo = tourModel.TourNo;
                            deleteTour();
                            Timber.i("Tour no : " + tourNo + " canceled");
                            try {
                                long affectedRows = new LocationManager(context).clearPoints();
                                Timber.i(affectedRows + " points removed from location manager");
                                long a = new TrackingLogManager(context).clearLogs();
                                Timber.i(a + " logs removed from Tracking log manager");
                                callBack.onSuccess();
                            } catch (DbException e) {
                                Timber.e(e, "Removing points from location manager failed ");
                                callBack.onSuccess();
                            }
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            String err = WebApiErrorBody.log(error, context);
                            callBack.onFailure(err);
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            Timber.d(t, "canceling tour failed");
                            callBack.onFailure(context.getString(R.string.network_error));
                        }
                    });

                } else {
                    Timber.d("Some customers order have been sent. We can not cancelCustomerOrders tour");
                    callBack.onFailure(context.getString(R.string.can_not_cancel_tour));
                }
            }

            @Override
            public void failed() {
                callBack.onFailure(context.getString(R.string.network_error));
            }
        });

    }

    private void emptyTempTables() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
            VaranegarApplication.getInstance().getDbHandler().emptyTables(true, OnHandQty.OnHandQtyTbl);
        VaranegarApplication.getInstance().getDbHandler().emptyTables(true, getListOfTourTempTables());
        new CustomerManager(context).updateIsNewCustomer();
        new UpdateManager(context).removeLog(UpdateKey.ProductRequest);
        new UpdateManager(context).removeLog(UpdateKey.TourStartTime);
        CustomerActionTimeManager.removeTimeOffset(context);
    }

    public interface VerifyCallBack {
        void onSuccess(List<UUID> customerIds);

        void onFailure(String error);
    }

    public void verifyData(@NonNull final VerifyCallBack callBack) {
        if (!isTourAvailable()) {
            callBack.onFailure(context.getString(R.string.tour_is_not_available));
            return;
        }
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                TourApi tourApi = new TourApi(context);
                TourModel tourModel = loadTour();
                CustomerManager customerManager = new CustomerManager(context);
                List<CustomerModel> customerModels = customerManager.getCustomersWithDataSent();
                List<UUID> customerIds = Linq.map(customerModels, new Linq.Map<CustomerModel, UUID>() {
                    @Override
                    public UUID run(CustomerModel item) {
                        return item.UniqueId;
                    }
                });
                tourApi.runWebRequest(tourApi.verifyData(tourModel.UniqueId.toString(), customerIds), new WebCallBack<List<UUID>>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(List<UUID> result, Request request) {
                        callBack.onSuccess(result);
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, context);
                        callBack.onFailure(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        callBack.onFailure(context.getString(R.string.network_error));
                    }
                });
            }

            @Override
            public void failed() {
                Timber.e("Ping failed");
                callBack.onFailure(context.getString(R.string.network_error));
            }
        });
    }

    /***
     * Use this for populate SyncGetTourViewModel for one customer.
     * @param customerId id of a customer
     * @param callBack callback
     */
    public void populatedAndSendTour(final UUID customerId, @Nullable final TourCallBack callBack) {
        try {
            calls = callManager.loadCalls(customerId);
            customerCalls = new CustomerCalls(calls);
            if (!isTourAvailable()) {
                onTourFailure(callBack, R.string.tour_is_not_available);
                return;
            }

            final CustomerManager customerManager = new CustomerManager(context);
            final CustomerModel customerModel = customerManager.getItem(customerId);
            if (customerModel == null) {
                Timber.wtf("customer id not found");
                throw new RuntimeException("customer id not found");
            }
            final CustomerCallManager callManager = new CustomerCallManager(context);
            CustomerCallModel callModel = callManager.loadCall(CustomerCallType.SendData, customerId);
            if (callModel != null && callModel.ConfirmStatus) {
                SysConfigManager sysConfigManager = new SysConfigManager(context);
                if (!SysConfigManager.isMultipleSendActive(context) || !VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                    onTourFailure(callBack, R.string.customer_operation_is_sent_already);
                    return;
                }
            }
            TourModel tourModel = loadTour();
            final SyncGetTourViewModel syncGetTourViewModel = new SyncGetTourViewModel(context, tourModel.UniqueId, tourModel.TourNo);
            populateCustomerCallDataForSend(syncGetTourViewModel, customerModel);
            Gson gson = VaranegarGsonBuilder.build().create();
            final String tourData = gson.toJson(syncGetTourViewModel);
            Timber.i("tour for customer id " + customerId.toString() + " \n " + tourData);
            PingApi pingApi = new PingApi();
            pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    populateAndSendFiles(customerId, new TourCallBack() {
                        @Override
                        public void onSuccess() {
                            TourApi tourApi = new TourApi(context);
                            SysConfigManager sysConfigManager = new SysConfigManager(context);
                            SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
                            tourApi.runWebRequest(tourApi.saveTourData(syncGetTourViewModel, settingsId.Value), new WebCallBack<ResponseBody>() {
                                @Override
                                protected void onFinish() {

                                }

                                @Override
                                protected void onSuccess(ResponseBody result, Request request) {
                                    Timber.i("Tour has been sent successfully for customer id = " + customerId.toString());
                                    try {
                                        callManager.saveSendCall(customerId);
                                        Timber.i("Tour was sent and confirmed");
                                        onTourSuccess(callBack);
                                    } catch (Exception ex) {
                                        Timber.e(ex);
                                        onTourFailure(callBack, R.string.error_saving_request);
                                    }
                                }

                                @Override
                                protected void onApiFailure(ApiError error, Request request) {
                                    Timber.e("Sending tour failed customer id = " + customerId.toString());
                                    String err = WebApiErrorBody.log(error, context);
                                    onTourFailure(callBack, err);
                                }

                                @Override
                                protected void onNetworkFailure(Throwable t, Request request) {
                                    Timber.e(t, request.url().toString());
                                    Timber.e("Network error, Sending tour failed customer id = " + customerId.toString());
                                    onTourFailure(callBack, R.string.network_error);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            Timber.e("connection to the server failed: " + error);
                            onTourFailure(callBack, error);
                        }

                        @Override
                        public void onProgressChanged(String progress) {

                        }
                    });
                }

                @Override
                public void failed() {
                    Timber.e("connection to the server failed");
                    onTourFailure(callBack, R.string.network_error);
                }
            });

        } catch (Throwable ex) {
            Timber.e(ex);
            onTourFailure(callBack, R.string.populating_tour_failed);
        }
    }

    /***
     * Use this for populate SyncGetTourViewModel for a group of customers who have operation.
     * @param callBack callback
     */
    public void populatedAndSendTour(List<CustomerModel> customerModels, @Nullable final TourCallBack callBack) {
        try {
            if (!isTourAvailable()) {
                onTourFailure(callBack, R.string.tour_is_not_available);
                return;
            }

            VaranegarApplication.getInstance().resetElapsedTime("Sending tour for customers who have confirmed operation. Populating data started.");
            TourModel tourModel = loadTour();
            final SyncGetTourViewModel syncGetTourViewModel = new SyncGetTourViewModel(context, tourModel.UniqueId, tourModel.TourNo);
            final List<UUID> customerIds = new ArrayList<>();

            for (CustomerModel customerModel :
                    customerModels) {
                CustomerCallManager callManager = new CustomerCallManager(context);
                calls = callManager.loadCalls(customerModel.UniqueId);
                customerCalls = new CustomerCalls(calls);
                if (callManager.isConfirmed(calls) && !callManager.isDataSent(calls, null)) {
                    populateCustomerCallDataForSend(syncGetTourViewModel, customerModel);
                    customerIds.add(customerModel.UniqueId);
                }
            }

            VaranegarApplication.getInstance().printElapsedTime("Sending tour for customers who have confirmed operation. Populating data finished.");
            Gson gson = VaranegarGsonBuilder.build().create();
            final String tourString = gson.toJson(syncGetTourViewModel);
            if (customerIds.size() > 0) {
                Timber.i("Tour data for customer ids = " + customerIds.toString() + "\n " + tourString);
                populateAndSendFiles(customerIds, new TourCallBack() {
                    @Override
                    public void onSuccess() {
                        PingApi pingApi = new PingApi();
                        pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                            @Override
                            public void done(String ipAddress) {
                                VaranegarApplication.getInstance().printElapsedTime("Sending tour for customers who have confirmed operation. Ping server was done");
                                TourApi tourApi = new TourApi(context);
                                SysConfigManager sysConfigManager = new SysConfigManager(context);
                                SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
                                sendTourDataCall = tourApi.saveTourData(syncGetTourViewModel, settingsId.Value);
                                tourApi.runWebRequest(sendTourDataCall, new WebCallBack<ResponseBody>() {
                                    @Override
                                    protected void onFinish() {
                                    }

                                    @Override
                                    protected void onSuccess(ResponseBody result, Request request) {
                                        int i = 0;
                                        Timber.i("Tour has been sent successfully for customer ids = " + customerIds.toString());
                                        for (UUID customerId : customerIds) {
                                            try {
                                                callManager.saveSendCall(customerId);
                                                i++;
                                            } catch (Exception ex) {
                                                Timber.e(ex);
                                            }
                                        }
                                        if (i == customerIds.size())
                                            onTourSuccess(callBack);
                                        else
                                            onTourFailure(callBack, R.string.error_saving_request);
                                    }


                                    @Override
                                    protected void onApiFailure(ApiError error, Request request) {
                                        Timber.e("Sending tour failed for customer ids = " + customerIds.toString());
                                        String err = WebApiErrorBody.log(error, context);
                                        onTourFailure(callBack, err);
                                    }

                                    @Override
                                    protected void onNetworkFailure(Throwable t, Request request) {
                                        Timber.e(t, request.url().toString());
                                        Timber.e("Network error, Sending tour failed for customer ids = " + customerIds.toString());
                                        onTourFailure(callBack, R.string.network_error);
                                    }
                                });
                            }

                            @Override
                            public void failed() {
                                Timber.d("Error connection to server");
                                onTourFailure(callBack, R.string.network_error);
                            }
                        });

                    }

                    @Override
                    public void onFailure(String error) {
                        Timber.d(error);
                        onTourFailure(callBack, error);
                    }

                    @Override
                    public void onProgressChanged(String progress) {

                    }
                });
            } else {
                Timber.i("There isn't any customer data for sending.");
                onTourSuccess(callBack);
            }
        } catch (Throwable ex) {
            Timber.e(ex);
            onTourFailure(callBack, R.string.populating_tour_failed);
        }
    }

    /***
     * Use this for populate SyncGetTourViewModel for all customers.
     * @param callBack callback
     */
    synchronized void populatedAndSendTour(@Nullable final TourCallBack callBack) {
        try {
            if (!isTourAvailable()) {
                onTourFailure(callBack, R.string.tour_is_not_available);
                return;
            }
            dataSent = false;

            VaranegarApplication.getInstance().resetElapsedTime("Sending tour data. Populating data started.");
            TourModel tourModel = loadTour();
            saveIsSending();
            progressChanged(callBack, R.string.tour_status_changed_to_is_sending);
            List<CustomerModel> customerModels = new CustomerManager(context).getAll();
            if (checkStop(callBack))
                return;
            final SyncGetTourViewModel syncGetTourViewModel = new SyncGetTourViewModel(context, tourModel.UniqueId, tourModel.TourNo);

            /*
             * populate product request for hot sales
             */
            if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
                Date date = new UpdateManager(context).getLog(UpdateKey.ProductRequest);
                boolean isEnabled = date.equals(UpdateManager.MIN_DATE);
                if (isEnabled) {
                    RequestLineViewManager requestLineManager = new RequestLineViewManager(context);
                    List<SyncGetRequestLineModel> requestLineModel = requestLineManager.getSyncRequestLine();
                    if (requestLineModel != null)
                        syncGetTourViewModel.RequestItemLines = requestLineModel;
                }
            }
            if (checkStop(callBack))
                return;

            progressChanged(callBack, R.string.operation_data_populating);
            for (CustomerModel customerModel :
                    customerModels) {
                if (checkStop(callBack))
                    return;
                CustomerCallManager callManager = new CustomerCallManager(context);
                calls = callManager.loadCalls(customerModel.UniqueId);
                customerCalls = new CustomerCalls(calls);

                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    if (!callManager.isLackOfVisit(calls) &&
                            !callManager.isCompleteLackOfDelivery(calls) &&
                            !callManager.isCompleteReturnDelivery(calls) &&
                            !callManager.hasConfirmedAllInvoices(calls, customerModel.UniqueId)) {
                        saveConfirm();
                        onTourFailure(callBack, context.getString(R.string.customer) + " " + customerModel.CustomerName + " " + context.getString(R.string.is_not_confirmed));
                        return;
                    }
                }

                if (((callManager.isConfirmed(calls) ||
                        VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) &&
                        !callManager.isDataSent(calls, null))) {
                    if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                        populateCustomerCallDataForSend(syncGetTourViewModel, customerModel);
                    } else if (callManager.hasOrderOrReturnCall(calls) ||
                            callManager.isLackOfVisit(calls) ||
                            callManager.isLackOfOrder(calls)) {
                        if (callManager.isConfirmed(calls) ||
                                VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
                            populateCustomerCallDataForSend(syncGetTourViewModel, customerModel);
                        else {
                            saveConfirm();
                            onTourFailure(callBack, context.getString(R.string.customer) + " " + context.getString(R.string.has_un_confiremd_operation));
                            return;
                        }
                    }
                } /*else if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    saveConfirm();
                    onTourFailure(callBack, context.getString(R.string.customer) + " " +
                            customerModel.CustomerName + " " +
                            context.getString(R.string.is_not_confirmed));
                    return;
                }*/
                else if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    populateCustomerCallDataForSend(syncGetTourViewModel, customerModel);
                }

                populateCanceledInvoices(syncGetTourViewModel, customerModel);
            }
            progressChanged(callBack, R.string.operation_data_populated);
            if (checkStop(callBack))
                return;

            VaranegarApplication.getInstance().printElapsedTime("Sending tour data. Populating data finished.");
            Gson gson = VaranegarGsonBuilder.build().create();
            final String tourString = gson.toJson(syncGetTourViewModel);
            Timber.i("tour for all customers =\n " + tourString);
            progressChanged(callBack, R.string.sending_tour_to_server_started);
            populateAndSendFiles(new TourCallBack() {
                @Override
                public void onSuccess() {
                    progressChanged(callBack, R.string.pinging_server);
                    PingApi pingApi = new PingApi();
                    pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                        @Override
                        public void done(String ipAddress) {
                            progressChanged(callBack, R.string.server_is_connected);
                            VaranegarApplication.getInstance().printElapsedTime("Sending tour data. Ping server was done");
                            TourApi tourApi = new TourApi(context);
                            SysConfigManager sysConfigManager = new SysConfigManager(context);
                            SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
                            sendTourDataCall = tourApi.saveTourData(syncGetTourViewModel, settingsId.Value);
                            tourApi.runWebRequest(sendTourDataCall, new WebCallBack<ResponseBody>() {
                                @Override
                                protected void onFinish() {
                                    dataSent = true;
                                }

                                @Override
                                protected void onSuccess(ResponseBody result, Request request) {
                                    progressChanged(callBack, R.string.tour_data_is_sent);
                                    Timber.i("Tour has been sent = \n " + tourString);
                                    confirmTourClose(new WebCallBack() {
                                        @Override
                                        protected void onFinish() {

                                        }

                                        @Override
                                        protected void onSuccess(Object result, Request request) {
                                            final LocationManager locationManager = new LocationManager(context);
                                            locationManager.stopWait();
                                            progressChanged(callBack, R.string.tour_data_confirmed_and_closed_on_server);
                                            Preferences preferences = new Preferences(context);
                                            preferences.clearAfterTourPreferences();
                                            VaranegarApplication.getInstance().printElapsedTime("Sending tour data. Tour has been sent");
                                            Timber.i("Tour successfully closed and confirmed\n ");
                                            Timber.i("Backing up database before deleting temp tour data");
                                            progressChanged(callBack, R.string.backing_up_database);
                                            try {


                                                BackupManager.exportData(context, true,true, BackupManager.SEND_TOUR_BACKUP);
                                                progressChanged(callBack, R.string.backup_finished_successfully);
                                               // sendBuckup();

                                                progressChanged(callBack, R.string.populating_tour_info);
                                                TourInfo tourInfo = createTourInfo();
                                                if (tourInfo != null) {
                                                    saveTourInfoToFile(tourInfo);
                                                    SendTourLocationViewModel locationViewModel = new SendTourLocationViewModel();
                                                    locationViewModel.eventData = new SendTourEventViewModel();
                                                    locationViewModel.eventData = new SendTourEventViewModel();
                                                    locationViewModel.eventData.CustomersCount = tourInfo.DayCustomersCount;
                                                    locationViewModel.eventData.VisitedCustomers = tourInfo.DayVisitedCount;
                                                    locationViewModel.eventData.OrderedCustomers = tourInfo.DayOrderedCount;
                                                    locationViewModel.eventData.SumOfOrdered = tourInfo.DayOrderSum;
                                                    locationViewModel.eventData.LackOfVisit = tourInfo.DayLackOfVisitCount;
                                                    locationViewModel.eventData.LackOfOrder = tourInfo.DayLackOfOrderCount;
                                                    locationViewModel.eventData.VisitToCustomer = new DecimalFormat("#.00").format(tourInfo.DayVisitRatio);
                                                    locationViewModel.eventData.Spd = tourInfo.Spd;
                                                    locationViewModel.eventData.TotalVisitTime = tourInfo.VisitTime;
                                                    locationViewModel.eventData.TotalTourTime = tourInfo.TourTime;
                                                    locationViewModel.eventData.TourId = tourInfo.TourId;
                                                    locationViewModel.eventData.TourNo = tourInfo.TourNo;
                                                    locationViewModel.eventData.PersonnelId = tourInfo.PersonnelId;
                                                    locationViewModel.eventData.Time = tourInfo.Time;
                                                    locationViewModel.eventData.description = context.getString(R.string.tour_sent);
                                                    TrackingLogManager.addLog(context, LogType.EVENT, LogLevel.Info, "ارسال تور");

                                                    locationManager.addTrackingPoint(locationViewModel, new OnSaveLocation() {
                                                        @Override
                                                        public void onSaved(LocationModel location) {
                                                            locationManager.tryToSendItem(location);
                                                        }

                                                        @Override
                                                        public void onFailed(String error) {
                                                            Timber.e(error);
                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                Timber.e(e, "Backing up data failed");
                                            } finally {
                                                progressChanged(callBack, R.string.clearing_temp_data);
                                                emptyTempTables();
                                                new CustomerManager(context).clearCache();
                                                progressChanged(callBack, R.string.tour_temp_data_cleared);
                                                Timber.i("temp tables wiped out");
                                                deleteTour();
                                                progressChanged(callBack, R.string.file_tour_deleted);
                                                Timber.i("Tour file deleted");
                                                VaranegarApplication.getInstance().printElapsedTime("Sending tour data. Sending tour finished.");
                                                onTourSuccess(callBack);
                                            }
                                        }

                                        @Override
                                        protected void onApiFailure(ApiError error, Request request) {
                                            Timber.e("Confirming tour failed");
                                            String err = WebApiErrorBody.log(error, context);
                                            try {
                                                saveConfirm();
                                                onTourFailure(callBack, err);
                                            } catch (IOException e) {
                                                Timber.e(e);
                                            }
                                        }

                                        @Override
                                        protected void onNetworkFailure(Throwable t, Request request) {
                                            Timber.e("confirming tour failed");
                                            Timber.d(t);
                                            try {
                                                saveConfirm();
                                                onTourFailure(callBack, R.string.network_error);
                                            } catch (IOException e) {
                                                Timber.e(e);
                                            }
                                        }


                                    });
                                }


                                @Override
                                protected void onApiFailure(ApiError error, Request request) {
                                    Timber.e("Sending tour failed");
                                    String err = WebApiErrorBody.log(error, context);
                                    try {
                                        saveConfirm();
                                        onTourFailure(callBack, err);
                                    } catch (IOException e) {
                                        Timber.e(e);
                                    }
                                }

                                @Override
                                protected void onNetworkFailure(Throwable t, Request request) {
                                    Timber.e("Sending tour failed");
                                    Timber.e(t);
                                    try {
                                        saveConfirm();
                                        onTourFailure(callBack, R.string.network_error);
                                    } catch (IOException e) {
                                        Timber.e(e);
                                    }
                                }

                                @Override
                                public void onCancel(Request request) {
                                    super.onCancel(request);
                                    try {
                                        saveConfirm();
                                        onTourFailure(callBack, R.string.sending_tour_canceled);
                                    } catch (IOException e) {
                                        Timber.e(e);
                                    }
                                }
                            });
                        }

                        @Override
                        public void failed() {
                            progressChanged(callBack, R.string.network_error);
                            Timber.d("Error connection to server");
                            try {
                                saveConfirm();
                                onTourFailure(callBack, R.string.network_error);
                            } catch (IOException e) {
                                Timber.e(e);
                            }
                        }
                    });

                }

                @Override
                public void onFailure(String error) {
                    Timber.d(error);
                    try {
                        saveConfirm();
                        onTourFailure(callBack, error);
                    } catch (IOException e) {
                        Timber.e(e);
                    }
                }

                @Override
                public void onProgressChanged(String progress) {

                }
            });
        } catch (Throwable ex) {
            Timber.e(ex);
            onTourFailure(callBack, R.string.populating_tour_failed);
        }
    }

    public void sendBuckup(){
        String date = DateHelper.toString(new Date(), DateFormat.FileName, Locale.US);
        String zipFilename;
        TourModel tourModel;
        tourModel = loadTour();
        zipFilename = date + "_f.backup";
        BackupManager.uploadBackup(context,String.valueOf(tourModel.TourNo),zipFilename, new BackupManager.IUploadCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
    public void sendTour(@Nullable final TourCallBack callBack) {
        TourModel tourModel = loadTour();
        if (tourModel == null) {
            if (callBack != null)
                callBack.onFailure(context.getString(R.string.tour_is_not_available));
            return;
        }
        if (tourModel.Status == TourStatus.IsSending) {
            if (callBack != null)
                callBack.onFailure(context.getString(R.string.tour_is_sending));
            return;
        }

        if (sendTourServiceBinder == null) {
            Intent intent = new Intent(context, SendTourService.class);
            context.startService(intent);
            context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    sendTourServiceBinder = (SendTourService.ServiceBinder) iBinder;
                    sendTourServiceBinder.getService().sendTour(callBack);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    Timber.e("Service unbounded");
                    sendTourServiceBinder = null;
                }
            }, Context.BIND_AUTO_CREATE);
        } else {
            sendTourServiceBinder.getService().sendTour(callBack);
        }
    }

    public boolean isSendServiceConnected() {
        return sendTourServiceBinder != null;
    }

    public void cancelSendingTour() {
        Timber.d("Sending tour canceled");
        TourModel tourModel = loadTour();
        if (tourModel == null) {
            return;
        }
        if (tourModel.Status != TourStatus.IsSending) {
            return;
        }

        if (sendTourServiceBinder != null)
            sendTourServiceBinder.getService().stopSendingTour();
    }

    public interface ITourStatusCallback {
        void onStatus(UUID status);

        void onFailure();
    }

    public void getTourStatus(@NonNull final ITourStatusCallback callback) {
        TourModel tourModel = loadTour();
        TourApi tourApi = new TourApi(context);
        tourApi.runWebRequest(tourApi.getTourStatus(tourModel.UniqueId.toString()), new WebCallBack<UUID>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(UUID result, Request request) {
                callback.onStatus(result);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                callback.onFailure();
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onFailure();
            }
        });
    }

    public void deleteTourData() {
        try {
            new CustomerManager(context).clearCache();
            BackupManager.exportData(context, true);
            emptyTempTables();
            Timber.i("temp tables wiped out");
            deleteTour();
            Timber.i("Tour file deleted");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected synchronized void stopSendingTour() {
        stopSendingTour = true;
        cancelSendTourDataCall();
    }

    private void cancelSendTourDataCall() {
        if (!dataSent) {
            if (sendTourDataCall != null && !sendTourDataCall.isCanceled())
                sendTourDataCall.cancel();
        }
    }

    private void progressChanged(TourCallBack callBack, @StringRes int str) {
        if (callBack != null)
            callBack.onProgressChanged(context.getString(str));
        Timber.d(context.getString(str));
    }

    private boolean checkStop(TourCallBack callBack) {
        if (stopSendingTour) {
            if (callBack != null)
                callBack.onFailure(context.getString(R.string.sending_tour_canceled));
            try {
                saveConfirm();
                return true;
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        return false;
    }

    private void populateAndSendFiles(@NonNull final TourCallBack callBack) {
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                QuestionnaireAnswerManager answerManager = new QuestionnaireAnswerManager(context);
                answerManager.uploadCustomerPictures(new UpdateCall() {
                    @Override
                    protected void onSuccess() {
                        Timber.i("Questionnaire files have been sent successfully.");
                        PictureCustomerViewManager pictureCustomerViewManager = new PictureCustomerViewManager(context);
                        pictureCustomerViewManager.uploadCustomerPictures(new UpdateCall() {
                            @Override
                            protected void onSuccess() {
                                Timber.i("customer pictures have been sent");
                                callBack.onSuccess();
                            }

                            @Override
                            protected void onFailure(String error) {
                                Timber.e("Uploading customer pictures failed");
                                Timber.e(error);
                                callBack.onFailure(error);
                            }
                        });

                    }

                    @Override
                    protected void onFailure(String error) {
                        Timber.e("Uploading questionnaire files failed.");
                        Timber.e(error);
                        callBack.onFailure(error);
                    }
                });
            }

            @Override
            public void failed() {
                Timber.e("Error connection to the server");
                callBack.onFailure(context.getString(R.string.network_error));
            }
        });


    }

    private void populateAndSendFiles(final List<UUID> customerIds, @NonNull final TourCallBack callBack) {
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {

            @Override
            public void done(String ipAddress) {
                QuestionnaireAnswerManager answerManager = new QuestionnaireAnswerManager(context);
                answerManager.uploadCustomerPictures(customerIds, new UpdateCall() {
                    @Override
                    protected void onSuccess() {
                        Timber.i("uploading questionnaire files finished successfully for customer ids = " + customerIds.toString());
                        PictureCustomerViewManager pictureCustomerViewManager = new PictureCustomerViewManager(context);
                        pictureCustomerViewManager.uploadCustomerPictures(customerIds, new UpdateCall() {
                            @Override
                            protected void onSuccess() {
                                Timber.i("customers pictures have been uploaded for customer ids = " + customerIds.toString());
                                callBack.onSuccess();
                            }

                            @Override
                            protected void onFailure(String error) {
                                Timber.e("uploading customer pictures failed for customer ids =" + customerIds.toString());
                                Timber.e(error);
                                callBack.onFailure(error);
                            }
                        });
                    }

                    @Override
                    protected void onFailure(String error) {
                        Timber.e("uploading questionnaire pictures failed for customer ids = " + customerIds.toString());
                        Timber.e(error);
                        callBack.onFailure(error);
                    }
                });
            }

            @Override
            public void failed() {
                Timber.e("Error connection to the server");
                callBack.onFailure(context.getString(R.string.network_error));
            }
        });
    }

    public void populateAndSendFiles(final UUID customerId, @NonNull final TourCallBack callBack) {
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                QuestionnaireAnswerManager answerManager = new QuestionnaireAnswerManager(context);
                answerManager.uploadCustomerPictures(customerId, new UpdateCall() {
                    @Override
                    protected void onSuccess() {
                        Timber.e("Uploading questionnaire files finished successfully.");
                        PictureCustomerViewManager pictureCustomerViewManager = new PictureCustomerViewManager(context);
                        pictureCustomerViewManager.uploadCustomerPictures(customerId, new UpdateCall() {
                            @Override
                            protected void onSuccess() {
                                Timber.i("customers pictures for customer id " + customerId.toString() + " have been uploaded.");
                                callBack.onSuccess();
                            }

                            @Override
                            protected void onFailure(String error) {
                                Timber.e("uploading customer pictures failed for customer id =" + customerId.toString());
                                Timber.e(error);
                                callBack.onFailure(error);
                            }
                        });

                    }

                    @Override
                    protected void onFailure(String error) {
                        Timber.e("uploading questionnaire pictures failed for customer id =" + customerId.toString());
                        Timber.e(error);
                        callBack.onFailure(error);
                    }
                });
            }

            @Override
            public void failed() {
                Timber.e("Error connection to the server");
                callBack.onFailure(context.getString(R.string.network_error));
            }
        });

    }

    public void populateCanceledInvoices(SyncGetTourViewModel syncGetTourViewModel, CustomerModel customerModel) {
        // Step 4 populate cancel invoice
        CancelInvoiceManager cancelInvoiceManager = new CancelInvoiceManager(context);
        List<SyncGetCancelInvoiceViewModel> cancelInvoiceViewModels = cancelInvoiceManager.getCancelInvoiceViewModel(customerModel.UniqueId);
        if (cancelInvoiceViewModels != null && cancelInvoiceViewModels.size() > 0) {
            syncGetTourViewModel.CancelInvoices.addAll(cancelInvoiceViewModels);
        }
    }

    private void populateCustomerCallDataForSend(SyncGetTourViewModel syncGetTourViewModel, CustomerModel customerModel) {

        // Step 1 populate calls
        SyncGetCustomerCallViewModel syncGetCustomerCallViewModel = populateCustomerCalls(customerModel);
        syncGetTourViewModel.CustomerCalls.add(syncGetCustomerCallViewModel);

        // Step 2 populate customer edition
        CustomerManager customerManager = new CustomerManager(context);
        SyncGetCustomerUpdateDataViewModel infoViewModel = customerManager.getCustomerUpdateDataViewModel(customerModel.UniqueId);
        if (infoViewModel != null) {
            syncGetTourViewModel.CustomerUpdates.add(infoViewModel);
        }

        // Step 3 populate customer locations
        SyncGetCustomerUpdateLocationViewModel locationViewModel = customerManager.getCustomerLocationDataViewModel(customerModel);
        if (locationViewModel != null) {
            syncGetTourViewModel.CustomerLocations.add(locationViewModel);
        }
    }

    private SyncGetCustomerCallViewModel populateCustomerCalls(CustomerModel customerModel) {
        SyncGetCustomerCallViewModel syncGetCustomerCallViewModel = new SyncGetCustomerCallViewModel();
        CustomerActionTimeManager customerActionTimeManager = new CustomerActionTimeManager(context);
        VisitTemplatePathCustomerManager visitTemplatePathCustomerManager = new VisitTemplatePathCustomerManager(context);
        List<VisitTemplatePathCustomerModel> visitTemplatePathCustomerModels = visitTemplatePathCustomerManager.getCustomerPathUniqueId(customerModel.UniqueId);
        if (visitTemplatePathCustomerModels.size() > 0)
            syncGetCustomerCallViewModel.VisitTemplatePathUniqueId = visitTemplatePathCustomerModels.get(0).VisitTemplatePathId;
        syncGetCustomerCallViewModel.CustomerUniqueId = customerModel.UniqueId;
        syncGetCustomerCallViewModel.IsNewCustomer = customerModel.IsNewCustomer;
        // Set visit status
        CustomerCallManager callManager = new CustomerCallManager(context);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (callManager.isLackOfVisit(calls)) { // we removed lack of visit from dist. But I let this check here to make sure it works (Maybe someone in the future adds NonVisitaction again!)
                CustomerCallModel lackOfVisit = customerCalls.getCall(CustomerCallType.LackOfVisit).get(0);
                syncGetCustomerCallViewModel.StartTime = lackOfVisit.CreatedTime;
                syncGetCustomerCallViewModel.EndTime = lackOfVisit.CreatedTime;
                syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.NotVisited;
                syncGetCustomerCallViewModel.NoSaleReasonUniqueId = UUID.fromString(lackOfVisit.ExtraField1);
            } else if (callManager.isCompleteLackOfDelivery(calls)) {
                CustomerCallModel lackOfOrder = customerCalls.getCall(CustomerCallType.CompleteLackOfDelivery).get(0);
                syncGetCustomerCallViewModel.StartTime = lackOfOrder.CreatedTime;
                syncGetCustomerCallViewModel.EndTime = lackOfOrder.CreatedTime;
                syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.NotDelivered;
                syncGetCustomerCallViewModel.UndeliveredReasonUniqueId = UUID.fromString(lackOfOrder.ExtraField1);
            } else if (callManager.isCompleteReturnDelivery(calls)) {
                CustomerCallModel lackOfOrder = customerCalls.getCall(CustomerCallType.CompleteReturnDelivery).get(0);
                syncGetCustomerCallViewModel.StartTime = lackOfOrder.CreatedTime;
                syncGetCustomerCallViewModel.EndTime = lackOfOrder.CreatedTime;
                syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.FullyReturned;
                syncGetCustomerCallViewModel.ReturnReasonUniqueId = UUID.fromString(lackOfOrder.ExtraField1);
            } else if (callManager.hasConfirmedAllInvoices(calls, customerModel.UniqueId)) {
                List<CustomerCallModel> orderReturns = customerCalls.getCall(CustomerCallType.OrderReturn);
                List<CustomerCallModel> orderLackOfDelivery = customerCalls.getCall(CustomerCallType.OrderLackOfDelivery);
                List<CustomerCallModel> orderDelivery = customerCalls.getCall(CustomerCallType.OrderDelivered);
                List<CustomerCallModel> orderPartialDelivery = customerCalls.getCall(CustomerCallType.OrderPartiallyDelivered);
                int all = orderReturns.size() + orderLackOfDelivery.size() + orderDelivery.size() + orderPartialDelivery.size();
                if (orderReturns.size() == all)
                    syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.FullyReturned;
                else if (orderLackOfDelivery.size() == all)
                    syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.NotDelivered;
                else if (orderDelivery.size() == all)
                    syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.FullyDelivered;
                else
                    syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.PartiallyDelivered;

                calculateStartEndTime(syncGetCustomerCallViewModel, customerModel);
            } else
                throw new RuntimeException(context.getString(R.string.please_verify_following_customer) + "\n" + customerModel.CustomerName);
        } else {
            if (callManager.hasOrderOrReturnCall(calls)) {
                syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.Certain;
                calculateStartEndTime(syncGetCustomerCallViewModel, customerModel);
            } else if (callManager.isLackOfVisit(calls)) {
                CustomerCallModel lackOfVisit = customerCalls.getCall(CustomerCallType.LackOfVisit).get(0);
                syncGetCustomerCallViewModel.StartTime = lackOfVisit.CreatedTime;
                syncGetCustomerCallViewModel.EndTime = lackOfVisit.CreatedTime;
                syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.NotVisited;
                syncGetCustomerCallViewModel.NoSaleReasonUniqueId = UUID.fromString(lackOfVisit.ExtraField1);
            } else if (callManager.isLackOfOrder(calls)) {
                CustomerCallModel lackOfOrder = customerCalls.getCall(CustomerCallType.LackOfOrder).get(0);
                calculateStartEndTime(syncGetCustomerCallViewModel, customerModel);
                syncGetCustomerCallViewModel.VisitStatusUniqueId = CustomerCallManager.CustomerVisitStatus.NotOrdered;
                syncGetCustomerCallViewModel.NoSaleReasonUniqueId = UUID.fromString(lackOfOrder.ExtraField1);
            } else
                throw new RuntimeException(context.getString(R.string.please_verify_following_customer) + "\n" + customerModel.CustomerName);
        }
        syncGetCustomerCallViewModel.Description = "";
        syncGetCustomerCallViewModel.Longitude = customerModel.Longitude;
        syncGetCustomerCallViewModel.Latitude = customerModel.Latitude;
        syncGetCustomerCallViewModel.CallDate = customerActionTimeManager.get(customerModel.UniqueId, CustomerActions.CustomerCallStart);
        syncGetCustomerCallViewModel.DeliveryDate = customerActionTimeManager.get(customerModel.UniqueId, CustomerActions.CustomerCallStart);
        syncGetCustomerCallViewModel.VisitDuration = CustomerActionTimeManager.getCustomerCallTime(context, customerModel.UniqueId) * 1000;

        boolean isLackOfVisit = callManager.isLackOfVisit(calls);
        boolean isDataSent = callManager.isDataSent(calls, null);
        boolean isLackOfOrder = false;
        boolean isLackOfOrderAndNeedImage = false;
        boolean isLackOfVisitAndNeedImage = false;

        /**
         * Need Image
         *LackOfVisit لیست عدم ویزیت
         * لیست عدم سفارش LackOfOrder
         */
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            isLackOfOrder = callManager.isLackOfOrder(calls);
            isLackOfOrderAndNeedImage = callManager.isLackOfOrderAndNeedImage(calls);
            isLackOfVisitAndNeedImage = callManager.isLackOfVisitAndNeedImage(calls);
        }
        // populate call orders
        if (!isLackOfOrder && !isLackOfVisit)
            syncGetCustomerCallViewModel.CustomerCallOrders = populateCustomerCallOrders(customerModel.UniqueId);
        // populate call returns
        if (!isLackOfVisit && !isDataSent)
            syncGetCustomerCallViewModel.CustomerCallReturns = populateCustomerCallReturns(customerModel.UniqueId);
        // populate call payments
        if (!isLackOfVisit && !isDataSent)
            syncGetCustomerCallViewModel.CustomerCallPayments = populateCustomerPayments(customerModel.UniqueId);
        // populate customer questionnaires
        if (!isLackOfVisit && !isDataSent)
            syncGetCustomerCallViewModel.CustomerCallQuestionnaires = populateCustomerQuestionnaires(customerModel.UniqueId);
        // populate customer pictures
        if ((!isLackOfVisit || isLackOfVisitAndNeedImage) && !isDataSent)
            syncGetCustomerCallViewModel.CustomerCallPictures = populateCustomerPictures(customerModel.UniqueId, isLackOfOrderAndNeedImage, isLackOfVisitAndNeedImage);
        // populate customer stock level (Customer inventory)
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.CheckCustomerStock, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, true))
            syncGetCustomerCallViewModel.CustomerCallStockLevels = populateCustomerCallStockLevels(customerModel.UniqueId);
        else
            syncGetCustomerCallViewModel.CustomerCallStockLevels = new ArrayList<>();

        // populate customer catalog view
        syncGetCustomerCallViewModel.CustomerCallCatalogs = populateCatalogViews(customerModel.UniqueId);

        return syncGetCustomerCallViewModel;

    }

    private void calculateStartEndTime(SyncGetCustomerCallViewModel syncGetCustomerCallViewModel, CustomerModel customerModel) {
        CustomerActionTimeManager customerActionTimeManager = new CustomerActionTimeManager(context);
        syncGetCustomerCallViewModel.StartTime = customerActionTimeManager.get(customerModel.UniqueId, CustomerActions.CustomerCallStart);
        if (syncGetCustomerCallViewModel.StartTime == null) {
            syncGetCustomerCallViewModel.StartTime = new Date();
            long m = syncGetCustomerCallViewModel.StartTime.getTime()  + 1000;
            syncGetCustomerCallViewModel.EndTime = new Date(m);
            return;
        }
        syncGetCustomerCallViewModel.EndTime = customerActionTimeManager.get(customerModel.UniqueId, CustomerActions.CustomerCallEnd);
        if (syncGetCustomerCallViewModel.EndTime == null || syncGetCustomerCallViewModel.EndTime.getTime() < syncGetCustomerCallViewModel.StartTime.getTime()) {
            long m = syncGetCustomerCallViewModel.StartTime.getTime() + CustomerActionTimeManager.getCustomerCallTime(context, customerModel.UniqueId);
            syncGetCustomerCallViewModel.EndTime = new Date(m);
        }
        if (syncGetCustomerCallViewModel.EndTime.getTime() < syncGetCustomerCallViewModel.StartTime.getTime()) {
            long m = syncGetCustomerCallViewModel.StartTime.getTime()  + 1000;
            syncGetCustomerCallViewModel.EndTime = new Date(m);
        }
    }

    private List<SyncGetCustomerCallCatalogViewModel> populateCatalogViews(UUID customerId) {
        CatalogueLogManager catalogueLogManager = new CatalogueLogManager(context);
        return catalogueLogManager.getLogs(customerId);
    }

    private List<SyncGetCustomerCallPictureViewModel> populateCustomerPictures(UUID customerId, boolean isLackOfOrderAndNeedImage, boolean isLackOfVisitAndNeedImage) {

        List<SyncGetCustomerCallPictureViewModel> syncGetCustomerCallPictureViewModels = new ArrayList<>();
        PictureCustomerViewManager pictureCustomerViewManager = new PictureCustomerViewManager(context);
        List<PictureCustomerViewModel> pictureCustomerViewModels = pictureCustomerViewManager.getPictures(customerId, isLackOfOrderAndNeedImage, isLackOfVisitAndNeedImage);
        for (PictureCustomerViewModel pictureCustomerViewModel :
                pictureCustomerViewModels) {
            SyncGetCustomerCallPictureViewModel syncGetCustomerCallPictureViewModel = new SyncGetCustomerCallPictureViewModel();
            syncGetCustomerCallPictureViewModel.PictureSubjectUniqueId = pictureCustomerViewModel.PictureSubjectId;
            syncGetCustomerCallPictureViewModel.UniqueId = pictureCustomerViewModel.UniqueId;
            if (pictureCustomerViewModel.FileCount > 0) {
                List<UUID> fileIds = pictureCustomerViewModel.getFileIds();
                for (UUID fileId :
                        fileIds) {
                    SyncGetCustomerCallPictureDetailViewModel syncGetCustomerCallPictureDetailViewModel = new SyncGetCustomerCallPictureDetailViewModel();
                    syncGetCustomerCallPictureDetailViewModel.PictureName = pictureCustomerViewModel.Title;
                    syncGetCustomerCallPictureDetailViewModel.PictureUniqueId = fileId;
                    syncGetCustomerCallPictureViewModel.CustomerCallPictureDetails.add(syncGetCustomerCallPictureDetailViewModel);
                }
                syncGetCustomerCallPictureViewModels.add(syncGetCustomerCallPictureViewModel);
            } else if (pictureCustomerViewModel.FileCount == 0 && pictureCustomerViewModel.NoPictureReason != null && !pictureCustomerViewModel.NoPictureReason.isEmpty()) {
                syncGetCustomerCallPictureViewModel.NoPictureReason = pictureCustomerViewModel.NoPictureReason;
                syncGetCustomerCallPictureViewModels.add(syncGetCustomerCallPictureViewModel);
            }
        }
        return syncGetCustomerCallPictureViewModels;
    }

    private List<SyncGetCustomerCallStockLevelViewModel> populateCustomerCallStockLevels(UUID customerId) {
        List<SyncGetCustomerCallStockLevelViewModel> syncGetCustomerCallStockLevelViewModels = new ArrayList<>();
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.CustomerStockCheckType, SysConfigManager.cloud);
        SyncGetCustomerCallStockLevelViewModel syncGetCustomerCallStockLevelViewModel = new SyncGetCustomerCallStockLevelViewModel();

        CustomerInventoryManager customerInventoryManager = new CustomerInventoryManager(context);
        CustomerInventoryQtyManager customerInventoryQtyManager = new CustomerInventoryQtyManager(context);
        List<CustomerInventoryModel> customerInventoryModels = customerInventoryManager.getLines(customerId);

        for (CustomerInventoryModel customerInventoryModel :
                customerInventoryModels) {
            SyncGetCustomerCallStockLevelLineViewModel syncGetCustomerCallStockLevelLineViewModel = new SyncGetCustomerCallStockLevelLineViewModel();
            syncGetCustomerCallStockLevelLineViewModel.UniqueId = UUID.randomUUID();
            syncGetCustomerCallStockLevelLineViewModel.ProductUniqueId = customerInventoryModel.ProductId;
            syncGetCustomerCallStockLevelLineViewModel.IsPurchased = customerInventoryModel.IsSold;

            if (customerInventoryModel.FactoryDate !=null&&!customerInventoryModel.FactoryDate.isEmpty()) {
                String dateString = DateHelper.toString(new Date(), DateFormat.Time, VasHelperMethods.getSysConfigLocale(context));
                syncGetCustomerCallStockLevelLineViewModel.ProductionDate = customerInventoryModel.FactoryDate + " " + dateString;
            }else{
                String dateString = DateHelper.toString(new Date(), DateFormat.Complete, VasHelperMethods.getSysConfigLocale(context));
                syncGetCustomerCallStockLevelLineViewModel.ProductionDate = dateString;
            }


            if (SysConfigManager.compare(sysConfigModel, ProductInventoryManager.CustomerStockCheckType.Count)) {
                List<CustomerInventoryQtyModel> qtys = customerInventoryQtyManager.getLines(customerInventoryModel.UniqueId);
                boolean isAvailable = false;
                for (CustomerInventoryQtyModel qty :
                        qtys) {
                    SyncGetCustomerCallStockLevelLineQtyDetailViewModel syncGetCustomerCallStockLevelLineQtyDetailViewModel = new SyncGetCustomerCallStockLevelLineQtyDetailViewModel();
                    double dqty = HelperMethods.bigDecimalToDouble(qty.Qty);
                    if (dqty > 0)
                        isAvailable = true;
                    syncGetCustomerCallStockLevelLineQtyDetailViewModel.Qty = dqty;
                    syncGetCustomerCallStockLevelLineQtyDetailViewModel.ProductUnitUniqueId = qty.ProductUnitId;
                    syncGetCustomerCallStockLevelLineViewModel.CustomerCallStockLevelLineQtyDetails.add(syncGetCustomerCallStockLevelLineQtyDetailViewModel);
                }
                syncGetCustomerCallStockLevelLineViewModel.HasLevel = isAvailable;
            } else {
                syncGetCustomerCallStockLevelLineViewModel.HasLevel = customerInventoryModel.IsAvailable;
            }
            syncGetCustomerCallStockLevelViewModel.CustomerCallStockLevelLines.add(syncGetCustomerCallStockLevelLineViewModel);
        }
        if (syncGetCustomerCallStockLevelViewModel.CustomerCallStockLevelLines.size() > 0)
            syncGetCustomerCallStockLevelViewModels.add(syncGetCustomerCallStockLevelViewModel);
        return syncGetCustomerCallStockLevelViewModels;
    }

    private List<SyncGetCustomerCallQuestionnaireViewModel> populateCustomerQuestionnaires(UUID customerId) {
        List<SyncGetCustomerCallQuestionnaireViewModel> syncGetCustomerCallQuestionnaireViewModels = new ArrayList<>();

        QuestionnaireCustomerViewManager questionnaireCustomerViewManager = new QuestionnaireCustomerViewManager(context);
        QuestionnaireAnswerManager questionnaireAnswerManager = new QuestionnaireAnswerManager(context);
        QuestionnaireLineManager questionnaireLineManager = new QuestionnaireLineManager(context);

        List<QuestionnaireCustomerViewModel> answeredQuestionnaires = questionnaireCustomerViewManager.getQuestionnaires(customerId, true);
        for (QuestionnaireCustomerViewModel questionnaireCustomerViewModel :
                answeredQuestionnaires) {
            SyncGetCustomerCallQuestionnaireViewModel syncGetCustomerCallQuestionnaireViewModel = new SyncGetCustomerCallQuestionnaireViewModel();
            syncGetCustomerCallQuestionnaireViewModel.QuestionnaireUniqueId = questionnaireCustomerViewModel.QuestionnaireId;
            List<QuestionnaireAnswerModel> questionnaireAnswerModels = questionnaireAnswerManager.getLines(customerId, questionnaireCustomerViewModel.QuestionnaireId);
            List<QuestionnaireLineModel> lines = questionnaireLineManager.getLines(questionnaireCustomerViewModel.QuestionnaireId);
            for (QuestionnaireLineModel line :
                    lines) {
                QuestionnaireAnswerModel answerModel = questionnaireAnswerManager.getLine(questionnaireAnswerModels, line.UniqueId);
                SyncGetCustomerCallQuestionnaireAnswerViewModel answer = new SyncGetCustomerCallQuestionnaireAnswerViewModel();
                answer.HasAttachments = answerModel.AttachmentId != null;
                answer.QuestionnaireLineUniqueId = line.UniqueId;
                answer.UniqueId = answerModel.AttachmentId == null ? UUID.randomUUID() : answerModel.AttachmentId;
                answer.CustomerCallQuestionnaireUniqueId = questionnaireCustomerViewModel.QuestionnaireId;
                answer.Answer = questionnaireLineManager.serialize(line, answerModel);
                answer.options = questionnaireLineManager.serializeOptions(line, answerModel);
                syncGetCustomerCallQuestionnaireViewModel.Answers.add(answer);
            }
            syncGetCustomerCallQuestionnaireViewModels.add(syncGetCustomerCallQuestionnaireViewModel);
        }
        return syncGetCustomerCallQuestionnaireViewModels;
    }

    private List<SyncGetCustomerCallPaymentViewModel> populateCustomerPayments(final UUID customerId) {
        final List<SyncGetCustomerCallPaymentViewModel> syncGetCustomerCallPaymentViewModels = new ArrayList<>();
        PaymentManager paymentManager = new PaymentManager(context);
        List<PaymentModel> paymentModels = paymentManager.getCustomerPayments(customerId);
        for (PaymentModel paymentModel :
                paymentModels) {
            SyncGetCustomerCallPaymentViewModel syncGetCustomerCallPaymentViewModel = new SyncGetCustomerCallPaymentViewModel();
            syncGetCustomerCallPaymentViewModel.UniqueId = paymentModel.UniqueId;
            syncGetCustomerCallPaymentViewModel.SettlementTypeUniqueId = paymentModel.PaymentType;
            syncGetCustomerCallPaymentViewModel.Amount = HelperMethods.currencyToDouble(paymentModel.Amount);
            syncGetCustomerCallPaymentViewModel.ChqAccountNo = paymentModel.CheckAccountNumber;
            syncGetCustomerCallPaymentViewModel.ChqNo = paymentModel.CheckNumber;
            syncGetCustomerCallPaymentViewModel.SayadNo = paymentModel.SayadNumber;
            syncGetCustomerCallPaymentViewModel.ChqDate = paymentModel.ChqDate;
            syncGetCustomerCallPaymentViewModel.Date = paymentModel.Date;
            syncGetCustomerCallPaymentViewModel.ChqBankUniqueId = paymentModel.BankId;
            syncGetCustomerCallPaymentViewModel.ChqBranchCode = paymentModel.ChqBranchCode;
            syncGetCustomerCallPaymentViewModel.ChqBranchName = paymentModel.ChqBranchName;
            syncGetCustomerCallPaymentViewModel.ChqAccountName = paymentModel.ChqAccountName;
            syncGetCustomerCallPaymentViewModel.ChqCityUniqueId = paymentModel.CityId;
            syncGetCustomerCallPaymentViewModel.FollowNo = paymentModel.Ref;
            InvoiceInfoViewManager invoiceInfoViewManager = new InvoiceInfoViewManager(context);
            List<InvoicePaymentInfoViewModel> paymentInfoViewModels = invoiceInfoViewManager.getAll(paymentModel.UniqueId);
            for (InvoicePaymentInfoViewModel infoViewModel :
                    paymentInfoViewModels) {
                SyncGetCustomerCallPaymentDetailViewModel syncGetCustomerCallPaymentDetailViewModel = new SyncGetCustomerCallPaymentDetailViewModel();
                syncGetCustomerCallPaymentDetailViewModel.UniqueId = infoViewModel.UniqueId;
                syncGetCustomerCallPaymentDetailViewModel.CustomerId = infoViewModel.CustomerId;
                if (infoViewModel.IsOldInvoice) {
                    syncGetCustomerCallPaymentDetailViewModel.InvoiceId = infoViewModel.InvoiceId;
                    syncGetCustomerCallPaymentDetailViewModel.SaleDate = infoViewModel.InvoiceDate;
                } else {
                    syncGetCustomerCallPaymentDetailViewModel.CallOrderId = infoViewModel.InvoiceId;
                    syncGetCustomerCallPaymentDetailViewModel.SaleDate = DateHelper.toString(new Date(Long.parseLong(infoViewModel.InvoiceDate)), DateFormat.Date, VasHelperMethods.getSysConfigLocale(context));
                }
                syncGetCustomerCallPaymentDetailViewModel.IsOldInvoice = infoViewModel.IsOldInvoice;
                syncGetCustomerCallPaymentDetailViewModel.PaidAmount = HelperMethods.currencyToDouble(infoViewModel.PaidAmount);
                syncGetCustomerCallPaymentDetailViewModel.PaymentId = infoViewModel.PaymentId;
                syncGetCustomerCallPaymentDetailViewModel.SaleNo = infoViewModel.InvoiceNo;
                syncGetCustomerCallPaymentDetailViewModel.SaleRef = infoViewModel.InvoiceRef;
                syncGetCustomerCallPaymentViewModel.PaymentDetails.add(syncGetCustomerCallPaymentDetailViewModel);
            }
            syncGetCustomerCallPaymentViewModels.add(syncGetCustomerCallPaymentViewModel);
        }
        return syncGetCustomerCallPaymentViewModels;
    }

    private List<SyncGetCustomerCallOrderViewModel> populateCustomerCallOrders(final UUID customerId) {
        /********
         * defined Parent for CustomerCallOrder
         */
        final List<SyncGetCustomerCallOrderViewModel> syncGetCustomerCallOrderViewModels = new ArrayList<>();

        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            CustomerCallInvoiceManager customerCallInvoiceManager = new CustomerCallInvoiceManager(context);
            List<CustomerCallInvoiceModel> customerCallInvoiceModels = customerCallInvoiceManager.getCustomerCallInvoices(customerId);

            CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(context);
            List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);

            for (final CustomerCallInvoiceModel customerCallInvoiceModel :
                    customerCallInvoiceModels) {
                CustomerCallOrderModel customerCallOrderModel = Linq.findFirst(customerCallOrderModels, new Linq.Criteria<CustomerCallOrderModel>() {
                    @Override
                    public boolean run(CustomerCallOrderModel item) {
                        return item.UniqueId.equals(customerCallInvoiceModel.UniqueId);
                    }
                });
                if (customerCallOrderModel == null)
                    customerCallOrderModel = customerCallInvoiceModel.convertInvoiceToOrderModel();

                SyncGetCustomerCallOrderViewModel syncGetCustomerCallOrderViewModel = new SyncGetCustomerCallOrderViewModel();

                syncGetCustomerCallOrderViewModel.UniqueId = customerCallOrderModel.UniqueId;
                syncGetCustomerCallOrderViewModel.PriceClassUniqueId = customerCallOrderModel.PriceClassId;
                syncGetCustomerCallOrderViewModel.SubSystemTypeUniqueId = VaranegarApplication.getInstance().getAppId();
                syncGetCustomerCallOrderViewModel.OrderTypeUniqueId = customerCallOrderModel.OrderTypeUniqueId;

                CustomerCallInvoiceManager callInvoiceManager = new CustomerCallInvoiceManager(context);
                CustomerCallInvoiceModel callInvoiceModel = callInvoiceManager.getCustomerCallInvoice(customerCallOrderModel.UniqueId);
                syncGetCustomerCallOrderViewModel.OrderPaymentTypeUniqueId = callInvoiceModel.OrderPaymentTypeUniqueId;
                syncGetCustomerCallOrderViewModel.InvoicePaymentTypeUniqueId = customerCallOrderModel.OrderPaymentTypeUniqueId;

                final CustomerCallOrderModel finalCustomerCallOrderModel = customerCallOrderModel;
                CustomerCallModel callModel = Linq.findFirst(calls, new Linq.Criteria<CustomerCallModel>() {
                    @Override
                    public boolean run(CustomerCallModel item) {
                        if (item.CallType == CustomerCallType.CompleteLackOfDelivery || item.CallType == CustomerCallType.CompleteReturnDelivery)
                            return customerId.equals(item.CustomerId);
                        else
                            return finalCustomerCallOrderModel.UniqueId.toString().equals(item.ExtraField1);
                    }
                });
                if (callModel.CallType == CustomerCallType.OrderDelivered)
                    syncGetCustomerCallOrderViewModel.DistributionDeliveryStatusUniqueId = CustomerCallManager.OrderVisitStatus.Delivered;
                else if (callModel.CallType == CustomerCallType.OrderPartiallyDelivered) {
                    syncGetCustomerCallOrderViewModel.DistributionDeliveryStatusUniqueId = CustomerCallManager.OrderVisitStatus.PartiallyDelivered;
                    syncGetCustomerCallOrderViewModel.ReturnReasonUniqueId = UUID.fromString(callModel.ExtraField2);
                } else if (callModel.CallType == CustomerCallType.OrderReturn) {
                    syncGetCustomerCallOrderViewModel.DistributionDeliveryStatusUniqueId = CustomerCallManager.OrderVisitStatus.EnRoute;
                    syncGetCustomerCallOrderViewModel.ReturnReasonUniqueId = UUID.fromString(callModel.ExtraField2);
                } else if (callModel.CallType == CustomerCallType.CompleteReturnDelivery) {
                    syncGetCustomerCallOrderViewModel.DistributionDeliveryStatusUniqueId = CustomerCallManager.OrderVisitStatus.EnRoute;
                    syncGetCustomerCallOrderViewModel.ReturnReasonUniqueId = UUID.fromString(callModel.ExtraField1);
                } else if (callModel.CallType == CustomerCallType.OrderLackOfDelivery) {
                    syncGetCustomerCallOrderViewModel.DistributionDeliveryStatusUniqueId = CustomerCallManager.OrderVisitStatus.Undelivered;
                    syncGetCustomerCallOrderViewModel.UndeliveredReasonUniqueId = UUID.fromString(callModel.ExtraField2);
                } else if (callModel.CallType == CustomerCallType.CompleteLackOfDelivery) {
                    syncGetCustomerCallOrderViewModel.DistributionDeliveryStatusUniqueId = CustomerCallManager.OrderVisitStatus.Undelivered;
                    syncGetCustomerCallOrderViewModel.UndeliveredReasonUniqueId = UUID.fromString(callModel.ExtraField1);
                }


                syncGetCustomerCallOrderViewModel.Comment = customerCallOrderModel.Comment;
                syncGetCustomerCallOrderViewModel.DeliveryDate = customerCallOrderModel.DeliveryDate;
                syncGetCustomerCallOrderViewModel.LocalPaperNo = customerCallOrderModel.LocalPaperNo;
                syncGetCustomerCallOrderViewModel.CallDate = customerCallOrderModel.StartTime;
                syncGetCustomerCallOrderViewModel.InvoiceStartTime = customerCallOrderModel.StartTime;
                syncGetCustomerCallOrderViewModel.InvoiceStartPTime = DateHelper.toString(customerCallOrderModel.StartTime, DateFormat.MicrosoftDateTime, DateHelper.fa);
                syncGetCustomerCallOrderViewModel.InvoiceEndTime = customerCallOrderModel.EndTime;
                syncGetCustomerCallOrderViewModel.SaleDate = customerCallOrderModel.SaleDate;


                syncGetCustomerCallOrderViewModel.BackOfficeOrderNo = customerCallOrderModel.BackOfficeOrderNo;
                syncGetCustomerCallOrderViewModel.BackOfficeInvoiceNo = customerCallOrderModel.BackOfficeInvoiceNo;
//                syncGetCustomerCallOrderViewModel.RoundInvoiceAmount = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceAmount);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceOtherDiscount = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceOtherDiscount);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceTax = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceTax);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceCharge = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceCharge);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceDis1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceDis1);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceDis2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceDis2);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceDis3 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceDis3);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceAdd1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceAdd1);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceAdd2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceAdd2);
                syncGetCustomerCallOrderViewModel.DisType = customerCallOrderModel.DisType;
                syncGetCustomerCallOrderViewModel.BackOfficeOrderId = customerCallOrderModel.BackOfficeOrderId;
                syncGetCustomerCallOrderViewModel.BackOfficeOrderTypeId = customerCallOrderModel.BackOfficeOrderTypeId;

                syncGetCustomerCallOrderViewModel.RoundOrderOtherDiscount = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderOtherDiscount);
                syncGetCustomerCallOrderViewModel.RoundOrderDis1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderDis1);
                syncGetCustomerCallOrderViewModel.RoundOrderDis2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderDis2);
                syncGetCustomerCallOrderViewModel.RoundOrderDis3 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderDis3);
                syncGetCustomerCallOrderViewModel.RoundOrderTax = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderTax);
                syncGetCustomerCallOrderViewModel.RoundOrderCharge = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderCharge);
                syncGetCustomerCallOrderViewModel.RoundOrderAdd1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderAdd1);
                syncGetCustomerCallOrderViewModel.RoundOrderAdd2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderAdd2);

                List<SyncGetCustomerCallOrderLineViewModel> syncGetCustomerCallOrderLineViewModels = new ArrayList<>();
                CustomerCallOrderOrderViewManager customerCallOrderLinesManager = new CustomerCallOrderOrderViewManager(context);
                List<CustomerCallOrderOrderViewModel> customerCallOrderLinesModels = customerCallOrderLinesManager.getLines(customerCallOrderModel.UniqueId, null);


                CallInvoiceLineManager callInvoiceLineManager = new CallInvoiceLineManager(context);
                List<CallInvoiceLineModel> callInvoiceLineModels = callInvoiceLineManager.getLines(customerCallOrderModel.UniqueId);
                for (CallInvoiceLineModel callInvoiceLineModel :
                        callInvoiceLineModels) {
                    SyncGetCustomerCallOrderLineViewModel syncGetCustomerCallOrderLineViewModel = new SyncGetCustomerCallOrderLineViewModel();
                    syncGetCustomerCallOrderLineViewModel.UniqueId = callInvoiceLineModel.UniqueId;
                    syncGetCustomerCallOrderLineViewModel.CustomerCallOrderUniqueId = callInvoiceLineModel.OrderUniqueId;
                    syncGetCustomerCallOrderLineViewModel.ProductUniqueId = callInvoiceLineModel.ProductUniqueId;
                    syncGetCustomerCallOrderLineViewModel.FreeReasonUniqueId = callInvoiceLineModel.FreeReasonId;
                    syncGetCustomerCallOrderLineViewModel.PriceUniqueId = callInvoiceLineModel.PriceUniqueId;
                    syncGetCustomerCallOrderLineViewModel.SortId = callInvoiceLineModel.SortId;
                    syncGetCustomerCallOrderLineViewModel.IsRequestFreeItem = callInvoiceLineModel.IsRequestFreeItem;
                    syncGetCustomerCallOrderLineViewModel.IsRequestPrizeItem = callInvoiceLineModel.IsPromoLine;
                    syncGetCustomerCallOrderLineViewModel.Description = callInvoiceLineModel.Description;
                    if (callInvoiceLineModel.FreeReasonId == null || (callInvoiceLineModel.FreeReasonId != null && callInvoiceLineModel.IsPromoLine)) {
                        if (!callInvoiceLineModel.IsPromoLine) {
                            syncGetCustomerCallOrderLineViewModel.UnitPrice = HelperMethods.currencyToDouble(callInvoiceLineModel.UnitPrice);
                        }
                        syncGetCustomerCallOrderLineViewModel.RequestAmount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestAmount);
                    }
                    syncGetCustomerCallOrderLineViewModel.RequestAdd1Amount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestAdd1Amount);
                    syncGetCustomerCallOrderLineViewModel.RequestAdd2Amount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestAdd2Amount);
                    syncGetCustomerCallOrderLineViewModel.RequestOtherAddAmount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestOtherAddAmount);
                    syncGetCustomerCallOrderLineViewModel.RequestTaxAmount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestTaxAmount);
                    syncGetCustomerCallOrderLineViewModel.RequestChargeAmount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestChargeAmount);
                    syncGetCustomerCallOrderLineViewModel.RequestDis1Amount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestDis1Amount);
                    syncGetCustomerCallOrderLineViewModel.RequestDis2Amount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestDis2Amount);
                    syncGetCustomerCallOrderLineViewModel.RequestDis3Amount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestDis3Amount);
                    syncGetCustomerCallOrderLineViewModel.RequestOtherDiscountAmount = HelperMethods.currencyToDouble(callInvoiceLineModel.RequestOtherDiscountAmount);
                    List<SyncGetCustomerQtyDetailViewModel> invoiceQtyDetails = new ArrayList<>();
                    if (callInvoiceLineModel.RequestBulkQtyUnitUniqueId != null) {
                        SyncGetCustomerQtyDetailViewModel qtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                        qtyDetailViewModel.UniqueId = UUID.randomUUID();
                        qtyDetailViewModel.CustomerCallOrderLineUniqueId = callInvoiceLineModel.UniqueId;
                        qtyDetailViewModel.ProductUnitUniqueId = callInvoiceLineModel.RequestBulkQtyUnitUniqueId;
                        qtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(callInvoiceLineModel.RequestBulkQty);
                        invoiceQtyDetails.add(qtyDetailViewModel);
                    } else {
                        InvoiceLineQtyManager invoiceLineQtyManager = new InvoiceLineQtyManager(context);
                        List<InvoiceLineQtyModel> invoiceLineQtyModels = invoiceLineQtyManager.getQtyLines(callInvoiceLineModel.UniqueId);
                        for (InvoiceLineQtyModel invoiceLineQtyModel :
                                invoiceLineQtyModels) {
                            if (invoiceLineQtyModel.Qty != null && invoiceLineQtyModel.Qty.compareTo(BigDecimal.ZERO) == 1) {
                                SyncGetCustomerQtyDetailViewModel syncGetCustomerQtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                                syncGetCustomerQtyDetailViewModel.UniqueId = invoiceLineQtyModel.UniqueId;
                                syncGetCustomerQtyDetailViewModel.CustomerCallOrderLineUniqueId = invoiceLineQtyModel.OrderLineUniqueId;
                                syncGetCustomerQtyDetailViewModel.ProductUnitUniqueId = invoiceLineQtyModel.ProductUnitId;
                                syncGetCustomerQtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(invoiceLineQtyModel.Qty);
                                invoiceQtyDetails.add(syncGetCustomerQtyDetailViewModel);
                            }
                        }
                    }
                    syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLineOrderQtyDetails = invoiceQtyDetails;

                    HashMap<UUID, CustomerCallOrderOrderViewModel> orderLineHashMap = Linq.toHashMap(customerCallOrderLinesModels, new Linq.Map<CustomerCallOrderOrderViewModel, UUID>() {
                        @Override
                        public UUID run(CustomerCallOrderOrderViewModel item) {
                            return item.UniqueId;
                        }
                    });
                    CustomerCallOrderOrderViewModel orderOrderViewModel = orderLineHashMap.get(callInvoiceLineModel.UniqueId);
                    if (orderOrderViewModel != null) {
                        syncGetCustomerCallOrderLineViewModel.InvoiceAdd1Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAdd1Amount);
                        syncGetCustomerCallOrderLineViewModel.InvoiceAdd2Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAdd2Amount);
                        syncGetCustomerCallOrderLineViewModel.InvoiceOtherAddAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAddOtherAmount);
                        syncGetCustomerCallOrderLineViewModel.InvoiceTaxAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestTaxAmount);
                        syncGetCustomerCallOrderLineViewModel.InvoiceChargeAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestChargeAmount);
                        syncGetCustomerCallOrderLineViewModel.InvoiceOtherDiscountAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestOtherDiscountAmount);
                        syncGetCustomerCallOrderLineViewModel.InvoiceDis1Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis1Amount);
                        syncGetCustomerCallOrderLineViewModel.InvoiceDis2Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis2Amount);
                        syncGetCustomerCallOrderLineViewModel.RuleNo = orderOrderViewModel.RuleNo;
                        syncGetCustomerCallOrderLineViewModel.PayDuration = orderOrderViewModel.PayDuration;
                        syncGetCustomerCallOrderLineViewModel.InvoiceDis3Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis3Amount);
                        if (orderOrderViewModel.FreeReasonId == null || (callInvoiceLineModel.FreeReasonId != null && callInvoiceLineModel.IsPromoLine)) {
                            if (orderOrderViewModel.IsPromoLine) {
                                syncGetCustomerCallOrderLineViewModel.InvoiceAmount = HelperMethods.currencyToDouble(orderOrderViewModel.PromotionPrice);
                            } else {
                                syncGetCustomerCallOrderLineViewModel.UnitPrice = HelperMethods.currencyToDouble(orderOrderViewModel.UnitPrice);
                                syncGetCustomerCallOrderLineViewModel.InvoiceAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAmount);
                            }
                        }

                        List<SyncGetCustomerQtyDetailViewModel> orderQtyDetails = new ArrayList<>();
                        if (orderOrderViewModel.RequestBulkQtyUnitUniqueId != null) {
                            SyncGetCustomerQtyDetailViewModel qtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                            qtyDetailViewModel.UniqueId = UUID.randomUUID();
                            qtyDetailViewModel.CustomerCallOrderLineUniqueId = orderOrderViewModel.UniqueId;
                            qtyDetailViewModel.ProductUnitUniqueId = orderOrderViewModel.RequestBulkQtyUnitUniqueId;
                            qtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(orderOrderViewModel.RequestBulkQty);
                            orderQtyDetails.add(qtyDetailViewModel);
                        } else {
                            OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(context);
                            List<OrderLineQtyModel> orderLineQtyModels = orderLineQtyManager.getQtyLines(orderOrderViewModel.UniqueId);
                            for (OrderLineQtyModel orderLineQtyModel :
                                    orderLineQtyModels) {
                                if (orderLineQtyModel.Qty != null && orderLineQtyModel.Qty.compareTo(BigDecimal.ZERO) > 0) {
                                    SyncGetCustomerQtyDetailViewModel qtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                                    qtyDetailViewModel.UniqueId = orderLineQtyModel.UniqueId;
                                    qtyDetailViewModel.CustomerCallOrderLineUniqueId = orderLineQtyModel.OrderLineUniqueId;
                                    qtyDetailViewModel.ProductUnitUniqueId = orderLineQtyModel.ProductUnitId;
                                    qtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(orderLineQtyModel.Qty);
                                    orderQtyDetails.add(qtyDetailViewModel);
                                }
                            }
                        }
                        syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLineInvoiceQtyDetails = orderQtyDetails;


                        List<SyncGetCustomerCallOrderLinePromotionViewModel> promotionViewModels = new ArrayList<>();
                        EVCItemStatuesCustomersManager evcItemStatuesCustomersManager = new EVCItemStatuesCustomersManager(context);
                        List<EVCItemStatuesCustomersModel> evcItemStatuesCustomersModels = evcItemStatuesCustomersManager.getEVCItemStatus(orderOrderViewModel.UniqueId);
                        for (EVCItemStatuesCustomersModel evcItemStatuesCustomersModel : evcItemStatuesCustomersModels) {
                            SyncGetCustomerCallOrderLinePromotionViewModel syncGetCustomerCallOrderLinePromotionViewModel = new SyncGetCustomerCallOrderLinePromotionViewModel();
                            syncGetCustomerCallOrderLinePromotionViewModel.UniqueId = evcItemStatuesCustomersModel.UniqueId;
                            syncGetCustomerCallOrderLinePromotionViewModel.AddAmount = evcItemStatuesCustomersModel.AddAmount;
                            syncGetCustomerCallOrderLinePromotionViewModel.CustomerCallOrderLineUniqueId = orderOrderViewModel.UniqueId;
                            syncGetCustomerCallOrderLinePromotionViewModel.DiscountAmount = evcItemStatuesCustomersModel.Discount;
                            syncGetCustomerCallOrderLinePromotionViewModel.DiscountRef = evcItemStatuesCustomersModel.DisRef;
                            syncGetCustomerCallOrderLinePromotionViewModel.SupAmount = evcItemStatuesCustomersModel.SupAmount;
                            promotionViewModels.add(syncGetCustomerCallOrderLinePromotionViewModel);
                        }
                        syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLinePromotions = promotionViewModels;
                        syncGetCustomerCallOrderLineViewModel.EditReasonUniqueId = orderOrderViewModel.EditReasonId;
                    }

                    ProductManager productManager = new ProductManager(context);
                    ProductModel productModel = productManager.getItem(ProductManager.getProduct(callInvoiceLineModel.ProductUniqueId));
                    syncGetCustomerCallOrderLineViewModel.StockUniqueId = productModel.StockUniqueId;

                    List<SyncGetCustomerCallOrderLineBatchQtyDetailViewModel> syncGetCustomerCallOrderLineBatchQtyDetailViewModels = new ArrayList<>();
                    CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(context);
                    List<CallOrderLineBatchQtyDetailModel> callOrderLineBatchQtyDetailModels = callOrderLineBatchQtyDetailManager.getLineBatchQtyDetails(callInvoiceLineModel.UniqueId);
                    for (CallOrderLineBatchQtyDetailModel callOrderLineBatchQtyDetailModel : callOrderLineBatchQtyDetailModels) {
                        SyncGetCustomerCallOrderLineBatchQtyDetailViewModel syncGetCustomerCallOrderLineBatchQtyDetailViewModel = new SyncGetCustomerCallOrderLineBatchQtyDetailViewModel();
                        syncGetCustomerCallOrderLineBatchQtyDetailViewModel.UniqueId = callOrderLineBatchQtyDetailModel.UniqueId;
                        syncGetCustomerCallOrderLineBatchQtyDetailViewModel.BatchNo = callOrderLineBatchQtyDetailModel.BatchNo;
                        syncGetCustomerCallOrderLineBatchQtyDetailViewModel.BatchRef = callOrderLineBatchQtyDetailModel.BatchRef;
                        syncGetCustomerCallOrderLineBatchQtyDetailViewModel.CustomerCallOrderLineUniqueId = callOrderLineBatchQtyDetailModel.CustomerCallOrderLineUniqueId;
                        syncGetCustomerCallOrderLineBatchQtyDetailViewModel.ItemRef = callOrderLineBatchQtyDetailModel.ItemRef;
                        syncGetCustomerCallOrderLineBatchQtyDetailViewModel.Qty = callOrderLineBatchQtyDetailModel.Qty == null ? 0 : callOrderLineBatchQtyDetailModel.Qty.doubleValue();
                        syncGetCustomerCallOrderLineBatchQtyDetailViewModels.add(syncGetCustomerCallOrderLineBatchQtyDetailViewModel);
                    }
                    syncGetCustomerCallOrderLineViewModel.CustomerCallInvoiceLineBatchQtyDetails = syncGetCustomerCallOrderLineBatchQtyDetailViewModels;

                    List<SyncGetCustomerCallOrderLineBatchQtyDetailViewModel> syncGetCustomerCallInvoiceLineBatchQtyDetailViewModels = new ArrayList<>();
                    CallInvoiceLineBatchQtyDetailManager callInvoiceLineBatchQtyDetailManager = new CallInvoiceLineBatchQtyDetailManager(context);
                    List<CallInvoiceLineBatchQtyDetailModel> callInvoiceLineBatchQtyDetailModels = callInvoiceLineBatchQtyDetailManager.getLineBatchQtyDetails(callInvoiceLineModel.UniqueId);
                    for (CallInvoiceLineBatchQtyDetailModel callInvoiceLineBatchQtyDetailModel : callInvoiceLineBatchQtyDetailModels) {
                        SyncGetCustomerCallOrderLineBatchQtyDetailViewModel syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel = new SyncGetCustomerCallOrderLineBatchQtyDetailViewModel();
                        syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel.UniqueId = callInvoiceLineBatchQtyDetailModel.UniqueId;
                        syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel.BatchNo = callInvoiceLineBatchQtyDetailModel.BatchNo;
                        syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel.BatchRef = callInvoiceLineBatchQtyDetailModel.BatchRef;
                        syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel.CustomerCallOrderLineUniqueId = callInvoiceLineBatchQtyDetailModel.CustomerCallOrderLineUniqueId;
                        syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel.ItemRef = callInvoiceLineBatchQtyDetailModel.ItemRef;
                        syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel.Qty = callInvoiceLineBatchQtyDetailModel.Qty == null ? 0 : callInvoiceLineBatchQtyDetailModel.Qty.doubleValue();
                        syncGetCustomerCallInvoiceLineBatchQtyDetailViewModels.add(syncGetCustomerCallInvoiceLineBatchQtyDetailViewModel);
                    }
                    syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLineBatchQtyDetails = syncGetCustomerCallInvoiceLineBatchQtyDetailViewModels;

                    syncGetCustomerCallOrderLineViewModels.add(syncGetCustomerCallOrderLineViewModel);
                }


                final List<SyncGetCustomerCallOrderPrizeViewModel> syncGetCustomerCallOrderPrizeViewModels = new ArrayList<>();
                List<OrderPrizeModel> orderPrizeModels;
                final OrderPrizeManager orderPrizeManager = new OrderPrizeManager(context);
                orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, customerCallOrderModel.UniqueId));
                Linq.forEach(orderPrizeModels, new Linq.Consumer<OrderPrizeModel>() {
                    @Override
                    public void run(OrderPrizeModel item) {
                        SyncGetCustomerCallOrderPrizeViewModel syncGetCustomerCallOrderPrizeViewModel = new SyncGetCustomerCallOrderPrizeViewModel();
                        syncGetCustomerCallOrderPrizeViewModel.UniqueId = item.UniqueId;
                        syncGetCustomerCallOrderPrizeViewModel.ProductId = item.ProductId;
                        syncGetCustomerCallOrderPrizeViewModel.DiscountId = item.DiscountId;
                        syncGetCustomerCallOrderPrizeViewModel.DisRef = item.DisRef;
                        syncGetCustomerCallOrderPrizeViewModel.TotalQty = item.TotalQty == null ? 0 : Double.valueOf(String.valueOf(item.TotalQty));
                        syncGetCustomerCallOrderPrizeViewModels.add(syncGetCustomerCallOrderPrizeViewModel);
                    }
                });
                syncGetCustomerCallOrderViewModel.OrderPrizes = syncGetCustomerCallOrderPrizeViewModels;
                syncGetCustomerCallOrderViewModel.OrderLines = syncGetCustomerCallOrderLineViewModels;
                if (syncGetCustomerCallOrderViewModel.OrderLines.size() > 0)
                    syncGetCustomerCallOrderViewModels.add(syncGetCustomerCallOrderViewModel);
            }

        } else {
            /*******
             * Get all CustomerCallOrders
             */
            CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(context);
            List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);

            /*******
             * A loop for populate all CustomerCallOrders.
             */

            for (final CustomerCallOrderModel customerCallOrderModel :
                    customerCallOrderModels) {
                if (!customerCallOrderModel.IsSent) {
                    SyncGetCustomerCallOrderViewModel syncGetCustomerCallOrderViewModel = new SyncGetCustomerCallOrderViewModel();
                    syncGetCustomerCallOrderViewModel.UniqueId = customerCallOrderModel.UniqueId;
                    syncGetCustomerCallOrderViewModel.PriceClassUniqueId = customerCallOrderModel.PriceClassId;
                    syncGetCustomerCallOrderViewModel.SubSystemTypeUniqueId = VaranegarApplication.getInstance().getAppId();
                    syncGetCustomerCallOrderViewModel.OrderTypeUniqueId = customerCallOrderModel.OrderTypeUniqueId;
                    if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                        syncGetCustomerCallOrderViewModel.OrderPaymentTypeUniqueId = customerCallOrderModel.OrderPaymentTypeUniqueId;

                        syncGetCustomerCallOrderViewModel.DocPDate = String.valueOf(customerCallOrderModel.SaleDate);
                            syncGetCustomerCallOrderViewModel.SalePDate = String.valueOf(customerCallOrderModel.SaleDate);
                            syncGetCustomerCallOrderViewModel.DocDate = customerCallOrderModel.SaleDate;
                        syncGetCustomerCallOrderViewModel.ShipToPartyUniqueId=customerCallOrderModel.ShipToPartyUniqueId;
                        syncGetCustomerCallOrderViewModel.ShipToPartyCode=customerCallOrderModel.ShipToPartyCode;
                    } else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
                        syncGetCustomerCallOrderViewModel.InvoicePaymentTypeUniqueId = customerCallOrderModel.OrderPaymentTypeUniqueId;

                    syncGetCustomerCallOrderViewModel.Comment = customerCallOrderModel.Comment;
                    syncGetCustomerCallOrderViewModel.DeliveryDate = customerCallOrderModel.DeliveryDate;
                    syncGetCustomerCallOrderViewModel.LocalPaperNo = customerCallOrderModel.LocalPaperNo;
                    syncGetCustomerCallOrderViewModel.CallDate = customerCallOrderModel.StartTime;
                    syncGetCustomerCallOrderViewModel.InvoiceStartTime = customerCallOrderModel.StartTime;
                    syncGetCustomerCallOrderViewModel.InvoiceStartPTime = DateHelper.toString(customerCallOrderModel.StartTime, DateFormat.MicrosoftDateTime, DateHelper.fa);
                    syncGetCustomerCallOrderViewModel.InvoiceEndTime = customerCallOrderModel.EndTime;
                    syncGetCustomerCallOrderViewModel.SaleDate = customerCallOrderModel.SaleDate;
                    syncGetCustomerCallOrderViewModel.BackOfficeOrderNo = customerCallOrderModel.BackOfficeOrderNo;
                    syncGetCustomerCallOrderViewModel.BackOfficeInvoiceNo = customerCallOrderModel.BackOfficeInvoiceNo;
//                syncGetCustomerCallOrderViewModel.RoundInvoiceAmount = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceAmount);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceOtherDiscount = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceOtherDiscount);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceTax = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceTax);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceCharge = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceCharge);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceDis1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceDis1);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceDis2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceDis2);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceDis3 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceDis3);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceAdd1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceAdd1);
//                syncGetCustomerCallOrderViewModel.RoundInvoiceAdd2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundInvoiceAdd2);
                    syncGetCustomerCallOrderViewModel.DisType = customerCallOrderModel.DisType;
                    syncGetCustomerCallOrderViewModel.BackOfficeOrderId = customerCallOrderModel.BackOfficeOrderId;
                    syncGetCustomerCallOrderViewModel.BackOfficeOrderTypeId = customerCallOrderModel.BackOfficeOrderTypeId;
                    if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
                        syncGetCustomerCallOrderViewModel.OrderOtherRoundDiscount = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderOtherDiscount);
                    else
                        syncGetCustomerCallOrderViewModel.RoundOrderOtherDiscount = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderOtherDiscount);
                    syncGetCustomerCallOrderViewModel.RoundOrderDis1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderDis1);
                    syncGetCustomerCallOrderViewModel.RoundOrderDis2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderDis2);
                    syncGetCustomerCallOrderViewModel.RoundOrderDis3 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderDis3);
                    syncGetCustomerCallOrderViewModel.RoundOrderTax = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderTax);
                    syncGetCustomerCallOrderViewModel.RoundOrderCharge = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderCharge);
                    syncGetCustomerCallOrderViewModel.RoundOrderAdd1 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderAdd1);
                    syncGetCustomerCallOrderViewModel.RoundOrderAdd2 = HelperMethods.currencyToDouble(customerCallOrderModel.RoundOrderAdd2);

                    List<SyncGetCustomerCallOrderLineViewModel> syncGetCustomerCallOrderLineViewModels = new ArrayList<>();
                    CustomerCallOrderOrderViewManager customerCallOrderLinesManager = new CustomerCallOrderOrderViewManager(context);
                    List<CustomerCallOrderOrderViewModel> customerCallOrderLinesModels = customerCallOrderLinesManager.getLines(customerCallOrderModel.UniqueId, null);

                    for (CustomerCallOrderOrderViewModel orderOrderViewModel :
                            customerCallOrderLinesModels) {
                        SyncGetCustomerCallOrderLineViewModel syncGetCustomerCallOrderLineViewModel = new SyncGetCustomerCallOrderLineViewModel();
                        syncGetCustomerCallOrderLineViewModel.UniqueId = orderOrderViewModel.UniqueId;
                        syncGetCustomerCallOrderLineViewModel.CustomerCallOrderUniqueId = orderOrderViewModel.OrderUniqueId;
                        syncGetCustomerCallOrderLineViewModel.ProductUniqueId = orderOrderViewModel.ProductId;
                        syncGetCustomerCallOrderLineViewModel.RuleNo = orderOrderViewModel.RuleNo;
                        syncGetCustomerCallOrderLineViewModel.PayDuration = orderOrderViewModel.PayDuration;
                        syncGetCustomerCallOrderLineViewModel.FreeReasonUniqueId = orderOrderViewModel.FreeReasonId;
                        syncGetCustomerCallOrderLineViewModel.PriceUniqueId = orderOrderViewModel.PriceId;
                        syncGetCustomerCallOrderLineViewModel.SortId = orderOrderViewModel.SortId;
                        syncGetCustomerCallOrderLineViewModel.IsRequestFreeItem = orderOrderViewModel.IsRequestFreeItem;
                        syncGetCustomerCallOrderLineViewModel.IsRequestPrizeItem = orderOrderViewModel.IsPromoLine;
                        syncGetCustomerCallOrderLineViewModel.Description = orderOrderViewModel.Description;
                        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                            if (orderOrderViewModel.FreeReasonId == null) {
                                syncGetCustomerCallOrderLineViewModel.UnitPrice = HelperMethods.currencyToDouble(orderOrderViewModel.UnitPrice);
                                syncGetCustomerCallOrderLineViewModel.RequestAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAmount);
                            }


                            syncGetCustomerCallOrderLineViewModel.RequestAdd1Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAdd1Amount);
                            syncGetCustomerCallOrderLineViewModel.RequestAdd2Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAdd2Amount);
                            syncGetCustomerCallOrderLineViewModel.RequestTaxAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestTaxAmount);
                            syncGetCustomerCallOrderLineViewModel.RequestChargeAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestChargeAmount);
                            syncGetCustomerCallOrderLineViewModel.RequestDis1Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis1Amount);
                            syncGetCustomerCallOrderLineViewModel.RequestDis2Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis2Amount);
                            syncGetCustomerCallOrderLineViewModel.RequestDis3Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis3Amount);
                            syncGetCustomerCallOrderLineViewModel.RequestOtherDiscountAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestOtherDiscountAmount);
                        } else {
                            if (orderOrderViewModel.FreeReasonId == null || (orderOrderViewModel.FreeReasonId != null && orderOrderViewModel.IsPromoLine)) {
                                if (orderOrderViewModel.IsPromoLine) {
                                    syncGetCustomerCallOrderLineViewModel.InvoiceAmount = HelperMethods.currencyToDouble(orderOrderViewModel.PromotionPrice);
                                    if (orderOrderViewModel.PromotionPrice.compareTo(Currency.ZERO) > 0)
                                        syncGetCustomerCallOrderLineViewModel.UnitPrice = HelperMethods.currencyToDouble(orderOrderViewModel.UnitPrice);
                                } else {
                                    syncGetCustomerCallOrderLineViewModel.UnitPrice = HelperMethods.currencyToDouble(orderOrderViewModel.UnitPrice);
                                    syncGetCustomerCallOrderLineViewModel.InvoiceAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAmount);
                                }
                            }
                            syncGetCustomerCallOrderLineViewModel.InvoiceAdd1Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAdd1Amount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceAdd2Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAdd2Amount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceOtherAddAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestAddOtherAmount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceTaxAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestTaxAmount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceChargeAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestChargeAmount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceOtherDiscountAmount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestOtherDiscountAmount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceDis1Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis1Amount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceDis2Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis2Amount);
                            syncGetCustomerCallOrderLineViewModel.InvoiceDis3Amount = HelperMethods.currencyToDouble(orderOrderViewModel.RequestDis3Amount);
                        }

                        ProductManager productManager = new ProductManager(context);
                        ProductModel productModel = productManager.getItem(ProductManager.getProduct(orderOrderViewModel.ProductId));

                        List<SyncGetCustomerCallOrderLineBatchQtyDetailViewModel> syncGetCustomerCallOrderLineBatchQtyDetailViewModels = new ArrayList<>();
                        CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(context);
                        List<CallOrderLineBatchQtyDetailModel> callOrderLineBatchQtyDetailModels = callOrderLineBatchQtyDetailManager.getLineBatchQtyDetails(orderOrderViewModel.UniqueId);
                        // TODO: 7/21/2018  check that this is only for presale or not
                        for (CallOrderLineBatchQtyDetailModel callOrderLineBatchQtyDetailModel : callOrderLineBatchQtyDetailModels) {
                            SyncGetCustomerCallOrderLineBatchQtyDetailViewModel syncGetCustomerCallOrderLineBatchQtyDetailViewModel = new SyncGetCustomerCallOrderLineBatchQtyDetailViewModel();
                            syncGetCustomerCallOrderLineBatchQtyDetailViewModel.UniqueId = callOrderLineBatchQtyDetailModel.UniqueId;
                            syncGetCustomerCallOrderLineBatchQtyDetailViewModel.BatchNo = callOrderLineBatchQtyDetailModel.BatchNo;
                            syncGetCustomerCallOrderLineBatchQtyDetailViewModel.BatchRef = callOrderLineBatchQtyDetailModel.BatchRef;
                            syncGetCustomerCallOrderLineBatchQtyDetailViewModel.CustomerCallOrderLineUniqueId = callOrderLineBatchQtyDetailModel.CustomerCallOrderLineUniqueId;
                            syncGetCustomerCallOrderLineBatchQtyDetailViewModel.ItemRef = callOrderLineBatchQtyDetailModel.ItemRef;
                            syncGetCustomerCallOrderLineBatchQtyDetailViewModel.Qty = callOrderLineBatchQtyDetailModel.Qty == null ? 0 : callOrderLineBatchQtyDetailModel.Qty.doubleValue();
                            syncGetCustomerCallOrderLineBatchQtyDetailViewModels.add(syncGetCustomerCallOrderLineBatchQtyDetailViewModel);
                        }

                        List<SyncGetCustomerCallOrderLinePromotionViewModel> syncGetCustomerCallOrderLinePromotionViewModels = new ArrayList<>();
                        EVCItemStatuesCustomersManager evcItemStatuesCustomersManager = new EVCItemStatuesCustomersManager(context);
                        if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                            List<EVCItemStatuesCustomersModel> evcItemStatuesCustomersModels = evcItemStatuesCustomersManager.getEVCItemStatus(orderOrderViewModel.UniqueId);
                            for (EVCItemStatuesCustomersModel evcItemStatuesCustomersModel : evcItemStatuesCustomersModels) {
                                SyncGetCustomerCallOrderLinePromotionViewModel syncGetCustomerCallOrderLinePromotionViewModel = new SyncGetCustomerCallOrderLinePromotionViewModel();
                                syncGetCustomerCallOrderLinePromotionViewModel.UniqueId = evcItemStatuesCustomersModel.UniqueId;
                                syncGetCustomerCallOrderLinePromotionViewModel.AddAmount = evcItemStatuesCustomersModel.AddAmount;
                                syncGetCustomerCallOrderLinePromotionViewModel.CustomerCallOrderLineUniqueId = orderOrderViewModel.UniqueId;
                                syncGetCustomerCallOrderLinePromotionViewModel.DiscountAmount = evcItemStatuesCustomersModel.Discount;
                                syncGetCustomerCallOrderLinePromotionViewModel.DiscountRef = evcItemStatuesCustomersModel.DisRef;
                                syncGetCustomerCallOrderLinePromotionViewModel.SupAmount = evcItemStatuesCustomersModel.SupAmount;
                                syncGetCustomerCallOrderLinePromotionViewModels.add(syncGetCustomerCallOrderLinePromotionViewModel);

                            }
                        }

                        syncGetCustomerCallOrderLineViewModel.StockUniqueId = productModel.StockUniqueId;
                        List<SyncGetCustomerQtyDetailViewModel> syncGetCustomerQtyDetailViewModels = new ArrayList<>();

                        if (orderOrderViewModel.RequestBulkQtyUnitUniqueId != null) {
                            SyncGetCustomerQtyDetailViewModel syncGetCustomerQtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                            syncGetCustomerQtyDetailViewModel.UniqueId = UUID.randomUUID();
                            syncGetCustomerQtyDetailViewModel.CustomerCallOrderLineUniqueId = orderOrderViewModel.UniqueId;
                            syncGetCustomerQtyDetailViewModel.ProductUnitUniqueId = orderOrderViewModel.RequestBulkQtyUnitUniqueId;
                            syncGetCustomerQtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(orderOrderViewModel.RequestBulkQty);
                            syncGetCustomerQtyDetailViewModels.add(syncGetCustomerQtyDetailViewModel);
                        }
                        if (orderOrderViewModel.RequestBulkQtyUnitUniqueId == null || !VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                            OrderLineQtyManager customerCallOrderLinesOrderQtyDetailManager = new OrderLineQtyManager(context);
                            List<OrderLineQtyModel> customerCallOrderLinesOrderQtyDetailModels = customerCallOrderLinesOrderQtyDetailManager.getQtyLines(orderOrderViewModel.UniqueId);
                            for (OrderLineQtyModel orderLineQtyModel :
                                    customerCallOrderLinesOrderQtyDetailModels) {
                                if (orderLineQtyModel.Qty != null && orderLineQtyModel.Qty.compareTo(BigDecimal.ZERO) > 0) {
                                    SyncGetCustomerQtyDetailViewModel syncGetCustomerQtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                                    syncGetCustomerQtyDetailViewModel.UniqueId = orderLineQtyModel.UniqueId;
                                    syncGetCustomerQtyDetailViewModel.CustomerCallOrderLineUniqueId = orderLineQtyModel.OrderLineUniqueId;
                                    syncGetCustomerQtyDetailViewModel.ProductUnitUniqueId = orderLineQtyModel.ProductUnitId;
                                    syncGetCustomerQtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(orderLineQtyModel.Qty);
                                    syncGetCustomerQtyDetailViewModels.add(syncGetCustomerQtyDetailViewModel);
                                }
                            }
                        }
                        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                            syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLineOrderQtyDetails = syncGetCustomerQtyDetailViewModels;

                        }else {
                            syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLineInvoiceQtyDetails = syncGetCustomerQtyDetailViewModels;
                            syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLinePromotions = syncGetCustomerCallOrderLinePromotionViewModels;
                        }

                        syncGetCustomerCallOrderLineViewModel.CustomerCallOrderLineBatchQtyDetails = syncGetCustomerCallOrderLineBatchQtyDetailViewModels;
                        syncGetCustomerCallOrderLineViewModels.add(syncGetCustomerCallOrderLineViewModel);
                    }


                    final List<SyncGetCustomerCallOrderPrizeViewModel> syncGetCustomerCallOrderPrizeViewModels = new ArrayList<>();
                    List<OrderPrizeModel> orderPrizeModels;
                    final OrderPrizeManager orderPrizeManager = new OrderPrizeManager(context);
                    orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, customerCallOrderModel.UniqueId));
                    Linq.forEach(orderPrizeModels, new Linq.Consumer<OrderPrizeModel>() {
                        @Override
                        public void run(OrderPrizeModel item) {
                            SyncGetCustomerCallOrderPrizeViewModel syncGetCustomerCallOrderPrizeViewModel = new SyncGetCustomerCallOrderPrizeViewModel();
                            syncGetCustomerCallOrderPrizeViewModel.UniqueId = item.UniqueId;
                            syncGetCustomerCallOrderPrizeViewModel.ProductId = item.ProductId;
                            syncGetCustomerCallOrderPrizeViewModel.DiscountId = item.DiscountId;
                            syncGetCustomerCallOrderPrizeViewModel.DisRef = item.DisRef;
                            syncGetCustomerCallOrderPrizeViewModel.TotalQty = item.TotalQty == null ? 0 : Double.valueOf(String.valueOf(item.TotalQty));
                            syncGetCustomerCallOrderPrizeViewModels.add(syncGetCustomerCallOrderPrizeViewModel);
                        }
                    });
                    syncGetCustomerCallOrderViewModel.OrderPrizes = syncGetCustomerCallOrderPrizeViewModels;
                    syncGetCustomerCallOrderViewModel.OrderLines = syncGetCustomerCallOrderLineViewModels;
                    if (syncGetCustomerCallOrderViewModel.OrderLines.size() > 0)
                        syncGetCustomerCallOrderViewModels.add(syncGetCustomerCallOrderViewModel);


                    if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                        List<CallOrderLinesTempModel> callOrderLinesTemp = new CallOrderLinesTempManager(context).getLines(customerCallOrderModel.UniqueId);
                        if (callOrderLinesTemp.size() > 0) {
                            for (CallOrderLinesTempModel callOrderLineTemp :
                                    callOrderLinesTemp) {

                                SyncGetCustomerCallOrderLineViewModel promotionPreviewLine = new SyncGetCustomerCallOrderLineViewModel();
                                promotionPreviewLine.Description = callOrderLineTemp.Description;
                                promotionPreviewLine.UniqueId = callOrderLineTemp.UniqueId;
                                promotionPreviewLine.CustomerCallOrderUniqueId = callOrderLineTemp.OrderUniqueId;
                                promotionPreviewLine.ProductUniqueId = callOrderLineTemp.ProductUniqueId;
                                promotionPreviewLine.RuleNo = callOrderLineTemp.RuleNo;
                                promotionPreviewLine.PayDuration = callOrderLineTemp.PayDuration;
                                promotionPreviewLine.FreeReasonUniqueId = callOrderLineTemp.FreeReasonId;
                                promotionPreviewLine.SortId = callOrderLineTemp.SortId;
                                promotionPreviewLine.IsRequestFreeItem = callOrderLineTemp.IsRequestFreeItem;
                                promotionPreviewLine.IsRequestPrizeItem = callOrderLineTemp.IsPromoLine;
                                promotionPreviewLine.RequestAdd1Amount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestAdd1Amount);
                                promotionPreviewLine.RequestAdd2Amount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestAdd2Amount);
                                promotionPreviewLine.RequestTaxAmount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestTaxAmount);
                                promotionPreviewLine.RequestChargeAmount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestChargeAmount);
                                promotionPreviewLine.RequestDis1Amount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestDis1Amount);
                                promotionPreviewLine.RequestDis2Amount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestDis2Amount);
                                promotionPreviewLine.RequestDis3Amount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestDis3Amount);
                                promotionPreviewLine.RequestOtherDiscountAmount = HelperMethods.currencyToDouble(callOrderLineTemp.RequestOtherDiscountAmount);
                                promotionPreviewLine.InvoiceAmount = HelperMethods.currencyToDouble(callOrderLineTemp.PromotionPrice);
                                ProductManager productManager = new ProductManager(context);
                                ProductModel productModel = productManager.getItem(ProductManager.getProduct(callOrderLineTemp.ProductUniqueId));

                                promotionPreviewLine.StockUniqueId = productModel.StockUniqueId;
                                List<SyncGetCustomerQtyDetailViewModel> syncGetCustomerQtyDetailViewModels = new ArrayList<>();

                                if (callOrderLineTemp.RequestBulkQtyUnitUniqueId != null) {
                                    SyncGetCustomerQtyDetailViewModel syncGetCustomerQtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                                    syncGetCustomerQtyDetailViewModel.UniqueId = UUID.randomUUID();
                                    syncGetCustomerQtyDetailViewModel.CustomerCallOrderLineUniqueId = callOrderLineTemp.UniqueId;
                                    syncGetCustomerQtyDetailViewModel.ProductUnitUniqueId = callOrderLineTemp.RequestBulkQtyUnitUniqueId;
                                    syncGetCustomerQtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(callOrderLineTemp.RequestBulkQty);
                                    syncGetCustomerQtyDetailViewModels.add(syncGetCustomerQtyDetailViewModel);
                                }
                                if (callOrderLineTemp.RequestBulkQtyUnitUniqueId == null || !VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                                    CallOrderLinesQtyTempManager qtyTempManager = new CallOrderLinesQtyTempManager(context);
                                    List<CallOrderLinesQtyTempModel> customerCallOrderLinesOrderQtyDetailModels = qtyTempManager.getLines(callOrderLineTemp.UniqueId);
                                    for (CallOrderLinesQtyTempModel qtyTempModel :
                                            customerCallOrderLinesOrderQtyDetailModels) {
                                        if (qtyTempModel.Qty != null && qtyTempModel.Qty.compareTo(BigDecimal.ZERO) > 0) {
                                            SyncGetCustomerQtyDetailViewModel syncGetCustomerQtyDetailViewModel = new SyncGetCustomerQtyDetailViewModel();
                                            syncGetCustomerQtyDetailViewModel.UniqueId = qtyTempModel.UniqueId;
                                            syncGetCustomerQtyDetailViewModel.CustomerCallOrderLineUniqueId = qtyTempModel.OrderLineUniqueId;
                                            syncGetCustomerQtyDetailViewModel.ProductUnitUniqueId = qtyTempModel.ProductUnitId;
                                            syncGetCustomerQtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(qtyTempModel.Qty);
                                            syncGetCustomerQtyDetailViewModels.add(syncGetCustomerQtyDetailViewModel);
                                        }
                                    }
                                }

                                promotionPreviewLine.CustomerCallOrderLineOrderQtyDetails = syncGetCustomerQtyDetailViewModels;
                                syncGetCustomerCallOrderViewModel.PromotionsPreview.add(promotionPreviewLine);
                            }
                        }
                    }
                }
            }

        }
        return syncGetCustomerCallOrderViewModels;
    }

    private List<SyncGetCustomerCallReturnViewModel> populateCustomerCallReturns(final UUID customerId) {
        final List<SyncGetCustomerCallReturnViewModel> syncGetCustomerCallReturnViewModels = new ArrayList<>();
        List<CustomerCallReturnModel> customerCallReturnModels = new ArrayList<>();
        final CustomerCallReturnManager customerCallReturnManager = new CustomerCallReturnManager(context);
        CustomerCallReturnLinesViewManager customerCallReturnLinesViewManager = new CustomerCallReturnLinesViewManager(context);
        customerCallReturnModels = customerCallReturnManager.getReturnCalls(customerId, null, null);
        for (CustomerCallReturnModel customerCallReturnModel :
                customerCallReturnModels) {

            SyncGetCustomerCallReturnViewModel syncGetCustomerCallReturnViewModel = new SyncGetCustomerCallReturnViewModel();
            syncGetCustomerCallReturnViewModel.UniqueId = customerCallReturnModel.UniqueId;
            syncGetCustomerCallReturnViewModel.ReturnTypeUniqueId = customerCallReturnModel.ReturnTypeUniqueId;
            //syncGetCustomerCallReturnViewModel.ReturnReasonUniqueId = customerCallReturnModel.ReturnReasonUniqueId;
            //syncGetCustomerCallReturnViewModel.CallActionStatusUniqueId = customerCallReturnModel.CallActionStatusUniqueId;
            syncGetCustomerCallReturnViewModel.SubSystemTypeUniqueId = VaranegarApplication.getInstance().getAppId();
            //syncGetCustomerCallReturnViewModel.ReturnRequestRejectReasonUniqueId = customerCallReturnModel.ReturnRequestRejectReasonUniqueId;
            syncGetCustomerCallReturnViewModel.Comment = customerCallReturnModel.Comment;
            //syncGetCustomerCallReturnViewModel.TotalReturnAmount = HelperMethods.currencyToDouble(customerCallReturnModel.TotalReturnAmount);
            syncGetCustomerCallReturnViewModel.ReturnStartTime = customerCallReturnModel.StartTime;
            syncGetCustomerCallReturnViewModel.ReturnEndTime = customerCallReturnModel.EndTime;
            syncGetCustomerCallReturnViewModel.ReplacementRegistration = customerCallReturnModel.ReplacementRegistration;
//            OrderAmount orderAmount = customerCallReturnLinesViewManager.calculateTotalAmount(customerId, customerCallReturnModel.BackOfficeInvoiceId, null);
//            syncGetCustomerCallReturnViewModel.TotalReturnOtherDiscount = HelperMethods.currencyToDouble(orderAmount.DiscountAmount);
//            syncGetCustomerCallReturnViewModel.TotalReturnTax = HelperMethods.currencyToDouble(orderAmount.TaxAmount);
//            syncGetCustomerCallReturnViewModel.TotalReturnAdd1 = HelperMethods.currencyToDouble(orderAmount.Add1Amount);
//            syncGetCustomerCallReturnViewModel.TotalReturnAdd2 = HelperMethods.currencyToDouble(orderAmount.Add2Amount);
//            syncGetCustomerCallReturnViewModel.TotalReturnCharge = HelperMethods.currencyToDouble(orderAmount.ChargeAmount);
//            syncGetCustomerCallReturnViewModel.TotalReturnNetAmount = HelperMethods.currencyToDouble(orderAmount.NetAmount);
            syncGetCustomerCallReturnViewModel.OperationDate = customerCallReturnModel.StartTime;
            syncGetCustomerCallReturnViewModel.DealerUniqueId = customerCallReturnModel.DealerUniqueId;
            //syncGetCustomerCallReturnViewModel.TotalReturnCharge = HelperMethods.currencyToDouble(customerCallReturnModel.TotalReturnCharge);
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                syncGetCustomerCallReturnViewModel.CustomerCallReturnTypeUniqueId = UUID.fromString("1EB2235D-54D2-40C1-B84C-6BE1AAC7ED7C");

                syncGetCustomerCallReturnViewModel.ShipToPartyUniqueId=customerCallReturnModel.ShipToPartyUniqueId;
                syncGetCustomerCallReturnViewModel.ShipToPartyCode=customerCallReturnModel.ShipToPartyCode;
            }else
                syncGetCustomerCallReturnViewModel.CustomerCallReturnTypeUniqueId = UUID.fromString("40E5F33E-EE5B-4EF0-AEF4-E98C376785AC");
            syncGetCustomerCallReturnViewModel.BackOfficeInvoiceNo = customerCallReturnModel.BackOfficeInvoiceNo;
            //syncGetCustomerCallReturnViewModel.BackOfficeInvoiceRef = customerCallReturnModel.BackOfficeInvoiceId;
            syncGetCustomerCallReturnViewModel.BackOfficeInvoiceId = customerCallReturnModel.BackOfficeInvoiceId == null ? null : customerCallReturnModel.BackOfficeInvoiceId.toString();
            syncGetCustomerCallReturnViewModel.BackOfficeInvoiceId = customerCallReturnModel.BackOfficeInvoiceId == null ? null : customerCallReturnModel.BackOfficeInvoiceId.toString();
            final List<SyncGetCustomerCallReturnLineViewModel> syncGetCustomerCallReturnLineViewModels = new ArrayList<>();

            List<CustomerCallReturnLinesViewModel> returnLinesViewModels = customerCallReturnLinesViewManager.getReturnLines(customerCallReturnModel.UniqueId, true);

            ReturnLinesRequestManager returnLinesRequestManager = new ReturnLinesRequestManager(context);

            for (final CustomerCallReturnLinesViewModel line :
                    returnLinesViewModels) {
                SyncGetCustomerCallReturnLineViewModel syncGetCustomerCallReturnLineViewModel = new SyncGetCustomerCallReturnLineViewModel();
                syncGetCustomerCallReturnLineViewModel.UniqueId = line.UniqueId;
                syncGetCustomerCallReturnLineViewModel.ProductUniqueId = line.ProductId;
                syncGetCustomerCallReturnLineViewModel.UnitPrice = HelperMethods.currencyToDouble(line.RequestUnitPrice);
                syncGetCustomerCallReturnLineViewModel.IsPrize = line.IsPromoLine;
                syncGetCustomerCallReturnLineViewModel.ReferenceDate = line.ReferenceDate;
                syncGetCustomerCallReturnLineViewModel.StockUniqueId = line.StockId;
                syncGetCustomerCallReturnLineViewModel.ReferenceId = line.ReferenceId;
                syncGetCustomerCallReturnLineViewModel.ReferenceNo = line.ReferenceNo;
                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                    syncGetCustomerCallReturnLineViewModel.TotalRequestAmount = HelperMethods.currencyToDouble(line.TotalRequestAmount);
                    syncGetCustomerCallReturnLineViewModel.TotalRequestAdd1Amount = HelperMethods.currencyToDouble(line.TotalRequestAdd1Amount);
                    syncGetCustomerCallReturnLineViewModel.TotalRequestAdd2Amount = HelperMethods.currencyToDouble(line.TotalRequestAdd2Amount);
                    syncGetCustomerCallReturnLineViewModel.TotalRequestAddOtherAmount = HelperMethods.currencyToDouble(line.TotalRequestAddOtherAmount);
                    syncGetCustomerCallReturnLineViewModel.TotalRequestDiscount = HelperMethods.currencyToDouble(line.TotalRequestDis1Amount) +
                            HelperMethods.currencyToDouble(line.TotalRequestDis2Amount) +
                            HelperMethods.currencyToDouble(line.TotalRequestDis3Amount) +
                            HelperMethods.currencyToDouble(line.TotalRequestDisOtherAmount);
                    syncGetCustomerCallReturnLineViewModel.TotalRequestTax = HelperMethods.currencyToDouble(line.TotalRequestTax);
                    syncGetCustomerCallReturnLineViewModel.TotalRequestCharge = HelperMethods.currencyToDouble(line.TotalRequestCharge);
                    syncGetCustomerCallReturnLineViewModel.TotalRequestNetAmount = HelperMethods.currencyToDouble(line.TotalRequestNetAmount);
                } else {
                    syncGetCustomerCallReturnLineViewModel.TotalReturnAmount = HelperMethods.currencyToDouble(line.TotalRequestAmount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnAdd1 = HelperMethods.currencyToDouble(line.TotalRequestAdd1Amount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnAdd2 = HelperMethods.currencyToDouble(line.TotalRequestAdd2Amount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnAddOther = HelperMethods.currencyToDouble(line.TotalRequestAddOtherAmount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnDis1 = HelperMethods.currencyToDouble(line.TotalRequestDis1Amount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnDis2 = HelperMethods.currencyToDouble(line.TotalRequestDis2Amount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnDis3 = HelperMethods.currencyToDouble(line.TotalRequestDis3Amount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnDisOther = HelperMethods.currencyToDouble(line.TotalRequestDisOtherAmount);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnTax = HelperMethods.currencyToDouble(line.TotalRequestTax);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnCharge = HelperMethods.currencyToDouble(line.TotalRequestCharge);
                    syncGetCustomerCallReturnLineViewModel.TotalReturnNetAmount = HelperMethods.currencyToDouble(line.TotalRequestNetAmount);
                    if (customerCallReturnModel.IsFromRequest) {
                        ReturnLinesRequestModel requestLine = returnLinesRequestManager.getItem(line.UniqueId);
                        if (requestLine != null) {
                            syncGetCustomerCallReturnLineViewModel.TotalRequestAmount = HelperMethods.currencyToDouble(requestLine.TotalRequestNetAmount);
                            syncGetCustomerCallReturnLineViewModel.TotalRequestAdd1Amount = HelperMethods.currencyToDouble(requestLine.TotalRequestAdd1Amount);
                            syncGetCustomerCallReturnLineViewModel.TotalRequestAdd2Amount = HelperMethods.currencyToDouble(requestLine.TotalRequestAdd2Amount);
                            syncGetCustomerCallReturnLineViewModel.TotalRequestAddOtherAmount = HelperMethods.currencyToDouble(requestLine.TotalRequestAddOtherAmount);
                            syncGetCustomerCallReturnLineViewModel.TotalRequestDiscount = HelperMethods.currencyToDouble(requestLine.TotalRequestDis1Amount) +
                                    HelperMethods.currencyToDouble(requestLine.TotalRequestDis2Amount) +
                                    HelperMethods.currencyToDouble(requestLine.TotalRequestDis3Amount) +
                                    HelperMethods.currencyToDouble(requestLine.TotalRequestDisOtherAmount);
                            syncGetCustomerCallReturnLineViewModel.TotalRequestTax = HelperMethods.currencyToDouble(requestLine.TotalRequestTax);
                            syncGetCustomerCallReturnLineViewModel.TotalRequestCharge = HelperMethods.currencyToDouble(requestLine.TotalRequestCharge);
                            syncGetCustomerCallReturnLineViewModel.TotalRequestNetAmount = HelperMethods.currencyToDouble(requestLine.TotalRequestNetAmount);
                        }
                    }
                }
                syncGetCustomerCallReturnLineViewModel.ReturnReasonUniqueId = line.ReturnReasonId;
                syncGetCustomerCallReturnLineViewModel.ReturnProductTypeUniqueId = line.ReturnProductTypeId;
                syncGetCustomerCallReturnLineViewModel.IsFreeItem = line.IsFreeItem;
                syncGetCustomerCallReturnLineViewModel.EditReasonUniqueId = line.EditReasonId;


                final List<SyncGetCustomerCallReturnLineQtyDetailViewModel> syncGetCustomerCallReturnLineQtyDetailViewModels = new ArrayList<SyncGetCustomerCallReturnLineQtyDetailViewModel>();
                if (line.RequestBulkUnitId != null) {
                    SyncGetCustomerCallReturnLineQtyDetailViewModel syncGetCustomerCallReturnLineQtyDetailViewModel = new SyncGetCustomerCallReturnLineQtyDetailViewModel();
                    syncGetCustomerCallReturnLineQtyDetailViewModel.UniqueId = UUID.randomUUID();
                    syncGetCustomerCallReturnLineQtyDetailViewModel.CustomerCallReturnLineUniqueId = line.UniqueId;
                    syncGetCustomerCallReturnLineQtyDetailViewModel.ProductUnitUniqueId = line.RequestBulkUnitId;
                    syncGetCustomerCallReturnLineQtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(line.RequestBulkQty);
                    syncGetCustomerCallReturnLineQtyDetailViewModels.add(syncGetCustomerCallReturnLineQtyDetailViewModel);
                }
                if (line.RequestBulkUnitId == null || !VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                    List<ReturnLineQtyModel> returnLineQtyModels;
                    final ReturnLineQtyManager returnLineQtyManager = new ReturnLineQtyManager(context);
                    returnLineQtyModels = returnLineQtyManager.getItems(ReturnLineQtyManager.getReturnLines(line.UniqueId));
                    for (ReturnLineQtyModel returnLineQtyModel :
                            returnLineQtyModels) {
                        if (returnLineQtyModel.Qty != null && returnLineQtyModel.Qty.compareTo(BigDecimal.ZERO) > 0) {
                            SyncGetCustomerCallReturnLineQtyDetailViewModel syncGetCustomerCallReturnLineQtyDetailViewModel = new SyncGetCustomerCallReturnLineQtyDetailViewModel();
                            syncGetCustomerCallReturnLineQtyDetailViewModel.UniqueId = returnLineQtyModel.UniqueId;
                            syncGetCustomerCallReturnLineQtyDetailViewModel.CustomerCallReturnLineUniqueId = returnLineQtyModel.ReturnLineUniqueId;
                            syncGetCustomerCallReturnLineQtyDetailViewModel.ProductUnitUniqueId = returnLineQtyModel.ProductUnitId;
                            syncGetCustomerCallReturnLineQtyDetailViewModel.Qty = HelperMethods.bigDecimalToDouble(returnLineQtyModel.Qty);
                            syncGetCustomerCallReturnLineQtyDetailViewModels.add(syncGetCustomerCallReturnLineQtyDetailViewModel);
                        }
                    }
                }
                syncGetCustomerCallReturnLineViewModel.CustomerCallReturnLineQtyDetails = syncGetCustomerCallReturnLineQtyDetailViewModels;
                syncGetCustomerCallReturnLineViewModels.add(syncGetCustomerCallReturnLineViewModel);
            }
            syncGetCustomerCallReturnViewModels.add(syncGetCustomerCallReturnViewModel);
            syncGetCustomerCallReturnViewModel.OrderReturnLines = syncGetCustomerCallReturnLineViewModels;
        }

        return syncGetCustomerCallReturnViewModels;
    }

    public static String getFilePath(Context context) {
        return context.getFilesDir() + "/" + FILE_NAME;
    }

    private static String getSentTourFilePath(Context context) {
        return context.getFilesDir() + "/" + "TourInfo.ser";
    }

    private void onTourFailure(@Nullable TourCallBack tourCallBack, String error) {
        if (tourCallBack != null)
            tourCallBack.onFailure(error == null ? context.getString(R.string.unknown_error) : error);
    }

    private void onTourFailure(@Nullable TourCallBack tourCallBack, @StringRes int error) {
        if (tourCallBack != null)
            tourCallBack.onFailure(context.getString(error));
    }

    private void onTourSuccess(@Nullable TourCallBack tourCallBack) {
        if (tourCallBack != null)
            tourCallBack.onSuccess();
    }

    public interface TourCallBack {
        void onSuccess();

        void onFailure(String error);

        void onProgressChanged(String progress);
    }

    @Nullable
    public TourInfo createTourInfo() {
        final TourModel tourModel = loadTour();
        if (tourModel == null)
            return null;
        TourInfo tourInfo = new TourInfo();
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            int totalCustomerCounts = 0, totalVisited = 0, totalOrdered = 0, totalLackOfOrder = 0, totalLackOfVisit = 0;
            int dayCustomerCounts = 0, dayVisited = 0, dayOrdered = 0, dayLackOfOrder = 0, dayLackOfVisit = 0;

            CustomerPathViewManager customerPathViewManager = new CustomerPathViewManager(context);

            List<CustomerModel> allCustomerModels = new CustomerManager(context).getAll();
            List<CustomerPathViewModel> dayCustomerModels = customerPathViewManager.getItems(CustomerPathViewManager.getDay(tourModel.DayVisitPathId, false));
            Set<UUID> todayCustomerIds = new HashSet<>(dayCustomerModels.size());
            for (CustomerPathViewModel customerPathViewModel :
                    dayCustomerModels) {
                todayCustomerIds.add(customerPathViewModel.UniqueId);
            }
            for (CustomerModel customerModel : allCustomerModels) {
                boolean isToday = false;
                totalCustomerCounts++;
                if (todayCustomerIds.contains(customerModel.UniqueId)) {
                    dayCustomerCounts++;
                    isToday = true;
                }
                CustomerCallManager callManager = new CustomerCallManager(context);
                List<CustomerCallModel> calls = callManager.loadCalls(customerModel.UniqueId);
                if (callManager.isLackOfOrder(calls)) {
                    totalLackOfOrder++;
                    totalVisited++;
                    if (isToday) {
                        dayLackOfOrder++;
                        dayVisited++;
                    }
                } else if (callManager.isLackOfVisit(calls)) {
                    totalLackOfVisit++;
                    if (isToday)
                        dayLackOfVisit++;
                } else if (callManager.hasOrderOrReturnCall(calls)) {
                    totalOrdered++;
                    totalVisited++;
                    if (isToday) {
                        dayOrdered++;
                        dayVisited++;
                    }
                }
            }


            tourInfo.DayCustomersCount = dayCustomerCounts;
            tourInfo.DayLackOfVisitCount = dayLackOfVisit;
            tourInfo.DayVisitedCount = dayVisited;
            tourInfo.DayLackOfOrderCount = dayLackOfOrder;
            tourInfo.DayOrderedCount = dayOrdered;

            tourInfo.TotalCustomersCount = totalCustomerCounts;
            tourInfo.TotalLackOfVisitCount = totalLackOfVisit;
            tourInfo.TotalVisitedCount = totalVisited;
            tourInfo.TotalLackOfOrderCount = totalLackOfOrder;
            tourInfo.TotalOrderedCount = totalOrdered;


            // SPD
            ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(context);
            tourInfo.Spd = productOrderViewManager.getSPD();


            // order net amounts
            RequestReportViewManager requestReportViewManager = new RequestReportViewManager(context);
            List<RequestReportViewModel> requestReportViewModels = requestReportViewManager.getItems(RequestReportViewManager.getAll());
            Currency allNetAmount = Currency.ZERO;
            Currency dayNetAmount = Currency.ZERO;
            for (RequestReportViewModel requestReportViewModel : requestReportViewModels) {
                allNetAmount = allNetAmount.add(requestReportViewModel.TotalOrderNetAmount == null ? Currency.ZERO : requestReportViewModel.TotalOrderNetAmount);
                if (todayCustomerIds.contains(requestReportViewModel.UniqueId))
                    dayNetAmount = dayNetAmount.add(requestReportViewModel.TotalOrderNetAmount);
            }

            tourInfo.DayOrderSum = dayNetAmount;
            tourInfo.TotalOrderSum = allNetAmount;

            // ratios
            if (dayCustomerCounts > 0)
                tourInfo.DayVisitRatio = (((double) dayVisited / dayCustomerCounts) * 100);
            if (totalCustomerCounts > 0)
                tourInfo.TotalVisitRatio = (((double) totalVisited / totalCustomerCounts) * 100);

        } else {
            Currency allNetAmount = Currency.ZERO;
            int totalDelivery = 0, totalPartialDelivery = 0, totalCustomerCounts = 0, totalVisited = 0, totalLackOfDelivery = 0, totalReturns = 0, totalLackOfVisit = 0;
            CustomerCallManager callManager = new CustomerCallManager(context);

            CustomerPathViewManager customerPathViewManager = new CustomerPathViewManager(context);
            List<CustomerPathViewModel> dayCustomerModels = customerPathViewManager.getItems(CustomerPathViewManager.getDay(tourModel.DayVisitPathId, false));
            totalCustomerCounts = dayCustomerModels.size();
            for (CustomerModel customerModel : dayCustomerModels) {
                List<CustomerCallModel> calls = callManager.loadCalls(customerModel.UniqueId);
                if (callManager.isCompleteLackOfDelivery(calls)) {
                    totalLackOfDelivery++;
                    totalVisited++;
                } else if (callManager.isCompleteReturnDelivery(calls)) {
                    totalReturns++;
                    totalVisited++;
                } else if (callManager.isLackOfVisit(calls)) {
                    totalLackOfVisit++;
                } else if (callManager.hasDistCall(calls)) {
                    CustomerCalls cc = new CustomerCalls(calls);
                    List<CustomerCallModel> orderReturns = cc.getCall(CustomerCallType.OrderReturn);
                    List<CustomerCallModel> orderLackOfDelivery = cc.getCall(CustomerCallType.OrderLackOfDelivery);
                    List<CustomerCallModel> orderDelivery = cc.getCall(CustomerCallType.OrderDelivered);
                    List<CustomerCallModel> orderPartialDelivery = cc.getCall(CustomerCallType.OrderPartiallyDelivered);
                    int all = orderPartialDelivery.size() + orderDelivery.size() + orderLackOfDelivery.size() + orderReturns.size();
                    if (orderReturns.size() == all)
                        totalReturns++;
                    else if (orderLackOfDelivery.size() == all)
                        totalLackOfDelivery++;
                    else if (orderDelivery.size() == all)
                        totalDelivery++;
                    else
                        totalPartialDelivery++;

                    totalVisited++;
                }

                // order net amounts
                RequestReportViewManager requestReportViewManager = new RequestReportViewManager(context);
                List<RequestReportViewModel> requestReportViewModels = requestReportViewManager.getItems(RequestReportViewManager.getCurrentCustomer(customerModel.UniqueId));
                for (RequestReportViewModel requestReportViewModel : requestReportViewModels) {
                    boolean isDelivery = callManager.hasDeliveryCall(calls, requestReportViewModel.OrderUniqueId, null);
                    if (isDelivery) {
                        allNetAmount = allNetAmount.add(requestReportViewModel.TotalOrderNetAmount == null ? Currency.ZERO : requestReportViewModel.TotalOrderNetAmount);
                    }
                }
            }


            tourInfo.DeliveriesCount = totalDelivery;
            tourInfo.LackOfDeliveriesCount = totalLackOfDelivery;
            tourInfo.ReturnsCount = totalReturns;
            tourInfo.PartialDeliveriesCount = totalPartialDelivery;


            tourInfo.TotalCustomersCount = totalCustomerCounts;
            tourInfo.TotalLackOfVisitCount = totalLackOfVisit;
            tourInfo.TotalVisitedCount = totalVisited;

            tourInfo.TotalOrderSum = allNetAmount;

            DistributionCustomerCallManager distributionCustomerCallManager = new DistributionCustomerCallManager(context);
            tourInfo.DistNo = distributionCustomerCallManager.getDistNumbers();
        }

        // times
        long totalVisitTime = CustomerActionTimeManager.getCustomerCallTimes(context);
        String totalVisitTimeStr = DateHelper.getTimeSpanString(totalVisitTime);
        UpdateManager updateManager = new UpdateManager(context);
        final Date date = updateManager.getLog(UpdateKey.TourStartTime);
        long totalTourTime = (new Date().getTime() - date.getTime()) / 1000;
        String totalTourTimeStr = DateHelper.getTimeSpanString(totalTourTime);
        tourInfo.VisitTime = totalVisitTimeStr;
        tourInfo.TourTime = totalTourTimeStr;


        tourInfo.PersonnelId = UserManager.readFromFile(context).UniqueId;
        tourInfo.Time = new Date();
        tourInfo.TourId = tourModel.UniqueId;
        tourInfo.TourNo = tourModel.TourNo;
        return tourInfo;
    }

    private void saveTourInfoToFile(TourInfo tourInfoForPrint) {
        try {
            FileOutputStream fos = new FileOutputStream(getSentTourFilePath(context));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tourInfoForPrint);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    @Nullable
    public TourInfo loadTourFromFile() {
        FileInputStream is;
        TourInfo tourInfo = null;
        try {
            File myFile = new File(getSentTourFilePath(context));
            if (!myFile.exists())
                return null;
            is = new FileInputStream(getSentTourFilePath(context));
            ObjectInputStream ois = new ObjectInputStream(is);
            tourInfo = (TourInfo) ois.readObject();
            ois.close();
            is.close();
        } catch (Exception e) {
            Timber.e(e);
        }
        return tourInfo;
    }

    public void deleteTourInfoFile() {
        File myFile = new File(getSentTourFilePath(context));
        if (myFile.exists())
            myFile.delete();
    }
}
