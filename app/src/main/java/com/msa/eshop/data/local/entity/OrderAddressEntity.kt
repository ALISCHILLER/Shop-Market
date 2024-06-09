package com.msa.eshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("orderAddress")
data class OrderAddressEntity(
    val centerName: String?,
    val customerAddress: String?,
    val customerMobile: String?,
    val customerPhone: String?,
    @PrimaryKey
    val id: String
)