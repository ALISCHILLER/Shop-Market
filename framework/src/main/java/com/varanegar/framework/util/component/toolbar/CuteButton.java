package com.varanegar.framework.util.component.toolbar;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.util.Date;

/**
 * Created by A.Torabi on 11/12/2017.
 */

public class CuteButton {
    public @StringRes
    int title;
    public @DrawableRes
    int icon;

    public void setTitle(@StringRes int title) {
        this.title = title;
    }

    public void setIcon(@DrawableRes int icon) {
        this.icon = icon;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onClickListener) {
        this.onLongClickListener = onClickListener;
    }

    public int getTitle() {
        return title;
    }

    public interface IIsEnabled {
        boolean run();
    }

    private IIsEnabled isEnabled;

    public void setEnabled(IIsEnabled isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled == null || isEnabled.run();
    }

    public interface OnClickListener {
        void onClick();
    }

    private OnClickListener onClickListener;

    public void runOnClickListener() {
        if (onClickListener != null) {
                onClickListener.onClick();
        }
    }

    public interface OnLongClickListener {
        void onLongClick();
    }
    private OnLongClickListener onLongClickListener;

    public void runOnLongClickListener() {
        if (onLongClickListener != null) {
            onLongClickListener.onLongClick();
        }
    }
}
