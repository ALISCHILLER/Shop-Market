package com.varanegar.framework.base.questionnaire.controls;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.varanegar.framework.R;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/1/2017.
 */

public class TextControl extends FormControl {
    private EditText editText;
    @Nullable
    public String value;
    private boolean isChanged;

    @Override
    public boolean isValueChanged() {
        return isChanged;
    }

    public TextControl(@NonNull Context context, @NonNull String title, @NonNull UUID uniqueId) {
        super(context, title, uniqueId);
    }

    @Override
    protected void onCreateContentView(@NonNull AppCompatActivity activity, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_control_text, parent, true);
        editText = (EditText) view.findViewById(R.id.value_edit_text);
        if (value == null)
            editText.setText("");
        else
            editText.setText(value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isChanged = true;
                value = s.toString();
            }
        });
    }

    @Override
    public void setError(String err) {
        editText.requestFocus();
        editText.setError(err);
    }

    @Override
    public void clearError() {
        editText.setError(null);
    }

    @Nullable
    @Override
    public String serializeValue() {
        return value;
    }

    @Override
    public void deserializeValue(@Nullable String value) {
        this.value = value;
    }


}
