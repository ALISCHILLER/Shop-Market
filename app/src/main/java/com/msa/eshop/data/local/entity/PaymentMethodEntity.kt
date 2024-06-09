package com.msa.eshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("paymentMethod")
data class PaymentMethodEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val cashprice: String?,
    val checkprice: String?,
    val receiptprice: String?,
)
