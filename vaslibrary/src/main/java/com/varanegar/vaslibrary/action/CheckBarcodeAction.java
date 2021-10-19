package com.varanegar.vaslibrary.action;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.customer.CustomerBarcodeManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/6/2017.
 */

public abstract class CheckBarcodeAction extends CheckPathAction {

    public CheckBarcodeAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
    }

    @Nullable
    @Override
    @CallSuper
    public String isEnabled() {
        String err = super.isEnabled();
        if (err != null)
            return err;

        if (checkCloudConfig(ConfigKey.CheckBarcode, true)) {
            CustomerBarcodeManager customerBarcodeManager = new CustomerBarcodeManager(getActivity());
            List<String> customerBarcodeModelList = customerBarcodeManager.getCustomerBarcode(getCustomer().UniqueId);
            if (customerBarcodeModelList == null || customerBarcodeModelList.isEmpty()) {
                return getActivity().getString(R.string.barcode_is_null);
            } else {
                UpdateManager updateManager = new UpdateManager(getActivity());
                String barcode = updateManager.readBarcode();
                if (barcode == null || !customerBarcodeModelList.contains(barcode))
                    return getActivity().getString(R.string.you_have_to_use_barcode);
                else
                    return null;
            }
        }
        return null;
    }
}
