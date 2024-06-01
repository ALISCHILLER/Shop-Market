package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductModelEntity

data class InsertCartModelResponse(
    val insertCart:Boolean
) : BaseResponse<Boolean>(insertCart, false, null)
