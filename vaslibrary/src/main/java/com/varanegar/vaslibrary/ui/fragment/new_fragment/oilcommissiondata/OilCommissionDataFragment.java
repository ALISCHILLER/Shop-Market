package com.varanegar.vaslibrary.ui.fragment.new_fragment.oilcommissiondata;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.evrencoskun.tableview.TableView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.tabs.TabLayout;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.newmanager.dealercommission.DealerCommissionDataManager;
import com.varanegar.vaslibrary.manager.newmanager.dealercommission.DealerCommissionDataModel;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.TableViewListener;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.model.Cell;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class OilCommissionDataFragment extends VaranegarFragment {
    private TableView mTableView;
    private TextView textViewDate,textViewDate_tabel,text_view_RankOnTeam
            ,text_view_RankOnGlobal,textviewMonth,txtReward;
    private LinearLayout layout_pie_chart,layout_idBarChart,header;
    private BarChart chart;
    private TabLayout reportsTabLayout;
    private RelativeLayout fragment_container;
    private PieChart pieChart;
    private ConstraintLayout shareCommission;
    private ProgressDialog productStockLevelProgressDialog;
    // variable for our bar chart
    BarChart barChart;
    // variable for our bar data set.
    BarDataSet barDataSet1, barDataSet2;
    // array list for storing entries.
    ArrayList barEntries;
    // creating a string array for displaying productGroup.
    String[] productGroup = new String[]{
            "روغن ذرت و ویتامینه",
            "روغن ترکیبی",
            "روغن ذرت و کنجد سرخ کردنی"};

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
                R.layout.layout_oilcommission_fragment, container, false);
        Activity a = getActivity();
        if(a != null){
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
        txtReward = view.findViewById(R.id.txtReward);
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

        pieChart = view.findViewById(R.id.pie_chart_c);

        Button buttonReport = view.findViewById(R.id.buttonReport);
        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealerCommissionDataManager dealerCommissionDataManager = new
                        DealerCommissionDataManager(requireContext());

                startProductStockLevelProgressDialog();
                dealerCommissionDataManager.sync(new UpdateCall() {
                    @Override
                    protected void onFinish() {
                        super.onFinish();
                        stopProductStockLevelProgressDialog();
                    }

                    @Override
                    protected void onSuccess() {
                        super.onSuccess();
                        initializeTableView();
                    }

                    @Override
                    protected void onFailure(String error) {
                        super.onFailure(error);
                        Toast.makeText(getContext(), "s"+error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onError(String error) {
                        super.onError(error);
                        Toast.makeText(getContext(), "s"+error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        initializeTableView();
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

    private void setChartSale(DealerCommissionDataModel dealerCommissionDataModel){
        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntriesOne(dealerCommissionDataModel), "هدف");
        barDataSet1.setColor(Color.BLUE);
        barDataSet2 = new BarDataSet(getBarEntriesTwo(dealerCommissionDataModel), "فروش");
        barDataSet2.setColor(Color.RED);

        // below line is to add bar data set to our bar data.
        BarData data = new BarData(barDataSet1, barDataSet2);
        data.setValueTextSize(23f);
        // after adding data to our bar data we
        // are setting that data to our bar chart.
        barChart.setData(data);

        // below line is to remove description
        // label of our bar chart.
        //  barChart.getDescription().setEnabled(false);

        // below line is to get x axis
        // of our bar chart.
        XAxis xAxis = barChart.getXAxis();

        // below line is to set value formatter to our x-axis and
        // we are adding our productGroup to our x axis.
        xAxis.setValueFormatter(new IndexAxisValueFormatter(productGroup));

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true);

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);
        // below line is to set granularity
        // to our x axis labels.
        //  xAxis.setGranularity(1);

        // below line is to enable
        // granularity to our x axis.
        // xAxis.setGranularityEnabled(true);

        // below line is to make our
        // bar chart as draggable.
        //  barChart.setDragEnabled(true);

        // below line is to make visible
        // range for our bar chart.
        //   barChart.setVisibleXRangeMaximum(2);

        // below line is to add bar
        // space to our chart.
        float barSpace = 0.1f;
        // below line is use to add group
        // spacing to our bar chart.
        float groupSpace = 0.5f;
        // we are setting width of
        // bar in below line.
        data.setBarWidth(0.15f);

        // below line is to set minimum
        // axis to our chart.
        barChart.getXAxis().setAxisMinimum(0);

        // below line is to
        // animate our chart.
        barChart.animate();

        // below line is to group bars
        // and add spacing to it.
        barChart.groupBars(0f, groupSpace, barSpace);

        // below line is to invalidate
        // our bar chart.
        barChart.invalidate();
    }

    private void initializeTableView() {
        // Create TableView View model class  to group view models of TableView
        TableViewModel
                tableViewModel = new TableViewModel();

        // Create TableView Adapter
        TableViewAdapter
                tableViewAdapter = new TableViewAdapter(tableViewModel);

        mTableView.setAdapter(tableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView));

        // Create an instance of a Filter and pass the TableView.
        //mTableFilter = new Filter(mTableView);

        // Load the dummy data to the TableView

        DealerCommissionDataModel d = new
                DealerCommissionDataManager(requireContext()).getAll();

        chart.setVisibility(View.GONE);
        if (d != null) {

            Currency c= new Currency(d.RewardOil);
            txtReward.setText("پاداش:"+c);
            if (d.LastUpdate != null) {
                textViewDate.setText("");
                textViewDate_tabel.setText("تاریخ آخرین بروزرسانی : " + d.LastUpdate);
            }else {
                textViewDate.setText("");
                textViewDate_tabel.setText("تاریخ آخرین بروزرسانی :");
            }

            if (d.RankOnTeam>0){
                text_view_RankOnTeam.setText("رتبه در تیم : " + d.RankOnTeam);
            } else {
                text_view_RankOnTeam.setText("رتبه در تیم:");
            }
            if (d.RankOnTeam>0){
                text_view_RankOnGlobal.setText("رتبه در کل کشور : " + d.RankOnGlobal);
            } else {
                text_view_RankOnGlobal.setText("رتبه در کل کشور:");
            }
            if (d.Month>0){
                textviewMonth.setText("ماه : " + d.Month);
            } else {
                textviewMonth.setText("ماه:");
            }

            if (d.oilSales_Corn!=0&& d.oilTarget_Corn!=0) {
                double f=((double) d.oilSales_Corn /(double) d.oilTarget_Corn) * 100;
                d.oilAchive_Corn = (int) f;
            }
            if (d.oilSales_Mix!=0&& d.oilTarget_Mix!=0) {
                double f = ((double) d.oilSales_Mix / (double) d.oilTarget_Mix) * 100;
                d.oilAchive_Mix = (int) f;
            }
            if (d.oilSales_Frying!=0&& d.oilTarget_Frying!=0) {
                double f = ((double) d.oilSales_Frying / (double) d.oilTarget_Frying) * 100;
                d.oilAchive_Frying =  (int) f;
            }
            mTableView.setShowCornerView(false);
            mTableView.setRowHeaderWidth(165);
            mTableView.setMinimumWidth(200);
            setChartSale(d);
            tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
                    .getRowHeaderList(), getCellListForSortingTest(d));
            setPieCharts(d);

            if (chart != null) {
                chart.setDrawBarShadow(false);
                chart.setDrawValueAboveBar(true);
                chart.getDescription().setEnabled(false);
                chart.setPinchZoom(false);
                chart.setDrawGridBackground(false);


                List<String> title = new ArrayList<>();
                title.add("روغن ذرت و ویتامینه");
                title.add("روغن ترکیبی");
                title.add("روغن ذرت و کنجد سرخ کردنی");

                ArrayList<BarEntry> values = new ArrayList<>();
                values.add(new BarEntry(0, d.oilAchive_Corn, "روغن ذرت و ویتامینه"));
                values.add(new BarEntry(1, d.oilAchive_Mix, "روغن ترکیبی"));
                values.add(new BarEntry(2, d.oilAchive_Frying , "روغن ذرت و کنجد سرخ کردنی"));

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f); // only intervals of 1 day
                xAxis.setLabelCount(8);
                xAxis.setTextSize(15f);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return title.get((int) value % title.size());
                    }
                });


                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setLabelCount(8, false);
                //leftAxis.setValueFormatter(custom);
                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                leftAxis.setSpaceTop(15f);
                leftAxis.setAxisMinimum(0f);

                YAxis rightAxis = chart.getAxisRight();

                rightAxis.setDrawGridLines(false);
                rightAxis.setLabelCount(8, false);

                //rightAxis.setValueFormatter(custom);
                rightAxis.setSpaceTop(15f);
                rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


                chart.getLegend().setEnabled(true);


                chart.setVisibility(View.VISIBLE);

                setData(values);

            }
        }

    }

    private List<List<Cell>> getCellListForSortingTest(DealerCommissionDataModel dealerCommissionDataModel) {


        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 1; j < 13; j++) {

                Cell cell;
                if (i == 0) {
                    if (j == 1) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.oilTarget_Corn)));
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.oilTarget_Mix)));
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.oilTarget_Frying)));
                        cellList.add(cell);
                    }  if (j == 4) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }if (j == 5) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }
                } else if (i == 1) {
                    if (j == 1) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.oilSales_Corn)));
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.oilSales_Mix)));
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1",HelperMethods.currencyToString(Currency
                                .valueOf( dealerCommissionDataModel.oilSales_Frying)));
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }if (j == 5) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }
                } else if (i == 2) {
                    if (j == 1) {
                        cell = new Cell("1", dealerCommissionDataModel.oilAchive_Corn + " %");
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", dealerCommissionDataModel.oilAchive_Mix + " %");
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", dealerCommissionDataModel.oilAchive_Frying + " %");
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }
                }else if (i==3){
                    if (j == 1) {
                        if (dealerCommissionDataModel.oilAchive_Corn >=100){
                            cell = new Cell("1", "0");
                            cellList.add(cell);
                        }else {
                            cell = new Cell("1", dealerCommissionDataModel.oilTarget_Corn -dealerCommissionDataModel.oilSales_Corn);
                            cellList.add(cell);
                        }
                    } if (j == 2) {
                        if (dealerCommissionDataModel.oilAchive_Mix >=100){
                            cell = new Cell("1", "0");
                            cellList.add(cell);
                        }else {
                            cell = new Cell("1", dealerCommissionDataModel.oilTarget_Mix -dealerCommissionDataModel.oilSales_Mix );
                            cellList.add(cell);
                        }
                    }
                    if (j == 3) {
                        if (dealerCommissionDataModel.oilAchive_Frying >=100){
                            cell = new Cell("1", "0");
                            cellList.add(cell);
                        }else {
                            cell = new Cell("1", dealerCommissionDataModel.oilTarget_Frying -dealerCommissionDataModel.oilSales_Frying );
                            cellList.add(cell);
                        }
                    }
                    if (j == 4) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1","");
                        cellList.add(cell);
                    }
                } else if (i == 4) {
                    if (j == 1) {
                        cell = new Cell("1",HelperMethods.currencyToString(Currency
                                .valueOf( dealerCommissionDataModel.oilPayment_Corn)));
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.oilPayment_Mix)));
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.oilPayment_Frying)));
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.coverageRatePaymentIOL)));
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.finalPayment_OIL)));
                        cellList.add(cell);
                    }
                }
            }
            list.add(cellList);
        }

        return list;
    }
    private ArrayList<BarEntry> getBarEntriesOne(DealerCommissionDataModel dealerCommissionDataModel) {

        // creating a new array list
        barEntries = new ArrayList<>();
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, dealerCommissionDataModel.oilTarget_Corn));
        barEntries.add(new BarEntry(2f, dealerCommissionDataModel.oilTarget_Mix));
        barEntries.add(new BarEntry(3f, dealerCommissionDataModel.oilTarget_Frying));
        return barEntries;
    }
    // array list for second set.
    private ArrayList<BarEntry> getBarEntriesTwo(DealerCommissionDataModel dealerCommissionDataModel) {

        // creating a new array list
        barEntries = new ArrayList<>();
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, dealerCommissionDataModel.oilSales_Corn));
        barEntries.add(new BarEntry(2f, dealerCommissionDataModel.oilSales_Mix));
        barEntries.add(new BarEntry(3f, dealerCommissionDataModel.oilSales_Frying));
        return barEntries;
    }


    private void setData(ArrayList<BarEntry> values) {

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setValueTextSize(23f);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "چارت درصد دستیابی");

            set1.setDrawIcons(false);

            List<Integer> colors = new ArrayList<>();
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_purple));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));

            set1.setColors(colors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return HelperMethods.convertToOrFromPersianDigits(value+"%");
                }
            });
            data.setValueTextSize(24f);
            data.setBarWidth(0.15f);
            chart.setData(data);
        }
    }
    private void setPieCharts( DealerCommissionDataModel dealerCommissionDataModel) {
        pieChart.setVisibility(View.VISIBLE);
        List<PieEntry> entries = new ArrayList<>();


        if (getMuth(dealerCommissionDataModel.oilPayment_Corn,dealerCommissionDataModel.finalPayment_CON)>0)
            entries.add(new PieEntry(100*dealerCommissionDataModel.oilPayment_Corn/dealerCommissionDataModel.finalPayment_CON,"روغن ذرت و ویتامینه"));
        if (getMuth(dealerCommissionDataModel.oilPayment_Mix,dealerCommissionDataModel.finalPayment_CON)>0)
            entries.add(new PieEntry((100*dealerCommissionDataModel.oilPayment_Mix/dealerCommissionDataModel.finalPayment_CON),"روغن ترکیبی"));
        if (getMuth(dealerCommissionDataModel.oilPayment_Frying,dealerCommissionDataModel.finalPayment_CON)>0)
            entries.add(new PieEntry((100*dealerCommissionDataModel.oilPayment_Frying/dealerCommissionDataModel.finalPayment_CON),"روغن ذرت و کنجد سرخ کردنی"));
        if (getMuth(dealerCommissionDataModel.coverageRatePaymentIOL,dealerCommissionDataModel.finalPayment_CON)>0)
            entries.add(new PieEntry((100*dealerCommissionDataModel.coverageRatePaymentIOL/dealerCommissionDataModel.finalPayment_CON),"CoverageRate"));
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setValueTextSize(20);
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return HelperMethods.convertToOrFromPersianDigits(value+"%");
            }
        });
        pieDataSet.setColors(
                getResources().getColor(R.color.pink)
                ,getResources().getColor(R.color.orange)
                ,getResources().getColor(R.color.blue)
                ,getResources().getColor(R.color.green)
                ,getResources().getColor(R.color.gradientOrange)
                ,getResources().getColor(R.color.gradientViolet)
                ,getResources().getColor(R.color.gradientLightOrange2)
                ,getResources().getColor(R.color.zarShop)
                ,getResources().getColor(R.color.gradientLightGreen)
        );
        Description description = new Description();
        description.setText("%");
        pieChart.setDescription(description);
        pieChart.animateY(1500);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);

    }
    public int getMuth(int i ,int y){
        if(i>0&&y>0)
            return 100*i/y;
        else
            return 0;
    }
    private void startProductStockLevelProgressDialog() {
        productStockLevelProgressDialog = new ProgressDialog(getActivity());
        productStockLevelProgressDialog.setMessage(requireActivity().getString(com.varanegar.vaslibrary.R.string.y_update));
        productStockLevelProgressDialog.setCancelable(false);
        productStockLevelProgressDialog.show();
    }

    private void stopProductStockLevelProgressDialog() {
        if (productStockLevelProgressDialog != null && productStockLevelProgressDialog.isShowing()) {
            try {
                productStockLevelProgressDialog.dismiss();
            } catch (Exception ignored) {

            }
        }
    }

}
