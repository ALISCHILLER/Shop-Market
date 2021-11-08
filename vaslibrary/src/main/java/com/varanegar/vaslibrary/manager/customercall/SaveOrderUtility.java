package com.varanegar.vaslibrary.manager.customercall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.CustomerPaymentTypesViewManager;
import com.varanegar.vaslibrary.manager.FreeReasonManager;
import com.varanegar.vaslibrary.manager.GoodsCustQuotaSummaryManager;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.PromotionException;
import com.varanegar.vaslibrary.manager.RequestReportViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.InventoryError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.model.CustomerPaymentTypesView.CustomerPaymentTypesViewModel;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrder;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.pricecalculator.PriceCalcCallback;
import com.varanegar.vaslibrary.pricecalculator.PriceCalculator;
import com.varanegar.vaslibrary.promotion.CalcPromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderLinePromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderPromotion;
import com.varanegar.vaslibrary.promotion.PromotionCallback;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlApi;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlDetailViewModel;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlHeaderViewModel;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlOrderDetailViewModel;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;

/**
 * محاسبات صفحه تحویل سفارش
 * و ثبت صفارش
 */
public class SaveOrderUtility {
    private CustomerCallOrderModel callOrderModel;
    private final CustomerCallOrderManager callOrderManager;
    private final Context context;
    private UUID customerId;
    private UUID callOrderId;
    private CustomerModel customer;
    private ProgressDialog progressDialog;
    private boolean dialog;

    public void setCallback(ISaveOrderCallback callback) {
        this.callback = callback;
    }

    private ISaveOrderCallback callback;

    public void setPriceClassRef(int priceClassRef) {
        this.priceClassRef = priceClassRef;
    }

    private int priceClassRef;

    PaymentTypeOrderModel oldPaymentType;

    public void setOldPaymentTypeId(UUID oldPaymentTypeId) {
        this.oldPaymentType = new PaymentOrderTypeManager(context).getItem(oldPaymentTypeId);
    }

    public PaymentTypeOrderModel getOldPaymentType() {
        return oldPaymentType;
    }

    PaymentTypeOrderModel newPaymentType;

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void setReturnReasonUniqueId(UUID returnReasonUniqueId) {
        this.returnReasonUniqueId = returnReasonUniqueId;
    }

    private UUID returnReasonUniqueId;

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void setOrderPrizeList(List<DiscountOrderPrizeViewModel> orderPrizeList) {
        this.orderPrizeList = orderPrizeList;
    }

    private List<DiscountOrderPrizeViewModel> orderPrizeList;

    public SaveOrderUtility(@NonNull Context context) {
        this.context = context;
        callOrderManager = new CustomerCallOrderManager(context);
    }


    private String getString(@StringRes int resId) {
        if (context != null)
            return context.getString(resId);
        else
            return "";
    }

    public enum SaveOrderCallbackType {
        NoSystemPayment,
        NetworkError,
        ReturnControl,
        SelectPrize,
        SelectReturnReason,
        CreditsError
    }

    public interface ISaveOrderCallback {
        void onSuccess();

        void onError(String msg);

        void onProcess(String msg);

        void onAlert(SaveOrderCallbackType type, String title, String msg, @Nullable IWarningCallBack callBack);

        void onCancel();
    }

    public interface IWarningCallBack {
        void onContinue();

        void cancel();
    }

    private void runCallBackSuccess() {
        dismissProgressDialog();
        if (callback != null)
            callback.onSuccess();
    }

    private void runCallBackError(String msg) {
        dismissProgressDialog();
        if (callback != null)
            callback.onError(msg);
    }

    private void runCallBackError(@StringRes int resId) {
        runCallBackError(getString(resId));
    }

    private void runCallBackProcess(String msg) {
        if (dialog)
            showProgressDialog(msg);
        if (callback != null)
            callback.onProcess(msg);
    }

    private void runCallBackProcess(@StringRes int resId) {
        runCallBackProcess(getString(resId));
    }

    private void runCallBackAlert(SaveOrderCallbackType type, String title, String msg, @Nullable IWarningCallBack callBack) {
        dismissProgressDialog();
        if (callback != null)
            callback.onAlert(type, title, msg, callBack);
    }

