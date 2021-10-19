package com.varanegar.vaslibrary.manager.invoiceinfo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.ColumnProjection;
import com.varanegar.framework.util.Linq;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoView;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoViewModel;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoViewModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/24/2018.
 */

public class InvoiceInfoViewManager extends BaseManager<InvoicePaymentInfoViewModel> {
    public InvoiceInfoViewManager(@NonNull Context context) {
        super(context, new InvoicePaymentInfoViewModelRepository());
    }

    /**
     * Returns list of  oprn invoices and filters current call orders
     *
     * @param customerId
     * @param paymentId
     * @return
     */
    public List<InvoicePaymentInfoViewModel> getOldInvoices(@NonNull UUID customerId, @Nullable UUID paymentId) {
        Query query = new Query().from(InvoicePaymentInfoView.InvoicePaymentInfoViewTbl)
                .whereAnd(Criteria.equals(InvoicePaymentInfoView.CustomerId, customerId.toString())
                        .and(Criteria.equals(ColumnProjection.column(InvoicePaymentInfoView.IsOldInvoice).castAsBoolean(), true)))
                .groupBy(ColumnProjection.column(InvoicePaymentInfoView.InvoiceId));
        List<InvoicePaymentInfoViewModel> list = getItems(query);
        Query query1 = new Query().from(InvoicePaymentInfoView.InvoicePaymentInfoViewTbl)
                .whereAnd(Criteria.equals(InvoicePaymentInfoView.CustomerId, customerId.toString())
                        .and(Criteria.equals(ColumnProjection.column(InvoicePaymentInfoView.IsOldInvoice).castAsBoolean(), true))
                        .and(Criteria.equals(InvoicePaymentInfoView.PaymentId, paymentId)));
        List<InvoicePaymentInfoViewModel> invoicePaymentInfoViewModels = getItems(query1);
        List<InvoicePaymentInfoViewModel> finalList = new ArrayList<>();
        for (final InvoicePaymentInfoViewModel item :
                list) {
            InvoicePaymentInfoViewModel invoicePaymentInfoViewModel = Linq.findFirst(invoicePaymentInfoViewModels, new Linq.Criteria<InvoicePaymentInfoViewModel>() {
                @Override
                public boolean run(InvoicePaymentInfoViewModel it) {
                    return it.InvoiceId.equals(item.InvoiceId);
                }
            });
            if (paymentId == null)
                item.PaidAmount = Currency.ZERO;
            if (invoicePaymentInfoViewModel == null)
                finalList.add(item);
            else
                finalList.add(invoicePaymentInfoViewModel);
        }
        return finalList;
    }

    /**
     * Returns list of call orders and filters open invoices
     *
     * @param customerId
     * @param paymentId
     * @return
     */
    public List<InvoicePaymentInfoViewModel> getCallOrders(@NonNull UUID customerId, @Nullable UUID paymentId) {
        Query query = new Query().from(InvoicePaymentInfoView.InvoicePaymentInfoViewTbl)
                .whereAnd(Criteria.equals(InvoicePaymentInfoView.CustomerId, customerId.toString())
                        .and(Criteria.equals(ColumnProjection.column(InvoicePaymentInfoView.IsOldInvoice).castAsBoolean(), false)))
                .groupBy(ColumnProjection.column(InvoicePaymentInfoView.InvoiceId));
        List<InvoicePaymentInfoViewModel> list = getItems(query);
        Query query1 = new Query().from(InvoicePaymentInfoView.InvoicePaymentInfoViewTbl)
                .whereAnd(Criteria.equals(InvoicePaymentInfoView.CustomerId, customerId.toString())
                        .and(Criteria.equals(ColumnProjection.column(InvoicePaymentInfoView.IsOldInvoice).castAsBoolean(), false))
                        .and(Criteria.equals(InvoicePaymentInfoView.PaymentId, paymentId)));
        List<InvoicePaymentInfoViewModel> invoicePaymentInfoViewModels = getItems(query1);
        List<InvoicePaymentInfoViewModel> finalList = new ArrayList<>();
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        List<CustomerCallModel> calls = callManager.loadCalls(customerId);
        for (final InvoicePaymentInfoViewModel item :
                list) {
            boolean add = true;
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                add = Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
                    @Override
                    public boolean run(CustomerCallModel callModel) {
                        return (callModel.CallType == CustomerCallType.OrderDelivered ||
                                callModel.CallType == CustomerCallType.OrderPartiallyDelivered)
                                && item.InvoiceId.toString().equals(callModel.ExtraField1);
                    }
                });

