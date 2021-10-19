package com.varanegar.vaslibrary.catalogue.groupcatalog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.framework.util.recycler.expandablerecycler.ChildRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.ProductGroupCatalogManager;
import com.varanegar.vaslibrary.manager.ProductGroupCatalogViewManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.catalogmanager.CatalogManager;
import com.varanegar.vaslibrary.manager.cataloguelog.CatalogueLogManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderBy;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogModel;
import com.varanegar.vaslibrary.model.productgroupcatalog.ProductGroupCatalogViewModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderView;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/13/2018.
 */

public class GroupCatalogFragment extends VaranegarFragment {
    private ViewPager albumViewPager;
    CatalogPagerAdapter pagerAdapter;
    ExpandableRecyclerAdapter<ProductGroupCatalogViewModel, ProductGroupCatalogModel> adapter;
    private SimpleToolbar toolbar;
    private List<ProductGroupCatalogModel> catalogsOfGroups;
    private UUID customerId;
    private UUID callOrderId;
    private HashMap<UUID, List<ProductOrderViewModel>> catalogOrdersHashMap;
    private SysConfigModel showStockLevel;
    private SysConfigModel orderPointCheckType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String cid = getStringArgument("3af8c4e9-c5c7-4540-8678-4669879caa79");
        if (cid != null)
            customerId = UUID.fromString(cid);
        String oid = getStringArgument("1c886632-a88a-4e73-9164-f6656c219917");
        if (oid != null)
            callOrderId = UUID.fromString(oid);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private CatalogPagerAdapter getPagerAdapter() {
        if (customerId != null && callOrderId != null) {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            showStockLevel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
            orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);
            catalogOrdersHashMap = new HashMap<>();
            List<ProductOrderViewModel> orderViewModelList = new ProductOrderViewManager(getActivity()).getItems(ProductOrderViewManager.getAll(null, customerId, callOrderId, null, null, false, new OrderBy(ProductOrderView.CatalogOrderOf, OrderType.ASC)));
            Linq.forEach(orderViewModelList, new Linq.Consumer<ProductOrderViewModel>() {
                @Override
                public void run(ProductOrderViewModel item) {
                    if (item.CatalogId != null && !item.CatalogId.isEmpty()) {
                        List<UUID> catalogIds = new ArrayList<>();
                        StringTokenizer st = new StringTokenizer(item.CatalogId, ":", false);
                        while (st.hasMoreTokens()) {
                            catalogIds.add(UUID.fromString(st.nextToken()));
                        }
                        for (UUID catalogId :
                                catalogIds) {
                            if (item.CatalogId != null) {
                                if (catalogOrdersHashMap.containsKey(catalogId)) {
                                    catalogOrdersHashMap.get(catalogId).add(item);
                                } else {
                                    List<ProductOrderViewModel> productOrderViewModels = new ArrayList<>();
                                    productOrderViewModels.add(item);
                                    catalogOrdersHashMap.put(catalogId, productOrderViewModels);
                                }
                            }
                        }
                    }
                }
            });
        }
        return new CatalogPagerAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View drawerView = setDrawerLayout(R.layout.album_group_drawer);
        BaseRecyclerView albumGroupRecyclerView = (BaseRecyclerView) drawerView.findViewById(R.id.album_group_recycler_view);

