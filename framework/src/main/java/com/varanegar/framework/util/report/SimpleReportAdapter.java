package com.varanegar.framework.util.report;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.R;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/24/2017.
 */

public class SimpleReportAdapter<T extends BaseModel> extends ReportAdapter<T> {
    private final boolean landscape;
    private LinearLayout recyclerHeader;
    private LinearLayout recyclerFooter;
    private FloatingActionButton expandFooterFab;
    private ReportHorizontalScrollView hScrollView;
    private ReportView frizzedReportView;
    private InnerReportAdapter<T> innerAdapter;
    private View progressBar;
    private long clickDelay = 500;

    /**
     * @param clickDelay in milliseconds
     */
    public void setItemClickDelay(long clickDelay) {
        this.clickDelay = clickDelay;
    }

    public void removeItemClickDelay() {
        this.clickDelay = 0;
    }

    public SimpleReportAdapter(MainVaranegarActivity activity, Class<T> tClass) {
        super(activity, tClass);
        this.landscape = activity.getResources().getBoolean(R.bool.is_landscape);
    }

    public SimpleReportAdapter(VaranegarFragment fragment, Class<T> tClass) {
        super(fragment, tClass);
        this.landscape = activity.getResources().getBoolean(R.bool.is_landscape);
    }

    protected void bindRowNumber(ReportColumns columns) {
        columns.add(bind(new RowModel(0), RowModelProjection.Row, getActivity().getString(R.string.row_number)));
    }

    @Override
    public void bind(ReportColumns columns, T entity) {

    }

