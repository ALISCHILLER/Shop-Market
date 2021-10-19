package com.varanegar.vaslibrary.webapi.price;

import com.varanegar.vaslibrary.model.contractprice.ContractPriceNestleModel;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceSDSModel;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceVnLiteModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 6/11/2017.
 */

public interface IContractPriceApi
{
    @GET("api/v2/ngt/price/contractPrice/zip")
    Call<List<ContractPriceSDSModel>> getVaranegarContractPrices(@Query("Date") String dateAfter);

    @GET("api/v2/ngt/pricevnlite/contractPrice/zip")
    Call<List<ContractPriceVnLiteModel>> getRastakContractPrices();

    @GET("api/v2/ngt/priceconditionnestle")
    Call<List<ContractPriceNestleModel>> getNestleContractPrices();

}
