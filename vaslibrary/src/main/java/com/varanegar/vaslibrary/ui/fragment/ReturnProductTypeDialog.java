package com.varanegar.vaslibrary.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercall.ReturnLinesManager;
import com.varanegar.vaslibrary.model.returnType.ReturnType;

import java.util.List;

import varanegar.com.discountcalculatorlib.viewmodel.DiscountCallReturnLineData;

public class ReturnProductTypeDialog extends CuteAlertDialog {
    public List<DiscountCallReturnLineData> DiscountCallReturnLineDataList;
    public ReturnLinesManager.OnSelectReturnTypeResult onSelectReturnTypeResult;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, final ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.return_product_type_fragment, viewGroup, true);
        setSizingPolicy(SizingPolicy.Maximum);
        setTitle(R.string.please_select_return_type_for_prize);
        RecyclerView recyclerView = view.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.color.grey_light, 1));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discount_call_return_line_row, viewGroup, false);
                return new DiscountCallReturnLineRowViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((DiscountCallReturnLineRowViewHolder) holder).onBind(position);
            }

            @Override
            public int getItemCount() {
                return DiscountCallReturnLineDataList == null ? 0 : DiscountCallReturnLineDataList.size();
            }
        });

    }

    @Override
    public void ok() {
        DiscountCallReturnLineData unknown = Linq.findFirst(DiscountCallReturnLineDataList, new Linq.Criteria<DiscountCallReturnLineData>() {
            @Override
            public boolean run(DiscountCallReturnLineData item) {
                return item.ReturnProductTypeId == null;
            }
        });
        if (unknown == null) {
            if (onSelectReturnTypeResult != null)
                onSelectReturnTypeResult.onOk();
            dismiss();
        } else {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(getString(R.string.please_select_return_type));
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    @Override
    public void cancel() {
        if (onSelectReturnTypeResult != null)
            onSelectReturnTypeResult.onCancel();
    }


    private class DiscountCallReturnLineRowViewHolder extends RecyclerView.ViewHolder {
        private final RadioButton healthyRadioBtn;
        private final RadioButton wasteRadioBtn;
        private final TextView productNameTextView;
        private final TextView invoiceRefTextView;

        public DiscountCallReturnLineRowViewHolder(View view) {
            super(view);
            healthyRadioBtn = view.findViewById(R.id.healthy_radio_button);
            wasteRadioBtn = view.findViewById(R.id.waste_radio_button);
            productNameTextView = view.findViewById(R.id.product_name_text_view);
            invoiceRefTextView = view.findViewById(R.id.invoice_ref_text_view);
        }

        public void onBind(final int position) {
            final DiscountCallReturnLineData data = DiscountCallReturnLineDataList.get(position);
            productNameTextView.setText(data.productName);
            invoiceRefTextView.setText(data.referenceNo);
            healthyRadioBtn.setSelected(ReturnType.Well.equals(data.ReturnProductTypeId));
            wasteRadioBtn.setSelected(ReturnType.Waste.equals(data.ReturnProductTypeId));
            healthyRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.ReturnProductTypeId = ReturnType.Well;
                }
            });
            wasteRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.ReturnProductTypeId = ReturnType.Waste;
                }
            });
        }
    }
}
