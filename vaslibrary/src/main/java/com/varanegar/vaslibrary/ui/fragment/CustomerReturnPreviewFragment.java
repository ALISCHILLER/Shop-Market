package com.varanegar.vaslibrary.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountItemCountManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountModel;
import com.varanegar.vaslibrary.promotion.CalcPromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderLinePromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderPromotion;
import com.varanegar.vaslibrary.promotion.PromotionCallback;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */

public class CustomerReturnPreviewFragment extends VisitFragment {
    List<DiscountItemCountModel> allDiscountItemCounts;
    BaseRecyclerAdapter<CustomerCallOrderLinePromotion> adapter;
    private PairedItems debitBalancePairedItems;
    private PairedItems creditBalancePairedItems;
    private PairedItems orderCostPairedItems;
    private PairedItems discountAmountPairedItems;
    private PairedItems addAmountPairedItems;
    private BaseRecyclerView recyclerView;
    private UUID customerId;
    private ProgressDialog discountProgressDialog;
    private CustomerCallOrderPromotion customerCallOrderPromotionData;
    private boolean withRef;
    private boolean isFromRequest;
    private PairedItems usanceDayPairedItems;
    private PairedItems usanceRefPairedItems;

    protected void calcPromotion(boolean withRef) {
        showProgressDialog();
        CalcPromotion.calcReturnPromotion(getContext(), customerId, withRef, isFromRequest, new PromotionCallback() {
            @Override
            public void onSuccess(CustomerCallOrderPromotion data) {
                customerCallOrderPromotionData = data;
                VaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    try {
                        dismissProgressDialog();
                        debitBalancePairedItems.setValue(HelperMethods.currencyToString(data.RemainDebit));
                        creditBalancePairedItems.setValue(HelperMethods.currencyToString(data.RemainCredit));
                        addAmountPairedItems.setValue(HelperMethods.currencyToString(data.TotalInvoiceAdd));
                        usanceDayPairedItems.setValue(String.valueOf(data.CashDuration));
                        usanceRefPairedItems.setValue(String.valueOf(data.CheckDuration));

                        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                        BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                        if (backOfficeType == BackOfficeType.ThirdParty && VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                            Currency total = Currency.ZERO;
                            Currency totalDiscount = Currency.ZERO;
                            for (final CustomerCallOrderLinePromotion p :
                                    data.LinesWithPromo) {
                                Currency totalPrice = p.UnitPrice == null ? Currency.ZERO : p.UnitPrice.multiply(p.TotalRequestQty);
                                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                                    totalPrice = totalPrice
                                            .subtract((p.RequestDis1 == null ? new Currency(BigDecimal.ZERO) : p.RequestDis1))
                                            .subtract((p.RequestDis2 == null ? new Currency(BigDecimal.ZERO) : p.RequestDis2))
                                            .subtract((p.RequestDis3 == null ? new Currency(BigDecimal.ZERO) : p.RequestDis3))
                                            .subtract((p.RequestDisOther == null ? new Currency(BigDecimal.ZERO) : p.RequestDisOther))
                                            .add((p.RequestAdd1 == null ? new Currency(BigDecimal.ZERO) : p.RequestAdd1))
                                            .add((p.RequestAdd2 == null ? new Currency(BigDecimal.ZERO) : p.RequestAdd2))
                                            .add((p.RequestAddOther == null ? new Currency(BigDecimal.ZERO) : p.RequestAddOther));
                                else
                                    totalPrice = totalPrice
                                            .subtract((p.InvoiceDis1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis1))
                                            .subtract((p.InvoiceDis2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis2))
                                            .subtract((p.InvoiceDis3 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis3))
                                            .subtract((p.InvoiceDisOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDisOther))
                                            .add((p.InvoiceAdd1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd1))
                                            .add((p.InvoiceAdd2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd2))
                                            .add((p.InvoiceAddOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAddOther));
                                total = total.add(totalPrice);

                                Currency discount;
                                ;
                                if (p.DiscountRef == 0) {
                                    CustomerCallOrderLinePromotion baseLine = Linq.findFirst(data.Lines, new Linq.Criteria<CustomerCallOrderLinePromotion>() {
                                        @Override
                                        public boolean run(CustomerCallOrderLinePromotion item) {
                                            if (item.UniqueId != null) {
                                                return item.UniqueId.equals(p.UniqueId);
                                            }
                                            return false;
                                        }
                                    });
                                    if (p.IsRequestFreeItem)
                                        discount = Currency.ZERO;
                                    else
                                        discount = baseLine.UnitPrice.multiply(p.TotalRequestQty).subtract(totalPrice);
                                } else {
                                    discount = Currency.ZERO;
                                }
                                totalDiscount = totalDiscount.add(discount);
                            }
                            orderCostPairedItems.setValue(HelperMethods.currencyToString(total.add(data.TotalInvoiceAdd)));
                            discountAmountPairedItems.setValue(HelperMethods.currencyToString(totalDiscount));
                        } else {
//                            orderCostPairedItems.setValue(HelperMethods.currencyToString(data.TotalPriceWithPromo.add(data.TotalInvoiceAdd).subtract(data.TotalInvoiceDiscount)));
                            orderCostPairedItems.setValue(HelperMethods.currencyToString(data.AmountNutPT03Add));
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("returnTotal", Context.MODE_PRIVATE);
                            sharedPreferences.edit()
                                    .putString(customerId.toString(),HelperMethods.currencyToString( data.AmountNutPT03Add)).apply();

                            discountAmountPairedItems.setValue(HelperMethods.currencyToString(data.TotalDiscont));
                        }

                        adapter = new BaseRecyclerAdapter<CustomerCallOrderLinePromotion>(getVaranegarActvity(), data.LinesWithPromo) {
                            @Override
                            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.return_preview_row_order_line_item, parent, false);
                                return new ItemViewHolder(view, this, getContext());
                            }
                        };
                        recyclerView.setAdapter(adapter);
                    } catch (UnknownBackOfficeException e) {
                        Timber.e(e);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                VaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    dismissProgressDialog();
                    showErrorMessage(error);
                }
            }

            @Override
            public void onProcess(String msg) {

            }
        });
    }


    private void showProgressDialog() {
        discountProgressDialog = new ProgressDialog(getActivity());
        discountProgressDialog.setMessage(getString(R.string.calculating_discount));
        discountProgressDialog.setCancelable(false);
        discountProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (discountProgressDialog != null && discountProgressDialog.isShowing()) {
            try {
                discountProgressDialog.dismiss();
            } catch (Exception ignored) {
                Timber.e(ignored);
            }
        }
    }

    public void setArguments(@NonNull UUID customerId, boolean withRef, boolean isRequest) {
        addArgument("3af8c4e9-c5c7-4540-8678-4669879caa79", customerId.toString());
        addArgument("c4afed7f-901a-4eae-898c-76dedb2bd738", withRef);
        addArgument("4333ae1b-e90f-423b-aa1c-6425c46ef4fc", isRequest);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.customer_return_preview_layout, container, false);
            customerId = UUID.fromString(getArguments().getString("3af8c4e9-c5c7-4540-8678-4669879caa79"));
            withRef = getArguments().getBoolean("c4afed7f-901a-4eae-898c-76dedb2bd738", false);
            isFromRequest = getArguments().getBoolean("4333ae1b-e90f-423b-aa1c-6425c46ef4fc", false);
            debitBalancePairedItems = view.findViewById(R.id.debit_balance_paired_items);
            creditBalancePairedItems = view.findViewById(R.id.credit_balance_paired_items);
            orderCostPairedItems = view.findViewById(R.id.order_cost_paired_items);
            discountAmountPairedItems = view.findViewById(R.id.discount_amount_paired_items);
            addAmountPairedItems = view.findViewById(R.id.add_amount_paired_items);
            usanceDayPairedItems = view.findViewById(R.id.usance_day_paired_items);
            usanceRefPairedItems = view.findViewById(R.id.usance_ref_paired_items);
            recyclerView = view.findViewById(R.id.recycler_order_line_preview);
            TextView grossAmountTextView = view.findViewById(R.id.gross_amount_text_view);
            TextView discountTextView = view.findViewById(R.id.discount_text_view);
            TextView addsTextView = view.findViewById(R.id.adds_text_view);
            LinearLayout mainHeaderLayout = view.findViewById(R.id.main_report_recycler_header);

            LinearLayout thirdPartyHeaderLayout = view.findViewById(R.id.third_party_report_recycler_header);
            TextView thirdPartyGrossAmount = view.findViewById(R.id.third_party_gross_amount_text_view);
            TextView thirdPartyDiscount = view.findViewById(R.id.third_party_discount_text_view);
            TextView thirdPartyValue = view.findViewById(R.id.third_party_value_text_view);
            TextView thirdPartyAdds = view.findViewById(R.id.third_party_adds_text_view);
            TextView thirdPartythirdPartyNetAmountLayout = view.findViewById(R.id.third_party_net_amount_text_view);

            SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType.equals(BackOfficeType.ThirdParty)) {
                if (mainHeaderLayout != null)
                    mainHeaderLayout.setVisibility(View.GONE);
                if (thirdPartyHeaderLayout != null) {
                    thirdPartyHeaderLayout.setVisibility(View.VISIBLE);
                    if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                        thirdPartyGrossAmount.setVisibility(View.VISIBLE);
                        thirdPartyDiscount.setVisibility(View.VISIBLE);
                        thirdPartyValue.setVisibility(View.VISIBLE);
                        thirdPartyValue.setText(getString(R.string.total_after_discount));
                        thirdPartyAdds.setVisibility(View.VISIBLE);
                        thirdPartythirdPartyNetAmountLayout.setVisibility(View.VISIBLE);
                    } else {
                        thirdPartyGrossAmount.setVisibility(View.GONE);
                        thirdPartyDiscount.setVisibility(View.VISIBLE);
                        thirdPartyValue.setVisibility(View.VISIBLE);
                        thirdPartyValue.setText(getString(R.string.value));
                        thirdPartyAdds.setVisibility(View.GONE);
                        thirdPartythirdPartyNetAmountLayout.setVisibility(View.GONE);
                    }
                }
            } else {
                if (mainHeaderLayout != null)
                    mainHeaderLayout.setVisibility(View.VISIBLE);
                if (thirdPartyHeaderLayout != null)
                    thirdPartyHeaderLayout.setVisibility(View.GONE);
                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    if (grossAmountTextView != null)
                        grossAmountTextView.setVisibility(View.VISIBLE);
                    if (discountTextView != null)
                        discountTextView.setVisibility(View.VISIBLE);
                    if (addsTextView != null)
                        addsTextView.setVisibility(View.VISIBLE);
                }
            }

            ((SimpleToolbar) view.findViewById(R.id.toolbar)).setOnBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getVaranegarActvity().popFragment();
                }
            });

            DiscountItemCountManager discountItemCountManager = new DiscountItemCountManager(getContext());
            allDiscountItemCounts = discountItemCountManager.getItems(DiscountItemCountManager.getAllDiscountItems());
            calcPromotion(withRef);

            return view;
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            return null;
        }
    }

    protected void showErrorMessage(String str) {
        Activity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(activity);
            cuteMessageDialog.setMessage(str);
            cuteMessageDialog.setTitle(R.string.error);
            cuteMessageDialog.setIcon(Icon.Error);
            cuteMessageDialog.setPositiveButton(R.string.ok, null);
            cuteMessageDialog.show();
        }
    }

    private void showErrorMessage() {
        CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
        cuteMessageDialog.setMessage(R.string.error_saving_request);
        cuteMessageDialog.setTitle(R.string.error);
        cuteMessageDialog.setIcon(Icon.Error);
        cuteMessageDialog.setPositiveButton(R.string.ok, null);
        cuteMessageDialog.show();
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    private class ItemViewHolder extends BaseViewHolder<CustomerCallOrderLinePromotion> {
        private final LinearLayout llyorderQty;
        protected TextView txtvProductName;
        protected TextView txtvProductCode;
        protected TextView txtvQty;
        protected TextView txtValue;
        protected TextView txtvUnitPrice;
        protected TextView txtvRow;
        protected ImageView imgType;
        protected TextView txtDiscount, txtAdds, txtOrderAmount;
        protected LinearLayout additionalValueLayout;
        protected LinearLayout mainLayout, thirdPartyLayout;
        protected LinearLayout thirdPartyGrossAmountLayout, thirdPartyAddAmountLayout, thirdPartyNetAmountLayout;
        protected TextView thirdPartyGrossAmountTextView, thirdPartyDiscountTextView,
                thirdPartyValueTextView, thirdPartyAddTextView, thirdPartyNetAmountTextView, thirdPartyValueTextViewLabel;


        public ItemViewHolder(View view, BaseRecyclerAdapter<CustomerCallOrderLinePromotion> recyclerAdapter, Context context) {
            super(view, recyclerAdapter, context);
            this.txtvProductCode = view.findViewById(R.id.txtv_product_code);
            this.txtvProductName = view.findViewById(R.id.txtv_product_name);
            this.txtvUnitPrice = view.findViewById(R.id.txtv_unit_price);
            this.txtvQty = view.findViewById(R.id.txtv_total_qty);
            this.txtvRow = view.findViewById(R.id.txtv_row_number);
            this.txtValue = view.findViewById(R.id.order_value_text_view);
            this.imgType = view.findViewById(R.id.icon_image_view);
            this.llyorderQty = itemView.findViewById(R.id.lly_order_qty);
            this.txtDiscount = (TextView) view.findViewById(R.id.discount_value_text_view);
            this.txtOrderAmount = (TextView) view.findViewById(R.id.gross_amount_value_text_view);
            this.txtAdds = (TextView) view.findViewById(R.id.add_value_text_view);
            this.additionalValueLayout = view.findViewById(R.id.editional_value_layout);
            this.mainLayout = view.findViewById(R.id.main_row_order_line_detail);

            this.thirdPartyLayout = view.findViewById(R.id.third_party_row_order_line_detail);
            this.thirdPartyGrossAmountLayout = view.findViewById(R.id.third_party_gross_amount_value_layout);
            this.thirdPartyAddAmountLayout = view.findViewById(R.id.third_party_add_value_layout);
            this.thirdPartyNetAmountLayout = view.findViewById(R.id.third_party_net_amount_value_layout);
            this.thirdPartyGrossAmountTextView = view.findViewById(R.id.third_party_gross_amount_value_text_view);
            this.thirdPartyDiscountTextView = view.findViewById(R.id.third_party_discount_value_text_view);
            this.thirdPartyValueTextView = view.findViewById(R.id.third_party_order_value_text_view);
            this.thirdPartyAddTextView = view.findViewById(R.id.third_party_add_value_text_view);
            this.thirdPartyNetAmountTextView = view.findViewById(R.id.third_party_net_amount_value_text_view);
            this.thirdPartyValueTextViewLabel = view.findViewById(R.id.third_party_order_value_text_view_label);
        }

        @Override
        public void bindView(final int position) {
            try {
                SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                if (backOfficeType == BackOfficeType.ThirdParty) {
                    if (mainLayout != null)
                        mainLayout.setVisibility(View.GONE);
                    if (thirdPartyLayout != null)
                        thirdPartyLayout.setVisibility(View.VISIBLE);

                    if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                        if (thirdPartyGrossAmountLayout != null)
                            thirdPartyGrossAmountLayout.setVisibility(View.VISIBLE);
                        if (thirdPartyAddAmountLayout != null)
                            thirdPartyAddAmountLayout.setVisibility(View.VISIBLE);
                        if (thirdPartyNetAmountLayout != null)
                            thirdPartyNetAmountLayout.setVisibility(View.VISIBLE);
                        if (thirdPartyValueTextViewLabel != null)
                            thirdPartyValueTextViewLabel.setText(getString(R.string.total_after_discount));
                    } else {
                        if (thirdPartyGrossAmountLayout != null)
                            thirdPartyGrossAmountLayout.setVisibility(View.GONE);
                        if (thirdPartyAddAmountLayout != null)
                            thirdPartyAddAmountLayout.setVisibility(View.GONE);
                        if (thirdPartyNetAmountLayout != null)
                            thirdPartyNetAmountLayout.setVisibility(View.GONE);
                        if (thirdPartyValueTextViewLabel != null)
                            thirdPartyValueTextViewLabel.setText(getString(R.string.value));
                    }
                } else {
                    if (mainLayout != null)
                        mainLayout.setVisibility(View.VISIBLE);
                    if (thirdPartyLayout != null)
                        thirdPartyLayout.setVisibility(View.GONE);
                    if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                        if (additionalValueLayout != null)
                            additionalValueLayout.setVisibility(View.VISIBLE);
                    } else {
                        if (additionalValueLayout != null)
                            additionalValueLayout.setVisibility(View.GONE);
                    }
                }

                final CustomerCallOrderLinePromotion p = recyclerAdapter.get(position);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerAdapter.runItemClickListener(position);
                    }
                });

                txtvProductName.setText(p.ProductName);
                txtvProductCode.setText(p.ProductCode);
                txtvQty.setText(HelperMethods.bigDecimalToString(p.TotalRequestQty));
                Currency totalPrice = p.UnitPrice == null ? Currency.ZERO : p.UnitPrice.multiply(p.TotalRequestQty);
                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                    totalPrice = totalPrice
                            .subtract((p.RequestDis1 == null ? new Currency(BigDecimal.ZERO) : p.RequestDis1))
                            .subtract((p.RequestDis2 == null ? new Currency(BigDecimal.ZERO) : p.RequestDis2))
                            .subtract((p.RequestDis3 == null ? new Currency(BigDecimal.ZERO) : p.RequestDis3))
                            .subtract((p.RequestDisOther == null ? new Currency(BigDecimal.ZERO) : p.RequestDisOther))
                            .add((p.RequestAdd1 == null ? new Currency(BigDecimal.ZERO) : p.RequestAdd1))
                            .add((p.RequestAdd2 == null ? new Currency(BigDecimal.ZERO) : p.RequestAdd2))
                            .add((p.RequestAddOther == null ? new Currency(BigDecimal.ZERO) : p.RequestAddOther));
                else
                    totalPrice = totalPrice
                            .subtract((p.InvoiceDis1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis1))
                            .subtract((p.InvoiceDis2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis2))
                            .subtract((p.InvoiceDis3 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis3))
                            .subtract((p.InvoiceDisOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDisOther))
                            .add((p.InvoiceAdd1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd1))
                            .add((p.InvoiceAdd2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd2))
                            .add((p.InvoiceAddOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAddOther));

                if (!backOfficeType.equals(BackOfficeType.ThirdParty)) {
                    txtValue.setText(p.IsRequestFreeItem ? getString(R.string.multiplication_sign) : HelperMethods.currencyToString(totalPrice));
                } else {
                    Currency valueAmount = p.UnitPrice == null ? Currency.ZERO : p.UnitPrice.multiply(p.TotalRequestQty);
                    valueAmount = valueAmount
                            .subtract((p.InvoiceDis1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis1))
                            .subtract((p.InvoiceDis2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis2))
                            .subtract((p.InvoiceDis3 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis3))
                            .subtract((p.InvoiceDisOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDisOther));
                    thirdPartyValueTextView.setText(HelperMethods.currencyToString(valueAmount));
                }

                txtvRow.setText(position + 1 + "");

                if (p.IsRequestFreeItem) {
                    imgType.setVisibility(View.VISIBLE);
                    imgType.setImageResource(R.drawable.ic_gift_teal_24dp);
                } else {
                    imgType.setVisibility(View.INVISIBLE);
                }

                if (p.DiscountRef == 0) {
                    imgType.setVisibility(View.INVISIBLE);
                    itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
                } else {
                    imgType.setVisibility(View.VISIBLE);
                    imgType.setImageResource(R.drawable.ic_prize);
                    itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.holo_green_light));
                    if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                        for (DiscountItemCountModel discountItemCountModel : allDiscountItemCounts) {
                            if (p.DiscountRef == discountItemCountModel.DisRef) {
                                imgType.setImageResource(R.drawable.ic_prizes);
                                break;
                            }
                        }
                }

                if (backOfficeType == BackOfficeType.ThirdParty && VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                    if (p.DiscountRef == 0) {
                        CustomerCallOrderLinePromotion baseLine = Linq.findFirst(customerCallOrderPromotionData.Lines, new Linq.Criteria<CustomerCallOrderLinePromotion>() {
                            @Override
                            public boolean run(CustomerCallOrderLinePromotion item) {
                                if (item.UniqueId != null) {
                                    return item.UniqueId.equals(p.UniqueId);
                                }
                                return false;
                            }
                        });
                        if (p.IsRequestFreeItem)
                            txtvUnitPrice.setText(p.FreeReasonName);
                        else {
                            txtvUnitPrice.setText(HelperMethods.currencyToString(baseLine.UnitPrice));
                            thirdPartyDiscountTextView.setText(HelperMethods.currencyToString(p.TakhfifatKol));
                        }
                    } else {
                        if (p.IsRequestFreeItem)
                            txtvUnitPrice.setText(p.FreeReasonName);
                        else
                            txtvUnitPrice.setText(HelperMethods.currencyToString(p.UnitPrice));
                    }
                } else {
                    if (p.IsRequestFreeItem)
                        txtvUnitPrice.setText(p.FreeReasonName);
                    else
                        txtvUnitPrice.setText(HelperMethods.currencyToString(p.Fee));
                }

                List<BaseUnit> units = new ArrayList<>();
                if (p.UnitName == null)
                    Timber.d("UnitName is null for ProductId = " + p.ProductId + " and DiscountRef = " + p.DiscountRef);
                String[] unitNames = p.UnitName.split(":");
                if (p.QtyCaption == null)
                    Timber.d("QtyCaption is null for ProductId = " + p.ProductId + " and DiscountRef = " + p.DiscountRef);
                String[] strUnits = p.QtyCaption.split(":");
                for (int i = 0; i < strUnits.length; i++) {
                    String strUnit = strUnits[i];
                    BaseUnit unit = new BaseUnit();
                    unit.value = Double.parseDouble(strUnit);
                    unit.Name = unitNames[i];
                    if (unit.value > 0)
                        units.add(unit);
                }
                new QtyView().build(llyorderQty, units);


                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    String orderAmount = p.IsRequestFreeItem ? "0" : HelperMethods.currencyToString(p.UnitPrice == null ? Currency.ZERO : p.UnitPrice.multiply(p.TotalRequestQty));
                    String discountAmount = HelperMethods.currencyToString((p.InvoiceDis1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis1)
                            .add((p.InvoiceDis2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis2))
                            .add((p.InvoiceDis3 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis3))
                            .add((p.InvoiceDisOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDisOther)));


                    String addAmount = HelperMethods.currencyToString((p.InvoiceAdd1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd1)
                            .add((p.InvoiceAdd2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd2)).add((p.InvoiceAddOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAddOther)));
                    if (backOfficeType.equals(BackOfficeType.ThirdParty)) {
                        thirdPartyGrossAmountTextView.setText(HelperMethods.currencyToString(p.FeeKol));
                        thirdPartyDiscountTextView.setText(HelperMethods.currencyToString(p.TakhfifatKol));
                        thirdPartyAddTextView.setText(addAmount);
                        thirdPartyNetAmountTextView.setText(p.IsRequestFreeItem ? getString(R.string.multiplication_sign) : HelperMethods.currencyToString(totalPrice));
                    } else {
                        txtOrderAmount.setText(orderAmount);
                        txtDiscount.setText(discountAmount);
                        txtAdds.setText(addAmount);
                    }
                }
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
            }
        }
    }
}
