//package com.varanegar.vaslibrary.ui.dialog;
//
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.varanegar.framework.database.querybuilder.criteria.Criteria;
//import com.varanegar.framework.util.component.CuteDialogWithToolbar;
//import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
//import com.varanegar.framework.util.component.cutemessagedialog.Icon;
//import com.varanegar.framework.util.recycler.DividerItemDecoration;
//import com.varanegar.vaslibrary.R;
//import com.varanegar.vaslibrary.manager.discountmanager.DiscountItemCountViewManager;
//import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
//import com.varanegar.vaslibrary.manager.orderprizeview.OrderPrizeViewManager;
//import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountViewModel;
//import com.varanegar.vaslibrary.model.orderprize.OrderPrize;
//import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
//import com.varanegar.vaslibrary.model.orderprizeview.OrderPrizeViewModel;
//import com.varanegar.vaslibrary.ui.dialog.choiceprize.ProductChoicePrizeAdapter;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * Created by A.Jafarzadeh on 12/19/2017.
// */
//
//public class ChoicePrizesDialog extends CuteDialogWithToolbar {
//
//    private UUID discountId;
//    private int discountRef;
//    private UUID customerId;
//    private long totalQty;
//    private UUID callOrderId;
//
//    @Override
//    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
//        super.setTargetFragment(fragment, requestCode);
//    }
//
//    public void setDiscountId(@NonNull UUID discountId, @NonNull int discountRef, @NonNull UUID customerId, @NonNull long totalQty, @NonNull UUID callOrderId) {
//        Bundle bundle = new Bundle();
//        bundle.putString("e38889ab-377d-47c5-baf9-98cc1c461a63", discountId.toString());
//        bundle.putInt("53c92c93-5dff-46dc-86fd-dd0860157812", discountRef);
//        bundle.putString("72c30c0d-10ba-407b-94d1-43d72de00c4b", customerId.toString());
//        bundle.putLong("700111c0-962e-4b42-85ad-a9667047f36e", totalQty);
//        bundle.putString("026d3f80-11c1-41e7-b1d3-60668860ed25", callOrderId.toString());
//        setArguments(bundle);
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        discountId = UUID.fromString(getArguments().getString("e38889ab-377d-47c5-baf9-98cc1c461a63"));
//        discountRef = getArguments().getInt("53c92c93-5dff-46dc-86fd-dd0860157812");
//        customerId = UUID.fromString(getArguments().getString("72c30c0d-10ba-407b-94d1-43d72de00c4b"));
//        totalQty = getArguments().getLong("700111c0-962e-4b42-85ad-a9667047f36e");
//        callOrderId = UUID.fromString(getArguments().getString("026d3f80-11c1-41e7-b1d3-60668860ed25"));
//        setTitle(getString(R.string.selective_prize) + "      " + getString(R.string.total_qty_label) + totalQty);
//        final View view = inflater.inflate(R.layout.choice_prize_dialog, container, false);
//        DiscountItemCountViewManager discountItemCountViewManager = new DiscountItemCountViewManager(getContext());
//        List<DiscountItemCountViewModel> discountItemCountViewModels = discountItemCountViewManager.getItems(DiscountItemCountViewManager.getAllDiscountItems(discountRef));
//        OrderPrizeViewManager orderPrizeViewManager = new OrderPrizeViewManager(getContext());
//        List<OrderPrizeViewModel> orderPrizes = orderPrizeViewManager.getItems(OrderPrizeViewManager.getCustomerOrderPrizes(customerId, discountRef, callOrderId));
//        final List<OrderPrizeViewModel> orderPrizeViewModels = new ArrayList<>();
//        if (orderPrizes != null && orderPrizes.size() > 0) {
//            for (DiscountItemCountViewModel discountItemCountViewModel : discountItemCountViewModels) {
//                boolean exsits = false;
//                for (OrderPrizeViewModel orderPrizeViewModel : orderPrizes) {
//                    if (discountItemCountViewModel.DisRef == orderPrizeViewModel.DisRef && discountItemCountViewModel.GoodsRef == orderPrizeViewModel.GoodsRef) {
//                        orderPrizeViewModels.add(orderPrizeViewModel);
//                        exsits = true;
//                    }
//                }
//                if (!exsits) {
//                    OrderPrizeViewModel orderPrizeViewModel = new OrderPrizeViewModel();
//                    orderPrizeViewModel.UniqueId = UUID.randomUUID();
//                    orderPrizeViewModel.CustomerId = customerId;
//                    orderPrizeViewModel.DiscountId = discountId;
//                    orderPrizeViewModel.DisRef = discountItemCountViewModel.DisRef;
//                    orderPrizeViewModel.GoodsRef = discountItemCountViewModel.GoodsRef;
//                    orderPrizeViewModel.ProductName = discountItemCountViewModel.ProductName;
//                    orderPrizeViewModel.ProductCode = discountItemCountViewModel.ProductCode;
//                    orderPrizeViewModel.TotalQty = BigDecimal.ZERO;
//                    orderPrizeViewModel.ProductId = discountItemCountViewModel.ProductId;
//                    orderPrizeViewModel.CallOrderId = callOrderId;
//                    orderPrizeViewModels.add(orderPrizeViewModel);
//                }
//            }
//
//        } else {
//            boolean first = true;
//            for (DiscountItemCountViewModel discountItemCountViewModel : discountItemCountViewModels) {
//                OrderPrizeViewModel orderPrizeViewModel = new OrderPrizeViewModel();
//                orderPrizeViewModel.UniqueId = UUID.randomUUID();
//                orderPrizeViewModel.CustomerId = customerId;
//                orderPrizeViewModel.DiscountId = discountId;
//                orderPrizeViewModel.DisRef = discountItemCountViewModel.DisRef;
//                orderPrizeViewModel.GoodsRef = discountItemCountViewModel.GoodsRef;
//                orderPrizeViewModel.ProductName = discountItemCountViewModel.ProductName;
//                orderPrizeViewModel.ProductCode = discountItemCountViewModel.ProductCode;
//                orderPrizeViewModel.CallOrderId = callOrderId;
//                if (first)
//                    orderPrizeViewModel.TotalQty = totalQty;
//                else
//                    orderPrizeViewModel.TotalQty = 0;
//                orderPrizeViewModel.ProductId = discountItemCountViewModel.ProductId;
//                orderPrizeViewModels.add(orderPrizeViewModel);
//                first = false;
//            }
//        }
//
//        long totalQty = 0;
//        for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels) {
//            totalQty = totalQty + orderPrizeViewModel.TotalQty;
//        }
//        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.choice_prize_recycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.color.grey_light_light_light, 1));
//        final ProductChoicePrizeAdapter productChoicePrizeAdapter = new ProductChoicePrizeAdapter(orderPrizeViewModels, totalQty, getVaranegarActvity());
//        recyclerView.setAdapter(productChoicePrizeAdapter);
//        TextView divideTextView = (TextView) view.findViewById(R.id.divide_text_view);
//        final long finalTotalQty = totalQty;
//        divideTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean first = true;
//                List<OrderPrizeViewModel> orderPrizeViewModels1 = new ArrayList<>();
//                for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels) {
//                    if (first) {
//                        orderPrizeViewModel.TotalQty = (finalTotalQty / orderPrizeViewModels.size()) + (finalTotalQty % orderPrizeViewModels.size());
//                        first = false;
//                    } else {
//                        orderPrizeViewModel.TotalQty = (finalTotalQty / orderPrizeViewModels.size());
//                    }
//                    orderPrizeViewModels1.add(orderPrizeViewModel);
//                }
//                orderPrizeViewModels.clear();
//                for (OrderPrizeViewModel orderPrizeViewModel : orderPrizeViewModels1) {
//                    orderPrizeViewModels.add(orderPrizeViewModel);
//                }
//                final ProductChoicePrizeAdapter productChoicePrizeAdapter = new ProductChoicePrizeAdapter(orderPrizeViewModels, finalTotalQty, getVaranegarActvity());
//                recyclerView.setAdapter(productChoicePrizeAdapter);
//            }
//        });
//        TextView cancelTextView = (TextView) view.findViewById(R.id.cancel_text_view);
//        cancelTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//            }
//        });
//        TextView saveTextView = (TextView) view.findViewById(R.id.ok_text_view);
//        saveTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                long orderTotalQty = 0;
//                for (OrderPrizeViewModel orderPrizeViewModel : productChoicePrizeAdapter.getOrderPrizeViewModel()) {
//                    orderTotalQty = orderTotalQty + orderPrizeViewModel.TotalQty;
//                }
//                if (orderTotalQty == finalTotalQty) {
//                    try {
//                        OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
//                        orderPrizeManager.delete(Criteria.equals(OrderPrize.DisRef, discountRef).and(Criteria.equals(OrderPrize.CustomerId, customerId)).and(Criteria.equals(OrderPrize.CallOrderId, callOrderId)));
//                        List<OrderPrizeModel> orderPrizeModels = new ArrayList<>();
//                        for (OrderPrizeViewModel orderPrizeViewModel : productChoicePrizeAdapter.getOrderPrizeViewModel()) {
//                            if (orderPrizeViewModel.TotalQty > 0) {
//                                orderPrizeModels.add(orderPrizeViewModel.getOrderPrize());
//                            }
//                        }
//                        orderPrizeManager.insertOrUpdate(orderPrizeModels);
//                        choicePrizeDialogListener listener = (choicePrizeDialogListener) getTargetFragment();
//                        listener.onFinishChoicePrize(discountRef, customerId, callOrderId);
//                        dismiss();
//                    } catch (Exception ex) {
//                        CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
//                        cuteMessageDialog.setIcon(Icon.Error);
//                        cuteMessageDialog.setTitle(R.string.error);
//                        cuteMessageDialog.setMessage(R.string.error_saving_request);
//                        cuteMessageDialog.setPositiveButton(R.string.ok, null);
//                        cuteMessageDialog.show();
//                    }
//                } else {
//                    CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
//                    cuteMessageDialog.setIcon(Icon.Error);
//                    cuteMessageDialog.setTitle(R.string.error);
//                    cuteMessageDialog.setMessage(getString(R.string.assige_prizes) + finalTotalQty + getString(R.string.number) + "\n" + getString(R.string.choiced_prizes) + orderTotalQty + getString(R.string.number));
//                    cuteMessageDialog.setPositiveButton(R.string.ok, null);
//                    cuteMessageDialog.show();
//                }
//            }
//        });
//        return view;
//    }
//
//    public interface choicePrizeDialogListener {
//        void onFinishChoicePrize(int disRef, UUID customerId, UUID callOrderId);
//    }
//
//}
