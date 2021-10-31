package com.varanegar.vaslibrary.ui.fragment.order;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CustomerOrderLineViewHolder extends BaseViewHolder<CustomerCallOrderOrderViewModel> {

    private final TextView valueTextView;
    private final ImageView iconImageView;
    private final TextView rowTextView;
    private final TextView productCodeTextView;
    private final TextView priceTextView;
    private final LinearLayout orderQtyLinearLayout;
    private final TextView totalOrderQtyTextView;
    TextView productNameTextView;

    public CustomerOrderLineViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallOrderOrderViewModel> recyclerAdapter, Context context) {
        super(itemView, recyclerAdapter, context);
        productNameTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.product_name_text_view);
        productCodeTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.product_code_text_view);
        priceTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.price_text_view);
        orderQtyLinearLayout = (LinearLayout) itemView.findViewById(com.varanegar.vaslibrary.R.id.order_qty_linear_layout);
        totalOrderQtyTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.total_order_qty_text_view);
        rowTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.row_text_view);
        iconImageView = (ImageView) itemView.findViewById(com.varanegar.vaslibrary.R.id.icon_image_view);
        valueTextView = (TextView) itemView.findViewById(com.varanegar.vaslibrary.R.id.order_value_text_view);

    }

    @Override
    public void bindView(final int position) {
        final CustomerCallOrderOrderViewModel product = recyclerAdapter.get(position);
        if (product == null)
            return;
        itemView.setOnClickListener(v -> {
            itemView.setEnabled(false);
            recyclerAdapter.showContextMenu(position);
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
            orderQtyLinearLayout.setVisibility(View.VISIBLE);
            List<BaseUnit> units = new ArrayList<>();
            String[] unitNames = product.UnitName.split(":");
            String[] strUnits = product.Qty.split(":");
            for (int i = 0; i < strUnits.length; i++) {
                String strUnit = strUnits[i];
                BaseUnit unit = new BaseUnit();
                unit.value = Double.parseDouble(strUnit);
                unit.Name = unitNames[i];
                if (unit.value > 0)
                    units.add(unit);
            }
            QtyView qtyView = new QtyView().build(orderQtyLinearLayout, units);
            BigDecimal remained = product.OnHandQty.subtract(product.ProductTotalOrderedQty);
            if (product.OnHandQty.compareTo(product.OrderPoint) <= 0)
                qtyView.setColor(QtyView.Color.Red);
            else if (remained.compareTo(product.OrderPoint) < 0)
                qtyView.setColor(QtyView.Color.Orange);
            else
                qtyView.setColor(QtyView.Color.Green);
        } else
            orderQtyLinearLayout.setVisibility(View.GONE);


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
    }
}
