package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.vpnfragment.VpnDialogFragment;

import java.util.UUID;

public class VpnAction  extends CheckPathAction{
    public VpnAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_baseline_vpn_lock_24;
    }

    @Override
    public String getName() {
        return "درگاه اتصال";
    }

    @Override
    protected boolean isDone() {
        return false;
    }

    @Override
    public void run() {
        VpnDialogFragment vpnDialogFragment = new VpnDialogFragment();
        vpnDialogFragment.show(getActivity().getSupportFragmentManager(), "SettingDialogFragment");
    }

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return null;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }
}
