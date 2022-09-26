package varanegar.com.discountcalculatorlib.api;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import varanegar.com.discountcalculatorlib.utility.DiscountException;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;
import varanegar.com.discountcalculatorlib.viewmodel.OwnerKeysViewModel;

/**
 * Created by atp on 6/7/2017.
 */

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        OwnerKeysViewModel ownerKeys = GlobalVariables.getOwnerKeys();
        Request originalRequest = chain.request();
        if (ownerKeys == null) {
            throw new DiscountException("Owner key is null");
        }
        else {
            if (ownerKeys.OwnerKey == null || ownerKeys.DataOwnerKey == null || ownerKeys.DataOwnerCenterKey == null)
                throw new DiscountException("Owner key value is null");
            else {
                Request request = originalRequest.newBuilder()
                            .header("OwnerKey", ownerKeys.OwnerKey)
                            .header("DataOwnerKey", ownerKeys.DataOwnerKey)
                            .header("DataOwnerCenterKey", ownerKeys.DataOwnerCenterKey)
                            .header("Accept", " application/json")
                            .header("http.keepAlive", "false")
                            .header("Authorization", "Bearer " + ownerKeys.Token)
                            .header("SubSystemTypeId",ownerKeys.subsystemtypeid)
                            .header("Version",ownerKeys.Version)
                            .build();

                Log.e("Header", String.valueOf(request));
                return chain.proceed(request);
            }

        }

    }
}
