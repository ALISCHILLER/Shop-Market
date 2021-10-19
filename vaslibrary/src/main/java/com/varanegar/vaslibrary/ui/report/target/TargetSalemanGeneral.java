package com.varanegar.vaslibrary.ui.report.target;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.Target.TargetDetailManager;
import com.varanegar.vaslibrary.manager.Target.TargetMasterManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceDetailManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceHeaderManager;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModel;
import com.varanegar.vaslibrary.model.target.TargetDetailModel;
import com.varanegar.vaslibrary.model.target.TargetMasterModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 1/6/2018.
 */

public class TargetSalemanGeneral extends VaranegarFragment {

    private UUID targetUniqueId;

    public void setTargetUniqueId(@NonNull UUID targetUniqueId) {
        Bundle bundle = new Bundle();
        bundle.putString("a8eb9ff3-7cad-40f1-a2a9-862774e91c06", targetUniqueId.toString());
        setArguments(bundle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        targetUniqueId = UUID.fromString(getStringArgument("a8eb9ff3-7cad-40f1-a2a9-862774e91c06"));
        View view = inflater.inflate(R.layout.target_saleman_general, container, false);

        TargetDetailManager targetDetailManager = new TargetDetailManager(getContext());
        TargetDetailModel targetDetailModel = targetDetailManager.getItem(TargetDetailManager.getDetail(targetUniqueId));

        TargetMasterManager targetMasterManager = new TargetMasterManager(getContext());
        TargetMasterModel targetMasterModel = targetMasterManager.getItem(TargetMasterManager.getTaregetMaster(targetUniqueId));

        double acheivedOrder, acheivedOrderItem;
        Currency acheivedOrdersAmount;

        CustomerOldInvoiceHeaderManager customerOldInvoiceHeaderManager = new CustomerOldInvoiceHeaderManager(getContext());
        List<CustomerOldInvoiceHeaderModel> customerOldInvoiceHeaderModels = customerOldInvoiceHeaderManager.getItems(CustomerOldInvoiceHeaderManager.getItemsInPeriod(targetMasterModel.FromDate, new Date()));
        acheivedOrder = customerOldInvoiceHeaderModels.size();
        CustomerOldInvoiceDetailManager customerOldInvoiceDetailManager = new CustomerOldInvoiceDetailManager(getContext());
        acheivedOrderItem = customerOldInvoiceDetailManager.getItemsInPeriod(targetMasterModel.FromDate, new Date());
        acheivedOrdersAmount = customerOldInvoiceDetailManager.getNetAmountInPeriod(targetMasterModel.FromDate, new Date());

        /**
         * Set Order Target
         */
        TextView txt_order_count_target, txt_order_count_achieved_in_period, txt_order_count_achieved_percent_in_day, txt_order_count_achieved_percent_in_period, txt_average_sale_in_rest_days;
        txt_order_count_target = (TextView) view.findViewById(R.id.order_count_target);
        txt_order_count_achieved_in_period = (TextView) view.findViewById(R.id.order_count_achieved_in_period);
        txt_order_count_achieved_percent_in_day = (TextView) view.findViewById(R.id.order_count_achieved_percent_in_day);
        txt_order_count_achieved_percent_in_period = (TextView) view.findViewById(R.id.order_count_achieved_percent_in_period);
        txt_average_sale_in_rest_days = (TextView) view.findViewById(R.id.average_sale_in_rest_days);
        if (targetDetailModel.OrderCount == 0) {
            txt_order_count_target.setText(getString(R.string.multiplication_sign));
            txt_order_count_achieved_in_period.setText(getString(R.string.multiplication_sign));
            txt_order_count_achieved_percent_in_day.setText(getString(R.string.multiplication_sign));
            txt_order_count_achieved_percent_in_period.setText(getString(R.string.multiplication_sign));
            txt_average_sale_in_rest_days.setText(getString(R.string.multiplication_sign));
        } else {
            txt_order_count_target.setText(String.valueOf(targetDetailModel.OrderCount));
            txt_order_count_achieved_in_period.setText(String.valueOf(acheivedOrder));
            txt_order_count_achieved_percent_in_day.setText(TargetHelperMethods.calculateAchievedPercentInDay((double) targetDetailModel.OrderCount, acheivedOrder, targetMasterModel.FromDate, targetMasterModel.ToDate) + " %");
            txt_order_count_achieved_percent_in_period.setText(TargetHelperMethods.calculatedAchievedPercentInPeriod((double) targetDetailModel.OrderCount, acheivedOrder) + " %");
            txt_average_sale_in_rest_days.setText(TargetHelperMethods.calculateAverageSaleInRestDays((double) targetDetailModel.OrderCount, acheivedOrder, targetMasterModel.ToDate));
        }

        /**
         * Set Order Items Target
         */
        TextView txt_order_item_count_target, txt_order_item_count_acheived_in_period, txt_order_item_count_acheived_percent_in_day, txt_order_item_count_acheived_percent_in_period, txt_average_sale_items_in_rest_days;
        txt_order_item_count_target = (TextView) view.findViewById(R.id.order_item_count_target);
        txt_order_item_count_acheived_in_period = (TextView) view.findViewById(R.id.order_item_count_acheived_in_period);
        txt_order_item_count_acheived_percent_in_day = (TextView) view.findViewById(R.id.order_item_count_acheived_percent_in_day);
        txt_order_item_count_acheived_percent_in_period = (TextView) view.findViewById(R.id.order_item_count_acheived_percent_in_period);
        txt_average_sale_items_in_rest_days = (TextView) view.findViewById(R.id.average_sale_items_in_rest_days);
        if (targetDetailModel.OrderItemCount == 0) {
            txt_order_item_count_target.setText(getString(R.string.multiplication_sign));
            txt_order_item_count_acheived_in_period.setText(getString(R.string.multiplication_sign));
            txt_order_item_count_acheived_percent_in_day.setText(getString(R.string.multiplication_sign));
            txt_order_item_count_acheived_percent_in_period.setText(getString(R.string.multiplication_sign));
            txt_average_sale_items_in_rest_days.setText(getString(R.string.multiplication_sign));
        } else {
            txt_order_item_count_target.setText(String.valueOf(targetDetailModel.OrderItemCount));
            txt_order_item_count_acheived_in_period.setText(String.valueOf(acheivedOrderItem));
            txt_order_item_count_acheived_percent_in_day.setText(TargetHelperMethods.calculateAchievedPercentInDay((double) targetDetailModel.OrderItemCount, acheivedOrderItem, targetMasterModel.FromDate, targetMasterModel.ToDate) + " %");
            txt_order_item_count_acheived_percent_in_period.setText(TargetHelperMethods.calculatedAchievedPercentInPeriod((double) targetDetailModel.OrderItemCount, acheivedOrderItem) + " %");
            txt_average_sale_items_in_rest_days.setText(TargetHelperMethods.calculateAverageSaleInRestDays((double) targetDetailModel.OrderItemCount, acheivedOrderItem, targetMasterModel.ToDate));

        }

        /**
         * Set Netamounts Target
         */
        TextView txt_netamount_sum, txt_netamount_sum_acheived_in_period, txt_netamount_sum_acheived_percent_in_day, txt_netamount_sum_acheived_percent_in_period, txt_average_net_amount_in_rest_days;
        txt_netamount_sum = (TextView) view.findViewById(R.id.netamount_sum);
        txt_netamount_sum_acheived_in_period = (TextView) view.findViewById(R.id.netamount_sum_acheived_in_period);
        txt_netamount_sum_acheived_percent_in_day = (TextView) view.findViewById(R.id.netamount_sum_acheived_percent_in_day);
        txt_netamount_sum_acheived_percent_in_period = (TextView) view.findViewById(R.id.netamount_sum_acheived_percent_in_period);
        txt_average_net_amount_in_rest_days = (TextView) view.findViewById(R.id.average_net_amount_in_rest_days);
        if (targetDetailModel.OrderAmount.compareTo(Currency.ZERO) == 0) {
            txt_netamount_sum.setText(getString(R.string.multiplication_sign));
            txt_netamount_sum_acheived_in_period.setText(getString(R.string.multiplication_sign));
            txt_netamount_sum_acheived_percent_in_day.setText(getString(R.string.multiplication_sign));
            txt_netamount_sum_acheived_percent_in_period.setText(getString(R.string.multiplication_sign));
            txt_average_net_amount_in_rest_days.setText(getString(R.string.multiplication_sign));
        } else {
            txt_netamount_sum.setText(HelperMethods.currencyToString(targetDetailModel.OrderAmount));
            txt_netamount_sum_acheived_in_period.setText(String.valueOf(acheivedOrdersAmount));
            txt_netamount_sum_acheived_percent_in_day.setText(TargetHelperMethods.calculateAchievedPercentInDay(targetDetailModel.OrderAmount.doubleValue(), acheivedOrdersAmount.doubleValue(), targetMasterModel.FromDate, targetMasterModel.ToDate) + " %");
            txt_netamount_sum_acheived_percent_in_period.setText(TargetHelperMethods.calculatedAchievedPercentInPeriod(targetDetailModel.OrderAmount.doubleValue(), acheivedOrdersAmount.doubleValue()) + " %");
            txt_average_net_amount_in_rest_days.setText(TargetHelperMethods.calculateAverageSaleInRestDays(targetDetailModel.OrderAmount.doubleValue(), acheivedOrdersAmount.doubleValue(), targetMasterModel.ToDate));
        }
        return view;
    }

}