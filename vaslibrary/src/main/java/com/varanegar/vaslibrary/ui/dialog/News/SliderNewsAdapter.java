package com.varanegar.vaslibrary.ui.dialog.News;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;

import java.util.List;

/**
 * Created by m-latifi on 10/26/2022.
 */

public class SliderNewsAdapter extends SliderViewAdapter<SlideNewsHolder> {


    private List<NewsData_Model> news;
    private LayoutInflater layoutInflater;

    //---------------------------------------------------------------------------------------------- SliderNewsAdapter
    public SliderNewsAdapter(List<NewsData_Model> news) {
        this.news = news;
    }
    //---------------------------------------------------------------------------------------------- SliderNewsAdapter



    //---------------------------------------------------------------------------------------------- onCreateViewHolder
    @Override
    public SlideNewsHolder onCreateViewHolder(ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());
        return new SlideNewsHolder(layoutInflater.inflate(R.layout.ap_news, parent, false));
    }
    //---------------------------------------------------------------------------------------------- onCreateViewHolder



    //---------------------------------------------------------------------------------------------- onBindViewHolder
    @Override
    public void onBindViewHolder(SlideNewsHolder viewHolder, int position) {
        viewHolder.bind(news.get(position), position);
    }
    //---------------------------------------------------------------------------------------------- onBindViewHolder


    //---------------------------------------------------------------------------------------------- getCount
    @Override
    public int getCount() {
        return news.size();
    }
    //---------------------------------------------------------------------------------------------- getCount


}
