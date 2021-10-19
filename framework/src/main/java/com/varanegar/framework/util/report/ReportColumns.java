package com.varanegar.framework.util.report;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by atp on 12/26/2016.
 */
public class ReportColumns implements List<ReportColumn> {
    List<ReportColumn> columns = new ArrayList<>();


    @Override
    public int size() {
        return columns.size();
    }

    @Override
    public boolean isEmpty() {
        return columns.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return columns.contains(o);
    }

    @NonNull
    @Override
    public Iterator<ReportColumn> iterator() {
        return columns.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return columns.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] ts) {
        return columns.toArray(ts);
    }

    @Override
    public boolean add(ReportColumn reportColumn) {
        return columns.add(reportColumn);
    }

    @Override
    public boolean remove(Object o) {
        return columns.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return columns.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends ReportColumn> collection) {
        return columns.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends ReportColumn> collection) {
        return columns.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return columns.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return columns.retainAll(collection);
    }

    @Override
    public void clear() {
        columns.clear();
    }

    @Override
    public ReportColumn get(int i) {
        return columns.get(i);
    }

    @Override
    public ReportColumn set(int i, ReportColumn reportColumn) {
        return columns.set(i, reportColumn);
    }

    @Override
    public void add(int i, ReportColumn reportColumn) {
        columns.add(i, reportColumn);
    }

    @Override
    public ReportColumn remove(int i) {
        return columns.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return columns.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return columns.lastIndexOf(o);
    }

    @Override
    public ListIterator<ReportColumn> listIterator() {
        return columns.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<ReportColumn> listIterator(int i) {
        return columns.listIterator(i);
    }

    @NonNull
    @Override
    public List<ReportColumn> subList(int i, int i1) {
        return columns.subList(i, i1);
    }
}
