package com.varanegar.vaslibrary.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.webapi.tsp.TspApi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PathOptimizationDialog extends CuteAlertDialog {
    public Location lastLocation;
    private RadioButton all;
    private RadioButton today;
    private List<UUID> todayCustomerIds;
    private List<UUID> allCustomerIds;
    private RadioButton delete;

    public interface IOnResponse {
        void done(List<CustomerModel> customerModels, List<LatLng> points);

        void failed(String error);
    }

    public IOnResponse onResponse;

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.path_optimization_dialog, viewGroup, true);
        setTitle(R.string.omptimizing_path);
        today = view.findViewById(R.id.today_customers_radio_button);
        all = view.findViewById(R.id.all_customers_radio_button);
        delete = view.findViewById(R.id.delete_radio_button);
        TourManager tourManager = new TourManager(getContext());
        TourModel tourModel = tourManager.loadTour();
        CustomerManager customerManager = new CustomerManager(getContext());
        List<UUID> customersWithLocation = Linq.map(customerManager.getCustomersWithLocation(), item -> item.UniqueId);
        List<CustomerPathViewModel> todayCustomers = new CustomerPathViewManager(getContext()).getItems(CustomerPathViewManager.getDay(tourModel.DayVisitPathId, false));
        todayCustomerIds = Linq.map(todayCustomers, item -> item.UniqueId);
        allCustomerIds = Linq.map(customerManager.getAll(), item -> item.UniqueId);

        List<UUID> visitedCustomers = Linq.map(customerManager.getVisitedCustomers(), item -> item.UniqueId);
        todayCustomerIds = Linq.findAll(todayCustomerIds, c -> !Linq.exists(visitedCustomers, vc -> vc.equals(c)));
        allCustomerIds = Linq.findAll(allCustomerIds, c -> !Linq.exists(visitedCustomers, vc -> vc.equals(c)));

        todayCustomerIds = Linq.findAll(todayCustomerIds, item -> Linq.exists(customersWithLocation, it -> it.equals(item)));
        allCustomerIds = Linq.findAll(allCustomerIds, item -> Linq.exists(customersWithLocation, it -> it.equals(item)));
        today.setEnabled(todayCustomerIds.size() > 1);
        all.setEnabled(allCustomerIds.size() > 1);

        delete.setEnabled(Linq.exists(customerManager.getAll(), it -> it.OPathId > 0));
    }

    @Override
    public void ok() {
        dismiss();
        List<CustomerModel> customers;
        if (delete.isChecked()) {
            try {
                CustomerManager customerManager = new CustomerManager(getContext());
                List<CustomerModel> customerModels = customerManager.getAll();
                for (CustomerModel customer :
                        customerModels) {
                    customer.OPathId = 0;
                }
                customerManager.update(customerModels);
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("OPTIMIZED_PATH", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                onResponse.done(new ArrayList<>(), new ArrayList<>());
                return;
            } catch (Exception ex) {
                onResponse.failed(ex.getMessage());
                return;
            }
        }

        if (today.isChecked())
            customers = new CustomerManager(getContext()).getCustomers(todayCustomerIds);
        else
            customers = new CustomerManager(getContext()).getCustomers(allCustomerIds);
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        for (CustomerModel customer :
                customers) {
            points.add(new LatLng(customer.Latitude, customer.Longitude));
        }
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Activity activity = getActivity();
        int oldOrientation = activity.getRequestedOrientation();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        TspApi tspApi = new TspApi(getActivity());
        tspApi.getRoute(points, new TspApi.OnTspResponse() {
            @Override
            public void onSuccess(List<LatLng> list, int[] indices) {
                activity.setRequestedOrientation(oldOrientation);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                int i = 1;
                for (CustomerModel customer :
                        customers) {
                    customer.OPathId = customers.size() + 1 - indices[i];
                    i++;
                }
                if (onResponse != null) {
                    onResponse.done(customers, list);
                }
            }

            @Override
            public void onFailure(String error) {
                activity.setRequestedOrientation(oldOrientation);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (onResponse != null)
                    onResponse.failed(error);
            }
        });
    }

    @Override
    public void cancel() {

    }
}
