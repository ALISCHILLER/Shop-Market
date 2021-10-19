package com.varanegar.vaslibrary.ui.report.review.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.reviewreport.TargetReviewReportView;
import com.varanegar.vaslibrary.webapi.reviewreport.TargetReviewReportViewModel;

/**
 * Created by e.hashemzadeh on 20/06/21.
 */

public class TargetReviewReportAdapter extends SimpleReportAdapter<TargetReviewReportViewModel> {
    public TargetReviewReportAdapter(MainVaranegarActivity activity) {
        super(activity, TargetReviewReportViewModel.class);
    }

    public TargetReviewReportAdapter(VaranegarFragment fragment) {
        super(fragment, TargetReviewReportViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, TargetReviewReportViewModel entity) {

        columns.add(bind(entity, TargetReviewReportView.type, getActivity().getString(R.string.target_type)).setFrizzed());
        columns.add(bind(entity, TargetReviewReportView.title, getActivity().getString(R.string.target_title)).setFrizzed().setWeight(2));
        columns.add(bind(entity, TargetReviewReportView.target, getActivity().getString(R.string.target_target)).setFrizzed().setCustomViewHolder(new CustomViewHolder<TargetReviewReportViewModel>() {
            @Override
            public void onBind(View view, TargetReviewReportViewModel entity) {
                TextView textView = (TextView) view.findViewById(R.id.target_base);
                textView.setText(HelperMethods.doubleToStringWithCommas(entity.target));
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.fragment_base_target, parent, false);
            }
        }));
        columns.add(bind(entity, TargetReviewReportView.targetToDate, getActivity().getString(R.string.target_daily)).setCustomViewHolder(new CustomViewHolder<TargetReviewReportViewModel>() {
            @Override
            public void onBind(View view, TargetReviewReportViewModel entity) {
                TextView textView = (TextView) view.findViewById(R.id.target_base);
                textView.setText(HelperMethods.doubleToStringWithCommas(entity.targetToDate));
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.fragment_base_target, parent, false);
            }
        }));
        columns.add(bind(entity, TargetReviewReportView.achievementToDate, getActivity().getString(R.string.target_access)));
        columns.add(bind(entity, TargetReviewReportView.achievement, getActivity().getString(R.string.target_access_period)));
        columns.add(bind(entity, TargetReviewReportView.dailyPlan, getActivity().getString(R.string.target_daily_plan)).setCustomViewHolder(new CustomViewHolder<TargetReviewReportViewModel>() {
            @Override
            public void onBind(View view, TargetReviewReportViewModel entity) {
                TextView textView = (TextView) view.findViewById(R.id.target_base);
                textView.setText(HelperMethods.doubleToStringWithCommas(entity.dailyPlan));
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.fragment_base_target, parent, false);
            }
        }));
        columns.add(bind(entity, TargetReviewReportView.remainTarget, getActivity().getString(R.string.remain_target)).setCustomViewHolder(new CustomViewHolder<TargetReviewReportViewModel>() {
            @Override
            public void onBind(View view, TargetReviewReportViewModel entity) {
                TextView textView = (TextView) view.findViewById(R.id.target_base);
                textView.setText(HelperMethods.doubleToStringWithCommas(entity.remainTarget));
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.fragment_base_target, parent, false);
            }
        }));
        columns.add(bind(entity, TargetReviewReportView.isSold, getActivity().getString(R.string.is_sold)).setCustomViewHolder(new CustomViewHolder<TargetReviewReportViewModel>() {
            @Override
            public void onBind(View view, TargetReviewReportViewModel entity) {
                TextView textView = (TextView) view.findViewById(R.id.target_base);
                textView.setText(HelperMethods.doubleToStringWithCommas(entity.isSold));
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.fragment_base_target, parent, false);
            }
        }));
        columns.add(bind(entity, TargetReviewReportView.calcDate, getActivity().getString(R.string.calc_date)));
        columns.add(bind(entity, TargetReviewReportView.dayOrderNumber, getActivity().getString(R.string.passed_days)));
        columns.add(bind(entity, TargetReviewReportView.remainDays, getActivity().getString(R.string.remains_day)));
        columns.add(bind(entity, TargetReviewReportView.PoursantTillDate, getActivity().getString(R.string.poursant_till_date)).setCustomViewHolder(new CustomViewHolder<TargetReviewReportViewModel>() {
            @Override
            public void onBind(View view, TargetReviewReportViewModel entity) {
                TextView textView = (TextView) view.findViewById(R.id.target_base);
                textView.setText(HelperMethods.doubleToStringWithCommas(entity.PoursantTillDate));
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return inflater.inflate(R.layout.fragment_base_target, parent, false);
            }
        }));
//        columns.add(bind(entity, TargetReviewReportView.Condition, getActivity().getString(R.string.target_condition)));
//        columns.add(bind(entity, TargetReviewReportView.AchievementStimate, getActivity().getString(R.string.target_esstimate)));
//        columns.add(bind(entity, TargetReviewReportView.CalculationPeriodId, getActivity().getString(R.string.target_calculation_period)));
    }
}
