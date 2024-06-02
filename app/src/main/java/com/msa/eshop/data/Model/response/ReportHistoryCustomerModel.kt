package com.msa.eshop.data.Model.response

data class ReportHistoryCustomerModel(
    val address: String,
    val customerCode: String,
    val customerName: String,
    val date: String,
    val id: String,
    val status: String
)