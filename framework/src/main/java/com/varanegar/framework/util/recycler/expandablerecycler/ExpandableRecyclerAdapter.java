package com.varanegar.framework.util.recycler.expandablerecycler;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.varanegar.framework.R;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by atp on 1/21/2017.
 */

public abstract class ExpandableRecyclerAdapter<T1 extends BaseModel, T2 extends BaseModel> extends BaseRecyclerAdapter<T1> {
    private HashMap<Integer, ChildRecyclerAdapter<T2>> adapters = new HashMap<>();
    private ArrayList<Integer> expandedItems = new ArrayList<>();
    private int headerBackgroundColor;
    private Drawable expandIcon;
    private Drawable collapseIcon;


    public void setHeaderBackgroundColor(int headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
    }

    public void setExpandIcon(Drawable expandIcon) {
        this.expandIcon = expandIcon;
    }

    public void setCollapseIcon(Drawable collapseIcon) {
        this.collapseIcon = collapseIcon;
    }

    public void deselect() {
        selectedItem = null;
        notifyDataSetChanged();
        for (ChildRecyclerAdapter childRecyclerAdapter :
                adapters.values()) {
            childRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public interface ChildQuery<T1> {
        Query onCreate(T1 parentItem);
    }

    private ChildQuery childQuery;

    public interface Children<T1, T2> {
        List<T2> onCreate(T1 parentItem);
    }

    private Children children;
    private Class<? extends BaseRepository<T2>> childRepositoryClass;

    public abstract BaseViewHolder<T1> onCreateParent(ViewGroup parent);

    public abstract BaseViewHolder<T2> onCreateChild(ViewGroup parent, ChildRecyclerAdapter<T2> adapter);

    public ExpandableRecyclerAdapter(AppCompatActivity activity, BaseRepository<T1> repository, Query query, Class<? extends BaseRepository<T2>> childRepositoryClass, ChildQuery<T1> childQuery) {
        super(activity, repository, query);
        this.childRepositoryClass = childRepositoryClass;
        this.childQuery = childQuery;
    }

    public ExpandableRecyclerAdapter(AppCompatActivity activity, BaseRepository<T1> repository, Query query, Children<T1, T2> children) {
        super(activity, repository, query);
        this.children = children;
    }

    public ExpandableRecyclerAdapter(AppCompatActivity activity, List<T1> items, Class<? extends BaseRepository<T2>> childRepositoryClass, ChildQuery<T1> childQuery) {
        super(activity, items);
        this.childRepositoryClass = childRepositoryClass;
        this.childQuery = childQuery;
    }

    public ExpandableRecyclerAdapter(AppCompatActivity activity, List<T1> items, Children<T1, T2> children) {
        super(activity, items);
        this.children = children;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View headerView = inflater.inflate(com.varanegar.framework.R.layout.layout_expandable_header_row, parent, false);
        FrameLayout headerContentFrame = (FrameLayout) headerView.findViewById(R.id.header_content_frame);
        BaseViewHolder<T1> customHeaderViewHolder = onCreateParent(parent);
        headerContentFrame.addView(customHeaderViewHolder.getItemView());
        return new HeaderViewHolder(headerView, customHeaderViewHolder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
        viewHolder.customHeaderViewHolder.bindView(position);
        viewHolder.headerLinearLayout.setBackgroundColor(headerBackgroundColor);
        if (expandedItems.contains(position)) {
            createAdapter(position);
            viewHolder.itemsRecyclerView.setAdapter(adapters.get(position));
            viewHolder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            viewHolder.itemsRecyclerView.setVisibility(View.VISIBLE);
            viewHolder.expandImageView.setImageDrawable(collapseIcon);
        } else {
            viewHolder.itemsRecyclerView.setVisibility(View.GONE);
            viewHolder.expandImageView.setImageDrawable(expandIcon);
        }

        viewHolder.expandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandedItems.contains(position)) {
                    expandedItems.remove((Integer) position);
                    final TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -viewHolder.itemsRecyclerView.getHeight());
                    animate.setDuration(500);
                    animate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            viewHolder.itemsRecyclerView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    viewHolder.itemsRecyclerView.startAnimation(animate);
                    viewHolder.expandImageView.setImageDrawable(expandIcon);
                } else {
                    createAdapter(position);
                    viewHolder.itemsRecyclerView.setAdapter(adapters.get(position));
                    viewHolder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    expandedItems.add(position);
                    float start = Math.min(-viewHolder.itemsRecyclerView.getHeight(), -200);
                    final TranslateAnimation animate = new TranslateAnimation(0, 0, start, 0);
                    animate.setDuration(500);
                    animate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            viewHolder.itemsRecyclerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    viewHolder.itemsRecyclerView.startAnimation(animate);
                    viewHolder.expandImageView.setImageDrawable(collapseIcon);
                }
            }
        });
    }

    private void createAdapter(final int position) {
        if (adapters.get(position) == null) {
            if (childRepositoryClass != null) {
                try {
                    ChildRecyclerAdapter<T2> adapter = new ChildRecyclerAdapter<T2>(getActivity(), this, position, childRepositoryClass.newInstance(), childQuery.onCreate(get(position))) {
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            return onCreateChild(parent, this);
                        }
                    };
                    adapter.buffer();
                    adapters.put(position, adapter);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            } else {
                ChildRecyclerAdapter<T2> adapter = new ChildRecyclerAdapter<T2>(getActivity(), this, position, children.onCreate(get(position))) {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
                        return onCreateChild(parent, this);
                    }
                };
                adapters.put(position, adapter);
            }
            final ChildRecyclerAdapter<T2> adapter = adapters.get(position);
            adapter.setOnItemClickListener(new OnItemClick<T2>() {
                @Override
                public void run(int position2) {
                    selectedItem = new SelectedItem(position, position2);
                    notifyDataSetChanged();
                    for (ChildRecyclerAdapter ada :
                            adapters.values()) {
                        ada.notifyDataSetChanged();
                    }
                    if (onChildItemClick != null)
                        onChildItemClick.onClick(position2 , adapter.get(position2));
                }
            });
        }

    }

    @Override
    public void setOnItemClickListener(final OnItemClick<T1> onItemClick) {
        super.setOnItemClickListener(new OnItemClick<T1>() {
            @Override
            public void run(int position) {
                onItemClick.run(position);
                selectedItem = new SelectedItem(position,-1);
                notifyDataSetChanged();
            }
        });
    }

    public void setOnChildItemClickListener(@NonNull OnChildItemClick<T2> onChildItemClick) {
        this.onChildItemClick = onChildItemClick;
    }

    private OnChildItemClick<T2> onChildItemClick;

    public interface OnChildItemClick<T2> {
        void onClick(int position, T2 clickedItem);
    }

    public int getSelectedPosition(){
        return selectedItem != null ? selectedItem.adapter : -1 ;
    }
    SelectedItem selectedItem;

    class SelectedItem {
        public SelectedItem(int adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        int adapter;
        int position;
    }
}
