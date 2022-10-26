package com.varanegar.vaslibrary.manager.news;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_ModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;
import java.util.List;
import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class NewsZarManager extends BaseManager<NewsData_Model> {

    public NewsZarManager(@NonNull Context context) {
        super(context, new NewsData_ModelRepository());
    }

    //---------------------------------------------------------------------------------------------- sync
    public void sync(final UpdateCall updateCall) {
        ApiNew apiNew=new ApiNew(getContext());
        Call<List<NewsData_Model>> call = apiNew.getNewsData();

        apiNew.runWebRequest(call, new WebCallBack<List<NewsData_Model>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<NewsData_Model> result, Request request) {
                try {
                    NewsData_ModelRepository repository = new
                            NewsData_ModelRepository();
                    if (result != null && result.size() > 0) {
                        repository.deleteAll();
                        repository.insertOrUpdate(result);
                    } else {
                        repository.deleteAll();
                    }
                    updateCall.success();
                }catch (Exception e){
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.data_error));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });

    }
    //---------------------------------------------------------------------------------------------- sync


    //---------------------------------------------------------------------------------------------- getAllNewNews
    public List<NewsData_Model> getAllNewNews() {
        Query query = new Query();
        query.from(NewsData_.NewsData_Tbl);
        return getItems(query);
    }
    //---------------------------------------------------------------------------------------------- getAllNewNews

}
