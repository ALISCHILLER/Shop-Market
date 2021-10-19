package com.varanegar.vaslibrary.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.Area;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.RegionAreaPointManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.map.BatteryLowMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.GPSOffMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.GPSOnMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.LackOfOrderMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.LackOfVisitMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.MapHelper;
import com.varanegar.vaslibrary.manager.locationmanager.map.OrderMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.SendTourMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.StartTourMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.TrackingPointMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WaitMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WifiOffMarker;
import com.varanegar.vaslibrary.manager.locationmanager.map.WifiOnMarker;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.BatteryLowLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOnLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfOrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfVisitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.SendTourLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.StartTourLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WifiOnLocationViewModel;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.tour.TourModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 7/17/2017.
 */


//                            List<LatLng> list = new ArrayList<>();
//        list.add(new LatLng(35.767018756508655, 51.516127318473174));
//        list.add(new LatLng(35.749467273061356, 51.529858457956905));
//        list.add(new LatLng(35.732473335703574, 51.51818749001735));
//        list.add(new LatLng(35.73053720593951, 51.53652636790115));

public class UserLocationFragment extends VaranegarFragment {
    private MapView mMapView;
    private GoogleMap googleMap;
    Geocoder geocoder;
    private TextView currentTextView;
    GoogleApiClient client;
    boolean locationUpdated = false;
    boolean myLocationClicked = false;
    boolean isDragging = false;
    List<Marker> markers = new ArrayList<>();
    private View customerInfoView;
    private TextView customerNameTextView;
    private TextView customerAddressTextView;
    private TextView customerStatusTextView;
    private View locationView;
    private TextView customerTelTextView;
    private Location lastLocation;
    private String customerId;
    MapHelper trackingMapHelper;
    private GoogleApiClient.ConnectionCallbacks connectionCallBack;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private View optimiseBtn;
    private List<Polyline> polyLines = new ArrayList<>();
    private int maxOPathId;

    public void setCustomerId(@NonNull UUID customerId) {
        addArgument("bc2c441a-a714-4bfd-a167-03c91d5e85a4", customerId.toString());
    }

