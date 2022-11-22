package com.varanegar.vaslibrary.ui.calculator;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by atp on 2/5/2017.
 */

public class Calculator extends FrameLayout {
    String strValue = "";
    double dotPosition = -1;
    private BaseUnit selectedUnit;
    private ValuesRecyclerAdapter valuesAdapter;
    private RecyclerView valuesRecyclerView;
    private TextView totalQtyTextView;
    private TextView totalQtyTextViewLabel;
    private TextView qtyTextView;
    private TextView unitTextView;
    private TextView totalUnitTextView;
    private LinearLayout totalQtyLinearLayout;
    private LinearLayout qtyLinearLayout;
    private BaseUnit bulkUnit;


    private boolean isBulk() {
        return (bulkUnit != null && valuesAdapter.getItems().size() == 0);
    }

    private void setBulkUnit(@Nullable BaseUnit baseUnit) {
        this.bulkUnit = baseUnit;
        if (isBulk()) {
            totalQtyLinearLayout.setVisibility(GONE);
        }
    }

    private void setUnits(@NonNull List<DiscreteUnit> units) throws IllegalArgumentException {
        if (units.size() == 0)
            throw new IllegalArgumentException();
        totalQtyLinearLayout.setVisibility(VISIBLE);
        valuesAdapter.setItems(units);
        totalUnitTextView.setText(units.get(units.size() - 1).Unit);
    }

