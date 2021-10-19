package com.varanegar.vaslibrary.catalogue.productcatalog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.catalogmanager.CatalogManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.model.catalog.CatalogModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/9/2018.
 */

public class MainPagerAdapter extends PagerAdapter {
    private final ViewPager viewPager;
    private final PagerAdapterInfoMap pagerAdapterInfoMap;
    private Context context;
    private List<CatalogModel> catalogsList;
    private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_XY;

    protected UUID getProductId(int position) {
        return catalogsList.get(position).ProductId;
    }

    public Context getContext() {
        return context;
    }

    public MainPagerAdapter(ViewPager viewPager, Context context, List<CatalogModel> catalogs, PagerAdapterInfoMap pagerAdapterInfoMap) {
        this.context = context;
        this.catalogsList = catalogs;
        this.viewPager = viewPager;
        this.pagerAdapterInfoMap = pagerAdapterInfoMap;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final View view = LayoutInflater.from(context).inflate(R.layout.album_main_layout, container, false);
        final View infoView = view.findViewById(R.id.info_linear_layout);
        view.findViewById(R.id.info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infoView.getVisibility() == View.VISIBLE) {
                    infoView.setVisibility(View.GONE);
                    ((FloatingActionButton) v).setImageResource(R.drawable.ic_unfold_more_white_24dp);
                } else {
                    infoView.setVisibility(View.VISIBLE);
                    ((FloatingActionButton) v).setImageResource(R.drawable.ic_unfold_less_white_24dp);
                }
            }
        });
        View linearLayout = view.findViewById(R.id.linear_layout);
        linearLayout.setBackgroundResource(R.drawable.shadow);

        final ImageView imageCover = (ImageView) view.findViewById(R.id.image_cover);
        view.findViewById(R.id.crop_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScaleType();
                ImageView.ScaleType scaleType = getScaleType();
                imageCover.setScaleType(scaleType);
            }
        });
        imageCover.setScaleType(getScaleType());
        CatalogManager catalogManager = new CatalogManager(context);
        UUID productId = getProductId(position);
        PagerAdapterInfo pagerAdapterInfo = pagerAdapterInfoMap.get(productId);
        ProductModel productModel = pagerAdapterInfo.productModel;
        if (pagerAdapterInfo.ProductUnits != null) {
            ProductUnitViewModel smallUnit = pagerAdapterInfo.ProductUnits.SmallUnit;
            ProductUnitViewModel largeUnit = pagerAdapterInfo.ProductUnits.LargeUnit;
            if (largeUnit == null)
                ((TextView) view.findViewById(R.id.unit_text_view)).setText(context.getString(R.string.product_unit_name) + ": " + smallUnit.UnitName);
            else
                ((TextView) view.findViewById(R.id.unit_text_view)).setText(context.getString(R.string.small_unit) + " : " + smallUnit.UnitName + "     " + context.getString(R.string.large_unit) + " : " + largeUnit.UnitName);
        }
        ImageManager imageManager = new ImageManager(getContext());
        String path = imageManager.getImagePath(productId, ImageType.Product);
        if (path != null)
            Picasso.with(context)
                    .load(new File(path))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageCover);
        container.addView(view);

        ((TextView) view.findViewById(R.id.product_name_text_view)).setText(productModel.ProductName);
        ((TextView) view.findViewById(R.id.code_text_view)).setText(getContext().getString(R.string.product_code) + " " + productModel.ProductCode);

        if (pagerAdapterInfoMap.isOrder()) {
            TextView qtyTextView = (TextView) view.findViewById(R.id.qty_text_view);
            qtyTextView.setTag("qty" + position);
            if (pagerAdapterInfo.productOrderViewModel != null) {
                ((TextView) view.findViewById(R.id.price_text_view)).setTextColor(HelperMethods.getColor(getContext(), R.color.green_complete));
                qtyTextView.setText(pagerAdapterInfo.productOrderViewModel.Qty);
                TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
                priceTextView.setText(HelperMethods.currencyToString(pagerAdapterInfo.productOrderViewModel.Price));

                if (pagerAdapterInfo.productOrderViewModel.UserPrice != null) {
                    TextView userPrice = view.findViewById(R.id.user_price);
                    TextView userPriceLabel = view.findViewById(R.id.user_price_label);
                    userPriceLabel.setText(getContext().getString(R.string.user_price_label));
                    userPrice.setText(HelperMethods.currencyToString(pagerAdapterInfo.productOrderViewModel.UserPrice));
                    userPrice.setTextColor(HelperMethods.getColor(getContext(), R.color.green_complete));

                }
                if (pagerAdapterInfo.productOrderViewModel.Price != null && pagerAdapterInfo.ProductUnits != null && pagerAdapterInfo.ProductUnits.LargeUnit != null) {
                    TextView largeUnitPriceTextView = view.findViewById(R.id.large_unit_price);
                    TextView largeUnitPriceLabelTextView = view.findViewById(R.id.large_unit_price_label);
                    largeUnitPriceLabelTextView.setText(getContext().getString(R.string.total_price_lablel) + " " + pagerAdapterInfo.ProductUnits.LargeUnit.UnitName + " ");
                    largeUnitPriceTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.green_complete));
                    largeUnitPriceTextView.setText(HelperMethods.currencyToString(pagerAdapterInfo.productOrderViewModel.Price.multiply(new Currency(pagerAdapterInfo.ProductUnits.LargeUnit.ConvertFactor))));
                }
            } else {
                ((TextView) view.findViewById(R.id.price_text_view)).setTextColor(HelperMethods.getColor(getContext(), R.color.red_error));
                TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);
                priceTextView.setText(getContext().getString(R.string.no_price));
                qtyTextView.setText("0");
            }
        } else {
            view.findViewById(R.id.order_info_layout).setVisibility(View.GONE);
        }
        return view;
    }

    private ImageView.ScaleType getScaleType() {
        SharedPreferences sp = getContext().getSharedPreferences("ALBUM_SCALE_TYPE", Context.MODE_PRIVATE);
        int scaleType = sp.getInt("SCT", 5);
        return ImageView.ScaleType.values()[scaleType];
    }

    private void setScaleType() {
        SharedPreferences sp = getContext().getSharedPreferences("ALBUM_SCALE_TYPE", Context.MODE_PRIVATE);
        int scaleType = sp.getInt("SCT", 5);
        scaleType++;
        if (scaleType > 7)
            scaleType = 0;
        sp.edit().putInt("SCT", scaleType).apply();
    }

    public void refreshView(ProductOrderViewModel productOrderViewModel, int position) {
        View view = viewPager.findViewWithTag("qty" + position);
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

    @Nullable
    public CatalogModel getItem(int position) {
        return position > catalogsList.size() || position < 0 ? null : catalogsList.get(position);
    }

}
