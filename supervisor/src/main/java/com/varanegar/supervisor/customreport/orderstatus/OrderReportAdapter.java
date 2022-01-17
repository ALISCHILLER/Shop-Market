package com.varanegar.supervisor.customreport.orderstatus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.supervisor.R;
import com.varanegar.supervisor.customreport.orderstatus.model.CustomerItem;
import com.varanegar.supervisor.customreport.orderstatus.model.DealersItem;
import com.varanegar.supervisor.customreport.orderstatus.model.IOnExpand;
import com.varanegar.supervisor.customreport.orderstatus.model.OrderStatusReport;

import java.util.List;

public class OrderReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<OrderStatusReport> data;
    private final IOnExpand onExpandListener;
    private final Context mContext;

    public OrderReportAdapter(
            Context context,
            List<OrderStatusReport> data,
            IOnExpand event) {
        this.data = data;
        this.onExpandListener = event;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.listitem_order_status_report,
                parent, false);
        return new StatusReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StatusReportViewHolder viewHolder = (StatusReportViewHolder) holder;
        viewHolder.bind(data.get(position), position == 0);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class StatusReportViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView date;
        AppCompatTextView orderWeight;
        AppCompatTextView pendingOrderWeight;
        AppCompatTextView inProgressOrderWeight;
        AppCompatTextView undeliverdOrderWeight;
        AppCompatTextView finalWeight;

        AppCompatTextView txt_dealerCode;
        AppCompatTextView txt_dealersItems_data;
        AppCompatTextView txt_dealerName;
        AppCompatTextView txt_orderWeight;
        AppCompatTextView txt_pendingOrderWeight;
        AppCompatTextView txt_inProgressOrderWeight;
        AppCompatTextView txt_undeliverdOrderWeight;
        AppCompatTextView txt_deliverdOrderWeight;
        AppCompatTextView add_item;
        LinearLayout linearLayout;
        LinearLayout layout_super;
        LinearLayout layout_hedar;
        LinearLayout layout_header_sub;
        LinearLayoutCompat pnl_customers;

        public StatusReportViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtDate);
            orderWeight = itemView.findViewById(R.id.txtorderWeight);
            pendingOrderWeight = itemView.findViewById(R.id.txtpendingOrderWeight);
            inProgressOrderWeight = itemView.findViewById(R.id.txtinProgressOrderWeight);
            undeliverdOrderWeight = itemView.findViewById(R.id.txtundeliverdOrderWeight);
            finalWeight = itemView.findViewById(R.id.finalWeight);

            txt_dealersItems_data = itemView.findViewById(R.id.txt_dealersItems_data);
            txt_dealerName = itemView.findViewById(R.id.txt_dealerName);
            txt_dealerCode = itemView.findViewById(R.id.txt_dealerCode);
            txt_orderWeight = itemView.findViewById(R.id.txt_orderWeight);
            txt_pendingOrderWeight = itemView.findViewById(R.id.txt_pendingOrderWeight);
            txt_inProgressOrderWeight = itemView.findViewById(R.id.txt_inProgressOrderWeight);
            txt_undeliverdOrderWeight = itemView.findViewById(R.id.txt_undeliverdOrderWeight);
            txt_deliverdOrderWeight = itemView.findViewById(R.id.txt_deliverdOrderWeight);
            add_item = itemView.findViewById(R.id.add_item);
            layout_super = itemView.findViewById(R.id.layout_super);
            linearLayout = itemView.findViewById(R.id.layout_sub);
            layout_hedar = itemView.findViewById(R.id.layout_hedar);
            layout_header_sub = itemView.findViewById(R.id.layout_header_sub);
            layout_super.setOnClickListener(v -> {
                if (getAdapterPosition() == -1)
                    return;
                onExpandListener.onClick(data.get(getAdapterPosition()), getAdapterPosition());
            });

            pnl_customers = itemView.findViewById(R.id.pnl_customers);
        }

        public void bind(OrderStatusReport item, boolean isFirstItem) {
            date.setText(item.getDate());
            orderWeight.setText(String.valueOf(item.getOrderWeight()));
            pendingOrderWeight.setText(String.valueOf(item
                    .getPendingOrderWeight()));
            inProgressOrderWeight.setText(String.valueOf(item
                    .getInProgressOrderWeight()));
            undeliverdOrderWeight.setText(String.valueOf(item
                    .getUndeliverdOrderWeight()));
            finalWeight.setText(String.valueOf(item.getFinalWeight()));

            if (isFirstItem) {
                layout_hedar.setVisibility(View.VISIBLE);
            } else {
                layout_hedar.setVisibility(View.GONE);
            }

            if (item.isExpand()) {
                List<DealersItem> dealersItem = item.getDealersItems();
                List<CustomerItem> customerItem = item
                        .getDealersItems().get(0).getCustomerItems();
                add_item.setText("-");
                txt_dealersItems_data.setText(dealersItem.get(0).getDate());
                txt_dealerName.setText(dealersItem.get(0).getDealerName());
                txt_dealerCode.setText(dealersItem.get(0).getDealerCode());
                txt_orderWeight.setText(String.valueOf(dealersItem.get(0).getOrderWeight()));
                txt_pendingOrderWeight.setText(String.valueOf(dealersItem.get(0)
                        .getPendingOrderWeight()));
                txt_inProgressOrderWeight.setText(String.valueOf(dealersItem.get(0)
                        .getInProgressOrderWeight()));
                txt_undeliverdOrderWeight.setText(String.valueOf(dealersItem.get(0)
                        .getUndeliverdOrderWeight()));
                txt_deliverdOrderWeight.setText(String.valueOf(dealersItem.get(0)
                        .getDeliverdOrderWeight()));

                linearLayout.setVisibility(View.VISIBLE);
                pnl_customers.setVisibility(View.VISIBLE);
                layout_header_sub.setVisibility(View.VISIBLE);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                pnl_customers.removeAllViews();
                for (CustomerItem customer : customerItem) {
                    View view = LayoutInflater.from(mContext).inflate(
                            R.layout.listitem_order_status_report_customer,
                            pnl_customers, false);

                    AppCompatTextView txt_customerItems_dealerCode;
                    AppCompatTextView txt_customerItems_customerName;
                    AppCompatTextView txt_customerItems_customerCode;
                    AppCompatTextView txt_customerItems_orderWeight;
                    AppCompatTextView txt_customerItems_pendingOrderWeight;
                    AppCompatTextView txt_customerItems_inProgressOrderWeight;
                    AppCompatTextView txt_customerItems_undeliverdOrderWeight;
                    AppCompatTextView txt_customerItems_finalWeight;

                    txt_customerItems_dealerCode = view.findViewById(R.id.txt_customerItems_dealerCode);
                    txt_customerItems_customerName = view.findViewById(R.id.txt_customerItems_customerName);
                    txt_customerItems_customerCode = view.findViewById(R.id.txt_customerItems_customerCode);
                    txt_customerItems_orderWeight = view.findViewById(R.id.txt_customerItems_orderWeight);
                    txt_customerItems_pendingOrderWeight = view.findViewById(R.id.txt_customerItems_pendingOrderWeight);
                    txt_customerItems_inProgressOrderWeight = view.findViewById(R.id.txt_customerItems_inProgressOrderWeight);
                    txt_customerItems_undeliverdOrderWeight = view.findViewById(R.id.txt_customerItems_undeliverdOrderWeight);
                    txt_customerItems_finalWeight = view.findViewById(R.id.txt_customerItems_finalWeight);

                    txt_customerItems_customerName.setText(String.valueOf(customer.getCustomerName()));
                    txt_customerItems_customerCode.setText(customer.getCustomerCode());
                    txt_customerItems_dealerCode.setText(customer.getDealerCode());
                    txt_customerItems_orderWeight.setText(customer.getOrderWeight() + "");
                    txt_customerItems_pendingOrderWeight
                            .setText("" + customer.getPendingOrderWeight());
                    txt_customerItems_inProgressOrderWeight
                            .setText("" + customer.getInProgressOrderWeight());
                    txt_customerItems_undeliverdOrderWeight
                            .setText("" + customer.getUndeliverdOrderWeight());
                    txt_customerItems_finalWeight
                            .setText("" + customer.getFinalWeight());

                    pnl_customers.addView(view, lp);
                }
            } else {
                linearLayout.setVisibility(View.GONE);
                pnl_customers.setVisibility(View.GONE);
                layout_header_sub.setVisibility(View.GONE);
                add_item.setText("+");
            }
        }
    }

    class CustomerStatusReportViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txt_customerItems_dealerCode;
        AppCompatTextView txt_customerItems_customerName;
        AppCompatTextView txt_customerItems_customerCode;
        AppCompatTextView txt_customerItems_orderWeight;
        AppCompatTextView txt_customerItems_pendingOrderWeight;
        AppCompatTextView txt_customerItems_inProgressOrderWeight;
        AppCompatTextView txt_customerItems_undeliverdOrderWeight;
        AppCompatTextView txt_customerItems_finalWeight;

        public CustomerStatusReportViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_customerItems_dealerCode = itemView.findViewById(R.id.txt_customerItems_dealerCode);
            txt_customerItems_customerName = itemView.findViewById(R.id.txt_customerItems_customerName);
            txt_customerItems_customerCode = itemView.findViewById(R.id.txt_customerItems_customerCode);
            txt_customerItems_orderWeight = itemView.findViewById(R.id.txt_customerItems_orderWeight);
            txt_customerItems_pendingOrderWeight = itemView.findViewById(R.id.txt_customerItems_pendingOrderWeight);
            txt_customerItems_inProgressOrderWeight = itemView.findViewById(R.id.txt_customerItems_inProgressOrderWeight);
            txt_customerItems_undeliverdOrderWeight = itemView.findViewById(R.id.txt_customerItems_undeliverdOrderWeight);
            txt_customerItems_finalWeight = itemView.findViewById(R.id.txt_customerItems_finalWeight);
        }

        public void bind(CustomerItem item) {
            txt_customerItems_customerName.setText(item.getCustomerName());
            txt_customerItems_customerCode.setText(item.getCustomerCode());
            txt_customerItems_dealerCode.setText(item.getDealerCode());
            txt_customerItems_orderWeight.setText(item.getOrderWeight() + "");
            txt_customerItems_pendingOrderWeight
                    .setText("" + item.getPendingOrderWeight());
            txt_customerItems_inProgressOrderWeight
                    .setText("" + item.getInProgressOrderWeight());
            txt_customerItems_undeliverdOrderWeight
                    .setText("" + item.getUndeliverdOrderWeight());
            txt_customerItems_finalWeight
                    .setText("" + item.getFinalWeight());
        }
    }
}
