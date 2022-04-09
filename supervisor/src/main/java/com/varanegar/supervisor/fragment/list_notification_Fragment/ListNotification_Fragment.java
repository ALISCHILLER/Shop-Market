package com.varanegar.supervisor.fragment.list_notification_Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_Model;
import com.varanegar.supervisor.firebase.notification.model.PinRequest_ModelRepository;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToParty;

import java.util.List;


public class ListNotification_Fragment  extends IMainPageFragment {
    private RecyclerView list_request;
    private List<PinRequest_Model> pinRequestModels;
    private ListNotificationAdapter listNotificationAdapter;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_notifaction_layout
                ,container,false);
        list_request=view.findViewById(R.id.recycler_list_request);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDataPinRequest();
        listNotificationAdapter=new ListNotificationAdapter(getActivity(),pinRequestModels);
    }



    private void getDataPinRequest(){
        PinRequest_ModelRepository pinRequest_modelRepository = new PinRequest_ModelRepository();
        Query q = new Query();
        q.from(PinRequest_.PinRequest_Tbl);
        pinRequestModels=pinRequest_modelRepository.getItems(q);
    }
}