            if (add) {
                InvoicePaymentInfoViewModel invoicePaymentInfoViewModel = Linq.findFirst(invoicePaymentInfoViewModels, new Linq.Criteria<InvoicePaymentInfoViewModel>() {
                    @Override
                    public boolean run(InvoicePaymentInfoViewModel it) {
                        return it.InvoiceId.equals(item.InvoiceId);
                    }
                });
                if (paymentId == null)
                    item.PaidAmount = Currency.ZERO;
                if (invoicePaymentInfoViewModel == null)
                    finalList.add(item);
                else
                    finalList.add(invoicePaymentInfoViewModel);
            }
        }
        return finalList;
    }

    public List<InvoicePaymentInfoViewModel> getAll(@NonNull UUID customerId, @Nullable UUID paymentId) {
        Query query = new Query().from(InvoicePaymentInfoView.InvoicePaymentInfoViewTbl)
                .whereAnd(Criteria.equals(InvoicePaymentInfoView.CustomerId, customerId.toString()))
                .groupBy(ColumnProjection.column(InvoicePaymentInfoView.InvoiceId));
        List<InvoicePaymentInfoViewModel> list = getItems(query);

        Query query1 = new Query().from(InvoicePaymentInfoView.InvoicePaymentInfoViewTbl)
                .whereAnd(Criteria.equals(InvoicePaymentInfoView.CustomerId, customerId.toString())
                        .and(Criteria.equals(InvoicePaymentInfoView.PaymentId, paymentId)));
        List<InvoicePaymentInfoViewModel> invoicePaymentInfoViewModels = getItems(query1);

        List<InvoicePaymentInfoViewModel> finalList = new ArrayList<>();
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        List<CustomerCallModel> calls = callManager.loadCalls(customerId);
        for (final InvoicePaymentInfoViewModel item :
                list) {
            boolean add = true;
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && !item.IsOldInvoice) {
                add = Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
                    @Override
                    public boolean run(CustomerCallModel callModel) {
                        return (callModel.CallType == CustomerCallType.OrderDelivered ||
                                callModel.CallType == CustomerCallType.OrderPartiallyDelivered) &&
                                item.InvoiceId.toString().equals(callModel.ExtraField1);
                    }
                });
            }

            InvoicePaymentInfoViewModel invoicePaymentInfoViewModel = Linq.findFirst(invoicePaymentInfoViewModels, new Linq.Criteria<InvoicePaymentInfoViewModel>() {
                @Override
                public boolean run(InvoicePaymentInfoViewModel it) {
                    return it.InvoiceId.equals(item.InvoiceId);
                }
            });
            if (add) {
                if (paymentId == null)
                    item.PaidAmount = Currency.ZERO;
                if (invoicePaymentInfoViewModel == null)
                    finalList.add(item);
                else
                    finalList.add(invoicePaymentInfoViewModel);
            }
        }
        return finalList;
    }

    public List<InvoicePaymentInfoViewModel> getAll(@NonNull UUID paymentId) {
        return getItems(new Query().from(InvoicePaymentInfoView.InvoicePaymentInfoViewTbl)
                .whereAnd(Criteria.equals(InvoicePaymentInfoView.PaymentId, paymentId.toString())
                        .and(Criteria.greaterThan(ColumnProjection.column(InvoicePaymentInfoView.PaidAmount).castAsInt(), 0))));

    }

}
