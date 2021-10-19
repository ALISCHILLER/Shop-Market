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
import com.varanegar.vaslibrary.model.returnType.ReturnType;

import java.util.ArrayList;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerReturnsFragment extends CustomerCallTabFragment {
    private View detailLayout;
    private PairedItems returnTypePairedItems;
    private PairedItems invoiceNoPairedItems;
    private PairedItems startTimePairedItems;
    private PairedItems endTimePairedItems;
    private ReportView itemsReportView;
    private BaseRecyclerView masterRecyclerView;
    private BaseSelectionRecyclerAdapter<CustomerCallReturnViewModel> callReturnsAdapter;

    @Override
    public void refresh(CustomerCallViewModel customerCallViewModel) {
        if (customerCallViewModel == null)
            return;
        if (customerCallViewModel.CustomerCallReturns == null || customerCallViewModel.CustomerCallReturns.size() == 0)
            customerCallViewModel.CustomerCallReturns = new ArrayList<>();
        callReturnsAdapter = new BaseSelectionRecyclerAdapter<CustomerCallReturnViewModel>(getVaranegarActvity(), customerCallViewModel.CustomerCallReturns, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_call_return_view_item, parent, false);
                return new CustomerCallReturnViewHolder(view, callReturnsAdapter, getContext());
            }
        };
        callReturnsAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                refreshDetail(callReturnsAdapter.get(position));
            }
        });
        masterRecyclerView.setAdapter(callReturnsAdapter);
        if (callReturnsAdapter.size() > 0) {
            callReturnsAdapter.select(0);
            refreshDetail(callReturnsAdapter.getSelectedItem());
        } else refreshDetail(null);
    }

    private void refreshDetail(CustomerCallReturnViewModel customerCallReturnViewModel) {
        if (customerCallReturnViewModel == null) {
            detailLayout.setVisibility(View.INVISIBLE);
            return;
        } else
            detailLayout.setVisibility(View.VISIBLE);
        startTimePairedItems.setValue(customerCallReturnViewModel.ReturnStartPTime);
        endTimePairedItems.setValue(customerCallReturnViewModel.ReturnEndPTime);
        returnTypePairedItems.setValue(customerCallReturnViewModel.ReturnTypeName);
        if (ReturnType.WithRef.equals(customerCallReturnViewModel.ReturnTypeUniqueId))
            invoiceNoPairedItems.setValue(customerCallReturnViewModel.BackOfficeInvoiceNo);
        else
            invoiceNoPairedItems.setValue("--");
        SimpleReportAdapter<CustomerCallReturnLineViewModel> linesAdapter = new SimpleReportAdapter<CustomerCallReturnLineViewModel>(getVaranegarActvity(), CustomerCallReturnLineViewModel.class) {
            @Override
            public void bind(ReportColumns columns, CustomerCallReturnLineViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, CustomerCallReturnLineView.ProductCode, getString(R.string.product_code)).setFrizzed());
                columns.add(bind(entity, CustomerCallReturnLineView.ProductName, getString(R.string.product_name)).setWeight(2.5f));
                columns.add(bind(entity, CustomerCallReturnLineView.UnitPrice, getString(R.string.unit_price)));
                columns.add(bind(entity, CustomerCallReturnLineView.TotalReturnAmount, getString(R.string.request_amount)).setWeight(1.5f).calcTotal());
                columns.add(bind(entity, CustomerCallReturnLineView.TotalReturnNetAmount, getString(R.string.net_amount)).setWeight(1.5f).calcTotal());
                columns.add(bind(entity, CustomerCallReturnLineView.ReturnQty, getString(R.string.qty)));
                columns.add(bind(entity, CustomerCallReturnLineView.ReturnUnit, getString(R.string.unit)));
                columns.add(bind(entity, CustomerCallReturnLineView.StockName, getString(R.string.stock)));
            }
        };
        linesAdapter.create(customerCallReturnViewModel.OrderReturnLines, null);
        linesAdapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        itemsReportView.setAdapter(linesAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_returns_layout, container, false);
        detailLayout = view.findViewById(R.id.detail_layout);
        returnTypePairedItems = view.findViewById(R.id.return_type_paired_items);
        invoiceNoPairedItems = view.findViewById(R.id.invoice_no_paired_items);
        startTimePairedItems = view.findViewById(R.id.start_time_paired_items);
        endTimePairedItems = view.findViewById(R.id.end_time_paired_items);
        itemsReportView = view.findViewById(R.id.items_report_view);
        masterRecyclerView = view.findViewById(R.id.master_recycler_view);
        masterRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), HelperMethods.getColor(getContext(), R.color.grey_light_light_light), 1));
        return view;
    }

    private class CustomerCallReturnViewHolder extends BaseViewHolder<CustomerCallReturnViewModel> {

        private final TextView returnTypeTextView;
        private final TextView invoiceNoTextView;

        public CustomerCallReturnViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallReturnViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            returnTypeTextView = itemView.findViewById(R.id.return_type_text_view);
            invoiceNoTextView = itemView.findViewById(R.id.invoice_no_text_view);

        }

        @Override
        public void bindView(final int position) {
            final BaseSelectionRecyclerAdapter selectionRecyclerAdapter = ((BaseSelectionRecyclerAdapter) recyclerAdapter);
            CustomerCallReturnViewModel customerCallReturnViewModel = recyclerAdapter.get(position);
            if (selectionRecyclerAdapter.getSelectedPosition() == position)
                itemView.setBackgroundResource(R.color.grey_light_light_light);
            else
                itemView.setBackgroundResource(R.color.white);
            returnTypeTextView.setText(customerCallReturnViewModel.ReturnTypeName);
            if (ReturnType.WithRef.equals(customerCallReturnViewModel.ReturnTypeUniqueId))
                invoiceNoTextView.setText(getString(R.string.invoice_no) + " " + customerCallReturnViewModel.BackOfficeInvoiceNo);
            else
                invoiceNoTextView.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionRecyclerAdapter.notifyItemClicked(position);
                }
            });
        }
    }
}
