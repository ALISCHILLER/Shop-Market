package com.varanegar.vaslibrary.ui.dialog.News;

import android.view.View;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;

/**
 * Created by m-latifi on 10/26/2022.
 */

public class SlideNewsHolder extends SliderViewAdapter.ViewHolder{

    private final TextView textViewCount;
    private final TextView textViewNewsTitle;
    private final TextView textViewNewsBody;

    //---------------------------------------------------------------------------------------------- SlideNewsHolder
    public SlideNewsHolder(View itemView) {
        super(itemView);
        textViewCount = itemView.findViewById(R.id.textViewCount);
        textViewNewsTitle = itemView.findViewById(R.id.textViewNewsTitle);
        textViewNewsBody = itemView.findViewById(R.id.textViewNewsBody);
    }
    //---------------------------------------------------------------------------------------------- SlideNewsHolder


    //---------------------------------------------------------------------------------------------- bind
    public void bind(NewsData_Model item, int position) {
        textViewCount.setText(String.valueOf(position+1));
        textViewNewsTitle.setText(item.title);
        textViewNewsBody.setText(item.body);
    }
    //---------------------------------------------------------------------------------------------- bind

}
