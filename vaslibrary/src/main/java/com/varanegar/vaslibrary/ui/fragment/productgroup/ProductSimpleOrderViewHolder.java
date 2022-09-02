package com.varanegar.vaslibrary.ui.fragment.productgroup;

import android.os.Handler;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductInventoryManager;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.customerpricemanager.CustomerPriceManager;
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
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.dialog.PriceEditorDialog;
import com.varanegar.vaslibrary.ui.dialog.ProductOrderInfoDialog;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 11/5/2017.
 */
public class ProductSimpleOrderViewHolder extends BaseViewHolder<ProductOrderViewModel> {

    private final TextView rowTextView;
    private final TextView productCodeTextView;
    private final TextView productNameTextView;
    private final TextView productPayDurationTextView;
    private final PairedItems pricePairedItems;
    private final PairedItems totalPricePairedItems;
    private final TextView totalOrderQtyTextView;
    private final ImageView emphasisImageView;
    private final TextView equalsTextView;
    private final AppCompatActivity activity;
    private final UUID callOrderId;


    private final HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnitHashMap;
    private final View largeUnitLayout;
    private final View smallUnitLayout;
    private final ImageView moreInfoImageView;
    private final PairedItems customerInventoryPairedItems;
    private final PairedItems inventoryPairedItems;
    private final SysConfigModel customerStockCheckType;
    private final SysConfigModel checkCustomerStock;
    private final SysConfigModel orderPointCheckType;
    private final SysConfigModel stockLevel;
    private final Handler handler;
    private long lastClick = 0;
    private final int delay = 200;
    private final UUID orderTypeId;
    OnItemQtyChangedHandler onItemQtyChangedHandler;
    HashMap<UUID, ProductUnitsViewModel> productUnitsHashMap;

    //    private OnItemClickedListener onItemClickedListener;
    private DiscreteUnit largeUnit;
    private DiscreteUnit smallUnit;

    public interface OnItemClickedListener {

        void run(int position, DiscreteUnit selected);
    }


    OnPriceChanged onPriceChanged;

    public interface OnPriceChanged {
        void run();
    }

    private final ImageButton plusImageViewSmall;
    private final ImageButton minusImageViewSmall;
    private final ImageButton plusImageViewLarge;
    private final ImageButton minusImageViewLarge;
    private final TextView qtyTextViewSmall;
    private final TextView unitTextViewSmall;
    private final TextView qtyTextViewLarge;
    private final TextView unitTextViewLarge;
    private final LinearLayout header_layout;
    public ProductSimpleOrderViewHolder(View itemView,
                                        SysConfigModel showStockLevel,
                                        SysConfigModel orderPointCheckType,
                                        SysConfigModel customerStockCheckType,
                                        SysConfigModel checkCustomerStock,
                                        HashMap<UUID, ProductUnitsViewModel> productUnitsHashMap,
                                        BaseRecyclerAdapter<ProductOrderViewModel> recyclerAdapter,
                                        UUID callOrderId,
                                        HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnitHashMap,
                                        AppCompatActivity activity,
                                        OnItemQtyChangedHandler onItemQtyChangedHandler,
                                        OnPriceChanged onPriceChanged,
                                        UUID orderTypeId) {
        super(itemView, recyclerAdapter, activity);
        this.stockLevel = showStockLevel;
        this.orderPointCheckType = orderPointCheckType;
        this.customerStockCheckType = customerStockCheckType;
        this.checkCustomerStock = checkCustomerStock;
        this.productUnitsHashMap = productUnitsHashMap;
        this.activity = activity;
        this.productUnitHashMap = productUnitHashMap;
//        this.onItemClickedListener = onItemClickedListener;
        this.onPriceChanged = onPriceChanged;
        this.orderTypeId = orderTypeId;
        rowTextView = (TextView) itemView.findViewById(R.id.row_text_view);
        productCodeTextView = (TextView) itemView.findViewById(R.id.product_code_text_view);
        productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
        productPayDurationTextView = (TextView) itemView.findViewById(R.id.product_pay_duration_text_view);
        pricePairedItems = itemView.findViewById(R.id.price_paired_items);
        totalPricePairedItems = itemView.findViewById(R.id.total_price_paired_items);
        totalOrderQtyTextView = (TextView) itemView.findViewById(R.id.total_order_qty_text_view);
        emphasisImageView = (ImageView) itemView.findViewById(R.id.emphasis_image_view);
        equalsTextView = (TextView) itemView.findViewById(R.id.equals_text_view);

        plusImageViewSmall = itemView.findViewById(R.id.plus_image_view_small);
        minusImageViewSmall = itemView.findViewById(R.id.minus_image_view_small);

        plusImageViewLarge = itemView.findViewById(R.id.plus_image_view_large);
        minusImageViewLarge = itemView.findViewById(R.id.minus_image_view_large);

        qtyTextViewSmall = itemView.findViewById(R.id.qty_text_view_small);
        unitTextViewSmall = itemView.findViewById(R.id.unit_text_view_small);

        qtyTextViewLarge = itemView.findViewById(R.id.qty_text_view_large);
        unitTextViewLarge = itemView.findViewById(R.id.unit_text_view_large);

        largeUnitLayout = itemView.findViewById(R.id.large_unit_layout);
        smallUnitLayout = itemView.findViewById(R.id.small_unit_layout);

        moreInfoImageView = itemView.findViewById(R.id.more_info_image_view);
        customerInventoryPairedItems = itemView.findViewById(R.id.customer_inventory_paired_items);
        inventoryPairedItems = itemView.findViewById(R.id.inventory_paired_items);
        header_layout= itemView.findViewById(R.id.header_layout);

        this.callOrderId = callOrderId;
        this.onItemQtyChangedHandler = onItemQtyChangedHandler;
        this.handler = new Handler();
    }

