package varanegar.com.discountcalculatorlib.api;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.network.WebRequest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import varanegar.com.discountcalculatorlib.model.DealerDivisionModelDC;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

public abstract class BaseApi extends WebRequest {
    protected static final long DEFAULT_CONNECT_TIMEOUT = 0;
    protected static final long DEFAULT_READ_TIMEOUT = 0;
    protected static final long DEFAULT_WRITE_TIMEOUT = 0;

    protected OkHttpClient getClient(long connectTimeout, long readTimeOut, long writeTimeout,
                                     DealerDivisionModelDC dealerDivisionModelDC) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        builder.addInterceptor(new HeaderInterceptor(dealerDivisionModelDC));
        return builder.build();
    }

    public Retrofit.Builder getRetrofitBuilder(DealerDivisionModelDC dealerDivisionModelDC) {
        OkHttpClient client = getClient(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT, dealerDivisionModelDC);
        GsonBuilder gsonBuilder = new GsonBuilder();
        return new Retrofit.Builder()
                .baseUrl(GlobalVariables.getServerUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
    }

}
