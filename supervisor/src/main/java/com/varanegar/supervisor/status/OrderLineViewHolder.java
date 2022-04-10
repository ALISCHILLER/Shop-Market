package com.varanegar.supervisor.status;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderLineOrderQtyDetailViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderLineViewModel;
import com.varanegar.vaslibrary.base.VasHelperMethods;

import java.math.BigDecimal;
import java.util.Comparator;

class OrderLineViewHolder extends BaseViewHolder<CustomerCallOrderLineViewModel> {
    private final TextView productNameTextView;
    private final TextView productCodeTextView;
    private final ImageButton plusImageViewSmall;
    private final ImageButton minusImageViewSmall;
    private final ImageButton plusImageViewMiddle;
    private final ImageButton minusImageViewMiddle;
    private final ImageButton plusImageViewLarge;
    private final ImageButton minusImageViewLarge;
    private final TextView qtyTextViewSmall;
    private final TextView unitTextViewSmall;
    private final TextView qtyTextViewMiddle;
    private final TextView unitTextViewMiddle;
    private final TextView qtyTextViewLarge;
    private final TextView unitTextViewLarge;
    private final TextView rowTextView;
    private final TextView totalOrderQtyTextView;
    private final View smallUnitView;
    private final View middleUnitView;
    private final View largeUnitView;
    private final TextView totalQtyLabelTextView;
    private final CustomerCallTabFragment.OnCallChanged onCallChanged;
    private final boolean readOnly;
    CustomerCallOrderLineOrderQtyDetailViewModel largeQty;
    CustomerCallOrderLineOrderQtyDetailViewModel middleQty;
    CustomerCallOrderLineOrderQtyDetailViewModel smallQty;

    public OrderLineViewHolder(CustomerCallTabFragment.OnCallChanged onCallChanged,
                               View itemView, BaseRecyclerAdapter<CustomerCallOrderLineViewModel> recyclerAdapter,
                               Context context,
                               boolean readOnly) {
        super(itemView, recyclerAdapter, context);
        this.onCallChanged = onCallChanged;
        productNameTextView = itemView.findViewById(R.id.product_name_text_view);
        productCodeTextView = itemView.findViewById(R.id.product_code_text_view);

        rowTextView = itemView.findViewById(R.id.row_text_view);
        totalOrderQtyTextView = itemView.findViewById(R.id.total_order_qty_text_view);


        smallUnitView = itemView.findViewById(R.id.small_unit_view);
        middleUnitView = itemView.findViewById(R.id.middle_unit_view);
        largeUnitView = itemView.findViewById(R.id.large_unit_view);

        plusImageViewSmall = itemView.findViewById(R.id.plus_image_view_small);
        minusImageViewSmall = itemView.findViewById(R.id.minus_image_view_small);

        plusImageViewMiddle = itemView.findViewById(R.id.plus_image_view_middle);
        minusImageViewMiddle = itemView.findViewById(R.id.minus_image_view_middle);

        plusImageViewLarge = itemView.findViewById(R.id.plus_image_view_large);
        minusImageViewLarge = itemView.findViewById(R.id.minus_image_view_large);

        qtyTextViewSmall = itemView.findViewById(R.id.qty_text_view_small);
        unitTextViewSmall = itemView.findViewById(R.id.unit_text_view_small);

        qtyTextViewMiddle = itemView.findViewById(R.id.qty_text_view_middle);
        unitTextViewMiddle = itemView.findViewById(R.id.unit_text_view_middle);

        qtyTextViewLarge = itemView.findViewById(R.id.qty_text_view_large);
        unitTextViewLarge = itemView.findViewById(R.id.unit_text_view_large);

        totalQtyLabelTextView = itemView.findViewById(R.id.total_qty_label_text_view);

        this.readOnly = readOnly;
    }

