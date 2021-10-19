package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerOpenInvoicesViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.CustomerOpenInvoicesView.CustomerOpenInvoicesView;
import com.varanegar.vaslibrary.model.CustomerOpenInvoicesView.CustomerOpenInvoicesViewModel;
import com.varanegar.vaslibrary.model.CustomerOpenInvoicesView.CustomerOpenInvoicesViewModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.util.UUID;

/**
 * Created by s.foroughi on 29/01/2017.
 */

public class CustomerOpenInvoicesReportFragment extends VaranegarFragment {
    ReportAdapter<CustomerOpenInvoicesViewModel> adapter;
    UUID customerUniqueId;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerUniqueId = UUID.fromString(getArguments().getString("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2"));

        View view = inflater.inflate(R.layout.fragment_open_invoices_report, container,false);
        ReportView customerCardexReport = (ReportView) view.findViewById(R.id.open_invoices_report);
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
       adapter = new SimpleReportAdapter<CustomerOpenInvoicesViewModel>((MainVaranegarActivity) getActivity(),CustomerOpenInvoicesViewModel.class) {
            @Override
            public void bind(ReportColumns columns, final CustomerOpenInvoicesViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, CustomerOpenInvoicesView.DealerName, getString(R.string.print_dealername)).setFilterable());
                columns.add(bind(entity, CustomerOpenInvoicesView.SaleNo, getString(R.string.sale_no)).setSortable().setFilterable());
                columns.add(bind(entity, CustomerOpenInvoicesView.SalePDate, getString(R.string.sale_date)).setSortable().setFilterable());
                columns.add(bind(entity, CustomerOpenInvoicesView.TotalAmount, getString(R.string.total_amount)).setSortable().calcTotal().setFilterable());
                columns.add(bind(entity, CustomerOpenInvoicesView.PayAmount, getString(R.string.pay_amount)).setSortable().calcTotal().setFilterable().sendToDetail());
                columns.add(bind(entity, CustomerOpenInvoicesView.RemAmount, getString(R.string.rem_invoice_amount)).setSortable().calcTotal().setFilterable().sendToDetail());
            }
        };
       adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel OpenInvoiceBase = sysConfigManager.read(ConfigKey.OpenInvoicesBasedOn, SysConfigManager.cloud);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && SysConfigManager.compare(OpenInvoiceBase, CustomerOpenInvoicesViewManager.OpenInvoiceBaseOnType.BaseOnDealer)) {
            UserModel userModel = UserManager.readFromFile(getContext());
            adapter.create(new CustomerOpenInvoicesViewModelRepository(), CustomerOpenInvoicesViewManager.getAllForDealer(customerUniqueId, userModel.UniqueId), savedInstanceState);
        }
        else
            adapter.create(new CustomerOpenInvoicesViewModelRepository(), CustomerOpenInvoicesViewManager.getAll(customerUniqueId),savedInstanceState);
        customerCardexReport.setAdapter(adapter);
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.ReportsPeriod, SysConfigManager.local);
        if (sysConfigModel != null) {
            String title = toolbar.getTitle().toString();
            toolbar.setTitle(title + " - " + SysConfigManager.getValue(sysConfigModel));
        }
        return view;
    }
}