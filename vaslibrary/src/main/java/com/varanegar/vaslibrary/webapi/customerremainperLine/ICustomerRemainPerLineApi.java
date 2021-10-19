package com.varanegar.vaslibrary.webapi.customerremainperLine;

import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by A.Jafarzadeh on 8/20/2018.
 */

public interface ICustomerRemainPerLineApi {
    @GET("api/v2/ngt/customer/remainperline")
    Call<List<CustomerRemainPerLineModel>> getAll(@Query("DealerId") String DealerId,
                                                  @Query("DeviceSettingNo") String DeviceSettingNo,
                                                  @Query("customerId") String customerId,
                                                  @Query("TourNo") int tourNo,
                                                  @Query("SubsystemTypeId") UUID subSystemTypeId);
}
