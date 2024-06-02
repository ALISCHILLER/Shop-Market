package com.msa.eshop.data.Model.response

data class DiscountResultModel(
    val discountPercent: Int,
    val endNumber: Int,
    val fromNumber: Int,
    val id: String,
    val productId: String
)