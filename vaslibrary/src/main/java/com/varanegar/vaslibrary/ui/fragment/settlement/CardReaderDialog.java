package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.framework.validation.annotations.validvalue.Compare;
import com.varanegar.framework.validation.annotations.validvalue.CompareValueChecker;
import com.varanegar.framework.validation.annotations.validvalue.Operator;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.DuplicatePaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.PaymentNotFoundException;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CardPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.settlement.invoiceinfo.InvoicePaymentInfoLayout;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/5/2017.
 */

public class CardReaderDialog extends PaymentDialog {

    @NotEmpty
    public PairedItemsEditable refNumberPairedItemsEditable;
    @NotEmpty
    @Compare(value = "0", operator = Operator.MoreThan)
    public PairedItemsEditable amountPairedItemsEditable;
    public PairedItems datePairedItems;
    private Date date;
    private InvoicePaymentInfoLayout invoicePaymentInfoLayout;
    private View cardReaderLayout;
    private ImageView cancelCardReaderImageView;
    private ImageView addCardReaderImageView;
    private boolean preset;
    private DeviceCardReader deviceCardReader;
    private List<CustomerCallOrderModel> customerCallOrderModels;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        if (getCustomerRemainAmount() != null)
            setTitle(getString(R.string.total_remained) + VasHelperMethods.currencyToString(getRemainedAmount()) + "  -  "
                    + getString(R.string.old_remain_amount) + getCustomerRemainAmount());
        else
            setTitle(getString(R.string.total_remained) + VasHelperMethods.currencyToString(getRemainedAmount()));
        View view = inflater.inflate(R.layout.card_reader_dialog, viewGroup, true);
        SysConfigModel settlementAllocation = new SysConfigManager(getContext())
                .read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
        invoicePaymentInfoLayout = view.findViewById(R.id.invoices_info_layout);
        int count = invoicePaymentInfoLayout.setArguments(getVaranegarActvity(),
                getCustomerId(), PaymentType.Card, getPaymentId());

        PaymentManager paymentManager = new PaymentManager(getContext());
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(getCustomerId());
        Currency returnVa = paymentManager.calcCashAndCheckValidAmountreturn(getCustomerId());
        Currency total = customerCallOrderModels.get(0).TotalAmountNutImmediate.subtract(returnVa);

        if (count <= 1 || !SysConfigManager.compare(settlementAllocation, true))
            invoicePaymentInfoLayout.setVisibility(View.GONE);
        refNumberPairedItemsEditable = (PairedItemsEditable)
                view.findViewById(R.id.ref_number_paired_items_editable);
        amountPairedItemsEditable = (PairedItemsEditable)
                view.findViewById(R.id.amount_paired_items_editable);

        datePairedItems = (PairedItems) view.findViewById(R.id.date_paired_items);

