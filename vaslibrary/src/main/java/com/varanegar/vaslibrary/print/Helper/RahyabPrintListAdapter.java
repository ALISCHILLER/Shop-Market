package com.varanegar.vaslibrary.print.Helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.varanegar.vaslibrary.R;

import java.util.ArrayList;

public class RahyabPrintListAdapter extends BaseAdapter {
    private ArrayList<RahyabPrintModel> printModels;
    private Context context;

    public RahyabPrintListAdapter(Context context, ArrayList<RahyabPrintModel> printModels) {
        this.printModels = printModels;
        this.context = context;
    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.rahyab_print_item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.img = (ImageView) convertView.findViewById(R.id.img);
        holder.img.setImageDrawable(printModels.get(position).getImg());
        convertView.setTag(holder);
        return convertView;
    }
    @Override
    public int getCount() {
        return printModels.size();
    }

    @Override
    public Object getItem(int position) {
        return printModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return printModels.indexOf(getItem(position));
    }

    private static class ViewHolder {
        private ImageView img;
    }
}
