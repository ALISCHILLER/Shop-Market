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
import com.varanegar.vaslibrary.manager.Target.TargetSaleManProductViewManager;
import com.varanegar.vaslibrary.model.target.TargetDetailModel;
import com.varanegar.vaslibrary.model.target.TargetDetailModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 1/4/2018.
 */

public class TargetSaleManPoductReport extends VaranegarFragment {
    ReportAdapter<TargetDetailModel> adapter;
    private UUID targetUniqueId;


    public void setTargetUniqueId(@NonNull UUID targetUniqueId) {
        Bundle bundle = new Bundle();
        bundle.putString("b7afb83f-9684-4c79-bf83-28025e4e31fa", targetUniqueId.toString());
        setArguments(bundle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        targetUniqueId = UUID.fromString(getArguments().getString("b7afb83f-9684-4c79-bf83-28025e4e31fa"));
        View view = inflater.inflate(R.layout.fargment_target_saleman_product_report, container, false);
        ReportView targetSalemanProductReport = (ReportView) view.findViewById(R.id.target_saleman_product_report);
        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        adapter = new SimpleReportAdapter<TargetDetailModel>((MainVaranegarActivity) getActivity(), TargetDetailModel.class) {
            @Override
            public void bind(ReportColumns columns, TargetDetailModel entity) {
                bindRowNumber(columns);
//                columns.add(bind(entity, TargetDetail.ProductName, getString(R.string.product_name)).setSortable());
//                columns.add(bind(entity, TargetDetail.Target, getString(R.string.target)));
//                columns.add(bind(entity, TargetDetail.AchievedInPeriod, getString(R.string.achieved_in_period)));
//                columns.add(bind(entity, TargetDetail.AchievedInDayPercent, getString(R.string.achieved_in_day_percent)));
//                columns.add(bind(entity, TargetDetail.AchievedInPeriodPercent, getString(R.string.achieved_in_period_percent)));
//                columns.add(bind(entity, TargetDetail.SaleAverageInDaysRemain, getString(R.string.sales_average_in_days_remain)));
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new TargetDetailModelRepository(), TargetSaleManProductViewManager.getAll(), savedInstanceState);
        targetSalemanProductReport.setAdapter(adapter);
        return view;
    }
}