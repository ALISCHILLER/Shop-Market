package com.varanegar.vaslibrary.ui.fragment.settlement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.FormValidator;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.annotations.LengthChecker;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.framework.validation.annotations.validvalue.CompareValueChecker;
import com.varanegar.framework.validation.annotations.validvalue.Operator;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.enums.ThirdPartyPaymentTypes;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.ValidPayTypeManager;
import com.varanegar.vaslibrary.manager.bank.BankManager;
import com.varanegar.vaslibrary.manager.city.CityManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.DuplicatePaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.PaymentNotFoundException;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.CheckPayment;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.bank.BankModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.city.CityModel;
import com.varanegar.vaslibrary.model.payment.PaymentModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.settlement.invoiceinfo.InvoicePaymentInfoLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 12/4/2017.
 */

public class CheckDialog extends PaymentDialog {

    public PairedItemsSpinner<CityModel> citySpinner;
    public PairedItemsSpinner<BankModel> banksSpinner;
    public PairedItemsEditable accountNumberPairedItems;
    public PairedItemsEditable checkNumberPairedItems;
    public PairedItemsEditable sayadNumberPairedItems;
    public PairedItemsEditable amountPairedItems;
    private PairedItems checkDatePairedItems;
    private Date checkDate;
    private InvoicePaymentInfoLayout invoicePaymentInfoLayout;
    private FormValidator validator;

