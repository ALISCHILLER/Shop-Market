package com.varanegar.supervisor.customreport.orderstatus;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.SlidingDialog;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.selectionlistadapter.BaseSelectionRecyclerAdapter;
import com.varanegar.framework.util.recycler.selectionlistadapter.SelectionRecyclerAdapter;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorFilter;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.report.ReportConfigDialog;
import com.varanegar.supervisor.utill.multispinnerfilter.KeyPairBoolData;
import com.varanegar.supervisor.utill.multispinnerfilter.MultiSpinnerListener;
import com.varanegar.supervisor.utill.multispinnerfilter.MultiSpinnerSearch;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class OrderReportConfigDialog extends SlidingDialog {
        private PairedItems fromdatePairedItems;
        private PairedItems todatePairedItems;
        private ImageView fromDateImageView;
        private ImageView toDateImageView;
        private OrderReportConfig config;
        private PairedItemsSpinner<String> report_name_paired_items;
        OnReportConfigUpdate onReportConfigUpdate;

        public interface OnReportConfigUpdate {
            void run();
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_supervisor_order_report_config, container, false);
            Context context = getContext();
            if (context != null) {
                config = new OrderReportConfig(context);
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




                /**
                 *لیست ویزیتورها
                 */
                MultiSpinnerSearch multiSelectSpinnerWithSearch = view.findViewById(R.id.multipleItemSelectionSpinner);



                // Pass true If you want searchView above the list. Otherwise false. default = true.
                multiSelectSpinnerWithSearch.setSearchEnabled(true);
                multiSelectSpinnerWithSearch.setHintText("لیست ویزیتورها");
                //A text that will display in clear text button
                multiSelectSpinnerWithSearch.setClearText("پاک کردن لیست");
                // A text that will display in search hint.
                multiSelectSpinnerWithSearch.setSearchHint("جستجو");
                // Set text that will display when search result not found...
                multiSelectSpinnerWithSearch.setEmptyTitle("Not Data Found!");
                // If you will set the limit, this button will not display automatically.
                multiSelectSpinnerWithSearch.setShowSelectAllButton(true);
                List<VisitorModel> visitorModelss = new VisitorManager(getContext()).getAll();
                final List<KeyPairBoolData> listArray1 = new ArrayList<>();
                List<String> list =new ArrayList<>();
                for (int i = 0; i < visitorModelss.size(); i++) {
                    list.clear();
                    KeyPairBoolData h = new KeyPairBoolData();
                    h.setId(visitorModelss.get(i).UniqueId);
                    h.setName(visitorModelss.get(i).Name);
                    h.setSelected(false);
                    listArray1.add(h);
                }
                /**
                 * گرفتن ویزیتورهای انتخابی برای نمایش
                 */
                // Removed second parameter, position. Its not required now..
                // If you want to pass preselected items, you can do it while making listArray,
                // Pass true in setSelected of any item that you want to preselect
                multiSelectSpinnerWithSearch.setItems(listArray1, new MultiSpinnerListener() {
                    @Override
                    public void onItemsSelected(List<KeyPairBoolData> selectedItems) {
                        //The followings are selected items.
                        for (int i = 0; i < selectedItems.size(); i++) {

                            list.add(String.valueOf(selectedItems.get(i).getId()));
                        }
                        VisitorFilter.setSaveVisitor(getContext(),list);
                    }

                    @Override
                    public void onClear() {

                    }
                });
                report_name_paired_items=view.findViewById(R.id.report_name_paired_items);
                List<String> reportlist=new ArrayList<>();
                reportlist.add(0,"گزارش وضعیت سفارش ها");
                reportlist.add(1,"گزارش برگشتی");
                report_name_paired_items.setup(getChildFragmentManager(), reportlist, new SearchBox.SearchMethod<String>() {
                    @Override
                    public boolean onSearch(String item, String text) {
                        String searchKey = VasHelperMethods.persian2Arabic(text);
                        return item.contains(searchKey);
                    }
                });


                report_name_paired_items.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int position, String item) {
                        if (position==0){
                            config.setReportitem(1);
                        }else if(position==1){
                            config.setReportitem(2);
                        }else {
                            config.setReportitem(0);
                        }
                    }
                });

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

