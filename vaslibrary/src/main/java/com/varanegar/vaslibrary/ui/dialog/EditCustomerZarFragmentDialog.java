package com.varanegar.vaslibrary.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteAlertDialog;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.FormValidator;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.annotations.IraniNationalCodeChecker;
import com.varanegar.framework.validation.annotations.LengthChecker;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.DataForRegisterManager;
import com.varanegar.vaslibrary.manager.canvertType.ConvertFaNumType;
import com.varanegar.vaslibrary.manager.city.CityManager;
import com.varanegar.vaslibrary.manager.customer.CustomerActivityManager;
import com.varanegar.vaslibrary.manager.customer.CustomerCategoryManager;
import com.varanegar.vaslibrary.manager.customer.CustomerLevelManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.updatemanager.CustomersUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.city.CityModel;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerCategoryModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;
import com.varanegar.vaslibrary.ui.dialog.new_dialog.SingleChoiceDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeCustomerRequestViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeViewModel;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.vaslibrary.webapi.customer.SyncZarGetNewCustomerViewModel;
import com.varanegar.vaslibrary.webapi.customer.ZarCustomerInfoViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class EditCustomerZarFragmentDialog extends CuteAlertDialog implements ValidationListener
        ,SingleChoiceDialog.SingleChoiceListener {
    CustomerModel customer;
    public OnCustomerEditedCallBack onCustomerEditedCallBack;
    UUID customerUniqueId;
    private ProgressDialog progressDialog;
    private PairedItemsEditable personNamePairedItem;
    private PairedItemsEditable tabloNamePairedItem;
    private PairedItemsEditable addressPairedItem;
    private PairedItemsEditable street2PairedItem;
    private PairedItemsEditable street3PairedItem;
    private PairedItemsEditable street4PairedItem;
    private PairedItemsEditable street5PairedItem;
    private PairedItemsEditable postalCodePairedItem;
    private PairedItemsEditable code_naghsh_paired_item;
    private Button request_codenaghsh;
    //    private PairedItemsSpinner<CityModel> citySpinner;
    private PairedItemsSpinner<DataForRegisterModel> deliveryZoneSpinner;
    private PairedItemsEditable telPairedItem;
    private PairedItemsEditable mobilePairedItem;
    private PairedItemsEditable economicCodePairedItem;
    private PairedItemsEditable nationalCodePairedItem;
    private PairedItemsSpinner<DataForRegisterModel> customerDegreeSpinner;
    private PairedItemsSpinner<DataForRegisterModel> saleZonesSpinner;
    //    private PairedItemsSpinner<DataForRegisterModel> customerGroupSpinner;
    private PairedItemsSpinner<DataForRegisterModel> customerGroup1Spinner;
    private PairedItemsSpinner<DataForRegisterModel> customerGroup2Spinner;
    private PairedItemsEditable cityNamePairedItem;
    private ZarCustomerInfoViewModel customerInfo;
    private Call<ZarCustomerInfoViewModel> call;
    private boolean isEnabled;
    private FormValidator validator;
    private SyncZarGetNewCustomerViewModel syncGetNewCustomerViewModel;
    private List<RoleCodeViewModel> roleCodeViewModels;
    private RoleCodeCustomerRequestViewModel roleCodeCustomerViewModel ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSizingPolicy(SizingPolicy.Maximum);
        validator = new FormValidator(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }

    @Override
    protected void onCreateContentView(LayoutInflater inflater,
                                       ViewGroup viewGroup,
                                       @Nullable Bundle savedInstanceState) {

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
        View view = inflater.inflate(R.layout.fragment_edit_customer_zar, viewGroup, true);

        personNamePairedItem = view.findViewById(R.id.person_name_paired_item);
        validator.addField(personNamePairedItem, getString(R.string.person_name_label), new NotEmptyChecker());

        tabloNamePairedItem = view.findViewById(R.id.tablo_name_paired_item);

        addressPairedItem = view.findViewById(R.id.address_paired_item);
        validator.addField(addressPairedItem, getString(R.string.address), new LengthChecker(0, 35,
                false));

        street2PairedItem = view.findViewById(R.id.street2_paired_item);
        validator.addField(street2PairedItem, getString(R.string.street2_label), new LengthChecker(0, 35,
                false));

        street3PairedItem = view.findViewById(R.id.street3_paired_item);
        validator.addField(street3PairedItem, getString(R.string.street3_label), new LengthChecker(0, 35,
                false));

        street4PairedItem = view.findViewById(R.id.street4_paired_item);
        validator.addField(street4PairedItem, getString(R.string.street4_label), new LengthChecker(0, 35,
                false));

        street5PairedItem = view.findViewById(R.id.street5_paired_item);
        validator.addField(street5PairedItem, getString(R.string.street5_label),
                new LengthChecker(0, 35, false));

        code_naghsh_paired_item = view.findViewById(R.id.code_naghsh_paired_item);
        validator.addField(code_naghsh_paired_item, getString(R.string.code_naghsh),
                new LengthChecker(0, 100, false));
        code_naghsh_paired_item.setEnabled(false);
        code_naghsh_paired_item.setFocusable(false);
        code_naghsh_paired_item.setFocusableInTouchMode(false);

        request_codenaghsh= view.findViewById(R.id.request_codenaghsh);

        postalCodePairedItem = view.findViewById(R.id.postal_code_paired_item);
        validator.addField(postalCodePairedItem, getString(R.string.postal_code_label), new LengthChecker(10, 10,
                false));

        cityNamePairedItem = view.findViewById(R.id.city_name_paired_item);

        deliveryZoneSpinner = view.findViewById(R.id.delivery_zone_spinner);
        validator.addField(deliveryZoneSpinner, getString(R.string.delivery_zone), new NotEmptyChecker());


        telPairedItem = view.findViewById(R.id.tel_paired_item);

        mobilePairedItem = view.findViewById(R.id.mobile_paired_item);

        economicCodePairedItem = view.findViewById(R.id.economic_code_paired_item);
        validator.addField(economicCodePairedItem, "شناسه ملی",
                new LengthChecker(11, 11,
                false));

        nationalCodePairedItem = view.findViewById(R.id.national_code_paired_item);
        validator.addField(nationalCodePairedItem, getString(R.string.customer_national_code), new IraniNationalCodeChecker());

        customerDegreeSpinner = view.findViewById(R.id.degree_spinner);

        saleZonesSpinner = view.findViewById(R.id.sale_zones_spinner);
        validator.addField(saleZonesSpinner, getString(R.string.sale_zones), new NotEmptyChecker());

//        customerGroupSpinner = view.findViewById(R.id.customer_group_spinner);
//        validator.addField(customerGroupSpinner, getString(R.string.customer_group), new NotEmptyChecker());

        customerGroup1Spinner = view.findViewById(R.id.customer_group1_spinner);
        validator.addField(customerGroup1Spinner, getString(R.string.customer_group_1), new NotEmptyChecker());

        customerGroup2Spinner = view.findViewById(R.id.customer_group2_spinner);


        request_codenaghsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataCodeNaghsh();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (customer.CustomerCode!=null) {
            prepareFields();
            enableForm(true);
        }
//        CustomerApi customerApi = new CustomerApi(getContext());
//        call = customerApi.getCustomerZarCustomerInfo(customer.CustomerCode);
//        startProgressDialog();
//        customerApi.runWebRequest(call, new WebCallBack<ZarCustomerInfoViewModel>() {
//            @Override
//            protected void onFinish() {
//                stopProgressDialog();
//            }
//
//            @Override
//            protected void onSuccess(ZarCustomerInfoViewModel result, Request request) {
//                if (isResumed()) {
//                    customerInfo = result;
//                    prepareFields();
//                    enableForm(true);
//                }
//            }
//
//            @Override
//            protected void onApiFailure(ApiError error, Request request) {
//                String err = WebApiErrorBody.log(error, getContext());
//                if (isResumed()) {
//                    showErrorDialog(err);
//                    enableForm(false);
//                }
//            }
//
//            @Override
//            protected void onNetworkFailure(Throwable t, Request request) {
//                if (isResumed()) {
//                    showErrorDialog(getString(R.string.network_error));
//                    enableForm(false);
//                }
//            }
//
//            @Override
//            public void onCancel(Request request) {
//                super.onCancel(request);
//                if (getActivity() != null)
//                    stopProgressDialog();
//            }
//        });
    }


    private void enableForm(boolean enabled) {
        isEnabled = enabled;
        personNamePairedItem.setEnabled(enabled);
        tabloNamePairedItem.setEnabled(enabled);
        addressPairedItem.setEnabled(enabled);
        street2PairedItem.setEnabled(enabled);
        street3PairedItem.setEnabled(enabled);
        street4PairedItem.setEnabled(enabled);
        street5PairedItem.setEnabled(enabled);
        postalCodePairedItem.setEnabled(enabled);
        telPairedItem.setEnabled(enabled);
        mobilePairedItem.setEnabled(enabled);

//        citySpinner.setEnabled(enabled);
        deliveryZoneSpinner.setEnabled(false);
        saleZonesSpinner.setEnabled(false);
        customerDegreeSpinner.setEnabled(enabled);
//        customerGroupSpinner.setEnabled(enabled);
        customerGroup1Spinner.setEnabled(enabled);
        customerGroup2Spinner.setEnabled(enabled);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (call != null && !call.isCanceled())
            call.cancel();
    }

    private void prepareFields() {
        View view = getView();
        if (view == null)
            return;
        DataForRegisterManager dataForRegisterManager = new DataForRegisterManager(getContext());
        final List<DataForRegisterModel> allDataList = dataForRegisterManager.getAll();
        final HashMap<String, List<DataForRegisterModel>> dataMap = new HashMap<>();
        for (DataForRegisterModel data :
                allDataList) {
            if (dataMap.containsKey(data.FieldName)) {
                dataMap.get(data.FieldName).add(data);
            } else {
                List<DataForRegisterModel> list = new ArrayList<>();
                list.add(data);
                dataMap.put(data.FieldName, list);
            }
        }

        if (customer.CustomerName != null)
            personNamePairedItem.setValue(customer.CustomerName);

        if (customer.StoreName != null)
            tabloNamePairedItem.setValue(customer.StoreName);

        if (customer.Address != null)
            addressPairedItem.setValue(customer.Address);

//        if (customerInfo.Street2 != null)
//            street2PairedItem.setValue(customerInfo.Street2);
//
//        if (customerInfo.Street3 != null)
//            street3PairedItem.setValue(customerInfo.Street3);
//
//        if (customerInfo.Street4 != null)
//            street4PairedItem.setValue(customerInfo.Street4);
//
//        if (customerInfo.Street5 != null)
//            street5PairedItem.setValue(customerInfo.Street5);

        if (customer.CustomerPostalCode != null)
            postalCodePairedItem.setValue(customer.CustomerPostalCode );

//        if (customer.CityId != null)
//            cityNamePairedItem.setValue(String.valueOf(customer.CityArea));


        CityManager cityManager = new CityManager(getContext());
        List<CityModel> cityModels = cityManager.getAllCities();
//        citySpinner.setup(getFragmentManager(), cityModels,
//        new SearchBox.SearchMethod<CityModel>() {
//            @Override
//            public boolean onSearch(CityModel item, String text) {
//                String str = HelperMethods.persian2Arabic(text);
//                if (str == null)
//                    return true;
//                str = str.toLowerCase();
//                return item.toString().toLowerCase().contains(str);
//            }
//        });
//        if (customerInfo.CityId != null) {
//            int selectedItemPosition = Linq.findFirstIndex(cityModels,
//            new Linq.Criteria<CityModel>() {
//                @Override
//                public boolean run(CityModel item) {
//                    return item.UniqueId.equals(customerInfo.CityId);
//                }
//            });
//            if (selectedItemPosition >= 0)
//                citySpinner.selectItem(selectedItemPosition);
//        }

        /**
         * محدود فروش
         */

//        List<DataForRegisterModel> deliveryZones = dataMap.get("ZONE1");
//        if (deliveryZones != null) {
//            deliveryZoneSpinner.setup(getChildFragmentManager(), deliveryZones,
//                    new SearchBox.SearchMethod<DataForRegisterModel>() {
//                @Override
//                public boolean onSearch(DataForRegisterModel item, String text) {
//                    return item.FieldValue != null && item.FieldValue.contains(text);
//                }
//            });
//            if (customerInfo.transportationZone != null) {
//                int selectedItemPosition = Linq.findFirstIndex(deliveryZones,
//                        new Linq.Criteria<DataForRegisterModel>() {
//                    @Override
//                    public boolean run(DataForRegisterModel item) {
//                        return item.FieldKey.equals(customerInfo.transportationZone);
//                    }
//                });
//                if (selectedItemPosition >= 0)
//                    deliveryZoneSpinner.selectItem(selectedItemPosition);
//            }
//        }

        if (customer.Phone != null)
            telPairedItem.setValue(customer.Phone );

        if (customer.Mobile != null)
            mobilePairedItem.setValue(customer.Mobile);



        if (customer.CodeNaghsh != null) {
            code_naghsh_paired_item.setValue(customer.CodeNaghsh);
            economicCodePairedItem.setEnabled(false);
            economicCodePairedItem.setFocusable(false);
            economicCodePairedItem.setFocusableInTouchMode(false);
            nationalCodePairedItem.setEnabled(false);
            nationalCodePairedItem.setFocusable(false);
            nationalCodePairedItem.setFocusableInTouchMode(false);
        }else {
            code_naghsh_paired_item.setValue("69696596");
            request_codenaghsh.setVisibility(View.VISIBLE);
        }
        if (!customer.EconomicCode.isEmpty()) {
            economicCodePairedItem.setValue(customer.EconomicCode);

        }
        if (!customer.NationalCode.isEmpty()) {
            nationalCodePairedItem.setValue(customer.NationalCode);

        }



        /**
         * درجه مشتری
         * گروه مشتری
         */
        List<DataForRegisterModel> KUKLAs = dataMap.get("KUKLA");
        if (KUKLAs != null) {
            customerDegreeSpinner.setup(getChildFragmentManager(), KUKLAs,
                    new SearchBox.SearchMethod<DataForRegisterModel>() {
                @Override
                public boolean onSearch(DataForRegisterModel item, String text) {
                    return item.FieldValue != null && item.FieldValue.contains(text);
                }
            });
            CustomerLevelManager customerLevelManager =
                    new CustomerLevelManager(getContext());
            final CustomerLevelModel customerLevel =
                    customerLevelManager.getItem(customer.CustomerLevelId);
            if (customerLevel != null) {
                int selectedItemPosition = Linq.findFirstIndex(KUKLAs,
                        new Linq.Criteria<DataForRegisterModel>() {
                    @Override
                    public boolean run(DataForRegisterModel item) {
                        return item.FieldValue.equals(customerLevel.CustomerLevelName);
                    }
                });
                if (selectedItemPosition >= 0)
                    customerDegreeSpinner.selectItem(selectedItemPosition);
            }
        }

        /**
         * منطقه فروش
         */
//        List<DataForRegisterModel> BZIRKs = dataMap.get("BZIRK");
//        if (BZIRKs != null) {
//            saleZonesSpinner.setup(getChildFragmentManager(), BZIRKs,
//                    new SearchBox.SearchMethod<DataForRegisterModel>() {
//                @Override
//                public boolean onSearch(DataForRegisterModel item, String text) {
//                    return item.FieldValue != null && item.FieldValue.contains(text);
//                }
//            });
//            if (customerInfo.bzirk != null) {
//                int selectedItemPosition = Linq.findFirstIndex(BZIRKs,
//                        new Linq.Criteria<DataForRegisterModel>() {
//                    @Override
//                    public boolean run(DataForRegisterModel item) {
//                        return item.FieldKey.equals(customerInfo.bzirk);
//                    }
//                });
//                if (selectedItemPosition >= 0)
//                    saleZonesSpinner.selectItem(selectedItemPosition);
//            }
//        }

//        customerGroupSpinner.setEnabled(false);
//        List<DataForRegisterModel> KDGRPs = dataMap.get("KDGRP");
//        if (KDGRPs != null) {
//            customerGroupSpinner.setup(getChildFragmentManager(), KDGRPs,
//            new SearchBox.SearchMethod<DataForRegisterModel>() {
//                @Override
//                public boolean onSearch(DataForRegisterModel item, String text) {
//                    return item.FieldValue != null && item.FieldValue.contains(text);
//                }
//            });
//            if (customerInfo.kdgrp != null) {
//                int selectedItemPosition = Linq.findFirstIndex(KDGRPs,
//                new Linq.Criteria<DataForRegisterModel>() {
//                    @Override
//                    public boolean run(DataForRegisterModel item) {
//                        return item.FieldKey.equals(customerInfo.kdgrp);
//                    }
//                });
//                if (selectedItemPosition >= 0)
//                    customerGroupSpinner.selectItem(selectedItemPosition);
//            }
//        }

        /**
         * گروه مشتری
         */
        customerGroup1Spinner.setEnabled(false);
        List<DataForRegisterModel> KVGR1s = dataMap.get("KVGR1");
        if (KVGR1s != null) {
            customerGroup1Spinner.setup(getChildFragmentManager(), KVGR1s,
                    new SearchBox.SearchMethod<DataForRegisterModel>() {
                @Override
                public boolean onSearch(DataForRegisterModel item, String text) {
                    return item.FieldValue != null && item.FieldValue.contains(text);
                }
            });
            if (customer.CustomerCategoryId!=null) {
                CustomerCategoryManager customerCategoryManager =
                        new CustomerCategoryManager(getContext());
                final CustomerCategoryModel customerCategory =
                        customerCategoryManager.getItem(customer.CustomerCategoryId);

                if (customerCategory != null) {
                    int selectedItemPosition = Linq.findFirstIndex(KVGR1s,
                            new Linq.Criteria<DataForRegisterModel>() {
                                @Override
                                public boolean run(DataForRegisterModel item) {
                                    return item.FieldValue.equals(customerCategory.CustomerCategoryName);
                                }
                            });
                    if (selectedItemPosition >= 0)
                        customerGroup1Spinner.selectItem(selectedItemPosition);
                }
            }
        }

        /**
         * گزوه مشتری 2
         * فعالیت مشتری
         */
        customerGroup2Spinner.setEnabled(false);
        List<DataForRegisterModel> KVGR2s = dataMap.get("KVGR2");
        if (KVGR2s != null) {
            customerGroup2Spinner.setup(getChildFragmentManager(), KVGR2s,
                    new SearchBox.SearchMethod<DataForRegisterModel>() {
                        @Override
                        public boolean onSearch(DataForRegisterModel item, String text) {
                            return item.FieldName != null && item.FieldValue.contains(text);
                        }
                    });


            if (customer.CustomerActivityId != null) {
                CustomerActivityManager customerActivityManager =
                        new CustomerActivityManager(getContext());
                final CustomerActivityModel customerActivity =
                        customerActivityManager
                                .getItem(customer.CustomerActivityId);
                if (customerActivity != null) {
                    int selectedItemPosition = Linq.findFirstIndex(KVGR2s,
                            new Linq.Criteria<DataForRegisterModel>() {
                                @Override
                                public boolean run(DataForRegisterModel item) {
                                    return item.FieldValue.equals(customerActivity.CustomerActivityName);
                                }
                            });
                    if (selectedItemPosition >= 0)
                        customerGroup2Spinner.selectItem(selectedItemPosition);
                }
            }
        }

    }

    @Override
    public void ok() {
        if (isEnabled)
            createSyncViewModel();
    }

    @Override
    public void cancel() {

    }

    public void submit() {
        syncGetNewCustomerViewModel.customerUniqueId=customerUniqueId;
        startProgressDialog();
        CustomerApi customerApi = new CustomerApi(getContext());
        customerApi.runWebRequest(customerApi.postCustomerZarCustomerInfo(syncGetNewCustomerViewModel),
                new WebCallBack<String>() {
            @Override
            protected void onFinish() {
                stopProgressDialog();
            }

            @Override
            protected void onSuccess(String result, Request request) {
//                if (result != null)
//                    updateCustomer(result.UniqueId);
//                else
//                    showErrorDialog(R.string.error_saving_customer);
                showResaltDialog(result);


            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                showErrorDialog(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                showErrorDialog(R.string.network_error);
            }
        });

    }

    private void updateCustomer(UUID customerId) {
        CustomersUpdateFlow flow = new CustomersUpdateFlow(getContext(), customerId);
        flow.syncCustomersAndInitPromotionDb(new UpdateCall() {
            @Override
            protected void onSuccess() {
                try {
                    new CustomerCallManager(getContext()).saveEditCustomerCall(customerId);
                    if (onCustomerEditedCallBack != null)
                        onCustomerEditedCallBack.done();
                    dismiss();
                } catch (Exception e) {
                    showErrorDialog(R.string.error_saving_request);
                }
            }

            @Override
            protected void onFailure(String error) {
                showErrorDialog(R.string.error_saving_request);
            }
        });
    }



    private void createSyncViewModel() {
       // syncGetNewCustomerViewModel.CityId = cityNamePairedItem.getValue();
        String postcode=postalCodePairedItem.getValue();
        String codenaghsh=code_naghsh_paired_item.getValue();
        DataForRegisterModel group1 = customerGroup1Spinner.getSelectedItem();
        DataForRegisterModel group2 = customerGroup2Spinner.getSelectedItem();
        DataForRegisterModel degree = customerDegreeSpinner.getSelectedItem();
        if (postcode !=null && !postcode.equals("") && !postcode.isEmpty()
                &&!codenaghsh.isEmpty() &&codenaghsh !=null&&!codenaghsh.equals("")) {

            if (addressPairedItem!=null&&personNamePairedItem!=null&&group1!=null&&group2!=null) {

                syncGetNewCustomerViewModel = new SyncZarGetNewCustomerViewModel();
                syncGetNewCustomerViewModel.CustomerCode = customer.CustomerCode;
                syncGetNewCustomerViewModel.PersonName = personNamePairedItem.getValue();
                syncGetNewCustomerViewModel.StoreName = tabloNamePairedItem.getValue();
                syncGetNewCustomerViewModel.Street = addressPairedItem.getValue();
                syncGetNewCustomerViewModel.Street2 = street2PairedItem.getValue();
                syncGetNewCustomerViewModel.Street3 = street3PairedItem.getValue();
                syncGetNewCustomerViewModel.Street4 = street4PairedItem.getValue();
                syncGetNewCustomerViewModel.Street5 = street5PairedItem.getValue();
                String convertPostalCode = ConvertFaNumType.convert(postalCodePairedItem.getValue());
                syncGetNewCustomerViewModel.PostalCode = convertPostalCode;

                syncGetNewCustomerViewModel.Tel = telPairedItem.getValue();
                syncGetNewCustomerViewModel.Mobile = mobilePairedItem.getValue();
                syncGetNewCustomerViewModel.CodeNaghsh = code_naghsh_paired_item.getValue();
                syncGetNewCustomerViewModel.EconomicCode = economicCodePairedItem.getValue();
                syncGetNewCustomerViewModel.NationalCode = nationalCodePairedItem.getValue();
//        CityModel city = citySpinner.getSelectedItem();
//        if (city != null)
//            syncGetNewCustomerViewModel.CityId = city.UniqueId;

                DataForRegisterModel zone = deliveryZoneSpinner.getSelectedItem();
                if (zone != null)
                    syncGetNewCustomerViewModel.TRANSPZONE = zone.FieldKey;


                if (degree != null)
                    syncGetNewCustomerViewModel.KUKLA = degree.FieldKey;

                DataForRegisterModel sale = saleZonesSpinner.getSelectedItem();
                if (sale != null)
                    syncGetNewCustomerViewModel.BZIRK = sale.FieldKey;

//        DataForRegisterModel group = customerGroupSpinner.getSelectedItem();
//        if (group != null)
//            syncGetNewCustomerViewModel.KDGRP = group.FieldKey;


                if (group1 != null) {
                    syncGetNewCustomerViewModel.KVGR1 = group1.FieldKey;
                    syncGetNewCustomerViewModel.KDGRP = group1.FieldKey;
                }

                if (group2 != null)
                    syncGetNewCustomerViewModel.KVGR2 = group2.FieldKey;

                validator.validate(this);
            }else {
                showErrorDialog("نام مشتری ,آدرس مشتری ,گروه مشتری1 , گروه مشتری 2و درجه مشتری  را ثیت کنید ");
            }
        }else {
            showErrorDialog("کد پستی و کدنقش مشتری را ثبت کنید ");
        }
    }


    @Override
    public void onValidationSucceeded() {
        submit();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validator.showErrors(errors);
    }




    public interface OnCustomerEditedCallBack {
        void done();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("68565e5e-d407-4858-bc5f-fd52b9318734", customer.UniqueId.toString());
    }

    private void showErrorDialog(@StringRes int err) {
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
    private void showResaltDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.getsettings);
            dialog.setIcon(Icon.Info);
            dialog.setMessage(err);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            dismiss();
        }
    }

    private void startProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void stopProgressDialog() {
        if (getVaranegarActvity() != null && progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
    }

    //send request get code naghsh

    private void  getDataCodeNaghsh(){


        String economicCode=economicCodePairedItem.getValue();
        String nationalCode=nationalCodePairedItem.getValue();

        roleCodeCustomerViewModel=new RoleCodeCustomerRequestViewModel();
        roleCodeCustomerViewModel.CustomerId=customerUniqueId;
        roleCodeCustomerViewModel.NationalCode=nationalCode;
        roleCodeCustomerViewModel.EconomicCode=economicCode;

        if (economicCode!=null&&!economicCode.equals("")||
                nationalCode!=null&&!nationalCode.equals("")){

            ApiNew apiNew=new ApiNew(getContext());
          Call<List<RoleCodeViewModel>> calll=apiNew.getCodeNaghsh(roleCodeCustomerViewModel);
            startProgressDialog();

            apiNew.runWebRequest(calll, new WebCallBack<List<RoleCodeViewModel>>() {
                @Override
                protected void onFinish() {
                    stopProgressDialog();
                }

                @Override
                protected void onSuccess(List<RoleCodeViewModel> result, Request request) {

                    if (result.size()>0){
                        if (result.size()==1){
                            code_naghsh_paired_item.setValue(result.get(0).Code);
                            request_codenaghsh.setVisibility(View.GONE);
                            nationalCodePairedItem.setEnabled(false);
                            nationalCodePairedItem.setFocusable(false);
                            nationalCodePairedItem.setFocusableInTouchMode(false);
                        }else {
                            dialogShow(result);
                        }
                    }else {
                        showErrorDialog("یرای این کد ملی کد نقش ثبت نشده است ");
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, getContext());
                    if (isResumed()) {
                        showErrorDialog(err);

                    }
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    if (isResumed()) {
                        showErrorDialog(getString(R.string.network_error));

                    }
                }
            });

        }else {
            showErrorDialog("کد ملی و کداقتصادی مشتری را ثبت کنید ");
        }
    }


    private void dialogShow(List<RoleCodeViewModel> roleCodeViewModel){
        String[] data = new String[roleCodeViewModel.size()];
        roleCodeViewModels=roleCodeViewModel;
        for (int i=0;i<roleCodeViewModel.size();i++){
            data[i]=roleCodeViewModel.get(i).Title+roleCodeViewModel.get(i).Code;
        }
        SingleChoiceDialog singleChoiceDialog = new
                SingleChoiceDialog(getContext(),"کد نقش مورد نظر را انتخاب کنید",data);
        singleChoiceDialog.setCancelable(false);
        singleChoiceDialog.addItemClickListener(this);
        singleChoiceDialog.show(getActivity().getSupportFragmentManager(),
                "Single Choice Dialog");
    }
    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        code_naghsh_paired_item.setValue(roleCodeViewModels.get(position).Code);
        request_codenaghsh.setVisibility(View.GONE);
        nationalCodePairedItem.setEnabled(false);
        nationalCodePairedItem.setFocusable(false);
        nationalCodePairedItem.setFocusableInTouchMode(false);
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}