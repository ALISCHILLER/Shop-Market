package com.varanegar.vaslibrary.ui.fragment.settlement.invoiceinfo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ValidPayTypeManager;
import com.varanegar.vaslibrary.manager.invoiceinfo.InvoiceInfoViewManager;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.invoiceinfo.InvoicePaymentInfoViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.AllowableRoundSettlementDigit;
import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.AllowableRoundSettlementDigitDist;
import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.SettlementDiscountPercent;

/**
 * Created by A.Torabi on 7/24/2018.
 */

public class InvoicePaymentInfoLayout extends LinearLayout {

    private BaseRecyclerView invoicesRecyclerView;
    BaseRecyclerAdapter<InvoicePaymentInfoViewModel> adapter;
    private Currency settlementAmount;
    private AppCompatActivity activity;
    private HashMap<UUID, InvoicePaymentInfoViewModel> initList;

    private View view;
    private SysConfigModel percent;
    private SysConfigModel round;
    private HashMap<UUID, Currency> maxAmounts;
    private UUID paymentType;


    public InvoicePaymentInfoLayout(Context context) {
        super(context);
    }

    public InvoicePaymentInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InvoicePaymentInfoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSettlementAmount(Currency amount) {
        settlementAmount = amount;
        Currency paid = Currency.ZERO;
        for (InvoicePaymentInfoViewModel viewModel :
                adapter.getItems()) {
            paid = paid.add(viewModel.PaidAmount);
        }
        for (InvoicePaymentInfoViewModel viewModel :
                adapter.getItems()) {
            if (paid.compareTo(amount) > 0 && !paymentType.equals(PaymentType.Discount)) {
                Currency oldPaidAmount = initList.get(viewModel.InvoiceId).PaidAmount;
                initList.get(viewModel.InvoiceId).PaidAmount = Currency.ZERO;
                initList.get(viewModel.InvoiceId).TotalPaidAmount = initList.get(viewModel.InvoiceId).TotalPaidAmount.subtract(oldPaidAmount);
                initList.get(viewModel.InvoiceId).RemAmount = initList.get(viewModel.InvoiceId).Amount;
                initList.get(viewModel.InvoiceId).TotalRemAmount = initList.get(viewModel.InvoiceId).TotalRemAmount.add(oldPaidAmount);
            }
            viewModel.PaidAmount = Currency.valueOf(initList.get(viewModel.InvoiceId).PaidAmount.doubleValue());
            viewModel.RemAmount = Currency.valueOf(initList.get(viewModel.InvoiceId).RemAmount.doubleValue());
            viewModel.TotalRemAmount = Currency.valueOf(initList.get(viewModel.InvoiceId).TotalRemAmount.doubleValue());
            viewModel.TotalPaidAmount = Currency.valueOf(initList.get(viewModel.InvoiceId).TotalPaidAmount.doubleValue());
            SysConfigModel settlementAllocation = new SysConfigManager(getContext()).read(ConfigKey.SettlementAllocation, SysConfigManager.cloud);
            if (adapter.getItems().size() == 1 && !paymentType.equals(PaymentType.Discount) && SysConfigManager.compare(settlementAllocation, true)) {
                Currency newPaidAmount = (amount.compareTo(viewModel.TotalRemAmount) > 0) ? viewModel.TotalRemAmount : amount;
                Currency oldPaidAmount = viewModel.PaidAmount;
                viewModel.TotalPaidAmount =
                        viewModel.TotalPaidAmount.subtract(oldPaidAmount)
                                .add(newPaidAmount);
                viewModel.TotalRemAmount = viewModel.TotalRemAmount.add(oldPaidAmount)
                        .subtract(newPaidAmount);
                viewModel.PaidAmount = newPaidAmount;
                viewModel.RemAmount = viewModel.Amount.subtract(newPaidAmount);
            }
        }
        adapter.notifyDataSetChanged();
    }

