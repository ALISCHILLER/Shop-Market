package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.TableMap;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerPaymentTypesViewManager;
import com.varanegar.vaslibrary.manager.DistributionCustomerPriceManager;
import com.varanegar.vaslibrary.manager.InvoiceLineQtyManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ValidPayTypeManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.customercardex.CustomerCardexCreditModel;
import com.varanegar.vaslibrary.manager.customercardex.CustomerCardexManager;
import com.varanegar.vaslibrary.manager.customerpricemanager.CustomerPriceManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetail;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.CallInvoiceLineBatchQtyDetailModelRepository;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetailModelRepository;
import com.varanegar.vaslibrary.model.CustomerPaymentTypesView.CustomerPaymentTypesViewModel;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.call.CallInvoiceLineModel;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrder;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModelRepository;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customerprice.CustomerPrice;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerPrice;
import com.varanegar.vaslibrary.model.distribution.DistributionCustomerPriceModel;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQtyModel;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 3/29/2017.
 */

public class CustomerCallOrderManager extends BaseManager<CustomerCallOrderModel> {
    public CustomerCallOrderManager(Context context) {
        super(context, new CustomerCallOrderModelRepository());
    }

    public List<CustomerCallOrderModel> getCustomerCallOrders(UUID customerUniqueId) {
        Query query = new Query();
        query.from(CustomerCallOrder.CustomerCallOrderTbl)
                .whereAnd(Criteria.equals(CustomerCallOrder.CustomerUniqueId,
                        customerUniqueId.toString()));
        return getItems(query);
    }

    public CustomerCallOrderModel getCustomerCallOrder(UUID customerUniqueId, UUID orderId) {
        Query query = new Query();
        query.from(CustomerCallOrder.CustomerCallOrderTbl).whereAnd(Criteria.equals(CustomerCallOrder.CustomerUniqueId, customerUniqueId.toString())
                .and(Criteria.equals(CustomerCallOrder.UniqueId, orderId.toString())));
        return getItem(query);
    }

    public void cancelCustomerOrders(@NonNull final UUID customerId) throws DbException, ValidationException {
        List<CustomerCallOrderModel> orderModels = getCustomerCallOrders(customerId);
        if (orderModels.size() > 0) {
            delete(Criteria.equals(CustomerCallOrder.CustomerUniqueId, customerId.toString()));
            final CustomerCallManager callManager = new CustomerCallManager(getContext());
            callManager.removeCall(CustomerCallType.SaveOrderRequest, customerId);
            PaymentManager paymentManager = new PaymentManager(getContext());
            paymentManager.deleteAllPayments(customerId);
        }
    }

    public void cancelCustomerOrder(@NonNull final UUID customerId, @NonNull final UUID callOrderId) throws DbException, ValidationException {
        final CustomerCallOrderModel orderModel = getItem(callOrderId);
        if (orderModel != null) {
            delete(Criteria.equals(CustomerCallOrder.UniqueId, callOrderId.toString()));
            CustomerCallManager callManager = new CustomerCallManager(getContext());
            callManager.removeCallOrder(customerId, callOrderId);
        }
    }


    public CustomerCallOrderModel addOrder(final UUID customerId) throws ValidationException, DbException, UnknownBackOfficeException {
        Query query = new Query().from(CustomerCallOrder.CustomerCallOrderTbl).orderByDescending(CustomerCallOrder.RowNo);
        CustomerCallOrderModel lastCallOrderModel = getItem(query);
        int rowNo = lastCallOrderModel == null ? 0 : lastCallOrderModel.RowNo;
        final CustomerCallOrderModel customerCallOrderModel = new CustomerCallOrderModel();
        customerCallOrderModel.UniqueId = UUID.randomUUID();
        customerCallOrderModel.CustomerUniqueId = customerId;
        customerCallOrderModel.SaleDate = new Date();
        customerCallOrderModel.StartTime = new Date();
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel saleOfficeRef = sysConfigManager.read(ConfigKey.SaleOfficeRef, SysConfigManager.cloud);
        customerCallOrderModel.SaleOfficeRefSDS = Integer.parseInt(saleOfficeRef.Value);
        customerCallOrderModel.RowNo = rowNo + 1;
        customerCallOrderModel.LocalPaperNo = generateRefNumber(rowNo + 1);
        insert(customerCallOrderModel);
        return customerCallOrderModel;
    }

