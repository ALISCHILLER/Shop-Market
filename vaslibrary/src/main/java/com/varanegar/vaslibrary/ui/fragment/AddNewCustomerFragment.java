package com.varanegar.vaslibrary.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.Validator;
import com.varanegar.framework.validation.annotations.IraniNationalCode;
import com.varanegar.framework.validation.annotations.Length;
import com.varanegar.framework.validation.annotations.PhoneNumber;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.enums.NewCustomerConfigType;
import com.varanegar.vaslibrary.manager.CustomerOwnerTypeManager;
import com.varanegar.vaslibrary.manager.StateManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.city.CityManager;
import com.varanegar.vaslibrary.manager.customer.CustomerActivityManager;
import com.varanegar.vaslibrary.manager.customer.CustomerCategoryManager;
import com.varanegar.vaslibrary.manager.customer.CustomerLevelManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.CustomersUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.city.CityModel;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerCategoryModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerTypeModel;
import com.varanegar.vaslibrary.model.state.StateModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.vaslibrary.webapi.customer.SyncGetNewCustomerViewModel;
import com.varanegar.vaslibrary.webapi.customer.SyncGuidViewModel;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public class AddNewCustomerFragment extends VaranegarFragment implements ValidationListener {

    Validator validator;
    @Length(min = 3, max = 100)
    public PairedItemsEditable firstName;
    @Length(min = 3, max = 100)
    public PairedItemsEditable storeName;
    @Length(min = 3, max = 100)
    public PairedItemsEditable lastName;
    @PhoneNumber
    public PairedItemsEditable mobile;
    @PhoneNumber
    public PairedItemsEditable tel;
    @Length(min = 15, max = 1000)
    public PairedItemsEditable address;
    @IraniNationalCode
    public PairedItemsEditable nationalCode;
    private PairedItemsEditable postalCode, cityZone;
    private ProgressDialog progressDialog;
    private PairedItemsSpinner<CustomerLevelModel> customerLevelsSpinner;
    private PairedItemsSpinner<CustomerCategoryModel> customerCategoriesSpinner;
    private PairedItemsSpinner<CustomerActivityModel> customerActivitiesSpinner;
    private PairedItemsSpinner<StateModel> satesSpinner;
//    private PairedItemsSpinner<CountyModel> countiesSpinner;
    private PairedItemsSpinner<CustomerOwnerTypeModel> customerOwnerTypesSpinner;
    private PairedItemsSpinner<CityModel> cityPairedItemSpinner;
    private ConfigMap configMap;
    SyncGetNewCustomerViewModel syncGetNewCustomerViewModel = new SyncGetNewCustomerViewModel();
    private BackOfficeType backOfficeType;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            activity.removeDrawer();
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            backOfficeType = sysConfigManager.getBackOfficeType();
            validator = new Validator();
            final View view = inflater.inflate(R.layout.fragment_new_customer, container, false);
            configMap = sysConfigManager.read(SysConfigManager.cloud);
            storeName = view.findViewById(R.id.store_name_paired_item);
            firstName = view.findViewById(R.id.first_name_paired_item);
            lastName = view.findViewById(R.id.last_name_paired_item);
            mobile = view.findViewById(R.id.mobile_paired_item);
            tel = view.findViewById(R.id.tel_paired_item);
            address = view.findViewById(R.id.address_paired_item);
            nationalCode = view.findViewById(R.id.national_code);
            if (configMap.compare(ConfigKey.CustomerNationalCode, NewCustomerConfigType.Hidden.toString()))
                nationalCode.setVisibility(View.GONE);

            postalCode = view.findViewById(R.id.postal_code);
            if (configMap.compare(ConfigKey.CustomerPostalCode, NewCustomerConfigType.Hidden.toString()))
                postalCode.setVisibility(View.GONE);

            cityZone = view.findViewById(R.id.city_zone_editable);
            if (configMap.compare(ConfigKey.CustomerCityZone, NewCustomerConfigType.Hidden.toString()))
                cityZone.setVisibility(View.GONE);

            customerLevelsSpinner = view.findViewById(R.id.customer_level_spinner);
            if (backOfficeType == BackOfficeType.Rastak)
                customerLevelsSpinner.setTitle(getContext().getString(R.string.sub_group_1));

            if (configMap.compare(ConfigKey.CustomerLevel, NewCustomerConfigType.Hidden.toString())) {
                customerLevelsSpinner.setVisibility(View.GONE);
            } else {
                CustomerLevelManager customerLevelManager = new CustomerLevelManager(getContext());
                List<CustomerLevelModel> customerLevelModels = customerLevelManager.getAll();
                customerLevelsSpinner.setup(getFragmentManager(), customerLevelModels, new SearchBox.SearchMethod<CustomerLevelModel>() {
                    @Override
                    public boolean onSearch(CustomerLevelModel item, String text) {
                        if (item.CustomerLevelName == null)
                            return false;
                        String str = HelperMethods.persian2Arabic(text);
                        return str == null || str.isEmpty() || item.CustomerLevelName.contains(str);
                    }
                });
                customerLevelsSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<CustomerLevelModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerLevelModel item) {
                        syncGetNewCustomerViewModel.CustomerLevelId = item.BackOfficeId;
                    }
                });
            }

            customerCategoriesSpinner = view.findViewById(R.id.customer_category_spinner);
            if (backOfficeType == BackOfficeType.Rastak)
                customerCategoriesSpinner.setTitle(getString(R.string.customer_group));
            if (configMap.compare(ConfigKey.CustomerCategory, NewCustomerConfigType.Hidden.toString())) {
                customerCategoriesSpinner.setVisibility(View.GONE);
            } else {
                CustomerCategoryManager customerCategoryManager = new CustomerCategoryManager(getContext());
                List<CustomerCategoryModel> customerCategoryModels = customerCategoryManager.getAll();
                customerCategoriesSpinner.setup(getFragmentManager(), customerCategoryModels, new SearchBox.SearchMethod<CustomerCategoryModel>() {
                    @Override
                    public boolean onSearch(CustomerCategoryModel item, String text) {
                        if (item.CustomerCategoryName == null)
                            return false;
                        String str = HelperMethods.persian2Arabic(text);
                        return str == null || str.isEmpty() || item.CustomerCategoryName.contains(str);
                    }
                });
                customerCategoriesSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<CustomerCategoryModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerCategoryModel item) {
                        syncGetNewCustomerViewModel.CustomerCategoryId = item.BackOfficeId;
                    }
                });
            }

            customerActivitiesSpinner = view.findViewById(R.id.customer_activity_spinner);
            if (backOfficeType == BackOfficeType.Rastak)
                customerActivitiesSpinner.setTitle(getContext().getString(R.string.sub_group_2));
            if (configMap.compare(ConfigKey.CustomerActivity, NewCustomerConfigType.Hidden.toString())) {
                customerActivitiesSpinner.setVisibility(View.GONE);
            } else {
                CustomerActivityManager customerActivityManager = new CustomerActivityManager(getContext());
                List<CustomerActivityModel> customerActivityModels = customerActivityManager.getAll();
                customerActivitiesSpinner.setup(getFragmentManager(), customerActivityModels, new SearchBox.SearchMethod<CustomerActivityModel>() {
                    @Override
                    public boolean onSearch(CustomerActivityModel item, String text) {
                        if (item.CustomerActivityName == null)
                            return false;
                        String str = HelperMethods.persian2Arabic(text);
                        return str == null || str.isEmpty() || item.CustomerActivityName.contains(str);
                    }
                });
                customerActivitiesSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<CustomerActivityModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerActivityModel item) {
                        syncGetNewCustomerViewModel.CustomerActivityId = item.BackOfficeId;
                    }
                });
            }

            satesSpinner = view.findViewById(R.id.state_spinner);
            cityPairedItemSpinner = view.findViewById(R.id.city_spinner);
            if (configMap.compare(ConfigKey.CustomerState, NewCustomerConfigType.Hidden.toString())) {
                satesSpinner.setVisibility(View.GONE);
            } else {
                StateManager stateManager = new StateManager(getContext());
                List<StateModel> stateModels = stateManager.getAll();
                satesSpinner.setup(getFragmentManager(), stateModels, new SearchBox.SearchMethod<StateModel>() {
                    @Override
                    public boolean onSearch(StateModel item, String text) {
                        String str = HelperMethods.persian2Arabic(text);
                        if (str == null)
                            return true;
                        str = str.toLowerCase();
                        return item.toString().toLowerCase().contains(str);
                    }
                });
                satesSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<StateModel>() {
                    @Override
                    public void onItemSelected(int position, StateModel item) {
                        syncGetNewCustomerViewModel.StateId = item.BackOfficeId;
                        cityPairedItemSpinner.setVisibility(View.VISIBLE);
                        if (configMap.compare(ConfigKey.CustomerCity, NewCustomerConfigType.Hidden.toString())) {
                            cityPairedItemSpinner.setVisibility(View.GONE);
                        } else {
                            CityManager cityManager = new CityManager(getContext());
                            List<CityModel> cityModels = cityManager.getSatesCities(item.UniqueId);
                            cityPairedItemSpinner.setup(getFragmentManager(), cityModels, new SearchBox.SearchMethod<CityModel>() {
                                @Override
                                public boolean onSearch(CityModel item, String text) {
                                    String str = HelperMethods.persian2Arabic(text);
                                    if (str == null)
                                        return true;
                                    str = str.toLowerCase();
                                    return item.toString().toLowerCase().contains(str);
                                }
                            });
                            cityPairedItemSpinner.deselect();
                            syncGetNewCustomerViewModel.CityId = 0;
                            cityPairedItemSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<CityModel>() {
                                @Override
                                public void onItemSelected(int position, CityModel item) {
                                    syncGetNewCustomerViewModel.CityId = item.BackOfficeId;
                                }
                            });
                        }
                    }
                });
            }

