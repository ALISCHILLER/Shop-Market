package com.varanegar.vaslibrary.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.MutableData;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.FormValidator;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.annotations.IraniNationalCodeChecker;
import com.varanegar.framework.validation.annotations.LengthChecker;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.framework.validation.annotations.NotNullChecker;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.DataForRegisterManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.CustomersUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.vaslibrary.webapi.customer.SyncGuidViewModel;
import com.varanegar.vaslibrary.webapi.customer.SyncZarGetNewCustomerViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public class AddNewCustomerZarFragment extends VaranegarFragment implements ValidationListener {


    private ProgressDialog progressDialog;
    private PairedItemsSpinner<DataForRegisterModel> customerAccountGroupSpinner;
    private PairedItemsEditable personNamePairedItem;
    private PairedItemsEditable tabloNamePairedItem;
    private PairedItemsEditable addressPairedItem;
    private PairedItemsEditable street2PairedItem;
    private PairedItemsEditable street3PairedItem;
    private PairedItemsEditable street4PairedItem;
    private PairedItemsEditable street5PairedItem;
    private PairedItemsEditable postalCodePairedItem;
    private PairedItemsSpinner<DataForRegisterModel> stateSpinner;
    private PairedItemsSpinner<DataForRegisterModel> deliveryZoneSpinner;
    private PairedItemsEditable telPairedItem;
    private PairedItemsEditable mobilePairedItem;
    private PairedItemsEditable economicCodePairedItem;
    private PairedItemsEditable nationalCodePairedItem;
    private ImageView nationalCardPic;
    private PairedItemsSpinner<DataForRegisterModel> customerDegreeSpinner;
    private PairedItemsSpinner<DataForRegisterModel> saleZonesSpinner;
    private PairedItems customerLocationPairedItem;
    private PairedItemsSpinner<DataForRegisterModel> customerGroup1Spinner;
    private PairedItemsSpinner<DataForRegisterModel> customerGroup2Spinner;
    private final MutableData<Double> latitude = new MutableData<>(null);
    private final MutableData<Double> longitude = new MutableData<>(null);
    private PairedItemsSpinner<PaymentTypeOrderModel> paymentTypeSpinner;
    private FormValidator validator;
    private SyncZarGetNewCustomerViewModel syncGetNewCustomerViewModel;
    private PairedItemsEditable cityNamePairedItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.fragment_new_customer_zar, container, false);

        view.findViewById(R.id.save_image_view).setOnClickListener(v -> {
            if (getContext() != null && !Connectivity.isConnected(getContext())) {
                ConnectionSettingDialog connectionSettingDialog =
                        new ConnectionSettingDialog();
                connectionSettingDialog
                        .show(getChildFragmentManager(), "ConnectionSettingDialog");
                return;
            }
            if (file !=null) {
                createSyncViewModel();
            }else{
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error);
                dialog.setMessage(getString(R.string.taking_picture_of) + "کارت ملی " +" "+ getString(R.string.is_mandatory));
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
                return;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Object lat = VaranegarApplication
                .getInstance().tryRetrieve("ZAR_CUST_LAT", true);
        if (lat != null)
            latitude.setData((Double) lat);
        Object lng = VaranegarApplication
                .getInstance().tryRetrieve("ZAR_CUST_LNG", true);
        if (lng != null)
            longitude.setData((Double) lng);
        if (latitude.getData() != null && longitude.getData() != null) {
            DecimalFormat df = new DecimalFormat("##.#####");
            customerLocationPairedItem
                    .setValue(df
                            .format(latitude.getData()) + "|" + df.format(longitude.getData()));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareFields(view);
    }

    private void prepareFields(View view) {
        if (getContext() == null) return;
        validator = new FormValidator(getActivity());

        DataForRegisterManager dataForRegisterManager =
                new DataForRegisterManager(getContext());
        final List<DataForRegisterModel> allDataList = dataForRegisterManager.getAll();
        final HashMap<String, List<DataForRegisterModel>> dataMap = new HashMap<>();
        for (DataForRegisterModel data : allDataList) {
            if (dataMap.containsKey(data.FieldName)) {
                Objects.requireNonNull(dataMap.get(data.FieldName)).add(data);
            } else {
                List<DataForRegisterModel> list = new ArrayList<>();
                list.add(data);
                dataMap.put(data.FieldName, list);
            }
        }

        customerAccountGroupSpinner =
                view.findViewById(R.id.customer_account_group_spinner);
        customerAccountGroupSpinner.setup(getChildFragmentManager(),
                dataMap.get("KTOKD"), (item, text) -> {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.FieldValue != null && item.FieldValue.contains(str);
                });
        validator.addField(
                customerAccountGroupSpinner,
                getString(R.string.customer_account_group),
                new NotEmptyChecker());

        personNamePairedItem = view.findViewById(R.id.person_name_paired_item);
        validator.addField(personNamePairedItem, getString(R.string.person_name_label), new NotEmptyChecker());

        tabloNamePairedItem = view.findViewById(R.id.tablo_name_paired_item);

        addressPairedItem = view.findViewById(R.id.address_paired_item);
        validator.addField(addressPairedItem, getString(R.string.address),
                new LengthChecker(0, 35, false));

        street2PairedItem = view.findViewById(R.id.street2_paired_item);
        validator.addField(street2PairedItem, getString(R.string.street2_label),
                new LengthChecker(0, 40, false));

        street3PairedItem = view.findViewById(R.id.street3_paired_item);
        validator.addField(street3PairedItem, getString(R.string.street3_label),
                new LengthChecker(0, 40, false));

        street4PairedItem = view.findViewById(R.id.street4_paired_item);
        validator.addField(street4PairedItem, getString(R.string.street4_label),
                new LengthChecker(0, 40, false));

        street5PairedItem = view.findViewById(R.id.street5_paired_item);
        validator.addField(street5PairedItem, getString(R.string.street5_label),
                new LengthChecker(0, 40, false));

        postalCodePairedItem = view.findViewById(R.id.postal_code_paired_item);
        validator.addField(postalCodePairedItem, getString(R.string.postal_code_label),
                new LengthChecker(10, 10, false));

        stateSpinner = view.findViewById(R.id.state_spinner);
        stateSpinner.setup(getFragmentManager(), dataMap.get("BLAND"), (item, text) -> {
            String str = HelperMethods.persian2Arabic(text);
            if (str == null)
                return true;
            str = str.toLowerCase();
            return item.FieldValue != null && item.FieldValue.contains(str);
        });

        cityNamePairedItem = view.findViewById(R.id.city_name_paired_item);

        deliveryZoneSpinner = view.findViewById(R.id.delivery_zone_spinner);
        deliveryZoneSpinner.setup(getChildFragmentManager(), dataMap.get("ZONE1"),
                (item, text) -> {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.FieldValue != null && item.FieldValue.contains(str);
                });
        validator.addField(deliveryZoneSpinner, getString(R.string.delivery_zone), new NotEmptyChecker());

        telPairedItem = view.findViewById(R.id.tel_paired_item);
        mobilePairedItem = view.findViewById(R.id.mobile_paired_item);
        economicCodePairedItem = view.findViewById(R.id.economic_code_paired_item);
        nationalCardPic=view.findViewById(R.id.national_card_pic);
        nationalCardPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        nationalCodePairedItem = view.findViewById(R.id.national_code_paired_item);
        validator.addField(nationalCodePairedItem, getString(R.string.customer_national_code), new IraniNationalCodeChecker());

        saleZonesSpinner = view.findViewById(R.id.sale_zones_spinner);
        saleZonesSpinner.setup(getChildFragmentManager(), dataMap.get("BZIRK"),
                (item, text) -> {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.FieldValue != null && item.FieldValue.contains(str);
                });
        validator.addField(saleZonesSpinner, getString(R.string.sale_zones), new NotEmptyChecker());

        customerDegreeSpinner = view.findViewById(R.id.degree_spinner);
        customerDegreeSpinner.setup(getChildFragmentManager(), dataMap.get("KUKLA"),
                (item, text) -> {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.FieldValue != null && item.FieldValue.contains(str);
                });

        customerGroup1Spinner = view.findViewById(R.id.customer_group1_spinner);
        customerGroup1Spinner.setup(getChildFragmentManager(), dataMap.get("KVGR1"),
                (item, text) -> {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.FieldValue != null && item.FieldValue.contains(str);
                });
        validator.addField(customerGroup1Spinner, getString(R.string.customer_group_1), new NotEmptyChecker());

        customerGroup2Spinner = view.findViewById(R.id.customer_group2_spinner);
        customerGroup2Spinner.setup(getChildFragmentManager(), dataMap.get("KVGR2"),
                (item, text) -> {
                    String str = HelperMethods.persian2Arabic(text);
                    if (str == null)
                        return true;
                    str = str.toLowerCase();
                    return item.FieldValue != null && item.FieldValue.contains(str);
                });

        customerLocationPairedItem = view.findViewById(R.id.customer_location_paired_item);
        ImageButton setLocationBtn = view.findViewById(R.id.set_location_btn);
        setLocationBtn.setOnClickListener(v -> {
            CustomerLocationZarFragment fragment =
                    new CustomerLocationZarFragment();
            if (getVaranegarActvity() != null)
                getVaranegarActvity().pushFragment(fragment);
        });

        PaymentOrderTypeManager paymentOrderTypeManager =
                new PaymentOrderTypeManager(getContext());
        List<PaymentTypeOrderModel> paymentTypes =
                paymentOrderTypeManager.getDealerPaymentTypes();
        paymentTypeSpinner = view.findViewById(R.id.payment_type_spinner);
        paymentTypeSpinner.setup(getChildFragmentManager(), paymentTypes,
                (item, text) -> item.PaymentTypeOrderName != null &&
                        item.PaymentTypeOrderName.contains(text));
        validator.addField(paymentTypeSpinner, getString(R.string.payment_type), new NotEmptyChecker());

        validator.addField(latitude, getString(R.string.geo_location), new NotNullChecker());
        validator.addField(longitude, getString(R.string.geo_location), new NotNullChecker());
    }

    private void submit(final SyncZarGetNewCustomerViewModel syncGetNewCustomerViewModel/*, File file*/) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.registering_new_customer));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final CustomerApi customerApi = new CustomerApi(getContext());


        customerApi.runWebRequest(
                customerApi.registerNewZarCustomer(syncGetNewCustomerViewModel),
                new WebCallBack<SyncGuidViewModel>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(final SyncGuidViewModel result, Request request) {
                        CustomersUpdateFlow flow =
                                new CustomersUpdateFlow(getContext(), result.UniqueId);
                        flow.syncCustomersAndInitPromotionDb(new UpdateCall() {
                            @Override
                            protected void onSuccess() {
                                final CustomerManager customerManager =
                                        new CustomerManager(getActivity());
                                List<CustomerModel> customerModels = customerManager.getAll();
                                boolean customerAddedToDb = false;
                                for (final CustomerModel customerModel : customerModels) {
                                    if (customerModel.UniqueId != null &&
                                            customerModel.UniqueId.equals(result.UniqueId)) {
                                        customerModel.IsNewCustomer = true;
                                        customerAddedToDb = true;
                                        try {
                                            customerManager.update(customerModel);
                                        } catch (Exception e) {
                                            Timber.e(e);
                                        }
                                        break;
                                    }
                                }
                                int messageRes;
                                if (customerAddedToDb)
                                    messageRes = R.string.registering_customer_completed;
                                else
                                    messageRes = R.string.registering_customer_completed_but_not_inserted_in_db;

                                sendNationalImage(messageRes, customerApi, result);

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

    private void sendNationalImage(int messageRes, CustomerApi customerApi, SyncGuidViewModel result) {
        customerApi.runWebRequest(
                customerApi.registerNewZarCustomerNationalCardImage(
                        result.UniqueId,
                        file
                ), new WebCallBack<Boolean>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(Boolean result, Request request) {
                        if (result){
                            final MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                stopProgressDialog();
                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                dialog.setMessage(messageRes);
                                dialog.setTitle(R.string.done);
                                dialog.setIcon(Icon.Success);
                                dialog.setPositiveButton(R.string.ok,
                                        v -> activity.popFragment());
                                dialog.show();
                            }
                        }else {
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                stopProgressDialog();
                                CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(activity);
                                cuteMessageDialog.setIcon(Icon.Error);
                                cuteMessageDialog.setTitle(R.string.error);
                                cuteMessageDialog.setMessage("خطا در ارسال تصویر کارت ملی ");
                                cuteMessageDialog.setNeutralButton(R.string.ok, null);
                                cuteMessageDialog.show();
                            }
                        }

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
                }

        );
    }

    private void createSyncViewModel() {
        syncGetNewCustomerViewModel = new SyncZarGetNewCustomerViewModel();
        syncGetNewCustomerViewModel.PersonName = personNamePairedItem.getValue();
        syncGetNewCustomerViewModel.StoreName = tabloNamePairedItem.getValue();
        syncGetNewCustomerViewModel.Street = addressPairedItem.getValue();
        syncGetNewCustomerViewModel.Street2 = street2PairedItem.getValue();
        syncGetNewCustomerViewModel.Street3 = street3PairedItem.getValue();
        syncGetNewCustomerViewModel.Street4 = street4PairedItem.getValue();
        syncGetNewCustomerViewModel.Street5 = street5PairedItem.getValue();
        syncGetNewCustomerViewModel.PostalCode = postalCodePairedItem.getValue();
        syncGetNewCustomerViewModel.Tel = telPairedItem.getValue();
        syncGetNewCustomerViewModel.Mobile = mobilePairedItem.getValue();
        syncGetNewCustomerViewModel.EconomicCode = economicCodePairedItem.getValue();
        syncGetNewCustomerViewModel.NationalCode = nationalCodePairedItem.getValue();
        syncGetNewCustomerViewModel.CityId = cityNamePairedItem.getValue();

        DataForRegisterModel ktokd = customerAccountGroupSpinner.getSelectedItem();
        if (ktokd != null)
            syncGetNewCustomerViewModel.KTOKD = ktokd.FieldKey;

        DataForRegisterModel state = stateSpinner.getSelectedItem();
        if (state != null)
            syncGetNewCustomerViewModel.StateId = state.FieldKey;

        DataForRegisterModel zone = deliveryZoneSpinner.getSelectedItem();
        if (zone != null)
            syncGetNewCustomerViewModel.TRANSPZONE = zone.FieldKey;

        DataForRegisterModel sale = saleZonesSpinner.getSelectedItem();
        if (sale != null)
            syncGetNewCustomerViewModel.BZIRK = sale.FieldKey;

        DataForRegisterModel degree = customerDegreeSpinner.getSelectedItem();
        if (degree != null)
            syncGetNewCustomerViewModel.KUKLA = degree.FieldKey;

        DataForRegisterModel group1 = customerGroup1Spinner.getSelectedItem();
        if (group1 != null) {
            syncGetNewCustomerViewModel.KVGR1 = group1.FieldKey;
            syncGetNewCustomerViewModel.KDGRP = group1.FieldKey;
        }

        DataForRegisterModel group2 = customerGroup2Spinner.getSelectedItem();
        if (group2 != null)
            syncGetNewCustomerViewModel.KVGR2 = group2.FieldKey;

        UserModel um = UserManager.readFromFile(getContext());
        if (um != null) {
            syncGetNewCustomerViewModel.DealerId = um.UniqueId;
        }

        TourManager tourManager = new TourManager(getContext());
        TourModel tm = tourManager.loadTour();
        if(tm!=null)
            syncGetNewCustomerViewModel.PathId = tm.DayVisitPathId;

        PaymentTypeOrderModel paymentTypeOrderModel =
                paymentTypeSpinner.getSelectedItem();
        if (paymentTypeOrderModel != null)
            syncGetNewCustomerViewModel.PaymentTypeId =
                    paymentTypeOrderModel.BackOfficeId;

        syncGetNewCustomerViewModel.latitude = latitude.getData();
        syncGetNewCustomerViewModel.longitude = longitude.getData();

        validator.validate(this);
    }

    private void stopProgressDialog() {
        if (getVaranegarActvity() != null &&
                progressDialog != null &&
                progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
    }

    @Override
    public void onValidationSucceeded() {
        submit(syncGetNewCustomerViewModel);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validator.showErrors(errors);
    }

    //region capture image
    File file = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            file = bitmapToFile(imageBitmap,"customercode.jpg");
            nationalCardPic.setImageBitmap(imageBitmap);
        }
    }

    public static File bitmapToFile(Bitmap bitmap, String fileNameToSave) {
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() +
                    File.separator +
                    fileNameToSave);
            boolean fileExist =  file.createNewFile();
            if(fileExist)
                return file;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 , bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        }catch (Exception e){
            e.printStackTrace();
            return file;
        }
    }
    //endregion
}
