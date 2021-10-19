package com.varanegar.framework.base;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by atp on 2/26/2017.
 */

public class PopupFragment extends VaranegarFragment {
    @Override
    public void onStart() {
        super.onStart();
        getVaranegarActvity().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onStop() {
        super.onStop();
        getVaranegarActvity().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
}
