package com.varanegar.vaslibrary.ui.fragment.new_fragment.edit_new_zar;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.base.questionnaire.controls.AttachImageDialog;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.CuteDialog;
import com.varanegar.framework.util.component.PairedItemsEditable;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.FormValidator;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.annotations.IraniIdCodeChecker;
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
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.city.CityModel;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerCategoryModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.dataforregister.DataForRegisterModel;
import com.varanegar.vaslibrary.ui.dialog.EditCustomerZarFragmentDialog;
import com.varanegar.vaslibrary.ui.dialog.new_dialog.SingleChoiceDialog;
import com.varanegar.vaslibrary.ui.fragment.CustomersContentFragment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeCustomerRequestViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeViewModel;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;
import com.varanegar.vaslibrary.webapi.customer.SyncZarGetNewCustomerViewModel;
import com.varanegar.vaslibrary.webapi.customer.ZarCustomerInfoViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class Edit_New_Customer_ZarFragment extends VaranegarFragment implements ValidationListener
        , SingleChoiceDialog.SingleChoiceListener {

    CustomerModel customer;
    public EditCustomerZarFragmentDialog.OnCustomerEditedCallBack onCustomerEditedCallBack;
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
    private ConstraintLayout camera_linear_view;
    private ImageView save_customer_image;
    private Button request_codenaghsh;
    //    private PairedItemsSpinner<CityModel> citySpinner;
    private PairedItemsSpinner<DataForRegisterModel> deliveryZoneSpinner;
    private PairedItemsEditable telPairedItem;
    private PairedItemsEditable mobilePairedItem;
    private PairedItemsEditable economicCodePairedItem;
    private PairedItemsEditable nationalCodePairedItem;
    private ImageView nationalCardPic;
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
    private RoleCodeCustomerRequestViewModel roleCodeCustomerViewModel;

    private LinearLayout header_linear_layout;
    private RelativeLayout main_layout;
    private CameraView cameraView;
    private FloatingActionButton take_picture;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("68565e5e-d407-4858-bc5f-fd52b9318734", customer.UniqueId.toString());
    }


    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

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


    public AttachImageDialog.OnAttachment onAttachment;

    public interface OnAttachment {
        void onDone();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (customer.CustomerCode != null) {
            prepareFields();
            enableForm(true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new FormValidator(getActivity());

        if (savedInstanceState != null)
            customerUniqueId = UUID.fromString(savedInstanceState
                    .getString("68565e5e-d407-4858-bc5f-fd52b9318734"));
        else {
            Bundle bundle = getArguments();
            customerUniqueId = UUID.fromString(bundle
                    .getString("68565e5e-d407-4858-bc5f-fd52b9318734"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(
                R.layout.fragment_edit_customer_zar, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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


        personNamePairedItem = view.findViewById(R.id.person_name_paired_item);
        validator.addField(personNamePairedItem, getString(R.string.person_name_label), new NotEmptyChecker());

        tabloNamePairedItem = view.findViewById(R.id.tablo_name_paired_item);


        /**گرفتن عکس*/
        nationalCardPic = view.findViewById(R.id.national_card_pic);
        main_layout = view.findViewById(R.id.main_layout);
        header_linear_layout = view.findViewById(R.id.header_linear_layout);
        take_picture = view.findViewById(R.id.take_picture);
        cameraView = view.findViewById(R.id.camera_view);
        nationalCardPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //    dispatchTakePictureIntent();
                main_layout.setVisibility(View.VISIBLE);
                header_linear_layout.setVisibility(View.GONE);
            }
        });


        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                CameraUtils.decodeBitmap(picture, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        persistImage(bitmap,"fh");
                        nationalCardPic.setImageBitmap(bitmap);
                        if (onAttachment != null)
                            onAttachment.onDone();

                        main_layout.setVisibility(View.GONE);
                        header_linear_layout.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.capturePicture();
            }
        });


        /**  آدرس*/
        addressPairedItem = view.findViewById(R.id.address_paired_item);
        validator.addField(addressPairedItem, getString(R.string.address), new LengthChecker(0, 150,
                false));
        addressPairedItem.setEnabled(false);
        addressPairedItem.setFocusable(false);
        addressPairedItem.setFocusableInTouchMode(false);
        //آدرس 2
        street2PairedItem = view.findViewById(R.id.street2_paired_item);
        validator.addField(street2PairedItem, getString(R.string.street2_label), new LengthChecker(0, 150,
                false));
        street2PairedItem.setEnabled(false);
        street2PairedItem.setFocusable(false);
        street2PairedItem.setFocusableInTouchMode(false);
        //آدرس3
        street3PairedItem = view.findViewById(R.id.street3_paired_item);
        validator.addField(street3PairedItem, getString(R.string.street3_label), new LengthChecker(0, 150,
                false));
        street3PairedItem.setEnabled(false);
        street3PairedItem.setFocusable(false);
        street3PairedItem.setFocusableInTouchMode(false);
        //  آدرس4
        street4PairedItem = view.findViewById(R.id.street4_paired_item);
        validator.addField(street4PairedItem, getString(R.string.street4_label), new LengthChecker(0, 150,
                false));
        street4PairedItem.setEnabled(false);
        street4PairedItem.setFocusable(false);
        street4PairedItem.setFocusableInTouchMode(false);
        //   آدرس5
        street5PairedItem = view.findViewById(R.id.street5_paired_item);
        validator.addField(street5PairedItem, getString(R.string.street5_label),
                new LengthChecker(0, 150, false));
        street5PairedItem.setEnabled(false);
        street5PairedItem.setFocusable(false);
        street5PairedItem.setFocusableInTouchMode(false);


        /**
         *     کد نقش
         */
        code_naghsh_paired_item = view.findViewById(R.id.code_naghsh_paired_item);
        validator.addField(code_naghsh_paired_item, getString(R.string.code_naghsh),
                new LengthChecker(0, 100, false));
        code_naghsh_paired_item.setEnabled(false);
        code_naghsh_paired_item.setFocusable(false);
        code_naghsh_paired_item.setFocusableInTouchMode(false);

        request_codenaghsh = view.findViewById(R.id.request_codenaghsh);

        /**
         *        کدپستی
         */
        postalCodePairedItem = view.findViewById(R.id.postal_code_paired_item);
        validator.addField(postalCodePairedItem, getString(R.string.postal_code_label), new LengthChecker(10, 10,
                false));

        cityNamePairedItem = view.findViewById(R.id.city_name_paired_item);

        deliveryZoneSpinner = view.findViewById(R.id.delivery_zone_spinner);
        validator.addField(deliveryZoneSpinner, getString(R.string.delivery_zone), new NotEmptyChecker());


        telPairedItem = view.findViewById(R.id.tel_paired_item);

        mobilePairedItem = view.findViewById(R.id.mobile_paired_item);

        /**
         *    شناسه ملی
         */
        economicCodePairedItem = view.findViewById(R.id.economic_code_paired_item);
        validator.addField(economicCodePairedItem, "شناسه ملی",
                new IraniIdCodeChecker());

        /**
         * کد ملی
         */
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
        camera_linear_view = view.findViewById(R.id.camera_linear_view);
        save_customer_image = view.findViewById(R.id.save_customer_image);
        save_customer_image.setOnClickListener(v -> {
            createSyncViewModel();

        });
        request_codenaghsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataCodeNaghsh();
            }
        });

    }

    @Override
    public void onValidationSucceeded() {
        submit();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validator.showErrors(errors);
    }


    @Override
    public void onNegativeButtonClicked() {

    }


    /**
     * ست کردن کد نقش بعد از انتخاب
     *
     * @param list
     * @param position
     */
    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        code_naghsh_paired_item.setValue(roleCodeViewModels.get(position).Code);
        request_codenaghsh.setVisibility(View.GONE);
        nationalCodePairedItem.setEnabled(false);
        nationalCodePairedItem.setFocusable(false);
        economicCodePairedItem.setFocusable(false);
        economicCodePairedItem.setFocusableInTouchMode(false);
        economicCodePairedItem.setEnabled(false);
        nationalCodePairedItem.setFocusableInTouchMode(false);
    }

    /**
     * ست کردن داده براس ارسال
     */

    private void createSyncViewModel() {
        // syncGetNewCustomerViewModel.CityId = cityNamePairedItem.getValue();
        String postcode = postalCodePairedItem.getValue();
        String codenaghsh = code_naghsh_paired_item.getValue();
        DataForRegisterModel group1 = customerGroup1Spinner.getSelectedItem();
        DataForRegisterModel group2 = customerGroup2Spinner.getSelectedItem();
        DataForRegisterModel degree = customerDegreeSpinner.getSelectedItem();

        if (file == null && !customer.HasNationalCodeImage) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.setMessage(getString(R.string.taking_picture_of) + "کارت ملی " + " " + getString(R.string.is_mandatory));
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
            return;
        }

        if (postcode != null && !postcode.equals("") && !postcode.isEmpty()
                && !codenaghsh.isEmpty() && codenaghsh != null && !codenaghsh.equals("")) {
            if (addressPairedItem != null && personNamePairedItem != null && group1 != null && group2 != null) {

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
            } else {
                showErrorDialog("نام مشتری ,آدرس مشتری ,گروه مشتری1 , گروه مشتری 2و درجه مشتری  را ثیت کنید ");
            }
        } else {
            showErrorDialog("کد پستی و کدنقش مشتری را ثبت کنید ");
        }
    }

    /**
     * دریافت کد نقش
     * send request get code naghsh
     */
    private void getDataCodeNaghsh() {


        String economicCode = economicCodePairedItem.getValue();
        String nationalCode = nationalCodePairedItem.getValue();

        roleCodeCustomerViewModel = new RoleCodeCustomerRequestViewModel();
        roleCodeCustomerViewModel.CustomerId = customerUniqueId;
        roleCodeCustomerViewModel.NationalCode = nationalCode;
        roleCodeCustomerViewModel.EconomicCode = economicCode;

        if (economicCode != null && !economicCode.equals("") && economicCode.length() == 11 ||
                nationalCode != null && !nationalCode.equals("") && nationalCode.length() == 10) {

            ApiNew apiNew = new ApiNew(getContext());
            Call<List<RoleCodeViewModel>> calll = apiNew.getCodeNaghsh(roleCodeCustomerViewModel);
            startProgressDialog();

            apiNew.runWebRequest(calll, new WebCallBack<List<RoleCodeViewModel>>() {
                @Override
                protected void onFinish() {
                    stopProgressDialog();
                }

                @Override
                protected void onSuccess(List<RoleCodeViewModel> result, Request request) {

                    if (result.size() > 0) {

                        dialogShow(result);

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

        } else {
            showErrorDialog("کد ملی و کداقتصادی مشتری را ثبت کنید ");
        }
    }

    /**
     * ست کردن دادها
     */
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
            postalCodePairedItem.setValue(customer.CustomerPostalCode);

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
            telPairedItem.setValue(customer.Phone);

        if (customer.Mobile != null)
            mobilePairedItem.setValue(customer.Mobile);


        if (customer.CodeNaghsh != null && !customer.CodeNaghsh.isEmpty()) {
            code_naghsh_paired_item.setValue(customer.CodeNaghsh);
            economicCodePairedItem.setEnabled(false);
            economicCodePairedItem.setFocusable(false);
            economicCodePairedItem.setFocusableInTouchMode(false);
            nationalCodePairedItem.setEnabled(false);
            nationalCodePairedItem.setFocusable(false);
            nationalCodePairedItem.setFocusableInTouchMode(false);
        } else {
            request_codenaghsh.setVisibility(View.VISIBLE);
        }
        if (!customer.EconomicCode.isEmpty()) {
            economicCodePairedItem.setValue(customer.EconomicCode);

        }
        if (!customer.NationalCode.isEmpty()) {
            nationalCodePairedItem.setValue(customer.NationalCode);

        }
        if (customer.HasNationalCodeImage) {
            camera_linear_view.setVisibility(View.GONE);
        } else {
            camera_linear_view.setVisibility(View.VISIBLE);
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
            if (customer.CustomerCategoryId != null) {
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


    /**
     * ارسال داده
     */
    public void submit() {
        syncGetNewCustomerViewModel.customerUniqueId = customerUniqueId;
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
                        sendNationalImage(result);


                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showErrorDialog(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        showErrorDialog(getString(R.string.network_error));
                    }
                });

    }

    /**
     * ارسال عکس
     *
     * @param result
     */
    private void sendNationalImage(String result) {
        if (getContext() == null) return;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new PictureCustomerViewManager(getContext())
                .uploadNationalCard(customerUniqueId, file, new UpdateCall() {
                    @Override
                    protected void onSuccess() {
                        Timber.i("customer national card have been sent");
                        final MainVaranegarActivity activity = getVaranegarActvity();
                        if (activity != null && !activity.isFinishing()) {
                            stopProgressDialog();
                            showResaltDialog(result);
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


    /**
     * دیالوگ ست کردن کد نقش
     *
     * @param roleCodeViewModel
     */
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
        singleChoiceDialog.show(getActivity().getSupportFragmentManager(),
                "Single Choice Dialog");
    }

    /**
     * دوربین عکس
     */
    //region capture image
    File file = null;
    static final int REQUEST_IMAGE_CAPTURE = 0;
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

    /**
     * دوربین عکس
     */
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
                        getActivity().getPackageName() + ".provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * دوربین عکس
     */
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


    private void stopProgressDialog() {
        if (getVaranegarActvity() != null && progressDialog != null && progressDialog.isShowing())
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
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
    public void onBackPressed() {
        super.onBackPressed();
        setSharedPer();

    }

    public void setSharedPer() {
        SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences("preferred_local", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(customer.UniqueId.toString(), "true").apply();
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
            setSharedPer();
            CustomersContentFragment contentFragment = new CustomersContentFragment();
            contentFragment.addArgument("a129ef75-77ce-432a-8982-6bcab0bf7b51", customer.UniqueId.toString());
            getVaranegarActvity().pushFragment(contentFragment);
        }
    }

    private void startProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
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

    public void copy() {
        try {
            file = createImageFile();
        } catch (IOException ex) {

        }

        if (file != null) {
            photoURI = FileProvider.getUriForFile(getContext(),
                    getActivity().getPackageName() + ".provider",
                    file);
        }

        try (InputStream in = getContext().getContentResolver().openInputStream(photoURI);
             OutputStream out = new FileOutputStream(file)) {
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
