package com.varanegar.supervisor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.supervisor.customers.CustomersFragment;
import com.varanegar.supervisor.customers.CustomersFullFragment;
import com.varanegar.supervisor.customreport.orderstatus.OrderReportFragment;
import com.varanegar.supervisor.fragment.menuTools_Fragment.MenuTools_Fragmnet;
import com.varanegar.supervisor.menu.MenuFragment;

import com.varanegar.supervisor.report.ReportsFragment;
import com.varanegar.supervisor.status.ToursStatusFragment;
import com.varanegar.supervisor.tracking.TrackingFragment;

/**
 * Created by A.Torabi on 6/23/2018.
 */

public class MainFragment extends VaranegarFragment {
    private TabLayout tl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_main_layout, container, false);
        CustomViewPager viewPager = view.findViewById(R.id.main_view_pager);
        final TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        tl = (TabLayout) view.findViewById(R.id.tabs);
        tl.setupWithViewPager(viewPager);
        tl.getTabAt(4).setIcon(R.drawable.ic_settings_applications_cyan_48dp)
                .setText(R.string.setting_su);

        tl.getTabAt(1).setIcon(R.drawable.ic_customers_cyan_48dp)
                .setText(R.string.editcustomer);

        tl.getTabAt(2).setIcon(R.drawable.ic_status_cyan_48dp)
                .setText(R.string.orrder_block);

       // tl.getTabAt(4).setIcon(R.drawable.ic_report_cyan_48dp);
        tl.getTabAt(3).setIcon(R.drawable.ic_tracking_cyan_48dp).setText(R.string.map_su);

        tl.getTabAt(0).setIcon(R.drawable.ic_view_list_cyan_48dp).setText(R.string.menu_su);
//        tl.getTabAt(6).setIcon(R.drawable.ic_baseline_format_quote_24);
//        tl.getTabAt(7).setIcon(R.drawable.ic_baseline_assignment_24);

        return view;
    }

    public void disableTab() {
        LinearLayout tabStrip = ((LinearLayout) tl.getChildAt(0));
        tabStrip.setEnabled(false);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
        }
    }

    public void enableTab() {
        LinearLayout tabStrip = ((LinearLayout) tl.getChildAt(0));
        tabStrip.setEnabled(true);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(true);
        }
    }

    class TabsPagerAdapter extends FragmentPagerAdapter {

        SettingsFragment settingsFragment = new SettingsFragment();
        CustomersFragment customersFragment = new CustomersFragment();
        ToursStatusFragment statusFragment = new ToursStatusFragment();
        ReportsFragment reportsFragment = new ReportsFragment();
        TrackingFragment trackingFragment = new TrackingFragment();
        OrderReportFragment orderReportFragment =new OrderReportFragment();
        CustomersFullFragment customersFullFragment=new CustomersFullFragment();
        MenuTools_Fragmnet menuToolsFragmnet=new MenuTools_Fragmnet();

        MenuFragment menuFragment = new MenuFragment();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 4)
                return settingsFragment;
            else if (position == 1)
                return customersFragment;
            else if (position == 2)
                return statusFragment;
            else if (position == 3)
                return trackingFragment;
           // else if (position == 4)
             //   return orderReportFragment;
            else if (position==0)
                return menuToolsFragmnet;

//                return menuFragment;
                //else return menuFragment;
            else  return statusFragment;

        }

        @Override
        public int getCount() {
            return 5;

        }
    }

}