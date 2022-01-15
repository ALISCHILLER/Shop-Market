package com.varanegar.supervisor.customreport.orderstatus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.supervisor.R;
import com.varanegar.supervisor.customreport.orderstatus.model.CustomerItem;
import com.varanegar.supervisor.customreport.orderstatus.model.DealersItem;
import com.varanegar.supervisor.customreport.orderstatus.model.IOnExpand;
import com.varanegar.supervisor.customreport.orderstatus.model.OrderStatusReport;

import java.util.ArrayList;
import java.util.List;

public class OrderReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderStatusReport> data;
    private IOnExpand onExpandListener;
    private Context mContext;

    public OrderReportAdapter(Context context,
            List<OrderStatusReport> data,
                              IOnExpand event) {
        this.mContext = context;
        this.data = data;
        this.onExpandListener=event;
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
        viewHolder.date.setText(data.get(position).getDate());
        viewHolder.orderWeight.setText(String.valueOf(data.get(position).getOrderWeight()));
        viewHolder.pendingOrderWeight.setText(String.valueOf(data.get(position).getPendingOrderWeight()));
        viewHolder.inProgressOrderWeight.setText(String.valueOf(data.get(position).getInProgressOrderWeight()));
        viewHolder.undeliverdOrderWeight.setText(String.valueOf(data.get(position).getUndeliverdOrderWeight()));
        viewHolder.finalWeight.setText(String.valueOf(data.get(position).getFinalWeight()));

        if(position==0){
            viewHolder.layout_hedar.setVisibility(View.VISIBLE);
        }else {
            viewHolder.layout_hedar.setVisibility(View.GONE);
        }

        if(data.get(position).isExpand()) {
            List<DealersItem> dealersItem=new ArrayList<>();
            List<CustomerItem> customerItem=new ArrayList<>();
            dealersItem=data.get(position).getDealersItems();
            customerItem=data.get(position).getDealersItems().get(0).getCustomerItems();
            viewHolder.linearLayout.setVisibility(View.VISIBLE);
            viewHolder.txt_dealersItems_data.setText(dealersItem.get(0).getDate());
            viewHolder.txt_dealerName.setText(dealersItem.get(0).getDealerName());
            viewHolder.txt_dealerCode.setText(dealersItem.get(0).getDealerCode());
            viewHolder.txt_pendingOrderWeight.setText(String.valueOf(dealersItem.get(0).getPendingOrderWeight()));
            viewHolder.txt_inProgressOrderWeight.setText(String.valueOf(dealersItem.get(0).getInProgressOrderWeight()));
            viewHolder.txt_undeliverdOrderWeight.setText(String.valueOf(dealersItem.get(0).getUndeliverdOrderWeight()));
            viewHolder.txt_deliverdOrderWeight.setText(String.valueOf(dealersItem.get(0).getDeliverdOrderWeight()));
            viewHolder.txt_customerItems_customerName.setText(String.valueOf(customerItem.get(0).getCustomerName()));
            viewHolder.txt_customerItems_customerCode.setText(""+customerItem.get(0).getCustomerCode());
            viewHolder.txt_customerItems_dealerCode.setText(""+customerItem.get(0).getDealerCode());
            viewHolder.txt_customerItems_pendingOrderWeight.setText(""+customerItem.get(0).getOrderWeight());
            viewHolder.txt_customerItems_pendingOrderWeight.setText(""+customerItem.get(0).getOrderWeight());
            viewHolder.txt_customerItems_inProgressOrderWeight.setText(""+customerItem.get(0).getInProgressOrderWeight());
            viewHolder. txt_customerItems_undeliverdOrderWeight.setText(""+customerItem.get(0).getUndeliverdOrderWeight());
            viewHolder.txt_customerItems_finalWeight.setText(""+customerItem.get(0).getFinalWeight());
            viewHolder.layout_sub_sub.setVisibility(View.VISIBLE);
            viewHolder.layout_header_sub.setVisibility(View.VISIBLE);
        } else {
            viewHolder.linearLayout.setVisibility(View.GONE);
            viewHolder.layout_sub_sub.setVisibility(View.GONE);
            viewHolder.layout_header_sub.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class StatusReportViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView date;
        AppCompatTextView isExpand;
        AppCompatTextView orderWeight;
        AppCompatTextView pendingOrderWeight;
        AppCompatTextView inProgressOrderWeight;
        AppCompatTextView  undeliverdOrderWeight;
        AppCompatTextView  finalWeight;
        AppCompatTextView  txt_dealerCode;
        AppCompatTextView  txt_dealersItems_data;
        AppCompatTextView  txt_dealerName;
        AppCompatTextView   txt_orderWeight;
        AppCompatTextView txt_pendingOrderWeight;
        AppCompatTextView   txt_inProgressOrderWeight;
        AppCompatTextView txt_undeliverdOrderWeight;
        AppCompatTextView txt_deliverdOrderWeight;
        LinearLayout linearLayout;
        LinearLayout layout_super;
        LinearLayout layout_sub_sub;
        LinearLayout layout_hedar;
        LinearLayout layout_header_sub;
        AppCompatTextView  txt_customerItems_dealerCode;
        AppCompatTextView  txt_customerItems_customerName;
        AppCompatTextView txt_customerItems_customerCode;
        AppCompatTextView  txt_customerItems_orderWeight;
        AppCompatTextView txt_customerItems_pendingOrderWeight;
        AppCompatTextView txt_customerItems_inProgressOrderWeight;
        AppCompatTextView   txt_customerItems_undeliverdOrderWeight;
        AppCompatTextView   txt_customerItems_finalWeight;
        public StatusReportViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtDate);
            orderWeight = itemView.findViewById(R.id.txtorderWeight);
            pendingOrderWeight = itemView.findViewById(R.id.txtpendingOrderWeight);
            inProgressOrderWeight = itemView.findViewById(R.id.txtinProgressOrderWeight);
            undeliverdOrderWeight = itemView.findViewById(R.id.txtundeliverdOrderWeight);
            finalWeight = itemView.findViewById(R.id.txtundeliverdOrderWeight);
            linearLayout=itemView.findViewById(R.id.layout_sub);
            txt_dealersItems_data=itemView.findViewById(R.id.txt_dealersItems_data);
            txt_dealerName=itemView.findViewById(R.id.txt_dealerName);
            txt_dealerCode=itemView.findViewById(R.id.txt_dealerCode);
            txt_orderWeight=itemView.findViewById(R.id.txt_orderWeight);
            txt_pendingOrderWeight=itemView.findViewById(R.id.txt_pendingOrderWeight);
            txt_inProgressOrderWeight=itemView.findViewById(R.id.txt_inProgressOrderWeight);
            txt_undeliverdOrderWeight=itemView.findViewById(R.id.txt_undeliverdOrderWeight);
            layout_sub_sub=itemView.findViewById(R.id.layout_sub_sub);
            txt_deliverdOrderWeight=itemView.findViewById(R.id.txt_deliverdOrderWeight);
            layout_super=itemView.findViewById(R.id.layout_super);
            txt_customerItems_dealerCode=itemView.findViewById(R.id.txt_customerItems_dealerCode);
            txt_customerItems_customerName=itemView.findViewById(R.id.txt_customerItems_customerName);
            txt_customerItems_customerCode=itemView.findViewById(R.id.txt_customerItems_customerCode);
            txt_customerItems_orderWeight=itemView.findViewById(R.id.txt_customerItems_orderWeight);
            txt_customerItems_pendingOrderWeight=itemView.findViewById(R.id.txt_customerItems_pendingOrderWeight);
            txt_customerItems_inProgressOrderWeight=itemView.findViewById(R.id.txt_customerItems_inProgressOrderWeight);
            txt_customerItems_undeliverdOrderWeight=itemView.findViewById(R.id.txt_customerItems_undeliverdOrderWeight);
            txt_customerItems_finalWeight=itemView.findViewById(R.id.txt_customerItems_finalWeight);
            layout_hedar=itemView.findViewById(R.id.layout_hedar);
            layout_header_sub=itemView.findViewById(R.id.layout_header_sub);
            layout_super.setOnClickListener(v -> {
                if (getAdapterPosition() == -1 )
                    return;
                onExpandListener.onClick(data.get(getAdapterPosition()),getAdapterPosition());
            });
        }
    }
}
