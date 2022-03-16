package com.varanegar.vaslibrary.action;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.ui.dialog.CompleteReturnActionDialog;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/25/2017.
 */

/**
 *صفحه برگشتی کامل
 */

public class DistReturnAction extends CheckDistanceAction {

    private List<CustomerCallInvoiceModel> customerCallOrderModels;

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("e92fd36b-14d5-4871-a52d-d7a95bb37eaa");
    }

    public DistReturnAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_return_all_black_24dp;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.complete_return);
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
        List<NoSaleReasonModel> noSalesReasons = noSaleReasonManager.getDistReturnReason();

        if (noSalesReasons.size() < 1)
            return getActivity().getString(R.string.complete_return_reasons_not_defined);

        CustomerCallInvoiceManager callInvoiceManager = new CustomerCallInvoiceManager(getActivity());
        List<CustomerCallInvoiceModel> invoiceModels = callInvoiceManager.getCustomerCallInvoices(getSelectedId());
        if (invoiceModels.size() == 0)
            return getActivity().getString(R.string.invoice_or_draft_not_found);

        boolean exists = Linq.exists(invoiceModels, new Linq.Criteria<CustomerCallInvoiceModel>() {
            @Override
            public boolean run(CustomerCallInvoiceModel item) {
                return item.IsInvoice;
            }
        });
        if (exists)
            return getActivity().getString(R.string.you_have_factor_in_orders);

        return null;

    }

    @Override
    public boolean isDone() {
        return getCallManager().isCompleteReturnDelivery(getCalls());
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
//                    showNonDeliveryReasons(getSelectedId());
                    dialogpincode();
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
            dialogpincode();
        }

    }



    private void dialogpincode(){
        CustomerCallInvoiceManager customerCallOrderManager = new CustomerCallInvoiceManager(getActivity());
        customerCallOrderModels = customerCallOrderManager.getCustomerCallInvoices(getSelectedId());

        InsertPinDialog dialog = new InsertPinDialog();
        dialog.setCancelable(false);
        dialog.setClosable(false);
        dialog.setValues(customerCallOrderModels.get(0).PinCode3);
        dialog.setValuesRequst("pin3",getSelectedId(),
                customerCallOrderModels.get(0).UniqueId);
        dialog.setOnResult(new InsertPinDialog.OnResult() {
            @Override
            public void done() {
//                    changePaymentType(paymentType, print);
                showNonDeliveryReasons(getSelectedId());
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
    private void showNonDeliveryReasons(UUID selectedItem) {
        CompleteReturnActionDialog completeReturnActionDialog = new CompleteReturnActionDialog();
        completeReturnActionDialog.onOrderStatusChanged = new CompleteReturnActionDialog.OnOrderStatusChanged() {
            @Override
            public void onChanged() {
                runActionCallBack();
                setRunning(false);
            }
        };

        completeReturnActionDialog.setOnResume(new CuteAlertDialog.OnResumeCallBack() {
            @Override
            public void run() {
                setRunning(false);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("CustomerUniqueId", selectedItem.toString());
        completeReturnActionDialog.setArguments(bundle);
        completeReturnActionDialog.show(getActivity().getSupportFragmentManager(), "CompleteReturnActionDialog");
    }

}
