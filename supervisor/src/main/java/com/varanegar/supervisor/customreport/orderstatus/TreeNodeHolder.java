package com.varanegar.supervisor.customreport.orderstatus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.unnamed.b.atv.model.TreeNode;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.customreport.orderstatus.model.OrderStatusReportFlat;

public class TreeNodeHolder extends TreeNode.BaseNodeViewHolder<OrderStatusReportFlat> {
    private final IOnViewCount onViewCount;

    interface IOnViewCount {
        void viewCount(TreeNode parentNode);
    }

    public TreeNodeHolder(Context context, IOnViewCount _onViewCount) {
        super(context);
        this.onViewCount = _onViewCount;
    }

    @Override
    public View createNodeView(TreeNode node, OrderStatusReportFlat value) {
        if (value.getLevel() == 1) {
            return level1(node,value);
        } else if (value.getLevel() == 2) {
            return level2(value);
        } else {

            if (onViewCount != null)
                onViewCount.viewCount(node.getParent());
            return level3(value);
        }
    }

    private View level3(OrderStatusReportFlat customer) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_order_status_report_customer,
                null, false);

        AppCompatTextView txt_customerItems_dealerCode;
        AppCompatTextView txt_customerItems_customerName;
        AppCompatTextView txt_customerItems_customerCode;
        AppCompatTextView txt_customerItems_orderWeight;
        AppCompatTextView txt_customerItems_pendingOrderWeight;
        AppCompatTextView txt_customerItems_inProgressOrderWeight;
        AppCompatTextView txt_customerItems_undeliverdOrderWeight;
        AppCompatTextView txt_customerItems_finalWeight;

        txt_customerItems_dealerCode = itemView.findViewById(R.id.txt_customerItems_dealerCode);
        txt_customerItems_customerName = itemView.findViewById(R.id.txt_customerItems_customerName);
        txt_customerItems_customerCode = itemView.findViewById(R.id.txt_customerItems_customerCode);
        txt_customerItems_orderWeight = itemView.findViewById(R.id.txt_customerItems_orderWeight);
        txt_customerItems_pendingOrderWeight = itemView.findViewById(R.id.txt_customerItems_pendingOrderWeight);
        txt_customerItems_inProgressOrderWeight = itemView.findViewById(R.id.txt_customerItems_inProgressOrderWeight);
        txt_customerItems_undeliverdOrderWeight = itemView.findViewById(R.id.txt_customerItems_undeliverdOrderWeight);
        txt_customerItems_finalWeight = itemView.findViewById(R.id.txt_customerItems_finalWeight);

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

        return itemView;
    }

    private View level2(OrderStatusReportFlat dealersItem) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_order_status_report_dealer, null, false);

        AppCompatTextView txt_dealerCode;
        AppCompatTextView txt_dealersItems_data;
        AppCompatTextView txt_dealerName;
        AppCompatTextView txt_orderWeight;
        AppCompatTextView txt_pendingOrderWeight;
        AppCompatTextView txt_inProgressOrderWeight;
        AppCompatTextView txt_undeliverdOrderWeight;
        AppCompatTextView txt_deliverdOrderWeight;
        txt_dealersItems_data = itemView.findViewById(R.id.txt_dealersItems_data);
        txt_dealerName = itemView.findViewById(R.id.txt_dealerName);
        txt_dealerCode = itemView.findViewById(R.id.txt_dealerCode);
        txt_orderWeight = itemView.findViewById(R.id.txt_orderWeight);
        txt_pendingOrderWeight = itemView.findViewById(R.id.txt_pendingOrderWeight);
        txt_inProgressOrderWeight = itemView.findViewById(R.id.txt_inProgressOrderWeight);
        txt_undeliverdOrderWeight = itemView.findViewById(R.id.txt_undeliverdOrderWeight);
        txt_deliverdOrderWeight = itemView.findViewById(R.id.txt_deliverdOrderWeight);

        txt_dealersItems_data.setText(dealersItem.getDate());
        txt_dealerName.setText(dealersItem.getDealerName());
        txt_dealerCode.setText(dealersItem.getDealerCode());
        txt_orderWeight.setText(String.valueOf(dealersItem.getOrderWeight()));
        txt_pendingOrderWeight.setText(String.valueOf(dealersItem
                .getPendingOrderWeight()));
        txt_inProgressOrderWeight.setText(String.valueOf(dealersItem
                .getInProgressOrderWeight()));
        txt_undeliverdOrderWeight.setText(String.valueOf(dealersItem
                .getUndeliverdOrderWeight()));
        txt_deliverdOrderWeight.setText(String.valueOf(dealersItem
                .getDeliverdOrderWeight()));

        return itemView;
    }

    private View level1(TreeNode node,OrderStatusReportFlat item) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_order_status_report_parent,
                null, false);


        AppCompatTextView date;
        AppCompatTextView orderWeight;
        AppCompatTextView pendingOrderWeight;
        AppCompatTextView inProgressOrderWeight;
        AppCompatTextView undeliverdOrderWeight;
        AppCompatTextView finalWeight;
        date = itemView.findViewById(R.id.txtDate);
        orderWeight = itemView.findViewById(R.id.txtorderWeight);
        pendingOrderWeight = itemView.findViewById(R.id.txtpendingOrderWeight);
        inProgressOrderWeight = itemView.findViewById(R.id.txtinProgressOrderWeight);
        undeliverdOrderWeight = itemView.findViewById(R.id.txtundeliverdOrderWeight);
        finalWeight = itemView.findViewById(R.id.finalWeight);
        date.setText(item.getDate());
        orderWeight.setText(String.valueOf(item.getOrderWeight()));
        pendingOrderWeight.setText(String.valueOf(item
                .getPendingOrderWeight()));
        inProgressOrderWeight.setText(String.valueOf(item
                .getInProgressOrderWeight()));
        undeliverdOrderWeight.setText(String.valueOf(item
                .getUndeliverdOrderWeight()));
        finalWeight.setText(String.valueOf(item.getFinalWeight()));
        return itemView;
    }
}
