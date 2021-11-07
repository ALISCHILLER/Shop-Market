package com.varanegar.supervisor.customers;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.model.SupervisorCustomerModel;
import com.varanegar.supervisor.webapi.CustomerSummaryViewModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/10/2018.
 */

class CustomerSummaryContextView extends ItemContextView<SupervisorCustomerModel> {
    private ProgressView progressView;
    private PairedItems remainingPairedItems;
    private PairedItems firstCreditPairedItems;
    private PairedItems openInvoicesQtyPairedItems;
    private PairedItems openInvoicesPairedItems;
    private PairedItems openChequeQtyPairedItems;
    private PairedItems openChequeAmountPairedItems;
    private PairedItems returnedChequeQtyPairedItems;
    private PairedItems returnedChequeAmountPairedItems;
    private PairedItems remainCreditPairedItems;
    private PairedItems remainDebitPairedItems;
    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng userPosition;
    private GoogleApiClient client;
    private TextView locationErrorTextView;
    private FloatingActionButton googleMapFab;
    private FloatingActionButton wazeFab;

    public CustomerSummaryContextView(BaseRecyclerAdapter<SupervisorCustomerModel> adapter, Context context) {
        super(adapter, context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private Marker createMarker(SupervisorCustomerModel customer) {
        Context context = getContext();
        if (customer.UniqueId != null && customer.Latitude != 0 && customer.Longitude != 0 && context != null) {
            LatLng customerPosition = new LatLng(customer.Latitude, customer.Longitude);
            MarkerOptions options = new MarkerOptions().position(customerPosition);
            Marker marker = googleMap.addMarker(options);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_green_a700_36dp);
            marker.setTitle(customer.CustomerName);
            marker.setIcon(bitmap);
            marker.setTag(customer.UniqueId.toString());
            return marker;
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                try {
                    googleMap.setMyLocationEnabled(true);
                    SupervisorCustomerModel customerModel = adapter.get(getPosition());
                    if (customerModel == null)
                        return;

                    createMarker(customerModel);

                    getUserLocation();

                } catch (SecurityException ex) {
                    Timber.e(ex);
                }
            }
        });


        final SupervisorCustomerModel customerModel = adapter.get(getPosition());
        if (customerModel == null)
            return;

