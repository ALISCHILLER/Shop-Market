package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.TempReportPreViewManager;
import com.varanegar.vaslibrary.model.tempreportpreview.TempReportPreViewModel;
import com.varanegar.vaslibrary.model.tempreportpreview.TempReportPreViewModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/16/2017.
 */

public class PreviewFragment extends VaranegarFragment
{
    ReportAdapter<TempReportPreViewModel> adapter;
    UUID customerId;
    UUID callOrderId;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }
    public interface OnTextChanged {
        void search(String str);
    }
    public WarehouseProductQtyFragment.OnTextChanged onTextChanged;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean IsOnChkboxInventoryQty = true;
        boolean IsNameFilter = true;

        View view = inflater.inflate(R.layout.preview_report, null);
        SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().toggleDrawer();
            }
        });

        customerId = UUID.fromString(getStringArgument("5f4ec813-ab92-4e48-8f69-7a2a4fffcc20"));
        callOrderId = UUID.fromString(getStringArgument("160fb182-c4b1-4180-b215-a35ac3e436b7"));
        ReportView warehouse_qty = (ReportView) view.findViewById(R.id.warehouse_qty);
        adapter = new PreviewReport(getVaranegarActvity());
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.addOnItemClickListener(new ContextMenuItem() {
            @Override
            public boolean isAvailable(int position) {
                return true;
            }

            @Override
            public String getName(int position) {
                return null;
            }

            @Override
            public int getIcon(int position) {
                return 0;
            }

            @Override
            public void run(int position) {
                Toast.makeText(getContext(), "item clicked " + Integer.toString(position), Toast.LENGTH_SHORT).show();
            }
        });



        adapter.create(new TempReportPreViewModelRepository(), TempReportPreViewManager.getAll(),savedInstanceState);
        warehouse_qty.setAdapter(adapter);

        return view;


    }
}
