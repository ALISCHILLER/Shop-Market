package com.varanegar.supervisor.status;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


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
import com.varanegar.supervisor.utill.multispinnerfilter.KeyPairBoolData;
import com.varanegar.supervisor.utill.multispinnerfilter.MultiSpinnerListener;
import com.varanegar.supervisor.utill.multispinnerfilter.MultiSpinnerSearch;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by A.Torabi on 7/2/2018.
 */

public class StatusConfigDialog extends SlidingDialog {
    private static final String TAG = "StatusConfig";
    private PairedItems fromdatePairedItems;
    private PairedItems todatePairedItems;
    private ImageView fromDateImageView;
    private ImageView toDateImageView;
    private TextView visitortext;

    OnConfigUpdated onConfigUpdated;
    private TourStatusConfig config;
    private RadioButton toursRadioBtn;
    private RadioButton requestsRadioBtn;
    private PairedItemsSpinner<VisitorModel> visitorNameSpinner;

    public interface OnConfigUpdated {
        void done();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_supervisor_status_config_layout,
                container, false);




        Context context = getContext();
        if (context != null) {

            config = new TourStatusConfig(context);

            List<VisitorModel> visitorModels = new VisitorManager(getContext()).getAll();
            VisitorModel visitorModel = new VisitorModel();
            visitorModel.Name = getString(R.string.all_visitors);
            visitorModels.add(0, visitorModel);
            visitorNameSpinner = view.findViewById(R.id.visitor_name_paired_items);
            visitorNameSpinner.setup(getChildFragmentManager(), visitorModels, new
                    SearchBox.SearchMethod<VisitorModel>() {
                @Override
                public boolean onSearch(VisitorModel item, String text) {
                    String searchKey = VasHelperMethods.persian2Arabic(text);
                    return item.Name.contains(searchKey);
                }
            });
            final VisitorModel selectedVisitor = VisitorFilter.read(getContext());
            if (selectedVisitor.UniqueId == null)
                visitorNameSpinner.selectItem(0);
            else {
                int index = Linq.findFirstIndex(visitorModels, new Linq.Criteria<VisitorModel>() {
                    @Override
                    public boolean run(VisitorModel item) {
                        return item.UniqueId != null && item.UniqueId.equals(selectedVisitor.UniqueId);
                    }
                });
                if (index >= 0)
                    visitorNameSpinner.selectItem(index);
            }
            visitorNameSpinner.setOnItemSelectedListener(new PairedItemsSpinner
                    .OnItemSelectedListener<VisitorModel>() {
                @Override
                public void onItemSelected(int position, VisitorModel item) {
                    VisitorFilter.save(getContext(), item);
                }
            });


            /**
             *لیست ویزیتورها
             */
            MultiSpinnerSearch multiSelectSpinnerWithSearch = view
                    .findViewById(R.id.multipleItemSelectionSpinner);

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
                        Log.e(TAG, i + " : " + selectedItems.get(i).getName() + " : "
                                +selectedItems.get(i).getId());
                        list.add(String.valueOf(selectedItems.get(i).getId()));
                    }
                    VisitorFilter.setSaveVisitor(getContext(),list);
                }

                @Override
                public void onClear() {
                    Log.e(TAG, "clear"+String.valueOf(multiSelectSpinnerWithSearch.getSelectedItems()));
                }
            });




            toursRadioBtn = view.findViewById(R.id.tours_radio_btn);
            requestsRadioBtn = view.findViewById(R.id.requests_radio_btn);

            toursRadioBtn.setVisibility(View.GONE);
