package com.varanegar.contractor;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
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
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.order.CustomerSaveOrderFragment;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class ContractorCustomersFragment extends CustomersFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new ContractorDrawerAdapter(getVaranegarActvity()));
    }
    @Override
    protected VaranegarFragment getSendTourFragment() {
        return new ContractorSendTourFragment();
    }

    @Override
    protected TourReportFragment getProfileFragment() {
        return new ContractorTourReportFragment();
    }

    @Override
    protected VaranegarFragment getContentFragment(UUID customerId) {
        CustomerModel customer = new CustomerPathViewManager(getContext()).getItem(customerId);
        CustomerOrderTypesManager customerOrderTypesManager = new CustomerOrderTypesManager(getActivity());
        List<CustomerOrderTypeModel> customerOrderTypeModels = customerOrderTypesManager.getItems();
        CustomerPaymentTypesViewManager customerPaymentTypesViewManager = new CustomerPaymentTypesViewManager(getActivity());
        try {
            List<CustomerPaymentTypesViewModel> customerPaymentTypes = customerPaymentTypesViewManager.getCustomerPaymentType(customerId);
            if (customerOrderTypeModels.size() > 0 && customerPaymentTypes.size() > 0)
                return gotoOrder(customerId, customer.CustomerLevelId);
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
                return null;
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setMessage(R.string.back_office_type_is_uknown);
            dialog.show();
            return null;
        }
    }

    @Override
    protected VaranegarFragment getContentTargetFragment() {
        return null;
    }

    @Override
    protected VaranegarFragment getContentTargetDetailFragment() {
        return null;
    }

    private boolean checkCloudConfig(ConfigKey configKey , boolean value){
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        return SysConfigManager.compare(sysConfigManager.read(configKey,SysConfigManager.cloud),value);
    }

    private void calculateEmphaticItems(final UUID customerId) throws ValidationException, DbException {
        CustomerEmphaticProductViewManager manager = new CustomerEmphaticProductViewManager(getActivity());
        manager.calcEmphaticProducts(customerId);
    }

    private VaranegarFragment gotoOrder(UUID customerId, UUID customerLevelId) {
        try {
            calculateEmphaticItems(customerId);
            CustomerEmphaticProductManager customerEmphaticProductManager = new CustomerEmphaticProductManager(getActivity());
            customerEmphaticProductManager.checkCustomerEmphaticItems(customerId);
            CustomerCallOrderPreviewManager orderManager = new CustomerCallOrderPreviewManager(getActivity());
            if (checkCloudConfig(ConfigKey.DoubleRequestIsEnabled, true)) {
                List<CustomerCallOrderPreviewModel> orderModels = orderManager.getCustomerCallOrders(customerId);
                if (orderModels.size() == 0) {
                    return addCustomerCallOrder(customerId, customerLevelId);
                } else {
                    OrderSelectionFragment orderSelectionFragment = new OrderSelectionFragment();
                    orderSelectionFragment.setCustomerId(customerId);
                    orderSelectionFragment.setCustomerLevelId(customerLevelId);
                    return orderSelectionFragment;
                }
            } else {
                List<CustomerCallOrderPreviewModel> orderModels = orderManager.getCustomerCallOrders(customerId);
                if (orderModels.size() == 0) {
                    return addCustomerCallOrder(customerId, customerLevelId);
                } else {
                    return letsGo(customerId, orderModels.get(0).UniqueId, customerLevelId);
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
            return null;
        } catch (Exception ex) {
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.emphatic_item_is_not_for_sale_therefore_order_is_not_possible);
            dialog.setMessage(R.string.error_calculating_emphatic_products);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            return null;
        }
    }

    private VaranegarFragment addCustomerCallOrder(final UUID customerId, UUID customerLevelId) {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getActivity());
        try {
            CustomerCallOrderModel callOrderModel = callOrderManager.addOrder(customerId);
            return letsGo(customerId, callOrderModel.UniqueId, customerLevelId);
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    private VaranegarFragment letsGo(UUID customerId, UUID orderId, UUID customerLevelId) {
        CustomerSaveOrderFragment saveOrderFragment = new CustomerSaveOrderFragment();
        saveOrderFragment.setArguments(customerId, orderId, customerLevelId);
        return saveOrderFragment;
    }

}
