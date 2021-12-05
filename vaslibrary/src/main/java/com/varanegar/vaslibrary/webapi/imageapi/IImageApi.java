package com.varanegar.vaslibrary.webapi.imageapi;

import com.varanegar.vaslibrary.model.image.ImageModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 11/1/2017.
 */

public interface IImageApi {
    @Multipart
    @POST("api/imageManager")
    Call<ResponseBody> postImage(@Header("OwnerKey") String ownerKey,
                                 @Header("DataOwnerKey") String dataownerkey,
                                 @Header("DataOwnerCenterKey") String dataownercenterkey,
                                 @Header("Authorization") String authorization,
                                 @Header("SubSystemTypeId") String subsystemtypeid,
                                 @Header("Version") String Version,
                                 @Part MultipartBody.Part file,
                                 @Query("token") String token,
                                 @Query("imageId") String imageId,
                                 @Query("imagetype") String imageType,
                                 @Query("isDefault") boolean isDefault);

    @GET("Content/Images/{type}/orginal/{tokenId}/{id}")
    Call<ResponseBody> downloadImage(@Path("type") String type, @Path("tokenId") String tokenId, @Path("id") String id);

    @GET("api/v2/ngt/pictureinfo")
    Call<List<ImageModel>> pictureInfo(@Query("imageType") String type, @Query("dealerId") String dealerId);
}
