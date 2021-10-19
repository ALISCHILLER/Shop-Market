package com.varanegar.supervisor.status;

import android.content.Context;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.CustomerCallViewModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.supervisor.webapi.TourCustomerSummaryViewModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/10/2018.
 */

class TourCustomerSummaryContextView extends ItemContextView<TourCustomerSummaryViewModel> {
    private TabLayout tabLayout;
    private ProgressView progressView;
    private CustomerCallViewModel customerCallViewModel;
    private ViewPager tabsViewPager;
    private TabsPagerAdapter tabsPagerAdapter;

    public TourCustomerSummaryContextView(BaseRecyclerAdapter<TourCustomerSummaryViewModel> adapter, Context context) {
        super(adapter, context);
    }

    @Override
    public void onStart() {
        super.onStart();

        TourCustomerSummaryViewModel tourCustomerSummaryViewModel = adapter.get(getPosition());
        if (tourCustomerSummaryViewModel == null)
            return;

        if (progressView != null) {
            progressView.setMessage(R.string.downloading_customer_call_data);
            progressView.start();
        }

        SupervisorApi api = new SupervisorApi(getContext());
        api.runWebRequest(api.customerCalls(tourCustomerSummaryViewModel.CustomerCallUniqueId.toString(), VaranegarApplication.getInstance().getAppId())
                , new WebCallBack<CustomerCallViewModel>() {
                    @Override
                    protected void onFinish() {
                        if (progressView != null)
                            progressView.finish();
                    }

                    @Override
                    protected void onSuccess(CustomerCallViewModel result, Request request) {
                        if (result != null) {
                                customerCallViewModel = result;
                            ((CustomerCallTabFragment) tabsPagerAdapter.getItem(tabsViewPager.getCurrentItem())).refresh(customerCallViewModel);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.fragment_supervisor_tour_customer_summary_context_layout, viewGroup, false);
        progressView = view.findViewById(R.id.progress_view);
        tabsViewPager = view.findViewById(R.id.tabs_view_pager);
        tabLayout = view.findViewById(R.id.customer_summary_tab_layout);
        tabLayout.setupWithViewPager(tabsViewPager);
        tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        tabsViewPager.setAdapter(tabsPagerAdapter);
        tabsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((CustomerCallTabFragment) tabsPagerAdapter.getItem(position)).refresh(customerCallViewModel);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
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
            else if (position == 2)
                return customerReturnsFragment;
            else
                return customerPaymentsFragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return getContext().getString(R.string.call);
            else if (position == 1)
                return getContext().getString(R.string.orders);
            else if (position == 2)
                return getContext().getString(R.string.return_calls);
            else
                return getContext().getString(R.string.payments);

        }

        @Override
        public int getCount() {
            return 4;
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
