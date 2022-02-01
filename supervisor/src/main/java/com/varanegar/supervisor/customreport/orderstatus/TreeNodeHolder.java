package com.varanegar.supervisor.customreport.orderstatus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.unnamed.b.atv.model.TreeNode;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.customreport.orderreturn.model.ReturnReportFlat;
import com.varanegar.supervisor.customreport.orderstatus.model.OrderStatusReportFlat;

import java.text.DecimalFormat;
import java.util.List;

public class TreeNodeHolder extends TreeNode.BaseNodeViewHolder<OrderStatusReportFlat> {
    private final IOnViewCount onViewCount;
    private int i;
    private List<OrderStatusReportFlat> _data;
    interface IOnViewCount {
        void viewCount(TreeNode parentNode);
    }

    public TreeNodeHolder(Context context,List<OrderStatusReportFlat> data, IOnViewCount _onViewCount) {
        super(context);
        this.onViewCount = _onViewCount;
        _data = data;
    }

    @Override
    public View createNodeView(TreeNode node, OrderStatusReportFlat value) {
        if (value.getLevel() == 1) {
            i++;
            return level1(node,value);
        } else if (value.getLevel() == 2) {
            return level2(node,value);
        } else {

            if (onViewCount != null)
                onViewCount.viewCount(node.getParent());
            return level3(node,value);
        }
    }

