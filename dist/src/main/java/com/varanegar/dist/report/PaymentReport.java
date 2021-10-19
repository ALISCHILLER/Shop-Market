package com.varanegar.dist.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.PaymentReportViewModelManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.model.PaymentReportView.PaymentReportView;
import com.varanegar.vaslibrary.model.PaymentReportView.PaymentReportViewModel;
import com.varanegar.vaslibrary.model.PaymentReportView.PaymentReportViewModelRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s.foroughi on 16/01/2017.
 */

public class PaymentReport extends VaranegarFragment {
    ReportAdapter<PaymentReportViewModel> adapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_report, null);

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

        final ReportView paymentReport = (ReportView) view.findViewById(R.id.payment_report);
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
                    adapter.create(new PaymentReportViewModelRepository(), PaymentReportViewModelManager.getAll(item.toString()), savedInstanceState);
                else
                    adapter.create(new PaymentReportViewModelRepository(), PaymentReportViewModelManager.getAll(), savedInstanceState);
                paymentReport.setAdapter(adapter);
            }
        });
        adapter = new SimpleReportAdapter<PaymentReportViewModel>((MainVaranegarActivity) getActivity(), PaymentReportViewModel.class) {
            @Override
            public void bind(ReportColumns columns, final PaymentReportViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, PaymentReportView.CustomerCode, getString(R.string.customer_code)).setSortable().setFilterable().setWeight(1).setFrizzed());
                columns.add(bind(entity, PaymentReportView.CustomerName, getString(R.string.customer_name)).setSortable().setFilterable().setWeight(2).setFrizzed());
                columns.add(bind(entity, PaymentReportView.CashAmount, getString(R.string.cash)).setSortable().setFilterable().setWeight(1).calcTotal());
                columns.add(bind(entity, PaymentReportView.ChequeAmount, getString(R.string.cheque)).setSortable().setFilterable().setWeight(1).calcTotal());
                columns.add(bind(entity, PaymentReportView.CardAmount, getString(R.string.pos)).setSortable().setFilterable().setWeight(1).calcTotal());
                columns.add(bind(entity, PaymentReportView.SettlementDiscountAmount, getString(R.string.settlement_discount)).setSortable().setFilterable().setWeight(1.5f).calcTotal());
                columns.add(bind(entity, PaymentReportView.CreditAmount, getString(R.string.pay_from_credit)).setSortable().setFilterable().setWeight(1.5f).calcTotal());
                columns.add(bind(entity, PaymentReportView.ChqNo, getString(R.string.chq_no)).setSortable().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, PaymentReportView.ChqDate, getString(R.string.chq_date)).setSortable().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, PaymentReportView.BankName, getString(R.string.bank_name)).setSortable().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, PaymentReportView.ChqBranchName, getString(R.string.chq_branch_name)).setSortable().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, PaymentReportView.CityName, getString(R.string.city_name)).setSortable().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, PaymentReportView.FollowNo, getString(R.string.follow_no)).setSortable().setFilterable().setWeight(1.5f).sendToDetail());
                columns.add(bind(entity, PaymentReportView.ChqAccountNo, getString(R.string.chq_account_no)).setSortable().setFilterable().setWeight(1.5f).sendToDetail());
                columns.add(bind(entity, PaymentReportView.ChqBranchCode, getString(R.string.chq_branch_code)).setSortable().setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, PaymentReportView.ChqAccountName, getString(R.string.chq_account_name)).setSortable().setFilterable().setWeight(1.5f).sendToDetail());
                columns.add(bind(entity, PaymentReportView.PaidAmount, getString(R.string.paid_amount)).setSortable().setFilterable().setWeight(1.5f).calcTotal().sendToDetail());
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new PaymentReportViewModelRepository(), PaymentReportViewModelManager.getAll(), savedInstanceState);
        paymentReport.setAdapter(adapter);
        return view;
    }
}
