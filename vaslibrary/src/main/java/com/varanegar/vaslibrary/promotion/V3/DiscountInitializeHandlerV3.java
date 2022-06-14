package com.varanegar.vaslibrary.promotion.V3;

import android.content.Context;

import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

/**
 * Created by A.Razavi on 11/14/2017.
 */

public class DiscountInitializeHandlerV3 {

    public static DiscountInitializeHandler getDiscountHandler(Context context){
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        try {
            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.ThirdParty))
                GlobalVariables.setIsThirdParty(true);
            else
                GlobalVariables.setIsThirdParty(false);
            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Rastak)){
                return new DiscountInitializeHandlerVnLite(context);
            }else
            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Varanegar) ||
                    sysConfigManager.getBackOfficeType().equals(BackOfficeType.ThirdParty))
            {
                return new DiscountInitializeHandlerSDS(context);
            }
            else{
                return null;
            }

        }catch (Exception e){
            Timber.d("No back office");
            return null;
        }

    }
}


