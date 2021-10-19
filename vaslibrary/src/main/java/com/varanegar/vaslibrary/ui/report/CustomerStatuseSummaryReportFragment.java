package com.varanegar.vaslibrary.ui.report;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.RequestReportViewManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import com.varanegar.java.util.Currency;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 05/13/2017.
 */

public class CustomerStatuseSummaryReportFragment extends VaranegarFragment {
    UUID customerUniqueId;

    PairedItems customerRemain, customerFirstCredit, customerFirtDebit, customrOpenInvoicesQty, customrOpenInvoicesAmount, customrOpenChequeQty, customrOpenChequeAmount,
            customrReturnedChequeQty, customrReturnedChequeAmount, customrReturnedRemainCredit, customrReturnedRemainDebit, customrOrderSumAmount,
            customrVisitStatuse;// customrPaymentType;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerUniqueId = UUID.fromString(getArguments().getString("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2"));
        final CustomerManager manager = new CustomerManager(getContext());
        final CustomerModel customer = manager.getItem(customerUniqueId);
        View view = inflater.inflate(R.layout.fragment_statuse_summary, container, false);
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
        // TODO Add Missing fields

        customerRemain = (PairedItems) view.findViewById(R.id.customr_remain);
        customerFirstCredit = (PairedItems) view.findViewById(R.id.customr_first_credit);
        customerFirtDebit = (PairedItems) view.findViewById(R.id.customr_firt_debit);
        customrOpenInvoicesQty = (PairedItems) view.findViewById(R.id.customr_open_invoices_qty);
        customrOpenInvoicesAmount = (PairedItems) view.findViewById(R.id.customr_open_invoices_amount);
        customrOpenChequeQty = (PairedItems) view.findViewById(R.id.customr_open_cheque_qty);
        customrOpenChequeAmount = (PairedItems) view.findViewById(R.id.customr_open_cheque_amount);
        customrReturnedChequeQty = (PairedItems) view.findViewById(R.id.customr_returned_cheque_qty);
        customrReturnedChequeAmount = (PairedItems) view.findViewById(R.id.customr_returned_cheque_amount);
        customrReturnedRemainCredit = (PairedItems) view.findViewById(R.id.customr_returned_remain_credit);
        customrReturnedRemainDebit = (PairedItems) view.findViewById(R.id.customr_returned_remain_debit);
        customrOrderSumAmount = (PairedItems) view.findViewById(R.id.customr_order_sum_amount);
        customrVisitStatuse = (PairedItems) view.findViewById(R.id.customr_visit_statuse);
        FloatingActionButton updateCustomerCredit = (FloatingActionButton) view.findViewById(R.id.update_customer_credit);
        updateCustomerCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Connectivity.isConnected(getActivity())) {
                    ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                    connectionSettingDialog.show(getActivity().getSupportFragmentManager(), "ConnectionSettingDialog");
                    return;
                }
                PingApi pingApi = new PingApi();
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.updateing_customer_credit));
                progressDialog.show();
                pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
                    @Override
                    public void done(String ipAddress) {
                        CustomerManager customerManager = new CustomerManager(getContext());
                        customerManager.sync(customerUniqueId, new UpdateCall() {
                            @Override
                            protected void onFinish() {

                            }

                            @Override
                            protected void onSuccess() {
                                BackOfficeType backOfficeType = null;
                                try {
                                    backOfficeType = new SysConfigManager(getContext()).getBackOfficeType();
                                    if (backOfficeType == BackOfficeType.Varanegar) {
                                        if (isResumed() && progressDialog.isShowing())
                                            progressDialog.setMessage(getString(R.string.updateing_customer_credit_per_line));
                                        CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getActivity());
                                        customerRemainPerLineManager.sync(customer.UniqueId, new UpdateCall() {
                                            @Override
                                            protected void onFinish() {
                                                if (isResumed() && progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                            }

                                            @Override
                                            protected void onSuccess() {
                                                if (isResumed()) {
                                                    setCustomerStatus(customerUniqueId);
                                                }
                                            }

                                            @Override
                                            protected void onFailure(String error) {
                                                if (isResumed()) {
                                                    CuteMessageDialog builder = new CuteMessageDialog(getContext());
                                                    builder.setIcon(Icon.Error);
                                                    builder.setTitle(getString(R.string.error));
                                                    builder.setMessage(error);
                                                    builder.setPositiveButton(R.string.ok, null);
                                                    builder.show();
                                                }
                                            }
                                        });
                                    } else {
                                        if (isResumed() && progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        if (isResumed()) {
                                            setCustomerStatus(customerUniqueId);
                                        }
                                    }
                                } catch (UnknownBackOfficeException e) {
                                    Timber.e(e);
                                }
                            }

                            @Override
                            protected void onFailure(String error) {
                                if (isResumed()) {
                                    CuteMessageDialog builder = new CuteMessageDialog(getContext());
                                    builder.setIcon(Icon.Error);
                                    builder.setTitle(getString(R.string.error));
                                    builder.setMessage(error);
                                    builder.setPositiveButton(R.string.ok, null);
                                    builder.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void failed() {
                        if (isResumed()) {
                            CuteMessageDialog builder = new CuteMessageDialog(getContext());
                            builder.setIcon(Icon.Error);
                            builder.setTitle(getString(R.string.error));
                            builder.setMessage(R.string.network_error);
                            builder.setPositiveButton(R.string.ok, null);
                            builder.show();
                        }
                    }
                });
            }
        });
        setCustomerStatus(customer.UniqueId);
        return view;
    }

    private void setCustomerStatus(UUID id) {
        try {
            CustomerManager customerManager = new CustomerManager(getContext());
            CustomerModel customer = customerManager.getItem(id);
            CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getContext());
            CustomerRemainPerLineModel customerRemainPerLineModel = customerRemainPerLineManager.getCustomerRemainPerLine(id);
            if (customerRemainPerLineModel != null) {
                customerRemain.setValue(HelperMethods.currencyToString(customerRemainPerLineModel.CustRemAmount));
                customrOpenInvoicesQty.setValue(String.valueOf(customerRemainPerLineModel.CountOpenFact));
                customrOpenInvoicesAmount.setValue(HelperMethods.currencyToString(customerRemainPerLineModel.AmountOpenFact));
                customrOpenChequeQty.setValue(String.valueOf(customerRemainPerLineModel.CountChq));
                customrOpenChequeAmount.setValue(HelperMethods.currencyToString(customerRemainPerLineModel.AmountChq));
                customrReturnedChequeQty.setValue(String.valueOf(customerRemainPerLineModel.CountRetChq));
                customrReturnedChequeAmount.setValue(HelperMethods.currencyToString(customerRemainPerLineModel.AmountRetChq));
            } else {
                customerRemain.setValue(HelperMethods.currencyToString(customer.CustomerRemain));
                customrOpenInvoicesQty.setValue(String.valueOf(customer.OpenInvoiceCount));
                customrOpenInvoicesAmount.setValue(HelperMethods.currencyToString(customer.OpenInvoiceAmount));
                customrOpenChequeQty.setValue(String.valueOf(customer.OpenChequeCount));
                customrOpenChequeAmount.setValue(HelperMethods.currencyToString(customer.OpenChequeAmount));
                customrReturnedChequeQty.setValue(String.valueOf(customer.ReturnChequeCount));
                customrReturnedChequeAmount.setValue(HelperMethods.currencyToString(customer.ReturnChequeAmount));
            }

            customerFirstCredit.setValue(HelperMethods.currencyToString(customer.InitCredit));
            customerFirtDebit.setValue(HelperMethods.currencyToString(customer.InitDebit));
            customrReturnedRemainCredit.setValue(HelperMethods.currencyToString(customer.RemainCredit));
            customrReturnedRemainDebit.setValue(HelperMethods.currencyToString(customer.RemainDebit));
            CustomerCallManager callManager = new CustomerCallManager(getContext());
            List<CustomerCallModel> calls = callManager.loadCalls(customer.UniqueId);
            customrVisitStatuse.setValue(callManager.getName(calls, VaranegarApplication.is(VaranegarApplication.AppId.Contractor)));

            RequestReportViewManager requestReportViewManager = new RequestReportViewManager(getContext());
            List<RequestReportViewModel> requestReportViewModels = requestReportViewManager.getItems(RequestReportViewManager.getCurrentCustomer(customer.UniqueId));
            if (requestReportViewModels != null) {
                Currency totalOrder = Currency.ZERO;
                for (RequestReportViewModel requestReportViewModel : requestReportViewModels) {
                    totalOrder = totalOrder.add(requestReportViewModel.TotalOrderNetAmount);
                }
                customrOrderSumAmount.setValue(HelperMethods.currencyToString(totalOrder));
            }
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.error));
            builder.setMessage(getString(R.string.some_information_null));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }
    }

}
