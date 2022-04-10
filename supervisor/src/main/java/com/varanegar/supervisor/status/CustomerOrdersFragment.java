package com.varanegar.supervisor.status;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.java.util.Currency;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.model.ProductModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderLineOrderQtyDetailViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderLineViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallViewModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/10/2018.
 */

public class CustomerOrdersFragment extends CustomerCallTabFragment {
    private PairedItems paymentTypePairedItems;
    private PairedItems orderTypeNamePairedItems;
    private BaseRecyclerView masterRecyclerView;
    private BaseSelectionRecyclerAdapter<CustomerCallOrderViewModel> callOrdersAdapter;
    private BaseRecyclerView itemsRecyclerView;
    private View detailLayout;
    private TextView refNumberTextView;
    private View addProductView;
    private ArrayList<ProductModel> productList = new ArrayList<>();
    private View loadingProductsProgressBar;
    private View addProductImageView;
    private boolean addProductIsEnabled;
    private View listSectionView;
    private Button previewBtn;
    private Button customerPreviewBtn;
    private View errorTextView;
    private View descriptionLayout;
    private TextView descriptionTextView;

    interface IOnProductUpdated {
        void done();
    }

    IOnProductUpdated onProductUpdated;

    private void updateProductList() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                SupervisorApi api = new SupervisorApi(getContext());
                api.runWebRequest(api.getProducts(null), new WebCallBack<List<ProductModel>>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(List<ProductModel> result, Request request) {
                        productList.addAll(result);
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onProductUpdated.done();
                                }
                            });
                        }
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        WebApiErrorBody.log(error, getContext());
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Timber.e(t);
                    }
                });

            }
        }).start();
    }

    @Override
    public void refresh(final CustomerCallViewModel customerCallViewModel) {
        super.refresh(customerCallViewModel);
        if (customerCallViewModel == null)
            return;
        if (customerCallViewModel.CustomerCallOrders == null || customerCallViewModel.CustomerCallOrders.size() == 0)
            customerCallViewModel.CustomerCallOrders = new ArrayList<>();


        if (customerCallViewModel.CustomerCallOrders.size() == 0 && listSectionView != null) {
            detailLayout.setVisibility(View.GONE);
            listSectionView.setVisibility(View.GONE);
            errorTextView.setVisibility(View.VISIBLE);
            previewBtn.setVisibility(View.GONE);
        }

        if (customerCallViewModel.CustomerCallOrders.size() == 1 && listSectionView != null)
            detailLayout.setVisibility(View.VISIBLE);

        if (customerCallViewModel.CustomerCallOrders.size() > 1 && listSectionView != null)
            listSectionView.setVisibility(View.VISIBLE);

        callOrdersAdapter = new BaseSelectionRecyclerAdapter<CustomerCallOrderViewModel>(getVaranegarActvity(), customerCallViewModel.CustomerCallOrders, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_call_order_view_item, parent, false);
                return new CustomerCallOrderViewHolder(view, callOrdersAdapter, getContext());
            }
        };

        callOrdersAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                refreshDetail(callOrdersAdapter.get(position));
            }
        });
        masterRecyclerView.setAdapter(callOrdersAdapter);
        if (callOrdersAdapter.size() > 0) {
            callOrdersAdapter.select(0);
            refreshDetail(callOrdersAdapter.getSelectedItem());
        } else refreshDetail(null);

        customerPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerSummarySlidingDialog dialog = new CustomerSummarySlidingDialog();
                Bundle bundle = new Bundle();
                bundle.putString("CUSTOMER_ID", customerCallViewModel.CustomerUniqueId.toString());
                dialog.setArguments(bundle);
                dialog.show(getChildFragmentManager(), "CustomerSummarySlidingDialog");
            }
        });
    }

    private void refreshDetail(final CustomerCallOrderViewModel customerCallOrderViewModel) {
        if (customerCallOrderViewModel == null) {
            previewBtn.setVisibility(View.GONE);
            detailLayout.setVisibility(View.INVISIBLE);
            return;
        }


        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderPreviewSlidingDialog dialog = new OrderPreviewSlidingDialog();
                dialog.customerCallOrderViewModel = customerCallOrderViewModel;
                dialog.customerCallViewModel = customerCallViewModel;
                dialog.show(getChildFragmentManager(), "OrderPreviewSlidingDialog");
            }
        });

        if (customerCallOrderViewModel.BackOfficeOrderNoCollection != null && !customerCallOrderViewModel.BackOfficeOrderNoCollection.isEmpty())
            refNumberTextView.setText(customerCallOrderViewModel.LocalPaperNo + " " + getString(R.string.back_office_ref_no) + ":" + customerCallOrderViewModel.BackOfficeOrderNoCollection);
        else
            refNumberTextView.setText(customerCallOrderViewModel.LocalPaperNo);

        if (customerCallOrderViewModel.Comment != null && !customerCallOrderViewModel.Comment.isEmpty()) {
            descriptionLayout.setVisibility(View.VISIBLE);
            descriptionTextView.setText(customerCallOrderViewModel.Comment);
        } else
            descriptionLayout.setVisibility(View.GONE);

        paymentTypePairedItems.setValue(customerCallOrderViewModel.OrderPaymentTypeName);
        orderTypeNamePairedItems.setValue(customerCallOrderViewModel.OrderTypeName);

        final BaseRecyclerAdapter<CustomerCallOrderLineViewModel> linesAdapter = new BaseRecyclerAdapter<CustomerCallOrderLineViewModel>(getVaranegarActvity(), customerCallOrderViewModel.OrderLines) {
            @NonNull
            @Override
            public BaseViewHolder<CustomerCallOrderLineViewModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.supervisor_order_line_layout, parent, false);
                return new OrderLineViewHolder(onCallChanged, itemView, this, getContext(), customerCallViewModel.isReadonly());
            }
        };
        itemsRecyclerView.setAdapter(linesAdapter);
        if (customerCallViewModel.isReadonly())
            addProductView.setVisibility(View.GONE);
        else
            addProductView.setVisibility(View.VISIBLE);

        addProductView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addProductIsEnabled)
                    return;
                final SearchBox<ProductModel> searchBox = new SearchBox<>();
                searchBox.setItems(productList, new SearchBox.SearchMethod<ProductModel>() {
                    @Override
                    public boolean onSearch(ProductModel item, String text) {
                        text = VasHelperMethods.persian2Arabic(text);
                        text = VasHelperMethods.convertToEnglishNumbers(text);
                        return item.ProductName.contains(text) || item.ProductCode.contains(text);
                    }
                });
                searchBox.setOnItemSelectedListener(new SearchBox.OnItemSelectedListener<ProductModel>() {
                    @Override
                    public void run(int position, final ProductModel item) {
                        searchBox.dismiss();
                        boolean alreadyExists = Linq.exists(customerCallOrderViewModel.OrderLines, new Linq.Criteria<CustomerCallOrderLineViewModel>() {
                            @Override
                            public boolean run(CustomerCallOrderLineViewModel line) {
                                return item.ProductUniqueId.equals(line.ProductUniqueId);
                            }
                        });
                        if (!alreadyExists) {
                            CustomerCallOrderLineViewModel newOrderLineViewModel = new CustomerCallOrderLineViewModel();
                            newOrderLineViewModel.UniqueId = UUID.randomUUID();
                            newOrderLineViewModel.CustomerCallOrderUniqueId = customerCallOrderViewModel.UniqueId;
                            newOrderLineViewModel.UnitPrice = Currency.ZERO;
                            newOrderLineViewModel.RequestTotalPrice = Currency.ZERO;
                            newOrderLineViewModel.ProductUniqueId = item.ProductUniqueId;
                            newOrderLineViewModel.ProductCode = item.ProductCode;
                            newOrderLineViewModel.ProductName = item.ProductName;
                            newOrderLineViewModel.IsEditedBySupervisor = true;
                            newOrderLineViewModel.RequestQty = "1";
                            Collections.sort(item.ProductUnits, new Comparator<CustomerCallOrderLineOrderQtyDetailViewModel>() {
                                @Override
                                public int compare(CustomerCallOrderLineOrderQtyDetailViewModel o1, CustomerCallOrderLineOrderQtyDetailViewModel o2) {
                                    return o1.ConvertFactor.compareTo(o2.ConvertFactor);
                                }
                            });

                            newOrderLineViewModel.CustomerCallOrderLineOrderQtyDetails = item.ProductUnits;
                            if (newOrderLineViewModel.CustomerCallOrderLineOrderQtyDetails.size() >= 1) {
                                for (CustomerCallOrderLineOrderQtyDetailViewModel qty :
                                        newOrderLineViewModel.CustomerCallOrderLineOrderQtyDetails) {
                                    qty.CustomerCallOrderLineUniqueId = newOrderLineViewModel.UniqueId;
                                    qty.UniqueId = UUID.randomUUID();
                                }
                                CustomerCallOrderLineOrderQtyDetailViewModel smallQty = newOrderLineViewModel.CustomerCallOrderLineOrderQtyDetails.get(0);
                                smallQty.Qty = BigDecimal.ONE;
                                customerCallOrderViewModel.OrderLines.add(newOrderLineViewModel);
                                linesAdapter.notifyDataSetChanged();
                                runOnCallChanged();
                            }
                        }
                    }
                });
                searchBox.show(getChildFragmentManager(), "SearchBox<ProductModel>");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_orders_layout, container, false);
        previewBtn = view.findViewById(R.id.preview_btn);
        customerPreviewBtn = view.findViewById(R.id.customer_preview_btn);


        detailLayout = view.findViewById(R.id.detail_layout);
        refNumberTextView = view.findViewById(R.id.ref_number_text_view);
        orderTypeNamePairedItems = view.findViewById(R.id.order_type_name_paired_items);
        paymentTypePairedItems = view.findViewById(R.id.payment_type_paired_items);
        itemsRecyclerView = view.findViewById(R.id.items_recycler_view);
        masterRecyclerView = view.findViewById(R.id.master_recycler_view);
        addProductView = view.findViewById(R.id.add_product_view);
        loadingProductsProgressBar = view.findViewById(R.id.loading_products_progress_bar);
        addProductImageView = view.findViewById(R.id.add_product_image_view);
        listSectionView = view.findViewById(R.id.list_section_view);
        errorTextView = view.findViewById(R.id.error_text_view);
        descriptionLayout = view.findViewById(R.id.description_layout);
        descriptionTextView = view.findViewById(R.id.description_text_view);

        onProductUpdated = new IOnProductUpdated() {
            @Override
            public void done() {
                if (productList != null && productList.size() > 0) {
                    addProductIsEnabled = true;
                    loadingProductsProgressBar.setVisibility(View.GONE);
                    addProductImageView.setVisibility(View.VISIBLE);
                }
            }
        };
        updateProductList();
        if (!isLandscape())
            masterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        return view;
    }

    private class CustomerCallOrderViewHolder extends BaseViewHolder<CustomerCallOrderViewModel> {

        private final TextView refNumberTextView;
        private final TextView paymentTypeTextView;
        private final TextView backOfficeRefNumberTextView;

        public CustomerCallOrderViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallOrderViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            refNumberTextView = itemView.findViewById(R.id.ref_number_text_view);
            paymentTypeTextView = itemView.findViewById(R.id.payment_type_text_view);
            backOfficeRefNumberTextView = itemView.findViewById(R.id.backoffice_ref_number_text_view);

        }

        @Override
        public void bindView(final int position) {
            final BaseSelectionRecyclerAdapter selectionRecyclerAdapter = ((BaseSelectionRecyclerAdapter) recyclerAdapter);
            CustomerCallOrderViewModel customerCallOrderViewModel = recyclerAdapter.get(position);
            if (selectionRecyclerAdapter.getSelectedPosition() == position) {
                itemView.setBackgroundResource(R.color.grey_light_light);
                itemView.findViewById(R.id.indicator).setVisibility(View.VISIBLE);
            } else {
                itemView.setBackgroundResource(R.color.white);
                itemView.findViewById(R.id.indicator).setVisibility(View.INVISIBLE);
            }
            refNumberTextView.setText(customerCallOrderViewModel.LocalPaperNo);
            paymentTypeTextView.setText(customerCallOrderViewModel.OrderPaymentTypeName);
            if (customerCallOrderViewModel.BackOfficeOrderNoCollection != null && !customerCallOrderViewModel.BackOfficeOrderNoCollection.isEmpty())
                backOfficeRefNumberTextView.setText(customerCallOrderViewModel.BackOfficeOrderNoCollection);
            else
                backOfficeRefNumberTextView.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectionRecyclerAdapter.notifyItemClicked(position);
                }
            });
        }
    }

}
