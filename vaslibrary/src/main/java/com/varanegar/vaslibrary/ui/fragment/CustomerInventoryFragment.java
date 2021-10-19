package com.varanegar.vaslibrary.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerInventoryManager;
import com.varanegar.vaslibrary.manager.CustomerInventoryQtyManager;
import com.varanegar.vaslibrary.manager.ProductInventoryManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryModel;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryQtyModel;
import com.varanegar.vaslibrary.model.customerinventory.ProductInventoryModel;
import com.varanegar.vaslibrary.model.customerinventory.ProductInventoryModelRepository;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CustomerInventoryCalculatorForm;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 9/12/2017.
 */

public class CustomerInventoryFragment extends VisitFragment {
    private UUID customerId;
    private CustomerModel customer;
    BaseRecyclerAdapter<ProductInventoryModel> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        customerId = UUID.fromString(getStringArgument("67485d97-5f0e-4b1e-9677-0798dec7a587"));
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.unConfirmAllCalls(customerId);
            CustomerManager customerManager = new CustomerManager(getContext());
            customer = customerManager.getItem(customerId);
            View view = inflater.inflate(R.layout.fragment_customer_inventory, container, false);
            BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.customer_inventory_report_view);
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel sysConfig = sysConfigManager.read(ConfigKey.CustomerStockCheckType, SysConfigManager.cloud);
            if (SysConfigManager.compare(sysConfig, ProductInventoryManager.CustomerStockCheckType.Boolean)) {
                view.findViewById(R.id.header_linear_layout_with_qty).setVisibility(View.GONE);
                view.findViewById(R.id.header_linear_layout).setVisibility(View.VISIBLE);
                adapter = new BaseRecyclerAdapter<ProductInventoryModel>(
                        getVaranegarActvity(),
                        new ProductInventoryModelRepository(),
                        ProductInventoryManager.getAll(customerId)
                ) {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer_inventory, parent, false);
                        CustomerInventoryViewHolder viewHolder = new CustomerInventoryViewHolder(view, this, getContext());
                        return viewHolder;
                    }
                };
            } else {
                view.findViewById(R.id.header_linear_layout_with_qty).setVisibility(View.VISIBLE);
                view.findViewById(R.id.header_linear_layout).setVisibility(View.GONE);
                adapter = new BaseRecyclerAdapter<ProductInventoryModel>(
                        getVaranegarActvity(),
                        new ProductInventoryModelRepository(),
                        ProductInventoryManager.getAll(customerId)
                ) {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_customer_inventory_with_qty, parent, false);
                        CustomerInventoryWithQtyViewHolder viewHolder = new CustomerInventoryWithQtyViewHolder(view, this, getContext());
                        return viewHolder;
                    }
                };
            }
            adapter.refresh();
            recyclerView.setAdapter(adapter);
            EditText searchEditText = (EditText) view.findViewById(R.id.search_edit_text);
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    adapter.refresh(ProductInventoryManager.getAll(customerId, s.toString()));
                }
            });
            SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
            toolbar.setTitle(getString(R.string.customer_inventory) + " " + getString(R.string.customer_name_label) + " " + customer.CustomerName);
            toolbar.setOnBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptSave();
                }
            });
            return view;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private void promptSave() {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        try {
            callManager.saveCustomerInventoryCall(customerId);
            getVaranegarActvity().popFragment();
        } catch (Exception e) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        promptSave();
    }

    public void setCustomerId(UUID customerId) {
        addArgument("67485d97-5f0e-4b1e-9677-0798dec7a587", customerId.toString());
    }

    @NonNull
    @Override
    protected UUID getCustomerId() {
        return customerId;
    }

    class CustomerInventoryViewHolder extends BaseViewHolder<ProductInventoryModel> {

        private final CheckBox isSoldCheckBox;
        private final CheckBox isAvailableCheckBox;
        private final TextView rowTextView;
        private final TextView productCodeTextView;
        private final TextView productNameTextView;
        private final CustomerInventoryManager manager;

        public CustomerInventoryViewHolder(View itemView, BaseRecyclerAdapter<ProductInventoryModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            isSoldCheckBox = (CheckBox) itemView.findViewById(R.id.is_sold_check_box);
            isAvailableCheckBox = (CheckBox) itemView.findViewById(R.id.is_available_check_box);
            productCodeTextView = (TextView) itemView.findViewById(R.id.product_code_text_view);
            productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
            rowTextView = (TextView) itemView.findViewById(R.id.row_text_view);
            manager = new CustomerInventoryManager(getContext());
        }

        @Override
        public void bindView(final int position) {
            final ProductInventoryModel item = adapter.get(position);
            productCodeTextView.setText(item.ProductCode);
            productNameTextView.setText(item.ProductName);
            rowTextView.setText(String.valueOf(position + 1));
            isAvailableCheckBox.setChecked(item.IsAvailable);
            isSoldCheckBox.setChecked(item.IsSold);
            isSoldCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSold = isSoldCheckBox.isChecked();
                    if (isSold)
                        update(item, true, item.IsAvailable);
                    else
                        update(item, false, false);
                }
            });
            isAvailableCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isAvailable = isAvailableCheckBox.isChecked();
                    if (isAvailable)
                        update(item, true, true);
                    else
                        update(item, item.IsSold, false);
                }
            });
        }

        private void update(final ProductInventoryModel item, final boolean isSold, final boolean isAvailable) {
            CustomerInventoryModel inventoryModel = manager.getLine(item.UniqueId, customerId);
            if (inventoryModel != null) {
                inventoryModel.IsSold = isSold;
                inventoryModel.IsAvailable = isAvailable;
                try {
                    manager.update(inventoryModel);
                    item.IsSold = isSold;
                    item.IsAvailable = isAvailable;
                    recyclerAdapter.notifyItemChanged(getAdapterPosition());
                } catch (Exception e) {
                    Timber.e(e);
                }
            } else {
                inventoryModel = new CustomerInventoryModel();
                inventoryModel.UniqueId = UUID.randomUUID();
                inventoryModel.CustomerId = customerId;
                inventoryModel.IsSold = isSold;
                inventoryModel.IsAvailable = isAvailable;
                inventoryModel.ProductId = item.UniqueId;
                try {
                    manager.insert(inventoryModel);
                    item.IsSold = isSold;
                    item.IsAvailable = isAvailable;
                    recyclerAdapter.notifyItemChanged(getAdapterPosition());
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }
        }
    }

    class CustomerInventoryWithQtyViewHolder extends BaseViewHolder<ProductInventoryModel> {

        private final CustomerInventoryManager manager;
        private final LinearLayout qtyView;
        private final TextView productCodeTextView;
        private final TextView productNameTextView;
        private final TextView rowTextView;
        private final TextView totalQtyTextView;
        private final CheckBox isSoldCheckBox;

        public CustomerInventoryWithQtyViewHolder(View itemView, BaseRecyclerAdapter<ProductInventoryModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            productCodeTextView = (TextView) itemView.findViewById(R.id.product_code_text_view);
            productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
            rowTextView = (TextView) itemView.findViewById(R.id.row_text_view);
            qtyView = (LinearLayout) itemView.findViewById(R.id.qty_layout);
            totalQtyTextView = (TextView) itemView.findViewById(R.id.total_qty_text_view);
            isSoldCheckBox = (CheckBox) itemView.findViewById(R.id.is_sold_check_box);
            manager = new CustomerInventoryManager(getContext());
        }

        @Override
        public void bindView(final int position) {
            final ProductInventoryModel item = adapter.get(position);
            productCodeTextView.setText(item.ProductCode);
            productNameTextView.setText(item.ProductName);
            isSoldCheckBox.setChecked(item.IsSold);
            totalQtyTextView.setText(HelperMethods.bigDecimalToString(item.TotalQty));
            rowTextView.setText(String.valueOf(position + 1));
            isSoldCheckBox.setOnClickListener(new View.OnClickListener() {
                private void update() {
                    ProductInventoryManager productInventoryManager = new ProductInventoryManager(getContext());
                    ProductInventoryModel updatedModel = productInventoryManager.getLine(item.UniqueId, customerId);
                    recyclerAdapter.set(getAdapterPosition(), updatedModel);
                    recyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onClick(View view) {
                    item.IsSold = isSoldCheckBox.isChecked();
                    CustomerInventoryManager customerInventoryManager = new CustomerInventoryManager(getContext());
                    CustomerInventoryModel customerInventoryModel;
                    if (item.CustomerId == null) {
                        customerInventoryModel = new CustomerInventoryModel();
                        customerInventoryModel.IsSold = isSoldCheckBox.isChecked();
                        customerInventoryModel.IsAvailable = false;
                        customerInventoryModel.UniqueId = UUID.randomUUID();
                        customerInventoryModel.ProductId = item.UniqueId;
                        customerInventoryModel.CustomerId = customerId;
                    } else {
                        customerInventoryModel = customerInventoryManager.getLine(item.UniqueId, customerId);
                    }
                    customerInventoryModel.IsSold = isSoldCheckBox.isChecked();
                    try {
                        customerInventoryManager.insertOrUpdate(customerInventoryModel);
                        if (!item.IsSold) {
                            CustomerInventoryQtyManager customerInventoryQtyManager = new CustomerInventoryQtyManager(getContext());
                            customerInventoryQtyManager.deleteLines(item.CustomerInventoryId);
                            update();
                        } else {
                            update();
                        }
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                }
            });
            qtyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCalculator(item);
                }
            });
            totalQtyTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCalculator(item);
                }
            });

            if (item.UnitName != null && !item.UnitName.isEmpty()) {
                List<BaseUnit> units = new ArrayList<>();
                String[] unitNames = item.UnitName.split(":");
                String[] strUnits = item.Qty.split(":");
                for (int i = 0; i < strUnits.length; i++) {
                    String strUnit = strUnits[i];
                    BaseUnit unit = new BaseUnit();
                    unit.value = Double.parseDouble(strUnit);
                    unit.Name = unitNames[i];
                    if (unit.value > 0)
                        units.add(unit);
                }
                new QtyView().build(qtyView, units);
            } else {
                qtyView.removeAllViews();
            }
        }

        private void showCalculator(final ProductInventoryModel item) {
            if (!item.IsSold) {
                return;
            }
            CustomerInventoryModel inventoryModel = manager.getLine(item.UniqueId, customerId);
            if (inventoryModel == null) {
                inventoryModel = new CustomerInventoryModel();
                inventoryModel.UniqueId = UUID.randomUUID();
                inventoryModel.CustomerId = customerId;
                inventoryModel.ProductId = item.UniqueId;
            }
            ProductModel productModel = new ProductManager(getContext()).getItem(inventoryModel.ProductId);
            if (productModel == null)
                throw new NullPointerException("Product id not found: " + inventoryModel.ProductId);
            final CustomerInventoryCalculatorForm calculatorForm = new CustomerInventoryCalculatorForm();
            CustomerInventoryQtyManager customerInventoryQtyManager = new CustomerInventoryQtyManager(getContext());
            List<CustomerInventoryQtyModel> qtys = customerInventoryQtyManager.getLines(inventoryModel.UniqueId);
            try {
                CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                CalculatorUnits calculatorUnits = calculatorHelper.generateCalculatorUnits(productModel.UniqueId, qtys, null, ProductType.All);
                calculatorForm.setArguments(productModel, calculatorUnits);
                final CustomerInventoryModel finalInventoryModel = inventoryModel;
                calculatorForm.onCalcFinish = new CustomerInventoryCalculatorForm.OnCalcFinish() {
                    @Override
                    public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
                        try {
                            manager.add(finalInventoryModel, discreteUnits);
                            Timber.i("Customer inventory updated");
                            ProductInventoryManager productInventoryManager = new ProductInventoryManager(getContext());
                            ProductInventoryModel updatedModel = productInventoryManager.getLine(item.UniqueId, customerId);
                            recyclerAdapter.set(getAdapterPosition(), updatedModel);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                };
                calculatorForm.show(getChildFragmentManager(), "CALCULATOR");
            } catch (ProductUnitViewManager.UnitNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
