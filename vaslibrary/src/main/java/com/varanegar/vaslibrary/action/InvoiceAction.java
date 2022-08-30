package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.CustomerPaymentTypesViewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoicePreviewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderPreviewManager;
import com.varanegar.vaslibrary.manager.customercall.OrderInitException;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.model.CustomerPaymentTypesView.CustomerPaymentTypesViewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoicePreviewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.ui.fragment.order.CustomerSaveOrderFragment;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 1/14/2018.
 */

public class InvoiceAction extends CheckDistanceAction {

    private UUID CustomerLevelId;

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("c81c3571-6f8f-4a53-bb64-4742fbf64f3a");
    }

    public InvoiceAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId, UUID CustomerLevelId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_thumb_up_white_24dp;
        this.CustomerLevelId = CustomerLevelId;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        List<CustomerCallInvoiceModel> invoices = ((VasActionsAdapter) getAdapter()).getInvoices();
        return getCallManager().checkCustomerDistributionStatus(invoices, getCalls());
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.delivery_of_order);
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (canNotEditOperationAfterPrint())
            return getActivity().getString(R.string.can_not_edit_customer_operation_after_print);

        boolean dataSent = getCallManager().isDataSent(getCalls(), null);
        if (dataSent) {
            List<CustomerCallOrderPreviewModel> orderModels = new CustomerCallOrderPreviewManager(getActivity()).getCustomerCallOrders(getSelectedId());
            if (orderModels.size() == 0)
                return getActivity().getString(R.string.no_order_and_data_is_sent);
        }

        if (!getCustomer().IsActive && !(getCustomer().IsNewCustomer))
            return getActivity().getString(R.string.the_customer_is_disabled);

        return null;
    }

    @Override
    public boolean isDone() {
        List<CustomerCallModel> callModels = ((VasActionsAdapter) getAdapter()).getCalls();
        boolean complete = Linq.exists(callModels, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.CompleteLackOfDelivery || item.CallType == CustomerCallType.CompleteReturnDelivery;
            }
        });
        if (complete)
            return false;


        List<CustomerCallModel> distCalls = Linq.findAll(callModels, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.OrderPartiallyDelivered ||
                        item.CallType == CustomerCallType.OrderReturn ||
                        item.CallType == CustomerCallType.OrderLackOfDelivery ||
                        item.CallType == CustomerCallType.OrderDelivered;
            }
        });

        List<CustomerCallInvoicePreviewModel> invoiceModels = new CustomerCallInvoicePreviewManager(getActivity()).getCustomerCallOrders(getSelectedId());
        return distCalls.size() > 0 && distCalls.size() == invoiceModels.size();
    }


    @Override
    public void run() {
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        List<CustomerCallModel> callModels = callManager.loadCalls(getSelectedId());
        if (!callManager.hasDistCall(callModels)) {
            CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getActivity());
            try {
                callOrderManager.initCustomerPrices(getSelectedId());
            } catch (DbException e) {
                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                dialog.setTitle(R.string.error);
                dialog.setIcon(Icon.Error);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.setMessage(R.string.error_saving_request);
                dialog.show();
                Timber.e(e);
                return;
            }
        }
        CustomerOrderTypesManager customerOrderTypesManager = new CustomerOrderTypesManager(getActivity());
        List<CustomerOrderTypeModel> customerOrderTypeModels = customerOrderTypesManager.getItems();
        CustomerPaymentTypesViewManager customerPaymentTypesViewManager = new CustomerPaymentTypesViewManager(getActivity());
        try {
            List<CustomerPaymentTypesViewModel> customerPaymentTypes = customerPaymentTypesViewManager.getCustomerPaymentType(getSelectedId());
            if (customerOrderTypeModels.size() > 0 && customerPaymentTypes.size() > 0) {
                CustomerCallInvoicePreviewManager invoiceManager = new CustomerCallInvoicePreviewManager(getActivity());
                List<CustomerCallInvoicePreviewModel> invoiceModels = invoiceManager.getCustomerCallOrders(getSelectedId());
                if (invoiceModels.size() == 0) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(R.string.customer_has_no_order);
                    dialog.setTitle(R.string.error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                } else if (invoiceModels.size() == 1) {
                    addCustomerCallOrder(invoiceModels.get(0).UniqueId);
                } else {
                    selectOrder(getSelectedId(), invoiceModels);
                }
            } else {
                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                dialog.setTitle(R.string.error);
                dialog.setIcon(Icon.Error);
                dialog.setPositiveButton(R.string.ok, null);
                if (customerOrderTypeModels.size() == 0 && customerPaymentTypes.size() == 0)
                    dialog.setMessage(R.string.no_payment_type_no_order_type);
                else if (customerOrderTypeModels.size() == 0 && customerPaymentTypes.size() > 0)
                    dialog.setMessage(R.string.no_order_type);
                else if (customerOrderTypeModels.size() > 0 && customerPaymentTypes.size() == 0)
                    dialog.setMessage(R.string.no_payment_type);
                dialog.show();
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setMessage(R.string.back_office_type_is_uknown);
            dialog.show();
        }

    }

    private void addCustomerCallOrder(final UUID callOrderId) {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getActivity());
        try {
            CustomerCallOrderModel callOrderModel = callOrderManager.getItem(callOrderId);
            if (callOrderModel == null) {
                new CustomerCallManager(getActivity()).removeCalls(getSelectedId(), CustomerCallType.CompleteLackOfDelivery, CustomerCallType.CompleteReturnDelivery);
                callOrderManager.initCall(callOrderId, false);
            }
            CustomerSaveOrderFragment saveOrderFragment = new CustomerSaveOrderFragment();
            saveOrderFragment.setArguments(getSelectedId(), callOrderId, CustomerLevelId);
            getActivity().pushFragment(saveOrderFragment);
        } catch (OrderInitException ex) {
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setIcon(Icon.Error);
            dialog.setMessage(ex.getMessage());
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception ex) {
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setIcon(Icon.Error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void selectOrder(final UUID customerId, List<CustomerCallInvoicePreviewModel> callOrders) {
        InvoiceSelectionDialog dialog = new InvoiceSelectionDialog();
        dialog.setOrders(callOrders);
        dialog.onOrderSelected = new InvoiceSelectionDialog.OnOrderSelected() {
            @Override
            public void run(UUID orderId) {
                if (orderId != null) {
                    addCustomerCallOrder(orderId);
                }
            }
        };
        dialog.show(getActivity().getSupportFragmentManager(), "OrderSelectionDialog");
    }

}
