package com.varanegar.vaslibrary.webapi.price;

import android.content.Context;

import com.varanegar.vaslibrary.model.contractprice.ContractPriceNestleModel;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceSDSModel;
import com.varanegar.vaslibrary.model.contractprice.ContractPriceVnLiteModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 6/11/2017.
 */

public class ContractPriceApi extends BaseApi implements IContractPriceApi {
    public ContractPriceApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<ContractPriceSDSModel>> getVaranegarContractPrices(String dateAfter) {
        IContractPriceApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IContractPriceApi.class);
        return api.getVaranegarContractPrices(dateAfter);
    }

    @Override
    public Call<List<ContractPriceVnLiteModel>> getRastakContractPrices() {
        IContractPriceApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IContractPriceApi.class);
        return api.getRastakContractPrices();
    }

    @Override
    public Call<List<ContractPriceNestleModel>> getNestleContractPrices() {
        IContractPriceApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IContractPriceApi.class);
        return api.getNestleContractPrices();
    }
}
