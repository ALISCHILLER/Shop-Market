package com.varanegar.framework.util.recycler;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.varanegar.framework.R;
import com.varanegar.framework.util.component.SlidingDialog;

import java.util.List;

/**
 * Created by atp on 5/6/2017.
 */

public class ContextItemDialog extends SlidingDialog {
    private List<ContextMenuItemRaw> contextMenuItems;
    private ItemContextView contextView;

    private FrameLayout detailFrameLayout;
    private int itemPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_item, container, false);
        ListView optionsListView = (ListView) view.findViewById(R.id.report_item_options_list_view);
        detailFrameLayout = (FrameLayout) view.findViewById(R.id.report_item_frame_layout);
        itemPosition = getArguments().getInt("3b7f8a85-6c88-4018-bc76-1eb38cf2b12e");
        View detailView = null;
        if (contextView != null) {
            detailView = contextView.getView(detailFrameLayout, itemPosition);
            contextView.onClose = this::dismiss;
        }
        if (detailView != null) {
            detailFrameLayout.setVisibility(View.VISIBLE);
            detailFrameLayout.addView(detailView);
        }
        OptionsAdapter adapter = new OptionsAdapter();
        optionsListView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contextView != null)
            contextView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (contextView != null)
            contextView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (contextView != null)
            contextView.onStart();
    }

    public void setContextView(ItemContextView contextView) {
        this.contextView = contextView;
        if (this.contextView != null) {
            contextView.setDialog(this);
        }
    }

    public void setOnItemClickListeners(List<ContextMenuItemRaw> onItemCLickListeners) {
        this.contextMenuItems = onItemCLickListeners;
    }

    public void setPosition(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("3b7f8a85-6c88-4018-bc76-1eb38cf2b12e", position);
        setArguments(bundle);
    }

    private class OptionsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contextMenuItems == null ? 0 : contextMenuItems.size();
        }

        @Override
        public ContextMenuItemRaw getItem(int position) {
            return contextMenuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ContextMenuItemRaw item = contextMenuItems.get(position);
            return item.onCreateView(itemPosition, convertView, parent);
        }
    }
}
