package com.varanegar.vaslibrary.action.confirm;

import android.content.Context;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.action.ActionData;
import com.varanegar.vaslibrary.action.IActionUtility;
import com.varanegar.vaslibrary.action.IActionUtilityCallBack;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.FreeReasonManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlApi;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlDetailViewModel;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlHeaderViewModel;
import com.varanegar.vaslibrary.webapi.returncontrol.ReturnControlOrderDetailViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

public class ControlReturnUtility implements IActionUtility {
    @Override
    public void run(ActionData data, IActionUtilityCallBack callBack) {
        Context context = data.getContext();
        final SysConfigModel configModel = data.getCloudConfig(ConfigKey.ReturnValidationErrorType);
        if ((!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) && (SysConfigManager.compare(configModel, UUID.fromString("266FB268-8106-40EE-B19F-C00FB79569C4")) || SysConfigManager.compare(configModel, UUID.fromString("337BCEA8-32A9-4A9E-8A70-C5C73944869D")))) {
            final PingApi pingApi = new PingApi();
            pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    try {

                        ReturnControlHeaderViewModel returnControlHeaderViewModel = new ReturnControlHeaderViewModel();
                        SysConfigManager sysConfigManager = new SysConfigManager(context);
                        int dealerRef = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.DealerRef, SysConfigManager.cloud), -1);
                        returnControlHeaderViewModel.OrderDetails = new ArrayList<>();
                        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(context);
                        List<CustomerCallOrderModel> orders = callOrderManager.getCustomerCallOrders(data.getCustomer().UniqueId);
                        FreeReasonManager freeReasonManager = new FreeReasonManager(context);
                        for (CustomerCallOrderModel order :
                                orders) {
                            List<CustomerCallOrderOrderViewModel> lines = new CustomerCallOrderOrderViewManager(context).getLines(order.UniqueId, null);
                            for (CustomerCallOrderOrderViewModel line :
                                    lines) {
                                ReturnControlOrderDetailViewModel viewModel = new ReturnControlOrderDetailViewModel();
                                viewModel.CustRef = data.getCustomer().BackOfficeId;
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
                                SysConfigModel saleOfficeRef = data.getCloudConfig(ConfigKey.SaleOfficeRef);
                                if (saleOfficeRef != null) {
                                    viewModel.SaleOfficeRef = Integer.valueOf(saleOfficeRef.Value);
                                }
                                viewModel.TotalQty = line.TotalQty;
                                viewModel.UnitPrice = line.UnitPrice == null ? null : line.UnitPrice.bigDecimalValue();
                                returnControlHeaderViewModel.OrderDetails.add(viewModel);
                            }

                        }

                        CustomerCallReturnLinesViewManager returnLinesViewManager = new CustomerCallReturnLinesViewManager(context);
                        final List<CustomerCallReturnLinesViewModel> returnLines = returnLinesViewManager.getCustomerLines(data.getCustomer().UniqueId, null);
                        returnControlHeaderViewModel.ReturnDetails = new ArrayList<>();
                        for (CustomerCallReturnLinesViewModel line :
                                returnLines) {
                            ReturnControlDetailViewModel viewModel = new ReturnControlDetailViewModel();
                            ProductManager productManager = new ProductManager(context);
                            ProductModel productModel = productManager.getItem(line.ProductId);
                            viewModel.CustRef = data.getCustomer().BackOfficeId;
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


                        List<CustomerCallOrderModel> allOrders = callOrderManager.getAllOrders();
                        returnControlHeaderViewModel.TotalOrderDetails = new ArrayList<>();
                        for (CustomerCallOrderModel order :
                                allOrders) {
                            CustomerModel customerModel = new CustomerManager(context).getItem(order.CustomerUniqueId);
                            List<CustomerCallOrderOrderViewModel> lines = new CustomerCallOrderOrderViewManager(context).getLines(order.UniqueId, null);
                            for (CustomerCallOrderOrderViewModel line :
                                    lines) {
                                ReturnControlOrderDetailViewModel viewModel = new ReturnControlOrderDetailViewModel();
                                viewModel.CustRef = customerModel.BackOfficeId;
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
                                SysConfigModel saleOfficeRef = data.getCloudConfig(ConfigKey.SaleOfficeRef);
                                if (saleOfficeRef != null) {
                                    viewModel.SaleOfficeRef = Integer.valueOf(saleOfficeRef.Value);
                                }
                                viewModel.TotalQty = line.TotalQty;
                                viewModel.UnitPrice = line.UnitPrice == null ? null : line.UnitPrice.bigDecimalValue();
                                returnControlHeaderViewModel.TotalOrderDetails.add(viewModel);
                            }

                        }

                        final List<CustomerCallReturnLinesViewModel> allReturnLines = returnLinesViewManager.getReturnLines();
                        returnControlHeaderViewModel.TotalReturnDetails = new ArrayList<>();
                        for (CustomerCallReturnLinesViewModel line :
                                allReturnLines) {
                            CustomerModel customerModel = new CustomerManager(context).getItem(line.CustomerUniqueId);
                            ReturnControlDetailViewModel viewModel = new ReturnControlDetailViewModel();
                            ProductManager productManager = new ProductManager(context);
                            ProductModel productModel = productManager.getItem(line.ProductId);
                            viewModel.CustRef = customerModel.BackOfficeId;
                            viewModel.DealerRef = dealerRef;
                            viewModel.GoodsRef = productModel.BackOfficeId;
                            viewModel.SaleRef = line.SaleNo;
                            viewModel.TotalQty = line.TotalReturnQty;
                            viewModel.UnitPrice = line.RequestUnitPrice == null ? null : line.RequestUnitPrice.bigDecimalValue();
                            viewModel.ReturnId = line.ReturnUniqueId;
                            viewModel.HeathCode = ReturnType.Well.equals(line.ReturnProductTypeId) ? 1 : 0;
                            viewModel.RetCauseId = line.ReturnReasonId;
                            returnControlHeaderViewModel.TotalReturnDetails.add(viewModel);
                        }


                        ReturnControlApi api = new ReturnControlApi(context);
                        api.runWebRequest(api.returnControl(returnControlHeaderViewModel), new WebCallBack<String>() {
                            @Override
                            protected void onFinish() {

                            }

                            @Override
                            protected void onSuccess(String result, Request request) {
                                Timber.d("Request =" + request.url().toString() + " . Result = " + result);
                                if (result == null || result.isEmpty())
                                    callBack.onDone();
                                else if (SysConfigManager.compare(configModel, UUID.fromString("266FB268-8106-40EE-B19F-C00FB79569C4"))) {
                                    CuteMessageDialog warning = new CuteMessageDialog(context);
                                    warning.setIcon(Icon.Warning);
                                    warning.setTitle(R.string.return_control);
                                    warning.setMessage(result);
                                    warning.setPositiveButton(R.string.ok, view -> callBack.onDone());
                                    warning.setNegativeButton(R.string.cancel, view -> callBack.onCancel());
                                    warning.show();
                                } else {
                                    callBack.onFailed(result);
                                }
                            }

                            @Override
                            protected void onApiFailure(ApiError error, Request request) {
                                String err = WebApiErrorBody.log(error, context);
                                callBack.onFailed(err);
                            }

                            @Override
                            protected void onNetworkFailure(Throwable t, Request request) {
                                callBack.onFailed(context.getString(R.string.network_error));
                            }
                        });
                    } catch (Exception ex) {
                        Timber.e(ex);
                        callBack.onFailed(context.getString(R.string.error_saving_request));
                    }
                }

                @Override
                public void failed() {
                    callBack.onFailed(context.getString(R.string.ip_addresses_are_not_available));
                }
            });
        } else
            callBack.onDone();
    }
}