    @Override
    public void bindView(int position) {
        CustomerCallOrderLineViewModel orderLineViewModel = recyclerAdapter.get(position);
        productCodeTextView.setText(orderLineViewModel.ProductCode);
        productNameTextView.setText(orderLineViewModel.ProductName);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemView.setEnabled(false);
                recyclerAdapter.showContextMenu(getAdapterPosition());
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemView.setEnabled(true);
                    }
                }, 2000);
            }
        });

        rowTextView.setText(String.valueOf(position + 1));

        createUnits(orderLineViewModel, position);
        if (readOnly) {
            minusImageViewSmall.setVisibility(View.GONE);
            minusImageViewMiddle.setVisibility(View.GONE);
            minusImageViewLarge.setVisibility(View.GONE);
            plusImageViewSmall.setVisibility(View.GONE);
            plusImageViewMiddle.setVisibility(View.GONE);
            plusImageViewLarge.setVisibility(View.GONE);
        } else {
            minusImageViewSmall.setVisibility(View.VISIBLE);
            minusImageViewMiddle.setVisibility(View.VISIBLE);
            minusImageViewLarge.setVisibility(View.VISIBLE);
            plusImageViewSmall.setVisibility(View.VISIBLE);
            plusImageViewMiddle.setVisibility(View.VISIBLE);
            plusImageViewLarge.setVisibility(View.VISIBLE);
        }


        totalOrderQtyTextView.setVisibility(View.VISIBLE);
        totalOrderQtyTextView.setText(VasHelperMethods.bigDecimalToString(calcTotalQty(orderLineViewModel)));

    }

    private void refresh(CustomerCallOrderLineViewModel orderLineViewModel) {
        orderLineViewModel.IsEditedBySupervisor = true;

        BigDecimal total = calcTotalQty(orderLineViewModel);
        orderLineViewModel.RequestQty = VasHelperMethods.bigDecimalToString(total);

        orderLineViewModel.RequestTotalPrice = orderLineViewModel.UnitPrice.multiply(total);
        if (onCallChanged != null)
            onCallChanged.run();
    }

    private void createUnits(final CustomerCallOrderLineViewModel orderLineViewModel, final int position) {

        Linq.sort(orderLineViewModel.CustomerCallOrderLineOrderQtyDetails, new Comparator<CustomerCallOrderLineOrderQtyDetailViewModel>() {
            @Override
            public int compare(CustomerCallOrderLineOrderQtyDetailViewModel o1, CustomerCallOrderLineOrderQtyDetailViewModel o2) {
                return o1.ConvertFactor.compareTo(o2.ConvertFactor);
            }
        });

        if (orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.size() == 0)
            return;

        if (orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.size() == 3) {
            largeUnitView.setVisibility(View.VISIBLE);
            largeQty = orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.get(2);
            qtyTextViewLarge.setText(VasHelperMethods.bigDecimalToString(largeQty.Qty));
            unitTextViewLarge.setText(largeQty.UnitName);

            middleUnitView.setVisibility(View.VISIBLE);
            middleQty = orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.get(1);
            qtyTextViewMiddle.setText(VasHelperMethods.bigDecimalToString(middleQty.Qty));
            unitTextViewMiddle.setText(middleQty.UnitName);
        }

        if (orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.size() == 2) {
            largeUnitView.setVisibility(View.VISIBLE);
            largeQty = orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.get(1);
            qtyTextViewLarge.setText(VasHelperMethods.bigDecimalToString(largeQty.Qty));
            unitTextViewLarge.setText(largeQty.UnitName);

            middleUnitView.setVisibility(View.GONE);
        }


        smallQty = orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.get(0);
        qtyTextViewSmall.setText(VasHelperMethods.bigDecimalToString(smallQty.Qty));
        unitTextViewSmall.setText(smallQty.UnitName);
        totalQtyLabelTextView.setText(smallQty.UnitName);

        if (largeQty != null && largeQty.Qty == null)
            largeQty.Qty = BigDecimal.ZERO;
        if (middleQty != null && middleQty.Qty == null)
            middleQty.Qty = BigDecimal.ZERO;
        if (smallQty != null && smallQty.Qty == null)
            smallQty.Qty = BigDecimal.ZERO;

        minusImageViewSmall.setEnabled(smallQty != null && smallQty.Qty.compareTo(BigDecimal.ZERO) > 0);
        minusImageViewMiddle.setEnabled(middleQty != null && middleQty.Qty.compareTo(BigDecimal.ZERO) > 0);
        minusImageViewLarge.setEnabled(largeQty != null && largeQty.Qty.compareTo(BigDecimal.ZERO) > 0);

        plusImageViewSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smallQty.Qty = smallQty.Qty.add(BigDecimal.ONE);
                refresh(orderLineViewModel);
                recyclerAdapter.notifyDataSetChanged();
            }
        });

        plusImageViewMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                middleQty.Qty = middleQty.Qty.add(BigDecimal.ONE);
                refresh(orderLineViewModel);
                recyclerAdapter.notifyDataSetChanged();
            }
        });

        plusImageViewLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                largeQty.Qty = largeQty.Qty.add(BigDecimal.ONE);
                refresh(orderLineViewModel);
                recyclerAdapter.notifyDataSetChanged();
            }
        });

        minusImageViewSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smallQty.Qty = smallQty.Qty.subtract(BigDecimal.ONE);
                if (smallQty.Qty.compareTo(BigDecimal.ZERO) < 0)
                    smallQty.Qty = BigDecimal.ZERO;
                if (smallQty.Qty.compareTo(BigDecimal.ZERO) == 0) {
                    if (calcTotalQty(orderLineViewModel).compareTo(BigDecimal.ZERO) == 0) {
                        recyclerAdapter.remove(position);
                        recyclerAdapter.notifyItemRemoved(position);
                        if (onCallChanged != null)
                            onCallChanged.run();
                    } else {
                        refresh(orderLineViewModel);
                        recyclerAdapter.notifyDataSetChanged();
                        if (onCallChanged != null)
                            onCallChanged.run();
                    }
                } else {
                    refresh(orderLineViewModel);
                    recyclerAdapter.notifyDataSetChanged();
                    if (onCallChanged != null)
                        onCallChanged.run();
                }
            }
        });

        minusImageViewMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                middleQty.Qty = middleQty.Qty.subtract(BigDecimal.ONE);
                if (middleQty.Qty.compareTo(BigDecimal.ZERO) < 0)
                    middleQty.Qty = BigDecimal.ZERO;
                if (middleQty.Qty.compareTo(BigDecimal.ZERO) == 0) {
                    if (calcTotalQty(orderLineViewModel).compareTo(BigDecimal.ZERO) == 0) {
                        if (onCallChanged != null)
                            onCallChanged.run();
                        recyclerAdapter.remove(position);
                        recyclerAdapter.notifyItemRemoved(position);
                    } else {
                        refresh(orderLineViewModel);
                        recyclerAdapter.notifyDataSetChanged();
                        if (onCallChanged != null)
                            onCallChanged.run();
                    }
                } else {
                    refresh(orderLineViewModel);
                    recyclerAdapter.notifyDataSetChanged();
                    if (onCallChanged != null)
                        onCallChanged.run();
                }
            }
        });


        minusImageViewLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                largeQty.Qty = largeQty.Qty.subtract(BigDecimal.ONE);
                if (largeQty.Qty.compareTo(BigDecimal.ZERO) < 0)
                    largeQty.Qty = BigDecimal.ZERO;
                if (largeQty.Qty.compareTo(BigDecimal.ZERO) == 0) {
                    if (calcTotalQty(orderLineViewModel).compareTo(BigDecimal.ZERO) == 0) {
                        if (onCallChanged != null)
                            onCallChanged.run();
                        recyclerAdapter.remove(position);
                        recyclerAdapter.notifyItemRemoved(position);
                    } else {
                        refresh(orderLineViewModel);
                        recyclerAdapter.notifyDataSetChanged();
                        if (onCallChanged != null)
                            onCallChanged.run();
                    }
                } else {
                    refresh(orderLineViewModel);
                    recyclerAdapter.notifyDataSetChanged();
                    if (onCallChanged != null)
                        onCallChanged.run();
                }
            }
        });

        minusImageViewSmall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                smallQty.Qty = BigDecimal.ZERO;
                if (calcTotalQty(orderLineViewModel).compareTo(BigDecimal.ZERO) == 0) {
                    if (onCallChanged != null)
                        onCallChanged.run();
                    recyclerAdapter.remove(position);
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    if (onCallChanged != null)
                        onCallChanged.run();
                    refresh(orderLineViewModel);
                    recyclerAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        minusImageViewMiddle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                middleQty.Qty = BigDecimal.ZERO;
                if (calcTotalQty(orderLineViewModel).compareTo(BigDecimal.ZERO) == 0) {
                    if (onCallChanged != null)
                        onCallChanged.run();
                    recyclerAdapter.remove(position);
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    if (onCallChanged != null)
                        onCallChanged.run();
                    refresh(orderLineViewModel);
                    recyclerAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        minusImageViewLarge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                largeQty.Qty = BigDecimal.ZERO;
                if (calcTotalQty(orderLineViewModel).compareTo(BigDecimal.ZERO) == 0) {
                    if (onCallChanged != null)
                        onCallChanged.run();
                    recyclerAdapter.remove(position);
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    if (onCallChanged != null)
                        onCallChanged.run();
                    refresh(orderLineViewModel);
                    recyclerAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });


    }

    private BigDecimal calcTotalQty(CustomerCallOrderLineViewModel orderLineViewModel) {
        BigDecimal total = BigDecimal.ZERO;
        for (CustomerCallOrderLineOrderQtyDetailViewModel qty :
                orderLineViewModel.CustomerCallOrderLineOrderQtyDetails) {
            if (qty.Qty != null && qty.ConvertFactor != null)
                total = qty.Qty.multiply(qty.ConvertFactor).add(total);
        }
        return total;
    }
}
