package com.varanegar.framework.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.ContentFrameLayout;

import com.varanegar.framework.R;
import com.varanegar.framework.util.component.ProgressView;

/**
 * Created by A.Torabi on 7/4/2018.
 */

public abstract class ProgressFragment extends VaranegarFragment {
    private ProgressView progressView;

    protected void startProgress(@StringRes int title, @StringRes int message) {
        if (progressView != null) {
            progressView.setTitle(title);
            progressView.setMessage(message);
            progressView.start();
        }
    }

    protected void startProgress(String title, String message) {
        if (progressView != null) {
            progressView.setTitle(title);
            progressView.setMessage(message);
            progressView.start();
        }
    }

    protected void changeProgress(@StringRes int message) {
        if (progressView != null)
            progressView.setMessage(message);

    }

    protected void changeProgress(String message) {
        if (progressView != null)
            progressView.setMessage(message);

    }

    protected void finishProgress() {
        if (progressView != null)
            progressView.finish();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        progressView = (ProgressView) inflater.inflate(R.layout.fragment_progress_layout, container, false);
        ContentFrameLayout frameLayout = progressView.findViewById(R.id.progress_content_frame_layout);
        View contentView = onCreateContentView(inflater, frameLayout, savedInstanceState);
        frameLayout.addView(contentView);
        return progressView;
    }

    protected abstract View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
}
