package com.msa.eshop.data.Model.response

data class OrderAddressModel(
    val centerName: String,
    val customerAddress: String,
    val customerMobile: String,
    val customerPhone: String,
    val id: String
)