package com.varanegar.vaslibrary.action;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.ui.dialog.EditCustomerFragmentDialog;
import com.varanegar.vaslibrary.ui.dialog.EditCustomerZarFragmentDialog;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 01/21/2017.
 */

public class EditCustomerAction extends CheckBarcodeAction {

    private String pin ;

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("CD63A5C1-2DA4-41D8-A597-68E61B0B7681");
    }

    public EditCustomerAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_mode_edit_black_24dp;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.edit_customer_label);
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (checkCloudConfig(ConfigKey.AllowEditCustomer, false))
            return getActivity().getString(R.string.not_allow_edit_customer);

        if (getCallManager().isLackOfVisit(getCalls()))
            return getActivity().getString(R.string.customer_is_not_visited);

        return null;
    }

    @Override
    public boolean isDone() {
        return getCallManager().isEdited(getCalls());
    }

    @Nullable
    @Override
    protected String isMandatory() {
        int p = getTaskPriority();
        if (p > 0 && Linq.findFirstIndex(getTaskPriorities().values(), item -> item.Priority > p) > 0)
            return getActivity().getString(R.string.editing_customer_is_mandatory);
        else
            return null;
    }


    /**
     * pincode4 Edit User
     * ویرایش مشتری
     */
    @Override
    public void run() {
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();
        if (ownerKeysWrapper.isZarMakaron()) {
            CustomerCallInvoiceManager customerCallOrderManager = new
                    CustomerCallInvoiceManager(getActivity());
            List<CustomerCallInvoiceModel> customerCallOrderModels =
                    customerCallOrderManager.getCustomerCallInvoices(getSelectedId());

            /**
             * گرفتن pincode4 برای ویرایش مشتری در presale
             */

            final TourModel tourModel = new TourManager(getActivity()).loadTour();
            for (int i = 0; i < tourModel.Pins.size(); i++) {
                if(getSelectedId().equals(tourModel.Pins.get(i).CustomerId)) {
                    pin = tourModel.Pins.get(i).PinCode4;
                }
            }
            if (!pin.isEmpty() && pin!=null) {
                InsertPinDialog dialog = new InsertPinDialog();
                dialog.setCancelable(false);
                dialog.setClosable(false);
                dialog.setValues(pin);
                dialog.setValuesRequst("pin4",getSelectedId(),
                        null,null);
                dialog.setOnResult(new InsertPinDialog.OnResult() {
                    @Override
                    public void done() {
                        showEditDialog();
                    }

                    @Override
                    public void failed(String error) {
                        Timber.e(error);
                        setRunning(false);
                        if (error.equals(getActivity().getString(R.string.pin_code_in_not_correct))) {
                            printFailed(getActivity(), error);
                        } else {
                            //saveSettlementFailed(getContext(), error);
                        }
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "InsertPinDialog");
            }
        } else {
            EditCustomerFragmentDialog editCustomerFragmentDialog = new EditCustomerFragmentDialog();
            editCustomerFragmentDialog.onCustomerEditedCallBack = this::runActionCallBack;
            Bundle bundle = new Bundle();
            bundle.putString("68565e5e-d407-4858-bc5f-fd52b9318734", getSelectedId().toString());
            editCustomerFragmentDialog.setArguments(bundle);
            editCustomerFragmentDialog.show(getActivity().getSupportFragmentManager(),
                    "EditCustomerFragmentDialog");
        }
    }

    private void showEditDialog(){
        EditCustomerZarFragmentDialog editCustomerFragmentDialog = new EditCustomerZarFragmentDialog();
        editCustomerFragmentDialog.onCustomerEditedCallBack = this::runActionCallBack;
        Bundle bundle = new Bundle();
        bundle.putString("68565e5e-d407-4858-bc5f-fd52b9318734", getSelectedId().toString());
        editCustomerFragmentDialog.setArguments(bundle);
        editCustomerFragmentDialog.show(getActivity().getSupportFragmentManager(),
                "EditCustomerFragmentDialog");
    }

    private void printFailed(Context context, String error) {
        try {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Warning);
            dialog.setTitle(R.string.DeliveryReasons);
            dialog.setMessage(error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e1) {
            Timber.e(e1);
        }
    }


}