    private void runCallBackAlert(SaveOrderCallbackType type, @StringRes int title, String msg, @Nullable IWarningCallBack callBack) {
        runCallBackAlert(type, getString(title), msg, callBack);
    }

    private void runCallBackAlert(SaveOrderCallbackType type, @StringRes int title, @StringRes int msg, @Nullable IWarningCallBack callBack) {
        runCallBackAlert(type, getString(title), getString(msg), callBack);
    }

    private void runCallBackCancel() {
        dismissProgressDialog();
        if (callback != null)
            callback.onCancel();
    }


    private void showProgressDialog(String msg) {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.setMessage(msg);
        else {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(msg);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Error e) {
            Timber.e(e);
        }
    }

    public void saveOrderWithProgressDialog(@NotNull CustomerCallOrderModel callOrderModel) {
        dialog = true;
        showProgressDialog(getString(R.string.please_wait));
        saveOrder(callOrderModel);
    }

    public void saveOrder(@NotNull CustomerCallOrderModel callOrderModel) {
        this.callOrderModel = callOrderModel;
        this.customerId = callOrderModel.CustomerUniqueId;
        this.customer = new CustomerManager(context).getItem(customerId);
        this.callOrderId = callOrderModel.UniqueId;
        PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(context);
        newPaymentType = paymentOrderTypeManager.getItem(callOrderModel.OrderPaymentTypeUniqueId);
        dialog = false;
        if (newPaymentType != null && oldPaymentType != null && !newPaymentType.UniqueId.equals(oldPaymentType.UniqueId)) {
            runCallBackProcess(R.string.calculating_prices_for_customer);
            extractAndCalcCustomerPrice(new PriceCalcCallback() {
                @Override
                public void onSucceeded() {
                    runCallBackProcess(R.string.please_wait);
                    saveOrder2();
                }

                @Override
                public void onFailed(String error) {
                    runCallBackError(error);
                }
            });
        } else
            saveOrder2();
    }

