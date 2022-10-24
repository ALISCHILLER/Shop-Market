package com.varanegar.framework.util.component.toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.varanegar.framework.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by A.Torabi on 11/12/2017.
 */

public class CuteToolbar extends LinearLayout {
    private RecyclerView buttonsRecyclerView;
    private RecyclerView.Adapter adapter;
    private ImageView expandImageView;
    private LinearLayout linearLayoutToolbarReport;
    private LinearLayout linearLayoutToolbarProfile;
    private boolean expanded = false;
    private List<CuteButton> buttons = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private long lastClickTime = 0;

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("cute_toolbar", Context.MODE_PRIVATE);
        expanded = sharedPreferences.getBoolean("expanded", false);
    }

    public CuteToolbar(Context context) {
        super(context);
        init();
    }

    public CuteToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CuteToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setButtons(@NonNull List<CuteButton> buttons) {
        this.buttons = buttons;
        setupAdapter();
    }

    public void setButtons(CuteButton... buttons) {
        Collections.addAll(this.buttons, buttons);
        setupAdapter();
    }

    private void setupAdapter() {
        this.adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cute_button_layout, parent, false);
                return new CuteButtonViewHolder(view);
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
                final CuteButton btn = buttons.get(position);
                CuteButtonViewHolder cuteButtonViewHolder = (CuteButtonViewHolder) holder;
                cuteButtonViewHolder.setTitle(btn.title);
                cuteButtonViewHolder.setIcon(btn.icon);
                cuteButtonViewHolder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long c = new Date().getTime();
                        if (c - lastClickTime > 1000) {
                            lastClickTime = c;
                            if (btn.isEnabled()) {
                                btn.runOnClickListener();
                            }
                        }
                    }
                });
                cuteButtonViewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        long c = new Date().getTime();
                        if (c - lastClickTime > 1000) {
                            lastClickTime = c;
                            if (btn.isEnabled()) {
                                btn.runOnLongClickListener();
                            }
                        }
                        return true;
                    }
                });
                cuteButtonViewHolder.setEnabled(btn.isEnabled());
                if (expanded)
                    cuteButtonViewHolder.enlarge();
                else
                    cuteButtonViewHolder.compress();
            }

            @Override
            public int getItemCount() {
                return buttons.size();
            }
        };
        if (buttonsRecyclerView != null)
            buttonsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate(getContext(), R.layout.cute_toolbar_layout, this);
        buttonsRecyclerView = (RecyclerView) view.findViewById(R.id.toolbar_buttons_recycler_view);
        buttonsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        buttonsRecyclerView.setAdapter(adapter);
        expandImageView = (ImageView) view.findViewById(R.id.expand_image_view);
        linearLayoutToolbarReport = view.findViewById(R.id.linearLayoutToolbarReport);
        linearLayoutToolbarProfile = view.findViewById(R.id.linearLayoutToolbarProfile);
        expandImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    private void toggle() {
        if (expanded)
            collapse();
        else
            expand();
    }

    private void expand() {
        sharedPreferences.edit().putBoolean("expanded", true).apply();
        expanded = true;
        expandImageView.setImageResource(R.drawable.ic_collapse_white_24dp);
        adapter.notifyDataSetChanged();
    }

    private void collapse() {
        sharedPreferences.edit().putBoolean("expanded", false).apply();
        expanded = false;
        expandImageView.setImageResource(R.drawable.ic_expand_white_24dp);
        adapter.notifyDataSetChanged();
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    public LinearLayout getLinearLayoutToolbarReport() {
        return linearLayoutToolbarReport;
    }

    public LinearLayout getLinearLayoutToolbarProfile() {
        return linearLayoutToolbarProfile;
    }
}