        if (customerModel.Latitude != 0 && customerModel.Longitude != 0) {
            googleMapFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String map = "http://maps.google.com/maps?daddr=" + customerModel.Latitude + "," + customerModel.Longitude;
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(map));
                        getContext().startActivity(intent);
                    } catch (Exception ignored) {

                    }

                }
            });
            wazeFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String waze = "waze://?ll=" + customerModel.Latitude + ", " + customerModel.Longitude + "&navigate=yes";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(waze));
                        getContext().startActivity(intent);
                    } catch (Exception ignored) {

                    }

                }
            });
            locationErrorTextView.setVisibility(View.GONE);
        } else {
            googleMapFab.setVisibility(View.GONE);
            wazeFab.setVisibility(View.GONE);
            locationErrorTextView.setVisibility(View.VISIBLE);
        }

        if (progressView != null) {
            progressView.setMessage(R.string.downloading_customer_data);
            progressView.start();
        }

        SupervisorApi api = new SupervisorApi(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.DeviceSettingNo, SysConfigManager.cloud);
        api.runWebRequest(api.getCustomerFinanceData(customerModel.UniqueId, customerModel.DealerId, VaranegarApplication.getInstance().getAppId(), SysConfigManager.getIntValue(sysConfigModel, 0))
                , new WebCallBack<List<CustomerSummaryViewModel>>() {
                    @Override
                    protected void onFinish() {
                        if (progressView != null)
                            progressView.finish();
                    }

                    @Override
                    protected void onSuccess(List<CustomerSummaryViewModel> result, Request request) {
                        if (result != null && result.size() == 1) {
                            CustomerSummaryViewModel viewModel = result.get(0);
                            remainingPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.CustomerRemain));
                            firstCreditPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.InitCredit));
                            openInvoicesQtyPairedItems.setValue(String.valueOf(viewModel.OpenInvoicesCount));
                            openInvoicesPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.OpenInvoicesAmount));
                            openChequeQtyPairedItems.setValue(String.valueOf(viewModel.OpenChequeCount));
                            openChequeAmountPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.OpenChequeAmount));
                            returnedChequeQtyPairedItems.setValue(String.valueOf(viewModel.ReturnChequeCount));
                            returnedChequeAmountPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.ReturnChequeAmount));
                            remainCreditPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.RemainCredit));
                            remainDebitPairedItems.setValue(VasHelperMethods.currencyToString(viewModel.RemainDebit));
                        } else
                            showErrorDialog(getContext().getString(R.string.downloading_data_failed));
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Timber.e(t);
                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                    }
                });
    }

    private void moveCamera() {
        if (googleMap == null)
            return;
        final List<LatLng> lls = new ArrayList<>();

        LatLng customerPosition = null;
        SupervisorCustomerModel customerModel = adapter.get(getPosition());
        if (customerModel != null && customerModel.Longitude != 0 && customerModel.Latitude != 0) {
            customerPosition = new LatLng(customerModel.Latitude, customerModel.Longitude);
        }

        if (customerPosition != null)
            lls.add(customerPosition);

        if (userPosition != null)
            lls.add(userPosition);

        if (lls.size() == 0)
            return;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LatLngBounds.Builder b = new LatLngBounds.Builder();
                    for (LatLng ll : lls) {
                        b.include(ll);
                    }
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(b.build(), 100);
                    googleMap.animateCamera(cu);
                } catch (Error ignored) {

                }
            }
        }, 1000);

    }

    void getUserLocation() {
        Context context = getContext();
        if (context != null) {
            client = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (isResumed())
                                try {
                                    LocationRequest request = LocationRequest.create();
                                    request.setInterval(1000);
                                    request.setSmallestDisplacement(1);
                                    LocationServices.getFusedLocationProviderClient(getContext()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            if (isResumed()) {
                                                if (location != null)
                                                    userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                                moveCamera();
                                            }
                                        }
                                    });


                                } catch (SecurityException ex) {
                                    Timber.e(ex);
                                }

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Timber.d("location connection suspended");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Timber.e(connectionResult.getErrorMessage());
                        }
                    })
                    .build();
            client.connect();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.fragment_supervisor_customer_summary_context_layout, viewGroup, false);
        googleMapFab = view.findViewById(R.id.google_map_fab);
        wazeFab = view.findViewById(R.id.waze_fab);
        locationErrorTextView = view.findViewById(R.id.location_error_text_view);
        mapView = view.findViewById(R.id.map_view);
        progressView = view.findViewById(R.id.progress_view);
        remainingPairedItems = view.findViewById(R.id.customer_remaining_paired_items);
        firstCreditPairedItems = view.findViewById(R.id.customer_first_credit_paired_items);
        openInvoicesQtyPairedItems = view.findViewById(R.id.customer_open_invoices_qty_paired_items);
        openInvoicesPairedItems = view.findViewById(R.id.customer_open_invoices_paired_items);
        openChequeQtyPairedItems = view.findViewById(R.id.customer_open_cheque_qty_paired_items);
        openChequeAmountPairedItems = view.findViewById(R.id.customer_open_cheque_amount_paired_items);
        returnedChequeQtyPairedItems = view.findViewById(R.id.customer_returned_cheque_qty_paired_items);
        returnedChequeAmountPairedItems = view.findViewById(R.id.customer_returned_cheque_amount_paired_items);
        remainCreditPairedItems = view.findViewById(R.id.customer_remain_credit_paired_items);
        remainDebitPairedItems = view.findViewById(R.id.customer_remain_debit_paired_items);
        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeContextView();
            }
        });
        return view;
    }

    private void showErrorDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(err);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
}
