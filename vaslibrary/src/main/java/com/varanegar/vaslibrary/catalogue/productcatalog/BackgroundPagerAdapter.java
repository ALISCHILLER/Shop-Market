package com.varanegar.vaslibrary.catalogue.productcatalog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.catalog.CatalogModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/9/2018.
 */

public class BackgroundPagerAdapter extends PagerAdapter {
    private PagerAdapterInfoMap pagerAdapterInfoMap;

    private final ViewPager viewPager;

    protected UUID getProductId(int position) {
        return catalogsList.get(position).ProductId;
    }


    public Context getContext() {
        return context;
    }

    private Context context;
    private List<CatalogModel> catalogsList;

    public BackgroundPagerAdapter(ViewPager viewPager, Context context, List<CatalogModel> catalogsList, PagerAdapterInfoMap pagerAdapterInfoMap) {
        this.context = context;
        this.catalogsList = catalogsList;
        this.viewPager = viewPager;
        this.pagerAdapterInfoMap = pagerAdapterInfoMap;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_background_layout, container, false);
        View linearLayout = view.findViewById(R.id.linear_layout);
        ImageView imageCover = (ImageView) view.findViewById(R.id.image_cover);
        linearLayout.setBackgroundResource(0);
        UUID productId = getProductId(position);
        ImageManager imageManager = new ImageManager(context);
        String path = imageManager.getImagePath(productId, ImageType.Product);
        if (path != null)
            Picasso.with(context)
                    .load(new File(path))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageCover);
        container.addView(view);

        PagerAdapterInfo pagerAdapterInfo = pagerAdapterInfoMap.get(productId);
        ProductModel product = pagerAdapterInfo.productModel;
        ((TextView) view.findViewById(R.id.product_name_text_view)).setText(product.ProductName);
        ((TextView) view.findViewById(R.id.code_text_view)).setText(product.ProductCode);
        if (pagerAdapterInfo.ProductUnits != null) {
            ProductUnitViewModel smallUnit = pagerAdapterInfo.ProductUnits.SmallUnit;
            ProductUnitViewModel largeUnit = pagerAdapterInfo.ProductUnits.LargeUnit;
            if (largeUnit == null)
                ((TextView) view.findViewById(R.id.unit_text_view)).setText(context.getString(R.string.product_unit_name) + " : " + smallUnit.UnitName);
            else
                ((TextView) view.findViewById(R.id.unit_text_view)).setText(context.getString(R.string.small_unit) + " : " + smallUnit.UnitName + "     " + context.getString(R.string.large_unit) + " : " + largeUnit.UnitName);
        }


        if (pagerAdapterInfoMap.isOrder()) {
            ProductOrderViewModel productOrderViewModel = pagerAdapterInfo.productOrderViewModel;
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel showStockLevel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
            SysConfigModel orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);
            TextView stockLevelTextView = (TextView) view.findViewById(R.id.stock_level_text_view);
            if (productOrderViewModel != null) {
                ((TextView) view.findViewById(R.id.price_text_view)).setTextColor(HelperMethods.getColor(getContext(), R.color.green_complete));
                ((TextView) view.findViewById(R.id.price_text_view)).setText(productOrderViewModel.Price.toString());
                ((TextView) view.findViewById(R.id.qty_text_view)).setText(productOrderViewModel.Qty);
                if (pagerAdapterInfo.productOrderViewModel.Price != null && pagerAdapterInfo.ProductUnits != null && pagerAdapterInfo.ProductUnits.LargeUnit != null) {
                    ((TextView) view.findViewById(R.id.large_unit_price_label)).setText(getContext().getString(R.string.total_price_lablel) + " " + pagerAdapterInfo.ProductUnits.LargeUnit.UnitName);
                    ((TextView) view.findViewById(R.id.large_unit_price)).setText(HelperMethods.currencyToString(pagerAdapterInfo.productOrderViewModel.Price.multiply(new Currency(pagerAdapterInfo.ProductUnits.LargeUnit.ConvertFactor))));
                    ((TextView) view.findViewById(R.id.large_unit_price)).setTextColor(HelperMethods.getColor(getContext(), R.color.green_complete));
                }
                if (productOrderViewModel.UserPrice != null) {
                    ((TextView) view.findViewById(R.id.user_price_label)).setText(getContext().getString(R.string.user_price_label));
                    ((TextView) view.findViewById(R.id.user_price)).setText(productOrderViewModel.UserPrice.toString());
                    ((TextView) view.findViewById(R.id.user_price)).setTextColor(HelperMethods.getColor(getContext(), R.color.green_complete));
                }

                if (productOrderViewModel.OnHandQty == null)
                    productOrderViewModel.OnHandQty = BigDecimal.ZERO;
                if (productOrderViewModel.RemainedAfterReservedQty == null)
                    productOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
                if (productOrderViewModel.ProductTotalOrderedQty == null)
                    productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
                if (productOrderViewModel.OrderPoint == null)
                    productOrderViewModel.OrderPoint = BigDecimal.ZERO;
                BigDecimal remained = productOrderViewModel.OnHandQty.subtract(productOrderViewModel.ProductTotalOrderedQty);
                if (SysConfigManager.compare(showStockLevel, true)) {
                    view.findViewById(R.id.stock_level_layout).setVisibility(View.VISIBLE);
                    if (SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty)) {
                        if (productOrderViewModel.OnHandQty.compareTo(productOrderViewModel.OrderPoint) <= 0) {
                            stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.red));
                            stockLevelTextView.setText(R.string.multiplication_sign);
                        } else {
                            if (remained.compareTo(productOrderViewModel.OrderPoint) >= 0) {
                                stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.green));
                            } else {
                                stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.orange));
                            }
                            ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getContext());
                            ProductUnitsViewModel productUnitsViewModel = productUnitsViewManager.getItem(productOrderViewModel.UniqueId);
                            String txt = VasHelperMethods.chopTotalQtyToString(productOrderViewModel.OnHandQty, productUnitsViewModel.UnitName, productUnitsViewModel.ConvertFactor, ":", ":");
                            stockLevelTextView.setText(txt);

                        }
                    } else {
                        if (productOrderViewModel.OnHandQty.compareTo(productOrderViewModel.OrderPoint) <= 0) {
                            stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.red));
                            stockLevelTextView.setText(R.string.multiplication_sign);
                        } else {
                            if (remained.compareTo(productOrderViewModel.OrderPoint) >= 0) {
                                stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.green));
                                stockLevelTextView.setText(R.string.check_sign);
                            } else {
                                stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.orange));
                                stockLevelTextView.setText(R.string.multiplication_sign);
                            }
                        }
                    }
                } else {
                    view.findViewById(R.id.stock_level_layout).setVisibility(View.GONE);
                }
            } else {
                ((TextView) view.findViewById(R.id.price_text_view)).setTextColor(HelperMethods.getColor(getContext(), R.color.red_error));
                ((TextView) view.findViewById(R.id.price_text_view)).setText(R.string.no_price);
                ((TextView) view.findViewById(R.id.qty_text_view)).setText("0");
            }
            view.findViewById(R.id.qty_text_view).setTag("backqty" + position);
        } else {
            view.findViewById(R.id.order_info_layout).setVisibility(View.GONE);
        }
        return view;
    }

    public void refreshView(ProductOrderViewModel productOrderViewModel, int position) {
        View view = viewPager.findViewWithTag("backqty" + position);
        if (view != null) {
            ((TextView) (view)).setText(productOrderViewModel.Qty);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return catalogsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

}
