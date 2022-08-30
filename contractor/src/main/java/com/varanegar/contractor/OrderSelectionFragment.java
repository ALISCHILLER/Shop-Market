package com.varanegar.contractor;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderPreviewManager;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModel;
import com.varanegar.vaslibrary.print.InvoicePrint.InvoicePrintHelper;
import com.varanegar.vaslibrary.print.datahelper.OrderPrintType;
import com.varanegar.vaslibrary.ui.fragment.order.CustomerSaveOrderFragment;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class OrderSelectionFragment extends VaranegarFragment {
    private BaseSelectionRecyclerAdapter<CustomerCallOrderPreviewModel> adapter;
    private UUID customerId;
    private UUID customerLevelId;
    private Button printButton;


    public void setCustomerId(@NonNull UUID customerId) {
        this.customerId = customerId;
    }

    public void setCustomerLevelId(UUID customerLevelId) {
        this.customerLevelId = customerLevelId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.select_order_layout_fragment, viewGroup, false);
        ((SimpleToolbar) view.findViewById(R.id.simple_toolbar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        BaseRecyclerView recyclerView = view.findViewById(R.id.orders_base_recycler_view);
        CustomerCallOrderPreviewManager orderManager = new CustomerCallOrderPreviewManager(getActivity());
        List<CustomerCallOrderPreviewModel> orderModels = orderManager.getCustomerCallOrders(customerId);
        adapter = new BaseSelectionRecyclerAdapter<CustomerCallOrderPreviewModel>(getVaranegarActvity(), orderModels, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
                return new OrderRowItemViewHolder(view, this, getContext());
            }
        };
        recyclerView.setAdapter(adapter);

        printButton = view.findViewById(R.id.print_btn);
        if (adapter.size() > 0)
            printButton.setVisibility(View.VISIBLE);

        adapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                CustomerCallOrderPreviewModel selectedOrder = adapter.getSelectedItem();
                CustomerSaveOrderFragment saveOrderFragment = new CustomerSaveOrderFragment();
                saveOrderFragment.setArguments(customerId, selectedOrder.UniqueId, customerLevelId);
                getVaranegarActvity().pushFragment(saveOrderFragment);
            }
        });

        view.findViewById(R.id.new_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CustomerSaveOrderFragment saveOrderFragment = new CustomerSaveOrderFragment();
                    CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getActivity());
                    CustomerCallOrderModel callOrderModel = callOrderManager.addOrder(customerId);
                    saveOrderFragment.setArguments(customerId, callOrderModel.UniqueId, customerLevelId);
                    getVaranegarActvity().pushFragment(saveOrderFragment);
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InvoicePrintHelper printContractor = new InvoicePrintHelper(getVaranegarActvity(), customerId, OrderPrintType.contractor);
                printContractor.start(null);
            }
        });

        return view;

    }

    class OrderRowItemViewHolder extends BaseViewHolder<CustomerCallOrderPreviewModel> {

        private final TextView rowTextView;
        private final TextView refNumberTextView;
        private final TextView totalAmountTextView;
        private final TextView totalQtyTextView;
        private final ImageView deleteImageView;

        public OrderRowItemViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallOrderPreviewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            rowTextView = (TextView) itemView.findViewById(R.id.row_text_view);
            refNumberTextView = (TextView) itemView.findViewById(R.id.ref_number_text_view);
            totalAmountTextView = (TextView) itemView.findViewById(R.id.total_amount_text_view);
            totalQtyTextView = (TextView) itemView.findViewById(R.id.total_qty_text_view);
            deleteImageView = itemView.findViewById(R.id.delete_image_view);

        }

        @Override
        public void bindView(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.notifyItemClicked(position);
                }
            });
            final CustomerCallOrderPreviewModel order = adapter.get(position);
            if (order == null)
                return;
            if (adapter.getSelectedPosition() == position)
                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.grey_light_light));
            else
                itemView.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
            if (order.UniqueId == null) {
                rowTextView.setVisibility(View.GONE);
                refNumberTextView.setText(getContext().getString(R.string.new_request));
                return;
            }

            rowTextView.setVisibility(View.VISIBLE);
            rowTextView.setText(String.valueOf(position + 1));
            refNumberTextView.setText(order.LocalPaperNo);
            totalAmountTextView.setText(HelperMethods.currencyToString(order.TotalPrice));
            totalQtyTextView.setText(HelperMethods.bigDecimalToString(order.TotalQty));

            deleteImageView.setVisibility(View.VISIBLE);
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Warning);
                    dialog.setMessage(R.string.are_you_sure);
                    dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
                                callOrderManager.cancelCustomerOrder(customerId, order.UniqueId);
                                adapter.remove(position);
                                if (adapter.size() <= 0)
                                    printButton.setVisibility(View.GONE);
                            } catch (Exception e) {
                                CuteMessageDialog dialog1 = new CuteMessageDialog(getContext());
                                dialog1.setIcon(Icon.Error);
                                dialog1.setMessage(R.string.error_deleting_order);
                                dialog1.setPositiveButton(R.string.ok, null);
                                dialog1.show();
                                Timber.e(e);
                            }
                        }
                    });
                    dialog.setNegativeButton(R.string.no, null);
                    dialog.show();
                }
            });
        }
    }

}