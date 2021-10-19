package com.varanegar.vaslibrary.action;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.ui.dialog.NonDeliveryActionDialog;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/25/2017.
 */

public class NonDeliveryAction extends CheckDistanceAction {


    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("e92fd36b-14d5-4871-a52d-d7a95bb37eaa");
    }

    public NonDeliveryAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_pan_tool_black_24dp;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.non_delivery);
    }


    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (canNotEditOperationAfterPrint())
            return getActivity().getString(R.string.can_not_edit_customer_operation_after_print);

        NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getActivity());
        List<NoSaleReasonModel> noSalesReasons = noSaleReasonManager.getNonDeliveryReason();

        if (noSalesReasons.size() < 1)
            return getActivity().getString(R.string.no_delivery_reasons_not_defined);

        CustomerCallInvoiceManager callInvoiceManager = new CustomerCallInvoiceManager(getActivity());
        List<CustomerCallInvoiceModel> invoiceModels = callInvoiceManager.getCustomerCallInvoices(getSelectedId());
        if (invoiceModels.size() == 0)
            return getActivity().getString(R.string.invoice_or_draft_not_found);

        else return null;

    }

    @Override
    public boolean isDone() {
        return getCallManager().isCompleteLackOfDelivery(getCalls());
    }

    @Override
    public void run() {
        setRunning(true);
        if (getCallManager().hasDeliveryOrReturnCall(getCalls())) {
            CuteMessageDialog builder = new CuteMessageDialog(getActivity());
            builder.setTitle(getActivity().getString(R.string.alert));
            builder.setMessage(getActivity().getString(R.string.txt_assure_to_delete_cust_order));
            builder.setPositiveButton(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNonDeliveryReasons(getSelectedId());
                }
            });
            builder.setNegativeButton(R.string.no, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setRunning(false);
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    setRunning(false);
                }
            });
            builder.show();
        } else {
            showNonDeliveryReasons(getSelectedId());
        }

    }

    private void showNonDeliveryReasons(UUID selectedItem) {
        NonDeliveryActionDialog nonDeliveryActionDialog = new NonDeliveryActionDialog();
        nonDeliveryActionDialog.onOrderStatusChanged = new NonDeliveryActionDialog.OnOrderStatusChanged() {
            @Override
            public void onChanged() {
                runActionCallBack();
                setRunning(false);
            }
        };

        nonDeliveryActionDialog.setOnResume(new CuteAlertDialog.OnResumeCallBack() {
            @Override
            public void run() {
                setRunning(false);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("CustomerUniqueId", selectedItem.toString());
        nonDeliveryActionDialog.setArguments(bundle);
        nonDeliveryActionDialog.show(getActivity().getSupportFragmentManager(), "NonDeliveryActionDialog");
    }

}
