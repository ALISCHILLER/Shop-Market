package com.varanegar.vaslibrary.ui.dialog.choiceprize;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountItemCountViewManager;
import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
import com.varanegar.vaslibrary.manager.orderprizeview.OrderPrizeViewManager;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountViewModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrize;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
import com.varanegar.vaslibrary.model.orderprizeview.OrderPrizeViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import varanegar.com.discountcalculatorlib.viewmodel.DiscountOrderPrizeViewModel;

/**
 * Created by A.Jafarzadeh on 12/19/2017.
 */

public class ChoicePrizesDialog extends CuteDialogWithToolbar {

    private int discountRef;
    private UUID customerId;
    private BigDecimal totalQty;
    private UUID callOrderId;
    ArrayList<DiscountOrderPrizeViewModel> discountOrderPrizeViewModels = new ArrayList<>();

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    public void setDiscountId(@NonNull int discountRef, @NonNull UUID customerId, @NonNull BigDecimal totalQty, @NonNull UUID callOrderId) {
        Bundle bundle = new Bundle();
        bundle.putInt("53c92c93-5dff-46dc-86fd-dd0860157812", discountRef);
        bundle.putString("72c30c0d-10ba-407b-94d1-43d72de00c4b", customerId.toString());
        bundle.putDouble("700111c0-962e-4b42-85ad-a9667047f36e", Double.valueOf(String.valueOf(totalQty)));
        bundle.putString("eb07e765-5d00-4d3c-9210-50dd3bc5e5ad", callOrderId.toString());
        setArguments(bundle);
    }

