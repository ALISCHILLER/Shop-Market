package com.varanegar.framework.util.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.R;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;


/**
 * Created by atp on 12/19/2016.
 */
public class BaseRecyclerView extends LinearLayout {

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    RecyclerView recyclerView;
    protected TextView messageTextView;
    protected RecyclerView.Adapter adapter;
    protected LinearLayoutManager layoutManager;
    protected RecyclerView.ItemDecoration itemDecoration;
    protected int lastLastVisibleItem;


    public BaseRecyclerView(Context context) {
        super(context);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected View inflate() {
        return inflate(getContext(), R.layout.base_recycler_view_layout, this);
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        recyclerView.addOnScrollListener(listener);
    }

    public int computeVerticalScrollOffset() {
        return recyclerView.computeVerticalScrollOffset();
    }

    public void scrollToPosition(int position) {
        layoutManager.scrollToPosition(position);
    }

    public int findFirstVisibleItemPosition() {
        return layoutManager.findFirstVisibleItemPosition();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate();

        messageTextView = (TextView) view.findViewById(R.id.message_text_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.base_recycler_view);

        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
        } else
            recyclerView.setLayoutManager(layoutManager);
        if (itemDecoration != null)
            recyclerView.addItemDecoration(itemDecoration);
        if (adapter != null)
            recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(bufferListener);
    }

    RecyclerView.OnScrollListener bufferListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == SCROLL_STATE_DRAGGING) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager lManager = (LinearLayoutManager) layoutManager;
                    int totalItemCount = lManager.getItemCount();
                    int lastVisibleItem = lManager.findLastVisibleItemPosition();
                    int firstVisibleItem = lManager.findFirstVisibleItemPosition();
                    int visibleItems = lastVisibleItem - firstVisibleItem + 1;
                    int a = lastVisibleItem / visibleItems + 1;
                    int b = totalItemCount / visibleItems;
                    if (a >= b && lastLastVisibleItem < lastVisibleItem) {
                        buffer();
                    }
                    lastLastVisibleItem = lastVisibleItem;
                }
            }
        }
    };

    protected void buffer() {
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ((BaseRecyclerAdapter) adapter).buffer();
                }
            });
            thread.start();
        }
    }

    public void setAdapter(final RecyclerView.Adapter adapter) {
        if (adapter != null) {
            this.adapter = adapter;
            if (adapter instanceof BaseRecyclerAdapter) {
                BaseRecyclerAdapter baseRecyclerAdapter = (BaseRecyclerAdapter) adapter;
                baseRecyclerAdapter.setDataCallback(new BaseRecyclerAdapter.OnDataCallback() {
                    @Override
                    public void onStart() {
                        ((BaseRecyclerAdapter) adapter).getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageTextView.setVisibility(VISIBLE);
                                messageTextView.setText(getContext().getString(R.string.loading_data_items));
                            }
                        });
                    }

                    @Override
                    public void onFinish(final int numberOfItems) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ignored) {
                                } finally {
                                    ((BaseRecyclerAdapter) adapter).getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            messageTextView.setText("");
                                            messageTextView.setVisibility(GONE);
                                        }
                                    });
                                }
                            }
                        });
                        thread.start();
                    }
                });
            }
            if (recyclerView != null) {
                recyclerView.setAdapter(adapter);
            }
        }
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        this.itemDecoration = itemDecoration;
        if (recyclerView != null) {
            recyclerView.addItemDecoration(itemDecoration);
        }
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void stopScroll() {
        recyclerView.stopScroll();
    }

    public void removeOnScrollListener(RecyclerView.OnScrollListener scrollListener) {
        recyclerView.removeOnScrollListener(scrollListener);
    }

    public void scrollBy(int dx, int dy) {
        recyclerView.scrollBy(dx, dy);
    }
}
