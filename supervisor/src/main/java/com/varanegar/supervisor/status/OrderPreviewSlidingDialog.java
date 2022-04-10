package com.varanegar.supervisor.status;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.SlidingDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderLineOrderQtyDetailViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderLineViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallOrderViewModel;
import com.varanegar.supervisor.webapi.model_old.CustomerCallViewModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.Request;
import timber.log.Timber;

public class OrderPreviewSlidingDialog extends SlidingDialog {
    public CustomerCallViewModel customerCallViewModel;
    private PairedItems orderTypeNamePairedItems;
    private PairedItems paymentTypePairedItems;
    private PairedItems totalAmountNamePairedItems;
    private PairedItems payableAmountPairedItems;
    private PairedItems addAmountNamePairedItems;
    private PairedItems discountAmountPairedItems;
    private BaseRecyclerView itemsRecyclerView;
    private ProgressView progressView;
    public CustomerCallOrderViewModel customerCallOrderViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.supervisor_order_preview_layout, container, false);
        progressView = view.findViewById(R.id.progress_view);
        orderTypeNamePairedItems = view.findViewById(R.id.order_type_name_paired_items);
        paymentTypePairedItems = view.findViewById(R.id.payment_type_paired_items);
        totalAmountNamePairedItems = view.findViewById(R.id.total_amount_name_paired_items);
        payableAmountPairedItems = view.findViewById(R.id.payable_amount_paired_items);
        addAmountNamePairedItems = view.findViewById(R.id.add_amount_name_paired_items);
        discountAmountPairedItems = view.findViewById(R.id.discount_amount_paired_items);


        itemsRecyclerView = view.findViewById(R.id.items_recycler_view);
        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        orderTypeNamePairedItems.setValue(customerCallOrderViewModel.OrderTypeName);
        paymentTypePairedItems.setValue(customerCallOrderViewModel.OrderPaymentTypeName);
        progressView.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OrderSummaryRequestViewModel requestViewModel = new OrderSummaryRequestViewModel();
                requestViewModel.CustId = customerCallOrderViewModel.CustomerUniqueId;
                requestViewModel.BuyTypeId = customerCallOrderViewModel.OrderPaymentTypeUniqueId;
                requestViewModel.OrderDate = customerCallOrderViewModel.CallDate;
                requestViewModel.SalePDate = customerCallOrderViewModel.SalePDate;
                requestViewModel.SaleOfficeRefSDS = customerCallOrderViewModel.SaleOfficeRefSDS;
                requestViewModel.DisType = 2;
                requestViewModel.OrderTypeId = customerCallOrderViewModel.OrderTypeUniqueId;
                requestViewModel.DealerId = customerCallViewModel.DealerUniqueId;
                requestViewModel.SupSaleEvcDetails = new ArrayList<>();
                for (CustomerCallOrderLineViewModel orderLineViewModel :
                        customerCallOrderViewModel.OrderLines) {
                    BigDecimal total = BigDecimal.ZERO;
                    for (CustomerCallOrderLineOrderQtyDetailViewModel qty :
                            orderLineViewModel.CustomerCallOrderLineOrderQtyDetails) {
                        total = total.add(qty.ConvertFactor.multiply(qty.Qty));
                    }
                    OrderSummaryRequestDetailViewModel rd = new OrderSummaryRequestDetailViewModel();
                    rd.GoodId = orderLineViewModel.ProductUniqueId;
                    rd.TotalQty = total;
                    requestViewModel.SupSaleEvcDetails.add(rd);
                }

                SupervisorApi api = new SupervisorApi(getContext());
                api.runWebRequest(api.getOrderPreview(requestViewModel), new WebCallBack<OrderSummaryResultViewModel>() {
                    @Override
                    protected void onFinish() {
                        Activity activity = getActivity();
                        if (isResumed() && activity != null && !activity.isFinishing())
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressView.finish();
                                }
                            });
                    }

                    @Override
                    protected void onSuccess(final OrderSummaryResultViewModel result, Request request) {
                        final Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalAmountNamePairedItems.setValue(VasHelperMethods.currencyToString(result.Amount));
                                    payableAmountPairedItems.setValue(VasHelperMethods.currencyToString(result.AmountNut));
                                    discountAmountPairedItems.setValue(VasHelperMethods.currencyToString(result.DisAmount));
                                    addAmountNamePairedItems.setValue(VasHelperMethods.currencyToString(result.AddAmount));
                                    BaseRecyclerAdapter<OrderSummaryLineResultViewModel> adapter =
                                            new BaseRecyclerAdapter<OrderSummaryLineResultViewModel>(getVaranegarActvity(), result.SaleEvcDetails) {
                                                @NonNull
                                                @Override
                                                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supervisor_order_preview_line, parent, false);
                                                    return new OrderPreviewLineViewHolder(view, this, activity);
                                                }
                                            };
                                    itemsRecyclerView.setAdapter(adapter);
                                }
                            });
                        }
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        Activity activity = getActivity();
                        if (isResumed() && activity != null && !activity.isFinishing()) {
                            String err = WebApiErrorBody.log(error, activity);
                            showErrorMessage(err);
                        }


                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Activity activity = getActivity();
                        if (isResumed() && activity != null && !activity.isFinishing()) {
                            Timber.e(t);
                            showErrorMessage(R.string.network_error);
                        }
                    }
                });

            }
        }).start();
    }

    void showErrorMessage(@StringRes int error) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            showErrorMessage(activity.getString(error));
        }
    }

    void showErrorMessage(String error) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    class OrderPreviewLineViewHolder extends BaseViewHolder<OrderSummaryLineResultViewModel> {

        private final TextView productNameTextView;
        private final TextView qtyTextView;
        private final TextView unitPriceTextView;
        private final TextView amountTextView;

        public OrderPreviewLineViewHolder(View itemView, BaseRecyclerAdapter<OrderSummaryLineResultViewModel> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            qtyTextView = itemView.findViewById(R.id.qty_text_view);
            unitPriceTextView = itemView.findViewById(R.id.unit_price_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);

        }

        @Override
        public void bindView(int position) {
            OrderSummaryLineResultViewModel item = recyclerAdapter.get(position);
            if (item.PrizeType == 1)
                itemView.setBackgroundColor(VasHelperMethods.getColor(getContext(), R.color.green_light_light_transparent));
            else
                itemView.setBackgroundColor(VasHelperMethods.getColor(getContext(), R.color.white));
            productNameTextView.setText(item.GoodName);
            qtyTextView.setText(VasHelperMethods.bigDecimalToString(item.TotalQty));
            unitPriceTextView.setText(VasHelperMethods.currencyToString(item.CustPrice));
            amountTextView.setText(VasHelperMethods.currencyToString(item.Amount));
        }
    }
}
