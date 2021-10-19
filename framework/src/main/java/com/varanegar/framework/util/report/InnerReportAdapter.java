package com.varanegar.framework.util.report;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public class InnerReportAdapter<T extends BaseModel> extends ReportAdapter<T> {
    private final SimpleReportAdapter<T> parentAdapter;
    private LinearLayout recyclerHeader;
    private HashMap<UUID, Integer> rowHeights = new HashMap();

    public InnerReportAdapter(MainVaranegarActivity activity, Class<T> tClass, final SimpleReportAdapter<T> parentAdapter) {
        super(activity, tClass);
        this.parentAdapter = parentAdapter;
        this.parentAdapter.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = 0;
                List<T> items = parentAdapter.getAdapter().getItems();
                for (T item :
                        items) {
                    RecyclerView.ViewHolder viewHOlder = parentAdapter.getRecyclerView().getRecyclerView().findViewHolderForAdapterPosition(i);
                    if (viewHOlder != null) {
                        View itemView = viewHOlder.itemView;
                        if (itemView != null) {
                            int h = itemView.getHeight();
                            rowHeights.put(item.UniqueId, h);
                        }
                    }
                    i++;
                }
                notifyDataSetChanged();
            }

        });
    }

    public InnerReportAdapter(VaranegarFragment fragment, Class<T> tClass, SimpleReportAdapter<T> parentAdapter) {
        this(fragment.getVaranegarActvity(), tClass, parentAdapter);
    }

    @Override
    public void bind(ReportColumns columns, T entity) {

    }

    @Override
    protected View onCreateItemView(ViewGroup parent) {
        LinearLayout layout;
        layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_row, parent, false);
        for (final ReportColumn column : columnTemplates
        ) {
            View view = column.createItemView(activity, parent);
            layout.addView(view);
        }
        return layout;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(BaseRecyclerAdapter adapter, View itemView) {
        return new ReportViewHolder(itemView, adapter, activity);
    }

    View onInflateLayout() {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_report_inner, null, false);
        int colorPrimaryLight = HelperMethods.getColorAttr(getActivity(), R.attr.colorPrimaryLight);
        int darker = HelperMethods.getDarker(colorPrimaryLight);
        View header = view.findViewById(R.id.report_recycler_header);
        header.setBackgroundColor(darker);
        return view;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        recyclerHeader = (LinearLayout) getReportView().findViewById(R.id.report_recycler_header);
        generateHeader();
    }


    private void generateHeader() {
        for (final ReportColumn header : columnTemplates
        ) {
            View headerItem = createHeaderItem(header);
            recyclerHeader.addView(headerItem);
        }
    }


    private View createHeaderItem(final ReportColumn header) {
        View view = header.createHeaderView(activity);
        final ImageView sortImageView = (ImageView) view.findViewById(R.id.sort_image_view);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleTextView.setText(header.title);
        if (header.isSortable()) {
            sortImageView.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sort(header);
                }
            });
        } else
            view.findViewById(R.id.sort_image_view).setVisibility(View.GONE);

        return view;
    }

    private class ReportViewHolder extends BaseViewHolder<T> {

        ReportViewHolder(View itemView, BaseRecyclerAdapter<T> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            T entity = recyclerAdapter.get(position);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerAdapter.showContextMenu(position);
                    recyclerAdapter.runItemClickListener(position);
                }
            });
            Integer h = rowHeights.get(entity.UniqueId);
            if (h != null && itemView instanceof ViewGroup)
                itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, h));
            List<Integer> selectedPosition = getSelectedPositions();
            boolean isSelected = Linq.exists(selectedPosition, new Linq.Criteria<Integer>() {
                @Override
                public boolean run(Integer item) {
                    return item == position;
                }
            });
            if (isSelected) {
                int color = HelperMethods.getColor(getContext(), R.color.grey_light_light);
                itemView.setBackgroundColor(color);
            } else {
                int color = HelperMethods.getColor(getContext(), R.color.white);
                itemView.setBackgroundColor(color);
            }

            LinearLayout viewGroup = (LinearLayout) itemView;
            ReportColumns columns = new ReportColumns();
            bind(columns, entity);
            if (columns.get(0).projection instanceof RowModelProjection) {
                columns.get(0).value = position + 1;
            }
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (columns.get(i).isCustomView()) {
                    View row_item = viewGroup.getChildAt(i);
                    columns.get(i).bindView(row_item, entity);
                } else {
                    LinearLayout row_item = (LinearLayout) viewGroup.getChildAt(i);
                    Object value = columns.get(i).value;
                    Double doubleValue = null;
                    if (columns.get(i).getDecimalFormat() != null) {
                        try {
                            doubleValue = (double) value;
                        } catch (Exception ex) {
                            Timber.e(ex);
                        }
                    }
                    String suffix = "";
                    if (columns.get(i).getSuffix() != null) {
                        suffix = columns.get(i).getSuffix();
                    }
                    if (value != null) {
                        if (value instanceof Date)
                            ((TextView) row_item.findViewById(R.id.text_view)).setText(DateHelper.toString((Date) value, DateFormat.Date, getLocale()) + suffix);
                        else if (doubleValue != null) {
                            DecimalFormat df = new DecimalFormat(columns.get(i).getDecimalFormat());
                            String result = df.format(doubleValue);
                            ((TextView) row_item.findViewById(R.id.text_view)).setText(result + suffix);
                        } else
                            ((TextView) row_item.findViewById(R.id.text_view)).setText(value.toString() + suffix);
                    } else
                        ((TextView) row_item.findViewById(R.id.text_view)).setText("" + suffix);
                }
            }
        }
    }

}
