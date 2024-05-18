package com.msa.eshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Product")
data class ProductModelEntity(
    @PrimaryKey
    val id: String,
    val productName: String?,
    val productCode: Int,
    val fullNameKala1: String?,
    val unit1: String?,
    val unitid1: String?,
    val convertFactor1: Int,
    val fullNameKala2: String?,
    val unit2: String?,
    val convertFactor2: Int,
    val unitid2: String?,
    val productGroupCode: Int,
    val price: Int,
    val discountPercent: Int,
    val priceByDiscountPercent: Int,
    val productImage: String?,

)