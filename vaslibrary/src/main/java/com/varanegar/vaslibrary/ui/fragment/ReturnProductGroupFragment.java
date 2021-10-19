package com.varanegar.vaslibrary.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.recycler.expandablerecycler.ChildRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerView;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.OldInvoiceDetailViewManager;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductReturnWithoutRefManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLinesManager;
import com.varanegar.vaslibrary.manager.customercallreturnview.CustomerCallReturnViewManager;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.oldinvoicedetailview.OldInvoiceDetailViewModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModelRepository;
import com.varanegar.vaslibrary.model.productreturn.ProductReturnWithoutRefModel;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.returncalculator.ReturnCalculatorForm;
import com.varanegar.vaslibrary.ui.calculator.returncalculator.ReturnCalculatorItem;
import com.varanegar.vaslibrary.ui.list.ProductReturnWithoutRefListAdapter;
import com.varanegar.vaslibrary.ui.report.OldInvoiceDetailReportAdapter;
import com.varanegar.vaslibrary.ui.viewholders.ChildProductGroupViewHolder;
import com.varanegar.vaslibrary.ui.viewholders.ProductGroupViewHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 4/15/2017.
 */

public class ReturnProductGroupFragment extends VisitFragment {
    private UUID customerId;
    private ReportAdapter adapter;
    private boolean isRef;
    private BigDecimal invoiceQty;
    private List<CustomerPriceModel> priceList;
    ExpandableRecyclerAdapter<ProductGroupModel, ProductGroupModel> groupsAdapter;
    private EditText searchEditText;
    @Nullable
    private UUID dealerId;
    private List<OldInvoiceDetailViewModel> oldInvoiceDetailViewModels = new ArrayList<>();
    private List<OldInvoiceDetailViewModel> filteredOldInvoiceDetailViewModels = new ArrayList<>();
    private List<ProductReturnWithoutRefModel> productReturnWithoutRefModels = new ArrayList<>();
    private List<ProductReturnWithoutRefModel> filteredProductReturnWithoutRefModels = new ArrayList<>();

