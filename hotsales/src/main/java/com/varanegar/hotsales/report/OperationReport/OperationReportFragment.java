package com.varanegar.hotsales.report.OperationReport;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.hotsales.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.operationReport.OperationReportViewManager;
import com.varanegar.vaslibrary.model.operationReport.OperationReportView;
import com.varanegar.vaslibrary.model.operationReport.OperationReportViewModel;
import com.varanegar.vaslibrary.model.operationReport.OperationReportViewModelRepository;
import com.varanegar.vaslibrary.print.OperationPrint.OperationPrintHelper;

/**
 * Created by g.aliakbar on 21/04/2018.
 */

public class OperationReportFragment extends VaranegarFragment {

    ReportAdapter<OperationReportViewModel> adapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operation_report, container, false);
        view.findViewById(R.id.menu_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ReportView reportView = view.findViewById(R.id.operation_report_view);

        adapter = new SimpleReportAdapter<OperationReportViewModel>((MainVaranegarActivity) getActivity(), OperationReportViewModel.class) {
            @Override
            public void bind(ReportColumns columns, OperationReportViewModel entity) {

                bindRowNumber(columns);
                columns.add(bind(entity, OperationReportView.CustomerCode, getString(com.varanegar.vaslibrary.R.string.customer_code)).setFilterable().setWeight(1).setFrizzed());
                columns.add(bind(entity, OperationReportView.CustomerName, getString(com.varanegar.vaslibrary.R.string.customer_name)).setFilterable().setWeight(1.5f).setFrizzed());
                columns.add(bind(entity, OperationReportView.StoreName, getString(com.varanegar.vaslibrary.R.string.print_storename)).setFilterable().setWeight(1.5f));
                columns.add(bind(entity, OperationReportView.TotalAmount, getString(com.varanegar.vaslibrary.R.string.total_order_amount)).calcTotal());
                columns.add(bind(entity, OperationReportView.OrderDiscountAmount, getString(com.varanegar.vaslibrary.R.string.discount_amount)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.OrderAddAmount, getString(com.varanegar.vaslibrary.R.string.add_amount)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.TotalNetAmount, getString(com.varanegar.vaslibrary.R.string.total_order_net_amount)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.ReturnRequestAmount, getString(R.string.return_net_amount)).calcTotal().setWeight(1.5f));
                columns.add(bind(entity, OperationReportView.PayableAmount, getString(R.string.payable)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.TotalPaidAmount, getString(com.varanegar.vaslibrary.R.string.payment)).calcTotal());
                columns.add(bind(entity, OperationReportView.AmountCredit, getString(com.varanegar.vaslibrary.R.string.pay_from_credit)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.AmountCash, getString(com.varanegar.vaslibrary.R.string.cash)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.AmountCheque, getString(com.varanegar.vaslibrary.R.string.cheque)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.AmountCard, getString(com.varanegar.vaslibrary.R.string.card_reader)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.AmountDiscount, getString(com.varanegar.vaslibrary.R.string.settlement_discount)).calcTotal().sendToDetail());
                columns.add(bind(entity, OperationReportView.Recipe, getString(com.varanegar.vaslibrary.R.string.receipt)).calcTotal().sendToDetail());
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new OperationReportViewModelRepository(), OperationReportViewManager.getAll(), savedInstanceState);
        reportView.setAdapter(adapter);

        if (adapter.size() > 0) {
            View printImageBtn = view.findViewById(R.id.print_image_button);
            printImageBtn.setVisibility(View.VISIBLE);
            printImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OperationPrintHelper print = new OperationPrintHelper(getVaranegarActvity());
                    print.start(null);
                }
            });
        }
        return view;
    }

}
