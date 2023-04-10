package com.varanegar.vaslibrary.ui.fragment.new_fragment.confectionerycommissiondata;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.evrencoskun.tableview.TableView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.google.android.material.tabs.TabLayout;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.R;

import java.util.ArrayList;

public class ConfectioneryCommissionDataFragment extends VaranegarFragment {



    private TableView mTableView;
    private TextView textViewDate,textViewDate_tabel,text_view_RankOnTeam
            ,text_view_RankOnGlobal,textviewMonth;
    private LinearLayout layout_pie_chart,layout_idBarChart,header;
    private BarChart chart;
    private TabLayout reportsTabLayout;
    private RelativeLayout fragment_container;
    private PieChart pieChart;
    private ConstraintLayout shareCommission;
    // variable for our bar chart
    BarChart barChart;
    // variable for our bar data set.
    BarDataSet barDataSet1, barDataSet2;
    // array list for storing entries.
    ArrayList barEntries;
    // creating a string array for displaying productGroup.
    String[] productGroup = new String[]{"ویفر","کیک","بیسکویت"};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.layout_confectionerycommission_fragment, container, false);
        Activity a = getActivity();
        if (a != null) {
            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }

        textViewDate = view.findViewById(R.id.textViewDate);
        textViewDate_tabel = view.findViewById(R.id.textViewDatetabel);
        text_view_RankOnTeam = view.findViewById(R.id.textviewRankOnTeam);
        text_view_RankOnGlobal = view.findViewById(R.id.textviewRankOnGlobal);
        layout_pie_chart=view.findViewById(R.id.layout_pie_chart);
        layout_idBarChart=view.findViewById(R.id.layout_idBarChart);
        chart = view.findViewById(R.id.pie_chart);
        // Let's get TableView
        mTableView = view.findViewById(R.id.tableview);
        fragment_container = view.findViewById(R.id.fragment_container);
        barChart = view.findViewById(R.id.idBarChart);
        header = view.findViewById(R.id.header);
        reportsTabLayout = view.findViewById(R.id.reports_tab_layout);
        layout_pie_chart = view.findViewById(R.id.layout_pie_chart);
        layout_idBarChart = view.findViewById(R.id.layout_idBarChart);
        shareCommission = view.findViewById(R.id.shareCommission);
        textviewMonth = view.findViewById(R.id.textviewMonth);
        reportsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    fragment_container.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    layout_pie_chart.setVisibility(View.GONE);
                    layout_idBarChart.setVisibility(View.GONE);
                    shareCommission.setVisibility(View.GONE);
                }if(tab.getPosition()==1){
                    fragment_container.setVisibility(View.GONE);
                    layout_pie_chart.setVisibility(View.VISIBLE);
                    layout_idBarChart.setVisibility(View.GONE);
                    shareCommission.setVisibility(View.GONE);

                }
                if(tab.getPosition()==2){
                    fragment_container.setVisibility(View.GONE);
                    layout_pie_chart.setVisibility(View.GONE);
                    shareCommission.setVisibility(View.GONE);
                    layout_idBarChart.setVisibility(View.VISIBLE);

                }
                if(tab.getPosition()==3){
                    fragment_container.setVisibility(View.GONE);
                    layout_pie_chart.setVisibility(View.GONE);
                    shareCommission.setVisibility(View.VISIBLE);
                    layout_idBarChart.setVisibility(View.GONE);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        return view;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Activity a = getActivity();
        if(a != null){
            a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
}
