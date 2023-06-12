package com.varanegar.vaslibrary.ui.fragment.order;

import android.app.Activity;
import android.content.Context;
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

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.toolbar.CuteToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.PromotionException;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountItemCountManager;
import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.promotion.CalcPromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderLinePromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderPromotion;
import com.varanegar.vaslibrary.promotion.PromotionCallback;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.dialog.choiceprize.ChoicePrizesDialog;
import com.varanegar.vaslibrary.ui.fragment.VisitFragment;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountPrizeViewModel;

import static varanegar.com.discountcalculatorlib.Global.orderPrize;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */

/**
 * صفحه پیش نمایش
 */
public class CustomerOrderPreviewFragment extends VisitFragment implements ChoicePrizesDialog.choicePrizeDialogListener {
    List<DiscountItemCountModel> allDiscountItemCounts;
    CustomerCallOrderPromotion customerCallOrderPromotion;
    BaseRecyclerAdapter<CustomerCallOrderLinePromotion> orderlineAdapter;
    private PairedItems debitBalancePairedItems;
    private PairedItems creditBalancePairedItems;
    private PairedItems paymentTypePairedItems;
    private PairedItems orderTypePairedItems;
    private PairedItems orderCostPairedItems;
    private PairedItems discountAmountPairedItems;
    private PairedItems addAmountPairedItems;
    private PairedItems payableCashPairedItems;
    private PairedItems payableChequePairedItems;
    private PairedItems payableImmediatePairedItems;
    private PairedItems discountImmediatePairedItems;
    private BaseRecyclerView recyclerView;
    private UUID customerId;
    private UUID callOrderId;
    private int loopCount = 0;
    private List<Integer> discountRefs = new ArrayList<>();
    private boolean last = false;
    private TextView titleTextView;
    List<DiscountOrderPrizeViewModel> orderPrizeList = new ArrayList<>();
    private TextView adjustmentPriceTextView;
    Boolean hasAdjustmentPrice = false;
    private PairedItems usanceDayPairedItems;
    private PairedItems usanceRefPairedItems;

