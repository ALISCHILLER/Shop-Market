package com.varanegar.vaslibrary.webapi;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.base.account.AccountManager;
import com.varanegar.framework.base.account.Token;
import com.varanegar.framework.network.WebRequest;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.manager.dealerdivision.DealerDivisionManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by atp on 6/7/2017.
 */

public abstract class BaseApi extends WebRequest {
    protected static final long DEFAULT_CONNECT_TIMEOUT = 0;
    protected static final long DEFAULT_READ_TIMEOUT = 0;
    protected static final long DEFAULT_WRITE_TIMEOUT = 0;
    private Context context;

    protected Context getContext() {
        return context;
    }

    public BaseApi(Context context) {
        this.context = context;
    }

    private String getWifiSSID() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getSSID();
    }

    protected String getBaseUrl() {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel serverAddress =
                sysConfigManager.read(ConfigKey.BaseAddress, SysConfigManager.local);
        if (serverAddress == null)
            return "http://localhost";
        if (serverAddress.Value.endsWith("/"))
            serverAddress.Value = HelperMethods.removeLastChar(serverAddress.Value);
        return serverAddress.Value;
    }

    protected OkHttpClient getClient(TokenType tokenType, long connectTimeout,
                                     long readTimeOut, long writeTimeout) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();
        DealerDivisionManager divisionManager = new DealerDivisionManager(getContext());
        builder.addInterceptor(new HeaderInterceptor(ownerKeys, getToken(tokenType), divisionManager.getDealerDivisionModel()));
        return builder.build();
    }

    protected OkHttpClient getClient(long connectTimeout,
                                     long readTimeOut, long writeTimeout) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();
        builder.addInterceptor(new HeaderInterceptor(ownerKeys, null, null));
        return builder.build();
    }

    public Retrofit.Builder getRetrofitBuilder(TokenType tokenType) {
        OkHttpClient client = getClient(tokenType, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
    }

    public Retrofit.Builder getRetrofitBuilder() {
        OkHttpClient client = getClient(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
    }

    public Retrofit.Builder getRetrofitBuilder(TokenType tokenType, String baseUrl) {
        OkHttpClient client = getClient(tokenType, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
    }

    public Retrofit.Builder getRetrofitBuilder(OkHttpClient client) {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
    }

    public Retrofit.Builder getRetrofitBuilder(String baseUrl, OkHttpClient client) {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
    }

    public Retrofit.Builder getRetrofitBuilder(String baseUrl, boolean camelCase, OkHttpClient client) {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build(camelCase);
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
    }

    protected String getToken(TokenType tokenType) {
        AccountManager accountManager = new AccountManager();
        if (tokenType == TokenType.UserToken) {
            Token token = accountManager.readFromFile(context, "user.token");
            return token == null ? null : token.accessToken;
        } else {
            Token token = accountManager.readFromFile(context, "app.token");
            return token == null ? null : token.accessToken;
        }
    }

    public void saveResponseBodyToPublicFolder(ResponseBody body, String folder, String fileName) throws IOException {
        byte[] buffer = new byte[4096];
        InputStream inputStream = body.byteStream();
        OutputStream outputStream;
        if (!folder.startsWith("/"))
            folder = "/" + folder;
        if (!folder.endsWith("/"))
            folder = folder + "/";

        String path = HelperMethods.getExternalFilesDir(getContext(), null).getAbsolutePath();
        File file = new File(path + folder);
        if (!file.exists())
            file.mkdirs();
        path = path + folder + fileName;
        outputStream = new FileOutputStream(path);


        try {
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        } catch (IOException ex) {
            Timber.e(ex);
            throw ex;
        }
    }

    public void saveResponseBodyToPrivateFolder(ResponseBody body, String fileName) throws IOException {
        byte[] buffer = new byte[4096];
        InputStream inputStream = body.byteStream();
        OutputStream outputStream;
        outputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);

        try {
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        } catch (IOException ex) {
            Timber.e(ex);
            throw ex;
        }
    }
}
