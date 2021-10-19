package com.varanegar.supervisor.status;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.CustomerCallOrderLineOrderQtyDetailViewModel;
import com.varanegar.supervisor.webapi.CustomerCallOrderLineViewModel;
import com.varanegar.supervisor.webapi.CustomerCallOrderViewModel;
import com.varanegar.supervisor.webapi.CustomerCallViewModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import okhttp3.Request;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/10/2018.
 */

class CustomerCallSummaryContextView extends ItemContextView<CustomerCallViewModel> {
    private TabLayout tabLayout;
    private ProgressView progressView;
    private CustomerCallViewModel customerCallViewModel;
    private ViewPager tabsViewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private View sendBtn;
    private View cancelBtn;
    public OnCustomerCallChanged onCustomerCallChanged;
    private TextView cancelTextView;
    private ImageView cancelImageView;
    private TextView sendTextView;

    public interface OnCustomerCallChanged {
        void run();
    }

    private void onCustomerCallChanged() {
        if (onCustomerCallChanged != null)
            onCustomerCallChanged.run();
    }

    public CustomerCallSummaryContextView(BaseRecyclerAdapter<CustomerCallViewModel> adapter, Context context) {
        super(adapter, context);
    }

    @Override
    public void onStart() {
        super.onStart();
        getCustomerCall();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.fragment_supervisor_tour_customer_summary_context_layout, viewGroup, false);
        cancelTextView = view.findViewById(R.id.cancel_text_view);
        cancelImageView = view.findViewById(R.id.cancel_image_view);
        sendTextView = view.findViewById(R.id.send_text_view);
        sendBtn = view.findViewById(R.id.send_btn);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        progressView = view.findViewById(R.id.progress_view);
        tabsViewPager = view.findViewById(R.id.tabs_view_pager);
        tabLayout = view.findViewById(R.id.customer_summary_tab_layout);
        tabLayout.setupWithViewPager(tabsViewPager);
        tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        tabsViewPager.setAdapter(tabsPagerAdapter);
        View v1 = createCustomTabView(getContext().getString(R.string.orders));
        TabLayout.Tab tab1 = tabLayout.getTabAt(1);
        if (tab1 != null)
            tab1.setCustomView(v1);
        View v2 = createCustomTabView(getContext().getString(R.string.returns));
        TabLayout.Tab tab2 = tabLayout.getTabAt(2);
        if (tab2 != null)
            tab2.setCustomView(v2);
        refreshButtons(false);
        tabsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                final CustomerCallTabFragment tab = ((CustomerCallTabFragment) tabsPagerAdapter.getItem(position));
                tab.refresh(customerCallViewModel);
                tab.onCallChanged = new CustomerCallTabFragment.OnCallChanged() {
                    @Override
                    public void run() {
                        refreshButtons(true);
                    }
                };
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    private void cancelCustomerCall() {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setTitle(R.string.canceling_request_in_console);
        dialog.setMessage(R.string.are_you_sure_canceling_request_in_console);
        dialog.setIcon(Icon.Warning);
        dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressView.setMessage(R.string.canceling_request_in_console);
                progressView.start();
                SupervisorApi api = new SupervisorApi(getContext());
                api.runWebRequest(api.cancelCustomerCall(customerCallViewModel.UniqueId.toString())
                        , new WebCallBack<ResponseBody>() {
                            @Override
                            protected void onFinish() {
                                progressView.finish();
                            }

                            @Override
                            protected void onSuccess(ResponseBody result, Request request) {
                                CuteMessageDialog dialog1 = new CuteMessageDialog(getContext());
                                dialog1.setIcon(Icon.Success);
                                dialog1.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        closeContextView();
                                        onCustomerCallChanged();
                                    }
                                });
                                dialog1.setTitle(R.string.request_canceled);
                                dialog1.show();
                            }

                            @Override
                            protected void onApiFailure(ApiError error, Request request) {
                                String err = WebApiErrorBody.log(error, getContext());
                                showErrorDialog(err);
                            }

