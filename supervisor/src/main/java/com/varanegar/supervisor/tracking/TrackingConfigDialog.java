package com.varanegar.supervisor.tracking;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.SlidingDialog;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/17/2018.
 */

public class TrackingConfigDialog extends SlidingDialog {

    private ViewPager tabsViewPager;
    private TabLayout t;
    private TabLayout trackingTypeTab;
    private SelectionRecyclerAdapter<VisitorModel> visitorsAdapter;
    private CheckBox allUsersCheckBox;

    public interface IOnTrackingConfigChanged {
        void onChanged();
    }

    public IOnTrackingConfigChanged onTrackingConfigChanged;

    @Override
    public void onStart() {
        super.onStart();
        tabsViewPager.setCurrentItem(new TrackingConfig(getContext()).isTracking() ? 0 : 1);
        if (visitorsAdapter != null) {
            final TrackingConfig trackingConfig = new TrackingConfig(getContext());
            List<UUID> personnelIds = trackingConfig.getPersonnelIds();
            for (int i = 0; i < visitorsAdapter.getItemCount(); i++) {
                final VisitorModel visitor = visitorsAdapter.get(i);
                boolean isSelected = Linq.exists(personnelIds, new Linq.Criteria<UUID>() {
                    @Override
                    public boolean run(UUID id) {
                        return id.equals(visitor.UniqueId);
                    }
                });
                if (isSelected)
                    visitorsAdapter.select(i);
            }
            if (visitorsAdapter.getSelectedItems().size() == visitorsAdapter.getItemCount())
                allUsersCheckBox.setChecked(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tracking_config_layout, container, false);

        view.findViewById(R.id.ok_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = t.getSelectedTabPosition();
                TrackingConfig trackingConfig = new TrackingConfig(getContext());
                trackingConfig.isMap(p == 0);
                trackingConfig.isTracking(trackingTypeTab.getSelectedTabPosition() == 0);
                if (allUsersCheckBox.isChecked())
                    trackingConfig.setPersonnelIds(visitorsAdapter.getItems());
                else {
                    if (visitorsAdapter.getSelectedItems().size() == 0)
                        trackingConfig.removePersonnelIds();
                    else
                        trackingConfig.setPersonnelIds(visitorsAdapter.getSelectedItems());
                }
                if (visitorsAdapter.getSelectedItems().size() == 0 && !allUsersCheckBox.isChecked()) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setTitle(R.string.error);
                    dialog.setTitle(R.string.please_select_a_visitor);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                    return;
                }
                dismiss();
                if (onTrackingConfigChanged != null)
                    onTrackingConfigChanged.onChanged();
            }
        });
        BaseRecyclerView usersRecyclerView = view.findViewById(R.id.users_recycler_view);
        VisitorManager visitorManager = new VisitorManager(getContext());
        final List<VisitorModel> visitors = visitorManager.getAll();
        visitorsAdapter = new SelectionRecyclerAdapter<>(getVaranegarActvity(), visitors, true);
        usersRecyclerView.setAdapter(visitorsAdapter);
        allUsersCheckBox = view.findViewById(R.id.all_users_check_box);
        allUsersCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TrackingConfig trackingConfig = new TrackingConfig(getContext());
                if (b) {
                    visitorsAdapter.selectAll();
                    trackingConfig.selectAllPersonnels();
                } else {
                    visitorsAdapter.deselectAll();
                    trackingConfig.removePersonnelIds();
                }
            }
        });

        tabsViewPager = (ViewPager) view.findViewById(R.id.tabs_view_pager);
        tabsViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
//                if (position == 0)
//                    return new VisitorsPathConfigTabFragment();
//                else
                return new LastStatusConfigTabFragment();
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
        trackingTypeTab = view.findViewById(R.id.tracking_type_tabs);
        trackingTypeTab.setupWithViewPager(tabsViewPager);
        //   trackingTypeTab.getTabAt(0).setIcon(R.drawable.ic_tracking_cyan_24dp);
        trackingTypeTab.getTabAt(0).setIcon(R.drawable.ic_location_on_cyan_24dp);

        t = view.findViewById(R.id.view_type_tab_layout);
//        TrackingConfig trackingConfig = new TrackingConfig(getContext());
//        if (trackingConfig.isMap())
//            t.getTabAt(0).select();
//        else
        t.getTabAt(0).select();
        return view;
    }

}
