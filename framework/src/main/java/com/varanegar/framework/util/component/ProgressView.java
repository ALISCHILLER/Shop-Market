package com.varanegar.framework.util.component;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;
import com.varanegar.framework.R;
import com.varanegar.framework.util.HelperMethods;

import jp.wasabeef.blurry.Blurry;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/4/2018.
 */

public class ProgressView extends FrameLayout {
    private boolean isDialog;
    private String title;
    private String message;
    private int titleId;
    private int messageId;
    private View view;
    private View progressBarLayout;
    private ImageView bluredImageView;
    private View rippleBackground;
    private TextView titleTextView;
    private TextView msgTextView;

    static class ProgressViewTag {

    }

    public void isDialog(boolean isDialog) {
        this.isDialog = isDialog;
    }

    private static ProgressViewTag tag = new ProgressViewTag();

    private void disable(ViewGroup layout) {
        if (layout.isEnabled()) {
            layout.setEnabled(false);
            layout.setTag(R.id.progress_view_smooth_progress_bar, tag);
        }
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child);
            } else {
                if (child.isEnabled()) {
                    child.setEnabled(false);
                    child.setTag(R.id.progress_view_smooth_progress_bar, tag);
                }
            }
        }
    }

    private void enable(ViewGroup layout) {
        if (layout.getTag(R.id.progress_view_smooth_progress_bar) == tag) {
            layout.setEnabled(true);
            layout.setTag(R.id.progress_view_smooth_progress_bar, null);
        }
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enable((ViewGroup) child);
            } else {
                if (child.getTag(R.id.progress_view_smooth_progress_bar) == tag) {
                    child.setEnabled(true);
                    child.setTag(R.id.progress_view_smooth_progress_bar, null);
                }
            }
        }
    }

    public void start() {
        disable(this);
        bluredImageView.setImageResource(0);
        bluredImageView.setVisibility(VISIBLE);
        try {
            if (!HelperMethods.isLowMemory())
                Blurry.with(getContext()).animate().radius(15).capture(ProgressView.this).into(bluredImageView);
        } catch (Throwable ignored) {
            Timber.d(ignored);
        }
        progressBarLayout.setVisibility(VISIBLE);
        if (!HelperMethods.isLowMemory()) {
            rippleBackground.setVisibility(VISIBLE);
            if (!isDialog) {
                final RippleBackground rbg = (RippleBackground) rippleBackground;
                rbg.startRippleAnimation();
            }
        }
    }

    public void finish() {
        enable(this);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_out);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBarLayout.setVisibility(INVISIBLE);
                rippleBackground.setVisibility(INVISIBLE);
                bluredImageView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        progressBarLayout.startAnimation(animation);
    }

    public void setTitle(String title) {
        this.title = title;
        if (titleTextView != null)
            titleTextView.setText(title);
    }

    public void setMessage(String message) {
        this.message = message;
        if (msgTextView != null) {
            msgTextView.setVisibility(VISIBLE);
            msgTextView.setText(message);
        }
    }

    public void setTitle(@StringRes int resId) {
        this.titleId = resId;
        if (titleTextView != null) {
            title = null;
            titleTextView.setText(titleId);
        }
    }

    public void setMessage(@StringRes int resId) {
        this.messageId = resId;
        if (msgTextView != null) {
            message = null;
            msgTextView.setVisibility(VISIBLE);
            msgTextView.setText(resId);
        }
    }

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);
        try {
            title = a.getString(R.styleable.ProgressView_title);
            message = a.getString(R.styleable.ProgressView_message);
            isDialog = a.getBoolean(R.styleable.ProgressView_is_dialog, false);
        } finally {
            a.recycle();
        }
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, 0);
        try {
            title = a.getString(R.styleable.ProgressView_title);
            message = a.getString(R.styleable.ProgressView_message);
            isDialog = a.getBoolean(R.styleable.ProgressView_is_dialog, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isDialog)
            view = inflate(getContext(), R.layout.progress_view_layout, this);
        else
            view = inflate(getContext(), R.layout.progress_view_simple_layout, this);
        progressBarLayout = view.findViewById(R.id.progress_bar_layout);
        bluredImageView = view.findViewById(R.id.blured_image_view);
        rippleBackground = view.findViewById(R.id.ripple_background);
        titleTextView = view.findViewById(R.id.title_text_view);
        msgTextView = view.findViewById(R.id.message_text_view);
        if (titleId != 0)
            titleTextView.setText(getContext().getString(titleId));
        if (title != null && !title.isEmpty())
            titleTextView.setText(title);
        if (messageId != 0) {
            msgTextView.setVisibility(VISIBLE);
            msgTextView.setText(getContext().getString(messageId));
        }
        if (message != null && !message.isEmpty()) {
            msgTextView.setVisibility(VISIBLE);
            msgTextView.setText(message);
        }


    }
}
