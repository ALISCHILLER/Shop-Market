package com.varanegar.vaslibrary.promotion;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.exception.DistException;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerCallReturnLinesViewManager;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.OrderAmount;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.customerpricemanager.CustomerPriceManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountItemCountViewManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceHeaderManager;
import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
import com.varanegar.vaslibrary.manager.orderprizeview.OrderPrizeViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercallreturnlinesview.CustomerCallReturnLinesViewModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountViewModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
import com.varanegar.vaslibrary.model.orderprizeview.OrderPrizeViewModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandler;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandlerV3;
import com.varanegar.vaslibrary.promotion.nestle.NestlePromotionCalculator;
import com.varanegar.vaslibrary.promotion.nestle.NestlePromotionModel;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.DiscountCalculatorHandler;
import varanegar.com.discountcalculatorlib.callback.DiscountHandlerOrderCallback;
import varanegar.com.discountcalculatorlib.handler.PromotionHandlerV3;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.utility.enumerations.OrderTypeEnum;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallOrderLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountEvcPrizeData;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;
import varanegar.com.vdmclient.VdmInitializer;

import static varanegar.com.discountcalculatorlib.Global.hasChoicePrize;
import static varanegar.com.discountcalculatorlib.Global.salesError;
import static varanegar.com.discountcalculatorlib.Global.totallyError;


/**
 * محاسبات تخفیف جوایز
 */
public class CalcPromotion {

    public static void calcReturnPromotion(final Context context, @NonNull final UUID customerUniqueId, boolean withRef, boolean isFromRequest, final PromotionCallback callback) {
        try {
            PingApi pingApi = new PingApi();
            pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    DiscountInitializeHandler disc = DiscountInitializeHandlerV3.getDiscountHandler(context);
                    final CustomerCallOrderPromotion callData = fillCustomerCallOrderPromotion(context, customerUniqueId, withRef, isFromRequest);
                    DiscountCalculatorHandler.init(disc, 0, null);
                    DiscountCalculatorHandler.setOnlineOptions(ipAddress, true, false, false);
                    try {
                        DiscountCallOrderData disCallData;
                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                            disCallData = PromotionHandlerV3.distCalcPromotionOnlineSDS(null, callData.toDiscount(context), context,
                                    null,null,null);
                        else
                            disCallData = PromotionHandlerV3.calcPromotionOnlineSDS(null, callData.toDiscount(context), context);

                        Timber.d("finished!");
                        callData.setFromDiscount(context, disCallData);
                        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                            fillOrderPrize(context, callData, callData.discountEvcPrize);
                        fillOrderFreeItem(callData);
                        callback.onSuccess(callData);
                    } catch (Exception e) {
                        Timber.e(e);
                        callback.onFailure(e.getMessage());
                    }
                }

                @Override
                public void failed() {
                    callback.onFailure(context.getResources().getString(R.string.error_connecting_to_server));
                }
            });
        } catch (final DiscountException ex) {
            callback.onFailure(ex.getMessage());
        }
    }

    public static void calcPromotionV3WithDialog(final List<Integer> SelIds, final List<DiscountOrderPrizeViewModel> orderPrizeList,
                                                 final Activity activity,
                                                 @NonNull final UUID callOrderUniqueId,
                                                 @NonNull final UUID customerUniqueId,
                                                 final EVCType evcType,
                                                 final boolean CalcDiscount,
                                                 final boolean CalcSaleRestriction,
                                                 final boolean CalcPaymentType,
                                                 final PromotionCallback callback

    ) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        calcPromotionV3(SelIds, orderPrizeList, activity, callOrderUniqueId, customerUniqueId, evcType, CalcDiscount,
                CalcSaleRestriction, CalcPaymentType, new PromotionCallback() {
            @Override
            public void onSuccess(CustomerCallOrderPromotion data) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(String error) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (error == null || error.isEmpty())
                    callback.onFailure(activity.getString(R.string.error_calculating_discount));
                else
                    callback.onFailure(error);
            }

            @Override
            public void onProcess(String msg) {
                progressDialog.setMessage(msg);
                callback.onProcess(msg);
            }
        });
    }

    public static void calcPromotionV3(final List<Integer> SelIds, final List<DiscountOrderPrizeViewModel> orderPrizeList,
                                       final Context context,
                                       @NonNull final UUID callOrderUniqueId,
                                       @NonNull final UUID customerUniqueId,
                                       final EVCType evcType,
                                       final boolean CalcDiscount,
                                       final boolean CalcSaleRestriction,
                                       final boolean CalcPaymentType,
                                       final PromotionCallback callback

    ) {
        final String[] message = {null};
        if (CalcSaleRestriction && CalcPaymentType)
            message[0] = context.getResources().getString(R.string.calculating_usance_day_and_sale_controling);
        else if (CalcDiscount)
            message[0] = context.getResources().getString(R.string.calculating_discount);
        else if (CalcSaleRestriction)
            message[0] = context.getResources().getString(R.string.sale_controling);
        else if (CalcPaymentType)
            message[0] = context.getResources().getString(R.string.calculating_usance_day);
        callback.onProcess(message[0]);
        final Handler handler = new Handler(Looper.getMainLooper());
        salesError = "";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final CustomerCallOrderPromotion callData = fillCustomerCallOrderPromotion(context, callOrderUniqueId, customerUniqueId);
                    SysConfigManager sysConfigManager = new SysConfigManager(context);
                    BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
//                    if (backOfficeType == BackOfficeType.ThirdParty) {
//                        calcThirdParty(callData);
//                    } else {
                    DiscountInitializeHandler disc = DiscountInitializeHandlerV3.getDiscountHandler(context);
                    if (!GlobalVariables.isCalcOnline())
                        DiscountCalculatorHandler.init(disc, 0, null);
                    SysConfigModel newDiscountConfig = sysConfigManager.read(ConfigKey.NewDiscount, SysConfigManager.cloud);
                    if (SysConfigManager.compare(newDiscountConfig, true)) {
                        DiscountCalculatorHandler.fillInitData(disc, new VdmInitializer.InitCallback() {
                            @Override
                            public void onSuccess(String s) {
                                Timber.d("Calc promotion started");
                                DiscountCalculatorHandler.calcPromotion(callData.toDiscount(context), evcType.value(), new DiscountHandlerOrderCallback() {
                                    @Override
                                    public void onSuccess(DiscountCallOrderData discountCallOrderData) {
                                        Timber.d("calc promotion finished!");
                                        callData.setFromDiscount(context, discountCallOrderData);
                                        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                                            fillOrderPrize(context, callData, callData.discountEvcPrize);
                                        fillOrderFreeItem(callData);
                                        handler.post(() -> callback.onSuccess(callData));
                                    }

                                    @Override
                                    public void onFailure(final String s) {
                                        handler.post(() -> callback.onFailure(s));
                                    }
                                });
                            }

                            @Override
                            public void onFailure(final String s) {
                                handler.post(() -> callback.onFailure(s));
                            }
                        });
                    } else {
                        if (!GlobalVariables.isCalcOnline())
                            DiscountCalculatorHandler.fillInitData(disc);
                        Timber.d("Calc promotion started");
                        SysConfigModel calcOnline = sysConfigManager.read(ConfigKey.OnliveEvc, SysConfigManager.cloud);
                        if (SysConfigManager.compare(calcOnline, true)) {
                            PingApi pingApi = new PingApi();
                            pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                                @Override
                                public void done(String ipAddress) {
//                                        runCalcPromotion(ipAddress, CalcDiscount, CalcSaleRestriction, CalcPaymentType, callData, context, evcType, callback);
                                    try {
                                        DiscountCalculatorHandler.setOnlineOptions(ipAddress, CalcDiscount, CalcSaleRestriction, CalcPaymentType);
                                        DiscountCallOrderData disCallData = null;
                                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                                            ArrayList<DiscountCallOrderLineData> orderProduct = populateOriginalData(context, callOrderUniqueId);
                                            List<CustomerCallInvoiceModel> callInvoiceModel= new CustomerCallInvoiceManager(context)
                                                    .getCustomerCallInvoices(callData.BackOfficeOrderId);
                                            distDiscountCalc(orderPrizeList, context, callData, disCallData, evcType, orderProduct,  callInvoiceModel.get(0));
                                            handler.post(() -> callback.onSuccess(callData));
//                                                disCallData = DiscountCalculatorHandler.calcPromotion(callData.toDiscount(context), evcType.value(), context);
                                            //                                                disCallData = DiscountCalculatorHandler.calcPromotion(callData.toDiscount(context), evcType.value(), context);
                                        } else {
                                            disCallData = DiscountCalculatorHandler.calcPromotion(SelIds, callData.toDiscount(context), evcType.value(), context);
                                            Timber.d("finished!");
                                            callData.setFromDiscount(context, disCallData);
                                            if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                                                fillOrderPrize(context, callData, callData.discountEvcPrize);
                                            fillOrderFreeItem(callData);
                                            handler.post(() -> callback.onSuccess(callData));
                                        }
                                    } catch (DiscountException e) {
                                        Timber.e("Exception for customerId " + callData.CustomerId + " " + e);
                                        salesError = e.getMessage();
                                        throw e;
                                    } catch (final Exception ex) {
                                        Timber.e("Exception for customerId " + callData.CustomerId + " " + ex);
                                        handler.post(() -> callback.onFailure(ex.getMessage()));
                                    }
                                }

                                @Override
                                public void failed() {
                                    if (hasChoicePrize) {
                                        handler.post(() -> callback.onFailure("3001"));
                                    } else if (!salesError.equals("")) {
                                        handler.post(() -> callback.onFailure(salesError));
                                    } else if (!totallyError.equals("")) {
                                        Timber.e("Error connection to the server");
                                        handler.post(() -> callback.onFailure(totallyError));
                                    } else if (!salesError.equals("")) {
                                        handler.post(() -> callback.onFailure(salesError));
                                    } else {
                                        handler.post(() -> callback.onFailure(context.getResources().getString(R.string.error_connecting_to_server)));
                                    }
                                }
                            });
                        } else {
                            DiscountCallOrderData disCallData = null;
                            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                                ArrayList<DiscountCallOrderLineData> orderProduct = populateOriginalData(context, callOrderUniqueId);
                                distDiscountCalc(null, context, callData, disCallData, evcType, orderProduct,null);
                                handler.post(() -> callback.onSuccess(callData));
                            } else {
                                disCallData = DiscountCalculatorHandler.calcPromotion(SelIds, callData.toDiscount(context), evcType.value(), context);
                                Timber.d("finished!");
                                callData.setFromDiscount(context, disCallData);
                                fillOrderPrize(context, callData, callData.discountEvcPrize);
                                fillOrderFreeItem(callData);
                                handler.post(() -> callback.onSuccess(callData));
                            }
                        }
                    }
