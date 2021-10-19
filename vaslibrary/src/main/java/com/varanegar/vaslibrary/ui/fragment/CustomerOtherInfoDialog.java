package com.varanegar.vaslibrary.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerOtherInfoDialog extends CuteDialogWithToolbar {
    public String CustomerMessage;

    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.customer_other_fields_layout, container , false);
        BaseRecyclerView baseRecyclerView = view.findViewById(R.id.items_recycler_view);
        String[] fields = CustomerMessage.split(("\\|\\|"));
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(fields));
        final BaseRecyclerAdapter<String> adapter = new BaseRecyclerAdapter<String>(getVaranegarActvity(), list) {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_other_info_row, parent , false);
                return new BaseViewHolder<String>(view, this, getContext()) {
                    @Override
                    public void bindView(int position) {
                        String text = recyclerAdapter.get(position);
                        ((TextView) itemView.findViewById(R.id.text_view)).setText(text);
                    }
                };
            }
        };
        baseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        baseRecyclerView.setAdapter(adapter);
        return view;
    }
}
