package com.varanegar.vaslibrary.webapi.backupapi;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Base64;

import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by A.Torabi on 3/7/2018.
 */

public class BackupApi extends BaseApi {
    public BackupApi(Context context) {
        super(context);
    }

    public Call<ResponseBody> uploadBackup(@Nullable UserModel userModel, @NonNull File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/zip"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        if (userModel != null && userModel.UserName != null && !userModel.UserName.isEmpty()) {
            String encoded = Base64.encodeToString(userModel.UserName.getBytes(), Base64.NO_WRAP);
            return getRetrofitBuilder(TokenType.UserToken).build().create(IBackupApi.class).uploadBackup(encoded, body);
        } else
            return getRetrofitBuilder(TokenType.UserToken).build().create(IBackupApi.class).uploadBackup("NO USER", body);
    }
}
