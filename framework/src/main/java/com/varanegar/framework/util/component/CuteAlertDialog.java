package com.varanegar.framework.util.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ContentFrameLayout;

import com.varanegar.framework.R;

/**
 * Created by atp on 4/3/2017.
 */

public abstract class CuteAlertDialog extends CuteDialogWithToolbar {
    private Options options = Options.Both;
    private View okTextView;
    private View cancelTextView;

    private OnResumeCallBack onResumeCallBack;

    public interface OnResumeCallBack {
        void run();
    }

    public void setOnResume(@NonNull OnResumeCallBack callBack) {
        this.onResumeCallBack = callBack;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onResumeCallBack = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        View view = null;
        if ((view = getView()) != null)
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (onResumeCallBack != null)
                        onResumeCallBack.run();
                }
            }, 500);
    }

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_cute_alert, container, false);
        okTextView = view.findViewById(R.id.ok_text_view);
        cancelTextView = view.findViewById(R.id.cancel_text_view);
        refreshButtons();
        ContentFrameLayout content = (ContentFrameLayout) view.findViewById(R.id.content_frame_layout);
        content.removeAllViews();
        onCreateContentView(inflater, content, savedInstanceState);
        setCancelable(false);
        return view;
    }

    private void refreshButtons() {
        if (options == Options.Ok) {
            okTextView.setVisibility(View.VISIBLE);
            cancelTextView.setVisibility(View.INVISIBLE);
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ok();
                }
            });
        } else if (options == Options.Cancel) {
            okTextView.setVisibility(View.INVISIBLE);
            cancelTextView.setVisibility(View.VISIBLE);
            cancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                    dismiss();
                }
            });
        } else {
            okTextView.setVisibility(View.VISIBLE);
            okTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ok();
                }
            });
            cancelTextView.setVisibility(View.VISIBLE);
            cancelTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                    dismiss();
                }
            });
        }
    }

    public enum Options {
        Ok,
        Cancel,
        Both
    }

    public void setOptions(Options options) {
        this.options = options;
        if (okTextView != null)
            refreshButtons();
    }

    protected abstract void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState);

    public abstract void ok();

    public abstract void cancel();
}
