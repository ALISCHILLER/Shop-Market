package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.CustomerPaymentTypesViewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderPreviewManager;
import com.varanegar.vaslibrary.manager.emphaticitems.CustomerEmphaticProductManager;
import com.varanegar.vaslibrary.manager.emphaticitems.CustomerEmphaticProductViewManager;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphaticItemNotFoundException;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.model.CustomerPaymentTypesView.CustomerPaymentTypesViewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.order.CustomerSaveOrderFragment;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 1/14/2018.
 */

public class SaveOrderAction extends CheckDistanceAction {

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("c81c3571-6f8f-4a53-bb64-4742fbf64f3a");
    }

    public SaveOrderAction(MainVaranegarActivity activity,
                           ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_thumb_up_white_24dp;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        boolean hasOrder = getCallManager().hasOrderOrReturnCall(getCalls());
        boolean isLackOfVisit = getCallManager().isLackOfVisit(getCalls());
        boolean isLackOfOrder = getCallManager().isLackOfOrder(getCalls());
        if (isLackOfOrder || isLackOfVisit || hasOrder)
            return null;
        else
            return getActivity().getString(R.string.customer_status_is_uknown);
    }

    @Override
    public String getName() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            return getActivity().getString(R.string.order_request);
        else
            return getActivity().getString(R.string.save_order);
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        boolean dataSent = getCallManager().isDataSent(getCalls(), null);
        List<CustomerCallOrderPreviewModel> orderModels = new
                CustomerCallOrderPreviewManager(getActivity()).getCustomerCallOrders(getSelectedId());
        if (dataSent && orderModels.size() == 0)
                return getActivity().getString(R.string.no_order_and_data_is_sent);

        if (orderModels.size() == 0 && canNotEditOperationAfterPrint())
            return getActivity().getString(R.string.can_not_edit_customer_operation_after_print);

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
            SysConfigModel inactiveCustomers = sysConfigManager
                    .read(ConfigKey.SendInactiveCustomers, SysConfigManager.cloud);
            SysConfigModel TakeOrderFromInactiveCustomers = sysConfigManager
                    .read(ConfigKey.TakeOrderFromInactiveCustomers, SysConfigManager.cloud);
            if(getCustomer().CodeNaghsh ==null){
                return getActivity().getString(R.string.the_customer_not_code);
            }
            if (!getCustomer().IsActive)
                return getActivity().getString(R.string.the_customer_is_disabled);
        } else {
            if (!getCustomer().IsActive && !(getCustomer().IsNewCustomer) &&
                    checkCloudConfig(ConfigKey.ScientificVisit, false))
                return getActivity().getString(R.string.the_customer_is_disabled);
        }
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel advancedCreditControlConfig = sysConfigManager
                .read(ConfigKey.AdvancedCreditControl, SysConfigManager.cloud);
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            if (SysConfigManager.compare(advancedCreditControlConfig, true) &&
                    getCustomer().ErrorMessage != null && !getCustomer().ErrorMessage.isEmpty())
                return getCustomer().ErrorMessage;
        } else {
            SysConfigModel sysConfigModel = sysConfigManager.
                    read(ConfigKey.AllowCashWithoutAdvancedCreditControl, SysConfigManager.cloud);
            if (SysConfigManager.compare(sysConfigModel, false) &&
                    SysConfigManager.compare(advancedCreditControlConfig, true) &&
                    getCustomer().ErrorMessage != null && !getCustomer().ErrorMessage.isEmpty())
                return getCustomer().ErrorMessage;
        }

        return null;
    }

    @Override
    public boolean isDone() {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getActivity());
        List<CustomerCallOrderModel> callOrderModels = callOrderManager.getCustomerCallOrders(getSelectedId());
        List<CustomerCallOrderModel> confirmedCallOrders = Linq.findAll(callOrderModels,
                new Linq.Criteria<CustomerCallOrderModel>() {
            @Override
            public boolean run(final CustomerCallOrderModel customerCallOrderModel) {
                return Linq.exists(getCalls(), new Linq.Criteria<CustomerCallModel>() {
                    @Override
                    public boolean run(CustomerCallModel customerCallModel) {
                        return customerCallOrderModel.UniqueId.toString()
                                .equals(customerCallModel.ExtraField1);
                    }
                });
            }
        });
        return callOrderModels.size() > 0 && confirmedCallOrders.size() == callOrderModels.size();
    }


    @Override
    public void run() {
        CustomerOrderTypesManager customerOrderTypesManager = new CustomerOrderTypesManager(getActivity());
        List<CustomerOrderTypeModel> customerOrderTypeModels = customerOrderTypesManager.getItems();
        CustomerPaymentTypesViewManager customerPaymentTypesViewManager = new CustomerPaymentTypesViewManager(getActivity());
        try {
            List<CustomerPaymentTypesViewModel> customerPaymentTypes =
                    customerPaymentTypesViewManager.getCustomerPaymentType(getSelectedId());
            if (customerOrderTypeModels.size() > 0 && customerPaymentTypes.size() > 0)
                gotoOrder();
            else {
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

    private void calculateEmphaticItems(final UUID customerId) throws ValidationException, DbException {
        CustomerEmphaticProductViewManager manager = new CustomerEmphaticProductViewManager(getActivity());
        manager.calcEmphaticProducts(customerId);
    }

    private void gotoOrder() {
        try {
            calculateEmphaticItems(getSelectedId());
            CustomerEmphaticProductManager customerEmphaticProductManager =
                    new CustomerEmphaticProductManager(getActivity());
            customerEmphaticProductManager.checkCustomerEmphaticItems(getSelectedId());
            CustomerCallOrderPreviewManager orderManager = new CustomerCallOrderPreviewManager(getActivity());
            if (checkCloudConfig(ConfigKey.DoubleRequestIsEnabled, true)) {
                List<CustomerCallOrderPreviewModel> orderModels = orderManager.getCustomerCallOrders(getSelectedId());
                if (orderModels.size() == 0) {
                    addCustomerCallOrder(getSelectedId());
                } else {
                    letsGo(getSelectedId(), orderModels);
                }
            } else {
                List<CustomerCallOrderPreviewModel> orderModels = orderManager.getCustomerCallOrders(getSelectedId());
                if (orderModels.size() == 0) {
                    addCustomerCallOrder(getSelectedId());
                } else {
                    letsGo(getSelectedId(), orderModels.get(0).UniqueId);
                }
            }

        } catch (EmphaticItemNotFoundException ex) {
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.emphatic_item_is_not_for_sale_therefore_order_is_not_possible);
            String msg = "";
            for (EmphaticProductCountModel model :
                    ex.getItems()) {
                msg += model.ProductName + "\n";
            }
            dialog.setMessage(msg);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception ex) {
            getActivity().showSnackBar(R.string.error_calculating_emphatic_products, MainVaranegarActivity.Duration.Short);
        }
    }

    private void addCustomerCallOrder(final UUID customerId) {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getActivity());
        try {
            CustomerCallOrderModel callOrderModel = callOrderManager.addOrder(customerId);
            letsGo(customerId, callOrderModel.UniqueId);
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    private void letsGo(final UUID customerId, List<CustomerCallOrderPreviewModel> callOrders) {
        OrderSelectionDialog dialog = new OrderSelectionDialog();
        dialog.setOrders(callOrders);
        dialog.setCustomerId(customerId);
        dialog.onOrderSelected = new OrderSelectionDialog.OnOrderSelected() {
            @Override
            public void run(UUID orderId) {
                if (orderId != null) {
                    CustomerSaveOrderFragment saveOrderFragment = new CustomerSaveOrderFragment();
                    saveOrderFragment.setArguments(customerId, orderId);
                    getActivity().pushFragment(saveOrderFragment);
                } else {
                    addCustomerCallOrder(getSelectedId());
                }
            }
        };
        dialog.show(getActivity().getSupportFragmentManager(), "OrderSelectionDialog");
    }

    private void letsGo(UUID customerId, UUID orderId) {
        CustomerSaveOrderFragment saveOrderFragment = new CustomerSaveOrderFragment();
        saveOrderFragment.setArguments(customerId, orderId);
        getActivity().pushFragment(saveOrderFragment);
    }

}