    private enum Type {
        All,
        Today,
        None
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        customerId = getStringArgument("bc2c441a-a714-4bfd-a167-03c91d5e85a4");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getVaranegarActvity().setDrawerLayout(R.layout.map_drawer_layout);
        view.findViewById(R.id.optimize_btn).setOnClickListener(v -> {
            if (lastLocation != null) {
                PathOptimizationDialog dialog = new PathOptimizationDialog();
                dialog.lastLocation = lastLocation;
                dialog.onResponse = new PathOptimizationDialog.IOnResponse() {
                    @Override
                    public void done(List<CustomerModel> customerModels, List<LatLng> points) {
                        if (googleMap != null && customerModels.size() > 0 && points.size() > 0) {
                            createPathPolyLine(points);
                            optimiseBtn.setVisibility(View.VISIBLE);
                            optimiseBtn.setOnClickListener(v1 -> {
                                try {
                                    CustomerManager customerManager = new CustomerManager(getContext());
                                    customerManager.updateOPathIds(customerModels, points);
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setIcon(Icon.Success);
                                    dialog.setMessage(R.string.customers_list_updated);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                } catch (Exception e) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setIcon(Icon.Error);
                                    dialog.setMessage(R.string.error_saving_request);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void failed(String error) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setIcon(Icon.Error);
                        dialog.setMessage(error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                };
                dialog.show(getChildFragmentManager(), "CuteAlertDialogOptimisation");
            } else {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setMessage(R.string.location_is_not_available);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }

        });
        ((RadioButton) view.findViewById(R.id.show_all_customers_radio_button)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                createMarkers(Type.All);
                hideTracking();
            }
        });
        ((RadioButton) view.findViewById(R.id.show_today_customers_radio_button)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                createMarkers(Type.Today);
                hideTracking();
            }
        });
        ((RadioButton) view.findViewById(R.id.hide_customers_radio_button)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                createMarkers(Type.None);
                hideTracking();
            }
        });
        ((RadioButton) view.findViewById(R.id.show_path_radio_button)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                createMarkers(Type.None);
                hideTracking();
                showTracking(true);
            }
        });
    }

    private void createPathPolyLine(List<LatLng> list) {

        int length = 10;
        int count = list.size() / length;
        for (Polyline polyline :
                polyLines) {
            polyline.remove();
        }

        PolylineOptions options1 = new PolylineOptions()
                .geodesic(true)
                .color(Color.GRAY)
                .width(6)
                .jointType(JointType.ROUND)
                .addAll(list);
        googleMap.addPolyline(options1);

        for (int i = 0; i < count; i++) {
            if (i * length < list.size()) {
                List<LatLng> l = list.subList((Math.max(0, (i * length) - 1)), Math.min((i * length + length + 1), list.size() - 1));
                if (l.size() > 2) {
                    int arrowColor = Color.RED;
                    int lineColor = Color.RED;
                    BitmapDescriptor endCapIcon = getEndCapIcon(getContext(), arrowColor);
                    PolylineOptions options = new PolylineOptions()
                            .geodesic(true)
                            .color(lineColor)
                            .width(3)
                            .startCap(new RoundCap())
                            .endCap(new CustomCap(endCapIcon, 8))
                            .jointType(JointType.ROUND)
                            .addAll(l);
                    polyLines.add(googleMap.addPolyline(options));
                }
            }
        }
    }

    public BitmapDescriptor getEndCapIcon(Context context, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void createMarker(CustomerPathViewModel customer) {
        Context context = getContext();
        if (customer.UniqueId != null && customer.Latitude != 0 && customer.Longitude != 0 && context != null) {
            LatLng customerPosition = new LatLng(customer.Latitude, customer.Longitude);
            MarkerOptions options = new MarkerOptions().position(customerPosition);
            Marker marker = googleMap.addMarker(options);
            CustomerCallManager callManager = new CustomerCallManager(context);
            List<CustomerCallModel> calls = callManager.loadCalls(customer.UniqueId);
            int icon = callManager.getIcon(calls);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(icon);
            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), icon);
            marker.setTitle(customer.CustomerName);
            marker.setIcon(bitmap);
            marker.setTag(customer.UniqueId.toString());
            markers.add(marker);
            if (customer.OPathId > 0) {
                MarkerOptions options2 = new MarkerOptions().position(customerPosition);
                Marker marker2 = googleMap.addMarker(options2);
                BitmapDescriptor bitmap2 = convertCharToBitmap(b.getWidth(), b.getHeight(), HelperMethods.convertToOrFromPersianDigits(String.valueOf(maxOPathId - customer.OPathId + 1)));
                marker2.setTitle(customer.CustomerName);
                marker2.setIcon(bitmap2);
                marker2.setTag(customer.UniqueId.toString());
                markers.add(marker2);
            }
        }
    }

    BitmapDescriptor convertCharToBitmap(int w, int h, String ch) {
        Bitmap bitmap = Bitmap.createBitmap(w, 2 * h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(2 * w / 3);
        textPaint.setColor(Color.BLACK);
        canvas.drawText(ch, (w / 2) - (textPaint.getTextSize() / 2), 2 * h, textPaint);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void createMarkers(Type type) {
        Context context = getContext();
        List<CustomerModel> customerModels = new CustomerManager(getContext()).getAll();
        maxOPathId = 0;
        for (CustomerModel customer :
                customerModels) {
            if (customer.OPathId > maxOPathId)
                maxOPathId = customer.OPathId;
        }

        if (context != null) {
            CustomerPathViewManager customerManager = new CustomerPathViewManager(getContext());
            if (type == Type.All) {
                List<CustomerPathViewModel> customers = customerManager.getItems(CustomerPathViewManager.getAll(null, CustomersFragment.setCheckConfirmStatus(null)));
                Linq.forEach(markers, new Linq.Consumer<Marker>() {
                    @Override
                    public void run(Marker item) {
                        item.remove();
                    }
                });
                markers = new ArrayList<>();
                for (CustomerPathViewModel customer :
                        customers) {
                    createMarker(customer);
                }
            } else if (type == Type.Today) {
                TourManager tourManager = new TourManager(getContext());
                TourModel tourModel = tourManager.loadTour();
                List<CustomerPathViewModel> customerPaths = customerManager.getItems(CustomerPathViewManager.getDay(tourModel.DayVisitPathId, false));

                Linq.forEach(markers, new Linq.Consumer<Marker>() {
                    @Override
                    public void run(Marker item) {
                        item.remove();
                    }
                });
                markers = new ArrayList<>();
                for (CustomerPathViewModel customer :
                        customerPaths) {
                    createMarker(customer);
                }
            } else if (type == Type.None) {
                Linq.forEach(markers, new Linq.Consumer<Marker>() {
                    @Override
                    public void run(Marker item) {
                        item.remove();
                    }
                });
                markers = new ArrayList<>();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        optimiseBtn = view.findViewById(R.id.optimize_path_btn);
        customerInfoView = view.findViewById(R.id.customer_info_layout);
        locationView = view.findViewById(R.id.location_layout);
        customerNameTextView = (TextView) view.findViewById(R.id.customer_name_text_view);
        customerAddressTextView = (TextView) view.findViewById(R.id.customer_address_text_view);
        customerStatusTextView = (TextView) view.findViewById(R.id.customer_status_text_view);
        customerTelTextView = (TextView) view.findViewById(R.id.customer_tel_text_view);
        currentTextView = (TextView) view.findViewById(R.id.current_address_text_view);
        view.findViewById(R.id.back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().popFragment();
            }
        });
        view.findViewById(R.id.drawer_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().toggleDrawer();
            }
        });
        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to getUnits the map to display immediately

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                try {
                    createMarkers(Type.Today);
                    googleMap.setMyLocationEnabled(true);
                    googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                        @Override
                        public void onCameraMoveStarted(int i) {
                            hideCustomerInfo();
                            if (!myLocationClicked && !locationUpdated) {
                                isDragging = true;
                            } else
                                isDragging = false;
                            myLocationClicked = false;
                        }
                    });
                    googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            myLocationClicked = true;
                            hideCustomerInfo();
                            return false;
                        }
                    });
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            String str = (String) marker.getTag();
                            if (str != null && !str.isEmpty()) {
                                UUID customerId = UUID.fromString(str);
                                CustomerManager customermanager = new CustomerManager(getContext());
                                CustomerModel customer = customermanager.getItem(customerId);
                                showCustomerInfo(customer);
                            }
                            marker.showInfoWindow();
                            return true;
                        }
                    });

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("OPTIMIZED_PATH", Context.MODE_PRIVATE);
                    int count = sharedPreferences.getInt("count", 0);
                    List<LatLng> list = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        String s = sharedPreferences.getString("point-" + i, null);
                        String[] ss = s.split(",");
                        list.add(new LatLng(Double.parseDouble(ss[0]), Double.parseDouble(ss[1])));
                    }
                    if (list.size() > 2)
                        createPathPolyLine(list);

                    createPolygons();
                    createMarkers(Type.All);
                } catch (SecurityException ex) {
                    Timber.e(ex);
                }
            }
        });

        return view;
    }

    private void hideTracking() {
        if (trackingMapHelper != null)
            trackingMapHelper.removeMarkers();

    }

    private void showTracking(boolean tracking) {
        Calendar from = Calendar.getInstance();
        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);

        Calendar to = (Calendar) from.clone();
        to.add(Calendar.DAY_OF_MONTH, 1);

        VaranegarActivity activity = getVaranegarActvity();
        if (!activity.isFinishing()) {
            List<LocationModel> locations = new LocationManager(activity).getLocations(from.getTime(), to.getTime(), tracking, false);
            if (!activity.isFinishing() && googleMap != null) {
                trackingMapHelper = new MapHelper(activity);
                trackingMapHelper.setGoogleMap(googleMap);
                trackingMapHelper.setDrawLines(true);
                trackingMapHelper.setMarkers(create(activity, locations, true));
                trackingMapHelper.draw(googleMap.getMaxZoomLevel());
            }
        }
    }

    public static List<TrackingMarker> create(Activity activity, List<LocationModel> locations, boolean showPoints) {
        List<TrackingMarker> markers = new ArrayList<>();
        LocationManager locationManager = new LocationManager(activity);
        int idx = 0;
        for (LocationModel current :
                locations) {
            BaseLocationViewModel locationViewModel = locationManager.convert(current);
            if (locationViewModel != null) {
                TrackingMarker marker = null;
                if (locationViewModel instanceof LackOfOrderLocationViewModel)
                    marker = new LackOfOrderMarker(activity, (LackOfOrderLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof LackOfVisitLocationViewModel)
                    marker = new LackOfVisitMarker(activity, (LackOfVisitLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof OrderLocationViewModel)
                    marker = new OrderMarker(activity, (OrderLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof WifiOffLocationViewModel)
                    marker = new WifiOffMarker(activity, (WifiOffLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof WifiOnLocationViewModel)
                    marker = new WifiOnMarker(activity, (WifiOnLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof BatteryLowLocationViewModel)
                    marker = new BatteryLowMarker(activity, (BatteryLowLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof GpsProviderOffLocationViewModel)
                    marker = new GPSOffMarker(activity, (GpsProviderOffLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof GpsProviderOnLocationViewModel)
                    marker = new GPSOnMarker(activity, (GpsProviderOnLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof WaitLocationViewModel)
                    marker = new WaitMarker(activity, (WaitLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof StartTourLocationViewModel)
                    marker = new StartTourMarker(activity, (StartTourLocationViewModel) locationViewModel);
                else if (locationViewModel instanceof SendTourLocationViewModel)
                    marker = new SendTourMarker(activity, (SendTourLocationViewModel) locationViewModel);
                else {
                    if (idx == 0)
                        marker = new TrackingPointMarker(activity, locationViewModel, TrackingPointMarker.PointType.Start);
                    else if (idx == locations.size() - 1)
                        marker = new TrackingPointMarker(activity, locationViewModel, TrackingPointMarker.PointType.End);
                    else if (showPoints)
                        marker = new TrackingPointMarker(activity, locationViewModel, TrackingPointMarker.PointType.Normal);
                }
                if (marker != null) {
                    markers.add(marker);
                }
                idx++;
            }
        }
        return markers;
    }

    private void createPolygons() {
        RegionAreaPointManager manager = new RegionAreaPointManager(getContext());
        Area regionArea = manager.getRegion();
        if (regionArea != null) {
            List<LatLng> latLongs = regionArea.getLatLongs();
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.addAll(latLongs);
            rectOptions.strokeColor(HelperMethods.getColor(getContext(), R.color.red));
            rectOptions.strokeWidth(4);
            googleMap.addPolygon(rectOptions);
        }

        Area dayPath = manager.getDayPath();
        if (dayPath != null) {
            List<LatLng> latLongs = dayPath.getLatLongs();
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.addAll(latLongs);
            rectOptions.strokeColor(HelperMethods.getColor(getContext(), R.color.green));
            rectOptions.fillColor(HelperMethods.getColor(getContext(), R.color.green_light_light_transparent));
            rectOptions.strokeWidth(2);
            googleMap.addPolygon(rectOptions);
        }
    }

    private void showCustomerInfo(final CustomerModel customer) {
        Context context = getContext();
        if (context != null && customer != null && customer.UniqueId != null) {
            locationView.setVisibility(View.GONE);
            customerInfoView.setVisibility(View.VISIBLE);
            customerNameTextView.setText(customer.CustomerName);
            customerAddressTextView.setText(customer.Address);
            customerTelTextView.setText(customer.Phone);
            CustomerCallManager callManager = new CustomerCallManager(context);
            List<CustomerCallModel> calls = callManager.loadCalls(customer.UniqueId);
            String statusName = callManager.getName(calls, VaranegarApplication.is(VaranegarApplication.AppId.Contractor));
            customerStatusTextView.setText(statusName);
        }
    }

    private void hideCustomerInfo() {
        locationView.setVisibility(View.VISIBLE);
        customerInfoView.setVisibility(View.GONE);
        customerNameTextView.setText("");
        customerAddressTextView.setText("");
        customerStatusTextView.setText("");
        customerTelTextView.setText("");
    }

    void updateCurrentAddress() {
        AddressThread thread = new AddressThread();
        thread.start();
    }

    void gotoCurrentLocation() {
        // For dropping a marker at a point on the Map
        if (customerId != null) {
            CustomerModel customer = new CustomerManager(getContext()).getItem(UUID.fromString(customerId));
            LatLng latLng = new LatLng(customer.Latitude, customer.Longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            customerId = null;
        } else if (lastLocation != null) {
            LatLng myLatLong = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            if (!isDragging) {
                // For zooming automatically to the location of the marker
                locationUpdated = true;
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLong).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                locationUpdated = false;
            }
        } else {
            Toast.makeText(getContext(), R.string.location_is_not_available, Toast.LENGTH_LONG).show();
        }
    }

    void startLocationUpdate() {
        Context context = getContext();
        if (context != null) {
            client = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(connectionCallBack)
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

    public class AddressThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (lastLocation != null) {
                Context context = getContext();
                if (context != null) {
                    if (!Connectivity.isConnected(getContext())) {
                        Timber.d("Missing internet connection");
                        VaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing() && isResumed())
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentTextView.setText(R.string.device_is_diconnected);
                                }
                            });
                        return;
                    }
                    try {
                        final List<Address> addresses = geocoder.getFromLocation(
                                lastLocation.getLatitude(),
                                lastLocation.getLongitude(),
                                1);

                        VaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing() && isResumed())
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (addresses != null && addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        List<String> addressFragments = new ArrayList<>();
                                        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                            addressFragments.add(address.getAddressLine(i));
                                        }
                                        if (isLandscape())
                                            currentTextView.setText(TextUtils.join(", ",
                                                    addressFragments));
                                        else
                                            currentTextView.setText(TextUtils.join(System.getProperty("line.separator"),
                                                    addressFragments));
                                    }
                                }
                            });

                    } catch (Exception ex) {
                        if (ex instanceof IOException)
                            Timber.e("Address is not available: " + ex.getMessage());
                        else
                            Timber.e(ex);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        connectionCallBack = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (isResumed()) {
                    try {
                        LocationRequest request = LocationRequest.create();
                        request.setInterval(2000);
                        request.setSmallestDisplacement(2);
                        Activity activity = getActivity();
                        if (activity != null) {
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
                            fusedLocationProviderClient.requestLocationUpdates(LocationManager.getLocationRequest(getContext()), new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    lastLocation = locationResult.getLastLocation();
                                    MainVaranegarActivity activity = getVaranegarActvity();
                                    if (isResumed() && activity != null && !activity.isFinishing()) {
                                        updateCurrentAddress();
                                        gotoCurrentLocation();
                                    }
                                }
                            }, Looper.myLooper())
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Timber.e(e);
                                            Context context = getContext();
                                            if (context != null)
                                                TrackingLogManager.addLog(context, LogType.PROVIDER, LogLevel.Error, "requesting location updates failed", e.getMessage());

                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Timber.d("point success");
                                    Context context = getContext();
                                    if (context != null)
                                        TrackingLogManager.addLog(context, LogType.PROVIDER, LogLevel.Info, "requesting location updates success");
                                }
                            });
                        }
                    } catch (SecurityException ex) {
                        Timber.e(ex);
                    }
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Timber.d("location connection suspended");
            }
        };
        startLocationUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        client.unregisterConnectionCallbacks(connectionCallBack);
        client.disconnect();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        client.unregisterConnectionCallbacks(connectionCallBack);
        client.disconnect();
        fusedLocationProviderClient = null;
        client = null;
        Runtime.getRuntime().gc();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        client.unregisterConnectionCallbacks(connectionCallBack);
        client.disconnect();
        Runtime.getRuntime().gc();
    }
}