    private ReportView reportView;
    private String keyWord;
    private UUID[] groupIds;
    long delay = 200; // milliseconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private ImageView categoriesImageView;
    private View groupBar;
    private boolean withoutRefReplacementRegistration;
    private boolean withRefReplacementRegistration;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setArguments(boolean isRef, @NonNull UUID customerId, @Nullable UUID dealerId, boolean withoutRefReplacementRegistration, boolean withRefReplacementRegistration) {
        addArgument("5fe3e465-0238-4dd4-a791-dbb08fd956dd", isRef);
        addArgument("983a0eda-6c52-4f79-ba98-c0279b7d8cf4", customerId.toString());
        addArgument("dc750626-9738-4732-abb4-ad3c4ad35abd", withoutRefReplacementRegistration);
        addArgument("6487bc11-7fe7-41b1-b30b-1bfd60f6ed76", withRefReplacementRegistration);
        if (dealerId != null)
            addArgument("442958fc-aaa6-4862-aaa8-d7f7958d8293", dealerId.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRef = getArguments().getBoolean("5fe3e465-0238-4dd4-a791-dbb08fd956dd");
        customerId = UUID.fromString(getArguments().getString("983a0eda-6c52-4f79-ba98-c0279b7d8cf4"));
        withoutRefReplacementRegistration = getArguments().getBoolean("dc750626-9738-4732-abb4-ad3c4ad35abd");
        withRefReplacementRegistration = getArguments().getBoolean("6487bc11-7fe7-41b1-b30b-1bfd60f6ed76");
        String d = getArguments().getString("442958fc-aaa6-4862-aaa8-d7f7958d8293");
        if (d != null)
            dealerId = UUID.fromString(d);
        priceList = VaranegarApplication.getInstance().retrieve("955119ea-eb2d-428a-9dd7-03bb95844d1f", false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_return_product_group_list, container, false);
        categoriesImageView = view.findViewById(R.id.categories_image_view);
        groupBar = view.findViewById(R.id.group_bar);
        if (isGroupBarOpen())
            groupBar.setVisibility(View.VISIBLE);
        else
            groupBar.setVisibility(View.GONE);
        categoriesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleGroupBar();
            }
        });
        view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().popFragment();
            }
        });
        view.findViewById(R.id.all_products_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsAdapter.deselect();
                groupIds = new UUID[]{};
                refreshAdapter();
            }
        });
        reportView = (ReportView) view.findViewById(R.id.product_list_fragment);
        setupAdapter(savedInstanceState);
        refreshAdapter();
        searchEditText = (EditText) view.findViewById(R.id.product_search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            String before = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String after = editable.toString();
                if (after.equals(before))
                    return;
                last_text_edit = System.currentTimeMillis();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (System.currentTimeMillis() > (last_text_edit + delay)) {
                            String str = null;
                            if (after.length() > 1)
                                str = after;
                            keyWord = str;
                            refreshAdapter();
                        }
                    }
                }, delay + 10);
                before = after;
            }
        });
        createExpandableRecyclerAdapter();
        ExpandableRecyclerView expandableRecyclerView = (ExpandableRecyclerView) view.findViewById(R.id.group_recycler_view);
        expandableRecyclerView.setAdapter(groupsAdapter);
        return view;
    }

    private boolean isGroupBarOpen() {
        SharedPreferences sp = getContext().getSharedPreferences("RETURN_PRODUCT_GROUP", Context.MODE_PRIVATE);
        return sp.getBoolean("IS_OPEN", true);
    }

    private void toggleGroupBar() {
        SharedPreferences sp = getContext().getSharedPreferences("RETURN_PRODUCT_GROUP", Context.MODE_PRIVATE);
        boolean isOpen = sp.getBoolean("IS_OPEN", true);
        if (isOpen) {
            groupBar.setVisibility(View.GONE);
            sp.edit().putBoolean("IS_OPEN", false).apply();
        } else {
            groupBar.setVisibility(View.VISIBLE);
            sp.edit().putBoolean("IS_OPEN", true).apply();
        }
    }

    private void refreshAdapter(int position, Object item) {
        if (isRef) {
            if (filteredOldInvoiceDetailViewModels.size() > position)
                filteredOldInvoiceDetailViewModels.set(position, (OldInvoiceDetailViewModel) item);
            oldInvoiceDetailViewModels.set(map.get(position), (OldInvoiceDetailViewModel) item);
            adapter.set(position, (OldInvoiceDetailViewModel) item);
        } else {
            if (filteredProductReturnWithoutRefModels.size() > position)
                filteredProductReturnWithoutRefModels.set(position, (ProductReturnWithoutRefModel) item);
            productReturnWithoutRefModels.set(map.get(position), (ProductReturnWithoutRefModel) item);
            adapter.set(position, (ProductReturnWithoutRefModel) item);
        }
        adapter.notifyDataSetChanged();
    }


    HashMap<Integer, Integer> map = new HashMap<>();

    private void refreshAdapter() {
        keyWord = VasHelperMethods.persian2Arabic(keyWord);
        keyWord = VasHelperMethods.convertToEnglishNumbers(keyWord);
        map = new HashMap<>();
        int i = 0;
        int j = 0;
        if (isRef) {
            filteredOldInvoiceDetailViewModels.clear();
            for (OldInvoiceDetailViewModel item :
                    oldInvoiceDetailViewModels) {
                if (checkGroup(item.ProductGroupId) && checkProductNameOrCode(item.ProductName, item.ProductCode)) {
                    filteredOldInvoiceDetailViewModels.add(item);
                    map.put(i, j);
                    i++;
                }
                j++;
            }
        } else {
            filteredProductReturnWithoutRefModels.clear();
            for (ProductReturnWithoutRefModel item :
                    productReturnWithoutRefModels) {
                if (checkGroup(item.ProductGroupId) && checkProductNameOrCode(item.ProductName, item.ProductCode)) {
                    filteredProductReturnWithoutRefModels.add(item);
                    map.put(i, j);
                    i++;
                }
                j++;
            }
        }

        if (isRef) {
            adapter.clear();
            adapter.addAll(filteredOldInvoiceDetailViewModels);
        } else {
            adapter.clear();
            adapter.addAll(filteredProductReturnWithoutRefModels);
        }
    }

    private boolean checkProductNameOrCode(String productName, String productCode) {
        if (keyWord == null || keyWord.isEmpty())
            return true;

        keyWord = keyWord.toLowerCase();
        if (productName != null && productName.toLowerCase().contains(keyWord))
            return true;
        else if (productCode != null && productCode.toLowerCase().contains(keyWord))
            return true;
        return false;
    }

    private boolean checkGroup(UUID productGroupId) {
        if (groupIds == null || groupIds.length == 0)
            return true;

        for (UUID groupId : groupIds) {
            if (productGroupId != null && productGroupId.equals(groupId))
                return true;
        }
        return false;
    }

    private void setupAdapter(Bundle savedInstanceState) {
        if (isRef) {
            oldInvoiceDetailViewModels = new OldInvoiceDetailViewManager(getContext()).getItems(OldInvoiceDetailViewManager.getAllCustomerLines(customerId));
            filteredOldInvoiceDetailViewModels.addAll(oldInvoiceDetailViewModels);
            adapter = new OldInvoiceDetailReportAdapter(getVaranegarActvity());
            adapter.create(filteredOldInvoiceDetailViewModels, savedInstanceState);
        } else {
            productReturnWithoutRefModels = new ProductReturnWithoutRefManager(getContext()).getItems(ProductReturnWithoutRefManager.getAll(customerId));
            filteredProductReturnWithoutRefModels.addAll(productReturnWithoutRefModels);
            adapter = new ProductReturnWithoutRefListAdapter(getVaranegarActvity(), false);
            adapter.create(filteredProductReturnWithoutRefModels, savedInstanceState);
        }
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.addOnItemClickListener(new ContextMenuItem() {
            @Override
            public boolean isAvailable(int position) {
                return true;
//                if (isRef) {
//                    OldInvoiceDetailViewModel oldInvoiceDetailViewModel = ((OldInvoiceDetailReportAdapter) adapter).get(position);
//                    return !oldInvoiceDetailViewModel.PrizeType;
//                } else
//                    return true;
            }

            @Override
            public String getName(int position) {
                return null;
            }

            @Override
            public int getIcon(int position) {
                return 0;
            }

            @Override
            public void run(final int position) {
                final ProductModel productModel;
                Currency price = null;
                CustomerCallReturnViewModel callReturnViewModel = null;
                CustomerCallReturnViewManager manager = new CustomerCallReturnViewManager(getContext());
                final UUID invoiceId;
                final String invoiceNo;
                final UUID stockId;
                final String referenceNo;
                if (isRef) {
                    OldInvoiceDetailViewModel oldInvoiceDetailViewModel = ((OldInvoiceDetailReportAdapter) adapter).get(position);
                    invoiceId = oldInvoiceDetailViewModel.SaleId;
                    invoiceNo = oldInvoiceDetailViewModel.SaleNo;
                    referenceNo = oldInvoiceDetailViewModel.Item;
                    invoiceQty = oldInvoiceDetailViewModel.TotalQty;
                    stockId = oldInvoiceDetailViewModel.StockId;
                    productModel = new ProductManager(getContext()).getItem(oldInvoiceDetailViewModel.ProductId);
                    if (productModel == null)
                        throw new NullPointerException("Product Id not found");
                    callReturnViewModel = manager.getLine(customerId, oldInvoiceDetailViewModel.ProductId, invoiceId, false);
                    price = oldInvoiceDetailViewModel.UnitPrice;
                } else {
                    ProductReturnWithoutRefModel returnViewModel = ((ProductReturnWithoutRefListAdapter) adapter).get(position);
                    productModel = new ProductManager(getContext()).getItem(returnViewModel.UniqueId);
                    if (productModel == null)
                        throw new NullPointerException("Product Id not found");
                    invoiceId = null;
                    invoiceNo = "0";
                    referenceNo = null;
                    stockId = productModel.ReturnStockUniqueId;
                    callReturnViewModel = manager.getLine(customerId, productModel.UniqueId, invoiceId, false);
                    CustomerPriceModel customerPrice = Linq.findFirst(priceList, new Linq.Criteria<CustomerPriceModel>() {
                        @Override
                        public boolean run(CustomerPriceModel priceModel) {
                            return priceModel.CustomerUniqueId.equals(customerId) && priceModel.ProductUniqueId.equals(productModel.UniqueId);
                        }
                    });
                    if (customerPrice != null)
                        price = customerPrice.Price;
                }

                try {
                    ReturnCalculatorForm calculatorForm = new ReturnCalculatorForm();
                    CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                    if (callReturnViewModel == null)
                        calculatorForm.setArguments(productModel, calculatorHelper.generateCalculatorUnits(productModel.UniqueId, ProductType.isForReturn, true), price, isRef);
                    else {
                        calculatorForm.setArguments(getContext(), productModel, calculatorHelper.generateCalculatorUnits(productModel.UniqueId, ProductType.isForReturn, true), callReturnViewModel, price, isRef);
                    }
                    calculatorForm.onItemDeleted = new ReturnCalculatorForm.OnItemDeleted() {
                        @Override
                        public void run(ReturnCalculatorItem returnCalculatorItem) {
                            ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
                            try {
                                returnLinesManager.removeReturnLineModel(returnCalculatorItem);
                                Timber.i("Deleting an item");
                                if (isRef) {
                                    OldInvoiceDetailViewModel oldInvoiceDetailViewModel = new OldInvoiceDetailViewManager(getContext()).getLine(customerId, productModel.UniqueId, invoiceId);
                                    refreshAdapter(position, oldInvoiceDetailViewModel);
                                } else {
                                    ProductReturnWithoutRefModel productReturnWithoutRefModel = new ProductReturnWithoutRefManager(getContext()).getLine(customerId, productModel.UniqueId);
                                    refreshAdapter(position, productReturnWithoutRefModel);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                Timber.e(ex, "Error deleting return item");
                                getVaranegarActvity().showSnackBar(R.string.error_saving_request, MainVaranegarActivity.Duration.Short);
                            }
                        }
                    };
                    calculatorForm.onCalcFinish = new ReturnCalculatorForm.OnCalcFinish() {
                        @Override
                        public void run(List<ReturnCalculatorItem> returnCalculatorItems, Currency price) {
                            if (isRef) {
                                final BigDecimal[] total = {BigDecimal.ZERO};
                                Linq.forEach(returnCalculatorItems, new Linq.Consumer<ReturnCalculatorItem>() {
                                    @Override
                                    public void run(ReturnCalculatorItem item) {
                                        total[0] = total[0].add(item.getTotalQty());
                                    }
                                });

                                if (total[0].compareTo(invoiceQty) > 0) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle(R.string.error);
                                    builder.setMessage(R.string.return_qty_is_larger_than_total_qty);
                                    builder.setPositiveButton(R.string.ok, null);
                                    builder.show();
                                    return;
                                }
                            }
                            CustomerCallReturnManager returnManger = new CustomerCallReturnManager(getContext());
                            try {
                                returnManger.addOrUpdateCallReturn(false,
                                        returnCalculatorItems,
                                        customerId,
                                        invoiceId,
                                        stockId,
                                        price,
                                        null,
                                        invoiceNo,
                                        dealerId,
                                        null,
                                        withoutRefReplacementRegistration || withRefReplacementRegistration,
                                        referenceNo,
                                        null);
                                if (isRef) {
                                    OldInvoiceDetailViewModel oldInvoiceDetailViewModel = new OldInvoiceDetailViewManager(getContext()).getLine(customerId, productModel.UniqueId, invoiceId);
                                    refreshAdapter(position, oldInvoiceDetailViewModel);
                                } else {
                                    ProductReturnWithoutRefModel productReturnWithoutRefModel = new ProductReturnWithoutRefManager(getContext()).getLine(customerId, productModel.UniqueId);
                                    refreshAdapter(position, productReturnWithoutRefModel);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception ex) {
                                Timber.e(ex);
                                getVaranegarActvity().showSnackBar(R.string.error, MainVaranegarActivity.Duration.Short);
                            }
                        }
                    };
                    calculatorForm.show(getChildFragmentManager(), "b34bd9dd-797b-4dc2-9d68-57e3cf84e1fb");
                } catch (ProductUnitViewManager.UnitNotFoundException e) {
                    getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                    Timber.e(e, "product group not found in return product group fragment");
                }
            }
        });
        reportView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    protected void createExpandableRecyclerAdapter() {
        groupsAdapter = new ExpandableRecyclerAdapter<ProductGroupModel, ProductGroupModel>(
                getVaranegarActvity(), new ProductGroupManager(getContext()).getParentItems(ProductType.isForReturn),
                new ExpandableRecyclerAdapter.Children<ProductGroupModel, ProductGroupModel>() {
                    @Override
                    public List<ProductGroupModel> onCreate(ProductGroupModel parentItem) {
                        return new ProductGroupModelRepository().getItems(ProductGroupManager.getSubGroups(parentItem.UniqueId, ProductType.isForReturn));
                    }
                }) {
            @Override
            public BaseViewHolder<ProductGroupModel> onCreateParent(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
                return new ProductGroupViewHolder(view, this, getContext());
            }

            @Override
            public BaseViewHolder<ProductGroupModel> onCreateChild(ViewGroup parent, ChildRecyclerAdapter<ProductGroupModel> adapter) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false);
                return new ChildProductGroupViewHolder(itemView, adapter, getContext());
            }

        };
        groupsAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<ProductGroupModel>() {
            @Override
            public void run(int position) {
                ProductGroupModel item = groupsAdapter.get(position);
                groupIds = new ProductGroupManager(getContext()).getSubGroupIds(item.UniqueId, ProductType.isForReturn);
                refreshAdapter();
            }
        });
        groupsAdapter.setOnChildItemClickListener(new ExpandableRecyclerAdapter.OnChildItemClick<ProductGroupModel>() {
            @Override
            public void onClick(int position, ProductGroupModel clickedItem) {
                groupIds = new UUID[]{clickedItem.UniqueId};
                refreshAdapter();
            }
        });
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }
}