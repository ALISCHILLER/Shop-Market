package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.OldInvoiceDetailReportViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportView;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportViewModel;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportViewModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.UUID;

/**
 * Created by s.foroughi on 16/01/2017.
 */

public class OldInvoiceDetailReportFragment extends VaranegarFragment {

    ReportAdapter<OldInvoiceDetailReportViewModel> adapter;
    UUID customerUniqueId;


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SimpleReportAdapter<OldInvoiceDetailReportViewModel>((MainVaranegarActivity) getActivity(), OldInvoiceDetailReportViewModel.class) {
            @Override
            public void bind(ReportColumns columns, final OldInvoiceDetailReportViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, OldInvoiceDetailReportView.SalePDate, getString(R.string.date)).setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceDetailReportView.ProductCode, getString(R.string.product_code)).setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceDetailReportView.ProductName, getString(R.string.product_name)).setWeight(2).setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceDetailReportView.SaleNo, getString(R.string.invoice_no)).setSortable().setFilterable());
                columns.add(bind(entity, OldInvoiceDetailReportView.TotalQty, getString(R.string.request_qty)).setSortable().calcTotal().setFilterable().sendToDetail());
                columns.add(bind(entity, OldInvoiceDetailReportView.TotalAmount, getString(R.string.request_amount)).setSortable().calcTotal().setWeight(1).sendToDetail());
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerUniqueId = UUID.fromString(getArguments().getString("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2"));
        View view = inflater.inflate(R.layout.fragment_buy_summary_report, container,false);
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
        ReportView buySummaryReport = (ReportView) view.findViewById(R.id.buy_summary_report);
        adapter.create(new OldInvoiceDetailReportViewModelRepository(), OldInvoiceDetailReportViewManager.getAll(customerUniqueId), savedInstanceState);
        buySummaryReport.setAdapter(adapter);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.ReportsPeriod, SysConfigManager.local);
        if (sysConfigModel != null) {
            String title = toolbar.getTitle().toString();
            toolbar.setTitle(title + " - " + SysConfigManager.getValue(sysConfigModel));
        }
        return view;
    }


}
