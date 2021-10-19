package com.varanegar.framework.util.component.drawer;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.varanegar.framework.base.MainVaranegarActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by atp on 1/29/2017.
 */

public class DrawerAdapter extends BaseAdapter implements List<BaseDrawerItem> {
    private ArrayList<BaseDrawerItem> items = new ArrayList<>();

    public MainVaranegarActivity getActivity() {
        return activity;
    }

    private MainVaranegarActivity activity;
    public DrawerAdapter(MainVaranegarActivity activity){
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public BaseDrawerItem getItem(int i) {
        return items == null || items.size() < i - 1 ? null : items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BaseDrawerItem item = getItem(i);
        if (item == null)
            return view;
        return item;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean contains(Object o) {
        return items.contains(o);
    }

    @NonNull
    @Override
    public Iterator<BaseDrawerItem> iterator() {
        return items.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] ts) {
        return items.toArray(ts);
    }

    @Override
    public boolean add(BaseDrawerItem baseDrawerItem) {
        boolean result = items.add(baseDrawerItem);
        if (result)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        return result;
    }

    @Override
    public boolean remove(Object o) {
        final Integer index = items.indexOf(o);
        boolean result = items.remove(o);
        if (result) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
        return result;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return items.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends BaseDrawerItem> collection) {
        final int oldSize = items.size();
        boolean result = items.addAll(collection);
        if (result) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Integer itemCount = items.size() - oldSize;
                    notifyDataSetChanged();
                }
            });
        }
        return result;
    }

    @Override
    public boolean addAll(int i, Collection<? extends BaseDrawerItem> collection) {
        boolean result = items.addAll(i, collection);
        if (result) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
        return result;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean result = items.removeAll(collection);
        if (result)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        return result;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        boolean result = items.retainAll(collection);
        if (result)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        return result;
    }

    @Override
    public void clear() {
        items.clear();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public BaseDrawerItem get(int i) {
        return items.get(i);
    }

    @Override
    public BaseDrawerItem set(int i, BaseDrawerItem t) {
        BaseDrawerItem p = items.set(i, t);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        return p;
    }

    @Override
    public void add(int i, BaseDrawerItem t) {
        items.add(i, t);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public BaseDrawerItem remove(int i) {
        BaseDrawerItem r = items.remove(i);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        return r;
    }

    @Override
    public int indexOf(Object o) {
        return items.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return items.lastIndexOf(o);
    }

    @Override
    public ListIterator<BaseDrawerItem> listIterator() {
        return items.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<BaseDrawerItem> listIterator(int i) {
        return items.listIterator(i);
    }

    @NonNull
    @Override
    public List<BaseDrawerItem> subList(int i, int i1) {
        return items.subList(i,i1);
    }

    public void setItems(ArrayList<BaseDrawerItem> items) {
        this.items = items;
    }

    public ArrayList<BaseDrawerItem> getItems() {
        return items;
    }
}