    static double round(double number, double max) {
        int temp = (int) number;
        while (temp > max) {
            int length = String.valueOf(temp).length() - 1;
            int t = (int) Math.pow(10, length);
            temp = temp - t;
        }
        return number - temp;
    }

    static double getRightDigits(double number, int numberOfDigits) {
        double t = Math.pow(10, numberOfDigits);
        double rightDigits = number - Math.floor(number / t) * t;
        return rightDigits;
    }

    public int setArguments(AppCompatActivity activity, @NonNull UUID customerId, @NonNull UUID paymentType, @Nullable UUID paymentId) {
        this.activity = activity;
        this.paymentType = paymentType;
        InvoiceInfoViewManager invoiceInfoViewManager = new InvoiceInfoViewManager(getContext());
        List<InvoicePaymentInfoViewModel> selectableInvoices = new ArrayList<>();
        Currency sumOfMax = Currency.ZERO;
        if (paymentType.equals(PaymentType.Discount)) {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            percent = sysConfigManager.read(SettlementDiscountPercent, SysConfigManager.cloud);
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                round = sysConfigManager.read(AllowableRoundSettlementDigitDist, SysConfigManager.cloud);
            else
                round = sysConfigManager.read(AllowableRoundSettlementDigit, SysConfigManager.cloud);
            maxAmounts = new HashMap<>();

            selectableInvoices = invoiceInfoViewManager.getCallOrders(customerId, paymentId);
            for (InvoicePaymentInfoViewModel viewModel :
                    selectableInvoices) {
                Currency maxAmount = getMaxAmount(viewModel);
                maxAmount = viewModel.TotalRemAmount.compareTo(maxAmount) < 0 ? Currency.valueOf(viewModel.TotalRemAmount.doubleValue()) : maxAmount;
                sumOfMax = sumOfMax.add(maxAmount);
                maxAmounts.put(viewModel.InvoiceId, maxAmount);
            }
        } else {
            List<InvoicePaymentInfoViewModel> invoices;
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                invoices = invoiceInfoViewManager.getOldInvoices(customerId, paymentId);
            else
                invoices = invoiceInfoViewManager.getAll(customerId, paymentId);
            ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
            for (InvoicePaymentInfoViewModel infoViewModel :
                    invoices) {
                Set<UUID> validPayTypes = validPayTypeManager.getValidPayTypes(infoViewModel.OrderPaymentTypeUniqueId);
                if (validPayTypes.contains(paymentType))
                    selectableInvoices.add(infoViewModel);
            }
        }

        adapter = new BaseRecyclerAdapter<InvoicePaymentInfoViewModel>(activity, selectableInvoices) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_info_view_holder, parent, false);
                return new InvoiceInfoViewHolder(itemView, this, getContext());
            }
        };
        initList = new HashMap<>();
        for (InvoicePaymentInfoViewModel info :
                adapter.getItems()) {
            InvoicePaymentInfoViewModel infoViewModel = new InvoicePaymentInfoViewModel();
            infoViewModel.PaidAmount = Currency.valueOf(info.PaidAmount == null ? 0 : info.PaidAmount.doubleValue());
            infoViewModel.UniqueId = info.UniqueId;
            infoViewModel.RemAmount = Currency.valueOf(info.RemAmount == null ? 0 : info.RemAmount.doubleValue());
            infoViewModel.TotalPaidAmount = Currency.valueOf(info.TotalPaidAmount == null ? 0 : info.TotalPaidAmount.doubleValue());
            infoViewModel.Amount = Currency.valueOf(info.Amount == null ? 0 : info.Amount.doubleValue());
            infoViewModel.PaymentId = info.PaymentId;
            infoViewModel.OrderPaymentTypeUniqueId = info.OrderPaymentTypeUniqueId;
            infoViewModel.CustomerId = info.CustomerId;
            infoViewModel.InvoiceDate = info.InvoiceDate;
            infoViewModel.InvoiceId = info.InvoiceId;
            infoViewModel.InvoiceNo = info.InvoiceNo;
            infoViewModel.IsOldInvoice = info.IsOldInvoice;
            infoViewModel.PaymentType = info.PaymentType;
            infoViewModel.TotalRemAmount = Currency.valueOf(info.TotalRemAmount == null ? 0 : info.TotalRemAmount.doubleValue());
            initList.put(info.InvoiceId, infoViewModel);
        }

        invoicesRecyclerView.setAdapter(adapter);

        if (paymentType.equals(PaymentType.Discount))
            setSettlementAmount(sumOfMax);
        return adapter.getItemCount();
    }

    private Currency getMaxAmount(InvoicePaymentInfoViewModel viewModel) {
        Currency currency = viewModel.Amount.multiply(SysConfigManager.getCurrencyValue(percent, Currency.ZERO));
        Currency finalCarrency;
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        try {
            if (percent.Value.equals("-1"))
                finalCarrency = viewModel.Amount;
            else if (currency.compareTo(Currency.ZERO) == 0)
                finalCarrency = SysConfigManager.getCurrencyValue(round, Currency.ZERO);
            else
                finalCarrency = currency.divide(new Currency(100)).add(SysConfigManager.getCurrencyValue(round, Currency.ZERO));
        } catch (Exception e) {
            Timber.d("No back office");
            return Currency.ZERO;
        }
//        if (finalCarrency.compareTo(Currency.ZERO) > 0)
//            return finalCarrency.multiply(Currency.valueOf(-1));
//        else
        return finalCarrency;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        view = inflate(getContext(), R.layout.invoice_info_layout, this);
        invoicesRecyclerView = view.findViewById(R.id.invoices_recycler_view);
    }

    public List<InvoicePaymentInfoViewModel> getItems() {
        return adapter.getItems();
    }

    private class InvoiceInfoViewHolder extends BaseViewHolder<InvoicePaymentInfoViewModel> {
        private final TextView labelTextView;
        private final TextView invoiceNoTextView;
        private final TextView invoiceRemAmountTextView;
        private final TextView settlementAmountTextView;
        private final SeekBar amountSeekBar;
        private final ImageView calculatorImageView;
        private final TextView dateTextView;
        private int max;
        private InvoicePaymentInfoViewModel invoicePaymentInfoViewModel;

        public InvoiceInfoViewHolder(View itemView, BaseRecyclerAdapter<InvoicePaymentInfoViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            labelTextView = itemView.findViewById(R.id.label_text_view);
            invoiceNoTextView = itemView.findViewById(R.id.invoice_no_text_view);
            invoiceRemAmountTextView = itemView.findViewById(R.id.invoice_rem_amount_text_view);
            settlementAmountTextView = itemView.findViewById(R.id.settlement_amount_text_view);
            amountSeekBar = itemView.findViewById(R.id.amount_seek_bar);
            calculatorImageView = itemView.findViewById(R.id.calculator_image_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);


        }

        @Override
        public void bindView(final int position) {
            if (settlementAmount == null)
                settlementAmount = Currency.ZERO;
            invoicePaymentInfoViewModel = recyclerAdapter.get(position);

            if (invoicePaymentInfoViewModel.IsOldInvoice) {
                labelTextView.setText(R.string.open_invoice_no);
                dateTextView.setText(invoicePaymentInfoViewModel.InvoiceDate);
            } else {
                labelTextView.setText(R.string.order_reference_no);
                try {
                    dateTextView.setText(DateHelper.toString(new Date(Long.parseLong(invoicePaymentInfoViewModel.InvoiceDate)), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext())));
                } catch (Exception ignored) {

                }
            }

            if (paymentType.equals(PaymentType.Discount)) {
                max = Math.min(invoicePaymentInfoViewModel.TotalRemAmount.intValue() + invoicePaymentInfoViewModel.PaidAmount.intValue(),
                        maxAmounts.get(invoicePaymentInfoViewModel.InvoiceId).intValue());
            } else {
                Currency totalPaidForAllInvoices = Linq.sumCurrency(adapter.getItems(), new Linq.Selector<InvoicePaymentInfoViewModel, Currency>() {
                    @Override
                    public Currency select(InvoicePaymentInfoViewModel item) {
                        if (item.InvoiceId != invoicePaymentInfoViewModel.InvoiceId)
                            return item.PaidAmount;
                        else
                            return Currency.ZERO;
                    }
                });
                Currency remainedAmount = settlementAmount.subtract(totalPaidForAllInvoices);
                max = Math.min(remainedAmount.intValue(),
                        invoicePaymentInfoViewModel.TotalRemAmount.intValue() + invoicePaymentInfoViewModel.PaidAmount.intValue());
            }
            invoiceNoTextView.setText(invoicePaymentInfoViewModel.InvoiceNo);
            invoiceRemAmountTextView.setText(HelperMethods.currencyToString(invoicePaymentInfoViewModel.TotalRemAmount));
            settlementAmountTextView.setText(HelperMethods.currencyToString(invoicePaymentInfoViewModel.PaidAmount));
            if (paymentType.equals(PaymentType.Discount)) {
                amountSeekBar.setMax(settlementAmount.intValue());
                amountSeekBar.setProgress(invoicePaymentInfoViewModel.PaidAmount.intValue());
            } else {
                amountSeekBar.setMax(invoicePaymentInfoViewModel.Amount.intValue());
                amountSeekBar.setProgress(invoicePaymentInfoViewModel.PaidAmount.intValue());
            }
            amountSeekBar.setSecondaryProgress(max);
            amountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        refresh(i);
                        invoiceRemAmountTextView.setText(HelperMethods.currencyToString(invoicePaymentInfoViewModel.TotalRemAmount));
                        settlementAmountTextView.setText(HelperMethods.currencyToString(invoicePaymentInfoViewModel.PaidAmount));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int value = amountSeekBar.getProgress();
                    if (value != invoicePaymentInfoViewModel.Amount.intValue()) {
                        value = Math.round(value / 10) * 10;
                    }
                    refresh(value);
                    adapter.notifyDataSetChanged();
                }
            });

            calculatorImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Math.abs() => because settlement amount for discount is smaller than zero
                    if (settlementAmount != null && (settlementAmount.intValue()) > 0) {
                        CurrencyInputDialog currencyInputDialog = new CurrencyInputDialog();
                        currencyInputDialog.setArguments(Currency.valueOf(invoicePaymentInfoViewModel.PaidAmount.doubleValue()), new Currency(max));
                        currencyInputDialog.onInputDoneListener = new CurrencyInputDialog.OnInputDoneListener() {
                            @Override
                            public void done(Currency value) {
                                refresh(value.intValue());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void canceled() {

                            }
                        };
                        currencyInputDialog.show(activity.getSupportFragmentManager(), "CurrencyInputDialog");
                    }
                }
            });

        }

        private void refresh(int i) {
            int val = i > max ? max : i;
            amountSeekBar.setProgress(val);
//            if (paymentType.equals(PaymentType.Discount))
//                val = -val;
            Currency newPaidAmount = new Currency(val);
            Currency oldPaidAmount = invoicePaymentInfoViewModel.PaidAmount;
            invoicePaymentInfoViewModel.TotalPaidAmount =
                    invoicePaymentInfoViewModel.TotalPaidAmount.subtract(oldPaidAmount)
                            .add(newPaidAmount);
//            if (paymentType.equals(PaymentType.Discount))
//                invoicePaymentInfoViewModel.TotalRemAmount = invoicePaymentInfoViewModel.TotalRemAmount.subtract(oldPaidAmount)
//                        .add(newPaidAmount);
//            else
            invoicePaymentInfoViewModel.TotalRemAmount = invoicePaymentInfoViewModel.TotalRemAmount.add(oldPaidAmount)
                    .subtract(newPaidAmount);
            invoicePaymentInfoViewModel.PaidAmount = newPaidAmount;
            invoicePaymentInfoViewModel.RemAmount = invoicePaymentInfoViewModel.Amount.subtract(newPaidAmount);
        }
    }
}