//            countiesSpinner = view.findViewById(R.id.county_spinner);
//            if (configMap.compare(ConfigKey.CustomerCounty, NewCustomerConfigType.Hidden.toString())) {
//                countiesSpinner.setVisibility(View.GONE);
//            } else {
//                CountyManager countyManager = new CountyManager(getContext());
//                List<CountyModel> countyModels = countyManager.getAll();
//                countiesSpinner.setup(getFragmentManager(), countyModels, new SearchBox.SearchMethod<CountyModel>() {
//                    @Override
//                    public boolean onSearch(CountyModel item, String text) {
//                        String str = HelperMethods.persian2Arabic(text);
//                        if (str == null)
//                            return true;
//                        str = str.toLowerCase();
//                        return item.toString().toLowerCase().contains(str);
//                    }
//                });
//                countiesSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<CountyModel>() {
//                    @Override
//                    public void onItemSelected(int position, CountyModel item) {
//                        syncGetNewCustomerViewModel.CountyId = item.BackOfficeId;
//                    }
//                });
//            }

            customerOwnerTypesSpinner = view.findViewById(R.id.ownership_type_spinner);
            if (configMap.compare(ConfigKey.CustomerOwnerType, NewCustomerConfigType.Hidden.toString())) {
                customerOwnerTypesSpinner.setVisibility(View.GONE);
            } else {
                CustomerOwnerTypeManager customerOwnerTypeManager = new CustomerOwnerTypeManager(getContext());
                List<CustomerOwnerTypeModel> customerOwnerTypeModels = customerOwnerTypeManager.getAll();
                customerOwnerTypesSpinner.setup(getFragmentManager(), customerOwnerTypeModels, new SearchBox.SearchMethod<CustomerOwnerTypeModel>() {
                    @Override
                    public boolean onSearch(CustomerOwnerTypeModel item, String text) {
                        String str = HelperMethods.persian2Arabic(text);
                        if (str == null)
                            return true;
                        str = str.toLowerCase();
                        return item.toString().toLowerCase().contains(str);
                    }
                });
                customerOwnerTypesSpinner.setOnItemSelectedListener(new PairedItemsSpinner.OnItemSelectedListener<CustomerOwnerTypeModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerOwnerTypeModel item) {
                        syncGetNewCustomerViewModel.OwnerTypeId = item.BackOfficeId;
                    }
                });
            }

            view.findViewById(R.id.save_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearErrors();
                    if (!Connectivity.isConnected(getContext())) {
                        ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                        connectionSettingDialog.show(getChildFragmentManager(), "ConnectionSettingDialog");
                        return;
                    }
                    validator.validate(AddNewCustomerFragment.this);
                }
            });
            return view;

        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
            return null;
        }


    }

    void clearErrors() {
        storeName.setError(null);
        firstName.setError(null);
        lastName.setError(null);
        mobile.setError(null);
        tel.setError(null);
        address.setError(null);
        nationalCode.setError(null);
        postalCode.setError(null);
    }

    @Override
    public void onValidationSucceeded() {
//        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
//        BackOfficeType backOfficeType = null;
//        try {
//            backOfficeType = sysConfigManager.getBackOfficeType();
        String errMsg = "";
//            if (backOfficeType.equals(BackOfficeType.Rastak)) {
//                if (customerCategoriesSpinner.getSelectedItem() == null)
//                    errMsg = getString(R.string.customer_category_cannot_be_empty) + "\n";
//                if (cityPairedItemSpinner.getSelectedItem() == null)
//                    errMsg = errMsg + getString(R.string.customer_city_cannot_be_empty) + "\n";
//                if (satesSpinner.getSelectedItem() == null)
//                    errMsg = errMsg + getString(R.string.customer_state_cannot_be_empty);
//            } else if (backOfficeType.equals(BackOfficeType.Varanegar)) {
        if (firstName.getValue() == null || firstName.getValue().isEmpty())
            errMsg = getString(R.string.first_name_cannot_be_empty) + "\n";
        if (lastName.getValue() == null || lastName.getValue().isEmpty())
            errMsg = errMsg + getString(R.string.last_name_cannot_be_empty) + "\n";
        if (!(VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) && (storeName.getValue() == null || storeName.getValue().isEmpty()))
            errMsg = errMsg + getString(R.string.store_name_cannot_be_empty) + "\n";
        if (!(VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) && configMap.compare(ConfigKey.CustomerCategory, NewCustomerConfigType.Necessary.toString()) && customerCategoriesSpinner.getSelectedItem() == null)
            errMsg = errMsg + getString(R.string.customer_category_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerLevel, NewCustomerConfigType.Necessary.toString()) && customerLevelsSpinner.getSelectedItem() == null) {
            if (backOfficeType == BackOfficeType.Rastak)
                errMsg = errMsg + getString(R.string.customer_sub_group_1_cannont_be_empty) + "\n";
            else
                errMsg = errMsg + getString(R.string.customer_level_cannont_be_empty) + "\n";
        }
        if (configMap.compare(ConfigKey.CustomerActivity, NewCustomerConfigType.Necessary.toString()) && customerActivitiesSpinner.getSelectedItem() == null) {
            if (backOfficeType == BackOfficeType.Rastak)
                errMsg = errMsg + getString(R.string.customer_sub_group_2_cannot_be_empty) + "\n";
            else
                errMsg = errMsg + getString(R.string.customer_activity_cannot_be_empty) + "\n";
        }
        if (configMap.compare(ConfigKey.CustomerCityZone, NewCustomerConfigType.Necessary.toString()) && (cityZone.getValue() == null || cityZone.getValue().isEmpty()))
            errMsg = errMsg + getString(R.string.customer_city_zone_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerPostalCode, NewCustomerConfigType.Necessary.toString()) && (postalCode.getValue() == null || postalCode.getValue().isEmpty()))
            errMsg = errMsg + getString(R.string.postal_code_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerNationalCode, NewCustomerConfigType.Necessary.toString()) && (nationalCode.getValue() == null || nationalCode.getValue().isEmpty()))
            errMsg = errMsg + getString(R.string.national_code_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerCity, NewCustomerConfigType.Necessary.toString()) && cityPairedItemSpinner.getSelectedItem() == null)
            errMsg = errMsg + getString(R.string.customer_city_cannot_be_empty) + "\n";
