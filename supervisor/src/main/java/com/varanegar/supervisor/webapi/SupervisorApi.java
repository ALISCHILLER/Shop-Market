package com.varanegar.supervisor.webapi;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.supervisor.model.ProductModel;
import com.varanegar.supervisor.model.SupervisorCustomerModel;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.status.OrderSummaryRequestViewModel;
import com.varanegar.supervisor.status.OrderSummaryResultViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.BaseEventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EventTypeId;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.reviewreport.OrderReviewReportViewModel;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductGroupReviewReportViewModel;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductReviewReportViewModel;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReturnReviewReportViewModel;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReviewReportViewModel;

import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class SupervisorApi extends BaseApi implements ISupervisorApi {
    public SupervisorApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<TourStatusSummaryViewModel>> tour(int tourViewType, boolean Show_ReadyToSend, boolean Show_Sent, boolean Show_InProgress, boolean Show_Received, boolean Show_Finished, boolean Show_Canceled, boolean Show_Deactivated, String from_date, String to_date, UUID agentUniqueId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).tour(tourViewType, Show_ReadyToSend, Show_Sent, Show_InProgress, Show_Received, Show_Finished, Show_Canceled, Show_Deactivated, from_date, to_date, agentUniqueId);
    }

    @Override
    public Call<List<TourCustomerSummaryViewModel>> tourCustomers(String tourId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).tourCustomers(tourId);
    }

    @Override
    public Call<ResponseBody> deactivateTour(String tourId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).deactivateTour(tourId);
    }

    @Override
    public Call<ResponseBody> replicateTour(String tourId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).replicateTour(tourId);
    }

    @Override
    public Call<ResponseBody> cancelCustomerCall(String customerCallId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).cancelCustomerCall(customerCallId);
    }

    @Override
    public Call<ResponseBody> replicateCustomerCall(String customerCallId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).replicateCustomerCall(customerCallId);
    }

    @Override
    public Call<ResponseBody> putCustomerCall(UUID customerCallId, UUID dealerId, CustomerCallViewModel customerCallViewModel) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).putCustomerCall(customerCallId, dealerId, customerCallViewModel);
    }

    @Override
    public Call<CustomerCallViewModel> customerCalls(String customerCallUniqueId, UUID subsystemTypeId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).customerCalls(customerCallUniqueId, subsystemTypeId);
    }

    @Override
    public Call<List<CustomerCallViewModel>> customerCalls(boolean showConfirmed, boolean showUnconfirmed, boolean showCanceled, String startDate, String endDate , UUID agentUniqueId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl())
                .build().create(ISupervisorApi.class).customerCalls(showConfirmed, showUnconfirmed, showCanceled, startDate, endDate , agentUniqueId);
    }

    @Override
    public Call<List<EventViewModel>> loadLastPoints(LastPointsParam parameter) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).loadLastPoints(parameter);
    }

    @Override
    public Call<List<EventViewModel>> loadPersonnelEvents(PersonnelPointsParam parameter) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).loadPersonnelEvents(parameter);
    }

    @Override
    public Call<List<MasterEventViewModel>> loadPersonnelPath(PersonnelPointsParam parameter) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).loadPersonnelPath(parameter);
    }

    @Override
    public Call<List<VisitorModel>> getVisitors(String supervisorId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).getVisitors(supervisorId);
    }

    @Override
    public Call<List<OrderReviewReportViewModel>> order(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).order(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<SellReviewReportViewModel>> sell(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).sell(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<SellReturnReviewReportViewModel>> sellReturn(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).sellReturn(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<ProductReviewReportViewModel>> product(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).product(dealerId, startDate, endDate);
    }

    @Override
    public Call<OnHandQtyReportViewModel> onHandQty(UUID supervisorId, int deviceSettingNo) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).onHandQty(supervisorId, deviceSettingNo);
    }

    @Override
    public Call<List<ProductGroupReviewReportViewModel>> productGroup(String dealerId, String startDate, String endDate) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).productGroup(dealerId, startDate, endDate);
    }

    @Override
    public Call<List<ProductModel>> getProducts(@Nullable String searchText) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).getProducts(searchText);
    }

    @Override
    public Call<List<SupervisorCustomerModel>> getCustomers(UUID supervisorId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).getCustomers(supervisorId);
    }

    @Override
    public Call<List<CustomerSummaryViewModel>> getCustomerFinanceData(UUID customerId, UUID dealerId, UUID subsystemTypeId, int deviceSettingNo) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).getCustomerFinanceData(customerId, dealerId, subsystemTypeId, deviceSettingNo);
    }

    @Override
    public Call<OrderSummaryResultViewModel> getOrderPreview(OrderSummaryRequestViewModel requestViewModel) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).getOrderPreview(requestViewModel);
    }

    @Override
    public Call<List<VisitorVisitInfoViewModel>> getVisitorsVisitInfo(UUID supervisorId, UUID dealerId) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).getVisitorsVisitInfo(supervisorId, dealerId);
    }

    @Nullable
    public static BaseLocationViewModel convert(@NonNull EventViewModel eventViewModel) {
        if (eventViewModel.PointType == null) {
            BaseLocationViewModel locationViewModel = new BaseLocationViewModel();
            locationViewModel.CompanyPersonnelId = eventViewModel.MasterId;
            locationViewModel.Latitude = eventViewModel.Latitude;
            locationViewModel.Longitude = eventViewModel.Longitude;
            locationViewModel.Speed = eventViewModel.Speed;
            locationViewModel.Accurancy = eventViewModel.Accurancy;
            locationViewModel.UniqueId = eventViewModel.Id;
            locationViewModel.Desc = eventViewModel.Desc;
            locationViewModel.SubType = eventViewModel.SubType;
            locationViewModel.Lable = eventViewModel.Lable;
            return locationViewModel;
        } else {
            Gson gson = VaranegarGsonBuilder.build(false).create();
            try {
                JsonReader reader = new JsonReader(new StringReader("{\"eventData\":" + eventViewModel.JData + "}"));
                reader.setLenient(true);
                Object obj = gson.fromJson(reader, EventTypeId.getClass(eventViewModel.PointType));
                BaseEventLocationViewModel locationViewModel = (BaseEventLocationViewModel) obj;
                locationViewModel.CompanyPersonnelId = eventViewModel.MasterId;
                locationViewModel.Latitude = eventViewModel.Latitude;
                locationViewModel.Longitude = eventViewModel.Longitude;
                locationViewModel.Speed = eventViewModel.Speed;
                locationViewModel.Accurancy = eventViewModel.Accurancy;
                locationViewModel.UniqueId = eventViewModel.Id;
                locationViewModel.Desc = eventViewModel.Desc;
                locationViewModel.SubType = eventViewModel.SubType;
                locationViewModel.Lable = eventViewModel.Lable;
                if (eventViewModel.ActivityDate != null)
                    locationViewModel.ActivityDate = eventViewModel.ActivityDate;
                return locationViewModel;
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }
        }
    }
}
