package com.varanegar.vaslibrary.webapi.discount;

import com.varanegar.vaslibrary.model.dissaleprizepackagesds.DisSalePrizePackageSDSModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import varanegar.com.discountcalculatorlib.dataadapter.evc.sds.DisSalePrizePackageDBAdapter;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */

public interface IDisSalePrizePackageSDSApi {
    @GET("api/v2/ngt/goodspackage/dissaleprizepackage")
    Call<List<DisSalePrizePackageSDSModel>> getAll();
}
