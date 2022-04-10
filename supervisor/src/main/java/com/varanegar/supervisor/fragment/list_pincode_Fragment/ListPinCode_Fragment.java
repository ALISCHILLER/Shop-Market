package com.varanegar.supervisor.fragment.list_pincode_Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;

public class ListPinCode_Fragment extends IMainPageFragment {
    private RecyclerView list_request;
    private FloatingActionButton fab;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_pin_code_layout
                ,container,false);
        list_request=view.findViewById(R.id.recycler_list_request);
        fab=view.findViewById(R.id.fab);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
