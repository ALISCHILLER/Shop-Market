package com.varanegar.framework.util.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.varanegar.framework.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/4/2017.
 */

public class PairedItemsSpinner<T> extends LinearLayout {
    private float textSize;
    private int textPadding;
    @DrawableRes
    private int icon;
    private boolean singleLine;
    private String title;
    private int orientation = LinearLayout.HORIZONTAL;
    private TextView titleTextView;
    private TextView valueTextView;
    private boolean inflated = false;
    private List<T> items = new ArrayList<>();
    private T selectedItem = null;
    private ImageView arrowImageView;
    private FragmentManager fragmentManager;
    private OnItemSelectedListener<T> onItemSelectedListener;
    private boolean enabled = true;
    private ImageView iconImageView;
    private int selectedPosition = -1;
    private SearchBox.SearchMethod<T> searchMethod;

    public void setup(FragmentManager fragmentManager, @Nullable List<T> items, @Nullable SearchBox.SearchMethod<T> searchMethod) {
        this.fragmentManager = fragmentManager;
        if (items != null)
            this.items = items;
        this.searchMethod = searchMethod;
    }

    public void setTitle(String title) {
        this.title = title;
        if (inflated) {
            iconImageView.setVisibility(GONE);
            titleTextView.setText(title);
            titleTextView.setVisibility(VISIBLE);
        }
    }

    public void selectItem(int position) {
        selectedItem = items.get(position);
        selectedPosition = position;
        if (inflated) {
            valueTextView.setError(null);
            valueTextView.setText(items.get(position).toString());
        }
    }

    public void deselect() {
        selectedItem = null;
        selectedPosition = -1;
        if (inflated) {
            valueTextView.setError(null);
            valueTextView.setText("");
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Nullable
    public T getSelectedItem() {
        return selectedItem;
    }

    public void setIconResource(@DrawableRes int icon) {
        this.icon = icon;
        if (inflated) {
            iconImageView.setVisibility(VISIBLE);
            iconImageView.setImageResource(icon);
            titleTextView.setVisibility(GONE);
        }
    }

    public List<T> getItems() {
        return items;
    }

    public interface OnItemSelectedListener<T> {
        void onItemSelected(int position, T item);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private OnClickListener onClickListener;

    public void setOnSpinnerClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = null;
        if (orientation == LinearLayout.HORIZONTAL)
            view = inflate(getContext(), R.layout.item_pair_spinner_single_layout, this);
        else
            view = inflate(getContext(), R.layout.item_pair_spinner_layout, this);
        titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        valueTextView = (TextView) view.findViewById(R.id.value_text_view);
        valueTextView.setTextColor(Color.BLACK);
        arrowImageView = (ImageView) view.findViewById(R.id.arrow_image_view);
        iconImageView = (ImageView) view.findViewById(R.id.icon_image_view);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enabled) {
                    if (onClickListener != null)
                        onClickListener.onClick(PairedItemsSpinner.this);
                }
            }
        });
        arrowImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enabled) {
                    if (fragmentManager == null)
                        return;
                    final SearchBox<T> searchBox = new SearchBox<T>();
                    if (searchMethod == null) {
                        searchBox.setItems(items, new SearchBox.SearchMethod<T>() {
                            @Override
                            public boolean onSearch(T item, String text) {
                                text = text.toLowerCase();
                                return item.toString().toLowerCase().contains(text);
                            }
                        });
                        searchBox.disableSearch();
                    } else
                        searchBox.setItems(items, searchMethod);
                    searchBox.setOnItemSelectedListener(new SearchBox.OnItemSelectedListener<T>() {
                        @Override
                        public void run(int position, T item) {
                            selectedPosition = position;
                            selectedItem = item;
                            valueTextView.setError(null);
                            valueTextView.setText(item.toString());
                            searchBox.dismiss();
                            if (onItemSelectedListener != null)
                                onItemSelectedListener.onItemSelected(position, item);
                        }
                    });
                    searchBox.show(fragmentManager, "eed36b22-3a64-469e-ac40-5d21c9ec5ffe");
                }

            }
        });

        valueTextView.setSingleLine(singleLine);
        if (title == null)
            titleTextView.setVisibility(GONE);
        else {
            titleTextView.setVisibility(VISIBLE);
            titleTextView.setText(title);
        }
        if (icon == 0)
            iconImageView.setVisibility(GONE);
        else {
            iconImageView.setVisibility(VISIBLE);
            iconImageView.setImageResource(icon);
        }
        titleTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
        valueTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
        if (textSize != -1) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        inflated = true;
    }


    public void setError(String message) {
        valueTextView.setError(message);
        if (message != null)
            valueTextView.setText(message);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
    }


    private static class SavedState extends BaseSavedState {
        public int selectedPosition = -1;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selectedPosition);
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
        ss.selectedPosition = this.selectedPosition;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            if (!(state instanceof SavedState)) {
                super.onRestoreInstanceState(state);
                return;
            }
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            if (ss.selectedPosition >= 0 && items.size() > ss.selectedPosition) {
                selectedPosition = ss.selectedPosition;
                selectedItem = items.get(selectedPosition);
                if (valueTextView != null)
                    valueTextView.setText(selectedItem.toString());
            }
        } catch (Error error) {
            Timber.e(error);
        }
    }

    public PairedItemsSpinner(Context context) {
        super(context);
    }

    public PairedItemsSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PairedItemsSpinner, 0, 0);
        try {
            title = a.getString(R.styleable.PairedItemsSpinner_title);
            orientation = a.getInt(R.styleable.PairedItemsSpinner_android_orientation, LinearLayout.HORIZONTAL);
            singleLine = a.getBoolean(R.styleable.PairedItemsSpinner_singleLine, false);
            icon = a.getResourceId(R.styleable.PairedItemsSpinner_icon, 0);
            textPadding = a.getDimensionPixelOffset(R.styleable.PairedItemsSpinner_textPadding, getContext().getResources().getDimensionPixelSize(R.dimen.padding_default));
            textSize = a.getDimension(R.styleable.PairedItemsSpinner_textSize, -1);
        } finally {
            a.recycle();
        }
    }

    public PairedItemsSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PairedItemsSpinner, 0, 0);
        try {
            title = a.getString(R.styleable.PairedItemsSpinner_title);
            orientation = a.getInt(R.styleable.PairedItemsSpinner_android_orientation, LinearLayout.HORIZONTAL);
            singleLine = a.getBoolean(R.styleable.PairedItemsSpinner_singleLine, false);
            icon = a.getResourceId(R.styleable.PairedItemsSpinner_icon, 0);
            textPadding = a.getDimensionPixelOffset(R.styleable.PairedItemsSpinner_textPadding, getContext().getResources().getDimensionPixelSize(R.dimen.padding_default));
            textSize = a.getDimension(R.styleable.PairedItemsSpinner_textSize, -1);
        } finally {
            a.recycle();
        }
    }
}
