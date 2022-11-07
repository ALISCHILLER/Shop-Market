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
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.ui.dialog.NonOrderActionDialog;
import com.varanegar.vaslibrary.ui.fragment.picturecustomer.CameraFragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/25/2017.
 */

public class NonOrderAction extends CheckDistanceAction {


    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("e92fd36b-14d5-4871-a52d-d7a95bb37eaa");
    }

    public NonOrderAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_pan_tool_black_24dp;
        setAnimation(true);
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.non_order);
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
        List<NoSaleReasonModel> noSalesReasons = noSaleReasonManager.getNonOrderReason();

        if (noSalesReasons.size() < 1)
            return getActivity().getString(R.string.no_sales_reasons);
        else return null;

    }

    @Override
    public boolean isDone() {
        return getCallManager().isLackOfOrder(getCalls());
    }

    @Override
    public void run() {
        setRunning(true);
        if (getCallManager().hasOrderOrReturnCall(getCalls())) {
            CuteMessageDialog builder = new CuteMessageDialog(getActivity());
            builder.setTitle(getActivity().getString(R.string.alert));
            builder.setMessage(getActivity().getString(R.string.txt_assure_to_delete_cust_order));
            builder.setPositiveButton(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNonOrderReasons(getSelectedId());
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
            showNonOrderReasons(getSelectedId());
        }

    }

    private void showNonOrderReasons(UUID selectedItem) {
        NonOrderActionDialog nonOrderFragmentDialog = new NonOrderActionDialog();
        nonOrderFragmentDialog.onOrderStatusChanged = new NonOrderActionDialog.OnOrderStatusChanged() {
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

        nonOrderFragmentDialog.setOnResume(new CuteAlertDialog.OnResumeCallBack() {
            @Override
            public void run() {
                setRunning(false);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("CustomerUniqueId", selectedItem.toString());
        nonOrderFragmentDialog.setArguments(bundle);
        nonOrderFragmentDialog.show(getActivity().getSupportFragmentManager(), "NonOrderActionDialog");
    }

}