        adapter = new ExpandableRecyclerAdapter<ProductGroupCatalogViewModel, ProductGroupCatalogModel>(
                getVaranegarActvity(),
                new ProductGroupCatalogViewManager(getContext()).getItems(ProductGroupCatalogViewManager.getAll()),
                new ExpandableRecyclerAdapter.Children<ProductGroupCatalogViewModel, ProductGroupCatalogModel>() {
                    @Override
                    public List<ProductGroupCatalogModel> onCreate(ProductGroupCatalogViewModel parentItem) {
                        return new ProductGroupCatalogManager(getContext()).getItems(ProductGroupCatalogManager.getAll(parentItem.UniqueId));
                    }
                }
        ) {
            @Override
            public BaseViewHolder<ProductGroupCatalogViewModel> onCreateParent(ViewGroup parent) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
                return new BaseViewHolder<ProductGroupCatalogViewModel>(itemView, this, getContext()) {
                    @Override
                    public void bindView(final int position) {
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapter.runItemClickListener(position);
                            }
                        });
                        ProductGroupCatalogViewModel productGroupModel = adapter.get(position);
                        String groupName = productGroupModel != null ? productGroupModel.ProductGroupName : "";
                        ((TextView) itemView.findViewById(R.id.product_name_text_view)).setText(groupName);
                        ((TextView) itemView.findViewById(R.id.product_name_text_view_selected)).setText(groupName);
                    }
                };
            }

            @Override
            public BaseViewHolder<ProductGroupCatalogModel> onCreateChild(ViewGroup parent, final ChildRecyclerAdapter<ProductGroupCatalogModel> adapter) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_group_album, parent, false);
                return new BaseViewHolder<ProductGroupCatalogModel>(itemView, adapter, getContext()) {
                    @Override
                    public void bindView(final int position) {
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                adapter.runItemClickListener(position);
                            }
                        });
                        ProductGroupCatalogModel catalogModel = adapter.get(position);
                        ((TextView) itemView.findViewById(R.id.group_catalog_name_text_view)).setText(catalogModel.CatalogName);
                        ImageView thumbNailImageView = (ImageView) itemView.findViewById(R.id.catalog_thumb_nail_image_view);
                        String path = new ImageManager(getContext()).getImagePath(catalogModel.UniqueId, ImageType.CatalogSmall);
                        if (path != null)
                            Glide.with(GroupCatalogFragment.this).load(new File(path)).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).skipMemoryCache(true).into(thumbNailImageView);

                    }
                };
            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<ProductGroupCatalogViewModel>() {
            @Override
            public void run(int position) {
                getVaranegarActvity().toggleDrawer();
                setupViewPagerAdapter(adapter.get(position).UniqueId);
            }
        });
        adapter.setOnChildItemClickListener(new ExpandableRecyclerAdapter.OnChildItemClick<ProductGroupCatalogModel>() {
            @Override
            public void onClick(int position, ProductGroupCatalogModel clickedItem) {
                getVaranegarActvity().toggleDrawer();
                changeViewPagerPosition(clickedItem);
            }
        });
        albumGroupRecyclerView.setAdapter(adapter);
        if (adapter.size() > 0)
            setupViewPagerAdapter(adapter.get(0).UniqueId);
    }

    private void changeViewPagerPosition(final ProductGroupCatalogModel clickedItem) {
        if (pagerAdapter == null) {
            setupViewPagerAdapter(clickedItem.ProductMainGroupId);
        }
        int position = pagerAdapter.getItemIndex(clickedItem);
        if (position == -1) {
            ProductGroupCatalogViewModel group = Linq.findFirst(adapter.getItems(), new Linq.Criteria<ProductGroupCatalogViewModel>() {
                @Override
                public boolean run(ProductGroupCatalogViewModel item) {
                    return item.UniqueId.equals(clickedItem.ProductMainGroupId);
                }
            });
            if (group != null) {
                setupViewPagerAdapter(group.UniqueId);
                changeViewPagerPosition(clickedItem);
            }
        } else
            albumViewPager.setCurrentItem(position);
    }

    private void setupViewPagerAdapter(final UUID groupId) {
        catalogsOfGroups = new
                ProductGroupCatalogManager(getContext()).getItems(ProductGroupCatalogManager.getAll(groupId));
        addLog(0);
        pagerAdapter = getPagerAdapter();
        albumViewPager.setOffscreenPageLimit(3);
        albumViewPager.setKeepScreenOn(true);
        albumViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(position);
                addLog(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        albumViewPager.setAdapter(pagerAdapter);
        if (catalogsOfGroups.size() > 0) {
            albumViewPager.setCurrentItem(0);
            setTitle(0);
        }
    }

    UUID lastGroupId = null;

    private void addLog(int position) {
        if (customerId != null) {
            CatalogueLogManager catalogueLogManager = new CatalogueLogManager(getVaranegarActvity());
            ProductGroupCatalogModel catalog = null;
            if (catalogsOfGroups.size() > 0)
                catalog = catalogsOfGroups.get(position);
            if (catalog != null)
                catalogueLogManager.catalogueLogStart(CatalogManager.BASED_ON_PRODUCT_GROUP, catalog.UniqueId, customerId);
            endLastLog();
            lastGroupId = catalog.UniqueId;
        }
    }

    private void endLastLog() {
        if (customerId != null) {
            CatalogueLogManager catalogueLogManager = new CatalogueLogManager(getVaranegarActvity());
            if (lastGroupId != null)
                catalogueLogManager.catalogueLogEnd(lastGroupId, customerId);
        }
    }

    private void setTitle(int position) {
        if (position >= 0 && position < catalogsOfGroups.size()) {
            ProductGroupCatalogModel catalog = catalogsOfGroups.get(position);
            toolbar.setTitle(catalog.CatalogName);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_batch_album_layout, container, false);
        toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
        toolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        toolbar.setOnMenuClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        albumViewPager = (ViewPager) view.findViewById(R.id.view_pager);

        return view;
    }

    private void back() {
        endLastLog();
        getVaranegarActvity().popFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (albumViewPager != null) {
            albumViewPager.destroyDrawingCache();
            albumViewPager = null;
        }
        if (adapter != null) {
            adapter.clear();
            adapter = null;
        }
        Glide.with(GroupCatalogFragment.this).onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onStop() {
        super.onStop();
        Glide.with(this).onStop();
    }


    class CatalogPagerAdapter extends PagerAdapter {
        public ProductGroupCatalogModel getCurrentCatalog() {
            return currentCatalog;
        }

        private ProductGroupCatalogModel currentCatalog;

        private BaseRecyclerAdapter getCataloguesAdapter() {
            if (callOrderId != null && customerId != null) {
                List<ProductOrderViewModel> catalogOrders = catalogOrdersHashMap.get(getCurrentCatalog().UniqueId);
                return new BaseRecyclerAdapter<ProductOrderViewModel>(getVaranegarActvity(), catalogOrders) {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_album_batch_item, parent, false);
                        return new ProductGroupCatalogProductOrderViewHolder(view, this, getActivity(), showStockLevel, orderPointCheckType, callOrderId, customerId);
                    }
                };
            } else {
                List<ProductModel> catalogItems = new ProductManager(getActivity()).getForSaleItemsOrderByCatalog(getCurrentCatalog().UniqueId);
                return new BaseRecyclerAdapter<ProductModel>(getVaranegarActvity(), catalogItems) {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_album_batch_item_readonly, parent, false);
                        return new ProductGroupCatalogProductViewHolder(view, this, getActivity());
                    }
                };
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            return onCreateView(container, position);
        }

        @NonNull
        public View onCreateView(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.group_album_layout, container, false);
            currentCatalog = catalogsOfGroups.get(position);
            String path = new ImageManager(getContext()).getImagePath(currentCatalog.UniqueId, ImageType.CatalogLarge);
            ImageView catalogImageView = (ImageView) view.findViewById(R.id.catalog_image_view);
            if (path != null)
                Glide.with(GroupCatalogFragment.this).load(new File(path)).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).skipMemoryCache(true).into(catalogImageView);

            final BaseRecyclerView qtysRecyclerView = (BaseRecyclerView) view.findViewById(R.id.product_controls_recycler_view);
            qtysRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            qtysRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.color.grey_light, 1));
            final BaseRecyclerAdapter adapter = getCataloguesAdapter();
            qtysRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            qtysRecyclerView.invalidate();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return catalogsOfGroups.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return (view == object);
        }


        public int getItemIndex(final ProductGroupCatalogModel clickedItem) {
            int index = Linq.findFirstIndex(catalogsOfGroups, new Linq.Criteria<ProductGroupCatalogModel>() {
                @Override
                public boolean run(ProductGroupCatalogModel item) {
                    return item.UniqueId.equals(clickedItem.UniqueId);
                }
            });
            return index;
        }

    }
}
