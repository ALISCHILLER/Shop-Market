package com.varanegar.framework.util.recycler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by atp on 12/10/2016.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {
    private AppCompatActivity activity;
    private List<T> items;
    private BaseRepository repository;
    private ExecutorService pool = Executors.newFixedThreadPool(1);
    private Query query;

    public boolean isEnabled() {
        return isEnabled;
    }

    private boolean isEnabled = true;

    public AppCompatActivity getActivity() {
        return activity;
    }

    public List<T> getItems() {
        return items;
    }

    public BaseRecyclerAdapter(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        items = new ArrayList<>();
    }

    public BaseRecyclerAdapter(@NonNull AppCompatActivity activity, @NonNull List<T> items) {
        this(activity);
        this.items = items;
    }

    public BaseRecyclerAdapter(@NonNull AppCompatActivity activity, @NonNull BaseRepository repository, @NonNull Query query) {
        this(activity);
        this.repository = repository;
        this.query = query;
    }

    public void setPagingSize(int size) {
        repository.setPagingSize(size);
    }

    public void setPagingIndex(int index) {
        repository.setPagingIndex(index);
    }

    /**
     * Loads more data from the repository. It will be ignored if no repository has been set to this adapter.
     */
    public void buffer() {
        if (repository != null) {
            if (onDataCallback != null)
                onDataCallback.onStart();
            final List<T> newItems = repository.getPagedItems(query);
            if (newItems != null) {
                addAll(newItems);
            }
            if (onDataCallback != null)
                if (newItems != null)
                    onDataCallback.onFinish(newItems.size());
                else
                    onDataCallback.onFinish(0);
        }
    }

    /**
     * Runs the query on the repository and clears the old data
     *
     * @param query
     */

    public void refresh(@NonNull Query query) {
        if (repository == null)
            throw new UnsupportedOperationException("Refresh is available only when a repository is used.");
        this.query = query;
        refresh();
    }


    public void refreshAsync(@NonNull final Query query) {
        pool.execute(() -> refresh(query));
    }

    /**
     * Refreshes data
     */

    public void refresh() {
        if (repository == null)
            throw new UnsupportedOperationException("Refresh is available only when a repository is used.");
        repository.resetPaging();
        items = new ArrayList<>();
        if (repository != null)
            repository.resetPaging();
        buffer();

    }

    public void refreshAsync() {
        if (repository == null)
            throw new UnsupportedOperationException("Refresh is available only when a repository is used.");
        pool.execute(() -> {
            items = new ArrayList<>();
            if (repository != null)
                repository.resetPaging();
            buffer();
        });
    }

    public void setDataCallback(OnDataCallback onDataCallback) {
        this.onDataCallback = onDataCallback;
    }

    private OnDataCallback onDataCallback;

    public void setEnabled(boolean b) {
        isEnabled = b;
    }

    public void sort(Comparator<T> comparator) {
        if (onDataCallback != null)
            onDataCallback.onStart();
        Collections.sort(items, comparator);
        if (onDataCallback != null)
            onDataCallback.onFinish(0);
    }

    public void reverse() {
        if (onDataCallback != null)
            onDataCallback.onStart();
        Collections.reverse(items);
        if (onDataCallback != null)
            onDataCallback.onFinish(0);
    }

    public interface OnDataCallback {
        void onStart();

        /*
        Number of new items that was added to the adapter
         */
        void onFinish(int numberOfItems);
    }

    private List<ContextMenuItemRaw> contextMenuItems = new ArrayList<>();

    public void addContextMenuItem(ContextMenuItemRaw item) {
        contextMenuItems.add(item);
    }

    @Nullable
    protected ItemContextView onCreateContextView() {
        return null;
    }

    /**
     * call this method from view holder that you provide for this adapter
     *
     * @param position
     */
    public void showContextMenu(final int position) {
        if (isEnabled) {
            ItemContextView itemContextView = onCreateContextView();
            final List<ContextMenuItemRaw> availableItems;
            availableItems = Linq.findAll(contextMenuItems, item -> item.isAvailable(position));

            if (availableItems.size() == 0 && itemContextView == null)
                return;
            if (availableItems.size() == 1 && itemContextView == null) {
                ContextMenuItemRaw item = availableItems.get(0);
                if (item instanceof ContextMenuItem)
                    ((ContextMenuItem) item).run(position);
            } else {
                ContextItemDialog contextItemDialog = new ContextItemDialog();
                contextItemDialog.setPosition(position);
                for (ContextMenuItemRaw item :
                        availableItems) {
                        item.setContextItemDialog(contextItemDialog);
                }
                contextItemDialog.setOnItemClickListeners(availableItems);
                if (itemContextView != null)
                    contextItemDialog.setContextView(itemContextView);
                contextItemDialog.show(activity.getSupportFragmentManager(), "QLKIGULIUFG");

            }
        }
    }


    public interface OnItemClick<T> {
        void run(int position);
    }

    private OnItemClick<T> onItemClick;

    public void setOnItemClickListener(OnItemClick<T> onItemClick) {
        this.onItemClick = onItemClick;
    }

    /**
     * You should call this method from the provided view holder to run action on selected Item
     *
     * @param position
     */
    public void runItemClickListener(int position) {
        if (isEnabled && onItemClick != null)
            onItemClick.run(position);
    }

    private void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseViewHolder baseHolder = (BaseViewHolder) holder;
        onBindViewHolder(baseHolder, position);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public int size() {
        return getItemCount();
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public boolean contains(T o) {
        return items != null && items.contains(o);
    }

    @NonNull
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @NonNull
    public Object[] toArray() {
        return items.toArray();
    }

    @NonNull
    public T[] toArray(@NonNull T[] t1s) {
        return items.toArray(t1s);
    }

    public synchronized boolean add(T t) {
        if (items == null)
            items = new ArrayList<>();
        boolean result = items.add(t);
        if (result)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    notifyItemInserted(items.size() - 1);
                    notifyDataSetChanged();
                }
            });