    @Override
    protected View onCreateItemView(ViewGroup parent) {
        LinearLayout layout;
        if (landscape) {
            layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_row, parent, false);
            for (final ReportColumn column : columnTemplates
            ) {
                View view = column.createItemView(activity, parent);
                layout.addView(view);
            }
        } else {
            layout = new LinearLayout(activity);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.HORIZONTAL);
            View frame = activity.getLayoutInflater().inflate(R.layout.layout_report_recycler_row_frame, layout, false);
            frame.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            LinearLayout headerLinearLayout = (LinearLayout) frame.findViewById(R.id.header_linear_layout);
            // LinearLayout rowLinearLayout = (LinearLayout) frame.findViewById(R.id.row_linear_layout);
            final LinearLayout contentLinearLayout = (LinearLayout) frame.findViewById(R.id.content_frame_layout);
            final ImageView expandImageView = (ImageView) frame.findViewById(R.id.expand_image_view);
            expandImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (contentLinearLayout.getVisibility() == View.GONE) {
                        float start = Math.min(-contentLinearLayout.getHeight(), -200);
                        final TranslateAnimation animate = new TranslateAnimation(0, 0, start, 0);
                        animate.setDuration(200);
                        animate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                contentLinearLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        contentLinearLayout.startAnimation(animate);
                        expandImageView.setImageResource(R.drawable.ic_expand_less_white_24dp);
                    } else {
                        final TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -contentLinearLayout.getHeight());
                        animate.setDuration(200);
                        animate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                contentLinearLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        contentLinearLayout.startAnimation(animate);
                        expandImageView.setImageResource(R.drawable.ic_expand_more_white_24dp);
                    }
                }
            });
            layout.addView(frame);
            boolean hasDetail = false;
            for (int i = 0; i < columnTemplates.size(); i++) {
                ReportColumn column = columnTemplates.get(i);
                View columnView = column.createPhoneItemView(activity, contentLinearLayout);
                columnView.setTag(column.getTag());
                if (column.isDetail()) {
                    {
                        contentLinearLayout.addView(columnView);
                        hasDetail = true;
                    }
                } else {
                    headerLinearLayout.addView(columnView);
                }
                if (column.isTotal() && expandFooterFab != null)
                    expandFooterFab.setVisibility(View.VISIBLE);
            }
            if (!hasDetail) {
                expandImageView.setVisibility(View.GONE);
            }
        }
        return layout;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(BaseRecyclerAdapter adapter, View itemView) {
        return new ReportViewHolder(itemView, adapter, activity);
    }

    View onInflateLayout() {
        View view = activity.getLayoutInflater().inflate(R.layout.layout_report, null, false);
        hScrollView = view.findViewById(R.id.horizontal_scroll_view);
        if (hScrollView != null)
            hScrollView.setOnHorizontalStatusChangeListener(new ReportHorizontalScrollView.HorizontalStatusChangeListener() {
                @Override
                public void onChanged(ReportHorizontalScrollView.Status status) {
                    Timber.e(status.name());
                }
            });
        frizzedReportView = view.findViewById(R.id.frizzed_report_view);
        View innerFooter = view.findViewById(R.id.inner_footer);
        if (innerFooter != null) {
            int colorPrimaryLight = HelperMethods.getColorAttr(getActivity(), R.attr.colorPrimaryLight);
            int darker = HelperMethods.getDarker(colorPrimaryLight);
            innerFooter.setBackgroundColor(darker);
        }
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    private boolean footerExpanded = false;

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        recyclerHeader = (LinearLayout) getReportView().findViewById(R.id.report_recycler_header);
        recyclerFooter = (LinearLayout) getReportView().findViewById(R.id.report_recycler_footer);
        expandFooterFab = getReportView().findViewById(R.id.expand_footer_fab);
        if (expandFooterFab != null)
            expandFooterFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (footerExpanded) {
                        recyclerFooter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                        footerExpanded = false;
                        expandFooterFab.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    } else {
                        recyclerFooter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        footerExpanded = true;
                        expandFooterFab.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    }
                }
            });
        generateHeader();
        updateFooter();

        List<ReportColumn> frizzedColumns = new ArrayList<>();
        for (ReportColumn column :
                columnTemplates) {
            if (column.isFrizzed()) {
                frizzedColumns.add(column);
            }
        }
        if (hScrollView != null && frizzedColumns.size() > 0) {
            hScrollView.setOnHorizontalStatusChangeListener(new ReportHorizontalScrollView.HorizontalStatusChangeListener() {
                @Override
                public void onChanged(ReportHorizontalScrollView.Status status) {
                    if (status == ReportHorizontalScrollView.Status.Scrolled) {
                        frizzedReportView.setVisibility(View.VISIBLE);
                    } else
                        frizzedReportView.setVisibility(View.GONE);
                }
            });
            setupInnerAdapter(frizzedColumns);
        }
    }

    private void setupInnerAdapter(final List<ReportColumn> frizzedColumns) {

        innerAdapter = new InnerReportAdapter<T>(getActivity(), getTClass(), this) {
            @Override
            public void bind(ReportColumns columns, T entity) {
                for (ReportColumn column :
                        frizzedColumns) {
                    columns.add(bind(entity, column.projection, column.title).setWeight(column.getWeight()));
                }
            }
        };
        innerAdapter.setLocale(getLocale());
        innerAdapter.create(getAdapter().getItems(), null);
        frizzedReportView.setAdapter(innerAdapter);
        getAdapter().setDataCallback(new BaseRecyclerAdapter.OnDataCallback() {
            @Override
            public void onStart() {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressBar != null)
                                progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onFinish(int numberOfItems) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressBar != null)
                                progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                innerAdapter.getAdapter().setItems(getAdapter().getItems());
            }
        });


        final BaseRecyclerView lvDetail2 = getRecyclerView();
        final BaseRecyclerView lvDetail = innerAdapter.getRecyclerView();
        final RecyclerView.OnScrollListener[] scrollListeners = new RecyclerView.OnScrollListener[2];
        scrollListeners[0] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lvDetail2.removeOnScrollListener(scrollListeners[1]);
                lvDetail2.scrollBy(dx, dy);
                lvDetail2.addOnScrollListener(scrollListeners[1]);
            }
        };
        scrollListeners[1] = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lvDetail.removeOnScrollListener(scrollListeners[0]);
                lvDetail.scrollBy(dx, dy);
                lvDetail.addOnScrollListener(scrollListeners[0]);
            }
        };
        lvDetail.addOnScrollListener(scrollListeners[0]);
        lvDetail2.addOnScrollListener(scrollListeners[1]);
    }


    private void generateHeader() {
        if (landscape) {
            for (final ReportColumn header : columnTemplates
            ) {
                View headerItem = createHeaderItem(header);
                recyclerHeader.addView(headerItem);
            }
        }
    }

    private void updateFooter() {
        if (recyclerHeader != null) {
            recyclerFooter.removeAllViews();
            if (landscape)
                for (final ReportColumn header : columnTemplates
                ) {
                    recyclerFooter.addView(createTotalView(header, recyclerFooter));
                }
            else {
                recyclerFooter.setOrientation(LinearLayout.VERTICAL);
                for (final ReportColumn header : columnTemplates
                ) {
                    if (header.isTotal())
                        recyclerFooter.addView(createTotalView(header, recyclerFooter));
                }
            }
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

    private LinearLayout createTotalView(final ReportColumn header, ViewGroup parent) {
        LinearLayout layout;
        TextView totalTextView;
        if (landscape) {
            layout = (LinearLayout) header.createTotalView(activity, parent);
            totalTextView = (TextView) layout.findViewById(R.id.text_view);
        } else {
            layout = (LinearLayout) header.createPhoneTotalView(activity, parent);
            if (header.isCustomTotalView() && header.isTotal())
                totalTextView = (TextView) layout.findViewById(R.id.text_view);
            else
                totalTextView = (TextView) layout.findViewById(R.id.title_text_view);
        }


        if (header.isCustomTotalView() && header.isTotal()) {
            if (landscape)
                header.bindTotalView(layout, getAdapter().getItems());
            else {
                String txt = " جمع " + header.title + " : ";
                totalTextView.setText(txt);
                header.bindTotalView(layout.findViewWithTag("custom-" + header.getTag()), getAdapter().getItems());
            }
        } else {
            if (totalTextView != null)
                totalTextView.setText(calculateTotal(header));
            if (!landscape) {
                String txt = " جمع " + header.title + " : " + totalTextView.getText();
                totalTextView.setText(txt);
            }
        }
        return layout;
    }

    @Override
    public void refresh() {
        super.refresh();
        updateFooter();
    }

    @Override
    public void refresh(Query newQuery) {
        super.refresh(newQuery);
        updateFooter();
    }


    private class ReportViewHolder extends BaseViewHolder<T> {

        private String toString(ReportColumn column) {
            Object value = column.value;
            Double doubleValue = null;
            if (column.getDecimalFormat() != null) {
                try {
                    doubleValue = (double) value;
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }
            String suffix = "";
            if (column.getSuffix() != null) {
                suffix = column.getSuffix();
            }
            if (value != null) {
                if (value instanceof Date)
                    return DateHelper.toString((Date) value, DateFormat.Date, getLocale()) + suffix;
                else if (doubleValue != null) {
                    DecimalFormat df = new DecimalFormat(column.getDecimalFormat());
                    String result = df.format(doubleValue);
                    return result + suffix;
                } else if (value instanceof Double) {
                    return HelperMethods.doubleToString((double) value) + suffix;
                } else if (value instanceof BigDecimal) {
                    return HelperMethods.bigDecimalToString((BigDecimal) value) + suffix;
                } else
                    return value.toString() + suffix;
            } else
                return suffix;
        }

        ReportViewHolder(View itemView, BaseRecyclerAdapter<T> recyclerAdapter, Context context) {
            super(itemView, recyclerAdapter, context);
        }

        @Override
        public void bindView(final int position) {
            T entity = recyclerAdapter.get(position);
            entity.setProperties();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickDelay > 0) {
                        itemView.setEnabled(false);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                itemView.setEnabled(true);
                            }
                        }, clickDelay);
                    }
                    recyclerAdapter.showContextMenu(position);
                    recyclerAdapter.runItemClickListener(position);
                }
            });
            List<Integer> selectedPosition = getSelectedPositions();
            boolean isSelected = Linq.exists(selectedPosition, new Linq.Criteria<Integer>() {
                @Override
                public boolean run(Integer item) {
                    return item == position;
                }
            });
            if (isSelected) {
                int color = HelperMethods.getColorAttr(getContext(), R.attr.colorPrimaryLight);
                itemView.setBackgroundColor(color);
            } else {
                int color = HelperMethods.getColor(getContext(), R.color.white);
                itemView.setBackgroundColor(color);
            }

            if (landscape) {
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
                        String str = toString(columns.get(i));
                        ((TextView) row_item.findViewById(R.id.text_view)).setText(str);
                    }
                }
            } else {
                LinearLayout viewGroup = (LinearLayout) itemView;
                ReportColumns columns = new ReportColumns();
                bind(columns, entity);
                for (int i = 0; i < columns.size(); i++) {
                    ReportColumn column = columns.get(i);
                    if (column.isCustomView()) {
                        View itemView = viewGroup.findViewWithTag(column.getTag());
                        column.bindView(itemView.findViewWithTag("custom-" + column.getTag()), entity);
                        if (column.title != null && !column.title.isEmpty())
                            ((TextView) itemView.findViewById(R.id.text_view)).setText(column.title + " : ");
                    } else {
                        LinearLayout item = (LinearLayout) viewGroup.findViewWithTag(column.getTag());
                        if (isSelected)
                            item.setBackgroundColor(HelperMethods.getColorAttr(getContext(), R.attr.colorPrimaryLight));
                        else
                            item.setBackgroundColor(HelperMethods.getColor(getContext(), R.color.white));
                        if (column.projection instanceof RowModelProjection) {
                            TextView titleTextView = item.findViewById(R.id.title_text_view);
                            TextView valueTextView = item.findViewById(R.id.value_text_view);
                            titleTextView.setTextColor(HelperMethods.getColor(getContext(), R.color.black));
                            titleTextView.setBackgroundColor(HelperMethods.getColorAttr(getContext(), isSelected ? R.attr.colorPrimary : R.attr.colorPrimaryLight));
                            valueTextView.setBackgroundColor(HelperMethods.getColorAttr(getContext(), isSelected ? R.attr.colorPrimary : R.attr.colorPrimaryLight));
                            (titleTextView).setText(String.valueOf(position + 1));
                        } else {
                            TextView titleTextView = item.findViewById(R.id.title_text_view);
                            TextView valueTextView = item.findViewById(R.id.value_text_view);
                            titleTextView.setText(column.title);
                            valueTextView.setText(toString(column));
                        }
                    }
                }
            }
        }

    }

    class RowModel extends BaseModel {
        public int row;

        RowModel(int position) {
            row = position;
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }

}
