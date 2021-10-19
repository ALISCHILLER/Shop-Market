package com.varanegar.vaslibrary.print.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kishcore.sdk.hybrid.api.PrintableData;
import com.varanegar.vaslibrary.R;

import java.util.ArrayList;


public class RahyabTestPrintableDataList implements PrintableData {
    private ArrayList<RahyabPrintModel> items;

    public RahyabTestPrintableDataList(ArrayList<RahyabPrintModel> items) {
        this.items = items;
    }

    @Override
    public View toView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = null;
        if (inflater != null) {
            root = inflater.inflate(R.layout.rahyab_list_printable_data, null);
        }

        ListView listView = (ListView) root.findViewById(R.id.lv_test);
        listView.setAdapter(new RahyabPrintListAdapter(context, items));
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        listView.setLayoutParams(layoutParams);
        setListViewHeightBasedOnChildren(listView);
        return root;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    public static boolean setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }
    }
}
