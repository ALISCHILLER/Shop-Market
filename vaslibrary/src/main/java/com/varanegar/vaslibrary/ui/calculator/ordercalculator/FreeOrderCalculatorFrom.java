package com.varanegar.vaslibrary.ui.calculator.ordercalculator;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mancj.slideup.SlideUp;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.FreeReasonManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.Calculator;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/10/2018.
 */

public class FreeOrderCalculatorFrom extends CuteDialog {
    private Context context;
    private UUID orderId;
    private ProductModel productModel;
    private Calculator calculator;
    private BaseSelectionRecyclerAdapter<FreeOrderItem> linesAdapter;
    private ArrayList<FreeOrderItem> freeOrderItems;

    public void setArguments(Context context, UUID orderId, ProductModel productModel) throws ProductUnitViewManager.UnitNotFoundException {
        this.orderId = orderId;
        this.productModel = productModel;
        this.context = context;
        final CalculatorHelper calculatorHelper = new CalculatorHelper(context);
        freeOrderItems = new ArrayList<>();
        OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(context);
        CustomerCallOrderOrderViewManager customerCallOrderOrderViewManager = new CustomerCallOrderOrderViewManager(context);
        List<CustomerCallOrderOrderViewModel> freeOrderLines = customerCallOrderOrderViewManager.getFreeOrderLines(orderId, productModel.UniqueId);
        FreeReasonManager freeReasonManager = new FreeReasonManager(context);
        List<FreeReasonModel> freeReasonModels = freeReasonManager.getAll();
        for (final FreeReasonModel freeReason :
                freeReasonModels) {
            CustomerCallOrderOrderViewModel orderLine = Linq.findFirst(freeOrderLines, new Linq.Criteria<CustomerCallOrderOrderViewModel>() {
                @Override
                public boolean run(CustomerCallOrderOrderViewModel item) {
                    return item.FreeReasonId.equals(freeReason.UniqueId);
                }
            });
            CalculatorUnits calculatorUnits = null;
            if (orderLine != null) {
                List<OrderLineQtyModel> qtys = orderLineQtyManager.getQtyLines(orderLine.UniqueId);
                BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(orderLine);
                calculatorUnits = calculatorHelper.generateCalculatorUnits(productModel.UniqueId, qtys, bulkUnit, ProductType.isForSale);
            } else {
                calculatorUnits = calculatorHelper.generateCalculatorUnits(productModel.UniqueId, ProductType.isForSale, true);
            }

            FreeOrderItem freeOrderItem = new FreeOrderItem();
            freeOrderItem.freeReason = freeReason;
            freeOrderItem.discreteUnits = calculatorUnits.getDiscreteUnits();
            freeOrderItem.bulkUnit = calculatorUnits.getBulkUnit();
            freeOrderItems.add(freeOrderItem);
        }

    }

    public interface OnCalcFinish {
        void run(List<FreeOrderItem> items);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    public OnCalcFinish onCalcFinish;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_free_item_calculator_dialog, container, false);


        BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.free_reason_recycler_view);
        linesAdapter = new BaseSelectionRecyclerAdapter(getVaranegarActvity(), freeOrderItems, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_free_item_reason, parent, false);
                FreeReasonViewHolder viewHolder = new FreeReasonViewHolder(itemView, this, context);
                return viewHolder;
            }
        };

        linesAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, boolean selected) {
                view.findViewById(R.id.msg_text_view).setVisibility(View.GONE);
                FreeOrderItem line = linesAdapter.get(position);
                calculator.setEnabled(true);
                calculator.setUnits(new CalculatorUnits(line.discreteUnits, null));

            }
        });
        recyclerView.setAdapter(linesAdapter);
        calculator = (Calculator) view.findViewById(R.id.calculator);
        calculator.setEnabled(false);
        calculator.setOnValueChangeListener(new Calculator.OnValueChangeListener() {
            @Override
            public void onValueChange(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
                linesAdapter.notifyDataSetChanged();
            }
        });
        calculator.onDoneClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculator.isEmpty()) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Error);
                    dialog.setMessage(R.string.please_input_qty);
                    dialog.setTitle(R.string.error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                } else {
                    dismiss();
                    if (onCalcFinish != null) {
                        onCalcFinish.run(freeOrderItems);
                    }
                }
            }
        };

        View slidingView = view.findViewById(R.id.sliding_panel);
        if (slidingView != null) {
            final SlideUp slideUp = new SlideUp.Builder(slidingView)
                    .withStartState(SlideUp.State.SHOWED)
                    .withStartGravity(Gravity.TOP)
                    .build();
            view.findViewById(R.id.menu_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slideUp.show();
                }
            });
            slideUp.addSlideListener(new SlideUp.Listener() {
                @Override
                public void onSlide(float percent) {

                }

                @Override
                public void onVisibilityChanged(int visibility) {
                    if (visibility == View.GONE) {
                        calculator.setEnabled(true);
                        if (linesAdapter.getSelectedPositions().size() == 0) {
                            slideUp.show();
                        }
                    } else if (visibility == View.VISIBLE) {
                        calculator.setEnabled(false);
                    }
                }
            });
            view.findViewById(R.id.close_sliding_panel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slideUp.hide();
                }
            });
            ((TextView) view.findViewById(R.id.title_text_view)).setText(productModel.ProductName);
        }


        return view;
    }

    class FreeReasonViewHolder extends BaseViewHolder<CustomerCallOrderOrderViewModel> {

        private final TextView freeReasonTextView;
        private final LinearLayout qtyLinearLayout;
        private final TextView totalQtyTextView;

        public FreeReasonViewHolder(View itemView, BaseRecyclerAdapter<CustomerCallOrderOrderViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            freeReasonTextView = (TextView) itemView.findViewById(R.id.free_reason_name_text_view);
            qtyLinearLayout = (LinearLayout) itemView.findViewById(R.id.qty_linear_layout);
            totalQtyTextView = (TextView) itemView.findViewById(R.id.total_qty_text_view);
        }

        @Override
        public void bindView(final int position) {
            if (position == linesAdapter.getSelectedPosition()) {
                itemView.setBackgroundColor(HelperMethods.getColor(context, R.color.grey_light_light));
            } else {
                itemView.setBackgroundColor(HelperMethods.getColor(context, R.color.grey_light_light_light));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linesAdapter.notifyItemClicked(position);
                }
            });
            FreeOrderItem freeOrderItem = linesAdapter.get(position);
            freeReasonTextView.setText(freeOrderItem.freeReason.FreeReasonName);
            totalQtyTextView.setText(HelperMethods.bigDecimalToString(freeOrderItem.getTotalQty()));
            if (freeOrderItem.discreteUnits != null) {
                List<BaseUnit> units = new ArrayList<>();
                for (DiscreteUnit discreteUnit : freeOrderItem.discreteUnits) {
                    BaseUnit unit = new BaseUnit();
                    unit.value = discreteUnit.value;
                    unit.Name = discreteUnit.Name;
                    unit.ProductUnitId = discreteUnit.ProductUnitId;
                    if (unit.value > 0)
                        units.add(unit);
                }
                new QtyView().build(qtyLinearLayout, units);
            }
        }
    }
}
