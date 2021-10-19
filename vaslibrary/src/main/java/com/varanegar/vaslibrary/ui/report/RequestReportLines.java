package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 1/27/2018.
 */

public class RequestReportLines extends CuteDialogWithToolbar {

    ReportAdapter<CustomerCallOrderOrderViewModel> adapter;
    private UUID customerId;
    private UUID orderId;

    public void setCustomerId(@NonNull UUID customerId, @NonNull UUID orderId) {
        Bundle bundle = new Bundle();
        bundle.putString("b16e196c-46bd-48a2-8b6f-59c9d2ecf8d9", customerId.toString());
        bundle.putString("fbd951e7-d5b2-4296-901c-776784e21545", orderId.toString());
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
        adapter = new SimpleReportAdapter<CustomerCallOrderOrderViewModel>((MainVaranegarActivity) getVaranegarActvity(), CustomerCallOrderOrderViewModel.class) {
            @Override
            public void bind(ReportColumns columns, CustomerCallOrderOrderViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, CustomerCallOrderOrderView.ProductCode, getString(R.string.product_code)).setSortable());
                columns.add(bind(entity, CustomerCallOrderOrderView.ProductName, getString(R.string.product_name)).setSortable().setWeight(2));
                columns.add(bind(entity, CustomerCallOrderOrderView.TotalQty, getString(R.string.count_label)).setSortable());
                if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
                    columns.add(bind(entity, CustomerCallOrderOrderView.UnitPrice, getString(R.string.unit_price)));
                }
                columns.add(bind(entity, CustomerCallOrderOrderView.RequestAmount, getString(R.string.value)).setSortable().calcTotal());
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
    }

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerId = UUID.fromString(getArguments().getString("b16e196c-46bd-48a2-8b6f-59c9d2ecf8d9"));
        orderId = UUID.fromString(getArguments().getString("fbd951e7-d5b2-4296-901c-776784e21545"));
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            setTitle(R.string.sale_report_lines);
        }else if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            setTitle(R.string.reauest_report_lines);
        }
        View view = inflater.inflate(R.layout.fragment_request_report_lines, container, false);
        ReportView RequestLinesReport = (ReportView) view.findViewById(R.id.request_report_lines);
        adapter.create(new CustomerCallOrderOrderViewModelRepository(), new CustomerCallOrderOrderViewManager(getContext()).getLinesQuery(orderId, null), savedInstanceState);
        RequestLinesReport.setAdapter(adapter);
        return view;
    }
}
