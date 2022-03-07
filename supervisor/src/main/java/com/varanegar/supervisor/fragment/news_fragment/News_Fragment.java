package com.varanegar.supervisor.fragment.news_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;

public class News_Fragment extends IMainPageFragment {
    private RecyclerView recyclerView;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container
            ,@Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news_layout,container,false);
        return view;
    }
}