//        if (configMap.compare(ConfigKey.CustomerCounty, NewCustomerConfigType.Necessary.toString()) && countiesSpinner.getSelectedItem() == null)
//            errMsg = errMsg + getString(R.string.customer_county_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerState, NewCustomerConfigType.Necessary.toString()) && satesSpinner.getSelectedItem() == null)
            errMsg = errMsg + getString(R.string.customer_state_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerOwnerType, NewCustomerConfigType.Necessary.toString()) && customerOwnerTypesSpinner.getSelectedItem() == null)
            errMsg = errMsg + getString(R.string.customer_owner_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerAddress, NewCustomerConfigType.Necessary.toString()) && (address.getValue() == null || address.getValue().equals("")))
            errMsg = errMsg + getString(R.string.address_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerMobile, NewCustomerConfigType.Necessary.toString()) && (mobile.getValue() == null || mobile.getValue().equals("")))
            errMsg = errMsg + getString(R.string.mobile_cannot_be_empty) + "\n";
        if (configMap.compare(ConfigKey.CustomerPhone, NewCustomerConfigType.Necessary.toString()) && (tel.getValue() == null || tel.getValue().equals("")))
            errMsg = errMsg + getString(R.string.tel_cannot_be_empty) + "\n";
        if (errMsg.equals(""))
            submit();
        else {
            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
            cuteMessageDialog.setIcon(Icon.Error);
            cuteMessageDialog.setMessage(errMsg);
            cuteMessageDialog.setNeutralButton(R.string.ok, null);
            cuteMessageDialog.show();
        }
