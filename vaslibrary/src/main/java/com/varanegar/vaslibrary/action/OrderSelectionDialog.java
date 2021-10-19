package com.varanegar.vaslibrary.action;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/17/2018.
 */

public class OrderSelectionDialog extends CuteAlertDialog {
    private List<CustomerCallOrderPreviewModel> orders;
    private BaseSelectionRecyclerAdapter<CustomerCallOrderPreviewModel> adapter;
    private UUID customerId;

    public void setOrders(List<CustomerCallOrderPreviewModel> callOrders) {
        this.orders = callOrders;
    }

    public OnOrderSelected onOrderSelected;

    public void setCustomerId(@NonNull UUID customerId) {
        this.customerId = customerId;
    }

    public interface OnOrderSelected {
        void run(UUID orderId);
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.please_select_an_order_or_create_a_new);
        View view = inflater.inflate(R.layout.select_order_layout, viewGroup, true);
        BaseRecyclerView recyclerView = view.findViewById(R.id.orders_base_recycler_view);
        CustomerCallOrderPreviewModel newCallOrderModel = new CustomerCallOrderPreviewModel();
        if (canAddOrder() && !VasHelperMethods.canNotEditOperationAfterPrint(getContext(), customerId))
            orders.add(newCallOrderModel);
        adapter = new BaseSelectionRecyclerAdapter<CustomerCallOrderPreviewModel>(getVaranegarActvity(), orders, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderRowItemViewHolder(view, this, getContext());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private boolean canAddOrder() {
        CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
        List<CustomerCallModel> calls = customerCallManager.loadCalls(customerId);
        boolean isConfirmed = customerCallManager.isConfirmed(calls);
        if (!isConfirmed)
            return true;

        boolean isDataSent = customerCallManager.isDataSent(calls, null);
        return isDataSent && SysConfigManager.isMultipleSendActive(getContext());
    }

    @Override
    public void ok() {
        if (adapter.getSelectedItem() != null) {
            dismiss();
            if (onOrderSelected != null)
                onOrderSelected.run(adapter.getSelectedItem().UniqueId);
        }
    }

    @Override
    public void cancel() {

    }

    class OrderRowItemViewHolder extends BaseViewHolder<CustomerCallOrderPreviewModel> {

        private final TextView rowTextView;
        private final TextView refNumberTextView;
        private final TextView totalAmountTextView;
        private final TextView totalQtyTextView;

        public OrderRowItemViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallOrderPreviewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            rowTextView = (TextView) itemView.findViewById(R.id.row_text_view);
            refNumberTextView = (TextView) itemView.findViewById(R.id.ref_number_text_view);
            totalAmountTextView = (TextView) itemView.findViewById(R.id.total_amount_text_view);
            totalQtyTextView = (TextView) itemView.findViewById(R.id.total_qty_text_view);

        }

        @Override
        public void bindView(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.notifyItemClicked(position);
                }
            });
            CustomerCallOrderPreviewModel order = adapter.get(position);
            if (order == null)
                return;
            if (adapter.getSelectedPosition() == position)
                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light_light));
            else
                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
            if (order.UniqueId == null) {
                rowTextView.setVisibility(View.GONE);
                refNumberTextView.setText(getContext().getString(R.string.new_request));
                return;
            }
            rowTextView.setVisibility(View.VISIBLE);
            rowTextView.setText(String.valueOf(position + 1));
            refNumberTextView.setText(order.LocalPaperNo);
            totalAmountTextView.setText(HelperMethods.currencyToString(order.TotalPrice));
            totalQtyTextView.setText(HelperMethods.bigDecimalToString(order.TotalQty));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }
}
