package com.varanegar.vaslibrary.webapi.imageapi;

import android.content.Context;

import com.varanegar.vaslibrary.manager.picture.ImageViewModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.image.ImageModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by A.Torabi on 11/1/2017.
 */

public class ImageApi extends BaseApi {
    public ImageApi(Context context) {
        super(context);
    }

    public Call<ResponseBody> postImage(ImageViewModel imageViewModel) {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.baseUrl(getBaseUrl())
                .build();
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data;"),
                        imageViewModel.file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(imageViewModel.imageId + ".jpg", imageViewModel.file.getName() + ".jpg", requestFile);
        String auth = getToken(TokenType.UserToken);
        return retrofit.create(IImageApi.class)
                .postImage(ownerKeysWrapper.OwnerKey,
                        ownerKeysWrapper.DataOwnerKey,
                        ownerKeysWrapper.DataOwnerCenterKey,
                        "Bearer " + auth,
                        body,
                        imageViewModel.token,
                        imageViewModel.imageId,
                        imageViewModel.imageType.toString(),
                        imageViewModel.isDefault);
    }


    public Call<ResponseBody> downloadImage(String type, String tokenId, String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .build();
        IImageApi api = retrofit.create(IImageApi.class);
        return api.downloadImage(type, tokenId, id);
    }

    public Call<List<ImageModel>> pictureInfo(String type, String dealerId) {
        IImageApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IImageApi.class);
        return api.pictureInfo(type, dealerId);
    }
}
