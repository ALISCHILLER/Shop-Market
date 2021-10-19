package com.varanegar.framework.util;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.java.util.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by atp on 3/27/2017.
 */

public class Linq {


    public interface Consumer<T> {
        void run(T item);
    }

    public interface Criteria<T> {
        boolean run(T item);
    }

    public interface Comparator<T> {
        boolean compare(T item1, T item2);
    }

    public interface Map<T1, T2> {
        T2 run(T1 item);
    }

    public interface Merge<T> {
        T run(T item1, T item2);
    }

    @NonNull
    public static <T> List<T> findAll(@NonNull Collection<T> items, @NonNull Criteria<T> criteria) {
        List<T> selectedItems = new ArrayList<>();
        for (T it :
                items) {
            if (criteria.run(it)) {
                selectedItems.add(it);
            }
        }
        return selectedItems;
    }

    @NonNull
    public static <T> List<Integer> findAllIndexes(@NonNull List<T> items, @NonNull Criteria<T> criteria) {
        List<Integer> selectedIPositions = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (criteria.run(items.get(i))) {
                selectedIPositions.add(i);
            }
        }
        return selectedIPositions;
    }


    @Nullable
    public static <T> T findFirst(@NonNull Collection<T> items, @NonNull Criteria<T> criteria) {
        for (T it :
                items) {
            if (criteria.run(it)) {
                return it;
            }
        }
        return null;
    }

    @Nullable
    public static <T> T findFirst(@NonNull T[] items, @NonNull Criteria<T> criteria) {
        return findFirst(Arrays.asList(items), criteria);
    }

    /*
    Returns -1 if item does not exist
     */
    @Nullable
    public static <T> T findLast(@NonNull List<T> items, @NonNull Criteria<T> criteria) {
        for (int i = items.size() - 1; i > 0; i--) {
            T it = items.get(i);
            if (criteria.run(it)) {
                return it;
            }
        }
        return null;
    }

    /*
    Returns -1 if item does not exist
     */
    public static <T> int findFirstIndex(@NonNull Collection<T> items, @NonNull Criteria<T> criteria) {
        int i = 0;
        for (T item :
                items) {
            if (criteria.run(item))
                return i;
            i++;
        }
        return -1;
    }

    public static <T> int findLastIndex(@NonNull Collection<T> items, @NonNull Criteria<T> criteria) {
        int i = items.size() - 1;
        for (T item :
                items) {
            if (criteria.run(item))
                return i;
            i--;
        }
        return -1;
    }

    public static <T> List<T> remove(@NonNull Collection<T> items, @NonNull Criteria<T> criteria) {
        List<T> result = new ArrayList<>();
        for (T it :
                items) {
            if (!criteria.run(it)) {
                result.add(it);
            }
        }
        return result;
    }

    public static <T> int removeFirst(@NonNull List<T> items, @NonNull Criteria<T> criteria) {
        for (int i = 0; i < items.size(); i++) {
            T it = items.get(i);
            if (criteria.run(it)) {
                items.remove(it);
                return i;
            }
        }
        return -1;
    }

    public static <T> boolean exists(@NonNull Collection<T> items, @NonNull Criteria<T> criteria) {
        T result = findFirst(items, criteria);
        return result != null;
    }

    public static <T> boolean all(@NonNull Collection<T> items, @NonNull Criteria<T> criteria) {
        List<T> result = findAll(items, criteria);
        return result.size() == items.size();
    }

    @NonNull
    public static <T1, T2> List<T2> map(@NonNull Collection<T1> items, @NonNull Map<T1, T2> map) {
        ArrayList<T2> output = new ArrayList<>(items.size());
        for (T1 it :
                items) {
            T2 result = map.run(it);
            output.add(result);
        }
        return output;
    }

    @NonNull
    public static <T1, T2> List<T2> map(@NonNull T1[] items, @NonNull Map<T1, T2> map) {
        return map(Arrays.asList(items), map);
    }

    @Nullable
    public static <T> T merge(@NonNull List<T> items, @NonNull Merge<T> merge) {
        if (items.size() == 0)
            return null;
        T output = items.get(0);
        for (int i = 1; i < items.size(); i++) {
            T it = items.get(i);
            output = merge.run(output, it);
        }
        return output;
    }

    public interface Selector<T, T2> {
        T2 select(T item);
    }

    public static <T> double sumDouble(@NonNull List<T> items, @NonNull Selector<T, Double> selector) {
        if (items.size() == 0)
            return 0;
        double output = selector.select(items.get(0));
        for (int i = 1; i < items.size(); i++) {
            T it = items.get(i);
            double val = selector.select(it);
            output = output + val;
        }
        return output;
    }

    public static <T> int sumInt(@NonNull List<T> items, @NonNull Selector<T, Integer> selector) {
        if (items.size() == 0)
            return 0;
        int output = selector.select(items.get(0));
        for (int i = 1; i < items.size(); i++) {
            T it = items.get(i);
            int val = selector.select(it);
            output = output + val;
        }
        return output;
    }

    @NonNull
    public static <T> Currency sumCurrency(@NonNull List<T> items, @NonNull Selector<T, Currency> selector) {
        if (items.size() == 0)
            return Currency.ZERO;
        Currency output = selector.select(items.get(0));
        if (output == null)
            output = Currency.ZERO;
        for (int i = 1; i < items.size(); i++) {
            T it = items.get(i);
            Currency val = selector.select(it);
            if (val == null)
                val = Currency.ZERO;
            output = output.add(val);
        }
        return output;
    }

    @NonNull
    public static <T> BigDecimal sumBigDecimal(@NonNull List<T> items, @NonNull Selector<T, BigDecimal> selector) {
        if (items.size() == 0)
            return BigDecimal.ZERO;
        BigDecimal output = selector.select(items.get(0));
        if (output == null)
            output = BigDecimal.ZERO;
        for (int i = 1; i < items.size(); i++) {
            T it = items.get(i);
            BigDecimal val = selector.select(it);
            if (val == null)
                val = BigDecimal.ZERO;
            output = output.add(val);
        }
        return output;
    }

    @Nullable
    public static <T1, T2> T2 mapMerge(@NonNull List<T1> items, @NonNull Map<T1, T2> map, @NonNull Merge<T2> merge) {
        if (items.size() == 0)
            return null;
        T2 result = map.run(items.get(0));
        for (int i = 1; i < items.size(); i++) {
            T1 it = items.get(i);
            T2 mapped = map.run(it);
            result = merge.run(result, mapped);
        }
        return result;
    }

    public static <T> void sort(@NonNull List<T> items, @NonNull java.util.Comparator<T> comparator) {
        Collections.sort(items, comparator);
    }

    public static <T> void forEach(@NonNull List<T> items, @NonNull Consumer<T> consumer) {
        for (T item :
                items) {
            consumer.run(item);
        }
    }

    public static <T> List<T> forEach(@NonNull List<T> items, @NonNull Criteria<T> criteria) {
        ArrayList<T> output = new ArrayList<>();
        for (T item :
                items) {
            if (criteria.run(item))
                output.add(item);
        }
        return output;
    }

    public static <T> void forEach(@NonNull List<T> items, @NonNull Criteria<T> criteria, @NonNull Consumer<T> consumer) {
        for (T item :
                items) {
            if (criteria.run(item))
                consumer.run(item);
        }
    }

    @NonNull
    public static <T> List<T> intersect(@NonNull List<T> left, @NonNull List<T> right, @NonNull final Comparator<T> comparator) {
        List<T> output = new ArrayList<>();
        for (final T item1 :
                left) {
            final T it = findFirst(right, item2 -> comparator.compare(item1, item2));
            if (it != null)
                output.add(it);
        }
        return output;
    }

    @NonNull
    public static <T> List<T> relativeComplement(@NonNull List<T> left, @NonNull List<T> right, @NonNull final Comparator<T> comparator) {
        List<T> output = new ArrayList<>();
        for (final T item1 :
                left) {
            final T it = findFirst(right, item2 -> comparator.compare(item1, item2));
            if (it == null)
                output.add(item1);
        }
        return output;
    }

    @NonNull
    public static <K, V> HashMap<K, V> toHashMap(List<V> list, Map<V, K> map) {
        HashMap<K, V> hashMap = new HashMap<>();
        for (V item :
                list) {
            K key = map.run(item);
            hashMap.put(key, item);
        }
        return hashMap;
    }
}

