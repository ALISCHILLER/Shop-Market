package com.varanegar.framework.util.fragment.extendedlist;

import android.content.pm.ActivityInfo;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by atp on 1/14/2017.
 */

public abstract class Action implements Serializable {
    private String isEnabled;
    private int oldOrientation;
    public static Action currentAction;
    private boolean isAnimation = false;

    public String getIsEnabled() {
        return isEnabled;
    }

    private boolean isDone;

    public boolean getIsDone() {
        return isDone;
    }


    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        adapter.notifyDataSetChanged();
        if (running) {
            oldOrientation = activity.getRequestedOrientation();
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            activity.setRequestedOrientation(oldOrientation);
        }
    }

    private boolean isRunning = false;

    public ActionsAdapter getAdapter() {
        return adapter;
    }

    private final ActionsAdapter adapter;

    /**
     * این شناسه مشتری می باشد. آی دی مشتری
     * @return customer id
     */
    public UUID getSelectedId() {
        return selectedId;
    }

    private final UUID selectedId;

    public MainVaranegarActivity getActivity() {
        return activity;
    }

    public int icon = -1;
    private MainVaranegarActivity activity;

    public Action(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        this.activity = activity;
        this.selectedId = selectedId;
        this.adapter = adapter;
    }

    public abstract String getName();

    /**
     * @return Returns null if this action is enabled. Returns error if action is not available
     */
    @Nullable
    protected abstract String isEnabled();

    protected abstract boolean isDone();


    public boolean isAnimation() {
        return isAnimation;
    }

    public void setAnimation(boolean animation) {
        isAnimation = animation;
    }

    /**
     * @return Returns Null if this action is not force.
     * else returns an appropriate message that tells user why there is force to do the action
     */
    @Nullable
    public abstract String isForce();

    public int priority() {
        return 0;
    }

    public abstract void run();

    public void runOnLongClick() {

    }

    @CallSuper
    public void refresh() {
        isEnabled = isEnabled();
        isDone = isDone();
    }

    public void disable() {
        isEnabled = activity.getString(R.string.action_pending);
    }

    public interface ActionCallBack {
        void done();
    }

    private ActionCallBack actionCallBack;

    public void setActionCallBack(@NonNull ActionCallBack callBack) {
        this.actionCallBack = callBack;
    }

    protected void runActionCallBack() {
        if (actionCallBack != null)
            actionCallBack.done();
    }

}
