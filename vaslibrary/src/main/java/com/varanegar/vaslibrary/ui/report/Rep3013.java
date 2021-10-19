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
import com.varanegar.vaslibrary.manager.Rep3013ViewManager;
import com.varanegar.vaslibrary.model.Rep3013View.Rep3013View;
import com.varanegar.vaslibrary.model.Rep3013View.Rep3013ViewModel;
import com.varanegar.vaslibrary.model.Rep3013View.Rep3013ViewModelRepository;

/**
 * Created by s.foroughi on 17/01/2017.
 */

public class Rep3013 extends VaranegarFragment {
    ReportAdapter<Rep3013ViewModel> adapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rep3013, container, false);
        ReportView rep3013 = (ReportView) view.findViewById(R.id.fragment_rep3013);

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
        adapter = new SimpleReportAdapter<Rep3013ViewModel>((MainVaranegarActivity) getActivity(), Rep3013ViewModel.class) {
            @Override
            public void bind(ReportColumns columns, final Rep3013ViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, Rep3013View.CustomerCode, getString(R.string.customer_code)).setSortable().setFilterable().setFrizzed());
                columns.add(bind(entity, Rep3013View.CustomerName, getString(R.string.customer_name)).setSortable().setFilterable().setWeight(2));
                columns.add(bind(entity, Rep3013View.CustRemAmountForSaleOffice, getString(R.string.cust_rem_amount_for_sales_office)).calcTotal().setFilterable().setWeight(1.5f));
                columns.add(bind(entity, Rep3013View.CustRemAmountAll, getString(R.string.cust_rem_amount_all)).calcTotal().setFilterable().setWeight(1.5f));
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new Rep3013ViewModelRepository(), Rep3013ViewManager.getAll(), savedInstanceState);
        rep3013.setAdapter(adapter);
        return view;

    }
}