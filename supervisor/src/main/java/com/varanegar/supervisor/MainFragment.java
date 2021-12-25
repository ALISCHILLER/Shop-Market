package com.varanegar.supervisor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.supervisor.customers.CustomersFragment;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_main_layout, container, false);
        CustomViewPager viewPager = view.findViewById(R.id.main_view_pager);
        final TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(tabsPagerAdapter);
        tl = (TabLayout) view.findViewById(R.id.tabs);
        tl.setupWithViewPager(viewPager);
        tl.getTabAt(0).setIcon(R.drawable.ic_settings_applications_cyan_48dp);
        tl.getTabAt(1).setIcon(R.drawable.ic_customers_cyan_48dp);
        tl.getTabAt(2).setIcon(R.drawable.ic_status_cyan_48dp);

        tl.getTabAt(4).setIcon(R.drawable.ic_report_cyan_48dp);
        tl.getTabAt(3).setIcon(R.drawable.ic_tracking_cyan_48dp);

//        tl.getTabAt(5).setIcon(R.drawable.ic_view_list_cyan_48dp);
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
        MenuFragment menuFragment = new MenuFragment();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return settingsFragment;
            else if (position == 1)
                return customersFragment;
            else if (position == 2)
                return statusFragment;

            else if (position == 4)
                return reportsFragment;
            else if (position == 3)
                return trackingFragment;

//            else if (position == 5)
//                return menuFragment;
//            else return menuFragment;
            else  return statusFragment;

        }

        @Override
        public int getCount() {


            return 5;

        }
    }

}
