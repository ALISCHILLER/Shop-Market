package com.varanegar.vaslibrary.promotion;

import android.app.Activity;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.gson.typeadapters.CurrencyTypeAdapter;
import com.varanegar.framework.network.gson.typeadapters.DateTypeAdapter;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.manager.CustomerMainSubTypeManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.model.customer.CustomerMainSubTypeModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.product.ProductModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.DiscountCalculatorHandler;
import varanegar.com.discountcalculatorlib.handler.IDiscountInitializeHandlerV3;
import varanegar.com.discountcalculatorlib.utility.enumerations.EVCType;
import varanegar.com.vdmclient.VdmInitializer;
import varanegar.com.vdmclient.call.CalcData;
import varanegar.com.vdmclient.call.Customer;
import varanegar.com.vdmclient.call.CustomerMainSubType;
import varanegar.com.vdmclient.call.Goods;

/**
 * Created by A.Torabi on 3/13/2019.
 */

public class TestHelper {

    private final Activity context;

    public TestHelper(Activity context) {
        this.context = context;
    }

    public CalcData importPack(String file) throws IOException, ClassNotFoundException {
        Timber.d("Importing pack started...");

        String filePath = HelperMethods.getExternalFilesDir(context, "evc_pack") + "/" + file;
        String json = readTextFile(filePath);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Currency.class, new CurrencyTypeAdapter());
        gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        CalcData calcData = gsonBuilder.create().fromJson(json, CalcData.class);
        Timber.d("Importing pack finished.");
        return calcData;
    }

    private String readTextFile(String filePath) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            Timber.e(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }
        return builder.toString();
    }

//    public void runSingle(IDiscountInitializeHandlerV3 disc, String file, final VaranegarActivity varanegarActivity) throws IOException, ClassNotFoundException, ValidationException, DbException {
//        CalcData calcData = importPack(file);
//        fillVndb(calcData);
//        DiscountCalculatorHandler.init(disc, 0, null);
//        DiscountCalculatorHandler.fillInitData(disc, new VdmInitializer.InitCallback() {
//            @Override
//            public void onSuccess(String s) {
//                Timber.d(s);
//                // todo: fill vndb call orders from call data
//
//                CalcPromotion.calcPromotionV3(false, context, null, null, EVCType.HOTSALE, true, false, false, new PromotionCallback() {
//                    @Override
//                    public void onSuccess(CustomerCallOrderPromotion data) {
//                        //todo : verify data with call data
//                    }
//
//                    @Override
//                    public void onFailure(String error) {
//                        Timber.e(error);
//                    }
//                }, varanegarActivity);
//            }
//
//            @Override
//            public void onFailure(String s) {
//                Timber.e(s);
//            }
//        });
//        CalcData calcData = importPack(file);
//        fillVndb(calcData);
//        DiscountCalculatorHandler.init(disc, 0, null);
//        DiscountCalculatorHandler.fillInitData(disc, new VdmInitializer.InitCallback() {
//            @Override
//            public void onSuccess(String s) {
//                Timber.d(s);
//                // todo: fill vndb call orders from call data
//
//                CalcPromotion.calcPromotionV3(context, null, null, EVCType.HOTSALE, true, false, false, new PromotionCallback() {
//                    @Override
//                    public void onSuccess(CustomerCallOrderPromotion data) {
//                        //todo : verify data with call data
//                    }
//
//                    @Override
//                    public void onFailure(String error) {
//                        Timber.e(error);
//                    }
//                }, varanegarActivity);
//            }
//
//            @Override
//            public void onFailure(String s) {
//                Timber.e(s);
//            }
//        });


//    }

    private void fillVndb(CalcData calcData) throws ValidationException, DbException {
        // TODO: 3/16/2019 add CustomerBoGroupManager
        // CustomerMainSubType
        CustomerMainSubTypeManager customerMainSubTypeManager = new CustomerMainSubTypeManager(context);
        customerMainSubTypeManager.deleteAll();
        List<CustomerMainSubTypeModel> customerMainSubTypeModels = new ArrayList<>();
        for (CustomerMainSubType customerMainSubType :
                calcData.CustomerMainSubTypes) {
            CustomerMainSubTypeModel customerMainSubTypeModel = new CustomerMainSubTypeModel();
            customerMainSubTypeModel.CustRef = customerMainSubType.CustRef;
            customerMainSubTypeModel.Id = customerMainSubType.Id;
            customerMainSubTypeModel.MainTypeRef = customerMainSubType.MainTypeRef;
            customerMainSubTypeModel.SubTypeRef = customerMainSubType.SubTypeRef;
            customerMainSubTypeModel.UniqueId = UUID.randomUUID();
            customerMainSubTypeModels.add(customerMainSubTypeModel);
        }
        customerMainSubTypeManager.insert(customerMainSubTypeModels);

        // Customer
        CustomerManager customerManager = new CustomerManager(context);
        customerManager.deleteAll();
        List<CustomerModel> customerModels = new ArrayList<>();
        for (Customer customer :
                calcData.Customers) {
            CustomerModel customerModel = new CustomerModel();
            customerModel.CustomerName = customer.CustName;
            customerModel.UniqueId = customer.UniqueId;
            // TODO: customerModel.AreaId = customer.AreaRef;
            // TODO: 3/16/2019 add all fields

            customerModels.add(customerModel);
        }
        customerManager.insert(customerModels);


        // TODO: 3/16/2019 Add GoodsFixUnit

        // Goods
        ProductManager productManager = new ProductManager(context);
        productManager.deleteAll();
        List<ProductModel> productModels = new ArrayList<>();
        for (Goods goods:
             calcData.Goods) {
            ProductModel productModel = new ProductModel();
            // TODO: 3/16/2019 Add fields
        }
    }
}
