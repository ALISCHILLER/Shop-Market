package com.varanegar.vaslibrary.webapi.promotion;

import com.varanegar.vaslibrary.model.bank.BankModel;
import com.varanegar.vaslibrary.ui.fragment.ReturnDisEvcHeaderViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 3/26/2018.
 */

public interface IPromotionApi {
    @POST("api/v2/ngt/evc/returnDisControl")
    Call<ReturnDistViewModel> returnDisControl(@Body List<ReturnDisEvcHeaderViewModel> body);
}
