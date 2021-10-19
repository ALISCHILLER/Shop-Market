package com.varanegar.vaslibrary.ui.report.target;

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
import com.varanegar.vaslibrary.manager.Target.TargetCustomerProductViewManager;
import com.varanegar.vaslibrary.model.targetcustomerproductview.TargetCustomerProductView;
import com.varanegar.vaslibrary.model.targetcustomerproductview.TargetCustomerProductViewModel;
import com.varanegar.vaslibrary.model.targetcustomerproductview.TargetCustomerProductViewModelRepository;

/**
 * Created by A.Jafarzadeh on 1/4/2018.
 */

public class TargetCustomerProductReport extends VaranegarFragment {
    ReportAdapter<TargetCustomerProductViewModel> adapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_target_customer_product_report, container, false);
        ReportView targetCustomerProductReport = (ReportView) view.findViewById(R.id.target_customer_product_report);
        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        adapter = new SimpleReportAdapter<TargetCustomerProductViewModel>((MainVaranegarActivity) getActivity(), TargetCustomerProductViewModel.class) {
            @Override
            public void bind(ReportColumns columns, TargetCustomerProductViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, TargetCustomerProductView.CustomerName, getString(R.string.customer_name)).setSortable());
                columns.add(bind(entity, TargetCustomerProductView.ProductName, getString(R.string.product_name)).setSortable());
                columns.add(bind(entity, TargetCustomerProductView.Target, getString(R.string.target)));
                columns.add(bind(entity, TargetCustomerProductView.AchievedInPeriod, getString(R.string.achieved_in_period)));
                columns.add(bind(entity, TargetCustomerProductView.AchievedInDayPercent, getString(R.string.achieved_in_day_percent)));
                columns.add(bind(entity, TargetCustomerProductView.AchievedInPeriodPercent, getString(R.string.achieved_in_period_percent)));
                columns.add(bind(entity, TargetCustomerProductView.SaleAverageInDaysRemain, getString(R.string.sales_average_in_days_remain)));
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new TargetCustomerProductViewModelRepository(), TargetCustomerProductViewManager.getAll(), savedInstanceState);
        targetCustomerProductReport.setAdapter(adapter);
        return view;
    }
}