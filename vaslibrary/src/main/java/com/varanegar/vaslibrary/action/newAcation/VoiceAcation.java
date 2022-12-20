package com.varanegar.vaslibrary.action.newAcation;

import android.content.Intent;
import android.speech.RecognizerIntent;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.action.CheckPathAction;
import com.varanegar.vaslibrary.ui.fragment.CustomersContentFragment;

import java.util.UUID;

public class VoiceAcation extends CheckPathAction {
    public VoiceAcation(MainVaranegarActivity activity,
                        ActionsAdapter adapter,
                        UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_voice;
    }

    @Override
    public String getName() {
        return "کنترل صوتی";
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