    @Override
    public void bindView(final int position) {

        itemView.setOnClickListener(v -> {
            itemView.setEnabled(false);
            recyclerAdapter.showContextMenu(getAdapterPosition());
            new Handler().postDelayed(() -> itemView.setEnabled(true), 2000);
        });

        final ProductOrderViewModel productOrderViewModel = recyclerAdapter.get(position);

        if (productOrderViewModel == null)
            return;

        pricePairedItems.setValue(HelperMethods.currencyToString(productOrderViewModel.Price));


        if (productOrderViewModel.ProductTotalOrderedQty == null)
            productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
        if (productOrderViewModel.TotalQty == null)
            productOrderViewModel.TotalQty = BigDecimal.ZERO;
        if (productOrderViewModel.Price == null)
            productOrderViewModel.Price = Currency.ZERO;


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

        rowTextView.setText(String.valueOf(position + 1));
        productCodeTextView.setText(Html.fromHtml(productCode));
        if (productOrderViewModel.PayDuration > 0) {
            productPayDurationTextView.setVisibility(View.VISIBLE);
            productPayDurationTextView.setText(getContext().getString(R.string.pay_duration) + productOrderViewModel.PayDuration);
        }

        createUnits(productOrderViewModel, position);

        double totalQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.TotalQty);

        if (productOrderViewModel.TotalQty.compareTo(BigDecimal.ZERO) == 0) {
            totalOrderQtyTextView.setVisibility(View.INVISIBLE);
            equalsTextView.setVisibility(View.INVISIBLE);
            totalPricePairedItems.setValue(HelperMethods.currencyToString(Currency.ZERO));
        } else {
            totalOrderQtyTextView.setVisibility(View.VISIBLE);
            equalsTextView.setVisibility(View.VISIBLE);
            totalOrderQtyTextView.setText(String.valueOf(totalQty));
            totalPricePairedItems.setValue(HelperMethods.currencyToString(productOrderViewModel.Price.multiply(productOrderViewModel.TotalQty)));
        }