    private View level3(TreeNode node,OrderStatusReportFlat customer) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_order_status_report_customer,
                null, false);
        DecimalFormat df=new DecimalFormat("###.##");

        AppCompatTextView txt_customerItems_customerName;
        AppCompatTextView txt_customerItems_customerCode;
        AppCompatTextView txt_customerItems_orderWeight;
        AppCompatTextView txt_customerItems_pendingOrderWeight;
        AppCompatTextView txt_customerItems_inProgressOrderWeight;
        AppCompatTextView txt_customerItems_undeliverdOrderWeight;
        AppCompatTextView txt_customerItems_finalWeight;


        txt_customerItems_customerName = itemView.findViewById(R.id.txt_customerItems_customerName);
        txt_customerItems_customerCode = itemView.findViewById(R.id.txt_customerItems_customerCode);
        txt_customerItems_orderWeight = itemView.findViewById(R.id.txt_customerItems_orderWeight);
      //  txt_customerItems_pendingOrderWeight = itemView.findViewById(R.id.txt_customerItems_pendingOrderWeight);
        txt_customerItems_inProgressOrderWeight = itemView.findViewById(R.id.txt_customerItems_inProgressOrderWeight);
    //    txt_customerItems_undeliverdOrderWeight = itemView.findViewById(R.id.txt_customerItems_undeliverdOrderWeight);
        txt_customerItems_finalWeight = itemView.findViewById(R.id.txt_customerItems_finalWeight);

        txt_customerItems_customerName.setText(String.valueOf(customer.getCustomerName()));
        txt_customerItems_customerCode.setText(customer.getCustomerCode());

        txt_customerItems_orderWeight.setText(df.format(customer.getOrderWeight()));
      //  txt_customerItems_pendingOrderWeight
       //         .setText(df.format( customer.getPendingOrderWeight()));
        txt_customerItems_inProgressOrderWeight
                .setText(df.format(customer.getInProgressOrderWeight()));
      //  txt_customerItems_undeliverdOrderWeight
               // .setText(df.format( customer.getUndeliverdOrderWeight()));
        txt_customerItems_finalWeight
                .setText(df.format( customer.getFinalWeight()));



        AppCompatTextView txt_sum_orderWeight;
        AppCompatTextView txt_sum_pendingOrderWeight;
        AppCompatTextView txt_sum_inProgressOrderWeight;
        AppCompatTextView txt_sum_undeliverdOrderWeight;
        AppCompatTextView txt_sum_finalWeight;
        LinearLayout layout_footer_order;

        txt_sum_orderWeight = itemView.findViewById(R.id.txt_sum_orderWeight);
      //  txt_sum_pendingOrderWeight = itemView.findViewById(R.id.txt_sum_pendingOrderWeight);
        txt_sum_inProgressOrderWeight = itemView.findViewById(R.id.txt_sum_inProgressOrderWeight);
      //  txt_sum_undeliverdOrderWeight = itemView.findViewById(R.id.txt_sum_undeliverdOrderWeight);
        txt_sum_finalWeight = itemView.findViewById(R.id.txt_sum_finalWeight);
        layout_footer_order= itemView.findViewById(R.id.layout_footer_order);

        if(node.getId() == node.getParent().getChildren().size()) {

            double sum_orderWeight= 0;
            double sum_pendingOrderWeightt= 0;
            double sum_inProgressOrderWeight= 0;
            double sum_undeliverdOrderWeight= 0;
            double sum_finalWeight= 0;

            for (OrderStatusReportFlat itemSub : ((OrderStatusReportFlat) node.getParent().getValue()).getChilds()) {
                sum_orderWeight += itemSub.getOrderWeight();
                sum_pendingOrderWeightt+= itemSub.getPendingOrderWeight();
                sum_inProgressOrderWeight+= itemSub.getInProgressOrderWeight();
                sum_undeliverdOrderWeight+= itemSub.getUndeliverdOrderWeight();
                sum_finalWeight+= itemSub.getFinalWeight();

            }
            txt_sum_orderWeight.setText(df.format(sum_orderWeight));
          //  txt_sum_pendingOrderWeight.setText(df.format(sum_pendingOrderWeightt));
            txt_sum_inProgressOrderWeight.setText(df.format(sum_inProgressOrderWeight));
           // txt_sum_undeliverdOrderWeight.setText(df.format(sum_undeliverdOrderWeight));
            txt_sum_finalWeight.setText(df.format(sum_finalWeight));
            layout_footer_order.setVisibility(View.VISIBLE);
        }else {
            layout_footer_order.setVisibility(View.GONE);
        }


        return itemView;
    }

    private View level2(TreeNode node,OrderStatusReportFlat dealersItem) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_order_status_report_dealer, null, false);
        AppCompatTextView txt_dealerCode;
        AppCompatTextView txt_dealerName;
        AppCompatTextView txt_orderWeight;
        AppCompatTextView txt_pendingOrderWeight;
        AppCompatTextView txt_inProgressOrderWeight;
       // AppCompatTextView txt_undeliverdOrderWeight;
        AppCompatTextView txt_deliverdOrderWeight;


        txt_dealerName = itemView.findViewById(R.id.txt_dealerName);
        txt_dealerCode = itemView.findViewById(R.id.txt_dealerCode);
        txt_orderWeight = itemView.findViewById(R.id.txt_orderWeight);
      //  txt_pendingOrderWeight = itemView.findViewById(R.id.txt_pendingOrderWeight);
        txt_inProgressOrderWeight = itemView.findViewById(R.id.txt_inProgressOrderWeight);
       // txt_undeliverdOrderWeight = itemView.findViewById(R.id.txt_undeliverdOrderWeight);
        txt_deliverdOrderWeight = itemView.findViewById(R.id.txt_deliverdOrderWeight);


        txt_dealerName.setText(dealersItem.getDealerName());
        txt_dealerCode.setText(dealersItem.getDealerCode());
        DecimalFormat df=new DecimalFormat("###.##");

        txt_orderWeight
                .setText(String.valueOf(df.format(dealersItem.getOrderWeight())));

      //  txt_pendingOrderWeight
       //         .setText(String.valueOf(df.format(dealersItem.getPendingOrderWeight())));

        txt_inProgressOrderWeight
                .setText(String.valueOf(df.format(dealersItem.getInProgressOrderWeight())));

       // txt_undeliverdOrderWeight
        //        .setText(String.valueOf(df.format(dealersItem.getUndeliverdOrderWeight())));

        txt_deliverdOrderWeight
                .setText(String.valueOf(df.format(dealersItem.getDeliverdOrderWeight())));


        AppCompatTextView txt_sum_orderWeight;
        AppCompatTextView txt_sum_pendingOrderWeight;
        AppCompatTextView txt_sum_inProgressOrderWeight;
        AppCompatTextView txt_sum_undeliverdOrderWeight;
        AppCompatTextView txt_sum_finalWeight;
        LinearLayout layout_footer_order;

        txt_sum_orderWeight = itemView.findViewById(R.id.txt_sum_orderWeight);
      //  txt_sum_pendingOrderWeight = itemView.findViewById(R.id.txt_sum_pendingOrderWeight);
        txt_sum_inProgressOrderWeight = itemView.findViewById(R.id.txt_sum_inProgressOrderWeight);
    //    txt_sum_undeliverdOrderWeight = itemView.findViewById(R.id.txt_sum_undeliverdOrderWeight);
        txt_sum_finalWeight = itemView.findViewById(R.id.txt_sum_finalWeight);

        layout_footer_order= itemView.findViewById(R.id.layout_footer_order);
        if(node.getId() == node.getParent().getChildren().size()) {

            double sum_orderWeight= 0;
            double sum_pendingOrderWeightt= 0;
            double sum_inProgressOrderWeight= 0;
            double sum_undeliverdOrderWeight= 0;
            double sum_finalWeight= 0;
            for (OrderStatusReportFlat itemSub : ((OrderStatusReportFlat) node.getParent().getValue()).getChilds()) {
                sum_orderWeight += itemSub.getOrderWeight();
                sum_pendingOrderWeightt+= itemSub.getPendingOrderWeight();
                sum_inProgressOrderWeight+= itemSub.getInProgressOrderWeight();
                sum_undeliverdOrderWeight+= itemSub.getUndeliverdOrderWeight();
                sum_finalWeight+= itemSub.getDeliverdOrderWeight();

            }
            txt_sum_orderWeight.setText(df.format(sum_orderWeight));
          //  txt_sum_pendingOrderWeight.setText(df.format(sum_pendingOrderWeightt));
            txt_sum_inProgressOrderWeight.setText(df.format(sum_inProgressOrderWeight));
          //  txt_sum_undeliverdOrderWeight.setText(df.format(sum_undeliverdOrderWeight));
            txt_sum_finalWeight.setText(df.format(sum_finalWeight));
            layout_footer_order.setVisibility(View.VISIBLE);
        }else {
            layout_footer_order.setVisibility(View.GONE);
        }




        return itemView;
    }

    private View level1(TreeNode node,OrderStatusReportFlat item) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_order_status_report_parent,
                null, false);


        AppCompatTextView date;
        AppCompatTextView orderWeight;
       // AppCompatTextView pendingOrderWeight;
        AppCompatTextView inProgressOrderWeight;
      //  AppCompatTextView undeliverdOrderWeight;
        AppCompatTextView finalWeight;

        AppCompatTextView txt_reportCode;


        date = itemView.findViewById(R.id.txtDate);
        orderWeight = itemView.findViewById(R.id.txtorderWeight);
       // pendingOrderWeight = itemView.findViewById(R.id.txtpendingOrderWeight);
        inProgressOrderWeight = itemView.findViewById(R.id.txtinProgressOrderWeight);
       // undeliverdOrderWeight = itemView.findViewById(R.id.txtundeliverdOrderWeight);
        finalWeight = itemView.findViewById(R.id.finalWeight);
        txt_reportCode= itemView.findViewById(R.id.txt_reportCode);

        DecimalFormat df=new DecimalFormat("###.##");

        date.setText(item.getDate());

        orderWeight.setText(String.valueOf(df.format(item.getOrderWeight())));

       /// pendingOrderWeight.setText(String.valueOf(df.format(item
            //    .getPendingOrderWeight())));

        inProgressOrderWeight.setText(String.valueOf(df.format(item
                .getInProgressOrderWeight())));

       // undeliverdOrderWeight.setText(String.valueOf(df.format(item
            //    .getUndeliverdOrderWeight())));

        finalWeight.setText(String.valueOf(df.format(item.getFinalWeight())));

        txt_reportCode.setText(String.valueOf(node.getId()));

        AppCompatTextView txt_sum_orderWeight;
        AppCompatTextView txt_sum_pendingOrderWeight;
        AppCompatTextView txt_sum_inProgressOrderWeight;
        AppCompatTextView txt_sum_undeliverdOrderWeight;
        AppCompatTextView txt_sum_finalWeight;
        LinearLayout layout_footer_order;

        txt_sum_orderWeight = itemView.findViewById(R.id.txt_sum_orderWeight);
      //  txt_sum_pendingOrderWeight = itemView.findViewById(R.id.txt_sum_pendingOrderWeight);
        txt_sum_inProgressOrderWeight = itemView.findViewById(R.id.txt_sum_inProgressOrderWeight);
      //  txt_sum_undeliverdOrderWeight = itemView.findViewById(R.id.txt_sum_undeliverdOrderWeight);
        txt_sum_finalWeight = itemView.findViewById(R.id.txt_sum_finalWeight);

        layout_footer_order= itemView.findViewById(R.id.layout_footer_order);
        if(node.getId() == node.getParent().getChildren().size()) {

            double sum_orderWeight= 0;
            double sum_pendingOrderWeightt= 0;
            double sum_inProgressOrderWeight= 0;
            double sum_undeliverdOrderWeight= 0;
            double sum_finalWeight= 0;
            for (OrderStatusReportFlat itemSub : _data) {
                sum_orderWeight += itemSub.getOrderWeight();
                sum_pendingOrderWeightt+= itemSub.getPendingOrderWeight();
                sum_inProgressOrderWeight+= itemSub.getInProgressOrderWeight();
                sum_undeliverdOrderWeight+= itemSub.getUndeliverdOrderWeight();
                sum_finalWeight+= itemSub.getFinalWeight();

            }
            txt_sum_orderWeight.setText(df.format(sum_orderWeight));
           // txt_sum_pendingOrderWeight.setText(df.format(sum_pendingOrderWeightt));
            txt_sum_inProgressOrderWeight.setText(df.format(sum_inProgressOrderWeight));
          //  txt_sum_undeliverdOrderWeight.setText(df.format(sum_undeliverdOrderWeight));
            txt_sum_finalWeight.setText(df.format(sum_finalWeight));


            layout_footer_order.setVisibility(View.VISIBLE);
        }else {
            layout_footer_order.setVisibility(View.GONE);
        }

        return itemView;
    }
}
