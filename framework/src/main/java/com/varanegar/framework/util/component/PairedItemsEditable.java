package com.varanegar.framework.util.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.util.HelperMethods;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by A.Jafarzadeh on 04/04/2017.
 */

public class PairedItemsEditable extends LinearLayout {
    private float textSize;
    private int textPadding;
    private int maxLength;
    private boolean singleLine;
    private int inputType;
    private String value;
    private String codeValue;
    private String title;
    private int orientation = LinearLayout.HORIZONTAL;

    TextView titleTextView;
    EditText valueEditText;
    private EditText codeValueEditText;
    private boolean inflated = false;
    private boolean isCommaSeparated = false;
    private boolean isEnabled = true;
    private boolean isPhone;


    public void setTitle(String title) {
        this.title = title;
        if (inflated)
            titleTextView.setText(title);
    }

    public void setValue(String value) {
        if (isPhone) {
            if (value == null) {
                this.value = null;
                if (inflated)
                    valueEditText.setText(null);
                this.codeValue = null;
                if (inflated)
                    codeValueEditText.setText(null);
            } else {
                String[] splits = value.split(" ");
                if (splits.length == 2) {
                    this.value = splits[1];
                    if (inflated)
                        valueEditText.setText(splits[1]);
                    this.codeValue = splits[0];
                    if (inflated)
                        codeValueEditText.setText(splits[0]);
                } else if (splits.length == 1) {
                    this.value = splits[0];
                    if (inflated)
                        valueEditText.setText(splits[0]);
                    this.codeValue = null;
                    if (inflated)
                        codeValueEditText.setText(null);
                }
            }
        } else {
            this.value = value;
            if (inflated)
                valueEditText.setText(value);
        }
    }

    @Nullable
    public String getValue() {
        if (isPhone) {
            return codeValue + " " + value;
        } else
            return value;
    }

