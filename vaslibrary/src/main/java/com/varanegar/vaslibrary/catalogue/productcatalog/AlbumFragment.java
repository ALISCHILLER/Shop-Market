package com.varanegar.vaslibrary.catalogue.productcatalog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.ProgressFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.catalogmanager.CatalogManager;
import com.varanegar.vaslibrary.manager.cataloguelog.CatalogueLogManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyWarning;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.model.catalog.CatalogModel;
import com.varanegar.vaslibrary.model.catalog.CatalogModelRepository;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.BatchQty;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CalculatorBatchUnits;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.OrderCalculatorForm;
import com.varanegar.vaslibrary.ui.dialog.ProductOrderInfoDialog;
import com.varanegar.vaslibrary.ui.viewholders.CatalogViewHolder;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.customer_not_allowed_product.CustomerNotAllowProductManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;


/**
 * Created by A.Torabi on 6/19/2017.
 */

public class AlbumFragment extends ProgressFragment {
    UUID customerId;
    UUID callOrderId;
    private ViewPager mainViewPager;
    private ViewPager backViewPager;
    private MainPagerAdapter mainPagerAdapter;
    private BackgroundPagerAdapter backgroundPagerAdapter;
    private PagerAdapterInfoMap pagerAdapterInfoMap;
    private BaseRecyclerView albumsRecyclerView;
    private CheckBox inStockSwitch;
    private BaseRecyclerAdapter<CatalogModel> albumsAdapter;
    private int lastSelectedPosition = -1;
    private FloatingActionButton addButton;
    private EditText searchEditText;
    private ImageView searchImageView;
    private ImageView clearSearchImageView;
    private boolean defaultInStock;
    private UUID lastProductId;
    private View infoButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String c1 = getStringArgument("3af8c4e9-c5c7-4540-8678-4669879caa79");
        customerId = c1 != null ? UUID.fromString(c1) : null;
        String c2 = getStringArgument("1c886632-a88a-4e73-9164-f6656c219917");
        callOrderId = c2 != null ? UUID.fromString(c2) : null;

        SharedPreferences sp = getContext().getSharedPreferences("IN_STOCK_CATALOGUE", Context.MODE_PRIVATE);
        defaultInStock = sp.getBoolean("IN_STOCK", false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getVaranegarActvity().removeDrawer();
    }

