package com.msa.eshop.data.Model

import com.msa.eshop.data.Model.response.ReportHistoryCustomerModel

data class ReportHistoryCustomerResponse(
    val reportHistoryCustomer:List<ReportHistoryCustomerModel>
):BaseResponse<List<ReportHistoryCustomerModel>>(reportHistoryCustomer,false,null){
}