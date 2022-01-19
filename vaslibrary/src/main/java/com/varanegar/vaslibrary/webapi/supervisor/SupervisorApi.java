package com.varanegar.vaslibrary.webapi.supervisor;

import android.content.Context;

import com.varanegar.vaslibrary.model.TourStatusSummaryViewModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model.ReturnDealerModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.OrderStatusModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.OrderStatusReport;
import com.varanegar.vaslibrary.ui.report.review.CustomerCallViewModel;
import com.varanegar.vaslibrary.ui.report.review.TourCustomerSummaryViewModel;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class SupervisorApi extends BaseApi implements ISupervisorApi {
    public SupervisorApi(Context context) {
        super(context);
    }

    @Override
    public Call<List<TourStatusSummaryViewModel>> tour(int tourViewType, boolean Show_ReadyToSend, boolean Show_Sent, boolean Show_InProgress, boolean Show_Received, boolean Show_Finished, boolean Show_Canceled, boolean Show_Deactivated, String from_date, String to_date) {
        return getRetrofitBuilder(TokenType.UserToken).build().create(ISupervisorApi.class).tour(tourViewType, Show_ReadyToSend, Show_Sent, Show_InProgress, Show_Received, Show_Finished, Show_Canceled, Show_Deactivated, from_date, to_date);
    }

    @Override
    public Call<List<TourCustomerSummaryViewModel>> tourCustomers(String tourId) {
        return getRetrofitBuilder(TokenType.UserToken)
                .build().create(ISupervisorApi.class).tourCustomers(tourId);
    }

    @Override
    public Call<ResponseBody> deactivateTour(String tourId) {
        return getRetrofitBuilder(TokenType.UserToken)
                .build().create(ISupervisorApi.class).deactivateTour(tourId);
    }

    @Override
    public Call<ResponseBody> replicate(String tourId) {
        return getRetrofitBuilder(TokenType.UserToken)
                .build().create(ISupervisorApi.class).replicate(tourId);
    }

    @Override
    public Call<CustomerCallViewModel> customerCalls(String tourId) {
        return getRetrofitBuilder(TokenType.UserToken)
                .build().create(ISupervisorApi.class).customerCalls(tourId);
    }

    @Override
    public Call<List<OrderStatusReport>> OrderStatusReport(OrderStatusModel orderStatusModel) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).OrderStatusReport(orderStatusModel);
    }
    @Override
    public Call<List<ReturnDealerModel>> GetReturnReport(OrderStatusModel param) {
        return getRetrofitBuilder(TokenType.UserToken, getBaseUrl()).build().create(ISupervisorApi.class).GetReturnReport(param);
    }


//    @Override
//    public Call<List<EventViewModel>> loadLastPoints(LastPointsParam parameter) {
//        return getRetrofitBuilder(TokenType.UserToken, "http://192.168.2.253:7171/").build().create(ISupervisorApi.class).loadLastPoints(parameter);
//    }
//
//    @Override
//    public Call<List<VisitorModel>> getVisitors(String supervisorId) {
//        return getRetrofitBuilder(TokenType.UserToken, "http://192.168.2.254:4000/").build().create(ISupervisorApi.class).getVisitors(supervisorId);
//    }
//
//    @Override
//    public Call<List<OrderReviewReportViewModel>> order(String dealerId, String startDate, String endDate) {
//        return getRetrofitBuilder(TokenType.UserToken, "http://192.168.2.254:4000/").build().create(ISupervisorApi.class).order(dealerId, startDate, endDate);
//    }
//
//    @Override
//    public Call<List<SellReviewReportViewModel>> sell(String dealerId, String startDate, String endDate) {
//        return getRetrofitBuilder(TokenType.UserToken, "http://192.168.2.254:4000/").build().create(ISupervisorApi.class).sell(dealerId, startDate, endDate);
//    }
//
//    @Override
//    public Call<List<SellReturnReviewReportViewModel>> sellReturn(String dealerId, String startDate, String endDate) {
//        return getRetrofitBuilder(TokenType.UserToken, "http://192.168.2.254:4000/").build().create(ISupervisorApi.class).sellReturn(dealerId, startDate, endDate);
//    }
//
//    @Override
//    public Call<List<ProductReviewReportViewModel>> product(String dealerId, String startDate, String endDate) {
//        return getRetrofitBuilder(TokenType.UserToken, "http://192.168.2.254:4000/").build().create(ISupervisorApi.class).product(dealerId, startDate, endDate);
//    }
//
//    @Override
//    public Call<List<ProductGroupReviewReportViewModel>> productGroup(String dealerId, String startDate, String endDate) {
//        return getRetrofitBuilder(TokenType.UserToken, "http://192.168.2.254:4000/").build().create(ISupervisorApi.class).productGroup(dealerId, startDate, endDate);
//    }
//
//    @Nullable
//    public static BaseLocationViewModel convert(@NonNull EventViewModel eventViewModel) {
//        if (eventViewModel.PointType == null) {
//            BaseLocationViewModel locationViewModel = new BaseLocationViewModel();
//            locationViewModel.Date = eventViewModel.ActivityDate;
//            locationViewModel.Latitude = eventViewModel.Latitude;
//            locationViewModel.Longitude = eventViewModel.Longitude;
//            locationViewModel.Speed = eventViewModel.Speed;
//            locationViewModel.Accurancy = eventViewModel.Accurancy;
//            locationViewModel.UniqueId = eventViewModel.Id;
//            locationViewModel.ActivityDate = DateHelper.toString(eventViewModel.ActivityDate, DateFormat.MicrosoftDateTime, Locale.US);
//            return locationViewModel;
//        } else {
//            Gson gson = VaranegarGsonBuilder.build().create();
//            try {
//                JsonReader reader = new JsonReader(new StringReader(eventViewModel.JData));
//                reader.setLenient(true);
//                Object obj = gson.fromJson(reader, EventTypeId.getClass(eventViewModel.PointType));
//                BaseEventLocationViewModel locationViewModel = (BaseEventLocationViewModel) obj;
//                locationViewModel.Date = eventViewModel.ActivityDate;
//                locationViewModel.Latitude = eventViewModel.Latitude;
//                locationViewModel.Longitude = eventViewModel.Longitude;
//                locationViewModel.Speed = eventViewModel.Speed;
//                locationViewModel.Accurancy = eventViewModel.Accurancy;
//                locationViewModel.UniqueId = eventViewModel.Id;
//                locationViewModel.ActivityDate = DateHelper.toString(eventViewModel.ActivityDate, DateFormat.MicrosoftDateTime, Locale.US);
//                return locationViewModel;
//            } catch (Exception e) {
//                Timber.e(e);
//                return null;
//            }
//        }
//    }
}
