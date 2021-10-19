package com.varanegar.vaslibrary.ui.dialog;

import android.animation.ValueAnimator;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CustomerOpenInvoicesViewManager;
import com.varanegar.vaslibrary.manager.OldInvoiceHeaderTempViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempView;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.promotion.CalcPromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderPromotion;
import com.varanegar.vaslibrary.promotion.PromotionCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;

import static varanegar.com.vdmclient.call.GlobalVariable.usanceDay;

public class MultiInvoiceVectorDialog extends CuteAlertDialog {
    private SimpleReportAdapter<OldInvoiceHeaderTempViewModel> adapter;
    private UUID customerId;
    private UUID callOrderId;
    private List<OldInvoiceHeaderTempViewModel> oldInvoiceHeaderTempViewModels;
    private TextView textView;
    private CardView cardView;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        setTitle(R.string.calculate_multi_invoice_vector);
        setSizingPolicy(SizingPolicy.Maximum);
        customerId = UUID.fromString(getArguments().getString("76921f49-0ce2-4540-8f58-fbe3a17c482a"));
        callOrderId = UUID.fromString(getArguments().getString("9390f3b1-6645-4f24-8a82-6684a08902a1"));
        View view = inflater.inflate(R.layout.dialog_multi_invoice_vector, viewGroup, true);
        textView = view.findViewById(R.id.date);
        cardView = view.findViewById(R.id.date_card_view);
        CheckBox selectAllCheckBox = view.findViewById(R.id.select_all_check_box);
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
        ReportView invoicesReportView = view.findViewById(R.id.invoices_report_view);
        loadOldInvoices();
        adapter = new SimpleReportAdapter<OldInvoiceHeaderTempViewModel>(getVaranegarActvity(), OldInvoiceHeaderTempViewModel.class) {
            @Override
            public void bind(ReportColumns columns, OldInvoiceHeaderTempViewModel entity) {
                bindRowNumber(columns);

                columns.add(bind(entity, OldInvoiceHeaderTempView.InvoiceNo, getString(R.string.invoice_no)).setSortable());
                columns.add(bind(entity, OldInvoiceHeaderTempView.InvoiceDate, getString(R.string.invoice_date)).setSortable());
                columns.add(bind(entity, OldInvoiceHeaderTempView.RemAmount, getString(R.string.rem_amount)).calcTotal().setSortable());
                columns.add(bind(entity, OldInvoiceHeaderTempView.Amount, getString(R.string.amount)).calcTotal().setSortable());
            }
        };
        adapter.create(oldInvoiceHeaderTempViewModels, savedInstanceState);
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.setOnItemSelectListener(new ReportAdapter.OnItemSelectListener<OldInvoiceHeaderTempViewModel>() {
            @Override
            public void onItemSelected(int idx) {
            }

            @Override
            public void onItemDeSelected(int idx) {
            }
        });
        invoicesReportView.setAdapter(adapter);
        for (OldInvoiceHeaderTempViewModel oldInvoiceHeaderTempViewModel :
                oldInvoiceHeaderTempViewModels) {
            if (oldInvoiceHeaderTempViewModel.HasPayment)
                adapter.select(oldInvoiceHeaderTempViewModel);
        }
    }

    private void loadOldInvoices() {
        OldInvoiceHeaderTempViewManager oldInvoiceHeaderTempViewManager = new OldInvoiceHeaderTempViewManager(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel OpenInvoiceBase = sysConfigManager.read(ConfigKey.OpenInvoicesBasedOn, SysConfigManager.cloud);
        oldInvoiceHeaderTempViewModels = new ArrayList<>();
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && SysConfigManager.compare(OpenInvoiceBase, CustomerOpenInvoicesViewManager.OpenInvoiceBaseOnType.BaseOnDealer)) {
            UserModel userModel = UserManager.readFromFile(getContext());
            oldInvoiceHeaderTempViewModels = oldInvoiceHeaderTempViewManager.getNotSettledInvoices(customerId, userModel.UniqueId);
        } else {
            oldInvoiceHeaderTempViewModels = oldInvoiceHeaderTempViewManager.getNotSettledInvoices(customerId);
        }
    }

    private void refreshForm() {
        List<Integer> SelIds = new ArrayList<>();
        for (OldInvoiceHeaderTempViewModel oldInvoiceHeaderTempViewModel : adapter.getSelectedItems()) {
            SelIds.add(oldInvoiceHeaderTempViewModel.InvoiceRef);
        }
        if (SelIds.size() == 0) {
            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
            cuteMessageDialog.setIcon(Icon.Error);
            cuteMessageDialog.setMessage(getString(R.string.not_selected_any_invoices));
            cuteMessageDialog.setNeutralButton(R.string.ok, null);
            cuteMessageDialog.show();
        } else {
            CalcPromotion.calcPromotionV3WithDialog(SelIds, null, getActivity(), callOrderId, customerId, EVCType.TOSELL, false, false, true, new PromotionCallback() {
                @Override
                public void onSuccess(CustomerCallOrderPromotion data) {
                    try {
                        if (cardView.getVisibility() == View.GONE)
                            cardView.setVisibility(View.VISIBLE);
                        ValueAnimator animator = new ValueAnimator();
                        animator.setObjectValues(0, usanceDay);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                textView.setText(" مهلت پرداخت " + ": " + animation.getAnimatedValue() + " روز");
                            }
                        });
                        animator.setDuration(4000); // here you set the duration of the anim
                        animator.start();
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

                @Override
                public void onFailure(String error) {
                    CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                    cuteMessageDialog.setIcon(Icon.Error);
                    cuteMessageDialog.setMessage(error);
                    cuteMessageDialog.setNeutralButton(R.string.ok, null);
                    cuteMessageDialog.show();
                }

                @Override
                public void onProcess(String msg) {

                }
            });
        }
    }

    @Override
    public void ok() {
        refreshForm();
    }

    @Override
    public void cancel() {

    }

    public void setCustomerAndOrderId(@NonNull UUID customerId, @NonNull UUID callOrderId) {
        Bundle bundle = new Bundle();
        bundle.putString("76921f49-0ce2-4540-8f58-fbe3a17c482a", customerId.toString());
        bundle.putString("9390f3b1-6645-4f24-8a82-6684a08902a1", callOrderId.toString());
        setArguments(bundle);
    }
}
