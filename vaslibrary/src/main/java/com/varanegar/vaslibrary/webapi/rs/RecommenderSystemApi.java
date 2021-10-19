package com.varanegar.vaslibrary.webapi.rs;

import android.content.Context;

import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class RecommenderSystemApi extends BaseApi implements IRecommenderSystemApi {
    public RecommenderSystemApi(Context context) {
        super(context);
    }

    @Override
    public Call<RecSysConfig> getConfigs() {
        IRecommenderSystemApi api = getRetrofitBuilder(TokenType.UserToken).build().create(IRecommenderSystemApi.class);
        return api.getConfigs();
    }

    @Override
    public Call<List<RecommendedProductModel>> getRecommendedProducts(UUID customerId) throws RecSysException {
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.RecSysServer, SysConfigManager.local);
        String baseUrl = SysConfigManager.getStringValue(sysConfigModel, null);
        if (baseUrl != null) {
            IRecommenderSystemApi api = getRetrofitBuilder(baseUrl, false, getClient(BaseApi.DEFAULT_CONNECT_TIMEOUT, BaseApi.DEFAULT_READ_TIMEOUT, BaseApi.DEFAULT_WRITE_TIMEOUT)).build().create(IRecommenderSystemApi.class);
            return api.getRecommendedProducts(customerId);
        } else {
            throw new RecSysException(getContext().getString(R.string.ip_not_found));
        }
    }
}