    @Override
    public void onStart() {
        super.onStart();
        startProgress(R.string.please_wait, R.string.preparing_catalogues);

        albumsAdapter = new BaseRecyclerAdapter<CatalogModel>(
                getVaranegarActvity(), new CatalogModelRepository(), CatalogManager.getAll(defaultInStock, searchEditText.getText().toString())) {

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_summary_layout, parent, false);
                return new CatalogViewHolder(view, this, getContext());
            }
        };
        albumsAdapter.setOnItemClickListener(position -> createPagerAdapter(position, defaultInStock));
        albumsRecyclerView.setAdapter(albumsAdapter);
        albumsAdapter.refresh();

        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
        HashMap<UUID, ProductUnitViewManager.ProductUnits> productUnits = productUnitViewManager.getUnitSet(ProductType.isForSale);


        pagerAdapterInfoMap = new PagerAdapterInfoMap(customerId, callOrderId);

        if (customerId != null && callOrderId != null) {
            ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());
            List<ProductOrderViewModel> productOrderViewModels = productOrderViewManager.getItems(ProductOrderViewManager.getAll(null, customerId, callOrderId, null, null, false, null));

            for (ProductOrderViewModel productOrderViewModel :
                    productOrderViewModels) {
                if (!pagerAdapterInfoMap.containsKey(productOrderViewModel.UniqueId)) {
                    PagerAdapterInfo pagerAdapterInfo = new PagerAdapterInfo();
                    if (productUnits.containsKey(productOrderViewModel.UniqueId))
                        pagerAdapterInfo.ProductUnits = productUnits.get(productOrderViewModel.UniqueId);
                    pagerAdapterInfo.productOrderViewModel = productOrderViewModel;
                    pagerAdapterInfoMap.put(productOrderViewModel.UniqueId, pagerAdapterInfo);
                }
            }
        }

        ProductManager productManager = new ProductManager(getContext());
        List<ProductModel> productModels = productManager.getAll(ProductType.isForSale);
        for (ProductModel productModel :
                productModels) {
            if (!pagerAdapterInfoMap.containsKey(productModel.UniqueId)) {
                PagerAdapterInfo pagerAdapterInfo = new PagerAdapterInfo();
                if (productUnits.containsKey(productModel.UniqueId))
                    pagerAdapterInfo.ProductUnits = productUnits.get(productModel.UniqueId);
                pagerAdapterInfo.productModel = productModel;
                pagerAdapterInfoMap.put(productModel.UniqueId, pagerAdapterInfo);
            } else {
                PagerAdapterInfo pagerAdapterInfo = pagerAdapterInfoMap.get(productModel.UniqueId);
                pagerAdapterInfo.productModel = productModel;
            }
        }

        finishProgress();
    }

    private void addLog(UUID productId) {
        if (customerId != null) {
            CatalogueLogManager catalogueLogManager = new CatalogueLogManager(getVaranegarActvity());
            catalogueLogManager.catalogueLogStart(CatalogManager.BASED_ON_PRODUCT, productId, customerId);
            endLastLog();
            lastProductId = productId;
        }
    }

    private void endLastLog() {
        if (customerId != null) {
            CatalogueLogManager catalogueLogManager = new CatalogueLogManager(getVaranegarActvity());
            if (lastProductId != null)
                catalogueLogManager.catalogueLogEnd(lastProductId, customerId);
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        endLastLog();
        getVaranegarActvity().popFragment();
    }

    private void createPagerAdapter(int position, boolean inStock) {
        mainViewPager.setVisibility(View.VISIBLE);
        backViewPager.setVisibility(View.VISIBLE);
        if (customerId != null && callOrderId != null)
            addButton.setVisibility(View.VISIBLE);
        lastSelectedPosition = position;
        CatalogManager manager = new CatalogManager(getContext());
        final List<CatalogModel> catalogModels = manager.getItems(CatalogManager.getAll(albumsAdapter.get(position).CatalogId, inStock, searchEditText.getText().toString()));
        mainPagerAdapter = new MainPagerAdapter(mainViewPager, getContext(), catalogModels, pagerAdapterInfoMap);
        backgroundPagerAdapter = new BackgroundPagerAdapter(backViewPager, getVaranegarActvity(), catalogModels, pagerAdapterInfoMap);
        if (catalogModels.size() > 0)
            addLog(catalogModels.get(0).ProductId);
        mainViewPager.setAdapter(mainPagerAdapter);
        backViewPager.setAdapter(backgroundPagerAdapter);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;
                if (catalogModels.size() > position)
                    addLog(catalogModels.get(position).ProductId);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int width = backViewPager.getWidth();
                backViewPager.scrollTo((int) (width * position + width * positionOffset), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    backViewPager.setCurrentItem(index);
                }
            }
        });
    }

    public void closeCategoriesDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getView().findViewById(R.id.groups_drawer_layout);
        LinearLayout drawerView = (LinearLayout) getView().findViewById(R.id.groups_drawer_view);
        drawerLayout.closeDrawer(drawerView);
    }

    public void openCategoriesDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getView().findViewById(R.id.groups_drawer_layout);
        LinearLayout drawerView = (LinearLayout) getView().findViewById(R.id.groups_drawer_view);
        drawerLayout.openDrawer(drawerView);
    }

    public void toggleCategoriesDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) getView().findViewById(R.id.groups_drawer_layout);
        LinearLayout drawerView = (LinearLayout) getView().findViewById(R.id.groups_drawer_view);
        if (drawerLayout.isDrawerOpen(drawerView))
            closeCategoriesDrawer();
        else
            openCategoriesDrawer();
    }

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_layout, container, false);
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                refreshAlbumsAdapter(inStockSwitch.isChecked());
            }
            return false;
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0)
                    clearSearchImageView.setVisibility(View.VISIBLE);
                else
                    clearSearchImageView.setVisibility(View.GONE);
            }
        });
        searchImageView = view.findViewById(R.id.search_image_view);
        searchImageView.setOnClickListener(view1 -> {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            refreshAlbumsAdapter(inStockSwitch.isChecked());
        });
        clearSearchImageView = view.findViewById(R.id.clear_search_image_view);
        clearSearchImageView.setOnClickListener(view12 -> {
            searchEditText.setText("");
            refreshAlbumsAdapter(inStockSwitch.isChecked());
        });
        inStockSwitch = (CheckBox) view.findViewById(R.id.in_stock_check_box);
        inStockSwitch.setChecked(defaultInStock);
        inStockSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences sp = getContext().getSharedPreferences("IN_STOCK_CATALOGUE", Context.MODE_PRIVATE);
            sp.edit().putBoolean("IN_STOCK", b).apply();
            refreshAlbumsAdapter(b);
        });
        mainViewPager = (ViewPager) view.findViewById(R.id.view_pager_main);
        backViewPager = (ViewPager) view.findViewById(R.id.view_pager_background);
        mainViewPager.setClipChildren(false);
        mainViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.padding_small));
        mainViewPager.setOffscreenPageLimit(3);
        mainViewPager.setPageTransformer(false, new CarouselEffectTransformer(getVaranegarActvity()), View.LAYER_TYPE_NONE);
        albumsRecyclerView = (BaseRecyclerView) view.findViewById(R.id.albums_recycler_view);

        addButton = (FloatingActionButton) view.findViewById(R.id.add_button);
        infoButton = view.findViewById(R.id.more_info_btn);

        FloatingActionButton groupsButton = (FloatingActionButton) view.findViewById(R.id.groups_button);
        if (groupsButton != null) {
            groupsButton.setOnClickListener(view13 -> toggleCategoriesDrawer());
        }
        FloatingActionButton groupsButtonT = (FloatingActionButton) view.findViewById(R.id.groups_button_600);
        LinearLayout groupsLayout = (LinearLayout) view.findViewById(R.id.groups_drawer_layout_600);
        if (groupsButtonT != null) {
            groupsButtonT.setOnClickListener(view14 -> {
                if (groupsLayout.getVisibility() == View.VISIBLE) {
                    groupsLayout.setVisibility(View.GONE);
                } else {
                    groupsLayout.setVisibility(View.VISIBLE);
                }
            });
        }
        if (customerId != null && callOrderId != null) {
            addButton.setVisibility(View.VISIBLE);
            infoButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
            infoButton.setVisibility(View.GONE);
        }

        infoButton.setOnClickListener(v -> {
            if (mainPagerAdapter != null) {
                if (customerId != null && callOrderId != null) {
                    final int position = mainViewPager.getCurrentItem();
                    CatalogModel catalogModel = mainPagerAdapter.getItem(position);
                    if (catalogModel != null && callOrderId != null && customerId != null) {
                        final ProductOrderViewModel productOrderViewModel = new ProductOrderViewManager(getContext()).getItem(ProductOrderViewManager.get(catalogModel.ProductId, customerId, callOrderId, false));
                        if (productOrderViewModel == null) {
                            Timber.wtf("Product order view model is null");
                            CuteMessageDialog dialog = new CuteMessageDialog(requireContext());
                            dialog.setIcon(Icon.Alert);
                            dialog.setTitle(R.string.error);
                            dialog.setMessage(R.string.product_is_not_for_sale);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        } else if (productOrderViewModel.UniqueId == null) {
                            Timber.wtf("ProductUnitId of product order view is null");
                            CuteMessageDialog dialog = new CuteMessageDialog(requireContext());
                            dialog.setIcon(Icon.Alert);
                            dialog.setTitle(R.string.error);
                            dialog.setMessage(R.string.product_is_not_for_sale);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        } else {
                            ProductOrderInfoDialog dialog = new ProductOrderInfoDialog();
                            dialog.setArguments(customerId, productOrderViewModel.UniqueId, callOrderId);
                            dialog.show(getChildFragmentManager(), "ProductInfoDialog");
                        }
                    }
                }
            }
        });
        addButton.setOnClickListener(v -> {
            if (mainPagerAdapter != null) {
                final int position = mainViewPager.getCurrentItem();
                CatalogModel catalogModel = mainPagerAdapter.getItem(position);
                if (catalogModel != null && callOrderId != null && customerId != null) {
                    final ProductOrderViewModel productOrderViewModel = new ProductOrderViewManager
                            (getContext()).getItem(ProductOrderViewManager
                            .get(catalogModel.ProductId, customerId, callOrderId, false));
                    if (productOrderViewModel == null) {
                        Timber.wtf("Product order view model is null");
                        CuteMessageDialog dialog = new CuteMessageDialog(requireContext());
                        dialog.setIcon(Icon.Alert);
                        dialog.setTitle(R.string.error);
                        dialog.setMessage(R.string.product_is_not_for_sale);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    } else if (productOrderViewModel.UniqueId == null) {
                        Timber.wtf("ProductUnitId of product order view is null");
                        CuteMessageDialog dialog = new CuteMessageDialog(requireContext());
                        dialog.setIcon(Icon.Alert);
                        dialog.setTitle(R.string.error);
                        dialog.setMessage(R.string.product_is_not_for_sale);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    } else {
                        CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = new CustomerCallOrderOrderViewModelRepository().getItem(
                                new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
                                        .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, callOrderId))
                                        .whereAnd(Criteria.equals(CustomerCallOrderOrderView.ProductId, productOrderViewModel.UniqueId))
                        );
                        List<OrderLineQtyModel> orderLineQtyModels = new ArrayList<>();
                        if (customerCallOrderOrderViewModel != null)
                            orderLineQtyModels = new OrderLineQtyManager(getContext())
                                    .getQtyLines(customerCallOrderOrderViewModel.UniqueId);
                        OrderCalculatorForm orderCalculatorForm = new OrderCalculatorForm();
                        try {
                            CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());

                            final OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
                            ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getContext());
                            ProductUnitsViewModel productUnitsViewModel = productUnitsViewManager.getItem(productOrderViewModel.UniqueId);
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
                            BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(customerCallOrderOrderViewModel);
                            if (productOrderViewModel.ExpDate == null)
                                orderCalculatorForm.setArguments(productOrderViewModel.UniqueId, productOrderViewModel.ProductName, calculatorHelper.generateCalculatorUnits(productOrderViewModel.UniqueId, orderLineQtyModels, bulkUnit, ProductType.isForSale), productOrderViewModel.Price, productOrderViewModel.UserPrice, onHandQtyStock, customerId, callOrderId);
                            else
                                orderCalculatorForm.setArguments(productOrderViewModel.UniqueId, productOrderViewModel.ProductName, CalculatorBatchUnits.generate(getContext(), productOrderViewModel, customerCallOrderOrderViewModel == null ? null : customerCallOrderOrderViewModel.UniqueId, productOrderViewModel.Price, productOrderViewModel.PriceId, productOrderViewModel.UserPrice), productOrderViewModel.UserPrice, onHandQtyStock, customerId, callOrderId);
                            orderCalculatorForm.onCalcFinish = (discreteUnits, bulkUnit1, batchQtyList) -> {
                                try {
                                    ProductOrderViewManager.checkOnHandQty(getContext(), onHandQtyStock, discreteUnits, bulkUnit1);
                                    //manager customerid productid
                                    CustomerNotAllowProductManager.checkNotAllowed(getContext()
                                            ,customerId,catalogModel.ProductId);
                                    onAdd(productOrderViewModel, discreteUnits, bulkUnit1, position, batchQtyList);
                                } catch (OnHandQtyWarning e) {
                                    Timber.e(e);
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setTitle(R.string.warning);
                                    dialog.setMessage(e.getMessage());
                                    dialog.setIcon(Icon.Warning);
                                    dialog.setPositiveButton(R.string.ok, v1 -> {
                                        try {
                                            onAdd(productOrderViewModel, discreteUnits, bulkUnit1, position, batchQtyList);
                                        } catch (Exception ex) {
                                            Timber.e(ex);
                                            CuteMessageDialog dialog1 = new CuteMessageDialog(getContext());
                                            dialog1.setTitle(R.string.error);
                                            dialog1.setMessage(R.string.error_saving_request);
                                            dialog1.setIcon(Icon.Error);
                                            dialog1.setPositiveButton(R.string.ok, null);
                                            dialog1.show();
                                        }
                                    });
                                    dialog.setNegativeButton(R.string.cancel, null);
                                    dialog.show();
                                } catch (OnHandQtyError e) {
                                    Timber.e(e);
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setTitle(R.string.error);
                                    dialog.setMessage(e.getMessage());
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                } catch (Exception e) {
                                    Timber.e(e);
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setTitle(R.string.error);
                                    dialog.setMessage(R.string.error_saving_request);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            };
                            orderCalculatorForm.show(getVaranegarActvity().getSupportFragmentManager(), "dc38bc80-72d4-4f10-8a1b-0d6c02e663bf");
                        } catch (ProductUnitViewManager.UnitNotFoundException e) {
                            getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                            Timber.e(e, "product unit not found in product group fragment");
                        }
                    }
                }
            } else {
                getVaranegarActvity().showSnackBar(R.string.please_select_a_product, MainVaranegarActivity.Duration.Short);
            }

        });
        return view;
    }

    private void refreshAlbumsAdapter(final boolean b) {
        albumsAdapter.refresh(CatalogManager.getAll(b, searchEditText.getText().toString()));
        albumsAdapter.notifyDataSetChanged();
        albumsAdapter.setOnItemClickListener(position -> createPagerAdapter(position, b));
        if (lastSelectedPosition >= 0 && albumsAdapter.getItems() != null && albumsAdapter.getItems().size() > lastSelectedPosition) {
            createPagerAdapter(lastSelectedPosition, b);
            mainViewPager.setVisibility(View.VISIBLE);
            backViewPager.setVisibility(View.VISIBLE);
            if (customerId != null && callOrderId != null)
                addButton.setVisibility(View.VISIBLE);

        } else {
            mainViewPager.setVisibility(View.INVISIBLE);
            backViewPager.setVisibility(View.INVISIBLE);
            addButton.setVisibility(View.INVISIBLE);
        }
    }


    private void onAdd(final ProductOrderViewModel productModel, final List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, final int position, @Nullable List<BatchQty> batchQtyList) throws ValidationException, DbException {
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
        callOrderLineManager.addOrUpdateQty(productModel.UniqueId, discreteUnits, bulkUnit, callOrderId, null, batchQtyList, false);
        ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());
        ProductOrderViewModel productOrderViewModel = productOrderViewManager.getLine(customerId, callOrderId, productModel.UniqueId, false);
        if (pagerAdapterInfoMap.containsKey(productOrderViewModel.UniqueId)) {
            pagerAdapterInfoMap.get(productOrderViewModel.UniqueId).productOrderViewModel = productOrderViewModel;
        }
        mainPagerAdapter.refreshView(productOrderViewModel, position);
        backgroundPagerAdapter.refreshView(productOrderViewModel, position);
    }
}
