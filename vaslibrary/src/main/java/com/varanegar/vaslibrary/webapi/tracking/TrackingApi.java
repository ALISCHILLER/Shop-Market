package com.varanegar.vaslibrary.webapi.tracking;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.HeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import timber.log.Timber;

/**
 * Created by A.Torabi on 8/12/2017.
 */

public class TrackingApi extends BaseApi implements ITrackingApi {
    public static final String VARANEGAR_SEVER_ADDRESS_MAIN = "http://192.168.50.110:7072";
    public static final String VARANEGAR_SEVER_ADDRESS_TEST = "http://crm.varanegar.com:7071";
    private static OwnerKeysWrapper ownerKeysWrapper;

    public static void setOwnerKeysWrapper(OwnerKeysWrapper keys) {
        ownerKeysWrapper = keys;
    }

    public static String getDefaultServer(Context context) {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        if (ownerKeysWrapper == null || ownerKeysWrapper.DataOwnerKey == null)
            ownerKeysWrapper = sysConfigManager.readOwnerKeys();
        SysConfigModel trackingServerChk = sysConfigManager.read(ConfigKey.TrackingServerChk, SysConfigManager.cloud);
        SysConfigModel trackingServerUrl = sysConfigManager.read(ConfigKey.TrackingServerUrl, SysConfigManager.cloud);

        if (SysConfigManager.compare(trackingServerChk, true) && SysConfigManager.getStringValue(trackingServerUrl, null) != null)
            return SysConfigManager.getStringValue(trackingServerUrl, null);
        else {
            String MAIN_SERVER = VARANEGAR_SEVER_ADDRESS_MAIN;
            if ("C9108AE4-6172-45EE-B38A-E2ABBDF05ED6".equalsIgnoreCase(ownerKeysWrapper.DataOwnerKey))
                MAIN_SERVER = "http://5.160.29.83:7070";
            else if ("b0654da0-b840-428e-b545-d01610c1d963".equalsIgnoreCase(ownerKeysWrapper.DataOwnerKey))
                MAIN_SERVER = "http://185.3.213.59:9095";
            else if ("587DFBAD-B1BB-457A-8A86-CD1F1683F5FA".equalsIgnoreCase(ownerKeysWrapper.DataOwnerKey))
                MAIN_SERVER = "http://46.209.116.110:9095";
            else if ("8F32C8FD-7170-4B95-8697-B676F37B8B4B".equalsIgnoreCase(ownerKeysWrapper.DataOwnerKey))
                MAIN_SERVER = "http://109.162.254.238:9095";
            else if ("64356085-D1FC-4C76-805A-EEA54EB51F35".equalsIgnoreCase(ownerKeysWrapper.DataOwnerKey))
                MAIN_SERVER = "http://178.173.131.177:9095";
            else if ("C0364F6F-D52E-459F-B549-552F31DA5A7E".equalsIgnoreCase(ownerKeysWrapper.DataOwnerKey))
                MAIN_SERVER = "http://94.139.176.24:9095";
            
            String TEST_OWNER_ID = "3784C8E6-B379-4812-9F72-5AF14DB5B92D";
            if (ownerKeysWrapper.DataOwnerKey != null &&
                    !ownerKeysWrapper.DataOwnerKey.isEmpty() &&
                    (ownerKeysWrapper.DataOwnerKey.equalsIgnoreCase(TEST_OWNER_ID) || ownerKeysWrapper.DataOwnerKey.equalsIgnoreCase("B2A54E5A-8C96-44C8-A99F-E44E3EA0513F")))
                return VARANEGAR_SEVER_ADDRESS_TEST;
            else
                return MAIN_SERVER;
        }
    }

    private String getDefaultServer() {
        return getDefaultServer(getContext());
    }

    public TrackingApi(Context context) {
        super(context);
    }

    private Retrofit buildRetrofit(String baseUrl) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();
        builder.addInterceptor(new HeaderInterceptor(ownerKeys, "", null));
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build().setLenient();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
    }

    @Override
    public Call<LicenseResponse> getLicense(@Body LicenseRequestBody body) {
        return buildRetrofit(VARANEGAR_SEVER_ADDRESS_MAIN).create(ITrackingApi.class).getLicense(body);
    }

    @Override
    public Call<DeviceSaveResponse> requestLicense(@Body LicenseRequestBody body) {
        return buildRetrofit(VARANEGAR_SEVER_ADDRESS_MAIN).create(ITrackingApi.class).requestLicense(body);
    }

    @Override
    public Call<Boolean> sendPoint(@Body TrackingRequestModel body) {
        Timber.i("LocationManager sendPoint 1");
        String baseUrl = getDefaultServer();
        if (!baseUrl.equals(TrackingApi.VARANEGAR_SEVER_ADDRESS_MAIN) && !baseUrl.equals(TrackingApi.VARANEGAR_SEVER_ADDRESS_TEST))
            runWebRequest(sendPoint(TrackingApi.VARANEGAR_SEVER_ADDRESS_MAIN, body), null);
        return buildRetrofit(getDefaultServer()).create(ITrackingApi.class).sendPoint(body);
    }

    public Call<Boolean> sendPoint(String baseUrl, @Body TrackingRequestModel body) {
        Timber.i("LocationManager sendPoint 2");
        if (!baseUrl.equals(TrackingApi.VARANEGAR_SEVER_ADDRESS_MAIN) && !baseUrl.equals(TrackingApi.VARANEGAR_SEVER_ADDRESS_TEST))
            runWebRequest(sendPoint(TrackingApi.VARANEGAR_SEVER_ADDRESS_MAIN, body), null);
        return buildRetrofit(baseUrl).create(ITrackingApi.class).sendPoint(body);
    }
}