//        } catch (UnknownBackOfficeException e) {
//            e.printStackTrace();
//        }

    }

    private void submit() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.registering_new_customer));
        progressDialog.setCancelable(false);
        progressDialog.show();
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {

                syncGetNewCustomerViewModel.FirstName = firstName.getValue();
                syncGetNewCustomerViewModel.LastName = lastName.getValue();
                syncGetNewCustomerViewModel.Address = address.getValue();
                syncGetNewCustomerViewModel.Mobile = mobile.getValue();
                syncGetNewCustomerViewModel.Phone = tel.getValue();
                if ((storeName.getValue() == null || storeName.getValue().equals("")) && (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)))
                    syncGetNewCustomerViewModel.StoreName = syncGetNewCustomerViewModel.FirstName + " " + syncGetNewCustomerViewModel.LastName;
                else
                    syncGetNewCustomerViewModel.StoreName = storeName.getValue();
                syncGetNewCustomerViewModel.NationalCode = nationalCode.getValue();
                syncGetNewCustomerViewModel.PostalCode = postalCode.getValue();
                syncGetNewCustomerViewModel.DealerId = UserManager.readFromFile(getContext()).UniqueId;
                syncGetNewCustomerViewModel.CustomerCityZoneId = Integer.valueOf((cityZone.getValue() == null || cityZone.getValue().equals("")) ? "0" : cityZone.getValue());
                TourManager tourManager = new TourManager(getContext());
                syncGetNewCustomerViewModel.pathId = tourManager.loadTour().DayVisitPathId.toString();
                final CustomerApi customerApi = new CustomerApi(getContext());
                customerApi.runWebRequest(customerApi.registerNewCustomer(syncGetNewCustomerViewModel), new WebCallBack<SyncGuidViewModel>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(final SyncGuidViewModel result, Request request) {
                        CustomersUpdateFlow flow = new CustomersUpdateFlow(getContext(), result.UniqueId);
                        flow.syncCustomersAndInitPromotionDb(new UpdateCall() {
                            @Override
                            protected void onSuccess() {
                                final CustomerManager customerManager = new CustomerManager(getActivity());
                                List<CustomerModel> customerModels = customerManager.getAll();
                                boolean customerAddedToDb = false;
                                for (final CustomerModel customerModel : customerModels) {
                                    if (customerModel.UniqueId.equals(result.UniqueId)) {
                                        customerModel.IsNewCustomer = true;
                                        customerAddedToDb = true;
                                        try {
                                            customerManager.update(customerModel);
                                        } catch (Exception e) {
                                            Timber.e(e);
                                        } finally {
                                            final MainVaranegarActivity activity = getVaranegarActvity();
                                            if (activity != null && !activity.isFinishing()) {
                                                stopProgressDialog();
                                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                                dialog.setMessage(R.string.registering_customer_completed);
                                                dialog.setTitle(R.string.done);
                                                dialog.setIcon(Icon.Success);
                                                dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        activity.popFragment();
                                                        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
                                                            android.location.LocationManager locationManager1 = (android.location.LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                                                            boolean gps = locationManager1.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
                                                            if (!gps) {
                                                                Timber.w("in contractor new customer lat and lng updated provider is not enable");
                                                                return;
                                                            }
                                                            Location lastLocation = ((VasActivity) getActivity()).getLastLocation();
                                                            if (lastLocation == null) {
                                                                Timber.w("in contractor new customer last location is null");
                                                                return;
                                                            } else {
                                                                customerModel.Latitude = lastLocation.getLatitude();
                                                                customerModel.Longitude = lastLocation.getLongitude();
                                                                try {
                                                                    customerManager.update(customerModel);
                                                                    CustomerCallManager callManager = new CustomerCallManager(getContext());
                                                                    callManager.saveLocationCall(customerModel.UniqueId);
                                                                    callManager.saveEditCustomerCall(customerModel.UniqueId);
                                                                    Timber.i("contractor new customer lat and lng updated " + customerModel.Latitude + ", " + customerModel.Longitude);
                                                                } catch (ValidationException e) {
                                                                    Timber.e("can not update contractor new customer lat and lng " + e.getMessage());
                                                                } catch (DbException e) {
                                                                    Timber.e("can not update contractor new customer lat and lng " + e.getMessage());
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        }
                                        break;
                                    }
                                }
                                if (!customerAddedToDb) {
                                    final MainVaranegarActivity activity = getVaranegarActvity();
                                    if (activity != null && !activity.isFinishing()) {
                                        stopProgressDialog();
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(R.string.registering_customer_completed_but_not_inserted_in_db);
                                        dialog.setTitle(R.string.done);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                activity.popFragment();
                                            }
                                        });
                                        dialog.show();
                                    }
                                }
                            }

                            @Override
                            protected void onFailure(String error) {
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    stopProgressDialog();
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setMessage(error);
                                    dialog.setTitle(R.string.error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            }
                        });
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            stopProgressDialog();
                            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(activity);
                            cuteMessageDialog.setIcon(Icon.Error);
                            cuteMessageDialog.setTitle(R.string.error);
                            cuteMessageDialog.setMessage(WebApiErrorBody.log(error, activity));
                            cuteMessageDialog.setNeutralButton(R.string.ok, null);
                            cuteMessageDialog.show();
                        }
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            stopProgressDialog();
                            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(activity);
                            cuteMessageDialog.setIcon(Icon.Error);
                            cuteMessageDialog.setTitle(R.string.error);
                            cuteMessageDialog.setMessage(t.getMessage());
                            cuteMessageDialog.setNeutralButton(R.string.ok, null);
                            cuteMessageDialog.show();
                        }
                    }
                });
            }

            @Override
            public void failed() {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    stopProgressDialog();
                    CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(activity);
                    cuteMessageDialog.setIcon(Icon.Error);
                    cuteMessageDialog.setTitle(R.string.error);
                    cuteMessageDialog.setMessage(R.string.network_error);
                    cuteMessageDialog.setNeutralButton(R.string.ok, null);
                    cuteMessageDialog.show();
                }
            }
        });
    }

    private void stopProgressDialog() {
        if (getVaranegarActvity() != null && progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error :
                errors) {
            String errorMessage = getString(R.string.error);
            switch (error.getViolation().getSimpleName()) {
                case "NotEmptyChecker":
                    errorMessage = getString(R.string.not_empty);
                    break;
                case "PhoneNumberChecker":
                    errorMessage = getString(R.string.phone_number_is_not_valid);
                    break;
                case "LengthChecker":
                    errorMessage = getString(R.string.length_is_not_valid);
                    break;
                case "NotNullChecker":
                    errorMessage = getString(R.string.not_empty);
                    break;
                case "IraniNationalCodeChecker":
                    errorMessage = getString(R.string.national_code_is_not_valid);
                    break;
            }

            if (error.getField() instanceof PairedItemsEditable)
                ((PairedItemsEditable) error.getField()).setError(errorMessage);
            else
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
