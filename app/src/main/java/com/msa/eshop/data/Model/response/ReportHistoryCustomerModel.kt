package com.msa.eshop.data.Model.response

data class ReportHistoryCustomerModel(
    val id: String,
    val customerCode: String,
    val customerName: String,
    val date: String,
    val address: String,
    val status: String,
    val color: String,
    val cartCode: Int,
)