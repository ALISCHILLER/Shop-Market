package com.varanegar.vaslibrary.ui.fragment.new_fragment.dealercommissiondata;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.filter.Filter;
import com.evrencoskun.tableview.pagination.Pagination;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.varanegar.framework.base.VaranegarApplication;
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
    private Spinner moodFilter, genderFilter;
    private ImageButton previousButton, nextButton;
    private TextView tablePaginationDetails;
    private PieChart pieChart;
    @Nullable
    private Filter mTableFilter; // This is used for filtering the table.
    @Nullable
    private Pagination mPagination; // This is used for paginating the table.

    private boolean mPaginationEnabled = false;
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
                R.layout.layout_dealercommission_fragment, container, false);
        EditText searchField = view.findViewById(R.id.query_string);
        searchField.addTextChangedListener(mSearchTextWatcher);

        moodFilter = view.findViewById(R.id.mood_spinner);
        moodFilter.setOnItemSelectedListener(mItemSelectionListener);

        genderFilter = view.findViewById(R.id.gender_spinner);
        genderFilter.setOnItemSelectedListener(mItemSelectionListener);

        Spinner itemsPerPage = view.findViewById(R.id.items_per_page_spinner);

        View tableTestContainer = view.findViewById(R.id.table_test_container);

        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);
        EditText pageNumberField = view.findViewById(R.id.page_number_text);
        tablePaginationDetails = view.findViewById(R.id.table_details);
         pieChart = (PieChart) view.findViewById(R.id.pie_chart);
        if (mPaginationEnabled) {
            tableTestContainer.setVisibility(View.VISIBLE);
            itemsPerPage.setOnItemSelectedListener(onItemsPerPageSelectedListener);

            previousButton.setOnClickListener(mClickListener);
            nextButton.setOnClickListener(mClickListener);
            pageNumberField.addTextChangedListener(onPageTextChanged);
        } else {
            tableTestContainer.setVisibility(View.GONE);
        }

        // Let's get TableView
        mTableView = view.findViewById(R.id.tableview);

        initializeTableView();

        if (mPaginationEnabled) {
            mTableFilter = new Filter(mTableView); // Create an instance of a Filter and pass the
            // created TableView.

            // Create an instance for the TableView pagination and pass the created TableView.
            mPagination = new Pagination(mTableView);

            // Sets the pagination listener of the TableView pagination to handle
            // pagination actions. See onTableViewPageTurnedListener variable declaration below.
            mPagination.setOnTableViewPageTurnedListener(onTableViewPageTurnedListener);
        }
        return view;
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

        DealerCommissionDataModel dealerCommissionDataModel= new
                DealerCommissionDataManager(getContext()).getAll();


        if (dealerCommissionDataModel!=null) {
            mTableView.setShowCornerView(false);
            mTableView.setRowHeaderWidth(165);

            tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(), tableViewModel
                    .getRowHeaderList(), getCellListForSortingTest(dealerCommissionDataModel));


            if (pieChart != null) {
                pieChart.setVisibility(View.VISIBLE);
                List<PieEntry> entries = new ArrayList<>();
                    entries.add(new PieEntry(dealerCommissionDataModel.SpaghettiAchive,"رشته ای"));
                    entries.add(new PieEntry(dealerCommissionDataModel.LasagnaAchive, "لازانیا"));
                    entries.add(new PieEntry(dealerCommissionDataModel.NestAchive, "آشیانه"));
                    entries.add(new PieEntry(dealerCommissionDataModel.JumboAchive, "جامبو"));
                    entries.add(new PieEntry(dealerCommissionDataModel.CakePowderAchive, "پودرکیک"));
                    entries.add(new PieEntry(dealerCommissionDataModel.FlourAchive, " آرد"));
                    entries.add(new PieEntry(dealerCommissionDataModel.ShapedAchive, " فرمی"));
                    entries.add(new PieEntry(dealerCommissionDataModel.PottageAchive, " رشته آش"));

                PieDataSet pieDataSet = new PieDataSet(entries, "");
                pieDataSet.setValueTextSize(20);
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                Description description = new Description();
                description.setText("");
                pieChart.setDescription(description);
                pieChart.animateY(1500);
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                Legend l = pieChart.getLegend();
                l.setTextSize(15.00f);
                l.setMaxSizePercent(12);
            }
        }
        //mTableView.setHasFixedWidth(true);

        /*for (int i = 0; i < mTableViewModel.getCellList().size(); i++) {
            mTableView.setColumnWidth(i, 200);
        }*)

        //mTableView.setColumnWidth(0, -2);
        //mTableView.setColumnWidth(1, -2);

        /*mTableView.setColumnWidth(2, 200);
        mTableView.setColumnWidth(3, 300);
        mTableView.setColumnWidth(4, 400);
        mTableView.setColumnWidth(5, 500);*/

    }

    private List<List<Cell>> getCellListForSortingTest(DealerCommissionDataModel dealerCommissionDataModel) {

        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 1; j < 13; j++) {
                
                Cell cell;
                if (i==0){
                    if (j==1){
                        cell=new Cell("1",dealerCommissionDataModel.SpaghettiTarget);
                        cellList.add(cell);
                    }if (j==2){
                        cell=new Cell("2",dealerCommissionDataModel.LasagnaTarget);
                        cellList.add(cell);
                    } if (j==3){
                        cell=new Cell("1",dealerCommissionDataModel.NestTarget);
                        cellList.add(cell);
                    }
                    if (j==4){
                        cell=new Cell("1",dealerCommissionDataModel.JumboTarget);
                        cellList.add(cell);
                    }
                    if (j==5){
                        cell=new Cell("1",dealerCommissionDataModel.CakePowderTarget);
                        cellList.add(cell);
                    }
                    if (j==6){
                        cell=new Cell("1",dealerCommissionDataModel.FlourTarget);
                        cellList.add(cell);
                    }
                    if (j==7){
                        cell=new Cell("1",dealerCommissionDataModel.ShapedTarget);
                        cellList.add(cell);
                    }
                    if (j==8){
                        cell=new Cell("1",dealerCommissionDataModel.PottageTarget);
                        cellList.add(cell);
                    }
                    if (j==9){
                        cell=new Cell("1","");
                        cellList.add(cell);
                    }if (j==10){
                        cell=new Cell("2","");
                        cellList.add(cell);
                    } if (j==11){
                        cell=new Cell("1","");
                        cellList.add(cell);
                    }
                    if (j==12){
                        cell=new Cell("1",dealerCommissionDataModel.FinalTarget);
                        cellList.add(cell);
                    }

                }
                else if (i==1){
                    if (j==1){
                        cell=new Cell("1",dealerCommissionDataModel.SpaghettiSales);
                        cellList.add(cell);
                    }if (j==2){
                        cell=new Cell("2",dealerCommissionDataModel.LasagnaSales);
                        cellList.add(cell);
                    } if (j==3){
                        cell=new Cell("1",dealerCommissionDataModel.NestSales);
                        cellList.add(cell);
                    }
                    if (j==4){
                        cell=new Cell("1",dealerCommissionDataModel.JumboSales);
                        cellList.add(cell);
                    }
                    if (j==5){
                        cell=new Cell("1",dealerCommissionDataModel.CakePowderSales);
                        cellList.add(cell);
                    }
                    if (j==6){
                        cell=new Cell("1",dealerCommissionDataModel.FlourSales);
                        cellList.add(cell);
                    }
                    if (j==7){
                        cell=new Cell("1",dealerCommissionDataModel.ShapedSales);
                        cellList.add(cell);
                    }
                    if (j==8){
                        cell=new Cell("1",dealerCommissionDataModel.PottageSales);
                        cellList.add(cell);
                    }



                    if (j==9){
                        cell=new Cell("1","");
                        cellList.add(cell);
                    }if (j==10){
                        cell=new Cell("2","");
                        cellList.add(cell);
                    } if (j==11){
                        cell=new Cell("1","");
                        cellList.add(cell);
                    }
                    if (j==12){
                        cell=new Cell("1",dealerCommissionDataModel.FinalSales);
                        cellList.add(cell);
                    }

                }
                else if (i==2){
                    if (j==1){
                        cell=new Cell("1",dealerCommissionDataModel.SpaghettiAchive);
                        cellList.add(cell);
                    }if (j==2){
                        cell=new Cell("2",dealerCommissionDataModel.LasagnaAchive);
                        cellList.add(cell);
                    } if (j==3){
                        cell=new Cell("1",dealerCommissionDataModel.NestAchive);
                        cellList.add(cell);
                    }
                    if (j==4){
                        cell=new Cell("1",dealerCommissionDataModel.JumboAchive);
                        cellList.add(cell);
                    }
                    if (j==5){
                        cell=new Cell("1",dealerCommissionDataModel.CakePowderAchive);
                        cellList.add(cell);
                    }
                    if (j==6){
                        cell=new Cell("1",dealerCommissionDataModel.FlourAchive);
                        cellList.add(cell);
                    }
                    if (j==7){
                        cell=new Cell("1",dealerCommissionDataModel.ShapedAchive);
                        cellList.add(cell);
                    }
                    if (j==8){
                        cell=new Cell("1",dealerCommissionDataModel.PottageAchive);
                        cellList.add(cell);
                    }
                    if (j==9){
                        cell=new Cell("1","");
                        cellList.add(cell);
                    }if (j==10){
                        cell=new Cell("2","");
                        cellList.add(cell);
                    } if (j==11){
                        cell=new Cell("1","");
                        cellList.add(cell);
                    }
                    if (j==12){
                        cell=new Cell("1",dealerCommissionDataModel.FinalAchive);
                        cellList.add(cell);
                    }
                }
                else if (i==3){
                    if (j==1){
                        cell=new Cell("1",dealerCommissionDataModel.SpaghettiPayment);
                        cellList.add(cell);
                    }if (j==2){
                        cell=new Cell("2",dealerCommissionDataModel.LasagnaPayment);
                        cellList.add(cell);
                    } if (j==3){
                        cell=new Cell("1",dealerCommissionDataModel.NestPayment);
                        cellList.add(cell);
                    }
                    if (j==4){
                        cell=new Cell("1",dealerCommissionDataModel.JumboTarget);
                        cellList.add(cell);
                    }
                    if (j==5){
                        cell=new Cell("1",dealerCommissionDataModel.CakePowderTarget);
                        cellList.add(cell);
                    }
                    if (j==6){
                        cell=new Cell("1",dealerCommissionDataModel.FlourPayment);
                        cellList.add(cell);
                    }
                    if (j==7){
                        cell=new Cell("1",dealerCommissionDataModel.SpaghettiPayment);
                        cellList.add(cell);
                    }
                    if (j==8){
                        cell=new Cell("1",dealerCommissionDataModel.PottagePayment);
                        cellList.add(cell);
                    }

                    if (j==9){
                        cell=new Cell("1", HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.CoverageRatePayment)));
                        cellList.add(cell);
                    }if (j==10){
                        cell=new Cell("2",HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.HitRatePayment)));
                        cellList.add(cell);
                    } if (j==11){
                        cell=new Cell("1",HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.LpscPayment)));
                        cellList.add(cell);
                    }
                    if (j==12){
                        cell=new Cell("1",HelperMethods.currencyToString(Currency
                                .valueOf(dealerCommissionDataModel.FinalPayment)));
                        cellList.add(cell);
                    }
                }
            }
            list.add(cellList);
        }

        return list;
    }

    public void filterTable(@NonNull String filter) {
        // Sets a filter to the table, this will filter ALL the columns.
        if (mTableFilter != null) {
            mTableFilter.set(filter);
        }
    }

    public void filterTableForMood(@NonNull String filter) {
        // Sets a filter to the table, this will only filter a specific column.
        // In the example data, this will filter the mood column.
        if (mTableFilter != null) {
            mTableFilter.set(TableViewModel.MOOD_COLUMN_INDEX, filter);
        }
    }

    public void filterTableForGender(@NonNull String filter) {
        // Sets a filter to the table, this will only filter a specific column.
        // In the example data, this will filter the gender column.
        if (mTableFilter != null) {
            mTableFilter.set(TableViewModel.GENDER_COLUMN_INDEX, filter);
        }
    }

    // The following four methods below: nextTablePage(), previousTablePage(),
    // goToTablePage(int page) and setTableItemsPerPage(int itemsPerPage)
    // are for controlling the TableView pagination.
    public void nextTablePage() {
        if (mPagination != null) {
            mPagination.nextPage();
        }
    }

    public void previousTablePage() {
        if (mPagination != null) {
            mPagination.previousPage();
        }
    }

    public void goToTablePage(int page) {
        if (mPagination != null) {
            mPagination.goToPage(page);
        }
    }

    public void setTableItemsPerPage(int itemsPerPage) {
        if (mPagination != null) {
            mPagination.setItemsPerPage(itemsPerPage);
        }
    }

    // Handler for the changing of pages in the paginated TableView.
    @NonNull
    private final Pagination.OnTableViewPageTurnedListener onTableViewPageTurnedListener = new
            Pagination.OnTableViewPageTurnedListener() {
                @Override
                public void onPageTurned(int numItems, int itemsStart, int itemsEnd) {
                    int currentPage = mPagination.getCurrentPage();
                    int pageCount = mPagination.getPageCount();
                    previousButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);

                    if (currentPage == 1 && pageCount == 1) {
                        previousButton.setVisibility(View.INVISIBLE);
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == 1) {
                        previousButton.setVisibility(View.INVISIBLE);
                    }

                    if (currentPage == pageCount) {
                        nextButton.setVisibility(View.INVISIBLE);
                    }

                    tablePaginationDetails.setText(getString(R.string.table_pagination_details, String
                            .valueOf(currentPage), String.valueOf(itemsStart), String.valueOf(itemsEnd)));

                }
            };

    @NonNull
    private final AdapterView.OnItemSelectedListener mItemSelectionListener = new AdapterView
            .OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // 0. index is for empty item of spinner.
            if (position > 0) {

                String filter = Integer.toString(position);

                if (parent == moodFilter) {
                    filterTableForMood(filter);
                } else if (parent == genderFilter) {
                    filterTableForGender(filter);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Left empty intentionally.
        }
    };

    @NonNull
    private final TextWatcher mSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            filterTable(String.valueOf(s));
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @NonNull
    private final AdapterView.OnItemSelectedListener onItemsPerPageSelectedListener = new AdapterView
            .OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int itemsPerPage;
            if ("All".equals(parent.getItemAtPosition(position).toString())) {
                itemsPerPage = 0;
            } else {
                itemsPerPage = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            setTableItemsPerPage(itemsPerPage);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @NonNull
    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == previousButton) {
                previousTablePage();
            } else if (v == nextButton) {
                nextTablePage();
            }
        }
    };

    @NonNull
    private final TextWatcher onPageTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int page;
            if (TextUtils.isEmpty(s)) {
                page = 1;
            } else {
                page = Integer.parseInt(String.valueOf(s));
            }

            goToTablePage(page);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


}
