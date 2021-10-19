package com.varanegar.vaslibrary.webapi.taskpriority;

import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Torabi on 1/15/2018.
 */

public interface ITaskPriorityApi {
    @GET("api/v2/ngt/devicesetting/taskpriority")
    Call<List<TaskPriorityModel>> getAll(@Query("deviceSettingNo") String deviceSettingNo, @Query("Date") String date);
}
