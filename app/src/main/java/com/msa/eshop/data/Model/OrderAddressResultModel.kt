package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductModelEntity

data class OrderAddressResultModel   (
    val orderaddress: List<OrderAddressModel>
) : BaseResponse<List<OrderAddressModel>>(orderaddress, false, null)