    public void setUnits(CalculatorUnits calculatorUnits) {
        List<DiscreteUnit> units = calculatorUnits.getDiscreteUnits();
        if (units.size() != 0)
            setUnits(units);
        setBulkUnit(calculatorUnits.getBulkUnit());
        setupAdapter();
    }
    public void setvalue(String txt){
        if (checkDecimalPlaces())
        strValue = strValue +txt;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DiscreteUnit unit :
                valuesAdapter.getItems()) {
            total = total.add(unit.getQty().multiply(new BigDecimal(unit.ConvertFactor)));
        }
        return total;
    }

    public boolean isEmpty() {
        BigDecimal total = getTotal();
        BigDecimal bulkQty = getBulkUnit() == null ? BigDecimal.ZERO : getBulkUnit().getQty();
        if (getBulkUnit() == null)
            return total.compareTo(BigDecimal.ZERO) == 0 && bulkQty.compareTo(BigDecimal.ZERO) == 0;
        else if (valuesAdapter.getItems().size() > 0)
            return total.compareTo(BigDecimal.ZERO) == 0 || bulkQty.compareTo(BigDecimal.ZERO) == 0;
        else
            return bulkQty.compareTo(BigDecimal.ZERO) == 0;
    }

    public List<DiscreteUnit> getUnits() {
        return valuesAdapter.getItems();
    }

    @Nullable
    public BaseUnit getBulkUnit() {
        return bulkUnit;
    }

    void setupAdapter() {
        strValue = "0";
        valuesRecyclerView.setAdapter(valuesAdapter);
        valuesAdapter.onItemClick = new ValuesRecyclerAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                qtyLinearLayout.setVisibility(VISIBLE);
                totalQtyTextView.setTypeface(totalQtyTextView.getTypeface(), Typeface.NORMAL);
                totalQtyTextViewLabel.setTypeface(totalQtyTextView.getTypeface(), Typeface.NORMAL);
                totalQtySelected = false;
                if (position == valuesAdapter.getItems().size()) {
                    selectedUnit = bulkUnit;
                } else {
                    selectedUnit = valuesAdapter.get(position);
                }
                if (selectedUnit == null)
                    return;
                qtyTextView.setText(getQtyString());
                unitTextView.setText(selectedUnit.Unit);
                if (selectedUnit.value == 0)
                    strValue = "";
                else {
                    strValue = getQtyString();
                }
            }
        };
        valuesAdapter.notifyDataSetChanged();
        selectedUnit = valuesAdapter.selectDefault();
        if (selectedUnit == null)
            selectedUnit = bulkUnit;
        if (selectedUnit != null) {
            qtyTextView.setText(getQtyString());
            unitTextView.setText(selectedUnit.Unit);
        }
        if (!isBulk())
            totalQtyTextView.setText(calcTotalQty());
    }

    private String getQtyString() {
        if (selectedUnit != null) {
            if (selectedUnit.getClass() == DiscreteUnit.class)
                return Integer.toString((int) selectedUnit.value);
            else
                return Double.toString(selectedUnit.value);
        } else
            return "";
    }

    boolean totalQtySelected = false;

    OnClickListener totalQtyOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!totalQtySelected) {
                qtyLinearLayout.setVisibility(GONE);
                totalQtySelected = true;
                valuesAdapter.deselect();
                selectedUnit = null;
                strValue = calcTotalQty();
                totalQtyTextView.setTypeface(null, Typeface.BOLD);
                totalQtyTextViewLabel.setTypeface(null, Typeface.BOLD);

            }
        }
    };

    class LManager extends LinearLayoutManager {
        public LManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public LManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public LManager(Context context) {
            super(context);
        }

        @Override
        protected boolean isLayoutRTL() {
            return false;
        }
    }

    void inflate() {
        View view = inflate(getContext(), R.layout.layout_calculator, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        valuesRecyclerView = (RecyclerView) view.findViewById(R.id.values_recycler_view);
        valuesRecyclerView.setLayoutManager(new LManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        totalQtyTextView = (TextView) view.findViewById(R.id.total_qty_text_view);
        totalQtyTextViewLabel = (TextView) view.findViewById(R.id.total_qty_text_view_label);
        totalQtyTextViewLabel.setOnClickListener(totalQtyOnClickListener);
        totalQtyTextView.setOnClickListener(totalQtyOnClickListener);
        totalQtyLinearLayout = (LinearLayout) view.findViewById(R.id.total_qty_linear_layout);
        qtyLinearLayout = (LinearLayout) view.findViewById(R.id.qty_linear_layout);
        qtyTextView = (TextView) view.findViewById(R.id.qty_text_view);
        unitTextView = (TextView) view.findViewById(R.id.unit_text_view);
        totalUnitTextView = (TextView) view.findViewById(R.id.total_unit_qty_text_view);
        view.findViewById(R.id.button1).setOnClickListener(numPadListener);
        view.findViewById(R.id.button2).setOnClickListener(numPadListener);
        view.findViewById(R.id.button3).setOnClickListener(numPadListener);
        view.findViewById(R.id.button4).setOnClickListener(numPadListener);
        view.findViewById(R.id.button5).setOnClickListener(numPadListener);
        view.findViewById(R.id.button6).setOnClickListener(numPadListener);
        view.findViewById(R.id.button7).setOnClickListener(numPadListener);
        view.findViewById(R.id.button8).setOnClickListener(numPadListener);
        view.findViewById(R.id.button9).setOnClickListener(numPadListener);
        view.findViewById(R.id.button0).setOnClickListener(numPadListener);
        view.findViewById(R.id.button00).setOnClickListener(numPadListener);
        view.findViewById(R.id.dot).setOnClickListener(numPadListener);
        view.findViewById(R.id.clear_image_button).setOnClickListener(numPadListener);
        view.findViewById(R.id.back_image_button).setOnClickListener(numPadListener);
        view.findViewById(R.id.done_image_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDoneClicked != null)
                    onDoneClicked.onClick(view);
            }
        });
        addView(view);
    }

    public OnClickListener onDoneClicked;
    private OnValueChangeListener onValueChangeListener;

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    public interface OnValueChangeListener {
        void onValueChange(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate();
    }

    public Calculator(Context context) {
        super(context);
        valuesAdapter = new ValuesRecyclerAdapter(this);
    }

    public Calculator(Context context, AttributeSet attrs) {
        super(context, attrs);
        valuesAdapter = new ValuesRecyclerAdapter(this);
    }

    public Calculator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        valuesAdapter = new ValuesRecyclerAdapter(this);
    }

    private boolean checkDecimalPlaces() {
        return dotPosition == -1 || strValue.length() - dotPosition <= 3;
    }

    OnClickListener numPadListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.button1 && checkDecimalPlaces())
                strValue = strValue + "1";
            else if (view.getId() == R.id.button2 && checkDecimalPlaces())
                strValue = strValue + "2";
            else if (view.getId() == R.id.button3 && checkDecimalPlaces())
                strValue = strValue + "3";
            else if (view.getId() == R.id.button4 && checkDecimalPlaces())
                strValue = strValue + "4";
            else if (view.getId() == R.id.button5 && checkDecimalPlaces())
                strValue = strValue + "5";
            else if (view.getId() == R.id.button6 && checkDecimalPlaces())
                strValue = strValue + "6";
            else if (view.getId() == R.id.button7 && checkDecimalPlaces())
                strValue = strValue + "7";
            else if (view.getId() == R.id.button8 && checkDecimalPlaces())
                strValue = strValue + "8";
            else if (view.getId() == R.id.button9 && checkDecimalPlaces())
                strValue = strValue + "9";
            else if (view.getId() == R.id.button0 && checkDecimalPlaces()) {
                if (!strValue.isEmpty())
                    strValue = strValue + "0";
            } else if (view.getId() == R.id.button00 && (dotPosition == -1 || strValue.length() - dotPosition <= 2)) {
                if (!strValue.isEmpty())
                    strValue = strValue + "00";
            } else if (view.getId() == R.id.dot) {
                if (!(strValue.contains("."))) {
                    if (selectedUnit != null) {
                        if (selectedUnit.getClass() == BaseUnit.class) {
                            if (dotPosition == -1) {
                                strValue = strValue + ".";
                                dotPosition = strValue.length() - 1;
                            }
                        }
                    }
                }
            } else if (view.getId() == R.id.back_image_button) {
                int end = strValue.length() - 1;
                if (end <= 0)
                    strValue = "";
                else if (end > 0)
                    strValue = strValue.substring(0, end);
                if (!strValue.contains("."))
                    dotPosition = -1;
            } else if (view.getId() == R.id.clear_image_button) {
                strValue = "";
                dotPosition = -1;
            }
            if (!strValue.isEmpty()) {
                while (!strValue.isEmpty() && strValue.startsWith("0")) {
                    if (strValue.length() > 1 && strValue.charAt(1) != '.')
                        strValue = strValue.replace("0", "");
                    else
                        break;
                }
            }
            if (strValue.isEmpty())
                strValue = "0";

            if (strValue.startsWith("."))
                strValue = "0" + strValue;
            if (selectedUnit != null)
                selectedUnit.value = Double.parseDouble(strValue);
            else if (!isBulk()) {
                totalQtyTextView.setText(strValue);
            }

            if (bulkUnit != null && bulkUnit.Readonly)
                bulkUnit.value = Math.round(totalQty() * 1000.0) / 1000.0;

            valuesAdapter.notifyDataSetChanged();
            if (!totalQtySelected) {
                if (!isBulk())
                    totalQtyTextView.setText(calcTotalQty());
                qtyTextView.setText(strValue);
            } else if (!isBulk()) {
                calcUnits();
            }

            if (onValueChangeListener != null)
                onValueChangeListener.onValueChange(valuesAdapter.getItems(), bulkUnit);

        }
    };

    private void calcUnits() {
        double value = 0;
        if (!strValue.isEmpty())
            value = Double.parseDouble(strValue);
        for (DiscreteUnit unit : valuesAdapter.getItems()) {
            unit.value = (int) value / (int) unit.ConvertFactor;
            value = (int) value % (int) unit.ConvertFactor;
        }
    }

    private String calcTotalQty() {
        double totalQty = 0;
        for (DiscreteUnit item : valuesAdapter.getItems()) {
            totalQty += (item.ConvertFactor * item.value);
        }
        if (totalQty == Math.rint(totalQty))
            return Integer.toString((int) totalQty);
        else
            return HelperMethods.doubleToString(totalQty);
    }

    private double totalQty() {
        double totalQty = 0;
        for (DiscreteUnit item : valuesAdapter.getItems()) {
            totalQty += (item.ConvertFactor * item.value);
        }
        return totalQty;
    }


    @Override
    public void setEnabled(boolean enabled) {
        valuesRecyclerView.setEnabled(enabled);
        totalQtyTextView.setEnabled(enabled);
        totalQtyTextViewLabel.setEnabled(enabled);
        totalQtyLinearLayout.setEnabled(enabled);
        qtyLinearLayout.setEnabled(enabled);
        qtyTextView.setEnabled(enabled);
        unitTextView.setEnabled(enabled);
        totalUnitTextView.setEnabled(enabled);
        findViewById(R.id.button1).setEnabled(enabled);
        findViewById(R.id.button2).setEnabled(enabled);
        findViewById(R.id.button3).setEnabled(enabled);
        findViewById(R.id.button4).setEnabled(enabled);
        findViewById(R.id.button5).setEnabled(enabled);
        findViewById(R.id.button6).setEnabled(enabled);
        findViewById(R.id.button7).setEnabled(enabled);
        findViewById(R.id.button8).setEnabled(enabled);
        findViewById(R.id.button9).setEnabled(enabled);
        findViewById(R.id.button0).setEnabled(enabled);
        findViewById(R.id.button00).setEnabled(enabled);
        findViewById(R.id.dot).setEnabled(enabled);
        findViewById(R.id.clear_image_button).setEnabled(enabled);
        findViewById(R.id.back_image_button).setEnabled(enabled);
        findViewById(R.id.done_image_button).setEnabled(enabled);
        if (!enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ((FrameLayout) findViewById(R.id.main_frame_layout)).setForeground(new ColorDrawable(getContext().getColor(R.color.grey_dark_dark)));
            else
                ((FrameLayout) findViewById(R.id.main_frame_layout)).setForeground(new ColorDrawable(getContext().getResources().getColor(R.color.grey_dark_dark)));
        } else
            ((FrameLayout) findViewById(R.id.main_frame_layout)).setForeground(null);
    }
}

