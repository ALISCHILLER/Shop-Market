package com.varanegar.vaslibrary.webapi.taskpriority;

import android.content.Context;

import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 1/15/2018.
 */

public class TaskPriorityApi extends BaseApi implements ITaskPriorityApi {
    public TaskPriorityApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<TaskPriorityModel>> getAll(String deviceSettingNo, String date) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ITaskPriorityApi.class).getAll(deviceSettingNo, date);
    }
}
