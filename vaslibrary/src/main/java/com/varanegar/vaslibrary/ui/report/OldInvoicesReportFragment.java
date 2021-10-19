package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.OldInvoiceHeaderTempViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempView;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempViewModel;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempViewModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.UUID;

/**
 * Created by a.Jafarzadeh on 17/08/2017.
 */

public class OldInvoicesReportFragment extends VaranegarFragment {

    ReportAdapter<OldInvoiceHeaderTempViewModel> adapter;
    UUID customerUniqueId;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SimpleReportAdapter<OldInvoiceHeaderTempViewModel>((MainVaranegarActivity) getActivity(), OldInvoiceHeaderTempViewModel.class) {
            @Override
            public void bind(ReportColumns columns, OldInvoiceHeaderTempViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, OldInvoiceHeaderTempView.InvoiceNo, getString(R.string.invoice_no)).setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceHeaderTempView.InvoiceDate, getString(R.string.invoice_date)).setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceHeaderTempView.Amount, getString(R.string.invoice_amount)).calcTotal().setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceHeaderTempView.RemAmount, getString(R.string.invoice_rem_amount)).calcTotal().setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceHeaderTempView.DiscountAmount, getString(R.string.discount_amount)).calcTotal().setSortable().setFilterable());
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerUniqueId = UUID.fromString(getArguments().getString("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2"));
        View view = inflater.inflate(R.layout.fragment_oldinvoice_report, container, false);
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
        ReportView customerOldInvoicesView = (ReportView) view.findViewById(R.id.old_invoice_report_view);

        // sort old invoices by date
        Query oldInvoicesOrderByDate = OldInvoiceHeaderTempViewManager.getAll(customerUniqueId);
        oldInvoicesOrderByDate.orderByAscending(OldInvoiceHeaderTempView.InvoiceDate);

        adapter.create(new OldInvoiceHeaderTempViewModelRepository(), oldInvoicesOrderByDate, savedInstanceState);
        customerOldInvoicesView.setAdapter(adapter);
        adapter.addOnItemClickListener(new ContextMenuItem() {
            @Override
            public boolean isAvailable(int position) {
                return true;
            }

            @Override
            public String getName(int posiotn) {
                return getContext().getString(R.string.invoice_products);
            }

            @Override
            public int getIcon(int position) {
                return R.drawable.ic_report_24dp;
            }

            @Override
            public void run(int position) {
                InvoiceProductsReportFragment fragment = new InvoiceProductsReportFragment();
                fragment.setInvoiceNo(adapter.get(position).UniqueId, customerUniqueId);
                fragment.show(getChildFragmentManager(), "InvoiceProductsReportFragment");
            }
        });
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.ReportsPeriod, SysConfigManager.local);
        if (sysConfigModel != null) {
            String title = toolbar.getTitle().toString();
            toolbar.setTitle(title + " - " + SysConfigManager.getValue(sysConfigModel));
        }
        return view;
    }

    //    OldInvoicesReportAdapter productsAdapter;
//    UUID customerUniqueId;
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        productsAdapter.saveInstanceState(outState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        customerUniqueId = UUID.fromString(getArguments().getString("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2"));
//
//        View view = inflater.inflate(R.layout.fragment_oldinvoice_report, null);
//        ReportView oldInvoiceReport = (ReportView) view.findViewById(R.id.oldinvoice_report);
//        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
//        toolbar.setOnMenuClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getVaranegarActvity().toggleDrawer();
//            }
//        });
//        productsAdapter = new OldInvoicesReportAdapter(getVaranegarActvity());
//        productsAdapter.create(new OldInvoiceHeaderViewModelRepository(), OldInvoiceHeaderViewManager.getAll(customerUniqueId), savedInstanceState);
//        oldInvoiceReport.setAdapter(productsAdapter);
//        return view;
//
//    }
}