    public void setProducts(ArrayList<DiscountOrderPrizeViewModel> discountOrderPrizeViewModels) {
        this.discountOrderPrizeViewModels = discountOrderPrizeViewModels;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        discountRef = getArguments().getInt("53c92c93-5dff-46dc-86fd-dd0860157812");
        customerId = UUID.fromString(getArguments().getString("72c30c0d-10ba-407b-94d1-43d72de00c4b"));
        totalQty = new BigDecimal(getArguments().getDouble("700111c0-962e-4b42-85ad-a9667047f36e"));
        callOrderId = UUID.fromString(getArguments().getString("eb07e765-5d00-4d3c-9210-50dd3bc5e5ad"));
        final View view = inflater.inflate(R.layout.choice_prize_dialog, container, false);
        DiscountItemCountViewManager discountItemCountViewManager = new DiscountItemCountViewManager(getContext());
        List<DiscountItemCountViewModel> discountItemCountViewModels = discountItemCountViewManager.getItems(DiscountItemCountViewManager.getAllDiscountItems(discountRef));
        OrderPrizeViewManager orderPrizeViewManager = new OrderPrizeViewManager(getContext());
        List<OrderPrizeViewModel> orderPrizes = orderPrizeViewManager.getItems(OrderPrizeViewManager.getCustomerOrderPrizes(customerId, discountRef, callOrderId));
        final List<OrderPrizeViewModel> orderPrizeViewModels = new ArrayList<>();
        if (orderPrizes != null && orderPrizes.size() > 0) {
            for (DiscountItemCountViewModel discountItemCountViewModel : discountItemCountViewModels) {
                boolean exsits = false;
                for (OrderPrizeViewModel orderPrizeViewModel : orderPrizes) {
                    if (discountItemCountViewModel.DisRef == orderPrizeViewModel.DisRef && discountItemCountViewModel.GoodsRef == orderPrizeViewModel.GoodsRef) {
                        orderPrizeViewModels.add(orderPrizeViewModel);
                        exsits = true;
                    }
                }
                if (!exsits) {
                    OrderPrizeViewModel orderPrizeViewModel = new OrderPrizeViewModel();
                    orderPrizeViewModel.UniqueId = UUID.randomUUID();
                    orderPrizeViewModel.CustomerId = customerId;
                    orderPrizeViewModel.DisRef = discountItemCountViewModel.DisRef;
                    orderPrizeViewModel.GoodsRef = discountItemCountViewModel.GoodsRef;
                    orderPrizeViewModel.ProductName = discountItemCountViewModel.ProductName;
                    orderPrizeViewModel.ProductCode = discountItemCountViewModel.ProductCode;
                    orderPrizeViewModel.TotalQty = BigDecimal.ZERO;
                    orderPrizeViewModel.ProductId = discountItemCountViewModel.ProductId;
                    orderPrizeViewModel.CallOrderId = callOrderId;
                    orderPrizeViewModels.add(orderPrizeViewModel);
                }
            }

        } else {
            boolean first = true;
            for (DiscountItemCountViewModel discountItemCountViewModel : discountItemCountViewModels) {
                OrderPrizeViewModel orderPrizeViewModel = new OrderPrizeViewModel();
                orderPrizeViewModel.UniqueId = UUID.randomUUID();
                orderPrizeViewModel.CustomerId = customerId;
                orderPrizeViewModel.CallOrderId = callOrderId;
                orderPrizeViewModel.DisRef = discountItemCountViewModel.DisRef;
                orderPrizeViewModel.GoodsRef = discountItemCountViewModel.GoodsRef;
                orderPrizeViewModel.ProductName = discountItemCountViewModel.ProductName;
                orderPrizeViewModel.ProductCode = discountItemCountViewModel.ProductCode;
                if (first)
                    orderPrizeViewModel.TotalQty = totalQty;
                else
                    orderPrizeViewModel.TotalQty = BigDecimal.ZERO;
                orderPrizeViewModel.ProductId = discountItemCountViewModel.ProductId;
                orderPrizeViewModels.add(orderPrizeViewModel);
                first = false;
            }
        }

        BigDecimal totalQty = BigDecimal.ZERO;
        for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels) {
            totalQty = totalQty.add(orderPrizeViewModel.TotalQty);
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            List<OrderPrizeViewModel> orderPrizeViewModel1 = new ArrayList<>();
            for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels) {
                orderPrizeViewModel.TotalQty = BigDecimal.ZERO;
                orderPrizeViewModel1.add(orderPrizeViewModel);
            }
            orderPrizeViewModels.clear();
            for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModel1) {
                for (DiscountOrderPrizeViewModel discountOrderPrizeViewModel:
                        discountOrderPrizeViewModels) {
                    if (discountOrderPrizeViewModel.goodsRef == orderPrizeViewModel.GoodsRef) {
                        orderPrizeViewModels.add(orderPrizeViewModel);
                        break;
                    }
                }
            }
        }
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.choice_prize_recycler);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && (discountOrderPrizeViewModels == null || discountOrderPrizeViewModels.size() == 0)) {
            dismiss();
            return view;
        }
        final ProductChoicePrizeAdapter productChoicePrizeAdapter = new ProductChoicePrizeAdapter(orderPrizeViewModels, totalQty, getVaranegarActvity(), discountOrderPrizeViewModels);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.color.grey_light_light_light, 1));
        recyclerView.setAdapter(productChoicePrizeAdapter);
        TextView divideTextView = (TextView) view.findViewById(R.id.divide_text_view);
        final BigDecimal finalTotalQty = totalQty;
        divideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean first = true;
                boolean secondFirst = true;
                if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    List<DiscountOrderPrizeViewModel> discountOrderPrizeViewModels1 = new ArrayList<>();
                    List<OrderPrizeViewModel> orderPrizeViewModels1 = new ArrayList<>();
                    for (DiscountOrderPrizeViewModel orderPrizeViewModel : discountOrderPrizeViewModels) {
                        if (first) {
                            orderPrizeViewModel.totalQTy = (finalTotalQty.divide(new BigDecimal(discountOrderPrizeViewModels.size()), 0, RoundingMode.FLOOR)).add(finalTotalQty.remainder(new BigDecimal(discountOrderPrizeViewModels.size())));
                            first = false;
                        } else {
                            orderPrizeViewModel.totalQTy = (finalTotalQty.divide(new BigDecimal(discountOrderPrizeViewModels.size()), 0, RoundingMode.FLOOR));
                        }
                        discountOrderPrizeViewModels1.add(orderPrizeViewModel);
                    }
                    for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels) {
                        if (secondFirst) {
                            orderPrizeViewModel.TotalQty = (finalTotalQty.divide(new BigDecimal(orderPrizeViewModels.size()), 0, RoundingMode.FLOOR)).add(finalTotalQty.remainder(new BigDecimal(orderPrizeViewModels.size())));
                            secondFirst = false;
                        } else {
                            orderPrizeViewModel.TotalQty = (finalTotalQty.divide(new BigDecimal(orderPrizeViewModels.size()), 0, RoundingMode.FLOOR));
                        }
                        orderPrizeViewModels1.add(orderPrizeViewModel);
                    }
                    discountOrderPrizeViewModels.clear();
                    for (DiscountOrderPrizeViewModel orderPrizeViewModel : discountOrderPrizeViewModels1) {
                        discountOrderPrizeViewModels.add(orderPrizeViewModel);
                    }
                    orderPrizeViewModels.clear();
                    for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels1) {
                        orderPrizeViewModels.add(orderPrizeViewModel);
                    }
                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            ProductChoicePrizeAdapter productChoicePrizeAdapter = new ProductChoicePrizeAdapter(orderPrizeViewModels, finalTotalQty, getVaranegarActvity(), discountOrderPrizeViewModels);
                            recyclerView.setAdapter(productChoicePrizeAdapter);
                        }
                    };
                    handler.post(r);
                } else {
                    List<OrderPrizeViewModel> orderPrizeViewModels1 = new ArrayList<>();
                    for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels) {
                        if (first) {
                            orderPrizeViewModel.TotalQty = (finalTotalQty.divide(new BigDecimal(orderPrizeViewModels.size()), 0, RoundingMode.FLOOR)).add(finalTotalQty.remainder(new BigDecimal(orderPrizeViewModels.size())));
                            first = false;
                        } else {
                            orderPrizeViewModel.TotalQty = (finalTotalQty.divide(new BigDecimal(orderPrizeViewModels.size()), 0, RoundingMode.FLOOR));
                        }
                        orderPrizeViewModels1.add(orderPrizeViewModel);
                    }
                    orderPrizeViewModels.clear();
                    for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels1) {
                        orderPrizeViewModels.add(orderPrizeViewModel);
                    }
                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            ProductChoicePrizeAdapter productChoicePrizeAdapter = new ProductChoicePrizeAdapter(orderPrizeViewModels, finalTotalQty, getVaranegarActvity(), discountOrderPrizeViewModels);
                            recyclerView.setAdapter(productChoicePrizeAdapter);
                        }
                    };
                    handler.post(r);
                }
            }
        });
        TextView cancelTextView = (TextView) view.findViewById(R.id.cancel_text_view);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView saveTextView = (TextView) view.findViewById(R.id.ok_text_view);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
                List<OrderPrizeModel> orderPrizes = orderPrizeManager.getItems(OrderPrizeManager.getCustomerOrderPrizes(customerId, discountRef, callOrderId));
                HashMap<UUID, OrderPrizeModel> oldPrize = new HashMap<>();
                for (OrderPrizeModel orderPrizeModel : orderPrizes) {
                    oldPrize.put(orderPrizeModel.ProductId, orderPrizeModel);
                }
                BigDecimal orderTotalQty = BigDecimal.ZERO;
                for (OrderPrizeViewModel orderPrizeViewModel : productChoicePrizeAdapter.getOrderPrizeViewModel()) {
                    orderTotalQty = orderTotalQty.add(orderPrizeViewModel.TotalQty);
                }
                if (orderTotalQty.compareTo(finalTotalQty) == 0) {
                    try {
                        orderPrizeManager.delete(Criteria.equals(OrderPrize.DisRef, discountRef).and(Criteria.equals(OrderPrize.CustomerId, customerId)).and(Criteria.equals(OrderPrize.CallOrderId, callOrderId)));
                        List<OrderPrizeModel> orderPrizeModels = new ArrayList<>();
                        for (OrderPrizeViewModel orderPrizeViewModel : productChoicePrizeAdapter.getOrderPrizeViewModel()) {
//                            if (orderPrizeViewModel.TotalQty > 0) {
                            orderPrizeModels.add(orderPrizeViewModel.getOrderPrize());
//                            }
                        }
                        orderPrizeManager.insertOrUpdate(orderPrizeModels);
                        choicePrizeDialogListener listener = (choicePrizeDialogListener) getTargetFragment();
                        listener.onFinishChoicePrize(discountRef, customerId, callOrderId, oldPrize);
                        dismiss();
                    } catch (Exception ex) {
                        CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                        cuteMessageDialog.setIcon(Icon.Error);
                        cuteMessageDialog.setTitle(R.string.error);
                        cuteMessageDialog.setMessage(R.string.error_saving_request);
                        cuteMessageDialog.setPositiveButton(R.string.ok, null);
                        cuteMessageDialog.show();
                    }
                } else {
                    CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
                    cuteMessageDialog.setIcon(Icon.Error);
                    cuteMessageDialog.setTitle(R.string.error);
                    cuteMessageDialog.setMessage(getString(R.string.assige_prizes) + finalTotalQty + getString(R.string.number) + "\n" + getString(R.string.choiced_prizes) + orderTotalQty + getString(R.string.number));
                    cuteMessageDialog.setPositiveButton(R.string.ok, null);
                    cuteMessageDialog.show();
                }
            }
        });
        setTitle(getString(R.string.selective_prize) + "      " + getString(R.string.total_qty_label) + finalTotalQty);
        return view;
    }

    public interface choicePrizeDialogListener {
        void onFinishChoicePrize(int disRef, UUID customerId, UUID callOrderId, HashMap<UUID, OrderPrizeModel> oldPrize);
    }

}
