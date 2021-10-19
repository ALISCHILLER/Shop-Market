package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.OldInvoiceDetailReportViewManager;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportView;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportViewModel;
import com.varanegar.vaslibrary.model.oldinvoicedetailreportview.OldInvoiceDetailReportViewModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/17/2017.
 */

public class InvoiceProductsReportFragment extends CuteDialogWithToolbar {

    ReportAdapter<OldInvoiceDetailReportViewModel> adapter;
    private UUID saleId;
    private UUID customerId;

    public void setInvoiceNo(@NonNull UUID invoiceNo, @NonNull UUID customerId) {
        Bundle bundle = new Bundle();
        bundle.putString("8fd281df-3547-44cc-81f5-b31abd706e12", invoiceNo.toString());
        bundle.putString("ef596ee1-9832-4007-861b-9c404f1cd5ec", customerId.toString());
        setArguments(bundle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SimpleReportAdapter<OldInvoiceDetailReportViewModel>((MainVaranegarActivity) getVaranegarActvity(), OldInvoiceDetailReportViewModel.class) {
            @Override
            public void bind(ReportColumns columns, OldInvoiceDetailReportViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, OldInvoiceDetailReportView.ProductCode, getString(R.string.product_code)).setSortable());
                columns.add(bind(entity, OldInvoiceDetailReportView.ProductName, getString(R.string.product_name)).setSortable());
                columns.add(bind(entity, OldInvoiceDetailReportView.TotalQty, getString(R.string.qty_numeric)).setSortable().calcTotal());
                columns.add(bind(entity, OldInvoiceDetailReportView.TotalAmount, getString(R.string.value)).setSortable().calcTotal());
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
    }


    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        saleId = UUID.fromString(getArguments().getString("8fd281df-3547-44cc-81f5-b31abd706e12"));
        customerId = UUID.fromString(getArguments().getString("ef596ee1-9832-4007-861b-9c404f1cd5ec"));
        setTitle(R.string.invoice_products);
        View view = inflater.inflate(R.layout.fragment_invoice_products_report, container, false);
        ReportView InvoiceProductsReport = (ReportView) view.findViewById(R.id.buy_summary_report);
        adapter.create(new OldInvoiceDetailReportViewModelRepository(), OldInvoiceDetailReportViewManager.getAll(customerId, saleId), savedInstanceState);
        InvoiceProductsReport.setAdapter(adapter);
        return view;
    }
}
