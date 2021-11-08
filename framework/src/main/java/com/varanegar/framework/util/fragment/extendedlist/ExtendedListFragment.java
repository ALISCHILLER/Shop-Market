package com.varanegar.framework.util.fragment.extendedlist;

import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.ProgressFragment;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.toolbar.CuteButton;
import com.varanegar.framework.util.component.toolbar.CuteToolbar;
import com.varanegar.framework.util.filter.Filter;
import com.varanegar.framework.util.filter.FiltersAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;

import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 1/14/2017.
 */
public abstract class ExtendedListFragment<DataModel extends BaseModel> extends ProgressFragment {
    private String searchText;
    EditText searchEditText;
    private ImageView advancedSearchImageView;
    private TextView titleTextView;
    String title;
    private CuteToolbar buttonsToolbar;
    private FiltersAdapter filtersAdapter;
    private PairedItemsSpinner filterSpinner;

    public void setTitle(String title) {
        this.title = title;
        if (titleTextView != null)
            titleTextView.setText(title);
    }


    protected List<Filter> getFilters() {
        return filtersAdapter == null ? null : filtersAdapter.getSelectedFilters();
    }

    @Nullable
    protected abstract VaranegarFragment onCreateContentFragment(UUID selectedItem, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    protected abstract BaseRecyclerAdapter<DataModel> createRecyclerAdapter();

    protected abstract void onMenuClicked();

    protected abstract List<Filter> createFilterOptions();

    protected abstract void runFilter(String text, List<Filter> filters, Object spinnerFilterItem);

    public BaseRecyclerAdapter<DataModel> getAdapter() {
        return adapter;
    }

    protected void refresh() {
        filtersAdapter.setFilters(createFilterOptions());
        filtersAdapter.notifyDataSetChanged();
        adapter.refreshAsync();
    }

    private BaseRecyclerAdapter<DataModel> adapter;


    FrameLayout contentFrameLayout;

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        return inflater.inflate(R.layout.fragment_extended_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getVaranegarActvity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    Bundle savedInstanceState;

    protected abstract List<CuteButton> onCreateCuteToolbar();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonsToolbar = (CuteToolbar) view.findViewById(R.id.buttons_toolbar_layout);
        List<CuteButton> buttons = onCreateCuteToolbar();
        buttonsToolbar.setButtons(buttons);
        titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleTextView.setText(title);
        advancedSearchImageView = (ImageView) view.findViewById(R.id.advanced_search_image_view);
        if (advancedSearch != null) {
            advancedSearchImageView.setImageResource(advancedSearchDrawable);
            advancedSearchImageView.setVisibility(View.VISIBLE);
            advancedSearchImageView.setOnClickListener(advancedSearch);
        }
        filterSpinner = view.findViewById(R.id.filter_spinner);
        boolean setup = setupAdvancedFilter(filterSpinner);
        if (setup)
            filterSpinner.setVisibility(View.VISIBLE);
        else
            filterSpinner.setVisibility(View.GONE);
        int spinnerPosition = 0;
        try {
            spinnerPosition = VaranegarApplication.getInstance().retrieve("2efe82ac-392f-405d-ba77-ea29b6825b16", false);
        } catch (RuntimeException ignored) {

        }
        if (spinnerPosition >= 0 && spinnerPosition < filterSpinner.getItems().size())
            filterSpinner.selectItem(spinnerPosition);
        else
            VaranegarApplication.getInstance().remove("2efe82ac-392f-405d-ba77-ea29b6825b16");
        filterSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object item) {
                VaranegarApplication.getInstance().save("2efe82ac-392f-405d-ba77-ea29b6825b16", position);
                runFilter(searchText, filtersAdapter.getSelectedFilters(), item);
            }
        });
        view.findViewById(R.id.drawer_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuClicked();
            }
        });
        RecyclerView optionsRecyclerView = (RecyclerView) view.findViewById(R.id.options_recycler_view);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        filtersAdapter = new FiltersAdapter(getContext(), UUID.fromString("8575dd8c-571f-4930-8bee-a12c66e2eb69"), 0, createFilterOptions());
        filtersAdapter.setOnItemClickListener(new FiltersAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                runFilter(searchText, filtersAdapter.getSelectedFilters(), filterSpinner.getSelectedItem());
            }
        });
        optionsRecyclerView.setAdapter(filtersAdapter);
        final BaseRecyclerView mainRecyclerView = (BaseRecyclerView) view.findViewById(R.id.main_recycler_view);
        searchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchText = editable.toString();
                runFilter(searchText, filtersAdapter.getSelectedFilters(), filterSpinner.getSelectedItem());

            }
        });

        adapter = createRecyclerAdapter();
        runFilter(searchText, filtersAdapter.getSelectedFilters(), filterSpinner.getSelectedItem());
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<DataModel>() {
            @Override
            public void run(int position) {
                setOnItemClickListener(position);
            }
        });
        mainRecyclerView.setAdapter(adapter);
    }

    /**
     * جهت نمایش ییام ویزیتور به موزع و برای اطمینان از ارسال درست از صفحه لیست مشتریان به جزئیات مشتری
     * @param position
     */

    public void setOnItemClickListener(int position) {
        DataModel selectedItem = adapter.get(position);
        VaranegarFragment contentFragment = onCreateContentFragment(selectedItem.UniqueId, LayoutInflater.from(getContext()), contentFrameLayout, savedInstanceState);
        if (contentFragment != null) {

            contentFragment.addArgument("is from customer list", "true");
            contentFragment.addArgument("a129ef75-77ce-432a-8982-6bcab0bf7b51", selectedItem.UniqueId.toString());
            getVaranegarActvity().pushFragment(contentFragment);
        }
    }


    private View.OnClickListener advancedSearch;
    private int advancedSearchDrawable;

    public void setAdvancedSearch(@DrawableRes int drawable, View.OnClickListener search) {
        this.advancedSearch = search;
        this.advancedSearchDrawable = drawable;
        if (advancedSearchImageView != null) {
            advancedSearchImageView.setImageResource(drawable);
            advancedSearchImageView.setVisibility(View.VISIBLE);
            advancedSearchImageView.setOnClickListener(search);
        }
    }

    protected boolean setupAdvancedFilter(PairedItemsSpinner spinner) {
        return false;
    }


}
