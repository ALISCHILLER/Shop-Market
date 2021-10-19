package com.varanegar.vaslibrary.action;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoicePreviewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/17/2018.
 */

public class InvoiceSelectionDialog extends CuteAlertDialog {
    private List<CustomerCallInvoicePreviewModel> orders;
    private BaseSelectionRecyclerAdapter<CustomerCallInvoicePreviewModel> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSizingPolicy(SizingPolicy.Maximum);
    }

    public void setOrders(List<CustomerCallInvoicePreviewModel> callOrders) {
        this.orders = callOrders;
    }

    public OnOrderSelected onOrderSelected;

    public interface OnOrderSelected {
        void run(UUID orderId);
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.invoice_selection);
        View view = inflater.inflate(R.layout.select_invoice_layout, viewGroup, true);
        BaseRecyclerView recyclerView = view.findViewById(R.id.orders_base_recycler_view);
        adapter = new BaseSelectionRecyclerAdapter<CustomerCallInvoicePreviewModel>(getVaranegarActvity(), orders, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item, parent, false);
                return new OrderRowItemViewHolder(view, this, getContext());
            }
        };
        recyclerView.setAdapter(adapter);
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

    class OrderRowItemViewHolder extends BaseViewHolder<CustomerCallInvoicePreviewModel> {

        private final TextView rowTextView;
        private final TextView refNumberTextView;
        private final TextView totalAmountTextView;
        private final TextView totalQtyTextView;
        private final TextView statusTextView;

        public OrderRowItemViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallInvoicePreviewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            rowTextView = (TextView) itemView.findViewById(R.id.row_text_view);
            refNumberTextView = (TextView) itemView.findViewById(R.id.ref_number_text_view);
            totalAmountTextView = (TextView) itemView.findViewById(R.id.total_amount_text_view);
            totalQtyTextView = (TextView) itemView.findViewById(R.id.total_qty_text_view);
            statusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
        }

        @Override
        public void bindView(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.notifyItemClicked(position);
                }
            });
            CustomerCallInvoicePreviewModel order = adapter.get(position);
            if (order == null)
                return;
            if (adapter.getSelectedPosition() == position)
                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light_light));
            else
                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));

            rowTextView.setVisibility(View.VISIBLE);
            rowTextView.setText(String.valueOf(position + 1));
            if (order.IsInvoice)
                refNumberTextView.setText(getContext().getString(R.string.invoice_no) + " " + order.SaleNoSDS);
            else
                refNumberTextView.setText(getContext().getString(R.string.draft_no) + " " + order.SaleNoSDS);
            totalAmountTextView.setText(HelperMethods.currencyToString(order.TotalPrice));
            totalQtyTextView.setText(HelperMethods.bigDecimalToString(order.TotalQty));
            if (order.CallType == CustomerCallType.OrderDelivered) {
                statusTextView.setText(getContext().getString(R.string.delivered_complete));
                statusTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.green));
            } else if (order.CallType == CustomerCallType.OrderPartiallyDelivered) {
                statusTextView.setText(getContext().getString(R.string.delivered_partially));
                statusTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.orange_light));
            } else if (order.CallType == CustomerCallType.OrderReturn) {
                statusTextView.setText(getContext().getString(R.string.complete_return));
                statusTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.red));
            } else if (order.CallType == CustomerCallType.OrderLackOfDelivery) {
                statusTextView.setText(getContext().getString(R.string.lack_of_delivery));
                statusTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.orange));
            } else {
                statusTextView.setText(getContext().getString(R.string.unKnown));
                statusTextView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey));
            }
        }
    }
}