        if (getPaymentId() != null) {

            PaymentModel paymentModel = paymentManager.getPaymentById(getPaymentId(), PaymentType.Card);
            datePairedItems.setValue(DateHelper.toString(paymentModel.ChqDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext())));
            amountPairedItemsEditable.setValue(HelperMethods.currencyToString(paymentModel.Amount));
            refNumberPairedItemsEditable.setValue(paymentModel.Ref);
            date = paymentModel.ChqDate;
        } else {
            date = new Date();
            datePairedItems.setValue(DateHelper.toString(date, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext())));
            if (preset) {
                if (new SysConfigManager(getContext()).readOwnerKeys().DataOwnerKey.equalsIgnoreCase("caf5a390-cbe1-435b-bdde-1f682e004693"))
                    amountPairedItemsEditable.setValue(getCustomerRemainAmount() != null ? VasHelperMethods.currencyToString(getRemainedAmount().add(getCustomerRemainAmount())) : VasHelperMethods.currencyToString(getRemainedAmount()));
                else
                    amountPairedItemsEditable.setValue(VasHelperMethods.currencyToString(total));
                refreshInvoicePaymentInfo();
            }
        }

        refNumberPairedItemsEditable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshInvoicePaymentInfo();
            }
        });
        amountPairedItemsEditable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshInvoicePaymentInfo();
            }
        });

        if (deviceHasCardReader() && getPaymentId() == null) {
            cardReaderLayout = view.findViewById(R.id.card_reader_layout);
            cancelCardReaderImageView = view.findViewById(R.id.cancel_card_reader_image_view);
            addCardReaderImageView = view.findViewById(R.id.add_card_reader_image_view);
            cardReaderLayout.setVisibility(View.VISIBLE);
            cardReaderLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String amountStr = amountPairedItemsEditable.getValue();
                    if (amountStr == null || amountStr.isEmpty()) {
                        showErrorMessage(R.string.amount_is_not_valid);
                        return;
                    }
                    try {
                        Currency amount = Currency.parse(amountPairedItemsEditable.getValue());
//                        Currency refNumber = Currency.parse(refNumberPairedItemsEditable.getValue());
                        if (becomesGreaterThanTotal(amount)) {
                            showErrorMessage(R.string.paid_amount_can_not_be_greater_than_total_amount);
                            return;
                        }
                        runCardReader(amount, null, new ICardReaderResult() {
                            @Override
                            public void onSuccess(TransactionData td) {
                                refNumberPairedItemsEditable.setValue(td.TransactionNo);
                                String id = retrieveTransactionLocalId();
                                if (id.equals(td.Id)) {
                                    setTransactionData(td);
                                    ok();
                                }
                            }

                            @Override
                            public void onFailure(@Nullable TransactionData td, String error) {
                                showErrorMessage(error);
                            }
                        });
                    } catch (ParseException e) {
                        showErrorMessage(R.string.amount_is_not_valid);
                    }
                }
            });
            cancelCardReaderImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Warning);
                    dialog.setTitle(R.string.warning);
                    dialog.setMessage(R.string.are_you_sure);
                    dialog.setNegativeButton(R.string.cancel, null);
                    dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeTransactionData();
                            amountPairedItemsEditable.setValue(null);
                            datePairedItems.setValue(null);
                            refNumberPairedItemsEditable.setValue(null);
                            amountPairedItemsEditable.setEnabled(true);
                            refNumberPairedItemsEditable.setEnabled(true);
                            cancelCardReaderImageView.setVisibility(View.GONE);
                            addCardReaderImageView.setVisibility(View.VISIBLE);
                            refreshInvoicePaymentInfo();
                        }
                    });
                    dialog.show();

                }
            });
            TransactionData td = getTransactionData();
            if (td != null)
                setTransactionData(td);
        }
    }

    private TransactionData getTransactionData() {
        String json = getContext().getSharedPreferences("CARD_READER_DIALOG", Context.MODE_PRIVATE).getString("TR_DATA", null);
        if (json == null)
            return null;
        try {
            return VaranegarGsonBuilder.build().create().fromJson(json, TransactionData.class);
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    private void setTransactionData(TransactionData td) {
        String json = VaranegarGsonBuilder.build().create().toJson(td);
        getContext().getSharedPreferences("CARD_READER_DIALOG", Context.MODE_PRIVATE).edit()
                .putString("TR_DATA", json).apply();
        amountPairedItemsEditable.setValue(HelperMethods.currencyToString(td.PaidAmount));
        datePairedItems.setValue(DateHelper.toString(td.PaymentTime, DateFormat.Complete,
                VasHelperMethods.getSysConfigLocale(getContext())));
        date = td.PaymentTime;
        refNumberPairedItemsEditable.setValue(td.TransactionNo);
        amountPairedItemsEditable.setEnabled(false);
        refNumberPairedItemsEditable.setEnabled(false);
        cancelCardReaderImageView.setVisibility(View.VISIBLE);
        addCardReaderImageView.setVisibility(View.GONE);
        refreshInvoicePaymentInfo();
    }

    private void removeTransactionData() {
        getContext().getSharedPreferences("CARD_READER_DIALOG", Context.MODE_PRIVATE).edit().remove("TR_DATA").apply();
    }

    private void runCardReader(Currency amount, Currency refNumber, @NonNull ICardReaderResult result) {
        String id = generateLocalTransactionId();
        TransactionData td = TransactionData.create(String.valueOf(amount), id, null, null);
        deviceCardReader = DeviceCardReader.getDeviceCardReader(getVaranegarActvity());

        if (deviceCardReader == null) {
            result.onFailure(null, getContext().getString(R.string.card_reader_is_not_available));
            return;
        }
        deviceCardReader.runTransaction(td, result);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (deviceCardReader != null)
            deviceCardReader.dispose();
    }

    private String generateLocalTransactionId() {
        String id = UUID.randomUUID().toString();
        getContext().getSharedPreferences("CARD_READER_DIALOG", Context.MODE_PRIVATE).edit().putString("TR_ID", id).apply();
        return id;
    }

    private String retrieveTransactionLocalId() {
        return getContext().getSharedPreferences("CARD_READER_DIALOG", Context.MODE_PRIVATE).getString("TR_ID", null);
    }

    private boolean deviceHasCardReader() {
        DeviceCardReader cardReader = DeviceCardReader.getDeviceCardReader(getVaranegarActvity());

        return cardReader != null;
    }

    private void refreshInvoicePaymentInfo() {
        String refNumber = refNumberPairedItemsEditable.getValue();
        String amountStr = amountPairedItemsEditable.getValue();
        if (refNumber != null && !refNumber.isEmpty() && amountStr != null && !amountStr.isEmpty()) {
            try {
                Currency amount = Currency.parse(amountPairedItemsEditable.getValue());
                invoicePaymentInfoLayout.setSettlementAmount(amount);
            } catch (Exception e) {
                invoicePaymentInfoLayout.setSettlementAmount(Currency.ZERO);
            }

        } else
            invoicePaymentInfoLayout.setSettlementAmount(Currency.ZERO);
    }

    @Override
    public void ok() {
        validator.validate(CardReaderDialog.this);
    }

    @Override
    public void cancel() {
        dismiss();
    }

    @Override
    public void onValidationSucceeded() {
        if (date == null)
            date = new Date();
        try {
            savePayment();
            removeTransactionData();
        } catch (ParseException e) {
            showErrorMessage(R.string.amount_is_not_valid);
        } catch (DuplicatePaymentException e) {
            showErrorMessage(R.string.the_payment_is_already_saved);
        } catch (PaymentNotFoundException e) {
            showErrorMessage(R.string.payment_is_not_saved);
        }

    }

    private void savePayment() throws ParseException, DuplicatePaymentException, PaymentNotFoundException {
        PaymentManager paymentManager = new PaymentManager(getContext());
        CardPayment payment = new CardPayment();
        payment.Amount = Currency.parse(amountPairedItemsEditable.getValue());
        payment.Date = new Date();
        payment.Ref = refNumberPairedItemsEditable.getValue();
        if (becomesGreaterThanTotal(payment.Amount))
            showErrorMessage(R.string.paid_amount_can_not_be_greater_than_total_amount);
        else {
            if (getPaymentId() == null) {
                try {
                    paymentManager.saveCardPayment(payment, getCustomerId(), invoicePaymentInfoLayout.getItems());
                    runCallBack();
                    if (Build.MODEL.equals("A930"))
                        dismissAllowingStateLoss();
                    else
                        dismiss();
                } catch (IllegalStateException e) {
                    Timber.e(e);
                } catch (Exception ex) {
                    showErrorMessage(R.string.error_saving_request);
                }
            } else {
                try {
                    paymentManager.updateCardPayment(getPaymentId(), payment, getCustomerId(), invoicePaymentInfoLayout.getItems());
                    runCallBack();
                    if (Build.MODEL.equals("A930"))
                        dismissAllowingStateLoss();
                    else
                        dismiss();
                } catch (IllegalStateException e) {
                    Timber.e(e);
                } catch (Exception ex) {
                    showErrorMessage(R.string.error_saving_request);
                }
            }
        }
    }

    private boolean becomesGreaterThanTotal(Currency amount) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            PaymentManager paymentManager = new PaymentManager(getContext());
            Currency total = paymentManager.getTotalPaid(getCustomerId());
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowSurplusInvoiceSettlement, SysConfigManager.cloud);
            if (SysConfigManager.compare(sysConfigModel, false) && !backOfficeType.equals(BackOfficeType.ThirdParty)) {
                if (getPaymentId() == null) {
                    total = total.add(amount);
                    return total.compareTo(getTotalAmount()) == 1;
                } else {
                    PaymentModel paymentModel = paymentManager.getPaymentById(getPaymentId(), PaymentType.Card);
                    total = total.subtract(paymentModel.Amount).add(amount);
                    return total.compareTo(getTotalAmount()) == 1;
                }
            } else return false;
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            return false;
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error :
                errors) {
            String errorMessage = getString(R.string.error);
            if (error.getViolation().equals(NotEmptyChecker.class))
                errorMessage = getString(R.string.not_empty);
            if (error.getViolation().equals(CompareValueChecker.class))
                errorMessage = getString(R.string.amount_is_not_valid);

            if (error.getField() instanceof PairedItemsEditable) {
                ((PairedItemsEditable) error.getField()).setError(errorMessage);
                ((PairedItemsEditable) error.getField()).requestFocus();
            } else {
                showErrorMessage(errorMessage);
            }
        }
    }

    public void preSetRemainedAmount() {
        this.preset = true;
    }
}