    private static class SavedState extends BaseSavedState {
        String value;
        String codeValue;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.value = in.readString();
            this.codeValue = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.value);
            out.writeString(this.codeValue);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.value = this.value;
        ss.codeValue = this.codeValue;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        value = ss.value;
        codeValue = ss.codeValue;
        if (valueEditText != null)
            valueEditText.setText(value);
        if (codeValueEditText != null)
            codeValueEditText.setText(codeValue);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setValue(value);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        isEnabled = enabled;
        if (valueEditText != null)
            valueEditText.setEnabled(isEnabled);
        if (codeValueEditText != null)
            codeValueEditText.setEnabled(isEnabled);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = null;
        if (isPhone) {
            if (orientation == LinearLayout.HORIZONTAL)
                view = inflate(getContext(), R.layout.item_pair_phone_editable_single_layout, this);
            else
                view = inflate(getContext(), R.layout.item_pair_phone_editable_layout, this);
        } else {
            if (orientation == LinearLayout.HORIZONTAL)
                view = inflate(getContext(), R.layout.item_pair_editable_single_layout, this);
            else
                view = inflate(getContext(), R.layout.item_pair_editable_layout, this);
        }
        titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        if (isPhone)
            codeValueEditText = (EditText) view.findViewWithTag("e7b1febc-0243-4ac5-a97c-eea7125a5bd7");
        valueEditText = (EditText) view.findViewWithTag("7cc7c054-f0b6-41ef-a97c-d843e672d16b");
        valueEditText.setTextColor(Color.BLACK);
        if (!isCommaSeparated)
            valueEditText.setInputType(inputType);
        else {
            valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            valueEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));
        }
        if (((InputType.TYPE_TEXT_VARIATION_PASSWORD
                | InputType.TYPE_NUMBER_VARIATION_PASSWORD
                | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) & inputType) == 0)
            valueEditText.setSingleLine(singleLine);
        titleTextView.setText(title);
        valueEditText.setText(value);
        valueEditText.setEnabled(isEnabled);
        if (maxLength != 0)
            valueEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                value = valueEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (isPhone) {
            codeValueEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    codeValue = codeValueEditText.getText().toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        for (TextWatcher textWatcher :
                textWatchers) {
            valueEditText.addTextChangedListener(textWatcher);
        }
        if (isCommaSeparated) {
            valueEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    valueEditText.removeTextChangedListener(this);
                    String str = s.toString().replaceAll("[^\\d]", "");
                    if (str != null && !str.isEmpty()) {
                        str = HelperMethods.convertToEnglishNumbers(str);
                        if (str != null && !str.isEmpty()) {
                            double s1 = Double.parseDouble(str);
                            NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
                            ((DecimalFormat) nf2).applyPattern("###,###.###");
                            String result = nf2.format(s1);
                            s.replace(0, s.length(), result);
                        }
                    }
                    valueEditText.addTextChangedListener(this);
                }
            });
        }
        titleTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
        valueEditText.setPadding(textPadding, textPadding, textPadding, textPadding);
        if (isPhone)
            codeValueEditText.setPadding(textPadding, textPadding, textPadding, textPadding);

        if (textSize != -1) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            valueEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            if (isPhone)
                codeValueEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        inflated = true;
    }

    private List<TextWatcher> textWatchers = new ArrayList<>();

    public void setError(String message) {
        valueEditText.setError(message);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        this.textWatchers.add(textWatcher);
        if (valueEditText != null) {
            valueEditText.addTextChangedListener(textWatcher);
        }
    }

    public PairedItemsEditable(Context context) {
        super(context);
    }

    public PairedItemsEditable(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PairedItemsEditable, 0, 0);
        try {
            title = a.getString(R.styleable.PairedItemsEditable_title);
            value = a.getString(R.styleable.PairedItemsEditable_value);
            orientation = a.getInt(R.styleable.PairedItemsEditable_android_orientation, LinearLayout.HORIZONTAL);
            inputType = a.getInt(R.styleable.PairedItemsEditable_android_inputType, InputType.TYPE_CLASS_TEXT);
            maxLength = a.getInt(R.styleable.PairedItemsEditable_android_maxLength, 0);
            singleLine = a.getBoolean(R.styleable.PairedItemsEditable_singleLine, false);
            isCommaSeparated = a.getBoolean(R.styleable.PairedItemsEditable_isCommaSeparated, false);
            isPhone = a.getBoolean(R.styleable.PairedItemsEditable_isPhone, false);
            textPadding = a.getDimensionPixelOffset(R.styleable.PairedItemsEditable_textPadding, getContext().getResources().getDimensionPixelSize(R.dimen.padding_default));
            textSize = a.getDimension(R.styleable.PairedItemsEditable_textSize, -1);
        } finally {
            a.recycle();
        }
    }

    public PairedItemsEditable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PairedItemsEditable, 0, 0);
        try {
            title = a.getString(R.styleable.PairedItemsEditable_title);
            value = a.getString(R.styleable.PairedItemsEditable_value);
            orientation = a.getInt(R.styleable.PairedItemsEditable_android_orientation, LinearLayout.HORIZONTAL);
            inputType = a.getInt(R.styleable.PairedItemsEditable_android_inputType, InputType.TYPE_CLASS_TEXT);
            maxLength = a.getInt(R.styleable.PairedItemsEditable_android_maxLength, 0);
            singleLine = a.getBoolean(R.styleable.PairedItemsEditable_singleLine, false);
            isCommaSeparated = a.getBoolean(R.styleable.PairedItemsEditable_isCommaSeparated, false);
            isPhone = a.getBoolean(R.styleable.PairedItemsEditable_isPhone, false);
            textPadding = a.getDimensionPixelSize(R.styleable.PairedItemsEditable_textPadding, getContext().getResources().getDimensionPixelSize(R.dimen.padding_default));
            textSize = a.getDimension(R.styleable.PairedItemsEditable_textSize, -1);
        } finally {
            a.recycle();
        }
    }

    public void removeTextChangedListener(TextWatcher textWatcher) {
        if (textWatchers.contains(textWatcher))
            textWatchers.remove(textWatcher);
        if (valueEditText != null)
            valueEditText.removeTextChangedListener(textWatcher);
    }
}