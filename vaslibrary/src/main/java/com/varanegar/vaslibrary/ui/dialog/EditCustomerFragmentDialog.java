package com.varanegar.vaslibrary.ui.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.Validator;
import com.varanegar.framework.validation.annotations.IraniNationalCode;
import com.varanegar.framework.validation.annotations.IraniNationalCodeChecker;
import com.varanegar.framework.validation.annotations.Length;
import com.varanegar.framework.validation.annotations.LengthChecker;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.framework.validation.annotations.PhoneNumber;
import com.varanegar.framework.validation.annotations.PhoneNumberChecker;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CustomerOwnerTypeManager;
import com.varanegar.vaslibrary.manager.StateManager;
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
import com.varanegar.vaslibrary.model.city.CityModel;
import com.varanegar.vaslibrary.model.customer.CustomerActivity;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerCategory;
import com.varanegar.vaslibrary.model.customer.CustomerCategoryModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerType;
import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerTypeModel;
import com.varanegar.vaslibrary.model.state.State;
import com.varanegar.vaslibrary.model.state.StateModel;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;


public class EditCustomerFragmentDialog extends CuteAlertDialog implements ValidationListener {
    CustomerModel customer;
    public OnCustomerEditedCallBack onCustomerEditedCallBack;
    UUID customerUniqueId;
    @IraniNationalCode
    public PairedItemsEditable customerNationalCode;
    @PhoneNumber
    public PairedItemsEditable customerMobile;
    @PhoneNumber
    public PairedItemsEditable customerPhone;
    @Length(min = 3, max = 100)
    public PairedItemsEditable customerStoreName;
    @Length(min = 20, max = 1000)
    public PairedItemsEditable customerAddress;
    public PairedItemsEditable postalCode, cityZone;
    private Validator validator;

    private PairedItemsSpinner<StateModel> satesSpinner;
    private PairedItemsSpinner<CustomerOwnerTypeModel> customerOwnerTypesSpinner;
    private PairedItemsSpinner<CityModel> cityPairedItemSpinner;
    private PairedItemsSpinner<CustomerActivityModel> customerActivitySpinner;
    private PairedItemsSpinner<CustomerLevelModel> customerLevelSpinner;
    private PairedItemsSpinner<CustomerCategoryModel> customerCategorySpinner;

    private boolean first = true;

