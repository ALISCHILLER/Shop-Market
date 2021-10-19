package com.varanegar.framework.util.report.filter;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.PopupFragment;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.filter.date.DateFilter;
import com.varanegar.framework.util.report.filter.integer.IntFilter;
import com.varanegar.framework.util.report.filter.string.StringFilter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by atp on 1/7/2017.
 */
public class FiltersForm extends PopupFragment {

    private static final String SAVEDFILTERS = "c0645d8b-6414-4639-b899-f769999cf8c6";
    private static final String SAVEDFIELDS = "30ce5621-b72b-4721-8c5a-7c4b4bab8cdd";
    private static final String LOCALE = "ddbf8979-f5ad-44b8-a891-c0cfd960acf2";
    public ArrayList<FilterField> fields;
    public ArrayList<Filter> filters;
    FilterAdapter filtersAdapter;
    Switch filterSwitch;
    public boolean filterIsOn;
    public ReportAdapter reportAdapter;
    private Locale locale;

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters_form, null);
        filterSwitch = (Switch) view.findViewById(R.id.filter_switch);
        ListView filtersListView = (ListView) view.findViewById(R.id.filters_list_view);
        if (filters == null)
            filters = new ArrayList<>();
        filtersAdapter = new FilterAdapter(filters);
        filtersListView.setAdapter(filtersAdapter);

        filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                filterIsOn = filterSwitch.isChecked();
            }
        });
        if (filterIsOn)
            filterSwitch.setChecked(true);
        view.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterSelectionFragment filterSelectionFragment = new FilterSelectionFragment();
                filterSelectionFragment.setLocale(locale);
                filterSelectionFragment.setFields(fields);
                getVaranegarActvity().pushFragment(filterSelectionFragment);
            }
        });
        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVaranegarActvity().popFragment();
                VaranegarApplication.getInstance().save("c02af914-af8b-44a8-ba98-baa2412e4068", filters);
                VaranegarApplication.getInstance().save("f4316586-c85f-4644-8cc1-c690501496f9", filterIsOn);
            }
        });

        if (savedInstanceState != null) {
            try {
                fields = VaranegarApplication.getInstance().retrieve(SAVEDFIELDS, false);
                locale = VaranegarApplication.getInstance().retrieve(LOCALE, false);
                ArrayList<Filter> savedFilters = VaranegarApplication.getInstance().retrieve(SAVEDFILTERS, true);
                filters.addAll(savedFilters);
                filtersAdapter.notifyDataSetChanged();
            } catch (Exception ignored) {

            }
        }

        try {
            Filter filter = VaranegarApplication.getInstance().retrieve("0e34cf19-4c5b-482f-bf73-55841fc442a9", true);
            filters.add(filter);
            filtersAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        VaranegarApplication.getInstance().save(SAVEDFILTERS, filters);
        VaranegarApplication.getInstance().save(SAVEDFIELDS, fields);
        VaranegarApplication.getInstance().save(LOCALE, locale);
    }

    private class FilterAdapter extends BaseAdapter {

        ArrayList<Filter> filters;

        FilterAdapter(ArrayList<Filter> filters) {
            this.filters = filters;

        }

        @Override
        public int getCount() {
            return filters == null ? 0 : filters.size();
        }

        @Override
        public Object getItem(int i) {
            return (filters == null || filters.size() < i) ? null : filters.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row_filter_summary, viewGroup, false);
            TextView titleTextView = (TextView) v.findViewById(R.id.field_name_text_view);
            TextView filterPreviewTextView = (TextView) v.findViewById(R.id.filter_preview_text_view);
            final ImageView deleteImageView = (ImageView) v.findViewById(R.id.delete_image_view);
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filters.remove(i);
                    notifyDataSetChanged();
                }
            });
            String txt = "";
            Filter filter = filters.get(i);
            titleTextView.setText(fields.get(filter.filterIndex).title);
            if (filter instanceof StringFilter) {
                StringFilter stringFilter = (StringFilter) filter;
                txt = " " + getString(R.string.exists_in) + " : " + stringFilter.text;
            }
            if (filter instanceof IntFilter) {
                IntFilter intFilter = (IntFilter) filter;
                if (intFilter.operator == IntFilter.IntFilterOperator.Equals) {
                    txt = getString(R.string.equals) + " : " + intFilter.value;
                }
                if (intFilter.operator == IntFilter.IntFilterOperator.GreaterThan) {
                    txt = getString(R.string.greater_than) + " : " + intFilter.value;
                }
                if (intFilter.operator == IntFilter.IntFilterOperator.LessThan) {
                    txt = getString(R.string.less_than) + " : " + intFilter.value;
                }
            }
            if (filter instanceof DateFilter) {
                DateFilter dateFilter = (DateFilter) filter;
                String date = "";
//                if (language.equals("fa")) {
//                    JalaliCalendar jalaliCalendar = new JalaliCalendar();
//                    jalaliCalendar.setTime(dateFilter.value);
//                    date = DateHelper.toString(jalaliCalendar, DateFormat.Date);
//                } else
                date = DateHelper.toString(dateFilter.value, DateFormat.Date, locale);

                date = " " + date;
                if (dateFilter.operator == DateFilter.DateFilterOperator.Equals)
                    txt = getString(R.string.equals) + date;
                if (dateFilter.operator == DateFilter.DateFilterOperator.After) {
                    txt = getString(R.string.after) + date;
                }
                if (dateFilter.operator == DateFilter.DateFilterOperator.Before) {
                    txt = getString(R.string.before) + date;
                }
            }
            filterPreviewTextView.setText(txt);
            return v;
        }
    }
}
