package com.msa.eshop.data.Model

import com.msa.eshop.data.Model.response.DiscountResultModel


data class DiscountResponse(
    val discountResultModel: List<DiscountResultModel>
) : BaseResponse<List<DiscountResultModel>>(discountResultModel, false, null)