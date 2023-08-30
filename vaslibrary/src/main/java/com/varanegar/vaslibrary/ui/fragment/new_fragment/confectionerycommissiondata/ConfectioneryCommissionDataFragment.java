package com.varanegar.vaslibrary.ui.fragment.new_fragment.confectionerycommissiondata;

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
import com.varanegar.vaslibrary.ui.fragment.new_fragment.confectionerycommissiondata.TableViewAdapter;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.TableViewListener;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.confectionerycommissiondata.TableViewModel;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.model.Cell;

import java.util.ArrayList;
import java.util.List;

public class ConfectioneryCommissionDataFragment extends VaranegarFragment {



    private TableView mTableView;
    private TextView textViewDate,textViewDate_tabel,text_view_RankOnTeam
            ,text_view_RankOnGlobal,textviewMonth,txtReward;
    private LinearLayout layout_pie_chart,layout_idBarChart,header;
    private BarChart chart;
    private TabLayout reportsTabLayout;
    private RelativeLayout fragment_container;
    private PieChart pieChart;
    private ProgressDialog productStockLevelProgressDialog;
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
        pieChart = (PieChart) view.findViewById(R.id.pie_chart_c);

        Button buttonReport = view.findViewById(R.id.buttonReport);
        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealerCommissionDataManager dealerCommissionDataManager = new
                        DealerCommissionDataManager(getContext());

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
    private void setChartSale(DealerCommissionDataModel d){
        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntriesOne(d), "هدف");
        barDataSet1.setColor(Color.BLUE);
        barDataSet2 = new BarDataSet(getBarEntriesTwo(d), "فروش");
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
        TableViewModel tableViewModel = new TableViewModel();

        // Create TableView Adapter
        TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);

        mTableView.setAdapter(tableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView));

        // Create an instance of a Filter and pass the TableView.
        //mTableFilter = new Filter(mTableView);

        // Load the dummy data to the TableView

        DealerCommissionDataModel d = new
                DealerCommissionDataManager(getContext()).getAll();

        chart.setVisibility(View.GONE);
        if (d != null) {
            Currency c= new Currency(d.RewardCon);
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




            if (d.waferSales !=0 && d.waferTarget!=0) {
                double f=((double) d.waferSales /(double) d.waferTarget)* 100;
                d.waferAchive = (int) f;
            }
            if (d.biscuitSales !=0 && d.biscuitTarget!=0) {
                double f = ((double) d.biscuitSales / (double) d.biscuitTarget) * 100;
                d.biscuitAchive = (int) f;
            }
            if (d.cakeSales !=0 && d.cakeTarget!=0) {
                double f = ((double) d.cakeSales / (double) d.cakeTarget) * 100;
                d.cakeAchive =(int) f;
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
                title.add("وییفر");
                title.add("کیک");
                title.add("بیسکوئیت");

                ArrayList<BarEntry> values = new ArrayList<>();
                values.add(new BarEntry(0, d.waferAchive, "وییفر"));
                values.add(new BarEntry(1, d.cakeAchive, "کیک"));
                values.add(new BarEntry(2, d.biscuitAchive , "بیسکوئیت"));

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

    private List<List<Cell>> getCellListForSortingTest(DealerCommissionDataModel d) {

        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 1; j < 13; j++) {

                Cell cell;
                if (i == 0) {
                    if (j == 1) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(d.waferTarget)));
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", HelperMethods.currencyToString(Currency
                                .valueOf(d.cakeTarget)));
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(d.biscuitTarget)));
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
                                .valueOf(d.waferSales)));
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", HelperMethods.currencyToString(Currency
                                .valueOf(d.cakeSales)));
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1",HelperMethods.currencyToString(Currency
                                .valueOf( d.biscuitSales)));
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
                        cell = new Cell("1", d.waferAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", d.cakeAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", d.biscuitAchive + " %");
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
                        if (d.waferAchive >=100){
                            cell = new Cell("1", "0");
                            cellList.add(cell);
                        }else {
                            cell = new Cell("1", d.waferTarget -d.waferSales);
                            cellList.add(cell);
                        }
                    } if (j == 2) {
                        if (d.cakeAchive >=100){
                            cell = new Cell("1", "0");
                            cellList.add(cell);
                        }else {
                            cell = new Cell("1", d.cakeTarget -d.cakeSales );
                            cellList.add(cell);
                        }
                    }
                    if (j == 3) {
                        if (d.biscuitAchive >=100){
                            cell = new Cell("1", "0");
                            cellList.add(cell);
                        }else {
                            cell = new Cell("1", d.biscuitTarget -d.biscuitSales );
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
                                .valueOf( d.waferPayment)));
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", HelperMethods.currencyToString(Currency
                                .valueOf(d.cakePayment)));
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(d.biscuitPayment)));
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(d.coverageRatePaymentCON)));
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(d.finalPayment_CON)));
                        cellList.add(cell);
                    }
                }
            }
            list.add(cellList);
        }

        return list;
    }
    private ArrayList<BarEntry> getBarEntriesOne(DealerCommissionDataModel d) {

        // creating a new array list
        barEntries = new ArrayList<>();
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, d.waferTarget));
        barEntries.add(new BarEntry(2f, d.cakeTarget));
        barEntries.add(new BarEntry(3f, d.biscuitTarget));
        return barEntries;
    }
    // array list for second set.
    private ArrayList<BarEntry> getBarEntriesTwo(DealerCommissionDataModel d) {

        // creating a new array list
        barEntries = new ArrayList<>();
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, d.waferSales));
        barEntries.add(new BarEntry(2f, d.cakeSales));
        barEntries.add(new BarEntry(3f, d.biscuitSales));
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
    private void setPieCharts( DealerCommissionDataModel d) {
        pieChart.setVisibility(View.VISIBLE);
        List<PieEntry> entries = new ArrayList<>();


        if (getMuth(d.waferPayment,d.finalPayment_CON)>0)
            entries.add(new PieEntry(100*d.waferPayment/d.finalPayment_CON,"ویفر"));
        if (getMuth(d.cakePayment,d.finalPayment_CON)>0)
            entries.add(new PieEntry((100*d.cakePayment/d.finalPayment_CON),"کیک"));
        if (getMuth(d.biscuitPayment,d.finalPayment_CON)>0)
            entries.add(new PieEntry((100*d.biscuitPayment/d.finalPayment_CON),"بیسکویت"));
        if (getMuth(d.coverageRatePaymentCON,d.finalPayment_CON)>0)
            entries.add(new PieEntry((100*d.coverageRatePaymentCON/d.finalPayment_CON),"CoverageRate"));
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
