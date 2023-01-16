package com.varanegar.vaslibrary.action.newAcation;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.action.CheckPathAction;

import java.util.UUID;

public class GroupSimilarProductAction extends CheckPathAction {

    public GroupSimilarProductAction(MainVaranegarActivity activity,
                        ActionsAdapter adapter,
                        UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_voice;
    }

    @Override
    public String getName() {
        return "لیست کالا مشتریان مشابه";
    }

    @Override
    protected boolean isDone() {
        return false;
    }

    @Override
    public void run() {
        runActionCallBack();
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
