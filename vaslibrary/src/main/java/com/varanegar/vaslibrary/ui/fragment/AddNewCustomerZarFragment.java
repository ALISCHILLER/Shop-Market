package com.varanegar.vaslibrary.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.MutableData;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.base.questionnaire.controls.AttachImageDialog;
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
import com.varanegar.framework.validation.annotations.MobileNumberChecker;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.DataForRegisterManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.dialog.new_dialog.SingleChoiceDialog;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeCustomerRequestViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeViewModel;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.vaslibrary.webapi.customer.SyncGuidViewModel;
import com.varanegar.vaslibrary.webapi.customer.SyncZarGetNewCustomerViewModel;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Torabi on 8/23/2017.
 */

public class AddNewCustomerZarFragment extends VaranegarFragment implements ValidationListener
        , SingleChoiceDialog.SingleChoiceListener {


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
    private PairedItems code_naghsh_paired_item;
    private Button request_codenaghsh;
    private RoleCodeCustomerRequestViewModel roleCodeCustomerViewModel;

    private List<RoleCodeViewModel> roleCodeViewModels;


    private LinearLayout header_linear_layout;
    private RelativeLayout main_layout;
    private CameraView cameraView;
    public AttachImageDialog.OnAttachment onAttachment;



    @Override
    public void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }



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
        final CustomerApi customerApi = new CustomerApi(getContext());
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

            if (file != null) {
                createSyncViewModel();
            } else {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error);
                dialog.setMessage(getString(R.string.taking_picture_of) + "کارت ملی " + " " + getString(R.string.is_mandatory));
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        });

        //گرفتن لوکیشن
        new LocationManager(getActivity())
                .getLocation(new LocationManager.OnLocationUpdated() {
                    @Override
                    public void onSucceeded(LocationModel location) {
                        if (location.Longitude>0){

                        }

                    }

                    @Override
                    public void onFailed(String error) {
                        TrackingLogManager.addLog(
                                getActivity(),
                                LogType.POINT,
                                LogLevel.Error,
                                " در زمان تایید عملیات پوینت دریافت نشد!",
                                error);
                        showErrorMessage(error);
                    }
                });



        return view;
    }

    void showErrorMessage(String error) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
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
        request_codenaghsh = view.findViewById(R.id.request_codenaghsh);

        request_codenaghsh.setOnClickListener(v -> getDataCodeNaghsh());

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

        mobilePairedItem = view.findViewById(R.id.mobile_paired_item);
        validator.addField(mobilePairedItem, getString(R.string.mobile_label_supervisor),
                new MobileNumberChecker());

        code_naghsh_paired_item = view.findViewById(R.id.code_naghsh_paired_item);
        validator.addField(code_naghsh_paired_item, getString(R.string.code_naghsh));

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

        economicCodePairedItem = view.findViewById(R.id.economic_code_paired_item);

       /**گرفتن عکس*/
        nationalCardPic = view.findViewById(R.id.national_card_pic);
        main_layout = view.findViewById(R.id.main_layout);
        header_linear_layout = view.findViewById(R.id.header_linear_layout);
        FloatingActionButton take_picture = view.findViewById(R.id.take_picture);
        cameraView = view.findViewById(R.id.camera_view);
        nationalCardPic.setOnClickListener(v -> {
            //    dispatchTakePictureIntent();
            main_layout.setVisibility(View.VISIBLE);
            header_linear_layout.setVisibility(View.GONE);
        });

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                CameraUtils.decodeBitmap(picture, bitmap -> {
                    persistImage(bitmap,"fh");
                    nationalCardPic.setImageBitmap(bitmap);
                    if (onAttachment != null)
                        onAttachment.onDone();

                    main_layout.setVisibility(View.GONE);
                    header_linear_layout.setVisibility(View.VISIBLE);
                });
            }
        });
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.capturePicture();
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

/*        List<PaymentTypeOrderModel> paymentTypes =
                paymentOrderTypeManager.getDealerPaymentTypes();*/

        List<PaymentTypeOrderModel> paymentTypes = new ArrayList<>();
        UUID instantSettlement = UUID.fromString("11fdf92f-724e-49af-ac01-be0384a6bc8e");
        paymentTypes.add(paymentOrderTypeManager.getPaymentType(instantSettlement));

        paymentTypeSpinner = view.findViewById(R.id.payment_type_spinner);
        paymentTypeSpinner.setup(getChildFragmentManager(), paymentTypes,
                (item, text) -> item.PaymentTypeOrderName != null &&
                        item.PaymentTypeOrderName.contains(text));
        paymentTypeSpinner.selectItem(0);
        paymentTypeSpinner.setEnabled(false);
        validator.addField(paymentTypeSpinner, getString(R.string.payment_type), new NotEmptyChecker());

        /*
        * Edit By Mehrdad Latifi
        * */

