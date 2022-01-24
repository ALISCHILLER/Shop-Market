package com.varanegar.supervisor.status;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.model.reviewreport.ItemsModelRepository;
import com.varanegar.supervisor.model.reviewreport.ItemsView;
import com.varanegar.supervisor.model.reviewreport.ItemsModel;
import com.varanegar.supervisor.model.reviewreport.ReviewreportView;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomer;
import com.varanegar.vaslibrary.model.customer.SupervisorFullCustomer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ToursStatusViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToursStatusViewFragment extends IMainPageFragment {
    private ReportView summaryStatustView;
    private SimpleReportAdapter<ItemsModel> adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<ItemsModel> list;
    public ToursStatusViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ToursStatusViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToursStatusViewFragment newInstance(String param1, String param2) {
        ToursStatusViewFragment fragment = new ToursStatusViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }


    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View view= inflater.inflate(R.layout.fragment_tours_status_view, container, false);
        summaryStatustView = view.findViewById(R.id.summary_status_view);
     return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences("valuesStatus", Context.MODE_PRIVATE);
        UUID uuid = UUID.fromString(sharedPreferences.getString("Status", null));
        Query query = new Query().from(ItemsView.ItemsTbl);

        query= query.whereAnd(Criteria.equals(ItemsView.UniqueId, uuid));


        adapter=new SimpleReportAdapter<ItemsModel>(getVaranegarActvity(), ItemsModel.class){
            @Override
            public void bind(ReportColumns columns, ItemsModel entity) {
                columns.add(bind(entity, ItemsView.productCategory,"گروه کالا").setFrizzed()
                        .setSortable().setWeight(2f));
                columns.add(bind(entity, ItemsView.productCode,"کد کالا").setWeight(1.5f));
                columns.add(bind(entity, ItemsView.productName,"نام کالا").setWeight(2.5f));
                columns.add(bind(entity, ItemsView.productCountStr,"تعداد"));
                columns.add(bind(entity, ItemsView.amount,"جمع مبلغ").sendToDetail()
                        .setWeight(1.5f).calcTotal());
                columns.add(bind(entity, ItemsView.tax,"مالیات").sendToDetail()
                        .setWeight(1.5f).calcTotal());
            }
        };
        adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
        adapter.create(new ItemsModelRepository(),query, null);
        summaryStatustView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){

        }
        super.onBackPressed();

    }
}