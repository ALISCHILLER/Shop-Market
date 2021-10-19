package com.varanegar.vaslibrary.ui.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.customercardex.CustomerCardexManager;
import com.varanegar.vaslibrary.manager.customercardex.CustomerCardexTempManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardex;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexModel;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexModelRepository;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexTemp;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexTempModel;
import com.varanegar.vaslibrary.model.customerCardex.CustomerCardexTempModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by a.Jafarzadeh on 14/01/2017.
 */

public class CustomerCardexReportFragment extends VaranegarFragment {
    ReportAdapter<CustomerCardexModel> adapter;
    ReportAdapter<CustomerCardexTempModel> tempAdapter;
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
        CustomerCardexManager customerCardexManager = new CustomerCardexManager(getContext());
        final boolean hasDouDate = customerCardexManager.checkHasDouDate(customerUniqueId);
        adapter = new SimpleReportAdapter<CustomerCardexModel>(this, CustomerCardexModel.class) {
            @Override
            public void bind(ReportColumns columns, final CustomerCardexModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, CustomerCardex.VoucherNo, getString(R.string.vouncherno)).setFilterable().setWeight(1).setFrizzed());
                columns.add(bind(entity, CustomerCardex.VoucherTypeName, getString(R.string.type)).setFilterable().setWeight(2));
                columns.add(bind(entity, CustomerCardex.VoucherDate, getString(R.string.date)).setFilterable().setWeight(1));
                columns.add(bind(entity, CustomerCardex.BesAmount, getString(R.string.besamount)).calcTotal().setFilterable().setWeight(1).sendToDetail().calcTotal());
                columns.add(bind(entity, CustomerCardex.BedAmount, getString(R.string.bedamount)).calcTotal().setFilterable().setWeight(1).sendToDetail().calcTotal());
                columns.add(bind(entity, CustomerCardex.RemainAmount, getString(R.string.remain_amount)).setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, CustomerCardex.BankName, getString(R.string.bank_name)).setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, CustomerCardex.ChqDate, getString(R.string.check_date)).setFilterable().setWeight(1).sendToDetail());
                if (hasDouDate) {
                    columns.add(bind(entity, CustomerCardex.NotDueDate, getString(R.string.settlement_usance)).setFilterable().setSortable());
                    columns.add(bind(entity, CustomerCardex.NotDueDateMiladi, getString(R.string.remain_settlement_usance)).setCustomViewHolder(new CustomViewHolder<CustomerCardexModel>() {
                        @Override
                        public void onBind(View view, CustomerCardexModel entity) {
                            if (entity.NotDueDateMiladi != null) {
                                long remainDays = HelperMethods.remainDaysBetweenTowDate(entity.NotDueDateMiladi, new Date());
                                if (remainDays > 0) {
                                    TextView textView = (TextView) view.findViewById(R.id.remain_settlement_days);
                                    textView.setText(String.valueOf(remainDays));
                                }
                            }
                        }

                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                            return inflater.inflate(R.layout.fragment_remain_settlement_days, parent, false);
                        }
                    }));
                }
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        tempAdapter = new SimpleReportAdapter<CustomerCardexTempModel>(this, CustomerCardexTempModel.class) {
            @Override
            public void bind(ReportColumns columns, final CustomerCardexTempModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, CustomerCardexTemp.VoucherNo, getString(R.string.vouncherno)).setFilterable().setWeight(1));
                columns.add(bind(entity, CustomerCardexTemp.VoucherTypeName, getString(R.string.type)).setFilterable().setWeight(2));
                columns.add(bind(entity, CustomerCardexTemp.VoucherDate, getString(R.string.date)).setFilterable().setWeight(1));
                columns.add(bind(entity, CustomerCardexTemp.BesAmount, getString(R.string.besamount)).calcTotal().setFilterable().setWeight(1).sendToDetail().calcTotal());
                columns.add(bind(entity, CustomerCardexTemp.BedAmount, getString(R.string.bedamount)).calcTotal().setFilterable().setWeight(1).sendToDetail().calcTotal());
                columns.add(bind(entity, CustomerCardexTemp.RemainAmount, getString(R.string.remain_amount)).setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, CustomerCardexTemp.BankName, getString(R.string.bank_name)).setFilterable().setWeight(1).sendToDetail());
                columns.add(bind(entity, CustomerCardexTemp.ChqDate, getString(R.string.check_date)).setFilterable().setWeight(1).sendToDetail());
                if (hasDouDate) {
                    columns.add(bind(entity, CustomerCardexTemp.NotDueDate, getString(R.string.settlement_usance)).setFilterable().setSortable());
                    columns.add(bind(entity, CustomerCardexTemp.NotDueDateMiladi, getString(R.string.remain_settlement_usance)).setCustomViewHolder(new CustomViewHolder<CustomerCardexTempModel>() {
                        @Override
                        public void onBind(View view, CustomerCardexTempModel entity) {
                            if (entity.NotDueDateMiladi != null) {
                                long remainDays = HelperMethods.remainDaysBetweenTowDate(entity.NotDueDateMiladi, new Date());
                                if (remainDays > 0) {
                                    TextView textView = (TextView) view.findViewById(R.id.remain_settlement_days);
                                    textView.setText(String.valueOf(remainDays));
                                }
                            }
                        }

                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                            return inflater.inflate(R.layout.fragment_remain_settlement_days, parent, false);
                        }
                    }));
                }
            }
        };
        tempAdapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        CustomerCardexTempManager customerCardexTempManager = new CustomerCardexTempManager(getContext());
        List<CustomerCardexTempModel> customerCardexTempModels = customerCardexTempManager.getCustomerItems(customerUniqueId);
        if (customerCardexTempModels != null && customerCardexTempModels.size() > 0) {
            tempAdapter.create(new CustomerCardexTempModelRepository(), CustomerCardexTempManager.getAll(customerUniqueId, true), savedInstanceState);
        } else
            adapter.create(new CustomerCardexModelRepository(), CustomerCardexManager.getAll(customerUniqueId, true), savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_customer_cardex_report, container, false);
        ReportView customerCardexReport = (ReportView) view.findViewById(R.id.customer_cardex_report);
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
        if (customerCardexTempModels != null && customerCardexTempModels.size() > 0)
            customerCardexReport.setAdapter(tempAdapter);
        else
            customerCardexReport.setAdapter(adapter);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.ReportsPeriod, SysConfigManager.local);
        if (sysConfigModel != null) {
            String title = toolbar.getTitle().toString();
            toolbar.setTitle(title + " - " + SysConfigManager.getValue(sysConfigModel));
        }
        return view;
    }
}

