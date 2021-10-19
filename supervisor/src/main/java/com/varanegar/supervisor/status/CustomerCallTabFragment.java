package com.varanegar.supervisor.status;

import androidx.annotation.CallSuper;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.supervisor.webapi.CustomerCallViewModel;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public abstract class CustomerCallTabFragment extends VaranegarFragment {
    protected CustomerCallViewModel customerCallViewModel;

    @CallSuper
    public void refresh(CustomerCallViewModel customerCallViewModel){
        this.customerCallViewModel = customerCallViewModel;
    }

    public OnCallChanged onCallChanged;

    protected void runOnCallChanged() {
        if (onCallChanged != null)
            onCallChanged.run();
    }

    interface OnCallChanged {
        void run();
    }


}
