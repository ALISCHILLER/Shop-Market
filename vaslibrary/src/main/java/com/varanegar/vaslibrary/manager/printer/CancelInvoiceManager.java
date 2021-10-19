package com.varanegar.vaslibrary.manager.printer;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.printer.CancelInvoice;
import com.varanegar.vaslibrary.model.printer.CancelInvoiceModel;
import com.varanegar.vaslibrary.model.printer.CancelInvoiceModelRepository;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.webapi.tour.SyncGetCancelInvoiceViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by g.aliakbar on 09/04/2018.
 */

public class CancelInvoiceManager extends BaseManager<CancelInvoiceModel> {


    public CancelInvoiceManager(Context context) {
        super(context, new CancelInvoiceModelRepository());
    }

    public List<CancelInvoiceModel> getCancelInvoiceModel(UUID customerId) {
        Query query = new Query();
        query.from(CancelInvoice.CancelInvoiceTbl).whereAnd(Criteria.equals(CancelInvoice.CustomerUniqueId, customerId.toString()));
        return getItems(query);
    }


    @Nullable
    public List<SyncGetCancelInvoiceViewModel> getCancelInvoiceViewModel(@NonNull UUID customerId) {

        List<CancelInvoiceModel> cancelInvoiceList = getCancelInvoiceModel(customerId);
        if (cancelInvoiceList.size() > 0) {
            List<SyncGetCancelInvoiceViewModel> cancelInvoiceViewModels = new ArrayList<>();
            for (int i = 0; i < cancelInvoiceList.size(); i++) {
                SyncGetCancelInvoiceViewModel viewModel = new SyncGetCancelInvoiceViewModel();
                viewModel.TourUniqueId = cancelInvoiceList.get(i).TourUniqueId;
                viewModel.Amount = cancelInvoiceList.get(i).Amount;
                viewModel.Comment = cancelInvoiceList.get(i).Comment;
                viewModel.CustomerUniqueId = cancelInvoiceList.get(i).CustomerUniqueId;
                cancelInvoiceViewModels.add(viewModel);
            }
            return cancelInvoiceViewModels;
        }
        return null;
    }

    public void addCancelInvoice(@NonNull UUID customerID) throws ValidationException, DbException {

        TourManager tourManager = new TourManager(getContext());
        TourModel tourModel = tourManager.loadTour();

        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
        Currency invoiceAmount = customerCallOrderOrderViewManager.calculateTotalAmountOfAllOrders(customerID, false);

        PaymentManager paymentManager = new PaymentManager(getContext());
        Currency paymentAmount = paymentManager.getTotalPaid(customerID);

        CancelInvoiceModel cancelInvoiceModel = new CancelInvoiceModel();
        if (tourModel != null) {
            cancelInvoiceModel.TourUniqueId = tourModel.UniqueId;
        }
        cancelInvoiceModel.CustomerUniqueId = customerID;
        cancelInvoiceModel.Comment = getContext().getString(R.string.customer_invoice_with_amount) + " " + invoiceAmount.doubleValue() + " " + getContext().getString(R.string.with_payed) + paymentAmount.doubleValue() + " " + getContext().getString(R.string.has_caceled);
        cancelInvoiceModel.Amount = invoiceAmount.doubleValue();
        cancelInvoiceModel.UniqueId = UUID.randomUUID();
        insert(cancelInvoiceModel);
    }
}
