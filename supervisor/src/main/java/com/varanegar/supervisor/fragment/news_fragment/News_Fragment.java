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
import com.varanegar.supervisor.fragment.news_fragment.cards.SliderAdapter;

public class News_Fragment extends IMainPageFragment {
    private RecyclerView recyclerView;
    private final int[] pics = {R.drawable.zar};
    private  SliderAdapter sliderAdapter;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container
            ,@Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliderAdapter=new SliderAdapter(pics, 20, new OnCardClickListener());
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
}