/*        validator.addField(latitude, getString(R.string.geo_location), new NotNullChecker());
        validator.addField(longitude, getString(R.string.geo_location), new NotNullChecker());*/
    }

    private void submit(final SyncZarGetNewCustomerViewModel syncGetNewCustomerViewModel/*, File file*/) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.registering_new_customer));
        progressDialog.setCancelable(false);
        progressDialog.show();

        PingApi pingApi = new PingApi();

        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                final CustomerApi customerApi = new CustomerApi(getContext());
                customerApi.runWebRequest(
                        customerApi.registerNewZarCustomer(syncGetNewCustomerViewModel),
                        new WebCallBack<SyncGuidViewModel>() {
                            @Override
                            protected void onFinish() {

                            }

                            @Override
                            protected void onSuccess(final SyncGuidViewModel result, Request request) {
                                stopProgressDialog();
                                //                        CustomersUpdateFlow flow =
//                                new CustomersUpdateFlow(getContext(), result.UniqueId);
//                        flow.syncCustomersAndInitPromotionDb(new UpdateCall() {
//                            @Override
//                            protected void onSuccess() {
//                                final CustomerManager customerManager =
//                                        new CustomerManager(getActivity());
//                                List<CustomerModel> customerModels = customerManager.getAll();
//                                boolean customerAddedToDb = false;
//                                for (final CustomerModel customerModel : customerModels) {
//                                    if (customerModel.UniqueId != null &&
//                                            customerModel.UniqueId.equals(result.UniqueId)) {
//                                        customerModel.IsNewCustomer = true;
//                                        customerAddedToDb = true;
//                                        try {
//                                            customerManager.update(customerModel);
//                                        } catch (Exception e) {
//                                            Timber.e(e);
//                                        }
//                                        break;
//                                    }
//                                }
//                                String messageRes;
//                                if (customerAddedToDb)
//                                    messageRes = String.valueOf(R.string.registering_customer_completed);
//                                else
//                                    messageRes = String.valueOf(R.string.registering_customer_completed_but_not_inserted_in_db);
//                                stopProgressDialog();
//                                sendNationalImage(messageRes, result);
//
//
//                            }
//
//                            @Override
//                            protected void onFailure(String error) {
//                                MainVaranegarActivity activity = getVaranegarActvity();
//                                if (activity != null && !activity.isFinishing()) {
//                                    stopProgressDialog();
//                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
//                                    dialog.setMessage(error);
//                                    dialog.setTitle(R.string.error);
//                                    dialog.setIcon(Icon.Error);
//                                    dialog.setPositiveButton(R.string.ok, null);
//                                    dialog.show();
//                                }
//                            }
//                        });
                                if (result != null) {
                                    String messageRes;
                                    messageRes = String.valueOf(R.string.registering_customer_completed);
                                    // stopProgressDialog();
                                    sendNationalImage(messageRes, result);
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

    private void sendNationalImage(String messageRes, SyncGuidViewModel result) {
        if (getContext() == null) return;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.registering_new_customer));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new PictureCustomerViewManager(getContext())
                .uploadNationalCard(result.UniqueId, file, new UpdateCall() {
                    @Override
                    protected void onSuccess() {
                        Timber.i("customer national card have been sent");
                        final MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            stopProgressDialog();
                            registernewNationalCardImage(result.UniqueId, messageRes);

                        }
                    }

                    @Override
                    protected void onFailure(String error) {
                        Timber.e("Uploading national card pictures failed");
                        Timber.e(error);

                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            stopProgressDialog();
                            CuteMessageDialog cuteMessageDialog = new CuteMessageDialog(activity);
                            cuteMessageDialog.setIcon(Icon.Error);
                            cuteMessageDialog.setTitle(R.string.error);
                            cuteMessageDialog.setMessage(error);
                            cuteMessageDialog.setNeutralButton(R.string.ok, null);
                            cuteMessageDialog.show();
                        }
                    }
                });

    }

    private void registernewNationalCardImage(UUID CustomeId, String messageRes) {
        final CustomerApi customerApi = new CustomerApi(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.registering_new_customer));
        progressDialog.setCancelable(false);
        progressDialog.show();
        customerApi.runWebRequest(
                customerApi.registernewNationalCardImage(CustomeId), new WebCallBack<Boolean>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(Boolean result, Request request) {
                        final MainVaranegarActivity activity = getVaranegarActvity();
                        if (result) {
                            if (activity != null && !activity.isFinishing()) {
                                stopProgressDialog();
                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                dialog.setTitle(R.string.done);
                                dialog.setMessage("مشتری جدید با موفقیت ثبت شد");
                                dialog.setIcon(Icon.Success);
                                dialog.setPositiveButton(R.string.ok,
                                        v -> activity.popFragment());
                                dialog.show();
                            }
                        } else {
                            if (activity != null && !activity.isFinishing()) {
                                stopProgressDialog();
                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                dialog.setMessage("خطا در ذخیره سازی ");
                                dialog.setTitle(R.string.error);
                                dialog.setIcon(Icon.Error);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
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
                });
    }

    private void createSyncViewModel() {

        /*
         * Edited By Mehrdad Latifi
         * */

/*        String codenaghsh=code_naghsh_paired_item.getValue();
        if (codenaghsh !=null&&!codenaghsh.equals("")) {*/
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
        syncGetNewCustomerViewModel.CodeNaghsh = code_naghsh_paired_item.getValue();

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
            syncGetNewCustomerViewModel.DealerUniqueId = um.UniqueId;
        }

        TourManager tourManager = new TourManager(getContext());
        TourModel tm = tourManager.loadTour();
        if (tm != null)
            syncGetNewCustomerViewModel.PathId = tm.DayVisitPathId;

        PaymentTypeOrderModel paymentTypeOrderModel =
                paymentTypeSpinner.getSelectedItem();
        if (paymentTypeOrderModel != null)
            syncGetNewCustomerViewModel.PaymentTypeId =
                    paymentTypeOrderModel.BackOfficeId;

        syncGetNewCustomerViewModel.latitude = latitude.getData();
        syncGetNewCustomerViewModel.longitude = longitude.getData();

        validator.validate(this);
/*        }
        else {
            showErrorDialog(" کدنقش مشتری را ثبت کنید ");
        }*/
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
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    Uri photoURI;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            try {
                file = createImageFile();
            } catch (IOException ex) {

            }

            if (file != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        getContext().getPackageName() + ".provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(getContext(), "Image saved", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoURI);
                nationalCardPic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void getDataCodeNaghsh() {

        String economicCode = economicCodePairedItem.getValue();
        String nationalCode = nationalCodePairedItem.getValue();

        roleCodeCustomerViewModel = new RoleCodeCustomerRequestViewModel();
        roleCodeCustomerViewModel.NationalCode = nationalCode;
        roleCodeCustomerViewModel.EconomicCode = economicCode;

        if (economicCode != null && !economicCode.equals("") && economicCode.length() == 11 ||
                nationalCode != null && !nationalCode.equals("") && nationalCode.length() == 10) {

            PingApi pingApi = new PingApi();
            startProgressDialog();
            pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    ApiNew apiNew = new ApiNew(getContext());
                    Call<List<RoleCodeViewModel>> calll = apiNew.getCodeNaghsh(roleCodeCustomerViewModel);

                    apiNew.runWebRequest(calll, new WebCallBack<List<RoleCodeViewModel>>() {
                        @Override
                        protected void onFinish() {
                            stopProgressDialog();
                        }

                        @Override
                        protected void onSuccess(List<RoleCodeViewModel> result, Request request) {

                            if (result.size() > 0) {
                                if (result.size() == 1) {
                                    code_naghsh_paired_item.setValue(result.get(0).Code);
                                    request_codenaghsh.setVisibility(View.GONE);
                                } else {
                                    dialogShow(result);
                                }
                            } else {
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

        } else {
            showErrorDialog("کد ملی و کداقتصادی مشتری را ثبت کنید ");
        }
    }

    private void dialogShow(List<RoleCodeViewModel> roleCodeViewModel) {
        String[] data = new String[roleCodeViewModel.size()];
        roleCodeViewModels = roleCodeViewModel;
        for (int i = 0; i < roleCodeViewModel.size(); i++) {
            data[i] = roleCodeViewModel.get(i).Title + roleCodeViewModel.get(i).Code;
        }
        SingleChoiceDialog singleChoiceDialog = new
                SingleChoiceDialog(getContext(), "کد نقش مورد نظر را انتخاب کنید", data);
        singleChoiceDialog.setCancelable(false);
        singleChoiceDialog.addItemClickListener(this);
        singleChoiceDialog.show(requireActivity().getSupportFragmentManager(),
                "Single Choice Dialog");
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        code_naghsh_paired_item.setValue(roleCodeViewModels.get(position).Code);
        request_codenaghsh.setVisibility(View.GONE);
    }

    @Override
    public void onNegativeButtonClicked() {

    }


    private void startProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void persistImage(Bitmap bitmap, String name) {
        File filesDir = getContext().getFilesDir();
        file = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

}