//                    }

                } catch (final DiscountException ex) {
                    Timber.e(ex);
                    handler.post(() -> callback.onFailure(ex.getMessage()));
                } catch (final UnknownBackOfficeException e) {
                    Timber.e(e);
                    handler.post(() -> callback.onFailure(e.getMessage()));
                } catch (final InterruptedException e) {
                    handler.post(() -> callback.onFailure(e.getMessage()));
                } catch (final Exception e) {
                    Timber.e(e);
                    handler.post(() -> callback.onFailure(e.getMessage()));
                }
            }
        });
        thread.start();
    }

    private static int loopCount = 0;
    private static boolean last = false;
    private static List<DiscountPrizeViewModel> discountPrizeViewModels = new ArrayList<>();

    /**
     * تخفیف جوایز توزیع
     * @param orderPrizeList
     * @param context
     * @param callData
     * @param disCallData
     * @param evcType
     * @param orderProduct
     * @throws DiscountException
     * @throws InterruptedException
     */
    private static void distDiscountCalc(List<DiscountOrderPrizeViewModel> orderPrizeList,
                                         Context context, CustomerCallOrderPromotion callData,
                                         DiscountCallOrderData disCallData,
                                         EVCType evcType,
                                         ArrayList<DiscountCallOrderLineData> orderProduct,
                                         CustomerCallInvoiceModel callInvoiceModel
    ) throws DiscountException, InterruptedException {
        //                                                if (true) {
        try {
            String DocPDate=callInvoiceModel.DocPDate;
            String SalePDate=callInvoiceModel.SalePDate;
            String ZTERM=callInvoiceModel.zterm;

            if (GlobalVariables.isCalcOnline())
                disCallData = PromotionHandlerV3.distCalcPromotionOnlineSDS(orderPrizeList, callData.toDiscount(context), context,SalePDate,DocPDate,ZTERM);
            else {
                DiscountInitializeHandler disc = DiscountInitializeHandlerV3.getDiscountHandler(context);
                //DiscountCalculatorHandler.init(disc, callData.BackOfficeOrderId, null);
                DiscountCalculatorHandler.fillInitDataIfEmpty(disc);
                disCallData = DiscountCalculatorHandler.DoCalcEVC(callData.toDiscount(context), evcType.value());
                callData.evcId = disCallData.evcRef;
                disCallData = DiscountCalculatorHandler.DoReCalcEVC(callData.toDiscount(context), String.valueOf(callData.evcId), evcType.value());
            }

            if (disCallData.callLineItemDataWithPromo != null) {
                HashMap<UUID, BigDecimal> linesMap = new HashMap<>();
                HashMap<Integer, BigDecimal> promoLinesMap = new HashMap<>();
                for (DiscountCallOrderLineData callOrderLine :
                        orderProduct) {
                    if (callOrderLine.isRequestPrizeItem)
                        promoLinesMap.put(callOrderLine.productId, callOrderLine.invoiceTotalQty);
                    else
                        linesMap.put(callOrderLine.orderLineId, callOrderLine.invoiceTotalQty);
                }
                ArrayList<String> errors = new ArrayList<>();
                for (DiscountCallOrderLineData item : disCallData.callLineItemDataWithPromo) {
                    boolean exits = false;
                    String productCode = item.productCode;
                    if (productCode == null) {
                        ProductManager productManager = new ProductManager(context);
                        ProductModel productModel = productManager.getProductByBackOfficeId(item.productId);
                        productCode = productModel.ProductCode;
                    }
                    if (linesMap.containsKey(item.orderLineId)) {
                        exits = true;
                        if (item.invoiceTotalQty.compareTo(linesMap.get(item.orderLineId)) > 0)
                            errors.add(context.getString(R.string.product_code_label) + productCode + " - "
                                    + context.getString(R.string.current_count) + linesMap.get(item.orderLineId) + " - "
                                    + context.getString(R.string.law_count) + item.disRef + ": " + item.invoiceTotalQty);
                    } else if (promoLinesMap.containsKey(item.productId)) {
                        exits = true;
                        if (item.invoiceTotalQty.compareTo(promoLinesMap.get(item.productId)) > 0)
                            errors.add(context.getString(R.string.prize_product_code) + productCode + " - "
                                    + context.getString(R.string.current_count) + promoLinesMap.get(item.productId) + " - "
                                    + context.getString(R.string.law_count) + item.disRef + ": " + item.invoiceTotalQty);
                    }
                    if (!exits)
                        errors.add(context.getString(R.string.prize_product_code) + productCode + " - "
                                + context.getString(R.string.current_count) + "0" + " - "
                                + context.getString(R.string.law_count) + item.disRef + ": " + item.invoiceTotalQty);
                }
                if (errors.size() > 0) {
                    String errorMsg = context.getString(R.string.has_discount_line_more_than_order);
                    for (String err :
                            errors) {
                        errorMsg += "\n" + err;
                    }
                    totallyError = errorMsg;
                    throw new DiscountException(errorMsg);
                }
            }

            callData.setFromDiscount(context, disCallData);
        } catch (DiscountException e) {
            throw new DiscountException(e.getMessage());
        }
//                                                }

    }