                            @Override
                            protected void onNetworkFailure(Throwable t, Request request) {
                                Timber.e(t);
                                showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                            }
                        });
            }
        });
        dialog.setNegativeButton(R.string.no, null);
        dialog.show();
    }

    private void confirmCustomerCall() {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setTitle(R.string.sending_request_in_backoffice);
        dialog.setMessage(R.string.are_you_sure_you_want_to_send_request_to_backoffice);
        dialog.setIcon(Icon.Warning);
        dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressView.setMessage(R.string.canceling_request);
                progressView.start();
                SupervisorApi api = new SupervisorApi(getContext());
                api.runWebRequest(api.replicateCustomerCall(customerCallViewModel.UniqueId.toString()), new WebCallBack<ResponseBody>() {
                    @Override
                    protected void onFinish() {
                        progressView.finish();
                    }

                    @Override
                    protected void onSuccess(ResponseBody result, Request request) {
                        CuteMessageDialog dialog1 = new CuteMessageDialog(getContext());
                        dialog1.setIcon(Icon.Success);
                        dialog1.setPositiveButton(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                closeContextView();
                                onCustomerCallChanged();
                            }
                        });
                        dialog1.setTitle(R.string.request_was_sent);
                        dialog1.show();
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        showErrorDialog(R.string.error_connecting_to_server);
                    }
                });
            }
        });
        dialog.setNegativeButton(R.string.no, null);
        dialog.show();
    }

    private void saveCustomerCall() {
        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
        dialog.setTitle(R.string.saving_customer_call_changes);
        dialog.setMessage(R.string.are_you_sure_you_want_to_save_changes_of_customer_call_in_console);
        dialog.setIcon(Icon.Warning);
        dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressView.setMessage(R.string.saving_customer_call_changes);
                progressView.start();
                SupervisorApi api = new SupervisorApi(getContext());
                CustomerCallViewModel viewModel = populateCustomerCall(customerCallViewModel);
                api.runWebRequest(api.putCustomerCall(viewModel.UniqueId, viewModel.DealerUniqueId, viewModel), new WebCallBack<ResponseBody>() {
                    @Override
                    protected void onFinish() {
                        progressView.finish();
                    }

                    @Override
                    protected void onSuccess(ResponseBody result, Request request) {
                        CuteMessageDialog dialog1 = new CuteMessageDialog(getContext());
                        dialog1.setIcon(Icon.Success);
                        dialog1.setPositiveButton(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getCustomerCall();
                            }
                        });
                        dialog1.setTitle(R.string.changes_saved_in_console);
                        dialog1.show();
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        showErrorDialog(R.string.error_connecting_to_server);
                    }
                });
            }
        });
        dialog.setNegativeButton(R.string.no, null);
        dialog.show();
    }

    private CustomerCallViewModel populateCustomerCall(CustomerCallViewModel customerCallViewModel) {
        Gson gson = new Gson();
        String json = gson.toJson(customerCallViewModel);
        CustomerCallViewModel viewModel = gson.fromJson(json, CustomerCallViewModel.class);
        for (CustomerCallOrderViewModel callOrderViewModel :
                viewModel.CustomerCallOrders) {
            for (CustomerCallOrderLineViewModel line :
                    callOrderViewModel.OrderLines) {
                final Set<UUID> toRemove = new HashSet<>();
                for (CustomerCallOrderLineOrderQtyDetailViewModel qty :
                        line.CustomerCallOrderLineOrderQtyDetails) {
                    if (qty.Qty == null || qty.Qty.compareTo(BigDecimal.ZERO) == 0)
                        toRemove.add(qty.ProductUnitUniqueId);
                }
                line.CustomerCallOrderLineOrderQtyDetails = Linq.remove(line.CustomerCallOrderLineOrderQtyDetails, new Linq.Criteria<CustomerCallOrderLineOrderQtyDetailViewModel>() {
                    @Override
                    public boolean run(CustomerCallOrderLineOrderQtyDetailViewModel item) {
                        return toRemove.contains(item.ProductUnitUniqueId);
                    }
                });
            }
        }
        return viewModel;
    }

    private void getCustomerCall() {
        CustomerCallViewModel customerCallSummaryViewModel = adapter.get(getPosition());
        if (customerCallSummaryViewModel == null)
            return;

        if (progressView != null) {
            progressView.setMessage(R.string.downloading_customer_call_data);
            progressView.start();
        }

        SupervisorApi api = new SupervisorApi(getContext());
        api.runWebRequest(api.customerCalls(customerCallSummaryViewModel.UniqueId.toString(), VaranegarApplication.getInstance().getAppId())
                , new WebCallBack<CustomerCallViewModel>() {
                    @Override
                    protected void onFinish() {
                        if (progressView != null)
                            progressView.finish();
                    }

                    @Override
                    protected void onSuccess(CustomerCallViewModel result, Request request) {
                        if (result != null) {
                            customerCallViewModel = processCustomerCallViewModel(result);
                            ((CustomerCallTabFragment) tabsPagerAdapter.getItem(tabsViewPager.getCurrentItem())).refresh(customerCallViewModel);
                            refreshButtons(false);
                            refreshTab(customerCallViewModel);
                        }
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Timber.e(t);
                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                    }
                });
    }

    private void refreshTab(CustomerCallViewModel customerCallViewModel) {
        try {
            int orderCount = customerCallViewModel.CustomerCallOrders == null ? 0 : customerCallViewModel.CustomerCallOrders.size();
            updateCustomTabView(tabLayout.getTabAt(1).getCustomView(), orderCount);
            int returnCount = customerCallViewModel.CustomerCallReturns == null ? 0 : customerCallViewModel.CustomerCallReturns.size();
            updateCustomTabView(tabLayout.getTabAt(2).getCustomView(), returnCount);

        } catch (Exception ignored) {

        }
    }

    private View createCustomTabView(String string) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.supervisor_custom_tab_layout, null);
        ((TextView) v.findViewById(R.id.title_text_view)).setText(string);
        return v;
    }

    private void updateCustomTabView(@Nullable View view, int badge) {
        if (view == null)
            return;
        TextView badgeTextView = view.findViewById(R.id.badge_text_view);
        badgeTextView.setText(VasHelperMethods.convertToOrFromPersianDigits(String.valueOf(badge)));
    }

    private CustomerCallViewModel processCustomerCallViewModel(CustomerCallViewModel result) {
        for (CustomerCallOrderViewModel customerCallOrderViewModel :
                result.CustomerCallOrders) {

            for (CustomerCallOrderLineViewModel orderLineViewModel :
                    customerCallOrderViewModel.OrderLines) {
                Linq.sort(orderLineViewModel.CustomerCallOrderLineOrderQtyDetails, new Comparator<CustomerCallOrderLineOrderQtyDetailViewModel>() {
                    @Override
                    public int compare(CustomerCallOrderLineOrderQtyDetailViewModel o1, CustomerCallOrderLineOrderQtyDetailViewModel o2) {
                        return o1.ConvertFactor.compareTo(o2.ConvertFactor);
                    }
                });
                for (final CustomerCallOrderLineOrderQtyDetailViewModel unit :
                        orderLineViewModel.ProductUnits) {
                    boolean exists = Linq.exists(orderLineViewModel.CustomerCallOrderLineOrderQtyDetails, new Linq.Criteria<CustomerCallOrderLineOrderQtyDetailViewModel>() {
                        @Override
                        public boolean run(CustomerCallOrderLineOrderQtyDetailViewModel qty) {
                            return qty.ProductUnitUniqueId.equals(unit.ProductUnitUniqueId);
                        }
                    });
                    if (!exists) {
                        CustomerCallOrderLineOrderQtyDetailViewModel qty = new CustomerCallOrderLineOrderQtyDetailViewModel();
                        qty.ProductUnitUniqueId = unit.ProductUnitUniqueId;
                        qty.Qty = BigDecimal.ZERO;
                        qty.UniqueId = UUID.randomUUID();
                        qty.CustomerCallOrderLineUniqueId = orderLineViewModel.UniqueId;
                        qty.UnitName = unit.UnitName;
                        qty.ConvertFactor = unit.ConvertFactor;
                        orderLineViewModel.CustomerCallOrderLineOrderQtyDetails.add(qty);
                    }
                }
            }
        }
        return result;
    }

    private void refreshButtons(boolean edited) {
        if (customerCallViewModel != null && customerCallViewModel.isReadonly()) {
            sendBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            return;
        }
        if (edited) {
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveCustomerCall();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCustomerCall();
                }
            });
        } else {
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmCustomerCall();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelCustomerCall();
                }
            });
        }
        if (sendTextView != null)
            sendTextView.setText(edited ? R.string.send_changes : R.string.send_to_backoffice);
        if (cancelTextView != null)
            cancelTextView.setText(edited ? R.string.cancel_changes : R.string.revoke);
        cancelImageView.setImageResource(edited ? R.drawable.ic_refresh_black_36dp : R.drawable.ic_power_settings_new_red_a700_36dp);
    }

    class TabsPagerAdapter extends FragmentPagerAdapter {

        CustomerOrdersFragment customerOrdersFragment = new CustomerOrdersFragment();
        CustomerReturnsFragment customerReturnsFragment = new CustomerReturnsFragment();
        CustomerPaymentsFragment customerPaymentsFragment = new CustomerPaymentsFragment();
        CustomerCallInfoFragment customerCallInfoFragment = new CustomerCallInfoFragment();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return customerCallInfoFragment;
            else if (position == 1)
                return customerOrdersFragment;
            else
                return customerReturnsFragment;
//            else
//                return customerPaymentsFragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return getContext().getString(R.string.call);
            else if (position == 1)
                return getContext().getString(R.string.orders);
            else
                return getContext().getString(R.string.return_calls);
//            else
//                return getContext().getString(R.string.payments);

        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void showErrorDialog(@StringRes int err) {
        Context context = getContext();
        if (context != null) {
            showErrorDialog(context.getString(err));
        }
    }

    private void showErrorDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(err);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
}