    protected void calcPromotion(final List<Integer> SelIds, List<DiscountOrderPrizeViewModel> orderPrizeList, PromotionCallback callback) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
            CalcPromotion.calcPromotionV3WithDialog(SelIds, orderPrizeList, getActivity(), callOrderId, customerId, EVCType.HOTSALE, true, false, false, callback);
        else
            CalcPromotion.calcPromotionV3WithDialog(SelIds, orderPrizeList, getActivity(), callOrderId, customerId, EVCType.TOSELL, true, false, false, callback);
    }

    public void setArguments(@NonNull UUID customerId, @NonNull UUID callOrderId) {
        addArgument("1c886632-a88a-4e73-9164-f6656c219917", callOrderId.toString());
        addArgument("3af8c4e9-c5c7-4540-8678-4669879caa79", customerId.toString());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.customer_order_preview_layout, container, false);
            customerId = UUID.fromString(getArguments().getString("3af8c4e9-c5c7-4540-8678-4669879caa79"));
            callOrderId = UUID.fromString(getArguments().getString("1c886632-a88a-4e73-9164-f6656c219917"));
            debitBalancePairedItems = (PairedItems) view.findViewById(R.id.debit_balance_paired_items);
            creditBalancePairedItems = (PairedItems) view.findViewById(R.id.credit_balance_paired_items);
            paymentTypePairedItems = (PairedItems) view.findViewById(R.id.payment_type_name_paired_items);
            orderTypePairedItems = (PairedItems) view.findViewById(R.id.order_type_name_paired_items);
            orderCostPairedItems = (PairedItems) view.findViewById(R.id.order_cost_paired_items);
            discountAmountPairedItems = (PairedItems) view.findViewById(R.id.discount_amount_paired_items);
            addAmountPairedItems = (PairedItems) view.findViewById(R.id.add_amount_paired_items);
            usanceDayPairedItems = view.findViewById(R.id.usance_day_paired_items);
            usanceRefPairedItems = view.findViewById(R.id.usance_ref_paired_items);
            View thirdPartyLayout = view.findViewById(R.id.third_party_layout);
            TextView grossAmountTextView = view.findViewById(R.id.gross_amount_text_view);
            TextView discountTextView = view.findViewById(R.id.discount_text_view);
            TextView addsTextView = view.findViewById(R.id.adds_text_view);
            titleTextView = (TextView) view.findViewById(R.id.title_text_view);
            recyclerView = view.findViewById(R.id.recycler_order_line_preview);
            adjustmentPriceTextView = view.findViewById(R.id.adjustment_price_text_view);
            LinearLayout mainHeaderLayout = view.findViewById(R.id.main_report_recycler_header);

            LinearLayout thirdPartyHeaderLayout = view.findViewById(R.id.third_party_report_recycler_header);
            TextView thirdPartyGrossAmount = view.findViewById(R.id.third_party_gross_amount_text_view);
            TextView thirdPartyDiscount = view.findViewById(R.id.third_party_discount_text_view);
            TextView thirdPartyValue = view.findViewById(R.id.third_party_value_text_view);
            TextView thirdPartyAdds = view.findViewById(R.id.third_party_adds_text_view);
            TextView thirdPartythirdPartyNetAmountLayout = view.findViewById(R.id.third_party_net_amount_text_view);

            CustomerModel customerModel = new CustomerManager(getContext()).getItem(customerId);
            CustomerCallOrderModel customerCallOrderModel = new CustomerCallOrderManager(getContext()).getItem(callOrderId);
            titleTextView.setText(getString(R.string.order_preview) + " : " + customerModel.CustomerName + " " + customerCallOrderModel.SaleNoSDS);

            payableCashPairedItems = view.findViewById(R.id.payable_cash_paired_items);
            payableChequePairedItems = view.findViewById(R.id.payable_cheque_paired_items);
            payableImmediatePairedItems = view.findViewById(R.id.payable_immediate_paired_items);
            discountImmediatePairedItems = view.findViewById(R.id.discount_immediate_paired_items);
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
                    if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                        payableCashPairedItems.setVisibility(View.VISIBLE);
                        payableChequePairedItems.setVisibility(View.VISIBLE);
                        payableImmediatePairedItems.setVisibility(View.VISIBLE);
                        discountImmediatePairedItems.setVisibility(View.VISIBLE);
                        orderCostPairedItems.setTitle(getString(R.string.cash_discount_amount));
                        discountAmountPairedItems.setTitle(getString(R.string.cheque_discount));
                    } else {
                        payableCashPairedItems.setVisibility(View.GONE);
                        payableChequePairedItems.setVisibility(View.GONE);
                        payableImmediatePairedItems.setVisibility(View.GONE);
                        discountImmediatePairedItems.setVisibility(View.GONE);

                    }
                }
                thirdPartyLayout.setVisibility(View.VISIBLE);
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
                thirdPartyLayout.setVisibility(View.GONE);
            }

            if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                orderTypePairedItems.setVisibility(View.GONE);
            else
                orderTypePairedItems.setVisibility(View.VISIBLE);

            view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            CuteToolbar toolbar = view.findViewById(R.id.options_toolbar);

            DiscountItemCountManager discountItemCountManager = new DiscountItemCountManager(getContext());
            allDiscountItemCounts = discountItemCountManager.getItems(DiscountItemCountManager.getAllDiscountItems());
            calc(true);

            return view;
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            return null;
        }


    }

    private void calc(Boolean first) {
        if (first)
            orderPrizeList = new ArrayList<>();
        calcPromotion(null, orderPrizeList, new PromotionCallback() {
            @Override
            public void onSuccess(CustomerCallOrderPromotion data) {
                customerCallOrderPromotion = data;
                //  titleTextView.setText(getString(R.string.order_preview) + " : " + customerCallOrderPromotion.CustomerName + " (" + customerCallOrderPromotion.CustomerCode + ")");

                for (CustomerCallOrderLinePromotion lines : customerCallOrderPromotion.LinesWithPromo) {
                    if (lines.AdjustmentPrice != null) {
                        hasAdjustmentPrice = true;
                        break;
                    }
                }
                if (adjustmentPriceTextView != null) {
                    if (hasAdjustmentPrice)
                        adjustmentPriceTextView.setVisibility(View.VISIBLE);
                    else
                        adjustmentPriceTextView.setVisibility(View.GONE);
                }
                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales) &&
                        customerCallOrderPromotion.LinesWithPromo != null &&
                        customerCallOrderPromotion.LinesWithPromo.size() > 0) {
                    try {
                        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                        callOrderLineManager.updateLineWithPromotionForPreSales(callOrderId, customerCallOrderPromotion);
                        VaranegarApplication.getInstance().save("CUSTOMER_ID_ORDER_PREVIEW", customerId);
                    } catch (ValidationException e) {
                        Timber.e(e);
                    } catch (DbException e) {
                        Timber.e(e);
                    } catch (ProductUnitViewManager.UnitNotFoundException e) {
                        Timber.e(e);
                    } catch (PromotionException e) {
                        Timber.e(e);
                    }
                }
                debitBalancePairedItems.setValue(customerCallOrderPromotion.RemainDebit.toString());
                creditBalancePairedItems.setValue(customerCallOrderPromotion.RemainCredit.toString());
                paymentTypePairedItems.setValue(customerCallOrderPromotion.OrderPaymentTypeName);
                orderTypePairedItems.setValue(customerCallOrderPromotion.OrderTypeName);
                usanceDayPairedItems.setValue(String.valueOf(data.CashDuration));
                usanceRefPairedItems.setValue(String.valueOf(data.CheckDuration));

                final OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
                orderlineAdapter = new BaseRecyclerAdapter<CustomerCallOrderLinePromotion>(getVaranegarActvity(), customerCallOrderPromotion.LinesWithPromo) {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_line_item, parent, false);
                        return new ItemViewHolder(view, this, getContext());
                    }

                    @Nullable
                    @Override
                    protected ItemContextView onCreateContextView() {
                        return new OrderPrizeContextView(this, getContext());
                    }
                };
                final List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerDisRefOrderPrizes(customerId, callOrderId));
                for (final OrderPrizeModel orderPrizeModel :
                        orderPrizeModels) {
                    orderlineAdapter.addContextMenuItem(new ContextMenuItem() {
                        @Override
                        public boolean isAvailable(int position) {
                            return true;
                        }

                        @Override
                        public String getName(int position) {
                            return getString(R.string.selective_prize) + " " + (position + 1);
                        }

                        @Override
                        public int getIcon(int position) {
                            return 0;
                        }

                        @Override
                        public void run(int position) {
                            showOrderPrizeDialog(orderPrizeModel.DiscountId, orderPrizeModel.DisRef);
                        }
                    });
                }

                recyclerView.setAdapter(orderlineAdapter);
                if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    CalcPromotion.fillChoicePrize(getContext(), customerCallOrderPromotion, customerId, callOrderId);
                    orderlineAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<CustomerCallOrderLinePromotion>() {
                        @Override
                        public void run(int position) {
                            showOrderPrizeDialog(orderlineAdapter.get(position).DiscountId, orderlineAdapter.get(position).DiscountRef);
                        }
                    });
                }

                refresh();
            }

            @Override
            public void onFailure(String error) {
                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && error.equals("3001")) {
                    loopCount = 0;
                    discountRefs = new ArrayList<>();
                    last = false;
                    orderPrizeList = new ArrayList<>();
                    showPrizeDialog();
                } else {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                        cuteMessageDialog.setIcon(Icon.Error);
                        cuteMessageDialog.setMessage(error);
                        cuteMessageDialog.setNegativeButton(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        cuteMessageDialog.show();
                    }
                }
            }

            @Override
            public void onProcess(String msg) {

            }
        });
    }

    private void showReturnReasonDialog() {
        OrderReturnReasonDialog returnReasonDialog = new OrderReturnReasonDialog();
        returnReasonDialog.onItemSelected = new OrderReturnReasonDialog.OnItemSelected() {
            @Override
            public void onChanged(UUID reasonUniqueId) {
                try {
                    CustomerCallManager callManager = new CustomerCallManager(getContext());
                    callManager.saveDistPartialDeliveryCall(customerId, callOrderId, reasonUniqueId);
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing())
                        activity.popFragment();
                } catch (Exception e) {
                    showErrorMessage();
                }
            }
        };
        returnReasonDialog.show(getActivity().getSupportFragmentManager(), "PartialOrderActionDialog");
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


    @Override
    public void onFinishChoicePrize(int disRef, UUID customerId, UUID callOrderId, HashMap<UUID, OrderPrizeModel> oldPrize) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
            List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, disRef, callOrderId));
            for (OrderPrizeModel orderPrizeModel : orderPrizeModels) {
                DiscountOrderPrizeViewModel choicePrizeDiscountOrderPrizeViewModel = new DiscountOrderPrizeViewModel();
                choicePrizeDiscountOrderPrizeViewModel.discountRef = disRef;
                ProductModel productModel = new ProductManager(getContext()).getItem(orderPrizeModel.ProductId);
                choicePrizeDiscountOrderPrizeViewModel.goodsRef = productModel.BackOfficeId;
                choicePrizeDiscountOrderPrizeViewModel.orderDiscountRef = orderPrize.get(0).orderDiscountRef;
                choicePrizeDiscountOrderPrizeViewModel.qty = Integer.parseInt(String.valueOf(orderPrizeModel.TotalQty.setScale(0, RoundingMode.HALF_DOWN)));
                DiscountPrizeViewModel discountPrizeViewModel = orderPrize.get(loopCount - 1);
                for (DiscountOrderPrizeViewModel discountOrderPrizeViewModel : discountPrizeViewModel.orderPrizeList) {
                    if (discountOrderPrizeViewModel.goodsRef == choicePrizeDiscountOrderPrizeViewModel.goodsRef) {
                        choicePrizeDiscountOrderPrizeViewModel.unitRef = discountOrderPrizeViewModel.unitRef;
                        choicePrizeDiscountOrderPrizeViewModel.id = discountOrderPrizeViewModel.id;
                    }
                }
                orderPrizeList.add(choicePrizeDiscountOrderPrizeViewModel);
            }
            if (last)
                calc(false);
            else
                showPrizeDialog();
        } else {
            CalcPromotion.finishChoicePrize(getContext(), customerCallOrderPromotion, disRef, customerId, callOrderId, oldPrize);
            refresh();
            recyclerView.setAdapter(orderlineAdapter);
        }

    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    private class ItemViewHolder extends BaseViewHolder<CustomerCallOrderLinePromotion> {
        protected TextView txtvProductName;
        protected TextView txtvProductCode;
        protected LinearLayout llyorderQty;
        protected TextView txtvQty;
        protected TextView txtValue;
        protected TextView txtvUnitPrice;
        protected TextView txtvRow;
        protected ImageView imgType;
        protected TextView txtOrderAmount;
        protected TextView txtDiscount;
        protected TextView txtAdds;
        protected LinearLayout additionalValueLayout;
        protected LinearLayout adjustmentPriceLayout;
        protected TextView adjustmentPriceTextView;

        protected LinearLayout mainLayout, thirdPartyLayout;
        protected LinearLayout thirdPartyGrossAmountLayout, thirdPartyAddAmountLayout, thirdPartyNetAmountLayout;
        protected TextView thirdPartyGrossAmountTextView, thirdPartyDiscountTextView, thirdPartyValueTextView, thirdPartyAddTextView, thirdPartyNetAmountTextView, thirdPartyValueTextViewLabel;

        public ItemViewHolder(View view, BaseRecyclerAdapter<CustomerCallOrderLinePromotion> recyclerAdapter, Context context) {
            super(view, recyclerAdapter, context);
            this.txtvProductName = (TextView) view.findViewById(R.id.txtv_product_name);
            this.txtvProductCode = (TextView) view.findViewById(R.id.txtv_product_code);
            this.txtvUnitPrice = (TextView) view.findViewById(R.id.txtv_unit_price);
            this.txtvProductCode = (TextView) view.findViewById(R.id.txtv_product_code);
            this.txtvQty = (TextView) view.findViewById(R.id.txtv_total_qty);
            this.llyorderQty = (LinearLayout) itemView.findViewById(R.id.lly_order_qty);
            this.txtvRow = (TextView) view.findViewById(R.id.txtv_row_number);
            this.txtValue = (TextView) view.findViewById(R.id.order_value_text_view);
            this.imgType = (ImageView) view.findViewById(R.id.icon_image_view);
            this.txtOrderAmount = (TextView) view.findViewById(R.id.gross_amount_value_text_view);
            this.txtDiscount = (TextView) view.findViewById(R.id.discount_value_text_view);
            this.txtAdds = (TextView) view.findViewById(R.id.add_value_text_view);
            this.additionalValueLayout = view.findViewById(R.id.editional_value_layout);
            this.adjustmentPriceLayout = view.findViewById(R.id.adjustment_price_layout);
            this.adjustmentPriceTextView = view.findViewById(R.id.adjustment_price_value_text_view);
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
                final CustomerCallOrderLinePromotion p = recyclerAdapter.get(position);

                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    if (!p.QtyCaption.contains(":") && p.UnitName.equals("EA")) {
                        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
                        try {
                            List<ProductUnitViewModel> unitViewModels = productUnitViewManager.getProductUnits(p.ProductId, ProductType.isForSale);

                            BigDecimal b = new BigDecimal(p.QtyCaption);

                            List<DiscreteUnit> units = VasHelperMethods.chopTotalQty(b, unitViewModels, false);
                            String UnitNames = null;
                            String ConvertFactors = null;
                            String UnitIds = null;
                            String qtys = null;
                            for (DiscreteUnit discreteUnit :
                                    units) {
                                if (discreteUnit.value > 0) {
                                    if (UnitNames != null) {
                                        UnitNames = UnitNames + ":" + discreteUnit.Name;
                                    } else {
                                        UnitNames = discreteUnit.Name;
                                    }
                                    if (ConvertFactors != null) {
                                        ConvertFactors = ConvertFactors + ":" + discreteUnit.ConvertFactor;
                                    } else {
                                        ConvertFactors = String.valueOf(discreteUnit.ConvertFactor);
                                    }
                                    if (UnitIds != null) {
                                        UnitIds = UnitIds + ":" + discreteUnit.ProductUnitId;
                                    } else {
                                        UnitIds = String.valueOf(discreteUnit.ProductUnitId);
                                    }
                                    if (qtys != null) {
                                        qtys = qtys + ":" + discreteUnit.value;
                                    } else {
                                        qtys = String.valueOf(discreteUnit.value);
                                    }
                                }
                            }


                            p.UnitName = UnitNames;
                            p.QtyCaption = qtys;


                        } catch (ProductUnitViewManager.UnitNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
                        if (p.DiscountRef != 0) {
                            List<OrderPrizeModel> orderPrizeModels = orderPrizeManager.getItems(OrderPrizeManager.getCustomerDisRefOrderPrizes(customerId, callOrderId));
                            if (orderPrizeModels.size() > 1)
                                recyclerAdapter.showContextMenu(getAdapterPosition());
                            else
                                recyclerAdapter.runItemClickListener(position);
                        }
                    }
                });

                if (p.ProductName == null || p.ProductCode == null) {
                    ProductManager productManager = new ProductManager(getContext());
                    ProductModel productModel = productManager.getProductByBackOfficeId(p.ProductRef);
                    txtvProductName.setText(productModel.ProductName);
                    txtvProductCode.setText(productModel.ProductCode);
                } else {
                    txtvProductName.setText(p.ProductName);
                    txtvProductCode.setText(p.ProductCode);
                }

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

                Currency amuntFol = Currency.ZERO;

                if (p.zterm.equals("PT01"))
                    amuntFol = p.AmountNutImmediate;
                else if (p.zterm.equals("PTCH"))
                    amuntFol = p.AmountNutCheque;
                else if (p.zterm.equals("PTCA"))
                    amuntFol = p.AmountNutCash;
<<<<<<<HEAD
                else if (p.zterm.equals("PT02"))
                    amuntFol = p.AmountNutCheque;
=======

>>>>>>>origin / dev
                if (backOfficeType == BackOfficeType.ThirdParty && VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                    if (p.DiscountRef == 0) {
                        CustomerCallOrderLinePromotion baseLine = Linq.findFirst(customerCallOrderPromotion.Lines, new Linq.Criteria<CustomerCallOrderLinePromotion>() {
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
                            txtvUnitPrice.setText(HelperMethods.currencyToString(baseLine.Fee));
                            thirdPartyDiscountTextView.setText(HelperMethods.currencyToString((baseLine.UnitPrice.multiply(p.TotalRequestQty).subtract(totalPrice))));
                        }
                    } else {
                        if (p.IsRequestFreeItem)
                            txtvUnitPrice.setText(p.FreeReasonName);
                        else
                            txtvUnitPrice.setText(HelperMethods.currencyToString(p.Fee));
                    }
                } else {
                    if (p.IsRequestFreeItem)
                        txtvUnitPrice.setText(p.FreeReasonName);
                    else
                        txtvUnitPrice.setText(HelperMethods.currencyToString(p.Fee));
                }

                if (!backOfficeType.equals(BackOfficeType.ThirdParty)) {
                    txtValue.setText(p.IsRequestFreeItem ? getString(R.string.multiplication_sign) : HelperMethods.currencyToString(totalPrice));
                } else {
                    Currency valueAmount = p.UnitPrice == null ? Currency.ZERO : p.UnitPrice.multiply(p.TotalRequestQty);

                    valueAmount = valueAmount
                            .subtract((p.InvoiceDis1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis1))
                            .subtract((p.InvoiceDis2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis2))
                            .subtract((p.InvoiceDis3 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis3))
                            .subtract((p.InvoiceDisOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDisOther));

                    Currency custPrice = p.amount == null ? Currency.ZERO : p.amount;
                  //  Currency custPrice = p.custPrice == null ? Currency.ZERO : p.custPrice;
                    thirdPartyValueTextView.setText(HelperMethods.currencyToString(custPrice));
                }
                txtvRow.setText(position + 1 + "");

                if (p.IsRequestFreeItem) {
                    imgType.setVisibility(View.VISIBLE);
                    imgType.setImageResource(R.drawable.ic_gift_teal_24dp);
                } else {
                    imgType.setVisibility(View.INVISIBLE);
                }

                if (p.DiscountRef != 0) {
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
                } else {
                    imgType.setVisibility(View.INVISIBLE);
                    itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
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


                    String grossAmountValue = p.IsRequestFreeItem ? "0" :
                            HelperMethods.currencyToString(p.UnitPrice == null
                                    ? Currency.ZERO : p.UnitPrice.multiply(p.TotalRequestQty));

                    String feeKol = HelperMethods.currencyToString(p.FeeKol);
//                    String discount = HelperMethods.currencyToString((p.InvoiceDis1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis1)
//                            .add((p.InvoiceDis2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis2))
//                            .add((p.InvoiceDis3 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDis3))
//                            .add((p.InvoiceDisOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceDisOther)));
                    String discount = HelperMethods.currencyToString(p.TakhfifatKol);
                    String addValue = HelperMethods.currencyToString((p.InvoiceAdd1 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd1)
                            .add((p.InvoiceAdd2 == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAdd2)).add((p.InvoiceAddOther == null ? new Currency(BigDecimal.ZERO) : p.InvoiceAddOther)));
                    if (backOfficeType.equals(BackOfficeType.ThirdParty)) {
                        thirdPartyGrossAmountTextView.setText(feeKol);
                        thirdPartyDiscountTextView.setText(discount);
                        thirdPartyAddTextView.setText(addValue);
                        thirdPartyNetAmountTextView.setText(p.IsRequestFreeItem ? getString(R.string.multiplication_sign) : HelperMethods.currencyToString(amuntFol));
                    } else {
                        txtOrderAmount.setText(grossAmountValue);
                        txtDiscount.setText(discount);
                        txtAdds.setText(addValue);
                    }
                }

                if (hasAdjustmentPrice) {
                    adjustmentPriceLayout.setVisibility(View.VISIBLE);
                    if (p.AdjustmentPrice != null)
                        adjustmentPriceTextView.setText(HelperMethods.currencyToString(p.AdjustmentPrice));
                    else {
                        if (p.IsRequestFreeItem)
                            adjustmentPriceTextView.setText(p.FreeReasonName);
                        else
                            adjustmentPriceTextView.setText(HelperMethods.currencyToString(p.UnitPrice));
                    }
                } else
                    adjustmentPriceLayout.setVisibility(View.GONE);
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
            }
        }
    }

    class OrderPrizeContextView extends ItemContextView<CustomerCallOrderLinePromotion> {

        public OrderPrizeContextView(BaseRecyclerAdapter<CustomerCallOrderLinePromotion> adapter, Context context) {
            super(adapter, context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
            View view = inflater.inflate(R.layout.order_prize_context_view, viewGroup, false);
            return view;
        }
    }

    public void showOrderPrizeDialog(UUID discountId, int discountRef) {
        DiscountItemCountManager discountItemCountManager = new DiscountItemCountManager(getContext());
        List<DiscountItemCountModel> discountItemCountModels = discountItemCountManager.getItems(DiscountItemCountManager.getAllDiscountItems(discountRef));
        if (discountItemCountModels.size() > 0) {
            ChoicePrizesDialog dialog = new ChoicePrizesDialog();
            BigDecimal totalQty = OrderPrizeManager.totalQtyOrderPrize(customerId, discountRef, callOrderId);
            if (totalQty == null) totalQty = BigDecimal.ZERO;
            dialog.setDiscountId(discountRef, customerId, totalQty, callOrderId);
            dialog.setTargetFragment(CustomerOrderPreviewFragment.this, 0);
            dialog.show(getActivity().getSupportFragmentManager(), "ChoicePrizesDialog");
        }
    }

    private void showPrizeDialog() {
        if (loopCount == (orderPrize.size() - 1))
            last = true;
        ChoicePrizesDialog dialog = new ChoicePrizesDialog();
        dialog.setDiscountId(orderPrize.get(loopCount).discountRef, customerId, new BigDecimal(orderPrize.get(loopCount).qty), callOrderId);
        dialog.setTargetFragment(CustomerOrderPreviewFragment.this, 0);
        dialog.setProducts(orderPrize.get(loopCount).orderPrizeList);
        dialog.show(getActivity().getSupportFragmentManager(), "ChoicePrizesDialog");
        loopCount++;
    }

    /**
     * Discount = sum(Line.Dis1);
     * dis1 for zar
     */
    // todo
    // NGT-4226 - refresh discount after choice prize (we may choice a prize a with different price. it is not a good solution and it should be changed later)
    public void refresh() {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.ThirdParty && VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                Currency total = Currency.ZERO;
                Currency totalDiscount = Currency.ZERO;
                for (final CustomerCallOrderLinePromotion p :
                        customerCallOrderPromotion.LinesWithPromo) {
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
                        CustomerCallOrderLinePromotion baseLine = Linq.findFirst(customerCallOrderPromotion.Lines, new Linq.Criteria<CustomerCallOrderLinePromotion>() {
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
                orderCostPairedItems.setValue(HelperMethods.currencyToString(total.add(customerCallOrderPromotion.TotalInvoiceAdd)));
                discountAmountPairedItems.setValue(HelperMethods.currencyToString(totalDiscount));
            } else {
                Currency totalPriceWithPromo = Currency.ZERO;
                Currency totalInvoiceDiscount = Currency.ZERO;
                for (CustomerCallOrderLinePromotion item : customerCallOrderPromotion.LinesWithPromo) {
                    totalPriceWithPromo = totalPriceWithPromo.add(item.InvoiceAmount);
                    totalInvoiceDiscount = totalInvoiceDiscount.add((item.InvoiceDis1 == null ? new Currency(BigDecimal.ZERO) : item.InvoiceDis1).add(item.InvoiceDis2 == null ? new Currency(BigDecimal.ZERO) : item.InvoiceDis2).add(item.InvoiceDis3 == null ? new Currency(BigDecimal.ZERO) : item.InvoiceDis3).add(item.InvoiceDisOther == null ? new Currency(BigDecimal.ZERO) : item.InvoiceDisOther));
                }
                customerCallOrderPromotion.TotalInvoiceDiscount = totalInvoiceDiscount;
                customerCallOrderPromotion.TotalPriceWithPromo = totalPriceWithPromo;
                discountAmountPairedItems.setValue(HelperMethods.currencyToString(customerCallOrderPromotion.TotalInvoiceDiscount));

                // change value of this pairedItem with chequeDiscount
//                orderCostPairedItems.setValue(HelperMethods.currencyToString(customerCallOrderPromotion.TotalPriceWithPromo.add(customerCallOrderPromotion.TotalInvoiceAdd).subtract(customerCallOrderPromotion.TotalInvoiceDiscount)));
                payableCashPairedItems.setValue(customerCallOrderPromotion.TotalAmountNutCash.toString());
                payableChequePairedItems.setValue(customerCallOrderPromotion.TotalAmountNutCheque.toString());
                payableImmediatePairedItems.setValue(customerCallOrderPromotion.TotalAmountNutImmediate.toString());

                discountImmediatePairedItems.setValue(customerCallOrderPromotion.TotalAmountNutCheque
                        .subtract(customerCallOrderPromotion.TotalAmountNutImmediate).toString());
                orderCostPairedItems.setValue(customerCallOrderPromotion.TotalDiscont.toString());
            }

            addAmountPairedItems.setValue(HelperMethods.currencyToString(customerCallOrderPromotion.TotalInvoiceAdd));

        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
    }
}
