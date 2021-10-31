package com.varanegar.vaslibrary.ui.fragment.order;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class SimpleCustomerOrderLineViewHolder extends BaseViewHolder<CustomerCallOrderOrderViewModel> {

    private final TextView valueTextView;
    private final TextView valueTitleTextView;
    private final ImageView iconImageView;
    private final TextView rowTextView;
    private final TextView productCodeTextView;
    private final TextView priceTextView;
    private final TextView totalOrderQtyTextView;
    private final TextView productNameTextView;

    private final View largeUnitLayout;
    //private final View smallUnitLayout;
    private final ImageButton plusImageViewSmall;
    private final ImageButton minusImageViewSmall;
    private final ImageButton plusImageViewLarge;
    private final ImageButton minusImageViewLarge;
    private final TextView qtyTextViewSmall;
    private final TextView unitTextViewSmall;
    private final TextView qtyTextViewLarge;
    private final TextView unitTextViewLarge;
    private final OrderAdapter orderAdapter;
    private DiscreteUnit largeUnit;
    private DiscreteUnit smallUnit;
    private final Handler handler;
    private long lastClick = 0;
    private final int delay = 200;

    public SimpleCustomerOrderLineViewHolder(View itemView, OrderAdapter recyclerAdapter, Context context) {
        super(itemView, recyclerAdapter, context);
        orderAdapter = (OrderAdapter) recyclerAdapter;
        productNameTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.product_name_text_view);
        productCodeTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.product_code_text_view);
        priceTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.price_text_view);
        totalOrderQtyTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.total_order_qty_text_view);
        rowTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.row_text_view);
        iconImageView = (ImageView) itemView.findViewById(com.varanegar.vaslibrary.R.id.icon_image_view);
        valueTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.order_value_text_view);
        valueTitleTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.order_value_title_text_view);

        plusImageViewSmall = itemView.findViewById(R.id.plus_image_view_small);
        minusImageViewSmall = itemView.findViewById(R.id.minus_image_view_small);

        plusImageViewLarge = itemView.findViewById(R.id.plus_image_view_large);
        minusImageViewLarge = itemView.findViewById(R.id.minus_image_view_large);

        qtyTextViewSmall = itemView.findViewById(R.id.qty_text_view_small);
        unitTextViewSmall = itemView.findViewById(R.id.unit_text_view_small);

        qtyTextViewLarge = itemView.findViewById(R.id.qty_text_view_large);
        unitTextViewLarge = itemView.findViewById(R.id.unit_text_view_large);

        largeUnitLayout = itemView.findViewById(R.id.large_unit_layout);
