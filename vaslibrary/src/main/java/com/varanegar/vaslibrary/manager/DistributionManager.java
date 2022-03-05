package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercall.CallInvoiceLineManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnRequestManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLinesRequestManager;
import com.varanegar.vaslibrary.manager.customerpromotionpricemanager.CustomerPromotionPriceManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.call.CallInvoiceLineModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnRequestModel;
import com.varanegar.vaslibrary.model.customerpromotionprice.CustomerPromotionPriceModel;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerCallModel;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerPriceModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQtyModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.distribution.DistributionApi;
import com.varanegar.vaslibrary.webapi.distribution.DistributionTourViewModel;
import com.varanegar.vaslibrary.webapi.distribution.InvoiceLineBatchQtyDetailViewModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 2/24/2018.
 */

public class DistributionManager {
    private Context context;

    public DistributionManager(Context context) {
        this.context = context;
    }

    public void sync(final UpdateCall call) {
        final TourManager tourManager = new TourManager(context);
        final TourModel tourModel = tourManager.loadTour();
        DistributionApi distributionApi = new DistributionApi(context);
        distributionApi.runWebRequest(distributionApi.getDistribution(tourModel.UniqueId.toString(), tourModel.IsVirtual), new WebCallBack<DistributionTourViewModel>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final DistributionTourViewModel result, Request request) {
                try {
                    tourModel.AgentMobile = result.AgentMobile;
                    tourManager.saveTour(tourModel, context);

                    ProductManager productManager = new ProductManager(context);
                    List<ProductModel> productModels = productManager.getAll();
                    for (ProductModel productModel :
                            productModels) {
                        productModel.IsFreeItem = false;
                    }
                    if (productModels != null && productModels.size() > 0)
                        productManager.update(productModels);

                    DistributionCustomerCallManager distributionCustomerCallManager = new DistributionCustomerCallManager(context);
                    distributionCustomerCallManager.deleteAll();

                    CustomerCallInvoiceManager customerCallInvoiceManager = new CustomerCallInvoiceManager(context);
                    customerCallInvoiceManager.deleteAll();

                    CallInvoiceLineManager callInvoiceLineManager = new CallInvoiceLineManager(context);
                    callInvoiceLineManager.deleteAll();

                    DistributionCustomerPriceManager customerPriceManager = new DistributionCustomerPriceManager(context);
                    customerPriceManager.deleteAll();

                    CustomerPromotionPriceManager customerPromotionPriceManager = new CustomerPromotionPriceManager(context);
                    customerPromotionPriceManager.deleteAll();

                    InvoiceLineQtyManager invoiceLineQtyManager = new InvoiceLineQtyManager(context);
                    invoiceLineQtyManager.deleteAll();

                    CustomerCallReturnRequestManager customerCallReturnRequestManager = new CustomerCallReturnRequestManager(context);
                    customerCallReturnRequestManager.deleteAll();

                    ReturnLinesRequestManager returnLinesRequestManager = new ReturnLinesRequestManager(context);
                    returnLinesRequestManager.deleteAll();

                    ReturnLineQtyRequestManager returnLineQtyRequestManager = new ReturnLineQtyRequestManager(context);
                    returnLineQtyRequestManager.deleteAll();

                    if (result.DistributionCustomerCalls != null && result.DistributionCustomerCalls.size() > 0) {
                        distributionCustomerCallManager.insert(result.DistributionCustomerCalls);

                        // populate customer call orders
                        if (result.DistributionCustomerCallOrders != null && result.DistributionCustomerCallOrders.size() > 0) {
                            for (final CustomerCallInvoiceModel customerCallInvoiceModel :
                                    result.DistributionCustomerCallOrders) {
                                DistributionCustomerCallModel customerCallModel =
                                        Linq.findFirst(result.DistributionCustomerCalls, item -> item.UniqueId
                                                != null && item.UniqueId.equals(customerCallInvoiceModel.CustomerCallUniqueId));
                                if (customerCallModel != null)
                                    customerCallInvoiceModel.CustomerUniqueId = customerCallModel.CustomerUniqueId;
                            }
                            customerCallInvoiceManager.insert(result.DistributionCustomerCallOrders);

                            if (result.DistributionCustomerCallOrderLines != null && result.DistributionCustomerCallOrderLines.size() > 0) {
                                callInvoiceLineManager.insert(result.DistributionCustomerCallOrderLines);

                                List<DistributionCustomerPriceModel> priceModels = new ArrayList<>();
                                List<CustomerPromotionPriceModel> promotionPriceModels = new ArrayList<>();
                                Set<String> priceModelSet = new HashSet<>();
                                Set<String> promotionPriceModelSet = new HashSet<>();
                                for (final CallInvoiceLineModel line :
                                        result.DistributionCustomerCallOrderLines) {
                                    if (line.UnitPrice != null && line.UnitPrice.compareTo(Currency.ZERO) > 0) {
                                        CustomerCallInvoiceModel invoiceModel = Linq.findFirst(result.DistributionCustomerCallOrders, item -> item.UniqueId.equals(line.OrderUniqueId));
                                        String key = line.ProductUniqueId + "|" + invoiceModel.CustomerUniqueId + "|" + invoiceModel.UniqueId;
                                        if (!line.IsPromoLine) {
                                            if (!priceModelSet.contains(key)) {
                                                DistributionCustomerPriceModel customerPriceModel = new DistributionCustomerPriceModel();
                                                customerPriceModel.PriceId = line.CPriceUniqueId;
                                                customerPriceModel.UniqueId = UUID.randomUUID();
                                                customerPriceModel.Price = line.UnitPrice;
                                                customerPriceModel.ProductUniqueId = line.ProductUniqueId;
                                                customerPriceModel.UserPrice = line.UserPrice;
                                                customerPriceModel.CustomerUniqueId = invoiceModel.CustomerUniqueId;
                                                customerPriceModel.CallOrderId = invoiceModel.UniqueId;
                                                priceModelSet.add(key);
                                                priceModels.add(customerPriceModel);
                                            }
                                        } else {
                                            if (!promotionPriceModelSet.contains(key)) {
                                                CustomerPromotionPriceModel customerPromotionPriceModel = new CustomerPromotionPriceModel();
                                                customerPromotionPriceModel.PriceId = line.CPriceUniqueId;
                                                customerPromotionPriceModel.UniqueId = UUID.randomUUID();
                                                customerPromotionPriceModel.Price = line.UnitPrice;
                                                customerPromotionPriceModel.ProductUniqueId = line.ProductUniqueId;
                                                customerPromotionPriceModel.UserPrice = line.UserPrice;
                                                customerPromotionPriceModel.CustomerUniqueId = invoiceModel.CustomerUniqueId;
                                                customerPromotionPriceModel.CallOrderId = invoiceModel.UniqueId;
                                                promotionPriceModelSet.add(key);
                                                promotionPriceModels.add(customerPromotionPriceModel);
                                            }
                                        }
                                    }
                                    if (line.IsRequestFreeItem) {
                                        ProductModel productModel = productManager.getItem(line.ProductUniqueId);
                                        if (productModel != null) {
                                            productModel.IsFreeItem = true;
                                            productManager.update(productModel);
                                        }
                                    }
                                }

                                if (priceModels.size() > 0)
                                    customerPriceManager.insert(priceModels);

                                if (promotionPriceModels.size() > 0)
                                    customerPromotionPriceManager.insert(promotionPriceModels);

                                if (result.DistributionCustomerCallOrderLineOrderQtyDetails != null && result.DistributionCustomerCallOrderLineOrderQtyDetails.size() > 0) {
                                    invoiceLineQtyManager.insert(result.DistributionCustomerCallOrderLineOrderQtyDetails);
                                } else {
                                    Timber.d("customer call order lines qty detail is empty");
                                    call.failure(context.getString(R.string.error_in_qty_details));
                                    return;
                                }

                                if (result.DistributionCustomerCallOrderLineBatchQtyDetails != null && result.DistributionCustomerCallOrderLineBatchQtyDetails.size() > 0) {
                                    CallInvoiceLineBatchQtyDetailManager CallInvoiceLineBatchQtyDetailManager = new CallInvoiceLineBatchQtyDetailManager(context);
                                    CallInvoiceLineBatchQtyDetailManager.deleteAll();
                                    Set<UUID> batchProducts = new HashSet<>();
                                    Map<String, ProductBatchOnHandQtyModel> batchOnHandQtys = new HashMap<>();
                                    for (InvoiceLineBatchQtyDetailViewModel batchLine :
                                            result.DistributionCustomerCallOrderLineBatchQtyDetails) {
                                        CallInvoiceLineModel invoiceLineModel = Linq.findFirst(result.DistributionCustomerCallOrderLines, item -> item.UniqueId.equals(batchLine.OrderLineUniqueId));
                                        String key = batchLine.BatchNo + "_" + invoiceLineModel.ProductUniqueId;
                                        ProductBatchOnHandQtyModel bq = batchOnHandQtys.get(key);
                                        if (bq == null) {
                                            bq = new ProductBatchOnHandQtyModel();
                                            bq.UniqueId = UUID.randomUUID();
                                            bq.ProductId = invoiceLineModel.ProductUniqueId;
                                            bq.BatchNo = batchLine.BatchNo;
                                            bq.BatchRef = batchLine.BatchRef;
                                            bq.ExpDate = batchLine.ExpDate == null ? " " : batchLine.ExpDate;
                                            bq.OnHandQty = BigDecimal.ZERO;
                                        }
                                        bq.OnHandQty = bq.OnHandQty.add(batchLine.Qty);
                                        batchOnHandQtys.put(key, bq);
                                        batchProducts.add(invoiceLineModel.ProductUniqueId);


                                        CallInvoiceLineBatchQtyDetailModel callInvoiceLineBatchQtyDetailModel = new CallInvoiceLineBatchQtyDetailModel();
                                        callInvoiceLineBatchQtyDetailModel.UniqueId = UUID.randomUUID();
                                        callInvoiceLineBatchQtyDetailModel.BatchNo = batchLine.BatchNo;
                                        callInvoiceLineBatchQtyDetailModel.CustomerCallOrderLineUniqueId = invoiceLineModel.UniqueId;
                                        callInvoiceLineBatchQtyDetailModel.Qty = batchLine.Qty;
                                        callInvoiceLineBatchQtyDetailModel.BatchRef = batchLine.BatchRef;
                                        CallInvoiceLineBatchQtyDetailManager.insert(callInvoiceLineBatchQtyDetailModel);
                                    }

                                    ProductBatchOnHandQtyManager productBatchOnHandQtyManager = new ProductBatchOnHandQtyManager(context);
                                    productBatchOnHandQtyManager.deleteAll();
                                    productBatchOnHandQtyManager.insert(batchOnHandQtys.values());
                                    OnHandQtyManager onHandQtyManager = new OnHandQtyManager(context);
                                    List<OnHandQtyModel> onHandQtyModels = onHandQtyManager.getAll();
                                    List<OnHandQtyModel> toUpdateOnHandQtys = new ArrayList<>();
                                    for (OnHandQtyModel onHandQtyModel :
                                            onHandQtyModels) {
                                        if (batchProducts.contains(onHandQtyModel.ProductId)) {
                                            onHandQtyModel.IsBatch = true;
                                            toUpdateOnHandQtys.add(onHandQtyModel);
                                        }
                                    }
                                    if (toUpdateOnHandQtys.size() > 0)
                                        onHandQtyManager.update(toUpdateOnHandQtys);

                                }

                            } else {
                                Timber.d("customer call order lines is empty");
                                call.failure(context.getString(R.string.order_line_items_empty));
                                return;
                            }
                        } else {
                            Timber.d("customer call order is empty");
                        }


                        // populate customer call returns
                        if (result.DistributionCustomerCallReturns != null && result.DistributionCustomerCallReturns.size() > 0) {
                            for (CustomerCallReturnRequestModel requestModel :
                                    result.DistributionCustomerCallReturns) {
                                requestModel.IsFromRequest = true;
                            }
                            long numberOfReturns = customerCallReturnRequestManager.insert(result.DistributionCustomerCallReturns);
                            Timber.d("Number of rows inserted for return call = " + numberOfReturns);

                            if (result.DistributionCustomerCallReturnLines != null && result.DistributionCustomerCallReturnLines.size() > 0) {
                                returnLinesRequestManager.insert(result.DistributionCustomerCallReturnLines);

                                if (result.DistributionCustomerCallReturnLineRequestQtyDetails != null && result.DistributionCustomerCallReturnLineRequestQtyDetails.size() > 0) {
                                    returnLineQtyRequestManager.insert(result.DistributionCustomerCallReturnLineRequestQtyDetails);
                                } else {
                                    Timber.d("customer call return lines qty detail is empty");
                                    call.failure(context.getString(R.string.error_in_qty_details));
                                    return;
                                }
                            } else {
                                Timber.d("customer call return lines is empty");
                                call.failure(context.getString(R.string.return_line_items_empty));
                                return;
                            }


                        } else {
                            Timber.d("customer call return is empty");
                        }

                    } else {
                        Timber.d("customer calls is empty");
                    }


                    call.success();

                } catch (ValidationException e) {
                    Timber.e(e);
                    call.failure(context.getString(R.string.data_validation_failed));
                } catch (DbException e) {
                    Timber.e(e);
                    call.failure(context.getString(R.string.data_error));
                } catch (IOException e) {
                    Timber.e(e);
                    call.failure(context.getString(R.string.error_saving_tour_file));
                    e.printStackTrace();
                }

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t, "Failed to download customer old invoice");
                call.failure(context.getString(R.string.network_error));
            }
        });
    }
}
