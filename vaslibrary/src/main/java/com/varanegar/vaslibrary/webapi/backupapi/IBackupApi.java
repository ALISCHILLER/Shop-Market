package com.varanegar.vaslibrary.webapi.backupapi;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by A.Torabi on 3/7/2018.
 */

public interface IBackupApi {
    @POST("api/v2/ngt/backup")
    @Multipart
    Call<ResponseBody> uploadBackup( @Header("FolderName")String folderName , @Header("TourNo") String TourNo,@Header("backUpName")String buckUpName, @Part MultipartBody.Part file );
}
