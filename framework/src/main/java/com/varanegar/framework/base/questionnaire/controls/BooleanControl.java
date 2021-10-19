package com.varanegar.framework.base.questionnaire.controls;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.varanegar.framework.R;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/3/2017.
 */

public class BooleanControl extends FormControl {
    @Nullable
    public Boolean value;
    private boolean isChanged;
    private TextView errorTextView;

    @Override
    public boolean isValueChanged() {
        return isChanged;
    }

    public BooleanControl(@NonNull Context context, @NonNull String title, @NonNull UUID uniqueId) {
        super(context, title, uniqueId);
    }

    @Override
    protected void onCreateContentView(@NonNull AppCompatActivity activity, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_control_boolean, parent, true);
        errorTextView = (TextView) view.findViewById(R.id.error_text_view);
        Switch switchButton = (Switch) view.findViewById(R.id.simple_switch);
        switchButton.setTextOn(activity.getString(R.string.yes));
        switchButton.setTextOff(activity.getString(R.string.no));
        if (value != null)
            switchButton.setChecked(value);
        else
            switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChanged = true;
                value = b;
            }
        });
    }

    @Override
    public void setError(String err) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(err);
    }

    @Override
    public void clearError() {
        errorTextView.setText("");
        errorTextView.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public String serializeValue() {
        if (value == null)
            return null;
        else
            return value ? "true" : "false";

    }

    @Override
    public void deserializeValue(@Nullable String value) {
        if (value == null)
            this.value = null;
        else
            this.value = value.equals("true");
    }
}
