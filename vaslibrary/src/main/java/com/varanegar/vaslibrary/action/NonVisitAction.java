package com.varanegar.vaslibrary.action;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import android.view.View;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.ui.dialog.NonVisitActionDialog;
import com.varanegar.vaslibrary.ui.fragment.picturecustomer.CameraFragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/03/2017.
 */

public class NonVisitAction extends CheckPathAction {

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("9e83bf64-ead5-4cb8-a7e7-4faa09ce098b");
    }

    public NonVisitAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_do_not_disturb_on_white_24dp;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.non_visit);
    }

    @Nullable
    @Override
    @CallSuper
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls() , null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (canNotEditOperationAfterPrint())
            return getActivity().getString(R.string.can_not_edit_customer_operation_after_print);

        NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getActivity());
        List<NoSaleReasonModel> noSalesReasons = noSaleReasonManager.getNoneVisitReasons();

        if (noSalesReasons.size() < 1)
            return getActivity().getString(R.string.no_visit_reasons);
        else
            return null;
    }

    @Override
    public boolean isDone() {
        return getCallManager().isLackOfVisit(getCalls());
    }


    @Override
    public void run() {
        setRunning(true);
        String warning = null;

        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (getCallManager().hasDeliveryOrReturnCall(getCalls()) || new PaymentManager(getActivity()).getCustomerPayments(getSelectedId()).size() > 0)
                if (getCallManager().hasCameraCall(getCalls()))
                    warning = getActivity().getString(R.string.txt_assure_to_delete_cust_order_and_payment_and_pictures);
                else
                    warning = getActivity().getString(R.string.txt_assure_to_delete_cust_order_and_payment);
        } else {
            if (getCallManager().hasOrderOrReturnCall(getCalls()) || new PaymentManager(getActivity()).getCustomerPayments(getSelectedId()).size() > 0)
                if (getCallManager().hasCameraCall(getCalls()))
                    warning = getActivity().getString(R.string.txt_assure_to_delete_cust_order_and_payment_and_pictures);
                else
                    warning = getActivity().getString(R.string.txt_assure_to_delete_cust_order_and_payment);
        }

        if (warning == null && getCallManager().hasCameraCall(getCalls()))
            warning = getActivity().getString(R.string.txt_assure_to_delete_pictures);


        if (warning != null) {
            final CuteMessageDialog builder = new CuteMessageDialog(getActivity());
            builder.setIcon(Icon.Warning);
            builder.setTitle(getActivity().getString(R.string.alert));
            builder.setMessage(warning);
            builder.setPositiveButton(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNonVisitReasons(getSelectedId());
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
        } else
            showNonVisitReasons(getSelectedId());
    }

    private void showNonVisitReasons(UUID selectedItem) {
        NonVisitActionDialog nonVisitFragmentDialog = new NonVisitActionDialog();
        nonVisitFragmentDialog.onVisitStatusChanged = new NonVisitActionDialog.OnVisitStatusChanged() {
            @Override
            public void onChanged() {
                runActionCallBack();
                PictureCustomerViewManager pictureCustomerManager = new PictureCustomerViewManager(getActivity());
                String pictureError = pictureCustomerManager.checkMandatoryPicture(getSelectedId(), getCalls());
                if (pictureError != null) {
                    CameraFragment fragment = new CameraFragment();
                    fragment.setCustomerId(getSelectedId());
                    getActivity().pushFragment(fragment);
                }
                setRunning(false);
            }
        };
        nonVisitFragmentDialog.setOnResume(new CuteAlertDialog.OnResumeCallBack() {
            @Override
            public void run() {
                setRunning(false);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("CustomerUniqueId", selectedItem.toString());
        nonVisitFragmentDialog.setArguments(bundle);
        nonVisitFragmentDialog.show(getActivity().getSupportFragmentManager(), "NonVisitActionDialog");

    }

}