        if (orderTypeId.toString().equals("4cc866f9-eb19-4c76-8a41-f8207104cdbf") && VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
            pricePairedItems.setOnClickListener(view -> {
                PriceEditorDialog priceEditor = new PriceEditorDialog();
                priceEditor.setInitialValue(productOrderViewModel.Price);
                priceEditor.setPriceChangedListener(value -> {
                    try {
                        CustomerPriceManager customerPriceManager = new CustomerPriceManager(getContext());
                        customerPriceManager.saveCustomPrice(productOrderViewModel.CustomerId, callOrderId, productOrderViewModel.UniqueId, value, null, null);
                        productOrderViewModel.Price = value;
                        recyclerAdapter.notifyItemChanged(position);
                        onPriceChanged.run();
                    } catch (Exception e) {
                        Timber.e(e);
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setMessage(R.string.error_saving_request);
                        dialog.setTitle(R.string.error);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                });
                priceEditor.show(activity.getSupportFragmentManager(), "PriceEditorDialog");
            });
        }

        if (productOrderViewModel.EmphaticType == EmphasisType.NotEmphatic) {
            rowTextView.setTextColor(activity.getResources().getColor(R.color.black));
            productCodeTextView.setTextColor(activity.getResources().getColor(R.color.black));
            productNameTextView.setTextColor(activity.getResources().getColor(R.color.black));
            productNameTextView.setText(Html.fromHtml(productName));
        } else {

            EmphaticProductCountModel temp = new EmphaticProductCountManager(getContext())
                    .getItem(productOrderViewModel.UniqueId);
            EmphaticProductManager emphaticProductManager = new EmphaticProductManager(getContext());
            EmphaticProductModel emphaticProductModel = emphaticProductManager.getItemByRoleId(temp.RuleId);
            if (!emphaticProductModel.IsEmphasis) {
                productNameTextView.setText(Html.fromHtml(productName + " ( " + activity.getString(R.string.emphatic_count) + HelperMethods.bigDecimalToString(emphaticProductModel.PackageCount) + " )"));
            }else {
                productNameTextView.setText(Html.fromHtml(productName + " ( " + activity.getString(R.string.NOemphatic_count) + " )"));
                header_layout.setBackgroundColor(activity.getResources().getColor(R.color.no_emphatic));
            }


//            productNameTextView.setText(Html.fromHtml(productName + " ( " + activity.getString(R.string.emphatic_count) + HelperMethods.bigDecimalToString(productOrderViewModel.EmphaticProductCount) + " )"));
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
                emphasisImageView.setVisibility(View.GONE);
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

        if (inventoryPairedItems != null) {
            Pair<String, Integer> p = OnHandQtyManager.showInventoryQty(getContext(), onHandQtyStock, stockLevel, orderPointCheckType);
            if (p != null) {
                inventoryPairedItems.setValue(p.first);
                inventoryPairedItems.setValueColor(p.second);
            } else
                inventoryPairedItems.setVisibility(View.GONE);
        }

        if (customerInventoryPairedItems != null) {
            if (SysConfigManager.compare(checkCustomerStock, true)) {
                if (SysConfigManager.compare(customerStockCheckType, ProductInventoryManager.CustomerStockCheckType.Count)) {
                    double customerStockLevel = HelperMethods.bigDecimalToDouble(productOrderViewModel.CustomerInventoryTotalQty);
                    if (customerStockLevel > 0) {
                        customerInventoryPairedItems.setValue(HelperMethods.doubleToString(customerStockLevel));
                        customerInventoryPairedItems.setValueColor(R.color.green);
                    } else {
                        customerInventoryPairedItems.setValue(getContext().getString(R.string.multiplication_sign));
                        customerInventoryPairedItems.setValueColor(R.color.red);
                    }
                } else {
                    if (productOrderViewModel.CustomerInventoryIsAvailable) {
                        customerInventoryPairedItems.setValue(getContext().getString(R.string.check_sign));
                        customerInventoryPairedItems.setValueColor(R.color.green);
                    } else {
                        customerInventoryPairedItems.setValue(getContext().getString(R.string.multiplication_sign));
                        customerInventoryPairedItems.setValueColor(R.color.red);
                    }
                }
            } else {
                customerInventoryPairedItems.setVisibility(View.GONE);
            }
        }

        moreInfoImageView.setOnClickListener(v -> {
            ProductOrderInfoDialog dialog = new ProductOrderInfoDialog();
            dialog.setArguments(productOrderViewModel.CustomerId, productOrderViewModel.UniqueId, callOrderId);
            dialog.show(activity.getSupportFragmentManager(), "ProductInfoDialog");
        });

    }

    private void createUnits(final ProductOrderViewModel productOrderViewModel, final int position) {

        smallUnit = null;
        largeUnit = null;
        String[] allQtys = null;
        if (productOrderViewModel.Qty != null)
            allQtys = productOrderViewModel.Qty.split(":");


        String[] productUnitIds = null;
        if (productOrderViewModel.ProductUnitId != null)
            productUnitIds = productOrderViewModel.ProductUnitId.split(":");

        ProductUnitViewManager.ProductUnits pu = productUnitHashMap.get(productOrderViewModel.UniqueId);
        if (pu == null)
            return;

        if (pu.LargeUnit != null) {
            largeUnit = new DiscreteUnit();
            largeUnitLayout.setVisibility(View.VISIBLE);
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
        } else
            largeUnitLayout.setVisibility(View.GONE);

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

        plusImageViewSmall.setOnClickListener(view -> addItem(smallUnit, largeUnit, qtyTextViewSmall, productOrderViewModel, position));

        plusImageViewLarge.setOnClickListener(view -> addItem(largeUnit, smallUnit, qtyTextViewLarge, productOrderViewModel, position));

        minusImageViewSmall.setOnClickListener(view -> removeItem(smallUnit, largeUnit, qtyTextViewSmall, productOrderViewModel, position));

        minusImageViewLarge.setOnClickListener(view -> removeItem(largeUnit, smallUnit, qtyTextViewLarge, productOrderViewModel, position));

        minusImageViewSmall.setOnLongClickListener(view -> {
            deleteItem(smallUnit, largeUnit, qtyTextViewSmall, productOrderViewModel, position);
            return true;
        });

        minusImageViewLarge.setOnLongClickListener(view -> {
            deleteItem(largeUnit, smallUnit, qtyTextViewLarge, productOrderViewModel, position);
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

    private synchronized void removeItem(@Nullable DiscreteUnit unit, @Nullable DiscreteUnit otherUnit, TextView qtyTextView, final ProductOrderViewModel productOrderViewModel, int position) {
        if (unit != null && unit.value > 0) {
            unit.value--;
            qtyTextView.setText(HelperMethods.doubleToString(unit.value));
            lastClick = new Date().getTime();
            onItemQtyChangedHandler.minusQty(position, productOrderViewModel.UniqueId, unit, otherUnit);
            handler.postDelayed(() -> {
                if (new Date().getTime() - lastClick < delay)
                    return;
                if (onItemQtyChangedHandler != null)
                    onItemQtyChangedHandler.start(productOrderViewModel);
            }, delay + 50);
        }
    }

    private synchronized void deleteItem(@Nullable DiscreteUnit unit,
                                         @Nullable DiscreteUnit otherUnit,
                                         TextView qtyTextView,
                                         ProductOrderViewModel productOrderViewModel,
                                         int position) {
        if (unit != null && unit.value > 0) {
            unit.value = 0;
            qtyTextView.setText(HelperMethods.doubleToString(unit.value));
            onItemQtyChangedHandler.minusQty(position, productOrderViewModel.UniqueId, unit, otherUnit);
            if (onItemQtyChangedHandler != null)
                onItemQtyChangedHandler.start(productOrderViewModel);
        }
    }

    private synchronized void addItem(@Nullable DiscreteUnit unit, @Nullable DiscreteUnit otherUnit,
                                      TextView qtyTextView, final ProductOrderViewModel productOrderViewModel, int position) {
        if (unit != null) {
            unit.value++;
            qtyTextView.setText(HelperMethods.doubleToString(unit.value));
            lastClick = new Date().getTime();
            onItemQtyChangedHandler.plusQty(position, productOrderViewModel.UniqueId, unit, otherUnit);
            handler.postDelayed(() -> {
                if (new Date().getTime() - lastClick < delay)
                    return;
                if (onItemQtyChangedHandler != null)
                    onItemQtyChangedHandler.start(productOrderViewModel);
            }, delay + 50);
        }
    }
}
