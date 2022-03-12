package com.varanegar.supervisor.tracking;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class TrackingFragment extends IMainPageFragment {

    MapFragment mapFragment = new MapFragment();
//    TableFragment tableFragment = new TableFragment();

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_tracking_layout,
                container, false);


        final ViewPager tabsViewPager = view.findViewById(R.id.tabs_view_pager);
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());
        tabsViewPager.setAdapter(tabsPagerAdapter);
        //set floating button to FabToolbar
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackingConfigDialog dialog = new TrackingConfigDialog();
                dialog.onTrackingConfigChanged = new TrackingConfigDialog.
                        IOnTrackingConfigChanged() {
                    @Override
                    public void onChanged() {
                        TrackingConfig trackingConfig = new TrackingConfig(getContext());
//                        if (trackingConfig.isMap())
//                        {
//                            if (tabsViewPager.getCurrentItem() == 1)
//                                tabsViewPager.setCurrentItem(0);
//                            else
                        mapFragment.showMarkers();
                        //}
//                        else
//                            tabsViewPager.setCurrentItem(1);
                    }
                };
                dialog.show(getChildFragmentManager(), "TrackingConfigDialog");
            }
        });


        return view;
    }


    class TabsPagerAdapter extends FragmentPagerAdapter {


        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mapFragment;
//            if (position == 1)
//                return tableFragment;
//            else return mapFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
