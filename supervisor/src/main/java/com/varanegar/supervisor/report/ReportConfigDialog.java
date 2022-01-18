package com.varanegar.supervisor.report;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
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
import com.varanegar.supervisor.VisitorFilter;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.utill.multispinnerfilter.KeyPairBoolData;
import com.varanegar.supervisor.utill.multispinnerfilter.MultiSpinnerListener;
import com.varanegar.supervisor.utill.multispinnerfilter.MultiSpinnerSearch;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.model.productGroup.ProductGroupModel;

import java.util.ArrayList;
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
    private int mproduct;
    public ReportConfigDialog(int product) {
        this.mproduct=product;
    }

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


            /**
             *لیست کالاها
             */
          MultiSpinnerSearch multiSelectproduct_group
                    = view.findViewById(R.id.multipleItem_product_group_Spinnerr);
            if (mproduct==1){
                multiSelectproduct_group.setVisibility(View.VISIBLE);
            }else {
                multiSelectproduct_group.setVisibility(View.GONE);
            }

            // Pass true If you want searchView above the list. Otherwise false. default = true.
            multiSelectproduct_group.setSearchEnabled(true);
            multiSelectproduct_group.setHintText("لیست کالاها");
            //A text that will display in clear text button
            multiSelectproduct_group.setClearText("پاک کردن لیست");

            multiSelectproduct_group.setSelectAllText("همه کالا ها");
            // A text that will display in search hint.
            multiSelectproduct_group.setSearchHint("جستجو");
            // Set text that will display when search result not found...
            multiSelectproduct_group.setEmptyTitle("Not Data Found!");
            // If you will set the limit, this button will not display automatically.
            multiSelectproduct_group.setShowSelectAllButton(true);


            ProductGroupManager ProductGroupManager = new ProductGroupManager(getContext());
            List<ProductGroupModel> catalogModels = ProductGroupManager.getParentItems(ProductType.All);
            final List<KeyPairBoolData> product_listArray = new ArrayList<>();
            List<String> product_list =new ArrayList<>();

            for (int i=0;i<catalogModels.size();i++){
                //list.add(catalogModels.get(i).ProductGroupName);
                product_list.clear();
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(catalogModels.get(i).UniqueId);
                h.setName(catalogModels.get(i).ProductGroupName);
                h.setSelected(false);
                product_listArray.add(h);
            }
            multiSelectproduct_group.setItems(product_listArray, new MultiSpinnerListener() {
                @Override
                public void onItemsSelected(List<KeyPairBoolData> selectedItems) {
                    for (int i = 0; i < selectedItems.size(); i++) {
                        product_list.add(String.valueOf(selectedItems.get(i).getId()));
                    }
                    VisitorFilter.setSave_product_group(getContext(),product_list);
                }

                @Override
                public void onClear() {

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
