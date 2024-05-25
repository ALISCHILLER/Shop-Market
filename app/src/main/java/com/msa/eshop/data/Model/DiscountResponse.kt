package com.msa.eshop.data.Model

import com.msa.eshop.data.local.entity.ProductGroupEntity


data class DiscountResponse(
    val discountResultModel: List<DiscountResultModel>
) : BaseResponse<List<DiscountResultModel>>(discountResultModel, false, null)