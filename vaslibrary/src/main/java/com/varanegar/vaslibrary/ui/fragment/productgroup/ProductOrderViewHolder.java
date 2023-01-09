package com.varanegar.vaslibrary.ui.fragment.productgroup;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductInventoryManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphaticProductCountManager;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphaticProductManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.emphaticproduct.EmphaticProductModel;
import com.varanegar.vaslibrary.model.emphaticproductcount.EmphaticProductCountModel;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.dialog.ProductOrderInfoDialog;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/5/2017.
 */
public class ProductOrderViewHolder extends BaseViewHolder<ProductOrderViewModel> {

    private final TextView rowTextView;
    private final TextView productCodeTextView;
    private final TextView productNameTextView;
    private final TextView priceTextView;
    private final LinearLayout orderQtyLinearLayout;
    private final TextView totalOrderQtyTextView;
    private final ImageView emphasisImageView;
    private final ImageView batchImageView;
    private final TextView equalsTextView;
    private final TextView averageFactorTextView;
    private final TextView customerInventoryTextView;
    private final ImageView moreInfoImageView;
    private final ImageView prizeImageView;
    private final AppCompatActivity activity;
    private final LinearLayout qtyLayout;
    private final TextView totalAmountTextView;
    private final SysConfigModel stockLevel;
    private final SysConfigModel customerStockCheckType;
    private final TextView storeTextView;
    private final SysConfigModel checkCustomerStock;
    private final SysConfigModel orderPointCheckType;
    private final UUID callOrderId;
    private final HashMap<UUID, ProductUnitsViewModel> productUnitsHashMap;
    private final HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnitHashMap;
    private final TextView usanceDayTextView;
    private final LinearLayout usanceDayLayout;
    private final TextView productPayDurationTextView;
    private boolean showAverageFactor;


    public ProductOrderViewHolder(View itemView,
                                  BaseRecyclerAdapter<ProductOrderViewModel> recyclerAdapter,
                                  SysConfigModel showStockLevel,
                                  SysConfigModel orderPointCheckType,
                                  SysConfigModel customerStockCheckType,
                                  SysConfigModel checkCustomerStock,
                                  boolean showAverageFactor,
                                  UUID callOrderId,
                                  HashMap<UUID, ProductUnitsViewModel> productUnitsHashMap,
                                  HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnitHashMap,
                                  AppCompatActivity activity) {
        super(itemView, recyclerAdapter, activity);
        this.activity = activity;
        this.stockLevel = showStockLevel;
        this.orderPointCheckType = orderPointCheckType;
        this.showAverageFactor = showAverageFactor;
        this.customerStockCheckType = customerStockCheckType;
        this.checkCustomerStock = checkCustomerStock;
        this.productUnitsHashMap = productUnitsHashMap;
        this.productUnitHashMap = productUnitHashMap;
        rowTextView = (TextView) itemView.findViewById(R.id.row_text_view);
        productCodeTextView = (TextView) itemView.findViewById(R.id.product_code_text_view);
        productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
        priceTextView = (TextView) itemView.findViewById(R.id.price_text_view);
        orderQtyLinearLayout = (LinearLayout) itemView.findViewById(R.id.order_qty_linear_layout);
        totalOrderQtyTextView = (TextView) itemView.findViewById(R.id.total_order_qty_text_view);
        totalAmountTextView = (TextView) itemView.findViewById(R.id.total_amount_text_view);
        emphasisImageView = (ImageView) itemView.findViewById(R.id.emphasis_image_view);
        batchImageView = (ImageView) itemView.findViewById(R.id.batch_image_view);
        equalsTextView = (TextView) itemView.findViewById(R.id.equals_text_view);
        averageFactorTextView = (TextView) itemView.findViewById(R.id.average_factor_text_view);
        customerInventoryTextView = (TextView) itemView.findViewById(R.id.customer_inventory_text_view);
        moreInfoImageView = (ImageView) itemView.findViewById(R.id.more_info_image_view);
        prizeImageView = (ImageView) itemView.findViewById(R.id.prize_image_view);
        qtyLayout = (LinearLayout) itemView.findViewById(R.id.qty_layout);
        storeTextView = (TextView) itemView.findViewById(R.id.store_text_view);
        usanceDayLayout = itemView.findViewById(R.id.usance_day_layout);
        usanceDayTextView = itemView.findViewById(R.id.usance_day_text_view);
        productPayDurationTextView = (TextView) itemView.findViewById(R.id.product_pay_duration_text_view);
        this.callOrderId = callOrderId;
    }

