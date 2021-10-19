package com.varanegar.supervisor.report;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.SlidingDialog;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/24/2018.
 */

public class ReportConfigDialog extends SlidingDialog {
    private PairedItems fromdatePairedItems;
    private PairedItems todatePairedItems;
    private ImageView fromDateImageView;
    private ImageView toDateImageView;
    private ReportConfig config;

    OnReportConfigUpdate onReportConfigUpdate;

    public interface OnReportConfigUpdate {
        void run();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_reports_config_layout, container, false);
        Context context = getContext();
        if (context != null) {
            config = new ReportConfig(context);
            BaseRecyclerView usersRecyclerView = view.findViewById(R.id.users_recycler_view);
            VisitorManager visitorManager = new VisitorManager(getContext());
            List<VisitorModel> visitors = visitorManager.getAll();
            final SelectionRecyclerAdapter<VisitorModel> visitorsAdapter = new SelectionRecyclerAdapter<>(getVaranegarActvity(), visitors, false);
            usersRecyclerView.setAdapter(visitorsAdapter);
            //CheckBox allUsersCheckBox = view.findViewById(R.id.all_users_check_box);

//        if (config.isAllVisitorsSelected()) {
//            allUsersCheckBox.setChecked(true);
//            visitorsAdapter.selectAll();
//        } else {
//            allUsersCheckBox.setChecked(false);
//            for (VisitorModel user :
//                    visitorsAdapter.getItems()) {
//                if (config.getSelectedVisitors().contains(user.UniqueId))
//                    visitorsAdapter.select(user);
//            }
//        }
            visitorsAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int position, boolean selected) {
                    if (selected)
                        config.selectVisitor(visitorsAdapter.get(position).UniqueId);
//                else
//                    config.deselectVisitor(visitorsAdapter.get(position).UniqueId);
                }
            });
//        allUsersCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    visitorsAdapter.selectAll();
//                    config.selectAllVisitors();
//                } else {
//                    visitorsAdapter.deselectAll();
//                    config.deSelectAllVisitors();
//                }
//            }
//        });

            final String visitorId = config.getSelectedVisitorId();
            if (visitorId != null) {
                visitorsAdapter.select(new Linq.Criteria<VisitorModel>() {
                    @Override
                    public boolean run(VisitorModel item) {
                        return UUID.fromString(visitorId).equals(item.UniqueId);
                    }
                });
            }

            fromdatePairedItems = view.findViewById(R.id.from_date_paired_items);
            todatePairedItems = view.findViewById(R.id.to_date_paired_items);
            fromDateImageView = view.findViewById(R.id.from_date_image_view);
            toDateImageView = view.findViewById(R.id.to_date_image_view);

            if (config.getFromDate() != null) {
                fromdatePairedItems.setValue(DateHelper.toString(config.getFromDate(), DateFormat.Date, Locale.getDefault()));
            }

            if (config.getToDate() != null) {
                todatePairedItems.setValue(DateHelper.toString(config.getToDate(), DateFormat.Date, Locale.getDefault()));
            }

            fromDateImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateHelper.showDatePicker(getVaranegarActvity(), null, new DateHelper.OnDateSelected() {
                        @Override
                        public void run(Calendar calendar) {
                            config.setFromDate(calendar.getTime());
                            fromdatePairedItems.setValue(DateHelper.toString(calendar, DateFormat.Date));
                        }
                    });
                }
            });


            toDateImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateHelper.showDatePicker(getVaranegarActvity(), null, new DateHelper.OnDateSelected() {
                        @Override
                        public void run(Calendar calendar) {
                            config.setToDate(calendar.getTime());
                            todatePairedItems.setValue(DateHelper.toString(calendar, DateFormat.Date));
                        }
                    });
                }
            });
            view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onReportConfigUpdate != null)
                        onReportConfigUpdate.run();
                }
            });
            return view;
        } else
            return null;

    }
}
