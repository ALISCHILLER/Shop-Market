package com.varanegar.vaslibrary.ui.dialog.News;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.varanegar.framework.util.component.CuteDialogWithToolbar;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;

import java.util.List;

/**
 * Created by m-latifi on 10/26/2022.
 */

public class NewsDialog extends CuteDialogWithToolbar {

    private SliderView imageSlider;
    private List<NewsData_Model> news;

    //---------------------------------------------------------------------------------------------- NewsDialog
    public NewsDialog(List<NewsData_Model> news) {
        this.news = news;
    }
    //---------------------------------------------------------------------------------------------- NewsDialog



    //---------------------------------------------------------------------------------------------- onCreateDialogView
    @Override
    public View onCreateDialogView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_news, container, false);
        imageSlider = view.findViewById(R.id.imageSlider);
        setSlideAdapter();
        return view;
    }
    //---------------------------------------------------------------------------------------------- onCreateDialogView


    //---------------------------------------------------------------------------------------------- setSlideAdapter
    public void setSlideAdapter() {
        SliderNewsAdapter adapter = new SliderNewsAdapter(news);
        imageSlider.setSliderAdapter(adapter);
        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);

    }
    //---------------------------------------------------------------------------------------------- setSlideAdapter


}
