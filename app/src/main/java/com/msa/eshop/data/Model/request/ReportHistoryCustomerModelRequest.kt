package com.msa.eshop.data.Model.request

data class ReportHistoryCustomerModelRequest(
    val customerId: String,
    val fromDate: String,
    val endDate: String,

)