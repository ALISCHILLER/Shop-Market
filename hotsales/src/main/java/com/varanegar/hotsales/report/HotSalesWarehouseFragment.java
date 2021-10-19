package com.varanegar.hotsales.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.hotsales.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.report.WarehouseProductQtyFragment;

/**
 * Created by A.Jafarzadeh on 3/11/2018.
 */

public class HotSalesWarehouseFragment extends WarehouseProductQtyFragment {
    @Override
    public ReportAdapter createAdapter(SysConfigModel showStockLevel, BackOfficeType backOfficeType) {
        return new HotSalesWarehouseReportAdapter(getVaranegarActvity(), showStockLevel, backOfficeType);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ImageView printBtn = view.findViewById(R.id.warehouse_report_print);
            printBtn.setVisibility(View.VISIBLE);
            printBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapter.size() > 0) {
                        WarehouseQtyPrintHelper print = new WarehouseQtyPrintHelper(getVaranegarActvity());
                        print.start(null);
                    }
                }
            });
        }
        return view;
    }
}
