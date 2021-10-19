package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
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
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;

/**
 * Created by A.Jafarzadeh on 8/10/2017.
 */

public class CustomerOrdersFragment extends VaranegarFragment {
    ReportAdapter<CustomerCallOrderModel> adapter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_order_report, null);

        ReportView customer_order_report = (ReportView) view.findViewById(R.id.fragment_customer_order_report);

        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });

        adapter = new SimpleReportAdapter<CustomerCallOrderModel>((MainVaranegarActivity) getActivity(), CustomerCallOrderModel.class) {
            @Override
            public void bind(ReportColumns columns, CustomerCallOrderModel entity) {
                // columns.add(bind(entity))
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));

        return view;
    }
}
