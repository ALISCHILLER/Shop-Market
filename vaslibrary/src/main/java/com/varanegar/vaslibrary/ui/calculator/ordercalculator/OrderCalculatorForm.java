package com.varanegar.vaslibrary.ui.calculator.ordercalculator;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.base.VaranegarApplication;
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
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.canvertType.ConvertFaNumType;
import com.varanegar.vaslibrary.manager.customerpricemanager.CustomerPriceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.Calculator;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 2/26/2017.
 */

public class OrderCalculatorForm extends CuteDialog {
    private CalculatorUnits calculatorUnits;
    private String productName;
    private Calculator calculator;
    private Currency unitPrice;
    private Currency userPrice;
    private OnHandQtyStock onHandQtyStock;
    private BaseSelectionRecyclerAdapter<CalculatorBatchUnits> batchesAdapter;
    private TextView benefitTextView;
    private TextView priceTextView;
    private boolean selective;
    private TextView inventoryQtyTextView;
    private TextView userPriceTextView;
    private UUID customerId;
    private UUID callOrderId;
    private CalculatorBatchUnits orderedBatchUnits;
    private UUID productId;
    private FloatingActionButton fabVoice;
    private String _voicetoText;
    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    private BigDecimal maxValue;

    public OrderCalculatorForm() {
        setSizingPolicy(SizingPolicy.Medium);
    }


    public void setArguments(@NonNull UUID productId, @NonNull String productName,
                             @NonNull CalculatorUnits calculatorUnits, @NonNull Currency unitPrice,
                             @NonNull Currency userPrice, @NonNull OnHandQtyStock onHandQtyStock,
                             @NonNull UUID customerId, @NonNull UUID callOrderId) {
        this.calculatorUnits = calculatorUnits;
        this.productName = productName;
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.userPrice = userPrice;
        this.onHandQtyStock = onHandQtyStock;
        this.customerId = customerId;
        this.callOrderId = callOrderId;
    }

    public void setArguments(String voicetoText){
        if (voicetoText!=null&&!voicetoText.isEmpty()){
            if (voicetoText.contains("عدد")){
                voicetoText= voicetoText.replace("عدد","");
                voicetoText= ConvertFaNumType.convert(voicetoText);
                if (voicetoText.contains("یک"))
                    voicetoText="1";
                if (voicetoText.contains("دو"))
                    voicetoText="2";
                if (voicetoText.contains("سه"))
                    voicetoText="3";
                if (voicetoText.contains("چهار"))
                    voicetoText="4";
                if (voicetoText.contains("پنج"))
                    voicetoText="5";
                if (voicetoText.contains("شش"))
                    voicetoText="6";
                if (voicetoText.contains("هفت"))
                    voicetoText="7";
                if (voicetoText.contains("هشت"))
                    voicetoText="8";
                if (voicetoText.contains("نه"))
                    voicetoText="9";
                if (voicetoText.contains("ده"))
                    voicetoText="10";
                voicetoText= voicetoText.replaceAll("[^0-9]", "");
                if (voicetoText!=null &&!voicetoText.isEmpty()) {
                    calculatorUnits.getDiscreteUnits().get(1).value = Double.parseDouble(voicetoText);
                    this._voicetoText = ConvertFaNumType.convert(voicetoText);
                }
            } if (voicetoText.contains("کارتن")||voicetoText.contains("کارتون")||voicetoText.contains("کارت")){
                voicetoText= voicetoText.replace("کارتون","");
                voicetoText= ConvertFaNumType.convert(voicetoText);
                if (voicetoText.contains("یک"))
                    voicetoText="1";
                if (voicetoText.contains("دو"))
                    voicetoText="2";
                if (voicetoText.contains("سه"))
                    voicetoText="3";
                if (voicetoText.contains("چهار"))
                    voicetoText="4";
                if (voicetoText.contains("پنج"))
                    voicetoText="5";
                if (voicetoText.contains("شش"))
                    voicetoText="6";
                if (voicetoText.contains("هفت"))
                    voicetoText="7";
                if (voicetoText.contains("هشت"))
                    voicetoText="8";
                if (voicetoText.contains("نه"))
                    voicetoText="9";
                if (voicetoText.contains("ده"))
                    voicetoText="10";
                voicetoText= voicetoText.replaceAll("[^0-9]", "");
                if (voicetoText!=null &&!voicetoText.isEmpty()) {
                    calculatorUnits.getDiscreteUnits().get(0).value = Double.parseDouble(voicetoText);
                    this._voicetoText = ConvertFaNumType.convert(voicetoText);
                }
            }
        }
    }