    private void saveOrder2() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            calculateCreditAndTrySave();
        else
            save();
    }

    protected void extractAndCalcCustomerPrice(PriceCalcCallback priceCalcCallback) {
        final Handler handler = new Handler(Looper.getMainLooper());
        Thread thread = new Thread(() -> {
            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
                int paymentTypeOrderGroupRef;
                if (sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false))
                    paymentTypeOrderGroupRef = newPaymentType.GroupBackOfficeId;
                else
                    paymentTypeOrderGroupRef = oldPaymentType.GroupBackOfficeId;
                int orderTypeId = 0;
                try {
                    orderTypeId = new CustomerOrderTypesManager(context).getItem(callOrderModel.BackOfficeOrderTypeId).BackOfficeId;
                } catch (Exception ignored) {
                }

                PriceCalculator priceCalculator = PriceCalculator.getPriceCalculator(context,
                        customerId,
                        callOrderId,
                        paymentTypeOrderGroupRef,
                        orderTypeId,
                        priceClassRef);
                priceCalculator.calculateAndSavePriceList(false, new PriceCalcCallback() {
                    @Override
                    public void onSucceeded() {
                        handler.post(() -> priceCalcCallback.onSucceeded());
                    }

                    @Override
                    public void onFailed(String error) {
                        handler.post(() -> priceCalcCallback.onFailed(error));
                    }
                });
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
                handler.post(() -> priceCalcCallback.onFailed(getString(R.string.back_office_type_is_uknown)));
            }

        });
        thread.start();
    }

    @SubsystemType(id = SubsystemTypeId.PreSales)
    private void calculateCreditAndTrySave() {
        try {
            List<CustomerCallOrderOrderViewModel> customerCallOrderOrderViewModels = new CustomerCallOrderOrderViewManager(context).getLines(callOrderId, null);
            final CustomerCallManager callManager = new CustomerCallManager(context);
            // todo: why !!!!!???????
            callManager.saveOrderCall(customerId, callOrderId);
            RequestReportViewManager requestReportViewManager = new RequestReportViewManager(context);
            List<RequestReportViewModel> requestReportViewModels = requestReportViewManager.getItems(RequestReportViewManager.getCurrentCustomer(customerId));
            CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(context);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            final SysConfigModel orderBedLimit = sysConfigManager.read(ConfigKey.OrderBedLimit, SysConfigManager.cloud);
            final SysConfigModel orderAsnLimit = sysConfigManager.read(ConfigKey.OrderAsnLimit, SysConfigManager.cloud);
            String errorMessage = customerCallOrderManager.checkCustomerCredits(requestReportViewModels, customer, orderBedLimit, orderAsnLimit);
            String maxMinErrorMessage = "";
            if (requestReportViewModels != null && requestReportViewModels.size() > 0)
                maxMinErrorMessage = checkMaxOrderAmount(requestReportViewModels.get(requestReportViewModels.size() - 1));
            String fullErrorMessage = maxMinErrorMessage;
            // todo: why !!!!!???????
            callManager.removeCallOrder(customerId, callOrderId);
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType != BackOfficeType.Rastak) {
                GoodsCustQuotaSummaryManager goodsCustQuotaSummaryManager = new GoodsCustQuotaSummaryManager(context);
                String customerProductQuotasErrorMessage = goodsCustQuotaSummaryManager.checkCustomerProductLimitation(context, customerCallOrderOrderViewModels, customer);
                String productQuotasAndCustomerCredit = fullErrorMessage + customerProductQuotasErrorMessage;
                if (!(VaranegarApplication.is(VaranegarApplication.AppId.PreSales))) {
                    if (productQuotasAndCustomerCredit.isEmpty()) {
                        save();
                    } else {
                        throw new Exception(productQuotasAndCustomerCredit);
                    }
                } else {
                    if (errorMessage.equalsIgnoreCase("") && productQuotasAndCustomerCredit.isEmpty())
                        save();
                    else {
                        if (!customerProductQuotasErrorMessage.isEmpty() || !maxMinErrorMessage.isEmpty()) {
                            throw new Exception(customerProductQuotasErrorMessage + "\n" + maxMinErrorMessage);
                        } else if (!errorMessage.isEmpty()) {
                            if (!SysConfigManager.compare(orderBedLimit, "2") && !SysConfigManager.compare(orderAsnLimit, "2") || backOfficeType == BackOfficeType.ThirdParty) {
                                runCallBackAlert(SaveOrderCallbackType.CreditsError, R.string.warning, errorMessage, new IWarningCallBack() {
                                    @Override
                                    public void onContinue() {
                                        save();
                                    }

                                    @Override
                                    public void cancel() {
                                        runCallBackCancel();
                                    }
                                });
                            } else
                                throw new Exception(errorMessage);
                        }
                    }
                }
            } else
                save();
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            runCallBackError(R.string.back_office_type_is_uknown);
        } catch (DbException e) {
            Timber.e(e);
            runCallBackError(R.string.error_saving_request);
        } catch (ValidationException e) {
            Timber.e(e);
            runCallBackError(R.string.error_saving_request);
        } catch (Exception e) {
            Timber.e(e);
            runCallBackError(e.getMessage());
        }
    }

    private String checkMaxOrderAmount(RequestReportViewModel requestReportViewModel) {
        String errorMessage = "";
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.MaxOrderAmount, SysConfigManager.cloud);
        if (sysConfigModel != null && !(sysConfigModel.Value.equals("0")) && requestReportViewModel.TotalOrderNetAmount.compareTo(new Currency(Integer.valueOf(sysConfigModel.Value))) == 1) {
            errorMessage = getString(R.string.this_order_amount) + requestReportViewModel.TotalOrderNetAmount + "\n" + getString(R.string.max_amount) + sysConfigModel.Value + "\n";
        }
        return errorMessage;
    }

    private void save() {
        try {
            final CustomerPaymentTypesViewModel[] selectedPaymentType = new CustomerPaymentTypesViewModel[1];
            List<CustomerPaymentTypesViewModel> customerPaymentTypes = new CustomerPaymentTypesViewManager(context).getCustomerPaymentType(customerId);
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            final SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.OrderValidationErrorType, SysConfigManager.cloud);
            boolean calcSaleRestriction = SysConfigManager.compare(sysConfigModel, "266FB268-8106-40EE-B19F-C00FB79569C4") || SysConfigManager.compare(sysConfigModel, "337BCEA8-32A9-4A9E-8A70-C5C73944869D");
            SharedPreferences sharedPreferences = context.getSharedPreferences("UsanceDaySharedPrefences", context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            final boolean calPaymentType = sharedPreferences.getBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false);
            if (calcSaleRestriction || calPaymentType) {
                CalcPromotion.calcPromotionV3(null, null, context, callOrderId, customerId, EVCType.TOSELL, false, calcSaleRestriction, calPaymentType, new PromotionCallback() {
                    @Override
                    public void onSuccess(CustomerCallOrderPromotion data) {
                        PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(context);
                        if (!calPaymentType) {
                            data.OrderPaymentTypeId = callOrderModel.OrderPaymentTypeUniqueId;
                        }
                        PaymentTypeOrderModel paymentTypeOrderModel = paymentOrderTypeManager.getItem(new Query().from(PaymentTypeOrder.PaymentTypeOrderTbl)
                                .whereAnd(Criteria.equals(PaymentTypeOrder.UniqueId, data.OrderPaymentTypeId)));
                        if (paymentTypeOrderModel != null) {
                            for (CustomerPaymentTypesViewModel customerPaymentTypesViewModel : customerPaymentTypes) {
                                if (customerPaymentTypesViewModel.UniqueId.equals(paymentTypeOrderModel.UniqueId))
                                    selectedPaymentType[0] = customerPaymentTypesViewModel;
                            }
                            if (callOrderModel == null || data == null || data.OrderPaymentTypeId == null)
                                runCallBackError(R.string.error_on_usance_day);
                            else {
                                try {
                                    callOrderModel.OrderPaymentTypeUniqueId = data.OrderPaymentTypeId;
                                    long affectedRows = new CustomerCallOrderManager(context).update(callOrderModel);
                                    Timber.e("update OrderPaymentTypeUniqueId in customer call order ", affectedRows);
                                    extractAndCalcCustomerPrice(new PriceCalcCallback() {
                                        @Override
                                        public void onSucceeded() {
                                            runCallBackProcess(R.string.please_wait);
                                            finalSave();
                                        }

                                        @Override
                                        public void onFailed(String error) {
                                            runCallBackError(error);
                                        }
                                    });
                                } catch (Exception ex) {
                                    Timber.e(ex, "Error on update customer call order");
                                    runCallBackError(ex.getMessage());
                                }
                            }
                        } else {
                            editor.putBoolean(callOrderId.toString() + customer.BackOfficeId + "CheckBoxChecked", false);
                            editor.apply();
                            runCallBackAlert(SaveOrderCallbackType.NoSystemPayment, R.string.warning, R.string.system_payment_not_exist, null);
                        }
                    }

                    @Override
                    public void onFailure(final String error) {
                        runCallBackAlert(SaveOrderCallbackType.NetworkError, R.string.warning, error, new IWarningCallBack() {
                            @Override
                            public void onContinue() {
                                if (SysConfigManager.compare(sysConfigModel, "266FB268-8106-40EE-B19F-C00FB79569C4") && !(error.equalsIgnoreCase(getString(R.string.network_access_error))) && !(error.equalsIgnoreCase(getString(R.string.error_connecting_to_server))))
                                    finalSave();
                                else
                                    runCallBackError(R.string.error_saving_request);
                            }

                            @Override
                            public void cancel() {
                                runCallBackCancel();
                            }
                        });
                    }

                    @Override
                    public void onProcess(String msg) {
                        runCallBackProcess(msg);
                    }
                });
            } else {
                finalSave();
            }
        } catch (UnknownBackOfficeException e) {
            runCallBackError(R.string.back_office_type_is_uknown);
        }
    }

    private void finalSave() {
        String error = checkOrderLines();
        if (error == null) {
            try {
                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                    controlReturn();
                else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
                    saveHotSales();
                else if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                    saveDist();
            } catch (Exception ex) {
                Timber.e(ex);
                runCallBackError(R.string.error_saving_request);
            }
        } else
            runCallBackError(error);
    }

    private void controlReturn() {
        UUID customerId = customer.UniqueId;
        final SysConfigManager sysConfigManager = new SysConfigManager(context);
        final SysConfigModel configModel = sysConfigManager.read(ConfigKey.ReturnValidationErrorType, SysConfigManager.cloud);
        if (SysConfigManager.compare(configModel, UUID.fromString("266FB268-8106-40EE-B19F-C00FB79569C4")) || SysConfigManager.compare(configModel, UUID.fromString("337BCEA8-32A9-4A9E-8A70-C5C73944869D"))) {
            final PingApi pingApi = new PingApi();
            pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    runCallBackProcess(R.string.return_control);
                    ReturnControlApi api = new ReturnControlApi(context);
                    ReturnControlHeaderViewModel returnControlHeaderViewModel = new ReturnControlHeaderViewModel();
                    int dealerRef = UserManager.readFromFile(context).BackOfficeId;
                    returnControlHeaderViewModel.OrderDetails = new ArrayList<>();
                    CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(context);
                    List<CustomerCallOrderModel> orders = callOrderManager.getCustomerCallOrders(customerId);
                    FreeReasonManager freeReasonManager = new FreeReasonManager(context);
                    for (CustomerCallOrderModel order :
                            orders) {
                        List<CustomerCallOrderOrderViewModel> lines = new CustomerCallOrderOrderViewManager(context).getLines(order.UniqueId, null);
                        for (CustomerCallOrderOrderViewModel line :
                                lines) {
                            ReturnControlOrderDetailViewModel viewModel = new ReturnControlOrderDetailViewModel();
                            viewModel.CustRef = customer.BackOfficeId;
                            viewModel.DealerRef = dealerRef;
                            viewModel.DisType = 1;
                            viewModel.FreeReasonRef = freeReasonManager.getBackOfficeId(line.FreeReasonId);
                            viewModel.GoodsRef = new ProductManager(context).getBackOfficeId(line.ProductId);
                            viewModel.OrderDate = DateHelper.toString(order.SaleDate, DateFormat.MicrosoftDateTime, Locale.getDefault());
                            viewModel.OrderId = order.UniqueId;
                            CustomerOrderTypeModel customerOrderTypeModel = new CustomerOrderTypesManager(context).getItem(order.OrderTypeUniqueId);
                            if (customerOrderTypeModel != null) {
                                viewModel.OrderTypeRef = customerOrderTypeModel.BackOfficeId;
                            }
                            PaymentTypeOrderModel paymentTypeOrderModel = new PaymentOrderTypeManager(context).getItem(order.OrderPaymentTypeUniqueId);
                            if (customerOrderTypeModel != null) {
                                viewModel.PaymentUsanceRef = Integer.valueOf(paymentTypeOrderModel.BackOfficeId);
                            }
                            SysConfigModel saleOfficeRef = sysConfigManager.read(ConfigKey.SaleOfficeRef, SysConfigManager.cloud);
                            if (saleOfficeRef != null) {
                                viewModel.SaleOfficeRef = Integer.valueOf(saleOfficeRef.Value);
                            }
                            viewModel.TotalQty = line.TotalQty;
                            viewModel.UnitPrice = line.UnitPrice == null ? null : line.UnitPrice.bigDecimalValue();
                            returnControlHeaderViewModel.OrderDetails.add(viewModel);
                        }
                    }
                    CustomerCallReturnLinesViewManager returnLinesViewManager = new CustomerCallReturnLinesViewManager(context);
                    final List<CustomerCallReturnLinesViewModel> returnLines = returnLinesViewManager.getCustomerLines(customerId, null);
                    returnControlHeaderViewModel.ReturnDetails = new ArrayList<>();
                    for (CustomerCallReturnLinesViewModel line :
                            returnLines) {
                        ReturnControlDetailViewModel viewModel = new ReturnControlDetailViewModel();
                        ProductManager productManager = new ProductManager(context);
                        ProductModel productModel = productManager.getItem(line.ProductId);
                        viewModel.CustRef = customer.BackOfficeId;
                        viewModel.DealerRef = dealerRef;
                        viewModel.GoodsRef = productModel.BackOfficeId;
                        viewModel.SaleRef = line.SaleNo;
                        viewModel.TotalQty = line.TotalReturnQty;
                        viewModel.UnitPrice = line.RequestUnitPrice == null ? null : line.RequestUnitPrice.bigDecimalValue();
                        viewModel.ReturnId = line.ReturnUniqueId;
                        viewModel.HeathCode = ReturnType.Well.equals(line.ReturnProductTypeId) ? 1 : 0;
                        viewModel.RetCauseId = line.ReturnReasonId;
                        returnControlHeaderViewModel.ReturnDetails.add(viewModel);
                    }
                    api.runWebRequest(api.returnControl(returnControlHeaderViewModel), new WebCallBack<String>() {
                        @Override
                        protected void onFinish() {

                        }

                        @Override
                        protected void onSuccess(String result, Request request) {
                            Timber.d("Request =" + request.url().toString() + " . Result = " + result);
                            if (result == null || result.isEmpty())
                                finalPresalesSave();
                            else if (SysConfigManager.compare(configModel, UUID.fromString("266FB268-8106-40EE-B19F-C00FB79569C4"))) {
                                runCallBackAlert(SaveOrderCallbackType.ReturnControl, R.string.return_control, result, new IWarningCallBack() {
                                    @Override
                                    public void onContinue() {
                                        finalPresalesSave();
                                    }

                                    @Override
                                    public void cancel() {
                                        runCallBackCancel();
                                    }
                                });
                            } else {
                                runCallBackError(result);
                            }
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            String err = WebApiErrorBody.log(error, context);
                            runCallBackError(err);
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            runCallBackError(R.string.network_error);
                        }
                    });
                }

                @Override
                public void failed() {
                    runCallBackError(R.string.ip_addresses_are_not_available);
                }
            });
        } else
            finalPresalesSave();
    }

    @SubsystemType(id = SubsystemTypeId.HotSales)
    private void saveHotSales() {
        final CallOrderLineManager callOrderLineManager = new CallOrderLineManager(context);
        runCallBackProcess(R.string.calculating_discount);
        CalcPromotion.calcPromotionV3(null, null, context, callOrderId, customerId, EVCType.HOTSALE, true, false, false, new PromotionCallback() {
            @Override
            public void onSuccess(CustomerCallOrderPromotion data) {
                try {
                    runCallBackProcess("");
                    CalcPromotion.fillChoicePrize(context, data, customerId, callOrderId);
                    checkFinalOnHandQty(customerId, callOrderId, data);
                    callOrderLineManager.insertOrUpdatePromoLines(callOrderId, data, customerId);
                    CustomerCallManager callManager = new CustomerCallManager(context);
                    callManager.saveOrderCall(customerId, callOrderId);
                    runCallBackSuccess();
                } catch (PromotionException e) {
                    Timber.e(e);
                    runCallBackError(R.string.error_in_promotion);
                } catch (ValidationException e) {
                    runCallBackError(R.string.error_saving_request);
                } catch (ProductUnitViewManager.UnitNotFoundException e) {
                    runCallBackError(R.string.no_unit_for_product);
                } catch (DbException e) {
                    runCallBackError(R.string.error_saving_request);
                } catch (OnHandQtyError e) {
                    runCallBackError(e.getMessage());
                } catch (Exception e) {
                    runCallBackError(R.string.error_saving_request);
                }
            }

            @Override
            public void onFailure(String error) {
                Timber.e(error);
                runCallBackError(error);
            }

            @Override
            public void onProcess(String msg) {
                runCallBackProcess(msg);
            }
        });
    }

    private void checkFinalOnHandQty(UUID customerId, UUID callOrderId, CustomerCallOrderPromotion customerCallOrderPromotion) throws InventoryError {
        HashMap<String, BigDecimal> products = new HashMap<>();
        List<CallOrderLineModel> lines = new CallOrderLineManager(context).getOrderLines(callOrderId);
        HashMap<UUID, CallOrderLineModel> linesMap = new HashMap<>();
        for (CallOrderLineModel callOrderLine :
                lines) {
            linesMap.put(callOrderLine.UniqueId, callOrderLine);
        }
        if (customerCallOrderPromotion.LinesWithPromo != null && customerCallOrderPromotion.LinesWithPromo.size() > 0) {
            for (CustomerCallOrderLinePromotion customerCallOrderLinePromotion :
                    customerCallOrderPromotion.LinesWithPromo) {
                if (!linesMap.containsKey(customerCallOrderLinePromotion.UniqueId)) {
                    ProductModel product = new ProductManager(context).getProductByBackOfficeId(customerCallOrderLinePromotion.ProductRef);
                    if (product != null) {
                        if (product.OrderPoint == null) product.OrderPoint = BigDecimal.ZERO;
                        OnHandQtyModel onHandQtyModel = new OnHandQtyManager(context).getOnHandQty(product.UniqueId);
                        if (onHandQtyModel != null) {
                            if (onHandQtyModel.OnHandQty == null)
                                onHandQtyModel.OnHandQty = BigDecimal.ZERO;
                            ProductOrderViewModel productOrderViewModel = new ProductOrderViewManager(context).getItem(ProductOrderViewManager.get(product.UniqueId, customerId, callOrderId, false));
                            BigDecimal total = customerCallOrderLinePromotion.TotalRequestQty;
                            if (productOrderViewModel != null)
                                total = total.add((productOrderViewModel.ProductTotalOrderedQty == null ? BigDecimal.ZERO : productOrderViewModel.ProductTotalOrderedQty));
                            if (onHandQtyModel.OnHandQty.subtract(total).compareTo(product.OrderPoint) < 0) {
                                BigDecimal newValue = customerCallOrderLinePromotion.TotalRequestQty;
                                if (products.containsKey(customerCallOrderLinePromotion.ProductCode)) {
                                    BigDecimal previousValue = products.get(customerCallOrderLinePromotion.ProductCode);
                                    newValue = newValue.add(previousValue);
                                }
                                products.put(customerCallOrderLinePromotion.ProductCode, newValue);
                            }
                        } else {
                            BigDecimal newValue = customerCallOrderLinePromotion.TotalRequestQty;
                            if (products.containsKey(customerCallOrderLinePromotion.ProductCode)) {
                                BigDecimal previousValue = products.get(customerCallOrderLinePromotion.ProductCode);
                                newValue = newValue.add(previousValue);
                            }
                            products.put(customerCallOrderLinePromotion.ProductCode, newValue);
                        }
                    } else {
                        BigDecimal newValue = customerCallOrderLinePromotion.TotalRequestQty;
                        if (products.containsKey(customerCallOrderLinePromotion.ProductCode)) {
                            BigDecimal previousValue = products.get(customerCallOrderLinePromotion.ProductCode);
                            newValue = newValue.add(previousValue);
                        }
                        products.put(customerCallOrderLinePromotion.ProductCode, newValue);
                    }
                }
            }
        }
        if (products.size() > 0) {
            StringBuilder err = new StringBuilder(getString(R.string.stock_level_is_not_enough));
            for (Map.Entry<String, BigDecimal> pair : products.entrySet()) {
                err.append("\n").append("کالای جایزه ").append(pair.getKey()).append(" به تعداد ").append(pair.getValue());
            }
            throw new InventoryError(err.toString());
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private UUID getReturnReasonId() {
        if (returnReasonUniqueId == null) {
            CustomerCallManager callManager = new CustomerCallManager(context);
            CustomerCallModel callModel = callManager.loadCall(CustomerCallType.OrderPartiallyDelivered, customerId);
            if (callModel != null)
                returnReasonUniqueId = UUID.fromString(callModel.ExtraField2);
        }
        return returnReasonUniqueId;
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private void saveDist() {
        final DistOrderStatus status = callOrderManager.getDistStatus(customerId, callOrderId);
        if (status.NotDelivered) {
            NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(context);
            List<NoSaleReasonModel> noSalesReasons = noSaleReasonManager.getDistOrderReturnReason();
            if (noSalesReasons.size() < 1)
                runCallBackError(R.string.all_qtys_are_zero);
            else
                saveDistReturn();
        } else
//            if (status.PartiallyDelivered || status.PaymentTypeChanged)
            {
            final CallOrderLineManager callOrderLineManager = new CallOrderLineManager(context);
            runCallBackProcess(R.string.calculating_discount);
            CalcPromotion.calcPromotionV3(null, orderPrizeList, context, callOrderId, customerId, EVCType.TOSELL, true, false, false, new PromotionCallback() {
                @Override
                public void onSuccess(CustomerCallOrderPromotion data) {
                    try {
                        callOrderModel.CashDuration = data.CashDuration;
                        callOrderModel.CheckDuration = data.CheckDuration;
                        callOrderModel.TotalAmountNutCash=data.TotalAmountNutCash;
                        callOrderModel.TotalAmountNutCheque=data.TotalAmountNutCheque;

                        callOrderManager.update(callOrderModel);
                        callOrderLineManager.insertOrUpdatePromoLines(callOrderId, data, customerId);
                        final CustomerCallManager callManager = new CustomerCallManager(context);
                        if (status.PartiallyDelivered)
                            callManager.saveDistPartialDeliveryCall(customerId, callOrderId, getReturnReasonId());
                        else
                            callManager.saveDistPartialDeliveryCall(customerId, callOrderId, NoSaleReasonManager.PAYMENT_TYPE_CHANGED_ID);
                        runCallBackSuccess();
                    } catch (Exception e) {
                        Timber.e(e);
                        runCallBackError(R.string.error_saving_request);
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (error.equals("3001")) {
                        runCallBackAlert(SaveOrderCallbackType.SelectPrize, R.string.error, error, new IWarningCallBack() {
                            @Override
                            public void onContinue() {
                                saveDist();
                            }

                            @Override
                            public void cancel() {
                                runCallBackCancel();
                            }
                        });
                    } else {
                        runCallBackError(error);
                    }
                }

                @Override
                public void onProcess(String msg) {
                    runCallBackProcess(msg);
                }
            });
        }
//            else {
//            try {
//                new CustomerCallManager(context).removeCalls(customerId, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.CompleteReturnDelivery);
//                new CustomerCallOrderManager(context).initCall(callOrderId, false);
//                new CustomerCallManager(context).saveDistDeliveryCall(customerId, callOrderId);
//                runCallBackSuccess();
//            } catch (Exception e) {
//                runCallBackError(R.string.error_saving_request);
//            }
//        }
    }




    @SubsystemType(id = SubsystemTypeId.Dist)
    private void saveDistReturn() {
        runCallBackAlert(SaveOrderCallbackType.SelectReturnReason, R.string.error, R.string.choose_return_reason, new IWarningCallBack() {
            @Override
            public void onContinue() {
                try {
                    new CustomerCallManager(context).saveDistReturnCall(customerId, callOrderId, getReturnReasonId());
                    runCallBackSuccess();
                } catch (Exception e) {
                    Timber.e(e);
                    runCallBackError(R.string.error_saving_request);
                }
            }

            @Override
            public void cancel() {
                runCallBackCancel();
            }
        });
    }

    @SubsystemType(id = SubsystemTypeId.PreSales)
    private void finalPresalesSave() {
        CustomerCallManager callManager = new CustomerCallManager(context);
        try {
            callManager.saveOrderCall(customerId, callOrderId);
            runCallBackSuccess();
        } catch (Exception ex) {
            runCallBackError(R.string.error_saving_request);
        }

    }

    private String checkOrderLines() {
        CustomerCallOrderOrderViewManager orderOrderViewManager = new CustomerCallOrderOrderViewManager(context);
        List<CustomerCallOrderOrderViewModel> lines = orderOrderViewManager.getLines(callOrderId, null);
        List<String> products = new ArrayList<>();
        for (CustomerCallOrderOrderViewModel line :
                lines) {
            if (!line.IsPromoLine && !line.IsFreeItem && (line.RequestAmount == null || line.RequestAmount.compareTo(Currency.ZERO) == 0)) {
                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && line.TotalQty != null && line.TotalQty.compareTo(BigDecimal.ZERO) > 0)
                    products.add(line.ProductName + "(" + line.ProductCode + ")");
                else if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                    products.add(line.ProductName + "(" + line.ProductCode + ")");
            }
        }
        StringBuilder error = new StringBuilder(getString(R.string.following_products_do_not_have_price));
        error.append("\n");
        if (products.size() > 0) {
            for (String p :
                    products) {
                error.append(p).append("\n");
            }
            return error.toString();
        } else
            return null;

    }
}
