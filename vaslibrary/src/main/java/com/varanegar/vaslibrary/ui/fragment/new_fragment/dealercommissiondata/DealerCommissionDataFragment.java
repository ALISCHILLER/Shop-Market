package com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.evrencoskun.tableview.TableView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.TableViewAdapter;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.TableViewListener;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.TableViewModel;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata.tabelview.model.Cell;
import java.util.ArrayList;
import java.util.List;

public class DealerCommissionDataFragment extends VaranegarFragment {
    private TableView mTableView;
    private TextView  textViewDate;
    private LinearLayout layout_pie_chart,layout_idBarChart;
    private BarChart chart;
    private TabLayout reportsTabLayout;
    private RelativeLayout fragment_container;
    // variable for our bar chart
    BarChart barChart;
    // variable for our bar data set.
    BarDataSet barDataSet1, barDataSet2;
    // array list for storing entries.
    ArrayList barEntries;
    // creating a string array for displaying productGroup.
    String[] productGroup = new String[]{"رشته ای", "لازانیا", "آشیانه",  "جامبو", "پودرکیک", "" +
            "آرد","فرمی","رشته آش"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.layout_dealercommission_fragment, container, false);
        textViewDate = view.findViewById(R.id.textViewDate);
        layout_pie_chart=view.findViewById(R.id.layout_pie_chart);
        layout_idBarChart=view.findViewById(R.id.layout_idBarChart);
        chart = (BarChart) view.findViewById(R.id.pie_chart);
        // Let's get TableView
        mTableView = view.findViewById(R.id.tableview);
        fragment_container = view.findViewById(R.id.fragment_container);
        barChart = view.findViewById(R.id.idBarChart);
        reportsTabLayout = view.findViewById(R.id.reports_tab_layout);
        layout_pie_chart = view.findViewById(R.id.layout_pie_chart);
        layout_idBarChart = view.findViewById(R.id.layout_idBarChart);

        reportsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    fragment_container.setVisibility(View.VISIBLE);
                    layout_pie_chart.setVisibility(View.GONE);
                    layout_idBarChart.setVisibility(View.GONE);
                }if(tab.getPosition()==1){
                    fragment_container.setVisibility(View.GONE);
                    layout_pie_chart.setVisibility(View.VISIBLE);
                    layout_idBarChart.setVisibility(View.GONE);
                }
                if(tab.getPosition()==2){
                    fragment_container.setVisibility(View.GONE);
                    layout_pie_chart.setVisibility(View.GONE);
                    layout_idBarChart.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        initializeTableView();
        // initializing variable for bar chart.

        return view;
    }

    private void setChartSale( DealerCommissionDataModel dealerCommissionDataModel){
        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntriesOne(dealerCommissionDataModel), "هدف");
        barDataSet1.setColor(getContext().getApplicationContext().getResources().getColor(R.color.blue_normal));
        barDataSet2 = new BarDataSet(getBarEntriesTwo(dealerCommissionDataModel), "فروش");
        barDataSet2.setColor(Color.BLUE);

        // below line is to add bar data set to our bar data.
        BarData data = new BarData(barDataSet1, barDataSet2);

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

        DealerCommissionDataModel dealerCommissionDataModel = new
                DealerCommissionDataManager(getContext()).getAll();

        chart.setVisibility(View.GONE);
        if (dealerCommissionDataModel != null) {

            if (dealerCommissionDataModel.LastUpdate != null)
                textViewDate.setText("تاریخ آخرین بروزرسانی : " + dealerCommissionDataModel.LastUpdate);
            else
                textViewDate.setText("");
            mTableView.setShowCornerView(false);
            mTableView.setRowHeaderWidth(165);
            mTableView.setMinimumWidth(200);
            setChartSale(dealerCommissionDataModel);
            tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
                    .getRowHeaderList(), getCellListForSortingTest(dealerCommissionDataModel));


            if (chart != null) {
                chart.setDrawBarShadow(false);
                chart.setDrawValueAboveBar(true);
                chart.getDescription().setEnabled(false);
                chart.setPinchZoom(false);
                chart.setDrawGridBackground(false);


                List<String> title = new ArrayList<>();
                title.add("رشته ای");
                title.add("لازانیا");
                title.add("آشیانه");
                title.add("جامبو");
                title.add("پودرکیک");
                title.add("آرد");
                title.add("فرمی");
                title.add("رشته آش");

                ArrayList<BarEntry> values = new ArrayList<>();
                values.add(new BarEntry(0, dealerCommissionDataModel.SpaghettiAchive, "رشته ای"));
                values.add(new BarEntry(1, dealerCommissionDataModel.LasagnaAchive, "لازانیا"));
                values.add(new BarEntry(2, dealerCommissionDataModel.NestAchive, "آشیانه"));
                values.add(new BarEntry(3, dealerCommissionDataModel.JumboAchive, "جامبو"));
                values.add(new BarEntry(4, dealerCommissionDataModel.CakePowderAchive, "پودرکیک"));
                values.add(new BarEntry(5, dealerCommissionDataModel.FlourAchive, " آرد"));
                values.add(new BarEntry(6, dealerCommissionDataModel.ShapedAchive, " فرمی"));
                values.add(new BarEntry(7, dealerCommissionDataModel.PottageAchive, " رشته آش"));

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f); // only intervals of 1 day
                xAxis.setLabelCount(8);
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


                chart.getLegend().setEnabled(false);


                chart.setVisibility(View.VISIBLE);
                setData(values);

            }
        }

    }
    private void setData(ArrayList<BarEntry> values) {

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "The year 2017");

            set1.setDrawIcons(false);

            List<Integer> colors = new ArrayList<>();
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_purple));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light));
            colors.add(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));

            set1.setColors(colors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }
    private List<List<Cell>> getCellListForSortingTest(DealerCommissionDataModel dealerCommissionDataModel) {

        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 1; j < 13; j++) {

                Cell cell;
                if (i == 0) {
                    if (j == 1) {
                        cell = new Cell("1", dealerCommissionDataModel.SpaghettiTarget);
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", dealerCommissionDataModel.LasagnaTarget);
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", dealerCommissionDataModel.NestTarget);
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1", dealerCommissionDataModel.JumboTarget);
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1", dealerCommissionDataModel.CakePowderTarget);
                        cellList.add(cell);
                    }
                    if (j == 6) {
                        cell = new Cell("1", dealerCommissionDataModel.FlourTarget);
                        cellList.add(cell);
                    }
                    if (j == 7) {
                        cell = new Cell("1", dealerCommissionDataModel.ShapedTarget);
                        cellList.add(cell);
                    }
                    if (j == 8) {
                        cell = new Cell("1", dealerCommissionDataModel.PottageTarget);
                        cellList.add(cell);
                    }
                    if (j == 9) {
                        cell = new Cell("1", "");
                        cellList.add(cell);
                    }
                    if (j == 10) {
                        cell = new Cell("2", "");
                        cellList.add(cell);
                    }
                    if (j == 11) {
                        cell = new Cell("1", "");
                        cellList.add(cell);
                    }
                    if (j == 12) {
                        cell = new Cell("1", dealerCommissionDataModel.FinalTarget);
                        cellList.add(cell);
                    }

                } else if (i == 1) {
                    if (j == 1) {
                        cell = new Cell("1", dealerCommissionDataModel.SpaghettiSales);
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", dealerCommissionDataModel.LasagnaSales);
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", dealerCommissionDataModel.NestSales);
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1", dealerCommissionDataModel.JumboSales);
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1", dealerCommissionDataModel.CakePowderSales);
                        cellList.add(cell);
                    }
                    if (j == 6) {
                        cell = new Cell("1", dealerCommissionDataModel.FlourSales);
                        cellList.add(cell);
                    }
                    if (j == 7) {
                        cell = new Cell("1", dealerCommissionDataModel.ShapedSales);
                        cellList.add(cell);
                    }
                    if (j == 8) {
                        cell = new Cell("1", dealerCommissionDataModel.PottageSales);
                        cellList.add(cell);
                    }


                    if (j == 9) {
                        cell = new Cell("1", "");
                        cellList.add(cell);
                    }
                    if (j == 10) {
                        cell = new Cell("2", "");
                        cellList.add(cell);
                    }
                    if (j == 11) {
                        cell = new Cell("1", "");
                        cellList.add(cell);
                    }
                    if (j == 12) {
                        cell = new Cell("1", dealerCommissionDataModel.FinalSales);
                        cellList.add(cell);
                    }

                } else if (i == 2) {
                    if (j == 1) {
                        cell = new Cell("1", dealerCommissionDataModel.SpaghettiAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", dealerCommissionDataModel.LasagnaAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", dealerCommissionDataModel.NestAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1", dealerCommissionDataModel.JumboAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1", dealerCommissionDataModel.CakePowderAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 6) {
                        cell = new Cell("1", dealerCommissionDataModel.FlourAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 7) {
                        cell = new Cell("1", dealerCommissionDataModel.ShapedAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 8) {
                        cell = new Cell("1", dealerCommissionDataModel.PottageAchive + " %");
                        cellList.add(cell);
                    }
                    if (j == 9) {
                        cell = new Cell("1", "");
                        cellList.add(cell);
                    }
                    if (j == 10) {
                        cell = new Cell("2", "");
                        cellList.add(cell);
                    }
                    if (j == 11) {
                        cell = new Cell("1", "");
                        cellList.add(cell);
                    }
                    if (j == 12) {
                        cell = new Cell("1", dealerCommissionDataModel.FinalAchive + " %");
                        cellList.add(cell);
                    }
                } else if (i == 3) {
                    if (j == 1) {
                        cell = new Cell("1", dealerCommissionDataModel.SpaghettiPayment);
                        cellList.add(cell);
                    }
                    if (j == 2) {
                        cell = new Cell("2", dealerCommissionDataModel.LasagnaPayment);
                        cellList.add(cell);
                    }
                    if (j == 3) {
                        cell = new Cell("1", dealerCommissionDataModel.NestPayment);
                        cellList.add(cell);
                    }
                    if (j == 4) {
                        cell = new Cell("1", dealerCommissionDataModel.JumboTarget);
                        cellList.add(cell);
                    }
                    if (j == 5) {
                        cell = new Cell("1", dealerCommissionDataModel.CakePowderTarget);
                        cellList.add(cell);
                    }
                    if (j == 6) {
                        cell = new Cell("1", dealerCommissionDataModel.FlourPayment);
                        cellList.add(cell);
                    }
                    if (j == 7) {
                        cell = new Cell("1", dealerCommissionDataModel.SpaghettiPayment);
                        cellList.add(cell);
                    }
                    if (j == 8) {
                        cell = new Cell("1", dealerCommissionDataModel.PottagePayment);
                        cellList.add(cell);
                    }

                    if (j == 9) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.CoverageRatePayment)));
                        cellList.add(cell);
                    }
                    if (j == 10) {
                        cell = new Cell("2", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.HitRatePayment)));
                        cellList.add(cell);
                    }
                    if (j == 11) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.LpscPayment)));
                        cellList.add(cell);
                    }
                    if (j == 12) {
                        cell = new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.FinalPayment)));
                        cellList.add(cell);
                    }
                }
            }
            list.add(cellList);
        }

        return list;
    }
    // array list for first set
    private ArrayList<BarEntry> getBarEntriesOne(DealerCommissionDataModel dealerCommissionDataModel) {

        // creating a new array list
        barEntries = new ArrayList<>();
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, dealerCommissionDataModel.SpaghettiTarget));
        barEntries.add(new BarEntry(2f, dealerCommissionDataModel.LasagnaTarget));
        barEntries.add(new BarEntry(3f, dealerCommissionDataModel.NestTarget));
        barEntries.add(new BarEntry(4f, dealerCommissionDataModel.JumboTarget));
        barEntries.add(new BarEntry(5f, dealerCommissionDataModel.CakePowderTarget));
        barEntries.add(new BarEntry(6f, dealerCommissionDataModel.FlourTarget));
        barEntries.add(new BarEntry(7f, dealerCommissionDataModel.ShapedTarget));
        barEntries.add(new BarEntry(8f, dealerCommissionDataModel.PottageTarget));
        return barEntries;
    }
    // array list for second set.
    private ArrayList<BarEntry> getBarEntriesTwo(DealerCommissionDataModel dealerCommissionDataModel) {

        // creating a new array list
        barEntries = new ArrayList<>();
        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, dealerCommissionDataModel.SpaghettiSales));
        barEntries.add(new BarEntry(2f, dealerCommissionDataModel.LasagnaSales));
        barEntries.add(new BarEntry(3f, dealerCommissionDataModel.NestSales));
        barEntries.add(new BarEntry(4f, dealerCommissionDataModel.JumboSales));
        barEntries.add(new BarEntry(5f, dealerCommissionDataModel.CakePowderSales));
        barEntries.add(new BarEntry(6f, dealerCommissionDataModel.FlourSales));
        barEntries.add(new BarEntry(7f, dealerCommissionDataModel.ShapedSales));
        barEntries.add(new BarEntry(8f, dealerCommissionDataModel.PottageSales));
        return barEntries;
    }
}
