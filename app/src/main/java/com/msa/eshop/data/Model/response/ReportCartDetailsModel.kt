package com.msa.eshop.data.Model.response

data class ReportCartDetailsModel(
    val cartCode: Int,
    val customerAddress: String,
    val customerName: String,
    val discount: Int,
    val id: String,
    val price: Int,
    val productCode: String,
    val productImageUrl: String,
    val productName: String,
    val quantity: Int,
    val salesDate: String,
    val statusName: String,
    val tax: Int,
    val total: Int
)