    public void setArguments(@NonNull UUID productId, @NonNull String productName,
                             @NonNull List<CalculatorBatchUnits> calculatorBatchUnits,
                             @NonNull Currency userPrice, @NonNull OnHandQtyStock onHandQtyStock,
                             @NonNull UUID customerId, UUID callOrderId) {
        if (calculatorBatchUnits.size() == 0)
            throw new IllegalArgumentException("");
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel showNormalItmDetail = sysConfigManager.read(ConfigKey.ShowNormalItmDetail, SysConfigManager.cloud);
        selective = SysConfigManager.compare(showNormalItmDetail, "0");
        if (!selective) {
            Collections.sort(calculatorBatchUnits, new Comparator<CalculatorBatchUnits>() {
                @Override
                public int compare(CalculatorBatchUnits calculatorBatchUnits1, CalculatorBatchUnits calculatorBatchUnits2) {
                    String exp1 = calculatorBatchUnits1.ExpireDate;
                    String exp2 = calculatorBatchUnits2.ExpireDate;
                    int res = String.CASE_INSENSITIVE_ORDER.compare(exp1, exp2);
                    if (res == 0) {
                        res = exp1.compareTo(exp2);
                    }
                    return res;
                }
            });
        }
        this.productName = productName;
        this.productId = productId;
        this.userPrice = userPrice;
        this.onHandQtyStock = onHandQtyStock;
        this.customerId = customerId;
        this.callOrderId = callOrderId;
        batchesAdapter = new BaseSelectionRecyclerAdapter<CalculatorBatchUnits>(getVaranegarActvity(), calculatorBatchUnits, false) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.batch_item_layout, parent, false);
                return new BatchViewHolder(view, batchesAdapter, getContext());
            }
        };
    }

    public interface OnCalcFinish {
        void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, @Nullable List<BatchQty> batchQtyList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    public OnCalcFinish onCalcFinish;




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        onActivityResult(requestCode, resultCode, data);
        setCancelable(false);
         if (requestCode == 4000){
            ArrayList<String> result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.close_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        fabVoice=view.findViewById(R.id.fabVoice);

        setCancelable(false);
        fabVoice.setOnClickListener(v -> {
            String language = "fa-IR";
            Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
            try {
                final Fragment callingFragment = getActivity().getFragmentManager()
                        .findFragmentById(R.id.li);
                getParentFragment().startActivityForResult(intent,4000);
            } catch (Exception e){

            }
        });

//        String numberProduct=getArguments().getString("productNumber","");
//
//        if (numberProduct !=null &&!numberProduct.isEmpty())
//            calculator.setvalue(ConvertFaNumType.convert(numberProduct));

        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        final SysConfigModel showStockLevel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
        final SysConfigModel orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);

        priceTextView = (TextView) view.findViewById(R.id.price_text_view);
        userPriceTextView = (TextView) view.findViewById(R.id.user_price_text_view);
        benefitTextView = (TextView) view.findViewById(R.id.benefit_text_view);
        inventoryQtyTextView = ((TextView) view.findViewById(R.id.inventory_qty_text_view));

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        productNameTextView.setText(productName);
        calculator = (Calculator) view.findViewById(R.id.calculator);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.batch_linear_layout);


        if (batchesAdapter != null) {
            BigDecimal totalQty = BigDecimal.ZERO;
            for (CalculatorBatchUnits calculatorBatchUnits :
                    batchesAdapter.getItems()) {
                BigDecimal qty = calculatorBatchUnits.calculatorUnits.getTotalQty();
                if (qty == null)
                    qty = BigDecimal.ZERO;
                totalQty = totalQty.add(qty);
            }
            /*if (totalQty.compareTo(BigDecimal.ZERO) == 1)
                selective = true;*/
            linearLayout.setVisibility(View.VISIBLE);
            BaseRecyclerView batchRecyclerView = view.findViewById(R.id.batch_recyclear_view);
            batchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            batchRecyclerView.setAdapter(batchesAdapter);
            batchesAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int position, boolean selected) {
                    if (selected) {
                        CalculatorBatchUnits selectedUnit = batchesAdapter.getSelectedItem();
                        if (benefitTextView != null)
                            benefitTextView.setText(selectedUnit.UserPrice.subtract(selectedUnit.Price).compareTo(Currency.valueOf(0)) >= 0 ? HelperMethods.currencyToString(selectedUnit.UserPrice.subtract(selectedUnit.Price)) : "-");
                        priceTextView.setText(HelperMethods.currencyToString(selectedUnit.Price));
                        if (userPriceTextView != null)
                            userPriceTextView.setText(HelperMethods.currencyToString(selectedUnit.UserPrice));
                        if (onHandQtyStock != null) {
                            OnHandQtyStock batchOnHandQtyStock = onHandQtyStock;
                            batchOnHandQtyStock.OnHandQty = selectedUnit.BatchOnHandQty;
                            Pair<String, Integer> p = OnHandQtyManager.showInventoryQty(getContext(), batchOnHandQtyStock, showStockLevel, orderPointCheckType);
                            if (p != null) {
                                inventoryQtyTextView.setText(p.first);
                                inventoryQtyTextView.setTextColor(p.second);
                            } else
                                inventoryQtyTextView.setVisibility(View.GONE);
                        }
                        calculator.setUnits(selectedUnit.calculatorUnits);

                    }
                }
            });

            calculator.setOnValueChangeListener(new Calculator.OnValueChangeListener() {
                @Override
                public void onValueChange(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit) {
                    if (selective)
                        batchesAdapter.notifyDataSetChanged();
                }
            });
            CalculatorBatchUnits selectedUnit = batchesAdapter.get(0);
            if (onHandQtyStock != null) {
                OnHandQtyStock batchOnHandQtyStock = onHandQtyStock;
                if (selective)
                    batchOnHandQtyStock.OnHandQty = selectedUnit.BatchOnHandQty;
                Pair<String, Integer> p = OnHandQtyManager.showInventoryQty(getContext(), batchOnHandQtyStock, showStockLevel, orderPointCheckType);
                if (p != null) {
                    inventoryQtyTextView.setText(p.first);
                    inventoryQtyTextView.setTextColor(p.second);
                } else
                    inventoryQtyTextView.setVisibility(View.GONE);
            }

            if (selective) {
                calculator.setUnits(selectedUnit.calculatorUnits);
                batchesAdapter.select(0);
                CustomerPriceManager customerPriceManager = new CustomerPriceManager(getContext());
                try {
                    customerPriceManager.saveCustomPrice(customerId, callOrderId, productId, selectedUnit.Price, selectedUnit.PriceId, selectedUnit.UserPrice);
                    priceTextView.setText(HelperMethods.currencyToString(selectedUnit.Price));
                    if (userPriceTextView != null)
                        userPriceTextView.setText(HelperMethods.currencyToString(selectedUnit.UserPrice));
                    if (benefitTextView != null)
                        benefitTextView.setText(selectedUnit.UserPrice.subtract(selectedUnit.Price).compareTo(Currency.valueOf(0)) >= 0 ? HelperMethods.currencyToString(selectedUnit.UserPrice.subtract(selectedUnit.Price)) : "-");
                    batchesAdapter.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                BaseUnit bulkUnits = null;
                BaseUnit bulkUnit = new BaseUnit();
                List<DiscreteUnit> discreteUnits;
                HashMap<String, DiscreteUnit> calculatedDiscreteUnits = new HashMap<>();
                for (CalculatorBatchUnits calculatorBatchUnits : batchesAdapter.getItems()) {
                    for (DiscreteUnit unit :
                            calculatorBatchUnits.calculatorUnits.getDiscreteUnits()) {
                        DiscreteUnit discreteUnit;
                        discreteUnit = calculatedDiscreteUnits.get(String.valueOf(unit.ConvertFactor));
                        if (discreteUnit == null) {
                            discreteUnit = new DiscreteUnit();
                            discreteUnit.ConvertFactor = unit.ConvertFactor;
                            discreteUnit.Name = unit.Name;
                            discreteUnit.ProductUnitId = unit.ProductUnitId;
                            discreteUnit.IsDefault = unit.IsDefault;
                            discreteUnit.Unit = unit.Unit;
                            discreteUnit.value = unit.value;
                        } else {
                            discreteUnit.value = discreteUnit.value + unit.value;
                            calculatedDiscreteUnits.remove(String.valueOf(unit.ConvertFactor));
                        }
                        calculatedDiscreteUnits.put(String.valueOf(unit.ConvertFactor), discreteUnit);
                    }
                    if (calculatorBatchUnits.calculatorUnits.getBulkUnit() != null) {
                        BaseUnit bulk = calculatorBatchUnits.calculatorUnits.getBulkUnit();
                        if (bulkUnits == null) {
                            bulkUnit.ProductUnitId = bulk.ProductUnitId;
                            bulkUnit.Name = bulk.Name;
                            bulkUnit.Unit = bulk.Unit;
                            bulkUnit.value = bulk.value;
                            bulkUnit.IsDefault = bulk.IsDefault;
                            bulkUnit.Readonly = bulk.Readonly;
                        } else {
                            bulkUnit.value = bulkUnit.value + bulk.value;
                        }
                        bulkUnits = bulkUnit;
                    }
                }
                discreteUnits = new ArrayList<>(calculatedDiscreteUnits.values());
                Collections.sort(discreteUnits, new Comparator<DiscreteUnit>() {
                    @Override
                    public int compare(DiscreteUnit discreteUnit1, DiscreteUnit discreteUnit2) {
                        return Double.compare(discreteUnit2.ConvertFactor, discreteUnit1.ConvertFactor);
                    }
                });
                CalculatorUnits calculatorUnits = new CalculatorUnits(discreteUnits, bulkUnits);
                calculator.setUnits(calculatorUnits);
                CustomerPriceManager customerPriceManager = new CustomerPriceManager(getContext());
                try {
                    customerPriceManager.saveCustomPrice(customerId, callOrderId, productId, selectedUnit.Price, selectedUnit.PriceId, selectedUnit.UserPrice);
                    priceTextView.setText(HelperMethods.currencyToString(selectedUnit.Price));
                    if (userPriceTextView != null)
                        userPriceTextView.setText(HelperMethods.currencyToString(selectedUnit.UserPrice));
                    if (benefitTextView != null)
                        benefitTextView.setText(selectedUnit.UserPrice.subtract(selectedUnit.Price).compareTo(Currency.valueOf(0)) >= 0 ? HelperMethods.currencyToString(selectedUnit.UserPrice.subtract(selectedUnit.Price)) : "-");
                    batchesAdapter.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            calculator.setUnits(calculatorUnits);
            if (benefitTextView != null)
                benefitTextView.setText(userPrice.subtract(unitPrice).compareTo(Currency.valueOf(0)) >= 0 ? HelperMethods.currencyToString(userPrice.subtract(unitPrice)) : "-");
            priceTextView.setText(HelperMethods.currencyToString(unitPrice));
            if (userPriceTextView != null)
                userPriceTextView.setText(HelperMethods.currencyToString(userPrice));
            if (onHandQtyStock != null) {
                Pair<String, Integer> p = OnHandQtyManager.showInventoryQty(getContext(), onHandQtyStock, showStockLevel, orderPointCheckType);
                if (p != null) {
                    inventoryQtyTextView.setText(p.first);
                    inventoryQtyTextView.setTextColor(p.second);
                } else
                    inventoryQtyTextView.setVisibility(View.GONE);
            }
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor) || SysConfigManager.compare(new SysConfigManager(getContext()).read(ConfigKey.SimplePresale, SysConfigManager.cloud), true)) {
            View header = view.findViewById(R.id.header);
            if (header != null)
                header.setVisibility(View.GONE);
        }

        calculator.onDoneClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (batchesAdapter != null) {
                    String error = null;
                    SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                    SysConfigModel inventoryControl = sysConfigManager.read(ConfigKey.InventoryControl, SysConfigManager.cloud);
                    if (!selective && !batchesAdapter.isEnabled()) {
                        boolean isBulk = (calculator.getBulkUnit() != null && calculator.getUnits().size() == 0);
                        double sum = 0;
                        if (isBulk) {
                            sum = calculator.getBulkUnit().value;
                        } else {
                            sum = Linq.sumDouble(calculator.getUnits(), new Linq.Selector<DiscreteUnit, Double>() {
                                @Override
                                public Double select(DiscreteUnit item) {
                                    return item.value * item.ConvertFactor;
                                }
                            });
                        }
                        for (CalculatorBatchUnits calculatorBatchUnits :
                                batchesAdapter.getItems()) {
                            for (DiscreteUnit discreteUnit : calculatorBatchUnits.calculatorUnits.getDiscreteUnits())
                                discreteUnit.value = 0;
                            if (calculatorBatchUnits.calculatorUnits.getBulkUnit() != null)
                                calculatorBatchUnits.calculatorUnits.getBulkUnit().value = 0;
                        }
                        for (CalculatorBatchUnits calculatorBatchUnits :
                                batchesAdapter.getItems()) {
                            if (sum <= 0)
                                break;
                            double onhandQty = calculatorBatchUnits.BatchOnHandQty == null ? 0 : HelperMethods.bigDecimalToDouble(calculatorBatchUnits.BatchOnHandQty);
                            double value;
                            if (sum <= onhandQty) {
                                if (isBulk) {
                                    value = sum;
                                    calculatorBatchUnits.calculatorUnits.getBulkUnit().value = value;
                                } else {
                                    List<DiscreteUnit> discreteUnits1 = calculatorBatchUnits.calculatorUnits.getDiscreteUnits();
                                    BigDecimal remainder = BigDecimal.valueOf(sum).remainder(BigDecimal.valueOf(discreteUnits1.get(discreteUnits1.size() - 1).ConvertFactor));
                                    value = sum - HelperMethods.bigDecimalToDouble(remainder);
                                    discreteUnits1.get(discreteUnits1.size() - 1).value = (value / discreteUnits1.get(discreteUnits1.size() - 1).ConvertFactor);
                                    if (calculatorBatchUnits.calculatorUnits.getBulkUnit() != null)
                                        calculatorBatchUnits.calculatorUnits.getBulkUnit().value = value;
                                }
                            } else {
                                if (isBulk) {
                                    value = onhandQty;
                                    calculatorBatchUnits.calculatorUnits.getBulkUnit().value = value;
                                } else {
                                    List<DiscreteUnit> discreteUnits1 = calculatorBatchUnits.calculatorUnits.getDiscreteUnits();
                                    BigDecimal remainder = BigDecimal.valueOf(onhandQty).remainder(BigDecimal.valueOf(discreteUnits1.get(discreteUnits1.size() - 1).ConvertFactor));
                                    value = onhandQty - HelperMethods.bigDecimalToDouble(remainder);
                                    discreteUnits1.get(discreteUnits1.size() - 1).value = (value / discreteUnits1.get(discreteUnits1.size() - 1).ConvertFactor);
                                    if (calculatorBatchUnits.calculatorUnits.getBulkUnit() != null) {
                                        calculatorBatchUnits.calculatorUnits.getBulkUnit().value = value;
                                    }
                                }
                            }
                            sum = sum - value;
                        }
                        if (sum > 0) {
                            if (SysConfigManager.compare(inventoryControl, true))
                                error = getString(R.string.input_value_is_larger_than_sum_of_batch_qtys);
                            else {
                                if (isBulk) {
                                    batchesAdapter.getItems().get(batchesAdapter.size() - 1).calculatorUnits.getBulkUnit().value += sum;
                                } else {
                                    BaseUnit bulkUnit = batchesAdapter.getItems().get(batchesAdapter.size() - 1).calculatorUnits.getBulkUnit();
                                    List<DiscreteUnit> discreteUnits1 = batchesAdapter.getItems().get(batchesAdapter.size() - 1).calculatorUnits.getDiscreteUnits();
                                    discreteUnits1.get(discreteUnits1.size() - 1).value += (sum / discreteUnits1.get(discreteUnits1.size() - 1).ConvertFactor);
                                    if (bulkUnit != null)
                                        bulkUnit.value += sum;
                                }
                            }
                        } else {
                            CalculatorBatchUnits selectedUnit = batchesAdapter.get(0);
                            calculator.setUnits(selectedUnit.calculatorUnits);
                            batchesAdapter.select(0);
                            CustomerPriceManager customerPriceManager = new CustomerPriceManager(getActivity());
                            try {
                                customerPriceManager.saveCustomPrice(customerId, callOrderId, productId, selectedUnit.Price, selectedUnit.PriceId, selectedUnit.UserPrice);
                                priceTextView.setText(HelperMethods.currencyToString(selectedUnit.Price));
                                if (userPriceTextView != null)
                                    userPriceTextView.setText(HelperMethods.currencyToString(selectedUnit.UserPrice));
                                benefitTextView.setText(selectedUnit.UserPrice.subtract(selectedUnit.Price).compareTo(Currency.valueOf(0)) >= 0 ? HelperMethods.currencyToString(selectedUnit.UserPrice.subtract(selectedUnit.Price)) : "-");
                                batchesAdapter.setEnabled(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    BaseUnit baseUnit = null;
                    List<DiscreteUnit> discreteUnitList = new ArrayList<>();
                    List<BatchQty> qtyList = new ArrayList<>();
                    double batchValue = 0;
                    if (error == null) {
                        error = checkBatchDifferentPrices();
                        for (CalculatorBatchUnits calculatorBatchUnits : batchesAdapter.getItems()) {
                            BaseUnit bulk = calculatorBatchUnits.calculatorUnits.getBulkUnit();
                            List<DiscreteUnit> discreteUnits = calculatorBatchUnits.calculatorUnits.getDiscreteUnits();
                            BatchQty batchQty = new BatchQty();
                            batchQty.Qty = (bulk != null && discreteUnits.size() == 0) ? new BigDecimal(String.valueOf(bulk.value)) : calculatorBatchUnits.getTotalQty();
                            batchQty.BatchRef = calculatorBatchUnits.BatchRef;
                            batchQty.BatchNo = calculatorBatchUnits.BatchNo;
                            batchQty.ItemRef = calculatorBatchUnits.ItemRef;
                            if (batchQty.Qty.compareTo(BigDecimal.ZERO) > 0) {
                                orderedBatchUnits = new CalculatorBatchUnits();
                                orderedBatchUnits.Price = calculatorBatchUnits.Price;
                                orderedBatchUnits.PriceId = calculatorBatchUnits.PriceId;
                                orderedBatchUnits.UserPrice = calculatorBatchUnits.UserPrice;
                            }
                            double oneBatchValue = 0;
                            if (bulk != null) {
                                if (baseUnit == null)
                                    baseUnit = new BaseUnit();
                                baseUnit.Unit = bulk.Unit;
                                baseUnit.Name = bulk.Name;
                                baseUnit.ProductUnitId = bulk.ProductUnitId;
                                baseUnit.value += bulk.value;
                                if (discreteUnits.size() == 0) {
                                    oneBatchValue = bulk.value;
                                    batchValue += bulk.value;
                                }
                            }
                            for (DiscreteUnit discreteUnit :
                                    discreteUnits) {
                                final DiscreteUnit dis = new DiscreteUnit();
                                dis.ConvertFactor = discreteUnit.ConvertFactor;
                                dis.Name = discreteUnit.Name;
                                dis.Unit = discreteUnit.Unit;
                                dis.value = discreteUnit.value;
                                double value = discreteUnit.getTotalQty();
                                oneBatchValue += value;
                                batchValue += value;
                                dis.ProductUnitId = discreteUnit.ProductUnitId;
                                DiscreteUnit exists = Linq.findFirst(discreteUnitList, new Linq.Criteria<DiscreteUnit>() {
                                    @Override
                                    public boolean run(DiscreteUnit item) {
                                        return item.ProductUnitId.equals(dis.ProductUnitId);
                                    }
                                });
                                if (exists != null)
                                    exists.value += dis.value;
                                else
                                    discreteUnitList.add(dis);
                            }
                            if (SysConfigManager.compare(inventoryControl, true) && oneBatchValue > HelperMethods.bigDecimalToDouble(calculatorBatchUnits.BatchOnHandQty)) {
                                error = getString(R.string.value_is_larger_than_batch_onhand_qty);
                            }
                            qtyList.add(batchQty);
                        }
                    }
                    if (error == null) {
                        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && batchValue == 0) {
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setIcon(Icon.Error);
                            dialog.setMessage(R.string.please_input_qty);
                            dialog.setTitle(R.string.error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.show();
                        } else {
                            CustomerPriceManager customerPriceManager = new CustomerPriceManager(getContext());
                            try {
                                customerPriceManager.saveCustomPrice(customerId, callOrderId, productId, orderedBatchUnits.Price, orderedBatchUnits.PriceId, orderedBatchUnits.UserPrice);
                            } catch (Exception e) {
                                Timber.e("Error on insert customer product price");
                            }
                            try {
                                checkMaxValue();
                                dismiss();
                                if (onCalcFinish != null)
                                    onCalcFinish.run(discreteUnitList, baseUnit, qtyList);
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        }
                    } else {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setIcon(Icon.Error);
                        dialog.setMessage(error);
                        dialog.setTitle(R.string.error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                } else {
                    if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && calculator.isEmpty()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setIcon(Icon.Error);
                        dialog.setMessage(R.string.please_input_qty);
                        dialog.setTitle(R.string.error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    } else {
                        try {
                            checkMaxValue();
                            dismiss();
                            if (onCalcFinish != null)
                                onCalcFinish.run(calculator.getUnits(), calculator.getBulkUnit(), null);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                }
            }
        };
        if (_voicetoText!=null &&!_voicetoText.isEmpty())
            calculator.setvalue(_voicetoText);
    }

    private void checkMaxValue() throws Exception {
        if (maxValue != null) {
            BigDecimal total = calculator.getBulkUnit() == null ? calculator.getTotal() : calculator.getBulkUnit().getQty();
            if (total == null)
                total = BigDecimal.ZERO;
            if (total.compareTo(maxValue) > 0) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setMessage(R.string.value_is_larger_than_max_value);
                dialog.setTitle(R.string.error);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
                throw new Exception("Maximum value is " + maxValue + " but the entered value is " + total);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_order_calculator_dialog, container, false);
    }

    private String checkBatchDifferentPrices() {
        List<CalculatorBatchUnits> calculatorBatchUnitss = batchesAdapter.getItems();
        Currency price = null;
        for (CalculatorBatchUnits calculatorBatchUnits1 : calculatorBatchUnitss) {
            BigDecimal qty1 = calculatorBatchUnits1.getTotalQty();
            if (qty1 != null && qty1.compareTo(BigDecimal.ZERO) == 1) {
                if (price != null && price.compareTo(calculatorBatchUnits1.Price) != 0)
                    return getString(R.string.can_not_choose_diffrecent_prices);
                else
                    price = calculatorBatchUnits1.Price;
            }
        }
        return null;
    }

    private class BatchViewHolder extends BaseViewHolder<CalculatorBatchUnits> {
        private final TextView batchTextView;
        private final TextView selectedBatchTextView;

        public BatchViewHolder(View itemView, BaseRecyclerAdapter<CalculatorBatchUnits> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            batchTextView = itemView.findViewById(R.id.batch_value_text_view);
            selectedBatchTextView = itemView.findViewById(R.id.selected_batch_value_text_view);
        }

        @Override
        public void bindView(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    batchesAdapter.notifyItemClicked(position);
                }
            });
            CalculatorBatchUnits calculatorBatchUnits = batchesAdapter.get(position);
            BigDecimal qty = (calculatorBatchUnits.calculatorUnits.getBulkUnit() != null && calculatorBatchUnits.calculatorUnits.getDiscreteUnits().size() == 0) ? new BigDecimal(String.valueOf(calculatorBatchUnits.calculatorUnits.getBulkUnit().value)) : calculatorBatchUnits.getTotalQty();
            String title = calculatorBatchUnits.BatchNo;
            if (!" ".equals(calculatorBatchUnits.ExpireDate))
                title += " - " + calculatorBatchUnits.ExpireDate + "\r\n" + " (" + HelperMethods.bigDecimalToString(qty);

            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel showStockLevel = sysConfigManager.read(ConfigKey.ShowStockLevel, SysConfigManager.cloud);
            SysConfigModel orderPointCheckType = sysConfigManager.read(ConfigKey.OrderPointCheckType, SysConfigManager.cloud);
            if (SysConfigManager.compare(showStockLevel, true) && SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty))
                title += "/" + HelperMethods.bigDecimalToString(calculatorBatchUnits.BatchOnHandQty);
            if (!" ".equals(calculatorBatchUnits.ExpireDate))
                title += ") ";
            if (batchesAdapter.getSelectedPosition() == position) {
                selectedBatchTextView.setVisibility(View.VISIBLE);
                batchTextView.setVisibility(View.GONE);
                selectedBatchTextView.setText(title);
            } else {
                selectedBatchTextView.setVisibility(View.GONE);
                batchTextView.setVisibility(View.VISIBLE);
                batchTextView.setText(title);
            }
        }
    }
}