//            if (recyclerView != null)
//                recyclerView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyItemInserted(items.size() - 1);
//                    }
//                });


        return result;
    }

    public synchronized boolean remove(Object o) {
        if (items == null)
            items = new ArrayList<>();
        final Integer index = items.indexOf(o);
        boolean result = items.remove(o);
        if (result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    notifyItemRemoved(index);
                    notifyDataSetChanged();
                }
            });
//            if (recyclerView != null)
//                recyclerView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyItemRemoved(index);
//                    }
//                });
        }
        return result;
    }

    public boolean containsAll(@NonNull Collection<?> collection) {
        if (items == null)
            return false;
        return items.containsAll(collection);
    }

//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        this.recyclerView = recyclerView;
//    }

    public void setItems(List<T> items) {
        this.items = items;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public synchronized boolean addAll(@NonNull Collection<? extends T> collection) {
        if (onDataCallback != null)
            onDataCallback.onStart();
        if (items == null)
            items = new ArrayList<>();
        final int oldSize = items.size();
        boolean result = items.addAll(collection);
        if (items.size() == 0 || oldSize == 0)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        else if (result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
        if (onDataCallback != null)
            onDataCallback.onFinish(collection.size());
        return result;
    }

    public synchronized boolean addAll(int i, @NonNull Collection<? extends T> collection) {
        if (onDataCallback != null)
            onDataCallback.onStart();
        if (items == null)
            items = new ArrayList<>();
        final Integer oldSize = items.size();
        final Integer index = i;
        boolean result = items.addAll(i, collection);
        if (items.size() == 0 || oldSize == 0)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        else if (result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        }
        if (onDataCallback != null)
            onDataCallback.onFinish(collection.size());
        return result;
    }

    public synchronized boolean removeAll(@NonNull Collection<?> collection) {
        if (items == null)
            items = new ArrayList<>();
        boolean result = items.removeAll(collection);
        if (result)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        return result;
    }

    public synchronized boolean retainAll(@NonNull Collection<?> collection) {
        if (items == null)
            items = new ArrayList<>();
        boolean result = items.retainAll(collection);
        if (result)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        return result;
    }

    public synchronized void clear() {
        items = new ArrayList<>();
        if (repository != null)
            repository.resetPaging();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }


    @Nullable
    public T get(int i) {
        if (items == null || items.size() == 0)
            return null;
        return items.get(i);
    }

    public synchronized T set(int i, T t) {
        if (items == null)
            items = new ArrayList<>();
        T p = items.set(i, t);
        final int index = i;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                notifyItemChanged(index);
                notifyDataSetChanged();
            }
        });
//        if (recyclerView != null)
//            recyclerView.post(new Runnable() {
//                @Override
//                public void run() {
//                    notifyItemChanged(index);
//                }
//            });
        return p;
    }

    public synchronized void add(int i, T t) {
        if (items == null)
            items = new ArrayList<>();
        items.add(i, t);
        final int index = i;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                notifyItemInserted(index);
                notifyDataSetChanged();
            }
        });
//        if (recyclerView != null)
//            recyclerView.post(new Runnable() {
//                @Override
//                public void run() {
//                    notifyItemInserted(index);
//                }
//            });
    }

    public synchronized T remove(int i) {
        if (items == null)
            items = new ArrayList<>();
        T r = items.remove(i);
        final int index = i;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (items.size() == 0)
//                    notifyDataSetChanged();
//                else
//                    notifyItemRemoved(index);
                notifyDataSetChanged();
            }
        });
//        if (recyclerView != null)
//            recyclerView.post(new Runnable() {
//                @Override
//                public void run() {
//                    notifyItemRemoved(index);
//                }
//            });
        return r;
    }

    public int indexOf(Object o) {
        if (items == null)
            items = new ArrayList<>();
        return items.indexOf(o);
    }


    public int lastIndexOf(Object o) {
        if (items == null)
            items = new ArrayList<>();
        return items.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        if (items == null)
            items = new ArrayList<>();
        return items.listIterator();
    }

    @NonNull
    public ListIterator<T> listIterator(int i) {
        if (items == null)
            items = new ArrayList<>();
        return items.listIterator(i);
    }

    @NonNull
    public List<T> subList(int i, int i1) {
        if (items == null)
            items = new ArrayList<>();
        return items.subList(i, i1);
    }

}
