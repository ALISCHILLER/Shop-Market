package com.varanegar.framework.util.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.util.HelperMethods;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by atp on 12/26/2016.
 */
public class ReportColumn<T extends BaseModel> {
    private boolean sortable;
    private boolean filterable;
    private boolean total;

    Object value;
    String title;
    Field field;
    ModelProjection<T> projection;

    public String toString() {
        return title;
    }

    public String getTag() {
        return field.getName() + title + String.valueOf(isCustomView());
    }

    public float getWeight() {
        return weight;
    }

    private float weight = 1;
    private float defaultWidth = 120;
    private int gravity = Gravity.CENTER;
    private boolean detail;
    private CustomViewHolder<T> customViewHolder;
    private CustomTotalView<T> customTotalView;
    private boolean frizzed;

    @NonNull
    public String getSuffix() {
        return suffix == null ? "" : suffix;
    }

    private String suffix;

    public String getDecimalFormat() {
        return decimaFormat;
    }

    private String decimaFormat;

    public boolean isDetail() {
        return detail;
    }

    public boolean isFrizzed() {
        return frizzed;
    }

    public boolean isFilterable() {
        return filterable && !projection.getName().equals("row");
    }

    public boolean isSortable() {
        return sortable && !projection.getName().equals("row");
    }

    public boolean isTotal() {
        return total;
    }

    public ReportColumn(T entity, ModelProjection<T> map, String title) throws NoSuchFieldException, IllegalAccessException {
        String fullName = map.getName();
        String name = fullName;
        if (fullName.contains("."))
            name = fullName.substring(fullName.indexOf(".") + 1, fullName.length());
        Field field = entity.getClass().getField(name);
        Object value = field.get(entity);
        projection = map;
        this.value = value;
        this.field = field;
        if (title != null)
            this.title = title;
        else
            this.title = "";
    }


    public ReportColumn<T> sendToDetail() {
        this.detail = true;
        return this;
    }

    public ReportColumn<T> setSortable() {
        this.sortable = true;
        return this;
    }

    public ReportColumn<T> setFrizzed() {
        this.frizzed = true;
        return this;
    }

    public ReportColumn<T> setFilterable() {
        this.filterable = true;
        return this;
    }

    public ReportColumn<T> calcTotal() {
        this.total = true;
        return this;
    }

    public ReportColumn<T> setWeight(float weight) {
        this.weight = weight;
        return this;
    }

    public ReportColumn<T> setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public ReportColumn<T> setCustomViewHolder(CustomViewHolder<T> customViewHolder) {
        this.customViewHolder = customViewHolder;
        return this;
    }

    public ReportColumn<T> setCustomTotalView(CustomTotalView<T> customTotalView) {
        this.customTotalView = customTotalView;
        return this;
    }

    public View createHeaderView(AppCompatActivity activity) {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_report_header_item, null, false);
        LinearLayout.LayoutParams lp;
        if (projection.getName().equals("Row.row")) {
            lp = new LinearLayout.LayoutParams(getDp(activity, 56), ViewGroup.LayoutParams.WRAP_CONTENT);
        } else
            lp = new LinearLayout.LayoutParams(getDp(activity, defaultWidth * weight), ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        ((TextView) view.findViewById(R.id.title_text_view)).setGravity(gravity);
        return view;
    }

    public View createItemView(AppCompatActivity activity, ViewGroup parent) {
        if (customViewHolder == null) {
            View view = activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_row_item, parent, false);
            LinearLayout.LayoutParams lp;
            if (projection.getName().equals("Row.row")) {
                lp = new LinearLayout.LayoutParams(getDp(activity, 56), ViewGroup.LayoutParams.WRAP_CONTENT);
            } else
                lp = new LinearLayout.LayoutParams(getDp(activity, defaultWidth * weight), ViewGroup.LayoutParams.WRAP_CONTENT);
            ((TextView) view.findViewById(R.id.text_view)).setGravity(gravity);
            view.setLayoutParams(lp);
            return view;
        } else {
            View view = customViewHolder.onCreateView(activity.getLayoutInflater(), parent);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getDp(activity, defaultWidth * weight), ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(lp);
            return view;
        }
    }

    public View createTotalView(AppCompatActivity activity, ViewGroup parent) {
        if (customTotalView == null) {
            View view = activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_row_item, null, false);
            LinearLayout.LayoutParams lp;
            if (projection.getName().equals("Row.row")) {
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, activity.getResources().getDisplayMetrics());
                lp = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else
                lp = new LinearLayout.LayoutParams(getDp(activity, defaultWidth * weight), ViewGroup.LayoutParams.WRAP_CONTENT);
            ((TextView) view.findViewById(R.id.text_view)).setGravity(gravity);
            view.setLayoutParams(lp);
            return view;
         } else {
            View view = customTotalView.onCreateView(activity.getLayoutInflater(), parent);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getDp(activity, defaultWidth * weight), ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(lp);
            return view;
        }
    }

    public View createPhoneItemView(AppCompatActivity activity, ViewGroup parent) {
        if (customViewHolder == null) {
            View view = activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_row_item_phone, parent, false);
            LinearLayout.LayoutParams lp;
            if (projection.getName().equals("Row.row"))
                view.setBackgroundColor(HelperMethods.getColor(activity, R.color.grey_light_light));
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return view;
        } else {
            View view = activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_custome_row_item_phone, parent, false);
            LinearLayout.LayoutParams lp;
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            View customView = customViewHolder.onCreateView(activity.getLayoutInflater(), null);
            customView.setTag("custom-" + getTag());
            ((LinearLayout) view.findViewById(R.id.wrapper_linear_layout)).addView(customView);
            return view;
        }
    }

    public View createPhoneTotalView(MainVaranegarActivity activity, ViewGroup parent) {
        View view;
        if (customTotalView == null) {
            view = activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_row_item_phone, null, false);
            LinearLayout.LayoutParams lp;
            if (projection.getName().equals("Row.row"))
                view.setBackgroundColor(HelperMethods.getColor(activity, R.color.grey_light_light));
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
        } else {
            view = activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_custome_total_item_phone, parent, false);
            LinearLayout.LayoutParams lp;
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            View customView = customTotalView.onCreateView(activity.getLayoutInflater(), null);
            LinearLayout.LayoutParams customLp;
            customLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
            customView.setLayoutParams(customLp);
            customView.setTag("custom-" + getTag());
            ((LinearLayout) view.findViewById(R.id.wrapper_linear_layout)).addView(customView);
        }
        return view;
    }

    private int getDp(AppCompatActivity activity, float width) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, activity.getResources().getDisplayMetrics());
    }


    public boolean isCustomView() {
        return customViewHolder != null;
    }

    public void bindView(View view, T entity) {
        customViewHolder.onBind(view, entity);
    }

    public boolean isCustomTotalView() {
        return customTotalView != null;
    }

    public void bindTotalView(View view, List<T> entities) {
        customTotalView.onBind(view, entities);
    }

    public ReportColumn setDecimalFormat(String s) {
        this.decimaFormat = s;
        return this;
    }

    public ReportColumn setSuffix(String s) {
        this.suffix = s;
        return this;
    }
}
