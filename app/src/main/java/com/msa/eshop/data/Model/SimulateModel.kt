package com.msa.eshop.data.Model

data class SimulateModel(
    val convertFactor1: Int,
    val convertFactor2: Int,
    val discountPercent: Int,
    val finalPrice: Int,
    val finalPriceDiscount: Int,
    val fullNameKala1: String,
    val fullNameKala2: String,
    val id: String,
    val isTax: Boolean,
    val price: Int,
    val priceByDiscountPercent: Int,
    val priceByDiscountPercentAndTax: Int,
    val priceDiscount: Int,
    val quantity: Int,
    val productCode: Int,
    val productGroupCode: Int,
    val productImage: String,
    val productName: String,
    val unit1: String,
    val unit2: String,
    val unitid1: String,
    val unitid2: String
)