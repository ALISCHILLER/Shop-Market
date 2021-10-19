package com.varanegar.vaslibrary.ui.calculator.returncalculator;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mancj.slideup.SlideUp;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.ReturnReasonManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLineQtyManager;
import com.varanegar.vaslibrary.manager.customercall.ReturnLinesManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyModel;
import com.varanegar.vaslibrary.model.call.ReturnLines;
import com.varanegar.vaslibrary.model.call.ReturnLinesModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnBaseViewModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.Calculator;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 4/12/2017.
 */

public class ReturnCalculatorForm extends CuteDialog {
    private ReturnCalculatorItem currentItem;
    private CalculatorUnits calculatorUnits;
    private ProductModel productModel;
    public OnCalcFinish onCalcFinish;
    public OnItemDeleted onItemDeleted;
    CalcItemSelectionAdapter calculatorAdapter;
    private List<ReturnCalculatorItem> returnCalculatorItemList;
    Currency price;
    private boolean withRef;
    private BigDecimal maxValue;
    private EditText priceEditText;
    private Calculator calculator;
    private boolean returnTypeEnabled;

    public void setFromRequest(boolean fromRequest) {
        isFromRequest = fromRequest;
    }

    private boolean isFromRequest;

    public ReturnCalculatorForm() {
        setSizingPolicy(SizingPolicy.Medium);
    }

    public void setArguments(@NonNull ProductModel productModel, @NonNull CalculatorUnits calculatorUnits, Currency price, boolean withRef) {
        this.calculatorUnits = calculatorUnits;
        this.productModel = productModel;
        returnCalculatorItemList = new ArrayList<>();
        this.price = price;
        this.withRef = withRef;
    }

