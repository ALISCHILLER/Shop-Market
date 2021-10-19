package com.varanegar.supervisor.status;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.CustomerCallReturnLineView;
import com.varanegar.supervisor.webapi.CustomerCallReturnLineViewModel;
import com.varanegar.supervisor.webapi.CustomerCallReturnViewModel;
import com.varanegar.supervisor.webapi.CustomerCallViewModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;

import java.util.ArrayList;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerReturnsFragment extends CustomerCallTabFragment {
    private View detailLayout;
    private PairedItems returnTypePairedItems;
    private PairedItems invoiceNoPairedItems;
    private ReportView itemsReportView;
    private BaseRecyclerView masterRecyclerView;
    private BaseSelectionRecyclerAdapter<CustomerCallReturnViewModel> callReturnsAdapter;
    private View mainListLayout;
    private Button customerPreviewBtn;
    private TextView errorTextView;

    @Override
    public void refresh(CustomerCallViewModel customerCallViewModel) {
        super.refresh(customerCallViewModel);
        if (customerCallViewModel == null)
            return;
        if (customerCallViewModel.CustomerCallReturns == null || customerCallViewModel.CustomerCallReturns.size() == 0)
            customerCallViewModel.CustomerCallReturns = new ArrayList<>();

        if (customerCallViewModel.CustomerCallReturns.size() == 1 && mainListLayout != null)
            mainListLayout.setVisibility(View.GONE);

        if (customerCallViewModel.CustomerCallReturns.size() == 0 && mainListLayout != null)
        {
            mainListLayout.setVisibility(View.GONE);
            detailLayout.setVisibility(View.GONE);
            errorTextView.setVisibility(View.VISIBLE);
        }


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
                columns.add(bind(entity, CustomerCallReturnLineView.ReturnQty, getString(R.string.qty)));
                columns.add(bind(entity, CustomerCallReturnLineView.ReturnUnit, getString(R.string.unit)));
                columns.add(bind(entity, CustomerCallReturnLineView.UnitPrice, getString(R.string.unit_price)).sendToDetail());
                columns.add(bind(entity, CustomerCallReturnLineView.TotalReturnAmount, getString(R.string.request_amount)).setWeight(1.5f).calcTotal().sendToDetail());
                columns.add(bind(entity, CustomerCallReturnLineView.TotalReturnNetAmount, getString(R.string.net_amount)).setWeight(1.5f).calcTotal().sendToDetail());
                columns.add(bind(entity, CustomerCallReturnLineView.StockName, getString(R.string.stock)).sendToDetail());
            }
        };
        linesAdapter.create(customerCallReturnViewModel.OrderReturnLines, null);
        itemsReportView.setAdapter(linesAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_returns_layout, container, false);
        mainListLayout = view.findViewById(R.id.main_list_layout);
        detailLayout = view.findViewById(R.id.detail_layout);
        returnTypePairedItems = view.findViewById(R.id.return_type_paired_items);
        invoiceNoPairedItems = view.findViewById(R.id.invoice_no_paired_items);
        itemsReportView = view.findViewById(R.id.items_report_view);
        masterRecyclerView = view.findViewById(R.id.master_recycler_view);
        customerPreviewBtn = view.findViewById(R.id.customer_preview_btn);
        errorTextView = view.findViewById(R.id.error_text_view);
        customerPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerSummarySlidingDialog dialog = new CustomerSummarySlidingDialog();
                Bundle bundle = new Bundle();
                bundle.putString("CUSTOMER_ID",customerCallViewModel.CustomerUniqueId.toString());
                dialog.setArguments(bundle);
                dialog.show(getChildFragmentManager(), "CustomerSummarySlidingDialog");
            }
        });
        masterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
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
                invoiceNoTextView.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionRecyclerAdapter.notifyItemClicked(position);
                }
            });
        }
    }
}
