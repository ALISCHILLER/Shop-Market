package com.varanegar.supervisor.customreport.orderreturn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.unnamed.b.atv.model.TreeNode;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.customreport.orderreturn.model.ReturnReportFlat;


public class ReturnTreeNodeHolder extends TreeNode.BaseNodeViewHolder<ReturnReportFlat> {
    private final IOnViewCount onViewCount;

    interface IOnViewCount {
        void viewCount(TreeNode parentNode);
    }

    public ReturnTreeNodeHolder(Context context, IOnViewCount _onViewCount) {
        super(context);
        this.onViewCount = _onViewCount;
    }

    @Override
    public View createNodeView(TreeNode node, ReturnReportFlat value) {
        if (node.getLevel() == 1) {
            return level1(node,value);
        }else if (node.getLevel() == 2) {
            return level2(value);
        }else {
            return level3(value);
        }
    }

    private View level3(ReturnReportFlat reson) {
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

        return itemView;
    }

    private View level2(ReturnReportFlat dealersItem) {
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

        return itemView;
    }

    private View level1(TreeNode node,ReturnReportFlat item) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.listitem_order_status_report_dealer,
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

        return itemView;
    }
}
