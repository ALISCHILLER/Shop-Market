package com.varanegar.supervisor;

import com.varanegar.framework.base.ProgressFragment;

public abstract class IMainPageFragment extends ProgressFragment {
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isResumed()) {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }
}