    public PairedItemsEditable branchNumberPairedItemsEditable;
    public PairedItemsEditable branchNamePairedItemsEditable;
    private PairedItemsEditable accountNamePairedItemsEditable;
    private List<CustomerCallOrderModel> customerCallOrderModels;
    SayadNumberCheckerViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSizingPolicy(SizingPolicy.Maximum);
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_dialog_fragment, viewGroup, true);

        PaymentManager paymentManager = new PaymentManager(getContext());
        Currency returnVa = paymentManager.calcCashAndCheckValidAmountreturn(getCustomerId());
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(getCustomerId());
        List<PaymentTypeOrderModel> paymentTypeOrderModels = new ValidPayTypeManager(getContext())
                .getPaymentTypeOrders(customerCallOrderModels, null);
        PaymentTypeOrderModel paymentTypeOrderModel = paymentTypeOrderModels.get(0);

        Currency total = Currency.ZERO;
        if (customerCallOrderModels.size() > 1) {
            for (CustomerCallOrderModel item :
                    customerCallOrderModels) {
                if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCA.toString()))
                    if (item.TotalAmountNutCash!=null)
                    total = item.TotalAmountNutCash.add(total).subtract(returnVa);
                else {
                    if (item.TotalAmountNutCheque!=null)
                        total = item.TotalAmountNutCheque.add(total).subtract(returnVa);
                    }
            }
        } else {
            if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCA.toString()))
                total = customerCallOrderModels.get(0).TotalAmountNutCash.subtract(returnVa);
            else
                total = customerCallOrderModels.get(0).TotalAmountNutCheque.subtract(returnVa);


        }


        // Currency total = customerCallOrderModels.get(0).TotalAmountNutCash.subtract(returnVa);
        if (total.compareTo(total) >= 0)
            setTitle(getString(R.string.insert_check_info) + " (" + getString(R.string.total_remained) + VasHelperMethods.currencyToString(total) + ")");
        else
            setTitle(getString(R.string.insert_check_info) + " (" + getString(R.string.total_remained) + VasHelperMethods.currencyToString(getRemainedAmount()) + ")");

        validator = new FormValidator(getActivity());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        BackOfficeType backOfficeType = null;
        try {
            backOfficeType = sysConfigManager.getBackOfficeType();
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }

        citySpinner = view.findViewById(R.id.city_spinner);
        banksSpinner = view.findViewById(R.id.bank_spinner);

        accountNumberPairedItems = view.findViewById(R.id.account_number_paired_items_editable);
        validator.addField(accountNumberPairedItems, getString(R.string.check_account_number), new LengthChecker(2, 24, true));

        checkNumberPairedItems = view.findViewById(R.id.check_number_paired_items_editable);
        validator.addField(checkNumberPairedItems, getString(R.string.check_serial_number), new LengthChecker(2, 20, true));

        sayadNumberPairedItems = view.findViewById(R.id.sayad_number_paired_items_editable);
        validator.addField(sayadNumberPairedItems, getString(R.string.sayad_number), new LengthChecker(0, 16, false));

        amountPairedItems = view.findViewById(R.id.amount_paired_items_editable);
        validator.addField(amountPairedItems, getString(R.string.amount), new CompareValueChecker(Operator.MoreThan, "0"));

        invoicePaymentInfoLayout = view.findViewById(R.id.invoices_info_layout);
        SysConfigModel settlementAllocation = new SysConfigManager(getContext()).read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
        int count = invoicePaymentInfoLayout.setArguments(getVaranegarActvity(), getCustomerId(), PaymentType.Check, getPaymentId());
        if (count <= 1 || !SysConfigManager.compare(settlementAllocation, true))
            invoicePaymentInfoLayout.setVisibility(View.GONE);

        CityManager cityManager = new CityManager(getContext());
        List<CityModel> cities = cityManager.getAllCities();
        if (cities.size() > 0) citySpinner.setEnabled(true);
        citySpinner.setup(getFragmentManager(), cities, new SearchBox.SearchMethod<CityModel>() {
            @Override
            public boolean onSearch(CityModel item, String text) {
                String str = HelperMethods.persian2Arabic(text);
                if (str == null)
                    return true;
                str = str.toLowerCase();
                return item.toString().toLowerCase().contains(str);
            }
        });
        citySpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<CityModel>() {
            @Override
            public void onItemSelected(int position, CityModel item) {
                refreshInvoicePaymentInfoLayout();
            }
        });

        banksSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<BankModel>() {
            @Override
            public void onItemSelected(int position, BankModel item) {
                refreshInvoicePaymentInfoLayout();
            }
        });
        BankManager bankManager = new BankManager(getContext());
        List<BankModel> banks = bankManager.getAllBanks();
        if (banks.size() > 0) banksSpinner.setEnabled(true);
        banksSpinner.setup(getFragmentManager(), banks, new SearchBox.SearchMethod<BankModel>() {
            @Override
            public boolean onSearch(BankModel item, String text) {
                String str = HelperMethods.persian2Arabic(text);
                if (str == null)
                    return true;
                str = str.toLowerCase();
                return item.toString().toLowerCase().contains(str);
            }
        });

        branchNumberPairedItemsEditable = view.findViewById(R.id.branch_number_paired_items_editable);
        branchNamePairedItemsEditable = view.findViewById(R.id.branch_name_paired_items_editable);
        accountNamePairedItemsEditable = view.findViewById(R.id.account_name_paired_items_editable);

        accountNumberPairedItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshInvoicePaymentInfoLayout();
            }
        });
        checkNumberPairedItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshInvoicePaymentInfoLayout();
            }
        });
        amountPairedItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshInvoicePaymentInfoLayout();
            }
        });
        checkDatePairedItems = view.findViewById(R.id.check_date_paired_items);
        view.findViewById(R.id.date_picker_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateHelper.showDatePicker(getVaranegarActvity(), VasHelperMethods.getSysConfigLocale(getContext()), new DateHelper.OnDateSelected() {
                    @Override
                    public void run(Calendar calendar) {
                        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                        try {
                            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Varanegar)) {
                                CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
                                PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(getContext());
                                int maxPaymentDeadLine = 0;
                                List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(getCustomerId());
                                for (CustomerCallOrderModel customerCallOrderModel : customerCallOrderModels) {
                                    PaymentTypeOrderModel paymentTypeOrder = paymentOrderTypeManager.getPaymentType(customerCallOrderModel.OrderPaymentTypeUniqueId);
                                    maxPaymentDeadLine = Math.max(paymentTypeOrder.PaymentTime, maxPaymentDeadLine);
                                }
                                Calendar deadLineTime = Calendar.getInstance();
                                deadLineTime.set(Calendar.HOUR_OF_DAY, 23);
                                deadLineTime.set(Calendar.MINUTE, 59);
                                deadLineTime.set(Calendar.SECOND, 59);
                                deadLineTime.set(Calendar.MILLISECOND, 999);
                                deadLineTime.add(Calendar.DAY_OF_YEAR, maxPaymentDeadLine);
                                if (calendar.getTime().after(deadLineTime.getTime()) && maxPaymentDeadLine != 0) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                                    dialog.setMessage(getResources().getString(R.string.max_date_for_cheque) + " " + DateHelper.toString(deadLineTime.getTime(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext())) + " " + getResources().getString(R.string.is));
                                    dialog.setTitle(R.string.error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                } else {
                                    checkDate = calendar.getTime();
                                    checkDatePairedItems.setValue(DateHelper.toString(calendar, DateFormat.Date));
                                    refreshInvoicePaymentInfoLayout();
                                }
                            } else {
                                checkDate = calendar.getTime();
                                checkDatePairedItems.setValue(DateHelper.toString(calendar, DateFormat.Date));
                                refreshInvoicePaymentInfoLayout();
                            }
                        } catch (UnknownBackOfficeException e) {
                            e.printStackTrace();
                            Timber.e(e);
                            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                            dialog.setTitle(R.string.error);
                            dialog.setIcon(Icon.Error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.setMessage(R.string.back_office_type_is_uknown);
                            dialog.show();
                        }
                    }
                });
            }
        });

        if (getPaymentId() != null) {
            final PaymentModel paymentModel = paymentManager.getItem(getPaymentId());
            if (paymentModel != null) {
                int cp = Linq.findFirstIndex(cities, new Linq.Criteria<CityModel>() {
                    @Override
                    public boolean run(CityModel item) {
                        return item.UniqueId.equals(paymentModel.CityId);
                    }
                });
                if (cp >= 0)
                    citySpinner.selectItem(cp);
                int bp = Linq.findFirstIndex(banks, new Linq.Criteria<BankModel>() {
                    @Override
                    public boolean run(BankModel item) {
                        return item.UniqueId.equals(paymentModel.BankId);
                    }
                });
                if (bp >= 0)
                    banksSpinner.selectItem(bp);
                accountNumberPairedItems.setValue(paymentModel.CheckAccountNumber);
                checkNumberPairedItems.setValue(paymentModel.CheckNumber);
                sayadNumberPairedItems.setValue(paymentModel.SayadNumber);
                amountPairedItems.setValue(HelperMethods.currencyToString(paymentModel.Amount));
                checkDate = paymentModel.ChqDate;
                checkDatePairedItems.setValue(DateHelper.toString(paymentModel.ChqDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext())));
                branchNumberPairedItemsEditable.setValue(paymentModel.ChqBranchCode);
                branchNamePairedItemsEditable.setValue(paymentModel.ChqBranchName);
                accountNamePairedItemsEditable.setValue(paymentModel.ChqAccountName);
            }
        }
        ImageView checkSayadNumber = view.findViewById(R.id.sayad_number_checked_button);
        if (backOfficeType == BackOfficeType.ThirdParty) {
            viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(SayadNumberCheckerViewModel.class);
            validator.addField(citySpinner, getString(R.string.city_name), new NotEmptyChecker());
            validator.addField(banksSpinner, getString(R.string.bank_name), new NotEmptyChecker());
            checkSayadNumber.setVisibility(View.VISIBLE);
            checkSayadNumber.setOnClickListener(view1 -> {
                if (sayadNumberPairedItems != null) {
                    if (sayadNumberPairedItems.getValue() != null && !sayadNumberPairedItems.getValue().isEmpty()) {
                        SayadNumberCheckerFragment fragment = new SayadNumberCheckerFragment();
                        fragment.setArguments(sayadNumberPairedItems.getValue());
                        fragment.show(requireFragmentManager(), "SayadNumberCheckerFragment");
                    } else
                        sayadNumberPairedItems.setError(getString(R.string.enter_sayad_number));
                }
            });
            viewModel.getSayadNumberCheckerLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
                if (aBoolean == null) {
                    checkSayadNumber.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_black_24dp));
                } else if (aBoolean) {
                    checkSayadNumber.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
                } else {
                    checkSayadNumber.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_black_24dp));
                }
            });
            sayadNumberPairedItems.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    viewModel.setSayadNumberCheckerLiveData(null);
                }
            });
        } else {
            citySpinner.setVisibility(View.GONE);
            checkSayadNumber.setVisibility(View.GONE);
        }
    }

    void refreshInvoicePaymentInfoLayout() {
        CityModel cityModel = citySpinner.getSelectedItem();
        BankModel bankModel = banksSpinner.getSelectedItem();
        String accountNumber = accountNumberPairedItems.getValue();
        String checkNumber = checkNumberPairedItems.getValue();
        String amount = amountPairedItems.getValue();
        if (cityModel != null && bankModel != null &&
                accountNumber != null && !accountNumber.isEmpty() &&
                checkNumber != null && !checkNumber.isEmpty() &&
                amount != null && !amount.isEmpty() &&
                checkDate != null) {
            try {
                invoicePaymentInfoLayout.setSettlementAmount(Currency.parse(amount));
            } catch (Exception ex) {
                invoicePaymentInfoLayout.setSettlementAmount(Currency.ZERO);
            }
        }
    }

    @Override
    public void ok() {
        validator.validate(CheckDialog.this);
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onValidationSucceeded() {
        if (checkDate == null) {
            showErrorMessage(R.string.please_insert_date);
        } else {
            try {
                saveCheck();
            } catch (ParseException e) {
                showErrorMessage(R.string.amount_is_not_valid);
            } catch (DuplicatePaymentException e) {
                showErrorMessage(R.string.the_check_is_already_saved);
            } catch (PaymentNotFoundException e) {
                showErrorMessage(R.string.payment_is_not_saved);
            }
        }
    }

    private void saveCheck() throws ParseException, DuplicatePaymentException, PaymentNotFoundException {
        PaymentManager paymentManager = new PaymentManager(getContext());
        CheckPayment payment = new CheckPayment();
        String amountString = amountPairedItems.getValue();
        payment.Amount = Currency.parse(amountString);
        if (banksSpinner.getSelectedItem() != null)
            payment.BankId = banksSpinner.getSelectedItem().UniqueId;
        if (citySpinner.getSelectedItem() != null)
            payment.CityId = citySpinner.getSelectedItem().UniqueId;
        payment.CheckAccountNumber = accountNumberPairedItems.getValue();
        payment.CheckNumber = checkNumberPairedItems.getValue();
        payment.SayadNumber = sayadNumberPairedItems.getValue();
        payment.Date = new Date();
        payment.ChqDate = checkDate;
        payment.BranchName = branchNamePairedItemsEditable.getValue();
        payment.BranchCode = branchNumberPairedItemsEditable.getValue();
        payment.AccountName = accountNamePairedItemsEditable.getValue();
        if (checkDuplicateCheck(payment.CheckAccountNumber, payment.CheckNumber))
            showErrorMessage(R.string.this_check_is_repeatit);
        else if (becomesGreaterThanTotal(payment.Amount))
            showErrorMessage(R.string.paid_amount_can_not_be_greater_than_total_amount);
        else {
            if (getPaymentId() == null) {
                try {
                    paymentManager.saveCheckPayment(payment, getCustomerId(), invoicePaymentInfoLayout.getItems());
                    runCallBack();
                    dismiss();
                } catch (Exception ex) {
                    showErrorMessage(R.string.error_saving_request);
                }
            } else {
                try {
                    paymentManager.updateCheckPayment(getPaymentId(), payment, getCustomerId(), invoicePaymentInfoLayout.getItems());
                    runCallBack();
                    dismiss();
                } catch (Exception ex) {
                    showErrorMessage(R.string.error_saving_request);
                }
            }
        }
    }

    private boolean checkDuplicateCheck(final String CheckAccountNumber, final String CheckNumber) {
        final boolean[] hasDuplicate = {false};
        PaymentManager paymentManager = new PaymentManager(getContext());
        List<PaymentModel> checkPaymentModels = paymentManager.listCheckPayments(getCustomerId());
        Linq.forEach(checkPaymentModels, new Linq.Consumer<PaymentModel>() {
            @Override
            public void run(PaymentModel item) {
                if (item.CheckNumber.equals(CheckNumber) && item.CheckAccountNumber.equals(CheckAccountNumber)) {
                    if (getPaymentId() == null || !getPaymentId().equals(item.UniqueId))
                        hasDuplicate[0] = true;
                }
            }
        });
        return hasDuplicate[0];
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
                    PaymentModel paymentModel = paymentManager.getPaymentById(getPaymentId(), PaymentType.Check);
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
        validator.showErrors(errors);
    }
}
