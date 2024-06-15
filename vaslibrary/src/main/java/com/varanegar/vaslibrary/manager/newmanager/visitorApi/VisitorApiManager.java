package com.varanegar.vaslibrary.manager.newmanager.visitorApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.manager.newmanager.dataCustomersContentManager.DataCustomerContentManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.goodsPackageItem.GoodsPackageItemModelRepository;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.List;

import okhttp3.Request;
import retrofit2.Call;

public class VisitorApiManager {
    static Context context;
    public interface Callback {
        void onSuccess();

        void onError(String error);
    }

    public VisitorApiManager(@NonNull Context context) {
     this.context=context;
    }
    public static void getCheckVisitor(
            final UpdateCall updateCall
    ) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences("CountVisitCustomersNotIn", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear();
        UserModel userModel = UserManager.readFromFile(context);
        ApiNew apiNew=new ApiNew(context);

        StringBuilder digits = new StringBuilder();
        StringBuilder letters = new StringBuilder();

        for (int i = 0; i < userModel.UserName.length(); i++) {
            char ch = userModel.UserName.charAt(i);
            if (Character.isDigit(ch)) {
                digits.append(ch);
            } else if (Character.isLetter(ch) || Character.isWhitespace(ch)) {
                letters.append(ch);
            }
        }
        String numbersString = digits.toString();

        Call<Boolean> call = apiNew
                .CheckVisitor(numbersString);

        apiNew.runWebRequest(call, new WebCallBack<Boolean>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(Boolean result, Request request) {
                if (result){
                    updateCall.success();
                }else{
                    updateCall.failure("حساب کاربری شما بلاک می باشد. لطفا با شعبه در تماس باشید!");
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                updateCall.failure(err);
                Log.e("err", String.valueOf(err));
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                updateCall.failure(context.getString(R.string.network_error));
            }
        });
    }
}