    public void setArguments(@NonNull Context context, @NonNull ProductModel productModel, @NonNull CalculatorUnits calculatorUnits, CustomerCallReturnBaseViewModel returnViewModel, Currency price, boolean withRef) {
        this.calculatorUnits = calculatorUnits;
        this.productModel = productModel;
        returnCalculatorItemList = new ArrayList<>();
        this.withRef = withRef;
        this.price = price;

        String[] returnTypeIds = returnViewModel.ReturnProductTypeId.split(":");
        String[] returnReasonIds = returnViewModel.ReturnReasonId.split(":");
        ReturnReasonManager returnReasonManager = new ReturnReasonManager(getContext());
        List<ReturnReasonModel> reasonModels = returnReasonManager.getAll();
        for (int i = 0; i < returnReasonIds.length; i++) {
            UUID returnTypeId = UUID.fromString(returnTypeIds[i]);
            final UUID returnReasonId = UUID.fromString(returnReasonIds[i]);

            ReturnLinesManager returnLinesManager = new ReturnLinesManager(getContext());
            ReturnLinesModel returnLinesModel = returnLinesManager.getItem(new Query().from(ReturnLines.ReturnLinesTbl)
                    .whereAnd(Criteria.equals(ReturnLines.ProductUniqueId, returnViewModel.ProductId)
                            .and(Criteria.equals(ReturnLines.ReturnProductTypeId, returnTypeId))
                            .and(Criteria.equals(ReturnLines.ReturnReasonId, returnReasonId))
                            .and(Criteria.equals(ReturnLines.ReturnUniqueId, returnViewModel.ReturnUniqueId))));

            ReturnLineQtyManager qtyManager = new ReturnLineQtyManager(getContext());
            List<ReturnLineQtyModel> qtys = qtyManager.getQtyLines(returnLinesModel.UniqueId);

            ReturnReasonModel reason = Linq.findFirst(reasonModels, new Linq.Criteria<ReturnReasonModel>() {
                @Override
                public boolean run(ReturnReasonModel item) {
                    return item.UniqueId.equals(returnReasonId);
                }
            });
            ReturnCalculatorItem returnCalculatorItem = new ReturnCalculatorItem(context, returnTypeId, ReturnType.getName(context, returnTypeId), returnReasonId, reason.ReturnReasonName);
            if (calculatorUnits.getBulkUnit() != null) {
                BaseUnit bulkUnit = new BaseUnit();
                bulkUnit.Name = calculatorUnits.getBulkUnit().Name;
                bulkUnit.ProductUnitId = calculatorUnits.getBulkUnit().ProductUnitId;
                bulkUnit.IsDefault = calculatorUnits.getBulkUnit().IsDefault;
                bulkUnit.Unit = calculatorUnits.getBulkUnit().Unit;
                bulkUnit.value = returnLinesModel.RequestBulkQty == null ? 0 : returnLinesModel.RequestBulkQty.doubleValue();
                returnCalculatorItem.setBulkUnit(bulkUnit);
            }
            for (final DiscreteUnit discreteUnit :
                    calculatorUnits.getDiscreteUnits()) {
                DiscreteUnit unit = new DiscreteUnit();
                unit.ConvertFactor = discreteUnit.ConvertFactor;
                unit.ProductUnitId = discreteUnit.ProductUnitId;
                unit.IsDefault = discreteUnit.IsDefault;
                unit.Name = discreteUnit.Name;
                ReturnLineQtyModel qty = Linq.findFirst(qtys, new Linq.Criteria<ReturnLineQtyModel>() {
                    @Override
                    public boolean run(ReturnLineQtyModel item) {
                        return item.ProductUnitId.equals(discreteUnit.ProductUnitId);
                    }
                });
                if (qty != null) {
                    unit.value = qty.Qty == null ? 0 : qty.Qty.doubleValue();
                }
                returnCalculatorItem.addDiscreteUnit(unit);
            }
            returnCalculatorItem.ReturnUniqueId = returnLinesModel.ReturnUniqueId;
            returnCalculatorItemList.add(returnCalculatorItem);
        }
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public interface OnCalcFinish {
        void run(List<ReturnCalculatorItem> returnCalculatorItems, Currency price);
    }

    public interface OnItemDeleted {
        void run(ReturnCalculatorItem returnCalculatorItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel AllowReturnDamagedProductSys = sysConfigManager.read(ConfigKey.AllowReturnDamagedProduct, SysConfigManager.cloud);
        SysConfigModel AllowReturnIntactProductSys = sysConfigManager.read(ConfigKey.AllowReturnIntactProduct, SysConfigManager.cloud);
        if (SysConfigManager.compare(AllowReturnDamagedProductSys, false) && SysConfigManager.compare(AllowReturnIntactProductSys, false))
            returnTypeEnabled = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSizingPolicy(SizingPolicy.Maximum);
        final View view = inflater.inflate(R.layout.dialog_return_calculator, container, false);
        if (!returnTypeEnabled)
            view.findViewById(R.id.return_type_label).setVisibility(View.GONE);
        final TextView landProductNameTextView = (TextView) view.findViewById(R.id.land_product_name_text_view);
        final TextView totalTextView = (TextView) view.findViewById(R.id.total_count_text_view);
        priceEditText = (EditText) view.findViewById(R.id.price_edit_text);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel allowEditSellReturnAmount = sysConfigManager.read(ConfigKey.AllowEditSellReturnAmount, SysConfigManager.cloud);
        if (withRef || SysConfigManager.compare(allowEditSellReturnAmount, false))
            priceEditText.setEnabled(false);
        if (price != null)
            priceEditText.setText(String.valueOf(price));
        calculator = (Calculator) view.findViewById(R.id.calculator);
        final View returnReasonsHeaderLayout = view.findViewById(R.id.reasons_linear_layout);
        calculator.onDoneClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        };
        calculator.setOnValueChangeListener(new Calculator.OnValueChangeListener() {
            @Override
            public void onValueChange(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
                calculatorAdapter.notifyDataSetChanged();
                BigDecimal wellCount = new BigDecimal(0);
                BigDecimal damagedCount = new BigDecimal(0);
                for (ReturnCalculatorItem item :
                        calculatorAdapter.getItems()) {
                    if (item.getReturnType().equals(ReturnType.Well)) {
                        wellCount = wellCount.add(item.getTotalQty());
                    } else if (item.getReturnType().equals(ReturnType.Waste)) {
                        damagedCount = damagedCount.add(item.getTotalQty());
                    }
                }
                totalTextView.setVisibility(View.VISIBLE);
                totalTextView.setText(
                        getContext().getString(R.string.total) + " : " + getContext().getString(R.string.well) + " " + HelperMethods.bigDecimalToString(wellCount) + " " +
                                getContext().getString(R.string.number) + " + " + getContext().getString(R.string.waste) + " " + HelperMethods.bigDecimalToString(damagedCount) + " " +
                                getContext().getString(R.string.number) + " = " + HelperMethods.bigDecimalToString(damagedCount.add(wellCount))
                );
            }
        });
//        calculator.setEnabled(false);
        calculator.setVisibility(View.GONE);
        final BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.rows_base_recycler_view);
        calculatorAdapter = new CalcItemSelectionAdapter(getVaranegarActvity(), returnCalculatorItemList, false);
        calculatorAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<ReturnCalculatorItem>() {
            @Override
            public void run(int position) {
                currentItem = calculatorAdapter.get(position);
                calculator.setUnits(new CalculatorUnits(currentItem.getDiscreteUnits(), currentItem.getBulkUnit()));
            }
        });
        recyclerView.setAdapter(calculatorAdapter);
        if (returnCalculatorItemList.size() > 0) {
            calculator.setVisibility(View.VISIBLE);
            calculator.setEnabled(true);
            recyclerView.setVisibility(View.VISIBLE);
            returnReasonsHeaderLayout.setVisibility(View.VISIBLE);
            calculatorAdapter.select(0);
            currentItem = calculatorAdapter.get(0);
            calculator.setUnits(new CalculatorUnits(currentItem.getDiscreteUnits(), currentItem.getBulkUnit()));
        }


        calculatorAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (calculatorAdapter.size() == 0) {
                    returnReasonsHeaderLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    returnReasonsHeaderLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        if (isFromRequest) {
            view.findViewById(R.id.add_row_image_button).setVisibility(View.GONE);
            view.findViewById(R.id.choose_reason_and_type_of_return).setVisibility(View.GONE);
        }
        view.findViewById(R.id.add_row_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnReasonDialog returnReasonDialog = new ReturnReasonDialog();
                returnReasonDialog.onReasonSelected = new ReturnReasonDialog.OnReasonSelected() {
                    @Override
                    public void run(final UUID returnTypeId, final ReturnReasonModel returnReasonModel) {
                        currentItem = Linq.findFirst(calculatorAdapter.getItems(), new Linq.Criteria<ReturnCalculatorItem>() {
                            @Override
                            public boolean run(ReturnCalculatorItem item) {
                                return item.returnReason.equals(returnReasonModel.UniqueId) && item.returnType.equals(returnTypeId);
                            }
                        });
                        if (currentItem == null) {
                            currentItem = new ReturnCalculatorItem(getContext(), returnTypeId, ReturnType.getName(getContext(), returnTypeId), returnReasonModel.UniqueId, returnReasonModel.ReturnReasonName);
                            if (calculatorUnits.getBulkUnit() != null) {
                                BaseUnit bulkUnit = new BaseUnit();
                                bulkUnit.Name = calculatorUnits.getBulkUnit().Name;
                                bulkUnit.ProductUnitId = calculatorUnits.getBulkUnit().ProductUnitId;
                                bulkUnit.IsDefault = calculatorUnits.getBulkUnit().IsDefault;
                                bulkUnit.Unit = calculatorUnits.getBulkUnit().Unit;
                                currentItem.setBulkUnit(bulkUnit);
                            }
                            for (DiscreteUnit discreteUnit :
                                    calculatorUnits.getDiscreteUnits()) {
                                DiscreteUnit unit = new DiscreteUnit();
                                unit.ConvertFactor = discreteUnit.ConvertFactor;
                                unit.ProductUnitId = discreteUnit.ProductUnitId;
                                unit.Name = discreteUnit.Name;
                                unit.IsDefault = discreteUnit.IsDefault;
                                currentItem.addDiscreteUnit(unit);
                            }
                            calculatorAdapter.add(currentItem);
                            if (landProductNameTextView != null) {
                                calculator.setVisibility(View.VISIBLE);
                                calculator.setEnabled(true);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            returnReasonsHeaderLayout.setVisibility(View.VISIBLE);
                        }
                        calculatorAdapter.select(currentItem);
                        calculator.setUnits(new CalculatorUnits(currentItem.getDiscreteUnits(), currentItem.getBulkUnit()));
                    }
                };
                returnReasonDialog.show(getVaranegarActvity().getSupportFragmentManager(), "ReturnReasonDialog");
            }
        });


