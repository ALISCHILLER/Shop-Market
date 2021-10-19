package com.varanegar.framework.util.component;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.R;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;

/**
 * Created by atp on 8/21/2016.
 */
public class SearchBox<T> extends CuteDialogWithToolbar {
    private Tokenizer tokenizer;
    private BaseRepository repository;
    private SearchBoxItemAdapter adapter;
    private EditText searchEditText;
    private SearchQuery searchQuery;
    private SearchMethod<T> searchMethod;
    private SearchMethodTokenized<T> searchMethodTokenized;
    private PreprocessMethod preprocessMethod;
    private OnItemSelectedListener<T> onItemSelectedListener;
    private boolean searchIsEnabled = true;
    private List<T> items;
    private List<T> filteredItems;
    private boolean multiSelect;
    private Set<Integer> selectedIndexes = new HashSet<>();
    private OnSelectionFinishedListener<T> onSelectionFinishedListener;
    long delay = 100; // milliseconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private RecyclerView resultListView;

    public Set<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }

    public List<T> getSelectedItems() {
        List<T> list = new ArrayList<>();
        for (Integer integer :
                selectedIndexes) {
            list.add(items.get(integer));
        }
        return list;
    }

    public void isMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    public interface Tokenizer {
        String[] tokenize(String text);
    }

    /**
     * if tokenization is enabled then you have to implement SearchMethodTokenized
     *
     * @param tokenizer
     */

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public interface OnSelectionFinishedListener<T> {
        void onDone(List<T> selectedItems);

        void onCancel();
    }

    public void setOnSelectionFinishedListener(OnSelectionFinishedListener<T> onSelectionFinishedListener) {
        this.onSelectionFinishedListener = onSelectionFinishedListener;
    }

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        long s = new Date().getTime();
        View view = inflater.inflate(R.layout.searchbox_layout, container, false);
        if (multiSelect) {
            view.findViewById(R.id.footer_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.cancel_text_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (onSelectionFinishedListener != null)
                        onSelectionFinishedListener.onCancel();
                }
            });
            view.findViewById(R.id.ok_text_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if (onSelectionFinishedListener != null) {
                        List<T> list = new ArrayList<>();
                        for (int idx :
                                selectedIndexes) {
                            list.add(items.get(idx));
                        }
                        onSelectionFinishedListener.onDone(list);
                    }
                }
            });
        } else
            view.findViewById(R.id.footer_layout).setVisibility(View.GONE);


        resultListView = view.findViewById(R.id.result_list_view);
        searchEditText = view.findViewById(R.id.search_edit_text);

        long d = new Date().getTime() - s;
        Log.e("onCreateDialogView", d + " miliseconds!");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        long s = new Date().getTime();
        resultListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        searchEditText.setVisibility(searchIsEnabled ? View.VISIBLE : View.GONE);
        searchEditText.addTextChangedListener(new TextWatcher() {
            String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = searchEditText.getText().toString();
                if (!oldText.equals(newText)) {
                    oldText = newText;
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (System.currentTimeMillis() > (last_text_edit + delay)) {
                                if (repository != null && searchQuery != null) {
                                    Query query = searchQuery.onSearch(searchEditText.getText().toString());
                                    repository.resetPaging();
                                    List<T> fetchedItems = repository.getPagedItems(query);
                                    adapter.setItems(fetchedItems);
                                } else if ((searchMethod != null || searchMethodTokenized != null) && items != null) {
                                    String keyWord = searchEditText.getText().toString();
                                    String[] splits = null;
                                    if (preprocessMethod != null)
                                        keyWord = preprocessMethod.run(keyWord);
                                    if (tokenizer != null)
                                        splits = tokenizer.tokenize(keyWord);
                                    final String finalKeyWord = keyWord;
                                    final String[] finalSplits = splits;
                                    filteredItems = Linq.findAll(items, new Linq.Criteria<T>() {
                                        @Override
                                        public boolean run(T item) {
                                            if (tokenizer != null) {
                                                if (searchMethodTokenized == null)
                                                    throw new IllegalStateException("Search method tokenized should be implemented!");
                                                return searchMethodTokenized.onSearch(item, finalSplits);
                                            } else {
                                                if (searchMethod == null)
                                                    throw new IllegalStateException("Search method should be implemented!");
                                                return searchMethod.onSearch(item, finalKeyWord);
                                            }
                                        }
                                    });
                                    adapter.setItems(filteredItems);
                                } else
                                    throw new IllegalStateException();
                            }

                        }
                    }, delay + 10);

                }

            }
        });

        if (repository != null && searchQuery != null) {
            Query query = searchQuery.onSearch(searchEditText.getText().toString());
            List<T> fetchedItems = repository.getPagedItems(query);
            if (fetchedItems != null)
                adapter = new SearchBoxItemAdapter(fetchedItems);
            else
                adapter = new SearchBoxItemAdapter(null);
        } else if ((searchMethod != null || searchMethodTokenized != null) && items != null) {
            filteredItems = new ArrayList<>();
            filteredItems.addAll(items);
            adapter = new SearchBoxItemAdapter(filteredItems);
        } else
            throw new IllegalStateException();

        resultListView.setAdapter(adapter);

        long d = new Date().getTime() - s;
        Log.e("OnResume", d + " miliseconds!");
    }

    protected int lastLastVisibleItem;

    @Override
    public void onStart() {
        super.onStart();


        if (repository != null) {

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
            resultListView.addOnScrollListener(bufferListener);
        }
    }

    private void buffer() {
        Query query = searchQuery.onSearch(searchEditText.getText().toString());
        List<T> pagedItems = repository.getPagedItems(query);
        if (pagedItems != null && adapter != null) {
            if (pagedItems.size() > 0) {
                adapter.addAll(pagedItems);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void disableSearch() {
        searchIsEnabled = false;
    }

    public void enableSearch() {
        searchIsEnabled = true;
    }

    public void setRepository(@NonNull BaseRepository repository, @NonNull SearchQuery query) {
        this.repository = repository;
        this.searchQuery = query;
    }

    public void setPreprocess(@NonNull PreprocessMethod method) {
        this.preprocessMethod = method;
    }

    public void setItems(@NonNull List<T> items, @NonNull SearchMethod<T> method) {
        this.items = items;
        this.searchMethod = method;
    }

    public void setItems(@NonNull List<T> items, @NonNull SearchMethodTokenized<T> method) {
        this.items = items;
        this.searchMethodTokenized = method;
    }

    public void setOnItemSelectedListener(@NonNull OnItemSelectedListener<T> onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface SearchQuery {
        Query onSearch(String text);
    }

    public interface SearchMethod<T> {
        boolean onSearch(T item, String text);
    }


    public interface SearchMethodTokenized<T> {
        boolean onSearch(T item, String[] keyWords);
    }

    public interface PreprocessMethod {
        String run(String text);
    }

    public interface OnItemSelectedListener<T> {
        void run(int position, T item);
    }

    class SearchBoxItemAdapter extends RecyclerView.Adapter {

        private List<T> adapterItems = new ArrayList<>();

        SearchBoxItemAdapter(List<T> items) {
            if (items == null)
                items = new ArrayList<>();
            this.adapterItems = items;
            notifyDataSetChanged();
        }

        public void addAll(List<T> pagedItems) {
            if (adapterItems == null)
                adapterItems = new ArrayList<>();
            adapterItems.addAll(pagedItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.searchbox_item_layout, parent, false);
            return new SearchBoxItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SearchBoxItemViewHolder viewHolder = (SearchBoxItemViewHolder) holder;
            viewHolder.bind(position);

        }

        @Override
        public int getItemCount() {
            return adapterItems.size();
        }

        public void setItems(List<T> items) {
            if (items == null)
                items = new ArrayList<>();
            adapterItems = new ArrayList<>();
            adapterItems.addAll(items);
            notifyDataSetChanged();
        }

        private class SearchBoxItemViewHolder extends RecyclerView.ViewHolder {
            private final ImageView checkBoxImageView;
            private final TextView rowNumberTextView;
            private final TextView titleTextView;

            public SearchBoxItemViewHolder(View itemView) {
                super(itemView);
                checkBoxImageView = itemView.findViewById(R.id.check_box_image_view);
                rowNumberTextView = itemView.findViewById(R.id.row_number_text_view);
                titleTextView = itemView.findViewById(R.id.title_text_view);

            }

            public void bind(final int position) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedIndexes.contains(position))
                            selectedIndexes.remove(Integer.valueOf(position));
                        else
                            selectedIndexes.add(position);
                        if (onItemSelectedListener != null) {
                            onItemSelectedListener.run(position, (T) adapterItems.get(position));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });


                if (multiSelect) {
                    checkBoxImageView.setVisibility(View.VISIBLE);
                    if (selectedIndexes.contains(position))
                        checkBoxImageView.setImageResource(R.drawable.ic_check_box_black_24dp);
                    else
                        checkBoxImageView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                } else
                    checkBoxImageView.setVisibility(View.GONE);
                rowNumberTextView.setText(String.valueOf(position + 1));
                titleTextView.setText(adapterItems.get(position).toString());

            }
        }
    }
}
