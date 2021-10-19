package com.varanegar.vaslibrary.webapi.personnel;

import android.content.Context;

import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import retrofit2.Call;

/**
 * Created by A.Jafarzadeh on 12/21/2017.
 */

public class PersonnelMetaDataApi extends BaseApi implements IPersonnelMetaDataApi {
    public PersonnelMetaDataApi(Context context) {
        super(context);
    }

    @Override
    public Call<PersonnelMetaDataViewModel> getPersonalMetaData(String PersonnelId, String SubSystemId) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(IPersonnelMetaDataApi.class).getPersonalMetaData(PersonnelId, SubSystemId);

    }
}
