package com.varanegar.vaslibrary.manager.customergrouplastsalesreportmanager;



import android.content.Context;
import androidx.annotation.NonNull;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport.CustomerGroupLastSalesReport;
import com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport.CustomerGroupLastSalesReportModel;
import com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport.CustomerGroupLastSalesReportModelRepository;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class CustomerGroupLastSalesReportManager extends
        BaseManager<CustomerGroupLastSalesReportModel> {

    public CustomerGroupLastSalesReportManager(@NonNull Context context) {
        super(context,new CustomerGroupLastSalesReportModelRepository());
    }

    public List<CustomerGroupLastSalesReportModel> getAll(UUID customerCode,int productGroup) {
        Query query=new Query()
                .from(CustomerGroupLastSalesReport.CustomerGroupLastSalesReportTbl)
                .whereAnd(Criteria.equals(CustomerGroupLastSalesReport.customerUniqeID,customerCode
                        .toString()));
        return getItems(query);
    }

    private Call<List<CustomerGroupLastSalesReportModel>> call;
    public void sync(final UpdateCall updateCall) {
        List<String> dealerId =new ArrayList<>();
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null) {
            updateCall.failure(getContext().getString(R.string.user_not_found));
            return;
        }
        dealerId.add(userModel.UniqueId.toString());
        ApiNew apiNew=new ApiNew(getContext());

        Call<List<CustomerGroupLastSalesReportModel>> call = apiNew
                .CustomerLastSaleReport(dealerId,"","");

        apiNew.runWebRequest(call, new WebCallBack<List<CustomerGroupLastSalesReportModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerGroupLastSalesReportModel> result, Request request) {
                CustomerGroupLastSalesReportModelRepository repository=new CustomerGroupLastSalesReportModelRepository();

                    try {
                        deleteAll();
                        if (result.size()>0)
                        insertOrUpdate(result);

                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }

                updateCall.success();

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
    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }
}


