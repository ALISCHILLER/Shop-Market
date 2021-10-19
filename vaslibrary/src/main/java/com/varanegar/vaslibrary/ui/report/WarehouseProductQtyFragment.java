package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.DistWarehouseProductQtyViewManager;
import com.varanegar.vaslibrary.manager.WarehouseProductQtyViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyViewModelRepository;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.print.warehouseprint.DistWarehousePrint;

import timber.log.Timber;

/**
 * Created by s.foroughi on 31/01/2017.
 */

public abstract class WarehouseProductQtyFragment extends VaranegarFragment {
    protected ReportAdapter<WarehouseProductQtyViewModel> adapter;
    protected ReportAdapter<DistWarehouseProductQtyViewModel> distAdapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            distAdapter.saveInstanceState(outState);
        else
            adapter.saveInstanceState(outState);

    }

    public interface OnTextChanged {
        void search(String str);
    }

    public OnTextChanged onTextChanged;

    public abstract ReportAdapter createAdapter(SysConfigModel showStockLevel, BackOfficeType backOfficeType);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            boolean showAll = true;
            View view = inflater.inflate(R.layout.fragment_warehouseqty_report, container, false);
            view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            view.findViewById(R.id.menu_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getVaranegarActvity().toggleDrawer();
                }
            });
            ReportView warehouse_qty = (ReportView) view.findViewById(R.id.warehouse_qty);
            final CheckBox btnInventoryQtyCheck = (CheckBox) view.findViewById(R.id.warehouse_checkbox);
            final EditText filterName = (EditText) view.findViewById(R.id.product_name_filter);
            btnInventoryQtyCheck.setChecked(showAll);
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel showStockLevel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                ImageView printImageView = view.findViewById(R.id.warehouse_report_print);
                printImageView.setVisibility(View.VISIBLE);
                printImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DistWarehousePrint print = new DistWarehousePrint(getVaranegarActvity(), !btnInventoryQtyCheck.isChecked());
                        print.start(null);
                    }
                });
                distAdapter = createAdapter(showStockLevel, backOfficeType);
                distAdapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                btnInventoryQtyCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (btnInventoryQtyCheck.isChecked())
                            distAdapter.refresh(DistWarehouseProductQtyViewManager.search(filterName.getText().toString(), false, false));
                        else
                            distAdapter.refresh(DistWarehouseProductQtyViewManager.search(filterName.getText().toString(), true, false));


                    }
                });


                filterName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        distAdapter.refresh(DistWarehouseProductQtyViewManager.search(charSequence.toString(), !btnInventoryQtyCheck.isChecked(), false));

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String text = editable.toString();
                        if (onTextChanged != null) {
                            onTextChanged.search(text);
                            distAdapter.refresh(DistWarehouseProductQtyViewManager.search(editable.toString(), !btnInventoryQtyCheck.isChecked(), false));
                        }
                    }
                });

                distAdapter.setPagingSize(500);
                distAdapter.create(new DistWarehouseProductQtyViewModelRepository(), DistWarehouseProductQtyViewManager.search(null, false, false), savedInstanceState);
                warehouse_qty.setAdapter(distAdapter);
            } else {
                adapter = createAdapter(showStockLevel, backOfficeType);
                adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                btnInventoryQtyCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (btnInventoryQtyCheck.isChecked())
                            adapter.refresh(WarehouseProductQtyViewManager.search(filterName.getText().toString(), null));
                        else
                            adapter.refresh(WarehouseProductQtyViewManager.search(filterName.getText().toString(), true));


                    }
                });


                filterName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.refresh(WarehouseProductQtyViewManager.search(charSequence.toString(), btnInventoryQtyCheck.isChecked() ? null : true));

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String text = editable.toString();
                        if (onTextChanged != null) {
                            onTextChanged.search(text);
                            adapter.refresh(WarehouseProductQtyViewManager.search(editable.toString(), btnInventoryQtyCheck.isChecked() ? null : true));
                        }
                    }
                });


                adapter.create(new WarehouseProductQtyViewModelRepository(), WarehouseProductQtyViewManager.search(null, null), savedInstanceState);
                warehouse_qty.setAdapter(adapter);
            }

            return view;
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setMessage(R.string.back_office_type_is_uknown);
            dialog.show();
            return null;
        }


    }

}
