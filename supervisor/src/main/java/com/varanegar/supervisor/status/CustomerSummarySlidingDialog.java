package com.varanegar.supervisor.status;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.SlidingDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.model.SupervisorCustomer;
import com.varanegar.supervisor.model.SupervisorCustomerModel;
import com.varanegar.supervisor.model.SupervisorCustomerModelRepository;
import com.varanegar.supervisor.webapi.CustomerSummaryViewModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerSummarySlidingDialog extends SlidingDialog {
    private ProgressView progressView;
    private PairedItems remainingPairedItems;
    private PairedItems firstCreditPairedItems;
    private PairedItems openInvoicesQtyPairedItems;
    private PairedItems openInvoicesPairedItems;
    private PairedItems openChequeQtyPairedItems;
    private PairedItems openChequeAmountPairedItems;
    private PairedItems returnedChequeQtyPairedItems;
    private PairedItems returnedChequeAmountPairedItems;
    private PairedItems remainCreditPairedItems;
    private PairedItems remainDebitPairedItems;


    @Override
    public void onStart() {
        super.onStart();

        UUID customerId = UUID.fromString(getArguments().getString("CUSTOMER_ID"));
        SupervisorCustomerModel customerModel = new SupervisorCustomerModelRepository().getItem(new Query().from(SupervisorCustomer.SupervisorCustomerTbl).whereAnd(Criteria.equals(SupervisorCustomer.UniqueId, customerId)));
        if (customerModel == null)
            return;

        if (progressView != null) {
            progressView.setMessage(R.string.downloading_customer_data);
            progressView.start();
        }

        SupervisorApi api = new SupervisorApi(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.DeviceSettingNo, SysConfigManager.cloud);
        api.runWebRequest(api.getCustomerFinanceData(customerModel.UniqueId, customerModel.DealerId, VaranegarApplication.getInstance().getAppId(), SysConfigManager.getIntValue(sysConfigModel, 0))
                , new WebCallBack<List<CustomerSummaryViewModel>>() {
                    @Override
                    protected void onFinish() {
                        if (progressView != null)
                            progressView.finish();
                    }

                    @Override
                    protected void onSuccess(List<CustomerSummaryViewModel> result, Request request) {
                        if (result != null && result.size() == 1) {
                            CustomerSummaryViewModel viewModel = result.get(0);
                            remainingPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.CustomerRemain));
                            firstCreditPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.InitCredit));
                            openInvoicesQtyPairedItems.setValue(String.valueOf(viewModel.OpenInvoicesCount));
                            openInvoicesPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.OpenInvoicesAmount));
                            openChequeQtyPairedItems.setValue(String.valueOf(viewModel.OpenChequeCount));
                            openChequeAmountPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.OpenChequeAmount));
                            returnedChequeQtyPairedItems.setValue(String.valueOf(viewModel.ReturnChequeCount));
                            returnedChequeAmountPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.ReturnChequeAmount));
                            remainCreditPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.RemainCredit));
                            remainDebitPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.RemainDebit));
                        } else
                            showErrorDialog(getContext().getString(R.string.downloading_data_failed));
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Timber.e(t);
                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_customer_summary_dialog_layout, container, false);
        progressView = view.findViewById(R.id.progress_view);
        remainingPairedItems = view.findViewById(R.id.customer_remaining_paired_items);
        firstCreditPairedItems = view.findViewById(R.id.customer_first_credit_paired_items);
        openInvoicesQtyPairedItems = view.findViewById(R.id.customer_open_invoices_qty_paired_items);
        openInvoicesPairedItems = view.findViewById(R.id.customer_open_invoices_paired_items);
        openChequeQtyPairedItems = view.findViewById(R.id.customer_open_cheque_qty_paired_items);
        openChequeAmountPairedItems = view.findViewById(R.id.customer_open_cheque_amount_paired_items);
        returnedChequeQtyPairedItems = view.findViewById(R.id.customer_returned_cheque_qty_paired_items);
        returnedChequeAmountPairedItems = view.findViewById(R.id.customer_returned_cheque_amount_paired_items);
        remainCreditPairedItems = view.findViewById(R.id.customer_remain_credit_paired_items);
        remainDebitPairedItems = view.findViewById(R.id.customer_remain_debit_paired_items);
        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        return view;
    }

    private void showErrorDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(err);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
}
