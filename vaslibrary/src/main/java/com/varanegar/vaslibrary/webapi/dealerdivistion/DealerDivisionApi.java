package com.varanegar.vaslibrary.webapi.dealerdivistion;

import android.content.Context;
import com.varanegar.vaslibrary.model.dealerdivision.DealerDivisionModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import retrofit2.Call;

/**
 * Create By Mehrdad Latifi on 9/21/2022
 */

public class DealerDivisionApi extends BaseApi implements IDealerDivisionApi{

    //---------------------------------------------------------------------------------------------- DealerDivisionApi
    public DealerDivisionApi(Context context) {
        super(context);
    }
    //---------------------------------------------------------------------------------------------- DealerDivisionApi


    //---------------------------------------------------------------------------------------------- get
    @Override
    public Call<DealerDivisionModel> get() {
        IDealerDivisionApi api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(IDealerDivisionApi.class);
        return api.get();
    }
    //---------------------------------------------------------------------------------------------- get
}