//    private static void runCalcPromotion(String ipAddress,
//                                         boolean CalcDiscount,
//                                         boolean CalcSaleRestriction,
//                                         boolean CalcPaymentType,
//                                         CustomerCallOrderPromotion callData,
//                                         Activity context,
//                                         EVCType evcType,
//                                         PromotionCallback callback) {
//        try {
//            DiscountCalculatorHandler.setOnlineOptions(ipAddress, CalcDiscount, CalcSaleRestriction, CalcPaymentType);
//            DiscountCallOrderData disCallData = DiscountCalculatorHandler.calcPromotion(callData.toDiscount(context), evcType.value(), context);
//            Timber.d("finished!");
//            callData.setFromDiscount(context, disCallData);
//            fillOrderPrize(context, callData);
//            fillOrderFreeItem(callData);
//            callback.onSuccess(callData);
//        } catch (Exception ex) {
//            callback.onFailure(ex.getMessage());
//        }
//    }

    private static CustomerCallOrderPromotion calcThirdParty(CustomerCallOrderPromotion callData) {

        ArrayList<CustomerCallOrderLinePromotion> withPromotList = new ArrayList<>();
        List<NestlePromotionModel> promotionList = new NestlePromotionCalculator().getPromotions(callData.CustomerId, callData.CustomerRef);
        callData.TotalInvoiceDis1 = Currency.ZERO;
        callData.TotalInvoiceAdd = Currency.ZERO;
        callData.TotalPriceWithPromo = Currency.ZERO;
        callData.TotalInvoiceDis1 = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceDis2 = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceDis3 = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceDisOther = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceAdd1 = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceAdd2 = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceAddOther = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceTax = new Currency(BigDecimal.ZERO);
        callData.TotalInvoiceCharge = new Currency(BigDecimal.ZERO);
        callData.TotalPriceWithPromo = new Currency(BigDecimal.ZERO);
        for (CustomerCallOrderLinePromotion item : callData.Lines) {
            for (NestlePromotionModel promotionItem : promotionList) {
                if (promotionItem.ProductUniqueId.equals(item.ProductId)) {
                    item.InvoiceDis1 = promotionItem.Dis1.setScale(0, BigDecimal.ROUND_HALF_DOWN);
                    callData.TotalInvoiceDis1 = callData.TotalInvoiceDis1.add(promotionItem.Dis1);
                    callData.TotalPriceWithPromo = callData.TotalPriceWithPromo.add(item.UnitPrice.multiply(item.TotalRequestQty));
                    break;
                }
            }
            withPromotList.add(item);
        }
        callData.TotalInvoiceDiscount = callData.TotalInvoiceDis1.setScale(0, BigDecimal.ROUND_HALF_DOWN);
        callData.TotalPriceWithPromo = callData.TotalPriceWithPromo.setScale(0, BigDecimal.ROUND_HALF_DOWN);
        if (callData.TotalPriceWithPromo.compareTo(new Currency(BigDecimal.ZERO)) > 0)
            callData.TotalInvoiceAdd = (callData.TotalPriceWithPromo.multiply(new Currency(9))).divide(new Currency(100)).setScale(0, BigDecimal.ROUND_HALF_DOWN);
        callData.LinesWithPromo = withPromotList;
        return callData;
    }

    private static void fillOrderFreeItem(CustomerCallOrderPromotion callData) {
        for (CustomerCallOrderLinePromotion line : callData.Lines) {
            if (line.IsRequestFreeItem) {
                callData.LinesWithPromo.add(line);
            }
        }
    }

    private static void fillOrderPrize(Context context, CustomerCallOrderPromotion callData, ArrayList<DiscountEvcPrizeData> discountEvcPrize) {
        OrderPrizeViewManager orderPrizeViewManager = new OrderPrizeViewManager(context);
        List<OrderPrizeModel> orderPrizeModels = new ArrayList<>();
        if (discountEvcPrize != null) {
            for (DiscountEvcPrizeData line : discountEvcPrize) {
                if (line.DiscountRef != 0) {
                    DiscountItemCountViewManager discountItemCountViewManager = new DiscountItemCountViewManager(context);
                    List<DiscountItemCountViewModel> discountItemCountViewModels = discountItemCountViewManager.getItems(DiscountItemCountViewManager.getAllDiscountItems(line.DiscountRef));
                    if (discountItemCountViewModels.size() > 0) { // selected promotion

                        //Convert to small unit
                        //ProductUnitViewModel selectunit = new ProductUnitViewManager(context).getSmallUnit(line.ProductId);
                        //line.UnitName = selectunit.UnitName;
                        //line.ConvertFactory = 1;

                        List<OrderPrizeViewModel> orderPrizes = orderPrizeViewManager.getItems(OrderPrizeViewManager.getCustomerOrderPrizes(callData.CustomerId, line.DiscountRef, callData.UniqueId));
                        if (orderPrizes.size() == 0) { // it is not selected before
                            for (DiscountItemCountViewModel discountItemCountViewModel :
                                    discountItemCountViewModels) {
                                OrderPrizeViewModel orderPrizeViewModel = new OrderPrizeViewModel();
                                orderPrizeViewModel.UniqueId = UUID.randomUUID();
                                orderPrizeViewModel.CustomerId = callData.CustomerId;
                                orderPrizeViewModel.DiscountId = line.DiscountId;
                                orderPrizeViewModel.DisRef = line.DiscountRef;
                                orderPrizeViewModel.GoodsRef = discountItemCountViewModel.GoodsRef;
                                orderPrizeViewModel.ProductName = discountItemCountViewModel.ProductName;
                                orderPrizeViewModel.ProductCode = discountItemCountViewModel.ProductCode;
                                if (discountItemCountViewModel.GoodsRef == line.GoodRef)
                                    orderPrizeViewModel.TotalQty = line.Qty;
                                else
                                    orderPrizeViewModel.TotalQty = BigDecimal.ZERO;
                                orderPrizeViewModel.ProductId = discountItemCountViewModel.ProductId;
                                orderPrizeViewModel.CallOrderId = callData.UniqueId;
                                orderPrizeModels.add(orderPrizeViewModel.getOrderPrize());
                            }
                        }
                    }
                }
            }
        }

        final OrderPrizeManager orderPrizeManager = new OrderPrizeManager(context);
        try {
            if (orderPrizeModels.size() > 0)
                orderPrizeManager.insertOrUpdate(orderPrizeModels);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static List<DiscountCallReturnLineData> calcReturnPromotionV3(Context context, CustomerCallReturnModel customerCallReturnModel, EVCType evcType) throws DistException {
        try {
            DiscountInitializeHandler disc = DiscountInitializeHandlerV3.getDiscountHandler(context);

            DiscountCalculatorHandler.init(disc, 0, null);
            DiscountCalculatorHandler.fillInitData(disc);
            DiscountCallReturnData disRet = fillCustomerCallReturnPromotion(context, customerCallReturnModel);
            List<DiscountCallReturnLineData> disCalllineData = DiscountCalculatorHandler.calcNewPromotion(disRet, evcType.value());
            return disCalllineData;
        } catch (Exception e) {
            throw new DistException(e.getMessage());
        }

    }

    private static CustomerCallOrderPromotion fillCustomerCallOrderPromotion(@NonNull Context context, @NonNull UUID customerUniqueId, boolean withRef, boolean isFromRequest) {
        CustomerCallOrderPromotion customerCallOrderPromotion = new CustomerCallOrderPromotion();
        List<CustomerCallReturnLinesViewModel> lines = new CustomerCallReturnLinesViewManager(context).getCustomerLines(customerUniqueId, null, withRef);
        CustomerModel customerModel = new CustomerManager(context).getItem(customerUniqueId);
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(context);
        customerCallOrderPromotion.UniqueId = UUID.randomUUID();
        // customerCallOrderPromotion.AccYearSDS = customerCallOrderModel.AccYearSDS;
        // customerCallOrderPromotion.SaleDate = customerCallOrderModel.SaleDate;
        // customerCallOrderPromotion.TotalPrice = customerCallOrderOrderViewManager.calculateTotalAmount(callOrderUniqueId).TotalAmount;
        customerCallOrderPromotion.CustomerId = customerModel.UniqueId;
        customerCallOrderPromotion.CustomerRef = customerModel.BackOfficeId;
        customerCallOrderPromotion.CustomerCode = customerModel.CustomerCode;
        customerCallOrderPromotion.CustomerName = customerModel.CustomerName;
        customerCallOrderPromotion.RemainCredit = customerModel.RemainCredit;
        customerCallOrderPromotion.RemainDebit = customerModel.RemainDebit;
        // customerCallOrderPromotion.Comment = customerCallOrderModel.Comment;
        // customerCallOrderPromotion.BackOfficeOrderId = customerCallOrderModel.BackOfficeOrderId;
        // customerCallOrderPromotion.BackOfficeInvoiceId = customerCallOrderModel.BackOfficeInvoiceId;
        // customerCallOrderPromotion.SaleOfficeRefSDS = customerCallOrderModel.SaleOfficeRefSDS;
        // customerCallOrderPromotion.DcRefSDS = customerCallOrderModel.DCRefSDS;
        // customerCallOrderPromotion.DealerRefSDS = customerCallOrderModel.DealerRefSDS;
        // customerCallOrderPromotion.DealerCodeSDS = customerCallOrderModel.DealerCodeSDS;

//        if (customerCallOrderModel.OrderPaymentTypeUniqueId != null) {
//            PaymentTypeOrderModel paymentTypeOrderModel = new PaymentOrderTypeManager(context).getItem(customerCallOrderModel.OrderPaymentTypeUniqueId);
//            customerCallOrderPromotion.OrderPaymentTypeId = customerCallOrderModel.OrderPaymentTypeUniqueId;
//            customerCallOrderPromotion.OrderPaymentTypeRef = paymentTypeOrderModel == null ? "0" : paymentTypeOrderModel.BackOfficeId;
//            customerCallOrderPromotion.OrderPaymentTypeName = paymentTypeOrderModel == null ? "" : paymentTypeOrderModel.PaymentTypeOrderName;
//        }

//        if (customerCallOrderModel.OrderTypeUniqueId != null) {
//            CustomerOrderTypeModel customerOrderTypeModel = new CustomerOrderTypesManager(context).getItem(customerCallOrderModel.OrderTypeUniqueId);
//            customerCallOrderPromotion.OrderTypeId = customerCallOrderModel.OrderTypeUniqueId;
//            customerCallOrderPromotion.OrderTypeRef = customerOrderTypeModel.BackOfficeId;
//            customerCallOrderPromotion.OrderTypeName = customerOrderTypeModel.OrderTypeName;
//        }
//        List<CustomerCallOrderOrderViewModel> lines = customerCallOrderOrderViewManager.getLines(callOrderUniqueId, null);
        ArrayList<CustomerCallOrderLinePromotion> promotionLines = new ArrayList<>();

        CustomerCallReturnManager customerCallReturnManager = new CustomerCallReturnManager(context);
        for (CustomerCallReturnLinesViewModel item : lines) {
            if (!item.IsPromoLine) {
                CustomerCallOrderLinePromotion promotionitem = new CustomerCallOrderLinePromotion();
                ProductModel productModel = new ProductManager(context).getItem(item.ProductId);
                promotionitem.UniqueId = item.UniqueId;
                promotionitem.ReturnReasonId = item.ReturnReasonId;
                if (GlobalVariables.getIsThirdParty() && isFromRequest) {
                    CustomerCallReturnModel returnModel = customerCallReturnManager.getItem(item.ReturnUniqueId);
                    if (returnModel != null) {
                        promotionitem.SaleNo = String.valueOf(returnModel.ReturnRequestBackOfficeId);
                        promotionitem.OrderDate = returnModel.BackOfficeInvoiceDate;
                    }
                    promotionitem.ReferenceNo = item.ItemRef;
                } else {
                    promotionitem.SaleNo = item.SaleNo;
                    promotionitem.ReferenceNo = item.ReferenceNo;
                }
                promotionitem.ProductName = item.ProductName;
                promotionitem.ProductCode = item.ProductCode;
                promotionitem.ProductId = item.ProductId;
                promotionitem.ProductRef = productModel.BackOfficeId;
                promotionitem.UnitName = item.UnitName;
                promotionitem.QtyCaption = item.Qty;
                promotionitem.TotalRequestQty = item.TotalReturnQty;
//                promotionitem.PriceId = item.PriceId == null ? "" : item.PriceId.toString();
                promotionitem.UnitPrice = item.RequestUnitPrice;
//                promotionitem.FreeReasonId = item.FreeReasonId;
//                promotionitem.FreeReasonName = item.FreeReasonName;
//                promotionitem.IsRequestFreeItem = item.IsRequestFreeItem;
//                promotionitem.PayDuration = item.PayDuration;
//                promotionitem.RuleNo = item.RuleNo;
                promotionitem.IsRequestPrizeItem = false;
                if (item.ReturnUniqueId != null)
                    promotionitem.OrderId = item.ReturnUniqueId.toString();
                promotionLines.add(promotionitem);
            }
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            customerCallOrderPromotion.DocumentType = OrderTypeEnum.PRESALE;
        else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
            customerCallOrderPromotion.DocumentType = OrderTypeEnum.INVOICE;
        else
            customerCallOrderPromotion.DocumentType = OrderTypeEnum.ORDER;
        customerCallOrderPromotion.Lines = promotionLines;
//        customerCallOrderPromotion.BackOfficeOrderNo = customerCallOrderModel.BackOfficeOrderNo;
        return customerCallOrderPromotion;
    }


    private static CustomerCallOrderPromotion fillCustomerCallOrderPromotion(@NonNull Context context, @NonNull UUID callOrderUniqueId, @NonNull UUID customerUniqueId) {
        CustomerCallOrderPromotion customerCallOrderPromotion = new CustomerCallOrderPromotion();
        CustomerCallOrderModel customerCallOrderModel = new CustomerCallOrderManager(context).getItem(callOrderUniqueId);
        CustomerModel customerModel = new CustomerManager(context).getItem(customerUniqueId);
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(context);
        customerCallOrderPromotion.UniqueId = customerCallOrderModel.UniqueId;
        customerCallOrderPromotion.AccYearSDS = customerCallOrderModel.AccYearSDS;
        customerCallOrderPromotion.SaleDate = customerCallOrderModel.SaleDate;
        customerCallOrderPromotion.TotalPrice = customerCallOrderOrderViewManager.calculateTotalAmount(callOrderUniqueId).TotalAmount;
        customerCallOrderPromotion.CustomerId = customerModel.UniqueId;
        customerCallOrderPromotion.CustomerRef = customerModel.BackOfficeId;
        customerCallOrderPromotion.CustomerCode = customerModel.CustomerCode;
        customerCallOrderPromotion.CustomerName = customerModel.CustomerName;
        customerCallOrderPromotion.RemainCredit = customerModel.RemainCredit;
        customerCallOrderPromotion.RemainDebit = customerModel.RemainDebit;
        customerCallOrderPromotion.Comment = customerCallOrderModel.Comment;
        customerCallOrderPromotion.BackOfficeOrderId = customerCallOrderModel.BackOfficeOrderId;
        customerCallOrderPromotion.BackOfficeInvoiceId = customerCallOrderModel.BackOfficeInvoiceId;
        customerCallOrderPromotion.SaleOfficeRefSDS = customerCallOrderModel.SaleOfficeRefSDS;
        customerCallOrderPromotion.DcRefSDS = customerCallOrderModel.DCRefSDS;
        customerCallOrderPromotion.DealerRefSDS = customerCallOrderModel.DealerRefSDS;
        customerCallOrderPromotion.DealerCodeSDS = customerCallOrderModel.DealerCodeSDS;

        if (customerCallOrderModel.OrderPaymentTypeUniqueId != null) {
            PaymentTypeOrderModel paymentTypeOrderModel = new PaymentOrderTypeManager(context).getItem(customerCallOrderModel.OrderPaymentTypeUniqueId);
            customerCallOrderPromotion.OrderPaymentTypeId = customerCallOrderModel.OrderPaymentTypeUniqueId;
            customerCallOrderPromotion.OrderPaymentTypeRef = paymentTypeOrderModel == null ? "0" : paymentTypeOrderModel.BackOfficeId;
            customerCallOrderPromotion.OrderPaymentTypeName = paymentTypeOrderModel == null ? "" : paymentTypeOrderModel.PaymentTypeOrderName;
        }

        if (customerCallOrderModel.OrderTypeUniqueId != null) {
            CustomerOrderTypeModel customerOrderTypeModel = new CustomerOrderTypesManager(context).getItem(customerCallOrderModel.OrderTypeUniqueId);
            customerCallOrderPromotion.OrderTypeId = customerCallOrderModel.OrderTypeUniqueId;
            customerCallOrderPromotion.OrderTypeRef = customerOrderTypeModel.BackOfficeId;
            customerCallOrderPromotion.OrderTypeName = customerOrderTypeModel.OrderTypeName;
        }
        List<CustomerCallOrderOrderViewModel> lines = customerCallOrderOrderViewManager.getLines(callOrderUniqueId, null);
        ArrayList<CustomerCallOrderLinePromotion> promotionLines = new ArrayList<>();

        for (CustomerCallOrderOrderViewModel item : lines) {
            if (!item.IsPromoLine && item.cart.isEmpty()) {
                CustomerCallOrderLinePromotion promotionitem = new CustomerCallOrderLinePromotion();
                ProductModel productModel = new ProductManager(context).getItem(item.ProductId);
                promotionitem.UniqueId = item.UniqueId;
                promotionitem.ProductName = item.ProductName;
                promotionitem.ProductCode = item.ProductCode;
                promotionitem.ProductId = item.ProductId;
                promotionitem.ProductRef = productModel.BackOfficeId;
                promotionitem.UnitName = item.UnitName;
                promotionitem.QtyCaption = item.Qty;
                promotionitem.TotalRequestQty = item.TotalQty;
                promotionitem.PriceId = item.PriceId == null ? "" : item.PriceId.toString();
                promotionitem.UnitPrice = item.UnitPrice;
                promotionitem.FreeReasonId = item.FreeReasonId;
                promotionitem.FreeReasonName = item.FreeReasonName;
                promotionitem.IsRequestFreeItem = item.IsRequestFreeItem;
                promotionitem.PayDuration = item.PayDuration;
                promotionitem.RuleNo = item.RuleNo;
                promotionitem.IsRequestPrizeItem = false;
                promotionLines.add(promotionitem);
            }
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            customerCallOrderPromotion.DocumentType = OrderTypeEnum.PRESALE;
        else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
            customerCallOrderPromotion.DocumentType = OrderTypeEnum.INVOICE;
        else
            customerCallOrderPromotion.DocumentType = OrderTypeEnum.ORDER;
        customerCallOrderPromotion.Lines = promotionLines;
        customerCallOrderPromotion.BackOfficeOrderNo = customerCallOrderModel.BackOfficeOrderNo;
        return customerCallOrderPromotion;
    }

    private static DiscountCallReturnData fillCustomerCallReturnPromotion(@NonNull Context context, @NonNull CustomerCallReturnModel customerCallReturnModel) {

        SysConfigManager sysConfigManager = new SysConfigManager(context);

        DiscountCallReturnData disRet = new DiscountCallReturnData();
//        List<CustomerCallReturnModel> customerCallReturnModels = new CustomerCallReturnManager(context).getReturnCalls(customerUniqueId, null, false);
//        CustomerCallReturnModel customerCallReturnModel = null;
//        for (CustomerCallReturnModel item : customerCallReturnModels) {
//            if (item.BackOfficeInvoiceId != null)
//                customerCallReturnModel = item;
//        }
//
//        if (customerCallReturnModels.isEmpty())
//            return new DiscountCallReturnData();


        CustomerOldInvoiceHeaderModel oldInvoice = new CustomerOldInvoiceHeaderManager(context).getItem(customerCallReturnModel.BackOfficeInvoiceId);
        CustomerModel customerModel = new CustomerManager(context).getItem(customerCallReturnModel.CustomerUniqueId);
        OrderAmount returnAmount = new CustomerCallReturnLinesViewManager(context).calculateTotalAmount(customerCallReturnModel.CustomerUniqueId, customerCallReturnModel.BackOfficeInvoiceId, false);
        disRet.customerId = customerModel.BackOfficeId;
        disRet.returnUniqueId = customerCallReturnModel.UniqueId.toString();
        disRet.callUniqueId = customerCallReturnModel.UniqueId.toString();
        disRet.totalRequestAmount = (returnAmount.TotalAmount == null ? BigDecimal.ZERO : returnAmount.TotalAmount.bigDecimalValue());
        disRet.totalRequestTax = (returnAmount.TaxAmount == null ? BigDecimal.ZERO : returnAmount.TaxAmount.bigDecimalValue());
        disRet.totalRequestCharge = (returnAmount.ChargeAmount == null ? BigDecimal.ZERO : returnAmount.ChargeAmount.bigDecimalValue());
        disRet.totalRequestDiscount = (returnAmount.DiscountAmount == null ? BigDecimal.ZERO : returnAmount.DiscountAmount.bigDecimalValue());
        disRet.returnRefDate = DateHelper.toString(customerCallReturnModel.BackOfficeInvoiceDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(context));

        //???
        //customerCallReturnPromotion.totalRequestNetAmount = customerCallReturnPromotion.totalRequestAmount.add(customerCallReturnPromotion.totalReq);
        if (returnAmount.Dis1Amount == null)
            returnAmount.Dis1Amount = Currency.ZERO;
        if (returnAmount.Dis2Amount == null)
            returnAmount.Dis2Amount = Currency.ZERO;
        if (returnAmount.Dis3Amount == null)
            returnAmount.Dis3Amount = Currency.ZERO;
        if (returnAmount.DisOtherAmount == null)
            returnAmount.DisOtherAmount = Currency.ZERO;
       /*
       TODO
        if (returnAmount.TotalReturnOtherDiscount == null)
            returnAmount.TotalReturnOtherDiscount = Currency.ZERO;
            */
        if (returnAmount.Add1Amount == null)
            returnAmount.Add1Amount = Currency.ZERO;
        if (returnAmount.Add2Amount == null)
            returnAmount.Add2Amount = Currency.ZERO;
        if (returnAmount.AddOtherAmount == null)
            returnAmount.AddOtherAmount = Currency.ZERO;

        disRet.totalReturnAmount = (returnAmount.TotalAmount == null ? BigDecimal.ZERO : returnAmount.TotalAmount.bigDecimalValue());
        disRet.totalReturnDiscount =
                returnAmount.Dis1Amount.bigDecimalValue()
                        .add(returnAmount.Dis2Amount.bigDecimalValue())
                        .add(returnAmount.Dis3Amount.bigDecimalValue())
                        .add(returnAmount.DisOtherAmount.bigDecimalValue())
        //.add(returnAmount.TotalReturnOtherDiscount.bigDecimalValue())
        ;

        disRet.totalReturnAddAmount = returnAmount.Add1Amount.bigDecimalValue().add(
                returnAmount.Add2Amount.bigDecimalValue()).add(returnAmount.AddOtherAmount.bigDecimalValue());
        disRet.totalReturnCharge = (returnAmount.ChargeAmount == null ? BigDecimal.ZERO : returnAmount.ChargeAmount.bigDecimalValue());
        disRet.totalReturnTax = (returnAmount.TaxAmount == null ? BigDecimal.ZERO : returnAmount.TaxAmount.bigDecimalValue());
        disRet.totalReturnNetAmount =
                disRet.totalReturnAmount
                        .add(disRet.totalReturnAddAmount)
                        .subtract(disRet.totalReturnDiscount);

        disRet.DCRef = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.DcRef, SysConfigManager.cloud), -1);
        disRet.SaleOfficeRef = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.SaleOfficeRef, SysConfigManager.cloud), -1);
        disRet.dealerId = SysConfigManager.getIntValue(sysConfigManager.read(ConfigKey.DealerRef, SysConfigManager.cloud), -1);
        if (customerCallReturnModel.ReturnTypeUniqueId == com.varanegar.vaslibrary.model.returnType.ReturnType.WithoutRef)
            disRet.returnType = 1;
        else if (customerCallReturnModel.ReturnTypeUniqueId == com.varanegar.vaslibrary.model.returnType.ReturnType.WithRef)
            disRet.returnType = 2;

        if (oldInvoice.SaleNo != null && !oldInvoice.SaleNo.isEmpty())
            disRet.returnRefNo = oldInvoice.SaleNo;//customerCallReturnModel.BackOfficeInvoiceNo;
        else
            disRet.returnRefNo = "0";
        if (oldInvoice.SaleRef != null && !oldInvoice.SaleRef.isEmpty())
            disRet.returnRefId = Integer.parseInt(oldInvoice.SaleRef);
        else
            disRet.returnRefId = 0;

/*
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            disRet.DocumentType = OrderTypeEnum.PRESALE;
        else if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
            disRet.DocumentType = OrderTypeEnum.INVOICE;
        else
            disRet.DocumentType = OrderTypeEnum.ORDER;

        cCRP.callUniqueId = ;
        cCRP.returnRequestId;
        cCRP.returnRequestDate;
        cCRP.returnRequestNo;
//        cCRP.distId;
//        cCRP.returnTypeText;
        cCRP.visitStatusId;
//        cCRP.visitStatusText;
        cCRP.visitRejectReason;
        cCRP.localPaperNo;
        cCRP.returnRefId;
        cCRP.returnRefNo;
        cCRP.returnRefDate;
        cCRP.returnReasonId;
//        cCRP.returnReason;

*/
        List<CustomerCallReturnLinesViewModel> retlines = new CustomerCallReturnLinesViewManager(context).getReturnLines(customerCallReturnModel.UniqueId, false);
        disRet.callReturnLineItemData = new HashMap<>();
        int i = 0;
        for (CustomerCallReturnLinesViewModel item : retlines) {
            i++;
            DiscountCallReturnLineData promotionitem = new DiscountCallReturnLineData();
            ProductModel productModel = new ProductManager(context).getItem(item.ProductId);
            promotionitem.callUniqueId = item.ReturnUniqueId.toString();
            promotionitem.returnLineUniqueId = item.UniqueId.toString();
            promotionitem.customerId = customerModel.BackOfficeId;
            promotionitem.sortId = item.SortId;
            promotionitem.productId = productModel.BackOfficeId;
            promotionitem.weight = item.Weight;
            promotionitem.unitPrice = item.RequestUnitPrice.bigDecimalValue();
            //promotionitem.priceId = item.PriceId;
            promotionitem.totalRequestCharge = new BigDecimal(0);
            promotionitem.totalRequestTax = new BigDecimal(0);
            //           promotionitem.callUniqueId = item.CallUniqueId;
            promotionitem.returnTotalQty = item.TotalReturnQty;

            promotionitem.returnProductTypeId = ReturnType.Well.equals(item.ReturnProductTypeId) ? 1 : 2;
            promotionitem.ReturnProductTypeId = item.ReturnProductTypeId;

            promotionitem.returnReasonId = item.ReturnReasonId;

            disRet.callReturnLineItemData.put(i + "", promotionitem);
        }

        return disRet;
    }

    private static ArrayList<DiscountCallOrderLineData> populateOriginalData(Context context, UUID callOrderUniqueId) {
        ArrayList<DiscountCallOrderLineData> orderProduct = new ArrayList<>();
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(context);
        List<CustomerCallOrderOrderViewModel> lines = customerCallOrderOrderViewManager.getLines(callOrderUniqueId, null);
        for (CustomerCallOrderOrderViewModel item : lines) {
            DiscountCallOrderLineData disLine = new DiscountCallOrderLineData();
            disLine.orderLineId = item.UniqueId;
            disLine.productId = new ProductManager(context).getBackOfficeId(item.ProductId);
            disLine.invoiceTotalQty = item.OriginalTotalQty == null ? BigDecimal.ZERO : item.OriginalTotalQty;
            if (item.IsPromoLine || !item.cart.isEmpty())
                disLine.isRequestPrizeItem = true;
            else
                disLine.isRequestPrizeItem = false;
            orderProduct.add(disLine);
        }
        return orderProduct;
    }

    public static void fillChoicePrize(Context context, CustomerCallOrderPromotion customerCallOrderPromotion, UUID customerId, UUID callOrderId) {
        OrderPrizeManager orderPrizeManager = new OrderPrizeManager(context);
        List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerDisRefOrderPrizes(customerCallOrderPromotion.CustomerId, callOrderId));
        if (orderPrizeModels.size() > 0) {
            for (OrderPrizeModel orderPrize : orderPrizeModels) {
                HashMap<UUID, OrderPrizeModel> oldPrizes = new HashMap<>();
                for (int i = 0; i < customerCallOrderPromotion.discountEvcPrize.size(); i++) {
                    DiscountEvcPrizeData discountEvcPrizeData = customerCallOrderPromotion.discountEvcPrize.get(i);
                    if (discountEvcPrizeData.DiscountRef == orderPrize.DisRef) {
                        OrderPrizeModel orderPrizeModel = new OrderPrizeModel();
                        orderPrizeModel.DiscountId = discountEvcPrizeData.DiscountId;
                        orderPrizeModel.DisRef = discountEvcPrizeData.DiscountRef;
                        orderPrizeModel.CustomerId = customerId;
                        orderPrizeModel.CallOrderId = callOrderId;
                        orderPrizeModel.TotalQty = discountEvcPrizeData.Qty;
                        ProductManager productManager = new ProductManager(context);
                        orderPrizeModel.ProductId = UUID.fromString(productManager.getIdByBackofficeId(discountEvcPrizeData.GoodRef));
                        oldPrizes.put(orderPrizeModel.ProductId, orderPrizeModel);
                    }
                }
                finishChoicePrize(context, customerCallOrderPromotion, orderPrize.DisRef, customerCallOrderPromotion.CustomerId, callOrderId, oldPrizes);
            }
        }
    }


    public static void finishChoicePrize(Context context, CustomerCallOrderPromotion customerCallOrderPromotion, int disRef, UUID customerId, UUID callOrderId, HashMap<UUID, OrderPrizeModel> oldPrize) {
        OrderPrizeManager orderPrizeManager = new OrderPrizeManager(context);
        List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, disRef, callOrderId));
        int itemConvertFactor = 0;
        String itemUnitName = null;
        HashMap<Integer, ArrayList<Integer>> evcPrize = new HashMap<>();
        for (DiscountEvcPrizeData item :
                customerCallOrderPromotion.discountEvcPrize) {
            ArrayList<Integer> disRefs;
            if (!evcPrize.containsKey(item.GoodRef)) {
                disRefs = new ArrayList<>();
            } else {
                disRefs = evcPrize.get(item.GoodRef);
            }
            disRefs.add(item.DiscountRef);
            evcPrize.put(item.GoodRef, disRefs);
        }
        CustomerCallOrderLinePromotion promotion = new CustomerCallOrderLinePromotion();
        for (int i = 0; i < customerCallOrderPromotion.LinesWithPromo.size(); i++) {
            if (customerCallOrderPromotion.LinesWithPromo.get(i).DiscountRef == disRef || ((evcPrize.get(customerCallOrderPromotion.LinesWithPromo.get(i).ProductRef) != null) && (evcPrize.get(customerCallOrderPromotion.LinesWithPromo.get(i).ProductRef).contains(customerCallOrderPromotion.LinesWithPromo.get(i).DiscountRef)))) { // we use this for when we have two disRef for one line
                CustomerCallOrderLinePromotion item = customerCallOrderPromotion.LinesWithPromo.get(i);
                promotion.TotalRequestQty = item.TotalRequestQty;
                promotion.QtyCaption = item.QtyCaption;
                promotion.DiscountRef = item.DiscountRef;
                promotion.DiscountId = item.DiscountId;
                promotion.ProductCode = item.ProductCode;
                promotion.ProductId = item.ProductId;
                promotion.ProductName = item.ProductName;
                promotion.EVCId = item.EVCId;
                promotion.FreeReasonId = item.FreeReasonId;
                promotion.IsRequestPrizeItem = item.IsRequestPrizeItem;
                promotion.UnitPrice = item.UnitPrice;
                promotion.OrderId = item.OrderId;
                promotion.ProductRef = item.ProductRef;
                promotion.UnitName = item.UnitName;
                promotion.ConvertFactory = item.ConvertFactory;
                promotion.PriceId = item.PriceId;
                promotion.SortId = item.SortId;
                promotion.IsRequestFreeItem = item.IsRequestFreeItem;
                promotion.RequestBulkQty = item.RequestBulkQty;
                promotion.RequesBulkQtyUnitUniqueId = item.RequesBulkQtyUnitUniqueId;
                promotion.InvoiceAdd1 = item.InvoiceAdd1;
                promotion.InvoiceAdd2 = item.InvoiceAdd2;
                promotion.InvoiceAddOther = item.InvoiceAddOther;
                promotion.InvoiceDis1 = item.InvoiceDis1;
                promotion.InvoiceDis2 = item.InvoiceDis2;
                promotion.InvoiceDis3 = item.InvoiceDis3;
                promotion.InvoiceDisOther = item.InvoiceDisOther;
                promotion.InvoiceAmount = item.InvoiceAmount;
                promotion.InvoiceNetAmount = item.InvoiceNetAmount;
                promotion.RequestNetAmount = item.RequestNetAmount;
                promotion.RequestTax = item.RequestTax;
                promotion.RequestCharge = item.RequestCharge;
                promotion.RequestAmount = item.RequestAmount;
                promotion.RequestOtherDiscount = item.RequestOtherDiscount;
            }
        }
        CustomerPriceManager customerPriceManager = new CustomerPriceManager(context);
        for (OrderPrizeModel prize : orderPrizeModels) {
            boolean exist = false;
            for (int i = 0; i < customerCallOrderPromotion.LinesWithPromo.size(); i++) {
                CustomerCallOrderLinePromotion line = customerCallOrderPromotion.LinesWithPromo.get(i);
                if (line.ProductId.equals(prize.ProductId) && line.DiscountRef != 0) {
                    exist = true;
                    BigDecimal totalQty = line.TotalRequestQty;
                    BigDecimal oldQty = oldPrize.get(prize.ProductId) == null ? BigDecimal.ZERO : oldPrize.get(prize.ProductId).TotalQty;
                    int convertFactor;
                    if (line.ConvertFactory == 0)
                        convertFactor = 1;
                    else
                        convertFactor = line.ConvertFactory;
                    BigDecimal qty = totalQty.subtract(oldQty.multiply(new BigDecimal(convertFactor))).add(prize.TotalQty.multiply(new BigDecimal(convertFactor)));
                    String qtyCaption = String.valueOf(new BigDecimal(line.QtyCaption).subtract(oldQty).add(prize.TotalQty));
                    itemConvertFactor = line.ConvertFactory;
                    itemUnitName = line.UnitName;

                    CustomerCallOrderLinePromotion customerCallOrderLinePromotion = customerCallOrderPromotion.LinesWithPromo.get(i);
                    BigDecimal total = customerCallOrderLinePromotion.TotalRequestQty;
                    customerCallOrderLinePromotion.TotalRequestQty = qty;
                    customerCallOrderLinePromotion.QtyCaption = qtyCaption;
                    customerCallOrderLinePromotion.InvoiceAdd1 = (customerCallOrderLinePromotion.InvoiceAdd1 != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceAdd1) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAdd2 = (customerCallOrderLinePromotion.InvoiceAdd2 != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceAdd2) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAddOther = (customerCallOrderLinePromotion.InvoiceAddOther != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceAddOther) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis1 = (customerCallOrderLinePromotion.InvoiceDis1 != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceDis1) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis2 = (customerCallOrderLinePromotion.InvoiceDis2 != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceDis2) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis3 = (customerCallOrderLinePromotion.InvoiceDis3 != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceDis3) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDisOther = (customerCallOrderLinePromotion.InvoiceDisOther != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceDisOther) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAmount = (customerCallOrderLinePromotion.InvoiceAmount != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceNetAmount = (customerCallOrderLinePromotion.InvoiceNetAmount != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.InvoiceNetAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.RequestNetAmount = (customerCallOrderLinePromotion.RequestNetAmount != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.RequestNetAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.RequestTax = (customerCallOrderLinePromotion.RequestTax != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.RequestTax) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.RequestCharge = (customerCallOrderLinePromotion.RequestCharge != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.RequestCharge) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.RequestAmount = (customerCallOrderLinePromotion.RequestAmount != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.RequestAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.RequestOtherDiscount = (customerCallOrderLinePromotion.RequestOtherDiscount != null) ? (new Currency((HelperMethods.currencyToDouble(customerCallOrderLinePromotion.RequestOtherDiscount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderPromotion.LinesWithPromo.set(i, customerCallOrderLinePromotion);
                }
            }
            if (!exist) {
                ProductManager productManager = new ProductManager(context);
                ProductModel productModel = productManager.getItem(ProductManager.getProduct(prize.ProductId));
                CustomerCallOrderLinePromotion customerCallOrderLinePromotion = new CustomerCallOrderLinePromotion();
                int convertFactor;
                if (promotion.ConvertFactory == 0)
                    convertFactor = 1;
                else
                    convertFactor = promotion.ConvertFactory;
                BigDecimal total = promotion.TotalRequestQty;
                BigDecimal qty = prize.TotalQty.multiply(new BigDecimal(convertFactor));
                customerCallOrderLinePromotion.TotalRequestQty = qty;
                customerCallOrderLinePromotion.QtyCaption = String.valueOf(prize.TotalQty);
                customerCallOrderLinePromotion.DiscountRef = prize.DisRef;
                customerCallOrderLinePromotion.DiscountId = prize.DiscountId;
                customerCallOrderLinePromotion.ProductCode = productModel.ProductCode;
                customerCallOrderLinePromotion.ProductId = prize.ProductId;
                customerCallOrderLinePromotion.ProductName = productModel.ProductName;
                customerCallOrderLinePromotion.EVCId = promotion.EVCId;
                customerCallOrderLinePromotion.FreeReasonId = promotion.FreeReasonId;
                customerCallOrderLinePromotion.IsRequestPrizeItem = promotion.IsRequestPrizeItem;
                customerCallOrderLinePromotion.OrderId = promotion.OrderId;
                customerCallOrderLinePromotion.ProductRef = productModel.BackOfficeId;
                customerCallOrderLinePromotion.UnitName = promotion.UnitName;
                if (customerCallOrderLinePromotion.UnitName == null)
                    customerCallOrderLinePromotion.UnitName = itemUnitName;
                customerCallOrderLinePromotion.ConvertFactory = promotion.ConvertFactory;
                if (customerCallOrderLinePromotion.ConvertFactory == 0)
                    customerCallOrderLinePromotion.ConvertFactory = itemConvertFactor;
                // todo
                // NGT-4226 - change amount and discount manually!! when we choice a prize with a different price. it is not a good solution and it should be changed later
                if ((promotion.InvoiceDis1 != null? promotion.InvoiceDis1: Currency.ZERO).compareTo(promotion.InvoiceAmount != null? promotion.InvoiceAmount: Currency.ZERO) == 0
                        && ((promotion.InvoiceNetAmount != null? promotion.InvoiceNetAmount: Currency.ZERO).compareTo(Currency.ZERO)) == 0
                        && ((promotion.InvoiceDis2 != null? promotion.InvoiceDis2: Currency.ZERO).compareTo(Currency.ZERO)) == 0
                        && ((promotion.InvoiceDis3 != null? promotion.InvoiceDis3: Currency.ZERO).compareTo(Currency.ZERO)) == 0
                        &&  ((promotion.InvoiceDisOther != null? promotion.InvoiceDisOther: Currency.ZERO).compareTo(Currency.ZERO)) == 0
                        &&  ((promotion.InvoiceAdd1 != null? promotion.InvoiceAdd1: Currency.ZERO).compareTo(Currency.ZERO)) == 0
                        &&  ((promotion.InvoiceAdd2 != null? promotion.InvoiceAdd2: Currency.ZERO).compareTo(Currency.ZERO)) == 0
                        &&  ((promotion.InvoiceAddOther != null? promotion.InvoiceAddOther: Currency.ZERO).compareTo(Currency.ZERO)) == 0) {
                    CustomerPriceModel newPrice = customerPriceManager.getProductPrice(customerId, callOrderId, prize.ProductId);
                    customerCallOrderLinePromotion.UnitPrice = newPrice.Price;
                    customerCallOrderLinePromotion.PriceId = newPrice.PriceId.toString();
                    customerCallOrderLinePromotion.InvoiceAdd1 = Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAdd2 = Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAddOther = Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis1 = newPrice.Price.multiply(qty);
                    customerCallOrderLinePromotion.InvoiceDis2 = Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis3 = Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDisOther = Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAmount = newPrice.Price.multiply(qty);
                    customerCallOrderLinePromotion.InvoiceNetAmount = Currency.ZERO;
                } else {
                    customerCallOrderLinePromotion.UnitPrice = promotion.UnitPrice;
                    customerCallOrderLinePromotion.PriceId = promotion.PriceId;
                    customerCallOrderLinePromotion.InvoiceAdd1 = (promotion.InvoiceAdd1 != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceAdd1) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAdd2 = (promotion.InvoiceAdd2 != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceAdd2) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAddOther = (promotion.InvoiceAddOther != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceAddOther) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis1 = (promotion.InvoiceDis1 != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceDis1) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis2 = (promotion.InvoiceDis2 != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceDis2) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDis3 = (promotion.InvoiceDis3 != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceDis3) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceDisOther = (promotion.InvoiceDisOther != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceDisOther) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceAmount = (promotion.InvoiceAmount != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                    customerCallOrderLinePromotion.InvoiceNetAmount = (promotion.InvoiceNetAmount != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.InvoiceNetAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;

                }
                customerCallOrderLinePromotion.SortId = promotion.SortId;
                customerCallOrderLinePromotion.IsRequestFreeItem = promotion.IsRequestFreeItem;
                customerCallOrderLinePromotion.RequestBulkQty = promotion.RequestBulkQty;
                customerCallOrderLinePromotion.RequesBulkQtyUnitUniqueId = promotion.RequesBulkQtyUnitUniqueId;
                customerCallOrderLinePromotion.RequestNetAmount = (promotion.RequestNetAmount != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.RequestNetAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                customerCallOrderLinePromotion.RequestTax = (promotion.RequestTax != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.RequestTax) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                customerCallOrderLinePromotion.RequestCharge = (promotion.RequestCharge != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.RequestCharge) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                customerCallOrderLinePromotion.RequestAmount = (promotion.RequestAmount != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.RequestAmount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                customerCallOrderLinePromotion.RequestOtherDiscount = (promotion.RequestOtherDiscount != null) ? (new Currency((HelperMethods.currencyToDouble(promotion.RequestOtherDiscount) / HelperMethods.bigDecimalToDouble(total)) * HelperMethods.bigDecimalToDouble(qty))) : Currency.ZERO;
                customerCallOrderPromotion.LinesWithPromo.add(customerCallOrderLinePromotion);
            }
        }
        for (int i = customerCallOrderPromotion.LinesWithPromo.size() - 1; i >= 0; i--) {
            if (customerCallOrderPromotion.LinesWithPromo.get(i).TotalRequestQty.compareTo(BigDecimal.ZERO) <= 0)
                customerCallOrderPromotion.LinesWithPromo.remove(i);
        }
    }
}
