package com.varanegar.framework.util.report;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbHandler;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.ContextMenuItem;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.filter.FieldType;
import com.varanegar.framework.util.report.filter.Filter;
import com.varanegar.framework.util.report.filter.FilterField;
import com.varanegar.framework.util.report.filter.FiltersForm;
import com.varanegar.framework.util.report.filter.date.DateFilter;
import com.varanegar.framework.util.report.filter.integer.IntFilter;
import com.varanegar.framework.util.report.filter.string.StringFilter;
import com.varanegar.java.util.Currency;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by atp on 12/25/2016.
 */
public abstract class ReportAdapter<T extends BaseModel> {
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    private Locale locale;

    private int pagingSize = 0;

    public int getPagingSize() {
        return pagingSize;
    }

    public void setPagingSize(int limit) {
        this.pagingSize = limit;
    }

    public BaseRecyclerView getRecyclerView() {
        return recyclerView;
    }

    private BaseRecyclerView recyclerView;

    public Class<T> getTClass() {
        return tClass;
    }

    private Class<T> tClass;
    private static final String FILTERS = "5533d7c1-75d3-49ac-9f7a-5d617a1433c0";
    private static final String ISFILTERON = "1dbbed7d-bc9a-4a33-92b6-806abe939fa4";
    private VaranegarFragment fragment;
    private View reportView;
    protected MainVaranegarActivity activity;
    private BaseRecyclerAdapter<T> adapter;

    public BaseRecyclerAdapter<T> getAdapter() {
        return adapter;
    }

    private BaseRepository<T> repository;
    private Query query;
    private Query initQuery;
    private ArrayList<Filter> currentFilters;
    private Boolean filterIsOn = true;
    protected ReportColumns columnTemplates;
    private int srt = 1;
    protected Field fieldToSort;

