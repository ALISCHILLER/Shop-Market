package com.varanegar.dist.report;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.DeliveryReportViewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.model.DeliveryReportView.DeliveryReportView;
import com.varanegar.vaslibrary.model.DeliveryReportView.DeliveryReportViewModel;
import com.varanegar.vaslibrary.model.DeliveryReportView.DeliveryReportViewModelRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s.foroughi on 18/01/2017.
 */

public class DeliveryReport extends VaranegarFragment {
    ReportAdapter<DeliveryReportViewModel> adapter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_report, null);

        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        toolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ReportView deliveryReport = (ReportView) view.findViewById(R.id.delivery_report);
        PairedItemsSpinner dealerNameSpinner = (PairedItemsSpinner) view.findViewById(R.id.dealer_name_spinner);
        List<String> dealerName = new ArrayList<>();
        dealerName.add(getString(R.string.all_case));
        CustomerCallInvoiceManager customerCallInvoiceManager = new CustomerCallInvoiceManager(getContext());
        dealerName.addAll(customerCallInvoiceManager.getAllDealerName());
        dealerNameSpinner.setup(getFragmentManager(), dealerName, null);
        dealerNameSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object item) {
                adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                if (item.toString()!=null && !item.toString().equals(getString(R.string.all_case)))
                    adapter.create(new DeliveryReportViewModelRepository(), DeliveryReportViewManager.getAll(item.toString()), savedInstanceState);
                else
                    adapter.create(new DeliveryReportViewModelRepository(), DeliveryReportViewManager.getAll(), savedInstanceState);
                deliveryReport.setAdapter(adapter);
            }
        });
        adapter = new SimpleReportAdapter<DeliveryReportViewModel>(getVaranegarActvity(), DeliveryReportViewModel.class) {
            @Override
            public void bind(ReportColumns columns, final DeliveryReportViewModel entity) {
                columns.add(bind(entity, DeliveryReportView.CustomerCode, getString(R.string.customer_code)).setSortable().setFilterable().setWeight(1).setFrizzed());
                columns.add(bind(entity, DeliveryReportView.CustomerName, getString(R.string.customer_name)).setSortable().setFilterable().setWeight(2).setFrizzed());
                columns.add(bind(entity, DeliveryReportView.InvoiceNetAmount, getString(R.string.request_amount)).setSortable().calcTotal().setFilterable().sendToDetail().setWeight(1.5f));
                columns.add(bind(entity, DeliveryReportView.InvoiceReturnNetAmount, getString(R.string.total_return_amount)).setSortable().calcTotal().setFilterable().sendToDetail().setWeight(1.5f));
                columns.add(bind(entity, DeliveryReportView.TotalReturnNetAmount, getString(R.string.return_invoice)).setSortable().calcTotal().setFilterable().sendToDetail().setWeight(1.5f).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.TotalOldInvoiceAmount, getString(R.string.old_invoices_amount)).setSortable().calcTotal().setFilterable().sendToDetail().setWeight(1.5f).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.TotalPayAbleAmount, getString(R.string.total_invoice_amount)).setSortable().calcTotal().setFilterable().sendToDetail().setWeight(1.5f).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.ReceiptAmount, getString(R.string.receipt)).setSortable().calcTotal().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.SettlementDiscountAmount, getString(R.string.discount)).setSortable().calcTotal().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.CashAmount, getString(R.string.cash)).setSortable().calcTotal().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.ChequeAmount, getString(R.string.cheque)).setSortable().calcTotal().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.CardAmount, getString(R.string.pos)).setSortable().calcTotal().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, DeliveryReportView.CreditAmount, getString(R.string.pay_from_credit)).setSortable().calcTotal().setFilterable().setWeight(1.5f).sendToDetail());
            }
        };
        adapter.create(new DeliveryReportViewModelRepository(), DeliveryReportViewManager.getAll(), savedInstanceState);
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        deliveryReport.setAdapter(adapter);
        return view;
    }
}