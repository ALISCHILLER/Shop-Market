package com.msa.eshop.data.Model

data class DiscountResultModel(
    val discountPercent: Int,
    val endNumber: Int,
    val fromNumber: Int,
    val id: String,
    val productId: String
)