    @Nullable
    protected T getInstanceOfT() {
        try {
            return tClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    View getReportView() {
        if (reportView == null)
            throw new IllegalStateException("Create() method has never been called.");
        return reportView;
    }

    protected MainVaranegarActivity getActivity() {
        return activity;
    }


    public ReportAdapter(MainVaranegarActivity activity, Class<T> tClass) {
        this.activity = activity;
        this.currentFilters = new ArrayList<>();
        this.tClass = tClass;
    }

    public ReportAdapter(VaranegarFragment fragment, Class<T> tClass) {
        this.activity = fragment.getVaranegarActvity();
        this.fragment = fragment;
        this.currentFilters = new ArrayList<>();
        this.tClass = tClass;
    }

    public abstract void bind(ReportColumns columns, T entity);

    abstract protected View onCreateItemView(ViewGroup parent);

    abstract protected BaseViewHolder<T> onCreateItemViewHolder(BaseRecyclerAdapter<T> adapter, View itemView);

    public void create(BaseRepository<T> baseRepository, Query query, Bundle savedInstanceState) {
        this.repository = baseRepository;
        try {
            this.query = query.clone();
            this.initQuery = query.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (getPagingSize() != 0)
            repository.setPagingSize(getPagingSize());
        createRecyclerAdapter(baseRepository, query);
        setup(savedInstanceState);
    }

    public void create(List<T> items, Bundle savedInstanceState) {
        createRecyclerAdapter();
        adapter.addAll(items);
        setup(savedInstanceState);
    }

    View onInflateLayout() {
        return activity.getLayoutInflater().inflate(R.layout.layout_report_base, null, false);
    }

    protected void setup(@Nullable Bundle savedInstanceState) {
        reportView = onInflateLayout();
        columnTemplates = new ReportColumns();
        bind(columnTemplates, getInstanceOfT());
        ImageView filterImageView = (ImageView) reportView.findViewById(R.id.filter_fab);
        if (filterImageView != null) {
            filterImageView.setVisibility(View.GONE);
            for (ReportColumn header : columnTemplates
                    ) {
                if (header.isFilterable()) {
                    filterImageView.setVisibility(View.VISIBLE);
                }
            }
            filterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFilters();
                }
            });
        }

        ImageView sortImageView = (ImageView) reportView.findViewById(R.id.sort_fab);
        final ReportColumns sortableReportColumn = new ReportColumns();
        for (ReportColumn column: columnTemplates) {
            if (column.isSortable())
                sortableReportColumn.add(column);
        }

        if (sortImageView != null) {
            if (sortableReportColumn.size()==0)
                sortImageView.setVisibility(View.GONE);
            sortImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final SearchBox<ReportColumn> searchBox = new SearchBox<>();
                    searchBox.setItems(sortableReportColumn, new SearchBox.SearchMethod<ReportColumn>() {
                        @Override
                        public boolean onSearch(ReportColumn item, String text) {
                            return item.title.contains(text);
                        }
                    });
                    searchBox.show(getActivity().getSupportFragmentManager(), "SortSearchBox");
                    searchBox.setOnItemSelectedListener(new SearchBox.OnItemSelectedListener<ReportColumn>() {
                        @Override
                        public void run(int position, ReportColumn item) {
                            searchBox.dismiss();
                            sort(item);
                        }
                    });
                }
            });
        }

        recyclerView = (BaseRecyclerView) reportView.findViewById(R.id.report_recycler_view);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (savedInstanceState != null)
            restoreSavedInstanceState(savedInstanceState);
        prepareData();
    }

    public void addOnItemClickListener(@NonNull ContextMenuItem contextMenuItem) {
        adapter.addContextMenuItem(contextMenuItem);
    }


    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Serializable filtersObject = savedInstanceState.getSerializable(FILTERS);
            if (filtersObject != null) {
                currentFilters = (ArrayList<Filter>) filtersObject;
            }
            filterIsOn = savedInstanceState.getBoolean(ISFILTERON, false);
            refresh();
        }
    }

    public void saveInstanceState(Bundle outState) {
        outState.putSerializable(FILTERS, currentFilters);
        outState.putBoolean(ISFILTERON, filterIsOn);
    }

    private void prepareData() {
        filterIsOn = VaranegarApplication.getInstance().tryRetrieve("f4316586-c85f-4644-8cc1-c690501496f9", true);
        filterIsOn = filterIsOn == null ? false : filterIsOn;
        currentFilters = VaranegarApplication.getInstance().tryRetrieve("c02af914-af8b-44a8-ba98-baa2412e4068", true);
        refresh();
    }

    private void createRecyclerAdapter(BaseRepository<T> baseRepository, Query query) {
        adapter = new BaseRecyclerAdapter<T>(this.activity, baseRepository, query) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = onCreateItemView(parent);
                return onCreateItemViewHolder(this, itemView);
            }

            @Override
            protected ItemContextView onCreateContextView() {
                return ReportAdapter.this.onCreateContextView();
            }
        };
    }

    protected ItemContextView<T> onCreateContextView() {
        return null;
    }

    private void createRecyclerAdapter() {
        adapter = new BaseRecyclerAdapter<T>(activity) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = onCreateItemView(parent);
                return onCreateItemViewHolder(this, itemView);
            }

            @Override
            protected ItemContextView onCreateContextView() {
                return ReportAdapter.this.onCreateContextView();
            }
        };
    }


    private void showFilters() {
        ArrayList<FilterField> fields = new ArrayList<>();
        for (int i = 0; i < columnTemplates.size(); i++) {
            if (columnTemplates.get(i).isFilterable()) {
                FilterField field = new FilterField();
                field.title = columnTemplates.get(i).title;
                field.columnIndex = i;
                Class type = columnTemplates.get(i).field.getType();
                if (type == int.class || type == Integer.class)
                    field.type = FieldType.Integer;
                else if (type == String.class)
                    field.type = FieldType.String;
                else if (type == Double.class || type == BigDecimal.class || type == Currency.class || type == double.class)
                    field.type = FieldType.Float;
                else if (type == Date.class)
                    field.type = FieldType.Date;
                fields.add(field);
            }
        }

        FiltersForm filtersForm = new FiltersForm();
        filtersForm.fields = fields;
        filtersForm.setLocale(locale);

        if (currentFilters != null) {
            filtersForm.filters = new ArrayList<>();
            for (Filter f :
                    currentFilters) {

                if (f instanceof StringFilter) {
                    StringFilter stringFilter = (StringFilter) f;
                    StringFilter filter = new StringFilter();
                    filter.filterIndex = stringFilter.filterIndex;
                    filter.columnIndex = stringFilter.columnIndex;
                    filter.text = stringFilter.text;
                    filtersForm.filters.add(filter);
                } else if (f instanceof IntFilter) {
                    IntFilter intFilter = (IntFilter) f;
                    IntFilter filter = new IntFilter();
                    filter.filterIndex = intFilter.filterIndex;
                    filter.columnIndex = intFilter.columnIndex;
                    filter.value = intFilter.value;
                    filter.operator = intFilter.operator;
                    filtersForm.filters.add(filter);
                } else if (f instanceof DateFilter) {
                    DateFilter dateFilter = (DateFilter) f;
                    DateFilter filter = new DateFilter();
                    filter.filterIndex = dateFilter.filterIndex;
                    filter.columnIndex = dateFilter.columnIndex;
                    filter.value = dateFilter.value;
                    filter.operator = dateFilter.operator;
                    filtersForm.filters.add(filter);
                }

            }
        }
        filtersForm.filterIsOn = filterIsOn;
        filtersForm.reportAdapter = this;
        activity.pushFragment(filtersForm);
    }

    public synchronized void refresh(Query newQuery) {
        initQuery = newQuery;
        refresh();
    }

    public synchronized void refresh() {
        try {
            if (initQuery != null)
                query = initQuery.clone();
            if (currentFilters != null && currentFilters.size() > 0 && filterIsOn) {
                for (Filter filter : currentFilters
                        ) {
                    if (filter instanceof StringFilter) {
                        runStringFilter(columnTemplates.get(filter.columnIndex), ((StringFilter) filter).text);
                    } else if (filter instanceof IntFilter) {
                        runIntFilter(columnTemplates.get(filter.columnIndex), (IntFilter) filter);
                    } else if (filter instanceof DateFilter) {
                        runDateFilter(columnTemplates.get(filter.columnIndex), (DateFilter) filter);
                    }
                }
            }
            if (repository != null) {
                adapter.refresh(query);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void runStringFilter(final ReportColumn header, String text) {
        text = HelperMethods.persian2Arabic(text);
        text = HelperMethods.convertToEnglishNumbers(text);
        if (this.repository == null) {
            List<T> filteredItems = new ArrayList<>();
            for (T item : adapter.getItems()
                    ) {
                if (header.field.getType() == String.class) {
                    String str;
                    try {
                        str = (String) header.field.get(item);
                        if (text == null)
                            filteredItems.add(item);
                        else if (str.contains(text))
                            filteredItems.add(item);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            adapter.clear();
            adapter.addAll(filteredItems);
        } else {
            query.whereAnd(Criteria.contains(header.projection, text));
        }

    }

    private synchronized void runIntFilter(ReportColumn header, IntFilter filter) {
        if (this.repository == null) {
            List<T> filteredItems = new ArrayList<>();
            for (T item : adapter.getItems()
                    ) {
                if (header.field.getType() == int.class) {
                    int number;
                    try {
                        number = header.field.getInt(item);
                        if (filter.operator == IntFilter.IntFilterOperator.Equals && filter.value == number)
                            filteredItems.add(item);
                        if (filter.operator == IntFilter.IntFilterOperator.GreaterThan && filter.value < number)
                            filteredItems.add(item);
                        if (filter.operator == IntFilter.IntFilterOperator.LessThan && filter.value > number)
                            filteredItems.add(item);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            adapter.clear();
            adapter.addAll(filteredItems);
        } else {
            if (filter.operator == IntFilter.IntFilterOperator.Equals)
                query.whereAnd(Criteria.equals(header.projection, filter.value));
            else if (filter.operator == IntFilter.IntFilterOperator.GreaterThan)
                query.whereAnd(Criteria.greaterThan(header.projection, filter.value));
            else if (filter.operator == IntFilter.IntFilterOperator.LessThan)
                query.whereAnd(Criteria.lesserThan(header.projection, filter.value));
        }
    }

    private synchronized void runDateFilter(ReportColumn header, DateFilter filter) {
        if (this.repository == null) {
            List<T> filteredItems = new ArrayList<>();
            for (T item : adapter.getItems()
                    ) {
                if (header.field.getType() == Date.class) {
                    Date date;
                    try {
                        date = (Date) header.field.get(item);
                        if (filter.operator == DateFilter.DateFilterOperator.Equals && filter.value.equals(date))
                            filteredItems.add(item);
                        if (filter.operator == DateFilter.DateFilterOperator.After && filter.value.before(date))
                            filteredItems.add(item);
                        if (filter.operator == DateFilter.DateFilterOperator.Before && filter.value.after(date))
                            filteredItems.add(item);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            adapter.clear();
            adapter.addAll(filteredItems);
        } else {
            if (filter.operator == DateFilter.DateFilterOperator.Equals)
                query.whereAnd(Criteria.equals(header.projection, filter.value.getTime()));
            else if (filter.operator == DateFilter.DateFilterOperator.After)
                query.whereAnd(Criteria.greaterThan(header.projection, filter.value.getTime()));
            else if (filter.operator == DateFilter.DateFilterOperator.Before)
                query.whereAnd(Criteria.lesserThan(header.projection, filter.value.getTime()));
        }
    }

    public int size() {
        return adapter.getItemCount();
    }

    public boolean isEmpty() {
        return adapter.isEmpty();
    }

    public boolean contains(T o) {
        return adapter.contains(o);
    }

    @NonNull
    public Iterator<T> iterator() {
        return adapter.iterator();
    }

    @NonNull
    public Object[] toArray() {
        return adapter.toArray();
    }

    @NonNull
    public T[] toArray(@NonNull T[] t1s) {
        return adapter.toArray(t1s);
    }

    public boolean add(T t) {
        return adapter.add(t);
    }

    public boolean remove(Object o) {
        return adapter.remove(o);
    }

    public boolean containsAll(@NonNull Collection<?> collection) {
        return adapter.containsAll(collection);
    }

    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return adapter.addAll(collection);
    }

    public boolean addAll(int i, @NonNull Collection<? extends T> collection) {
        return adapter.addAll(i, collection);
    }

    public boolean removeAll(@NonNull Collection<?> collection) {
        return adapter.retainAll(collection);
    }

    public boolean retainAll(@NonNull Collection<?> collection) {
        return adapter.retainAll(collection);
    }

    public void clear() {
        adapter.clear();
    }

    public T get(int i) {
        return adapter.get(i);
    }

    public T set(int i, T t) {
        return adapter.set(i, t);
    }


    public void add(int i, T t) {
        adapter.add(i, t);
    }


    public T remove(int i) {
        return adapter.remove(i);
    }


    public int indexOf(Object o) {
        return adapter.indexOf(o);
    }


    public int lastIndexOf(Object o) {
        return adapter.lastIndexOf(o);
    }


    public ListIterator<T> listIterator() {
        return adapter.listIterator();
    }

    @NonNull

    public ListIterator<T> listIterator(int i) {
        return adapter.listIterator(i);
    }

    @NonNull

    public List<T> subList(int i, int i1) {
        return adapter.subList(i, i1);
    }


    public <T extends BaseModel> ReportColumn bind(T entity, ModelProjection<T> map, String title) {
        try {
            return new ReportColumn(entity, map, title);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected synchronized String calculateTotal(final ReportColumn header) {
        if (header.isTotal()) {
            if (repository != null) {
                try {
                    Query q = query.clone();
                    q.select(Projection.sum(header.projection).as("count"));
                    DbHandler db = VaranegarApplication.getInstance().getDbHandler();
                    if (header.field.getType() == int.class) {
                        Integer result = db.getIntegerSingle(q);
                        return result == null ? "" : Integer.toString(result);
                    } else if (header.field.getType() == double.class) {
                        Double result = db.getDoubleSingle(q);
                        return result == null ? "" : Double.toString(result);
                    } else if (header.field.getType() == float.class) {
                        Float result = db.getFloatSingle(q);
                        return result == null ? "" : Float.toString(result);
                    } else if (header.field.getType() == short.class) {
                        Short result = db.getShortSingle(q);
                        return result == null ? "" : Short.toString(result);
                    } else if (header.field.getType() == long.class) {
                        Long result = db.getLongSingle(q);
                        return result == null ? "" : Long.toString(result);
                    } else if (header.field.getType() == BigDecimal.class) {
                        BigDecimal result = db.getBigDecimalSingle(q);
                        return result == null ? "" : result.toString();
                    } else if (header.field.getType() == Currency.class) {
                        Currency result = db.getCurrencySingle(q);
                        return result == null ? "" : result.toString();
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            } else {
                if (adapter.getItemCount() > 0) {
                    if (header.field.getType() == int.class) {
                        int sum = 0;
                        for (T t : adapter.getItems()
                                ) {
                            try {
                                sum += header.field.getInt(t);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return Integer.toString(sum);
                    } else if (header.field.getType() == double.class) {
                        double sum = 0;
                        for (T t : adapter.getItems()
                                ) {
                            try {
                                sum += header.field.getDouble(t);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return Double.toString(sum);
                    } else if (header.field.getType() == float.class) {
                        float sum = 0;
                        for (T t : adapter.getItems()
                                ) {
                            try {
                                sum += header.field.getFloat(t);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return Float.toString(sum);
                    } else if (header.field.getType() == short.class) {
                        short sum = 0;
                        for (T t : adapter.getItems()
                                ) {
                            try {
                                sum += header.field.getShort(t);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return Short.toString(sum);
                    } else if (header.field.getType() == long.class) {
                        long sum = 0;
                        for (T t : adapter.getItems()
                                ) {
                            try {
                                sum += header.field.getLong(t);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return Long.toString(sum);
                    } else if (header.field.getType() == Currency.class) {
                        Currency sum = new Currency(0);
                        for (T t : adapter.getItems()
                                ) {
                            try {
                                Currency d = (Currency) (header.field.get(t));
                                if (d != null)
                                    sum = (Currency) sum.add(d);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return sum.toString();

                    } else if (header.field.getType() == BigDecimal.class) {
                        BigDecimal sum = new BigDecimal(0);
                        for (T t : adapter.getItems()
                                ) {
                            try {
                                BigDecimal d = (BigDecimal) header.field.get(t);
                                if (d != null)
                                    sum = sum.add(d);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        return sum.toString();
                    }
                }
            }

        }
        return "";
    }


    public synchronized void sort(final ReportColumn header) {
        if (repository != null) {
            initQuery.clearOrderBy();
            if (fieldToSort != header.field) {
                initQuery.orderByAscendingIgnoreCase(header.projection);
                fieldToSort = header.field;
            } else {
                initQuery.orderByDescendingIgnoreCase(header.projection);
                fieldToSort = null;
            }
            refresh();
        } else {
            Comparator<T> comparator = new Comparator<T>() {
                @Override
                public int compare(T t, T t1) {
                    int res = 0;
                    try {
                        if (header.field.getType() == String.class) {
                            String str1 = (String) header.field.get(t);
                            String str2 = (String) header.field.get(t1);
                            res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                            if (res == 0) {
                                res = str1.compareTo(str2);
                            }
                            return res;
                        } else if (header.field.getType() == int.class) {
                            Integer val1 = header.field.getInt(t);
                            Integer val2 = header.field.getInt(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == double.class) {
                            Double val1 = header.field.getDouble(t);
                            Double val2 = header.field.getDouble(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == float.class) {
                            Float val1 = header.field.getFloat(t);
                            Float val2 = header.field.getFloat(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == short.class) {
                            Short val1 = header.field.getShort(t);
                            Short val2 = header.field.getShort(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == byte.class) {
                            Byte val1 = header.field.getByte(t);
                            Byte val2 = header.field.getByte(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == boolean.class) {
                            Boolean val1 = header.field.getBoolean(t);
                            Boolean val2 = header.field.getBoolean(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == Date.class) {
                            Date val1 = (Date) header.field.get(t);
                            Date val2 = (Date) header.field.get(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == Currency.class) {
                            Currency val1 = (Currency) header.field.get(t);
                            Currency val2 = (Currency) header.field.get(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == BigDecimal.class) {
                            BigDecimal val1 = (BigDecimal) header.field.get(t);
                            BigDecimal val2 = (BigDecimal) header.field.get(t1);
                            return val1.compareTo(val2);
                        } else if (header.field.getType() == long.class) {
                            Long val1 = header.field.getLong(t);
                            Long val2 = header.field.getLong(t1);
                            return val1.compareTo(val2);
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return res * srt;
                }
            };

            if (fieldToSort != header.field) {
                adapter.sort(comparator);
                fieldToSort = header.field;
            } else
                adapter.reverse();
            adapter.notifyDataSetChanged();
        }
    }

    public void deselectAll() {
        selectedPositions.clear();
    }

    public void selectAll() {
        for (int i = 0; i < size(); i++) {
            select(i);
        }
    }

    public void select(final int position) {
        if (!selectedPositions.contains(position)) {
            selectedPositions.add(position);
        }
    }

    public void select(final T item) {
        int idx = Linq.findFirstIndex(adapter.getItems(), new Linq.Criteria<T>() {
            @Override
            public boolean run(T it) {
                return it.equals(item);
            }
        });
        if (idx >= 0) {
            select(idx);
        }
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public interface OnItemSelectListener<T> {
        void onItemSelected(int idx);

        void onItemDeSelected(int idx);
    }

    private List<Integer> selectedPositions = new ArrayList<>();

    public List<Integer> getSelectedPositions() {
        return selectedPositions;
    }

    public List<T> getSelectedItems() {
        List<T> selectedItems = new ArrayList<>();
        for (Integer p :
                selectedPositions) {
            selectedItems.add(adapter.get(p));
        }
        return selectedItems;
    }

    public void setOnItemSelectListener(@NonNull final OnItemSelectListener<T> onItemSelectListener) {
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClick<T>() {
            @Override
            public void run(final int position) {
                if (selectedPositions.contains(position)) {
                    selectedPositions = Linq.remove(selectedPositions, new Linq.Criteria<Integer>() {
                        @Override
                        public boolean run(Integer item) {
                            return item == position;
                        }
                    });
                    adapter.notifyDataSetChanged();
                    onItemSelectListener.onItemDeSelected(position);
                } else {
                    selectedPositions.add(position);
                    onItemSelectListener.onItemSelected(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void setEnabled(boolean enabled) {
        adapter.setEnabled(enabled);
    }
}
