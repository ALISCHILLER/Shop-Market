package com.msa.eshop.data.Model

import com.msa.eshop.data.Model.response.OrderAddressModel

data class OrderAddressResultModel   (
    val orderaddress: List<OrderAddressModel>
) : BaseResponse<List<OrderAddressModel>>(orderaddress, false, null)