    private ConfigMap configMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSizingPolicy(SizingPolicy.Maximum);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater, ViewGroup viewGroup,
                                       @Nullable Bundle savedInstanceState) {
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        try {
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            validator = new Validator();
            if (savedInstanceState != null)
                customerUniqueId = UUID.fromString(savedInstanceState
                        .getString("68565e5e-d407-4858-bc5f-fd52b9318734"));
            else {
                Bundle bundle = getArguments();
                customerUniqueId = UUID.fromString(bundle
                        .getString("68565e5e-d407-4858-bc5f-fd52b9318734"));
            }

            CustomerManager manager = new CustomerManager(getContext());
            customer = manager.getItem(customerUniqueId);
            manager.cacheOriginalCustomer(customer.UniqueId);
            setTitle(R.string.edit_customer_label);
            View view = inflater.inflate(R.layout.fragment_edit_customer, viewGroup, true);
            PairedItems customerCode = view.findViewById(R.id.customer_code_paired_item);
            PairedItems customerName = view.findViewById(R.id.customer_name_paired_item);
            customerStoreName = view.findViewById(R.id.customer_store_name_paired_item_editable);

            customerNationalCode = view.findViewById(R.id.national_code);
            customerMobile = view.findViewById(R.id.customer_mobile_paired_item_editable);
            customerPhone = view.findViewById(R.id.customer_tel_paired_item_editable);
            customerAddress = view.findViewById(R.id.customer_address_paired_item_editable);
            postalCode = view.findViewById(R.id.postal_code);
            cityZone = view.findViewById(R.id.city_zone_editable);
            cityPairedItemSpinner = view.findViewById(R.id.city_spinner);
            satesSpinner = view.findViewById(R.id.state_spinner);
            customerOwnerTypesSpinner = view.findViewById(R.id.ownership_type_spinner);
            customerActivitySpinner = view.findViewById(R.id.customer_activity_spinner);
            customerLevelSpinner = view.findViewById(R.id.customer_level_spinner);
            customerCategorySpinner = view.findViewById(R.id.customer_category_spinner);
            customerLevelSpinner = view.findViewById(R.id.customer_level_spinner);
            if (backOfficeType == BackOfficeType.Rastak) {
                customerActivitySpinner.setTitle(getString(R.string.sub_group_2));
                customerLevelSpinner.setTitle(getString(R.string.sub_group_1));
                customerCategorySpinner.setTitle(getString(R.string.customer_group));
            }
            CustomerActivityManager customerActivityManager = new
                    CustomerActivityManager(getContext());
            List<CustomerActivityModel> customerActivityModels =
                    customerActivityManager.getItems(new Query().from(CustomerActivity.CustomerActivityTbl));
            customerActivitySpinner.setup(getFragmentManager(),
                    customerActivityModels, new SearchBox.SearchMethod<CustomerActivityModel>() {
                @Override
                public boolean onSearch(CustomerActivityModel item, String text) {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.toString().toLowerCase().contains(str);
                }
            });
            for (int i = 0; i < customerActivityModels.size(); i++) {
                if (customerActivityModels.get(i).UniqueId.equals(customer.CustomerActivityId))
                    customerActivitySpinner.selectItem(i);
            }

            CustomerLevelManager customerLevelManager = new CustomerLevelManager(getContext());
            List<CustomerLevelModel> customerLevelModels =
                    customerLevelManager.getItems(new Query().from(CustomerLevel.CustomerLevelTbl));
            customerLevelSpinner.setup(getFragmentManager(),
                    customerLevelModels, new SearchBox.SearchMethod<CustomerLevelModel>() {
                @Override
                public boolean onSearch(CustomerLevelModel item, String text) {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.toString().toLowerCase().contains(str);
                }
            });
            for (int i = 0; i < customerLevelModels.size(); i++) {
                if (customerLevelModels.get(i).UniqueId.equals(customer.CustomerLevelId))
                    customerLevelSpinner.selectItem(i);
            }

            CustomerCategoryManager customerCategoryManager = new
                    CustomerCategoryManager(getContext());
            List<CustomerCategoryModel> customerCategoryModels =
                    customerCategoryManager.getItems(new
                            Query().from(CustomerCategory.CustomerCategoryTbl));
            customerCategorySpinner.setup(getFragmentManager(),
                    customerCategoryModels, new SearchBox.SearchMethod<CustomerCategoryModel>() {
                @Override
                public boolean onSearch(CustomerCategoryModel item, String text) {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.toString().toLowerCase().contains(str);
                }
            });
            for (int i = 0; i < customerCategoryModels.size(); i++) {
                if (customerCategoryModels.get(i).UniqueId.equals(customer.CustomerCategoryId))
                    customerCategorySpinner.selectItem(i);
            }

            StateManager stateManager = new StateManager(getContext());
            List<StateModel> stateModels = stateManager.getAll();
            satesSpinner.setup(getFragmentManager(), stateModels,
                    new SearchBox.SearchMethod<StateModel>() {
                @Override
                public boolean onSearch(StateModel item, String text) {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.toString().toLowerCase().contains(str);
                }
            });
            for (int i = 0; i < stateModels.size(); i++) {
                if (stateModels.get(i).UniqueId.equals(customer.StateId)) {
                    satesSpinner.selectItem(i);
                    CityManager cityManager = new CityManager(getContext());
                    List<CityModel> cityModels = cityManager
                            .getSatesCities(stateModels.get(i).UniqueId);
                    cityPairedItemSpinner.setup(getFragmentManager(),
                            cityModels, new SearchBox.SearchMethod<CityModel>() {
                        @Override
                        public boolean onSearch(CityModel item, String text) {
                            String str = HelperMethods.persian2Arabic(text);
                            if (str == null)
                                return true;
                            str = str.toLowerCase();
                            return item.toString().toLowerCase().contains(str);
                        }
                    });
                    for (int j = 0; j < cityModels.size(); j++) {
                        if (cityModels.get(j).UniqueId.equals(customer.CityId))
                            cityPairedItemSpinner.selectItem(j);
                    }
                }
            }

            CustomerOwnerTypeManager customerOwnerTypeManager = new CustomerOwnerTypeManager(getContext());
            List<CustomerOwnerTypeModel> customerOwnerTypeModels =
                    customerOwnerTypeManager.getItems(new Query().from(CustomerOwnerType.CustomerOwnerTypeTbl));
            customerOwnerTypesSpinner.setup(getFragmentManager(),
                    customerOwnerTypeModels, new SearchBox.SearchMethod<CustomerOwnerTypeModel>() {
                @Override
                public boolean onSearch(CustomerOwnerTypeModel item, String text) {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.toString().toLowerCase().contains(str);
                }
            });
            for (int i = 0; i < customerOwnerTypeModels.size(); i++) {
                if (customerOwnerTypeModels.get(i).BackOfficeId == customer.OwnerTypeRef)
                    customerOwnerTypesSpinner.selectItem(i);
            }

            customerStoreName.setValue(customer.StoreName);
            customerName.setValue(customer.CustomerName);
            customerCode.setValue(customer.CustomerCode);
            customerNationalCode.setValue(customer.NationalCode);
            customerMobile.setValue(customer.Mobile);
            customerPhone.setValue(customer.Phone);
            customerAddress.setValue(customer.Address);
            postalCode.setValue(customer.CustomerPostalCode);
            cityZone.setValue(String.valueOf(customer.CityZone));

            configMap = sysConfigManager.read(SysConfigManager.cloud);

            if (configMap.compare(ConfigKey.EditCustomerAddress, false))
                customerAddress.setEnabled(false);
            if (configMap.compare(ConfigKey.EditCustomerMobile, false))
                customerMobile.setEnabled(false);
            if (configMap.compare(ConfigKey.EditCustomerPhone, false))
                customerPhone.setEnabled(false);
            if (configMap.compare(ConfigKey.EditCustomerICode, false))
                customerNationalCode.setEnabled(false);
            if (configMap.compare(ConfigKey.EditCustomerStoreName, false))
                customerStoreName.setEnabled(false);
            if (configMap.compare(ConfigKey.EditCustomerCityZone, false))
                cityZone.setEnabled(false);
            if (configMap.compare(ConfigKey.EditCustomerCity, false))
                cityPairedItemSpinner.setEnabled(false);
            else
                cityPairedItemSpinner.setOnItemSelectedListener(
                        new PairedItemsSpinner.OnItemSelectedListener<CityModel>() {
                    @Override
                    public void onItemSelected(int position, CityModel item) {
                        customer.CityId = item.UniqueId;
                    }
                });
//            if (configMap.compare(ConfigKey.EditCustomerCounty, false))
//                countiesSpinner.setEnabled(false);
//            else
//                countiesSpinner.setOnItemSelectedListener(
//                new PairedItemsSpinner.OnItemSelectedListener<CountyModel>() {
//                    @Override
//                    public void onItemSelected(int position, CountyModel item) {
//                        customer.CountyId = item.UniqueId;
//                    }
//                });
            if (configMap.compare(ConfigKey.EditCustomerState, false))
                satesSpinner.setEnabled(false);
            else
                satesSpinner.setOnItemSelectedListener(
                        new PairedItemsSpinner.OnItemSelectedListener<StateModel>() {
                    @Override
                    public void onItemSelected(int position, StateModel item) {
                        customer.StateId = item.UniqueId;
                        CityManager cityManager = new CityManager(getContext());
                        List<CityModel> cityModels = cityManager.getSatesCities(item.UniqueId);
                        cityPairedItemSpinner.setup(getFragmentManager(), cityModels,
                                new SearchBox.SearchMethod<CityModel>() {
                            @Override
                            public boolean onSearch(CityModel item, String text) {
                                String str = HelperMethods.persian2Arabic(text);
                                if (str == null)
                                    return true;
                                str = str.toLowerCase();
                                return item.toString().toLowerCase().contains(str);
                            }
                        });
                        cityPairedItemSpinner.selectItem(0);
                        customer.CityId = cityModels.get(0).UniqueId;
                    }
                });
            if (configMap.compare(ConfigKey.EditCustomerOwnerType, false))
                customerOwnerTypesSpinner.setEnabled(false);
            else
                customerOwnerTypesSpinner.setOnItemSelectedListener(
                        new PairedItemsSpinner.OnItemSelectedListener<CustomerOwnerTypeModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerOwnerTypeModel item) {
                        customer.OwnerTypeRef = item.BackOfficeId;
                    }
                });
            if (configMap.compare(ConfigKey.EditCustomerActivity, false))
                customerActivitySpinner.setEnabled(false);
            else
                customerActivitySpinner.setOnItemSelectedListener(
                        new PairedItemsSpinner.OnItemSelectedListener<CustomerActivityModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerActivityModel item) {
                        customer.CustomerActivityId = item.UniqueId;
                        customer.CustomerActivityRef = item.BackOfficeId;
                    }
                });
            if (configMap.compare(ConfigKey.EditCustomerLevel, false))
                customerLevelSpinner.setEnabled(false);
            else
                customerLevelSpinner.setOnItemSelectedListener(
                        new PairedItemsSpinner.OnItemSelectedListener<CustomerLevelModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerLevelModel item) {
                        customer.CustomerLevelId = item.UniqueId;
                        customer.CustomerLevelRef = item.BackOfficeId;
                    }
                });
            if (configMap.compare(ConfigKey.EditCustomerCategory, false))
                customerCategorySpinner.setEnabled(false);
            else
                customerCategorySpinner.setOnItemSelectedListener(
                        new PairedItemsSpinner.OnItemSelectedListener<CustomerCategoryModel>() {
                    @Override
                    public void onItemSelected(int position, CustomerCategoryModel item) {
                        customer.CustomerCategoryId = item.UniqueId;
                        customer.CustomerCategoryRef = item.BackOfficeId;
                    }
                });
            if (configMap.compare(ConfigKey.EditCustomerPostalCode, false))
                postalCode.setEnabled(false);
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }

    }

    @Override
    public void ok() {
        validator.validate(this);
    }

    @Override
    public void cancel() {

    }

    @Override
    public void onValidationSucceeded() {
        String errMsg = "";
        if (!(VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) &&
                (customerStoreName.getValue() == null || customerStoreName.getValue().isEmpty()))
            errMsg = errMsg + getString(R.string.store_name_cannot_be_empty) + "\n";
        if (errMsg.equals("")) {
            customer.Phone = customerPhone.getValue();
            customer.NationalCode = customerNationalCode.getValue();
            customer.Address = customerAddress.getValue();
            customer.Mobile = customerMobile.getValue();
            customer.StoreName = customerStoreName.getValue();
            customer.CustomerPostalCode = postalCode.getValue();
            if (cityZone.getValue() != null && !(cityZone.getValue().equals("")))
                customer.CityZone = Integer.parseInt(cityZone.getValue());
            else
                customer.CityZone = 0;
            try {
                new CustomerManager(getContext()).update(customer);
                getDialog().dismiss();
                new CustomerCallManager(getContext()).saveEditCustomerCall(customerUniqueId);
                if (onCustomerEditedCallBack != null)
                    onCustomerEditedCallBack.done();
                dismiss();
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.error_saving_request, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(getContext());
            cuteMessageDialog.setIcon(Icon.Error);
            cuteMessageDialog.setMessage(errMsg);
            cuteMessageDialog.setNeutralButton(R.string.ok, null);
            cuteMessageDialog.show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error :
                errors) {
            String errorMessage = getString(R.string.error);
            if (error.getViolation().equals(NotEmptyChecker.class))
                errorMessage = getString(R.string.not_empty);
            if (error.getViolation().equals(IraniNationalCodeChecker.class))
                errorMessage = getString(R.string.national_code_is_not_valid);
            if (error.getViolation().equals(LengthChecker.class))
                errorMessage = getString(R.string.length_is_not_valid);
            if (error.getViolation().equals(PhoneNumberChecker.class))
                errorMessage = getString(R.string.phone_number_is_not_valid);

            if (error.getField() instanceof PairedItemsEditable) {
                ((PairedItemsEditable) error.getField()).setError(errorMessage);
                ((PairedItemsEditable) error.getField()).requestFocus();
            } else
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnCustomerEditedCallBack {
        void done();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("68565e5e-d407-4858-bc5f-fd52b9318734", customer.UniqueId.toString());
    }
}