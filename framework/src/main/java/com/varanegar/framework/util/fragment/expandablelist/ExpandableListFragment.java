package com.varanegar.framework.util.fragment.expandablelist;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerAdapter;
import com.varanegar.framework.util.recycler.expandablerecycler.ExpandableRecyclerView;

/**
 * Created by atp on 1/21/2017.
 */

public abstract class ExpandableListFragment<T1 extends BaseModel, T2 extends BaseModel> extends VaranegarFragment {
    private ExpandableRecyclerView mainRecyclerView;
    View view;


    protected abstract <T extends BaseModel> View onCreateContentView(T selectedItem, LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    protected abstract ExpandableRecyclerAdapter<T1, T2> createExpandableRecyclerAdapter();

    ExpandableRecyclerAdapter<T1, T2> adapter;
    LinearLayout contentLinearLayout;

    private Object selectedItem;

    public interface OnParentItemClick<T1> {
        void onClick(T1 item);
    }

    public OnParentItemClick onParentItemClick;

    public interface OnChildItemClick<T2> {
        void onClick(T2 item);
    }

    public OnChildItemClick<T2> onChildItemClick;

    <T extends BaseModel> void selectItem(LayoutInflater inflater, Bundle savedInstanceState) {
        View v = onCreateContentView((T) selectedItem, inflater, contentLinearLayout, savedInstanceState);
        contentLinearLayout.removeAllViews();
        contentLinearLayout.addView(v);
    }

    protected void createRecyclerAdapter() {
        adapter = createExpandableRecyclerAdapter();
        adapter.buffer();
    }

    public void select(int position) {
        adapter.runItemClickListener(position);
    }

    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        if (isTablet()) {
            view = inflater.inflate(R.layout.fragment_multipan_extended_expandalbe_list, container, false);
            mainRecyclerView = (ExpandableRecyclerView) view.findViewById(R.id.main_recycler_view);
            SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
            toolbar.setOnBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getVaranegarActvity().popFragment();
                }
            });
        } else {
            View drawerView = setDrawerLayout(R.layout.layout_expandable_drawer);
            view = inflater.inflate(R.layout.fragment_extended_expandalbe_list, container, false);
            mainRecyclerView = (ExpandableRecyclerView) drawerView.findViewById(R.id.main_recycler_view);
            drawerTitleTextView = (TextView) drawerView.findViewById(R.id.drawer_title_text_view);
            drawerTitleTextView.setText(title);
            SimpleToolbar toolbar = (SimpleToolbar) view.findViewById(R.id.toolbar);
            toolbar.setOnBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getVaranegarActvity().popFragment();
                }
            });
            toolbar.setOnMenuClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getVaranegarActvity().toggleDrawer();
                }
            });
        }
        createRecyclerAdapter();
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<T1>() {
            @Override
            public void run(int position) {
                if (onParentItemClick != null)
                    onParentItemClick.onClick(adapter.get(position));
                selectedItem = adapter.get(position);
                selectItem(inflater, savedInstanceState);
            }
        });
        adapter.setOnChildItemClickListener(new ExpandableRecyclerAdapter.OnChildItemClick<T2>() {
            @Override
            public void onClick(int position, T2 clickedItem) {
                if (onChildItemClick != null)
                    onChildItemClick.onClick(clickedItem);
                selectedItem = clickedItem;
                selectItem(inflater, savedInstanceState);
                getVaranegarActvity().closeDrawer();
            }
        });
        contentLinearLayout = (LinearLayout) view.findViewById(R.id.content_frame_layout);
        mainRecyclerView.setAdapter(adapter);
        View v = onCreateContentView(null, inflater, contentLinearLayout, savedInstanceState);
        contentLinearLayout.removeAllViews();
        contentLinearLayout.addView(v);
        return view;
    }

    private String title;
    private TextView drawerTitleTextView;

    public void setDrawerTitle(String title) {
        this.title = title;
        if (drawerTitleTextView != null)
            drawerTitleTextView.setText(title);
    }

    public void setDrawerTitle(@StringRes int title) {
        setDrawerTitle(getString(title));
    }
}
