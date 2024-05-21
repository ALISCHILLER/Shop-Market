package com.msa.eshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("order")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val convertFactor1: Int,
    val convertFactor2: Int,
    val fullNameKala1: String?,
    val fullNameKala2: String?,
    val productCode: Int,
    val productGroupCode: Int,
    val productName: String?,
    val unit1: String?,
    val unit2: String?,
    val unitid1: String?,
    val unitid2: String?,
    val price: Int,
    val productImage: String?,
    var numberOrder: Int,
    var numberOrder1: Int,
    var numberOrder2: Int,
    val unitOrder: String?,
)
