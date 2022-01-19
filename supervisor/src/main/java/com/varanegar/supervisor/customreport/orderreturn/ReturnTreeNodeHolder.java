package com.varanegar.supervisor.customreport.orderreturn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import com.unnamed.b.atv.model.TreeNode;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.customreport.orderreturn.model.ReturnReportFlat;

import java.text.DecimalFormat;
import java.util.List;


public class ReturnTreeNodeHolder extends TreeNode.BaseNodeViewHolder<ReturnReportFlat> {
    private List<ReturnReportFlat> _data;

    public ReturnTreeNodeHolder(Context context, List<ReturnReportFlat> data) {
        super(context);
        _data = data;
    }

    @Override
    public View createNodeView(TreeNode node, ReturnReportFlat value) {
        if (node.getLevel() == 1) {
            return level1(node,value);
        }else if (node.getLevel() == 2) {

            return level2(node,value);
        }else {
            return level3(node,value);
        }
    }

    private View level3(TreeNode node,ReturnReportFlat reson) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_get_return_report_reason,
                null, false);
        AppCompatTextView txtreason;
        AppCompatTextView txtreasonCode;
        AppCompatTextView txtproductCountCa;

        txtreason = itemView.findViewById(R.id.txtreason);
        txtreasonCode = itemView.findViewById(R.id.txtreasonCode);
        txtproductCountCa = itemView.findViewById(R.id.txtproductCountCa);



        txtreason.setText(reson.getReason());
        txtreasonCode.setText(reson.getReasonCode());
        txtproductCountCa.setText(String.valueOf(reson.getProductCountCa()));

        AppCompatTextView  txt_sum= itemView.findViewById(R.id.txt_sum);
        LinearLayout layout_footer_sub=itemView.findViewById(R.id.layout_footer_sub);
        if(node.getId() == node.getParent().getChildren().size()) {

            double sum = 0;
            for (ReturnReportFlat itemSub : ((ReturnReportFlat) node.getParent().getValue()).getChilds()) {
                sum += itemSub.getProductCountCa();
            }
            txt_sum.setText(String.valueOf(sum));
            layout_footer_sub.setVisibility(View.VISIBLE);
        }else {
            layout_footer_sub.setVisibility(View.GONE);
        }
        return itemView;
    }

    private View level2(TreeNode node,ReturnReportFlat dealersItem) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_get_return_report_customer,
                null, false);

        AppCompatTextView txtcustomerName;
        AppCompatTextView txtCustomerCode;
        AppCompatTextView txtproductCountCa;

        txtcustomerName = itemView.findViewById(R.id.txtcustomerName);
        txtCustomerCode = itemView.findViewById(R.id.txtCustomerCode);
        txtproductCountCa = itemView.findViewById(R.id.txtproductCountCa);



        txtcustomerName.setText(dealersItem.getCustomerName());
        txtCustomerCode.setText(dealersItem.getCustomerCode());
        txtproductCountCa.setText(String.valueOf(dealersItem.getProductCountCa()));

        AppCompatTextView  txt_sum= itemView.findViewById(R.id.txt_sum);
        LinearLayout layout_footer_sub=itemView.findViewById(R.id.layout_footer_sub);
        if(node.getId() == node.getParent().getChildren().size()) {
            DecimalFormat df=new DecimalFormat("###.##");
            double sum = 0;
            for (ReturnReportFlat itemSub : ((ReturnReportFlat) node.getParent().getValue()).getChilds()) {
                sum += itemSub.getProductCountCa();
            }
            txt_sum.setText(df.format(sum));
            layout_footer_sub.setVisibility(View.VISIBLE);
        }else {
            layout_footer_sub.setVisibility(View.GONE);
        }



        return itemView;
    }

    private View level1(TreeNode node,ReturnReportFlat item) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_get_return_report_dealer,
                null, false);


        AppCompatTextView txtdealerNamee;
        AppCompatTextView txtdealerCode;
        AppCompatTextView txtproductCountCa;

        txtdealerNamee = itemView.findViewById(R.id.txtdealerNamee);
        txtdealerCode = itemView.findViewById(R.id.txtdealerCode);
        txtproductCountCa = itemView.findViewById(R.id.txtproductCountCa);

        txtdealerNamee.setText(item.getDealerName());
        txtdealerCode.setText(String.valueOf(item.getDealerCode()));
        txtproductCountCa.setText(String.valueOf(item.getProductCountCa()));


        AppCompatTextView  txt_sum= itemView.findViewById(R.id.txt_sum);
        LinearLayout layout_footer_sub=itemView.findViewById(R.id.layout_footer_sub);

        if(node.getId() == node.getParent().getChildren().size()) {
            DecimalFormat df=new DecimalFormat("###.##");
            double sum = 0;
            for (ReturnReportFlat itemSub : _data) {
                sum += itemSub.getProductCountCa();
            }

            txt_sum.setText(df.format(sum));
            layout_footer_sub.setVisibility(View.VISIBLE);
        }else {
            layout_footer_sub.setVisibility(View.GONE);
        }
        return itemView;
    }
}
