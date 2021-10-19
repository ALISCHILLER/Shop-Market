package com.varanegar.hotsales.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.recycler.expandablerecycler.ChildRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerView;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.framework.validation.ConstraintViolation;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.hotsales.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.productrequest.RequestLineManager;
import com.varanegar.vaslibrary.manager.productrequest.RequestLineQtyManager;
import com.varanegar.vaslibrary.manager.productrequest.RequestLineViewManager;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineQtyModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineView;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineViewModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineViewModelRepository;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModelRepository;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.viewholders.ChildProductGroupViewHolder;
import com.varanegar.vaslibrary.ui.viewholders.ProductGroupViewHolder;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 3/25/2018.
 */

public class RequestProductGroupFragment extends VaranegarFragment {

    private ExpandableRecyclerAdapter<ProductGroupModel, ProductGroupModel> groupsAdapter;


    Query productsQuery;
    private EditText searchEditText;
    private SimpleReportAdapter<RequestLineViewModel> requestAdapter;


    private void onAddItem(RequestLineViewModel requestLineViewModel, List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
        RequestLineManager requestLineManager = new RequestLineManager(getContext());
        try {
            requestLineManager.addOrUpdateQty(discreteUnits, bulkUnit);
            requestAdapter.refresh();
        } catch (ValidationException e) {
            Timber.e(e);
        } catch (DbException e) {
            Timber.e(e);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_product_group_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().popFragment();
            }
        });
        view.findViewById(R.id.all_products_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetOptions();
            }
        });
        setupGroupsList(view);
        setupSearch(view);

        requestAdapter = new SimpleReportAdapter<RequestLineViewModel>(getVaranegarActvity(), RequestLineViewModel.class) {
            @Override
            public void bind(ReportColumns columns, RequestLineViewModel entity) {
                bindRowNumber(columns);
                columns.add(bind(entity, RequestLineView.ProductCode, getString(R.string.product_code)).setSortable());
                columns.add(bind(entity, RequestLineView.ProductName, getString(R.string.product_name)).setWeight(2).setSortable());
                columns.add(bind(entity, RequestLineView.Qty, getString(R.string.qty)));
                columns.add(bind(entity, RequestLineView.TotalQty, getString(R.string.total_qty)).calcTotal().setSortable());
                columns.add(bind(entity, RequestLineView.UnitPrice, getString(R.string.unit_price)).setSortable());
                columns.add(bind(entity, RequestLineView.TotalPrice, getString(R.string.total_price)).calcTotal().setSortable());
            }

        };
        requestAdapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        requestAdapter.create(new RequestLineViewModelRepository(), RequestLineViewManager.getLines(null), savedInstanceState);
        requestAdapter.addOnItemClickListener(new ContextMenuItem() {
            @Override
            public boolean isAvailable(int position) {
                return true;
            }

            @Override
            public String getName(int posiotn) {
                return getString(R.string.edit);
            }

            @Override
            public int getIcon(int position) {
                return R.drawable.ic_mode_edit_black_24dp;
            }

            @Override
            public void run(int position) {
                final RequestLineViewModel selectedItem = requestAdapter.get(position);
                RequestCalculatorForm requestCalculatorForm = new RequestCalculatorForm();
                try {
                    final ProductModel productModel = new ProductManager(getContext()).getItem(selectedItem.UniqueId);
                    if (productModel == null)
                        throw new NullPointerException("Product id " + selectedItem.UniqueId + " not found");
                    CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                    RequestLineQtyManager requestLineQtyManager = new RequestLineQtyManager(getContext());
                    List<RequestLineQtyModel> requestLineQtyModels = requestLineQtyManager.getQtyLines(selectedItem.RequestLineUniqueId);
                    BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(new RequestLineManager(getContext()).getRequestLine(productModel.UniqueId));
                    requestCalculatorForm.setArguments(productModel, calculatorHelper.generateCalculatorUnits(productModel.UniqueId, requestLineQtyModels, bulkUnit, ProductType.isForSale));
                    requestCalculatorForm.onCalcFinish = new RequestCalculatorForm.OnCalcFinish() {
                        @Override
                        public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
                            onAddItem(selectedItem, discreteUnits, bulkUnit);
                        }
                    };
                    requestCalculatorForm.show(getChildFragmentManager(), "9d0ab16e-09b2-4bc4-8e6e-e6ef7252ee43");
                } catch (ProductUnitViewManager.UnitNotFoundException e) {
                    getVaranegarActvity().showSnackBar(R.string.no_unit_for_product, MainVaranegarActivity.Duration.Short);
                    Timber.e(e);
                }
            }
        });
        requestAdapter.addOnItemClickListener(new ContextMenuItem() {
            @Override
            public boolean isAvailable(int position) {
                return requestAdapter.get(position).RequestLineUniqueId != null;
            }

            @Override
            public String getName(int posiotn) {
                return getString(R.string.remove);
            }

            @Override
            public int getIcon(int position) {
                return R.drawable.ic_delete_forever_black_24dp;
            }

            @Override
            public void run(int position) {
                final RequestLineViewModel selectedItem = requestAdapter.get(position);
                RequestLineManager requestLineManager = new RequestLineManager(getContext());
                try {
                    requestLineManager.deleteProduct(selectedItem.UniqueId);
                    requestAdapter.refresh();
                } catch (DbException e) {
                    Timber.e(e);
                }
            }
        });
        ReportView reportView = (ReportView) view.findViewById(R.id.report_view);
        reportView.setAdapter(requestAdapter);
    }

    private void resetOptions() {
        groupsAdapter.deselect();
        productsQuery = RequestLineViewManager.searchQuery(null, null);
        requestAdapter.refresh(productsQuery);
    }


    private void setupSearch(View view) {
        searchEditText = (EditText) view.findViewById(R.id.product_search_edit_text);
        final ImageView clearSearchImageView = (ImageView) view.findViewById(R.id.clear_search_image_view);
        clearSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            String before = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String after = s.toString();
                if (after.isEmpty())
                    clearSearchImageView.setVisibility(View.GONE);
                else
                    clearSearchImageView.setVisibility(View.VISIBLE);
                if (after.equals(before))
                    return;
                if (after.length() > 1) {
                    productsQuery = RequestLineViewManager.searchQuery(after, null);
                    requestAdapter.refresh(productsQuery);
                } else if (before.length() > 1) {
                    productsQuery = RequestLineViewManager.searchQuery(after, null);
                    requestAdapter.refresh(productsQuery);
                }
                before = after;
            }
        });
    }

    private void setupGroupsList(View view) {
        ExpandableRecyclerView mainRecyclerView = (ExpandableRecyclerView) view.findViewById(R.id.groups_recycler_view);
        groupsAdapter =
                new ExpandableRecyclerAdapter<ProductGroupModel, ProductGroupModel>(
                        getVaranegarActvity(), new ProductGroupManager(getContext()).getParentItems(ProductType.isForRequest),
                        new ExpandableRecyclerAdapter.Children<ProductGroupModel, ProductGroupModel>() {
                            @Override
                            public List<ProductGroupModel> onCreate(ProductGroupModel parentItem) {
                                return new ProductGroupModelRepository().getItems(ProductGroupManager.getSubGroups(parentItem.UniqueId, ProductType.isForRequest));
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
                UUID[] ids = new ProductGroupManager(getContext()).getSubGroupIds(item.UniqueId, ProductType.isForRequest);
                productsQuery = RequestLineViewManager.searchQuery(searchEditText.getText().toString(), ids);
                requestAdapter.refresh(productsQuery);
            }
        });
        groupsAdapter.setOnChildItemClickListener(new ExpandableRecyclerAdapter.OnChildItemClick<ProductGroupModel>() {
            @Override
            public void onClick(int position, ProductGroupModel clickedItem) {
                getVaranegarActvity().closeDrawer();
                if (clickedItem.UniqueId == null) {
                    Timber.wtf("unique id of product group is null");
                } else {
                    productsQuery = RequestLineViewManager.searchQuery(searchEditText.getText().toString(), new UUID[]{clickedItem.UniqueId});
                    requestAdapter.refresh(productsQuery);
                }
            }
        });
        mainRecyclerView.setAdapter(groupsAdapter);
    }

}