    @Override
    public void bindView(final int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemView.setEnabled(false);
                recyclerAdapter.showContextMenu(getAdapterPosition());
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemView.setEnabled(true);
                    }
                }, 2000);
            }
        });
        final ProductOrderViewModel productOrderViewModel = recyclerAdapter.get(position);

        if (productOrderViewModel == null)
            return;


        if (productOrderViewModel.OnHandQty == null)
            productOrderViewModel.OnHandQty = BigDecimal.ZERO;
        if (productOrderViewModel.RemainedAfterReservedQty == null)
            productOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
        if (productOrderViewModel.ProductTotalOrderedQty == null)
            productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
        if (productOrderViewModel.TotalQty == null)
            productOrderViewModel.TotalQty = BigDecimal.ZERO;
        if (productOrderViewModel.Price == null)
            productOrderViewModel.Price = Currency.ZERO;
        if (productOrderViewModel.OrderPoint == null)
            productOrderViewModel.OrderPoint = BigDecimal.ZERO;

        priceTextView.setText(HelperMethods.currencyToString(productOrderViewModel.Price));
        String productName = productOrderViewModel.ProductName;
        if (ProductGroupFragment.splits != null)
            for (String split :
                    ProductGroupFragment.splits) {
                productName = productName.toLowerCase().replaceAll(split, "<font color='red'>" + split + "</font>");
            }

        String productCode = productOrderViewModel.ProductCode;
        if (ProductGroupFragment.splits != null)
            for (String split :
                    ProductGroupFragment.splits) {
                productCode = productCode.toLowerCase().replaceAll(split, "<font color='red'>" + split + "</font>");
            }


        if (productOrderViewModel.PayDuration > 0) {
            productPayDurationTextView.setVisibility(View.VISIBLE);
            productPayDurationTextView.setText(getContext().getString(R.string.pay_duration) + productOrderViewModel.PayDuration);
        }

        moreInfoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductOrderInfoDialog dialog = new ProductOrderInfoDialog();
                dialog.setArguments(productOrderViewModel.CustomerId, productOrderViewModel.UniqueId, callOrderId);
                dialog.show(activity.getSupportFragmentManager(), "ProductInfoDialog");
            }
        });
        if (productOrderViewModel.PrizeComment == null || productOrderViewModel.PrizeComment.isEmpty())
            prizeImageView.setVisibility(View.GONE);
        else
            prizeImageView.setVisibility(View.VISIBLE);
        double totalQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.TotalQty);
        rowTextView.setText(String.valueOf(position + 1));
        productCodeTextView.setText(Html.fromHtml(productCode));
        if (productOrderViewModel.TotalQty.compareTo(BigDecimal.ZERO) == 1) {
            String[] allQtys = productOrderViewModel.Qty.split("\\|");
            String[] allUnitNames = productOrderViewModel.UnitName.split("\\|");
            String[] unitNames = allUnitNames[0].split(":");
            List<BaseUnit> units = new ArrayList<>();
            for (int i = 0; i < unitNames.length; i++) {
                BaseUnit unit = new BaseUnit();
                unit.Name = unitNames[i];
                for (int j = 0; j < allQtys.length; j++) {
                    String[] reasonQtys = allQtys[j].split(":");
                    unit.value += Double.parseDouble(reasonQtys[i]);
                }
                if (unit.getQty().compareTo(BigDecimal.ZERO) == 1)
                    units.add(unit);
            }
            qtyLayout.setVisibility(View.VISIBLE);
            new QtyView().build(orderQtyLinearLayout, units);
        } else {
            orderQtyLinearLayout.removeAllViews();
            qtyLayout.setVisibility(View.GONE);
        }

        if (productOrderViewModel.TotalQty.compareTo(BigDecimal.ZERO) == 0 ||
                VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            totalOrderQtyTextView.setVisibility(View.INVISIBLE);
            equalsTextView.setVisibility(View.INVISIBLE);
            usanceDayLayout.setVisibility(View.GONE);
        } else {
            totalOrderQtyTextView.setVisibility(View.VISIBLE);
            equalsTextView.setVisibility(View.VISIBLE);
            usanceDayLayout.setVisibility(View.VISIBLE);
            DecimalFormat format = new DecimalFormat("0.#");
            totalOrderQtyTextView.setText(format.format(totalQty));
            totalAmountTextView.setText(HelperMethods.currencyToString(productOrderViewModel.Price.multiply(productOrderViewModel.TotalQty)));
            if (!productOrderViewModel.IsRequestFreeItem) {
                int usanceDay = productOrderViewModel.PayDuration;
                if (usanceDay == 1)
                    usanceDay = 0;
                Calendar newDate = Calendar.getInstance();
                newDate.add(Calendar.DAY_OF_YEAR, usanceDay);
                String date = DateHelper.toString(newDate.getTime(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
                usanceDayTextView.setText(date);
            } else
                usanceDayTextView.setText("-");
        }


        if (productOrderViewModel.EmphaticType == EmphasisType.NotEmphatic) {
            rowTextView.setTextColor(activity.getResources().getColor(R.color.black));
            productCodeTextView.setTextColor(activity.getResources().getColor(R.color.black));
            productNameTextView.setTextColor(activity.getResources().getColor(R.color.black));
            productNameTextView.setText(Html.fromHtml(productName));
        } else {
            if (productOrderViewModel.EmphaticProductCount == null)
                productOrderViewModel.EmphaticProductCount = BigDecimal.ZERO;
            if (productOrderViewModel.EmphaticProductCount.compareTo(BigDecimal.ZERO) == 0)
                productNameTextView.setText(Html.fromHtml(productName + " ( " + activity.getString(R.string.package_) + " )"));
            else {
                EmphaticProductCountModel temp = new EmphaticProductCountManager(getContext())
                        .getItem(productOrderViewModel.UniqueId);
                EmphaticProductManager emphaticProductManager = new EmphaticProductManager(getContext());
                EmphaticProductModel emphaticProductModel = emphaticProductManager.getItemByRoleId(temp.RuleId);
                if (!emphaticProductModel.IsEmphasis) {
                    productNameTextView.setText(Html.fromHtml(productName + " ( " + activity.getString(R.string.emphatic_count) + HelperMethods.bigDecimalToString(emphaticProductModel.PackageCount) + " )"));
                }else
                    productNameTextView.setText(Html.fromHtml(productName + " ( " + activity.getString(R.string.NOemphatic_count) + HelperMethods.bigDecimalToString(productOrderViewModel.EmphaticProductCount) + " )"));
            }

                if (productOrderViewModel.EmphaticType == EmphasisType.Deterrent) {
                productNameTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.red));
            } else if (productOrderViewModel.EmphaticType == EmphasisType.Warning) {
                productNameTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.orange));
            } else if (productOrderViewModel.EmphaticType == EmphasisType.Suggestion) {
                productNameTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.green));
            }
        }

        if (productOrderViewModel.EmphaticType == EmphasisType.NotEmphatic) {
            if (productOrderViewModel.IsRequestFreeItem)
                emphasisImageView.setImageResource(R.drawable.ic_gift_teal_24dp);
            else
                emphasisImageView.setVisibility(View.INVISIBLE);
        } else {
            emphasisImageView.setVisibility(View.VISIBLE);
            double wQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.WarningQty);
            double dQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.DangerQty);
            double qty = HelperMethods.bigDecimalToDouble(productOrderViewModel.EmphaticProductCount);
            if (wQty + totalQty >= qty)
                emphasisImageView.setImageResource(R.drawable.ic_star_green_900_24dp);
            else if (wQty + dQty + totalQty >= qty)
                emphasisImageView.setImageResource(R.drawable.ic_star_half_orange_900_24dp);
            else
                emphasisImageView.setImageResource(R.drawable.ic_star_border_red_900_24dp);
        }

        if (productOrderViewModel.BatchNo != null)
            batchImageView.setVisibility(View.VISIBLE);
        else
            batchImageView.setVisibility(View.INVISIBLE);


        if (showAverageFactor) {
            itemView.findViewById(R.id.average_factor_layout).setVisibility(View.VISIBLE);
            averageFactorTextView.setText(String.valueOf(productOrderViewModel.Average));
        } else itemView.findViewById(R.id.average_factor_layout).setVisibility(View.GONE);

        OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
        ProductUnitsViewModel productUnitsViewModel = productUnitsHashMap.get(productOrderViewModel.UniqueId);
        onHandQtyStock.ConvertFactors = productUnitsViewModel.ConvertFactor;
        onHandQtyStock.UnitNames = productUnitsViewModel.UnitName;
        if (productOrderViewModel.OnHandQty == null)
            productOrderViewModel.OnHandQty = BigDecimal.ZERO;
        onHandQtyStock.OnHandQty = productOrderViewModel.OnHandQty;
        if (productOrderViewModel.RemainedAfterReservedQty == null)
            productOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
        onHandQtyStock.RemainedAfterReservedQty = productOrderViewModel.RemainedAfterReservedQty;
        if (productOrderViewModel.OrderPoint == null)
            productOrderViewModel.OrderPoint = BigDecimal.ZERO;
        onHandQtyStock.OrderPoint = productOrderViewModel.OrderPoint;
        if (productOrderViewModel.ProductTotalOrderedQty == null)
            productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
        onHandQtyStock.ProductTotalOrderedQty = productOrderViewModel.ProductTotalOrderedQty;
        if (productOrderViewModel.RequestBulkQty == null)
            onHandQtyStock.TotalQty = productOrderViewModel.TotalQty == null ? BigDecimal.ZERO : productOrderViewModel.TotalQty;
        else
            onHandQtyStock.TotalQty = productOrderViewModel.TotalQtyBulk == null ? BigDecimal.ZERO : productOrderViewModel.TotalQtyBulk;
        onHandQtyStock.HasAllocation = productOrderViewModel.HasAllocation;
        Pair<String, Integer> p = OnHandQtyManager.showInventoryQty(getContext(), onHandQtyStock, stockLevel, orderPointCheckType);
        if (p != null) {
            storeTextView.setText(p.first);
            storeTextView.setTextColor(p.second);
        } else
            storeTextView.setVisibility(View.GONE);

        if (SysConfigManager.compare(checkCustomerStock, true)) {
            if (SysConfigManager.compare(customerStockCheckType, ProductInventoryManager.CustomerStockCheckType.Count)) {
                double customerStockLevel = HelperMethods.bigDecimalToDouble(productOrderViewModel.CustomerInventoryTotalQty);
                if (customerStockLevel > 0) {
                    customerInventoryTextView.setText(HelperMethods.doubleToString(customerStockLevel));
                    customerInventoryTextView.setTextColor(activity.getResources().getColor(R.color.green));
                } else {
                    customerInventoryTextView.setText(R.string.multiplication_sign);
                    customerInventoryTextView.setTextColor(activity.getResources().getColor(R.color.red));
                }
            } else {
                if (productOrderViewModel.CustomerInventoryIsAvailable) {
                    customerInventoryTextView.setText(R.string.check_sign);
                    customerInventoryTextView.setTextColor(activity.getResources().getColor(R.color.green));
                } else {
                    customerInventoryTextView.setText(R.string.multiplication_sign);
                    customerInventoryTextView.setTextColor(activity.getResources().getColor(R.color.red));
                }
            }
        } else {
            itemView.findViewById(R.id.customer_inventory_layout).setVisibility(View.GONE);
        }

    }

}