    public String checkCustomerCredits(List<RequestReportViewModel> requestReportViewModels, CustomerModel customer, SysConfigModel orderBedLimit, SysConfigModel orderAsnLimit) {
        if (customer.InitCredit == null)
            customer.InitCredit = Currency.ZERO;
        if (customer.RemainCredit == null)
            customer.RemainCredit = Currency.ZERO;
        if (customer.InitDebit == null)
            customer.InitDebit = Currency.ZERO;
        if (customer.RemainDebit == null)
            customer.RemainDebit = Currency.ZERO;

        String errorMessage = "";
        if (requestReportViewModels != null && requestReportViewModels.size() > 0) {

            //  nestle NotDueDate
            CustomerCardexManager customerCardexManager = new CustomerCardexManager(getContext());
            CustomerCardexCreditModel customerCardexCreditModel = customerCardexManager.getCustomerValidSumDebAndCredit(customer.UniqueId);
            if (customerCardexCreditModel != null && customerCardexCreditModel.BedAmount != null && customerCardexCreditModel.BesAmount != null) {
                Currency cardextCreditCompares = customerCardexCreditModel.BedAmount.subtract(customerCardexCreditModel.BesAmount);
                if (cardextCreditCompares.compareTo(Currency.ZERO) == 1) {
                    errorMessage = getString(R.string.customer_mablagh) + cardextCreditCompares + getString(R.string.has_debit) + "\n";
                }
            }
            //  nestle NotDueDate end
            Currency TotalOrderNetAmount = Currency.ZERO;
//            boolean checkCredit, chekDebit;
//            PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(getContext());
            // TODO Add from payment type
            boolean checkCredit = false;
            boolean checkDebit = false;
            CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
            List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customer.UniqueId);
            ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
            List<PaymentTypeOrderModel> paymentTypeOrderModels = validPayTypeManager.getPaymentTypeCustomerCallOrders(customerCallOrderModels);

            for (PaymentTypeOrderModel paymentTypeOrderModel : paymentTypeOrderModels) {
                checkCredit |= paymentTypeOrderModel.CheckCredit;
                checkDebit |= paymentTypeOrderModel.CheckDebit;
            }

            for (RequestReportViewModel requestReportViewModel : requestReportViewModels) {
                TotalOrderNetAmount = TotalOrderNetAmount.add(requestReportViewModel.TotalOrderNetAmount);
            }
            try {
                BackOfficeType backOfficeType = new SysConfigManager(getContext()).getBackOfficeType();
                if (backOfficeType == BackOfficeType.ThirdParty) {
                    if (!(customer.InitCredit.compareTo(Currency.ZERO) == 0) && TotalOrderNetAmount.compareTo(customer.RemainCredit) > 0) {
                        errorMessage = errorMessage + getString(R.string.debit_remain_credit) + customer.RemainCredit + "\n" + getString(R.string.this_order_amount) + TotalOrderNetAmount + "\n" + getString(R.string.credit_deficit) + TotalOrderNetAmount.subtract(customer.RemainCredit);
                    }
                } else {
                    if (!SysConfigManager.compare(orderBedLimit, "0") && !SysConfigManager.compare(orderAsnLimit, "0") && checkCredit && checkDebit && !(customer.InitCredit.compareTo(Currency.ZERO) == 0) && !(customer.InitDebit.compareTo(Currency.ZERO) == 0) && TotalOrderNetAmount.compareTo(customer.RemainCredit.add(customer.RemainDebit)) == 1) {
                        errorMessage = errorMessage + getString(R.string.debit_and_creidt_remain) + (customer.RemainDebit).add(customer.RemainCredit) + "\n" + getString(R.string.this_order_amount) + TotalOrderNetAmount + "\n" + getString(R.string.credit_deficit) + TotalOrderNetAmount.subtract(customer.RemainDebit.add(customer.RemainCredit));
                    } else if (!SysConfigManager.compare(orderBedLimit, "0") && checkCredit && !checkDebit && !(customer.InitCredit.compareTo(Currency.ZERO) == 0) && TotalOrderNetAmount.compareTo(customer.RemainCredit) == 1) {
                        errorMessage = errorMessage + getString(R.string.debit_remain_credit) + customer.RemainCredit + "\n" + getString(R.string.this_order_amount) + TotalOrderNetAmount + "\n" + getString(R.string.credit_deficit) + TotalOrderNetAmount.subtract(customer.RemainCredit);
                    } else if (!SysConfigManager.compare(orderAsnLimit, "0") && checkDebit && !checkCredit && !(customer.InitDebit.compareTo(Currency.ZERO) == 0) && TotalOrderNetAmount.compareTo(customer.RemainDebit) == 1) {
                        errorMessage = errorMessage + getString(R.string.credit_remain_credit) + customer.RemainDebit + "\n" + getString(R.string.this_order_amount) + TotalOrderNetAmount + "\n" + getString(R.string.credit_deficit) + TotalOrderNetAmount.subtract(customer.RemainDebit);
                    }
                }
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
            }
        }
        return errorMessage;
    }

    private String getString(@StringRes int resId) {
        return getContext().getString(resId);
    }

    @Nullable
    private String generateRefNumber(int callRowNo) throws UnknownBackOfficeException {
        String dateString = DateHelper.toString(new Date(), DateFormat.Simple, VasHelperMethods.getSysConfigLocale(getContext()));
        TourModel tour = new TourManager(getContext()).loadTour();
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        BackOfficeType backOfficeType = null;
        backOfficeType = sysConfigManager.getBackOfficeType();
        if (tour != null && backOfficeType == BackOfficeType.ThirdParty) {
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.DealerCode, SysConfigManager.cloud);
            if (sysConfigModel != null)
                return sysConfigModel.Value + "-" + tour.TourNo + "-" + callRowNo;
            else
                return null;
        } else if (tour != null)
            return dateString + "-" + tour.TourNo + "-" + callRowNo;
        else
            return null;
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void initCall(@NonNull UUID callOrderId, boolean isReturn) throws OrderInitException {
        CustomerCallInvoiceManager callInvoiceManager = new CustomerCallInvoiceManager(getContext());
        final CustomerCallInvoiceModel callInvoiceModel = callInvoiceManager.getCustomerCallInvoice(callOrderId);
        if (callInvoiceModel == null)
            throw new OrderInitException(getContext().getString(R.string.order_id_not_found));


        try {
            List<CustomerPaymentTypesViewModel> customerPaymentTypes = new CustomerPaymentTypesViewManager(getContext()).getCustomerPaymentType(callInvoiceModel.CustomerUniqueId);
            if (!Linq.exists(customerPaymentTypes, new Linq.Criteria<CustomerPaymentTypesViewModel>() {
                @Override
                public boolean run(CustomerPaymentTypesViewModel item) {
                    return item.UniqueId.equals(callInvoiceModel.OrderPaymentTypeUniqueId);
                }
            })) {
                throw new OrderInitException(getContext().getString(R.string.order_type_payment_id_is_not_available_for_this_customer));
            }
            CallInvoiceLineManager callInvoiceLineManager = new CallInvoiceLineManager(getContext());
            List<CallInvoiceLineModel> lines = callInvoiceLineManager.getLines(callOrderId);
            if (lines.size() == 0)
                throw new OrderInitException(getContext().getString(R.string.order_line_items_empty));


            CustomerCallOrderModel old = getItem(callOrderId);
            delete(callOrderId);
            CustomerCallInvoiceModel invoice = new CustomerCallInvoiceManager(getContext()).getCustomerCallInvoice(callOrderId);
            CustomerCallOrderModel orderModel = invoice.convertInvoiceToOrderModel();
            if (old != null)
                orderModel.StartTime = old.StartTime;
            else
                orderModel.StartTime = new Date();
            orderModel.EndTime = new Date();
            insert(orderModel);
            Timber.i("Customer call order init ");


//            TableMap map2 = new TableMap(CallInvoiceLine.CallInvoiceLineTbl, CallOrderLine.CallOrderLineTbl);
//            map2.addAllColumns();
//            insert(map2, Criteria.equals(CallInvoiceLine.OrderUniqueId, callOrderId.toString()));
//            Timber.i("Customer call order line init ");


            for (CallInvoiceLineModel line :
                    lines) {
                ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
                List<ProductUnitViewModel> units = productUnitViewManager.getProductUnits(line.ProductUniqueId, ProductType.isForSale);

                if (line.UniqueId == null)
                    throw new OrderInitException(getContext().getString(R.string.error_in_qty_details));

                CallOrderLineModel orderLineModel = line.convertToCallOrderLine();
                if (isReturn) {
                    orderLineModel.RequestAdd1Amount = Currency.ZERO;
                    orderLineModel.RequestAdd2Amount = Currency.ZERO;
                    orderLineModel.RequestOtherAddAmount = Currency.ZERO;
                    orderLineModel.RequestTaxAmount = Currency.ZERO;
                    orderLineModel.RequestChargeAmount = Currency.ZERO;
                    orderLineModel.RequestDis1Amount = Currency.ZERO;
                    orderLineModel.RequestDis2Amount = Currency.ZERO;
                    orderLineModel.RequestDis3Amount = Currency.ZERO;
                    orderLineModel.RequestOtherDiscountAmount = Currency.ZERO;
                    orderLineModel.PromotionPrice = Currency.ZERO;
                } else if (orderLineModel.IsPromoLine)
                    orderLineModel.PromotionPrice = line.RequestAmount;
                CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                callOrderLineManager.insertOrUpdate(orderLineModel);


                InvoiceLineQtyManager invoiceLineQtyManager = new InvoiceLineQtyManager(getContext());
                List<InvoiceLineQtyModel> qtys = invoiceLineQtyManager.getQtyLines(line.UniqueId);
                List<OrderLineQtyModel> orderLineQtyModelList = new ArrayList<>();
                for (InvoiceLineQtyModel qty :
                        qtys) {
                    final OrderLineQtyModel orderLineQtyModel = new OrderLineQtyModel();
                    orderLineQtyModel.Qty = isReturn ? BigDecimal.ZERO : qty.Qty;
                    orderLineQtyModel.ProductUnitId = qty.ProductUnitId;
                    orderLineQtyModel.OrderLineUniqueId = qty.OrderLineUniqueId;
                    orderLineQtyModel.UniqueId = qty.UniqueId;
                    orderLineQtyModelList.add(orderLineQtyModel);
                }
                if (orderLineQtyModelList.size() > 0) {
                    OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getContext());
                    orderLineQtyManager.insert(orderLineQtyModelList);
                    Timber.i("Customer call order line qty details init ");
                } else
                    throw new OrderInitException(getContext().getString(R.string.no_qty_details));


                CallInvoiceLineBatchQtyDetailModelRepository invoiceBatchRep = new CallInvoiceLineBatchQtyDetailModelRepository();
                List<CallInvoiceLineBatchQtyDetailModel> batchQtyDetailModels = invoiceBatchRep.getItems(new Query().from(CallInvoiceLineBatchQtyDetail.CallInvoiceLineBatchQtyDetailTbl)
                        .whereAnd(Criteria.equals(CallInvoiceLineBatchQtyDetail.CustomerCallOrderLineUniqueId, line.UniqueId)));

                List<CallOrderLineBatchQtyDetailModel> orderBatchQtyDetailModels = new ArrayList<>();
                for (CallInvoiceLineBatchQtyDetailModel batchQtyDetailModel :
                        batchQtyDetailModels) {
                    CallOrderLineBatchQtyDetailModel orderBatchQty = new CallOrderLineBatchQtyDetailModel();
                    orderBatchQty.BatchNo = batchQtyDetailModel.BatchNo;
                    orderBatchQty.BatchRef = batchQtyDetailModel.BatchRef;
                    orderBatchQty.CustomerCallOrderLineUniqueId = batchQtyDetailModel.CustomerCallOrderLineUniqueId;
                    orderBatchQty.ItemRef = batchQtyDetailModel.ItemRef;
                    orderBatchQty.Qty = batchQtyDetailModel.Qty;
                    orderBatchQty.UniqueId = batchQtyDetailModel.UniqueId;
                    orderBatchQtyDetailModels.add(orderBatchQty);
                }
                CallOrderLineBatchQtyDetailModelRepository orderBatchRep = new CallOrderLineBatchQtyDetailModelRepository();
                if(orderBatchQtyDetailModels.size()>0)
                orderBatchRep.insert(orderBatchQtyDetailModels);
                Timber.i("Customer call order batch qty details init ");

            }


            TableMap map4 = new TableMap(DistributionCustomerPrice.DistributionCustomerPriceTbl, CustomerPrice.CustomerPriceTbl);
            map4.addAllColumns();
            new CustomerPriceManager(getContext()).delete(Criteria.equals(CustomerPrice.CallOrderId, callOrderId.toString()));
            insert(map4, Criteria.equals(DistributionCustomerPrice.CallOrderId, callOrderId.toString()));
            Timber.i("Customer call order price init ");

        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            throw new OrderInitException(getContext().getString(R.string.back_office_type_is_uknown));
        } catch (DbException e) {
            Timber.e(e);
            throw new OrderInitException(getContext().getString(R.string.order_line_items_empty));
        } catch (ValidationException e) {
            Timber.e(e);
            throw new OrderInitException(getContext().getString(R.string.error_saving_request));
        } catch (ProductUnitViewManager.UnitNotFoundException e) {
            Timber.e(e);
            throw new OrderInitException(getContext().getString(R.string.no_unit_for_product));
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public DistOrderStatus getDistStatus(UUID customerId, UUID callOrderId) {
        CustomerCallOrderModel customerCallOrderModel = new CustomerCallOrderManager(getContext()).getCustomerCallOrder(customerId, callOrderId);
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
        List<CustomerCallOrderOrderViewModel> lines = customerCallOrderOrderViewManager.getLines(callOrderId, null);
        CustomerCallInvoiceManager callInvoiceManager = new CustomerCallInvoiceManager(getContext());
        CustomerCallInvoiceModel callInvoiceModel = callInvoiceManager.getCustomerCallInvoice(callOrderId);
        boolean isPaymentTypeChanged = !callInvoiceModel.OrderPaymentTypeUniqueId.equals(customerCallOrderModel.OrderPaymentTypeUniqueId);
        boolean isPartiallyDelivered = false;
        boolean isAllLinesZero = true;
        for (CustomerCallOrderOrderViewModel line :
                lines) {
            if (line.TotalQty.compareTo(BigDecimal.ZERO) != 0 && !line.IsPromoLine && line.cart.isEmpty())
                isAllLinesZero = false;
            if (line.TotalQty.compareTo(line.OriginalTotalQty) != 0) {
                isPartiallyDelivered = true;
            }
        }
        DistOrderStatus orderStatus = new DistOrderStatus();
        if (isAllLinesZero)
            orderStatus.NotDelivered = true;
        if (isPaymentTypeChanged)
            orderStatus.PaymentTypeChanged = true;
        if (isPartiallyDelivered && !isAllLinesZero)
            orderStatus.PartiallyDelivered = true;
        if (!isPartiallyDelivered && !isAllLinesZero)
            orderStatus.Delivered = true;
        return orderStatus;
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void initCustomerPrices(@NonNull UUID customerId) throws DbException {
        new CustomerPriceManager(getContext()).delete(Criteria.equals(CustomerPrice.CustomerUniqueId, customerId.toString()));
        TableMap map4 = new TableMap(DistributionCustomerPrice.DistributionCustomerPriceTbl, CustomerPrice.CustomerPriceTbl);
        map4.addAllColumns();
        insert(map4, Criteria.equals(DistributionCustomerPrice.CustomerUniqueId, customerId.toString()));
        Timber.i("Customer price init finished");
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public List<CustomerPriceModel> initCallPrices(UUID callOrderId) {
        DistributionCustomerPriceManager manager = new DistributionCustomerPriceManager(getContext());
        List<DistributionCustomerPriceModel> prices = manager.getItems(new Query().from(DistributionCustomerPrice.DistributionCustomerPriceTbl).whereAnd(Criteria.equals(DistributionCustomerPrice.CallOrderId, callOrderId.toString())));
        List<CustomerPriceModel> priceModels = new ArrayList<>();
        for (DistributionCustomerPriceModel distributionCustomerPriceModel :
                prices) {
            CustomerPriceModel customerPriceModel = new CustomerPriceModel();
            customerPriceModel.CallOrderId = callOrderId;
            customerPriceModel.UniqueId = distributionCustomerPriceModel.UniqueId;
            customerPriceModel.CustomerUniqueId = distributionCustomerPriceModel.CustomerUniqueId;
            customerPriceModel.UserPrice = distributionCustomerPriceModel.UserPrice;
            customerPriceModel.Price = distributionCustomerPriceModel.Price;
            customerPriceModel.ProductUniqueId = distributionCustomerPriceModel.ProductUniqueId;
            customerPriceModel.PriceId = distributionCustomerPriceModel.PriceId;
            priceModels.add(customerPriceModel);
        }
        return priceModels;
    }

    public boolean isDataSent(UUID callOrderId) {
        CustomerCallOrderModel callOrderModel = getItem(callOrderId);
        return callOrderModel != null && callOrderModel.IsSent;
    }

    public List<CustomerCallOrderModel> getAllOrders() {
        return getItems(new Query().from(CustomerCallOrder.CustomerCallOrderTbl));
    }
}