//            smallUnitLayout = itemView.findViewById(R.id.small_unit_layout);

        this.handler = new Handler();
    }

    @Override
    public void bindView(int position) {
        final CustomerCallOrderOrderViewModel product = recyclerAdapter.get(position);
        if (product == null)
            return;
        itemView.setOnClickListener(v -> {
            itemView.setEnabled(false);
            recyclerAdapter.showContextMenu(getAdapterPosition());
            new Handler().postDelayed(() -> itemView.setEnabled(true), 2000);
        });
        productNameTextView.setText(product.ProductName);
        productCodeTextView.setText(product.ProductCode);

        if (product.OnHandQty == null)
            product.OnHandQty = BigDecimal.ZERO;
        if (product.RemainedAfterReservedQty == null)
            product.RemainedAfterReservedQty = BigDecimal.ZERO;
        if (product.ProductTotalOrderedQty == null)
            product.ProductTotalOrderedQty = BigDecimal.ZERO;
        if (product.TotalQty == null)
            product.TotalQty = BigDecimal.ZERO;
        if (product.UnitPrice == null)
            product.UnitPrice = Currency.ZERO;
        if (product.OrderPoint == null)
            product.OrderPoint = BigDecimal.ZERO;

        if (product.RequestAmount == null)
            product.RequestAmount = Currency.ZERO;
        if (product.RequestDis1Amount == null)
            product.RequestDis1Amount = Currency.ZERO;
        if (product.RequestDis2Amount == null)
            product.RequestDis2Amount = Currency.ZERO;
        if (product.RequestDis3Amount == null)
            product.RequestDis3Amount = Currency.ZERO;
        if (product.RequestOtherDiscountAmount == null)
            product.RequestOtherDiscountAmount = Currency.ZERO;

        if (product.RequestAdd1Amount == null)
            product.RequestAdd1Amount = Currency.ZERO;
        if (product.RequestAdd2Amount == null)
            product.RequestAdd2Amount = Currency.ZERO;
        if (product.RequestAddOtherAmount == null)
            product.RequestAddOtherAmount = Currency.ZERO;
        if (product.RequestChargeAmount == null)
            product.RequestChargeAmount = Currency.ZERO;
        if (product.RequestTaxAmount == null)
            product.RequestTaxAmount = Currency.ZERO;

        if (product.IsRequestFreeItem)
            priceTextView.setText(product.FreeReasonName);
        else if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && product.IsPromoLine)
            priceTextView.setText(HelperMethods.currencyToString(product.PromotionUnitPrice));
        else
            priceTextView.setText(HelperMethods.currencyToString(product.UnitPrice));
        totalOrderQtyTextView.setText(HelperMethods.bigDecimalToString(product.TotalQty));
        rowTextView.setText(String.valueOf(position + 1));

        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (product.IsPromoLine && product.OriginalTotalQty == null)
                valueTextView.setText("--");
            else {
                double totalQty = product.TotalQty == null ? 0 : product.TotalQty.doubleValue();
                double originalQty = product.OriginalTotalQty == null ? 0 : product.OriginalTotalQty.doubleValue();
                valueTextView.setText(HelperMethods.doubleToString(originalQty - totalQty));
            }
        } else {
            if (valueTitleTextView != null)
                valueTitleTextView.setText(R.string.amount);
            if (product.IsPromoLine) {
                if (product.PromotionPrice == null || Currency.ZERO.compareTo(product.PromotionPrice) == 0)
                    valueTextView.setText(getContext().getString(R.string.multiplication_sign));
                else
                    valueTextView.setText(HelperMethods.currencyToString(product.PromotionPrice));
            } else {
                Currency totalPrice = product.RequestAmount
                        .subtract(product.RequestDis1Amount)
                        .subtract(product.RequestDis2Amount)
                        .subtract(product.RequestDis3Amount)
                        .subtract(product.RequestOtherDiscountAmount)
                        .add(product.RequestChargeAmount)
                        .add(product.RequestTaxAmount)
                        .add(product.RequestAdd1Amount)
                        .add(product.RequestAdd2Amount)
                        .add(product.RequestAddOtherAmount);
                valueTextView.setText(HelperMethods.currencyToString(totalPrice));
            }
        }

        if (product.TotalQty.compareTo(BigDecimal.ZERO) > 0) {
            String[] unitNames = product.UnitName.split(":");
            String[] strUnits = product.Qty.split(":");
            for (int i = 0; i < strUnits.length; i++) {
                String strUnit = strUnits[i];
                BaseUnit unit = new BaseUnit();
                unit.value = Double.parseDouble(strUnit);
                unit.Name = unitNames[i];
            }
            BigDecimal remained = product.OnHandQty.subtract(product.ProductTotalOrderedQty);
            if (product.OnHandQty.compareTo(product.OrderPoint) <= 0) {
                qtyTextViewSmall.setBackgroundResource(R.drawable.calculator_left_red_corner);
                qtyTextViewSmall.setTextColor(getResources().getColor(R.color.red));
                unitTextViewSmall.setBackgroundResource(R.drawable.calculator_right_red_corner);
                qtyTextViewLarge.setBackgroundResource(R.drawable.calculator_left_red_corner);
                qtyTextViewLarge.setTextColor(getResources().getColor(R.color.red));
                unitTextViewLarge.setBackgroundResource(R.drawable.calculator_right_red_corner);
            } else if (remained.compareTo(product.OrderPoint) < 0) {
                qtyTextViewSmall.setBackgroundResource(R.drawable.calculator_left_orange_corner);
                qtyTextViewSmall.setTextColor(getResources().getColor(R.color.orange));
                unitTextViewSmall.setBackgroundResource(R.drawable.calculator_right_orange_corner);
                qtyTextViewLarge.setBackgroundResource(R.drawable.calculator_left_orange_corner);
                qtyTextViewLarge.setTextColor(getResources().getColor(R.color.orange));
                unitTextViewLarge.setBackgroundResource(R.drawable.calculator_right_orange_corner);
            } else {
                qtyTextViewSmall.setBackgroundResource(R.drawable.calculator_left_green_corner);
                qtyTextViewSmall.setTextColor(getResources().getColor(R.color.green));
                unitTextViewSmall.setBackgroundResource(R.drawable.calculator_right_green_corner);
                qtyTextViewLarge.setBackgroundResource(R.drawable.calculator_left_green_corner);
                qtyTextViewLarge.setTextColor(getResources().getColor(R.color.green));
                unitTextViewLarge.setBackgroundResource(R.drawable.calculator_right_green_corner);
            }
        }


        if (product.EmphaticProductCount != null) {
            iconImageView.setVisibility(View.VISIBLE);
            iconImageView.setImageResource(com.varanegar.vaslibrary.R.drawable.ic_pin_orange_24dp);
        } else if (product.IsRequestFreeItem) {
            iconImageView.setVisibility(View.VISIBLE);
            iconImageView.setImageResource(com.varanegar.vaslibrary.R.drawable.ic_gift_teal_24dp);
        } else if (product.IsPromoLine) {
            iconImageView.setVisibility(View.VISIBLE);
            iconImageView.setImageResource(R.drawable.ic_prize);
        } else
            iconImageView.setVisibility(View.INVISIBLE);

        createUnits(product, position);

    }

    private Resources getResources() {
        return getContext().getResources();
    }

    private void createUnits(final CustomerCallOrderOrderViewModel orderLine, final int position) {

        String[] allQtys = null;
        if (orderLine.Qty != null)
            allQtys = orderLine.Qty.split(":");


        String[] productUnitIds = null;
        if (orderLine.ProductUnitId != null)
            productUnitIds = orderLine.ProductUnitId.split(":");

        ProductUnitViewManager.ProductUnits pu = orderAdapter.getProductUnits().get(orderLine.ProductId);


        if (pu.LargeUnit != null) {
            largeUnitLayout.setVisibility(View.VISIBLE);
            largeUnit = new DiscreteUnit();
            largeUnit.ProductUnitId = pu.LargeUnit.UniqueId;
            largeUnit.Name = pu.LargeUnit.UnitName;
            largeUnit.ConvertFactor = pu.LargeUnit.ConvertFactor;
            if (productUnitIds != null && allQtys != null)
                for (int i = 0; i < productUnitIds.length; i++) {
                    UUID productUnitId = UUID.fromString(productUnitIds[i]);
                    if (productUnitId.equals(largeUnit.ProductUnitId)) {
                        largeUnit.value = Double.parseDouble(allQtys[i]);
                    }
                }
        } else {
            largeUnit = null;
            largeUnitLayout.setVisibility(View.GONE);
        }

        smallUnit = new DiscreteUnit();
        smallUnit.ProductUnitId = pu.SmallUnit.UniqueId;
        smallUnit.Name = pu.SmallUnit.UnitName;
        smallUnit.ConvertFactor = pu.SmallUnit.ConvertFactor;
        if (productUnitIds != null && allQtys != null)
            for (int i = 0; i < productUnitIds.length; i++) {
                UUID productUnitId = UUID.fromString(productUnitIds[i]);
                if (productUnitId.equals(smallUnit.ProductUnitId)) {
                    smallUnit.value = Double.parseDouble(allQtys[i]);
                }
            }


        qtyTextViewSmall.setText(HelperMethods.doubleToString(smallUnit.value));
        if (largeUnit != null)
            qtyTextViewLarge.setText(HelperMethods.doubleToString(largeUnit.value));
        unitTextViewSmall.setText(smallUnit.Name);
        if (largeUnit != null)
            unitTextViewLarge.setText(largeUnit.Name);


        minusImageViewSmall.setEnabled(smallUnit.value > 0);
        if (largeUnit != null)
            minusImageViewLarge.setEnabled(largeUnit.value > 0);

        plusImageViewSmall.setOnClickListener(view -> {
            if (!orderLine.IsPromoLine && orderAdapter.isEnabled() && !orderAdapter.getCallOrderModel().IsInvoice)
                addItem(smallUnit, largeUnit, qtyTextViewSmall, orderLine, position);

        });

        plusImageViewLarge.setOnClickListener(view -> {
            if (!orderLine.IsPromoLine && orderAdapter.isEnabled() && !orderAdapter.getCallOrderModel().IsInvoice)
                addItem(largeUnit, smallUnit, qtyTextViewLarge, orderLine, position);

        });

        minusImageViewSmall.setOnClickListener(view -> {
            if (!orderLine.IsPromoLine && orderAdapter.isEnabled() && !orderAdapter.getCallOrderModel().IsInvoice)
                removeItem(smallUnit, largeUnit, qtyTextViewSmall, orderLine, position);
        });

        minusImageViewLarge.setOnClickListener(view -> {
            if (!orderLine.IsPromoLine && orderAdapter.isEnabled() && !orderAdapter.getCallOrderModel().IsInvoice)
                removeItem(largeUnit, smallUnit, qtyTextViewLarge, orderLine, position);
        });

        minusImageViewSmall.setOnLongClickListener(view -> {
            if (!orderLine.IsPromoLine && orderAdapter.isEnabled() && !orderAdapter.getCallOrderModel().IsInvoice)
                deleteItem(smallUnit, largeUnit, qtyTextViewSmall, orderLine, position);
            return true;
        });

        minusImageViewLarge.setOnLongClickListener(view -> {
            if (!orderLine.IsPromoLine && orderAdapter.isEnabled() && !orderAdapter.getCallOrderModel().IsInvoice)
                deleteItem(largeUnit, smallUnit, qtyTextViewLarge, orderLine, position);
            return true;
        });
//
//        unitTextViewSmall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onItemClickedListener.run(position, (DiscreteUnit) smallUnit);
//            }
//        });
//        qtyTextViewSmall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onItemClickedListener.run(position, (DiscreteUnit) smallUnit);
//            }
//        });
//
//        unitTextViewLarge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onItemClickedListener.run(position, (DiscreteUnit) largeUnit);
//            }
//        });
//        qtyTextViewLarge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onItemClickedListener.run(position, (DiscreteUnit) largeUnit);
//            }
//        });


    }

    private synchronized void removeItem(DiscreteUnit unit, DiscreteUnit otherUnit, TextView qtyTextView, final CustomerCallOrderOrderViewModel orderLine, int position) {
        if (unit.value > 0) {
            unit.value--;
            qtyTextView.setText(HelperMethods.doubleToString(unit.value));
            lastClick = new Date().getTime();
            orderAdapter.getOnItemQtyChangedHandler().minusQty(position, orderLine.ProductId, unit, otherUnit);
            handler.postDelayed(() -> {
                if (new Date().getTime() - lastClick < delay)
                    return;
                if (orderAdapter.getOnItemQtyChangedHandler() != null)
                    orderAdapter.getOnItemQtyChangedHandler().start(orderLine);
            }, delay + 50);
        }
    }

    private synchronized void deleteItem(DiscreteUnit unit, @Nullable DiscreteUnit otherUnit, TextView qtyTextView, CustomerCallOrderOrderViewModel orderLine, int position) {
        if (unit.value > 0) {
            unit.value = 0;
            qtyTextView.setText(HelperMethods.doubleToString(unit.value));
            orderAdapter.getOnItemQtyChangedHandler().minusQty(position, orderLine.ProductId, unit, otherUnit);
            if (orderAdapter.getOnItemQtyChangedHandler() != null)
                orderAdapter.getOnItemQtyChangedHandler().start(orderLine);
        }
    }

    private synchronized void addItem(DiscreteUnit unit, @Nullable DiscreteUnit otherUnit, TextView qtyTextView, final CustomerCallOrderOrderViewModel orderLine, int position) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            BigDecimal newQty = BigDecimal.valueOf((unit.value + 1) * unit.ConvertFactor);
            if (otherUnit != null)
                newQty = newQty.add(BigDecimal.valueOf(otherUnit.ConvertFactor * otherUnit.value));
            if (newQty.compareTo(orderLine.OriginalTotalQty) > 0)
                return;
        }
        unit.value++;
        qtyTextView.setText(HelperMethods.doubleToString(unit.value));
        lastClick = new Date().getTime();
        orderAdapter.getOnItemQtyChangedHandler().plusQty(position, orderLine.ProductId, unit, otherUnit);
        handler.postDelayed(() -> {
            if (new Date().getTime() - lastClick < delay)
                return;
            if (orderAdapter.getOnItemQtyChangedHandler() != null)
                orderAdapter.getOnItemQtyChangedHandler().start(orderLine);
        }, delay + 50);
    }

}



