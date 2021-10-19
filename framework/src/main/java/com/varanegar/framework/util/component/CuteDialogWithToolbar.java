package com.varanegar.framework.util.component;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.varanegar.framework.R;

/**
 * Created by atp on 12/31/2016.
 */
public abstract class CuteDialogWithToolbar extends CuteDialog {
    TextView titleTextView;
    private String title;
    private boolean closable = true;
    private CheckBox doNotShowCheckBox;
    private String showPreferenceId;

    public void setUserPreferenceId(String id) {
        showPreferenceId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean multipan = getResources().getBoolean(R.bool.multipane);
        boolean landscap = getResources().getBoolean(R.bool.is_landscape);
        if (!multipan && !landscap)
            setSizingPolicy(SizingPolicy.Maximum);
        View view = inflater.inflate(R.layout.layout_dialog_toolbar, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.findViewById(R.id.dialog_close_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (closable)
                    dismiss();
            }
        });
        titleTextView = (TextView) view.findViewById(R.id.dialog_title_text_view);
        if (title != null)
            titleTextView.setText(title);
        if (showPreferenceId != null && UserDialogPreferences.isPreferenceAvailable(getContext())) {
            doNotShowCheckBox = (CheckBox) view.findViewById(R.id.do_not_show_check_box);
            doNotShowCheckBox.setVisibility(View.VISIBLE);
            doNotShowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    UserDialogPreferences.setPreference(getContext(), showPreferenceId, !b);
                }
            });
        }
        FrameLayout contentFrame = (FrameLayout) view.findViewById(R.id.dialog_content_frame);
        View dialogView = onCreateDialogView(inflater, container, savedInstanceState);
        contentFrame.addView(dialogView);
        return view;
    }

    public void setTitle(String title) {
        this.title = title;
        if (titleTextView != null)
            titleTextView.setText(title);
    }

    protected void setTitle(@StringRes int id) {
        setTitle(getString(id));
    }

    public abstract View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public void setClosable(boolean closable) {
        this.closable = closable;
    }
}
