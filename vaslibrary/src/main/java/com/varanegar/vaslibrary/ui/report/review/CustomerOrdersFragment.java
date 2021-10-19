package com.varanegar.vaslibrary.ui.report.review;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.ui.report.review.adapter.CustomerCallOrderLineView;
import com.varanegar.vaslibrary.ui.report.review.adapter.CustomerCallOrderLineViewModel;

import java.util.ArrayList;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerOrdersFragment extends CustomerCallTabFragment {
    private PairedItems endTimePairedItems;
    private PairedItems startTimePairedItems;
    private PairedItems paymentTypePairedItems;
    private PairedItems orderTypeNamePairedItems;
    private BaseRecyclerView masterRecyclerView;
    private BaseSelectionRecyclerAdapter<CustomerCallOrderViewModel> callOrdersAdapter;
    private ReportView itemsReportView;
    private View detailLayout;

    @Override
    public void refresh(CustomerCallViewModel customerCallViewModel) {
        if (customerCallViewModel == null)
            return;
        if (customerCallViewModel.CustomerCallOrders == null || customerCallViewModel.CustomerCallOrders.size() == 0)
            customerCallViewModel.CustomerCallOrders = new ArrayList<>();
        callOrdersAdapter = new BaseSelectionRecyclerAdapter<CustomerCallOrderViewModel>(getVaranegarActvity(), customerCallViewModel.CustomerCallOrders, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_call_order_view_item, parent, false);
                return new CustomerCallOrderViewHolder(view, callOrdersAdapter, getContext());
            }
        };

        callOrdersAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                refreshDetail(callOrdersAdapter.get(position));
            }
        });
        masterRecyclerView.setAdapter(callOrdersAdapter);
        if (callOrdersAdapter.size() > 0) {
            callOrdersAdapter.select(0);
            refreshDetail(callOrdersAdapter.getSelectedItem());
        } else refreshDetail(null);
    }

    private void refreshDetail(CustomerCallOrderViewModel customerCallOrderViewModel) {
        if (customerCallOrderViewModel == null) {
            detailLayout.setVisibility(View.INVISIBLE);
            return;
        } else
            detailLayout.setVisibility(View.VISIBLE);
        startTimePairedItems.setValue(customerCallOrderViewModel.InvoiceStartPTime);
        endTimePairedItems.setValue(customerCallOrderViewModel.InvoiceEndPTime);
        paymentTypePairedItems.setValue(customerCallOrderViewModel.OrderPaymentTypeName);
        orderTypeNamePairedItems.setValue(customerCallOrderViewModel.OrderTypeName);

        SimpleReportAdapter<CustomerCallOrderLineViewModel> linesAdapter = new SimpleReportAdapter<CustomerCallOrderLineViewModel>(getVaranegarActvity(), CustomerCallOrderLineViewModel.class) {
            @Override
            public void bind(ReportColumns columns, CustomerCallOrderLineViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, CustomerCallOrderLineView.ProductCode, getString(R.string.product_code)).setFrizzed());
                columns.add(bind(entity, CustomerCallOrderLineView.ProductName, getString(R.string.product_name)).setWeight(2.5f));
                columns.add(bind(entity, CustomerCallOrderLineView.UnitPrice, getString(R.string.unit_price)));
                columns.add(bind(entity, CustomerCallOrderLineView.RequestAmount, getString(R.string.request_amount)).setWeight(1.5f).calcTotal());
                columns.add(bind(entity, CustomerCallOrderLineView.RequestNetAmount, getString(R.string.net_amount)).setWeight(1.5f).calcTotal());
                columns.add(bind(entity, CustomerCallOrderLineView.RequestQty, getString(R.string.qty)));
                columns.add(bind(entity, CustomerCallOrderLineView.RequestUnit, getString(R.string.unit)));
                columns.add(bind(entity, CustomerCallOrderLineView.StockName, getString(R.string.stock)));
            }
        };
        linesAdapter.create(customerCallOrderViewModel.OrderLines, null);
        linesAdapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        itemsReportView.setAdapter(linesAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_orders_layout, container, false);
        detailLayout = view.findViewById(R.id.detail_layout);
        orderTypeNamePairedItems = view.findViewById(R.id.order_type_name_paired_items);
        paymentTypePairedItems = view.findViewById(R.id.payment_type_paired_items);
        startTimePairedItems = view.findViewById(R.id.start_time_paired_items);
        endTimePairedItems = view.findViewById(R.id.end_time_paired_items);
        itemsReportView = view.findViewById(R.id.items_report_view);
        masterRecyclerView = view.findViewById(R.id.master_recycler_view);
        masterRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), HelperMethods.getColor(getContext(), R.color.grey_light_light_light), 1));
        return view;
    }

    private class CustomerCallOrderViewHolder extends BaseViewHolder<CustomerCallOrderViewModel> {

        private final TextView refNumberTextView;
        private final TextView paymentTypetextView;

        public CustomerCallOrderViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallOrderViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            refNumberTextView = itemView.findViewById(R.id.ref_number_text_view);
            paymentTypetextView = itemView.findViewById(R.id.payment_type_text_view);
        }

        @Override
        public void bindView(final int position) {
            final BaseSelectionRecyclerAdapter selectionRecyclerAdapter = ((BaseSelectionRecyclerAdapter) recyclerAdapter);
            CustomerCallOrderViewModel customerCallOrderViewModel = recyclerAdapter.get(position);
            if (selectionRecyclerAdapter.getSelectedPosition() == position)
                itemView.setBackgroundResource(R.color.grey_light_light_light);
            else
                itemView.setBackgroundResource(R.color.white);
            refNumberTextView.setText(customerCallOrderViewModel.LocalPaperNo);
            paymentTypetextView.setText(customerCallOrderViewModel.OrderPaymentTypeName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionRecyclerAdapter.notifyItemClicked(position);
                }
            });
        }
    }

}
