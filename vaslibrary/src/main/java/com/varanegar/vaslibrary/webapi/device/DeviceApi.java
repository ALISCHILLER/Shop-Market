package com.varanegar.vaslibrary.webapi.device;

import android.content.Context;

import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.tracking.LicenseRequestBody;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceApi extends BaseApi implements IDeviceApi {
    public DeviceApi(Context context) {
        super(context);
    }

    private static OwnerKeysWrapper ownerKeysWrapper;

    public static String getDefaultServer(Context context) {
        if (ownerKeysWrapper == null || ownerKeysWrapper.DataOwnerKey == null)
            ownerKeysWrapper = new SysConfigManager(context).readOwnerKeys();
        String TEST_SERVER = "http://crm.varanegar.com:7071";
        String MAIN_SERVER = "http://crm.varanegar.com:7070";
        String TEST_OWNER_ID = "3784C8E6-B379-4812-9F72-5AF14DB5B92D";
        if (ownerKeysWrapper.DataOwnerKey != null && !ownerKeysWrapper.DataOwnerKey.isEmpty() && (ownerKeysWrapper.DataOwnerKey.equalsIgnoreCase(TEST_OWNER_ID) || ownerKeysWrapper.DataOwnerKey.equalsIgnoreCase("B2A54E5A-8C96-44C8-A99F-E44E3EA0513F")))
            return TEST_SERVER;
        else
            return MAIN_SERVER;
    }

    @Override
    public Call<CompanyDeviceAppResult> checkLicense(LicenseRequestBody info) {
        IDeviceApi api = getRetrofitBuilder(getDefaultServer(getContext()),
                getClient(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT))
                .build().create(IDeviceApi.class);
        return api.checkLicense(info);
    }

    @Override
    public Call<Void> registerDeviceToken(UserDeviceTokenViewModel tokenViewModel) {
        IDeviceApi api = getRetrofitBuilder(TokenType.UserToken, "http://77.238.123.10:12301").build().create(IDeviceApi.class);
        return api.registerDeviceToken(tokenViewModel);
    }
}
