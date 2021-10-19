package com.varanegar.vaslibrary.manager.contractpricemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;

import retrofit2.Call;

/**
 * Created by A.Torabi on 12/5/2017.
 */

public class ContractPriceManager {
    private final Context context;
    private Call call;

    public ContractPriceManager(@NonNull Context context) {
        this.context = context;
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        try {
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.Rastak) {
                ContractPriceVnLiteManager contractPriceVnLiteManager = new ContractPriceVnLiteManager(context);
                call = contractPriceVnLiteManager.sync(updateCall);
            } else if (backOfficeType == BackOfficeType.Varanegar) {
                ContractPriceSDSManager contractPriceSDSManager = new ContractPriceSDSManager(context);
                call = contractPriceSDSManager.sync(updateCall);
            } else if (backOfficeType == BackOfficeType.ThirdParty) {
                ContractPriceNestleManager contractPriceNestleManager = new ContractPriceNestleManager(context);
                call = contractPriceNestleManager.sync(updateCall);
            } else
                updateCall.failure(context.getString(R.string.back_office_type_is_uknown));
        } catch (UnknownBackOfficeException e) {
            updateCall.failure(context.getString(R.string.back_office_type_is_uknown));
        }

    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }
}