//            if ("Tour".equals(config.getStatusType()))
//                toursRadioBtn.setChecked(true);
//            else
                requestsRadioBtn.setChecked(true);


            toursRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    createOptions(view, isChecked ? "Tour" : "Request");
                }
            });


            createOptions(view, config.getStatusType());
            fromdatePairedItems = view.findViewById(R.id.from_date_paired_items);
            todatePairedItems = view.findViewById(R.id.to_date_paired_items);
            fromDateImageView = view.findViewById(R.id.from_date_image_view);
            toDateImageView = view.findViewById(R.id.to_date_image_view);

            if (config.getFromDate() != null) {
                fromdatePairedItems.setValue(DateHelper.toString(config.getFromDate(),
                        DateFormat.Date, Locale.getDefault()));
            } else {
                Date from = new Date();
                from.setHours(0);
                from.setMinutes(0);
                from.setSeconds(0);
                fromdatePairedItems.setValue(DateHelper.toString(from, DateFormat.Date,
                        Locale.getDefault()));
            }

            if (config.getToDate() != null) {
                todatePairedItems.setValue(DateHelper.toString(config.getToDate(), DateFormat.Date,
                        Locale.getDefault()));
            } else {
                Date to = new Date();
                to.setHours(23);
                to.setMinutes(59);
                to.setSeconds(59);
                todatePairedItems.setValue(DateHelper.toString(to, DateFormat.Date, Locale.getDefault()));
            }

            fromDateImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateHelper.showDatePicker(getVaranegarActvity(), null,
                            new DateHelper.OnDateSelected() {
                        @Override
                        public void run(Calendar calendar) {
                            config.setFromDate(calendar.getTime());
                            fromdatePairedItems.setValue(DateHelper.toString(calendar,
                                    DateFormat.Date));
                        }
                    });
                }
            });

            toDateImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateHelper.showDatePicker(getVaranegarActvity(), null,
                            new DateHelper.OnDateSelected() {
                        @Override
                        public void run(Calendar calendar) {
                            config.setToDate(calendar.getTime());
                            todatePairedItems.setValue(DateHelper.toString(calendar, DateFormat.Date));
                        }
                    });
                }
            });

            /**
             * دکمه تایید کانفیگ
             */
            Button okBtn = view.findViewById(R.id.ok_button);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (requestsRadioBtn.isChecked())
//                        config.setStatusType("Request");
//                    else
//                        config.setStatusType("Tour");
                  //  if (onConfigUpdated != null)
                        onConfigUpdated.done();
                }
            });
            return view;
        } else
            return null;
    }



    private void saveRequestStatusOptions(){
        List<String> list =new ArrayList<String>();
        HashMap<OptionId, RequestStatusOption> map = config.getMapOfRequestStatusOptions();
        if (map.get(OptionId.NotVerified).value==true){
            list.add(map.get(OptionId.NotVerified).nameid);
            Log.e(TAG,"save"+map.get(OptionId.NotVerified).nameid);
        }
    }
    private void createOptions(View view, String type) {
        BaseRecyclerView statusOptionsRecyclerView = view.findViewById(R.id.status_options_recycler_view);
        if (type.equals("Tour")) {
            final List<TourStatusOption> options = config.getTourStatusOptions();

            SelectionRecyclerAdapter optionRecyclerAdapter = new
                    SelectionRecyclerAdapter<>(getVaranegarActvity(), options, true);
            List<Integer> selectedPositions = Linq.findAllIndexes(options, new Linq.Criteria<TourStatusOption>() {
                @Override
                public boolean run(TourStatusOption item) {
                    return item.value;
                }
            });
            optionRecyclerAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter
                    .OnItemSelectedListener() {
                @Override
                public void onItemSelected(int position, boolean selected) {
                    options.get(position).value = selected;
                    config.saveTourOption(options.get(position));
                }
            });
            optionRecyclerAdapter.select(selectedPositions);
            statusOptionsRecyclerView.setAdapter(optionRecyclerAdapter);





        } else {
            final List<RequestStatusOption> options = config.getRequestStatusOptions();
            SelectionRecyclerAdapter optionRecyclerAdapter = new
                    SelectionRecyclerAdapter<>(getVaranegarActvity(), options, true);
            List<Integer> selectedPositions = Linq.findAllIndexes(options, new Linq.Criteria<RequestStatusOption>() {
                @Override
                public boolean run(RequestStatusOption item) {
                    return item.value;
                }
            });
            optionRecyclerAdapter.setOnItemSelectedListener(new BaseSelectionRecyclerAdapter
                    .OnItemSelectedListener() {
                @Override
                public void onItemSelected(int position, boolean selected) {
                    options.get(position).value = selected;
                    config.saveRequestOption(options.get(position));
                    if (selected)
                    Log.e(TAG, String.valueOf(options.get(position).nameid));
                }
            });
            optionRecyclerAdapter.select(selectedPositions);
            statusOptionsRecyclerView.setAdapter(optionRecyclerAdapter);
        }

    }

}
