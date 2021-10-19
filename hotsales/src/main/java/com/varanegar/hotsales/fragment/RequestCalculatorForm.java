package com.varanegar.hotsales.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.hotsales.R;
import com.varanegar.vaslibrary.manager.WarehouseProductQtyViewManager;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.Calculator;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.util.List;

/**
 * Created by A.Torabi on 3/25/2018.
 */

public class RequestCalculatorForm extends CuteDialog {
    private CalculatorUnits calculatorUnits;
    private ProductModel productModel;
    private Calculator calculator;

    public RequestCalculatorForm() {
        setSizingPolicy(SizingPolicy.Medium);
    }

    public void setArguments(@NonNull ProductModel productModel, @NonNull CalculatorUnits calculatorUnits) {
        this.calculatorUnits = calculatorUnits;
        this.productModel = productModel;
    }

    public interface OnCalcFinish {
        void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    public OnCalcFinish onCalcFinish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_request_product_calculator_dialog, container, false);
        view.findViewById(R.id.close_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView inventoryQty = view.findViewById(R.id.inventory_qty_text_view);
        WarehouseProductQtyViewModel warehouseProductQtyViewModel = new WarehouseProductQtyViewManager(getContext()).getItem(WarehouseProductQtyViewManager.getWarehouseProductQty(productModel.UniqueId));
        if (warehouseProductQtyViewModel != null)
            inventoryQty.setText(warehouseProductQtyViewModel.RemainedQtyView);

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        productNameTextView.setText(productModel.ProductName);
        calculator = (Calculator) view.findViewById(R.id.calculator);
        calculator.setUnits(calculatorUnits);
        calculator.onDoneClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculator.isEmpty()) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(R.string.please_input_qty);
                    dialog.setTitle(R.string.error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                } else {
                    dismiss();
                    if (onCalcFinish != null)
                        onCalcFinish.run(calculator.getUnits(), calculator.getBulkUnit());
                }
            }
        };
        return view;
    }

}