        if (landProductNameTextView != null) {
            // landscape view
            landProductNameTextView.setText(productModel.ProductName);

        } else {
            // portrait view
            View slidingView = view.findViewById(R.id.sliding_panel);
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
                        calculator.setVisibility(View.VISIBLE);
                        calculator.setEnabled(true);
                        if (calculatorAdapter.getSelectedPositions().size() == 0) {
                            slideUp.show();
                        }
                    } else if (visibility == View.VISIBLE) {
//                        calculator.setEnabled(false);
                        calculator.setVisibility(View.GONE);
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

    private void save() {
        if (isEmpty() && !isFromRequest) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            dialog.setMessage(R.string.please_input_qty);
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            return;
        } else if (priceEditText.getText().length() < 1) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            //begin
            // It should change in 14.2 -> if the return product doesn't have price and AllowEditSellReturnAmount is false it should be removed from productReturnList
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel allowEditSellReturnAmount = sysConfigManager.read(ConfigKey.AllowEditSellReturnAmount, SysConfigManager.cloud);
            if (SysConfigManager.compare(allowEditSellReturnAmount, false))
                dialog.setMessage(R.string.no_product_price);
            else
                //end
                dialog.setMessage(R.string.please_input_price);
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            return;
        } else if (maxValue != null) {
            BigDecimal total = calculator.getTotal();
            if (total == null)
                total = BigDecimal.ZERO;
            if (total.compareTo(maxValue) > 0) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setMessage(R.string.value_is_larger_than_max_value);
                dialog.setTitle(R.string.error);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
                return;
            }
        }
        String p = priceEditText.getText().toString();
        try {
            price = Currency.parse(p);
            if (onCalcFinish != null)
                onCalcFinish.run(calculatorAdapter.getItems(), price);
            dismiss();
        } catch (ParseException e) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            dialog.setMessage(R.string.input_price_is_not_valid);
            dialog.setTitle(R.string.error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            Timber.e(e);
        }
    }

    private boolean isEmpty() {
        if (calculatorAdapter.getItems() == null || calculatorAdapter.getItems().size() == 0)
            return true;
        for (ReturnCalculatorItem item :
                calculatorAdapter.getItems()) {
            if (item.getTotalQty().compareTo(BigDecimal.ZERO) == 0)
                return true;
        }
        return false;
    }

    class CalcItemSelectionAdapter extends BaseSelectionRecyclerAdapter<ReturnCalculatorItem> {

        public CalcItemSelectionAdapter(@NonNull AppCompatActivity activity, boolean multiSelect) {
            super(activity, multiSelect);
        }

        public CalcItemSelectionAdapter(@NonNull AppCompatActivity activity, @NonNull List<ReturnCalculatorItem> items, boolean multiSelect) {
            super(activity, items, multiSelect);
        }

        public CalcItemSelectionAdapter(@NonNull AppCompatActivity activity, @NonNull BaseRepository<ReturnCalculatorItem> repository, @NonNull Query query, boolean multiSelect) {
            super(activity, repository, query, multiSelect);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calc_item_row, parent, false);
            return new CalcItemViewHolder(view, this, getContext());
        }
    }

    class CalcItemViewHolder extends BaseViewHolder<ReturnCalculatorItem> {


        private final TextView returnTypeTextView;
        private final TextView returnReasonTextView;
        private final TextView returnQtyTextView;
        private final View editImageView;

        public CalcItemViewHolder(View itemView, BaseRecyclerAdapter<ReturnCalculatorItem> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            returnTypeTextView = (TextView) itemView.findViewById(R.id.return_type_text_view);
            returnReasonTextView = (TextView) itemView.findViewById(R.id.return_reason_text_view);
            returnQtyTextView = (TextView) itemView.findViewById(R.id.return_qty_text_view);
            editImageView = itemView.findViewById(R.id.edit_image_view);

            if (!returnTypeEnabled)
                returnTypeTextView.setVisibility(View.GONE);
        }

        @Override
        public void bindView(final int position) {
            final CalcItemSelectionAdapter adapter = (CalcItemSelectionAdapter) recyclerAdapter;
            ReturnCalculatorItem item = adapter.get(position);
            if (item == null)
                return;
            if (position == adapter.getSelectedPosition()) {
                returnQtyTextView.setTypeface(Typeface.DEFAULT_BOLD);
                returnReasonTextView.setTypeface(Typeface.DEFAULT_BOLD);
                returnTypeTextView.setTypeface(Typeface.DEFAULT_BOLD);
                editImageView.setVisibility(View.VISIBLE);
            } else {
                returnQtyTextView.setTypeface(Typeface.DEFAULT);
                returnReasonTextView.setTypeface(Typeface.DEFAULT);
                returnTypeTextView.setTypeface(Typeface.DEFAULT);
                editImageView.setVisibility(View.INVISIBLE);
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Alert);
                    dialog.setMessage(R.string.do_you_delete_item);
                    dialog.setTitle(R.string.alert);
                    dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ReturnCalculatorItem removedItem = adapter.get(getAdapterPosition());
                            if (onItemDeleted != null && removedItem.ReturnUniqueId != null)
                                onItemDeleted.run(removedItem);
                            adapter.remove(getAdapterPosition());
                        }
                    });
                    dialog.setNeutralButton(R.string.no, null);
                    dialog.show();
                    return true;
                }
            });
            returnReasonTextView.setText(item.reasonName);
            returnTypeTextView.setText(item.typeName);
            returnQtyTextView.setText(HelperMethods.bigDecimalToString(item.getTotalQty()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getAdapterPosition();
                    adapter.notifyItemClicked(p);
                    adapter.runItemClickListener(p);
                }
            });
        }
    }
}
