package com.varanegar.vaslibrary.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductBatchOnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductInventoryManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.productbatchonhandqtymodel.ProductBatchOnHandQtyModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/18/2017.
 */

public class ProductOrderInfoDialog extends CuteDialogWithToolbar {
    private PairedItems totalRequestQtyPairedItems;
    private PairedItems productTypePairedItems;
    private PairedItems pricePairedItems;
    private PairedItems totalPricePairedItems;
    private PairedItems userPricePairedItems;
    private PairedItems stockLevelPairedItems;
    private PairedItems customerInventoryPairedItems;
    private PairedItems totalOrderedQtyPairedItems;
    private PairedItems averagePairedItems;
    private PairedItems invoiceCountItems;
    private PairedItems prizePairedItems;
    private ImageView productImageView;
    private BaseRecyclerView batchListRecyclerView;
    BaseRecyclerAdapter<ProductBatchOnHandQtyModel> adapter;


    public ProductOrderInfoDialog() {
        setSizingPolicy(SizingPolicy.Maximum);
    }

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_product_order_view, container, false);
        totalRequestQtyPairedItems = (PairedItems) view.findViewById(R.id.total_request_qty_paired_items);
        productTypePairedItems = (PairedItems) view.findViewById(R.id.product_type_paired_items);
        pricePairedItems = (PairedItems) view.findViewById(R.id.price_paired_items);
        totalPricePairedItems = (PairedItems) view.findViewById(R.id.total_price_paired_items);
        userPricePairedItems = (PairedItems) view.findViewById(R.id.user_price_paired_items);
        stockLevelPairedItems = (PairedItems) view.findViewById(R.id.stock_level_paired_items);
        customerInventoryPairedItems = (PairedItems) view.findViewById(R.id.customer_inventory_paired_items);
        totalOrderedQtyPairedItems = (PairedItems) view.findViewById(R.id.total_ordered_qty_paired_items);
        averagePairedItems = (PairedItems) view.findViewById(R.id.average_paired_items);
        invoiceCountItems = (PairedItems) view.findViewById(R.id.invoice_count_items);
        prizePairedItems = (PairedItems) view.findViewById(R.id.prize_paired_items);
        productImageView = (ImageView) view.findViewById(R.id.product_image_view);
        batchListRecyclerView = (BaseRecyclerView) view.findViewById(R.id.batch_list_recycler);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        UUID customerId = UUID.fromString(getArguments().getString("5503855d-a6af-4bda-b68c-928179b1ca93"));
        UUID productId = UUID.fromString(getArguments().getString("817458fc-fdd2-42f9-90bd-d0fd5cbf3edb"));
        UUID orderId = UUID.fromString(getArguments().getString("381c803d-1e5c-4249-9823-4859bed5d9c2"));
        ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());
        ProductOrderViewModel productOrderViewModel = productOrderViewManager.getLine(customerId, orderId, productId, false);
        double totalQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.TotalQty);
        double onHandQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.OnHandQty);
        if (productOrderViewModel.OnHandQty == null)
            productOrderViewModel.OnHandQty = BigDecimal.ZERO;
        setTitle(productOrderViewModel.ProductName + " " + getString(R.string.product_code_label) + " " + productOrderViewModel.ProductCode);
        totalRequestQtyPairedItems.setValue(HelperMethods.bigDecimalToString(productOrderViewModel.TotalQty));
        if (productOrderViewModel.IsRequestFreeItem) {
            productTypePairedItems.setValue(getString(R.string.free));
        } else if (productOrderViewModel.EmphaticType == EmphasisType.NotEmphatic) {
            productTypePairedItems.setValue(getString(R.string.normal));
        } else {
            if (productOrderViewModel.EmphaticType == EmphasisType.Deterrent) {
                productTypePairedItems.setValue(getString(R.string.emphatic_detterent));
                productTypePairedItems.setValueColor(R.color.red);
            } else if (productOrderViewModel.EmphaticType == EmphasisType.Suggestion) {
                productTypePairedItems.setValue(getString(R.string.emphatic_suggection));
                productTypePairedItems.setValueColor(R.color.grey);
            } else {
                productTypePairedItems.setValue(getString(R.string.emphatic_warning));
                productTypePairedItems.setValueColor(R.color.orange);
            }
        }
        pricePairedItems.setValue(HelperMethods.currencyToString(productOrderViewModel.Price));
        totalPricePairedItems.setValue(HelperMethods.currencyToString(productOrderViewModel.RequestAmount));
        userPricePairedItems.setValue(HelperMethods.currencyToString(productOrderViewModel.UserPrice));
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel showStockLevel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
        SysConfigModel orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);

        if (SysConfigManager.compare(showStockLevel, true)) {
            stockLevelPairedItems.setVisibility(View.VISIBLE);
            if (SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty)) {
                if (onHandQty <= 0) {
                    stockLevelPairedItems.setValue(getString(R.string.lack_of_inventory));
                    stockLevelPairedItems.setValueColor(R.color.red);
                } else if (onHandQty < totalQty) {
                    stockLevelPairedItems.setValue(HelperMethods.bigDecimalToString(productOrderViewModel.OnHandQty) + "   " + getString(R.string.total_qty));
                    stockLevelPairedItems.setValueColor(R.color.orange);
                } else {
                    stockLevelPairedItems.setValue(HelperMethods.bigDecimalToString(productOrderViewModel.OnHandQty) + "   " + getString(R.string.total_qty));
                    stockLevelPairedItems.setValueColor(R.color.green);
                }
            } else {
                if (onHandQty <= 0) {
                    stockLevelPairedItems.setValue(getString(R.string.lack_of_inventory));
                    stockLevelPairedItems.setValueColor(R.color.red);
                } else if (onHandQty < totalQty) {
                    stockLevelPairedItems.setValue(getString(R.string.in_adequate_inventory));
                    stockLevelPairedItems.setValueColor(R.color.orange);
                } else {
                    stockLevelPairedItems.setValue(getString(R.string.enough_inventory));
                    stockLevelPairedItems.setValueColor(R.color.green);
                }
            }
        } else {
            stockLevelPairedItems.setVisibility(View.GONE);
        }

        totalOrderedQtyPairedItems.setValue(HelperMethods.bigDecimalToString(productOrderViewModel.ProductTotalOrderedQty));
        averagePairedItems.setValue(String.valueOf(productOrderViewModel.Average));
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            invoiceCountItems.setVisibility(View.VISIBLE);
            invoiceCountItems.setValue(String.valueOf(productOrderViewModel.InvoiceCount));
        }
        if (productOrderViewModel.PrizeComment == null || productOrderViewModel.PrizeComment.isEmpty()) {
            prizePairedItems.setValue(getString(R.string.no_discount_or_award_for_this_item));
            prizePairedItems.setValueColor(R.color.red);
        } else
            prizePairedItems.setValue(productOrderViewModel.PrizeComment);
        SysConfigModel customerStockLevelType = sysConfigManager.read(ConfigKey.CustomerStockCheckType, SysConfigManager.cloud);
        if (SysConfigManager.compare(customerStockLevelType, ProductInventoryManager.CustomerStockCheckType.Boolean)) {
            if (productOrderViewModel.CustomerInventoryIsAvailable) {
                customerInventoryPairedItems.setValue(getString(R.string.enough_inventory));
                customerInventoryPairedItems.setValueColor(R.color.green);
            } else {
                customerInventoryPairedItems.setValue(getString(R.string.lack_of_inventory));
                customerInventoryPairedItems.setValueColor(R.color.red);
            }
        } else {
            double stockLevelQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.CustomerInventoryTotalQty);
            if (stockLevelQty > 0) {
                customerInventoryPairedItems.setValue(String.valueOf(stockLevelQty));
                customerInventoryPairedItems.setValueColor(R.color.green);
            } else {
                customerInventoryPairedItems.setValue(getString(R.string.lack_of_inventory));
                customerInventoryPairedItems.setValueColor(R.color.red);
            }
        }
        String path = new ImageManager(getContext()).getImagePath(productId, ImageType.Product);
        if (path != null)
            Picasso.with(getContext())
                    .load(new File(path))
                    .placeholder(R.drawable.product_no_image)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(productImageView);

        ProductBatchOnHandQtyManager productBatchOnHandQtyManager = new ProductBatchOnHandQtyManager(getContext());
        List<ProductBatchOnHandQtyModel> productBatchOnHandQtyModels = productBatchOnHandQtyManager.getProductBatches(productId);

        adapter = new BaseRecyclerAdapter<ProductBatchOnHandQtyModel>(getVaranegarActvity(), productBatchOnHandQtyModels) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_product_item_row_layout, parent, false);
                return new ProductBatchViewHolder(view, adapter, getContext());
            }
        };

        batchListRecyclerView.setAdapter(adapter);
    }

    private class ProductBatchViewHolder extends BaseViewHolder<ProductBatchOnHandQtyModel> {

        private final TextView batchItem;

        public ProductBatchViewHolder(View itemView, BaseRecyclerAdapter<ProductBatchOnHandQtyModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            batchItem = itemView.findViewById(R.id.batch_row);
        }

        @Override
        public void bindView(int position) {
            ProductBatchOnHandQtyModel productBatchOnHandQtyModel = adapter.get(position);
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
            String onHand;
            if (SysConfigManager.compare(sysConfigModel, true))
                onHand =  " ، " + getString(R.string.stock_level_label) + " " + HelperMethods.bigDecimalToString(productBatchOnHandQtyModel.OnHandQty);
            else
                onHand = "";
            if (productBatchOnHandQtyModel.DateTimeExpDate != null)
            batchItem.setText(productBatchOnHandQtyModel.BatchNo + " ، " + getString(R.string.expire_date_label) + " "
                    + productBatchOnHandQtyModel.ExpDate + onHand);
            else
                batchItem.setText(productBatchOnHandQtyModel.BatchNo + " ، " + onHand);
        }
    }

    public void setArguments(UUID customerId, UUID productId, UUID callOrderId) {
        Bundle bundle = new Bundle();
        bundle.putString("5503855d-a6af-4bda-b68c-928179b1ca93", customerId.toString());
        bundle.putString("817458fc-fdd2-42f9-90bd-d0fd5cbf3edb", productId.toString());
        bundle.putString("381c803d-1e5c-4249-9823-4859bed5d9c2", callOrderId.toString());
        setArguments(bundle);
    }
}
