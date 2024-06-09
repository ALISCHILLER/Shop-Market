package com.msa.eshop.data.Model.request

data class InsertCartModelRequest(
    val customerAddressId: String,
    val paymentTermId: String,
    val productCode: Int,
    val quantity: Int,

)