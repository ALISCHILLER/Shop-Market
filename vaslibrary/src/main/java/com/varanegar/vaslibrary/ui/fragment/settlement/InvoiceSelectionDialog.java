package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.prefs.Preferences;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerOpenInvoicesViewManager;
import com.varanegar.vaslibrary.manager.OldInvoiceHeaderViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerInvoicePaymentManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderView;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/12/2017.
 */

public class InvoiceSelectionDialog extends CuteAlertDialog {
    private SimpleReportAdapter<OldInvoiceHeaderViewModel> adapter;
    private TextView totalAmountTextView;
    private UUID customerId;
    private List<OldInvoiceHeaderViewModel> oldInvoiceHeaderViewModels;
    private CheckBox selectAllCheckBox;
    private Preferences preferences;

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.open_invoices);
        setSizingPolicy(SizingPolicy.Maximum);
        customerId = UUID.fromString(getArguments().getString("6e234a39-9d67-462c-af24-6b17d49cabf0"));
        View view = inflater.inflate(R.layout.layout_not_settled_invoices, viewGroup, true);
        selectAllCheckBox = view.findViewById(R.id.select_all_check_box);
        preferences = new Preferences(getContext());
        boolean selectAll = preferences.getBoolean(Preferences.UserPreferences, null, null, UUID.fromString("dba935a4-9260-4183-9d5b-6827b91b4255"), false);
        selectAllCheckBox.setChecked(selectAll);
        selectAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    adapter.selectAll();
                else
                    adapter.deselectAll();
                adapter.notifyDataSetChanged();
            }
        });
        ReportView invoicesReportView = (ReportView) view.findViewById(R.id.invoices_report_view);
        totalAmountTextView = (TextView) view.findViewById(R.id.total_amount_text_view);
        loadOldInvoices();

        adapter = new SimpleReportAdapter<OldInvoiceHeaderViewModel>(getVaranegarActvity(), OldInvoiceHeaderViewModel.class) {
            @Override
            public void bind(ReportColumns columns, OldInvoiceHeaderViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, OldInvoiceHeaderView.InvoiceNo, getString(R.string.invoice_no)).setSortable());
                columns.add(bind(entity, OldInvoiceHeaderView.InvoiceDate, getString(R.string.invoice_date)).setSortable());
                columns.add(bind(entity, OldInvoiceHeaderView.RemAmount, getString(R.string.rem_amount)).calcTotal().setSortable());
                columns.add(bind(entity, OldInvoiceHeaderView.Amount, getString(R.string.amount)).calcTotal().setSortable());
            }
        };
        adapter.create(oldInvoiceHeaderViewModels, savedInstanceState);
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        invoicesReportView.setAdapter(adapter);
        adapter.setOnItemSelectListener(new ReportAdapter.OnItemSelectListener<OldInvoiceHeaderViewModel>() {
            @Override
            public void onItemSelected(int idx) {
                refreshForm();
            }

            @Override
            public void onItemDeSelected(int idx) {
                refreshForm();
            }
        });
        for (OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel :
                oldInvoiceHeaderViewModels) {
            if (oldInvoiceHeaderViewModel.HasPayment)
                adapter.select(oldInvoiceHeaderViewModel);
        }
        adapter.notifyDataSetChanged();
        refreshForm();
    }

    private void loadOldInvoices() {
        OldInvoiceHeaderViewManager oldInvoiceHeaderViewManager = new OldInvoiceHeaderViewManager(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel OpenInvoiceBase = sysConfigManager.read(ConfigKey.OpenInvoicesBasedOn, SysConfigManager.cloud);
        oldInvoiceHeaderViewModels = new ArrayList<>();
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && SysConfigManager.compare(OpenInvoiceBase, CustomerOpenInvoicesViewManager.OpenInvoiceBaseOnType.BaseOnDealer)) {
            UserModel userModel = UserManager.readFromFile(getContext());
            oldInvoiceHeaderViewModels = oldInvoiceHeaderViewManager.getNotSettledInvoices(customerId, userModel.UniqueId);
        } else {
            oldInvoiceHeaderViewModels = oldInvoiceHeaderViewManager.getNotSettledInvoices(customerId);
        }
    }

    private void refreshForm() {
        double totalRemAmount = 0;
        for (OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel :
                adapter.getSelectedItems()) {
            double rem = oldInvoiceHeaderViewModel.RemAmount == null ? 0 : oldInvoiceHeaderViewModel.RemAmount.doubleValue();
            totalRemAmount += rem;
        }
        totalAmountTextView.setText(HelperMethods.currencyToString(Currency.valueOf(totalRemAmount)));
    }

    public interface OnInvoiceSelected {
        void run();
    }

    public OnInvoiceSelected onInvoiceSelected;

    @Override
    public void ok() {
        preferences.putBoolean(Preferences.UserPreferences, null, null, UUID.fromString("dba935a4-9260-4183-9d5b-6827b91b4255"), selectAllCheckBox.isChecked());
        final List<OldInvoiceHeaderViewModel> list = adapter.getSelectedItems();
        final CustomerInvoicePaymentManager customerInvoicePaymentManager = new CustomerInvoicePaymentManager(getContext());
        try {
            customerInvoicePaymentManager.removeAll(customerId);
            if (list.size() > 0 && onInvoiceSelected != null) {
                final int[] i = {0};
                for (OldInvoiceHeaderViewModel oldInvoiceHeaderViewModel :
                        list) {
                    customerInvoicePaymentManager.add(oldInvoiceHeaderViewModel);
                }
            }
            if (onInvoiceSelected != null)
                onInvoiceSelected.run();
            dismiss();
        } catch (Exception ex) {
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    @Override
    public void cancel() {

    }

    public void setCustomerId(@NonNull UUID customerId) {
        Bundle bundle = new Bundle();
        bundle.putString("6e234a39-9d67-462c-af24-6b17d49cabf0", customerId.toString());
        setArguments(bundle);
    }
}
