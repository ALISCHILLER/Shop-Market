package com.varanegar.supervisor.fragment.news_fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.ui.card_slider.CardSliderLayoutManager;
import com.varanegar.framework.ui.card_slider.CardSnapHelper;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.DataManager;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_;
import com.varanegar.supervisor.fragment.news_fragment.cards.SliderAdapter;
import com.varanegar.supervisor.fragment.news_fragment.model.NewsData_;
import com.varanegar.supervisor.fragment.news_fragment.model.NewsData_Model;
import com.varanegar.supervisor.fragment.news_fragment.model.NewsData_ModelRepository;

import java.util.List;

public class News_Fragment extends IMainPageFragment {
    private RecyclerView recyclerView;

    private  SliderAdapter sliderAdapter;
    private CardSliderLayoutManager layoutManger;
    private FloatingActionButton fab;
    private TextSwitcher temperatureSwitcher;
    private TextSwitcher placeSwitcher;
    private TextSwitcher clockSwitcher;
    private TextSwitcher descriptionsSwitcher;

    private TextView country1TextView;
    private TextView country2TextView;
    private int countryOffset1;
    private int countryOffset2;
    private long countryAnimDuration;
    private int currentPosition;

    private View greenDot;
    private final int[] pics = {R.drawable.zar};
    private final int[][] dotCoords = new int[5][2];


    private List<NewsData_Model> newsData_list;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news_layout,container,false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        getdata();
        sliderAdapter=new SliderAdapter(pics, newsData_list.size(), new OnCardClickListener());
        initRecyclerView();
        if (newsData_list.size()>0) {
            initCountryText(view);
            initSwitchers(view);
        }
        fab=view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restData();
            }
        });
    }
    private void getdata(){
        NewsData_ModelRepository repository=new NewsData_ModelRepository();
        Query query=new Query();
        query.from(NewsData_.NewsData_Tbl);
       newsData_list=repository.getItems(query);
    }
    private void setNewsData_list(){


    }
    private void initRecyclerView() {


        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });

        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        new CardSnapHelper().attachToRecyclerView(recyclerView);
    }

    private void onActiveCardChange() {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }

        onActiveCardChange(pos);
    }

    private void initCountryText(View view) {
        countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        countryOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
        countryOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
        country1TextView = (TextView) view.findViewById(R.id.tv_country_1);
        country2TextView = (TextView) view.findViewById(R.id.tv_country_2);

        country1TextView.setX(countryOffset1);
        country2TextView.setX(countryOffset2);
        country1TextView.setText(newsData_list.get(0).title);
        country2TextView.setAlpha(0f);


    }

    private void initSwitchers(View view) {
        temperatureSwitcher = (TextSwitcher)view.findViewById(R.id.ts_temperature);
        temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
        temperatureSwitcher.setCurrentText("0");
        placeSwitcher = (TextSwitcher) view.findViewById(R.id.ts_place);

        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
        placeSwitcher.setCurrentText(newsData_list.get(0).title);

        clockSwitcher = (TextSwitcher) view.findViewById(R.id.ts_clock);
        clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
        clockSwitcher.setCurrentText(newsData_list.get(0).publishPDate);
        descriptionsSwitcher = (TextSwitcher) view.findViewById(R.id.ts_description);
        descriptionsSwitcher.setInAnimation(getActivity(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(getActivity(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
        descriptionsSwitcher.setCurrentText(newsData_list.get(0).body);
    }
    private void onActiveCardChange(int pos) {
        int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        setCountryText(newsData_list.get(pos).title, left2right);

        temperatureSwitcher.setInAnimation(getActivity(), animH[0]);
        temperatureSwitcher.setOutAnimation(getActivity(), animH[1]);
        temperatureSwitcher.setText(String.valueOf(pos));

        placeSwitcher.setInAnimation(getActivity(), animV[0]);
        placeSwitcher.setOutAnimation(getActivity(), animV[1]);
        placeSwitcher.setText(newsData_list.get(pos).title);

        clockSwitcher.setInAnimation(getActivity(), animV[0]);
        clockSwitcher.setOutAnimation(getActivity(), animV[1]);
        clockSwitcher.setText(newsData_list.get(pos).publishPDate);

        descriptionsSwitcher.setText(newsData_list.get(pos).body);


        ViewCompat.animate(greenDot)
                .translationX(dotCoords[pos % dotCoords.length][0])
                .translationY(dotCoords[pos % dotCoords.length][1])
                .start();

        currentPosition = pos;
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(getActivity());

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(getActivity(), styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }

    private void setCountryText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (country1TextView.getAlpha() > country2TextView.getAlpha()) {
            visibleText = country1TextView;
            invisibleText = country2TextView;
        } else {
            visibleText = country2TextView;
            invisibleText = country1TextView;
        }

        final int vOffset;
        if (left2right) {
            invisibleText.setX(0);
            vOffset = countryOffset2;
        } else {
            invisibleText.setX(countryOffset2);
            vOffset = 0;
        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(countryAnimDuration);
        animSet.start();
    }
    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
    public void restData(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.downloading_data));
        progressDialog.show();
        DataManager dataManager=new DataManager(getContext());
        dataManager.getNewsZar2(new DataManager.Callback() {
            @Override
            public void onSuccess() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                        getdata();
                        sliderAdapter=new SliderAdapter(pics, newsData_list.size(),
                                new OnCardClickListener());
                        initRecyclerView();
                    } catch (Exception ignored) {
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onError(String error) {
                showError(error);
                if (progressDialog != null && progressDialog.isShowing()) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ignored) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }
    private void showError(String error) {
        Context context = getContext();
        if (isResumed() && context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
}
